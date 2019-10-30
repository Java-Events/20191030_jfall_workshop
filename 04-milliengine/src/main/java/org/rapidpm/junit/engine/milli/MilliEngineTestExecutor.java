package org.rapidpm.junit.engine.milli;

import org.jboss.weld.environment.se.WeldContainer;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.rapidpm.dependencies.core.logger.HasLogger;

import static org.junit.platform.engine.TestExecutionResult.successful;

public class MilliEngineTestExecutor
    implements HasLogger {

  private WeldContainer container;

  public MilliEngineTestExecutor(WeldContainer container) {
    this.container = container;
  }

  public void execute(ExecutionRequest request, TestDescriptor rootNode) {
    // all could be non-blocking / async
    if (rootNode instanceof EngineDescriptor) executeContainer(request, rootNode);
    if (rootNode instanceof MilliEngineClassTestDescriptor) executeContainer(request, rootNode);
    if (rootNode instanceof MilliEngineMethodTestDescriptor) executeMethod(request,
                                                                           (MilliEngineMethodTestDescriptor) rootNode);

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
           .executionFinished(rootNode, successful());
  }

  private void executeMethod(ExecutionRequest request, MilliEngineMethodTestDescriptor descriptor) {
    request.getEngineExecutionListener()
           .executionStarted(descriptor);
    TestExecutionResult executionResult = executeTestMethod(descriptor);
    request.getEngineExecutionListener()
           .executionFinished(descriptor, executionResult);
  }

  //concurrent -> CompletableFuture?
  private TestExecutionResult executeTestMethod(MilliEngineMethodTestDescriptor descriptor) {

    try {
      Class testClass = descriptor.getTestClass();
      logger().info("use CDI " + descriptor.useCDI() + " for class " + testClass);
      //TODO CDI for example
      //TODO get Instance from WELD Container
      //send method name and params over the wire
      Object newInstance = (descriptor.useCDI())
                           ? container.select(testClass)
                                      .get()
                           : ReflectionUtils.newInstance(descriptor.getTestClass());

      try {
        //could check result types for more detailed return info
        //TODO invoke with Method Param Injection from WELD Container
        ReflectionUtils.invokeMethod(descriptor.getTestMethod(), newInstance);
        return successful();
      } catch (Exception e) {
        logger().warning(e.getLocalizedMessage());
        return TestExecutionResult.failed(e);
      }
    } catch (Exception e) {
      String msg = "can  not create instance of " + descriptor.getClass() + " -- " + e.getLocalizedMessage();
      logger().warning(msg);
      return TestExecutionResult.failed(new RuntimeException(msg, e));
    }
  }


}
