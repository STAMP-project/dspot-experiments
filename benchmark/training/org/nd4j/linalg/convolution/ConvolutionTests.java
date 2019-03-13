/**
 * -
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 */
package org.nd4j.linalg.convolution;


import Pooling2D.Divisor.EXCLUDE_PADDING;
import Pooling2D.Divisor.INCLUDE_PADDING;
import Pooling2D.Pooling2DType.AVG;
import Pooling2D.Pooling2DType.MAX;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.DynamicCustomOp;
import org.nd4j.linalg.checkutil.NDArrayCreationUtil;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.primitives.Pair;


/**
 * Created by agibsonccc on 9/6/14.
 */
@RunWith(Parameterized.class)
public class ConvolutionTests extends BaseNd4jTest {
    public ConvolutionTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testIm2ColKnownValues() {
        // Input: w=3, h=3, depth=2, minibatch = 2
        // kh=2, kw=2
        /* ----- Input images -----
        example 0:
        depth 0     depth 1
        [ 0  1  2      [ 9 10 11
        3  4  5       12 13 14
        6  7  8]      15 16 17]
        example 1:
        [18 19 20      [27 28 29
        21 22 23       30 31 32
        24 25 26]      33 34 35]

        ----- Expected Output -----
        Shape: [miniBatch,depth,kH,kW,outH,outW]
        - example 0 -
        depth 0                        depth 1
        h0,w0      h0,w1               h0,w0      h0,w1
        0  1     1  2                 9 10      10 11
        3  4     4  5                12 13      13 14

        h1,w0      h1,w1               h1,w0      h1,w1
        3  4     4  5                12 13      13 14
        6  7     7  8                15 16      16 17

        - example 1 -
        depth 0                        depth 1
        h0,w0      h0,w1               h0,w0      h0,w1
        18 19     19 20               27 28      28 29
        21 22     22 23               30 31      31 32

        h1,w0      h1,w1               h1,w0      h1,w1
        21 22     22 23               30 31      31 32
        24 25     25 26               33 34      34 35
         */
        int miniBatch = 2;
        int depth = 2;
        int height = 3;
        int width = 3;
        int outH = 2;
        int outW = 2;
        int kH = 2;
        int kW = 2;
        int sX = 1;
        int sY = 1;
        int pX = 0;
        int pY = 0;
        // Input data: shape [miniBatch,depth,height,width]
        INDArray input = Nd4j.create(new int[]{ miniBatch, depth, height, width }, 'c');
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 0, 1, 2 }, new double[]{ 3, 4, 5 }, new double[]{ 6, 7, 8 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 9, 10, 11 }, new double[]{ 12, 13, 14 }, new double[]{ 15, 16, 17 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 18, 19, 20 }, new double[]{ 21, 22, 23 }, new double[]{ 24, 25, 26 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 27, 28, 29 }, new double[]{ 30, 31, 32 }, new double[]{ 33, 34, 35 } }));
        // Expected data:
        INDArray expected = Nd4j.create(new int[]{ miniBatch, depth, kH, kW, outH, outW }, 'c');
        // Example 0
        // depth 0
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 1 }, new double[]{ 3, 4 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 4, 5 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 3, 4 }, new double[]{ 6, 7 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 4, 5 }, new double[]{ 7, 8 } }));
        // depth 1
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 9, 10 }, new double[]{ 12, 13 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 10, 11 }, new double[]{ 13, 14 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 12, 13 }, new double[]{ 15, 16 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 13, 14 }, new double[]{ 16, 17 } }));
        // Example 1
        // depth 0
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 18, 19 }, new double[]{ 21, 22 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 19, 20 }, new double[]{ 22, 23 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 21, 22 }, new double[]{ 24, 25 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 22, 23 }, new double[]{ 25, 26 } }));
        // depth 1
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 27, 28 }, new double[]{ 30, 31 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 28, 29 }, new double[]{ 31, 32 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 30, 31 }, new double[]{ 33, 34 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 31, 32 }, new double[]{ 34, 35 } }));
        INDArray out = Convolution.im2col(input, kH, kW, sY, sX, pY, pX, false);
        Assert.assertEquals(expected, out);
        // Now: test with a provided results array, where the results array has weird strides
        INDArray out2 = Nd4j.create(new int[]{ miniBatch, depth, outH, outW, kH, kW }, 'c');
        INDArray out2p = out2.permute(0, 1, 4, 5, 2, 3);
        Convolution.im2col(input, kH, kW, sY, sX, pY, pX, false, out2p);
        Assert.assertEquals(expected, out2p);
        INDArray out3 = Nd4j.create(new int[]{ miniBatch, outH, outW, depth, kH, kW }, 'c');
        INDArray out3p = out3.permute(0, 3, 4, 5, 1, 2);
        Convolution.im2col(input, kH, kW, sY, sX, pY, pX, false, out3p);
        Assert.assertEquals(expected, out3p);
    }

    @Test
    public void testIm2ColKnownValuesDilated() {
        // Input: w=4, h=4, depth=1, minibatch = 2, dilation=2, stride 1
        // kh=2, kw=2
        /* ----- Input images -----
        example 0:
        depth 0
        [ 0  1  2  3
        4  5  6  7
        8  9 10 11
        12 13 14 15 ]

        example 1:
        [16 17 18 19
        20 21 22 23
        24 25 26 27
        28 29 30 31 ]

        ----- Expected Output -----
        Shape: [miniBatch,depth,kH,kW,outH,outW]
        - example 0 -
        depth 0
        h0,w0      h0,w1
        0  2     1  3
        8 10     9 11

        h1,w0      h1,w1
        4  6     5  7
        12 14    13 15

        - example 1 -
        depth 0
        h0,w0      h0,w1
        16 18     17 19
        24 26     25 27

        h1,w0      h1,w1
        20 22     21 23
        28 30     29 31
         */
        int miniBatch = 2;
        int depth = 1;
        int height = 4;
        int width = 4;
        int outH = 2;
        int outW = 2;
        int kH = 2;
        int kW = 2;
        int sX = 1;
        int sY = 1;
        int pX = 0;
        int pY = 0;
        int dh = 2;
        int dw = 2;
        // Input data: shape [miniBatch,depth,height,width]
        INDArray input = Nd4j.create(new int[]{ miniBatch, depth, height, width }, 'c');
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 0, 1, 2, 3 }, new double[]{ 4, 5, 6, 7 }, new double[]{ 8, 9, 10, 11 }, new double[]{ 12, 13, 14, 15 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 16, 17, 18, 19 }, new double[]{ 20, 21, 22, 23 }, new double[]{ 24, 25, 26, 27 }, new double[]{ 28, 29, 30, 31 } }));
        // Expected data:
        INDArray expected = Nd4j.create(new int[]{ miniBatch, depth, kH, kW, outH, outW }, 'c');
        // Example 0
        // depth 0
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 2 }, new double[]{ 8, 10 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 1, 3 }, new double[]{ 9, 11 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 4, 6 }, new double[]{ 12, 14 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 5, 7 }, new double[]{ 13, 15 } }));
        // Example 1
        // depth 0
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 16, 18 }, new double[]{ 24, 26 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 17, 19 }, new double[]{ 25, 27 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 20, 22 }, new double[]{ 28, 30 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 21, 23 }, new double[]{ 29, 31 } }));
        INDArray out = Convolution.im2col(input, kH, kW, sY, sX, pY, pX, dh, dw, false);
        Assert.assertEquals(expected, out);
        // Now: test with a provided results array, where the results array has weird strides
        INDArray out2 = Nd4j.create(new int[]{ miniBatch, depth, outH, outW, kH, kW }, 'c');
        INDArray out2p = out2.permute(0, 1, 4, 5, 2, 3);
        Convolution.im2col(input, kH, kW, sY, sX, pY, pX, dh, dw, false, out2p);
        Assert.assertEquals(expected, out2p);
        INDArray out3 = Nd4j.create(new int[]{ miniBatch, outH, outW, depth, kH, kW }, 'c');
        INDArray out3p = out3.permute(0, 3, 4, 5, 1, 2);
        Convolution.im2col(input, kH, kW, sY, sX, pY, pX, dh, dw, false, out3p);
        Assert.assertEquals(expected, out3p);
    }

    @Test
    public void testIm2ColKnownValuesDilatedStrided() {
        // Input: w=5, h=5, depth=1, minibatch = 1, dilation=2, stride 2
        // kh=2, kw=2
        /* ----- Input images -----
        example 0:
        depth 0
        [ 0  1  2  3  4
        5  6  7  8  9
        10 11 12 13 14
        15 16 17 18 19
        20 21 22 23 24 ]

        ----- Expected Output -----
        Shape: [miniBatch,depth,kH,kW,outH,outW]
        - example 0 -
        depth 0
        h0,w0      h0,w1
        0  2     2  4
        10 12    12 14

        h1,w0      h1,w1
        10 12    12 14
        20 22    22 24
         */
        int miniBatch = 1;
        int depth = 1;
        int height = 5;
        int width = 5;
        int outH = 2;
        int outW = 2;
        int kH = 2;
        int kW = 2;
        int sX = 2;
        int sY = 2;
        int pX = 0;
        int pY = 0;
        int dh = 2;
        int dw = 2;
        // Input data: shape [miniBatch,depth,height,width]
        INDArray input = Nd4j.create(new int[]{ miniBatch, depth, height, width }, 'c');
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 0, 1, 2, 3, 4 }, new double[]{ 5, 6, 7, 8, 9 }, new double[]{ 10, 11, 12, 13, 14 }, new double[]{ 15, 16, 17, 18, 19 }, new double[]{ 20, 21, 22, 23, 24 } }));
        // Expected data:
        INDArray expected = Nd4j.create(new int[]{ miniBatch, depth, kH, kW, outH, outW }, 'c');
        // Example 0
        // depth 0
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 2 }, new double[]{ 10, 12 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 2, 4 }, new double[]{ 12, 14 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 10, 12 }, new double[]{ 20, 22 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 12, 14 }, new double[]{ 22, 24 } }));
        INDArray out = Convolution.im2col(input, kH, kW, sY, sX, pY, pX, dh, dw, false);
        Assert.assertEquals(expected, out);
        // Now: test with a provided results array, where the results array has weird strides
        INDArray out2 = Nd4j.create(new int[]{ miniBatch, depth, outH, outW, kH, kW }, 'c');
        INDArray out2p = out2.permute(0, 1, 4, 5, 2, 3);
        Convolution.im2col(input, kH, kW, sY, sX, pY, pX, dh, dw, false, out2p);
        Assert.assertEquals(expected, out2p);
        INDArray out3 = Nd4j.create(new int[]{ miniBatch, outH, outW, depth, kH, kW }, 'c');
        INDArray out3p = out3.permute(0, 3, 4, 5, 1, 2);
        Convolution.im2col(input, kH, kW, sY, sX, pY, pX, dh, dw, false, out3p);
        Assert.assertEquals(expected, out3p);
    }

    @Test
    public void testIm2ColKnownValuesMiniBatch3() {
        // Input: w=3, h=3, depth=2, minibatch = 3
        // kh=2, kw=2
        /* ----- Input images -----
        example 0:
        depth 0     depth 1
        [ 0  1  2      [ 9 10 11
        3  4  5       12 13 14
        6  7  8]      15 16 17]
        example 1:
        [18 19 20      [27 28 29
        21 22 23       30 31 32
        24 25 26]      33 34 35]
        example 2:
        [36 37 38      [45 46 47
        39 40 41       48 49 50
        42 43 44]      51 52 53]


        ----- Expected Output -----
        Shape: [miniBatch,depth,kH,kW,outH,outW]
        - example 0 -
        depth 0                        depth 1
        h0,w0      h0,w1               h0,w0      h0,w1
        0  1     1  2                 9 10      10 11
        3  4     4  5                12 13      13 14

        h1,w0      h1,w1               h1,w0      h1,w1
        3  4     4  5                12 13      13 14
        6  7     7  8                15 16      16 17

        - example 1 -
        depth 0                        depth 1
        h0,w0      h0,w1               h0,w0      h0,w1
        18 19     19 20               27 28      28 29
        21 22     22 23               30 31      31 32

        h1,w0      h1,w1               h1,w0      h1,w1
        21 22     22 23               30 31      31 32
        24 25     25 26               33 34      34 35

        - example 2 -
        depth 0                        depth 1
        h0,w0      h0,w1               h0,w0      h0,w1
        36 37     37 38               45 46      46 47
        39 40     40 41               48 49      49 50

        h1,w0      h1,w1               h1,w0      h1,w1
        39 40     40 41               48 49      49 50
        42 43     43 44               51 52      52 53
         */
        int miniBatch = 3;
        int depth = 2;
        int height = 3;
        int width = 3;
        int outH = 2;
        int outW = 2;
        int kH = 2;
        int kW = 2;
        int sX = 1;
        int sY = 1;
        int pX = 0;
        int pY = 0;
        // Input data: shape [miniBatch,depth,height,width]
        INDArray input = Nd4j.create(new int[]{ miniBatch, depth, height, width }, 'c');
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 0, 1, 2 }, new double[]{ 3, 4, 5 }, new double[]{ 6, 7, 8 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 9, 10, 11 }, new double[]{ 12, 13, 14 }, new double[]{ 15, 16, 17 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 18, 19, 20 }, new double[]{ 21, 22, 23 }, new double[]{ 24, 25, 26 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 27, 28, 29 }, new double[]{ 30, 31, 32 }, new double[]{ 33, 34, 35 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(2), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 36, 37, 38 }, new double[]{ 39, 40, 41 }, new double[]{ 42, 43, 44 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(2), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 45, 46, 47 }, new double[]{ 48, 49, 50 }, new double[]{ 51, 52, 53 } }));
        // Expected data:
        INDArray expected = Nd4j.create(new int[]{ miniBatch, depth, kH, kW, outH, outW }, 'c');
        // Example 0
        // depth 0
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 1 }, new double[]{ 3, 4 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 4, 5 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 3, 4 }, new double[]{ 6, 7 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 4, 5 }, new double[]{ 7, 8 } }));
        // depth 1
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 9, 10 }, new double[]{ 12, 13 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 10, 11 }, new double[]{ 13, 14 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 12, 13 }, new double[]{ 15, 16 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 13, 14 }, new double[]{ 16, 17 } }));
        // Example 1
        // depth 0
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 18, 19 }, new double[]{ 21, 22 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 19, 20 }, new double[]{ 22, 23 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 21, 22 }, new double[]{ 24, 25 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 22, 23 }, new double[]{ 25, 26 } }));
        // depth 1
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 27, 28 }, new double[]{ 30, 31 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 28, 29 }, new double[]{ 31, 32 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 30, 31 }, new double[]{ 33, 34 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 31, 32 }, new double[]{ 34, 35 } }));
        // Example 2
        // depth 0
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(2), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 36, 37 }, new double[]{ 39, 40 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(2), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 37, 38 }, new double[]{ 40, 41 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(2), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 39, 40 }, new double[]{ 42, 43 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(2), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 40, 41 }, new double[]{ 43, 44 } }));
        // depth 1
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(2), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 45, 46 }, new double[]{ 48, 49 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(2), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 46, 47 }, new double[]{ 49, 50 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(2), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 48, 49 }, new double[]{ 51, 52 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(2), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 49, 50 }, new double[]{ 52, 53 } }));
        INDArray out = Convolution.im2col(input, kH, kW, sY, sX, pY, pX, false);
        Assert.assertEquals(expected, out);
        // Now: test with a provided results array, where the results array has weird strides
        INDArray out2 = Nd4j.create(new int[]{ miniBatch, depth, outH, outW, kH, kW }, 'c');
        INDArray out2p = out2.permute(0, 1, 4, 5, 2, 3);
        Convolution.im2col(input, kH, kW, sY, sX, pY, pX, false, out2p);
        Assert.assertEquals(expected, out2p);
        INDArray out3 = Nd4j.create(new int[]{ miniBatch, outH, outW, depth, kH, kW }, 'c');
        INDArray out3p = out3.permute(0, 3, 4, 5, 1, 2);
        Convolution.im2col(input, kH, kW, sY, sX, pY, pX, false, out3p);
        Assert.assertEquals(expected, out3p);
    }

    @Test
    public void testIm2ColSamePadding() {
        // Input: w=3, h=3, depth=2, minibatch = 2, kH/kW = 2, stride=1
        // Idea with same padding:
        // outH = ceil(inH / strideH)
        // outW = ceil(inW / strideW)
        int miniBatch = 2;
        int depth = 2;
        int inH = 3;
        int inW = 3;
        int strideH = 1;
        int strideW = 1;
        int kH = 2;
        int kW = 2;
        int outH = ((int) (Math.ceil((inH / ((double) (strideH))))));
        int outW = ((int) (Math.ceil((inW / ((double) (strideW))))));
        Assert.assertEquals(outH, inH);
        Assert.assertEquals(outW, inW);
        int sumPadHeight = (((outH - 1) * strideH) + kH) - inH;
        int padTop = sumPadHeight / 2;
        int padBottom = sumPadHeight - padTop;
        int sumPadWidth = (((outW - 1) * strideW) + kW) - inW;
        int padLeft = sumPadWidth / 2;
        int padRight = sumPadWidth - padLeft;
        System.out.println(((("Output size: " + outH) + ", ") + outW));
        System.out.println(((("Pad top/bottom: " + padTop) + "\t") + padBottom));
        System.out.println(((("Pad left/right: " + padLeft) + "\t") + padRight));
        /* ----- Input images -----
        example 0:
        depth 0     depth 1
        [ 0  1  2      [ 9 10 11
        3  4  5       12 13 14
        6  7  8]      15 16 17]
        example 1:
        [18 19 20      [27 28 29
        21 22 23       30 31 32
        24 25 26]      33 34 35]

        ----- Expected Output -----
        Shape: [miniBatch,depth,kH,kW,outH,outW]
        - example 0 -
        depth 0                        depth 1
        h0,w0    h0,w1    h0,w2        h0,w0    h0,w1    h0,w2
        0  1     1  2     2  0         9 10    10 11    11  0
        3  4     4  5     5  0        12 13    13 14    14  0

        h1,w0    h1,w1    h1,w2        h1,w0    h1,w1    h1,w2
        3  4     4  5     5  0        12 13    13 14    14  0
        6  7     7  8     8  0        15 16    16 17    17  0

        h2,w0    h2,w1    h2,w2        h2,w0    h2,w1    h2,w2
        6  7     7  8     8  0        15 16    16 17    17  0
        0  0     0  0     0  0         0  0     0  0     0  0

        - example 1 -
        depth 0                        depth 1
        h0,w0     h0,w1    h0,w2        h0,w0    h0,w1    h0,w2
        18 19    19 20    20  0        27 28    28 29    29  0
        21 22    22 23    23  0        30 31    31 32    32  0

        h1,w0     h1,w1    h1,w2        h1,w0    h1,w1    h1,w2
        21 22    22 23    23  0        30 31    31 32    32  0
        24 25    25 26    26  0        33 34    34 35    35  0

        h2,w0     h2,w1    h2,w2        h2,w0    h2,w1    h2,w2
        24 25    25 26    26  0        33 34    34 35    35  0
        0  0     0  0     0  0         0  0     0  0     0  0
         */
        // Input data: shape [miniBatch,depth,height,width]
        INDArray input = Nd4j.create(new int[]{ miniBatch, depth, inH, inW }, 'c');
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 0, 1, 2 }, new double[]{ 3, 4, 5 }, new double[]{ 6, 7, 8 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 9, 10, 11 }, new double[]{ 12, 13, 14 }, new double[]{ 15, 16, 17 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 18, 19, 20 }, new double[]{ 21, 22, 23 }, new double[]{ 24, 25, 26 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 27, 28, 29 }, new double[]{ 30, 31, 32 }, new double[]{ 33, 34, 35 } }));
        // Expected data:
        INDArray expected = Nd4j.create(new int[]{ miniBatch, depth, kH, kW, outH, outW }, 'c');
        // Example 0
        // depth 0
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 1 }, new double[]{ 3, 4 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 4, 5 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 2, 0 }, new double[]{ 5, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 3, 4 }, new double[]{ 6, 7 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 4, 5 }, new double[]{ 7, 8 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 5, 0 }, new double[]{ 8, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 6, 7 }, new double[]{ 0, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 7, 8 }, new double[]{ 0, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 8, 0 }, new double[]{ 0, 0 } }));
        // depth 1
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 9, 10 }, new double[]{ 12, 13 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 10, 11 }, new double[]{ 13, 14 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 11, 0 }, new double[]{ 14, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 12, 13 }, new double[]{ 15, 16 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 13, 14 }, new double[]{ 16, 17 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 14, 0 }, new double[]{ 17, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 15, 16 }, new double[]{ 0, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 16, 17 }, new double[]{ 0, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 17, 0 }, new double[]{ 0, 0 } }));
        // Example 1
        // depth 0
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 18, 19 }, new double[]{ 21, 22 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 19, 20 }, new double[]{ 22, 23 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 20, 0 }, new double[]{ 23, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 21, 22 }, new double[]{ 24, 25 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 22, 23 }, new double[]{ 25, 26 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 23, 0 }, new double[]{ 26, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 24, 25 }, new double[]{ 0, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 25, 26 }, new double[]{ 0, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 26, 0 }, new double[]{ 0, 0 } }));
        // depth 1
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 27, 28 }, new double[]{ 30, 31 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 28, 29 }, new double[]{ 31, 32 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 29, 0 }, new double[]{ 32, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 30, 31 }, new double[]{ 33, 34 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 31, 32 }, new double[]{ 34, 35 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 32, 0 }, new double[]{ 35, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 33, 34 }, new double[]{ 0, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 34, 35 }, new double[]{ 0, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(1), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 35, 0 }, new double[]{ 0, 0 } }));
        // [miniBatch,depth,kH,kW,outH,outW]
        INDArray outAlloc = Nd4j.create(miniBatch, depth, kH, kW, outH, outW);
        INDArray out = Convolution.im2col(input, kH, kW, strideH, strideW, padTop, padLeft, true, outAlloc);
        // System.out.println("Output shape: " + Arrays.toString(out.shape()));
        // 
        // for( int mb = 0; mb<2; mb++ ){
        // for( int d = 0; d<2; d++ ){
        // for( int h=0; h<3; h++ ){
        // for( int w=0; w<3; w++ ){
        // INDArrayIndex[] indx = new INDArrayIndex[]{NDArrayIndex.point(mb),NDArrayIndex.point(d),NDArrayIndex.all(),NDArrayIndex.all(), NDArrayIndex.point(h), NDArrayIndex.point(w)};
        // INDArray e = expected.get(indx);
        // INDArray a = out.get(indx);
        // 
        // System.out.println("minibatch = " + mb + ", depth = " + depth + ", outY = " + h + ", outX = " + w + "\t" + (e.equals(a) ? "ok" : "FAILED"));
        // System.out.println(e);
        // System.out.println(a);
        // System.out.println("\n-------------------------");
        // }
        // }
        // 
        // }
        // }
        Assert.assertEquals(expected, out);
        // Now: test with a provided results array, where the results array has weird strides
        INDArray out2 = Nd4j.create(new int[]{ miniBatch, depth, outH, outW, kH, kW }, 'c');
        INDArray out2p = out2.permute(0, 1, 4, 5, 2, 3);
        Convolution.im2col(input, kH, kW, strideH, strideW, padTop, padLeft, true, out2p);
        Assert.assertEquals(expected, out2p);
        INDArray out3 = Nd4j.create(new int[]{ miniBatch, outH, outW, depth, kH, kW }, 'c');
        INDArray out3p = out3.permute(0, 3, 4, 5, 1, 2);
        Convolution.im2col(input, kH, kW, strideH, strideW, padTop, padLeft, true, out3p);
        Assert.assertEquals(expected, out3p);
        // /////////
        // Finally: Check col2im with the same shapes. This doesn't check the results, more 'does it crash or not'
        INDArray col2imResult = Nd4j.create(input.shape());
        INDArray col2im = Convolution.col2im(out, col2imResult, strideH, strideW, padTop, padLeft, inH, inW, 1, 1);
        System.out.println(Arrays.toString(col2im.data().asDouble()));
    }

    @Test
    public void testIm2ColSamePaddingStride2() {
        // Input: h=3, w=4, depth=2, minibatch = 1, kH/kW = 3, stride=2
        // Idea with same padding:
        // outH = ceil(inH / strideH)
        // outW = ceil(inW / strideW)
        int miniBatch = 1;
        int depth = 2;
        int inH = 3;
        int inW = 4;
        int strideH = 2;
        int strideW = 2;
        int kH = 3;
        int kW = 3;
        int outH = ((int) (Math.ceil((inH / ((double) (strideH))))));
        int outW = ((int) (Math.ceil((inW / ((double) (strideW))))));
        Assert.assertEquals(2, outH);// ceil(3/2) = 2

        Assert.assertEquals(2, outW);// ceil(4/2) = 2

        int sumPadHeight = (((outH - 1) * strideH) + kH) - inH;
        int padTop = sumPadHeight / 2;
        int padBottom = sumPadHeight - padTop;
        Assert.assertEquals(1, padTop);
        Assert.assertEquals(1, padBottom);
        int sumPadWidth = (((outW - 1) * strideW) + kW) - inW;
        int padLeft = sumPadWidth / 2;
        int padRight = sumPadWidth - padLeft;
        Assert.assertEquals(0, padLeft);
        Assert.assertEquals(1, padRight);
        System.out.println(((("Output size: " + outH) + ", ") + outW));
        System.out.println(((("Pad top/bottom: " + padTop) + "\t") + padBottom));
        System.out.println(((("Pad left/right: " + padLeft) + "\t") + padRight));
        /* ----- Input images -----
        example 0:
        depth 0       depth 1
        [ 0  1  2  3      [12 13 14 15
        4  5  6  7       16 17 18 19
        8  9 10 11]      20 21 22 23]

        ----- Expected Output -----
        Shape: [miniBatch,depth,kH,kW,outH,outW]
        - example 0 -
        depth 0                        depth 1
        h0,w0        h0,w1            h0,w0       h0,w1
        0  0  0     0  0  0           0  0  0    0  0  0
        0  1  2     2  3  0          12 13 14   14 15  0
        4  5  6     6  7  0          16 17 18   18 19  0

        h1,w0
        4  5  6     6  7  0          16 17 18   18 19  0
        8  9 10    10 11  0          20 21 22   22 23  0
        0  0  0     0  0  0           0  0  0    0  0  0
         */
        // Input data: shape [miniBatch,depth,height,width]
        INDArray input = Nd4j.create(new int[]{ miniBatch, depth, inH, inW }, 'c');
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 0, 1, 2, 3 }, new double[]{ 4, 5, 6, 7 }, new double[]{ 8, 9, 10, 11 } }));
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 12, 13, 14, 15 }, new double[]{ 16, 17, 18, 19 }, new double[]{ 20, 21, 22, 23 } }));
        // Expected data:
        INDArray expected = Nd4j.create(new int[]{ miniBatch, depth, kH, kW, outH, outW }, 'c');
        // Example 0
        // depth 0
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 0, 1, 2 }, new double[]{ 4, 5, 6 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 2, 3, 0 }, new double[]{ 6, 7, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 4, 5, 6 }, new double[]{ 8, 9, 10 }, new double[]{ 0, 0, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 6, 7, 0 }, new double[]{ 10, 11, 0 }, new double[]{ 0, 0, 0 } }));
        // depth 1
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 12, 13, 14 }, new double[]{ 16, 17, 18 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 14, 15, 0 }, new double[]{ 18, 19, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 16, 17, 18 }, new double[]{ 20, 21, 22 }, new double[]{ 0, 0, 0 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 18, 19, 0 }, new double[]{ 22, 23, 0 }, new double[]{ 0, 0, 0 } }));
        // [miniBatch,depth,kH,kW,outH,outW]
        INDArray outAlloc = Nd4j.create(miniBatch, depth, kH, kW, outH, outW);
        INDArray out = Convolution.im2col(input, kH, kW, strideH, strideW, padTop, padLeft, true, outAlloc);
        // System.out.println("Output shape: " + Arrays.toString(out.shape()));
        // 
        // for( int mb = 0; mb<2; mb++ ){
        // for( int d = 0; d<2; d++ ){
        // for( int h=0; h<3; h++ ){
        // for( int w=0; w<3; w++ ){
        // INDArrayIndex[] indx = new INDArrayIndex[]{NDArrayIndex.point(mb),NDArrayIndex.point(d),NDArrayIndex.all(),NDArrayIndex.all(), NDArrayIndex.point(h), NDArrayIndex.point(w)};
        // INDArray e = expected.get(indx);
        // INDArray a = out.get(indx);
        // 
        // System.out.println("minibatch = " + mb + ", depth = " + depth + ", outY = " + h + ", outX = " + w + "\t" + (e.equals(a) ? "ok" : "FAILED"));
        // System.out.println(e);
        // System.out.println(a);
        // System.out.println("\n-------------------------");
        // }
        // }
        // 
        // }
        // }
        Assert.assertEquals(expected, out);
        // Now: test with a provided results array, where the results array has weird strides
        INDArray out2 = Nd4j.create(new int[]{ miniBatch, depth, outH, outW, kH, kW }, 'c');
        INDArray out2p = out2.permute(0, 1, 4, 5, 2, 3);
        Convolution.im2col(input, kH, kW, strideH, strideW, padTop, padLeft, true, out2p);
        Assert.assertEquals(expected, out2p);
        INDArray out3 = Nd4j.create(new int[]{ miniBatch, outH, outW, depth, kH, kW }, 'c');
        INDArray out3p = out3.permute(0, 3, 4, 5, 1, 2);
        Convolution.im2col(input, kH, kW, strideH, strideW, padTop, padLeft, true, out3p);
        Assert.assertEquals(expected, out3p);
        // /////////
        // Finally: Check col2im with the same shapes. This doesn't check the results, more 'does it crash or not'
        INDArray col2imResult = Nd4j.create(input.shape());
        INDArray col2im = Convolution.col2im(out, col2imResult, strideH, strideW, padTop, padLeft, inH, inW, 1, 1);
        System.out.println(Arrays.toString(col2im.data().asDouble()));
    }

    @Test
    public void testCol2ImSamePaddingStride2() {
        // Input: h=3, w=4, depth=2, minibatch = 1, kH/kW = 3, stride=2
        // Idea with same padding:
        // outH = ceil(inH / strideH)
        // outW = ceil(inW / strideW)
        int miniBatch = 1;
        int depth = 2;
        int inH = 3;
        int inW = 4;
        int strideH = 2;
        int strideW = 2;
        int kH = 3;
        int kW = 3;
        int outH = ((int) (Math.ceil((inH / ((double) (strideH))))));
        int outW = ((int) (Math.ceil((inW / ((double) (strideW))))));
        Assert.assertEquals(2, outH);// ceil(3/2) = 2

        Assert.assertEquals(2, outW);// ceil(4/2) = 2

        int sumPadHeight = (((outH - 1) * strideH) + kH) - inH;
        int padTop = sumPadHeight / 2;
        int padBottom = sumPadHeight - padTop;
        Assert.assertEquals(1, padTop);
        Assert.assertEquals(1, padBottom);
        int sumPadWidth = (((outW - 1) * strideW) + kW) - inW;
        int padLeft = sumPadWidth / 2;
        int padRight = sumPadWidth - padLeft;
        Assert.assertEquals(0, padLeft);
        Assert.assertEquals(1, padRight);
        // System.out.println("Output size: " + outH + ", " + outW);
        // System.out.println("Pad top/bottom: " + padTop + "\t" + padBottom);
        // System.out.println("Pad left/right: " + padLeft + "\t" + padRight);
        /* ----- Input images -----
        example 0:
        depth 0       depth 1
        [ 0  1  2  3      [12 13 14 15
        4  5  6  7       16 17 18 19
        8  9 10 11]      20 21 22 23]

        ----- Expected Output -----
        Shape: [miniBatch,depth,kH,kW,outH,outW]
        - example 0 -
        depth 0                        depth 1
        h0,w0        h0,w1            h0,w0       h0,w1
        0  0  0     0  0  0           0  0  0    0  0  0
        0  1  2     2  3  0          12 13 14   14 15  0
        4  5  6     6  7  0          16 17 18   18 19  0

        h1,w0
        4  5  6     6  7  0          16 17 18   18 19  0
        8  9 10    10 11  0          20 21 22   22 23  0
        0  0  0     0  0  0           0  0  0    0  0  0
         */
        /* Col2im result:

        example 0:
        depth 0           depth 1
        [ 0  1  4  3      [12 13 28 15
        8 10 24 14       32 34 72 38
        8  9 20 11]      20 21 44 23]
         */
        // Input data: shape [miniBatch,depth,height,width]
        // INDArray input = Nd4j.create(new int[]{miniBatch,depth,inH,inW},'c');
        // input.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.point(0),NDArrayIndex.all(), NDArrayIndex.all()}, Nd4j.create(new double[][]{{0,1,2,3},{4,5,6,7},{8,9,10,11}}));
        // input.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.point(1),NDArrayIndex.all(), NDArrayIndex.all()}, Nd4j.create(new double[][]{{12,13,14,15},{16,17,18,19},{20,21,22,23}}));
        INDArray col6d = Nd4j.create(new int[]{ miniBatch, depth, kH, kW, outH, outW }, 'c');
        // Example 0
        // depth 0
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 0, 1, 2 }, new double[]{ 4, 5, 6 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 2, 3, 0 }, new double[]{ 6, 7, 0 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 4, 5, 6 }, new double[]{ 8, 9, 10 }, new double[]{ 0, 0, 0 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 6, 7, 0 }, new double[]{ 10, 11, 0 }, new double[]{ 0, 0, 0 } }));
        // depth 1
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 12, 13, 14 }, new double[]{ 16, 17, 18 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 0, 0, 0 }, new double[]{ 14, 15, 0 }, new double[]{ 18, 19, 0 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 16, 17, 18 }, new double[]{ 20, 21, 22 }, new double[]{ 0, 0, 0 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 18, 19, 0 }, new double[]{ 22, 23, 0 }, new double[]{ 0, 0, 0 } }));
        // Expected result:
        INDArray expected = Nd4j.create(miniBatch, depth, inH, inW);
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 0, 1, 4, 3 }, new double[]{ 8, 10, 24, 14 }, new double[]{ 8, 9, 20, 11 } }));
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 12, 13, 28, 15 }, new double[]{ 32, 34, 72, 38 }, new double[]{ 20, 21, 44, 23 } }));
        INDArray col2imResult = Nd4j.create(miniBatch, depth, inH, inW);
        INDArray col2im = Convolution.col2im(col6d, col2imResult, strideH, strideW, padTop, padLeft, inH, inW, 1, 1);
        Assert.assertEquals(expected, col2im);
    }

    @Test
    public void testCol2ImSamePaddingStride1Dilation2() {
        // Input: h=4, w=5, depth=1, minibatch = 1, kH/kW = 2, stride=1, dilation 2
        // Idea with same padding:
        // outH = ceil(inH / strideH)
        // outW = ceil(inW / strideW)
        int miniBatch = 1;
        int depth = 1;
        int inH = 4;
        int inW = 5;
        int strideH = 1;
        int strideW = 1;
        int dH = 2;
        int dW = 2;
        int kH = 2;
        int kW = 2;
        int effectiveKH = kH + ((kH - 1) * (dH - 1));
        int effectiveKW = kW + ((kW - 1) * (dW - 1));
        int outH = ((int) (Math.ceil((inH / ((double) (strideH))))));
        int outW = ((int) (Math.ceil((inW / ((double) (strideW))))));
        Assert.assertEquals(5, outW);// ceil(5/1) = 5

        Assert.assertEquals(4, outH);// ceil(4/1) = 5

        int sumPadHeight = (((outH - 1) * strideH) + effectiveKH) - inH;
        int padTop = sumPadHeight / 2;
        int padBottom = sumPadHeight - padTop;
        Assert.assertEquals(1, padTop);
        Assert.assertEquals(1, padBottom);
        int sumPadWidth = (((outW - 1) * strideW) + effectiveKW) - inW;
        int padLeft = sumPadWidth / 2;
        int padRight = sumPadWidth - padLeft;
        Assert.assertEquals(1, padLeft);
        Assert.assertEquals(1, padRight);
        // System.out.println("Output size: " + outH + ", " + outW);
        // System.out.println("Pad top/bottom: " + padTop + "\t" + padBottom);
        // System.out.println("Pad left/right: " + padLeft + "\t" + padRight);
        /* ----- Input images -----
        example 0:
        depth 0
        [ 0  1  2  3  4
        5  6  7  8  9
        10 11 12 13 14
        15 16 17 18 19 ]

        Effective input, with padding:
        [ 0  0  0  0  0  0  0
        0  0  1  2  3  4  0
        0  5  6  7  8  9  0
        0 10 11 12 13 14  0
        0 15 16 17 18 19  0
        0  0  0  0  0  0  0]

        ----- Expected Output -----
        Shape: [miniBatch,depth,kH,kW,outH,outW]
        - example 0 -
        depth 0
        h0,w0     h0,w1    h0,w2    h0,w3    h0,w4
        0  0     0  0     0  0     0  0     0  0
        0  6     5  7     6  8     7  9     8  0

        h0,w0     h0,w1    h0,w2    h0,w3    h0,w4
        0  1     0  2     1  3     2  4     3  0
        0 11    10 12    11 13    12 14    13  0

        h0,w0     h0,w1    h0,w2    h0,w3    h0,w4
        0  6     5  7     6  8     7  9     8  0
        0 16    15 17    16 18    17 19    18  0

        h0,w0     h0,w1    h0,w2    h0,w3    h0,w4
        0 11    10 12    11 13    12 14    13  0
        0  0     0  0     0  0     0  0     0  0
         */
        /* Col2im result:

        example 0:
        depth 0
        [ 0  2  4  6  4
        10 24 28 32 18
        20 44 48 52 28
        15 32 34 36 19]
         */
        // Input data: shape [miniBatch,depth,height,width]
        INDArray input = Nd4j.create(new int[]{ miniBatch, depth, inH, inW }, 'c');
        input.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 0, 1, 2, 3, 4 }, new double[]{ 5, 6, 7, 8, 9 }, new double[]{ 10, 11, 12, 13, 14 }, new double[]{ 15, 16, 17, 18, 19 } }));
        INDArray col6d = Nd4j.create(new int[]{ miniBatch, depth, kH, kW, outH, outW }, 'c');
        // Example 0
        // depth 0
        // Iterate over width, then height
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 0 }, new double[]{ 0, 6 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 0, 0 }, new double[]{ 5, 7 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 0, 0 }, new double[]{ 6, 8 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(3) }, Nd4j.create(new double[][]{ new double[]{ 0, 0 }, new double[]{ 7, 9 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(4) }, Nd4j.create(new double[][]{ new double[]{ 0, 0 }, new double[]{ 8, 0 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 1 }, new double[]{ 0, 11 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 0, 2 }, new double[]{ 10, 12 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 1, 3 }, new double[]{ 11, 13 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(3) }, Nd4j.create(new double[][]{ new double[]{ 2, 4 }, new double[]{ 12, 14 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(4) }, Nd4j.create(new double[][]{ new double[]{ 3, 0 }, new double[]{ 13, 0 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 6 }, new double[]{ 0, 16 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 5, 7 }, new double[]{ 15, 17 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 6, 8 }, new double[]{ 16, 18 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(3) }, Nd4j.create(new double[][]{ new double[]{ 7, 9 }, new double[]{ 17, 19 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.point(4) }, Nd4j.create(new double[][]{ new double[]{ 8, 0 }, new double[]{ 18, 0 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(3), NDArrayIndex.point(0) }, Nd4j.create(new double[][]{ new double[]{ 0, 11 }, new double[]{ 0, 0 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(3), NDArrayIndex.point(1) }, Nd4j.create(new double[][]{ new double[]{ 10, 12 }, new double[]{ 0, 0 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(3), NDArrayIndex.point(2) }, Nd4j.create(new double[][]{ new double[]{ 11, 13 }, new double[]{ 0, 0 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(3), NDArrayIndex.point(3) }, Nd4j.create(new double[][]{ new double[]{ 12, 14 }, new double[]{ 0, 0 } }));
        col6d.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(3), NDArrayIndex.point(4) }, Nd4j.create(new double[][]{ new double[]{ 13, 0 }, new double[]{ 0, 0 } }));
        // Check im2col:
        INDArray im2col = Convolution.im2col(input, kH, kW, strideH, strideW, padTop, padLeft, dH, dW, true);
        for (int j = 0; j < outH; j++) {
            for (int i = 0; i < outW; i++) {
                INDArray exp = col6d.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(j), NDArrayIndex.point(i));
                INDArray act = im2col.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(j), NDArrayIndex.point(i));
                if (!(exp.equals(act))) {
                    System.out.println(((i + "\t") + j));
                    System.out.println(exp);
                    System.out.println();
                    System.out.println(act);
                    System.out.println("\n");
                }
            }
        }
        Assert.assertEquals(col6d, im2col);
        // Expected result:
        INDArray expected = Nd4j.create(miniBatch, depth, inH, inW);
        expected.put(new INDArrayIndex[]{ NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all() }, Nd4j.create(new double[][]{ new double[]{ 0, 2, 4, 6, 4 }, new double[]{ 10, 24, 28, 32, 18 }, new double[]{ 20, 44, 48, 52, 28 }, new double[]{ 15, 32, 34, 36, 19 } }));
        INDArray col2imResult = Nd4j.create(miniBatch, depth, inH, inW);
        INDArray col2im = Convolution.col2im(col6d, col2imResult, strideH, strideW, padTop, padLeft, inH, inW, dH, dW);
        Assert.assertEquals(expected, col2im);
    }

    @Test
    public void testConvOutWidthAndHeight() {
        int outSize = Convolution.outSize(2, 1, 1, 2, 1, false);
        Assert.assertEquals(6, outSize);
    }

    @Test
    public void testIm2Col() {
        INDArray linspaced = Nd4j.linspace(1, 16, 16).reshape(2, 2, 2, 2);
        INDArray ret = Convolution.im2col(linspaced, 1, 1, 1, 1, 2, 2, 0, false);
        System.out.println(ret);
    }

    @Test
    public void testCol2Im() {
        int kh = 1;
        int kw = 1;
        int sy = 1;
        int sx = 1;
        int ph = 1;
        int pw = 1;
        INDArray linspaced = Nd4j.linspace(1, 64, 64).reshape(2, 2, 2, 2, 2, 2);
        INDArray newTest = Convolution.col2im(linspaced, sy, sx, ph, pw, 2, 2);
        INDArray assertion = OldConvolution.col2im(linspaced, sy, sx, ph, pw, 2, 2);
        System.out.println(("Ordering of the result, new test: " + (newTest.ordering())));
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
        INDArray in = Nd4j.linspace(1, prod, prod).reshape(nEx, depth, width, height);
        INDArray assertim2col = OldConvolution.im2col(in, kernel, stride, padding);
        INDArray im2col = Convolution.im2col(in, kernel, stride, padding);
        Assert.assertEquals(assertim2col, im2col);
        INDArray assertcol2im = OldConvolution.col2im(im2col, stride, padding, height, width);
        INDArray col2im = Convolution.col2im(im2col, stride, padding, height, width);
        Assert.assertEquals(assertcol2im, col2im);
    }

    @Test
    public void testIm2ColWithDilation() {
        int kH = 2;
        int kW = 2;
        int sH = 1;
        int sW = 1;
        int pH = 0;
        int pW = 0;
        int dH = 1;
        int dW = 2;
        boolean same = false;
        /* Input:
        [ 1,  2,  3
        4,  5,  6
        7,  8,  9 ]

        Im2col:
        [ 1,  3
        4,  6 ]

        [ 4,  6
        7,  9 ]
         */
        INDArray in = Nd4j.create(1, 1, 3, 3);
        in.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all()).assign(Nd4j.linspace(1, 9, 9).reshape('c', 3, 3));
        INDArray out = Nd4j.create(1, 1, 2, 2, 2, 1);// minibatch, depth, kH, kW, outH, outW

        Convolution.im2col(in, kH, kW, sH, sW, pH, pW, dH, dW, same, out);
        INDArray act0 = out.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(0), NDArrayIndex.point(0));
        INDArray act1 = out.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.point(0));
        INDArray exp0 = Nd4j.create(new double[][]{ new double[]{ 1, 3 }, new double[]{ 4, 6 } });
        INDArray exp1 = Nd4j.create(new double[][]{ new double[]{ 4, 6 }, new double[]{ 7, 9 } });
        Assert.assertEquals(exp0, act0);
        Assert.assertEquals(exp1, act1);
    }

    @Test
    public void testPoolingEdgeCases() {
        // Average pooling with same mode: should we include the padded values, when deciding what to divide by?
        // /*** Note: Mode 2 is the "DL4J always divide by kH*kW" approach ***
        /* Input:
        [ 1, 2, 3
        4, 5, 6
        7, 8, 9 ]


        Kernel 2, stride 1
        outH = 3, outW = 3 (i.e., ceil(in/stride)
        totalHPad = (outH-1) * strideH + kH - inH = (3-1)*1 + 2 - 3 = 1
        topPad = 0, bottomPad = 1
        leftPad = 0, rightPad = 1
         */
        for (char inputOrder : new char[]{ 'c', 'f' }) {
            for (char outputOrder : new char[]{ 'c', 'f' }) {
                INDArray input = Nd4j.create(1, 1, 3, 3);
                input.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all()).assign(Nd4j.linspace(1, 9, 9).reshape('c', 3, 3)).dup(inputOrder);
                input = input.dup('c');
                INDArray input2 = Nd4j.create(new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 }, new int[]{ 1, 1, 3, 3 }, 'c');// .dup(inputOrder);

                Assert.assertEquals(input, input2);
                input = input2;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        System.out.print(((input.getDouble(0, 0, i, j)) + ","));
                    }
                    System.out.println();
                }
                System.out.println();
                INDArray sums = Nd4j.create(new double[][]{ new double[]{ ((1 + 2) + 4) + 5, ((2 + 3) + 5) + 6, 3 + 6 }, new double[]{ ((4 + 5) + 7) + 8, ((5 + 6) + 8) + 9, 6 + 9 }, new double[]{ 7 + 8, 8 + 9, 9 } });
                INDArray divEnabled = Nd4j.create(new double[][]{ new double[]{ 4, 4, 2 }, new double[]{ 4, 4, 2 }, new double[]{ 2, 2, 1 } });
                INDArray expEnabled = sums.div(divEnabled);
                INDArray expDl4j = sums.div(4);
                // https://github.com/deeplearning4j/libnd4j/blob/master/include/ops/declarable/generic/convo/pooling/avgpool2d.cpp
                DynamicCustomOp op1 = // ky, kx, sy, sx, py, px, dy, dx, isSameMode, ???, divisor, nchw
                DynamicCustomOp.builder("avgpool2d").addIntegerArguments(new int[]{ 2, 2, 1, 1, 0, 0, 1, 1, 1, 0, 0 }).addInputs(input).addOutputs(Nd4j.create(new int[]{ 1, 1, 3, 3 }, outputOrder)).build();
                DynamicCustomOp op2 = // ky, kx, sy, sx, py, px, dy, dx, isSameMode, ???, divisor, nchw
                DynamicCustomOp.builder("avgpool2d").addIntegerArguments(new int[]{ 2, 2, 1, 1, 0, 0, 1, 1, 1, 2, 0 }).addInputs(input).addOutputs(Nd4j.create(new int[]{ 1, 1, 3, 3 }, outputOrder)).build();
                Nd4j.getExecutioner().exec(op1);
                Nd4j.getExecutioner().exec(op2);
                INDArray actEnabled = op1.getOutputArgument(0);
                INDArray actDl4j = op2.getOutputArgument(0);
                String msg = (("inOrder=" + inputOrder) + ", outOrder=") + outputOrder;
                Assert.assertEquals(msg, expDl4j, actDl4j.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all()));
                Assert.assertEquals(msg, expEnabled, actEnabled.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all()));
            }
        }
    }

    @Test
    public void testPooling1() {
        for (char outputOrder : new char[]{ 'c', 'f' }) {
            INDArray exp = Nd4j.create(new float[]{ 6.0F, 7.0F, 10.0F, 11.0F, 22.0F, 23.0F, 26.0F, 27.0F, 38.0F, 39.0F, 42.0F, 43.0F, 54.0F, 55.0F, 58.0F, 59.0F }, new int[]{ 2, 2, 2, 2 }, 'c');
            int len = ((2 * 4) * 4) * 2;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 2, 4, 4, 2);
            DynamicCustomOp op = // ky, kx, sy, sx, py, px, dy, dx, isSameMode, ???, divisor, nchw
            DynamicCustomOp.builder("avgpool2d").addIntegerArguments(new int[]{ 2, 2, 2, 2, 0, 0, 1, 1, 1, 1, 1 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 2, 2, 2, 2 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
            /* k=2, s=2, p=0, d=1, same mode, divisor = 1


            //c order: strides are descending... i.e., last dimension changes quickest

            //Minibatch 0:
            //Depth 0
            [ 0,  1
            2,  3
            4,  5
            6,  7 ]

            //Depth 1
            [ 8,  9
            10, 11
            12, 13
            14, 15 ]

            //Depth 2
            [16, 17
            18, 19
            20, 21
            22, 23 ]

            //Depth 3
            [24, 25
            26, 27
            28, 29
            30, 31 ]



            //Minibatch 1:
             */
        }
    }

    @Test
    public void testPooling2() {
        for (char outputOrder : new char[]{ 'c', 'f' }) {
            INDArray exp = Nd4j.create(new float[]{ 6.0F, 7.0F, 10.0F, 11.0F, 22.0F, 23.0F, 26.0F, 27.0F, 38.0F, 39.0F, 42.0F, 43.0F, 54.0F, 55.0F, 58.0F, 59.0F }, new int[]{ 2, 2, 2, 2 }, 'c');
            int len = ((2 * 4) * 4) * 2;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 2, 4, 4, 2);
            DynamicCustomOp op = DynamicCustomOp.builder("avgpool2d").addIntegerArguments(new int[]{ 2, 2, 2, 2, 0, 0, 1, 1, 0, 1, 1 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 2, 2, 2, 2 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
        }
    }

    @Test
    public void testPooling3() {
        for (char outputOrder : new char[]{ 'c', 'f' }) {
            INDArray exp = Nd4j.create(new float[]{ 11.0F, 12.0F, 15.0F, 16.0F, 27.0F, 28.0F, 31.0F, 32.0F, 43.0F, 44.0F, 47.0F, 48.0F, 59.0F, 60.0F, 63.0F, 64.0F }, new int[]{ 2, 2, 2, 2 }, 'c');
            int len = ((2 * 4) * 4) * 2;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 2, 4, 4, 2);
            DynamicCustomOp op = DynamicCustomOp.builder("maxpool2d").addIntegerArguments(new int[]{ 2, 2, 2, 2, 0, 0, 1, 1, 1, 1, 1 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 2, 2, 2, 2 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
        }
    }

    @Test
    public void testPooling4() {
        for (char outputOrder : new char[]{ 'c', 'f' }) {
            INDArray exp = Nd4j.create(new float[]{ 11.0F, 12.0F, 15.0F, 16.0F, 27.0F, 28.0F, 31.0F, 32.0F, 43.0F, 44.0F, 47.0F, 48.0F, 59.0F, 60.0F, 63.0F, 64.0F }, new int[]{ 2, 2, 2, 2 }, 'c');
            int len = ((2 * 4) * 4) * 2;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 2, 4, 4, 2);
            DynamicCustomOp op = DynamicCustomOp.builder("maxpool2d").addIntegerArguments(new int[]{ 2, 2, 2, 2, 0, 0, 1, 1, 0, 1, 1 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 2, 2, 2, 2 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
        }
    }

    @Test
    public void testPooling5() {
        for (char outputOrder : new char[]{ 'c', 'f' }) {
            INDArray exp = Nd4j.create(new float[]{ 7.0F, 8.0F, 11.0F, 12.0F, 14.0F, 15.0F, 27.0F, 28.0F, 31.0F, 32.0F, 34.0F, 35.0F, 42.0F, 43.0F, 46.0F, 47.0F, 49.0F, 50.0F, 57.0F, 58.0F, 61.0F, 62.0F, 64.0F, 65.0F, 77.0F, 78.0F, 81.0F, 82.0F, 84.0F, 85.0F, 92.0F, 93.0F, 96.0F, 97.0F, 99.0F, 100.0F }, new int[]{ 2, 3, 3, 2 }, 'c');
            int len = ((2 * 5) * 5) * 2;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 2, 5, 5, 2);
            DynamicCustomOp op = DynamicCustomOp.builder("avgpool2d").addIntegerArguments(new int[]{ 2, 2, 2, 2, 0, 0, 1, 1, 1, 0, 1 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 2, 3, 3, 2 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
        }
    }

    @Test
    public void testPooling6() {
        for (char outputOrder : new char[]{ 'c', 'f' }) {
            INDArray exp = Nd4j.create(new float[]{ 7.0F, 8.0F, 11.0F, 12.0F, 27.0F, 28.0F, 31.0F, 32.0F, 57.0F, 58.0F, 61.0F, 62.0F, 77.0F, 78.0F, 81.0F, 82.0F }, new int[]{ 2, 2, 2, 2 }, 'c');
            int len = ((2 * 5) * 5) * 2;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 2, 5, 5, 2);
            DynamicCustomOp op = DynamicCustomOp.builder("avgpool2d").addIntegerArguments(new int[]{ 2, 2, 2, 2, 0, 0, 1, 1, 0, 1, 1 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 2, 2, 2, 2 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
        }
    }

    @Test
    public void testPooling7() {
        for (char outputOrder : new char[]{ 'c', 'f' }) {
            INDArray exp = Nd4j.create(new float[]{ 7.0F, 9.0F, 17.0F, 19.0F, 32.0F, 34.0F, 42.0F, 44.0F, 57.0F, 59.0F, 67.0F, 69.0F, 82.0F, 84.0F, 92.0F, 94.0F }, new int[]{ 2, 2, 2, 2 }, 'c');
            int len = ((2 * 2) * 5) * 5;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 2, 2, 5, 5);
            DynamicCustomOp op = DynamicCustomOp.builder("maxpool2d").addIntegerArguments(new int[]{ 2, 2, 2, 2, 0, 0, 1, 1, 0, 1, 0 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 2, 2, 2, 2 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
        }
    }

    @Test
    public void testPooling8() {
        for (char outputOrder : new char[]{ 'c', 'f' }) {
            INDArray exp = Nd4j.create(new float[]{ 1.0F, 2.5F, 4.5F, 8.5F, 10.0F, 12.0F, 18.5F, 20.0F, 22.0F, 26.0F, 27.5F, 29.5F, 33.5F, 35.0F, 37.0F, 43.5F, 45.0F, 47.0F, 51.0F, 52.5F, 54.5F, 58.5F, 60.0F, 62.0F, 68.5F, 70.0F, 72.0F, 76.0F, 77.5F, 79.5F, 83.5F, 85.0F, 87.0F, 93.5F, 95.0F, 97.0F }, new int[]{ 2, 2, 3, 3 }, 'c');
            int len = ((2 * 2) * 5) * 5;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 2, 2, 5, 5);
            DynamicCustomOp op = DynamicCustomOp.builder("avgpool2d").addIntegerArguments(new int[]{ 2, 2, 2, 2, 1, 1, 1, 1, 0, 0, 0 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 2, 2, 3, 3 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
        }
    }

    @Test
    public void testPooling9() {
        for (char outputOrder : new char[]{ 'c', 'f' }) {
            INDArray exp = Nd4j.create(new float[]{ 0.25F, 1.25F, 2.25F, 4.25F, 10.0F, 12.0F, 9.25F, 20.0F, 22.0F, 6.5F, 13.75F, 14.75F, 16.75F, 35.0F, 37.0F, 21.75F, 45.0F, 47.0F, 12.75F, 26.25F, 27.25F, 29.25F, 60.0F, 62.0F, 34.25F, 70.0F, 72.0F, 19.0F, 38.75F, 39.75F, 41.75F, 85.0F, 87.0F, 46.75F, 95.0F, 97.0F }, new int[]{ 2, 2, 3, 3 }, 'c');
            int len = ((2 * 2) * 5) * 5;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 2, 2, 5, 5);
            DynamicCustomOp op = DynamicCustomOp.builder("avgpool2d").addIntegerArguments(new int[]{ 2, 2, 2, 2, 1, 1, 1, 1, 0, 1, 0 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 2, 2, 3, 3 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
        }
    }

    @Test
    public void testPooling10() {
        for (char outputOrder : new char[]{ 'c', 'f' }) {
            INDArray exp = Nd4j.create(new float[]{ 4.0F, 6.0F, 7.5F, 14.0F, 16.0F, 17.5F, 21.5F, 23.5F, 25.0F, 29.0F, 31.0F, 32.5F, 39.0F, 41.0F, 42.5F, 46.5F, 48.5F, 50.0F, 54.0F, 56.0F, 57.5F, 64.0F, 66.0F, 67.5F, 71.5F, 73.5F, 75.0F, 79.0F, 81.0F, 82.5F, 89.0F, 91.0F, 92.5F, 96.5F, 98.5F, 100.0F }, new int[]{ 2, 2, 3, 3 }, 'c');
            int len = ((2 * 2) * 5) * 5;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 2, 2, 5, 5);
            DynamicCustomOp op = DynamicCustomOp.builder("avgpool2d").addIntegerArguments(new int[]{ 2, 2, 2, 2, 0, 0, 1, 1, 1, 0, 0 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 2, 2, 3, 3 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
        }
    }

    @Test
    public void testPooling11() {
        for (char outputOrder : new char[]{ 'c', 'f' }) {
            INDArray exp = Nd4j.create(new float[]{ 3, 4, 6, 7 }, new int[]{ 1, 1, 2, 2 }, 'c');
            int len = ((1 * 1) * 3) * 3;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 1, 1, 3, 3);
            DynamicCustomOp op = DynamicCustomOp.builder("avgpool2d").addIntegerArguments(new int[]{ 2, 2, 1, 1, 0, 0, 1, 1, 0, 0, 0 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 1, 1, 2, 2 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
        }
    }

    @Test
    public void testPooling12() {
        for (char outputOrder : new char[]{ 'c', 'f' }) {
            INDArray exp = Nd4j.create(new float[]{ 3.0F, 4.0F, 4.5F, 6.0F, 7.0F, 7.5F, 7.5F, 8.5F, 9.0F }, new int[]{ 1, 1, 3, 3 }, 'c');
            int len = ((1 * 1) * 3) * 3;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 1, 1, 3, 3);
            DynamicCustomOp op = DynamicCustomOp.builder("avgpool2d").addIntegerArguments(new int[]{ 2, 2, 1, 1, 0, 0, 1, 1, 1, 0, 0 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 1, 1, 3, 3 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
        }
    }

    @Test
    public void testPooling13() {
        for (char outputOrder : new char[]{ 'c' }) {
            INDArray exp = Nd4j.create(new float[]{ 3.0F, 4.0F, 4.5F, 6.0F, 7.0F, 7.5F, 7.5F, 8.5F, 9.0F }, new int[]{ 1, 1, 3, 3 }, 'c');
            int len = ((1 * 1) * 3) * 3;
            INDArray x = Nd4j.linspace(1, len, len).reshape('c', 1, 1, 3, 3);
            DynamicCustomOp op = DynamicCustomOp.builder("avgpool2d").addIntegerArguments(new int[]{ 2, 2, 1, 1, 0, 0, 1, 1, 1, 0, 0 }).addInputs(x).addOutputs(Nd4j.create(new int[]{ 1, 1, 3, 3 }, outputOrder)).build();
            Nd4j.getExecutioner().exec(op);
            INDArray out = op.getOutputArgument(0);
            Assert.assertEquals(("Output order: " + outputOrder), exp, out);
        }
    }

    @Test
    public void testPoolingDilation() {
        int[] inputShape = new int[]{ 1, 1, 4, 5 };
        int outH = inputShape[2];
        int outW = inputShape[3];
        int[] kernel = new int[]{ 2, 2 };
        int[] strides = new int[]{ 1, 1 };
        int[] pad = new int[]{ 1, 1 };// From same mode

        int[] dilation = new int[]{ 2, 2 };
        boolean same = true;
        /* Input:
        [ 1,  2,  3,  4,  5
        6,  7,  8,  9, 10
        11, 12, 13, 14, 15
        16, 17, 18, 19, 20 ]

        Input with SAME padding:
        [ 0,  0,  0,  0,  0,  0,  0
        0,  1,  2,  3,  4,  5,  0
        0,  6,  7,  8,  9, 10,  0
        0, 11, 12, 13, 14, 15,  0
        0, 16, 17, 18, 19, 20,  0
        0,  0,  0,  0,  0,  0,  0]

        4x5 in
        Same mode, stride 1, dilation 2, kernel 2
        kHEffective = (2 + (2-1)*(2-1)) = 3
        oH = ceil(iH/sH) = 4
        oW = ceil(iW/sW) = 5
        totalPadH = (oH-1)*sH + kH - inH = (4-1)*1 + 3 - 4 = 2
        padTop = 1, padBottom = 1

        totalPadW = (oW-1)*sW + kW - inW = (5-1)*1 + 3 - 5 = 2
        padLeft = 1, padRight = 1

        [ 0,  0]    [ 0,  0]    [ 0,  0]    [ 0,  0]    [ 0,  0]
        [ 0,  7]    [ 6,  8]    [ 7,  9]    [ 8, 10]    [ 9,  0]

        [ 0   2]    [ 1,  3]    [ 2,  4]    [ 3,  5]    [ 4,  0]
        [ 0, 12]    [11, 13]    [12, 14]    [13, 15]    [14,  0]

        [ 0,  7]    [ 6,  8]    [ 7,  9]    [ 8, 10]    [ 9,  0]
        [ 0, 17]    [16, 18]    [17, 19]    [18, 20]    [19,  0]

        [ 0, 12]    [11, 13]    [12, 14]    [13, 15]    [14,  0]
        [ 0,  0],   [ 0,  0]    [ 0,  0]    [ 0,  0]    [ 0,  0]
         */
        INDArray origInput = Nd4j.create(inputShape);
        origInput.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all()).assign(Nd4j.linspace(1, 20, 20).reshape('c', 4, 5));
        INDArray expMax = Nd4j.create(1, 1, 4, 5);
        expMax.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all()).assign(Nd4j.create(new double[][]{ new double[]{ 7, 8, 9, 10, 9 }, new double[]{ 12, 13, 14, 15, 14 }, new double[]{ 17, 18, 19, 20, 19 }, new double[]{ 12, 13, 14, 15, 14 } }));
        INDArray sum = Nd4j.create(1, 1, 4, 5);
        sum.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all()).assign(Nd4j.create(new double[][]{ new double[]{ 7, 6 + 8, 7 + 9, 8 + 10, 9 }, new double[]{ 2 + 12, ((1 + 3) + 11) + 13, ((2 + 4) + 12) + 14, ((3 + 5) + 13) + 15, 4 + 14 }, new double[]{ 7 + 17, ((6 + 8) + 16) + 18, ((7 + 9) + 17) + 19, ((8 + 10) + 18) + 20, 9 + 19 }, new double[]{ 12, 11 + 13, 12 + 14, 13 + 15, 14 } }));
        INDArray expAvgExclude = sum.dup();
        expAvgExclude.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all()).divi(Nd4j.create(new double[][]{ new double[]{ 1, 2, 2, 2, 1 }, new double[]{ 2, 4, 4, 4, 2 }, new double[]{ 2, 4, 4, 4, 2 }, new double[]{ 1, 2, 2, 2, 1 } }));
        INDArray expAvgInclude = sum.div(4.0);
        int testNum = 0;
        for (int i = 0; i < 3; i++) {
            List<Pair<INDArray, String>> inputs = NDArrayCreationUtil.getAll4dTestArraysWithShape(12345, inputShape);
            for (Pair<INDArray, String> pIn : inputs) {
                INDArray input = pIn.getFirst().assign(origInput);
                INDArray out = Nd4j.create(input.shape(), 'c');
                // TODO Test on weird strides also (i.e., remove the dup here)
                input = input.dup('c');
                INDArray exp;
                String mode;
                switch (i) {
                    case 0 :
                        // Max
                        Convolution.pooling2D(input, kernel[0], kernel[1], strides[0], strides[1], pad[0], pad[1], dilation[0], dilation[1], same, MAX, INCLUDE_PADDING, 0.0, outH, outW, out);
                        exp = expMax;
                        mode = "max";
                        break;
                    case 1 :
                        // Avg + mode 0 (exclude padding)
                        Convolution.pooling2D(input, kernel[0], kernel[1], strides[0], strides[1], pad[0], pad[1], dilation[0], dilation[1], same, AVG, EXCLUDE_PADDING, 0.0, outH, outW, out);
                        exp = expAvgExclude;
                        mode = "avg_0";
                        break;
                    case 2 :
                        // Avg + mode 1 (include padding)
                        Convolution.pooling2D(input, kernel[0], kernel[1], strides[0], strides[1], pad[0], pad[1], dilation[0], dilation[1], same, AVG, INCLUDE_PADDING, 0.0, outH, outW, out);
                        exp = expAvgInclude;
                        mode = "avg_2";
                        break;
                    default :
                        throw new RuntimeException();
                }
                String msg = (((("TestNum=" + testNum) + ", Mode: ") + mode) + ", ") + (pIn.getSecond());
                Assert.assertEquals(msg, exp, out);
                testNum++;
            }
        }
    }
}

