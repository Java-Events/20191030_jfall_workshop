package junit.org.rapidpm.junit.engine.micro;

import junit.org.rapidpm.junit.engine.micro.mocks.MyMockedService;
import org.junit.jupiter.api.Assertions;
import org.rapidpm.junit.engine.micro.MicroTest;
import org.rapidpm.junit.engine.micro.MicroTestClass;

import javax.inject.Inject;

@MicroTestClass(useCDI = true)
public class FirstMicroTest {

  @Inject
  private MyMockedService service;

  @MicroTest
  void test001_A() {
    Assertions.assertEquals("someWork", service.doSomeWork());
  }
}
