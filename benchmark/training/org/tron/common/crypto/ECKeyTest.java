package org.tron.common.crypto;


import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.SignatureException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.crypto.ECKey.ECDSASignature;
import org.tron.core.Wallet;


@Slf4j
public class ECKeyTest {
    private String privString = "c85ef7d79691fe79573b1a7064c19c1a9819ebdbd1faaab1a8ec92344438aaf4";

    private BigInteger privateKey = new BigInteger(privString, 16);

    private String pubString = "040947751e3022ecf3016be03ec77ab0ce3c2662b4843898cb068d74f698ccc8ad75aa17564ae80a20bb044ee7a6d903e8e8df624b089c95d66a0570f051e5a05b";

    private String compressedPubString = "030947751e3022ecf3016be03ec77ab0ce3c2662b4843898cb068d74f698ccc8ad";

    private byte[] pubKey = Hex.decode(pubString);

    private byte[] compressedPubKey = Hex.decode(compressedPubString);

    private String address = "cd2a3d9f938e13cd947ec05abc7fe734df8dd826";

    @Test
    public void testHashCode() {
        Assert.assertEquals((-351262686), ECKey.fromPrivate(privateKey).hashCode());
    }

    @Test
    public void testECKey() {
        ECKey key = new ECKey();
        Assert.assertTrue(key.isPubKeyCanonical());
        Assert.assertNotNull(key.getPubKey());
        Assert.assertNotNull(key.getPrivKeyBytes());
        logger.info(((Hex.toHexString(key.getPrivKeyBytes())) + " :Generated privkey"));
        logger.info(((Hex.toHexString(key.getPubKey())) + " :Generated pubkey"));
    }

