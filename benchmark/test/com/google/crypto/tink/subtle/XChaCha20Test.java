/**
 * Copyright 2018 Google Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.google.crypto.tink.subtle;


import com.google.crypto.tink.TestUtil;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link XChaCha20}
 */
@RunWith(JUnit4.class)
public class XChaCha20Test {
    @Test
    public void testEncryptDecrypt() throws Exception {
        for (int i = 0; i < 64; i++) {
            byte[] key = Random.randBytes(32);
            IndCpaCipher cipher = createInstance(key);
            for (int j = 0; j < 64; j++) {
                byte[] expectedInput = Random.randBytes(new java.util.Random().nextInt(300));
                byte[] output = cipher.encrypt(expectedInput);
                byte[] actualInput = cipher.decrypt(output);
                Assert.assertArrayEquals(String.format("\n\nMessage: %s\nKey: %s\nOutput: %s\nDecrypted Msg: %s\n", TestUtil.hexEncode(expectedInput), TestUtil.hexEncode(key), TestUtil.hexEncode(output), TestUtil.hexEncode(actualInput)), expectedInput, actualInput);
            }
        }
    }

    @Test
    public void testNewCipherThrowsIllegalArgExpWhenKeyLenIsLessThan32() throws Exception {
        try {
            createInstance(new byte[1]);
            Assert.fail("Expected InvalidKeyException.");
        } catch (InvalidKeyException e) {
            assertThat(e).hasMessageThat().containsMatch("The key length in bytes must be 32.");
        }
    }

    @Test
    public void testNewCipherThrowsIllegalArgExpWhenKeyLenIsGreaterThan32() throws Exception {
        try {
            createInstance(new byte[33]);
            Assert.fail("Expected InvalidKeyException.");
        } catch (InvalidKeyException e) {
            assertThat(e).hasMessageThat().containsMatch("The key length in bytes must be 32.");
        }
    }

    @Test
    public void testDecryptThrowsGeneralSecurityExpWhenCiphertextIsTooShort() throws Exception {
        try {
            IndCpaCipher cipher = createInstance(Random.randBytes(32));
            cipher.decrypt(new byte[2]);
            Assert.fail("Expected GeneralSecurityException.");
        } catch (GeneralSecurityException e) {
            assertThat(e).hasMessageThat().containsMatch("ciphertext too short");
        }
    }

    private static class HChaCha20TestVector {
        public int[] key;

        public int[] in;

        public int[] out;

        public HChaCha20TestVector(String key, String in, String out) {
            this.key = ChaCha20.toIntArray(Hex.decode(key));
            this.in = ChaCha20.toIntArray(Hex.decode(in));
            this.out = ChaCha20.toIntArray(Hex.decode(out));
        }
    }

    private static final XChaCha20Test.HChaCha20TestVector[] hChaCha20TestVectors = new XChaCha20Test.HChaCha20TestVector[]{ // From libsodium's test/default/xchacha20.c (tv_hchacha20).
    new XChaCha20Test.HChaCha20TestVector("24f11cce8a1b3d61e441561a696c1c1b7e173d084fd4812425435a8896a013dc", "d9660c5900ae19ddad28d6e06e45fe5e", "5966b3eec3bff1189f831f06afe4d4e3be97fa9235ec8c20d08acfbbb4e851e3"), new XChaCha20Test.HChaCha20TestVector("80a5f6272031e18bb9bcd84f3385da65e7731b7039f13f5e3d475364cd4d42f7", "c0eccc384b44c88e92c57eb2d5ca4dfa", "6ed11741f724009a640a44fce7320954c46e18e0d7ae063bdbc8d7cf372709df"), new XChaCha20Test.HChaCha20TestVector("cb1fc686c0eec11a89438b6f4013bf110e7171dace3297f3a657a309b3199629", "fcd49b93e5f8f299227e64d40dc864a3", "84b7e96937a1a0a406bb7162eeaad34308d49de60fd2f7ec9dc6a79cbab2ca34"), new XChaCha20Test.HChaCha20TestVector("6640f4d80af5496ca1bc2cfff1fefbe99638dbceaabd7d0ade118999d45f053d", "31f59ceeeafdbfe8cae7914caeba90d6", "9af4697d2f5574a44834a2c2ae1a0505af9f5d869dbe381a994a18eb374c36a0"), new XChaCha20Test.HChaCha20TestVector("0693ff36d971225a44ac92c092c60b399e672e4cc5aafd5e31426f123787ac27", "3a6293da061da405db45be1731d5fc4d", "f87b38609142c01095bfc425573bb3c698f9ae866b7e4216840b9c4caf3b0865"), new XChaCha20Test.HChaCha20TestVector("809539bd2639a23bf83578700f055f313561c7785a4a19fc9114086915eee551", "780c65d6a3318e479c02141d3f0b3918", "902ea8ce4680c09395ce71874d242f84274243a156938aaa2dd37ac5be382b42"), new XChaCha20Test.HChaCha20TestVector("1a170ddf25a4fd69b648926e6d794e73408805835c64b2c70efddd8cd1c56ce0", "05dbee10de87eb0c5acb2b66ebbe67d3", "a4e20b634c77d7db908d387b48ec2b370059db916e8ea7716dc07238532d5981"), new XChaCha20Test.HChaCha20TestVector("3b354e4bb69b5b4a1126f509e84cad49f18c9f5f29f0be0c821316a6986e15a6", "d8a89af02f4b8b2901d8321796388b6c", "9816cb1a5b61993735a4b161b51ed2265b696e7ded5309c229a5a99f53534fbc"), new XChaCha20Test.HChaCha20TestVector("4b9a818892e15a530db50dd2832e95ee192e5ed6afffb408bd624a0c4e12a081", "a9079c551de70501be0286d1bc78b045", "ebc5224cf41ea97473683b6c2f38a084bf6e1feaaeff62676db59d5b719d999b"), new XChaCha20Test.HChaCha20TestVector("c49758f00003714c38f1d4972bde57ee8271f543b91e07ebce56b554eb7fa6a7", "31f0204e10cf4f2035f9e62bb5ba7303", "0dd8cc400f702d2c06ed920be52048a287076b86480ae273c6d568a2e9e7518c"), // From https://tools.ietf.org/html/draft-arciszewski-xchacha-01#section-2.2.1.
    new XChaCha20Test.HChaCha20TestVector("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f", "000000090000004a0000000031415927", "82413b4227b27bfed30e42508a877d73a0f9e4d58a74a853c12ec41326d3ecdc") };

