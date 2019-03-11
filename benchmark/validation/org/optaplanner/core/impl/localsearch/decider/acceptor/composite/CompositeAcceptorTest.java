/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.impl.localsearch.decider.acceptor.composite;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.CompositeAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class CompositeAcceptorTest {
    @Test
    public void phaseLifecycle() {
        DefaultSolverScope<TestdataSolution> solverScope = Mockito.mock(DefaultSolverScope.class);
        LocalSearchPhaseScope<TestdataSolution> phaseScope = Mockito.mock(LocalSearchPhaseScope.class);
        LocalSearchStepScope<TestdataSolution> stepScope = Mockito.mock(LocalSearchStepScope.class);
        Acceptor acceptor1 = Mockito.mock(Acceptor.class);
        Acceptor acceptor2 = Mockito.mock(Acceptor.class);
        Acceptor acceptor3 = Mockito.mock(Acceptor.class);
        CompositeAcceptor compositeAcceptor = new CompositeAcceptor(acceptor1, acceptor2, acceptor3);
        compositeAcceptor.solvingStarted(solverScope);
        compositeAcceptor.phaseStarted(phaseScope);
        compositeAcceptor.stepStarted(stepScope);
        compositeAcceptor.stepEnded(stepScope);
        compositeAcceptor.stepStarted(stepScope);
        compositeAcceptor.stepEnded(stepScope);
        compositeAcceptor.phaseEnded(phaseScope);
        compositeAcceptor.phaseStarted(phaseScope);
        compositeAcceptor.stepStarted(stepScope);
        compositeAcceptor.stepEnded(stepScope);
        compositeAcceptor.phaseEnded(phaseScope);
        compositeAcceptor.solvingEnded(solverScope);
        PlannerAssert.verifyPhaseLifecycle(acceptor1, 1, 2, 3);
        PlannerAssert.verifyPhaseLifecycle(acceptor2, 1, 2, 3);
        PlannerAssert.verifyPhaseLifecycle(acceptor3, 1, 2, 3);
    }

    @Test
    public void isAccepted() {
        Assert.assertEquals(true, isCompositeAccepted(true, true, true));
        Assert.assertEquals(false, isCompositeAccepted(false, true, true));
        Assert.assertEquals(false, isCompositeAccepted(true, false, true));
        Assert.assertEquals(false, isCompositeAccepted(true, true, false));
        Assert.assertEquals(false, isCompositeAccepted(false, false, false));
    }
}

