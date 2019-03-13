/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.nlp.collocation;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Test;
import smile.data.parser.IOUtils;
import smile.nlp.NGram;
import smile.nlp.stemmer.PorterStemmer;
import smile.nlp.tokenizer.SimpleParagraphSplitter;
import smile.nlp.tokenizer.SimpleSentenceSplitter;
import smile.nlp.tokenizer.SimpleTokenizer;


/**
 *
 *
 * @author Haifeng Li
 */
public class AprioriPhraseExtractorTest {
    public AprioriPhraseExtractorTest() {
    }

    /**
     * Test of extract method, of class AprioriPhraseExtractorTest.
     */
    @Test
    public void testExtract() throws FileNotFoundException {
        System.out.println("extract");
        Scanner scanner = new Scanner(IOUtils.getTestDataReader("text/turing.txt"));
        String text = scanner.useDelimiter("\\Z").next();
        scanner.close();
        PorterStemmer stemmer = new PorterStemmer();
        SimpleTokenizer tokenizer = new SimpleTokenizer();
        ArrayList<String[]> sentences = new ArrayList<>();
        for (String paragraph : SimpleParagraphSplitter.getInstance().split(text)) {
            for (String s : SimpleSentenceSplitter.getInstance().split(paragraph)) {
                String[] sentence = tokenizer.split(s);
                for (int i = 0; i < (sentence.length); i++) {
                    sentence[i] = stemmer.stripPluralParticiple(sentence[i]).toLowerCase();
                }
                sentences.add(sentence);
            }
        }
        AprioriPhraseExtractor instance = new AprioriPhraseExtractor();
        ArrayList<ArrayList<NGram>> result = instance.extract(sentences, 4, 4);
        Assert.assertEquals(5, result.size());
        for (ArrayList<NGram> ngrams : result) {
            for (NGram ngram : ngrams) {
                System.out.print(ngram);
            }
            System.out.println();
        }
    }
}

