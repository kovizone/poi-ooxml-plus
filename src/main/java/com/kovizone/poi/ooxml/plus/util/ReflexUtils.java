package com.kovizone.poi.ooxml.plus.util;


import com.kovizone.poi.ooxml.plus.exception.ReflexException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>反射工具类</p>
 *
 * @author KoviChen
 * @version 1.0
 */
public class ReflexUtils {

    /**
     * 属性数组缓存
     */
    // TODO 待优化，改成LRU或LFU算法
    private static final Map<Class<?>, Field[]> FIELD_ARRAY_CACHE = new HashMap<>(16);

    /**
     * 注解数组
     */
    // TODO 待优化，改成LRU或LFU算法
    private static final Map<Class<?>, Annotation[]> ANNOTATION_ARRAY_CACHE = new HashMap<>(16);

    /**
     * <p>获取属性</p>
     *
     * <p>基于{@code ReflexUtils#getFields}实现</p>
     *
     * <p>将会读取{@code clazz}的本类及其所有父类的属性，找到并返回属性名为{@code fieldName}的属性</p>
     *
     * @param clazz     类
     * @param fieldName 属性名
     * @return 属性
     * @throws ReflexException 反射异常
     * @see ReflexUtils#getDeclaredFields
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws ReflexException {
        Field[] fields = getDeclaredFields(clazz);
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        throw new ReflexException("找不到属性：" + fieldName);
    }

    /**
     * <p>获取属性</p>
     *
     * <p>将会读取{@code clazz}的本类及其所有父类的属性，返回所有属性集合</p>
     *
     * <p>若子类与父类有相同的属性名的{@code Field}，将会舍弃父类的{@code Field}</p>
     *
     * <p>结果将写入缓存{@code FIELD_ARRAY_CACHE}，第二次开始获取时从缓存中获取</p>
     *
     * @param clazz 类
     * @return 属性
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        Field[] fields = FIELD_ARRAY_CACHE.get(clazz);
        if (fields == null) {
            fields = new Field[0];
            while (!clazz.equals(Object.class)) {
                Field[] currentFields = clazz.getDeclaredFields();
                for (int i = 0; i < currentFields.length; i++) {
                    for (Field field : fields) {
                        if (currentFields[i].getName().equals(field.getName())) {
                            currentFields[i] = null;
                            break;
                        }
                    }
                }
                fields = ArrayUtils.addTrimAll(fields, currentFields);
                clazz = clazz.getSuperclass();
            }
            FIELD_ARRAY_CACHE.put(clazz, fields);
        }
        return fields;
    }

    public static Annotation getDeclaredAnnotation(Class<?> clazz, String annotationName) throws ReflexException {
        Annotation[] annotations = getDeclaredAnnotations(clazz);
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().equals(annotationName)) {
                return annotation;
            }
        }
        throw new ReflexException("找不到注解：" + annotationName);
    }

    public static Annotation[] getDeclaredAnnotations(Class<?> clazz) {
        Annotation[] annotations = ANNOTATION_ARRAY_CACHE.get(clazz);
        if (annotations == null) {
            annotations = new Annotation[0];
            while (!clazz.equals(Object.class)) {
                Annotation[] currentAnnotations = clazz.getDeclaredAnnotations();
                for (int i = 0; i < currentAnnotations.length; i++) {
                    for (Annotation annotation : annotations) {
                        if (currentAnnotations[i].annotationType().equals(annotation.annotationType())) {
                            currentAnnotations[i] = null;
                            break;
                        }
                    }
                }
                annotations = ArrayUtils.addTrimAll(annotations, currentAnnotations);
                clazz = clazz.getSuperclass();
            }
            ANNOTATION_ARRAY_CACHE.put(clazz, annotations);
        }
        return annotations;
    }

    public static Object getValue(Object object, String fieldName) throws ReflexException {
        return getValue(object, getDeclaredField(object.getClass(), fieldName));
    }

    public static Object getValue(Object object, Field field) throws ReflexException {
        Class<?> clazz = object.getClass();

        // 公开变量直接读
        if (Modifier.isPublic(field.getModifiers())) {
            try {
                return field.get(object);
            } catch (IllegalAccessException ignored) {
            }
        }

        // 私有静态变量强制读
        if (Modifier.isStatic(field.getModifiers())) {
            return accessibleGetValue(object, field);
        }

        // 私有非静态变量优先使用getter
        try {
            Method getMethod = clazz.getMethod("get".concat(StringUtils.upperFirstCase(field.getName())));
            return getMethod.invoke(object);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // 若无getter，强制读取
            return accessibleGetValue(object, field);
        }

    }

    private static Object accessibleGetValue(Object object, Field field) throws ReflexException {
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException ex) {
            throw new ReflexException("读取属性值失败：" + field.toString());
        }
    }

    public static void setValue(Object object, String fieldName, Object value) throws ReflexException {
        setValue(object, getDeclaredField(object.getClass(), fieldName), value);
    }

    public static void setValue(Object object, Field field, Object value) throws ReflexException {
        Class<?> clazz = object.getClass();
        // 常量写入拦截
        if (Modifier.isFinal(field.getModifiers())) {
            return;
        }

        // 公开变量直接写入
        if (Modifier.isPublic(field.getModifiers())) {
            try {
                field.set(object, value);
                return;
            } catch (IllegalAccessException ignored) {
            }
        }

        // 私有静态变量强制写入
        if (Modifier.isStatic(field.getModifiers())) {
            accessibleSetValue(object, field, value);
            return;
        }

        // 私有非静态变量优先使用setter
        try {
            Method setMethod = clazz.getMethod("set".concat(StringUtils.upperFirstCase(field.getName())), field.getType());
            setMethod.invoke(object, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // 若无setter，强制写入
            accessibleSetValue(object, field, value);
        }
    }

    private static void accessibleSetValue(Object object, Field field, Object value) throws ReflexException {
        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (IllegalAccessException ex) {
            throw new ReflexException("设置值失败：" + field.toString());
        }
    }

    public static <T> T newInstance(Class<T> clazz) throws ReflexException {
        return newInstance(clazz, new Object[0]);
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz, Object... params) throws ReflexException {
        if (params == null) {
            params = new Object[0];
        }
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterTypes().length == params.length) {
                try {
                    Object entity = constructor.newInstance(params);
                    return (T) entity;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                }
            }
        }
        throw new ReflexException("没有找到合适的构造方法：" + clazz.toString());
    }
}
