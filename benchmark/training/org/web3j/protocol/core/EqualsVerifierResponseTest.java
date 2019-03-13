package org.web3j.protocol.core;


import AbiDefinition.NamedType;
import EthBlock.Block;
import EthBlock.TransactionHash;
import EthCompileSolidity.Code;
import EthCompileSolidity.Documentation;
import EthCompileSolidity.SolidityInfo;
import EthLog.Hash;
import EthSyncing.Syncing;
import Response.Error;
import ShhMessages.SshMessage;
import Warning.NONFINAL_FIELDS;
import Warning.STRICT_INHERITANCE;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.AbiDefinition;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;


public class EqualsVerifierResponseTest {
    @Test
    public void testBlock() {
        EqualsVerifier.forClass(Block.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testTransaction() {
        EqualsVerifier.forClass(Transaction.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testTransactionReceipt() {
        EqualsVerifier.forClass(TransactionReceipt.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testLog() {
        EqualsVerifier.forClass(Log.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testSshMessage() {
        EqualsVerifier.forClass(SshMessage.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testSolidityInfo() {
        EqualsVerifier.forClass(SolidityInfo.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testSyncing() {
        EqualsVerifier.forClass(Syncing.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testAbiDefinition() {
        EqualsVerifier.forClass(AbiDefinition.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testAbiDefinitionNamedType() {
        EqualsVerifier.forClass(NamedType.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testHash() {
        EqualsVerifier.forClass(Hash.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testCode() {
        EqualsVerifier.forClass(Code.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testTransactionHash() {
        EqualsVerifier.forClass(TransactionHash.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testCompiledSolidityCode() {
        EqualsVerifier.forClass(Code.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testDocumentation() {
        EqualsVerifier.forClass(Documentation.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }

    @Test
    public void testError() {
        EqualsVerifier.forClass(Error.class).suppress(NONFINAL_FIELDS).suppress(STRICT_INHERITANCE).verify();
    }
}

