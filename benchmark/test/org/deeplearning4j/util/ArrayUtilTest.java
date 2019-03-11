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
package org.deeplearning4j.util;


import java.util.Arrays;
import org.deeplearning4j.BaseDL4JTest;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.util.ArrayUtil;


/**
 *
 */
public class ArrayUtilTest extends BaseDL4JTest {
    @Test
    public void testRange() {
        int[] range = ArrayUtil.range(0, 2);
        int[] test = new int[]{ 0, 1 };
        Assert.assertEquals(true, Arrays.equals(test, range));
        int[] test2 = new int[]{ -1, 0 };
        int[] range2 = ArrayUtil.range((-1), 1);
        Assert.assertEquals(true, Arrays.equals(test2, range2));
    }

    @Test
    public void testStrides() {
        int[] shape = new int[]{ 5, 4, 3 };
        int[] cStyleStride = new int[]{ 12, 3, 1 };
        int[] fortranStyleStride = new int[]{ 1, 5, 20 };
        int[] fortranStyleTest = ArrayUtil.calcStridesFortran(shape);
        int[] cStyleTest = ArrayUtil.calcStrides(shape);
        Assert.assertEquals(true, Arrays.equals(cStyleStride, cStyleTest));
        Assert.assertEquals(true, Arrays.equals(fortranStyleStride, fortranStyleTest));
        int[] shape2 = new int[]{ 2, 2 };
        int[] cStyleStride2 = new int[]{ 2, 1 };
        int[] fortranStyleStride2 = new int[]{ 1, 2 };
        int[] cStyleTest2 = ArrayUtil.calcStrides(shape2);
        int[] fortranStyleTest2 = ArrayUtil.calcStridesFortran(shape2);
        Assert.assertEquals(true, Arrays.equals(cStyleStride2, cStyleTest2));
        Assert.assertEquals(true, Arrays.equals(fortranStyleStride2, fortranStyleTest2));
    }
}

