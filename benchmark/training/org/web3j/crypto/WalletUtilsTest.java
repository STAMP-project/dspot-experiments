package org.web3j.crypto;


import SampleKeys.ADDRESS;
import SampleKeys.ADDRESS_NO_PREFIX;
import SampleKeys.PRIVATE_KEY_STRING;
import java.io.File;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.utils.Numeric;

import static SampleKeys.ADDRESS;
import static SampleKeys.PRIVATE_KEY_STRING;


public class WalletUtilsTest {
    private File tempDir;

    @Test
    public void testGenerateBip39Wallets() throws Exception {
        Bip39Wallet wallet = WalletUtils.generateBip39Wallet(SampleKeys.PASSWORD, tempDir);
        byte[] seed = MnemonicUtils.generateSeed(wallet.getMnemonic(), SampleKeys.PASSWORD);
        Credentials credentials = Credentials.create(ECKeyPair.create(Hash.sha256(seed)));
        Assert.assertEquals(credentials, WalletUtils.loadBip39Credentials(SampleKeys.PASSWORD, wallet.getMnemonic()));
    }

    @Test
    public void testGenerateFullNewWalletFile() throws Exception {
        String fileName = WalletUtils.generateFullNewWalletFile(SampleKeys.PASSWORD, tempDir);
        testGeneratedNewWalletFile(fileName);
    }

    @Test
    public void testGenerateNewWalletFile() throws Exception {
        String fileName = WalletUtils.generateNewWalletFile(SampleKeys.PASSWORD, tempDir);
        testGeneratedNewWalletFile(fileName);
    }

    @Test
    public void testGenerateLightNewWalletFile() throws Exception {
        String fileName = WalletUtils.generateLightNewWalletFile(SampleKeys.PASSWORD, tempDir);
        testGeneratedNewWalletFile(fileName);
    }

    @Test
    public void testGenerateFullWalletFile() throws Exception {
        String fileName = WalletUtils.generateWalletFile(SampleKeys.PASSWORD, SampleKeys.KEY_PAIR, tempDir, true);
        testGenerateWalletFile(fileName);
    }

    @Test
    public void testGenerateLightWalletFile() throws Exception {
        String fileName = WalletUtils.generateWalletFile(SampleKeys.PASSWORD, SampleKeys.KEY_PAIR, tempDir, false);
        testGenerateWalletFile(fileName);
    }

    @Test
    public void testLoadCredentialsFromFile() throws Exception {
        Credentials credentials = WalletUtils.loadCredentials(SampleKeys.PASSWORD, new File(WalletUtilsTest.class.getResource(("/keyfiles/" + ("UTC--2016-11-03T05-55-06." + "340672473Z--ef678007d18427e6022059dbc264f27507cd1ffc"))).getFile()));
        Assert.assertThat(credentials, IsEqual.equalTo(SampleKeys.CREDENTIALS));
    }

    @Test
    public void testLoadCredentialsFromString() throws Exception {
        Credentials credentials = WalletUtils.loadCredentials(SampleKeys.PASSWORD, WalletUtilsTest.class.getResource(("/keyfiles/" + ("UTC--2016-11-03T05-55-06." + "340672473Z--ef678007d18427e6022059dbc264f27507cd1ffc"))).getFile());
        Assert.assertThat(credentials, IsEqual.equalTo(SampleKeys.CREDENTIALS));
    }

    @Test
    public void testGetDefaultKeyDirectory() {
        Assert.assertTrue(WalletUtils.getDefaultKeyDirectory("Mac OS X").endsWith(String.format("%sLibrary%sEthereum", File.separator, File.separator)));
        Assert.assertTrue(WalletUtils.getDefaultKeyDirectory("Windows").endsWith(String.format("%sEthereum", File.separator)));
        Assert.assertTrue(WalletUtils.getDefaultKeyDirectory("Linux").endsWith(String.format("%s.ethereum", File.separator)));
    }

    @Test
    public void testGetTestnetKeyDirectory() {
        Assert.assertTrue(WalletUtils.getMainnetKeyDirectory().endsWith(String.format("%skeystore", File.separator)));
        Assert.assertTrue(WalletUtils.getTestnetKeyDirectory().endsWith(String.format("%stestnet%skeystore", File.separator, File.separator)));
        Assert.assertTrue(WalletUtils.getRinkebyKeyDirectory().endsWith(String.format("%srinkeby%skeystore", File.separator, File.separator)));
    }

    @Test
    public void testIsValidPrivateKey() {
        Assert.assertTrue(WalletUtils.isValidPrivateKey(PRIVATE_KEY_STRING));
        Assert.assertTrue(WalletUtils.isValidPrivateKey(Numeric.prependHexPrefix(PRIVATE_KEY_STRING)));
        Assert.assertFalse(WalletUtils.isValidPrivateKey(""));
        Assert.assertFalse(WalletUtils.isValidPrivateKey(((PRIVATE_KEY_STRING) + "a")));
        Assert.assertFalse(WalletUtils.isValidPrivateKey(PRIVATE_KEY_STRING.substring(1)));
    }

    @Test
    public void testIsValidAddress() {
        Assert.assertTrue(WalletUtils.isValidAddress(ADDRESS));
        Assert.assertTrue(WalletUtils.isValidAddress(ADDRESS_NO_PREFIX));
        Assert.assertFalse(WalletUtils.isValidAddress(""));
        Assert.assertFalse(WalletUtils.isValidAddress(((ADDRESS) + 'a')));
        Assert.assertFalse(WalletUtils.isValidAddress(ADDRESS.substring(1)));
    }
}

