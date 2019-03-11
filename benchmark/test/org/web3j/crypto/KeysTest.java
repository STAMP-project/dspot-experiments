package org.web3j.crypto;


import Keys.PUBLIC_KEY_SIZE;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;

import static Keys.PUBLIC_KEY_LENGTH_IN_HEX;


public class KeysTest {
    private static final byte[] ENCODED;

    static {
        byte[] privateKey = Numeric.hexStringToByteArray(SampleKeys.PRIVATE_KEY_STRING);
        byte[] publicKey = Numeric.hexStringToByteArray(SampleKeys.PUBLIC_KEY_STRING);
        ENCODED = Arrays.copyOf(privateKey, ((privateKey.length) + (publicKey.length)));
        System.arraycopy(publicKey, 0, KeysTest.ENCODED, privateKey.length, publicKey.length);
    }

    @Test
    public void testCreateSecp256k1KeyPair() throws Exception {
        KeyPair keyPair = Keys.createSecp256k1KeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        Assert.assertNotNull(privateKey);
        Assert.assertNotNull(publicKey);
        Assert.assertThat(privateKey.getEncoded().length, CoreMatchers.is(144));
        Assert.assertThat(publicKey.getEncoded().length, CoreMatchers.is(88));
    }

    @Test
    public void testCreateEcKeyPair() throws Exception {
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        Assert.assertThat(ecKeyPair.getPublicKey().signum(), CoreMatchers.is(1));
        Assert.assertThat(ecKeyPair.getPrivateKey().signum(), CoreMatchers.is(1));
    }

    @Test
    public void testGetAddressString() {
        Assert.assertThat(Keys.getAddress(SampleKeys.PUBLIC_KEY_STRING), CoreMatchers.is(SampleKeys.ADDRESS_NO_PREFIX));
    }

    @Test
    public void testGetAddressZeroPaddedAddress() {
        String publicKey = "0xa1b31be4d58a7ddd24b135db0da56a90fb5382077ae26b250e1dc9cd6232ce22" + "70f4c995428bc76aa78e522316e95d7834d725efc9ca754d043233af6ca90113";
        Assert.assertThat(Keys.getAddress(publicKey), CoreMatchers.is("01c52b08330e05d731e38c856c1043288f7d9744"));
    }

    @Test
    public void testGetAddressBigInteger() {
        Assert.assertThat(Keys.getAddress(SampleKeys.PUBLIC_KEY), CoreMatchers.is(SampleKeys.ADDRESS_NO_PREFIX));
    }

    @Test
    public void testGetAddressSmallPublicKey() {
        byte[] address = Keys.getAddress(Numeric.toBytesPadded(BigInteger.valueOf(4660), PUBLIC_KEY_SIZE));
        String expected = Numeric.toHexStringNoPrefix(address);
        Assert.assertThat(Keys.getAddress("0x1234"), CoreMatchers.equalTo(expected));
    }

    @Test
    public void testGetAddressZeroPadded() {
        byte[] address = Keys.getAddress(Numeric.toBytesPadded(BigInteger.valueOf(4660), PUBLIC_KEY_SIZE));
        String expected = Numeric.toHexStringNoPrefix(address);
        String value = "1234";
        Assert.assertThat(Keys.getAddress((("0x" + (Strings.zeros(((PUBLIC_KEY_LENGTH_IN_HEX) - (value.length()))))) + value)), CoreMatchers.equalTo(expected));
    }

    @Test
    public void testToChecksumAddress() {
        // Test cases as per https://github.com/ethereum/EIPs/blob/master/EIPS/eip-55.md#test-cases
        Assert.assertThat(Keys.toChecksumAddress("0xfb6916095ca1df60bb79ce92ce3ea74c37c5d359"), CoreMatchers.is("0xfB6916095ca1df60bB79Ce92cE3Ea74c37c5d359"));
        // All uppercase
        Assert.assertThat(Keys.toChecksumAddress("0x52908400098527886E0F7030069857D2E4169EE7"), CoreMatchers.is("0x52908400098527886E0F7030069857D2E4169EE7"));
        Assert.assertThat(Keys.toChecksumAddress("0x8617E340B3D01FA5F11F306F4090FD50E238070D"), CoreMatchers.is("0x8617E340B3D01FA5F11F306F4090FD50E238070D"));
        // All lowercase
        Assert.assertThat(Keys.toChecksumAddress("0xde709f2102306220921060314715629080e2fb77"), CoreMatchers.is("0xde709f2102306220921060314715629080e2fb77"));
        Assert.assertThat(Keys.toChecksumAddress("0x27b1fdb04752bbc536007a920d24acb045561c26"), CoreMatchers.is("0x27b1fdb04752bbc536007a920d24acb045561c26"));
        // Normal
        Assert.assertThat(Keys.toChecksumAddress("0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed"), CoreMatchers.is("0x5aAeb6053F3E94C9b9A09f33669435E7Ef1BeAed"));
        Assert.assertThat(Keys.toChecksumAddress("0xfB6916095ca1df60bB79Ce92cE3Ea74c37c5d359"), CoreMatchers.is("0xfB6916095ca1df60bB79Ce92cE3Ea74c37c5d359"));
        Assert.assertThat(Keys.toChecksumAddress("0xdbF03B407c01E7cD3CBea99509d93f8DDDC8C6FB"), CoreMatchers.is("0xdbF03B407c01E7cD3CBea99509d93f8DDDC8C6FB"));
        Assert.assertThat(Keys.toChecksumAddress("0xD1220A0cf47c7B9Be7A2E6BA89F429762e7b9aDb"), CoreMatchers.is("0xD1220A0cf47c7B9Be7A2E6BA89F429762e7b9aDb"));
    }

    @Test
    public void testSerializeECKey() {
        Assert.assertThat(Keys.serialize(SampleKeys.KEY_PAIR), CoreMatchers.is(KeysTest.ENCODED));
    }

    @Test
    public void testDeserializeECKey() {
        Assert.assertThat(Keys.deserialize(KeysTest.ENCODED), CoreMatchers.is(SampleKeys.KEY_PAIR));
    }

    @Test(expected = RuntimeException.class)
    public void testDeserializeInvalidKey() {
        Keys.deserialize(new byte[0]);
    }
}

