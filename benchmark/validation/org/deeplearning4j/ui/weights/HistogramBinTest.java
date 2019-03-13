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
package org.deeplearning4j.ui.weights;


import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class HistogramBinTest {
    @Test
    public void testGetBins() throws Exception {
        INDArray array = Nd4j.create(new double[]{ 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.0 });
        HistogramBin histogram = setBinCount(10).build();
        Assert.assertEquals(0.1, histogram.getMin(), 0.001);
        Assert.assertEquals(1.0, histogram.getMax(), 0.001);
        System.out.println(("Result: " + (histogram.getBins())));
        Assert.assertEquals(2, histogram.getBins().getDouble(9), 0.001);
    }

    @Test
    public void testGetData1() throws Exception {
        INDArray array = Nd4j.create(new double[]{ 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.0 });
        HistogramBin histogram = setBinCount(10).build();
        Assert.assertEquals(0.1, histogram.getMin(), 0.001);
        Assert.assertEquals(1.0, histogram.getMax(), 0.001);
        System.out.println(("Result: " + (histogram.getData())));
        Assert.assertEquals(10, histogram.getData().size());
    }

    @Test
    public void testGetData2() throws Exception {
        INDArray array = Nd4j.create(new double[]{ -1.0F, -0.5F, 0.0F, 0.5F, 1.0F, -1.0F, -0.5F, 0.0F, 0.5F, 1.0F });
        HistogramBin histogram = setBinCount(10).build();
        Assert.assertEquals((-1.0), histogram.getMin(), 0.001);
        Assert.assertEquals(1.0, histogram.getMax(), 0.001);
        System.out.println(("Result: " + (histogram.getData())));
        Assert.assertEquals(10, histogram.getData().size());
        Assert.assertEquals(2, histogram.getData().get(new BigDecimal("1.00")).get());
    }

    @Test
    public void testGetData4() throws Exception {
        INDArray array = Nd4j.create(new double[]{ -1.0F, -0.5F, 0.0F, 0.5F, 1.0F, -1.0F, -0.5F, 0.0F, 0.5F, 1.0F });
        HistogramBin histogram = setBinCount(50).build();
        Assert.assertEquals((-1.0), histogram.getMin(), 0.001);
        Assert.assertEquals(1.0, histogram.getMax(), 0.001);
        System.out.println(("Result: " + (histogram.getData())));
        Assert.assertEquals(50, histogram.getData().size());
        Assert.assertEquals(2, histogram.getData().get(new BigDecimal("1.00")).get());
    }
}

