package com.vaadin.v7.tests.server.component.table;


import com.vaadin.v7.ui.Table;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.fail;


public class TableVisibleColumnsTest {
    String[] defaultColumns3 = new String[]{ "Property 0", "Property 1", "Property 2" };

    @Test
    public void defaultVisibleColumns() {
        for (int properties = 0; properties < 10; properties++) {
            Table t = TableGeneratorTest.createTableWithDefaultContainer(properties, 10);
            Object[] expected = new Object[properties];
            for (int i = 0; i < properties; i++) {
                expected[i] = "Property " + i;
            }
            Assert.assertArrayEquals("getVisibleColumns", expected, t.getVisibleColumns());
        }
    }

    @Test
    public void explicitVisibleColumns() {
        Table t = TableGeneratorTest.createTableWithDefaultContainer(5, 10);
        Object[] newVisibleColumns = new Object[]{ "Property 1", "Property 2" };
        t.setVisibleColumns(newVisibleColumns);
        Assert.assertArrayEquals("Explicit visible columns, 5 properties", newVisibleColumns, t.getVisibleColumns());
    }

    @Test
    public void invalidVisibleColumnIds() {
        Table t = TableGeneratorTest.createTableWithDefaultContainer(3, 10);
        try {
            t.setVisibleColumns("a", "Property 2", "Property 3");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // OK, expected
        }
        Assert.assertArrayEquals(defaultColumns3, t.getVisibleColumns());
    }

    @Test
    public void duplicateVisibleColumnIds() {
        Table t = TableGeneratorTest.createTableWithDefaultContainer(3, 10);
        try {
            t.setVisibleColumns("Property 0", "Property 1", "Property 2", "Property 1");
        } catch (IllegalArgumentException e) {
            // OK, expected
        }
        Assert.assertArrayEquals(defaultColumns3, t.getVisibleColumns());
    }

    @Test
    public void noVisibleColumns() {
        Table t = TableGeneratorTest.createTableWithDefaultContainer(3, 10);
        t.setVisibleColumns();
        Assert.assertArrayEquals(new Object[]{  }, t.getVisibleColumns());
    }
}

