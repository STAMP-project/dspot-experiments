package org.sufficientlysecure.keychain.pgp;


import java.io.ByteArrayInputStream;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.pgp.UncachedKeyRing.IteratorWithIOThrow;


@SuppressWarnings("WeakerAccess")
@RunWith(KeychainTestRunner.class)
public class PgpHelperTest {
    static final String INPUT_KEY_BLOCK_TWO_OCTET_LENGTH = "-----BEGIN PGP PUBLIC KEY BLOCK----- Version: GnuPG v2 " + ((((((((((((((((((((((((("mQENBFnA7Y0BCAC+pdQ1mV9QguWvAyMsKiKkzeP5VxbIDUyQ8OBDFKIrZKZGTzjZ " + "xvZs22j5pXWYlyDi4HU4+nuQmAMo6jxJMKQQkW7Y5AmRYijboLN+lZ8L28bYJC4o ") + "PcAnS3xlib2lE7aNK2BRUbctkhahhb2hohAxiXTUdGmfr9sHgZ2+B9sPTfuhrvtI ") + "9cFHZ5/0Wp4pLhLB53gduYeLuw4vVfUd7t0C4IhqB+t5HE+F3lolgya7hXxdH0ji ") + "oFNldCWT2qNdmmehIyY0WaoIrnUm2gVA4LMZ2fQTfsInpec5YT85OZaPEeBs3Opg ") + "3aGxV4mhXOxHkfREJRuYXcqL1s/UERGNprp9ABEBAAG0E01yLiBNYWxmb3JtZWQg ") + "QXNjaWmJAVQEEwEIAD4WIQTLHyFqT4uzqqEXR5Zz6vdoZVkMnwUCWcDtjQIbAwUJ ") + "A8JnAAULCQgHAgYVCAkKCwIEFgIDAQIeAQIXgAAKCRBz6vdoZVkMnwpcB/985UbR ") + "qxG5pzaLGePdl+Cm6JBra2mC3AfMbJobjHR8YVufDw1tA6qyCMW0emHFi8t/rG8j ") + "tzPIadcl30rOzaGUTF85G5SqbwgAFHddZ01af36F6em0d5tY3+FCclQR7ynFPQlA ") + "+KB9k/M5X91ty6Q3/EAaXst5Uwh1WnKNC1js9RAcYL1s1MXKxg2iMmtE0DvwMAWq ") + "XRR+ADjzqkVdpdrzanTY7b72nuiGfe/H75b7/StIAfyxSZc5BU5535J0wF7Boz4p ") + "A6zRFXTphabmAE9FIKhgj5X7fbU64Hsrc5OkvWt4dF/6VRE4oXgUYwLKaDEH7A0k ") + "32GBGOkmnQGLKuqhuQENBFnA7Y0BCADKxQ1APSraxNKMpJv9vEVcXK3Sr91SYpGY ") + "s/ugYNio2xvIt9Qe2AjYGNSE9+wS6qLRxbbzucIRxl9jbn2QTNbJr7epLVdj3wtL ") + "JlkKsv13iao77Hg9WMvKh+NHpGoIFn4g5LOeYYG0QkZOvdu6b4Eg0RAryTLBh9jB ") + "eMLELkZTFDuQAgOSrVY0XgoURNcaDRtVarnVNBIO1N7/7TNXtmL22wR0wpqh4mKv ") + "vIvhE5itlIrthJHWzTcLDv5BHfyX23wqEpQFEffs1D72k9Ruh60OGgU0XAiVF654 ") + "WjgZCUoscPCLWcDGDOlcN7FpBxMDi1Ao3+7sLOMi9zES0InJ9q8LABEBAAGJATwE ") + "GAEIACYWIQTLHyFqT4uzqqEXR5Zz6vdoZVkMnwUCWcDtjQIbDAUJA8JnAAAKCRBz ") + "6vdoZVkMn3/+B/9B0vrDITV2FpdT+99WVsXJsoiXOsqLfv3WkC0l0RBAcCaUeXxp ") + "EqzyXQ0dVi6RoXu67dSnah6bdgfVnH14hJE8jc30GJ4QEpPD9kAKyodej15ledR5 ") + "sbdjeEfsavn9tvACJ0svfu8YVJjUjJLOj5axXy8wUBm5UvCdZuSL4EjPq7hXdq+j ") + "O/eTJGOfMl6hC4rRxRUbM+piZzbYcQ0lO3R2yPlEwzlO+asM9820V9bkviJUrXiY ") + "c5EX44mwFdhpXuHbRS18DJjCVcMhEsPG6rQ0Qy/6/dafow5HExRBmZl6ZkfjR2Lb ") + "alOZH0SNi47bvn6QKqKgiqT4f9mImyEDtSj/ =2V66 -----END PGP PUBLIC KEY BLOCK-----");

