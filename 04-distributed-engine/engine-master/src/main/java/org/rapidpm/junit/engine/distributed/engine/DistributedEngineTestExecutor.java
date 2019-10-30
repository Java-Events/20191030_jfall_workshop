package org.rapidpm.junit.engine.distributed.engine;

import com.hazelcast.core.HazelcastInstance;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.junit.engine.distributed.shared.JUnitTaskExecutorTask;

import java.util.concurrent.Future;

public class DistributedEngineTestExecutor
    implements HasLogger {

  private HazelcastInstance client;

  public DistributedEngineTestExecutor(HazelcastInstance client) {
    this.client = client;
  }

  public void execute(ExecutionRequest request, TestDescriptor rootNode) {
    // all could be non-blocking / async
    if (rootNode instanceof EngineDescriptor) executeContainer(request, rootNode);
    if (rootNode instanceof DistributedEngineClassTestDescriptor) executeContainer(request, rootNode);
    if (rootNode instanceof DistributedEngineMethodTestDescriptor) executeMethod(request,
                                                                                 (DistributedEngineMethodTestDescriptor) rootNode);

    //waiting for join()
  }

  private void executeContainer(ExecutionRequest request, TestDescriptor rootNode) {
    request.getEngineExecutionListener()
           .executionStarted(rootNode);

    //concurrent -> CompletableFuture?
    rootNode.getChildren()
            .forEach(c -> execute(request, c));

    //waiting to join()?
    request.getEngineExecutionListener()
           .executionFinished(rootNode, TestExecutionResult.successful());
  }

  private void executeMethod(ExecutionRequest request, DistributedEngineMethodTestDescriptor descriptor) {
    request.getEngineExecutionListener()
           .executionStarted(descriptor);
    TestExecutionResult executionResult = executeTestMethod(descriptor);
    request.getEngineExecutionListener()
           .executionFinished(descriptor, executionResult);
  }

  //concurrent -> CompletableFuture?
  private TestExecutionResult executeTestMethod(DistributedEngineMethodTestDescriptor descriptor) {

    try {

      String className = descriptor.getTestClass()
                                   .getName();
      String methodName = descriptor.getTestMethod()
                                    .getName();

      Future<String> submit = client.getExecutorService("distributed-junit")
                                    .submit(new JUnitTaskExecutorTask(className, methodName));

      String result = submit.get();
      return result.equals("SUCCESS")
             ? TestExecutionResult.successful()
             : TestExecutionResult.failed(new AssertionError());

//      //TODO CDI for example
//      //send method name and params over the wire
//      Object newInstance = ReflectionUtils.newInstance(descriptor.getTestClass());
//      try {
//        //could check result types for more detailed return info
//        ReflectionUtils.invokeMethod(descriptor.getTestMethod(), newInstance);
//        return TestExecutionResult.successful();
//      } catch (Exception e) {
//        logger().warning(e.getLocalizedMessage());
//        return TestExecutionResult.failed(e);
//      }
    } catch (Exception e) {
      String msg = "can  not create instance of " + descriptor.getClass() + " -- " + e.getLocalizedMessage();
      logger().warning(msg);
      return TestExecutionResult.failed(new RuntimeException(msg, e));
    }
  }


}
