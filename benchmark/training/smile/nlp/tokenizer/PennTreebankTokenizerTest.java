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
package smile.nlp.tokenizer;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng
 */
public class PennTreebankTokenizerTest {
    public PennTreebankTokenizerTest() {
    }

    /**
     * Test of split method, of class TreebankWordTokenizer.
     */
    @Test
    public void testSplit() {
        System.out.println("tokenize");
        String text = "Good muffins cost $3.88\nin New York.  Please buy " + ("me\ntwo of them.\n\nYou cannot eat them. I gonna eat them. " + "Thanks.");
        String[] expResult = new String[]{ "Good", "muffins", "cost", "$", "3.88", "in", "New", "York.", "Please", "buy", "me", "two", "of", "them", ".", "You", "can", "not", "eat", "them.", "I", "gon", "na", "eat", "them.", "Thanks", "." };
        PennTreebankTokenizer instance = PennTreebankTokenizer.getInstance();
        String[] result = instance.split(text);
        Assert.assertEquals(expResult.length, result.length);
        for (int i = 0; i < (result.length); i++) {
            Assert.assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of split method, of class TreebankWordTokenizer.
     */
    @Test
    public void testSplitContraction() {
        System.out.println("tokenize contraction");
        String text = "Here are some examples of contractions: 'tis, " + ((((((("'twas, ain't, aren't, can't, could've, couldn't, didn't, doesn't, " + "don't, hasn't, he'd, he'll, he's, how'd, how'll, how's, i'd, i'll, i'm, ") + "i've, isn't, it's, might've, mightn't, must've, mustn't, shan't, ") + "she'd, she'll, she's, should've, shouldn't, that'll, that's, ") + "there's, they'd, they'll, they're, they've, wasn't, we'd, we'll, ") + "we're, weren't, what'd, what's, when'd, when'll, when's, ") + "where'd, where'll, where's, who'd, who'll, who's, why'd, why'll, ") + "why's, won't, would've, wouldn't, you'd, you'll, you're, you've");
        String[] expResult = new String[]{ "Here", "are", "some", "examples", "of", "contractions", ":", "'t", "is", ",", "'t", "was", ",", "ai", "n't", ",", "are", "n't", ",", "ca", "n't", ",", "could", "'ve", ",", "could", "n't", ",", "did", "n't", ",", "does", "n't", ",", "do", "n't", ",", "has", "n't", ",", "he", "'d", ",", "he", "'ll", ",", "he", "'s", ",", "how", "'d", ",", "how", "'ll", ",", "how", "'s", ",", "i", "'d", ",", "i", "'ll", ",", "i", "'m", ",", "i", "'ve", ",", "is", "n't", ",", "it", "'s", ",", "might", "'ve", ",", "might", "n't", ",", "must", "'ve", ",", "must", "n't", ",", "sha", "n't", ",", "she", "'d", ",", "she", "'ll", ",", "she", "'s", ",", "should", "'ve", ",", "should", "n't", ",", "that", "'ll", ",", "that", "'s", ",", "there", "'s", ",", "they", "'d", ",", "they", "'ll", ",", "they", "'re", ",", "they", "'ve", ",", "was", "n't", ",", "we", "'d", ",", "we", "'ll", ",", "we", "'re", ",", "were", "n't", ",", "what", "'d", ",", "what", "'s", ",", "when", "'d", ",", "when", "'ll", ",", "when", "'s", ",", "where", "'d", ",", "where", "'ll", ",", "where", "'s", ",", "who", "'d", ",", "who", "'ll", ",", "who", "'s", ",", "why", "'d", ",", "why", "'ll", ",", "why", "'s", ",", "wo", "n't", ",", "would", "'ve", ",", "would", "n't", ",", "you", "'d", ",", "you", "'ll", ",", "you", "'re", ",", "you", "'ve" };
        PennTreebankTokenizer instance = PennTreebankTokenizer.getInstance();
        String[] result = instance.split(text);
        Assert.assertEquals(expResult.length, result.length);
        for (int i = 0; i < (result.length); i++) {
            Assert.assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of split method, of class TreebankWordTokenizer.
     */
    @Test
    public void testSplitAbbreviation() {
        System.out.println("tokenize abbreviation");
        String text = "Here are some examples of abbreviations: A.B., abbr., " + "acad., A.D., alt., A.M., B.C., etc.";
        String[] expResult = new String[]{ "Here", "are", "some", "examples", "of", "abbreviations", ":", "A.B.", ",", "abbr.", ",", "acad.", ",", "A.D.", ",", "alt.", ",", "A.M.", ",", "B.C.", ",", "etc.", "." };
        PennTreebankTokenizer instance = PennTreebankTokenizer.getInstance();
        String[] result = instance.split(text);
        Assert.assertEquals(expResult.length, result.length);
        for (int i = 0; i < (result.length); i++) {
            Assert.assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of split method, of class TreebankWordTokenizer.
     */
    @Test
    public void testSplitTis() {
        System.out.println("tokenize tis");
        String text = "'tis, 'tisn't, and 'twas were common in early modern English texts.";
        String[] expResult = new String[]{ "'t", "is", ",", "'t", "is", "n't", ",", "and", "'t", "was", "were", "common", "in", "early", "modern", "English", "texts", "." };
        PennTreebankTokenizer instance = PennTreebankTokenizer.getInstance();
        String[] result = instance.split(text);
        Assert.assertEquals(expResult.length, result.length);
        for (int i = 0; i < (result.length); i++) {
            Assert.assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of split method, of class TreebankWordTokenizer.
     */
    @Test
    public void testSplitHyphen() {
        System.out.println("tokenize hyphen");
        String text = "On a noncash basis for the quarter, the bank reported a " + (("loss of $7.3 billion because of a $10.4 billion write-down " + "in the value of its credit card unit, attributed to federal ") + "regulations that limit debit fees and other charges.");
        String[] expResult = new String[]{ "On", "a", "noncash", "basis", "for", "the", "quarter", ",", "the", "bank", "reported", "a", "loss", "of", "$", "7.3", "billion", "because", "of", "a", "$", "10.4", "billion", "write-down", "in", "the", "value", "of", "its", "credit", "card", "unit", ",", "attributed", "to", "federal", "regulations", "that", "limit", "debit", "fees", "and", "other", "charges", "." };
        PennTreebankTokenizer instance = PennTreebankTokenizer.getInstance();
        String[] result = instance.split(text);
        Assert.assertEquals(expResult.length, result.length);
        for (int i = 0; i < (result.length); i++) {
            Assert.assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of split method, of class TreebankWordTokenizer.
     */
    @Test
    public void testSplitSingleQuote() {
        System.out.println("tokenize single quote");
        String text = "String literals can be enclosed in matching single " + "quotes ('). But it's also appearing in contractions such as can't.";
        String[] expResult = new String[]{ "String", "literals", "can", "be", "enclosed", "in", "matching", "single", "quotes", "(", "'", ")", ".", "But", "it", "'s", "also", "appearing", "in", "contractions", "such", "as", "ca", "n't", "." };
        PennTreebankTokenizer instance = PennTreebankTokenizer.getInstance();
        String[] result = instance.split(text);
        Assert.assertEquals(expResult.length, result.length);
        for (int i = 0; i < (result.length); i++) {
            Assert.assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of split method, of class TreebankWordTokenizer.
     */
    @Test
    public void testSplitRomanNumeral() {
        System.out.println("tokenize roman numeral");
        String text = "S.. or S: means \"twice\" (as in \"twice a third\").";
        String[] expResult = new String[]{ "S..", "or", "S", ":", "means", "\"", "twice", "\"", "(", "as", "in", "\"", "twice", "a", "third", "\"", ")", "." };
        PennTreebankTokenizer instance = PennTreebankTokenizer.getInstance();
        String[] result = instance.split(text);
        Assert.assertEquals(expResult.length, result.length);
        for (int i = 0; i < (result.length); i++) {
            Assert.assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of split method, of class TreebankWordTokenizer.
     */
    @Test
    public void testTokenizeMixedAlphanumWords() {
        System.out.println("tokenize words with mixed numbers, letters, and punctuation");
        String text = "3M, L-3, BB&T, AutoZone, O'Reilly, Harley-Davidson, CH2M, A-Mark, " + "Quad/Graphics, Bloomin' Brands, B/E Aerospace, J.Crew, E*Trade.";
        // Note: would be very hard to get "Bloomin'" and "E*Trade" correct
        String[] expResult = new String[]{ "3M", ",", "L-3", ",", "BB&T", ",", "AutoZone", ",", "O'Reilly", ",", "Harley-Davidson", ",", "CH2M", ",", "A-Mark", ",", "Quad/Graphics", ",", "Bloomin", "'", "Brands", ",", "B/E", "Aerospace", ",", "J.Crew", ",", "E", "*", "Trade", "." };
        SimpleTokenizer instance = new SimpleTokenizer();
        String[] result = instance.split(text);
        Assert.assertEquals(expResult.length, result.length);
        for (int i = 0; i < (result.length); i++) {
            Assert.assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of split method, of class TreebankWordTokenizer.
     */
    @Test
    public void testTokenizeDiacritizedWords() {
        System.out.println("tokenize words with diacritized chars (both composite and combining)");
        String text = "The na\u00efve r\u00e9sum\u00e9 of Ra\u00fal Ib\u00e1\u00f1ez; re\u0301sume\u0301.";
        String[] expResult = new String[]{ "The", "na?ve", "r?sum?", "of", "Ra?l", "Ib??ez", ";", "re\u0301sume\u0301", "." };
        SimpleTokenizer instance = new SimpleTokenizer();
        String[] result = instance.split(text);
        // assertEquals(expResult.length, result.length);
        for (int i = 0; i < (result.length); i++) {
            Assert.assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of split method, of class TreebankWordTokenizer.
     */
    @Test
    public void testTokenizeNonLatinChars() {
        System.out.println("tokenize words containing non-Latin chars");
        // See https://en.wikipedia.org/wiki/Zero-width_non-joiner
        String text = "????????   ????????   Auf?lage";
        String[] expResult = new String[]{ "????????", "????????", "Auf?lage" };
        SimpleTokenizer instance = new SimpleTokenizer();
        String[] result = instance.split(text);
        Assert.assertEquals(expResult.length, result.length);
        for (int i = 0; i < (result.length); i++) {
            Assert.assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of split method, of class TreebankWordTokenizer.
     */
    @Test
    public void testTokenizeVariousSpaces() {
        System.out.println("tokenize words separated by various kinds of space");
        // No-break space and em-space
        String text = "the\u00a0cat\u2003the_cat";
        String[] expResult = new String[]{ "the", "cat", "the_cat" };
        SimpleTokenizer instance = new SimpleTokenizer();
        String[] result = instance.split(text);
        Assert.assertEquals(expResult.length, result.length);
        for (int i = 0; i < (result.length); i++) {
            Assert.assertEquals(expResult[i], result[i]);
        }
    }
}

