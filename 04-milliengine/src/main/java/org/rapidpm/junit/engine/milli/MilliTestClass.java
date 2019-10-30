package org.rapidpm.junit.engine.milli;


import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface MilliTestClass {
  //params
  boolean forceRandomExecution() default false;
  boolean useCDI() default false;
}
