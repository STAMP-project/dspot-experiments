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
package org.nd4j.linalg.convolution;


import DataType.DOUBLE;
import Pooling2D.Divisor.INCLUDE_PADDING;
import Pooling2D.Pooling2DType;
import Pooling2D.Pooling2DType.AVG;
import Pooling2D.Pooling2DType.MAX;
import Pooling2D.Pooling2DType.PNORM;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.layers.convolution.Pooling2D;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.ops.transforms.Transforms;


/**
 * Created by agibsonccc on 9/6/14.
 */
@Slf4j
@RunWith(Parameterized.class)
public class ConvolutionTestsC extends BaseNd4jTest {
    public ConvolutionTestsC(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testConvOutWidthAndHeight() {
        int outSize = Convolution.outSize(2, 1, 1, 2, 1, false);
        Assert.assertEquals(6, outSize);
    }

    @Test
    public void testIm2Col() {
        INDArray linspaced = Nd4j.linspace(1, 16, 16, DOUBLE).reshape(2, 2, 2, 2);
        INDArray ret = Convolution.im2col(linspaced, 1, 1, 1, 1, 2, 2, 0, false);
        INDArray im2colAssertion = Nd4j.create(new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 0.0, 0.0, 0.0, 0.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 5.0, 6.0, 0.0, 0.0, 0.0, 0.0, 7.0, 8.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 9.0, 10.0, 0.0, 0.0, 0.0, 0.0, 11.0, 12.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 13.0, 14.0, 0.0, 0.0, 0.0, 0.0, 15.0, 16.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, new long[]{ 2, 2, 1, 1, 6, 6 });
        Assert.assertEquals(im2colAssertion, ret);
        INDArray col2ImAssertion = Nd4j.create(new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 }, new int[]{ 2, 2, 2, 2 });
        INDArray otherConv = Convolution.col2im(ret, 1, 1, 2, 2, 2, 2);
        Assert.assertEquals(col2ImAssertion, otherConv);
    }

    @Test
    public void testIm2Col2() {
        int kh = 2;
        int kw = 2;
        int ph = 0;
        int pw = 0;
        int sy = 2;
        int sx = 2;
        int depth = 2;
        INDArray assertion = Nd4j.create(new double[]{ 1, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 3, 2, 2, 2, 2, 4, 4, 4, 4, 2, 2, 2, 2, 4, 4, 4, 4, 2, 2, 2, 2, 4, 4, 4, 4, 2, 2, 2, 2, 4, 4, 4, 4 }, new long[]{ 1, 1, 2, 2, 4, 4 });
        INDArray ret = Nd4j.create(new double[]{ 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4 }, new long[]{ 1, 1, 8, 8 });
        INDArray test = Convolution.im2col(ret, kh, kw, sy, sx, ph, pw, 0, false);
        Assert.assertEquals(assertion, test);
    }

