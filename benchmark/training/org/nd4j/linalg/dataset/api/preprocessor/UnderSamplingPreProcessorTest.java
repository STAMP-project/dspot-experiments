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
package org.nd4j.linalg.dataset.api.preprocessor;


import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.dataset.api.preprocessor.classimbalance.UnderSamplingByMaskingMultiDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.preprocessor.classimbalance.UnderSamplingByMaskingPreProcessor;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.NDArrayIndex;


/**
 *
 *
 * @author susaneraly
 */
@Slf4j
@RunWith(Parameterized.class)
public class UnderSamplingPreProcessorTest extends BaseNd4jTest {
    int shortSeq = 10000;

    int longSeq = 20020;// not a perfect multiple of windowSize


    int window = 5000;

    int minibatchSize = 3;

    double targetDist = 0.3;

    double tolerancePerc = 0.03;// 10% +/- because this is not a very large sample


    public UnderSamplingPreProcessorTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void allMajority() {
        float[] someTargets = new float[]{ 0.01F, 0.1F, 0.5F };
        DataSet d = allMajorityDataSet(false);
        DataSet dToPreProcess;
        for (int i = 0; i < (someTargets.length); i++) {
            // if all majority default is to mask all time steps
            UnderSamplingByMaskingPreProcessor preProcessor = new UnderSamplingByMaskingPreProcessor(someTargets[i], ((shortSeq) / 2));
            dToPreProcess = d.copy();
            preProcessor.preProcess(dToPreProcess);
            INDArray exp = Nd4j.zeros(dToPreProcess.getLabelsMaskArray().shape());
            INDArray lm = dToPreProcess.getLabelsMaskArray();
            Assert.assertEquals(exp, lm);
            // change default and check distribution which should be 1-targetMinorityDist
            preProcessor.donotMaskAllMajorityWindows();
            dToPreProcess = d.copy();
            preProcessor.preProcess(dToPreProcess);
            INDArray percentagesNow = dToPreProcess.getLabelsMaskArray().sum(1).div(shortSeq);
            Assert.assertTrue(Nd4j.valueArrayOf(percentagesNow.shape(), (1 - (someTargets[i]))).castTo(Nd4j.defaultFloatingPointType()).equalsWithEps(percentagesNow, tolerancePerc));
        }
    }

    @Test
    public void allMinority() {
        float[] someTargets = new float[]{ 0.01F, 0.1F, 0.5F };
        DataSet d = allMinorityDataSet(false);
        DataSet dToPreProcess;
        for (int i = 0; i < (someTargets.length); i++) {
            UnderSamplingByMaskingPreProcessor preProcessor = new UnderSamplingByMaskingPreProcessor(someTargets[i], ((shortSeq) / 2));
            dToPreProcess = d.copy();
            preProcessor.preProcess(dToPreProcess);
            // all minority classes present  - check that no time steps are masked
            Assert.assertEquals(Nd4j.ones(minibatchSize, shortSeq), dToPreProcess.getLabelsMaskArray());
            // check behavior with override minority - now these are seen as all majority classes
            preProcessor.overrideMinorityDefault();
            preProcessor.donotMaskAllMajorityWindows();
            dToPreProcess = d.copy();
            preProcessor.preProcess(dToPreProcess);
            INDArray percentagesNow = dToPreProcess.getLabelsMaskArray().sum(1).div(shortSeq);
            Assert.assertTrue(Nd4j.valueArrayOf(percentagesNow.shape(), (1 - (someTargets[i]))).castTo(Nd4j.defaultFloatingPointType()).equalsWithEps(percentagesNow, tolerancePerc));
        }
    }

