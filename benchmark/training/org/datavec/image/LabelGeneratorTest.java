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
package org.datavec.image;


import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.recordreader.ImageRecordReader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.io.ClassPathResource;


public class LabelGeneratorTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testParentPathLabelGenerator() throws Exception {
        // https://github.com/deeplearning4j/DataVec/issues/273
        File orig = new ClassPathResource("datavec-data-image/testimages/class0/0.jpg").getFile();
        for (String dirPrefix : new String[]{ "m.", "m" }) {
            File f = testDir.newFolder();
            int numDirs = 3;
            int filesPerDir = 4;
            for (int i = 0; i < numDirs; i++) {
                File currentLabelDir = new File(f, (dirPrefix + i));
                currentLabelDir.mkdirs();
                for (int j = 0; j < filesPerDir; j++) {
                    File f3 = new File(currentLabelDir, (("myImg_" + j) + ".jpg"));
                    FileUtils.copyFile(orig, f3);
                    Assert.assertTrue(f3.exists());
                }
            }
            ImageRecordReader rr = new ImageRecordReader(28, 28, 1, new ParentPathLabelGenerator());
            rr.initialize(new FileSplit(f));
            List<String> labelsAct = rr.getLabels();
            List<String> labelsExp = Arrays.asList((dirPrefix + "0"), (dirPrefix + "1"), (dirPrefix + "2"));
            Assert.assertEquals(labelsExp, labelsAct);
            int expCount = numDirs * filesPerDir;
            int actCount = 0;
            while (rr.hasNext()) {
                rr.next();
                actCount++;
            } 
            Assert.assertEquals(expCount, actCount);
        }
    }
}

