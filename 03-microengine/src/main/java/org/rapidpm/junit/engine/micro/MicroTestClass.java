package org.rapidpm.junit.engine.micro;


import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface MicroTestClass {
  boolean forceRandomExecution() default false;
  boolean useCDI() default false;
}
