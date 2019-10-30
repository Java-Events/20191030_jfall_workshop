package org.rapidpm.junit.engine.distributed.engine;

import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

import java.lang.reflect.Method;

public class DistributedEngineMethodTestDescriptor
    extends AbstractTestDescriptor {

  private final Method testMethod;
  private final Class testClass;

  public DistributedEngineMethodTestDescriptor(Method testMethod,
                                               Class testClass,
                                               DistributedEngineClassTestDescriptor parent) {
    super(parent.getUniqueId().append("method", testMethod.getName()),
          testMethod.getName(),
          MethodSource.from(testMethod));

    this.testMethod = testMethod;
    this.testClass  = testClass;
  }

  @Override
  public Type getType() {
    return Type.TEST;
  }

  public Method getTestMethod() {
    return testMethod;
  }

  public Class getTestClass() {
    return testClass;
  }
}
