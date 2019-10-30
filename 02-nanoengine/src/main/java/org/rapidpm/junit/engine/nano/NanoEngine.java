package org.rapidpm.junit.engine.nano;

import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.discovery.PackageSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.dependencies.core.logger.Logger;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.function.Predicate;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;
import static org.junit.platform.commons.util.ReflectionUtils.isAbstract;
import static org.junit.platform.commons.util.ReflectionUtils.isPrivate;
import static org.rapidpm.frp.matcher.Case.match;
import static org.rapidpm.frp.matcher.Case.matchCase;
import static org.rapidpm.frp.model.Result.failure;
import static org.rapidpm.frp.model.Result.success;

public class NanoEngine
    implements TestEngine, HasLogger {

  public static final String ENGINE_ID = NanoEngine.class.getSimpleName();

  protected static Predicate<Class<?>> isTestClass() {
    return classCandidate -> match(matchCase(
        () -> failure("this class is not a supported by this TestEngine - " + classCandidate.getSimpleName())),
                                   matchCase(() -> isAbstract(classCandidate), () -> failure(
                                       "no support for abstract classes" + classCandidate.getSimpleName())),
                                   matchCase(() -> isPrivate(classCandidate), () -> failure(
                                       "no support for private classes" + classCandidate.getSimpleName())),
                                   matchCase(() -> isAnnotated(classCandidate, NanoTestClass.class),
                                             () -> success(Boolean.TRUE))).ifFailed(
        msg -> Logger.getLogger(NanoEngine.class)
                     .info(msg))
                                                                          .ifPresent(
                                                                              b -> Logger.getLogger(NanoEngine.class)
                                                                                         .info("selected class "
                                                                                               + classCandidate))
                                                                          .getOrElse(() -> Boolean.FALSE);
  }

  protected static Predicate<Method> isTestMethod() {
    return method -> {
      if (ReflectionUtils.isStatic(method)) return false;
      if (ReflectionUtils.isPrivate(method)) return false;
      if (ReflectionUtils.isAbstract(method)) return false;
      if (method.getParameterCount() > 0) return false;
      return AnnotationSupport.isAnnotated(method, NanoTest.class) && method.getReturnType()
                                                                            .equals(void.class);
    };
  }

  @Override
  public String getId() {
    return ENGINE_ID;
  }

  @Override
  public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId engineID) {
    EngineDescriptor rootNode = new EngineDescriptor(engineID, "The NanoEngine");

    //TODO https://github.com/junit-team/junit5/issues/2001
    request.getSelectorsByType(ClasspathRootSelector.class)
//           .forEach(selector -> resolver().get().resolve(request,rootNode));
           .forEach(selector -> appendTestInRoot(rootNode, selector));

    request.getSelectorsByType(PackageSelector.class)
           .forEach(selector -> appendTestInPackage(selector.getPackageName(), rootNode));

    request.getSelectorsByType(ClassSelector.class)
           .forEach(classSelector -> appendTestInClass(classSelector.getJavaClass(), rootNode));

    request.getSelectorsByType(MethodSelector.class)
           .forEach(selector -> appendTestInMethod(selector.getJavaMethod(), rootNode));

    return rootNode;
  }

  private void appendTestInRoot(EngineDescriptor rootNode, ClasspathRootSelector selector) {
    URI classpathRoot = selector.getClasspathRoot();
    ReflectionUtils.findAllClassesInClasspathRoot(classpathRoot, isTestClass(), (name) -> true)
                   .forEach(clazz -> appendTestInClass(clazz, rootNode));
  }

  private void appendTestInMethod(Method javaMethod, EngineDescriptor rootNode) {
    Class<?> declaringClass = javaMethod.getDeclaringClass();
    if (isTestClass().test(declaringClass)) {
      final NanoEngineMethodTestDescriptor child = new NanoEngineMethodTestDescriptor(javaMethod, declaringClass,
                                                                                      rootNode.getUniqueId());
      rootNode.addChild(child);
    }
  }

  private void appendTestInClass(Class<?> javaClass, EngineDescriptor rootNode) {
    if (isTestClass().test(javaClass)) rootNode.addChild(
        new NanoEngineClassTestDescriptor(javaClass, rootNode.getUniqueId()));
  }

  private void appendTestInPackage(String packageName, EngineDescriptor rootNode) {

    ReflectionSupport.findAllClassesInPackage(packageName, isTestClass(), name -> true)
                     .stream()
                     .peek((e) -> logger().info("class in package -> " + e.getSimpleName()))
                     .map(javaClass -> new NanoEngineClassTestDescriptor(javaClass, rootNode.getUniqueId()))
                     .forEach(rootNode::addChild);
  }

  @Override
  public void execute(ExecutionRequest request) {
    TestDescriptor rootNode = request.getRootTestDescriptor();
    new NanoEngineTestExecutor().execute(request, rootNode);
  }
}