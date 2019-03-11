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


import SpaceToDepthLayer.DataFormat;
import java.util.Arrays;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;


public class SpaceToDepthTest extends BaseDL4JTest {
    private int mb = 1;

    private int inDepth = 2;

    private int inputWidth = 2;

    private int inputHeight = 2;

    private int blockSize = 2;

    private DataFormat dataFormat = DataFormat.NCHW;

    private int outDepth = ((inDepth) * (blockSize)) * (blockSize);

    private int outputHeight = (inputHeight) / (blockSize);

    private int outputWidth = (inputWidth) / (blockSize);

    @Test
    public void testSpaceToDepthForward() throws Exception {
        INDArray containedInput = getContainedData();
        INDArray containedExpectedOut = getContainedOutput();
        Layer std = getSpaceToDepthLayer();
        INDArray containedOutput = std.activate(containedInput, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertTrue(Arrays.equals(containedExpectedOut.shape(), containedOutput.shape()));
        Assert.assertEquals(containedExpectedOut, containedOutput);
    }

    @Test
    public void testSpaceToDepthBackward() throws Exception {
        INDArray containedInputEpsilon = getContainedOutput();
        INDArray containedExpectedOut = getContainedData();
        Layer std = getSpaceToDepthLayer();
        std.setInput(getContainedData(), LayerWorkspaceMgr.noWorkspaces());
        INDArray containedOutput = std.backpropGradient(containedInputEpsilon, LayerWorkspaceMgr.noWorkspaces()).getRight();
        Assert.assertTrue(Arrays.equals(containedExpectedOut.shape(), containedOutput.shape()));
        Assert.assertEquals(containedExpectedOut, containedOutput);
    }
}

