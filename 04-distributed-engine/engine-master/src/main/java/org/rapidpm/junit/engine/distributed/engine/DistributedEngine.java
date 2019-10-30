package org.rapidpm.junit.engine.distributed.engine;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.discovery.PackageSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.junit.engine.distributed.demo.DemoClassA;
import org.rapidpm.junit.engine.distributed.demo.DemoClassB;
import org.rapidpm.junit.engine.distributed.demo.DemoClassC;
import org.rapidpm.junit.engine.distributed.demo.MasterDemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;
import static org.junit.platform.commons.util.ReflectionUtils.isAbstract;
import static org.junit.platform.commons.util.ReflectionUtils.isPrivate;
import static org.rapidpm.frp.matcher.Case.match;
import static org.rapidpm.frp.matcher.Case.matchCase;
import static org.rapidpm.frp.model.Result.failure;
import static org.rapidpm.frp.model.Result.success;

public class DistributedEngine
    implements TestEngine, HasLogger {

  public static final String ENGINE_ID = DistributedEngine.class.getSimpleName();

  @Override
  public String getId() {
    return ENGINE_ID;
  }


  private Predicate<Class<?>> checkClass() {
    return classCandidate -> match(matchCase(
        () -> failure("this class is not a supported by this TestEngine - " + classCandidate.getSimpleName())),
                                   matchCase(() -> isAbstract(classCandidate), () -> failure(
                                       "no support for abstract classes" + classCandidate.getSimpleName())),
                                   matchCase(() -> isPrivate(classCandidate), () -> failure(
                                       "no support for private classes" + classCandidate.getSimpleName())),
                                   matchCase(() -> isAnnotated(classCandidate, DistributedTestClass.class),
                                             () -> success(Boolean.TRUE))).ifFailed(msg -> logger().info(msg))
                                                                          .ifPresent(b -> logger().info(
                                                                              "selected class " + classCandidate))
                                                                          .getOrElse(() -> Boolean.FALSE);
  }

  @Override
  public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId engineID) {


    prepareCluster();


    EngineDescriptor rootNode = new EngineDescriptor(engineID, "The DistributedEngine");

    request.getSelectorsByType(ClasspathRootSelector.class)
           .forEach(selector -> {
             URI classpathRoot = selector.getClasspathRoot();
             ReflectionUtils.findAllClassesInClasspathRoot(classpathRoot, checkClass(), (name) -> true)
                            .forEach(clazz -> appendTestInClass(clazz, rootNode));
           });

    request.getSelectorsByType(PackageSelector.class)
           .forEach(selector -> appendTestInPackage(selector.getPackageName(), rootNode));

    request.getSelectorsByType(ClassSelector.class)
           .forEach(classSelector -> appendTestInClass(classSelector.getJavaClass(), rootNode));

    request.getSelectorsByType(MethodSelector.class)
           .forEach(selector -> appendTestInMethod(selector.getJavaMethod(), rootNode));

    return rootNode;
  }

  //  private Config               cfg               = new Config();
//  private HazelcastInstance    hazelcastInstance = Hazelcast.newHazelcastInstance(cfg);
  //TODO IP´s via properties

  ClientConfig      clientConfig      = new ClientConfig().setNetworkConfig(
//      new ClientNetworkConfig().setAddresses(asList("192.168.1.8")));
      new ClientNetworkConfig().setAddresses(asList("10.64.173.238")));
  HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
  private IMap<String, byte[]> mapOfClasses = hazelcastInstance.getMap("mapOfClasses");

  private void prepareCluster() {

    //warm UP -prod classes
    mapOfClasses.put(DemoClassA.class.getName(), loadClassFromFile(DemoClassA.class.getName()));
    mapOfClasses.put(DemoClassB.class.getName(), loadClassFromFile(DemoClassB.class.getName()));
    mapOfClasses.put(DemoClassC.class.getName(), loadClassFromFile(DemoClassC.class.getName()));

    //warm up test classes

  }

  private void addTestClassToCluster(Class<?> testClass) {
    mapOfClasses.put(testClass.getName(), loadClassFromFile(testClass.getName()));
  }

  private byte[] loadClassFromFile(String fileName) {
    InputStream inputStream = MasterDemo.class.getClassLoader()
                                              .getResourceAsStream(
                                                  fileName.replace('.', File.separatorChar) + ".class");
    byte[]                buffer;
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    int                   nextValue  = 0;
    try {
      while ((nextValue = inputStream.read()) != -1) {
        byteStream.write(nextValue);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    buffer = byteStream.toByteArray();
    return buffer;
  }


  private void appendTestInMethod(Method javaMethod, EngineDescriptor rootNode) {
    Class<?> declaringClass = javaMethod.getDeclaringClass();
    if (checkClass().test(declaringClass)) {
      addTestClassToCluster(declaringClass);
      rootNode.addChild(new DistributedEngineMethodTestDescriptor(javaMethod, declaringClass,
                                                                  new DistributedEngineClassTestDescriptor(
                                                                      declaringClass, rootNode)));
    }
  }

  private void appendTestInClass(Class<?> javaClass, EngineDescriptor rootNode) {
    if (checkClass().test(javaClass)) {
      addTestClassToCluster(javaClass);
      rootNode.addChild(new DistributedEngineClassTestDescriptor(javaClass, rootNode));
    }
  }

  private void appendTestInPackage(String packageName, EngineDescriptor rootNode) {

    ReflectionSupport.findAllClassesInPackage(packageName, checkClass(), name -> true)
                     .stream()
                     .peek((e) -> logger().info("class in package -> " + e.getSimpleName()))
                     .peek(this::addTestClassToCluster)
                     .map(javaClass -> new DistributedEngineClassTestDescriptor(javaClass, rootNode))
                     .forEach(rootNode::addChild);
  }

  @Override
  public void execute(ExecutionRequest request) {
    TestDescriptor rootNode = request.getRootTestDescriptor();
    new DistributedEngineTestExecutor(hazelcastInstance).execute(request, rootNode);

    hazelcastInstance.shutdown();
  }
}
