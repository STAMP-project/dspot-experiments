/**
 * Copyright 2010-2012 VMware and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springsource.loaded.test;


import ReturnType.Kind.ARRAY;
import ReturnType.Kind.PRIMITIVE;
import ReturnType.Kind.REFERENCE;
import ReturnType.ReturnTypeBoolean;
import ReturnType.ReturnTypeByte;
import ReturnType.ReturnTypeChar;
import ReturnType.ReturnTypeDouble;
import ReturnType.ReturnTypeFloat;
import ReturnType.ReturnTypeInt;
import ReturnType.ReturnTypeLong;
import ReturnType.ReturnTypeShort;
import ReturnType.ReturnTypeVoid;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.springsource.loaded.Constants;
import org.springsource.loaded.GlobalConfiguration;
import org.springsource.loaded.TypeRegistry;
import org.springsource.loaded.Utils;
import org.springsource.loaded.Utils.ReturnType;
import org.springsource.loaded.test.infra.FakeMethodVisitor;


/**
 * Test the Util methods.
 *
 * @author Andy Clement
 */
public class UtilsTests extends SpringLoadedTests implements Constants {
    // Test the encoding of a number to a string and the subsequent decoding
    @Test
    public void encoding() {
        // long l = 82348278L;
        Random rand = new Random(666);
        for (int r = 0; r < 2000; r++) {
            long l = Math.abs(rand.nextLong());
            String encoded = Utils.encode(l);
            // System.out.println("Encoded " + l + " to " + encoded);
            long decoded = Utils.decode(encoded);
            Assert.assertEquals(l, decoded);
        }
    }

    @Test
    public void testParamDescriptors() {
        Assert.assertEquals("(Ljava/lang/String;)", Utils.toParamDescriptor(String.class));
        Assert.assertEquals("([Ljava/lang/String;)", Utils.toParamDescriptor(String[].class));
        Assert.assertEquals("(I)", Utils.toParamDescriptor(Integer.TYPE));
        Assert.assertEquals("([I)", Utils.toParamDescriptor(int[].class));
    }

    @Test
    public void testReturnDescriptors() {
        Assert.assertEquals("java/lang/String", Utils.getReturnTypeDescriptor("(II)Ljava/lang/String;").descriptor);
        Assert.assertEquals("I", Utils.getReturnTypeDescriptor("(II)I").descriptor);
        Assert.assertEquals("[I", Utils.getReturnTypeDescriptor("(II)[I").descriptor);
        Assert.assertEquals("[Ljava/lang/String;", Utils.getReturnTypeDescriptor("(II)[Ljava/lang/String;").descriptor);
    }