    /* Different distribution of labels within a minibatch, different time series length within a minibatch
    Checks distribution of classes after preprocessing
     */
    @Test
    public void mixedDist() {
        UnderSamplingByMaskingPreProcessor preProcessor = new UnderSamplingByMaskingPreProcessor(targetDist, window);
        DataSet dataSet = knownDistVariedDataSet(new float[]{ 0.1F, 0.2F, 0.8F }, false);
        // Call preprocess for the same dataset multiple times to mimic calls with .next() and checks total distribution
        int loop = 2;
        for (int i = 0; i < loop; i++) {
            // preprocess dataset
            DataSet dataSetToPreProcess = dataSet.copy();
            INDArray labelsBefore = dataSetToPreProcess.getLabels().dup();
            preProcessor.preProcess(dataSetToPreProcess);
            INDArray labels = dataSetToPreProcess.getLabels();
            Assert.assertEquals(labelsBefore, labels);
            // check masks are zero where there are no time steps
            INDArray masks = dataSetToPreProcess.getLabelsMaskArray();
            INDArray shouldBeAllZeros = masks.get(NDArrayIndex.interval(0, 3), NDArrayIndex.interval(shortSeq, longSeq));
            Assert.assertEquals(Nd4j.zeros(shouldBeAllZeros.shape()), shouldBeAllZeros);
            // check distribution of masks in window, going backwards from last time step
            for (int j = ((int) (Math.ceil((((double) (longSeq)) / (window))))); j > 0; j--) {
                // collect mask and labels
                int maxIndex = Math.min(longSeq, (j * (window)));
                int minIndex = Math.min(0, (maxIndex - (window)));
                INDArray maskWindow = masks.get(NDArrayIndex.all(), NDArrayIndex.interval(minIndex, maxIndex));
                INDArray labelWindow = labels.get(NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.interval(minIndex, maxIndex));
                // calc minority class distribution
                INDArray minorityDist = labelWindow.mul(maskWindow).sum(1).div(maskWindow.sum(1));
                if (j < ((shortSeq) / (window))) {
                    Assert.assertEquals(((("Failed on window " + j) + " batch 0, loop ") + i), targetDist, minorityDist.getFloat(0), tolerancePerc);// should now be close to target dist

                    Assert.assertEquals(((("Failed on window " + j) + " batch 1, loop ") + i), targetDist, minorityDist.getFloat(1), tolerancePerc);// should now be close to target dist

                    Assert.assertEquals(((("Failed on window " + j) + " batch 2, loop ") + i), 0.8, minorityDist.getFloat(2), tolerancePerc);// should be unchanged as it was already above target dist

                }
                Assert.assertEquals(((("Failed on window " + j) + " batch 3, loop ") + i), targetDist, minorityDist.getFloat(3), tolerancePerc);// should now be close to target dist

                Assert.assertEquals(((("Failed on window " + j) + " batch 4, loop ") + i), targetDist, minorityDist.getFloat(4), tolerancePerc);// should now be close to target dist

                Assert.assertEquals(((("Failed on window " + j) + " batch 5, loop ") + i), 0.8, minorityDist.getFloat(5), tolerancePerc);// should be unchanged as it was already above target dist

            }
        }
    }

