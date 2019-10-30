package org.rapidpm.junit.engine.distributed.engine;

import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.rapidpm.dependencies.core.logger.HasLogger;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public class DistributedEngineClassTestDescriptor
    extends AbstractTestDescriptor
    implements HasLogger {

  private final Class<?> testClass;

  public DistributedEngineClassTestDescriptor(Class<?> testClass, TestDescriptor parent) {
    super(parent.getUniqueId()
                .append("class", testClass.getSimpleName()), testClass.getSimpleName(), ClassSource.from(testClass));

    this.testClass = testClass;

    setParent(parent);
    addChildren();
  }

  private void addChildren() {

    Predicate<Method> isTestMethod = method -> {
      if (ReflectionUtils.isStatic(method)) return false;
      if (ReflectionUtils.isPrivate(method)) return false;
      if (ReflectionUtils.isAbstract(method)) return false;
      if (method.getParameterCount() > 0) return false;
      return AnnotationSupport.isAnnotated(method, DistributedTest.class)
             && method.getReturnType().equals(void.class);
    };

    ReflectionUtils.findMethods(testClass, isTestMethod)
                   .stream()
                   .peek((e) -> logger().info("method in class -> " + e.getName()))
                   .map(method -> new DistributedEngineMethodTestDescriptor(method, testClass, this))
                   .forEach(this::addChild);
  }


  @Override
  public Type getType() {
    return Type.CONTAINER;
  }
}
