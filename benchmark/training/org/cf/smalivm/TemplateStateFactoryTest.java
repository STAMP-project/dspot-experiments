package org.cf.smalivm;


import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UninitializedInstance;
import org.cf.smalivm.type.UnknownValue;
import org.cf.smalivm.type.VirtualMethod;
import org.junit.Assert;
import org.junit.Test;


public class TemplateStateFactoryTest {
    private static final String CLASS_NAME = "Lchild_class;";

    private VirtualMachine vm;

    @Test
    public void methodStateForVirtualMethodCreatedCorrectly() throws VirtualMachineException {
        String methodDescriptor = "someString()Ljava/lang/String;";
        VirtualMethod method = vm.getClassManager().getMethod(TemplateStateFactoryTest.CLASS_NAME, methodDescriptor);
        ExecutionContext spawnedContext = new ExecutionContext(vm, method);
        ClassState templateClassState = TemplateStateFactory.forClass(spawnedContext, method.getDefiningClass());
        spawnedContext.setClassState(templateClassState);
        MethodState templateMethodState = TemplateStateFactory.forMethod(spawnedContext);
        spawnedContext.setMethodState(templateMethodState);
        Assert.assertEquals(2, templateMethodState.getRegisterCount());
        Assert.assertEquals(1, templateMethodState.getParameterCount());
        int instanceRegister = templateMethodState.getParameterStart();
        Assert.assertEquals(1, instanceRegister);
        HeapItem instanceItem = templateMethodState.peekRegister(instanceRegister);
        Assert.assertEquals(TemplateStateFactoryTest.CLASS_NAME, instanceItem.getType());
        Assert.assertEquals(UnknownValue.class, instanceItem.getValue().getClass());
    }

    @Test
    public void methodStateForObjectInitializationMethodCreatedCorrectly() throws VirtualMachineException {
        String methodDescriptor = "<init>()V";
        VirtualMethod method = vm.getClassManager().getMethod(TemplateStateFactoryTest.CLASS_NAME, methodDescriptor);
        ExecutionContext spawnedContext = new ExecutionContext(vm, method);
        ClassState templateClassState = TemplateStateFactory.forClass(spawnedContext, method.getDefiningClass());
        spawnedContext.setClassState(templateClassState);
        MethodState templateMethodState = TemplateStateFactory.forMethod(spawnedContext);
        spawnedContext.setMethodState(templateMethodState);
        Assert.assertEquals(1, templateMethodState.getRegisterCount());
        Assert.assertEquals(1, templateMethodState.getParameterCount());
        int instanceRegister = templateMethodState.getParameterStart();
        Assert.assertEquals(0, instanceRegister);
        HeapItem instanceItem = templateMethodState.peekRegister(instanceRegister);
        Assert.assertEquals(TemplateStateFactoryTest.CLASS_NAME, instanceItem.getType());
        Assert.assertEquals(UninitializedInstance.class, instanceItem.getValue().getClass());
    }
}

