package tech.tablesaw.columns.booleans;


import org.junit.jupiter.api.Test;
import tech.tablesaw.api.BooleanColumn;


public class BooleanFillersTest {
    @Test
    public void test() {
        BooleanColumn booleanColumn = BooleanColumn.create("booleans", new boolean[5]);
        assertContentEquals(booleanColumn.fillWith(bits(6, 3)), true, true, false, true, true);
    }
}

