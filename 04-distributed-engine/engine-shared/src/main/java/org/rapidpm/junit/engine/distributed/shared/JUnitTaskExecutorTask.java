package org.rapidpm.junit.engine.distributed.shared;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

public class JUnitTaskExecutorTask
    implements Callable<String>, Serializable, HazelcastInstanceAware {

  String className;
  String methodName;

  public JUnitTaskExecutorTask() {
  }

  public JUnitTaskExecutorTask(String className, String methodName) {
    this.className  = className;
    this.methodName = methodName;
  }

  private transient HazelcastInstance hazelcastInstance;

  @Override
  public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
    this.hazelcastInstance = hazelcastInstance;
  }

  @Override
  public String call() throws Exception {
    try {
      HZClassLoader classLoader = new HZClassLoader(hazelcastInstance);
      Class<?>      aClass      = classLoader.findClass(className);
      Object        newInstance = aClass.newInstance();
      aClass.getMethod(methodName)
            .invoke(newInstance, null);
      return "SUCCESS";
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      return "ERROR";
    } catch (InstantiationException e) {
      e.printStackTrace();
      return "ERROR";
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      return "ERROR";
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      return "ERROR";
    }
  }
}
