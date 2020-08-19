package com.skillbox.socialnetwork.main.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodLogWithTime {
    String message() default "";
    String fullMessage() default "";
    boolean userAuth() default false;
    String exceptionMessage() default  "";
}
