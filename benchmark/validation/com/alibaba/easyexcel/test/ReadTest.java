package com.alibaba.easyexcel.test;


import com.alibaba.easyexcel.test.listen.ExcelListener;
import com.alibaba.easyexcel.test.model.ReadModel;
import com.alibaba.easyexcel.test.model.ReadModel2;
import com.alibaba.easyexcel.test.util.FileUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.Test;


public class ReadTest {
    /**
     * 07??excel??????1?????????????.
     *
     * @throws IOException
     * 		?????????????catch??,???finally????
     */
    @Test
    public void simpleReadListStringV2007() throws IOException {
        InputStream inputStream = FileUtil.getResourcesFileInputStream("2007.xlsx");
        List<Object> data = EasyExcelFactory.read(inputStream, new Sheet(1, 0));
        inputStream.close();
        print(data);
    }

    /**
     * 07??excel??????1????????javamodel?????????.
     *
     * @throws IOException
     * 		?????????????catch??,???finally????
     */
    @Test
    public void simpleReadJavaModelV2007() throws IOException {
        InputStream inputStream = FileUtil.getResourcesFileInputStream("2007.xlsx");
        List<Object> data = EasyExcelFactory.read(inputStream, new Sheet(2, 1, ReadModel.class));
        inputStream.close();
        print(data);
    }

    /**
     * 07??excel??????1???????????.
     *
     * @throws IOException
     * 		?????????????catch??,???finally????
     */
    @Test
    public void saxReadListStringV2007() throws IOException {
        InputStream inputStream = FileUtil.getResourcesFileInputStream("2007.xlsx");
        ExcelListener excelListener = new ExcelListener();
        EasyExcelFactory.readBySax(inputStream, new Sheet(1, 1), excelListener);
        inputStream.close();
    }

    /**
     * 07??excel??????1???????????.
     *
     * @throws IOException
     * 		?????????????catch??,???finally????
     */
    @Test
    public void saxReadJavaModelV2007() throws IOException {
        InputStream inputStream = FileUtil.getResourcesFileInputStream("2007.xlsx");
        ExcelListener excelListener = new ExcelListener();
        EasyExcelFactory.readBySax(inputStream, new Sheet(2, 1, ReadModel.class), excelListener);
        inputStream.close();
    }

    /**
     * 07??excel??sheet
     *
     * @throws IOException
     * 		?????????????catch??,???finally????
     */
    @Test
    public void saxReadSheetsV2007() throws IOException {
        InputStream inputStream = FileUtil.getResourcesFileInputStream("2007.xlsx");
        ExcelListener excelListener = new ExcelListener();
        ExcelReader excelReader = EasyExcelFactory.getReader(inputStream, excelListener);
        List<Sheet> sheets = excelReader.getSheets();
        System.out.println(("llll****" + sheets));
        System.out.println();
        for (Sheet sheet : sheets) {
            if ((sheet.getSheetNo()) == 1) {
                excelReader.read(sheet);
            } else
                if ((sheet.getSheetNo()) == 2) {
                    sheet.setHeadLineMun(1);
                    sheet.setClazz(ReadModel.class);
                    excelReader.read(sheet);
                } else
                    if ((sheet.getSheetNo()) == 3) {
                        sheet.setHeadLineMun(1);
                        sheet.setClazz(ReadModel2.class);
                        excelReader.read(sheet);
                    }


        }
        inputStream.close();
    }

    /**
     * 03??excel??????1?????????????.
     *
     * @throws IOException
     * 		?????????????catch??,???finally????
     */
    @Test
    public void simpleReadListStringV2003() throws IOException {
        InputStream inputStream = FileUtil.getResourcesFileInputStream("2003.xls");
        List<Object> data = EasyExcelFactory.read(inputStream, new Sheet(1, 0));
        inputStream.close();
        print(data);
    }

    /**
     * 03??excel??????1??????javamodel?????????.
     *
     * @throws IOException
     * 		?????????????catch??,???finally????
     */
    @Test
    public void simpleReadJavaModelV2003() throws IOException {
        InputStream inputStream = FileUtil.getResourcesFileInputStream("2003.xls");
        List<Object> data = EasyExcelFactory.read(inputStream, new Sheet(2, 1, ReadModel.class));
        inputStream.close();
        print(data);
    }

    /**
     * 03??excel??????1?????????????.
     *
     * @throws IOException
     * 		?????????????catch??,???finally????
     */
    @Test
    public void saxReadListStringV2003() throws IOException {
        InputStream inputStream = FileUtil.getResourcesFileInputStream("2003.xls");
        ExcelListener excelListener = new ExcelListener();
        EasyExcelFactory.readBySax(inputStream, new Sheet(2, 1), excelListener);
        inputStream.close();
    }

    /**
     * 03??excel??????1??????javamodel?????????.
     *
     * @throws IOException
     * 		?????????????catch??,???finally????
     */
    @Test
    public void saxReadJavaModelV2003() throws IOException {
        InputStream inputStream = FileUtil.getResourcesFileInputStream("2003.xls");
        ExcelListener excelListener = new ExcelListener();
        EasyExcelFactory.readBySax(inputStream, new Sheet(2, 1, ReadModel.class), excelListener);
        inputStream.close();
    }

    /**
     * 00??excel??sheet
     *
     * @throws IOException
     * 		?????????????catch??,???finally????
     */
    @Test
    public void saxReadSheetsV2003() throws IOException {
        InputStream inputStream = FileUtil.getResourcesFileInputStream("2003.xls");
        ExcelListener excelListener = new ExcelListener();
        ExcelReader excelReader = EasyExcelFactory.getReader(inputStream, excelListener);
        List<Sheet> sheets = excelReader.getSheets();
        System.out.println();
        for (Sheet sheet : sheets) {
            if ((sheet.getSheetNo()) == 1) {
                excelReader.read(sheet);
            } else {
                sheet.setHeadLineMun(2);
                sheet.setClazz(ReadModel.class);
                excelReader.read(sheet);
            }
        }
        inputStream.close();
    }
}

