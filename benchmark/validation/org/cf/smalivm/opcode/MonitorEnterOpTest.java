package org.cf.smalivm.opcode;


import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.junit.Assert;
import org.junit.Test;


public class MonitorEnterOpTest {
    private static final String CLASS_NAME = "Lmonitor_enter_test;";

    @Test
    public void monitorEnterHasExpectedTostring() {
        String methodDescriptor = "monitorEnter()V";
        VirtualMachine vm = VMTester.spawnVM();
        ExecutionGraph graph = vm.spawnInstructionGraph(MonitorEnterOpTest.CLASS_NAME, methodDescriptor);
        ExecutionNode node = graph.getTemplateNode(1);
        Assert.assertEquals("monitor-enter r0", node.getOp().toString());
    }
}

