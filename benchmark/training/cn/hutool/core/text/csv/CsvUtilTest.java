package cn.hutool.core.text.csv;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import java.util.List;
import org.junit.Test;


public class CsvUtilTest {
    @Test
    public void readTest() {
        CsvReader reader = CsvUtil.getReader();
        // ??????CSV??
        CsvData data = reader.read(FileUtil.file("test.csv"));
        List<CsvRow> rows = data.getRows();
        for (CsvRow csvRow : rows) {
            Assert.notEmpty(csvRow.getRawList());
        }
    }
}

