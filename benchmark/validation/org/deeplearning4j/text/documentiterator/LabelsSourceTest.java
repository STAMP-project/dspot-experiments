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


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by raver on 26.11.2015.
 */
public class LabelsSourceTest {
    @Test
    public void testNextLabel1() throws Exception {
        LabelsSource generator = new LabelsSource("SENTENCE_");
        Assert.assertEquals("SENTENCE_0", generator.nextLabel());
    }

    @Test
    public void testNextLabel2() throws Exception {
        LabelsSource generator = new LabelsSource("SENTENCE_%d_HAHA");
        Assert.assertEquals("SENTENCE_0_HAHA", generator.nextLabel());
    }

    @Test
    public void testNextLabel3() throws Exception {
        List<String> list = Arrays.asList("LABEL0", "LABEL1", "LABEL2");
        LabelsSource generator = new LabelsSource(list);
        Assert.assertEquals("LABEL0", generator.nextLabel());
    }

    @Test
    public void testLabelsCount1() throws Exception {
        List<String> list = Arrays.asList("LABEL0", "LABEL1", "LABEL2");
        LabelsSource generator = new LabelsSource(list);
        Assert.assertEquals("LABEL0", generator.nextLabel());
        Assert.assertEquals("LABEL1", generator.nextLabel());
        Assert.assertEquals("LABEL2", generator.nextLabel());
        Assert.assertEquals(3, generator.getNumberOfLabelsUsed());
    }

    @Test
    public void testLabelsCount2() throws Exception {
        LabelsSource generator = new LabelsSource("SENTENCE_");
        Assert.assertEquals("SENTENCE_0", generator.nextLabel());
        Assert.assertEquals("SENTENCE_1", generator.nextLabel());
        Assert.assertEquals("SENTENCE_2", generator.nextLabel());
        Assert.assertEquals("SENTENCE_3", generator.nextLabel());
        Assert.assertEquals("SENTENCE_4", generator.nextLabel());
        Assert.assertEquals(5, generator.getNumberOfLabelsUsed());
    }

    @Test
    public void testLabelsCount3() throws Exception {
        LabelsSource generator = new LabelsSource("SENTENCE_");
        Assert.assertEquals("SENTENCE_0", generator.nextLabel());
        Assert.assertEquals("SENTENCE_1", generator.nextLabel());
        Assert.assertEquals("SENTENCE_2", generator.nextLabel());
        Assert.assertEquals("SENTENCE_3", generator.nextLabel());
        Assert.assertEquals("SENTENCE_4", generator.nextLabel());
        Assert.assertEquals(5, generator.getNumberOfLabelsUsed());
        generator.reset();
        Assert.assertEquals(5, generator.getNumberOfLabelsUsed());
    }
}

