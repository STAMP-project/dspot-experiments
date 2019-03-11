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
package org.nd4j.linalg.dataset;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.TestMultiDataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.MultiNormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * Most of the normalizer functionality is shared with {@link MultiNormalizerMinMaxScaler}
 * and is covered in {@link NormalizerMinMaxScalerTest}. This test suite just verifies if it deals properly with
 * multiple inputs and multiple outputs
 *
 * @author Ede Meijer
 */
@RunWith(Parameterized.class)
public class MultiNormalizerMinMaxScalerTest extends BaseNd4jTest {
    private static final double TOLERANCE_PERC = 0.01;// 0.01% of correct value


    private static final int INPUT1_SCALE = 1;

    private static final int INPUT2_SCALE = 2;

    private static final int OUTPUT1_SCALE = 3;

    private static final int OUTPUT2_SCALE = 4;

    private MultiNormalizerMinMaxScaler SUT;

    private MultiDataSet data;

    private double naturalMin;

    private double naturalMax;

    public MultiNormalizerMinMaxScalerTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testMultipleInputsAndOutputsWithDataSet() {
        SUT.fit(data);
        assertExpectedMinMax();
    }

    @Test
    public void testMultipleInputsAndOutputsWithIterator() {
        MultiDataSetIterator iter = new TestMultiDataSetIterator(1, data);
        SUT.fit(iter);
        assertExpectedMinMax();
    }

    @Test
    public void testRevertFeaturesINDArray() {
        SUT.fit(data);
        MultiDataSet transformed = data.copy();
        SUT.preProcess(transformed);
        INDArray reverted = transformed.getFeatures(0).dup();
        SUT.revertFeatures(reverted, null, 0);
        Assert.assertNotEquals(reverted, transformed.getFeatures(0));
        SUT.revert(transformed);
        Assert.assertEquals(reverted, transformed.getFeatures(0));
    }

    @Test
    public void testRevertLabelsINDArray() {
        SUT.fit(data);
        MultiDataSet transformed = data.copy();
        SUT.preProcess(transformed);
        INDArray reverted = transformed.getLabels(0).dup();
        SUT.revertLabels(reverted, null, 0);
        Assert.assertNotEquals(reverted, transformed.getLabels(0));
        SUT.revert(transformed);
        Assert.assertEquals(reverted, transformed.getLabels(0));
    }

    @Test
    public void testRevertMultiDataSet() {
        SUT.fit(data);
        MultiDataSet transformed = data.copy();
        SUT.preProcess(transformed);
        double diffBeforeRevert = getMaxRelativeDifference(data, transformed);
        Assert.assertTrue((diffBeforeRevert > (MultiNormalizerMinMaxScalerTest.TOLERANCE_PERC)));
        SUT.revert(transformed);
        double diffAfterRevert = getMaxRelativeDifference(data, transformed);
        Assert.assertTrue((diffAfterRevert < (MultiNormalizerMinMaxScalerTest.TOLERANCE_PERC)));
    }

    @Test
    public void testFullyMaskedData() {
        MultiDataSetIterator iter = new TestMultiDataSetIterator(1, new MultiDataSet(new INDArray[]{ Nd4j.create(new float[]{ 1 }).reshape(1, 1, 1) }, new INDArray[]{ Nd4j.create(new float[]{ 2 }).reshape(1, 1, 1) }), new MultiDataSet(new INDArray[]{ Nd4j.create(new float[]{ 2 }).reshape(1, 1, 1) }, new INDArray[]{ Nd4j.create(new float[]{ 4 }).reshape(1, 1, 1) }, null, new INDArray[]{ Nd4j.create(new float[]{ 0 }).reshape(1, 1) }));
        SUT.fit(iter);
        // The label min value should be 2, as the second row with 4 is masked.
        Assert.assertEquals(2.0F, SUT.getLabelMin(0).getFloat(0), 1.0E-6);
    }
}

