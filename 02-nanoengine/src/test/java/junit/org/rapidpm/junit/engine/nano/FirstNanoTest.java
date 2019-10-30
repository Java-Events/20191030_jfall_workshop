package junit.org.rapidpm.junit.engine.nano;

import org.rapidpm.junit.engine.nano.NanoTest;
import org.rapidpm.junit.engine.nano.NanoTestClass;

@NanoTestClass
public class FirstNanoTest {
  @NanoTest
  void test001_A() { }

  @NanoTest
  void test001_B() { }

  //@NanoTest
  void test002() { }
}
