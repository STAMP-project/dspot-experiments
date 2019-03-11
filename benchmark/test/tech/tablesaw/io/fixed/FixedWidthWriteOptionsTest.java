package tech.tablesaw.io.fixed;


import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;


public class FixedWidthWriteOptionsTest {
    @Test
    public void testSettingsPropagation() {
        Table test = Table.create("test", StringColumn.create("t"));
        test.stringColumn(0).appendCell("testing");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        FixedWidthWriteOptions options = new FixedWidthWriteOptions.Builder(stream).header(true).lineSeparatorString("\r\n").padding('~').build();
        Assertions.assertTrue(options.header());
        FixedWidthWriter writer = new FixedWidthWriter(test, options);
        Assertions.assertTrue(writer.getHeader());
        Assertions.assertEquals("\r\n", writer.getFormat().getLineSeparatorString());
        Assertions.assertEquals('~', writer.getFormat().getPadding());
    }

    @Test
    public void testSettingsAutoConfigurationPropagation() {
        Table test = Table.create("test", StringColumn.create("t"));
        test.stringColumn(0).appendCell("testing");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        FixedWidthWriteOptions options = new FixedWidthWriteOptions.Builder(stream).autoConfigurationEnabled(true).build();
        Assertions.assertTrue(options.autoConfigurationEnabled());
        Assertions.assertTrue(options.header());
        FixedWidthWriter writer = new FixedWidthWriter(test, options);
        Assertions.assertTrue(writer.getHeader());
    }
}

