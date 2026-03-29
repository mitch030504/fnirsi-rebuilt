package com.inuker.bluetooth.library.utils.hook.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class HookUtils {
    public static Class<?> getClass(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(Class<?> cls, String str, Class<?>... clsArr) {
        return MethodUtils.getAccessibleMethod(cls, str, clsArr);
    }

    public static Field getField(Class<?> cls, String str) {
        if (cls != null) {
            return FieldUtils.getDeclaredField(cls, str, true);
        }
        return null;
    }

    public static <T> T getValue(Field field) {
        return (T) getValue(field, null);
    }

    public static <T> T getValue(Field field, Object obj) {
        if (field == null) {
            return null;
        }
        try {
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T invoke(Method method, Object obj, Object... objArr) {
        try {
            return (T) method.invoke(obj, objArr);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
            return null;
        }
    }
}
