package org.cf.smalivm.context;


import org.cf.smalivm.VMTester;
import org.junit.Assert;
import org.junit.Test;


public class MethodStateTest {
    private static final String CLASS_NAME = "Lsimple_loop;";

    @Test
    public void hasExpectedToString() {
        ExecutionGraph graph = VMTester.execute(MethodStateTest.CLASS_NAME, "simpleLoop()V");
        ExecutionNode node = graph.getRoot();
        MethodState state = node.getContext().getMethodState();
        String expected = "params: 0, locals: 2\nv0: type=I, value=0";
        Assert.assertEquals(expected, state.toString());
    }
}

