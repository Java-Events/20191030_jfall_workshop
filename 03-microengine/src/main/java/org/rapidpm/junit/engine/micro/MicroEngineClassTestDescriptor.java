package org.rapidpm.junit.engine.micro;

import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.rapidpm.dependencies.core.logger.HasLogger;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static org.rapidpm.junit.engine.micro.MicroEngine.isTestMethod;

public class MicroEngineClassTestDescriptor
    extends AbstractTestDescriptor
    implements HasLogger {

  private final Class<?> testClass;

  private Boolean forceRandomExecution = false;
  private Boolean useCDI               = false;


  public MicroEngineClassTestDescriptor(Class<?> testClass, UniqueId uniqueId) {
    super(uniqueId
                .append("class",
                        testClass.getSimpleName()),
          testClass.getSimpleName(),
          ClassSource.from(testClass));
    this.testClass = testClass;
    addChildren();
  }


  private void addChildren() {
    findAnnotation(testClass, MicroTestClass.class).ifPresent(a -> {
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
                   .map(method -> new MicroEngineMethodTestDescriptor(method, testClass, getUniqueId()))
                   .forEach(this::addChild);

  }


  @Override
  public Type getType() {
    return Type.CONTAINER;
  }
}
