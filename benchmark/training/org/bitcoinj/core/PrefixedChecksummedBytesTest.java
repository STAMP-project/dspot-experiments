/**
 * Copyright 2014 bitcoinj project
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


import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Assert;
import org.junit.Test;


public class PrefixedChecksummedBytesTest {
    private static final NetworkParameters TESTNET = TestNet3Params.get();

    private static final NetworkParameters MAINNET = MainNetParams.get();

    private static class PrefixedChecksummedBytesToTest extends PrefixedChecksummedBytes {
        public PrefixedChecksummedBytesToTest(NetworkParameters params, byte[] bytes) {
            super(params, bytes);
        }

        @Override
        public String toString() {
            return Base58.encodeChecked(params.getAddressHeader(), bytes);
        }
    }

    @Test
    public void stringification() throws Exception {
        // Test a testnet address.
        PrefixedChecksummedBytes a = new PrefixedChecksummedBytesTest.PrefixedChecksummedBytesToTest(PrefixedChecksummedBytesTest.TESTNET, Utils.HEX.decode("fda79a24e50ff70ff42f7d89585da5bd19d9e5cc"));
        Assert.assertEquals("n4eA2nbYqErp7H6jebchxAN59DmNpksexv", a.toString());
        PrefixedChecksummedBytes b = new PrefixedChecksummedBytesTest.PrefixedChecksummedBytesToTest(PrefixedChecksummedBytesTest.MAINNET, Utils.HEX.decode("4a22c3c4cbb31e4d03b15550636762bda0baf85a"));
        Assert.assertEquals("17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL", b.toString());
    }

    @Test
    public void cloning() throws Exception {
        PrefixedChecksummedBytes a = new PrefixedChecksummedBytesTest.PrefixedChecksummedBytesToTest(PrefixedChecksummedBytesTest.TESTNET, Utils.HEX.decode("fda79a24e50ff70ff42f7d89585da5bd19d9e5cc"));
        PrefixedChecksummedBytes b = a.clone();
        Assert.assertEquals(a, b);
        Assert.assertNotSame(a, b);
    }

    @Test
    public void comparisonCloneEqualTo() throws Exception {
        PrefixedChecksummedBytes a = new PrefixedChecksummedBytesTest.PrefixedChecksummedBytesToTest(PrefixedChecksummedBytesTest.TESTNET, Utils.HEX.decode("fda79a24e50ff70ff42f7d89585da5bd19d9e5cc"));
        PrefixedChecksummedBytes b = a.clone();
        Assert.assertTrue(((a.compareTo(b)) == 0));
    }
}

