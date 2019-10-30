package org.rapidpm.junit.engine.nano;


import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface NanoTestClass {
//params
}
