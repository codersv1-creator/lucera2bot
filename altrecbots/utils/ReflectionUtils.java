/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.lucera2.scripts.altrecbots.utils;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.util.function.BiPredicate;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.reflect.FieldUtils;

public class ReflectionUtils {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T, V> boolean forEachField(final T t, BiPredicate<Field, Mutable<V>> biPredicate) throws Exception {
        Object t2 = t;
        synchronized (t2) {
            return AccessController.doPrivileged(() -> {
                Class<?> clazz = t.getClass();
                for (final Field field : FieldUtils.getAllFields(clazz)) {
                    final boolean bl = field.isAccessible();
                    try {
                        if (biPredicate.test(field, new Mutable<V>(){

                            @Override
                            public V getValue() {
                                try {
                                    if (!bl) {
                                        field.setAccessible(true);
                                    }
                                    return field.get(t);
                                } catch (Exception exception) {
                                    throw new RuntimeException(exception);
                                }
                            }

                            @Override
                            public void setValue(V v) {
                                try {
                                    if (!bl) {
                                        field.setAccessible(true);
                                    }
                                    field.set(t, v);
                                } catch (Exception exception) {
                                    throw new RuntimeException(exception);
                                }
                            }
                        })) continue;
                        Boolean bl2 = false;
                        return bl2;
                    } finally {
                        if (!bl && field.isAccessible()) {
                            field.setAccessible(false);
                        }
                    }
                }
                return true;
            });
        }
    }
}

