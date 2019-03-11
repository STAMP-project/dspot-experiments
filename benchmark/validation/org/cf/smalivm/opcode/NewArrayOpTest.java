package org.cf.smalivm.opcode;


import java.lang.reflect.Array;
import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;


@RunWith(Enclosed.class)
public class NewArrayOpTest {
    private static final String CLASS_NAME = "Lnew_array_test;";

    private VMState expected;

    private VMState initial;

    @Test
    public void canCreate2DIntegerArray() {
        int length = 3;
        initial.setRegisters(0, length, "I");
        expected.setRegisters(0, new int[length][], "[[I");
        VMTester.test(NewArrayOpTest.CLASS_NAME, "create2DIntegerArray()V", initial, expected);
    }

    @Test
    public void canCreate2DLocalInstanceArray() throws ClassNotFoundException {
        int length = 5;
        initial.setRegisters(0, length, "I");
        ExecutionGraph graph = VMTester.execute(NewArrayOpTest.CLASS_NAME, "create2DLocalInstanceArray()V", initial);
        HeapItem consensus = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(("[[" + (NewArrayOpTest.CLASS_NAME)), consensus.getType());
        Assert.assertEquals(length, Array.getLength(consensus.getValue()));
        Class<?> actualClass = consensus.getValue().getClass();
        Assert.assertEquals(("[[" + (NewArrayOpTest.CLASS_NAME)), actualClass.getName());
    }

    @Test
    public void canCreateIntegerArray() {
        int length = 1;
        initial.setRegisters(0, length, "I");
        expected.setRegisters(0, new int[length], "[I");
        VMTester.test(NewArrayOpTest.CLASS_NAME, "createIntegerArray()V", initial, expected);
    }

    @Test
    public void canCreateIntegerArrayWithShortTypeLengthValue() {
        Short length = 1;
        initial.setRegisters(0, length, "S");
        expected.setRegisters(0, new int[length.intValue()], "[I");
        VMTester.test(NewArrayOpTest.CLASS_NAME, "createIntegerArray()V", initial, expected);
    }

    @Test
    public void canCreateIntegerArrayWithUnkonwnLengthValue() {
        Object length = new UnknownValue();
        initial.setRegisters(0, length, "I");
        expected.setRegisters(0, length, "[I");
        VMTester.test(NewArrayOpTest.CLASS_NAME, "createIntegerArray()V", initial, expected);
    }

    @Test
    public void canCreateLocalInstanceArray() {
        int length = 1;
        initial.setRegisters(0, length, "I");
        ExecutionGraph graph = VMTester.execute(NewArrayOpTest.CLASS_NAME, "createLocalInstanceArray()V", initial);
        HeapItem consensus = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(("[" + (NewArrayOpTest.CLASS_NAME)), consensus.getType());
        Assert.assertEquals(length, Array.getLength(consensus.getValue()));
        Class<?> actualClass = consensus.getValue().getClass();
        Assert.assertEquals(("[" + (NewArrayOpTest.CLASS_NAME)), actualClass.getName());
    }

    @Test
    public void nonExistentClassNameThrowsException() {
        initial.setRegisters(0, 1, "I");
        NewArrayOpTest.testException("createNonExistentArrayClass()V", ClassNotFoundException.class, "does.not.exist", initial);
    }
}

