package com.kovizone.poi.ooxml.plus.anno;

import com.kovizone.poi.ooxml.plus.api.anno.Processor;
import com.kovizone.poi.ooxml.plus.processor.WriteCriteriaProcessors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 输出到单元格的条件
 *
 * @author KoviChen
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Processor(WriteCriteriaProcessors.class)
public @interface WriteCriteria {

    /**
     * 显示条件表达式，
     * 使用#FieldName指定属性的值，
     * 若为false则值为null
     *
     * @return 条件集
     */
    String value();

}
