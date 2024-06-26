package org.vansama.ctuhc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NotNullProcessor {

    public static void validate(Object instance) {
        Class<?> clazz = instance.getClass();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(NotNull.class)) {
                // 检查方法参数是否被 @NotNull 注解
                for (int i = 0; i < method.getParameters().length; i++) {
                    if (method.getParameters()[i].isAnnotationPresent(NotNull.class)) {
                        try {
                            // 调用方法并检查参数值
                            method.invoke(instance);
                            Object[] parameters = new Object[method.getParameters().length];
                            for (int j = 0; j < parameters.length; j++) {
                                parameters[j] = getParamFromInvocation(method, i);
                            }
                            for (Object param : parameters) {
                                if (param == null) {
                                    throw new IllegalArgumentException(method.getParameters()[i].getAnnotation(NotNull.class).value());
                                }
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static Object getParamFromInvocation(Method method, int paramIndex) {
        // 这是一个示例实现，具体细节取决于您的应用程序如何传递参数
        // 您可能需要根据实际情况来获取方法调用的参数值
        return null;
    }
}