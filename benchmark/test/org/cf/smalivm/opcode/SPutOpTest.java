package org.cf.smalivm.opcode;


import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.junit.Test;


public class SPutOpTest {
    private static final String CLASS_NAME = "Lsput_test;";

    private VMState expected;

    private VMState initial;

    @Test
    public void putStaticBoolean() {
        initial.setRegisters(0, true, "Z");
        expected.setFields(SPutOpTest.CLASS_NAME, "myBoolean:Z", true);
        VMTester.test(SPutOpTest.CLASS_NAME, "putStaticBoolean()V", initial, expected);
    }

    @Test
    public void putStaticByte() {
        initial.setRegisters(0, ((byte) (255)), "B");
        expected.setFields(SPutOpTest.CLASS_NAME, "myByte:B", ((byte) (255)));
        VMTester.test(SPutOpTest.CLASS_NAME, "putStaticByte()V", initial, expected);
    }

    @Test
    public void putStaticChar() {
        initial.setRegisters(0, '!', "C");
        expected.setFields(SPutOpTest.CLASS_NAME, "myChar:C", '!');
        VMTester.test(SPutOpTest.CLASS_NAME, "putStaticChar()V", initial, expected);
    }

    @Test
    public void putStaticInt() {
        initial.setRegisters(0, 66, "I");
        expected.setFields(SPutOpTest.CLASS_NAME, "myInt:I", 66);
        VMTester.test(SPutOpTest.CLASS_NAME, "putStaticInt()V", initial, expected);
    }

    @Test
    public void putStaticObject() {
        initial.setRegisters(0, "Do not pray for an easy life", "Ljava/lang/String;");
        expected.setFields(SPutOpTest.CLASS_NAME, "myString:Ljava/lang/String;", "Do not pray for an easy life");
        VMTester.test(SPutOpTest.CLASS_NAME, "putStaticObject()V", initial, expected);
    }

    @Test
    public void putStaticShort() {
        initial.setRegisters(0, ((short) (16962)), "S");
        expected.setFields(SPutOpTest.CLASS_NAME, "myShort:S", ((short) (16962)));
        VMTester.test(SPutOpTest.CLASS_NAME, "putStaticShort()V", initial, expected);
    }

    @Test
    public void putStaticWide() {
        initial.setRegisters(0, 68719476735L, "J");
        expected.setFields(SPutOpTest.CLASS_NAME, "myLong:J", 68719476735L);
        VMTester.test(SPutOpTest.CLASS_NAME, "putStaticWide()V", initial, expected);
    }
}

