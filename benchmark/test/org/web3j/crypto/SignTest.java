package org.web3j.crypto;


import Sign.SignatureData;
import java.math.BigInteger;
import java.security.SignatureException;
import org.bouncycastle.math.ec.ECPoint;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.utils.Numeric;


public class SignTest {
    private static final byte[] TEST_MESSAGE = "A test message".getBytes();

    @Test
    public void testSignMessage() {
        Sign.SignatureData signatureData = Sign.signPrefixedMessage(SignTest.TEST_MESSAGE, SampleKeys.KEY_PAIR);
        Sign.SignatureData expected = new Sign.SignatureData(((byte) (28)), Numeric.hexStringToByteArray("0x0464eee9e2fe1a10ffe48c78b80de1ed8dcf996f3f60955cb2e03cb21903d930"), Numeric.hexStringToByteArray("0x06624da478b3f862582e85b31c6a21c6cae2eee2bd50f55c93c4faad9d9c8d7f"));
        Assert.assertThat(signatureData, CoreMatchers.is(expected));
    }

    @Test
    public void testSignedMessageToKey() throws SignatureException {
        Sign.SignatureData signatureData = Sign.signPrefixedMessage(SignTest.TEST_MESSAGE, SampleKeys.KEY_PAIR);
        BigInteger key = Sign.signedPrefixedMessageToKey(SignTest.TEST_MESSAGE, signatureData);
        Assert.assertThat(key, IsEqual.equalTo(SampleKeys.PUBLIC_KEY));
    }

    @Test
    public void testPublicKeyFromPrivateKey() {
        Assert.assertThat(Sign.publicKeyFromPrivate(SampleKeys.PRIVATE_KEY), IsEqual.equalTo(SampleKeys.PUBLIC_KEY));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidSignature() throws SignatureException {
        Sign.signedMessageToKey(SignTest.TEST_MESSAGE, new Sign.SignatureData(((byte) (27)), new byte[]{ 1 }, new byte[]{ 0 }));
    }

    @Test
    public void testPublicKeyFromPrivatePoint() {
        ECPoint point = Sign.publicPointFromPrivate(SampleKeys.PRIVATE_KEY);
        Assert.assertThat(Sign.publicFromPoint(point.getEncoded(false)), IsEqual.equalTo(SampleKeys.PUBLIC_KEY));
    }
}

