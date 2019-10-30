package org.rapidpm.junit.engine.nano;

import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.rapidpm.dependencies.core.logger.HasLogger;

import static org.rapidpm.junit.engine.nano.NanoEngine.isTestMethod;

public class NanoEngineClassTestDescriptor
    extends AbstractTestDescriptor
    implements HasLogger {

  private final Class<?> testClass;

  public NanoEngineClassTestDescriptor(Class<?> testClass, UniqueId uniqueId) {
    super(uniqueId.append("class", testClass.getSimpleName()), testClass.getSimpleName(), ClassSource.from(testClass));
    this.testClass = testClass;
    addChildren();
  }

  private void addChildren() {
    ReflectionUtils.findMethods(testClass, isTestMethod())
                   .forEach(method -> {
                     final NanoEngineMethodTestDescriptor child = new NanoEngineMethodTestDescriptor(method, testClass,
                                                                                                     this.getUniqueId());
                     addChild(child);
                   });
  }

  @Override
  public Type getType() {
    return Type.CONTAINER;
  }
}
