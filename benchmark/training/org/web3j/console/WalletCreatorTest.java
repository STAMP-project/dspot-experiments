package org.web3j.console;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class WalletCreatorTest extends WalletTester {
    @Test
    public void testWalletCreation() {
        Mockito.when(console.readPassword(ArgumentMatchers.contains("password"))).thenReturn(WalletTester.WALLET_PASSWORD, WalletTester.WALLET_PASSWORD);
        Mockito.when(console.readLine(ArgumentMatchers.startsWith("Please enter a destination directory "))).thenReturn(tempDirPath);
        WalletCreator.main(console);
        Mockito.verify(console).printf(ArgumentMatchers.contains("successfully created in"));
    }
}

