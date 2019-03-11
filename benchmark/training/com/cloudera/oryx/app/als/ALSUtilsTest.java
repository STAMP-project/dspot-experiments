/**
 * Copyright (c) 2015, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.als;


import com.cloudera.oryx.common.OryxTest;
import com.cloudera.oryx.common.math.LinearSystemSolver;
import com.cloudera.oryx.common.math.Solver;
import com.cloudera.oryx.common.math.VectorMath;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;


public final class ALSUtilsTest extends OryxTest {
    @Test
    public void testImplicitQui() {
        assertNaN(ALSUtils.computeTargetQui(true, 0.0, 1.0));
        assertNaN(ALSUtils.computeTargetQui(true, 0.0, 0.0));
        assertNaN(ALSUtils.computeTargetQui(true, 0.0, (-1.0)));
        assertNaN(ALSUtils.computeTargetQui(true, 0.5, 1.0));
        assertNaN(ALSUtils.computeTargetQui(true, (-0.5), 0.0));
        assertEquals(0.75, ALSUtils.computeTargetQui(true, 1.0, 0.5));
        assertEquals(0.25, ALSUtils.computeTargetQui(true, (-1.0), 0.5));
        for (double d : new double[]{ -1.0, 0.0, 0.5, 1.0, 2.0 }) {
            assertEquals(d, ALSUtils.computeTargetQui(false, d, 0.0));
        }
    }

    @Test
    public void testComputeUpdatedXu() {
        Collection<float[]> rows = Arrays.asList(new float[][]{ new float[]{ 1.0F, 2.0F }, new float[]{ 3.0F, 0.0F }, new float[]{ 0.0F, 1.0F } });
        Solver solver = LinearSystemSolver.getSolver(VectorMath.transposeTimesSelf(rows));
        assertNull(ALSUtils.computeUpdatedXu(solver, 1.0, null, null, true));
        assertArrayEquals(new float[]{ 0.13043478F, 0.097826086F }, ALSUtils.computeUpdatedXu(solver, 1.0, null, new float[]{ 2.0F, 1.0F }, true));
        assertArrayEquals(new float[]{ 0.11594203F, 0.08695652F }, ALSUtils.computeUpdatedXu(solver, 0.5, null, new float[]{ 2.0F, 1.0F }, true));
        assertArrayEquals(new float[]{ 0.16086957F, 0.14565217F }, ALSUtils.computeUpdatedXu(solver, 1.0, new float[]{ 0.1F, 0.1F }, new float[]{ 2.0F, 1.0F }, true));
    }
}

