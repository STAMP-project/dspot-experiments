package tech.tablesaw.io.csv;


import com.google.common.collect.ImmutableList;
import java.io.StringWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;


public class CsvWriterTest {
    @Test
    public void toWriterWithExtension() {
        StringColumn colA = StringColumn.create("colA", ImmutableList.of("a", "b"));
        StringColumn colB = StringColumn.create("colB", ImmutableList.of("1", "2"));
        Table table = Table.create("testTable", colA, colB);
        StringWriter writer = new StringWriter();
        table.write().toWriter(writer, "csv");
        Assertions.assertEquals("colA,colB\na,1\nb,2\n", writer.toString());
    }
}

