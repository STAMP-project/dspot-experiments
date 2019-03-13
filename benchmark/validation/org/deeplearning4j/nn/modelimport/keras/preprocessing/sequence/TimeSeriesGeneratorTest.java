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
package org.deeplearning4j.nn.modelimport.keras.preprocessing.sequence;


import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;


public class TimeSeriesGeneratorTest {
    @Test
    public void tsGeneratorTest() throws InvalidKerasConfigurationException {
        INDArray data = Nd4j.create(50, 10);
        INDArray targets = Nd4j.create(50, 10);
        int length = 10;
        int samplingRate = 2;
        int stride = 1;
        int startIndex = 0;
        int endIndex = 49;
        int batchSize = 1;
        boolean shuffle = false;
        boolean reverse = false;
        TimeSeriesGenerator gen = new TimeSeriesGenerator(data, targets, length, samplingRate, stride, startIndex, endIndex, shuffle, reverse, batchSize);
        Assert.assertEquals(length, gen.getLength());
        Assert.assertEquals((startIndex + length), gen.getStartIndex());
        Assert.assertEquals(reverse, gen.isReverse());
        Assert.assertEquals(shuffle, gen.isShuffle());
        Assert.assertEquals(endIndex, gen.getEndIndex());
        Assert.assertEquals(batchSize, gen.getBatchSize());
        Assert.assertEquals(samplingRate, gen.getSamplingRate());
        Assert.assertEquals(stride, gen.getStride());
        Pair<INDArray, INDArray> next = gen.next(0);
    }
}

