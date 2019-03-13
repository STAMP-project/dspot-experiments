/**
 * Copyright (C) 2013-2018 Vasilis Vryniotis <bbriniotis@datumbox.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datumbox.framework.core.mathematics.linearprogramming;


import Constants.DOUBLE_ACCURACY_HIGH;
import LPSolver.LPConstraint;
import LPSolver.LPResult;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static LPSolver.LEQ;


/**
 * Test cases for LPSolver.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class LPSolverTest extends AbstractTest {
    /**
     * Test of solve method, of class LPSolver.
     */
    @Test
    public void testSolve() {
        logger.info("solve");
        // Example from http://lpsolve.sourceforge.net/5.5/PHP.htm
        double[] linearObjectiveFunction = new double[]{ 143.0, 60.0, 195.0 };
        List<LPSolver.LPConstraint> linearConstraintsList = new ArrayList<>();
        linearConstraintsList.add(new LPSolver.LPConstraint(new double[]{ 120.0, 210.0, 150.75 }, LEQ, 15000.0));
        linearConstraintsList.add(new LPSolver.LPConstraint(new double[]{ 110.0, 30.0, 125.0 }, LEQ, 4000.0));
        linearConstraintsList.add(new LPSolver.LPConstraint(new double[]{ 1.0, 1.0, 1.0 }, LEQ, 75.0));
        LPSolver.LPResult expResult = new LPSolver.LPResult();
        expResult.setObjectiveValue(6986.8421052632);
        expResult.setVariableValues(new double[]{ 0.0, 56.578947368421, 18.421052631579 });
        LPSolver.LPResult result = LPSolver.solve(linearObjectiveFunction, linearConstraintsList, true, true);
        Assert.assertEquals(expResult.getObjectiveValue(), result.getObjectiveValue(), DOUBLE_ACCURACY_HIGH);
        Assert.assertArrayEquals(expResult.getVariableValues(), result.getVariableValues(), DOUBLE_ACCURACY_HIGH);
    }
}

