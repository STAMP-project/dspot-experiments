/**
 * Copyright (c) 2013-2016 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;


import Opcode.ALOAD_0;
import Opcode.ALOAD_1;
import Opcode.ALOAD_2;
import Opcode.ALOAD_3;
import Opcode.ANEWARRAY;
import Opcode.ARETURN;
import Opcode.ASTORE_0;
import Opcode.ASTORE_3;
import Opcode.ATHROW;
import Opcode.CHECKCAST;
import Opcode.DUP;
import Opcode.GETSTATIC;
import Opcode.GOTO;
import Opcode.ICONST_0;
import Opcode.ICONST_1;
import Opcode.ICONST_2;
import Opcode.ICONST_M1;
import Opcode.IFEQ;
import Opcode.ILOAD;
import Opcode.INVOKESPECIAL;
import Opcode.INVOKEVIRTUAL;
import Opcode.ISTORE;
import Opcode.LDC;
import Opcode.LOOKUPSWITCH;
import Opcode.NEW;
import Opcode.RETURN;
import Opcode.TABLESWITCH;
import java.util.Map;
import org.adoptopenjdk.jitwatch.jarscan.sequencecount.InstructionSequence;
import org.adoptopenjdk.jitwatch.jarscan.sequencecount.SequenceCountOperation;
import org.adoptopenjdk.jitwatch.model.bytecode.MemberBytecode;
import org.junit.Assert;
import org.junit.Test;


public class TestJarScan {
    public static final boolean SHOW_OUTPUT = false;

    @Test
    public void testLongBytecodeChain1() {
        String[] lines = new String[]{ "0: aload_1", "1: invokevirtual #38                 // Method org/adoptopenjdk/jitwatch/model/Tag.getName:()Ljava/lang/String;", "4: astore_3", "5: iconst_m1", "6: istore        4", "8: aload_3", "9: invokevirtual #39                 // Method java/lang/String.hashCode:()I", "12: lookupswitch  { // 3", "   -1067517155: 63", "     106437299: 48", "     922165544: 78", "       default: 90", "  }", "48: aload_3", "49: ldc           #14                 // String parse", "51: invokevirtual #40                 // Method java/lang/String.equals:(Ljava/lang/Object;)Z", "54: ifeq          90", "57: iconst_0", "58: istore        4", "60: goto          90", "63: aload_3", "64: ldc           #41                 // String eliminate_allocation", "66: invokevirtual #40                 // Method java/lang/String.equals:(Ljava/lang/Object;)Z", "69: ifeq          90", "72: iconst_1", "73: istore        4", "75: goto          90", "78: aload_3", "79: ldc           #42                 // String eliminate_lock", "81: invokevirtual #40                 // Method java/lang/String.equals:(Ljava/lang/Object;)Z", "84: ifeq          90", "87: iconst_2", "88: istore        4", "90: iload         4", "92: tableswitch   { // 0 to 2", "             0: 120", "             1: 129", "             2: 138", "       default: 147", "  }", "120: aload_0", "121: aload_1", "122: aload_2", "123: invokespecial #43                 // Method visitTagParse:(Lorg/adoptopenjdk/jitwatch/model/Tag;Lorg/adoptopenjdk/jitwatch/model/IParseDictionary;)V", "126: goto          152", "129: aload_0", "130: aload_1", "131: aload_2", "132: invokespecial #44                 // Method visitTagEliminateAllocation:(Lorg/adoptopenjdk/jitwatch/model/Tag;Lorg/adoptopenjdk/jitwatch/model/IParseDictionary;)V", "135: goto          152", "138: aload_0", "139: aload_1", "140: aload_2", "141: invokespecial #45                 // Method visitTagEliminateLock:(Lorg/adoptopenjdk/jitwatch/model/Tag;Lorg/adoptopenjdk/jitwatch/model/IParseDictionary;)V", "144: goto          152", "147: aload_0", "148: aload_1", "149: invokevirtual #46                 // Method handleOther:(Lorg/adoptopenjdk/jitwatch/model/Tag;)V", "152: return" };
        MemberBytecode memberBytecode = UnitTestUtil.createMemberBytecode(lines);
        SequenceCountOperation counter = new SequenceCountOperation(1);
        counter.processInstructions("Foo", memberBytecode);
        Map<InstructionSequence, Integer> result = counter.getSequenceScores();
        log(result);
        Assert.assertEquals(19, result.size());
        checkSequence(result, 5, ALOAD_1);
        checkSequence(result, 6, INVOKEVIRTUAL);
        checkSequence(result, 1, ASTORE_3);
        checkSequence(result, 1, ICONST_M1);
        checkSequence(result, 4, ISTORE);
        checkSequence(result, 4, ALOAD_3);
        checkSequence(result, 1, LOOKUPSWITCH);
        checkSequence(result, 3, LDC);
        checkSequence(result, 3, IFEQ);
        checkSequence(result, 1, ICONST_0);
        checkSequence(result, 1, ICONST_1);
        checkSequence(result, 1, ICONST_2);
        checkSequence(result, 1, ILOAD);
        checkSequence(result, 1, TABLESWITCH);
        checkSequence(result, 4, ALOAD_0);
        checkSequence(result, 3, ALOAD_2);
        checkSequence(result, 3, INVOKESPECIAL);
        checkSequence(result, 5, GOTO);
        checkSequence(result, 1, RETURN);
    }

    @Test
    public void testLongBytecodeChain6() {
        String[] lines = new String[]{ "0: aload_1", "1: invokevirtual #38                 // Method org/adoptopenjdk/jitwatch/model/Tag.getName:()Ljava/lang/String;", "4: astore_3", "5: iconst_m1", "6: istore        4", "8: aload_3", "9: invokevirtual #39                 // Method java/lang/String.hashCode:()I", "12: lookupswitch  { // 3", "   -1067517155: 63", "     106437299: 48", "     922165544: 78", "       default: 90", "  }", "48: aload_3", "49: ldc           #14                 // String parse", "51: invokevirtual #40                 // Method java/lang/String.equals:(Ljava/lang/Object;)Z", "54: ifeq          90", "57: iconst_0", "58: istore        4", "60: goto          90", "63: aload_3", "64: ldc           #41                 // String eliminate_allocation", "66: invokevirtual #40                 // Method java/lang/String.equals:(Ljava/lang/Object;)Z", "69: ifeq          90", "72: iconst_1", "73: istore        4", "75: goto          90", "78: aload_3", "79: ldc           #42                 // String eliminate_lock", "81: invokevirtual #40                 // Method java/lang/String.equals:(Ljava/lang/Object;)Z", "84: ifeq          90", "87: iconst_2", "88: istore        4", "90: iload         4", "92: tableswitch   { // 0 to 2", "             0: 120", "             1: 129", "             2: 138", "       default: 147", "  }", "120: aload_0", "121: aload_1", "122: aload_2", "123: invokespecial #43                 // Method visitTagParse:(Lorg/adoptopenjdk/jitwatch/model/Tag;Lorg/adoptopenjdk/jitwatch/model/IParseDictionary;)V", "126: goto          152", "129: aload_0", "130: aload_1", "131: aload_2", "132: invokespecial #44                 // Method visitTagEliminateAllocation:(Lorg/adoptopenjdk/jitwatch/model/Tag;Lorg/adoptopenjdk/jitwatch/model/IParseDictionary;)V", "135: goto          152", "138: aload_0", "139: aload_1", "140: aload_2", "141: invokespecial #45                 // Method visitTagEliminateLock:(Lorg/adoptopenjdk/jitwatch/model/Tag;Lorg/adoptopenjdk/jitwatch/model/IParseDictionary;)V", "144: goto          152", "147: aload_0", "148: aload_1", "149: invokevirtual #46                 // Method handleOther:(Lorg/adoptopenjdk/jitwatch/model/Tag;)V", "152: return" };
        MemberBytecode memberBytecode = UnitTestUtil.createMemberBytecode(lines);
        SequenceCountOperation counter = new SequenceCountOperation(6);
        counter.processInstructions("Foo", memberBytecode);
        Map<InstructionSequence, Integer> result = counter.getSequenceScores();
        log(result);
        Assert.assertEquals(29, result.size());
        checkSequence(result, 1, ALOAD_1, INVOKEVIRTUAL, ASTORE_3, ICONST_M1, ISTORE, ALOAD_3);
        checkSequence(result, 1, INVOKEVIRTUAL, ASTORE_3, ICONST_M1, ISTORE, ALOAD_3, INVOKEVIRTUAL);
        checkSequence(result, 1, ASTORE_3, ICONST_M1, ISTORE, ALOAD_3, INVOKEVIRTUAL, LOOKUPSWITCH);
        checkSequence(result, 1, ICONST_M1, ISTORE, ALOAD_3, INVOKEVIRTUAL, LOOKUPSWITCH, ALOAD_3);
        checkSequence(result, 1, ISTORE, ALOAD_3, INVOKEVIRTUAL, LOOKUPSWITCH, ALOAD_3, LDC);
        checkSequence(result, 1, ALOAD_3, INVOKEVIRTUAL, LOOKUPSWITCH, ALOAD_3, LDC, INVOKEVIRTUAL);
        checkSequence(result, 1, INVOKEVIRTUAL, LOOKUPSWITCH, ALOAD_3, LDC, INVOKEVIRTUAL, IFEQ);
        checkSequence(result, 1, LOOKUPSWITCH, ALOAD_3, LDC, INVOKEVIRTUAL, IFEQ, ICONST_0);
        checkSequence(result, 1, ALOAD_3, LDC, INVOKEVIRTUAL, IFEQ, ICONST_0, ISTORE);
        checkSequence(result, 1, LDC, INVOKEVIRTUAL, IFEQ, ICONST_0, ISTORE, GOTO);
        checkSequence(result, 1, INVOKEVIRTUAL, IFEQ, ICONST_0, ISTORE, GOTO, ILOAD);
        checkSequence(result, 1, IFEQ, ICONST_0, ISTORE, GOTO, ILOAD, TABLESWITCH);
        checkSequence(result, 1, ICONST_0, ISTORE, GOTO, ILOAD, TABLESWITCH, ALOAD_0);
        checkSequence(result, 2, ISTORE, GOTO, ILOAD, TABLESWITCH, ALOAD_0, ALOAD_1);
        checkSequence(result, 2, GOTO, ILOAD, TABLESWITCH, ALOAD_0, ALOAD_1, ALOAD_2);
        checkSequence(result, 1, ILOAD, TABLESWITCH, ALOAD_0, ALOAD_1, ALOAD_2, INVOKESPECIAL);
        checkSequence(result, 1, TABLESWITCH, ALOAD_0, ALOAD_1, ALOAD_2, INVOKESPECIAL, GOTO);
        checkSequence(result, 3, ALOAD_0, ALOAD_1, ALOAD_2, INVOKESPECIAL, GOTO, RETURN);
        checkSequence(result, 1, ALOAD_3, LDC, INVOKEVIRTUAL, IFEQ, ICONST_1, ISTORE);
        checkSequence(result, 1, LDC, INVOKEVIRTUAL, IFEQ, ICONST_1, ISTORE, GOTO);
        checkSequence(result, 1, INVOKEVIRTUAL, IFEQ, ICONST_1, ISTORE, GOTO, ILOAD);
        checkSequence(result, 1, IFEQ, ICONST_1, ISTORE, GOTO, ILOAD, TABLESWITCH);
        checkSequence(result, 1, ICONST_1, ISTORE, GOTO, ILOAD, TABLESWITCH, ALOAD_0);
        checkSequence(result, 1, ALOAD_3, LDC, INVOKEVIRTUAL, IFEQ, ICONST_2, ISTORE);
        checkSequence(result, 1, LDC, INVOKEVIRTUAL, IFEQ, ICONST_2, ISTORE, ILOAD);
        checkSequence(result, 1, INVOKEVIRTUAL, IFEQ, ICONST_2, ISTORE, ILOAD, TABLESWITCH);
        checkSequence(result, 1, IFEQ, ICONST_2, ISTORE, ILOAD, TABLESWITCH, ALOAD_0);
        checkSequence(result, 1, ICONST_2, ISTORE, ILOAD, TABLESWITCH, ALOAD_0, ALOAD_1);
        checkSequence(result, 1, ISTORE, ILOAD, TABLESWITCH, ALOAD_0, ALOAD_1, ALOAD_2);
    }

    @Test
    public void testInfiniteLoopChain1() {
        String[] lines = new String[]{ "0: aload_1", "1: invokevirtual #38                 // Method org/adoptopenjdk/jitwatch/model/Tag.getName:()Ljava/lang/String;", "4: astore_3", "5: iconst_m1", "6: istore        4", "8: goto          0" };
        MemberBytecode memberBytecode = UnitTestUtil.createMemberBytecode(lines);
        SequenceCountOperation counter = new SequenceCountOperation(1);
        counter.processInstructions("Foo", memberBytecode);
        Map<InstructionSequence, Integer> result = counter.getSequenceScores();
        log(result);
        Assert.assertEquals(6, result.size());
        checkSequence(result, 1, ALOAD_1);
        checkSequence(result, 1, INVOKEVIRTUAL);
        checkSequence(result, 1, ASTORE_3);
        checkSequence(result, 1, ICONST_M1);
        checkSequence(result, 1, ISTORE);
        checkSequence(result, 1, GOTO);
    }

    @Test
    public void testInfiniteLoopChain2() {
        String[] lines = new String[]{ "0: aload_1", "1: invokevirtual #38                 // Method org/adoptopenjdk/jitwatch/model/Tag.getName:()Ljava/lang/String;", "4: astore_3", "5: iconst_m1", "6: istore        4", "8: goto          0" };
        MemberBytecode memberBytecode = UnitTestUtil.createMemberBytecode(lines);
        SequenceCountOperation counter = new SequenceCountOperation(2);
        counter.processInstructions("Foo", memberBytecode);
        Map<InstructionSequence, Integer> result = counter.getSequenceScores();
        log(result);
        Assert.assertEquals(6, result.size());
        checkSequence(result, 1, ALOAD_1, INVOKEVIRTUAL);
        checkSequence(result, 1, INVOKEVIRTUAL, ASTORE_3);
        checkSequence(result, 1, ASTORE_3, ICONST_M1);
        checkSequence(result, 1, ICONST_M1, ISTORE);
        checkSequence(result, 1, ISTORE, GOTO);
        checkSequence(result, 1, GOTO, ALOAD_1);
    }

    @Test
    public void testInfiniteLoopChain3() {
        String[] lines = new String[]{ "0: aload_1", "1: invokevirtual #38                 // Method org/adoptopenjdk/jitwatch/model/Tag.getName:()Ljava/lang/String;", "4: astore_3", "5: iconst_m1", "6: istore        4", "8: goto          0" };
        MemberBytecode memberBytecode = UnitTestUtil.createMemberBytecode(lines);
        SequenceCountOperation counter = new SequenceCountOperation(3);
        counter.processInstructions("Foo", memberBytecode);
        Map<InstructionSequence, Integer> result = counter.getSequenceScores();
        log(result);
        Assert.assertEquals(6, result.size());
        checkSequence(result, 1, ALOAD_1, INVOKEVIRTUAL, ASTORE_3);
        checkSequence(result, 1, INVOKEVIRTUAL, ASTORE_3, ICONST_M1);
        checkSequence(result, 1, ASTORE_3, ICONST_M1, ISTORE);
        checkSequence(result, 1, ICONST_M1, ISTORE, GOTO);
        checkSequence(result, 1, ISTORE, GOTO, ALOAD_1);
        checkSequence(result, 1, GOTO, ALOAD_1, INVOKEVIRTUAL);
    }

    @Test
    public void testFollowGotoChain1() {
        String[] lines = new String[]{ "0: goto          2", "1: goto          3", "2: goto          1", "3: return        " };
        MemberBytecode memberBytecode = UnitTestUtil.createMemberBytecode(lines);
        SequenceCountOperation counter = new SequenceCountOperation(1);
        counter.processInstructions("Foo", memberBytecode);
        Map<InstructionSequence, Integer> result = counter.getSequenceScores();
        log(result);
        Assert.assertEquals(2, result.size());
        checkSequence(result, 3, GOTO);
        checkSequence(result, 1, RETURN);
    }

    @Test
    public void testFollowGotoChain2() {
        String[] lines = new String[]{ "0: goto          2", "1: goto          3", "2: goto          1", "3: return        " };
        MemberBytecode memberBytecode = UnitTestUtil.createMemberBytecode(lines);
        SequenceCountOperation counter = new SequenceCountOperation(2);
        counter.processInstructions("Foo", memberBytecode);
        Map<InstructionSequence, Integer> result = counter.getSequenceScores();
        log(result);
        Assert.assertEquals(2, result.size());
        checkSequence(result, 2, GOTO, GOTO);
        checkSequence(result, 1, GOTO, RETURN);
    }

    @Test
    public void testAthrow() {
        String[] lines = new String[]{ "0: getstatic       #5   // Field socketImplCtor:Ljava/lang/reflect/Constructor;", "3: iconst_0        ", "4: anewarray       #6   // class java/lang/Object", "7: invokevirtual   #7   // Method java/lang/reflect/Constructor.newInstance:([Ljava/lang/Object;)Ljava/lang/Object;", "10: checkcast       #8   // class java/net/SocketImpl", "13: areturn         ", "14: astore_0        ", "15: new             #10  // class java/lang/AssertionError", "18: dup             ", "19: aload_0         ", "20: invokespecial   #11  // Method java/lang/AssertionError.\"<init>\":(Ljava/lang/Object;)V", "23: athrow          ", "24: astore_0        ", "25: new             #10  // class java/lang/AssertionError", "28: dup             ", "29: aload_0         ", "30: invokespecial   #11  // Method java/lang/AssertionError.\"<init>\":(Ljava/lang/Object;)V", "33: athrow          ", "34: astore_0        ", "35: new             #10  // class java/lang/AssertionError", "38: dup             ", "39: aload_0         ", "40: invokespecial   #11  // Method java/lang/AssertionError.\"<init>\":(Ljava/lang/Object;)V", "43: athrow        " };
        MemberBytecode memberBytecode = UnitTestUtil.createMemberBytecode(lines);
        SequenceCountOperation counter = new SequenceCountOperation(3);
        counter.processInstructions("Foo", memberBytecode);
        Map<InstructionSequence, Integer> result = counter.getSequenceScores();
        log(result);
        Assert.assertEquals(8, result.size());
        checkSequence(result, 1, GETSTATIC, ICONST_0, ANEWARRAY);
        checkSequence(result, 1, ICONST_0, ANEWARRAY, INVOKEVIRTUAL);
        checkSequence(result, 1, ANEWARRAY, INVOKEVIRTUAL, CHECKCAST);
        checkSequence(result, 1, INVOKEVIRTUAL, CHECKCAST, ARETURN);
        checkSequence(result, 3, ASTORE_0, NEW, DUP);
        checkSequence(result, 3, NEW, DUP, ALOAD_0);
        checkSequence(result, 3, DUP, ALOAD_0, INVOKESPECIAL);
        checkSequence(result, 3, ALOAD_0, INVOKESPECIAL, ATHROW);
    }
}

