package org.rapidpm.junit.engine.micro;

import org.jboss.weld.environment.se.WeldContainer;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.rapidpm.dependencies.core.logger.HasLogger;

import java.lang.reflect.Method;
import java.util.Objects;

import static org.junit.platform.engine.TestExecutionResult.successful;

public class MicroEngineTestExecutor
    implements HasLogger {

  private WeldContainer container;

  public MicroEngineTestExecutor(WeldContainer container) {
    this.container = container;
  }

  public void execute(ExecutionRequest request, TestDescriptor rootNode) {
    // all could be non-blocking / async
    if (rootNode instanceof EngineDescriptor) executeContainer(request, rootNode);
    if (rootNode instanceof MicroEngineClassTestDescriptor) executeContainer(request, rootNode);
    if (rootNode instanceof MicroEngineMethodTestDescriptor) executeMethod(request,
                                                                           (MicroEngineMethodTestDescriptor) rootNode);

  }

  private void executeContainer(ExecutionRequest request, TestDescriptor rootNode) {
    request.getEngineExecutionListener()
           .executionStarted(rootNode);
    rootNode.getChildren()
            .forEach(c -> execute(request, c));
    request.getEngineExecutionListener()
           .executionFinished(rootNode, successful());
  }

  private void executeMethod(ExecutionRequest request, MicroEngineMethodTestDescriptor descriptor) {
    request.getEngineExecutionListener()
           .executionStarted(descriptor);
    TestExecutionResult executionResult = executeTestMethod(descriptor);
    request.getEngineExecutionListener()
           .executionFinished(descriptor, executionResult);
  }

  //TODO invoke with Method Param Injection from WELD Container
  private TestExecutionResult executeTestMethod(MicroEngineMethodTestDescriptor descriptor) {
    try {
      Class<?> testClass = descriptor.getTestClass();
      Objects.requireNonNull(testClass);
      Object obj = (descriptor.useCDI())
                   ? container.select(testClass)
                              .get()
                   : ReflectionUtils.newInstance(testClass);
      final Method testMethod = descriptor.getTestMethod();
      ReflectionUtils.invokeMethod(testMethod, obj);
      return successful();
    } catch (Exception e) {
      return TestExecutionResult.failed(new RuntimeException(e));
    }
  }
}