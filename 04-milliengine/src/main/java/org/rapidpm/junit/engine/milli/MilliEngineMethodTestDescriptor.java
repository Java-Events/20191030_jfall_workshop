package org.rapidpm.junit.engine.milli;

import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

import java.lang.reflect.Method;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

public class MilliEngineMethodTestDescriptor
    extends AbstractTestDescriptor {

  private final Method testMethod;
  private final Class  testClass;

  private Boolean forceRandomExecution = false;
  private Boolean useCDI               = false;

  public MilliEngineMethodTestDescriptor(Method testMethod, Class testClass, MilliEngineClassTestDescriptor parent) {
    super(parent.getUniqueId()
                .append("method", testMethod.getName()), testMethod.getName(), MethodSource.from(testMethod));

    this.testMethod = testMethod;
    this.testClass  = testClass;

    findAnnotation(testClass, MilliTestClass.class).ifPresent(a -> {
      forceRandomExecution = a.forceRandomExecution();
      useCDI               = a.useCDI();
    });
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

  public boolean useCDI() {
    return useCDI;
  }

  public Boolean getForceRandomExecution() {
    return forceRandomExecution;
  }
}
