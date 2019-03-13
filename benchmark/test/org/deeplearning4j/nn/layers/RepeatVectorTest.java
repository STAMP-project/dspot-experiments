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


import java.util.Arrays;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;


public class RepeatVectorTest {
    private int REPEAT = 4;

    @Test
    public void testRepeatVector() {
        double[] arr = new double[]{ 1.0, 2.0, 3.0, 1.0, 2.0, 3.0, 1.0, 2.0, 3.0, 1.0, 2.0, 3.0 };
        INDArray expectedOut = Nd4j.create(arr, new long[]{ 1, 3, REPEAT }, 'f');
        INDArray input = Nd4j.create(new double[]{ 1.0, 2.0, 3.0 }, new long[]{ 1, 3 });
        Layer layer = getRepeatVectorLayer();
        INDArray output = layer.activate(input, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertTrue(Arrays.equals(expectedOut.shape(), output.shape()));
        Assert.assertEquals(expectedOut, output);
        INDArray epsilon = Nd4j.ones(1, 3, 4);
        Pair<Gradient, INDArray> out = layer.backpropGradient(epsilon, LayerWorkspaceMgr.noWorkspaces());
        INDArray outEpsilon = out.getSecond();
        INDArray expectedEpsilon = Nd4j.create(new double[]{ 4.0, 4.0, 4.0 }, new long[]{ 1, 3 });
        Assert.assertEquals(expectedEpsilon, outEpsilon);
    }
}

