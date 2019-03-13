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
package org.deeplearning4j.models.glove.count;


import java.io.File;
import org.deeplearning4j.models.word2vec.Huffman;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by fartovii on 25.12.15.
 */
public class BinaryCoOccurrenceReaderTest {
    private static final Logger log = LoggerFactory.getLogger(BinaryCoOccurrenceReaderTest.class);

    @Test
    public void testHasMoreObjects1() throws Exception {
        File tempFile = File.createTempFile("tmp", "tmp");
        tempFile.deleteOnExit();
        VocabCache<VocabWord> vocabCache = new AbstractCache.Builder<VocabWord>().build();
        VocabWord word1 = new VocabWord(1.0, "human");
        VocabWord word2 = new VocabWord(2.0, "animal");
        VocabWord word3 = new VocabWord(3.0, "unknown");
        vocabCache.addToken(word1);
        vocabCache.addToken(word2);
        vocabCache.addToken(word3);
        Huffman huffman = new Huffman(vocabCache.vocabWords());
        huffman.build();
        huffman.applyIndexes(vocabCache);
        BinaryCoOccurrenceWriter<VocabWord> writer = new BinaryCoOccurrenceWriter(tempFile);
        CoOccurrenceWeight<VocabWord> object1 = new CoOccurrenceWeight();
        object1.setElement1(word1);
        object1.setElement2(word2);
        object1.setWeight(3.14159265);
        writer.writeObject(object1);
        CoOccurrenceWeight<VocabWord> object2 = new CoOccurrenceWeight();
        object2.setElement1(word2);
        object2.setElement2(word3);
        object2.setWeight(0.197);
        writer.writeObject(object2);
        writer.finish();
        BinaryCoOccurrenceReader<VocabWord> reader = new BinaryCoOccurrenceReader(tempFile, vocabCache, null);
        CoOccurrenceWeight<VocabWord> r1 = reader.nextObject();
        BinaryCoOccurrenceReaderTest.log.info(("Object received: " + r1));
        Assert.assertNotEquals(null, r1);
        r1 = reader.nextObject();
        BinaryCoOccurrenceReaderTest.log.info(("Object received: " + r1));
        Assert.assertNotEquals(null, r1);
    }

    @Test
    public void testHasMoreObjects2() throws Exception {
        File tempFile = File.createTempFile("tmp", "tmp");
        tempFile.deleteOnExit();
        VocabCache<VocabWord> vocabCache = new AbstractCache.Builder<VocabWord>().build();
        VocabWord word1 = new VocabWord(1.0, "human");
        VocabWord word2 = new VocabWord(2.0, "animal");
        VocabWord word3 = new VocabWord(3.0, "unknown");
        vocabCache.addToken(word1);
        vocabCache.addToken(word2);
        vocabCache.addToken(word3);
        Huffman huffman = new Huffman(vocabCache.vocabWords());
        huffman.build();
        huffman.applyIndexes(vocabCache);
        BinaryCoOccurrenceWriter<VocabWord> writer = new BinaryCoOccurrenceWriter(tempFile);
        CoOccurrenceWeight<VocabWord> object1 = new CoOccurrenceWeight();
        object1.setElement1(word1);
        object1.setElement2(word2);
        object1.setWeight(3.14159265);
        writer.writeObject(object1);
        CoOccurrenceWeight<VocabWord> object2 = new CoOccurrenceWeight();
        object2.setElement1(word2);
        object2.setElement2(word3);
        object2.setWeight(0.197);
        writer.writeObject(object2);
        CoOccurrenceWeight<VocabWord> object3 = new CoOccurrenceWeight();
        object3.setElement1(word1);
        object3.setElement2(word3);
        object3.setWeight(0.001);
        writer.writeObject(object3);
        writer.finish();
        BinaryCoOccurrenceReader<VocabWord> reader = new BinaryCoOccurrenceReader(tempFile, vocabCache, null);
        CoOccurrenceWeight<VocabWord> r1 = reader.nextObject();
        BinaryCoOccurrenceReaderTest.log.info(("Object received: " + r1));
        Assert.assertNotEquals(null, r1);
        r1 = reader.nextObject();
        BinaryCoOccurrenceReaderTest.log.info(("Object received: " + r1));
        Assert.assertNotEquals(null, r1);
        r1 = reader.nextObject();
        BinaryCoOccurrenceReaderTest.log.info(("Object received: " + r1));
        Assert.assertNotEquals(null, r1);
    }
}

