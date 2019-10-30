package org.rapidpm.junit.engine.useless;

import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.rapidpm.dependencies.core.logger.HasLogger;

public class UselessEngineTestDescriptor
    extends AbstractTestDescriptor
    implements HasLogger {

  public UselessEngineTestDescriptor(UniqueId uniqueId, String displayName) {
    super(uniqueId.append("useless", displayName), displayName);
  }

  public UselessEngineTestDescriptor(UniqueId uniqueId, String displayName, TestSource source) {
    super(uniqueId.append("useless", displayName), displayName, source);
  }

  @Override
  public Type getType() {
    return Type.TEST;
  }
}