    @Test
    public void testPooling2D_Same() {
        int[] miniBatches = new int[]{ 1, 3, 5 };
        int[] depths = new int[]{ 1, 3, 5 };
        int[] inHeights = new int[]{ 5, 21 };
        int[] inWidths = new int[]{ 5, 21 };
        int[] strideH = new int[]{ 1, 2 };
        int[] strideW = new int[]{ 1, 2 };
        int[] sizeW = new int[]{ 1, 2, 3 };
        int[] sizeH = new int[]{ 1, 2, 3 };
        int[] padH = new int[]{ 0 };
        int[] padW = new int[]{ 0 };
        Pooling2D[] types = new Pooling2D.Pooling2DType[]{ Pooling2DType.PNORM, Pooling2DType.AVG, Pooling2DType.MAX };
        int cnt = 0;
        for (Pooling2D.Pooling2DType type : types) {
            log.info("Trying pooling type: [{}]", type);
            for (int m : miniBatches) {
                for (int d : depths) {
                    for (int h : inHeights) {
                        for (int w : inWidths) {
                            for (int sh : strideH) {
                                for (int sw : strideW) {
                                    for (int kh : sizeH) {
                                        for (int kw : sizeW) {
                                            INDArray in = Nd4j.linspace(1, (((m * d) * h) * w), (((m * d) * h) * w), Nd4j.defaultFloatingPointType()).reshape(new int[]{ m, d, h, w });
                                            int[] outSize = ConvolutionTestsC.getOutputSize(in, new int[]{ kh, kw }, new int[]{ sh, sw }, null, true);
                                            // Calculate padding for same mode:
                                            int pHTotal = ((((outSize[0]) - 1) * sh) + kh) - h;
                                            int pWTotal = ((((outSize[1]) - 1) * sw) + kw) - w;
                                            int padTop = pHTotal / 2;
                                            int padLeft = pWTotal / 2;
                                            INDArray col = Nd4j.create(new int[]{ m, d, outSize[0], outSize[1], kh, kw }, 'c');
                                            INDArray col2 = col.permute(0, 1, 4, 5, 2, 3);
                                            // INDArray col = Nd4j.createUninitialized(new int[]{m, d, kH, kW, outSize[0], outSize[1]}, 'c');
                                            // INDArray col2 = col;
                                            Convolution.im2col(in, kh, kw, sh, sw, padTop, padLeft, true, col2);
                                            INDArray col2d = col.reshape('c', (((m * d) * (outSize[0])) * (outSize[1])), (kh * kw));
                                            INDArray output = Nd4j.create(m, d, outSize[0], outSize[1]);
                                            INDArray reduced = null;
                                            switch (type) {
                                                case PNORM :
                                                    int pnorm = 3;
                                                    Transforms.abs(col2d, false);
                                                    Transforms.pow(col2d, pnorm, false);
                                                    reduced = col2d.sum(1);
                                                    Transforms.pow(reduced, (1.0 / pnorm), false);
                                                    Convolution.pooling2D(in, kh, kw, sh, sw, padTop, padLeft, 1, 1, true, PNORM, INCLUDE_PADDING, ((double) (pnorm)), outSize[0], outSize[1], output);
                                                    break;
                                                case MAX :
                                                    Convolution.pooling2D(in, kh, kw, sh, sw, padTop, padLeft, 1, 1, true, MAX, INCLUDE_PADDING, 0.0, outSize[0], outSize[1], output);
                                                    reduced = col2d.max(1);
                                                    break;
                                                case AVG :
                                                    Convolution.pooling2D(in, kh, kw, sh, sw, padTop, padLeft, 1, 1, true, AVG, INCLUDE_PADDING, 0.0, outSize[0], outSize[1], output);
                                                    reduced = col2d.mean(1);
                                                    break;
                                            }
                                            reduced = reduced.reshape('c', m, d, outSize[0], outSize[1]).dup('c');
                                            Assert.assertEquals(("Failed opType: " + type), reduced, output);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testMoreIm2Col2() {
        int kh = 2;
        int kw = 2;
        int ph = 0;
        int pw = 0;
        int sy = 2;
        int sx = 2;
        INDArray ret = Nd4j.create(new double[]{ 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4 }, new long[]{ 1, 1, 8, 8 });
        INDArray assertion = Nd4j.create(new double[]{ 1, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 3, 2, 2, 2, 2, 4, 4, 4, 4, 2, 2, 2, 2, 4, 4, 4, 4, 2, 2, 2, 2, 4, 4, 4, 4, 2, 2, 2, 2, 4, 4, 4, 4 }, new long[]{ 1, 1, 2, 2, 4, 4 });
        INDArray im2colTest = Convolution.im2col(ret, kh, kw, sy, sx, ph, pw, 0, false);
        Assert.assertEquals(assertion, im2colTest);
    }

    @Test
    public void testCol2Im() {
        int kh = 1;
        int kw = 1;
        int sy = 1;
        int sx = 1;
        int ph = 1;
        int pw = 1;
        INDArray linspaced = Nd4j.linspace(1, 64, 64, Nd4j.defaultFloatingPointType()).reshape(2, 2, 2, 2, 2, 2);
        INDArray newTest = Convolution.col2im(linspaced, sy, sx, ph, pw, 2, 2);
        INDArray assertion = OldConvolution.col2im(linspaced, sy, sx, ph, pw, 2, 2);
        System.out.println(("Assertion dimensions: " + (Arrays.toString(assertion.shape()))));
        Assert.assertEquals(assertion, newTest);
    }

    @Test
    public void testimcolim() {
        int nEx = 2;
        int depth = 3;
        int width = 7;
        int height = 7;
        int[] kernel = new int[]{ 3, 2 };
        int[] stride = new int[]{ 2, 3 };
        int[] padding = new int[]{ 1, 2 };
        int prod = ((nEx * depth) * width) * height;
        INDArray in = Nd4j.linspace(1, prod, prod, Nd4j.defaultFloatingPointType()).reshape(nEx, depth, width, height);
        INDArray assertim2col = OldConvolution.im2col(in, kernel, stride, padding);
        INDArray im2col = Convolution.im2col(in, kernel, stride, padding);
        Assert.assertEquals(assertim2col, im2col);
        INDArray assertcol2im = OldConvolution.col2im(im2col, stride, padding, height, width);
        INDArray col2im = Convolution.col2im(im2col, stride, padding, height, width);
        Assert.assertEquals(assertcol2im, col2im);
    }
}

