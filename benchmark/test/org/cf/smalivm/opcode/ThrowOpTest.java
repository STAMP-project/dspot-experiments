package org.cf.smalivm.opcode;


import MethodState.ThrowRegister;
import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.util.ClassNameUtils;
import org.junit.Assert;
import org.junit.Test;


public class ThrowOpTest {
    private static final String CLASS_NAME = "Lthrow_test;";

    private VMState expected;

    private VMState initial;

    @Test
    public void canThrowNullPointerException() {
        ExecutionGraph graph = VMTester.execute(ThrowOpTest.CLASS_NAME, "throwNullPointerException()V", initial);
        Class<?> exceptionClass = NullPointerException.class;
        HeapItem item = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(exceptionClass, item.getValue().getClass());
        Assert.assertEquals(ClassNameUtils.toInternal(exceptionClass), item.getType());
        HeapItem throwItem = graph.getTerminatingRegisterConsensus(ThrowRegister);
        Assert.assertEquals(item, throwItem);
        VMTester.test(ThrowOpTest.CLASS_NAME, "throwNullPointerException()V", expected);
    }
}

