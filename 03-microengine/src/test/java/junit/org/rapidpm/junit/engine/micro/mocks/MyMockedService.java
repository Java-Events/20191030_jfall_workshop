package junit.org.rapidpm.junit.engine.micro.mocks;

import org.rapidpm.dependencies.core.logger.HasLogger;

import javax.enterprise.context.Dependent;

@Dependent
public class MyMockedService implements HasLogger {

  public MyMockedService() {
    logger().info(this.getClass().getSimpleName() + " will be created now");
  }

  public String doSomeWork(){
    return "someWork";
  }
}
