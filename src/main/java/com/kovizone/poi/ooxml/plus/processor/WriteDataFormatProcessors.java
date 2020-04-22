package com.kovizone.poi.ooxml.plus.processor;

import com.kovizone.poi.ooxml.plus.command.ExcelCommand;
import com.kovizone.poi.ooxml.plus.anno.WriteDateFormat;
import com.kovizone.poi.ooxml.plus.api.processor.WriteDataBodyProcessor;
import com.kovizone.poi.ooxml.plus.util.StringUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * DateFormat注解处理器
 *
 * @author KoviChen
 */
public class WriteDataFormatProcessors implements WriteDataBodyProcessor<WriteDateFormat> {

    @Override
    public Object dataBodyProcess(WriteDateFormat writeDateFormat,
                                  ExcelCommand excelCommand,
                                  List<?> entityList,
                                  int entityListIndex,
                                  Field targetField,
                                  Object columnValue) {
        if (!(columnValue instanceof Date)) {
            return columnValue;
        }
        Date targetFieldDateValue = (Date) columnValue;
        if (StringUtils.isEmpty(writeDateFormat.value())) {
            return targetFieldDateValue.toString();
        }
        return new SimpleDateFormat(writeDateFormat.value()).format(targetFieldDateValue);
    }
}