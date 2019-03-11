package org.web3j.console;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class WalletUpdaterTest extends WalletTester {
    @Test
    public void testWalletUpdate() {
        Mockito.when(console.readPassword(ArgumentMatchers.startsWith("Please enter your existing wallet file password"))).thenReturn(WalletTester.WALLET_PASSWORD);
        Mockito.when(console.readPassword(ArgumentMatchers.contains("password"))).thenReturn(WalletTester.WALLET_PASSWORD, WalletTester.WALLET_PASSWORD);
        Mockito.when(console.readLine(ArgumentMatchers.startsWith("Please enter a destination directory "))).thenReturn(tempDirPath);
        Mockito.when(console.readLine(ArgumentMatchers.startsWith("Would you like to delete"))).thenReturn("N");
        WalletUpdater.main(console, KeyImporterTest.class.getResource(("/keyfiles/" + "UTC--2016-11-03T05-55-06.340672473Z--ef678007d18427e6022059dbc264f27507cd1ffc")).getFile());
        Mockito.verify(console).printf(ArgumentMatchers.contains("successfully created in"));
    }
}

