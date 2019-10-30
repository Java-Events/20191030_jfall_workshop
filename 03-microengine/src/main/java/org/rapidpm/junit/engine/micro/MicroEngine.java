package org.rapidpm.junit.engine.micro;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.discovery.PackageSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver;
import org.junit.platform.engine.support.discovery.SelectorResolver;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.dependencies.core.logger.Logger;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;
import static org.junit.platform.commons.util.ReflectionUtils.isAbstract;
import static org.junit.platform.commons.util.ReflectionUtils.isPrivate;
import static org.junit.platform.engine.support.discovery.SelectorResolver.Resolution.unresolved;
import static org.rapidpm.frp.matcher.Case.match;
import static org.rapidpm.frp.matcher.Case.matchCase;
import static org.rapidpm.frp.model.Result.failure;
import static org.rapidpm.frp.model.Result.success;

public class MicroEngine
    implements TestEngine, HasLogger {

  public static final String ENGINE_ID                = MicroEngine.class.getSimpleName();
  public static final String TEST_ENGINE_DISPLAY_NAME = "The MicroEngine";

  private final Weld weld = new Weld();

  @Override
  public String getId() {
    return ENGINE_ID;
  }

  protected static Predicate<Class<?>> isTestClass() {
    return classCandidate -> match(matchCase(
        () -> failure("this class is not a supported by this TestEngine - " + classCandidate.getSimpleName())),
                                   matchCase(() -> isAbstract(classCandidate), () -> failure(
                                       "no support for abstract classes" + classCandidate.getSimpleName())),
                                   matchCase(() -> isPrivate(classCandidate), () -> failure(
                                       "no support for private classes" + classCandidate.getSimpleName())),
                                   matchCase(() -> isAnnotated(classCandidate, MicroTestClass.class),
                                             () -> success(Boolean.TRUE))).ifFailed(
        msg -> Logger.getLogger(MicroEngine.class)
                     .info(msg))
                                                                          .ifPresent(
                                                                              b -> Logger.getLogger(MicroEngine.class)
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
      return AnnotationSupport.isAnnotated(method, MicroTest.class) && method.getReturnType()
                                                                             .equals(void.class);
    };
  }

  @Override
  public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId engineID) {
    EngineDescriptor rootNode = new EngineDescriptor(engineID, TEST_ENGINE_DISPLAY_NAME);

    request.getSelectorsByType(ClasspathRootSelector.class)
           .forEach(selector -> resolver().get()
                                          .resolve(request, rootNode));

    request.getSelectorsByType(PackageSelector.class)
           .forEach(selector -> appendTestInPackage(selector.getPackageName(), rootNode));

    request.getSelectorsByType(ClassSelector.class)
           .forEach(classSelector -> appendTestInClass(classSelector.getJavaClass(), rootNode));

    request.getSelectorsByType(MethodSelector.class)
           .forEach(selector -> appendTestInMethod(selector.getJavaMethod(), rootNode));

    return rootNode;
  }

  private void appendTestInMethod(Method javaMethod, EngineDescriptor rootNode) {
    Class<?> declaringClass = javaMethod.getDeclaringClass();
    if (isTestClass().test(declaringClass)) rootNode.addChild(
        new MicroEngineMethodTestDescriptor(javaMethod, declaringClass, rootNode.getUniqueId()));
  }

  private void appendTestInClass(Class<?> javaClass, EngineDescriptor rootNode) {
    if (isTestClass().test(javaClass)) rootNode.addChild(
        new MicroEngineClassTestDescriptor(javaClass, rootNode.getUniqueId()));
  }

  private void appendTestInPackage(String packageName, EngineDescriptor rootNode) {
    ReflectionSupport.findAllClassesInPackage(packageName, isTestClass(), name -> true)
                     .stream()
                     .peek((e) -> logger().info("class in package -> " + e.getSimpleName()))
                     .map(javaClass -> new MicroEngineClassTestDescriptor(javaClass, rootNode.getUniqueId()))
                     .forEach(rootNode::addChild);
  }

  @Override
  public void execute(ExecutionRequest request) {
    TestDescriptor rootNode  = request.getRootTestDescriptor();
    WeldContainer  container = weld.initialize();
    new MicroEngineTestExecutor(container).execute(request, rootNode);
    container.shutdown();
  }


  private Supplier<EngineDiscoveryRequestResolver<TestDescriptor>> resolver() {
    return () -> EngineDiscoveryRequestResolver.builder()
                                               .addClassContainerSelectorResolver(isTestClass())
                                               .addSelectorResolver(context -> {
                                                 final Predicate<String> classNameFilter = context.getClassNameFilter();
                                                 return new SelectorResolver() {
                                                   @Override
                                                   public Resolution resolve(ClassSelector selector, Context context) {
                                                     return (!classNameFilter.test(selector.getJavaClass()
                                                                                           .getName()))
                                                            ? unresolved()
                                                            : resolveTestClass(selector.getJavaClass(), context);
                                                   }

                                                   private Resolution resolveTestClass(Class<?> testClass,
                                                                                       Context context) {
                                                     return context.addToParent(parent -> Optional.of(
                                                         new MicroEngineClassTestDescriptor(testClass,
                                                                                            parent.getUniqueId())))
                                                                   .map(d -> Match.exact(d, Collections::emptySet))
                                                                   .map(Resolution::match)
                                                                   .orElse(unresolved());
                                                   }
                                                 };
                                               })
                                               .addTestDescriptorVisitor(context -> TestDescriptor::prune)
                                               .build();
  }
}
