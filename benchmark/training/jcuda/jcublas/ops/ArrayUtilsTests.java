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


import java.util.Arrays;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.ArrayUtil;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
@Slf4j
public class ArrayUtilsTests {
    @Test
    public void testArrayRemoveIndex1() throws Exception {
        // INDArray arraySource = Nd4j.create(new float[]{1,2,3,4,5,6,7,8});
        int[] arraySource = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
        int[] dst = ArrayUtil.removeIndex(arraySource, new int[]{ 0, 1 });
        Assert.assertEquals(6, dst.length);
        Assert.assertEquals(3, dst[0]);
    }

    @Test
    public void testArrayRemoveIndex2() throws Exception {
        // INDArray arraySource = Nd4j.create(new float[]{1,2,3,4,5,6,7,8});
        int[] arraySource = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
        int[] dst = ArrayUtil.removeIndex(arraySource, new int[]{ 0, 7 });
        Assert.assertEquals(6, dst.length);
        Assert.assertEquals(2, dst[0]);
        Assert.assertEquals(7, dst[5]);
    }

    @Test
    public void testArrayRemoveIndex4() throws Exception {
        // INDArray arraySource = Nd4j.create(new float[]{1,2,3,4,5,6,7,8});
        int[] arraySource = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
        int[] dst = ArrayUtil.removeIndex(arraySource, new int[]{ 0 });
        Assert.assertEquals(7, dst.length);
        Assert.assertEquals(2, dst[0]);
        Assert.assertEquals(8, dst[6]);
    }

    @Test
    public void testArrayFlatten1() {
        INDArray arrayC = Nd4j.create(new double[][]{ new double[]{ 3, 5 }, new double[]{ 4, 6 } }, 'c');
        INDArray arrayF = Nd4j.create(new double[][]{ new double[]{ 3, 5 }, new double[]{ 4, 6 } }, 'f');
        System.out.println(("C: " + (Arrays.toString(arrayC.data().asFloat()))));
        System.out.println(("F: " + (Arrays.toString(arrayF.data().asFloat()))));
        Assert.assertEquals(arrayC, arrayF);
    }

    @Test
    public void testInterleavedVector1() {
        int[] vector = ArrayUtil.buildInterleavedVector(new Random(), 11);
        log.error("Vector: {}", vector);
    }

    @Test
    public void testHalfVector1() {
        int[] vector = ArrayUtil.buildHalfVector(new Random(), 12);
        log.error("Vector: {}", vector);
    }
}

