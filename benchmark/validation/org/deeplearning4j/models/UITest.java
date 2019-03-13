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
package org.deeplearning4j.models;


import java.io.File;
import java.util.ArrayList;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.plot.BarnesHutTsne;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.UimaSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.ui.UiConnectionInfo;
import org.deeplearning4j.ui.api.UIServer;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Created by Alex on 10/01/2017.
 */
@Ignore
public class UITest {
    @Test
    public void testPosting() throws Exception {
        // File inputFile = new ClassPathResource("/big/raw_sentences.txt").getFile();
        File inputFile = new ClassPathResource("/basic/word2vec_advance.txt").getFile();
        SentenceIterator iter = UimaSentenceIterator.createWithPath(inputFile.getAbsolutePath());
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = new Word2Vec.Builder().minWordFrequency(1).epochs(1).layerSize(20).stopWords(new ArrayList<String>()).useAdaGrad(false).negativeSample(5).seed(42).windowSize(5).iterate(iter).tokenizerFactory(t).build();
        vec.fit();
        File tempFile = File.createTempFile("temp", "w2v");
        tempFile.deleteOnExit();
        WordVectorSerializer.writeWordVectors(vec, tempFile);
        WordVectors vectors = WordVectorSerializer.loadTxtVectors(tempFile);
        UIServer.getInstance();// Initialize

        UiConnectionInfo uiConnectionInfo = new UiConnectionInfo.Builder().setAddress("localhost").setPort(9000).build();
        BarnesHutTsne tsne = new BarnesHutTsne.Builder().normalize(false).setFinalMomentum(0.8F).numDimension(2).setMaxIter(10).build();
        vectors.lookupTable().plotVocab(tsne, vectors.lookupTable().getVocabCache().numWords(), uiConnectionInfo);
        Thread.sleep(100000);
    }
}

