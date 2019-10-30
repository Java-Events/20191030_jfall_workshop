package junit.org.rapidpm.junit.basics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import org.rapidpm.frp.model.serial.Pair;


public class Basic05Test {

  public static class Demo
      extends Pair<Integer, String> {
    public Demo(Integer id, String value) {
      super(id, value);
    }

    public Integer id() {
      return getT1();
    }

    public String value() {
      return getT2();
    }
  }

  public static class DemoParameterResolver
      implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
      final Class<?> type = parameterContext.getParameter()
                                            .getType();
      return Demo.class.isAssignableFrom(type);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
      return new Demo(1, "Hello World");
    }
  }


  @Test
  @ExtendWith(Basic05Test.DemoParameterResolver.class)
  void test001(Demo demo){
    Assertions.assertEquals("Hello World", demo.value());
  }

}
