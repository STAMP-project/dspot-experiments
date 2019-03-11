package org.cf.smalivm;


import CommonTypes.UNKNOWN;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.Instance;
import org.cf.smalivm.type.UninitializedInstance;
import org.junit.Assert;
import org.junit.Test;


public class TestExceptionHandling {
    private static final String CLASS_NAME = "Lorg/cf/test/ExceptionalCode;";

    private static final String EXCEPTION_CLASS_NAME = "Lorg/cf/test/CustomException;";

    private VirtualMachine vm;

    @Test
    public void unsafeExceptionIsNotInstantiated() throws VirtualMachineException {
        String methodName = "createAndThrowException()V";
        ExecutionGraph graph = vm.execute(TestExceptionHandling.CLASS_NAME, methodName);
        HeapItem item = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(TestExceptionHandling.EXCEPTION_CLASS_NAME, item.getType());
        Assert.assertEquals(UninitializedInstance.class, item.getValue().getClass());
        Instance instance = ((Instance) (item.getValue()));
        Assert.assertEquals(TestExceptionHandling.EXCEPTION_CLASS_NAME, instance.getType().getName());
    }

    @Test
    public void unsafeExceptionIsNotThrown() throws VirtualMachineException {
        String methodName = "callsExceptionalMethod()V";
        ExecutionGraph graph = vm.execute(TestExceptionHandling.CLASS_NAME, methodName);
        HeapItem item = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(UNKNOWN, item.getType());
        Assert.assertTrue(item.isUnknown());
        // assertEquals(EXCEPTION_CLASS_NAME, item.getType());
        // assertEquals(UninitializedInstance.class, item.getValue().getClass());
        // Instance instance = (Instance) item.getValue();
        // assertEquals(EXCEPTION_CLASS_NAME, instance.getType().getName());
        // System.out.println(Arrays.toString(graph.getAddresses()));
        // for ( int address : graph.getAddresses() ) {
        // System.out.println("visited " + address + " ? " + graph.wasAddressReached(address));
        // }
    }
}

