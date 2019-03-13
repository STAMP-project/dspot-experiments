package org.web3j.console;


import SampleKeys.PRIVATE_KEY_STRING;
import org.junit.Test;


public class KeyImporterTest extends WalletTester {
    @Test
    public void testSpecifyPrivateKey() {
        prepareWalletCreation(PRIVATE_KEY_STRING);
    }

    @Test
    public void testLoadPrivateKeyFromFile() {
        prepareWalletCreation(KeyImporterTest.class.getResource(("/keyfiles/" + "sample-private-key.txt")).getFile());
    }
}