    /**
     * Check the sequence of instructions created for a cast and unbox for all primitives
     */
    @Test
    public void testUnboxing() {
        FakeMethodVisitor fmv = new FakeMethodVisitor();
        // First variant checks passing string rather than just one char
        Utils.insertUnboxInsnsIfNecessary(fmv, "F", true);
        Assert.assertEquals("visitTypeInsn(CHECKCAST,java/lang/Float) visitMethodInsn(INVOKEVIRTUAL,java/lang/Float,floatValue,()F)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'F', true);
        Assert.assertEquals("visitTypeInsn(CHECKCAST,java/lang/Float) visitMethodInsn(INVOKEVIRTUAL,java/lang/Float,floatValue,()F)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'Z', true);
        Assert.assertEquals("visitTypeInsn(CHECKCAST,java/lang/Boolean) visitMethodInsn(INVOKEVIRTUAL,java/lang/Boolean,booleanValue,()Z)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'S', true);
        Assert.assertEquals("visitTypeInsn(CHECKCAST,java/lang/Short) visitMethodInsn(INVOKEVIRTUAL,java/lang/Short,shortValue,()S)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'J', true);
        Assert.assertEquals("visitTypeInsn(CHECKCAST,java/lang/Long) visitMethodInsn(INVOKEVIRTUAL,java/lang/Long,longValue,()J)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'D', true);
        Assert.assertEquals("visitTypeInsn(CHECKCAST,java/lang/Double) visitMethodInsn(INVOKEVIRTUAL,java/lang/Double,doubleValue,()D)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'C', true);
        Assert.assertEquals("visitTypeInsn(CHECKCAST,java/lang/Character) visitMethodInsn(INVOKEVIRTUAL,java/lang/Character,charValue,()C)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'B', true);
        Assert.assertEquals("visitTypeInsn(CHECKCAST,java/lang/Byte) visitMethodInsn(INVOKEVIRTUAL,java/lang/Byte,byteValue,()B)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'I', true);
        Assert.assertEquals("visitTypeInsn(CHECKCAST,java/lang/Integer) visitMethodInsn(INVOKEVIRTUAL,java/lang/Integer,intValue,()I)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsnsIfNecessary(fmv, "Rubbish", true);
        // should be a nop as nothing to do
        Assert.assertEquals(0, fmv.getEvents().length());
        fmv.clearEvents();
        try {
            Utils.insertUnboxInsns(fmv, '[', true);
            Assert.fail("Should have blown up due to invalid primitive being passed in");
        } catch (IllegalArgumentException iae) {
            // success
        }
    }

    /**
     * Check the sequence of instructions created for an unbox (no cast)
     */
    @Test
    public void testUnboxingNoCast() {
        FakeMethodVisitor fmv = new FakeMethodVisitor();
        Utils.insertUnboxInsns(fmv, 'F', false);
        Assert.assertEquals("visitMethodInsn(INVOKEVIRTUAL,java/lang/Float,floatValue,()F)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'Z', false);
        Assert.assertEquals("visitMethodInsn(INVOKEVIRTUAL,java/lang/Boolean,booleanValue,()Z)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'S', false);
        Assert.assertEquals("visitMethodInsn(INVOKEVIRTUAL,java/lang/Short,shortValue,()S)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'J', false);
        Assert.assertEquals("visitMethodInsn(INVOKEVIRTUAL,java/lang/Long,longValue,()J)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'D', false);
        Assert.assertEquals("visitMethodInsn(INVOKEVIRTUAL,java/lang/Double,doubleValue,()D)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'C', false);
        Assert.assertEquals("visitMethodInsn(INVOKEVIRTUAL,java/lang/Character,charValue,()C)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'B', false);
        Assert.assertEquals("visitMethodInsn(INVOKEVIRTUAL,java/lang/Byte,byteValue,()B)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertUnboxInsns(fmv, 'I', false);
        Assert.assertEquals("visitMethodInsn(INVOKEVIRTUAL,java/lang/Integer,intValue,()I)", fmv.getEvents());
        fmv.clearEvents();
    }

    /**
     * Check the sequence of instructions created for an unbox (no cast)
     */
    @Test
    public void testBoxing() {
        FakeMethodVisitor fmv = new FakeMethodVisitor();
        Utils.insertBoxInsns(fmv, 'F');
        Assert.assertEquals("visitMethodInsn(INVOKESTATIC,java/lang/Float,valueOf,(F)Ljava/lang/Float;)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertBoxInsns(fmv, 'Z');
        Assert.assertEquals("visitMethodInsn(INVOKESTATIC,java/lang/Boolean,valueOf,(Z)Ljava/lang/Boolean;)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertBoxInsns(fmv, 'S');
        Assert.assertEquals("visitMethodInsn(INVOKESTATIC,java/lang/Short,valueOf,(S)Ljava/lang/Short;)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertBoxInsns(fmv, 'J');
        Assert.assertEquals("visitMethodInsn(INVOKESTATIC,java/lang/Long,valueOf,(J)Ljava/lang/Long;)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertBoxInsns(fmv, 'D');
        Assert.assertEquals("visitMethodInsn(INVOKESTATIC,java/lang/Double,valueOf,(D)Ljava/lang/Double;)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertBoxInsns(fmv, 'C');
        Assert.assertEquals("visitMethodInsn(INVOKESTATIC,java/lang/Character,valueOf,(C)Ljava/lang/Character;)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertBoxInsns(fmv, 'B');
        Assert.assertEquals("visitMethodInsn(INVOKESTATIC,java/lang/Byte,valueOf,(B)Ljava/lang/Byte;)", fmv.getEvents());
        fmv.clearEvents();
        Utils.insertBoxInsns(fmv, 'I');
        Assert.assertEquals("visitMethodInsn(INVOKESTATIC,java/lang/Integer,valueOf,(I)Ljava/lang/Integer;)", fmv.getEvents());
        fmv.clearEvents();
    }

    @Test
    public void loads() {
        FakeMethodVisitor fmv = new FakeMethodVisitor();
        Utils.createLoadsBasedOnDescriptor(fmv, "(Ljava/lang/String;)V", 0);
        Assert.assertEquals("visitVarInsn(ALOAD,0)", fmv.getEvents());
        fmv.clearEvents();
        Utils.createLoadsBasedOnDescriptor(fmv, "(B)V", 0);
        Assert.assertEquals("visitVarInsn(ILOAD,0)", fmv.getEvents());
        fmv.clearEvents();
        Utils.createLoadsBasedOnDescriptor(fmv, "(C)V", 0);
        Assert.assertEquals("visitVarInsn(ILOAD,0)", fmv.getEvents());
        fmv.clearEvents();
        Utils.createLoadsBasedOnDescriptor(fmv, "(D)V", 0);
        Assert.assertEquals("visitVarInsn(DLOAD,0)", fmv.getEvents());
        fmv.clearEvents();
        Utils.createLoadsBasedOnDescriptor(fmv, "(Z)V", 0);
        Assert.assertEquals("visitVarInsn(ILOAD,0)", fmv.getEvents());
        fmv.clearEvents();
        Utils.createLoadsBasedOnDescriptor(fmv, "(J)V", 0);
        Assert.assertEquals("visitVarInsn(LLOAD,0)", fmv.getEvents());
        fmv.clearEvents();
        Utils.createLoadsBasedOnDescriptor(fmv, "(F)V", 0);
        Assert.assertEquals("visitVarInsn(FLOAD,0)", fmv.getEvents());
        fmv.clearEvents();
        Utils.createLoadsBasedOnDescriptor(fmv, "(S)V", 0);
        Assert.assertEquals("visitVarInsn(ILOAD,0)", fmv.getEvents());
        fmv.clearEvents();
        Utils.createLoadsBasedOnDescriptor(fmv, "(I)V", 0);
        Assert.assertEquals("visitVarInsn(ILOAD,0)", fmv.getEvents());
        fmv.clearEvents();
        Utils.createLoadsBasedOnDescriptor(fmv, "([[S)V", 0);
        Assert.assertEquals("visitVarInsn(ALOAD,0)", fmv.getEvents());
        fmv.clearEvents();
        Utils.createLoadsBasedOnDescriptor(fmv, "([[Ljava/lang/String;)V", 0);
        Assert.assertEquals("visitVarInsn(ALOAD,0)", fmv.getEvents());
    }

    @Test
    public void loads2() {
        FakeMethodVisitor fmv = new FakeMethodVisitor();
        Utils.createLoadsBasedOnDescriptor(fmv, "(BCDS)V", 0);
        Assert.assertEquals("visitVarInsn(ILOAD,0) visitVarInsn(ILOAD,1) visitVarInsn(DLOAD,2) visitVarInsn(ILOAD,4)", fmv.getEvents());
        fmv.clearEvents();
        Utils.createLoadsBasedOnDescriptor(fmv, "(Ljava/lang/String;J[I)V", 0);
        Assert.assertEquals("visitVarInsn(ALOAD,0) visitVarInsn(LLOAD,1) visitVarInsn(ALOAD,3)", fmv.getEvents());
        fmv.clearEvents();
        Utils.createLoadsBasedOnDescriptor(fmv, "(Ljava/lang/String;J[I)V", 4);
        Assert.assertEquals("visitVarInsn(ALOAD,4) visitVarInsn(LLOAD,5) visitVarInsn(ALOAD,7)", fmv.getEvents());
        fmv.clearEvents();
    }

    @Test
    public void generateInstructionsToUnpackArrayAccordingToDescriptor() {
        FakeMethodVisitor fmv = new FakeMethodVisitor();
        Utils.generateInstructionsToUnpackArrayAccordingToDescriptor(fmv, "(Ljava/lang/String;)V", 1);
        Assert.assertEquals("visitVarInsn(ALOAD,1) visitLdcInsn(0) visitInsn(AALOAD) visitTypeInsn(CHECKCAST,java/lang/String)", fmv.getEvents());
        fmv.clearEvents();
        Utils.generateInstructionsToUnpackArrayAccordingToDescriptor(fmv, "(I)V", 1);
        Assert.assertEquals("visitVarInsn(ALOAD,1) visitLdcInsn(0) visitInsn(AALOAD) visitTypeInsn(CHECKCAST,java/lang/Integer) visitMethodInsn(INVOKEVIRTUAL,java/lang/Integer,intValue,()I)", fmv.getEvents());
        fmv.clearEvents();
        Utils.generateInstructionsToUnpackArrayAccordingToDescriptor(fmv, "(Ljava/lang/String;Ljava/lang/Integer;)V", 2);
        Assert.assertEquals("visitVarInsn(ALOAD,2) visitLdcInsn(0) visitInsn(AALOAD) visitTypeInsn(CHECKCAST,java/lang/String) visitVarInsn(ALOAD,2) visitLdcInsn(1) visitInsn(AALOAD) visitTypeInsn(CHECKCAST,java/lang/Integer)", fmv.getEvents());
        fmv.clearEvents();
        Utils.generateInstructionsToUnpackArrayAccordingToDescriptor(fmv, "([Ljava/lang/String;)V", 2);
        Assert.assertEquals("visitVarInsn(ALOAD,2) visitLdcInsn(0) visitInsn(AALOAD) visitTypeInsn(CHECKCAST,[Ljava/lang/String;)", fmv.getEvents());
        fmv.clearEvents();
        Utils.generateInstructionsToUnpackArrayAccordingToDescriptor(fmv, "([[I)V", 2);
        Assert.assertEquals("visitVarInsn(ALOAD,2) visitLdcInsn(0) visitInsn(AALOAD) visitTypeInsn(CHECKCAST,[[I)", fmv.getEvents());
        fmv.clearEvents();
        try {
            Utils.generateInstructionsToUnpackArrayAccordingToDescriptor(fmv, "(Y)V", 1);
            Assert.fail();
        } catch (IllegalStateException ise) {
        }
    }

    /**
     * Test the helper that adds the correct return instructions based on the descriptor in use.
     */
    @Test
    public void testReturning() {
        FakeMethodVisitor fmv = new FakeMethodVisitor();
        Utils.addCorrectReturnInstruction(fmv, ReturnTypeVoid, true);
        Assert.assertEquals("visitInsn(RETURN)", fmv.getEvents());
        fmv.clearEvents();
        Utils.addCorrectReturnInstruction(fmv, ReturnTypeFloat, true);
        Assert.assertEquals("visitInsn(FRETURN)", fmv.getEvents());
        fmv.clearEvents();
        Utils.addCorrectReturnInstruction(fmv, ReturnTypeBoolean, true);
        Assert.assertEquals("visitInsn(IRETURN)", fmv.getEvents());
        fmv.clearEvents();
        Utils.addCorrectReturnInstruction(fmv, ReturnTypeShort, true);
        Assert.assertEquals("visitInsn(IRETURN)", fmv.getEvents());
        fmv.clearEvents();
        Utils.addCorrectReturnInstruction(fmv, ReturnTypeLong, true);
        Assert.assertEquals("visitInsn(LRETURN)", fmv.getEvents());
        fmv.clearEvents();
        Utils.addCorrectReturnInstruction(fmv, ReturnTypeDouble, true);
        Assert.assertEquals("visitInsn(DRETURN)", fmv.getEvents());
        fmv.clearEvents();
        Utils.addCorrectReturnInstruction(fmv, ReturnTypeChar, true);
        Assert.assertEquals("visitInsn(IRETURN)", fmv.getEvents());
        fmv.clearEvents();
        Utils.addCorrectReturnInstruction(fmv, ReturnTypeByte, true);
        Assert.assertEquals("visitInsn(IRETURN)", fmv.getEvents());
        fmv.clearEvents();
        Utils.addCorrectReturnInstruction(fmv, ReturnTypeInt, true);
        Assert.assertEquals("visitInsn(IRETURN)", fmv.getEvents());
        fmv.clearEvents();
        Utils.addCorrectReturnInstruction(fmv, ReturnType.getReturnType("java/lang/String", REFERENCE), true);
        Assert.assertEquals("visitTypeInsn(CHECKCAST,java/lang/String) visitInsn(ARETURN)", fmv.getEvents());
        fmv.clearEvents();
        Utils.addCorrectReturnInstruction(fmv, ReturnType.getReturnType("[[I", ARRAY), true);
        Assert.assertEquals("visitTypeInsn(CHECKCAST,[[I) visitInsn(ARETURN)", fmv.getEvents());
        fmv.clearEvents();
        Utils.addCorrectReturnInstruction(fmv, ReturnType.getReturnType("[[Ljava/lang/String;", ARRAY), true);
        Assert.assertEquals("visitTypeInsn(CHECKCAST,[[Ljava/lang/String;) visitInsn(ARETURN)", fmv.getEvents());
        fmv.clearEvents();
    }

    @Test
    public void descriptorSizes() {
        Assert.assertEquals(1, Utils.getSize("(I)V"));
        Assert.assertEquals(1, Utils.getSize("(B)V"));
        Assert.assertEquals(1, Utils.getSize("(C)V"));
        Assert.assertEquals(2, Utils.getSize("(D)V"));
        Assert.assertEquals(1, Utils.getSize("(S)V"));
        Assert.assertEquals(1, Utils.getSize("(Z)V"));
        Assert.assertEquals(2, Utils.getSize("(J)V"));
        Assert.assertEquals(1, Utils.getSize("(F)V"));
        Assert.assertEquals(1, Utils.getSize("(Ljava/lang/String;)V"));
        Assert.assertEquals(1, Utils.getSize("([Ljava/lang/String;)V"));
        Assert.assertEquals(1, Utils.getSize("([D)V"));
        Assert.assertEquals(2, Utils.getSize("(II)V"));
        Assert.assertEquals(5, Utils.getSize("(BLjava/lang/String;[[JD)V"));
        try {
            Assert.assertEquals(5, Utils.getSize("(Y)V"));
            Assert.fail();
        } catch (IllegalStateException ise) {
        }
    }

    @Test
    public void typeDescriptorSizes() {
        Assert.assertEquals(1, Utils.sizeOf("I"));
        Assert.assertEquals(1, Utils.sizeOf("B"));
        Assert.assertEquals(1, Utils.sizeOf("C"));
        Assert.assertEquals(2, Utils.sizeOf("D"));
        Assert.assertEquals(1, Utils.sizeOf("S"));
        Assert.assertEquals(1, Utils.sizeOf("Z"));
        Assert.assertEquals(2, Utils.sizeOf("J"));
        Assert.assertEquals(1, Utils.sizeOf("F"));
        Assert.assertEquals(1, Utils.sizeOf("Lava/lang/String;"));
        Assert.assertEquals(1, Utils.sizeOf("[D"));
    }

    @Test
    public void methodDescriptorParameterCounts() {
        Assert.assertEquals(0, Utils.getParameterCount("()V"));
        Assert.assertEquals(1, Utils.getParameterCount("(I)V"));
        Assert.assertEquals(1, Utils.getParameterCount("(Ljava/lang/String;)V"));
        Assert.assertEquals(1, Utils.getParameterCount("([Ljava/lang/String;)V"));
        Assert.assertEquals(2, Utils.getParameterCount("(IZ)V"));
        Assert.assertEquals(2, Utils.getParameterCount("(Ljava/lang/String;Z)V"));
        Assert.assertEquals(3, Utils.getParameterCount("(DZ[[J)V"));
        Assert.assertEquals(3, Utils.getParameterCount("([[D[[Z[[J)V"));
    }

    @Test
    public void paramSequencing() {
        Assert.assertNull(Utils.getParamSequence("()V"));
        Assert.assertEquals("I", Utils.getParamSequence("(I)V"));
        Assert.assertEquals("B", Utils.getParamSequence("(B)V"));
        Assert.assertEquals("C", Utils.getParamSequence("(C)V"));
        Assert.assertEquals("D", Utils.getParamSequence("(D)V"));
        Assert.assertEquals("F", Utils.getParamSequence("(F)V"));
        Assert.assertEquals("Z", Utils.getParamSequence("(Z)V"));
        Assert.assertEquals("J", Utils.getParamSequence("(J)V"));
        Assert.assertEquals("S", Utils.getParamSequence("(S)V"));
        Assert.assertEquals("O", Utils.getParamSequence("(Ljava/lang/String;)V"));
        Assert.assertEquals("O", Utils.getParamSequence("([[Ljava/lang/String;)V"));
        Assert.assertEquals("O", Utils.getParamSequence("([I)V"));
    }

    @Test
    public void appendDescriptor() {
        StringBuilder temp = new StringBuilder();
        Utils.appendDescriptor(Integer.TYPE, (temp = new StringBuilder()));
        Assert.assertEquals("I", temp.toString());
        Utils.appendDescriptor(Byte.TYPE, (temp = new StringBuilder()));
        Assert.assertEquals("B", temp.toString());
        Utils.appendDescriptor(Character.TYPE, (temp = new StringBuilder()));
        Assert.assertEquals("C", temp.toString());
        Utils.appendDescriptor(Boolean.TYPE, (temp = new StringBuilder()));
        Assert.assertEquals("Z", temp.toString());
        Utils.appendDescriptor(Short.TYPE, (temp = new StringBuilder()));
        Assert.assertEquals("S", temp.toString());
        Utils.appendDescriptor(Float.TYPE, (temp = new StringBuilder()));
        Assert.assertEquals("F", temp.toString());
        Utils.appendDescriptor(Double.TYPE, (temp = new StringBuilder()));
        Assert.assertEquals("D", temp.toString());
        Utils.appendDescriptor(Long.TYPE, (temp = new StringBuilder()));
        Assert.assertEquals("J", temp.toString());
        Utils.appendDescriptor(Void.TYPE, (temp = new StringBuilder()));
        Assert.assertEquals("V", temp.toString());
        Utils.appendDescriptor(String.class, (temp = new StringBuilder()));
        Assert.assertEquals("Ljava/lang/String;", temp.toString());
        Utils.appendDescriptor(Array.newInstance(String.class, 1).getClass(), (temp = new StringBuilder()));
        Assert.assertEquals("[Ljava/lang/String;", temp.toString());
        Utils.appendDescriptor(Array.newInstance(Array.newInstance(Integer.TYPE, 1).getClass(), 1).getClass(), (temp = new StringBuilder()));
        Assert.assertEquals("[[I", temp.toString());
    }

    @Test
    public void toMethodDescriptor() throws Exception {
        Method toStringMethod = Object.class.getDeclaredMethod("toString");
        Assert.assertEquals("()Ljava/lang/String;", Utils.toMethodDescriptor(toStringMethod, false));
        try {
            Assert.assertEquals("()Ljava/lang/String;", Utils.toMethodDescriptor(toStringMethod, true));
            Assert.fail();
        } catch (IllegalStateException ise) {
        }
        Method numberOfLeadingZerosMethod = Integer.class.getDeclaredMethod("numberOfLeadingZeros", Integer.TYPE);
        Assert.assertEquals("(I)I", Utils.toMethodDescriptor(numberOfLeadingZerosMethod, false));
        // First ( is skipped, caller is expected to build the first part
        Assert.assertEquals(")I", Utils.toMethodDescriptor(numberOfLeadingZerosMethod, true));
        Method valueOfMethod = Integer.class.getDeclaredMethod("valueOf", String.class, Integer.TYPE);
        Assert.assertEquals("(Ljava/lang/String;I)Ljava/lang/Integer;", Utils.toMethodDescriptor(valueOfMethod, false));
        Assert.assertEquals("(Ljava/lang/String;I)Ljava/lang/Integer;", Utils.toMethodDescriptor(valueOfMethod));
        Assert.assertEquals("I)Ljava/lang/Integer;", Utils.toMethodDescriptor(valueOfMethod, true));
    }

    @Test
    public void isAssignableFromWithClass() throws Exception {
        TypeRegistry reg = getTypeRegistry();
        Assert.assertTrue(Utils.isAssignableFrom(reg, String.class, "java.lang.Object"));
        Assert.assertTrue(Utils.isAssignableFrom(reg, String.class, "java.io.Serializable"));
        Assert.assertTrue(Utils.isAssignableFrom(reg, HashMap.class, "java.util.Map"));
        Assert.assertTrue(Utils.isAssignableFrom(reg, String.class, "java.lang.String"));
        Assert.assertFalse(Utils.isAssignableFrom(reg, Map.class, "java.lang.String"));
    }

    @Test
    public void toPaddedNumber() {
        Assert.assertEquals("01", Utils.toPaddedNumber(1, 2));
        Assert.assertEquals("0032768", Utils.toPaddedNumber(32768, 7));
    }

    @Test
    public void isInitializer() {
        Assert.assertTrue(Utils.isInitializer("<init>"));
        Assert.assertTrue(Utils.isInitializer("<clinit>"));
        Assert.assertFalse(Utils.isInitializer("foobar"));
    }

    @Test
    public void toCombined() {
        Assert.assertEquals(1, ((Utils.toCombined(1, 2)) >>> 16));
        Assert.assertEquals(2, ((Utils.toCombined(1, 2)) & 65535));
    }

    @Test
    public void dump() {
        byte[] basicBytes = loadBytesForClass("basic.Basic");
        String s = Utils.dump("basic/Basic", basicBytes);
        // System.out.println(s);
        File f = new File(s);
        Assert.assertTrue(f.exists());
        // tidy up
        while ((f.toString().indexOf("sl_")) != (-1)) {
            f.delete();
            // System.out.println("deleted " + f);
            f = f.getParentFile();
        } 
    }

    @Test
    public void dumpAndLoad() throws Exception {
        byte[] basicBytes = loadBytesForClass("basic.Basic");
        String s = Utils.dump("basic/Basic", basicBytes);
        // System.out.println(s);
        File f = new File(s);
        Assert.assertTrue(f.exists());
        byte[] loadedBytes = Utils.loadFromStream(new FileInputStream(f));
        Assert.assertEquals(basicBytes.length, loadedBytes.length);
        // tidy up
        while ((f.toString().indexOf("sl_")) != (-1)) {
            f.delete();
            // System.out.println("deleted " + f);
            f = f.getParentFile();
        } 
    }

    @Test
    public void toOpcodeString() throws Exception {
        Assert.assertEquals("ACONST_NULL", Utils.toOpcodeString(ACONST_NULL));
        Assert.assertEquals("ICONST_0", Utils.toOpcodeString(ICONST_0));
        Assert.assertEquals("ICONST_1", Utils.toOpcodeString(ICONST_1));
        Assert.assertEquals("ICONST_2", Utils.toOpcodeString(ICONST_2));
        Assert.assertEquals("ICONST_3", Utils.toOpcodeString(ICONST_3));
        Assert.assertEquals("ICONST_4", Utils.toOpcodeString(ICONST_4));
        Assert.assertEquals("ICONST_5", Utils.toOpcodeString(ICONST_5));
        Assert.assertEquals("FCONST_0", Utils.toOpcodeString(FCONST_0));
        Assert.assertEquals("FCONST_1", Utils.toOpcodeString(FCONST_1));
        Assert.assertEquals("FCONST_2", Utils.toOpcodeString(FCONST_2));
        Assert.assertEquals("BIPUSH", Utils.toOpcodeString(BIPUSH));
        Assert.assertEquals("SIPUSH", Utils.toOpcodeString(SIPUSH));
        Assert.assertEquals("IALOAD", Utils.toOpcodeString(IALOAD));
        Assert.assertEquals("LALOAD", Utils.toOpcodeString(LALOAD));
        Assert.assertEquals("FALOAD", Utils.toOpcodeString(FALOAD));
        Assert.assertEquals("AALOAD", Utils.toOpcodeString(AALOAD));
        Assert.assertEquals("IASTORE", Utils.toOpcodeString(IASTORE));
        Assert.assertEquals("AASTORE", Utils.toOpcodeString(AASTORE));
        Assert.assertEquals("BASTORE", Utils.toOpcodeString(BASTORE));
        Assert.assertEquals("POP", Utils.toOpcodeString(POP));
        Assert.assertEquals("POP2", Utils.toOpcodeString(POP2));
        Assert.assertEquals("DUP", Utils.toOpcodeString(DUP));
        Assert.assertEquals("DUP_X1", Utils.toOpcodeString(DUP_X1));
        Assert.assertEquals("DUP_X2", Utils.toOpcodeString(DUP_X2));
        Assert.assertEquals("DUP2", Utils.toOpcodeString(DUP2));
        Assert.assertEquals("DUP2_X1", Utils.toOpcodeString(DUP2_X1));
        Assert.assertEquals("DUP2_X2", Utils.toOpcodeString(DUP2_X2));
        Assert.assertEquals("IADD", Utils.toOpcodeString(IADD));
        Assert.assertEquals("LMUL", Utils.toOpcodeString(LMUL));
        Assert.assertEquals("FMUL", Utils.toOpcodeString(FMUL));
        Assert.assertEquals("DMUL", Utils.toOpcodeString(DMUL));
        Assert.assertEquals("I2D", Utils.toOpcodeString(I2D));
        Assert.assertEquals("L2F", Utils.toOpcodeString(L2F));
        Assert.assertEquals("I2C", Utils.toOpcodeString(I2C));
        Assert.assertEquals("I2S", Utils.toOpcodeString(I2S));
        Assert.assertEquals("IFNE", Utils.toOpcodeString(IFNE));
        Assert.assertEquals("IFLT", Utils.toOpcodeString(IFLT));
        Assert.assertEquals("IFGE", Utils.toOpcodeString(IFGE));
        Assert.assertEquals("IFGT", Utils.toOpcodeString(IFGT));
        Assert.assertEquals("IFLE", Utils.toOpcodeString(IFLE));
        Assert.assertEquals("IFLE", Utils.toOpcodeString(IFLE));
        Assert.assertEquals("IF_ICMPEQ", Utils.toOpcodeString(IF_ICMPEQ));
        Assert.assertEquals("IF_ICMPNE", Utils.toOpcodeString(IF_ICMPNE));
        Assert.assertEquals("IF_ICMPLT", Utils.toOpcodeString(IF_ICMPLT));
        Assert.assertEquals("IF_ICMPGE", Utils.toOpcodeString(IF_ICMPGE));
        Assert.assertEquals("IF_ICMPGT", Utils.toOpcodeString(IF_ICMPGT));
        Assert.assertEquals("IF_ICMPLE", Utils.toOpcodeString(IF_ICMPLE));
        Assert.assertEquals("IF_ACMPEQ", Utils.toOpcodeString(IF_ACMPEQ));
        Assert.assertEquals("IF_ACMPNE", Utils.toOpcodeString(IF_ACMPNE));
        Assert.assertEquals("INVOKESPECIAL", Utils.toOpcodeString(INVOKESPECIAL));
        Assert.assertEquals("INVOKESTATIC", Utils.toOpcodeString(INVOKESTATIC));
        Assert.assertEquals("INVOKEINTERFACE", Utils.toOpcodeString(INVOKEINTERFACE));
        Assert.assertEquals("NEWARRAY", Utils.toOpcodeString(NEWARRAY));
        Assert.assertEquals("ANEWARRAY", Utils.toOpcodeString(ANEWARRAY));
        Assert.assertEquals("ARRAYLENGTH", Utils.toOpcodeString(ARRAYLENGTH));
        Assert.assertEquals("IFNONNULL", Utils.toOpcodeString(IFNONNULL));
    }

    @Test
    public void getDispatcherName() throws Exception {
        Assert.assertEquals("A$$D123", Utils.getDispatcherName("A", "123"));
    }

    @Test
    public void toResultCheckIfNull() throws Exception {
        Assert.assertEquals(1, Utils.toResultCheckIfNull(1, "I"));
        Assert.assertEquals(new Integer(1), Utils.toResultCheckIfNull(1, "Ljava/lang/Integer;"));
        Assert.assertEquals(0, Utils.toResultCheckIfNull(null, "I"));
        Assert.assertEquals(((byte) (0)), Utils.toResultCheckIfNull(null, "B"));
        Assert.assertEquals(((char) (0)), Utils.toResultCheckIfNull(null, "C"));
        Assert.assertEquals(((short) (0)), Utils.toResultCheckIfNull(null, "S"));
        Assert.assertEquals(((long) (0)), Utils.toResultCheckIfNull(null, "J"));
        Assert.assertEquals(0.0F, Utils.toResultCheckIfNull(null, "F"));
        Assert.assertEquals(0.0, Utils.toResultCheckIfNull(null, "D"));
        Assert.assertEquals(false, Utils.toResultCheckIfNull(null, "Z"));
        Assert.assertNull(Utils.toResultCheckIfNull(null, "Ljava/lang/String;"));
        try {
            Assert.assertEquals(((long) (0)), Utils.toResultCheckIfNull(null, "L"));
            Assert.fail();
        } catch (IllegalStateException ise) {
            // success
        }
        // public static final Integer DEFAULT_INT = Integer.valueOf(0);
        // public static final Byte DEFAULT_BYTE = Byte.valueOf((byte) 0);
        // public static final Character DEFAULT_CHAR = Character.valueOf((char) 0);
        // public static final Short DEFAULT_SHORT = Short.valueOf((short) 0);
        // public static final Long DEFAULT_LONG = Long.valueOf(0);
        // public static final Float DEFAULT_FLOAT = Float.valueOf(0);
        // public static final Double DEFAULT_DOUBLE = Double.valueOf(0);
    }

    @Test
    public void isObjectUnboxableTo() throws Exception {
        Assert.assertFalse(Utils.isObjectIsUnboxableTo(String.class, 'I'));
        Assert.assertTrue(Utils.isObjectIsUnboxableTo(Integer.class, 'I'));
        Assert.assertFalse(Utils.isObjectIsUnboxableTo(String.class, 'S'));
        Assert.assertTrue(Utils.isObjectIsUnboxableTo(Short.class, 'S'));
        Assert.assertFalse(Utils.isObjectIsUnboxableTo(String.class, 'J'));
        Assert.assertTrue(Utils.isObjectIsUnboxableTo(Long.class, 'J'));
        Assert.assertFalse(Utils.isObjectIsUnboxableTo(String.class, 'F'));
        Assert.assertTrue(Utils.isObjectIsUnboxableTo(Float.class, 'F'));
        Assert.assertFalse(Utils.isObjectIsUnboxableTo(String.class, 'D'));
        Assert.assertTrue(Utils.isObjectIsUnboxableTo(Double.class, 'D'));
        Assert.assertFalse(Utils.isObjectIsUnboxableTo(String.class, 'B'));
        Assert.assertTrue(Utils.isObjectIsUnboxableTo(Byte.class, 'B'));
        Assert.assertFalse(Utils.isObjectIsUnboxableTo(String.class, 'C'));
        Assert.assertTrue(Utils.isObjectIsUnboxableTo(Character.class, 'C'));
        Assert.assertFalse(Utils.isObjectIsUnboxableTo(String.class, 'Z'));
        Assert.assertTrue(Utils.isObjectIsUnboxableTo(Boolean.class, 'Z'));
        try {
            Assert.assertTrue(Utils.isObjectIsUnboxableTo(Boolean.class, 'V'));
            Assert.fail("Should not know about 'V'");
        } catch (IllegalStateException ise) {
            // success
        }
    }

    @Test
    public void getExecutorName() throws Exception {
        Assert.assertEquals("A$$E123", Utils.getExecutorName("A", "123"));
    }

    @Test
    public void stripFirstParameter() throws Exception {
        Assert.assertEquals("(Ljava/lang/Object;)V", Utils.stripFirstParameter("(Ljava/lang/String;Ljava/lang/Object;)V"));
        if (GlobalConfiguration.assertsMode) {
            try {
                Utils.stripFirstParameter("()V");
                Assert.fail();
            } catch (IllegalStateException ise) {
                // success
            }
        }
    }

    @Test
    public void getReturnType() throws Exception {
        Assert.assertEquals(ReturnTypeVoid, Utils.ReturnType.getReturnType("V()", PRIMITIVE));
        Assert.assertEquals(ReturnTypeFloat, Utils.ReturnType.getReturnType("F()", PRIMITIVE));
        Assert.assertEquals(ReturnTypeBoolean, Utils.ReturnType.getReturnType("Z()", PRIMITIVE));
        Assert.assertEquals(ReturnTypeShort, Utils.ReturnType.getReturnType("S()", PRIMITIVE));
        Assert.assertEquals(ReturnTypeInt, Utils.ReturnType.getReturnType("I()", PRIMITIVE));
        Assert.assertEquals(ReturnTypeByte, Utils.ReturnType.getReturnType("B()", PRIMITIVE));
        Assert.assertEquals(ReturnTypeChar, Utils.ReturnType.getReturnType("C()", PRIMITIVE));
        Assert.assertEquals(ReturnTypeLong, Utils.ReturnType.getReturnType("J()", PRIMITIVE));
        Assert.assertEquals(ReturnTypeDouble, Utils.ReturnType.getReturnType("D()", PRIMITIVE));
    }

    @Test
    public void toSuperAccessor() throws Exception {
        Assert.assertEquals("__super$FooType$Foomethod", Utils.toSuperAccessor("a/b/c/FooType", "Foomethod"));
    }

    @Test
    public void isConvertableFrom() throws Exception {
        Assert.assertTrue(Utils.isConvertableFrom(short.class, byte.class));
        Assert.assertTrue(Utils.isConvertableFrom(int.class, byte.class));
        Assert.assertTrue(Utils.isConvertableFrom(long.class, byte.class));
        Assert.assertTrue(Utils.isConvertableFrom(float.class, byte.class));
        Assert.assertTrue(Utils.isConvertableFrom(double.class, byte.class));
        Assert.assertTrue(Utils.isConvertableFrom(short.class, short.class));
        Assert.assertTrue(Utils.isConvertableFrom(int.class, short.class));
        Assert.assertTrue(Utils.isConvertableFrom(long.class, short.class));
        Assert.assertTrue(Utils.isConvertableFrom(float.class, short.class));
        Assert.assertTrue(Utils.isConvertableFrom(double.class, short.class));
        Assert.assertTrue(Utils.isConvertableFrom(int.class, char.class));
        Assert.assertTrue(Utils.isConvertableFrom(long.class, char.class));
        Assert.assertTrue(Utils.isConvertableFrom(float.class, char.class));
        Assert.assertTrue(Utils.isConvertableFrom(double.class, char.class));
        Assert.assertTrue(Utils.isConvertableFrom(long.class, int.class));
        Assert.assertTrue(Utils.isConvertableFrom(float.class, int.class));
        Assert.assertTrue(Utils.isConvertableFrom(double.class, int.class));
        Assert.assertFalse(Utils.isConvertableFrom(byte.class, int.class));
        Assert.assertTrue(Utils.isConvertableFrom(float.class, long.class));
        Assert.assertTrue(Utils.isConvertableFrom(double.class, long.class));
        Assert.assertTrue(Utils.isConvertableFrom(double.class, float.class));
        Assert.assertTrue(Utils.isConvertableFrom(String.class, String.class));
        Assert.assertFalse(Utils.isConvertableFrom(Integer.class, String.class));
    }
}

