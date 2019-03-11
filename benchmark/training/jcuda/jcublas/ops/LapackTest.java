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


import java.util.Random;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author rcorbish
 */
@Ignore
public class LapackTest {
    private Random rng = new Random(1230);

    @Test
    public void testSgetrf1() {
        int m = 3;
        int n = 3;
        INDArray arr = Nd4j.create(new float[]{ 1.0F, 4.0F, 7.0F, 2.0F, 5.0F, -2.0F, 3.0F, 0.0F, 3.0F }, new int[]{ m, n }, 'f');
        Nd4j.getBlasWrapper().lapack().getrf(arr);
        // The above matrix factorizes to :
        // 7.00000  -2.00000   3.00000
        // 0.57143   6.14286  -1.71429
        // 0.14286   0.37209   3.20930
        Assert.assertEquals(7.0F, arr.getFloat(0), 1.0E-5F);
        Assert.assertEquals(0.57143F, arr.getFloat(1), 1.0E-5F);
        Assert.assertEquals(0.14286F, arr.getFloat(2), 1.0E-5F);
        Assert.assertEquals((-2.0F), arr.getFloat(3), 1.0E-5F);
        Assert.assertEquals(6.14286F, arr.getFloat(4), 1.0E-5F);
        Assert.assertEquals(0.37209F, arr.getFloat(5), 1.0E-5F);
        Assert.assertEquals(3.0F, arr.getFloat(6), 1.0E-5F);
        Assert.assertEquals((-1.71429F), arr.getFloat(7), 1.0E-5F);
        Assert.assertEquals(3.2093F, arr.getFloat(8), 1.0E-5F);
    }

    @Test
    public void testGetrf() {
        int m = 150;
        int n = 100;
        float[] f = new float[m * n];
        for (int i = 0; i < (f.length); i++)
            f[i] = (rng.nextInt(5)) + 1;

        // there is a very very small (non-zero) chance that the random matrix is singular
        // and may fail a test
        long start = System.currentTimeMillis();
        INDArray IPIV = null;
        INDArray arr = null;
        final int N = 100;
        for (int i = 0; i < N; i++) {
            arr = Nd4j.create(f, new int[]{ m, n }, 'f');
            IPIV = Nd4j.getBlasWrapper().lapack().getrf(arr);
        }
        INDArray L = Nd4j.getBlasWrapper().lapack().getLFactor(arr);
        INDArray U = Nd4j.getBlasWrapper().lapack().getUFactor(arr);
        INDArray P = Nd4j.getBlasWrapper().lapack().getPFactor(m, IPIV);
        INDArray orig = P.mmul(L).mmul(U);
        Assert.assertEquals("PxLxU is not the expected size - rows", orig.size(0), arr.size(0));
        Assert.assertEquals("PxLxU is not the expected size - cols", orig.size(1), arr.size(1));
        arr = Nd4j.create(f, new int[]{ m, n }, 'f');
        for (int r = 0; r < (orig.size(0)); r++) {
            for (int c = 0; c < (orig.size(1)); c++) {
                Assert.assertEquals("Original & recombined matrices differ", orig.getFloat(r, c), arr.getFloat(r, c), 0.001F);
            }
        }
    }

    @Test
    public void testSvdTallFortran() {
        testSvd(5, 3, 'f');
    }

    @Test
    public void testSvdTallC() {
        testSvd(5, 3, 'c');
    }

    @Test
    public void testSvdWideFortran() {
        testSvd(3, 5, 'f');
    }

    @Test
    public void testSvdWideC() {
        testSvd(3, 5, 'c');
    }

    @Test
    public void testEigsFortran() {
        testEv(5, 'f');
    }

    @Test
    public void testEigsC() {
        testEv(5, 'c');
    }
}

