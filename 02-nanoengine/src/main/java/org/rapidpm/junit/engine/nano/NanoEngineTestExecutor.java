package org.rapidpm.junit.engine.nano;

import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.rapidpm.dependencies.core.logger.HasLogger;

public class NanoEngineTestExecutor
    implements HasLogger {

  public void execute(ExecutionRequest request, TestDescriptor rootNode) {
    // all could be non-blocking / async
    if (rootNode instanceof EngineDescriptor) executeContainer(request, rootNode);
    if (rootNode instanceof NanoEngineClassTestDescriptor) executeContainer(request, rootNode);
    if (rootNode instanceof NanoEngineMethodTestDescriptor) executeMethod(request,
                                                                          (NanoEngineMethodTestDescriptor) rootNode);
  }

  private void executeContainer(ExecutionRequest request, TestDescriptor rootNode) {
    final EngineExecutionListener engineExecutionListener = request.getEngineExecutionListener();
    engineExecutionListener.executionStarted(rootNode);

    rootNode.getChildren()
            .forEach(c -> execute(request, c));

    engineExecutionListener.executionFinished(rootNode, TestExecutionResult.successful());
  }

  private void executeMethod(ExecutionRequest request, NanoEngineMethodTestDescriptor descriptor) {
    request.getEngineExecutionListener()
           .executionStarted(descriptor);

    TestExecutionResult executionResult = executeTestMethod(descriptor);

    request.getEngineExecutionListener()
           .executionFinished(descriptor, executionResult);
  }

  private TestExecutionResult executeTestMethod(NanoEngineMethodTestDescriptor descriptor) {
    try {
      Object newInstance = ReflectionUtils.newInstance(descriptor.getTestClass());
      ReflectionUtils.invokeMethod(descriptor.getTestMethod(), newInstance);
      return TestExecutionResult.successful();
    } catch (Exception e) {
      logger().warning(e.getMessage());
      return TestExecutionResult.failed(e);
    }
  }
}
