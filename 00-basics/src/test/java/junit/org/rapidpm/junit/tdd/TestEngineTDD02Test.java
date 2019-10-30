package junit.org.rapidpm.junit.tdd;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.platform.testkit.engine.Events;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.testkit.engine.EventConditions.*;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.instanceOf;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.message;

public class TestEngineTDD02Test {
  public static class DemoTestClass {
    @Test
    void test001() { }

    @Test
    @Disabled //enable for testing TestEngine ;-)
    void test002() { throw new RuntimeException("I must fail");}

    @Test
    @Disabled
    void test003() { }
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


  @Test
  @Disabled //enable for testing TestEngine ;-)
  void verifyTest002() {
    final String methodName = "test002";
    final Events events = EngineTestKit.engine("junit-jupiter")
                                       .selectors(selectMethod(DemoTestClass.class, methodName))
                                       .execute()
                                       .tests();

    events.assertStatistics(stats -> stats.failed(1));

    events.assertThatEvents()
          .haveExactly(1, event(test(methodName),
                                finishedWithFailure(
                                    instanceOf(RuntimeException.class),
                                    message("I must fail"))));
  }
  @Test
  void verifyTest003() {
    final String methodName = "test003";
    final Events events = EngineTestKit.engine("junit-jupiter")
                                       .selectors(selectMethod(DemoTestClass.class, methodName))
                                       .execute()
                                       .tests();

    events.assertStatistics(stats -> stats.skipped(1));

    events.assertThatEvents()
          .haveExactly(1, event(test(methodName),
                                skippedWithReason((msg)-> msg.contains("@Disabled"))));
  }
}
