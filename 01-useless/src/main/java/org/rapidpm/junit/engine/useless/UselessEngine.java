package org.rapidpm.junit.engine.useless;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.rapidpm.dependencies.core.logger.HasLogger;

import static org.junit.platform.engine.TestExecutionResult.successful;

public class UselessEngine
    implements TestEngine, HasLogger {
  @Override
  public String getId() {
    return UselessEngine.class.getSimpleName();
  }

  @Override
  public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId engineID) {
    EngineDescriptor rootNode = new EngineDescriptor(engineID, "The UselessEngine");
    //inject test tree nodes - TestDescriptors
    return rootNode;
  }

  @Override
  public void execute(ExecutionRequest request) {
    TestDescriptor          engine   = request.getRootTestDescriptor();
    EngineExecutionListener listener = request.getEngineExecutionListener();
    listener.executionStarted(engine);
    engine.getChildren()
          .forEach(child -> {
            listener.executionStarted(child);
            listener.executionFinished(child, successful());
          });
    listener.executionFinished(engine, successful());
  }
}

//will come from somewhere
//    UselessEngineTestDescriptor test001 = new UselessEngineTestDescriptor(engineID, "useless test001");
//    rootNode.addChild(test001);