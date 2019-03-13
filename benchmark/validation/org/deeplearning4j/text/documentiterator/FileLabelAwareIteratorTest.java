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


import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Created by raver119 on 03.01.16.
 */
public class FileLabelAwareIteratorTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testExtractLabelFromPath1() throws Exception {
        val dir = testDir.newFolder();
        val resource = new ClassPathResource("/labeled");
        resource.copyDirectory(dir);
        val iterator = new FileLabelAwareIterator.Builder().addSourceFolder(dir).build();
        int cnt = 0;
        while (iterator.hasNextDocument()) {
            val document = iterator.nextDocument();
            Assert.assertNotEquals(null, document);
            Assert.assertNotEquals(null, document.getContent());
            Assert.assertNotEquals(null, document.getLabel());
            cnt++;
        } 
        Assert.assertEquals(3, cnt);
        Assert.assertEquals(3, iterator.getLabelsSource().getNumberOfLabelsUsed());
        Assert.assertTrue(iterator.getLabelsSource().getLabels().contains("positive"));
        Assert.assertTrue(iterator.getLabelsSource().getLabels().contains("negative"));
        Assert.assertTrue(iterator.getLabelsSource().getLabels().contains("neutral"));
    }

    @Test
    public void testExtractLabelFromPath2() throws Exception {
        val dir0 = testDir.newFolder();
        val dir1 = testDir.newFolder();
        val resource = new ClassPathResource("/labeled");
        val resource2 = new ClassPathResource("/rootdir");
        resource.copyDirectory(dir0);
        resource2.copyDirectory(dir1);
        FileLabelAwareIterator iterator = new FileLabelAwareIterator.Builder().addSourceFolder(dir0).addSourceFolder(dir1).build();
        int cnt = 0;
        while (iterator.hasNextDocument()) {
            LabelledDocument document = iterator.nextDocument();
            Assert.assertNotEquals(null, document);
            Assert.assertNotEquals(null, document.getContent());
            Assert.assertNotEquals(null, document.getLabel());
            cnt++;
        } 
        Assert.assertEquals(5, cnt);
        Assert.assertEquals(5, iterator.getLabelsSource().getNumberOfLabelsUsed());
        Assert.assertTrue(iterator.getLabelsSource().getLabels().contains("positive"));
        Assert.assertTrue(iterator.getLabelsSource().getLabels().contains("negative"));
        Assert.assertTrue(iterator.getLabelsSource().getLabels().contains("neutral"));
        Assert.assertTrue(iterator.getLabelsSource().getLabels().contains("label1"));
        Assert.assertTrue(iterator.getLabelsSource().getLabels().contains("label2"));
    }
}

