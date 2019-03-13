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
package org.deeplearning4j.datasets.iterator;


import org.deeplearning4j.BaseDL4JTest;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.dataset.api.MultiDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.preprocessor.MultiNormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by susaneraly on 6/17/17.
 */
public class CombinedPreProcessorTests extends BaseDL4JTest {
    @Test
    public void somePreProcessorsCombined() {
        INDArray[] featureArr = new INDArray[]{ Nd4j.linspace(100, 200, 20).reshape(10, 2) };
        MultiDataSet multiDataSet = new MultiDataSet(featureArr, null, null, null);
        MultiNormalizerMinMaxScaler minMaxScaler = new MultiNormalizerMinMaxScaler();
        minMaxScaler.fit(multiDataSet);
        CombinedMultiDataSetPreProcessor multiDataSetPreProcessor = new CombinedMultiDataSetPreProcessor.Builder().addPreProcessor(minMaxScaler).addPreProcessor(1, new CombinedPreProcessorTests.addFivePreProcessor()).build();
        multiDataSetPreProcessor.preProcess(multiDataSet);
        Assert.assertEquals(Nd4j.zeros(10, 2).addColumnVector(Nd4j.linspace(0, 1, 10).reshape(10, 1)).addi(5), multiDataSet.getFeatures(0));
    }

    /* Adds five to the features - assumes multidataset here is one feature and one label */
    public final class addFivePreProcessor implements MultiDataSetPreProcessor {
        @Override
        public void preProcess(org.nd4j.linalg.dataset.api.MultiDataSet multiDataSet) {
            multiDataSet.getFeatures(0).addi(5);
        }
    }
}

