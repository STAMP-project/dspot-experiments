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


import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.io.ClassPathResource;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class FilenamesLabelAwareIteratorTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testNextDocument() throws Exception {
        ClassPathResource big = new ClassPathResource("/big");
        val tempDir = testDir.newFolder();
        big.copyDirectory(tempDir);
        FilenamesLabelAwareIterator iterator = new FilenamesLabelAwareIterator.Builder().addSourceFolder(tempDir).useAbsolutePathAsLabel(false).build();
        List<String> labels = new ArrayList<>();
        LabelledDocument doc1 = iterator.nextDocument();
        labels.add(doc1.getLabel());
        LabelledDocument doc2 = iterator.nextDocument();
        labels.add(doc2.getLabel());
        LabelledDocument doc3 = iterator.nextDocument();
        labels.add(doc3.getLabel());
        LabelledDocument doc4 = iterator.nextDocument();
        labels.add(doc4.getLabel());
        Assert.assertFalse(iterator.hasNextDocument());
        System.out.println(("Labels: " + labels));
        Assert.assertTrue(labels.contains("coc.txt"));
        Assert.assertTrue(labels.contains("occurrences.txt"));
        Assert.assertTrue(labels.contains("raw_sentences.txt"));
        Assert.assertTrue(labels.contains("tokens.txt"));
    }
}

