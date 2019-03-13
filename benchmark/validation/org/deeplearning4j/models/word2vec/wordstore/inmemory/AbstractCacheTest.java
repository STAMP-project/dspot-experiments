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
package org.deeplearning4j.models.word2vec.wordstore.inmemory;


import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.deeplearning4j.models.sequencevectors.serialization.ExtVocabWord;
import org.deeplearning4j.models.word2vec.Huffman;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fartovii on 10.12.15.
 */
@Slf4j
public class AbstractCacheTest {
    @Test
    public void testNumWords() throws Exception {
        AbstractCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        cache.addToken(new VocabWord(1.0, "word"));
        cache.addToken(new VocabWord(1.0, "test"));
        Assert.assertEquals(2, cache.numWords());
    }

    @Test
    public void testHuffman() throws Exception {
        AbstractCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        cache.addToken(new VocabWord(1.0, "word"));
        cache.addToken(new VocabWord(2.0, "test"));
        cache.addToken(new VocabWord(3.0, "tester"));
        Assert.assertEquals(3, cache.numWords());
        Huffman huffman = new Huffman(cache.tokens());
        huffman.build();
        huffman.applyIndexes(cache);
        Assert.assertEquals("tester", cache.wordAtIndex(0));
        Assert.assertEquals("test", cache.wordAtIndex(1));
        Assert.assertEquals("word", cache.wordAtIndex(2));
        VocabWord word = cache.tokenFor("tester");
        Assert.assertEquals(0, word.getIndex());
    }

    @Test
    public void testWordsOccurencies() throws Exception {
        AbstractCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        cache.addToken(new VocabWord(1.0, "word"));
        cache.addToken(new VocabWord(2.0, "test"));
        cache.addToken(new VocabWord(3.0, "tester"));
        Assert.assertEquals(3, cache.numWords());
        Assert.assertEquals(6, cache.totalWordOccurrences());
    }

    @Test
    public void testRemoval() throws Exception {
        AbstractCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        cache.addToken(new VocabWord(1.0, "word"));
        cache.addToken(new VocabWord(2.0, "test"));
        cache.addToken(new VocabWord(3.0, "tester"));
        Assert.assertEquals(3, cache.numWords());
        Assert.assertEquals(6, cache.totalWordOccurrences());
        cache.removeElement("tester");
        Assert.assertEquals(2, cache.numWords());
        Assert.assertEquals(3, cache.totalWordOccurrences());
    }

    @Test
    public void testLabels() throws Exception {
        AbstractCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        cache.addToken(new VocabWord(1.0, "word"));
        cache.addToken(new VocabWord(2.0, "test"));
        cache.addToken(new VocabWord(3.0, "tester"));
        Collection<String> collection = cache.words();
        Assert.assertEquals(3, collection.size());
        Assert.assertTrue(collection.contains("word"));
        Assert.assertTrue(collection.contains("test"));
        Assert.assertTrue(collection.contains("tester"));
    }

    @Test
    public void testSerialization() {
        AbstractCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        val words = new VocabWord[3];
        words[0] = new VocabWord(1.0, "word");
        words[1] = new VocabWord(2.0, "test");
        words[2] = new VocabWord(3.0, "tester");
        for (int i = 0; i < (words.length); ++i) {
            cache.addToken(words[i]);
            cache.addWordToIndex(i, getLabel());
        }
        String json = null;
        AbstractCache<VocabWord> unserialized = null;
        try {
            json = cache.toJson();
            log.info("{}", json.toString());
            unserialized = AbstractCache.fromJson(json);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals(cache.totalWordOccurrences(), unserialized.totalWordOccurrences());
        Assert.assertEquals(cache.totalNumberOfDocs(), unserialized.totalNumberOfDocs());
        for (int i = 0; i < (words.length); ++i) {
            val cached = cache.wordAtIndex(i);
            val restored = unserialized.wordAtIndex(i);
            Assert.assertNotNull(cached);
            Assert.assertEquals(cached, restored);
        }
    }

    @Test
    public void testUserClassSerialization() {
        AbstractCache<ExtVocabWord> cache = new AbstractCache.Builder<ExtVocabWord>().build();
        ExtVocabWord[] words = new ExtVocabWord[3];
        words[0] = new ExtVocabWord("some", 1100, 1.0, "word");
        words[1] = new ExtVocabWord("none", 23214, 2.0, "test");
        words[2] = new ExtVocabWord("wwew", 13223, 3.0, "tester");
        for (int i = 0; i < 3; ++i) {
            cache.addToken(words[i]);
            cache.addWordToIndex(i, getLabel());
        }
        String json = null;
        AbstractCache<VocabWord> unserialized = null;
        try {
            json = cache.toJson();
            unserialized = AbstractCache.fromJson(json);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals(cache.totalWordOccurrences(), unserialized.totalWordOccurrences());
        Assert.assertEquals(cache.totalNumberOfDocs(), unserialized.totalNumberOfDocs());
        for (int i = 0; i < 3; ++i) {
            val t = cache.wordAtIndex(i);
            Assert.assertNotNull(t);
            Assert.assertTrue(unserialized.containsWord(t));
            Assert.assertEquals(cache.wordAtIndex(i), unserialized.wordAtIndex(i));
        }
    }
}

