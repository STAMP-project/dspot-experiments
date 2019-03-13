package org.cf.smalivm;


import java.util.ArrayList;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualField;
import org.junit.Assert;
import org.junit.Test;


public class TestStaticFieldAccess {
    private static final String CLASS_NAME = "Lstatic_fields_test;";

    @Test
    public void canGetFieldsWhileInitializing() {
        VirtualMachine vm = VMTester.spawnVM();
        ExecutionGraph graph = VMTester.execute(vm, TestStaticFieldAccess.CLASS_NAME, "stubMethod()V");
        VirtualClass virtualClass = vm.getClassManager().getVirtualClass(TestStaticFieldAccess.CLASS_NAME);
        VirtualField alwaysZeroField = virtualClass.getField("alwaysZero");
        HeapItem alwaysZeroItem = graph.getTerminatingFieldConsensus(alwaysZeroField);
        Assert.assertEquals(0, alwaysZeroItem.getValue());
        VirtualField testInt1Field = virtualClass.getField("testInt1");
        HeapItem testInt1Item = graph.getTerminatingFieldConsensus(testInt1Field);
        Assert.assertEquals(0, testInt1Item.getValue());
        VirtualField testInt2Field = virtualClass.getField("testInt2");
        HeapItem testInt2Item = graph.getTerminatingFieldConsensus(testInt2Field);
        Assert.assertEquals(1, testInt2Item.getValue());
    }

    @Test
    public void canAccessFieldAfterCollapsingMultiverse() {
        VirtualMachine vm = VMTester.spawnVM();
        ExecutionGraph graph = VMTester.execute(vm, TestStaticFieldAccess.CLASS_NAME, "callMethodThenGetField()V");
        VirtualClass virtualClass = vm.getClassManager().getVirtualClass(TestStaticFieldAccess.CLASS_NAME);
        VirtualField listField = virtualClass.getField("myList");
        HeapItem item = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(ArrayList.class, item.getValue().getClass());
        ArrayList list = ((ArrayList) (item.getValue()));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("added item!", list.get(0));
        HeapItem fieldItem = graph.getTerminatingFieldConsensus(listField);
        Assert.assertEquals(item, fieldItem);
    }

    @Test
    public void canAccessFieldAfterCollapsingMultiverseTwice() {
        VirtualMachine vm = VMTester.spawnVM();
        ExecutionGraph graph = VMTester.execute(vm, TestStaticFieldAccess.CLASS_NAME, "callMethodWhichCallsAnotherMethodThenGetsField()V");
        VirtualClass virtualClass = vm.getClassManager().getVirtualClass(TestStaticFieldAccess.CLASS_NAME);
        VirtualField listField = virtualClass.getField("myList");
        HeapItem item = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(ArrayList.class, item.getValue().getClass());
        ArrayList list = ((ArrayList) (item.getValue()));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("added item!", list.get(0));
        HeapItem fieldItem = graph.getTerminatingFieldConsensus(listField);
        Assert.assertEquals(item, fieldItem);
    }
}

