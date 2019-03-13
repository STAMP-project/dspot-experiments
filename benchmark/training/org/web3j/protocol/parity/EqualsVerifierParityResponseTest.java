package org.web3j.protocol.parity;


import ParityAllAccountsInfo.AccountsInfo;
import StateDiff.AddedState;
import StateDiff.ChangedState;
import StateDiff.UnchangedState;
import Trace.CallAction;
import Trace.CreateAction;
import Trace.Result;
import Trace.SuicideAction;
import VMTrace.VMOperation;
import VMTrace.VMOperation.Ex;
import VMTrace.VMOperation.Ex.Mem;
import VMTrace.VMOperation.Ex.Store;
import Warning.NONFINAL_FIELDS;
import Warning.STRICT_INHERITANCE;
import java.math.BigInteger;
import java.util.ArrayList;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.web3j.protocol.parity.methods.response.FullTraceInfo;
import org.web3j.protocol.parity.methods.response.StateDiff;
import org.web3j.protocol.parity.methods.response.Trace;
import org.web3j.protocol.parity.methods.response.VMTrace;


public class EqualsVerifierParityResponseTest {
    @Test
    public void testAccountsInfo() {
        EqualsVerifier.forClass(AccountsInfo.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testFullTraceInfo() {
        VMTrace vmTrace1 = new VMTrace("one", new ArrayList());
        VMTrace vmTrace2 = new VMTrace("two", new ArrayList());
        EqualsVerifier.forClass(FullTraceInfo.class).withPrefabValues(VMTrace.class, vmTrace1, vmTrace2).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testStateDiff() {
        EqualsVerifier.forClass(StateDiff.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testChangedState() {
        EqualsVerifier.forClass(ChangedState.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testUnchangedState() {
        EqualsVerifier.forClass(UnchangedState.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testAddedState() {
        EqualsVerifier.forClass(AddedState.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testVMTrace() {
        VMTrace.VMOperation op1 = new VMTrace.VMOperation(null, BigInteger.ZERO, null, null);
        VMTrace.VMOperation op2 = new VMTrace.VMOperation(null, BigInteger.ONE, null, null);
        EqualsVerifier.forClass(VMTrace.class).withPrefabValues(VMOperation.class, op1, op2).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testVMTraceVMOperation() {
        VMTrace vmTrace1 = new VMTrace("one", new ArrayList());
        VMTrace vmTrace2 = new VMTrace("two", new ArrayList());
        EqualsVerifier.forClass(VMOperation.class).withPrefabValues(VMTrace.class, vmTrace1, vmTrace2).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testVMTraceVMOperationEx() {
        EqualsVerifier.forClass(Ex.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testVMTraceVMOperationExMem() {
        EqualsVerifier.forClass(Mem.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testVMTraceVMOperationExStore() {
        EqualsVerifier.forClass(Store.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testTrace() {
        EqualsVerifier.forClass(Trace.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testTraceSuicideAction() {
        EqualsVerifier.forClass(SuicideAction.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testTraceCallAction() {
        EqualsVerifier.forClass(CallAction.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testTraceCreateAction() {
        EqualsVerifier.forClass(CreateAction.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testTraceResult() {
        EqualsVerifier.forClass(Result.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }
}

