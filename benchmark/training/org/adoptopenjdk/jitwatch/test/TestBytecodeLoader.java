/**
 * Copyright (c) 2013-2017 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;


import Opcode.ALOAD_0;
import Opcode.ALOAD_1;
import Opcode.GETFIELD;
import Opcode.IF_ICMPNE;
import Opcode.IINC;
import Opcode.ILOAD;
import Opcode.INVOKESTATIC;
import Opcode.IREM;
import Opcode.ISTORE_1;
import Opcode.LDC;
import Opcode.LOOKUPSWITCH;
import Opcode.LSTORE_2;
import Opcode.NEWARRAY;
import Opcode.POP2;
import Opcode.RETURN;
import Opcode.TABLESWITCH;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.adoptopenjdk.jitwatch.loader.BytecodeLoader;
import org.adoptopenjdk.jitwatch.model.IMetaMember;
import org.adoptopenjdk.jitwatch.model.JITDataModel;
import org.adoptopenjdk.jitwatch.model.MemberSignatureParts;
import org.adoptopenjdk.jitwatch.model.MetaClass;
import org.adoptopenjdk.jitwatch.model.bytecode.BCParamConstant;
import org.adoptopenjdk.jitwatch.model.bytecode.BCParamNumeric;
import org.adoptopenjdk.jitwatch.model.bytecode.BCParamString;
import org.adoptopenjdk.jitwatch.model.bytecode.BCParamSwitch;
import org.adoptopenjdk.jitwatch.model.bytecode.BytecodeInstruction;
import org.adoptopenjdk.jitwatch.model.bytecode.ClassBC;
import org.adoptopenjdk.jitwatch.model.bytecode.ExceptionTable;
import org.adoptopenjdk.jitwatch.model.bytecode.ExceptionTableEntry;
import org.adoptopenjdk.jitwatch.model.bytecode.IBytecodeParam;
import org.adoptopenjdk.jitwatch.model.bytecode.LineTable;
import org.adoptopenjdk.jitwatch.model.bytecode.LineTableEntry;
import org.adoptopenjdk.jitwatch.model.bytecode.MemberBytecode;
import org.junit.Assert;
import org.junit.Test;


public class TestBytecodeLoader {
    @Test
    public void testBytecodeSignature() {
        String fqClassName = "java.lang.StringBuilder";
        String methodName = "charAt";
        Class<?>[] params = new Class<?>[]{ int.class };
        IMetaMember member = UnitTestUtil.createTestMetaMember(fqClassName, methodName, params, char.class);
        ClassBC classBytecode = BytecodeLoader.fetchBytecodeForClass(new ArrayList<String>(), fqClassName, false);
        MemberBytecode memberBytecode = classBytecode.getMemberBytecode(member);
        Assert.assertNotNull(memberBytecode);
        List<BytecodeInstruction> instructions = memberBytecode.getInstructions();
        Assert.assertNotNull(instructions);
    }

    @Test
    public void testParseBytecodes() {
        StringBuilder builder = new StringBuilder();
        builder.append("0: ldc           #224                // int 1000000").append(S_NEWLINE);
        builder.append("2: istore_1").append(S_NEWLINE);
        builder.append("3: aload_0").append(S_NEWLINE);
        builder.append("4: arraylength").append(S_NEWLINE);
        builder.append("5: iconst_1").append(S_NEWLINE);
        builder.append("6: if_icmpne     28").append(S_NEWLINE);
        builder.append("9: aload_0").append(S_NEWLINE);
        builder.append("10: iconst_0").append(S_NEWLINE);
        builder.append("11: aaload").append(S_NEWLINE);
        List<BytecodeInstruction> instructions = BytecodeLoader.parseInstructions(builder.toString());
        Assert.assertEquals(9, instructions.size());
        BytecodeInstruction i0 = instructions.get(0);
        Assert.assertEquals(0, i0.getOffset());
        Assert.assertEquals(LDC, i0.getOpcode());
        Assert.assertEquals(true, i0.hasParameters());
        Assert.assertEquals(1, i0.getParameters().size());
        IBytecodeParam paramI0 = i0.getParameters().get(0);
        Assert.assertTrue((paramI0 instanceof BCParamConstant));
        Assert.assertEquals(224, paramI0.getValue());
        Assert.assertEquals(true, i0.hasComment());
        Assert.assertEquals("// int 1000000", i0.getComment());
        BytecodeInstruction i1 = instructions.get(1);
        Assert.assertEquals(2, i1.getOffset());
        Assert.assertEquals(ISTORE_1, i1.getOpcode());
        Assert.assertEquals(false, i1.hasParameters());
        Assert.assertEquals(0, i1.getParameters().size());
        Assert.assertEquals(false, i1.hasComment());
        Assert.assertEquals(null, i1.getComment());
        BytecodeInstruction i5 = instructions.get(5);
        Assert.assertEquals(6, i5.getOffset());
        Assert.assertEquals(IF_ICMPNE, i5.getOpcode());
        Assert.assertEquals(1, i5.getParameters().size());
        Assert.assertEquals(true, i5.hasParameters());
        IBytecodeParam paramI5 = i5.getParameters().get(0);
        Assert.assertTrue((paramI5 instanceof BCParamNumeric));
        Assert.assertEquals(28, paramI5.getValue());
        Assert.assertEquals(false, i5.hasComment());
        Assert.assertEquals(null, i5.getComment());
    }

    @Test
    public void testParseBytecodeRegressionIinc() {
        StringBuilder builder = new StringBuilder();
        builder.append("0: lconst_0").append(S_NEWLINE);
        builder.append("1: lstore_2").append(S_NEWLINE);
        builder.append("2: iconst_0").append(S_NEWLINE);
        builder.append("3: lstore    2").append(S_NEWLINE);
        builder.append("4: iinc      4, 1").append(S_NEWLINE);
        builder.append("7: goto      47").append(S_NEWLINE);
        List<BytecodeInstruction> instructions = BytecodeLoader.parseInstructions(builder.toString());
        Assert.assertEquals(6, instructions.size());
        BytecodeInstruction i4 = instructions.get(4);
        Assert.assertEquals(4, i4.getOffset());
        Assert.assertEquals(IINC, i4.getOpcode());
        Assert.assertEquals(true, i4.hasParameters());
        Assert.assertEquals(2, i4.getParameters().size());
        IBytecodeParam paramI4a = i4.getParameters().get(0);
        IBytecodeParam paramI4b = i4.getParameters().get(1);
        Assert.assertTrue((paramI4a instanceof BCParamNumeric));
        Assert.assertTrue((paramI4b instanceof BCParamNumeric));
        Assert.assertEquals(4, paramI4a.getValue());
        Assert.assertEquals(1, paramI4b.getValue());
        Assert.assertEquals(false, i4.hasComment());
    }

    @Test
    public void testIloadW() {
        StringBuilder builder = new StringBuilder();
        builder.append("1: iload_w       340").append(S_NEWLINE);
        List<BytecodeInstruction> instructions = BytecodeLoader.parseInstructions(builder.toString());
        Assert.assertEquals(1, instructions.size());
        BytecodeInstruction i = instructions.get(0);
        Assert.assertEquals(1, i.getOffset());
        Assert.assertEquals(ILOAD, i.getOpcode());
        Assert.assertEquals(true, i.hasParameters());
        Assert.assertEquals(1, i.getParameters().size());
        BCParamNumeric varIndex = ((BCParamNumeric) (i.getParameters().get(0)));
        Assert.assertEquals(Integer.valueOf(340), varIndex.getValue());
    }

    @Test
    public void testParseBytecodeRegressionNegativeInc() {
        StringBuilder builder = new StringBuilder();
        builder.append("0: lconst_0").append(S_NEWLINE);
        builder.append("1: lstore_2").append(S_NEWLINE);
        builder.append("2: iconst_0").append(S_NEWLINE);
        builder.append("3: lstore    2").append(S_NEWLINE);
        builder.append("4: iinc      4, -1").append(S_NEWLINE);
        builder.append("7: goto      47").append(S_NEWLINE);
        List<BytecodeInstruction> instructions = BytecodeLoader.parseInstructions(builder.toString());
        Assert.assertEquals(6, instructions.size());
        BytecodeInstruction i4 = instructions.get(4);
        Assert.assertEquals(4, i4.getOffset());
        Assert.assertEquals(IINC, i4.getOpcode());
        Assert.assertEquals(true, i4.hasParameters());
        Assert.assertEquals(2, i4.getParameters().size());
        IBytecodeParam paramI4a = i4.getParameters().get(0);
        IBytecodeParam paramI4b = i4.getParameters().get(1);
        Assert.assertTrue((paramI4a instanceof BCParamNumeric));
        Assert.assertTrue((paramI4b instanceof BCParamNumeric));
        Assert.assertEquals(4, paramI4a.getValue());
        Assert.assertEquals((-1), paramI4b.getValue());
        Assert.assertEquals(false, i4.hasComment());
    }

    @Test
    public void testParseBytecodeRegressionNewArray() {
        StringBuilder builder = new StringBuilder();
        builder.append("3: newarray       int").append(S_NEWLINE);
        List<BytecodeInstruction> instructions = BytecodeLoader.parseInstructions(builder.toString());
        Assert.assertEquals(1, instructions.size());
        BytecodeInstruction i0 = instructions.get(0);
        Assert.assertEquals(3, i0.getOffset());
        Assert.assertEquals(NEWARRAY, i0.getOpcode());
        Assert.assertEquals(true, i0.hasParameters());
        Assert.assertEquals(1, i0.getParameters().size());
        IBytecodeParam paramI0 = i0.getParameters().get(0);
        Assert.assertTrue((paramI0 instanceof BCParamString));
        Assert.assertEquals("int", paramI0.getValue());
        Assert.assertEquals(false, i0.hasComment());
    }

    @Test
    public void testParseBytecodeRegressionTableSwitch() {
        StringBuilder builder = new StringBuilder();
        builder.append("0: lconst_0").append(S_NEWLINE);
        builder.append("1: lstore_2").append(S_NEWLINE);
        builder.append("2: iconst_0").append(S_NEWLINE);
        builder.append("3: tableswitch   { // -1 to 5").append(S_NEWLINE);
        builder.append("             -1: 100").append(S_NEWLINE);
        builder.append("              0: 101").append(S_NEWLINE);
        builder.append("              1: 102").append(S_NEWLINE);
        builder.append("              2: 103").append(S_NEWLINE);
        builder.append("              3: 104").append(S_NEWLINE);
        builder.append("              4: 105").append(S_NEWLINE);
        builder.append("              5: 106").append(S_NEWLINE);
        builder.append("        default: 107").append(S_NEWLINE);
        builder.append("}").append(S_NEWLINE);
        builder.append("99: lstore_2").append(S_NEWLINE);
        List<BytecodeInstruction> instructions = BytecodeLoader.parseInstructions(builder.toString());
        Assert.assertEquals(5, instructions.size());
        BytecodeInstruction i3 = instructions.get(3);
        Assert.assertEquals(3, i3.getOffset());
        Assert.assertEquals(TABLESWITCH, i3.getOpcode());
        Assert.assertEquals(true, i3.hasParameters());
        Assert.assertEquals(1, i3.getParameters().size());
        IBytecodeParam paramI0 = i3.getParameters().get(0);
        Assert.assertTrue((paramI0 instanceof BCParamSwitch));
        BytecodeInstruction i99 = instructions.get(4);
        Assert.assertEquals(99, i99.getOffset());
        Assert.assertEquals(LSTORE_2, i99.getOpcode());
        Assert.assertEquals(false, i99.hasParameters());
    }

    @Test
    public void testParseBytecodeRegressionTableSwitch2() {
        StringBuilder builder = new StringBuilder();
        builder.append("39: ldc           #8                  // int 1000000").append(S_NEWLINE);
        builder.append("41: if_icmpge     104").append(S_NEWLINE);
        builder.append("44: iload         5").append(S_NEWLINE);
        builder.append("46: iconst_3").append(S_NEWLINE);
        builder.append("47: irem          ").append(S_NEWLINE);
        builder.append("48: tableswitch   { // 0 to 2").append(S_NEWLINE);
        builder.append("             0: 76").append(S_NEWLINE);
        builder.append("             1: 82").append(S_NEWLINE);
        builder.append("             2: 88").append(S_NEWLINE);
        builder.append("       default: 91").append(S_NEWLINE);
        builder.append("  }").append(S_NEWLINE);
        builder.append("76: aload_1       ").append(S_NEWLINE);
        builder.append("77: astore        4").append(S_NEWLINE);
        builder.append("79: goto          91").append(S_NEWLINE);
        List<BytecodeInstruction> instructions = BytecodeLoader.parseInstructions(builder.toString());
        Assert.assertEquals(9, instructions.size());
        BytecodeInstruction i47 = instructions.get(4);
        Assert.assertEquals(47, i47.getOffset());
        Assert.assertEquals(IREM, i47.getOpcode());
        Assert.assertEquals(false, i47.hasParameters());
        BytecodeInstruction i76 = instructions.get(6);
        Assert.assertEquals(76, i76.getOffset());
        Assert.assertEquals(ALOAD_1, i76.getOpcode());
        Assert.assertEquals(false, i76.hasParameters());
    }

    @Test
    public void testParseBytecodeRegressionLookupSwitch() {
        StringBuilder builder = new StringBuilder();
        builder.append("0: lconst_0").append(S_NEWLINE);
        builder.append("1: lstore_2").append(S_NEWLINE);
        builder.append("2: iconst_0").append(S_NEWLINE);
        builder.append("3: lookupswitch   { // -1 to 5").append(S_NEWLINE);
        builder.append("             -1: 100").append(S_NEWLINE);
        builder.append("              0: 101").append(S_NEWLINE);
        builder.append("              1: 102").append(S_NEWLINE);
        builder.append("              2: 103").append(S_NEWLINE);
        builder.append("              3: 104").append(S_NEWLINE);
        builder.append("              4: 105").append(S_NEWLINE);
        builder.append("              5: 106").append(S_NEWLINE);
        builder.append("        default: 107").append(S_NEWLINE);
        builder.append("}").append(S_NEWLINE);
        builder.append("99: lstore_2").append(S_NEWLINE);
        List<BytecodeInstruction> instructions = BytecodeLoader.parseInstructions(builder.toString());
        Assert.assertEquals(5, instructions.size());
        BytecodeInstruction i3 = instructions.get(3);
        Assert.assertEquals(3, i3.getOffset());
        Assert.assertEquals(LOOKUPSWITCH, i3.getOpcode());
        Assert.assertEquals(true, i3.hasParameters());
        Assert.assertEquals(1, i3.getParameters().size());
        IBytecodeParam paramI0 = i3.getParameters().get(0);
        Assert.assertTrue((paramI0 instanceof BCParamSwitch));
    }

    @Test
    public void testLineNumberTable() throws ClassNotFoundException {
        String[] lines = new String[]{ "Classfile TestBytecodeLoader.class", "  Last modified 18-May-2014; size 426 bytes", "  MD5 checksum d8d0af7620175f82d2c5c753b493196f", "  Compiled from \"TestBytecodeLoader.java\"", "public class org.adoptopenjdk.jitwatch.test.TestBytecodeLoader", "  SourceFile: \"TestBytecodeLoader.java\"", "  minor version: 0", "  major version: 51", "  flags: ACC_PUBLIC, ACC_SUPER", "Constant pool:", "   #1 = Methodref          #3.#18         //  java/lang/Object.\"<init>\":()V", "   #2 = Class              #19            //  org/adoptopenjdk/jitwatch/test/TestBytecodeLoader", "   #3 = Class              #20            //  java/lang/Object", "   #4 = Utf8               <init>", "   #5 = Utf8               ()V", "   #6 = Utf8               Code", "   #7 = Utf8               LineNumberTable", "   #8 = Utf8               LocalVariableTable", "   #9 = Utf8               this", "  #10 = Utf8               Lorg/adoptopenjdk/jitwatch/test/TestBytecodeLoader;", "  #11 = Utf8               add", "  #12 = Utf8               (II)I", "  #13 = Utf8               a", "  #14 = Utf8               I", "  #15 = Utf8               b", "  #16 = Utf8               SourceFile", "  #17 = Utf8               TestBytecodeLoader.java", "  #18 = NameAndType        #4:#5          //  \"<init>\":()V", "  #19 = Utf8               org/adoptopenjdk/jitwatch/test/TestBytecodeLoader", "  #20 = Utf8               java/lang/Object", "{", "  public org.adoptopenjdk.jitwatch.test.TestBytecodeLoader();", "    flags: ACC_PUBLIC", "    Code:", "      stack=1, locals=1, args_size=1", "         0: aload_0       ", "         1: invokespecial #1                  // Method java/lang/Object.\"<init>\":()V", "         4: return        ", "      LineNumberTable:", "        line 3: 0", "      LocalVariableTable:", "        Start  Length  Slot  Name   Signature", "               0       5     0  this   Lorg/adoptopenjdk/jitwatch/test/TestBytecodeLoader;", "", "  public int add(int, int);", "    flags: ACC_PUBLIC", "    Code:", "      stack=2, locals=3, args_size=3", "         0: iload_1       ", "         1: iload_2       ", "         2: iadd          ", "         3: ireturn       ", "      LineNumberTable:", "        line 7: 0", "      LocalVariableTable:", "        Start  Length  Slot  Name   Signature", "               0       4     0  this   Lorg/adoptopenjdk/jitwatch/test/TestBytecodeLoader;", "               0       4     1     a   I", "               0       4     2     b   I", "}" };
        IMetaMember member = UnitTestUtil.createTestMetaMember(getClass().getName(), "add", new Class<?>[]{ int.class, int.class }, int.class);
        ClassBC classBytecode = BytecodeLoader.parse(getClass().getName(), lines, false);
        MemberBytecode memberBytecode = classBytecode.getMemberBytecode(member);
        Assert.assertNotNull(memberBytecode);
        List<BytecodeInstruction> instructions = memberBytecode.getInstructions();
        Assert.assertEquals(4, instructions.size());
        LineTable lineTable = memberBytecode.getLineTable();
        Assert.assertEquals(1, lineTable.size());
        LineTableEntry entry = lineTable.getEntryForSourceLine(7);
        int offset = entry.getBytecodeOffset();
        Assert.assertEquals(0, offset);
        JITDataModel model = new JITDataModel();
        MetaClass metaClass = UnitTestUtil.createMetaClassFor(model, getClass().getName());
        IMetaMember constructor = metaClass.getFirstConstructor();
        MemberBytecode memberBytecode2 = classBytecode.getMemberBytecode(constructor);
        Assert.assertNotNull(memberBytecode2);
        List<BytecodeInstruction> instructions2 = memberBytecode2.getInstructions();
        Assert.assertEquals(3, instructions2.size());
        LineTable lineTable2 = memberBytecode2.getLineTable();
        LineTableEntry entry2 = lineTable2.getEntryForSourceLine(3);
        int offset2 = entry2.getBytecodeOffset();
        Assert.assertEquals(0, offset2);
    }

    @Test
    public void testExceptionTable() throws ClassNotFoundException {
        String[] lines = new String[]{ "Classfile TestBytecodeLoader.class", "  Last modified 18-May-2014; size 426 bytes", "  MD5 checksum d8d0af7620175f82d2c5c753b493196f", "  Compiled from \"TestBytecodeLoader.java\"", "public class org.adoptopenjdk.jitwatch.test.TestBytecodeLoader", "  SourceFile: \"TestBytecodeLoader.java\"", "  minor version: 0", "  major version: 51", "  flags: ACC_PUBLIC, ACC_SUPER", "Constant pool:", "   #1 = Methodref          #3.#18         //  java/lang/Object.\"<init>\":()V", "   #2 = Class              #19            //  org/adoptopenjdk/jitwatch/test/TestBytecodeLoader", "   #3 = Class              #20            //  java/lang/Object", "   #4 = Utf8               <init>", "   #5 = Utf8               ()V", "   #6 = Utf8               Code", "   #7 = Utf8               LineNumberTable", "   #8 = Utf8               LocalVariableTable", "   #9 = Utf8               this", "  #10 = Utf8               Lorg/adoptopenjdk/jitwatch/test/TestBytecodeLoader;", "  #11 = Utf8               add", "  #12 = Utf8               (II)I", "  #13 = Utf8               a", "  #14 = Utf8               I", "  #15 = Utf8               b", "  #16 = Utf8               SourceFile", "  #17 = Utf8               TestBytecodeLoader.java", "  #18 = NameAndType        #4:#5          //  \"<init>\":()V", "  #19 = Utf8               org/adoptopenjdk/jitwatch/test/TestBytecodeLoader", "  #20 = Utf8               java/lang/Object", "{", "  public org.adoptopenjdk.jitwatch.test.TestBytecodeLoader();", "    flags: ACC_PUBLIC", "    Code:", "      stack=1, locals=1, args_size=1", "         0: aload_0       ", "         1: invokespecial #1                  // Method java/lang/Object.\"<init>\":()V", "         4: return        ", "      LineNumberTable:", "        line 3: 0", "      LocalVariableTable:", "        Start  Length  Slot  Name   Signature", "               0       5     0  this   Lorg/adoptopenjdk/jitwatch/test/TestBytecodeLoader;", "", "  public char getChar(char[], int);", "descriptor: ([CI)C", "flags: ACC_PUBLIC", "Code:", "  stack=2, locals=4, args_size=3", "     0: aload_1", "     1: iload_2", "     2: caload", "     3: ireturn", "     4: astore_3", "     5: bipush        42", "     7: ireturn", "  Exception table:", "     from    to  target type", "         0     3     4   Class java/lang/ArrayIndexOutOfBoundsException", "  LineNumberTable:", "    line 31: 0", "    line 33: 4", "    line 35: 5", "  LocalVariableTable:", "    Start  Length  Slot  Name   Signature", "        5       3     3     e   Ljava/lang/ArrayIndexOutOfBoundsException;", "        0       8     0  this   LHotThrow;", "        0       8     1 chars   [C", "        0       8     2 index   I", "  StackMapTable: number_of_entries = 1", "    frame_type = 68 /* same_locals_1_stack_item */", "      stack = [ class java/lang/ArrayIndexOutOfBoundsException ]", "}" };
        IMetaMember member = UnitTestUtil.createTestMetaMember(getClass().getName(), "getChar", new Class<?>[]{ char[].class, int.class }, char.class);
        ClassBC classBytecode = BytecodeLoader.parse(getClass().getName(), lines, false);
        MemberBytecode memberBytecode = classBytecode.getMemberBytecode(member);
        Assert.assertNotNull(memberBytecode);
        List<BytecodeInstruction> instructions = memberBytecode.getInstructions();
        Assert.assertEquals(7, instructions.size());
        LineTable lineTable = memberBytecode.getLineTable();
        Assert.assertEquals(3, lineTable.size());
        ExceptionTable exceptionTable = memberBytecode.getExceptionTable();
        Assert.assertEquals(1, exceptionTable.size());
        ExceptionTableEntry entry = exceptionTable.getEntries().get(0);
        Assert.assertEquals(0, entry.getFrom());
        Assert.assertEquals(3, entry.getTo());
        Assert.assertEquals(4, entry.getTarget());
        Assert.assertEquals("java/lang/ArrayIndexOutOfBoundsException", entry.getType());
        Assert.assertEquals(entry, exceptionTable.getEntryForBCI(2));
    }

    @Test
    public void testClassFileVersion() {
        String[] lines = new String[]{ "public class org.adoptopenjdk.jitwatch.demo.MakeHotSpotLog", "SourceFile: \"MakeHotSpotLog.java\"", "minor version: 1", "major version: 51", "flags: ACC_PUBLIC, ACC_SUPER" };
        ClassBC classBytecode = BytecodeLoader.parse("org.adoptopenjdk.jitwatch.demo.MakeHotSpotLog", lines, false);
        Assert.assertEquals(1, classBytecode.getMinorVersion());
        Assert.assertEquals(51, classBytecode.getMajorVersion());
    }

    @Test
    public void testClassFileVersionWithRuntimeAnnotations() {
        String[] lines = new String[]{ "public class org.adoptopenjdk.jitwatch.demo.MakeHotSpotLog", "SourceFile: \"MakeHotSpotLog.java\"", "  RuntimeVisibleAnnotations:", "    0: #49(#50=e#51.#52)", "    0: #49(#50=e#51.#52)", "    1: #53(#50=[e#54.#55])", "    2: #56(#50=e#57.#58)", "minor version: 1", "major version: 51", "flags: ACC_PUBLIC, ACC_SUPER" };
        ClassBC classBytecode = BytecodeLoader.parse("org.adoptopenjdk.jitwatch.demo.MakeHotSpotLog", lines, false);
        Assert.assertEquals(1, classBytecode.getMinorVersion());
        Assert.assertEquals(51, classBytecode.getMajorVersion());
    }

    @Test
    public void testRegressionJMHSampleWithRuntimeAnnotations() {
        String bcSig = "public void measureWrong();";
        String[] lines = new String[]{ bcSig, "      descriptor: ()V", "    flags: ACC_PUBLIC", "    Code:", "      stack=2, locals=1, args_size=1", "         0: aload_0       ", "         0: aload_0       ", "         1: getfield      #4          // Field x:D", "         4: invokestatic  #5                  // Method java/lang/Math.log:(D)D", "         7: pop2          ", "         8: return        ", "      LineNumberTable:", "        line 65: 0", "        line 66: 8", "      LocalVariableTable:", "        Start  Length  Slot  Name   Signature", "            0       9     0  this   Lorg/openjdk/jmh/samples/JMHSample_08_DeadCode;", "    RuntimeVisibleAnnotations:", "      0: #35()", "      0: #35()" };
        ClassBC classBytecode = BytecodeLoader.parse(getClass().getName(), lines, false);
        MemberSignatureParts msp = MemberSignatureParts.fromBytecodeSignature(getClass().getName(), bcSig);
        MemberBytecode memberBytecode = classBytecode.getMemberBytecodeForSignature(msp);
        Assert.assertNotNull(memberBytecode);
        List<BytecodeInstruction> instructions = memberBytecode.getInstructions();
        Assert.assertEquals(6, instructions.size());
        int pos = 0;
        BytecodeInstruction i0 = instructions.get((pos++));
        Assert.assertEquals(0, i0.getOffset());
        Assert.assertEquals(ALOAD_0, i0.getOpcode());
        Assert.assertEquals(false, i0.hasParameters());
        Assert.assertEquals(0, i0.getParameters().size());
        BytecodeInstruction i1 = instructions.get((pos++));
        Assert.assertEquals(0, i1.getOffset());
        Assert.assertEquals(ALOAD_0, i1.getOpcode());
        Assert.assertEquals(false, i1.hasParameters());
        Assert.assertEquals(0, i1.getParameters().size());
        BytecodeInstruction i2 = instructions.get((pos++));
        Assert.assertEquals(1, i2.getOffset());
        Assert.assertEquals(GETFIELD, i2.getOpcode());
        Assert.assertEquals(true, i2.hasParameters());
        Assert.assertEquals(1, i2.getParameters().size());
        BytecodeInstruction i3 = instructions.get((pos++));
        Assert.assertEquals(4, i3.getOffset());
        Assert.assertEquals(INVOKESTATIC, i3.getOpcode());
        Assert.assertEquals(true, i3.hasParameters());
        Assert.assertEquals(1, i3.getParameters().size());
        BytecodeInstruction i4 = instructions.get((pos++));
        Assert.assertEquals(7, i4.getOffset());
        Assert.assertEquals(POP2, i4.getOpcode());
        Assert.assertEquals(false, i4.hasParameters());
        Assert.assertEquals(0, i4.getParameters().size());
        BytecodeInstruction i5 = instructions.get((pos++));
        Assert.assertEquals(8, i5.getOffset());
        Assert.assertEquals(RETURN, i5.getOpcode());
        Assert.assertEquals(false, i5.hasParameters());
        Assert.assertEquals(0, i5.getParameters().size());
        LineTable lineTable = memberBytecode.getLineTable();
        Assert.assertEquals(2, lineTable.size());
        Assert.assertEquals(0, lineTable.getEntryForSourceLine(65).getBytecodeOffset());
        Assert.assertEquals(8, lineTable.getEntryForSourceLine(66).getBytecodeOffset());
        Assert.assertEquals(65, lineTable.findSourceLineForBytecodeOffset(0));
        Assert.assertEquals(65, lineTable.findSourceLineForBytecodeOffset(1));
        Assert.assertEquals(65, lineTable.findSourceLineForBytecodeOffset(2));
        Assert.assertEquals(65, lineTable.findSourceLineForBytecodeOffset(3));
        Assert.assertEquals(65, lineTable.findSourceLineForBytecodeOffset(4));
        Assert.assertEquals(65, lineTable.findSourceLineForBytecodeOffset(5));
        Assert.assertEquals(65, lineTable.findSourceLineForBytecodeOffset(6));
        Assert.assertEquals(65, lineTable.findSourceLineForBytecodeOffset(7));
        Assert.assertEquals(66, lineTable.findSourceLineForBytecodeOffset(8));
        Assert.assertEquals(66, lineTable.findSourceLineForBytecodeOffset(9));
        Assert.assertEquals(66, lineTable.findSourceLineForBytecodeOffset(100));
    }

    @Test
    public void testStaticInitialiserRegression() {
        String[] lines = new String[]{ "  static {};", "                        descriptor: ()V", "                        flags: ACC_STATIC", "                        Code:", "                          stack=3, locals=0, args_size=0", "                             0: ldc           #149                // class org/adoptopenjdk/jitwatch/loader/BytecodeLoader", "                             2: invokestatic  #150                // Method org/slf4j/LoggerFactory.getLogger:(Ljava/lang/Class;)Lorg/slf4j/Logger;", "                             5: putstatic     #33                 // Field logger:Lorg/slf4j/Logger;", "                             8: new           #151                // class java/util/HashMap", "                            11: dup", "                            12: invokespecial #152                // Method java/util/HashMap.\"<init>\":()V", "                            15: putstatic     #90                 // Field sectionLabelMap:Ljava/util/Map;", "                            18: getstatic     #90                 // Field sectionLabelMap:Ljava/util/Map;", "                            21: ldc           #153                // String Constant pool:", "                            23: getstatic     #69                 // Field org/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection.CONSTANT_POOL:Lorg/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection;", "                            26: invokeinterface #154,  3          // InterfaceMethod java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "                            31: pop", "                            32: getstatic     #90                 // Field sectionLabelMap:Ljava/util/Map;", "                            35: ldc           #155                // String Code:", "                            37: getstatic     #67                 // Field org/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection.CODE:Lorg/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection;", "                            40: invokeinterface #154,  3          // InterfaceMethod java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "                            45: pop", "                            46: getstatic     #90                 // Field sectionLabelMap:Ljava/util/Map;", "                            49: ldc           #156                // String LineNumberTable:", "                            51: getstatic     #71                 // Field org/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection.LINETABLE:Lorg/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection;", "                            54: invokeinterface #154,  3          // InterfaceMethod java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "                            59: pop", "                            60: getstatic     #90                 // Field sectionLabelMap:Ljava/util/Map;", "                            63: ldc           #157                // String LocalVariableTable:", "                            65: getstatic     #158                // Field org/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection.LOCALVARIABLETABLE:Lorg/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection;", "                            68: invokeinterface #154,  3          // InterfaceMethod java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "                            73: pop", "                            74: getstatic     #90                 // Field sectionLabelMap:Ljava/util/Map;", "                            77: ldc           #159                // String RuntimeVisibleAnnotations:", "                            79: getstatic     #160                // Field org/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection.RUNTIMEVISIBLEANNOTATIONS:Lorg/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSecti$", "                            82: invokeinterface #154,  3          // InterfaceMethod java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "                            87: pop", "                            88: getstatic     #90                 // Field sectionLabelMap:Ljava/util/Map;", "                            91: ldc           #161                // String Exceptions:", "                            93: getstatic     #162                // Field org/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection.EXCEPTIONS:Lorg/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection;", "                            96: invokeinterface #154,  3          // InterfaceMethod java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "                           101: pop", "                           102: getstatic     #90                 // Field sectionLabelMap:Ljava/util/Map;", "                           105: ldc           #163                // String StackMapTable:", "                           107: getstatic     #164                // Field org/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection.STACKMAPTABLE:Lorg/adoptopenjdk/jitwatch/loader/BytecodeLoader$BytecodeSection;", "                           110: invokeinterface #154,  3          // InterfaceMethod java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "                           115: pop", "                           116: return", "                          LineNumberTable:", "                            line 30: 0", "                            line 37: 8", "                            line 41: 18", "                            line 42: 32", "                            line 43: 46", "                            line 44: 60", "                            line 45: 74" };
        ClassBC classBytecode = BytecodeLoader.parse(getClass().getName(), lines, false);
        MemberSignatureParts msp = MemberSignatureParts.fromBytecodeSignature(getClass().getName(), S_BYTECODE_STATIC_INITIALISER_SIGNATURE);
        MemberBytecode memberBytecode = classBytecode.getMemberBytecodeForSignature(msp);
        Assert.assertNotNull(memberBytecode);
        List<BytecodeInstruction> instructions = memberBytecode.getInstructions();
        Assert.assertEquals(43, instructions.size());
    }

    @Test
    public void testExampleOverloadedMethodString() throws URISyntaxException {
        doTestOverloadedMethod(String.class);
    }

    @Test
    public void testExampleOverloadedMethodObject() throws URISyntaxException {
        doTestOverloadedMethod(Object.class);
    }

    @Test
    public void testSignatureGenericsParser() {
        String line = "Signature: #259                         // <K:Ljava/lang/Integer;V:Ljava/lang/String;>Ljava/util/AbstractMap<TK;TV;>;Ljava/util/concurrent/ConcurrentMap<TK;TV;>;Ljava/io/Serializable;";
        ClassBC classBytecode = new ClassBC(getClass().getName());
        BytecodeLoader.buildClassGenerics(line, classBytecode);
        Assert.assertEquals(2, classBytecode.getGenericsMap().size());
        Assert.assertTrue(classBytecode.getGenericsMap().containsKey("K"));
        Assert.assertEquals("java.lang.Integer", classBytecode.getGenericsMap().get("K"));
        Assert.assertTrue(classBytecode.getGenericsMap().containsKey("V"));
        Assert.assertEquals("java.lang.String", classBytecode.getGenericsMap().get("V"));
    }
}

