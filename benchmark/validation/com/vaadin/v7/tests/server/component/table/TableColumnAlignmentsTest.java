package com.vaadin.v7.tests.server.component.table;


import Align.LEFT;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.Align;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.fail;


public class TableColumnAlignmentsTest {
    @Test
    public void defaultColumnAlignments() {
        for (int properties = 0; properties < 10; properties++) {
            Table t = TableGeneratorTest.createTableWithDefaultContainer(properties, 10);
            Object[] expected = new Object[properties];
            for (int i = 0; i < properties; i++) {
                expected[i] = Align.LEFT;
            }
            Assert.assertArrayEquals("getColumnAlignments", expected, t.getColumnAlignments());
        }
    }

    @Test
    public void explicitColumnAlignments() {
        int properties = 5;
        Table t = TableGeneratorTest.createTableWithDefaultContainer(properties, 10);
        Align[] explicitAlignments = new Align[]{ Align.CENTER, Align.LEFT, Align.RIGHT, Align.RIGHT, Align.LEFT };
        t.setColumnAlignments(explicitAlignments);
        Assert.assertArrayEquals("Explicit visible columns, 5 properties", explicitAlignments, t.getColumnAlignments());
    }

    @Test
    public void invalidColumnAlignmentStrings() {
        Table t = TableGeneratorTest.createTableWithDefaultContainer(3, 7);
        Align[] defaultAlignments = new Align[]{ Align.LEFT, Align.LEFT, Align.LEFT };
        try {
            t.setColumnAlignments(new Align[]{ Align.RIGHT, Align.RIGHT });
            fail("No exception thrown for invalid array length");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }
        Assert.assertArrayEquals("Invalid change affected alignments", defaultAlignments, t.getColumnAlignments());
    }

    @Test
    public void columnAlignmentForPropertyNotInContainer() {
        Table t = TableGeneratorTest.createTableWithDefaultContainer(3, 7);
        Align[] defaultAlignments = new Align[]{ Align.LEFT, Align.LEFT, Align.LEFT };
        try {
            t.setColumnAlignment("Property 1200", LEFT);
            // FIXME: Uncomment as there should be an exception (#6475)
            // junit.framework.Assert
            // .fail("No exception thrown for property not in container");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }
        Assert.assertArrayEquals("Invalid change affected alignments", defaultAlignments, t.getColumnAlignments());
        // FIXME: Uncomment as null should be returned (#6474)
        // junit.framework.Assert.assertEquals(
        // "Column alignment for property not in container returned",
        // null, t.getColumnAlignment("Property 1200"));
    }

    @Test
    public void invalidColumnAlignmentsLength() {
        Table t = TableGeneratorTest.createTableWithDefaultContainer(7, 7);
        Align[] defaultAlignments = new Align[]{ Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT };
        try {
            t.setColumnAlignments(new Align[]{ Align.LEFT });
            fail("No exception thrown for invalid array length");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }
        Assert.assertArrayEquals("Invalid change affected alignments", defaultAlignments, t.getColumnAlignments());
        try {
            t.setColumnAlignments(new Align[]{  });
            fail("No exception thrown for invalid array length");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }
        Assert.assertArrayEquals("Invalid change affected alignments", defaultAlignments, t.getColumnAlignments());
        try {
            t.setColumnAlignments(new Align[]{ Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT });
            fail("No exception thrown for invalid array length");
        } catch (IllegalArgumentException e) {
            // Ok, expected
        }
        Assert.assertArrayEquals("Invalid change affected alignments", defaultAlignments, t.getColumnAlignments());
    }

    @Test
    public void explicitColumnAlignmentOneByOne() {
        int properties = 5;
        Table t = TableGeneratorTest.createTableWithDefaultContainer(properties, 10);
        Align[] explicitAlignments = new Align[]{ Align.CENTER, Align.LEFT, Align.RIGHT, Align.RIGHT, Align.LEFT };
        Align[] currentAlignments = new Align[]{ Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT, Align.LEFT };
        for (int i = 0; i < properties; i++) {
            t.setColumnAlignment(("Property " + i), explicitAlignments[i]);
            currentAlignments[i] = explicitAlignments[i];
            Assert.assertArrayEquals((("Explicit visible columns, " + i) + " alignments set"), currentAlignments, t.getColumnAlignments());
        }
    }
}

