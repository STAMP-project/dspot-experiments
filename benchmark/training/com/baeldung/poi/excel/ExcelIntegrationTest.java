package com.baeldung.poi.excel;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ExcelIntegrationTest {
    private ExcelPOIHelper excelPOIHelper;

    private static String FILE_NAME = "temp.xlsx";

    private String fileLocation;

    @Test
    public void whenParsingPOIExcelFile_thenCorrect() throws IOException {
        Map<Integer, List<String>> data = excelPOIHelper.readExcel(fileLocation);
        Assert.assertEquals("Name", data.get(0).get(0));
        Assert.assertEquals("Age", data.get(0).get(1));
        Assert.assertEquals("John Smith", data.get(1).get(0));
        Assert.assertEquals("20", data.get(1).get(1));
    }
}

