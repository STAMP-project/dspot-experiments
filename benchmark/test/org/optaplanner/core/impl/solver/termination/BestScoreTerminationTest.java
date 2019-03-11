/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.solver.termination;


import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;


public class BestScoreTerminationTest {
    @Test
    public void solveTermination() {
        ScoreDefinition scoreDefinition = Mockito.mock(ScoreDefinition.class);
        Mockito.when(scoreDefinition.getLevelsSize()).thenReturn(1);
        Termination termination = new BestScoreTermination(scoreDefinition, SimpleScore.of((-1000)), new double[]{  });
        DefaultSolverScope solverScope = Mockito.mock(DefaultSolverScope.class);
        Mockito.when(solverScope.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        Mockito.when(solverScope.isBestSolutionInitialized()).thenReturn(true);
        Mockito.when(solverScope.getStartingInitializedScore()).thenReturn(SimpleScore.of((-1100)));
        Mockito.when(solverScope.getBestScore()).thenReturn(SimpleScore.of((-1100)));
        Assert.assertEquals(false, termination.isSolverTerminated(solverScope));
        Assert.assertEquals(0.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
        Mockito.when(solverScope.getBestScore()).thenReturn(SimpleScore.of((-1100)));
        Assert.assertEquals(false, termination.isSolverTerminated(solverScope));
        Assert.assertEquals(0.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
        Mockito.when(solverScope.getBestScore()).thenReturn(SimpleScore.of((-1040)));
        Assert.assertEquals(false, termination.isSolverTerminated(solverScope));
        Assert.assertEquals(0.6, termination.calculateSolverTimeGradient(solverScope), 0.0);
        Mockito.when(solverScope.getBestScore()).thenReturn(SimpleScore.of((-1040)));
        Assert.assertEquals(false, termination.isSolverTerminated(solverScope));
        Assert.assertEquals(0.6, termination.calculateSolverTimeGradient(solverScope), 0.0);
        Mockito.when(solverScope.getBestScore()).thenReturn(SimpleScore.of((-1000)));
        Assert.assertEquals(true, termination.isSolverTerminated(solverScope));
        Assert.assertEquals(1.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
        Mockito.when(solverScope.getBestScore()).thenReturn(SimpleScore.of((-900)));
        Assert.assertEquals(true, termination.isSolverTerminated(solverScope));
        Assert.assertEquals(1.0, termination.calculateSolverTimeGradient(solverScope), 0.0);
    }

    @Test
    public void phaseTermination() {
        ScoreDefinition scoreDefinition = Mockito.mock(ScoreDefinition.class);
        Mockito.when(scoreDefinition.getLevelsSize()).thenReturn(1);
        Termination termination = new BestScoreTermination(scoreDefinition, SimpleScore.of((-1000)), new double[]{  });
        AbstractPhaseScope phaseScope = Mockito.mock(AbstractPhaseScope.class);
        Mockito.when(phaseScope.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        Mockito.when(phaseScope.isBestSolutionInitialized()).thenReturn(true);
        Mockito.when(phaseScope.getStartingScore()).thenReturn(SimpleScore.of((-1100)));
        Mockito.when(phaseScope.getBestScore()).thenReturn(SimpleScore.of((-1100)));
        Assert.assertEquals(false, termination.isPhaseTerminated(phaseScope));
        Assert.assertEquals(0.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        Mockito.when(phaseScope.getBestScore()).thenReturn(SimpleScore.of((-1100)));
        Assert.assertEquals(false, termination.isPhaseTerminated(phaseScope));
        Assert.assertEquals(0.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        Mockito.when(phaseScope.getBestScore()).thenReturn(SimpleScore.of((-1040)));
        Assert.assertEquals(false, termination.isPhaseTerminated(phaseScope));
        Assert.assertEquals(0.6, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        Mockito.when(phaseScope.getBestScore()).thenReturn(SimpleScore.of((-1040)));
        Assert.assertEquals(false, termination.isPhaseTerminated(phaseScope));
        Assert.assertEquals(0.6, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        Mockito.when(phaseScope.getBestScore()).thenReturn(SimpleScore.of((-1000)));
        Assert.assertEquals(true, termination.isPhaseTerminated(phaseScope));
        Assert.assertEquals(1.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        Mockito.when(phaseScope.getBestScore()).thenReturn(SimpleScore.of((-900)));
        Assert.assertEquals(true, termination.isPhaseTerminated(phaseScope));
        Assert.assertEquals(1.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
    }

    @Test
    public void calculateTimeGradientSimpleScore() {
        ScoreDefinition scoreDefinition = Mockito.mock(ScoreDefinition.class);
        Mockito.when(scoreDefinition.getLevelsSize()).thenReturn(1);
        BestScoreTermination termination = new BestScoreTermination(scoreDefinition, SimpleScore.of(10), new double[]{  });
        Assert.assertEquals(0.0, termination.calculateTimeGradient(SimpleScore.of(0), SimpleScore.of(10), SimpleScore.of(0)), 0.0);
        Assert.assertEquals(0.6, termination.calculateTimeGradient(SimpleScore.of(0), SimpleScore.of(10), SimpleScore.of(6)), 0.0);
        Assert.assertEquals(1.0, termination.calculateTimeGradient(SimpleScore.of(0), SimpleScore.of(10), SimpleScore.of(10)), 0.0);
        Assert.assertEquals(1.0, termination.calculateTimeGradient(SimpleScore.of(0), SimpleScore.of(10), SimpleScore.of(11)), 0.0);
        Assert.assertEquals(0.25, termination.calculateTimeGradient(SimpleScore.of((-10)), SimpleScore.of(30), SimpleScore.of(0)), 0.0);
        Assert.assertEquals(0.33333, termination.calculateTimeGradient(SimpleScore.of(10), SimpleScore.of(40), SimpleScore.of(20)), 1.0E-5);
    }

    @Test
    public void calculateTimeGradientSimpleBigDecimalScore() {
        ScoreDefinition scoreDefinition = Mockito.mock(ScoreDefinition.class);
        Mockito.when(scoreDefinition.getLevelsSize()).thenReturn(1);
        BestScoreTermination termination = new BestScoreTermination(scoreDefinition, SimpleBigDecimalScore.of(new BigDecimal("10.00")), new double[]{  });
        Assert.assertEquals(0.0, termination.calculateTimeGradient(SimpleBigDecimalScore.of(new BigDecimal("0.00")), SimpleBigDecimalScore.of(new BigDecimal("10.00")), SimpleBigDecimalScore.of(new BigDecimal("0.00"))), 0.0);
        Assert.assertEquals(0.6, termination.calculateTimeGradient(SimpleBigDecimalScore.of(new BigDecimal("0.00")), SimpleBigDecimalScore.of(new BigDecimal("10.00")), SimpleBigDecimalScore.of(new BigDecimal("6.00"))), 0.0);
        Assert.assertEquals(1.0, termination.calculateTimeGradient(SimpleBigDecimalScore.of(new BigDecimal("0.00")), SimpleBigDecimalScore.of(new BigDecimal("10.00")), SimpleBigDecimalScore.of(new BigDecimal("10.00"))), 0.0);
        Assert.assertEquals(1.0, termination.calculateTimeGradient(SimpleBigDecimalScore.of(new BigDecimal("0.00")), SimpleBigDecimalScore.of(new BigDecimal("10.00")), SimpleBigDecimalScore.of(new BigDecimal("11.00"))), 0.0);
        Assert.assertEquals(0.25, termination.calculateTimeGradient(SimpleBigDecimalScore.of(new BigDecimal("-10.00")), SimpleBigDecimalScore.of(new BigDecimal("30.00")), SimpleBigDecimalScore.of(new BigDecimal("0.00"))), 0.0);
        Assert.assertEquals(0.33333, termination.calculateTimeGradient(SimpleBigDecimalScore.of(new BigDecimal("10.00")), SimpleBigDecimalScore.of(new BigDecimal("40.00")), SimpleBigDecimalScore.of(new BigDecimal("20.00"))), 1.0E-5);
    }

    @Test
    public void calculateTimeGradientHardSoftScore() {
        ScoreDefinition scoreDefinition = Mockito.mock(ScoreDefinition.class);
        Mockito.when(scoreDefinition.getLevelsSize()).thenReturn(2);
        BestScoreTermination termination = new BestScoreTermination(scoreDefinition, HardSoftScore.of((-10), (-300)), new double[]{ 0.75 });
        // Normal cases
        // Smack in the middle
        Assert.assertEquals(0.6, termination.calculateTimeGradient(HardSoftScore.of((-20), (-400)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-14), (-340))), 0.0);
        // No hard broken, total soft broken
        Assert.assertEquals(0.75, termination.calculateTimeGradient(HardSoftScore.of((-20), (-400)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-10), (-400))), 0.0);
        // Total hard broken, no soft broken
        Assert.assertEquals(0.25, termination.calculateTimeGradient(HardSoftScore.of((-20), (-400)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-20), (-300))), 0.0);
        // No hard broken, more than total soft broken
        Assert.assertEquals(0.75, termination.calculateTimeGradient(HardSoftScore.of((-20), (-400)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-10), (-900))), 0.0);
        // More than total hard broken, no soft broken
        Assert.assertEquals(0.0, termination.calculateTimeGradient(HardSoftScore.of((-20), (-400)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-90), (-300))), 0.0);
        // Perfect min/max cases
        Assert.assertEquals(1.0, termination.calculateTimeGradient(HardSoftScore.of((-10), (-300)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-10), (-300))), 0.0);
        Assert.assertEquals(0.0, termination.calculateTimeGradient(HardSoftScore.of((-20), (-400)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-20), (-400))), 0.0);
        Assert.assertEquals(1.0, termination.calculateTimeGradient(HardSoftScore.of((-20), (-400)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-10), (-300))), 0.0);
        // Hard total delta is 0
        Assert.assertEquals((0.75 + (0.6 * 0.25)), termination.calculateTimeGradient(HardSoftScore.of((-10), (-400)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-10), (-340))), 0.0);
        Assert.assertEquals(0.0, termination.calculateTimeGradient(HardSoftScore.of((-10), (-400)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-20), (-340))), 0.0);
        Assert.assertEquals(1.0, termination.calculateTimeGradient(HardSoftScore.of((-10), (-400)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-0), (-340))), 0.0);
        // Soft total delta is 0
        Assert.assertEquals(((0.6 * 0.75) + 0.25), termination.calculateTimeGradient(HardSoftScore.of((-20), (-300)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-14), (-300))), 0.0);
        Assert.assertEquals((0.6 * 0.75), termination.calculateTimeGradient(HardSoftScore.of((-20), (-300)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-14), (-400))), 0.0);
        Assert.assertEquals(((0.6 * 0.75) + 0.25), termination.calculateTimeGradient(HardSoftScore.of((-20), (-300)), HardSoftScore.of((-10), (-300)), HardSoftScore.of((-14), (-0))), 0.0);
    }

    @Test
    public void calculateTimeGradientHardSoftBigDecimalScore() {
        ScoreDefinition scoreDefinition = Mockito.mock(ScoreDefinition.class);
        Mockito.when(scoreDefinition.getLevelsSize()).thenReturn(2);
        BestScoreTermination termination = new BestScoreTermination(scoreDefinition, HardSoftBigDecimalScore.of(new BigDecimal("10.00"), new BigDecimal("10.00")), new double[]{ 0.75 });
        // hard == soft
        Assert.assertEquals(0.0, termination.calculateTimeGradient(HardSoftBigDecimalScore.of(new BigDecimal("0.00"), new BigDecimal("0.00")), HardSoftBigDecimalScore.of(new BigDecimal("10.00"), new BigDecimal("10.00")), HardSoftBigDecimalScore.of(new BigDecimal("0.00"), new BigDecimal("0.00"))), 0.0);
        Assert.assertEquals(0.6, termination.calculateTimeGradient(HardSoftBigDecimalScore.of(new BigDecimal("0.00"), new BigDecimal("0.00")), HardSoftBigDecimalScore.of(new BigDecimal("10.00"), new BigDecimal("10.00")), HardSoftBigDecimalScore.of(new BigDecimal("6.00"), new BigDecimal("6.00"))), 0.0);
        Assert.assertEquals(1.0, termination.calculateTimeGradient(HardSoftBigDecimalScore.of(new BigDecimal("0.00"), new BigDecimal("0.00")), HardSoftBigDecimalScore.of(new BigDecimal("10.00"), new BigDecimal("10.00")), HardSoftBigDecimalScore.of(new BigDecimal("10.00"), new BigDecimal("10.00"))), 0.0);
        Assert.assertEquals(1.0, termination.calculateTimeGradient(HardSoftBigDecimalScore.of(new BigDecimal("0.00"), new BigDecimal("0.00")), HardSoftBigDecimalScore.of(new BigDecimal("10.00"), new BigDecimal("10.00")), HardSoftBigDecimalScore.of(new BigDecimal("11.00"), new BigDecimal("11.00"))), 0.0);
        Assert.assertEquals(0.25, termination.calculateTimeGradient(HardSoftBigDecimalScore.of(new BigDecimal("-10.00"), new BigDecimal("-10.00")), HardSoftBigDecimalScore.of(new BigDecimal("30.00"), new BigDecimal("30.00")), HardSoftBigDecimalScore.of(new BigDecimal("0.00"), new BigDecimal("0.00"))), 0.0);
        Assert.assertEquals(0.33333, termination.calculateTimeGradient(HardSoftBigDecimalScore.of(new BigDecimal("10.00"), new BigDecimal("10.00")), HardSoftBigDecimalScore.of(new BigDecimal("40.00"), new BigDecimal("40.00")), HardSoftBigDecimalScore.of(new BigDecimal("20.00"), new BigDecimal("20.00"))), 1.0E-5);
    }

    @Test
    public void calculateTimeGradientBendableScoreHS() {
        ScoreDefinition scoreDefinition = Mockito.mock(ScoreDefinition.class);
        Mockito.when(scoreDefinition.getLevelsSize()).thenReturn(2);
        BestScoreTermination termination = new BestScoreTermination(scoreDefinition, BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), new double[]{ 0.75 });
        // Normal cases
        // Smack in the middle
        Assert.assertEquals(0.6, termination.calculateTimeGradient(BendableScore.of(new int[]{ -20 }, new int[]{ -400 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -14 }, new int[]{ -340 })), 0.0);
        // No hard broken, total soft broken
        Assert.assertEquals(0.75, termination.calculateTimeGradient(BendableScore.of(new int[]{ -20 }, new int[]{ -400 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -10 }, new int[]{ -400 })), 0.0);
        // Total hard broken, no soft broken
        Assert.assertEquals(0.25, termination.calculateTimeGradient(BendableScore.of(new int[]{ -20 }, new int[]{ -400 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -20 }, new int[]{ -300 })), 0.0);
        // No hard broken, more than total soft broken
        Assert.assertEquals(0.75, termination.calculateTimeGradient(BendableScore.of(new int[]{ -20 }, new int[]{ -400 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -10 }, new int[]{ -900 })), 0.0);
        // More than total hard broken, no soft broken
        Assert.assertEquals(0.0, termination.calculateTimeGradient(BendableScore.of(new int[]{ -20 }, new int[]{ -400 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -90 }, new int[]{ -300 })), 0.0);
        // Perfect min/max cases
        Assert.assertEquals(1.0, termination.calculateTimeGradient(BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 })), 0.0);
        Assert.assertEquals(0.0, termination.calculateTimeGradient(BendableScore.of(new int[]{ -20 }, new int[]{ -400 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -20 }, new int[]{ -400 })), 0.0);
        Assert.assertEquals(1.0, termination.calculateTimeGradient(BendableScore.of(new int[]{ -20 }, new int[]{ -400 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 })), 0.0);
        // Hard total delta is 0
        Assert.assertEquals((0.75 + (0.6 * 0.25)), termination.calculateTimeGradient(BendableScore.of(new int[]{ -10 }, new int[]{ -400 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -10 }, new int[]{ -340 })), 0.0);
        Assert.assertEquals(0.0, termination.calculateTimeGradient(BendableScore.of(new int[]{ -10 }, new int[]{ -400 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -20 }, new int[]{ -340 })), 0.0);
        Assert.assertEquals(1.0, termination.calculateTimeGradient(BendableScore.of(new int[]{ -10 }, new int[]{ -400 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -0 }, new int[]{ -340 })), 0.0);
        // Soft total delta is 0
        Assert.assertEquals(((0.6 * 0.75) + 0.25), termination.calculateTimeGradient(BendableScore.of(new int[]{ -20 }, new int[]{ -300 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -14 }, new int[]{ -300 })), 0.0);
        Assert.assertEquals((0.6 * 0.75), termination.calculateTimeGradient(BendableScore.of(new int[]{ -20 }, new int[]{ -300 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -14 }, new int[]{ -400 })), 0.0);
        Assert.assertEquals(((0.6 * 0.75) + 0.25), termination.calculateTimeGradient(BendableScore.of(new int[]{ -20 }, new int[]{ -300 }), BendableScore.of(new int[]{ -10 }, new int[]{ -300 }), BendableScore.of(new int[]{ -14 }, new int[]{ -0 })), 0.0);
    }

    @Test
    public void calculateTimeGradientBendableScoreHHSSS() {
        ScoreDefinition scoreDefinition = Mockito.mock(ScoreDefinition.class);
        Mockito.when(scoreDefinition.getLevelsSize()).thenReturn(5);
        BestScoreTermination termination = new BestScoreTermination(scoreDefinition, BendableScore.of(new int[]{ 0, 0 }, new int[]{ 0, 0, -10 }), new double[]{ 0.75, 0.75, 0.75, 0.75 });
        // Normal cases
        // Smack in the middle
        Assert.assertEquals(((0.6 * 0.75) + ((0.6 * 0.25) * 0.75)), termination.calculateTimeGradient(BendableScore.of(new int[]{ -10, -100 }, new int[]{ -50, -60, -70 }), BendableScore.of(new int[]{ 0, 0 }, new int[]{ 0, 0, -10 }), BendableScore.of(new int[]{ -4, -40 }, new int[]{ -50, -60, -70 })), 0.0);
    }
}

