/**
 * Copyright (c) 2013-2015 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;


import org.adoptopenjdk.jitwatch.model.bytecode.LineTable;
import org.adoptopenjdk.jitwatch.model.bytecode.LineTableEntry;
import org.junit.Assert;
import org.junit.Test;


public class TestLineTable {
    @Test
    public void testCompositeLineTable() {
        LineTable table1 = new LineTable(null);
        LineTable table2 = new LineTable(null);
        LineTableEntry entry1 = new LineTableEntry(0, 0);
        LineTableEntry entry2 = new LineTableEntry(5, 5);
        LineTableEntry entry3 = new LineTableEntry(10, 10);
        LineTableEntry entry4 = new LineTableEntry(15, 15);
        table1.add(entry1);
        table1.add(entry2);
        table2.add(entry3);
        table2.add(entry4);
        LineTable composite = new LineTable(null);
        composite.add(table2);
        composite.add(table1);
        Assert.assertEquals(0, composite.findSourceLineForBytecodeOffset(0));
        Assert.assertEquals(0, composite.findSourceLineForBytecodeOffset(1));
        Assert.assertEquals(5, composite.findSourceLineForBytecodeOffset(5));
        Assert.assertEquals(5, composite.findSourceLineForBytecodeOffset(6));
        Assert.assertEquals(10, composite.findSourceLineForBytecodeOffset(10));
        Assert.assertEquals(10, composite.findSourceLineForBytecodeOffset(11));
        Assert.assertEquals(15, composite.findSourceLineForBytecodeOffset(15));
        Assert.assertEquals(15, composite.findSourceLineForBytecodeOffset(16));
        Assert.assertTrue(table1.sourceLineInRange(0));
        Assert.assertTrue(table1.sourceLineInRange(1));
        Assert.assertTrue(table1.sourceLineInRange(5));
        Assert.assertFalse(table1.sourceLineInRange((-1)));
        Assert.assertFalse(table1.sourceLineInRange(6));
        Assert.assertFalse(table1.sourceLineInRange(10));
        Assert.assertTrue(table2.sourceLineInRange(10));
        Assert.assertTrue(table2.sourceLineInRange(11));
        Assert.assertTrue(table2.sourceLineInRange(15));
        Assert.assertFalse(table2.sourceLineInRange((-1)));
        Assert.assertFalse(table2.sourceLineInRange(6));
        Assert.assertFalse(table2.sourceLineInRange(25));
        Assert.assertTrue(composite.sourceLineInRange(1));
        Assert.assertTrue(composite.sourceLineInRange(11));
        Assert.assertTrue(composite.sourceLineInRange(15));
        Assert.assertFalse(composite.sourceLineInRange((-1)));
        Assert.assertFalse(composite.sourceLineInRange(25));
    }

    @Test
    public void testNashornLineTableRegression() {
        LineTable table = new LineTable(null);
        LineTableEntry entry1 = new LineTableEntry(8, 21);
        LineTableEntry entry2 = new LineTableEntry(1, 50);
        LineTableEntry entry3 = new LineTableEntry(3, 57);
        LineTableEntry entry4 = new LineTableEntry(5, 81);
        LineTableEntry entry5 = new LineTableEntry(3, 118);
        table.add(entry1);
        table.add(entry2);
        table.add(entry3);
        table.add(entry4);
        table.add(entry5);
        Assert.assertTrue(table.sourceLineInRange(1));
    }

    @Test
    public void testNonSequentialBCIs() {
        LineTable table = new LineTable(null);
        table.add(new LineTableEntry(21, 2));
        table.add(new LineTableEntry(23, 4));
        table.add(new LineTableEntry(23, 73));
        table.add(new LineTableEntry(25, 14));
        table.add(new LineTableEntry(26, 17));
        table.add(new LineTableEntry(28, 20));
        table.add(new LineTableEntry(30, 27));
        table.add(new LineTableEntry(33, 30));
        table.add(new LineTableEntry(34, 42));
        table.add(new LineTableEntry(36, 54));
        table.add(new LineTableEntry(38, 64));
        table.add(new LineTableEntry(42, 70));
        table.add(new LineTableEntry(46, 79));
        table.add(new LineTableEntry(48, 103));
        for (LineTableEntry entry : table.getEntries()) {
            Assert.assertEquals(entry.getSourceOffset(), table.findSourceLineForBytecodeOffset(entry.getBytecodeOffset()));
        }
        Assert.assertEquals(33, table.findSourceLineForBytecodeOffset(31));
        Assert.assertEquals(46, table.findSourceLineForBytecodeOffset(80));
        Assert.assertEquals(46, table.findSourceLineForBytecodeOffset(81));
        Assert.assertEquals(46, table.findSourceLineForBytecodeOffset(82));
        Assert.assertEquals(48, table.findSourceLineForBytecodeOffset(5000));
    }

    @Test
    public void testHighlightRange() {
        LineTable table = new LineTable(null);
        table.add(new LineTableEntry(8, 0));
        table.add(new LineTableEntry(9, 4));
        table.add(new LineTableEntry(12, 6));
        table.add(new LineTableEntry(12, 22));
        table.add(new LineTableEntry(14, 14));
        table.add(new LineTableEntry(17, 28));
        table.add(new LineTableEntry(18, 53));
        int[] range = table.getSourceRange(8, 25);
        Assert.assertEquals(12, range[0]);
        Assert.assertEquals(14, range[1]);
    }
}

