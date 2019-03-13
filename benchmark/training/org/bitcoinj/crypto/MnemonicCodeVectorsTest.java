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


import java.util.List;
import org.bitcoinj.core.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * This is a parametrized test class to execute all {@link MnemonicCode} test vectors.
 */
@RunWith(Parameterized.class)
public class MnemonicCodeVectorsTest {
    private MnemonicCode mc;

    private String vectorEntropy;

    private String vectorMnemonicCode;

    private String vectorSeed;

    private String vectorPassphrase;

    public MnemonicCodeVectorsTest(String vectorEntropy, String vectorMnemonicCode, String vectorSeed, String vectorPassphrase) {
        this.vectorEntropy = vectorEntropy;
        this.vectorMnemonicCode = vectorMnemonicCode;
        this.vectorSeed = vectorSeed;
        this.vectorPassphrase = vectorPassphrase;
    }

    @Test
    public void testMnemonicCode() throws Exception {
        final List<String> mnemonicCode = mc.toMnemonic(Utils.HEX.decode(vectorEntropy));
        final byte[] seed = MnemonicCode.toSeed(mnemonicCode, vectorPassphrase);
        final byte[] entropy = mc.toEntropy(Utils.WHITESPACE_SPLITTER.splitToList(vectorMnemonicCode));
        Assert.assertEquals(vectorEntropy, Utils.HEX.encode(entropy));
        Assert.assertEquals(vectorMnemonicCode, Utils.SPACE_JOINER.join(mnemonicCode));
        Assert.assertEquals(vectorSeed, Utils.HEX.encode(seed));
    }
}