    static final String INPUT_KEY_BLOCK_ONE_OCTET_LENGTH = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" + ((((((((((("\n" + "mFIEVk2iwRMIKoZIzj0DAQcCAwTBaWEpVYOfZDm85s/zszd4J4CBW8FesYiYQTeX\n") + "5WMtwXsqrG5/ZcIgHNBzI0EvUbm/oSBFUJNk7RhmOk6MpS2gtAdNci4gRUNDiHkE\n") + "ExMIACEFAlZNosECGwMFCwkIBwIGFQgJCgsCBBYCAwECHgECF4AACgkQt60Zc7T/\n") + "SfQTPAD/bZ0ld3UyqAt8oPoHyJduGMkbur5KYoht1w/MMtiogG0BAN8Anhy55kTe\n") + "H4VmMWxzK9M+kIFPzqEVHOzsuE5nhJOouFYEVk2iwRIIKoZIzj0DAQcCAwSvfTrq\n") + "kkVeD0cVM8FZwhjTaG+B9wgk7yeoMgjIrSuZLiRjGAYC7Kq+6OiczduoItC2oMuK\n") + "GpymTF6t+CmQpUfuAwEIB4hhBBgTCAAJBQJWTaLBAhsMAAoJELetGXO0/0n00BwA\n") + "/2d1w/A4xMwfIFrKDwHeHALUBaIOuhF2AKd/43HujmuLAQDdcWf3h/0zjgBTjSoB\n") + "bcVr5AE/huKUnwKYa7SP7wzoZg==\n") + "=ou9N\n") + "-----END PGP PUBLIC KEY BLOCK-----\n");

    @Test
    public void reformatPgpPublicKeyBlock() throws Exception {
        String reformattedKey = PgpHelper.reformatPgpPublicKeyBlock(PgpHelperTest.INPUT_KEY_BLOCK_TWO_OCTET_LENGTH);
        Assert.assertNotNull(reformattedKey);
        UncachedKeyRing.decodeFromData(reformattedKey.getBytes());
    }

    @Test
    public void reformatPgpPublicKeyBlock_consecutiveKeys() throws Exception {
        String reformattedKey = PgpHelper.reformatPgpPublicKeyBlock(((PgpHelperTest.INPUT_KEY_BLOCK_TWO_OCTET_LENGTH) + (PgpHelperTest.INPUT_KEY_BLOCK_TWO_OCTET_LENGTH)));
        Assert.assertNotNull(reformattedKey);
        IteratorWithIOThrow<UncachedKeyRing> uncachedKeyRingIteratorWithIOThrow = UncachedKeyRing.fromStream(new ByteArrayInputStream(reformattedKey.getBytes()));
        Assert.assertNotNull(uncachedKeyRingIteratorWithIOThrow.next());
        Assert.assertNotNull(uncachedKeyRingIteratorWithIOThrow.next());
        Assert.assertFalse(uncachedKeyRingIteratorWithIOThrow.hasNext());
    }

    @Test
    public void reformatPgpPublicKeyBlock_shouldBeIdempotent() throws Exception {
        String reformattedKey1 = PgpHelper.reformatPgpPublicKeyBlock(PgpHelperTest.INPUT_KEY_BLOCK_TWO_OCTET_LENGTH);
        Assert.assertNotNull(reformattedKey1);
        String reformattedKey2 = PgpHelper.reformatPgpPublicKeyBlock(reformattedKey1);
        Assert.assertEquals(reformattedKey1, reformattedKey2);
    }

    @Test
    public void reformatPgpPublicKeyBlock_withOneOctetLengthHeader() throws Exception {
        String reformattedKey = PgpHelper.reformatPgpPublicKeyBlock(PgpHelperTest.INPUT_KEY_BLOCK_ONE_OCTET_LENGTH);
        Assert.assertNotNull(reformattedKey);
        UncachedKeyRing.decodeFromData(reformattedKey.getBytes());
    }
}

