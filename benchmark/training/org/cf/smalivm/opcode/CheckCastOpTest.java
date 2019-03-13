package org.cf.smalivm.opcode;


import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.junit.Test;


public class CheckCastOpTest {
    private static final String CLASS_NAME = "Lcheck_cast_test;";

    private VMState expected;

    private VMState initial;

    @Test
    public void canCastNullWithObjectTypeToString() {
        initial.setRegisters(0, null, "Ljava/lang/Object;");
        expected.setRegisters(0, null, "Ljava/lang/String;");
        VMTester.test(CheckCastOpTest.CLASS_NAME, "castToString()V", initial, expected);
    }

    @Test
    public void canCastStringWithObjectTypeToString() {
        initial.setRegisters(0, "great maker", "Ljava/lang/Object;");
        expected.setRegisters(0, "great maker", "Ljava/lang/String;");
        VMTester.test(CheckCastOpTest.CLASS_NAME, "castToString()V", initial, expected);
    }

    @Test
    public void canCastStringWithStringTypeToObject() {
        initial.setRegisters(0, "great maker", "Ljava/lang/String;");
        expected.setRegisters(0, "great maker", "Ljava/lang/Object;");
        VMTester.test(CheckCastOpTest.CLASS_NAME, "castToObject()V", initial, expected);
    }

    @Test
    public void objectNotOfCastTypeThrowsClassCastException() {
        initial.setRegisters(0, 66, "Ljava/lang/Integer;");
        CheckCastOpTest.testException("castToStringWithCatch()V", ClassCastException.class, "java.lang.Integer cannot be cast to java.lang.String", initial);
    }
}

