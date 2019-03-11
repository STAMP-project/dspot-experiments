package org.web3j.crypto;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class Bip44WalletUtilsTest {
    private File tempDir;

    @SuppressWarnings("checkstyle:LineLength")
    @Test
    public void generateBip44KeyPair() {
        String mnemonic = "spider elbow fossil truck deal circle divert sleep safe report laundry above";
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);
        String seedStr = bytesToHex(seed);
        Assert.assertEquals("f0d2ab78b96acd147119abad1cd70eb4fec4f0e0a95744cf532e6a09347b08101213b4cbf50eada0eb89cba444525fe28e69707e52aa301c6b47ce1c5ef82eb5", seedStr);
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        Assert.assertEquals("xprv9s21ZrQH143K2yA9Cdad5gjqHRC7apVUgEyYq5jXeXigDZ3PfEnps44tJprtMXr7PZivEsin6Qrbad7PuiEy4tn5jAEK6A3U46f9KvfRCmD", Base58.encode(Bip32Test.addChecksum(Bip32Test.serializePrivate(masterKeypair))));
        Bip32ECKeyPair bip44Keypair = Bip44WalletUtils.generateBip44KeyPair(masterKeypair);
        Assert.assertEquals("xprv9zvpunws9gusoXVkmqAXWQm5z5hjR5kY3ifRGL7M8Kpjn8kRhavkGnFLjnFWPGGS2gAUw8rP33Lmj6SwZUpwy2mn2fXRYWzGa9WRTnE8DPz", Base58.encode(Bip32Test.addChecksum(Bip32Test.serializePrivate(bip44Keypair))));
        Assert.assertEquals("xpub6DvBKJUkz4UB21aDsrhXsYhpY7YDpYUPQwb24iWxgfMiew5aF8EzpaZpb567bYYbMfUnPwFNuRYvVpMGQUcaGPMoXUEUZKFvx7LaU5b7zBD", Base58.encode(Bip32Test.addChecksum(Bip32Test.serializePublic(bip44Keypair))));
    }

    @SuppressWarnings("checkstyle:LineLength")
    @Test
    public void generateBip44KeyPairTestNet() {
        String mnemonic = "spider elbow fossil truck deal circle divert sleep safe report laundry above";
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, null);
        String seedStr = bytesToHex(seed);
        Assert.assertEquals("f0d2ab78b96acd147119abad1cd70eb4fec4f0e0a95744cf532e6a09347b08101213b4cbf50eada0eb89cba444525fe28e69707e52aa301c6b47ce1c5ef82eb5", seedStr);
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        Assert.assertEquals("xprv9s21ZrQH143K2yA9Cdad5gjqHRC7apVUgEyYq5jXeXigDZ3PfEnps44tJprtMXr7PZivEsin6Qrbad7PuiEy4tn5jAEK6A3U46f9KvfRCmD", Base58.encode(Bip32Test.addChecksum(Bip32Test.serializePrivate(masterKeypair))));
        Bip32ECKeyPair bip44Keypair = Bip44WalletUtils.generateBip44KeyPair(masterKeypair, true);
        Assert.assertEquals("xprv9zhLxq63By3SX5hAMKnxjGy7L18bnn7GzDQv53eYYqeRX9M82riC1dqovamttwFpk2ZkDQxgcikBQzs1DTu2KShJJqnqgx83EftUB3k39uc", Base58.encode(Bip32Test.addChecksum(Bip32Test.serializePrivate(bip44Keypair))));
        Assert.assertEquals("xpub6DghNLcw2LbjjZmdTMKy6Quqt2y6CEq8MSLWsS4A7BBQPwgGaQ2SZSAHmsrqBVxLegjW2mBfcvDBhpeEqCmucTTPJiNLHQkiDuKwHs9gEtk", Base58.encode(Bip32Test.addChecksum(Bip32Test.serializePublic(bip44Keypair))));
    }

    @Test
    public void testGenerateBip44Wallets() throws Exception {
        Bip39Wallet wallet = Bip44WalletUtils.generateBip44Wallet(SampleKeys.PASSWORD, tempDir);
        byte[] seed = MnemonicUtils.generateSeed(wallet.getMnemonic(), SampleKeys.PASSWORD);
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        Bip32ECKeyPair bip44Keypair = Bip44WalletUtils.generateBip44KeyPair(masterKeypair);
        Credentials credentials = Credentials.create(bip44Keypair);
        Assert.assertEquals(credentials, Bip44WalletUtils.loadBip44Credentials(SampleKeys.PASSWORD, wallet.getMnemonic()));
    }
}

