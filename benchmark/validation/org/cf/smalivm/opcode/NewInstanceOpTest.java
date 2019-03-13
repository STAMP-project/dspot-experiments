package org.cf.smalivm.opcode;


import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.type.VirtualType;
import org.junit.Test;


public class NewInstanceOpTest {
    private static final String CLASS_NAME = "Lnew_instance_test;";

    private VMState expected;

    private VMState initial;

    private VirtualMachine vm;

    @Test
    public void canCreateLocalInstance() throws ClassNotFoundException {
        initial.setRegisters(0, 1, "I");
        ExecutionGraph graph = VMTester.execute(vm, NewInstanceOpTest.CLASS_NAME, "newLocalInstance()V", initial);
        VirtualType instanceType = vm.getClassManager().getVirtualType(NewInstanceOpTest.CLASS_NAME);
        expected.setRegisters(0, new org.cf.smalivm.type.UninitializedInstance(instanceType), NewInstanceOpTest.CLASS_NAME);
        VMTester.testState(graph, expected);
    }

    @Test
    public void canCreateNonLocalInstance() {
        initial.setRegisters(0, 1, "I");
        ExecutionGraph graph = VMTester.execute(vm, NewInstanceOpTest.CLASS_NAME, "newNonLocalInstance()V", initial);
        VirtualType instanceType = vm.getClassManager().getVirtualType("Ljava/lang/Integer;");
        expected.setRegisters(0, new org.cf.smalivm.type.UninitializedInstance(instanceType), "Ljava/lang/Integer;");
        VMTester.testState(graph, expected);
    }
}

