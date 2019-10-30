package org.rapidpm.junit.engine.nano;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

import java.lang.reflect.Method;

public class NanoEngineMethodTestDescriptor
    extends AbstractTestDescriptor {

  private final Method testMethod;
  private final Class  testClass;

  public NanoEngineMethodTestDescriptor(Method testMethod, Class testClass, UniqueId uniqueId) {
    super(uniqueId.append("method", testMethod.getName()), testMethod.getName(), MethodSource.from(testMethod));
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
