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


import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class AsyncLabelAwareIteratorTest {
    @Test
    public void nextDocument() throws Exception {
        SentenceIterator sentence = new org.deeplearning4j.text.sentenceiterator.BasicLineIterator(new ClassPathResource("/big/raw_sentences.txt").getFile());
        BasicLabelAwareIterator backed = build();
        int cnt = 0;
        while (backed.hasNextDocument()) {
            backed.nextDocument();
            cnt++;
        } 
        Assert.assertEquals(97162, cnt);
        backed.reset();
        AsyncLabelAwareIterator iterator = new AsyncLabelAwareIterator(backed, 64);
        cnt = 0;
        while (iterator.hasNext()) {
            iterator.next();
            cnt++;
            if (cnt == 10)
                iterator.reset();

        } 
        Assert.assertEquals(97172, cnt);
    }
}

