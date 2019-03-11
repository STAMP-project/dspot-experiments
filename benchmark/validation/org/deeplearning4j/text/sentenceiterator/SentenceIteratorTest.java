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
package org.deeplearning4j.text.sentenceiterator;


import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Created by agibsonccc on 9/9/14.
 */
public class SentenceIteratorTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    public File testTxt;

    public File testSingle;

    public File testMulti;

    @Test
    public void testUimaSentenceIterator() throws Exception {
        SentenceIterator multiIter = UimaSentenceIterator.createWithPath("multidir");
        SentenceIterator iter = UimaSentenceIterator.createWithPath("dir");
        testMulti(multiIter, 1);
    }

    @Test
    public void testFileSentenceIterator() throws Exception {
        SentenceIterator iter = new FileSentenceIterator(testSingle);
        SentenceIterator multiIter = new FileSentenceIterator(testMulti);
        testSingle(iter);
        testMulti(multiIter, 3);
    }
}

