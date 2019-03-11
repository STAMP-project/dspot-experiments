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
package org.deeplearning4j.text.tokenization.tokenizer;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.LowCasePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.BertWordPieceTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BertWordPieceTokenizerTests {
    protected static final Logger log = LoggerFactory.getLogger(BertWordPieceTokenizerTests.class);

    private File pathToVocab = new ClassPathResource("other/vocab.txt").getFile();

    public BertWordPieceTokenizerTests() throws IOException {
    }

    @Test
    public void testBertWordPieceTokenizer1() throws Exception {
        String toTokenize = "I saw a girl with a telescope.";
        TokenizerFactory t = new BertWordPieceTokenizerFactory(pathToVocab);
        Tokenizer tokenizer = t.create(toTokenize);
        Tokenizer tokenizer2 = t.create(new ByteArrayInputStream(toTokenize.getBytes()));
        int position = 1;
        while (tokenizer2.hasMoreTokens()) {
            String tok1 = tokenizer.nextToken();
            String tok2 = tokenizer2.nextToken();
            BertWordPieceTokenizerTests.log.info((((((("Position: [" + position) + "], token1: '") + tok1) + "', token 2: '") + tok2) + "'"));
            position++;
            Assert.assertEquals(tok1, tok2);
        } 
    }

    @Test
    public void testBertWordPieceTokenizer2() throws Exception {
        TokenizerFactory t = new BertWordPieceTokenizerFactory(pathToVocab);
        ClassPathResource resource = new ClassPathResource("reuters/5250");
        String str = FileUtils.readFileToString(resource.getFile());
        int stringCount = t.create(str).countTokens();
        int stringCount2 = t.create(resource.getInputStream()).countTokens();
        Assert.assertTrue(((Math.abs((stringCount - stringCount2))) < 2));
    }

    @Test
    public void testBertWordPieceTokenizer3() throws Exception {
        String toTokenize = "Donaudampfschifffahrtskapit?nsm?tzeninnenfuttersaum";
        TokenizerFactory t = new BertWordPieceTokenizerFactory(pathToVocab);
        Tokenizer tokenizer = t.create(toTokenize);
        Tokenizer tokenizer2 = t.create(new ByteArrayInputStream(toTokenize.getBytes()));
        final List<String> expected = Arrays.asList("Donau", "##dam", "##pf", "##schiff", "##fahrt", "##skap", "##it?", "##ns", "##m", "##?tzen", "##innen", "##fu", "##tter", "##sa", "##um");
        Assert.assertEquals(expected, tokenizer.getTokens());
        Assert.assertEquals(expected, tokenizer2.getTokens());
    }

    @Test
    public void testBertWordPieceTokenizer4() throws Exception {
        String toTokenize = "I saw a girl with a telescope.";
        TokenizerFactory t = new BertWordPieceTokenizerFactory(pathToVocab);
        Tokenizer tokenizer = t.create(toTokenize);
        Tokenizer tokenizer2 = t.create(new ByteArrayInputStream(toTokenize.getBytes()));
        final List<String> expected = Arrays.asList("I", "saw", "a", "girl", "with", "a", "tele", "##scope", ".");
        Assert.assertEquals(expected, tokenizer.getTokens());
        Assert.assertEquals(expected, tokenizer2.getTokens());
    }

    @Test
    public void testBertWordPieceTokenizer5() throws Exception {
        // Longest Token in Vocab is 22 chars long, so make sure splits on the edge are properly handled
        String toTokenize = "Donaudampfschifffahrts Kapit?nsm?tzeninnenfuttersaum";
        TokenizerFactory t = new BertWordPieceTokenizerFactory(pathToVocab);
        Tokenizer tokenizer = t.create(toTokenize);
        Tokenizer tokenizer2 = t.create(new ByteArrayInputStream(toTokenize.getBytes()));
        final List<String> expected = Arrays.asList("Donau", "##dam", "##pf", "##schiff", "##fahrt", "##s", "Kapit?n", "##sm", "##?tzen", "##innen", "##fu", "##tter", "##sa", "##um");
        Assert.assertEquals(expected, tokenizer.getTokens());
        Assert.assertEquals(expected, tokenizer2.getTokens());
    }

    @Test
    public void testBertWordPieceTokenizer6() throws Exception {
        String toTokenize = "I sAw A gIrL wItH a tElEsCoPe.";
        BertWordPieceTokenizerFactory t = new BertWordPieceTokenizerFactory(pathToVocab);
        t.setLowerCaseOnly(true);
        Tokenizer tokenizer = t.create(toTokenize);
        Tokenizer tokenizer2 = t.create(new ByteArrayInputStream(toTokenize.getBytes()));
        final List<String> expected = Arrays.asList("i", "saw", "a", "girl", "with", "a", "tele", "##scope", ".");
        Assert.assertEquals(expected, tokenizer.getTokens());
        Assert.assertEquals(expected, tokenizer2.getTokens());
    }

    @Test
    public void testBertWordPieceTokenizer7() throws Exception {
        String toTokenize = "I saw a girl with a telescope.";
        BertWordPieceTokenizerFactory t = new BertWordPieceTokenizerFactory(pathToVocab);
        t.setTokenPreProcessor(new LowCasePreProcessor());
        Tokenizer tokenizer = t.create(toTokenize);
        Tokenizer tokenizer2 = t.create(new ByteArrayInputStream(toTokenize.getBytes()));
        final List<String> expected = Arrays.asList("i", "saw", "a", "girl", "with", "a", "tele", "##scope", ".");
        Assert.assertEquals(expected, tokenizer.getTokens());
        Assert.assertEquals(expected, tokenizer2.getTokens());
    }
}

