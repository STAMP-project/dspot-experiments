package com.baeldung.jexcel;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import jxl.read.biff.BiffException;
import org.junit.Assert;
import org.junit.Test;


public class JExcelIntegrationTest {
    private JExcelHelper jExcelHelper;

    private static String FILE_NAME = "temp.xls";

    private String fileLocation;

    @Test
    public void whenParsingJExcelFile_thenCorrect() throws IOException, BiffException {
        Map<Integer, List<String>> data = jExcelHelper.readJExcel(fileLocation);
        Assert.assertEquals("Name", data.get(0).get(0));
        Assert.assertEquals("Age", data.get(0).get(1));
        Assert.assertEquals("John Smith", data.get(2).get(0));
        Assert.assertEquals("20", data.get(2).get(1));
    }
}

