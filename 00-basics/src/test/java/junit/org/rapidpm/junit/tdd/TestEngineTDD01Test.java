package junit.org.rapidpm.junit.tdd;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestEngineTDD01Test {

  public static class DemoTestClass {
    @Test
    void test001() { }

    @Test
    @Disabled //enable for testing TestEngine ;-)
    void test002() {  throw new RuntimeException("I must fail");}

    @Test
    @Disabled
    void test003() { }
  }

  @Test
  void verifyJupiterContainerStats() {
    EngineTestKit.engine("junit-jupiter")
                 .selectors(selectClass(DemoTestClass.class))
                 .execute()
                 .containers()
                 .assertStatistics(stats -> stats.started(2)
                                                 .succeeded(2)
                                                 .skipped(0));
  }

  @Test
  @Disabled //enable for testing TestEngine ;-)
  void verifyJupiterTestStats() {
    EngineTestKit.engine("junit-jupiter")
                 .selectors(selectClass(DemoTestClass.class))
                 .execute()
                 .tests()
                 .assertStatistics(stats -> stats.started(2)
                                                 .succeeded(1)
                                                 .skipped(1)
                                                 .failed(1));
  }


}
