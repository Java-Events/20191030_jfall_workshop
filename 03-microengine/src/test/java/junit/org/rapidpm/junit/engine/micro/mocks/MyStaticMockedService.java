package junit.org.rapidpm.junit.engine.micro.mocks;

import org.rapidpm.dependencies.core.logger.HasLogger;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
public class MyStaticMockedService
    implements HasLogger {

  private Instant created = Instant.now();

  public MyStaticMockedService() {
    logger().info(this.getClass()
                      .getSimpleName() + ".. will be created now ..");


  }

  public Instant getCreated() {
    return created;
  }
}
