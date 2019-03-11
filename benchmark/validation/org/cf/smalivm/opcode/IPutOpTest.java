package org.cf.smalivm.opcode;


import Opcode.IPUT;
import gnu.trove.map.TIntObjectMap;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class IPutOpTest {
    private static final int ADDRESS = 0;

    private static final int REGISTER_A = 0;

    private static final int REGISTER_B = 2;

    private TIntObjectMap<MethodLocation> addressToLocation;

    private ExecutionContext ectx;

    private BuilderInstruction instruction;

    private HeapItem itemA;

    private HeapItem itemB;

    private MethodLocation location;

    private MethodState mState;

    private ExecutionNode node;

    private IPutOp op;

    private IPutOpFactory opFactory;

    private ArgumentCaptor<HeapItem> setItem;

    private VirtualMachine vm;

    @Test
    public void testIGetReturnsUnknownValueOfCorrectType() {
        Mockito.when(instruction.getOpcode()).thenReturn(IPUT);
        op = ((IPutOp) (opFactory.create(location, addressToLocation, vm)));
        op.execute(node, ectx);
        Mockito.verify(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(IPutOpTest.REGISTER_A));
        Mockito.verify(mState, Mockito.times(1)).readRegister(ArgumentMatchers.eq(IPutOpTest.REGISTER_B));
        Mockito.verify(mState, Mockito.times(1)).assignRegister(ArgumentMatchers.eq(IPutOpTest.REGISTER_B), ArgumentMatchers.any(HeapItem.class));
        Assert.assertEquals((((("iput r" + (IPutOpTest.REGISTER_A)) + ", r") + (IPutOpTest.REGISTER_B)) + ", Lsome/class;->someMethod:I"), op.toString());
    }
}

