package org.vansama.ctuhc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解在运行时可见
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD}) // 可用于方法、方法参数和字段
public @interface NotNull {
    String value() default "Cannot be null";
}
