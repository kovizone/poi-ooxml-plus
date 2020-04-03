package com.kovizone.poi.ooxml.plus.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * int类型参数转义
 *
 * @author KoviChen
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberMapper {

    /**
     * 关键字
     *
     * @return 关键字
     */
    int[] target();

    /**
     * 转义词
     *
     * @return 转义词
     */
    String[] replacement();

}
