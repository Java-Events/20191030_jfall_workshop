package junit.org.rapidpm.junit.basics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.Extensions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Basic03Test {
  public static class MyExtensionA
      implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception { }
  }

  public static class MyExtensionB
      implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception { }
  }

  @ExtendWith(Basic03Test.MyExtensionB.class)
  @ExtendWith(Basic03Test.MyExtensionA.class)
  public static class ExtendedTestClass {
    @Test
    void test001() { }
  }

  @Target({ ElementType.TYPE, ElementType.METHOD })
  @Retention(RetentionPolicy.RUNTIME)
  @Extensions({
   @ExtendWith({Basic03Test.MyExtensionB.class}),
   @ExtendWith({Basic03Test.MyExtensionA.class})
  })
  public @interface BothExtensions { }

  @BothExtensions
  public static class MyExtendedTestClass {
    @Test
    void test001() { }
  }
}


