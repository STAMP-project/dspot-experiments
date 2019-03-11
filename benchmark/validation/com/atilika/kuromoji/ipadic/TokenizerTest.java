/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
/**
 * -*
 * Copyright ? 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the
 * License is distributed with this work in the LICENSE.md file.  You may
 * also obtain a copy of the License from
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atilika.kuromoji.ipadic;


import Tokenizer.Mode.SEARCH;
import com.atilika.kuromoji.CommonCornerCasesTest;
import com.atilika.kuromoji.TestUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;


public class TokenizerTest {
    private static Tokenizer tokenizer;

    @Test
    public void testSimpleSegmentation() {
        String input = "???????????????????????";
        String[] surfaces = new String[]{ "????", "??????", "?", "??", "??", "?", "??????", "?" };
        List<Token> tokens = TokenizerTest.tokenizer.tokenize(input);
        Assert.assertTrue(((tokens.size()) == (surfaces.length)));
        for (int i = 0; i < (tokens.size()); i++) {
            Assert.assertEquals(surfaces[i], tokens.get(i).getSurface());
        }
    }

    @Test
    public void testSimpleReadings() {
        List<Token> tokens = TokenizerTest.tokenizer.tokenize("??????????");
        Assert.assertTrue(((tokens.size()) == 6));
        Assert.assertEquals(tokens.get(0).getReading(), "??");
        Assert.assertEquals(tokens.get(1).getReading(), "?");
        Assert.assertEquals(tokens.get(2).getReading(), "??");
        Assert.assertEquals(tokens.get(3).getReading(), "??");
        Assert.assertEquals(tokens.get(4).getReading(), "??");
        Assert.assertEquals(tokens.get(5).getReading(), "?");
    }

    @Test
    public void testSimpleReading() {
        List<Token> tokens = TokenizerTest.tokenizer.tokenize("??");
        Assert.assertEquals(tokens.get(0).getReading(), "????");
    }

    @Test
    public void testSimpleBaseFormKnownWord() {
        List<Token> tokens = TokenizerTest.tokenizer.tokenize("?????????");
        Assert.assertTrue(((tokens.size()) == 6));
        Assert.assertEquals("??", tokens.get(3).getSurface());
        Assert.assertEquals("???", tokens.get(3).getBaseForm());
    }

    @Test
    public void testSimpleBaseFormUnknownWord() {
        List<Token> tokens = TokenizerTest.tokenizer.tokenize("?????????");
        Assert.assertTrue(((tokens.size()) == 2));
        Assert.assertFalse(tokens.get(0).isKnown());
        Assert.assertEquals("*", tokens.get(0).getBaseForm());
        Assert.assertTrue(tokens.get(1).isKnown());
        Assert.assertEquals("????", tokens.get(1).getBaseForm());
    }

    @Test
    public void testYabottaiCornerCase() {
        List<Token> tokens = TokenizerTest.tokenizer.tokenize("?????");
        Assert.assertEquals(1, tokens.size());
        Assert.assertEquals("?????", tokens.get(0).getSurface());
    }

    @Test
    public void testTsukitoshaCornerCase() {
        List<Token> tokens = TokenizerTest.tokenizer.tokenize("?????");
        Assert.assertEquals(1, tokens.size());
        Assert.assertEquals("?????", tokens.get(0).getSurface());
    }

    @Test
    public void testIpadicTokenAPIs() throws Exception {
        List<Token> tokens = TokenizerTest.tokenizer.tokenize("?????????");
        String[] pronunciations = new String[]{ "?", "??", "?", "??", "??", "?" };
        Assert.assertEquals(pronunciations.length, tokens.size());
        for (int i = 0; i < (tokens.size()); i++) {
            Assert.assertEquals(pronunciations[i], tokens.get(i).getPronunciation());
        }
        String[] conjugationForms = new String[]{ "*", "*", "*", "???", "???", "*" };
        for (int i = 0; i < (tokens.size()); i++) {
            Assert.assertEquals(conjugationForms[i], tokens.get(i).getConjugationForm());
        }
        String[] conjugationTypes = new String[]{ "*", "*", "*", "??", "?????", "*" };
        for (int i = 0; i < (tokens.size()); i++) {
            Assert.assertEquals(conjugationTypes[i], tokens.get(i).getConjugationType());
        }
        String[] posLevel1 = new String[]{ "???", "??", "??", "??", "???", "??" };
        for (int i = 0; i < (tokens.size()); i++) {
            Assert.assertEquals(posLevel1[i], tokens.get(i).getPartOfSpeechLevel1());
        }
        String[] posLevel2 = new String[]{ "????", "??", "???", "??", "*", "??" };
        for (int i = 0; i < (tokens.size()); i++) {
            Assert.assertEquals(posLevel2[i], tokens.get(i).getPartOfSpeechLevel2());
        }
        String[] posLevel3 = new String[]{ "*", "*", "??", "*", "*", "*" };
        for (int i = 0; i < (tokens.size()); i++) {
            Assert.assertEquals(posLevel3[i], tokens.get(i).getPartOfSpeechLevel3());
        }
        String[] posLevel4 = new String[]{ "*", "*", "*", "*", "*", "*" };
        for (int i = 0; i < (tokens.size()); i++) {
            Assert.assertEquals(posLevel4[i], tokens.get(i).getPartOfSpeechLevel4());
        }
    }

    @Test
    public void testCustomPenalties() {
        String input = "?????????????????????";
        Tokenizer customTokenizer = new Tokenizer.Builder().mode(SEARCH).kanjiPenalty(3, 10000).otherPenalty(Integer.MAX_VALUE, 0).build();
        String[] expected1 = new String[]{ "??????????????", "?", "??", "?", "?", "??" };
        TestUtils.assertTokenSurfacesEquals(Arrays.asList(expected1), customTokenizer.tokenize(input));
        Tokenizer searchTokenizer = new Tokenizer.Builder().mode(SEARCH).build();
        String[] expected2 = new String[]{ "???", "??????", "?????", "?", "??", "?", "?", "??" };
        TestUtils.assertTokenSurfacesEquals(Arrays.asList(expected2), searchTokenizer.tokenize(input));
    }

    @Test
    public void testNakaguroSplit() {
        Tokenizer defaultTokenizer = new Tokenizer();
        Tokenizer nakakuroSplittingTokenizer = new Tokenizer.Builder().isSplitOnNakaguro(true).build();
        String input = "????????????????";
        TestUtils.assertTokenSurfacesEquals(Arrays.asList("?????????", "?", "??", "?", "??", "?"), defaultTokenizer.tokenize(input));
        TestUtils.assertTokenSurfacesEquals(Arrays.asList("??", "?", "??????", "?", "??", "?", "??", "?"), nakakuroSplittingTokenizer.tokenize(input));
    }

    @Test
    public void testAllFeatures() {
        Tokenizer tokenizer = new Tokenizer();
        String input = "??????????";
        List<Token> tokens = tokenizer.tokenize(input);
        Assert.assertEquals("\u5bff\u53f8\t\u540d\u8a5e,\u4e00\u822c,*,*,*,*,\u5bff\u53f8,\u30b9\u30b7,\u30b9\u30b7", toString(tokens.get(0)));
        Assert.assertEquals("\u304c\t\u52a9\u8a5e,\u683c\u52a9\u8a5e,\u4e00\u822c,*,*,*,\u304c,\u30ac,\u30ac", toString(tokens.get(1)));
        Assert.assertEquals("\u98df\u3079\t\u52d5\u8a5e,\u81ea\u7acb,*,*,\u4e00\u6bb5,\u9023\u7528\u5f62,\u98df\u3079\u308b,\u30bf\u30d9,\u30bf\u30d9", toString(tokens.get(2)));
        Assert.assertEquals("\u305f\u3044\t\u52a9\u52d5\u8a5e,*,*,*,\u7279\u6b8a\u30fb\u30bf\u30a4,\u57fa\u672c\u5f62,\u305f\u3044,\u30bf\u30a4,\u30bf\u30a4", toString(tokens.get(3)));
        Assert.assertEquals("\u3067\u3059\t\u52a9\u52d5\u8a5e,*,*,*,\u7279\u6b8a\u30fb\u30c7\u30b9,\u57fa\u672c\u5f62,\u3067\u3059,\u30c7\u30b9,\u30c7\u30b9", toString(tokens.get(4)));
    }

    @Test
    public void testCompactedTrieCrash() {
        String input = "??";
        Tokenizer tokenizer = new Tokenizer();
        TestUtils.assertTokenSurfacesEquals(Arrays.asList("?", "?"), tokenizer.tokenize(input));
    }

    @Test
    public void testFeatureLengths() throws IOException {
        String userDictionary = "" + "gsf,gsf,\u30b8\u30fc\u30a8\u30b9\u30fc\u30a8\u30d5,\u30ab\u30b9\u30bf\u30e0\u540d\u8a5e\n";
        Tokenizer tokenizer = new Tokenizer.Builder().userDictionary(new ByteArrayInputStream(userDictionary.getBytes(StandardCharsets.UTF_8))).build();
        TestUtils.assertEqualTokenFeatureLengths("ahgsfdajhgsfd??????????????????", tokenizer);
    }

    @Test
    public void testNewBocchan() throws IOException {
        TestUtils.assertTokenizedStreamEquals(new ClassPathResource("deeplearning4j-nlp-japanese/bocchan-ipadic-features.txt").getInputStream(), new ClassPathResource("deeplearning4j-nlp-japanese/bocchan.txt").getInputStream(), TokenizerTest.tokenizer);
    }

    @Test
    public void testPunctuation() {
        CommonCornerCasesTest.testPunctuation(new Tokenizer());
    }
}

