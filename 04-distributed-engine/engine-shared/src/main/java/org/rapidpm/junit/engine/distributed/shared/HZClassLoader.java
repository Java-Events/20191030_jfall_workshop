package org.rapidpm.junit.engine.distributed.shared;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.rapidpm.dependencies.core.logger.HasLogger;

public class HZClassLoader
    extends ClassLoader
    implements HasLogger {

  public static final String CLASS_PREFIX = "org.rapidpm.junit";

  public HZClassLoader(ClassLoader parent) {
    super(parent);
    System.out.println("HZClassLoader .. (with parent)");
  }

  public HZClassLoader() {
    super();
    System.out.println("HZClassLoader .. ");
  }


  private transient HazelcastInstance    hazelcastInstance;
  private           IMap<String, byte[]> mapOfClasses;

  public HZClassLoader(HazelcastInstance hazelcastInstance) {
    System.out.println("HZClassLoader .. hazelcastInstance");
    this.hazelcastInstance = hazelcastInstance;
    this.mapOfClasses      = hazelcastInstance.getMap("mapOfClasses");
  }

  @Override
  protected Class<?> findClass(String name) {
    if (name.contains(CLASS_PREFIX)) logger().info("findClass - " + name);
    byte[] bytes = loadClassFromFile(name);
    try {
      return (name.contains(CLASS_PREFIX))
             ? defineClass(name, bytes, 0, bytes.length)
             : ClassLoader.getSystemClassLoader()
                          .loadClass(name);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException(e);

    }
  }


  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    Class<?> aClass = findClass(name);
    if (aClass == null) aClass = super.findClass(name);
    if (name.contains(CLASS_PREFIX)) System.out.println("loadClass(String name .. " + name);
    return aClass;
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    Class<?> aClass = loadClass(name);
    if (name.contains(CLASS_PREFIX)) System.out.println("loadClass(String name, boolean resolve .. " + name);
    return aClass;
  }

  private byte[] loadClassFromFile(String fileName) {
    byte[] bytes = mapOfClasses.get(fileName);
    return bytes;

  }
}
