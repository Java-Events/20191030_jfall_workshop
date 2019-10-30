package org.rapidpm.junit.engine.distributed.demo;

import org.rapidpm.dependencies.core.logger.HasLogger;

public class DemoClassA
    implements HasLogger {

  private DemoClassB demoClassB = new DemoClassB();

  private DemoClassC demoClassC;

  public DemoClassA() {
    logger().info(this.getClass()
                      .getSimpleName() + " - " + this.getClass()
                                                     .getClassLoader()
                                                     .getClass()
                                                     .getSimpleName());
    demoClassC = new DemoClassC();

    logger().info("start with new DemoClassC() ");
    //is using own ClassLoader
    DemoClassC demoClassC2 = new DemoClassC();
    logger().info("stop with new DemoClassC() ");
  }


  public void doSomething() {
    logger().info("start with doSomething -new DemoClassC() ");
    DemoClassC demoClassC2 = new DemoClassC();
//    IntStream.range(0,100).forEach(i -> logger().info("stop with doSomething loop " + i));

    logger().info("stop with doSomething DEMO MSG XXX");
    logger().info("stop with doSomething -new DemoClassC() ");
  }
}
