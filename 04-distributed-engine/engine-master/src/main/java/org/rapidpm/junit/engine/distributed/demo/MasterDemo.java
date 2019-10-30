package org.rapidpm.junit.engine.distributed.demo;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MasterDemo {

  private MasterDemo() {
  }

  public static void main(String[] args) {

//    ClientConfig        clientConfig = new ClientConfig();
//    HazelcastInstance   client       = HazelcastClient.newHazelcastClient(clientConfig);
    Config               cfg          = new Config();
    HazelcastInstance    client       = Hazelcast.newHazelcastInstance(cfg);
    IMap<String, byte[]> mapOfClasses = client.getMap("mapOfClasses");

    //warm UP
    mapOfClasses.put(DemoClassA.class.getName(), loadClassFromFile(DemoClassA.class.getName()));
    mapOfClasses.put(DemoClassB.class.getName(), loadClassFromFile(DemoClassB.class.getName()));
    mapOfClasses.put(DemoClassC.class.getName(), loadClassFromFile(DemoClassC.class.getName()));

//    ReflectionUtils.findAllClassesInClasspathRoot()

    //wait



  }

  private static byte[] loadClassFromFile(String fileName) {
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
}
