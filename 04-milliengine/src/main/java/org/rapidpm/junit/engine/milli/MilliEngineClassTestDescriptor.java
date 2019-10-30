package org.rapidpm.junit.engine.milli;

import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.rapidpm.dependencies.core.logger.HasLogger;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

public class MilliEngineClassTestDescriptor
    extends AbstractTestDescriptor
    implements HasLogger {

  private final Class<?> testClass;

  private Boolean forceRandomExecution = false;
  private Boolean useCDI               = false;


  public MilliEngineClassTestDescriptor(Class<?> testClass, TestDescriptor parent) {
    super(parent.getUniqueId()
                .append("class", testClass.getSimpleName()), testClass.getSimpleName(), ClassSource.from(testClass));

    this.testClass = testClass;

    setParent(parent);
    addChildren();
  }


  private Predicate<Method> isTestMethod() {
    return method -> {
      if (ReflectionUtils.isStatic(method)) return false;
      if (ReflectionUtils.isPrivate(method)) return false;
      if (ReflectionUtils.isAbstract(method)) return false;
      if (method.getParameterCount() > 0) return false;
      return AnnotationSupport.isAnnotated(method, MilliTest.class) && method.getReturnType()
                                                                             .equals(void.class);
    };
  }

  private void addChildren() {

    findAnnotation(testClass, MilliTestClass.class).ifPresent(a -> {
      forceRandomExecution = a.forceRandomExecution();
      useCDI               = a.useCDI();
    });


    //TODO forceRandomExecution if needed
    ReflectionUtils.findMethods(testClass, isTestMethod())
                   .stream()
                   .collect(collectingAndThen(toList(), elements -> {
                     if (forceRandomExecution) shuffle(elements);
                     return elements.stream();
                   }))
                   .peek((e) -> logger().info("method in class -> " + e.getName()))
                   .map(method -> new MilliEngineMethodTestDescriptor(method, testClass, this))
                   .forEach(this::addChild);

  }


  @Override
  public Type getType() {
    return Type.CONTAINER;
  }
}
