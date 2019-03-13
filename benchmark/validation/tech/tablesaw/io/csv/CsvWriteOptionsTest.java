package tech.tablesaw.io.csv;


import com.univocity.parsers.csv.CsvWriterSettings;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CsvWriteOptionsTest {
    @Test
    public void testSettingsPropagation() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriteOptions options = CsvWriteOptions.builder(stream).escapeChar('~').header(true).lineEnd("\r\n").quoteChar('"').separator('.').build();
        Assertions.assertEquals('~', options.escapeChar());
        Assertions.assertTrue(options.header());
        Assertions.assertEquals('"', options.quoteChar());
        Assertions.assertEquals('.', options.separator());
        CsvWriterSettings settings = CsvWriter.createSettings(options);
        Assertions.assertEquals('~', settings.getFormat().getQuoteEscape());
        Assertions.assertEquals("\r\n", settings.getFormat().getLineSeparatorString());
        Assertions.assertEquals('"', settings.getFormat().getQuote());
        Assertions.assertEquals('.', settings.getFormat().getDelimiter());
    }
}

