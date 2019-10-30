package junit.org.rapidpm.junit.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rapidpm.junit.example.LoginService;

public class DataSourceTest {


//  @Test
  void test01(LoginService service){
    Assertions.assertTrue(service.checkLogin("admin", "admin"));
  }

}
