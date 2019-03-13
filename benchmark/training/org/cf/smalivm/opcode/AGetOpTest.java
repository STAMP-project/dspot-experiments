package org.cf.smalivm.opcode;


import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;


public class AGetOpTest {
    private static final String CLASS_NAME = "Laget_test;";

    private VMState expected;

    private VMState initial;

    @Test
    public void canGet() {
        initial.setRegisters(0, new int[]{ 66 }, "[I", 1, 0, "I");
        expected.setRegisters(0, 66, "I");
        VMTester.test(AGetOpTest.CLASS_NAME, "get()V", initial, expected);
    }

    @Test
    public void canGetBoolean() {
        initial.setRegisters(0, new boolean[]{ true }, "[Z", 1, 0, "I");
        expected.setRegisters(0, true, "Z");
        VMTester.test(AGetOpTest.CLASS_NAME, "getBoolean()V", initial, expected);
    }

    @Test
    public void canGetByte() {
        initial.setRegisters(0, new byte[]{ 14 }, "[B", 1, 0, "I");
        expected.setRegisters(0, ((byte) (14)), "B");
        VMTester.test(AGetOpTest.CLASS_NAME, "getByte()V", initial, expected);
    }

    @Test
    public void canGetChar() {
        initial.setRegisters(0, new char[]{ 'a' }, "[C", 1, 0, "I");
        expected.setRegisters(0, 'a', "C");
        VMTester.test(AGetOpTest.CLASS_NAME, "getChar()V", initial, expected);
    }

    @Test
    public void canGetObject() {
        String objectValue = "stringy";
        String[] objectArray = new String[]{ objectValue };
        String objectType = "Ljava/lang/String;";
        initial.setRegisters(0, objectArray, objectType, 1, 0, "I");
        expected.setRegisters(0, objectValue, objectType);
        VMTester.test(AGetOpTest.CLASS_NAME, "getObject()V", initial, expected);
    }

    @Test
    public void canGetShort() {
        initial.setRegisters(0, new short[]{ 66 }, "[S", 1, 0, "I");
        expected.setRegisters(0, ((short) (66)), "S");
        VMTester.test(AGetOpTest.CLASS_NAME, "getShort()V", initial, expected);
    }

    @Test
    public void canGetUninitializedPrimitive() {
        initial.setRegisters(0, new int[1], "[I", 1, 0, "I");
        expected.setRegisters(0, new int[1][0], "I");
        VMTester.test(AGetOpTest.CLASS_NAME, "getUninitializedInt()V", initial, expected);
    }

    @Test
    public void canGetUnknownArray() {
        initial.setRegisters(0, new UnknownValue(), "[I", 1, 0, "I");
        expected.setRegisters(0, new UnknownValue(), "I");
        VMTester.test(AGetOpTest.CLASS_NAME, "get()V", initial, expected);
    }

    @Test
    public void canGetUnknownElement() {
        initial.setRegisters(0, new Object[]{ new UnknownValue(), 5 }, "[I", 1, 0, "I");
        expected.setRegisters(0, new UnknownValue(), "I");
        VMTester.test(AGetOpTest.CLASS_NAME, "get()V", initial, expected);
    }

    @Test
    public void canGetUnknownIndex() {
        initial.setRegisters(0, new int[]{ 66 }, "[I", 1, new UnknownValue(), "I");
        expected.setRegisters(0, new UnknownValue(), "I");
        VMTester.test(AGetOpTest.CLASS_NAME, "get()V", initial, expected);
    }

    @Test
    public void canGetWide() {
        initial.setRegisters(0, new long[]{ 1099511627776L }, "J", 1, 0, "I");
        expected.setRegisters(0, 1099511627776L, "J");
        VMTester.test(AGetOpTest.CLASS_NAME, "getWide()V", initial, expected);
    }

    @Test
    public void canGetWithShortIndex() {
        Short index = 0;
        initial.setRegisters(0, new int[]{ 66 }, "[I", 1, index, "S");
        expected.setRegisters(index.intValue(), 66, "I", 1, Short.valueOf(index.shortValue()), "S");
        VMTester.test(AGetOpTest.CLASS_NAME, "get()V", initial, expected);
    }

    @Test
    public void nullArrayValueThrowsNullPointerExceptionAndHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, null, "[I", 1, 0, "I");
        AGetOpTest.testException("getWithCatch()V", NullPointerException.class, initial);
    }

    @Test
    public void outOfBoundsIndexThrowsArrayIndexOutOfBoundsExceptionAndHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, new int[5], "[I", 1, 10, "I");
        AGetOpTest.testException("getWithCatch()V", ArrayIndexOutOfBoundsException.class, initial);
    }
}

