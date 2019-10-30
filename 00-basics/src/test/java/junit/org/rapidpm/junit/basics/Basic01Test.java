package junit.org.rapidpm.junit.basics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;

public class Basic01Test
    implements BeforeAllCallback, BeforeEachCallback,
               AfterAllCallback, AfterEachCallback {

  @Override
  public void beforeAll(ExtensionContext extensionContext) throws Exception { }

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception { }

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception { }


  @Override
  public void afterAll(ExtensionContext extensionContext) throws Exception { }

  @Test
  void test001() { }
}
