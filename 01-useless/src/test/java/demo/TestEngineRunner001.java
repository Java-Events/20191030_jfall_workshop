package demo;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;
import java.util.stream.Collectors;

import static java.lang.System.out;

public class TestEngineRunner001 {

  public static void main(String[] args) {
    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                                                                      .build();
    Launcher launcher = LauncherFactory.create();
    TestPlan testPlan = launcher.discover(request);
    // Register a listener of your choice
    SummaryGeneratingListener listener = new SummaryGeneratingListener();
    launcher.registerTestExecutionListeners(listener);
    launcher.execute(request);

    TestExecutionSummary summary = listener.getSummary();
    summary.printTo(new PrintWriter(out));
    testPlan.getRoots()
            .stream()
            .filter(TestIdentifier::isContainer)
            .map(TestIdentifier::getDisplayName)
            .map(name -> "TestEngineName: " + name)
            .collect(Collectors.toList())
            .forEach(out::println);
  }
}
