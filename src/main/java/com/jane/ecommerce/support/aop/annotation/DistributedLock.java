package com.jane.ecommerce.support.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    // Lock 이름
    String key();

    // Lock 시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    // Lock 대기 시간
    long waitTime() default 5L;

    // Lock 유지 시간
    long leaseTime() default 3L;
}
