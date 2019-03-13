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
package jcuda.jcublas.ops;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class DoublesTests {
    @Test
    public void testDoubleAxpy1() throws Exception {
        // Nd4j.getConstantHandler().getConstantBuffer(new double[]{7.0});
        Nd4j.getConstantHandler().getConstantBuffer(new int[10]);
        Nd4j.getConstantHandler().getConstantBuffer(new int[7]);
        // Nd4j.getConstantHandler().getConstantBuffer(new double[]{1.0, 63.0});
        INDArray array1 = Nd4j.zeros(63).reshape('f', 7, 9);
        // INDArray array1 = Nd4j.create(7, 9, 'f');
        // array1.assign(0);
        INDArray array2 = Nd4j.create(new double[]{ 0.48634444232816687, 1.4758265649675548, 0.39963731960854953, 1.0023591510099152, 0.7645957605153649, 1.9310904186956557, 1.1878964257563174, 0.9057360169583474, -0.3769285854145248, 0.2946010549062492, 0.46557669032521287, 1.2115125297848275, 0.9569626633310937, 0.3256059072916878, 1.612267239273259, 0.33003744088867437, 1.0449266064014164, -0.00789237850243385, 0.5410744173090415, 2.782774008354224, 1.2842283430247856, 0.9086056301544619, 1.1085112167932198, 0.7433898520033356, 1.2140223632630698, 0.7934105561182277, 1.005842641658745, 0.9997499007926636, -0.1593645983224512, 0.07349026680376536, -0.5085137730665015, 0.850035725832587, 0.24118248705567213, -0.13896796919660326, -0.43713991780505523, 0.6690021182865782, 0.17830184441787855, 0.29319561397733207, -0.1418393347014404, -0.2680684817530423, 0.17735833749207552, -0.004662964475220743, 1.0057286813222013, 0.4512230513884966, 0.9534626972218946, 0.40334611958442246, 1.019885308172407, 1.2501698497386884, 0.7623575059565331, 1.887393331295686, 0.9690210825194697, 2.0731574687887475, 1.0805132391495538, 2.8244644868991746, 3.0849853112831913, 2.2252621118259084, 1.0998660836316718, 0.5441178083600947, 1.0045439544127797, 0.3382649318030707, 1.0090081066003418, 0.5477619833704549, 0.7327435087799476 }).reshape('f', 7, 9);
        long time1 = System.nanoTime();
        Nd4j.getBlasWrapper().axpy(new Double(1.0), array1, array2);
        long time2 = System.nanoTime();
        System.out.println((("AXPY execution time: [" + (time2 - time1)) + "] ns"));
        Assert.assertEquals(0.4863444, array2.getDouble(0), 0.001);
        Assert.assertEquals(1.4758265, array2.getDouble(1), 0.001);
    }
}

