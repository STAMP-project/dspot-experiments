package org.cf.smalivm;


import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualField;
import org.cf.smalivm.type.VirtualMethod;
import org.junit.Assert;
import org.junit.Test;


public class TestFieldInheritance {
    private VirtualMachine vm;

    private VirtualClass childClass;

    private VirtualClass parentClass;

    private VirtualClass grandparentClass;

    @Test
    public void parentInitializedAndAccessible() throws VirtualMachineException {
        VirtualMethod method = childClass.getMethod("stubMethod()V");
        ExecutionGraph graph = vm.execute(method);
        String fieldName = "parentField";
        int expectedValue = 3;
        HeapItem fieldItem;
        VirtualField field;
        field = childClass.getField(fieldName);
        fieldItem = graph.getTerminatingFieldConsensus(field);
        Assert.assertEquals(expectedValue, fieldItem.getValue());
        field = parentClass.getField(fieldName);
        fieldItem = graph.getTerminatingFieldConsensus(field);
        Assert.assertEquals(expectedValue, fieldItem.getValue());
    }

    @Test
    public void grandparentInitializedAndAccessible() throws VirtualMachineException {
        VirtualMethod method = childClass.getMethod("stubMethod()V");
        ExecutionGraph graph = vm.execute(method);
        String fieldName = "grandparentField";
        int expectedValue = 4;
        HeapItem fieldItem;
        VirtualField field;
        field = childClass.getField(fieldName);
        fieldItem = graph.getTerminatingFieldConsensus(field);
        Assert.assertEquals(expectedValue, fieldItem.getValue());
        field = parentClass.getField(fieldName);
        fieldItem = graph.getTerminatingFieldConsensus(field);
        Assert.assertEquals(expectedValue, fieldItem.getValue());
        field = grandparentClass.getField(fieldName);
        fieldItem = graph.getTerminatingFieldConsensus(field);
        Assert.assertEquals(expectedValue, fieldItem.getValue());
    }

    @Test
    public void grandparentInitializedIncludingLiteralFieldsAndAccessible() throws VirtualMachineException {
        VirtualMethod method = childClass.getMethod("stubMethod()V");
        ExecutionGraph graph = vm.execute(method);
        String fieldName = "intLiteral";
        int expectedValue = 5;
        HeapItem fieldItem;
        VirtualField field;
        field = childClass.getField(fieldName);
        fieldItem = graph.getTerminatingFieldConsensus(field);
        Assert.assertEquals(expectedValue, fieldItem.getValue());
        field = parentClass.getField(fieldName);
        fieldItem = graph.getTerminatingFieldConsensus(field);
        Assert.assertEquals(expectedValue, fieldItem.getValue());
        field = grandparentClass.getField(fieldName);
        fieldItem = graph.getTerminatingFieldConsensus(field);
        Assert.assertEquals(expectedValue, fieldItem.getValue());
    }
}

