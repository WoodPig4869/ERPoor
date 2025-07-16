package org.allen.erpoor.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)             // 這個註解只能放在方法上
@Retention(RetentionPolicy.RUNTIME)     // 保留到執行階段，讓 AOP 可以偵測到
public @interface SetAppUser {
}