package junit.org.rapidpm.junit.basics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.rapidpm.dependencies.core.logger.HasLogger;

public class Basic04Test {

  public static class MyExtension
      implements BeforeEachCallback, AfterEachCallback, HasLogger {

    public static final String KEY = "KEY";

    @Override
    public void beforeEach(ExtensionContext ctx) throws Exception {
      final ExtensionContext.Store store = ctx.getStore(Namespace.GLOBAL);
      //create and start something
      store.put(KEY, "something");
      logger().info("beforeEach");
    }

    @Override
    public void afterEach(ExtensionContext ctx) throws Exception {
      final ExtensionContext.Store store = ctx.getStore(Namespace.GLOBAL);
      //stop something again
      final String value = store.get(KEY, String.class);
      logger().info("afterEach");
    }

    @Test
    @ExtendWith(MyExtension.class)
    void test001() {
      //possible to annotate only a method
    }
  }


}
