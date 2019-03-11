package tech.tablesaw.io.csv;


import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;


public class CsvWriteOptionsTest {
    @Test
    public void testSettingsPropagation() {
        Table test = Table.create("test", StringColumn.create("t"));
        test.stringColumn(0).appendCell("testing");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriteOptions options = new CsvWriteOptions.Builder(stream).escapeChar('~').header(true).lineEnd("\r\n").quoteChar('"').separator('.').build();
        Assertions.assertEquals('~', options.escapeChar());
        Assertions.assertTrue(options.header());
        Assertions.assertEquals('"', options.quoteChar());
        Assertions.assertEquals('.', options.separator());
        CsvWriter writer = new CsvWriter(test, options);
        Assertions.assertEquals('~', writer.getEscapeChar());
        Assertions.assertTrue(writer.getHeader());
        Assertions.assertEquals("\r\n", writer.getLineEnd());
        Assertions.assertEquals('"', writer.getQuoteCharacter());
        Assertions.assertEquals('.', writer.getSeparator());
    }
}

