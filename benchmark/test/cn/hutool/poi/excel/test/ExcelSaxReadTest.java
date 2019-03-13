package cn.hutool.poi.excel.test;


import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.sax.Excel03SaxReader;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import org.junit.Test;


/**
 * Excel sax????
 *
 * @author looly
 */
public class ExcelSaxReadTest {
    @Test
    public void readBySaxTest() {
        ExcelUtil.readBySax("blankAndDateTest.xlsx", 0, createRowHandler());
    }

    @Test
    public void excel07Test() {
        Excel07SaxReader reader = new Excel07SaxReader(createRowHandler());
        reader.read("aaa.xlsx", 0);
        // ???????
        ExcelUtil.read07BySax("aaa.xlsx", 0, createRowHandler());
    }

    @Test
    public void excel03Test() {
        Excel03SaxReader reader = new Excel03SaxReader(createRowHandler());
        reader.read("aaa.xls", 1);
        // Console.log("Sheet index: [{}], Sheet name: [{}]", reader.getSheetIndex(), reader.getSheetName());
        ExcelUtil.read03BySax("aaa.xls", 1, createRowHandler());
    }
}

