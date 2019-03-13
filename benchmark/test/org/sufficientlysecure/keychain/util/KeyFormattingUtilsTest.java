package org.sufficientlysecure.keychain.util;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.ui.util.KeyFormattingUtils;


@RunWith(KeychainTestRunner.class)
public class KeyFormattingUtilsTest {
    static final byte[] fp = new byte[]{ ((byte) (212)), ((byte) (171)), ((byte) (25)), ((byte) (41)), ((byte) (100)), ((byte) (247)), ((byte) (106)), ((byte) (127)), ((byte) (143)), ((byte) (138)), ((byte) (155)), ((byte) (53)), ((byte) (123)), ((byte) (209)), ((byte) (131)), ((byte) (32)), ((byte) (222)), ((byte) (173)), ((byte) (250)), ((byte) (17)) };

    static final long keyId = 8922056513995799057L;

    @Test
    public void testStuff() {
        Assert.assertEquals(KeyFormattingUtils.convertFingerprintToKeyId(KeyFormattingUtilsTest.fp), KeyFormattingUtilsTest.keyId);
        Assert.assertEquals("d4ab192964f76a7f8f8a9b357bd18320deadfa11", KeyFormattingUtils.convertFingerprintToHex(KeyFormattingUtilsTest.fp));
        Assert.assertEquals("0x7bd18320deadfa11", KeyFormattingUtils.convertKeyIdToHex(KeyFormattingUtilsTest.keyId));
        Assert.assertEquals("0xdeadfa11", KeyFormattingUtils.convertKeyIdToHexShort(KeyFormattingUtilsTest.keyId));
    }
}

