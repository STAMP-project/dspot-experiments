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
import org.nd4j.linalg.util.ArrayUtil;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class CudaPairwiseTrainformsTests {
    @Test
    public void testPinnedAddiRowVector() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F });
        INDArray array2 = Nd4j.create(new float[]{ 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F });
        array1.addiRowVector(array2);
        System.out.println(("Array1: " + array1));
        System.out.println(("Array2: " + array2));
        Assert.assertEquals(3.5F, array1.getRow(0).getFloat(0), 0.01);
    }

    @Test
    public void testPinnedDiviRowVector() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F });
        INDArray array2 = Nd4j.create(new float[]{ 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F });
        INDArray result = array1.diviRowVector(array2);
        System.out.println(("Array1: " + array1));
        System.out.println(("Array2: " + array2));
        System.out.println(("Result: " + result));
        Assert.assertEquals(0.75F, array1.getRow(0).getFloat(0), 0.01);
    }

    @Test
    public void testPinnedRDiviRowVector() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F });
        INDArray array2 = Nd4j.create(new float[]{ 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F });
        array1.rdiviRowVector(array2);
        System.out.println(("Array1: " + array1));
        System.out.println(("Array2: " + array2));
        Assert.assertEquals(1.33F, array1.getRow(0).getFloat(0), 0.01);
    }

    @Test
    public void testPinnedSubiRowVector() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F });
        INDArray array2 = Nd4j.create(new float[]{ 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F });
        array1.subiRowVector(array2);
        System.out.println(("Array1: " + array1));
        System.out.println(("Array2: " + array2));
        Assert.assertEquals((-0.5F), array1.getRow(0).getFloat(0), 0.01);
    }

    @Test
    public void testPinnedRSubiRowVector() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F });
        INDArray array2 = Nd4j.create(new float[]{ 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F });
        array1.rsubiRowVector(array2);
        System.out.println(("Array1: " + array1));
        System.out.println(("Array2: " + array2));
        Assert.assertEquals(0.5F, array1.getRow(0).getFloat(0), 0.01);
    }

    @Test
    public void testPinnedMuliRowVector() throws Exception {
        // simple way to stop test if we're not on CUDA backend here
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.create(new float[]{ 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F, 1.5F });
        INDArray array2 = Nd4j.create(new float[]{ 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F });
        array1.muliRowVector(array2);
        System.out.println(("Array1: " + array1));
        System.out.println(("Array2: " + array2));
        Assert.assertEquals(3.0F, array1.getRow(0).getFloat(0), 0.01);
    }

    @Test
    public void testPinnedMuliColumnVector1() throws Exception {
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.linspace(1, 1280, 128000).reshape(128, 1000);
        INDArray array2 = Nd4j.linspace(1, 1280, 128000).reshape(128, 1000).dup('f');
        array1.muli(array2);
        // System.out.println("Array1: " + array1);
        // System.out.println("Array2: " + array2);
    }

    @Test
    public void testPinnedMuliColumnVector2() throws Exception {
        Assert.assertEquals("JcublasLevel1", Nd4j.getBlasWrapper().level1().getClass().getSimpleName());
        INDArray array1 = Nd4j.linspace(1, 1280, 128000).reshape(128, 1000);
        INDArray array2 = Nd4j.linspace(1, 1280, 128000).reshape(128, 1000);
        INDArray arrayTest = array1.dup('f');
        Assert.assertEquals(array1, arrayTest);
        array1.muli(array2);
        // System.out.println("Array1: " + array1);
        Assert.assertNotEquals(array1, arrayTest);
        // System.out.println("Array2: " + array2);
    }

    @Test
    public void testFOrdering1() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1280, 128000).reshape(128, 1000);
        INDArray array2 = Nd4j.linspace(1, 1280, 128000).reshape(128, 1000);
        INDArray array3 = Nd4j.linspace(1, 1280, 128000).reshape(128, 1000);
        INDArray array4 = Nd4j.linspace(1, 1280, 128000).reshape(128, 1000).dup('f');
        System.out.println(("a4 stride: " + (array4.elementWiseStride())));
        long time1 = System.currentTimeMillis();
        array1.muli(array2);
        long time2 = System.currentTimeMillis();
        System.out.println(("Execution time 1: " + (time2 - time1)));
        time1 = System.currentTimeMillis();
        array3.muli(array4);
        time2 = System.currentTimeMillis();
        System.out.println(("Execution time 2: " + (time2 - time1)));
        Assert.assertEquals(array3, array1);
    }

    @Test
    public void testFOrdering2() throws Exception {
        INDArray array1 = Nd4j.linspace(1, 1280, 128000);
        INDArray array2 = Nd4j.linspace(1, 1280, 128000);
        INDArray array3 = Nd4j.linspace(1, 1280, 128000);
        INDArray array4 = Nd4j.linspace(1, 1280, 128000).dup('f');
        array1.muli(array2);
        array3.muli(array4);
        Assert.assertEquals(array3, array1);
    }

    @Test
    public void testAssign() {
        int[] shape1 = new int[]{ 3, 2, 2, 2, 2, 2 };
        int[] shape2 = new int[]{ 12, 8 };
        int length = ArrayUtil.prod(shape1);
        Assert.assertEquals(ArrayUtil.prod(shape1), ArrayUtil.prod(shape2));
        INDArray arr = Nd4j.linspace(1, length, length).reshape('c', shape1);
        INDArray arr2c = Nd4j.create(shape2, 'c');
        INDArray arr2f = Nd4j.create(shape2, 'f');
        arr2c.assign(arr);
        System.out.println("--------------");
        arr2f.assign(arr);
        INDArray exp = Nd4j.linspace(1, length, length).reshape('c', shape2);
        Assert.assertEquals(exp, arr2c);
        Assert.assertEquals(exp, arr2f);
    }
}

