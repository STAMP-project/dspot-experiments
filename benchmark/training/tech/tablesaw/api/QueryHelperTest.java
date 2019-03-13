package tech.tablesaw.api;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class QueryHelperTest {
    private Table t;

    @Test
    public void test1() {
        Table result = t.where(t.stringColumn("who").startsWith("f").and(t.dateColumn("date").isInYear(2002).and(t.numberColumn("approval").isLessThan(75))));
        Assertions.assertTrue(result.getString(0, "who").startsWith("f"));
    }

    @Test
    public void test3() {
        Table result = t.where(t.stringColumn("who").isIn("fox"));
        Assertions.assertEquals("fox", result.getString(0, "who"));
        result = t.where(t.stringColumn("who").isNotIn("fox", "zogby"));
        Assertions.assertFalse(result.getString(0, "who").startsWith("f"));
    }

    @Test
    public void test2() {
        Table result = t.where(t.stringColumn("who").startsWith("f"));
        Assertions.assertTrue(result.getString(0, "who").startsWith("f"));
    }
}

