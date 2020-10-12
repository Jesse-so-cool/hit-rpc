package com.jesse.annotation;

import java.lang.annotation.*;

/**
 * TODO
 *
 * @author jesse hsj
 * @date 2020/10/12 15:37
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HitService {
    String version() default "";
}
