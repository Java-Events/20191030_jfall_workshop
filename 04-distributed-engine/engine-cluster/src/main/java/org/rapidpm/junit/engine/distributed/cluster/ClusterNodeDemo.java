package org.rapidpm.junit.engine.distributed.cluster;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class ClusterNodeDemo {

  private ClusterNodeDemo() {
  }

  public static IMap<String, byte[]> mapOfClasses;

  public static void main(String[] args) {
    Config            cfg      = new Config();
    HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
    mapOfClasses = instance.getMap("mapOfClasses");



  }
}