    @Test
    public void testHChaCha20TestVectors() {
        for (XChaCha20Test.HChaCha20TestVector test : XChaCha20Test.hChaCha20TestVectors) {
            int[] output = XChaCha20.hChaCha20(test.key, test.in);
            assertThat(output).isEqualTo(test.out);
        }
    }

    private static class XChaCha20TestVector {
        public byte[] key;

        public byte[] nonce;

        public byte[] ciphertext;

        public byte[] plaintext;

        public XChaCha20TestVector(String key, String nonce, String ciphertext, String plaintext) {
            this.key = Hex.decode(key);
            this.nonce = Hex.decode(nonce);
            this.ciphertext = Hex.decode(ciphertext);
            this.plaintext = Hex.decode(plaintext);
            if ((plaintext.length()) == 0) {
                this.plaintext = new byte[this.ciphertext.length];
            }
        }
    }

    // From libsodium's test/default/xchacha20.c (tv_stream_xchacha20)
    private static final XChaCha20Test.XChaCha20TestVector[] xChaCha20TestVectors = new XChaCha20Test.XChaCha20TestVector[]{ new XChaCha20Test.XChaCha20TestVector("79c99798ac67300bbb2704c95c341e3245f3dcb21761b98e52ff45b24f304fc4", "b33ffd3096479bcfbc9aee49417688a0a2554f8d95389419", "c6e9758160083ac604ef90e712ce6e75d7797590744e0cf060f013739c", ""), new XChaCha20Test.XChaCha20TestVector("ddf7784fee099612c40700862189d0397fcc4cc4b3cc02b5456b3a97d1186173", "a9a04491e7bf00c3ca91ac7c2d38a777d88993a7047dfcc4", "2f289d371f6f0abc3cb60d11d9b7b29adf6bc5ad843e8493e928448d", ""), new XChaCha20Test.XChaCha20TestVector("3d12800e7b014e88d68a73f0a95b04b435719936feba60473f02a9e61ae60682", "56bed2599eac99fb27ebf4ffcb770a64772dec4d5849ea2d", "a2c3c1406f33c054a92760a8e0666b84f84fa3a618f0", ""), new XChaCha20Test.XChaCha20TestVector("5f5763ff9a30c95da5c9f2a8dfd7cc6efd9dfb431812c075aa3e4f32e04f53e4", "a5fa890efa3b9a034d377926ce0e08ee6d7faccaee41b771", ("8a1a5ba898bdbcff602b1036e469a18a5e45789d0e8d9837d81a2388a52b0b6a0f51891528f424c4a7f492" + "a8dd7bce8bac19fbdbe1fb379ac0"), ""), new XChaCha20Test.XChaCha20TestVector("eadc0e27f77113b5241f8ca9d6f9a5e7f09eee68d8a5cf30700563bf01060b4e", "a171a4ef3fde7c4794c5b86170dc5a099b478f1b852f7b64", ("23839f61795c3cdbcee2c749a92543baeeea3cbb721402aa42e6cae140447575f2916c5d71108e3b13357e" + "af86f060cb"), ""), new XChaCha20Test.XChaCha20TestVector("91319c9545c7c804ba6b712e22294c386fe31c4ff3d278827637b959d3dbaab2", "410e854b2a911f174aaf1a56540fc3855851f41c65967a4e", "cbe7d24177119b7fdfa8b06ee04dade4256ba7d35ffda6b89f014e479faef6", ""), new XChaCha20Test.XChaCha20TestVector("6a6d3f412fc86c4450fc31f89f64ed46baa3256ffcf8616e8c23a06c422842b6", "6b7773fce3c2546a5db4829f53a9165f41b08faae2fb72d5", "8b23e35b3cdd5f3f75525fc37960ec2b68918e8c046d8a832b9838f1546be662e54feb1203e2", ""), new XChaCha20Test.XChaCha20TestVector("d45e56368ebc7ba9be7c55cfd2da0feb633c1d86cab67cd5627514fd20c2b391", "fd37da2db31e0c738754463edadc7dafb0833bd45da497fc", ("47950efa8217e3dec437454bd6b6a80a287e2570f0a48b3fa1ea3eb868be3d486f6516606d85e5643becc4" + "73b370871ab9ef8e2a728f73b92bd98e6e26ea7c8ff96ec5a9e8de95e1eee9300c"), ""), new XChaCha20Test.XChaCha20TestVector("aface41a64a9a40cbc604d42bd363523bd762eb717f3e08fe2e0b4611eb4dcf3", "6906e0383b895ab9f1cf3803f42f27c79ad47b681c552c63", ("a5fa7c0190792ee17675d52ad7570f1fb0892239c76d6e802c26b5b3544d13151e67513b8aaa1ac5af2d7f" + "d0d5e4216964324838"), ""), new XChaCha20Test.XChaCha20TestVector("9d23bd4149cb979ccf3c5c94dd217e9808cb0e50cd0f67812235eaaf601d6232", "c047548266b7c370d33566a2425cbf30d82d1eaf5294109e", ("a21209096594de8c5667b1d13ad93f744106d054df210e4782cd396fec692d3515a20bf351eec011a92c36" + ("7888bc464c32f0807acd6c203a247e0db854148468e9f96bee4cf718d68d5f637cbd5a376457788e" + "6fae90fc31097cfc")), ""), // https://tools.ietf.org/html/draft-arciszewski-xchacha-00.
    new XChaCha20Test.XChaCha20TestVector("808182838485868788898a8b8c8d8e8f909192939495969798999a9b9c9d9e9f", "404142434445464748494a4b4c4d4e4f5051525354555658", ("4559abba4e48c16102e8bb2c05e6947f50a786de162f9b0b7e592a9b53d0d4e98d8d6410d540a1a6375b26" + (((((("d80dace4fab52384c731acbf16a5923c0c48d3575d4d0d2c673b666faa731061277701093a6bf7a15" + "8a8864292a41c48e3a9b4c0daece0f8d98d0d7e05b37a307bbb66333164ec9e1b24ea0d6c3ffddcec") + "4f68e7443056193a03c810e11344ca06d8ed8a2bfb1e8d48cfa6bc0eb4e2464b748142407c9f431ae") + "e769960e15ba8b96890466ef2457599852385c661f752ce20f9da0c09ab6b19df74e76a95967446f8") + "d0fd415e7bee2a12a114c20eb5292ae7a349ae577820d5520a1f3fb62a17ce6a7e68fa7c79111d886") + "0920bc048ef43fe84486ccb87c25f0ae045f0cce1e7989a9aa220a28bdd4827e751a24a6d5c62d790") + "a66393b93111c1a55dd7421a10184974c7c5")), ("5468652064686f6c65202870726f6e6f756e6365642022646f6c65222920697320616c736f206b6e6f776e2" + (((((("061732074686520417369617469632077696c6420646f672c2072656420646f672c20616e64207768" + "6973746c696e6720646f672e2049742069732061626f7574207468652073697a65206f66206120476") + "5726d616e20736865706865726420627574206c6f6f6b73206d6f7265206c696b652061206c6f6e67") + "2d6c656767656420666f782e205468697320686967686c7920656c757369766520616e6420736b696") + "c6c6564206a756d70657220697320636c6173736966696564207769746820776f6c7665732c20636f") + "796f7465732c206a61636b616c732c20616e6420666f78657320696e20746865207461786f6e6f6d6") + "9632066616d696c792043616e696461652e"))) };

    @Test
    public void testXChaCha20TestVectors() throws Exception {
        for (XChaCha20Test.XChaCha20TestVector test : XChaCha20Test.xChaCha20TestVectors) {
            IndCpaCipher cipher = /* initialCounter */
            new XChaCha20(test.key, 0);
            byte[] message = cipher.decrypt(Bytes.concat(test.nonce, test.ciphertext));
            assertThat(message).isEqualTo(test.plaintext);
        }
    }
}

