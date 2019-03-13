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
package org.deeplearning4j.zoo;


import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.zoo.model.LeNet;
import org.deeplearning4j.zoo.model.NASNet;
import org.deeplearning4j.zoo.model.SimpleCNN;
import org.deeplearning4j.zoo.model.UNet;
import org.deeplearning4j.zoo.util.darknet.COCOLabels;
import org.deeplearning4j.zoo.util.darknet.DarknetLabels;
import org.deeplearning4j.zoo.util.imagenet.ImageNetLabels;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Tests downloads and checksum verification.
 *
 * @unknown This test uses a temporary directory, so local model copies won't be impacted
 * @author Justin Long (crockpotveggies)
 */
@Slf4j
public class TestDownload extends BaseDL4JTest {
    @ClassRule
    public static TemporaryFolder testDir = new TemporaryFolder();

    private static File f;

    @Test
    public void testDownloadAllModels() throws Exception {
        // iterate through each available model
        ZooModel[] models = new ZooModel[]{ LeNet.builder().build(), SimpleCNN.builder().build(), UNet.builder().build(), NASNet.builder().build() };
        for (int i = 0; i < (models.length); i++) {
            log.info(("Testing zoo model " + (models[i].getClass().getName())));
            ZooModel model = models[i];
            for (PretrainedType pretrainedType : PretrainedType.values()) {
                if (model.pretrainedAvailable(pretrainedType)) {
                    model.initPretrained(pretrainedType);
                }
            }
            // clean up for current model
            Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
            System.gc();
            Thread.sleep(1000);
        }
    }

    @Test
    public void testLabelsDownload() throws Exception {
        Assert.assertEquals("person", new COCOLabels().getLabel(0));
        Assert.assertEquals("kit fox", new DarknetLabels(true).getLabel(0));
        Assert.assertEquals("n02119789", new DarknetLabels(false).getLabel(0));
        Assert.assertEquals("tench", new ImageNetLabels().getLabel(0));
    }
}

