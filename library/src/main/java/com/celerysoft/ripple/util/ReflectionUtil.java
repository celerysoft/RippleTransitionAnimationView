package com.celerysoft.ripple.util;

import android.util.Log;

import com.celerysoft.ripple.BuildConfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Celery on 16/7/14.
 * Util of Java reflection
 */
public class ReflectionUtil {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "ReflectionUtil";

    @SuppressWarnings("unused")
    public static Field[] getAllDeclaredFields(Object object) {
        int declaredFieldsCount = 0;

        ArrayList<Field[]> fieldList = new ArrayList<>();
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            Field[] declaredFields = clazz.getDeclaredFields();
            declaredFieldsCount += declaredFields.length;
            fieldList.add(declaredFields);
        }

        Field[] allDeclaredFields = new Field[declaredFieldsCount];
        int count = 0;

        for (int i = 0; i < fieldList.size(); ++i) {
            Field[] declaredFields = fieldList.get(i);
            for (Field field : declaredFields) {
                allDeclaredFields[count] = field;
                count++;
            }
        }

        return allDeclaredFields;
    }

    @SuppressWarnings("unused")
    public static Field getDeclaredField(Object object, String fieldName) {
        for(Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                //e.printStackTrace();
                // no such field in current class, find this field on its superclass
            }
        }

        if (DEBUG) {
            Log.e(TAG, "Could not find the field with name '" + fieldName + "' in '" + object.getClass().getSimpleName() + "' and its superclass.");
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static Method[] getAllDeclaredMethods(Object object) {
        int declaredMethodsCount = 0;

        ArrayList<Method[]> methodList = new ArrayList<>();
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            declaredMethodsCount += declaredMethods.length;
            methodList.add(declaredMethods);
        }

        Method[] allDeclaredMethods = new Method[declaredMethodsCount];
        int count = 0;

        for (int i = 0; i < methodList.size(); ++i) {
            Method[] declaredMethods = methodList.get(i);
            for (Method method : declaredMethods) {
                allDeclaredMethods[count] = method;
                count++;
            }
        }

        return allDeclaredMethods;
    }

    @SuppressWarnings("unused")
    public static void setFieldValue(Object object, String fieldName, Object value) {
        Field field = getDeclaredField(object, fieldName);

        if (field != null) {
            field.setAccessible(true);
            try {
                field.set(object, value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unused")
    public static Object getFieldValue(Object object, String fieldName) {
        Field field = getDeclaredField(object, fieldName);

        if (field != null) {
            field.setAccessible(true);
            try {
                return field.get(object);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @SuppressWarnings("unused")
    public static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        for(Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                //e.printStackTrace();
                // no such method in current class, find this method on its superclass
            }
        }

        if (DEBUG) {
            Log.e(TAG, "Could not find the method with name '" + methodName + "' in '" + object.getClass().getSimpleName() + "' and its superclass.");
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        Method method = getDeclaredMethod(object, methodName, parameterTypes);

        if (method != null) {
            method.setAccessible(true);
            try {
                return method.invoke(object, parameters);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @SuppressWarnings("unused")
    public static Object invokeMethod(Object object, String methodName) {
        return invokeMethod(object, methodName, null, null);
    }
}
