package aima.test.core.unit.util.datastructure;


import aima.core.util.datastructure.Table;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 */
public class TableTest {
    private Table<String, String, Integer> table;

    @Test
    public void testTableInitialization() {
        Assert.assertNull(table.get("ravi", "iq"));
        table.set("ravi", "iq", 50);
        int i = table.get("ravi", "iq");
        Assert.assertEquals(50, i);
    }

    @Test
    public void testNullAccess() {
        // No value yet assigned
        Assert.assertNull(table.get("row1", "col2"));
        table.set("row1", "col1", 1);
        Assert.assertEquals(1, ((int) (table.get("row1", "col1"))));
        // Check null returned if column does not exist
        Assert.assertNull(table.get("row1", "col2"));
        // Check null returned if row does not exist
        Assert.assertNull(table.get("row2", "col1"));
    }
}

