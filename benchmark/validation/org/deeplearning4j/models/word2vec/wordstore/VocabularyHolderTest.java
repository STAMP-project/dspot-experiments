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
package org.deeplearning4j.models.word2vec.wordstore;


import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fartovii on 08.11.15.
 */
public class VocabularyHolderTest {
    @Test
    public void testTransferBackToVocabCache() throws Exception {
        VocabularyHolder holder = new VocabularyHolder();
        holder.addWord("test");
        holder.addWord("tests");
        holder.addWord("testz");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("testz");
        InMemoryLookupCache cache = new InMemoryLookupCache(false);
        holder.updateHuffmanCodes();
        holder.transferBackToVocabCache(cache);
        // checking word frequency transfer
        Assert.assertEquals(3, cache.numWords());
        Assert.assertEquals(1, cache.wordFrequency("test"));
        Assert.assertEquals(2, cache.wordFrequency("testz"));
        Assert.assertEquals(3, cache.wordFrequency("tests"));
        // checking Huffman tree transfer
        Assert.assertEquals("tests", cache.wordAtIndex(0));
        Assert.assertEquals("testz", cache.wordAtIndex(1));
        Assert.assertEquals("test", cache.wordAtIndex(2));
    }

    @Test
    public void testConstructor() throws Exception {
        InMemoryLookupCache cache = new InMemoryLookupCache(true);
        VocabularyHolder holder = new VocabularyHolder(cache, false);
        // no more UNK token here
        Assert.assertEquals(0, holder.numWords());
    }

    /**
     * In this test we make sure SPECIAL words are not affected by truncation in extending vocab
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSpecial1() throws Exception {
        VocabularyHolder holder = new VocabularyHolder.Builder().minWordFrequency(1).build();
        holder.addWord("test");
        holder.addWord("tests");
        holder.truncateVocabulary();
        Assert.assertEquals(2, holder.numWords());
        VocabCache cache = new InMemoryLookupCache();
        holder.transferBackToVocabCache(cache);
        VocabularyHolder holder2 = // .markAsSpecial(true)
        new VocabularyHolder.Builder().externalCache(cache).minWordFrequency(10).build();
        holder2.addWord("testz");
        Assert.assertEquals(3, holder2.numWords());
        holder2.truncateVocabulary();
        Assert.assertEquals(2, holder2.numWords());
    }

    @Test
    public void testScavenger1() throws Exception {
        VocabularyHolder holder = // this value doesn't really matters, since we'll call for scavenger manually
        new VocabularyHolder.Builder().minWordFrequency(5).hugeModelExpected(true).scavengerActivationThreshold(1000000).scavengerRetentionDelay(3).build();
        holder.addWord("test");
        holder.addWord("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.activateScavenger();
        Assert.assertEquals(2, holder.numWords());
        holder.activateScavenger();
        Assert.assertEquals(2, holder.numWords());
        // after third activation, word "test" should be removed
        holder.activateScavenger();
        Assert.assertEquals(1, holder.numWords());
    }

    @Test
    public void testScavenger2() throws Exception {
        VocabularyHolder holder = // this value doesn't really matters, since we'll call for scavenger manually
        new VocabularyHolder.Builder().minWordFrequency(5).hugeModelExpected(true).scavengerActivationThreshold(1000000).scavengerRetentionDelay(3).build();
        holder.addWord("test");
        holder.incrementWordCounter("test");
        holder.addWord("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.activateScavenger();
        Assert.assertEquals(2, holder.numWords());
        holder.activateScavenger();
        Assert.assertEquals(2, holder.numWords());
        // after third activation, word "test" should be removed
        holder.activateScavenger();
        Assert.assertEquals(1, holder.numWords());
    }

    @Test
    public void testScavenger3() throws Exception {
        VocabularyHolder holder = // this value doesn't really matters, since we'll call for scavenger manually
        new VocabularyHolder.Builder().minWordFrequency(5).hugeModelExpected(true).scavengerActivationThreshold(1000000).scavengerRetentionDelay(3).build();
        holder.addWord("test");
        holder.activateScavenger();
        Assert.assertEquals(1, holder.numWords());
        holder.incrementWordCounter("test");
        holder.addWord("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.activateScavenger();
        Assert.assertEquals(2, holder.numWords());
        // after third activation, word "test" should NOT be removed, since at point 0 we have freq == 1, and 2 in the following tests
        holder.activateScavenger();
        Assert.assertEquals(2, holder.numWords());
        // here we should have all retention points shifted, and word "test" should be removed
        holder.activateScavenger();
        Assert.assertEquals(1, holder.numWords());
    }

    @Test
    public void testScavenger4() throws Exception {
        VocabularyHolder holder = // this value doesn't really matters, since we'll call for scavenger manually
        new VocabularyHolder.Builder().minWordFrequency(5).hugeModelExpected(true).scavengerActivationThreshold(1000000).scavengerRetentionDelay(3).build();
        holder.addWord("test");
        holder.activateScavenger();
        Assert.assertEquals(1, holder.numWords());
        holder.incrementWordCounter("test");
        holder.addWord("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.incrementWordCounter("tests");
        holder.activateScavenger();
        Assert.assertEquals(2, holder.numWords());
        // after third activation, word "test" should NOT be removed, since at point 0 we have freq == 1, and 2 in the following tests
        holder.activateScavenger();
        Assert.assertEquals(2, holder.numWords());
        holder.incrementWordCounter("test");
        // here we should have all retention points shifted, and word "test" should NOT be removed, since now it's above the scavenger threshold
        holder.activateScavenger();
        Assert.assertEquals(2, holder.numWords());
    }
}

