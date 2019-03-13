/**
 * Copyright 2013 Ken Sedgwick
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


import MnemonicException.MnemonicChecksumException;
import MnemonicException.MnemonicLengthException;
import MnemonicException.MnemonicWordException;
import com.google.common.collect.Lists;
import java.util.List;
import org.bitcoinj.core.Utils;
import org.junit.Test;


/**
 * Test the various guard clauses of {@link MnemonicCode}.
 *
 * See {@link MnemonicCodeVectorsTest} test vectors.
 */
public class MnemonicCodeTest {
    private MnemonicCode mc;

    @Test(expected = MnemonicLengthException.class)
    public void testBadEntropyLength() throws Exception {
        byte[] entropy = Utils.HEX.decode("7f7f7f7f7f7f7f7f7f7f7f7f7f7f");
        mc.toMnemonic(entropy);
    }

    @Test(expected = MnemonicLengthException.class)
    public void testBadLength() throws Exception {
        List<String> words = Utils.WHITESPACE_SPLITTER.splitToList("risk tiger venture dinner age assume float denial penalty hello");
        mc.check(words);
    }

    @Test(expected = MnemonicWordException.class)
    public void testBadWord() throws Exception {
        List<String> words = Utils.WHITESPACE_SPLITTER.splitToList("risk tiger venture dinner xyzzy assume float denial penalty hello game wing");
        mc.check(words);
    }

    @Test(expected = MnemonicChecksumException.class)
    public void testBadChecksum() throws Exception {
        List<String> words = Utils.WHITESPACE_SPLITTER.splitToList("bless cloud wheel regular tiny venue bird web grief security dignity zoo");
        mc.check(words);
    }

    @Test(expected = MnemonicLengthException.class)
    public void testEmptyMnemonic() throws Exception {
        List<String> words = Lists.newArrayList();
        mc.check(words);
    }

    @Test(expected = MnemonicLengthException.class)
    public void testEmptyEntropy() throws Exception {
        byte[] entropy = new byte[]{  };
        mc.toMnemonic(entropy);
    }

    @Test(expected = NullPointerException.class)
    public void testNullPassphrase() throws Exception {
        List<String> code = Utils.WHITESPACE_SPLITTER.splitToList("legal winner thank year wave sausage worth useful legal winner thank yellow");
        MnemonicCode.toSeed(code, null);
    }
}

