/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.math.hadoop.stochasticsvd;


import Functions.ABS;
import Functions.PLUS;
import java.io.IOException;
import java.util.Random;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.math.function.DoubleFunction;
import org.junit.Test;


public class LocalSSVDPCASparseTest extends MahoutTestCase {
    private static final double s_epsilon = 1.0E-10;

    @Test
    public void testOmegaTRightMultiply() {
        final Random rnd = RandomUtils.getRandom();
        final long seed = rnd.nextLong();
        final int n = 2000;
        final int kp = 100;
        final Omega omega = new Omega(seed, kp);
        final Matrix materializedOmega = new DenseMatrix(n, kp);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < kp; j++)
                materializedOmega.setQuick(i, j, omega.getQuick(i, j));


        Vector xi = new DenseVector(n);
        xi.assign(new DoubleFunction() {
            @Override
            public double apply(double x) {
                return (rnd.nextDouble()) * 100;
            }
        });
        Vector s_o = omega.mutlithreadedTRightMultiply(xi);
        Matrix xiVector = new DenseMatrix(n, 1);
        xiVector.assignColumn(0, xi);
        Vector s_o_control = materializedOmega.transpose().times(xiVector).viewColumn(0);
        assertEquals(0, s_o.minus(s_o_control).aggregate(PLUS, ABS), 1.0E-10);
        System.out.printf("s_omega=\n%s\n", s_o);
        System.out.printf("s_omega_control=\n%s\n", s_o_control);
    }

    @Test
    public void runPCATest1() throws IOException {
        runSSVDSolver(1);
    }
}