    /* Same as above but with one hot vectors instead of label size = 1
    Also checks minority override
     */
    @Test
    public void mixedDistOneHot() {
        // preprocessor should give 30% minority class for every "window"
        UnderSamplingByMaskingPreProcessor preProcessor = new UnderSamplingByMaskingPreProcessor(targetDist, window);
        preProcessor.overrideMinorityDefault();
        // construct a dataset with known distribution of minority class and varying time steps
        DataSet dataSet = knownDistVariedDataSet(new float[]{ 0.9F, 0.8F, 0.2F }, true);
        // Call preprocess for the same dataset multiple times to mimic calls with .next() and checks total distribution
        int loop = 10;
        for (int i = 0; i < loop; i++) {
            // preprocess dataset
            DataSet dataSetToPreProcess = dataSet.copy();
            preProcessor.preProcess(dataSetToPreProcess);
            INDArray labels = dataSetToPreProcess.getLabels();
            INDArray masks = dataSetToPreProcess.getLabelsMaskArray();
            // check masks are zero where there were no time steps
            INDArray shouldBeAllZeros = masks.get(NDArrayIndex.interval(0, 3), NDArrayIndex.interval(shortSeq, longSeq));
            Assert.assertEquals(Nd4j.zeros(shouldBeAllZeros.shape()), shouldBeAllZeros);
            // check distribution of masks in the window length, going backwards from last time step
            for (int j = ((int) (Math.ceil((((double) (longSeq)) / (window))))); j > 0; j--) {
                // collect mask and labels
                int maxIndex = Math.min(longSeq, (j * (window)));
                int minIndex = Math.min(0, (maxIndex - (window)));
                INDArray maskWindow = masks.get(NDArrayIndex.all(), NDArrayIndex.interval(minIndex, maxIndex));
                INDArray labelWindow = labels.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(minIndex, maxIndex));
                // calc minority class distribution after accounting for masks
                INDArray minorityClass = labelWindow.get(NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.all()).mul(maskWindow);
                INDArray majorityClass = labelWindow.get(NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.all()).mul(maskWindow);
                INDArray minorityDist = minorityClass.sum(1).div(majorityClass.add(minorityClass).sum(1));
                if (j < ((shortSeq) / (window))) {
                    Assert.assertEquals(((("Failed on window " + j) + " batch 0, loop ") + i), targetDist, minorityDist.getFloat(0), tolerancePerc);// should now be close to target dist

                    Assert.assertEquals(((("Failed on window " + j) + " batch 1, loop ") + i), targetDist, minorityDist.getFloat(1), tolerancePerc);// should now be close to target dist

                    Assert.assertEquals(((("Failed on window " + j) + " batch 2, loop ") + i), 0.8, minorityDist.getFloat(2), tolerancePerc);// should be unchanged as it was already above target dist

                }
                Assert.assertEquals(((("Failed on window " + j) + " batch 3, loop ") + i), targetDist, minorityDist.getFloat(3), tolerancePerc);// should now be close to target dist

                Assert.assertEquals(((("Failed on window " + j) + " batch 4, loop ") + i), targetDist, minorityDist.getFloat(4), tolerancePerc);// should now be close to target dist

                Assert.assertEquals(((("Failed on window " + j) + " batch 5, loop ") + i), 0.8, minorityDist.getFloat(5), tolerancePerc);// should be unchanged as it was already above target dist

            }
        }
    }

    // all the tests above into one multidataset
    @Test
    public void testForMultiDataSet() {
        DataSet dataSetA = knownDistVariedDataSet(new float[]{ 0.8F, 0.1F, 0.2F }, false);
        DataSet dataSetB = knownDistVariedDataSet(new float[]{ 0.2F, 0.9F, 0.8F }, true);
        HashMap<Integer, Double> targetDists = new HashMap<>();
        targetDists.put(0, 0.5);// balance inputA

        targetDists.put(1, 0.3);// inputB dist = 0.2%

        UnderSamplingByMaskingMultiDataSetPreProcessor maskingMultiDataSetPreProcessor = new UnderSamplingByMaskingMultiDataSetPreProcessor(targetDists, window);
        maskingMultiDataSetPreProcessor.overrideMinorityDefault(1);
        MultiDataSet multiDataSet = fromDataSet(dataSetA, dataSetB);
        maskingMultiDataSetPreProcessor.preProcess(multiDataSet);
        INDArray labels;
        INDArray minorityCount;
        INDArray seqCount;
        INDArray minorityDist;
        // datasetA
        labels = multiDataSet.getLabels(0).reshape(((minibatchSize) * 2), longSeq).mul(multiDataSet.getLabelsMaskArray(0));
        minorityCount = labels.sum(1);
        seqCount = multiDataSet.getLabelsMaskArray(0).sum(1);
        minorityDist = minorityCount.div(seqCount);
        Assert.assertEquals(minorityDist.getDouble(1), 0.5, tolerancePerc);
        Assert.assertEquals(minorityDist.getDouble(2), 0.5, tolerancePerc);
        Assert.assertEquals(minorityDist.getDouble(4), 0.5, tolerancePerc);
        Assert.assertEquals(minorityDist.getDouble(5), 0.5, tolerancePerc);
        // datasetB - override is switched so grab index=0
        labels = multiDataSet.getLabels(1).get(NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.all()).mul(multiDataSet.getLabelsMaskArray(1));
        minorityCount = labels.sum(1);
        seqCount = multiDataSet.getLabelsMaskArray(1).sum(1);
        minorityDist = minorityCount.div(seqCount);
        Assert.assertEquals(minorityDist.getDouble(1), 0.3, tolerancePerc);
        Assert.assertEquals(minorityDist.getDouble(2), 0.3, tolerancePerc);
        Assert.assertEquals(minorityDist.getDouble(4), 0.3, tolerancePerc);
        Assert.assertEquals(minorityDist.getDouble(5), 0.3, tolerancePerc);
    }
}

