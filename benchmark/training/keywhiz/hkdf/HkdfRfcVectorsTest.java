/**
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package keywhiz.hkdf;


import com.google.common.io.BaseEncoding;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * See <a href="http://tools.ietf.org/html/rfc5869">RFC-5869</a> for standard test vectors.
 */
@RunWith(Parameterized.class)
public class HkdfRfcVectorsTest {
    private static final BaseEncoding HEX = BaseEncoding.base16().lowerCase();

    private final Hash hash;

    private final HkdfRfcVectorsTest.PossibleBytes salt;

    private final String ikm;

    private final HkdfRfcVectorsTest.PossibleBytes info;

    private final int outputLen;

    private final String expectedPrk;

    private final String expectedOkm;

    public HkdfRfcVectorsTest(@SuppressWarnings("unused")
    String name, Hash hash, HkdfRfcVectorsTest.PossibleBytes salt, String ikm, HkdfRfcVectorsTest.PossibleBytes info, int outputLen, String expectedPrk, String expectedOkm) {
        this.hash = hash;
        this.salt = salt;
        this.ikm = ikm;
        this.info = info;
        this.outputLen = outputLen;
        this.expectedPrk = expectedPrk;
        this.expectedOkm = expectedOkm;
    }

    @Test
    public void testVector() throws Exception {
        Hkdf hkdf = Hkdf.usingHash(hash);
        SecretKey saltKey = (salt.isNull()) ? null : new SecretKeySpec(salt.get(), hash.getAlgorithm());
        SecretKey prk = hkdf.extract(saltKey, HkdfRfcVectorsTest.HEX.decode(ikm));
        byte[] okm = hkdf.expand(prk, info.get(), outputLen);
        assertThat(prk.getEncoded()).containsExactly(HkdfRfcVectorsTest.HEX.decode(expectedPrk));
        assertThat(okm).containsExactly(HkdfRfcVectorsTest.HEX.decode(expectedOkm));
    }

    private static class PossibleBytes {
        public static final HkdfRfcVectorsTest.PossibleBytes NULL = new HkdfRfcVectorsTest.PossibleBytes(null);

        public static final HkdfRfcVectorsTest.PossibleBytes EMPTY = new HkdfRfcVectorsTest.PossibleBytes(new byte[0]);

        public static HkdfRfcVectorsTest.PossibleBytes of(String hex) {
            return new HkdfRfcVectorsTest.PossibleBytes(HkdfRfcVectorsTest.HEX.decode(hex));
        }

        private final byte[] bytes;

        private PossibleBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public byte[] get() {
            return bytes;
        }

        public boolean isNull() {
            return (bytes) == null;
        }
    }
}

