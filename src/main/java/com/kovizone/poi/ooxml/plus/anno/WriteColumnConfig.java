package com.kovizone.poi.ooxml.plus.anno;

import com.kovizone.poi.ooxml.plus.api.anno.Processor;
import com.kovizone.poi.ooxml.plus.processor.impl.WriteColumnConfigProcessors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定为Excel列字段
 *
 * @author KoviChen
 */
@Target({ElementType.FIELD})
@Processor(WriteColumnConfigProcessors.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface WriteColumnConfig {


    /**
     * 排序，由小到大
     *
     * @return 排序值
     */
    int sort();

    /**
     * 指定显示列名
     *
     * @return 列名
     */
    String title();

    /**
     * 列宽设置
     *
     * @return 列宽
     */
    int width() default -1;

}