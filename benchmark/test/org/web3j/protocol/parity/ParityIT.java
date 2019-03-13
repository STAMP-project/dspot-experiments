package org.web3j.protocol.parity;


import org.junit.Assert;
import org.junit.Test;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.admin.methods.response.PersonalSign;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;


/**
 * JSON-RPC 2.0 Integration Tests.
 */
public class ParityIT {
    private static String PASSWORD = "1n5ecur3P@55w0rd";

    private Parity parity;

    @Test
    public void testPersonalListAccounts() throws Exception {
        PersonalListAccounts personalListAccounts = parity.personalListAccounts().send();
        Assert.assertNotNull(personalListAccounts.getAccountIds());
    }

    @Test
    public void testPersonalNewAccount() throws Exception {
        NewAccountIdentifier newAccountIdentifier = createAccount();
        Assert.assertFalse(newAccountIdentifier.getAccountId().isEmpty());
    }

    @Test
    public void testPersonalUnlockAccount() throws Exception {
        NewAccountIdentifier newAccountIdentifier = createAccount();
        PersonalUnlockAccount personalUnlockAccount = parity.personalUnlockAccount(newAccountIdentifier.getAccountId(), ParityIT.PASSWORD).send();
        Assert.assertTrue(personalUnlockAccount.accountUnlocked());
    }

    @Test
    public void testPersonalSign() throws Exception {
        PersonalListAccounts personalListAccounts = parity.personalListAccounts().send();
        Assert.assertNotNull(personalListAccounts.getAccountIds());
        PersonalSign personalSign = parity.paritySignMessage("0xdeadbeaf", personalListAccounts.getAccountIds().get(0), "123").send();
        // address : 0xadfc0262bbed8c1f4bd24a4a763ac616803a8c54
        Assert.assertNotNull(personalSign.getSignedMessage());
        // result : 0x80ab45a65bd5acce92eac60b52235a34eee647c8dbef8e62108be90a4ac9a22222f87dd8934f
        // c71545cf2ea1b71d8b62146e6d741ac6ee12fd1d1d740adca9021b
    }
}

