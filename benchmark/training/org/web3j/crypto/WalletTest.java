package org.web3j.crypto;


import SampleKeys.KEY_PAIR;
import SampleKeys.PASSWORD;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.utils.Numeric;


// CHECKSTYLE:ON
public class WalletTest {
    @Test
    public void testCreateStandard() throws Exception {
        testCreate(Wallet.createStandard(SampleKeys.PASSWORD, KEY_PAIR));
    }

    @Test
    public void testCreateLight() throws Exception {
        testCreate(Wallet.createLight(SampleKeys.PASSWORD, KEY_PAIR));
    }

    @Test
    public void testEncryptDecryptStandard() throws Exception {
        testEncryptDecrypt(Wallet.createStandard(SampleKeys.PASSWORD, KEY_PAIR));
    }

    @Test
    public void testEncryptDecryptLight() throws Exception {
        testEncryptDecrypt(Wallet.createLight(SampleKeys.PASSWORD, KEY_PAIR));
    }

    @Test
    public void testDecryptAes128Ctr() throws Exception {
        WalletFile walletFile = load(WalletTest.AES_128_CTR);
        ECKeyPair ecKeyPair = Wallet.decrypt(WalletTest.PASSWORD, walletFile);
        Assert.assertThat(Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey()), Is.is(WalletTest.SECRET));
    }

    @Test
    public void testDecryptScrypt() throws Exception {
        WalletFile walletFile = load(WalletTest.SCRYPT);
        ECKeyPair ecKeyPair = Wallet.decrypt(WalletTest.PASSWORD, walletFile);
        Assert.assertThat(Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey()), Is.is(WalletTest.SECRET));
    }

    @Test
    public void testGenerateRandomBytes() {
        Assert.assertThat(Wallet.generateRandomBytes(0), Is.is(new byte[]{  }));
        Assert.assertThat(Wallet.generateRandomBytes(10).length, Is.is(10));
    }

    private static final String PASSWORD = "Insecure Pa55w0rd";

    private static final String SECRET = "a392604efc2fad9c0b3da43b5f698a2e3f270f170d859912be0d54742275c5f6";

    // CHECKSTYLE:OFF
    private static final String AES_128_CTR = "{\n" + ((((((((((((((((("    \"crypto\" : {\n" + "        \"cipher\" : \"aes-128-ctr\",\n") + "        \"cipherparams\" : {\n") + "            \"iv\" : \"02ebc768684e5576900376114625ee6f\"\n") + "        },\n") + "        \"ciphertext\" : \"7ad5c9dd2c95f34a92ebb86740b92103a5d1cc4c2eabf3b9a59e1f83f3181216\",\n") + "        \"kdf\" : \"pbkdf2\",\n") + "        \"kdfparams\" : {\n") + "            \"c\" : 262144,\n") + "            \"dklen\" : 32,\n") + "            \"prf\" : \"hmac-sha256\",\n") + "            \"salt\" : \"0e4cf3893b25bb81efaae565728b5b7cde6a84e224cbf9aed3d69a31c981b702\"\n") + "        },\n") + "        \"mac\" : \"2b29e4641ec17f4dc8b86fc8592090b50109b372529c30b001d4d96249edaf62\"\n") + "    },\n") + "    \"id\" : \"af0451b4-6020-4ef0-91ec-794a5a965b01\",\n") + "    \"version\" : 3\n") + "}");

    private static final String SCRYPT = "{\n" + (((((((((((((((((("    \"crypto\" : {\n" + "        \"cipher\" : \"aes-128-ctr\",\n") + "        \"cipherparams\" : {\n") + "            \"iv\" : \"3021e1ef4774dfc5b08307f3a4c8df00\"\n") + "        },\n") + "        \"ciphertext\" : \"4dd29ba18478b98cf07a8a44167acdf7e04de59777c4b9c139e3d3fa5cb0b931\",\n") + "        \"kdf\" : \"scrypt\",\n") + "        \"kdfparams\" : {\n") + "            \"dklen\" : 32,\n") + "            \"n\" : 262144,\n") + "            \"r\" : 8,\n") + "            \"p\" : 1,\n") + "            \"salt\" : \"4f9f68c71989eb3887cd947c80b9555fce528f210199d35c35279beb8c2da5ca\"\n") + "        },\n") + "        \"mac\" : \"7e8f2192767af9be18e7a373c1986d9190fcaa43ad689bbb01a62dbde159338d\"\n") + "    },\n") + "    \"id\" : \"7654525c-17e0-4df5-94b5-c7fde752c9d2\",\n") + "    \"version\" : 3\n") + "}");
}

