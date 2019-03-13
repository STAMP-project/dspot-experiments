package org.cf.smalivm;


import SideEffect.Level.NONE;
import SideEffect.Level.STRONG;
import SideEffect.Level.WEAK;
import org.cf.smalivm.context.ExecutionGraph;
import org.junit.Assert;
import org.junit.Test;


public class TestSideEffects {
    /* A side-effect is any modification of state that persists outside the method, e.g. changing class static or
    instance variables, file and network IO, etc. To determine with 100% accuracy is tricky, and a lot of work, so we
    take the shortcut of white listing certain classes and methods as not causing side effects. Knowing that a method
    has no side effects lets the optimizer remove the invocation if the result is not used.
     */
    private static final String CLASS_NAME = "Lside_effects_test;";

    private VirtualMachine vm;

    @Test
    public void constOpsHaveNoSideEffects() throws VirtualMachineException {
        String methodName = "constOps()V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void emptyMethodHasNoSideEffects() throws VirtualMachineException {
        String methodName = "emptyMethod()V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void invokeMethodWithNoSideEffectsHasNoSideEffects() throws VirtualMachineException {
        String methodName = "invokeMethodWithNoSideEffects()V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void invokeOfNonAnalyzableMethodHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "invokeOfNonAnalyzableMethod()V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void invokeSideEffectMethodHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "invokeSideEffectMethod(Ljava/io/OutputStream;[B)V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void invokeWhitelistedMethodsHasNoSideEffects() throws VirtualMachineException {
        String methodName = "invokeWhitelistedMethods()V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void modifyInstanceMemberHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "modifyInstanceMember()V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(WEAK, graph.getHighestSideEffectLevel());
    }

    @Test
    public void newInstanceNonLocalWhitelistedClassHasNoSideEffects() throws VirtualMachineException {
        String methodName = "newInstanceNonLocalWhitelistedClass()V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void newInstanceOfClassWithStaticInitializerWithStrongSideEffectsHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "newInstanceOfClassWithStaticInitializerWithStrongSideEffects()V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void newInstanceOfClassWithStaticInitializerWithWeakSideEffectsHasWeakSideEffects() throws VirtualMachineException {
        String methodName = "newInstanceOfClassWithStaticInitializerWithWeakSideEffects()V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(WEAK, graph.getHighestSideEffectLevel());
    }

    @Test
    public void newInstanceOfMethodWithNoStaticInitializerHasNoSideEffects() throws VirtualMachineException {
        String methodName = "newInstanceOfMethodWithNoStaticInitializer()V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void newInstanceOfMethodWithStaticInitializerWithNoSideEffectsHasNoSideEffects() throws VirtualMachineException {
        String methodName = "newInstanceOfMethodWithStaticInitializerWithNoSideEffects()V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void writeOutputStreamHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "writeOutputStream(Ljava/io/OutputStream;[B)V";
        ExecutionGraph graph = vm.execute(TestSideEffects.CLASS_NAME, methodName);
        Assert.assertEquals(STRONG, graph.getHighestSideEffectLevel());
    }
}

