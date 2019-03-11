/**
 * Copyright 2011 Google Inc.
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.core;


import ScriptType.P2PKH;
import ScriptType.P2SH;
import Utils.HEX;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.Networks;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.junit.Assert;
import org.junit.Test;


public class LegacyAddressTest {
    private static final NetworkParameters TESTNET = TestNet3Params.get();

    private static final NetworkParameters MAINNET = MainNetParams.get();

    @Test
    public void testJavaSerialization() throws Exception {
        LegacyAddress testAddress = LegacyAddress.fromBase58(LegacyAddressTest.TESTNET, "n4eA2nbYqErp7H6jebchxAN59DmNpksexv");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new ObjectOutputStream(os).writeObject(testAddress);
        LegacyAddress testAddressCopy = ((LegacyAddress) (new ObjectInputStream(new ByteArrayInputStream(os.toByteArray())).readObject()));
        Assert.assertEquals(testAddress, testAddressCopy);
        LegacyAddress mainAddress = LegacyAddress.fromBase58(LegacyAddressTest.MAINNET, "17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL");
        os = new ByteArrayOutputStream();
        new ObjectOutputStream(os).writeObject(mainAddress);
        LegacyAddress mainAddressCopy = ((LegacyAddress) (new ObjectInputStream(new ByteArrayInputStream(os.toByteArray())).readObject()));
        Assert.assertEquals(mainAddress, mainAddressCopy);
    }

    @Test
    public void stringification() throws Exception {
        // Test a testnet address.
        LegacyAddress a = LegacyAddress.fromPubKeyHash(LegacyAddressTest.TESTNET, Utils.HEX.decode("fda79a24e50ff70ff42f7d89585da5bd19d9e5cc"));
        Assert.assertEquals("n4eA2nbYqErp7H6jebchxAN59DmNpksexv", a.toString());
        Assert.assertEquals(P2PKH, a.getOutputScriptType());
        LegacyAddress b = LegacyAddress.fromPubKeyHash(LegacyAddressTest.MAINNET, Utils.HEX.decode("4a22c3c4cbb31e4d03b15550636762bda0baf85a"));
        Assert.assertEquals("17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL", b.toString());
        Assert.assertEquals(P2PKH, a.getOutputScriptType());
    }

    @Test
    public void decoding() throws Exception {
        LegacyAddress a = LegacyAddress.fromBase58(LegacyAddressTest.TESTNET, "n4eA2nbYqErp7H6jebchxAN59DmNpksexv");
        Assert.assertEquals("fda79a24e50ff70ff42f7d89585da5bd19d9e5cc", HEX.encode(a.getHash()));
        LegacyAddress b = LegacyAddress.fromBase58(LegacyAddressTest.MAINNET, "17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL");
        Assert.assertEquals("4a22c3c4cbb31e4d03b15550636762bda0baf85a", HEX.encode(b.getHash()));
    }

    @Test
    public void errorPaths() {
        // Check what happens if we try and decode garbage.
        try {
            LegacyAddress.fromBase58(LegacyAddressTest.TESTNET, "this is not a valid address!");
            Assert.fail();
        } catch (AddressFormatException e) {
            Assert.fail();
        } catch (AddressFormatException e) {
            // Success.
        }
        // Check the empty case.
        try {
            LegacyAddress.fromBase58(LegacyAddressTest.TESTNET, "");
            Assert.fail();
        } catch (AddressFormatException e) {
            Assert.fail();
        } catch (AddressFormatException e) {
            // Success.
        }
        // Check the case of a mismatched network.
        try {
            LegacyAddress.fromBase58(LegacyAddressTest.TESTNET, "17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL");
            Assert.fail();
        } catch (AddressFormatException e) {
            // Success.
        } catch (AddressFormatException e) {
            Assert.fail();
        }
    }

    @Test
    public void getNetwork() throws Exception {
        NetworkParameters params = LegacyAddress.getParametersFromAddress("17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL");
        Assert.assertEquals(LegacyAddressTest.MAINNET.getId(), params.getId());
        params = LegacyAddress.getParametersFromAddress("n4eA2nbYqErp7H6jebchxAN59DmNpksexv");
        Assert.assertEquals(LegacyAddressTest.TESTNET.getId(), params.getId());
    }

    @Test
    public void getAltNetwork() throws Exception {
        // An alternative network
        class AltNetwork extends MainNetParams {
            AltNetwork() {
                super();
                id = "alt.network";
                addressHeader = 48;
                p2shHeader = 5;
            }
        }
        AltNetwork altNetwork = new AltNetwork();
        // Add new network params
        Networks.register(altNetwork);
        // Check if can parse address
        NetworkParameters params = LegacyAddress.getParametersFromAddress("LLxSnHLN2CYyzB5eWTR9K9rS9uWtbTQFb6");
        Assert.assertEquals(getId(), params.getId());
        // Check if main network works as before
        params = LegacyAddress.getParametersFromAddress("17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL");
        Assert.assertEquals(LegacyAddressTest.MAINNET.getId(), params.getId());
        // Unregister network
        Networks.unregister(altNetwork);
        try {
            LegacyAddress.getParametersFromAddress("LLxSnHLN2CYyzB5eWTR9K9rS9uWtbTQFb6");
            Assert.fail();
        } catch (AddressFormatException e) {
        }
    }

    @Test
    public void p2shAddress() throws Exception {
        // Test that we can construct P2SH addresses
        LegacyAddress mainNetP2SHAddress = LegacyAddress.fromBase58(MainNetParams.get(), "35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU");
        Assert.assertEquals(mainNetP2SHAddress.getVersion(), LegacyAddressTest.MAINNET.p2shHeader);
        Assert.assertEquals(P2SH, mainNetP2SHAddress.getOutputScriptType());
        LegacyAddress testNetP2SHAddress = LegacyAddress.fromBase58(TestNet3Params.get(), "2MuVSxtfivPKJe93EC1Tb9UhJtGhsoWEHCe");
        Assert.assertEquals(testNetP2SHAddress.getVersion(), LegacyAddressTest.TESTNET.p2shHeader);
        Assert.assertEquals(P2SH, testNetP2SHAddress.getOutputScriptType());
        // Test that we can determine what network a P2SH address belongs to
        NetworkParameters mainNetParams = LegacyAddress.getParametersFromAddress("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU");
        Assert.assertEquals(LegacyAddressTest.MAINNET.getId(), mainNetParams.getId());
        NetworkParameters testNetParams = LegacyAddress.getParametersFromAddress("2MuVSxtfivPKJe93EC1Tb9UhJtGhsoWEHCe");
        Assert.assertEquals(LegacyAddressTest.TESTNET.getId(), testNetParams.getId());
        // Test that we can convert them from hashes
        byte[] hex = Utils.HEX.decode("2ac4b0b501117cc8119c5797b519538d4942e90e");
        LegacyAddress a = LegacyAddress.fromScriptHash(LegacyAddressTest.MAINNET, hex);
        Assert.assertEquals("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU", a.toString());
        LegacyAddress b = LegacyAddress.fromScriptHash(LegacyAddressTest.TESTNET, Utils.HEX.decode("18a0e827269b5211eb51a4af1b2fa69333efa722"));
        Assert.assertEquals("2MuVSxtfivPKJe93EC1Tb9UhJtGhsoWEHCe", b.toString());
        LegacyAddress c = LegacyAddress.fromScriptHash(LegacyAddressTest.MAINNET, ScriptPattern.extractHashFromP2SH(ScriptBuilder.createP2SHOutputScript(hex)));
        Assert.assertEquals("35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU", c.toString());
    }

    @Test
    public void p2shAddressCreationFromKeys() throws Exception {
        // import some keys from this example: https://gist.github.com/gavinandresen/3966071
        ECKey key1 = DumpedPrivateKey.fromBase58(LegacyAddressTest.MAINNET, "5JaTXbAUmfPYZFRwrYaALK48fN6sFJp4rHqq2QSXs8ucfpE4yQU").getKey();
        key1 = ECKey.fromPrivate(key1.getPrivKeyBytes());
        ECKey key2 = DumpedPrivateKey.fromBase58(LegacyAddressTest.MAINNET, "5Jb7fCeh1Wtm4yBBg3q3XbT6B525i17kVhy3vMC9AqfR6FH2qGk").getKey();
        key2 = ECKey.fromPrivate(key2.getPrivKeyBytes());
        ECKey key3 = DumpedPrivateKey.fromBase58(LegacyAddressTest.MAINNET, "5JFjmGo5Fww9p8gvx48qBYDJNAzR9pmH5S389axMtDyPT8ddqmw").getKey();
        key3 = ECKey.fromPrivate(key3.getPrivKeyBytes());
        List<ECKey> keys = Arrays.asList(key1, key2, key3);
        Script p2shScript = ScriptBuilder.createP2SHOutputScript(2, keys);
        LegacyAddress address = LegacyAddress.fromScriptHash(LegacyAddressTest.MAINNET, ScriptPattern.extractHashFromP2SH(p2shScript));
        Assert.assertEquals("3N25saC4dT24RphDAwLtD8LUN4E2gZPJke", address.toString());
    }

    @Test
    public void cloning() throws Exception {
        LegacyAddress a = LegacyAddress.fromPubKeyHash(LegacyAddressTest.TESTNET, Utils.HEX.decode("fda79a24e50ff70ff42f7d89585da5bd19d9e5cc"));
        LegacyAddress b = a.clone();
        Assert.assertEquals(a, b);
        Assert.assertNotSame(a, b);
    }

    @Test
    public void roundtripBase58() throws Exception {
        String base58 = "17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL";
        Assert.assertEquals(base58, LegacyAddress.fromBase58(null, base58).toBase58());
    }

    @Test
    public void comparisonCloneEqualTo() throws Exception {
        LegacyAddress a = LegacyAddress.fromBase58(LegacyAddressTest.MAINNET, "1Dorian4RoXcnBv9hnQ4Y2C1an6NJ4UrjX");
        LegacyAddress b = a.clone();
        int result = a.compareTo(b);
        Assert.assertEquals(0, result);
    }

    @Test
    public void comparisonLessThan() throws Exception {
        LegacyAddress a = LegacyAddress.fromBase58(LegacyAddressTest.MAINNET, "1Dorian4RoXcnBv9hnQ4Y2C1an6NJ4UrjX");
        LegacyAddress b = LegacyAddress.fromBase58(LegacyAddressTest.MAINNET, "1EXoDusjGwvnjZUyKkxZ4UHEf77z6A5S4P");
        int result = a.compareTo(b);
        Assert.assertTrue((result < 0));
    }

    @Test
    public void comparisonGreaterThan() throws Exception {
        LegacyAddress a = LegacyAddress.fromBase58(LegacyAddressTest.MAINNET, "1EXoDusjGwvnjZUyKkxZ4UHEf77z6A5S4P");
        LegacyAddress b = LegacyAddress.fromBase58(LegacyAddressTest.MAINNET, "1Dorian4RoXcnBv9hnQ4Y2C1an6NJ4UrjX");
        int result = a.compareTo(b);
        Assert.assertTrue((result > 0));
    }

    @Test
    public void comparisonBytesVsString() throws Exception {
        BufferedReader dataSetReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("LegacyAddressTestDataset.txt")));
        String line;
        while ((line = dataSetReader.readLine()) != null) {
            String[] addr = line.split(",");
            LegacyAddress first = LegacyAddress.fromBase58(LegacyAddressTest.MAINNET, addr[0]);
            LegacyAddress second = LegacyAddress.fromBase58(LegacyAddressTest.MAINNET, addr[1]);
            Assert.assertTrue(((first.compareTo(second)) < 0));
            Assert.assertTrue(((first.toString().compareTo(second.toString())) < 0));
        } 
    }
}