    @Test
    public void testFromPrivateKey() {
        ECKey key = ECKey.fromPrivate(privateKey);
        Assert.assertTrue(key.isPubKeyCanonical());
        Assert.assertTrue(key.hasPrivKey());
        Assert.assertArrayEquals(pubKey, key.getPubKey());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrivatePublicKeyBytesNoArg() {
        new ECKey(((BigInteger) (null)), null);
        Assert.fail("Expecting an IllegalArgumentException for using only null-parameters");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPrivateKey() throws Exception {
        new ECKey(Security.getProvider("SunEC"), KeyPairGenerator.getInstance("RSA").generateKeyPair().getPrivate(), ECKey.fromPublicOnly(pubKey).getPubKeyPoint());
        Assert.fail("Expecting an IllegalArgumentException for using an non EC private key");
    }

    @Test
    public void testIsPubKeyOnly() {
        ECKey key = ECKey.fromPublicOnly(pubKey);
        Assert.assertTrue(key.isPubKeyCanonical());
        Assert.assertTrue(key.isPubKeyOnly());
        Assert.assertArrayEquals(key.getPubKey(), pubKey);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSignIncorrectInputSize() {
        ECKey key = new ECKey();
        String message = "The quick brown fox jumps over the lazy dog.";
        ECDSASignature sig = key.doSign(message.getBytes());
        Assert.fail("Expecting an IllegalArgumentException for a non 32-byte input");
    }

    @Test(expected = SignatureException.class)
    public void testBadBase64Sig() throws SignatureException {
        byte[] messageHash = new byte[32];
        ECKey.signatureToKey(messageHash, "This is not valid Base64!");
        Assert.fail("Expecting a SignatureException for invalid Base64");
    }

    @Test(expected = SignatureException.class)
    public void testInvalidSignatureLength() throws SignatureException {
        byte[] messageHash = new byte[32];
        ECKey.signatureToKey(messageHash, "abcdefg");
        Assert.fail("Expecting a SignatureException for invalid signature length");
    }

    @Test
    public void testPublicKeyFromPrivate() {
        byte[] pubFromPriv = ECKey.publicKeyFromPrivate(privateKey, false);
        Assert.assertArrayEquals(pubKey, pubFromPriv);
    }

    @Test
    public void testPublicKeyFromPrivateCompressed() {
        byte[] pubFromPriv = ECKey.publicKeyFromPrivate(privateKey, true);
        Assert.assertArrayEquals(compressedPubKey, pubFromPriv);
    }

    @Test
    public void testGetAddress() {
        ECKey key = ECKey.fromPublicOnly(pubKey);
        // Addresses are prefixed with a constant.
        byte[] prefixedAddress = key.getAddress();
        byte[] unprefixedAddress = Arrays.copyOfRange(key.getAddress(), 1, prefixedAddress.length);
        Assert.assertArrayEquals(Hex.decode(address), unprefixedAddress);
        Assert.assertEquals(Wallet.getAddressPreFixByte(), prefixedAddress[0]);
    }

    @Test
    public void testGetAddressFromPrivateKey() {
        ECKey key = ECKey.fromPrivate(privateKey);
        // Addresses are prefixed with a constant.
        byte[] prefixedAddress = key.getAddress();
        byte[] unprefixedAddress = Arrays.copyOfRange(key.getAddress(), 1, prefixedAddress.length);
        Assert.assertArrayEquals(Hex.decode(address), unprefixedAddress);
        Assert.assertEquals(Wallet.getAddressPreFixByte(), prefixedAddress[0]);
    }

    @Test
    public void testToString() {
        ECKey key = ECKey.fromPrivate(BigInteger.TEN);// An example private key.

        Assert.assertEquals("pub:04a0434d9e47f3c86235477c7b1ae6ae5d3442d49b1943c2b752a68e2a47e247c7893aba425419bc27a3b6c7e693a24c696f794c2ed877a1593cbee53b037368d7", key.toString());
    }

    @Test
    public void testIsPubKeyCanonicalCorect() {
        // Test correct prefix 4, right length 65
        byte[] canonicalPubkey1 = new byte[65];
        canonicalPubkey1[0] = 4;
        Assert.assertTrue(ECKey.isPubKeyCanonical(canonicalPubkey1));
        // Test correct prefix 2, right length 33
        byte[] canonicalPubkey2 = new byte[33];
        canonicalPubkey2[0] = 2;
        Assert.assertTrue(ECKey.isPubKeyCanonical(canonicalPubkey2));
        // Test correct prefix 3, right length 33
        byte[] canonicalPubkey3 = new byte[33];
        canonicalPubkey3[0] = 3;
        Assert.assertTrue(ECKey.isPubKeyCanonical(canonicalPubkey3));
    }

    @Test
    public void testIsPubKeyCanonicalWrongLength() {
        // Test correct prefix 4, but wrong length !65
        byte[] nonCanonicalPubkey1 = new byte[64];
        nonCanonicalPubkey1[0] = 4;
        Assert.assertFalse(ECKey.isPubKeyCanonical(nonCanonicalPubkey1));
        // Test correct prefix 2, but wrong length !33
        byte[] nonCanonicalPubkey2 = new byte[32];
        nonCanonicalPubkey2[0] = 2;
        Assert.assertFalse(ECKey.isPubKeyCanonical(nonCanonicalPubkey2));
        // Test correct prefix 3, but wrong length !33
        byte[] nonCanonicalPubkey3 = new byte[32];
        nonCanonicalPubkey3[0] = 3;
        Assert.assertFalse(ECKey.isPubKeyCanonical(nonCanonicalPubkey3));
    }

    @Test
    public void testIsPubKeyCanonicalWrongPrefix() {
        // Test wrong prefix 4, right length 65
        byte[] nonCanonicalPubkey4 = new byte[65];
        Assert.assertFalse(ECKey.isPubKeyCanonical(nonCanonicalPubkey4));
        // Test wrong prefix 2, right length 33
        byte[] nonCanonicalPubkey5 = new byte[33];
        Assert.assertFalse(ECKey.isPubKeyCanonical(nonCanonicalPubkey5));
        // Test wrong prefix 3, right length 33
        byte[] nonCanonicalPubkey6 = new byte[33];
        Assert.assertFalse(ECKey.isPubKeyCanonical(nonCanonicalPubkey6));
    }

    @Test
    public void testGetPrivKeyBytes() {
        ECKey key = new ECKey();
        Assert.assertNotNull(key.getPrivKeyBytes());
        Assert.assertEquals(32, key.getPrivKeyBytes().length);
    }

    @Test
    public void testEqualsObject() {
        ECKey key0 = new ECKey();
        ECKey key1 = ECKey.fromPrivate(privateKey);
        ECKey key2 = ECKey.fromPrivate(privateKey);
        Assert.assertFalse(key0.equals(key1));
        Assert.assertTrue(key1.equals(key1));
        Assert.assertTrue(key1.equals(key2));
    }

    @Test
    public void decryptAECSIC() {
        ECKey key = ECKey.fromPrivate(Hex.decode("abb51256c1324a1350598653f46aa3ad693ac3cf5d05f36eba3f495a1f51590f"));
        byte[] payload = key.decryptAES(Hex.decode("84a727bc81fa4b13947dc9728b88fd08"));
        System.out.println(Hex.toHexString(payload));
    }

    @Test
    public void testNodeId() {
        ECKey key = ECKey.fromPublicOnly(pubKey);
        Assert.assertEquals(key, ECKey.fromNodeId(key.getNodeId()));
    }
}

