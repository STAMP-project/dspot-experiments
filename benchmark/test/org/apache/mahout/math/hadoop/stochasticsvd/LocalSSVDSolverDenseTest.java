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


import java.io.IOException;
import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;


/**
 * Tests SSVD solver with a made-up data running hadoop solver in a local mode.
 * It requests full-rank SSVD and then compares singular values to that of
 * Colt's SVD asserting epsilon(precision) 1e-10 or whatever most recent value
 * configured.
 */
public class LocalSSVDSolverDenseTest extends MahoutTestCase {
    private static final double s_epsilon = 1.0E-10;

    /* I actually never saw errors more than 3% worst case for this particular
    test, but since it's non-deterministic test, it still may occasionally
    produce bad results with a non-zero probability, so i put this pct% for
    error margin high enough so it (almost) never fails.
     */
    private static final double s_precisionPct = 10;

    @Test
    public void testSSVDSolverDense() throws IOException {
        runSSVDSolver(0);
    }

    @Test
    public void testSSVDSolverPowerIterations1() throws IOException {
        runSSVDSolver(1);
    }
}

