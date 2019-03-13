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
package org.apache.mahout.math.solver;


import ConjugateGradientSolver.DEFAULT_MAX_ERROR;
import org.apache.mahout.math.MahoutTestCase;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.Vector;
import org.junit.Test;


public class TestConjugateGradientSolver extends MahoutTestCase {
    @Test
    public void testConjugateGradientSolver() {
        Matrix a = TestConjugateGradientSolver.getA();
        Vector b = TestConjugateGradientSolver.getB();
        ConjugateGradientSolver solver = new ConjugateGradientSolver();
        Vector x = solver.solve(a, b);
        assertEquals(0.0, Math.sqrt(a.times(x).getDistanceSquared(b)), MahoutTestCase.EPSILON);
        assertEquals(0.0, solver.getResidualNorm(), DEFAULT_MAX_ERROR);
        assertEquals(10, solver.getIterations());
    }

    @Test
    public void testConditionedConjugateGradientSolver() {
        Matrix a = TestConjugateGradientSolver.getIllConditionedMatrix();
        Vector b = TestConjugateGradientSolver.getB();
        Preconditioner conditioner = new JacobiConditioner(a);
        ConjugateGradientSolver solver = new ConjugateGradientSolver();
        Vector x = solver.solve(a, b, null, 100, DEFAULT_MAX_ERROR);
        double distance = Math.sqrt(a.times(x).getDistanceSquared(b));
        assertEquals(0.0, distance, MahoutTestCase.EPSILON);
        assertEquals(0.0, solver.getResidualNorm(), DEFAULT_MAX_ERROR);
        assertEquals(16, solver.getIterations());
        Vector x2 = solver.solve(a, b, conditioner, 100, DEFAULT_MAX_ERROR);
        // the Jacobi preconditioner isn't very good, but it does result in one less iteration to converge
        distance = Math.sqrt(a.times(x2).getDistanceSquared(b));
        assertEquals(0.0, distance, MahoutTestCase.EPSILON);
        assertEquals(0.0, solver.getResidualNorm(), DEFAULT_MAX_ERROR);
        assertEquals(15, solver.getIterations());
    }

    @Test
    public void testEarlyStop() {
        Matrix a = TestConjugateGradientSolver.getA();
        Vector b = TestConjugateGradientSolver.getB();
        ConjugateGradientSolver solver = new ConjugateGradientSolver();
        // specifying a looser max error will result in few iterations but less accurate results
        Vector x = solver.solve(a, b, null, 10, 0.1);
        double distance = Math.sqrt(a.times(x).getDistanceSquared(b));
        assertTrue((distance > (MahoutTestCase.EPSILON)));
        assertEquals(0.0, distance, 0.1);// should be equal to within the error specified

        assertEquals(7, solver.getIterations());// should have taken fewer iterations

        // can get a similar effect by bounding the number of iterations
        x = solver.solve(a, b, null, 7, DEFAULT_MAX_ERROR);
        distance = Math.sqrt(a.times(x).getDistanceSquared(b));
        assertTrue((distance > (MahoutTestCase.EPSILON)));
        assertEquals(0.0, distance, 0.1);
        assertEquals(7, solver.getIterations());
    }
}

