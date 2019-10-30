package junit.org.rapidpm.junit.engine.micro;


import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.junit.engine.micro.MicroTest;
import org.rapidpm.junit.engine.micro.MicroTestClass;

@MicroTestClass(forceRandomExecution = true, useCDI = false)
public class ForceRandomExecTest
    implements HasLogger {

  //Test this with the Testkit !!!

  @MicroTest
  void test001() {
    logger().info("test001");
  }

  @MicroTest
  void test002() {
    logger().info("test002");
  }

  @MicroTest
  void test003() {
    logger().info("test003");
  }

  @MicroTest
  void test004() {
    logger().info("test004");
  }

  @MicroTest
  void test005() {
    logger().info("test005");
  }

  @MicroTest
  void test006() {
    logger().info("test006");
  }

  @MicroTest
  void test007() {
    logger().info("test007");
  }

  @MicroTest
  void test008() {
    logger().info("test008");
  }

  @MicroTest
  void test009() {
    logger().info("test009");
  }

  @MicroTest
  void test010() {
    logger().info("test010");
  }
}
