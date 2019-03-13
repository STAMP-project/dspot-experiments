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
package org.deeplearning4j.nn.layers;


import java.util.Map;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by nyghtowl on 11/15/15.
 */
public class BaseLayerTest extends BaseDL4JTest {
    protected INDArray weight = Nd4j.create(new double[]{ 0.1, -0.2, -0.15, 0.05 }, new int[]{ 2, 2 });

    protected INDArray bias = Nd4j.create(new double[]{ 0.5, 0.5 }, new int[]{ 1, 2 });

    protected Map<String, INDArray> paramTable;

    @Test
    public void testSetExistingParamsConvolutionSingleLayer() {
        Layer layer = configureSingleLayer();
        Assert.assertNotEquals(paramTable, layer.paramTable());
        layer.setParamTable(paramTable);
        Assert.assertEquals(paramTable, layer.paramTable());
    }

    @Test
    public void testSetExistingParamsDenseMultiLayer() {
        MultiLayerNetwork net = configureMultiLayer();
        for (Layer layer : net.getLayers()) {
            Assert.assertNotEquals(paramTable, layer.paramTable());
            layer.setParamTable(paramTable);
            Assert.assertEquals(paramTable, layer.paramTable());
        }
    }
}

