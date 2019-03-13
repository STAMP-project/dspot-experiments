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
package org.deeplearning4j.text.documentiterator;


import java.io.File;
import java.io.InputStream;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by agibsonccc on 9/29/14.
 */
public class DefaultDocumentIteratorTest {
    private static final Logger log = LoggerFactory.getLogger(DefaultDocumentIteratorTest.class);

    @Test
    public void testDocumentIterator() throws Exception {
        ClassPathResource reuters5250 = new ClassPathResource("/reuters/5250");
        File f = reuters5250.getFile();
        DocumentIterator iter = new FileDocumentIterator(f.getAbsolutePath());
        InputStream doc = iter.nextDocument();
        TokenizerFactory t = new DefaultTokenizerFactory();
        Tokenizer next = t.create(doc);
        String[] list = "PEARSON CONCENTRATES ON FOUR SECTORS".split(" ");
        // /PEARSON CONCENTRATES ON FOUR SECTORS
        int count = 0;
        while ((next.hasMoreTokens()) && (count < (list.length))) {
            String token = next.nextToken();
            Assert.assertEquals(list[(count++)], token);
        } 
        doc.close();
    }
}

