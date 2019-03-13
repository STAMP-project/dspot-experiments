package org.sufficientlysecure.keychain.securitytoken;


import junit.framework.Assert;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;

import static org.junit.Assert.assertArrayEquals;


@SuppressWarnings("WeakerAccess")
@RunWith(KeychainTestRunner.class)
public class CommandApduTest {
    static final byte[] DATA_LONG = new byte[500];

    static final byte[] DATA_SHORT = new byte[]{ 1, 2, 3 };

    static final int CLA = 1;

    static final int INS = 2;

    static final int P1 = 3;

    static final int P2 = 4;

    static final int NE_SHORT = 5;

    static final int NE_LONG = 500;

    static final int NE_SPECIAL = 256;

    @Test
    public void testCase1() throws Exception {
        CommandApdu commandApdu = CommandApdu.create(CommandApduTest.CLA, CommandApduTest.INS, CommandApduTest.P1, CommandApduTest.P2);
        assertParsesCorrectly(commandApdu);
    }

    @Test
    public void testCase2s() throws Exception {
        CommandApdu commandApdu = CommandApdu.create(CommandApduTest.CLA, CommandApduTest.INS, CommandApduTest.P1, CommandApduTest.P2, CommandApduTest.NE_SHORT);
        Assert.assertEquals(5, commandApdu.toBytes().length);
        assertParsesCorrectly(commandApdu);
    }

    @Test
    public void testCase2e() throws Exception {
        CommandApdu commandApdu = CommandApdu.create(CommandApduTest.CLA, CommandApduTest.INS, CommandApduTest.P1, CommandApduTest.P2, CommandApduTest.NE_LONG);
        Assert.assertEquals(7, commandApdu.toBytes().length);
        assertParsesCorrectly(commandApdu);
    }

    @Test
    public void testCase2e_specialNe() throws Exception {
        CommandApdu commandApdu = CommandApdu.create(CommandApduTest.CLA, CommandApduTest.INS, CommandApduTest.P1, CommandApduTest.P2, CommandApduTest.NE_SPECIAL);
        Assert.assertEquals(5, commandApdu.toBytes().length);
        assertParsesCorrectly(commandApdu);
    }

    @Test
    public void testCase3s() throws Exception {
        CommandApdu commandApdu = CommandApdu.create(CommandApduTest.CLA, CommandApduTest.INS, CommandApduTest.P1, CommandApduTest.P2, CommandApduTest.DATA_SHORT);
        Assert.assertEquals(((4 + 1) + (CommandApduTest.DATA_SHORT.length)), commandApdu.toBytes().length);
        assertParsesCorrectly(commandApdu);
    }

    @Test
    public void testCase3e() throws Exception {
        CommandApdu commandApdu = CommandApdu.create(CommandApduTest.CLA, CommandApduTest.INS, CommandApduTest.P1, CommandApduTest.P2, CommandApduTest.DATA_LONG);
        Assert.assertEquals(((4 + 3) + (CommandApduTest.DATA_LONG.length)), commandApdu.toBytes().length);
        assertParsesCorrectly(commandApdu);
    }

    @Test
    public void testCase4s() throws Exception {
        CommandApdu commandApdu = CommandApdu.create(CommandApduTest.CLA, CommandApduTest.INS, CommandApduTest.P1, CommandApduTest.P2, CommandApduTest.DATA_SHORT, 5);
        assertArrayEquals(Hex.decode("010203040301020305"), commandApdu.toBytes());
        assertParsesCorrectly(commandApdu);
    }

    @Test
    public void testCase4e() throws Exception {
        CommandApdu commandApdu = CommandApdu.create(CommandApduTest.CLA, CommandApduTest.INS, CommandApduTest.P1, CommandApduTest.P2, CommandApduTest.DATA_LONG, 5);
        Assert.assertEquals(((4 + 5) + (CommandApduTest.DATA_LONG.length)), commandApdu.toBytes().length);
        assertParsesCorrectly(commandApdu);
    }
}

