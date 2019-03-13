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
package org.deeplearning4j.nn.layers.convolution;


import ConvolutionMode.Same;
import ConvolutionMode.Strict;
import java.util.Arrays;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author Max Pumperla
 */
public class Convolution3DTest {
    private int nExamples = 1;

    private int nChannelsOut = 1;

    private int nChannelsIn = 1;

    private int inputDepth = 2 * 2;

    private int inputWidth = 28 / 2;

    private int inputHeight = 28 / 2;

    private int[] kernelSize = new int[]{ 2, 2, 2 };

    private int outputDepth = ((inputDepth) - (kernelSize[0])) + 1;

    private int outputHeight = ((inputHeight) - (kernelSize[1])) + 1;

    private int outputWidth = ((inputWidth) - (kernelSize[2])) + 1;

    private INDArray epsilon = Nd4j.ones(nExamples, nChannelsOut, outputDepth, outputHeight, outputWidth);

    @Test
    public void testConvolution3dForwardSameMode() {
        INDArray containedInput = getContainedData();
        Convolution3DLayer layer = ((Convolution3DLayer) (getConvolution3DLayer(Same)));
        Assert.assertTrue(((layer.convolutionMode) == (ConvolutionMode.Same)));
        INDArray containedOutput = layer.activate(containedInput, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertTrue(Arrays.equals(containedInput.shape(), containedOutput.shape()));
    }

    @Test
    public void testConvolution3dForwardValidMode() throws Exception {
        Convolution3DLayer layer = ((Convolution3DLayer) (getConvolution3DLayer(Strict)));
        Assert.assertTrue(((layer.convolutionMode) == (ConvolutionMode.Strict)));
        INDArray input = getData();
        INDArray output = layer.activate(input, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertTrue(Arrays.equals(new long[]{ nExamples, nChannelsOut, outputDepth, outputWidth, outputHeight }, output.shape()));
    }
}

