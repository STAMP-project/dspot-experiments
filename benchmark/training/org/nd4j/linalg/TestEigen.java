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
package org.nd4j.linalg;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.eigen.Eigen;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.util.ArrayUtil;


/**
 * Created by rcorbish
 */
@RunWith(Parameterized.class)
public class TestEigen extends BaseNd4jTest {
    protected DataType initialType;

    public TestEigen(Nd4jBackend backend) {
        super(backend);
        initialType = Nd4j.dataType();
    }

    // test of functions added by Luke Czapla
    // Compares solution of A x = L x  to solution to A x = L B x when it is simple
    @Test
    public void test2Syev() {
        double[][] matrix = new double[][]{ new double[]{ 0.0427, -0.04, 0, 0, 0, 0 }, new double[]{ -0.04, 0.0427, 0, 0, 0, 0 }, new double[]{ 0, 0.0, 0.0597, 0, 0, 0 }, new double[]{ 0, 0, 0, 50, 0, 0 }, new double[]{ 0, 0, 0, 0, 50, 0 }, new double[]{ 0, 0, 0, 0, 0, 50 } };
        INDArray m = Nd4j.create(ArrayUtil.flattenDoubleArray(matrix), new int[]{ 6, 6 });
        INDArray res = Eigen.symmetricGeneralizedEigenvalues(m, true);
        INDArray n = Nd4j.create(ArrayUtil.flattenDoubleArray(matrix), new int[]{ 6, 6 });
        INDArray res2 = Eigen.symmetricGeneralizedEigenvalues(n, Nd4j.eye(6).mul(2.0), true);
        for (int i = 0; i < 6; i++) {
            Assert.assertEquals(res.getDouble(i), (2 * (res2.getDouble(i))), 1.0E-6);
        }
    }

    @Test
    public void testSyev() {
        INDArray A = Nd4j.create(new float[][]{ new float[]{ 1.96F, -6.49F, -0.47F, -7.2F, -0.65F }, new float[]{ -6.49F, 3.8F, -6.39F, 1.5F, -6.34F }, new float[]{ -0.47F, -6.39F, 4.17F, -1.51F, 2.67F }, new float[]{ -7.2F, 1.5F, -1.51F, 5.7F, 1.8F }, new float[]{ -0.65F, -6.34F, 2.67F, 1.8F, -7.1F } });
        INDArray B = A.dup();
        INDArray e = Eigen.symmetricGeneralizedEigenvalues(A);
        for (int i = 0; i < (A.rows()); i++) {
            INDArray LHS = B.mmul(A.slice(i, 1));
            INDArray RHS = A.slice(i, 1).mul(e.getFloat(i));
            for (int j = 0; j < (LHS.length()); j++) {
                Assert.assertEquals(LHS.getFloat(j), RHS.getFloat(j), 0.001F);
            }
        }
    }
}

