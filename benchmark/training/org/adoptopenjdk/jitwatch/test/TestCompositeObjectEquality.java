/**
 * Copyright (c) 2013-2017 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;


import Opcode.ALOAD_0;
import Opcode.ALOAD_1;
import org.adoptopenjdk.jitwatch.model.bytecode.BytecodeInstruction;
import org.adoptopenjdk.jitwatch.model.bytecode.LineTable;
import org.adoptopenjdk.jitwatch.model.bytecode.LineTableEntry;
import org.junit.Assert;
import org.junit.Test;


public class TestCompositeObjectEquality {
    @Test
    public void testLineTableEqual() {
        LineTable table1 = new LineTable(null);
        LineTable table2 = new LineTable(null);
        table1.add(new LineTableEntry(1, 2));
        table1.add(new LineTableEntry(3, 4));
        table2.add(new LineTableEntry(1, 2));
        table2.add(new LineTableEntry(3, 4));
        Assert.assertEquals(table1, table2);
    }

    @Test
    public void testLineTableEmptyEqual() {
        LineTable table1 = new LineTable(null);
        LineTable table2 = new LineTable(null);
        Assert.assertEquals(table1, table2);
    }

    @Test
    public void testLineTableNotEqual() {
        LineTable table1 = new LineTable(null);
        LineTable table2 = new LineTable(null);
        table1.add(new LineTableEntry(0, 1));
        table1.add(new LineTableEntry(2, 3));
        table2.add(new LineTableEntry(4, 5));
        table2.add(new LineTableEntry(6, 7));
        Assert.assertNotSame(table1, table2);
    }

    @Test
    public void testBytecodeInstructionEqual() {
        BytecodeInstruction instruction1 = new BytecodeInstruction();
        instruction1.setOpcode(ALOAD_0);
        instruction1.setOffset(10);
        instruction1.setComment("foo");
        BytecodeInstruction instruction2 = new BytecodeInstruction();
        instruction2.setOpcode(ALOAD_0);
        instruction2.setOffset(10);
        instruction2.setComment("foo");
        Assert.assertEquals(instruction1, instruction2);
    }

    @Test
    public void testBytecodeInstructionNotEqualOpcode() {
        BytecodeInstruction instruction1 = new BytecodeInstruction();
        instruction1.setOpcode(ALOAD_0);
        instruction1.setOffset(10);
        instruction1.setComment("foo");
        BytecodeInstruction instruction2 = new BytecodeInstruction();
        instruction2.setOpcode(ALOAD_1);
        instruction2.setOffset(10);
        instruction2.setComment("foo");
        Assert.assertNotSame(instruction1, instruction2);
    }

    @Test
    public void testBytecodeInstructionNotEqualBCI() {
        BytecodeInstruction instruction1 = new BytecodeInstruction();
        instruction1.setOpcode(ALOAD_0);
        instruction1.setOffset(10);
        instruction1.setComment("foo");
        BytecodeInstruction instruction2 = new BytecodeInstruction();
        instruction2.setOpcode(ALOAD_0);
        instruction2.setOffset(99);
        instruction2.setComment("foo");
        Assert.assertNotSame(instruction1, instruction2);
    }

    @Test
    public void testBytecodeInstructionNotEqualComment() {
        BytecodeInstruction instruction1 = new BytecodeInstruction();
        instruction1.setOpcode(ALOAD_0);
        instruction1.setOffset(10);
        instruction1.setComment("foo");
        BytecodeInstruction instruction2 = new BytecodeInstruction();
        instruction2.setOpcode(ALOAD_0);
        instruction2.setOffset(10);
        instruction2.setComment("bar");
        Assert.assertNotSame(instruction1, instruction2);
    }
}

