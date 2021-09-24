package com.example.demo;

import static org.mockito.ArgumentMatchers.booleanThat;

import java.lang.reflect.Field;

public class TestUtils {
    public static void injectDependency(Object target, String fieldName, Object toInject) {
        boolean isFieldPrivate = false;

        try {
            Field injectionTargetField  = target.getClass().getDeclaredField(fieldName);
        
            if (!injectionTargetField.isAccessible()) {
                injectionTargetField.setAccessible(true);
                isFieldPrivate = true;
            }
            injectionTargetField.set(target, toInject);

            if(isFieldPrivate) {
                injectionTargetField.setAccessible(false);
            }

        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

