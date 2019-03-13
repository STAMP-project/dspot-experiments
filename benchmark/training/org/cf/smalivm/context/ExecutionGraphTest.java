package org.cf.smalivm.context;


import CommonTypes.OBJECT;
import CommonTypes.STRING;
import CommonTypes.UNKNOWN;
import MethodState.ReturnRegister;
import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.type.UnknownValue;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualMethod;
import org.junit.Assert;
import org.junit.Test;


public class ExecutionGraphTest {
    private static final String CLASS_NAME = "Lexecution_graph;";

    private VirtualMachine vm;

    private VirtualClass virtualClass;

    private VMState initial;

    @Test
    public void hasExpectedTerminatingAddresses() {
        String methodDescriptor = "terminatingAddresses()V";
        VirtualMethod method = virtualClass.getMethod(methodDescriptor);
        ExecutionGraph graph = vm.spawnInstructionGraph(method);
        int[] expected = new int[]{ 6, 7, 8, 9, 10, 12 };
        int[] actual = graph.getTerminatingAddresses();
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void getsExpectedConsensusTypeForTypesInSameHierarchy() {
        String methodDescriptor = "returnsObjectOrString()Ljava/lang/Object;";
        initial.setRegister(0, new UnknownValue(), "I");
        ExecutionGraph graph = VMTester.execute(vm, ExecutionGraphTest.CLASS_NAME, methodDescriptor, initial);
        HeapItem item = graph.getTerminatingRegisterConsensus(ReturnRegister);
        Assert.assertEquals(OBJECT, item.getType());
    }

    @Test
    public void getsExpectedConsensusTypeForTypesInSameHierarchyAndNull() {
        String methodDescriptor = "returnsObjectOrStringOrNull()Ljava/lang/Object;";
        initial.setRegister(0, new UnknownValue(), "I");
        ExecutionGraph graph = VMTester.execute(vm, ExecutionGraphTest.CLASS_NAME, methodDescriptor, initial);
        HeapItem item = graph.getTerminatingRegisterConsensus(ReturnRegister);
        Assert.assertEquals(OBJECT, item.getType());
    }

    @Test
    public void getsReturnTypeForAmbiguousReturnTypes() {
        String methodDescriptor = "returnsStringOrThrowsException()Ljava/lang/String;";
        initial.setRegister(0, new UnknownValue(), "I");
        ExecutionGraph graph = VMTester.execute(vm, ExecutionGraphTest.CLASS_NAME, methodDescriptor, initial);
        HeapItem item = graph.getTerminatingRegisterConsensus(ReturnRegister);
        Assert.assertEquals(STRING, item.getType());
    }

    @Test
    public void getsMostRecentCommonAncestorForTypesNotInSameHierarchy() {
        String methodDescriptor = "storesStringOrInteger()V";
        initial.setRegister(0, new UnknownValue(), "I");
        ExecutionGraph graph = VMTester.execute(vm, ExecutionGraphTest.CLASS_NAME, methodDescriptor, initial);
        HeapItem item = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(OBJECT, item.getType());
    }

    @Test
    public void getsUnknownConsensusTypeForAmbiguousTypes() {
        String methodDescriptor = "storesStringOrInt()V";
        initial.setRegister(0, new UnknownValue(), "I");
        ExecutionGraph graph = VMTester.execute(vm, ExecutionGraphTest.CLASS_NAME, methodDescriptor, initial);
        HeapItem item = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(UNKNOWN, item.getType());
    }

    @Test
    public void getsMostCommonAncestorTypeForArrayTypes() {
        String methodDescriptor = "returnsStringArrayOr2DIntArray()Ljava/lang/Object;";
        initial.setRegister(0, new UnknownValue(), "I");
        ExecutionGraph graph = VMTester.execute(vm, ExecutionGraphTest.CLASS_NAME, methodDescriptor, initial);
        HeapItem item = graph.getTerminatingRegisterConsensus(ReturnRegister);
        // Could be an Object, but more accurate to say Object[]
        Assert.assertEquals(("[" + (CommonTypes.OBJECT)), item.getType());
    }
}

