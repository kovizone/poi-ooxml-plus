package com.kovizone.poi.ooxml.plus.test;

import com.kovizone.poi.ooxml.plus.ExcelWriter;
import com.kovizone.poi.ooxml.plus.anno.*;
import com.kovizone.poi.ooxml.plus.exception.ExcelWriteException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Test {

    public static void main(String[] args) throws IOException, ExcelWriteException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<TestEntity2> testEntities = new ArrayList<>();
        testEntities.add(new TestEntity2("我", "222222", "00", "4", 1, new Date(), null, null));
        testEntities.add(new TestEntity2("5", "6", "01", "8", 2, new Date(), "test", "test8"));
        testEntities.add(new TestEntity2("9", "10", "02", null, 3, new Date(), null, null));
        testEntities.add(new TestEntity2("我和", "10", "02", null, 3, new Date(), null, null));
        testEntities.add(new TestEntity2("我和你", "10", "02", null, 3, new Date(), null, null));

        Workbook workbook = new HSSFWorkbook();
        new ExcelWriter().write(workbook, testEntities, new HashMap<String, Object>() {{
            put("#datetime", new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date()));
            put("#author", "KoviChen");
        }});

        workbook.write(new FileOutputStream(new File("D:/test/" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".xls")));
    }

    public static class TestEntity2 extends TestEntity{

        public TestEntity2(String test, String test2, String test3, String test4, Integer test5, Date test6, String test7, String test8) {
            super(test, test2, test3, test4, test5, test6, test7, test8);
        }
    }

    @WriteInit
    @WriteHeader("这是标题")
    public static class TestEntity {

        @WriteColumnConfig(value = "测试1", width = 7 * 256)
        String test;

        @WriteColumnConfig(sort = 20, value = "测试2", width = 3000)
        String test2;

        @WriteStringReplace(target = {"'00'", "'01'", "'02'"}, replacement = {"'零零'", "'零一'", "'零二' + #list[i].test2"})
        @WriteColumnConfig(sort = 30, value = "测试3")
        String test3;

        @WriteSubstitute(criteria = "#list[#i].test4 == null", value = "空值")
        @WriteColumnConfig(sort = 40, value = "测试4")
        String test4;

        @WriteColumnConfig(sort = 50, value = "测试5")
        Integer test5;

        @WriteDateFormat("yyyy-MM-dd HH:mm:ss SSS")
        @WriteColumnConfig(sort = 60, value = "测试6")
        Date test6;

        @WriteSubstitute(criteria = "#list[#i].test7 == null", value = "'空值' + #test")
        @WriteColumnConfig(sort = 70, value = "测试7")
        String test7;

        @WriteColumnConfig(sort = 80, value = "测试8")
        @WriteCriteria("#this.test8 == null ? #this.test2 : #this.test8")
        String test8;

        public TestEntity(String test, String test2, String test3, String test4, Integer test5, Date test6, String test7, String test8) {
            this.test = test;
            this.test2 = test2;
            this.test3 = test3;
            this.test4 = test4;
            this.test5 = test5;
            this.test6 = test6;
            this.test7 = test7;
            this.test8 = test8;
        }

        public String getTest7() {
            return test7;
        }

        public void setTest7(String test7) {
            this.test7 = test7;
        }

        public Date getTest6() {
            return test6;
        }

        public void setTest6(Date test6) {
            this.test6 = test6;
        }

        public Integer getTest5() {
            return test5;
        }

        public void setTest5(Integer test5) {
            this.test5 = test5;
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public String getTest2() {
            return test2;
        }

        public void setTest2(String test2) {
            this.test2 = test2;
        }

        public String getTest3() {
            return test3;
        }

        public void setTest3(String test3) {
            this.test3 = test3;
        }

        public String getTest4() {
            return test4;
        }

        public void setTest4(String test4) {
            this.test4 = test4;
        }

        public String getTest8() {
            return test8;
        }

        public void setTest8(String test8) {
            this.test8 = test8;
        }
    }
}
