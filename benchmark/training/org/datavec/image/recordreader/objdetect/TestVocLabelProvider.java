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
package org.datavec.image.recordreader.objdetect;


import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.datavec.image.recordreader.objdetect.impl.VocLabelProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.io.ClassPathResource;


public class TestVocLabelProvider {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testVocLabelProvider() throws Exception {
        File f = testDir.newFolder();
        new ClassPathResource("datavec-data-image/voc/2007/").copyDirectory(f);
        String path = f.getAbsolutePath();// new ClassPathResource("voc/2007/JPEGImages/000005.jpg").getFile().getParentFile().getParent();

        ImageObjectLabelProvider lp = new VocLabelProvider(path);
        String img5 = new File(f, "JPEGImages/000005.jpg").getPath();
        List<ImageObject> l5 = lp.getImageObjectsForPath(img5);
        Assert.assertEquals(5, l5.size());
        List<ImageObject> exp5 = Arrays.asList(new ImageObject(263, 211, 324, 339, "chair"), new ImageObject(165, 264, 253, 372, "chair"), new ImageObject(5, 244, 67, 374, "chair"), new ImageObject(241, 194, 295, 299, "chair"), new ImageObject(277, 186, 312, 220, "chair"));
        Assert.assertEquals(exp5, l5);
        String img7 = new File(f, "JPEGImages/000007.jpg").getPath();
        List<ImageObject> exp7 = Collections.singletonList(new ImageObject(141, 50, 500, 330, "car"));
        Assert.assertEquals(exp7, lp.getImageObjectsForPath(img7));
    }
}

