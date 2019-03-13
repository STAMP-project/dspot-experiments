package org.web3j.protocol.geth;


import org.junit.Test;
import org.web3j.protocol.RequestTester;


public class RequestTest extends RequestTester {
    private Geth web3j;

    @Test
    public void testPersonalImportRawKey() throws Exception {
        // CHECKSTYLE:OFF
        String rawKey = "a08165236279178312660610114131826512483935470542850824183737259708197206310322";
        String password = "hunter2";
        web3j.personalImportRawKey(rawKey, password).send();
        verifyResult(("{\"jsonrpc\":\"2.0\",\"method\":\"personal_importRawKey\"," + "\"params\":[\"a08165236279178312660610114131826512483935470542850824183737259708197206310322\",\"hunter2\"],\"id\":1}"));
        // CHECKSTYLE:ON
    }

    @Test
    public void testPersonalLockAccount() throws Exception {
        String accountId = "0x407d73d8a49eeb85d32cf465507dd71d507100c1";
        web3j.personalLockAccount(accountId).send();
        verifyResult(("{\"jsonrpc\":\"2.0\",\"method\":\"personal_lockAccount\"," + "\"params\":[\"0x407d73d8a49eeb85d32cf465507dd71d507100c1\"],\"id\":1}"));
    }

    @Test
    public void testPersonalSign() throws Exception {
        // CHECKSTYLE:OFF
        web3j.personalSign("0xdeadbeaf", "0x9b2055d370f73ec7d8a03e965129118dc8f5bf83", "hunter2").send();
        verifyResult(("{\"jsonrpc\":\"2.0\",\"method\":\"personal_sign\"," + "\"params\":[\"0xdeadbeaf\",\"0x9b2055d370f73ec7d8a03e965129118dc8f5bf83\",\"hunter2\"],\"id\":1}"));
        // CHECKSTYLE:ON
    }

    @Test
    public void testPersonalEcRecover() throws Exception {
        // CHECKSTYLE:OFF
        web3j.personalEcRecover("0xdeadbeaf", "0xa3f20717a250c2b0b729b7e5becbff67fdaef7e0699da4de7ca5895b02a170a12d887fd3b17bfdce3481f10bea41f45ba9f709d39ce8325427b57afcfc994cee1b").send();
        verifyResult("{\"jsonrpc\":\"2.0\",\"method\":\"personal_ecRecover\",\"params\":[\"0xdeadbeaf\",\"0xa3f20717a250c2b0b729b7e5becbff67fdaef7e0699da4de7ca5895b02a170a12d887fd3b17bfdce3481f10bea41f45ba9f709d39ce8325427b57afcfc994cee1b\"],\"id\":1}");
        // CHECKSTYLE:ON
    }

    @Test
    public void testMinerStart() throws Exception {
        web3j.minerStart(4).send();
        verifyResult(("{\"jsonrpc\":\"2.0\",\"method\":\"miner_start\"," + "\"params\":[4],\"id\":1}"));
    }

    @Test
    public void testMinerStop() throws Exception {
        web3j.minerStop().send();
        verifyResult(("{\"jsonrpc\":\"2.0\",\"method\":\"miner_stop\"," + "\"params\":[],\"id\":1}"));
    }
}

