package com.kovizone.poi.ooxml.plus.processor;

import com.kovizone.poi.ooxml.plus.command.ExcelCommand;
import com.kovizone.poi.ooxml.plus.exception.PoiOoxmlPlusException;

import java.util.List;

/**
 * 表头处理器接口
 *
 * @author KoviChen
 */
public interface WriteHeaderProcessor {
    /**
     * 处理
     *
     * @param annotation      注解类实体
     * @param excelCommand 基础命令
     * @param entityList      渲染实体集合
     * @param clazz           实体类
     * @throws PoiOoxmlPlusException 异常
     */
    void headerProcess(Object annotation,
                       ExcelCommand excelCommand,
                       List<?> entityList,
                       Class<?> clazz
    ) throws PoiOoxmlPlusException;
}