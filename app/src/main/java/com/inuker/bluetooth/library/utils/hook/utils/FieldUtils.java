package com.inuker.bluetooth.library.utils.hook.utils;

import com.inuker.bluetooth.library.utils.StringUtils;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
public class FieldUtils {
    public static Field getDeclaredField(Class<?> cls, String str, boolean z) {
        Validate.isTrue(cls != null, "The class must not be null", new Object[0]);
        Validate.isTrue(StringUtils.isNotBlank(str), "The field name must not be blank/empty", new Object[0]);
        try {
            Field declaredField = cls.getDeclaredField(str);
            if (!MemberUtils.isAccessible(declaredField)) {
                if (!z) {
                    return null;
                }
                declaredField.setAccessible(true);
            }
            return declaredField;
        } catch (NoSuchFieldException unused) {
            return null;
        }
    }
}
