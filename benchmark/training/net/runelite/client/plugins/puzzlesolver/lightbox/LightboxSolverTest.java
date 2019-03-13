/**
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.puzzlesolver.lightbox;


import Combination.A;
import Combination.B;
import Combination.C;
import Combination.D;
import Combination.E;
import Combination.F;
import Combination.G;
import Combination.H;
import org.junit.Assert;
import org.junit.Test;


public class LightboxSolverTest {
    private static final int[] INITIAL = new int[]{ 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 };

    private static final int[] A = new int[]{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 };

    private static final int[] B = new int[]{ 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 1 };

    private static final int[] C = new int[]{ 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1 };

    private static final int[] D = new int[]{ 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1 };

    private static final int[] E = new int[]{ 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1 };

    private static final int[] F = new int[]{ 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0 };

    private static final int[] G = new int[]{ 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0 };

    private static final int[] H = new int[]{ 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 0 };

    @Test
    public void test() {
        LightboxSolver solver = new LightboxSolver();
        solver.setInitial(LightboxSolverTest.fromArray(LightboxSolverTest.INITIAL));
        solver.setSwitchChange(Combination.A, LightboxSolverTest.fromArray(LightboxSolverTest.A));
        solver.setSwitchChange(Combination.B, LightboxSolverTest.fromArray(LightboxSolverTest.B));
        solver.setSwitchChange(Combination.C, LightboxSolverTest.fromArray(LightboxSolverTest.C));
        solver.setSwitchChange(Combination.D, LightboxSolverTest.fromArray(LightboxSolverTest.D));
        solver.setSwitchChange(Combination.E, LightboxSolverTest.fromArray(LightboxSolverTest.E));
        solver.setSwitchChange(Combination.F, LightboxSolverTest.fromArray(LightboxSolverTest.F));
        solver.setSwitchChange(Combination.G, LightboxSolverTest.fromArray(LightboxSolverTest.G));
        solver.setSwitchChange(Combination.H, LightboxSolverTest.fromArray(LightboxSolverTest.H));
        LightboxSolution solution = solver.solve();
        LightboxSolution expected = new LightboxSolution();
        expected.flip(Combination.A);
        expected.flip(Combination.B);
        expected.flip(Combination.D);
        expected.flip(Combination.E);
        expected.flip(Combination.F);
        expected.flip(Combination.G);
        Assert.assertEquals(expected, solution);
    }
}

