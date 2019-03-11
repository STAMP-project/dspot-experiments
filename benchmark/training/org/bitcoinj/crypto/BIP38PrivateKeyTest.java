/**
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
package org.bitcoinj.crypto;


import AddressFormatException.InvalidDataLength;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.BIP38PrivateKey.BadPassphraseException;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Assert;
import org.junit.Test;


public class BIP38PrivateKeyTest {
    private static final NetworkParameters MAINNET = MainNetParams.get();

    private static final NetworkParameters TESTNET = TestNet3Params.get();

    @Test
    public void bip38testvector_noCompression_noEcMultiply_test1() throws Exception {
        BIP38PrivateKey encryptedKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.MAINNET, "6PRVWUbkzzsbcVac2qwfssoUJAN1Xhrg6bNk8J7Nzm5H7kxEbn2Nh2ZoGg");
        ECKey key = encryptedKey.decrypt("TestingOneTwoThree");
        Assert.assertEquals("5KN7MzqK5wt2TP1fQCYyHBtDrXdJuXbUzm4A9rKAteGu3Qi5CVR", key.getPrivateKeyEncoded(BIP38PrivateKeyTest.MAINNET).toString());
    }

    @Test
    public void bip38testvector_noCompression_noEcMultiply_test2() throws Exception {
        BIP38PrivateKey encryptedKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.MAINNET, "6PRNFFkZc2NZ6dJqFfhRoFNMR9Lnyj7dYGrzdgXXVMXcxoKTePPX1dWByq");
        ECKey key = encryptedKey.decrypt("Satoshi");
        Assert.assertEquals("5HtasZ6ofTHP6HCwTqTkLDuLQisYPah7aUnSKfC7h4hMUVw2gi5", key.getPrivateKeyEncoded(BIP38PrivateKeyTest.MAINNET).toString());
    }

    @Test
    public void bip38testvector_noCompression_noEcMultiply_test3() throws Exception {
        BIP38PrivateKey encryptedKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.MAINNET, "6PRW5o9FLp4gJDDVqJQKJFTpMvdsSGJxMYHtHaQBF3ooa8mwD69bapcDQn");
        StringBuilder passphrase = new StringBuilder();
        passphrase.appendCodePoint(978);// GREEK UPSILON WITH HOOK

        passphrase.appendCodePoint(769);// COMBINING ACUTE ACCENT

        passphrase.appendCodePoint(0);// NULL

        passphrase.appendCodePoint(66560);// DESERET CAPITAL LETTER LONG I

        passphrase.appendCodePoint(128169);// PILE OF POO

        ECKey key = encryptedKey.decrypt(passphrase.toString());
        Assert.assertEquals("5Jajm8eQ22H3pGWLEVCXyvND8dQZhiQhoLJNKjYXk9roUFTMSZ4", key.getPrivateKeyEncoded(BIP38PrivateKeyTest.MAINNET).toString());
    }

    @Test
    public void bip38testvector_compression_noEcMultiply_test1() throws Exception {
        BIP38PrivateKey encryptedKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.MAINNET, "6PYNKZ1EAgYgmQfmNVamxyXVWHzK5s6DGhwP4J5o44cvXdoY7sRzhtpUeo");
        ECKey key = encryptedKey.decrypt("TestingOneTwoThree");
        Assert.assertEquals("L44B5gGEpqEDRS9vVPz7QT35jcBG2r3CZwSwQ4fCewXAhAhqGVpP", key.getPrivateKeyEncoded(BIP38PrivateKeyTest.MAINNET).toString());
    }

    @Test
    public void bip38testvector_compression_noEcMultiply_test2() throws Exception {
        BIP38PrivateKey encryptedKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.MAINNET, "6PYLtMnXvfG3oJde97zRyLYFZCYizPU5T3LwgdYJz1fRhh16bU7u6PPmY7");
        ECKey key = encryptedKey.decrypt("Satoshi");
        Assert.assertEquals("KwYgW8gcxj1JWJXhPSu4Fqwzfhp5Yfi42mdYmMa4XqK7NJxXUSK7", key.getPrivateKeyEncoded(BIP38PrivateKeyTest.MAINNET).toString());
    }

    @Test
    public void bip38testvector_ecMultiply_noCompression_noLotAndSequence_test1() throws Exception {
        BIP38PrivateKey encryptedKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.MAINNET, "6PfQu77ygVyJLZjfvMLyhLMQbYnu5uguoJJ4kMCLqWwPEdfpwANVS76gTX");
        ECKey key = encryptedKey.decrypt("TestingOneTwoThree");
        Assert.assertEquals("5K4caxezwjGCGfnoPTZ8tMcJBLB7Jvyjv4xxeacadhq8nLisLR2", key.getPrivateKeyEncoded(BIP38PrivateKeyTest.MAINNET).toString());
    }

    @Test
    public void bip38testvector_ecMultiply_noCompression_noLotAndSequence_test2() throws Exception {
        BIP38PrivateKey encryptedKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.MAINNET, "6PfLGnQs6VZnrNpmVKfjotbnQuaJK4KZoPFrAjx1JMJUa1Ft8gnf5WxfKd");
        ECKey key = encryptedKey.decrypt("Satoshi");
        Assert.assertEquals("5KJ51SgxWaAYR13zd9ReMhJpwrcX47xTJh2D3fGPG9CM8vkv5sH", key.getPrivateKeyEncoded(BIP38PrivateKeyTest.MAINNET).toString());
    }

    @Test
    public void bip38testvector_ecMultiply_noCompression_lotAndSequence_test1() throws Exception {
        BIP38PrivateKey encryptedKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.MAINNET, "6PgNBNNzDkKdhkT6uJntUXwwzQV8Rr2tZcbkDcuC9DZRsS6AtHts4Ypo1j");
        ECKey key = encryptedKey.decrypt("MOLON LABE");
        Assert.assertEquals("5JLdxTtcTHcfYcmJsNVy1v2PMDx432JPoYcBTVVRHpPaxUrdtf8", key.getPrivateKeyEncoded(BIP38PrivateKeyTest.MAINNET).toString());
    }

    @Test
    public void bip38testvector_ecMultiply_noCompression_lotAndSequence_test2() throws Exception {
        BIP38PrivateKey encryptedKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.MAINNET, "6PgGWtx25kUg8QWvwuJAgorN6k9FbE25rv5dMRwu5SKMnfpfVe5mar2ngH");
        ECKey key = encryptedKey.decrypt("????? ????");
        Assert.assertEquals("5KMKKuUmAkiNbA3DazMQiLfDq47qs8MAEThm4yL8R2PhV1ov33D", key.getPrivateKeyEncoded(BIP38PrivateKeyTest.MAINNET).toString());
    }

    @Test
    public void bitcoinpaperwallet_testnet() throws Exception {
        // values taken from bitcoinpaperwallet.com
        BIP38PrivateKey encryptedKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.TESTNET, "6PRPhQhmtw6dQu6jD8E1KS4VphwJxBS9Eh9C8FQELcrwN3vPvskv9NKvuL");
        ECKey key = encryptedKey.decrypt("password");
        Assert.assertEquals("93MLfjbY6ugAsLeQfFY6zodDa8izgm1XAwA9cpMbUTwLkDitopg", key.getPrivateKeyEncoded(BIP38PrivateKeyTest.TESTNET).toString());
    }

    @Test
    public void bitaddress_testnet() throws Exception {
        // values taken from bitaddress.org
        BIP38PrivateKey encryptedKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.TESTNET, "6PfMmVHn153N3x83Yiy4Nf76dHUkXufe2Adr9Fw5bewrunGNeaw2QCpifb");
        ECKey key = encryptedKey.decrypt("password");
        Assert.assertEquals("91tCpdaGr4Khv7UAuUxa6aMqeN5GcPVJxzLtNsnZHTCndxkRcz2", key.getPrivateKeyEncoded(BIP38PrivateKeyTest.TESTNET).toString());
    }

    @Test(expected = BadPassphraseException.class)
    public void badPassphrase() throws Exception {
        BIP38PrivateKey encryptedKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.MAINNET, "6PRVWUbkzzsbcVac2qwfssoUJAN1Xhrg6bNk8J7Nzm5H7kxEbn2Nh2ZoGg");
        encryptedKey.decrypt("BAD");
    }

    @Test(expected = InvalidDataLength.class)
    public void fromBase58_invalidLength() {
        String base58 = Base58.encodeChecked(1, new byte[16]);
        BIP38PrivateKey.fromBase58(null, base58);
    }

    @Test
    public void testJavaSerialization() throws Exception {
        BIP38PrivateKey testKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.TESTNET, "6PfMmVHn153N3x83Yiy4Nf76dHUkXufe2Adr9Fw5bewrunGNeaw2QCpifb");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new ObjectOutputStream(os).writeObject(testKey);
        BIP38PrivateKey testKeyCopy = ((BIP38PrivateKey) (new ObjectInputStream(new ByteArrayInputStream(os.toByteArray())).readObject()));
        Assert.assertEquals(testKey, testKeyCopy);
        BIP38PrivateKey mainKey = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.MAINNET, "6PfMmVHn153N3x83Yiy4Nf76dHUkXufe2Adr9Fw5bewrunGNeaw2QCpifb");
        os = new ByteArrayOutputStream();
        new ObjectOutputStream(os).writeObject(mainKey);
        BIP38PrivateKey mainKeyCopy = ((BIP38PrivateKey) (new ObjectInputStream(new ByteArrayInputStream(os.toByteArray())).readObject()));
        Assert.assertEquals(mainKey, mainKeyCopy);
    }

    @Test
    public void cloning() throws Exception {
        BIP38PrivateKey a = BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.TESTNET, "6PfMmVHn153N3x83Yiy4Nf76dHUkXufe2Adr9Fw5bewrunGNeaw2QCpifb");
        // TODO: Consider overriding clone() in BIP38PrivateKey to narrow the type
        BIP38PrivateKey b = ((BIP38PrivateKey) (a.clone()));
        Assert.assertEquals(a, b);
        Assert.assertNotSame(a, b);
    }

    @Test
    public void roundtripBase58() throws Exception {
        String base58 = "6PfMmVHn153N3x83Yiy4Nf76dHUkXufe2Adr9Fw5bewrunGNeaw2QCpifb";
        Assert.assertEquals(base58, BIP38PrivateKey.fromBase58(BIP38PrivateKeyTest.MAINNET, base58).toBase58());
    }
}

