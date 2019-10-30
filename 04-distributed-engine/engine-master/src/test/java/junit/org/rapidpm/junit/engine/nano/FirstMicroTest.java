package junit.org.rapidpm.junit.engine.nano;

import org.rapidpm.junit.engine.distributed.demo.DemoClassA;
import org.rapidpm.junit.engine.distributed.engine.DistributedTest;
import org.rapidpm.junit.engine.distributed.engine.DistributedTestClass;

@DistributedTestClass
public class FirstMicroTest {

  @DistributedTest
  public void test001_A() {
    new DemoClassA().doSomething();
  }

  @DistributedTest
  public void test001_B() {

  }

  //@DistributedTest
  void test002() {

  }
}
