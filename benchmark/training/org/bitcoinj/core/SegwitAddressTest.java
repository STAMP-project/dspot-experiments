/**
 * Copyright by the original author or authors.
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


import AddressFormatException.InvalidDataLength;
import AddressFormatException.InvalidPrefix;
import AddressFormatException.WrongNetwork;
import ScriptType.P2WPKH;
import ScriptType.P2WSH;
import Utils.HEX;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.junit.Assert;
import org.junit.Test;

import static com.google.common.base.MoreObjects.toStringHelper;


public class SegwitAddressTest {
    private static final MainNetParams MAINNET = MainNetParams.get();

    private static final TestNet3Params TESTNET = TestNet3Params.get();

    @Test
    public void example_p2wpkh_mainnet() {
        String bech32 = "bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4";
        SegwitAddress address = SegwitAddress.fromBech32(SegwitAddressTest.MAINNET, bech32);
        Assert.assertEquals(SegwitAddressTest.MAINNET, address.params);
        Assert.assertEquals("0014751e76e8199196d454941c45d1b3a323f1433bd6", HEX.encode(ScriptBuilder.createOutputScript(address).getProgram()));
        Assert.assertEquals(P2WPKH, address.getOutputScriptType());
        Assert.assertEquals(bech32.toLowerCase(Locale.ROOT), address.toBech32());
        Assert.assertEquals(bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void example_p2wsh_mainnet() {
        String bech32 = "bc1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3qccfmv3";
        SegwitAddress address = SegwitAddress.fromBech32(SegwitAddressTest.MAINNET, bech32);
        Assert.assertEquals(SegwitAddressTest.MAINNET, address.params);
        Assert.assertEquals("00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262", HEX.encode(ScriptBuilder.createOutputScript(address).getProgram()));
        Assert.assertEquals(P2WSH, address.getOutputScriptType());
        Assert.assertEquals(bech32.toLowerCase(Locale.ROOT), address.toBech32());
        Assert.assertEquals(bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void example_p2wpkh_testnet() {
        String bech32 = "tb1qw508d6qejxtdg4y5r3zarvary0c5xw7kxpjzsx";
        SegwitAddress address = SegwitAddress.fromBech32(SegwitAddressTest.TESTNET, bech32);
        Assert.assertEquals(SegwitAddressTest.TESTNET, address.params);
        Assert.assertEquals("0014751e76e8199196d454941c45d1b3a323f1433bd6", HEX.encode(ScriptBuilder.createOutputScript(address).getProgram()));
        Assert.assertEquals(P2WPKH, address.getOutputScriptType());
        Assert.assertEquals(bech32.toLowerCase(Locale.ROOT), address.toBech32());
        Assert.assertEquals(bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void example_p2wsh_testnet() {
        String bech32 = "tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sl5k7";
        SegwitAddress address = SegwitAddress.fromBech32(SegwitAddressTest.TESTNET, bech32);
        Assert.assertEquals(SegwitAddressTest.TESTNET, address.params);
        Assert.assertEquals("00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262", HEX.encode(ScriptBuilder.createOutputScript(address).getProgram()));
        Assert.assertEquals(P2WSH, address.getOutputScriptType());
        Assert.assertEquals(bech32.toLowerCase(Locale.ROOT), address.toBech32());
        Assert.assertEquals(bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void validAddresses() {
        for (SegwitAddressTest.AddressData valid : SegwitAddressTest.VALID_ADDRESSES) {
            SegwitAddress address = SegwitAddress.fromBech32(null, valid.address);
            Assert.assertEquals(valid.expectedParams, address.params);
            Assert.assertEquals(valid.expectedScriptPubKey, HEX.encode(ScriptBuilder.createOutputScript(address).getProgram()));
            Assert.assertEquals(valid.address.toLowerCase(Locale.ROOT), address.toBech32());
            if ((valid.expectedWitnessVersion) == 0) {
                Script expectedScriptPubKey = new Script(HEX.decode(valid.expectedScriptPubKey));
                Assert.assertEquals(address, SegwitAddress.fromHash(valid.expectedParams, ScriptPattern.extractHashFromP2WH(expectedScriptPubKey)));
            }
            Assert.assertEquals(valid.expectedWitnessVersion, address.getWitnessVersion());
        }
    }

    private static class AddressData {
        final String address;

        final NetworkParameters expectedParams;

        final String expectedScriptPubKey;

        final int expectedWitnessVersion;

        AddressData(String address, NetworkParameters expectedParams, String expectedScriptPubKey, int expectedWitnessVersion) {
            this.address = address;
            this.expectedParams = expectedParams;
            this.expectedScriptPubKey = expectedScriptPubKey;
            this.expectedWitnessVersion = expectedWitnessVersion;
        }

        @Override
        public String toString() {
            return toStringHelper(this).add("address", address).add("params", expectedParams.getId()).add("scriptPubKey", expectedScriptPubKey).add("witnessVersion", expectedWitnessVersion).toString();
        }
    }

    private static SegwitAddressTest.AddressData[] VALID_ADDRESSES = new SegwitAddressTest.AddressData[]{ new SegwitAddressTest.AddressData("BC1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7KV8F3T4", SegwitAddressTest.MAINNET, "0014751e76e8199196d454941c45d1b3a323f1433bd6", 0), new SegwitAddressTest.AddressData("tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sl5k7", SegwitAddressTest.TESTNET, "00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262", 0), new SegwitAddressTest.AddressData("bc1pw508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7k7grplx", SegwitAddressTest.MAINNET, "5128751e76e8199196d454941c45d1b3a323f1433bd6751e76e8199196d454941c45d1b3a323f1433bd6", 1), new SegwitAddressTest.AddressData("BC1SW50QA3JX3S", SegwitAddressTest.MAINNET, "6002751e", 16), new SegwitAddressTest.AddressData("bc1zw508d6qejxtdg4y5r3zarvaryvg6kdaj", SegwitAddressTest.MAINNET, "5210751e76e8199196d454941c45d1b3a323", 2), new SegwitAddressTest.AddressData("tb1qqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsesrxh6hy", SegwitAddressTest.TESTNET, "0020000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433", 0) };

    @Test
    public void invalidAddresses() {
        for (String invalid : SegwitAddressTest.INVALID_ADDRESSES) {
            try {
                SegwitAddress.fromBech32(null, invalid);
                Assert.fail(invalid);
            } catch (AddressFormatException x) {
                // expected
            }
        }
    }

    private static String[] INVALID_ADDRESSES = new String[]{ // 
    "tc1qw508d6qejxtdg4y5r3zarvary0c5xw7kg3g4ty"// Invalid human-readable part
    , "bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t5"// Invalid checksum
    , "BC13W508D6QEJXTDG4Y5R3ZARVARY0C5XW7KN40WF2"// Invalid witness version
    , "bc1rw5uspcuh"// Invalid program length
    , "bc10w508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7kw5rljs90"// Invalid program length
    , "BC1QR508D6QEJXTDG4Y5R3ZARVARYV98GJ9P"// Invalid program length for witness version 0 (per BIP141)
    , "tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sL5k7"// Mixed case
    , "bc1zw508d6qejxtdg4y5r3zarvaryvqyzf3du"// Zero padding of more than 4 bits
    , "tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3pjxtptv"// Non-zero padding in 8-to-5 conversion
    , "bc1gmk9yu"// Empty data section
     };

    @Test(expected = InvalidDataLength.class)
    public void fromBech32_version0_invalidLength() {
        SegwitAddress.fromBech32(null, "BC1QR508D6QEJXTDG4Y5R3ZARVARYV98GJ9P");
    }

    @Test(expected = InvalidDataLength.class)
    public void fromBech32_tooShort() {
        SegwitAddress.fromBech32(null, "bc1rw5uspcuh");
    }

    @Test(expected = InvalidDataLength.class)
    public void fromBech32_tooLong() {
        SegwitAddress.fromBech32(null, "bc10w508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7kw5rljs90");
    }

    @Test(expected = InvalidPrefix.class)
    public void fromBech32_invalidHrp() {
        SegwitAddress.fromBech32(null, "tc1qw508d6qejxtdg4y5r3zarvary0c5xw7kg3g4ty");
    }

    @Test(expected = WrongNetwork.class)
    public void fromBech32_wrongNetwork() {
        SegwitAddress.fromBech32(SegwitAddressTest.TESTNET, "bc1zw508d6qejxtdg4y5r3zarvaryvg6kdaj");
    }

    @Test
    public void testJavaSerialization() throws Exception {
        SegwitAddress address = SegwitAddress.fromBech32(null, "BC1SW50QA3JX3S");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new ObjectOutputStream(os).writeObject(address);
        PrefixedChecksummedBytes addressCopy = ((PrefixedChecksummedBytes) (new ObjectInputStream(new ByteArrayInputStream(os.toByteArray())).readObject()));
        Assert.assertEquals(address, addressCopy);
        Assert.assertEquals(address.params, addressCopy.params);
        Assert.assertArrayEquals(address.bytes, addressCopy.bytes);
    }
}

