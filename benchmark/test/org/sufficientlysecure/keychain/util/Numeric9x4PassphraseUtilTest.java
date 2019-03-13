package org.sufficientlysecure.keychain.util;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class Numeric9x4PassphraseUtilTest {
    private static final int RANDOM_SEED = 12345;

    private static final String TRANSFER_CODE_3x3x4 = "1018-5452-1972-0624-7325-1126-8153-1997-1535";

    @Test
    public void generateNumeric9x4Passphrase() {
        Random r = new Random(Numeric9x4PassphraseUtilTest.RANDOM_SEED);
        Passphrase passphrase = Numeric9x4PassphraseUtil.generateNumeric9x4Passphrase(r);
        Assert.assertEquals(Numeric9x4PassphraseUtilTest.TRANSFER_CODE_3x3x4, passphrase.toStringUnsafe());
    }

    @Test
    public void isNumeric9x4Passphrase() {
        boolean isValidCodeLayout = Numeric9x4PassphraseUtil.isNumeric9x4Passphrase(Numeric9x4PassphraseUtilTest.TRANSFER_CODE_3x3x4);
        Assert.assertTrue(isValidCodeLayout);
    }

    @Test
    public void isNumeric9x4Passphrase_withBadSuffix() {
        boolean isValidCodeLayout = Numeric9x4PassphraseUtil.isNumeric9x4Passphrase(((Numeric9x4PassphraseUtilTest.TRANSFER_CODE_3x3x4) + "x"));
        Assert.assertFalse(isValidCodeLayout);
    }
}

