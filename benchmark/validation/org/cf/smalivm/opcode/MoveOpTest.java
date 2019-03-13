package org.cf.smalivm.opcode;


import MethodState.ExceptionRegister;
import MethodState.ResultRegister;
import java.util.Arrays;
import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.junit.Assert;
import org.junit.Test;


public class MoveOpTest {
    private static final String CLASS_NAME = "Lmove_test;";

    private VMState expected;

    private VMState initial;

    @Test
    public void canMoveException() {
        String exception = "any object would do";
        initial.setRegisters(ExceptionRegister, exception, "Ljava/lang/Exception;");
        expected.setRegisters(0, exception, "Ljava/lang/Exception;");
        VMTester.test(MoveOpTest.CLASS_NAME, "moveException()V", initial, expected);
    }

    @Test
    public void canMoveRegisterObject() {
        initial.setRegisters(0, new Object(), "Ljava/lang/Object;");
        // Must invoke VM directly to ensure parse identity
        ExecutionGraph graph = VMTester.execute(MoveOpTest.CLASS_NAME, "moveRegisterObject()V", initial);
        int[] addresses = graph.getConnectedTerminatingAddresses();
        Assert.assertTrue((("Should terminate when expected: " + (Arrays.toString(addresses))) + " == {1}"), Arrays.equals(addresses, new int[]{ 1 }));
        HeapItem register0 = graph.getRegisterConsensus(1, 0);
        HeapItem register1 = graph.getRegisterConsensus(1, 1);
        Assert.assertSame(register0, register1);
        Assert.assertTrue((register0 instanceof Object));
    }

    @Test
    public void canMoveRegisterPrimitive() {
        initial.setRegisters(0, 42, "I");
        expected.setRegisters(0, 42, "I", 1, 42, "I");
        VMTester.test(MoveOpTest.CLASS_NAME, "moveRegisterPrimitive()V", initial, expected);
    }

    @Test
    public void canMoveResult() {
        initial.setRegisters(ResultRegister, 42, "I");
        expected.setRegisters(0, 42, "I");
        VMTester.test(MoveOpTest.CLASS_NAME, "moveResult()V", initial, expected);
    }
}

