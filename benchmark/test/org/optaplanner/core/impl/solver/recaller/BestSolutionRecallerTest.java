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
package org.optaplanner.core.impl.solver.recaller;


import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;


public class BestSolutionRecallerTest {
    @Test
    public void unimprovedUninitializedProcessWorkingSolutionDuringStep() {
        SimpleScore originalBestScore = SimpleScore.ofUninitialized((-1), (-300));
        SimpleScore stepScore = SimpleScore.ofUninitialized((-2), 0);
        doProcessWorkingSolutionDuringStep(originalBestScore, stepScore, false);
    }

    @Test
    public void unimprovedInitializedProcessWorkingSolutionDuringStep() {
        Score originalBestScore = SimpleScore.of(0);
        Score stepScore = SimpleScore.of((-1));
        doProcessWorkingSolutionDuringStep(originalBestScore, stepScore, false);
    }

    @Test
    public void improvedUninitializedProcessWorkingSolutionDuringStep() {
        Score originalBestScore = SimpleScore.ofUninitialized((-2), 0);
        Score stepScore = SimpleScore.ofUninitialized((-1), 0);
        doProcessWorkingSolutionDuringStep(originalBestScore, stepScore, true);
    }

    @Test
    public void improvedInitializedProcessWorkingSolutionDuringStep() {
        Score originalBestScore = SimpleScore.of((-1));
        Score stepScore = SimpleScore.of(0);
        doProcessWorkingSolutionDuringStep(originalBestScore, stepScore, true);
    }

    @Test
    public void unimprovedUninitializedProcessWorkingSolutionDuringMove() {
        Score bestScore = SimpleScore.of((-10));
        Score moveScore = SimpleScore.ofUninitialized((-1), (-1));
        doProcessWorkingSolutionDuringMove(bestScore, moveScore, false);
    }

    @Test
    public void unimprovedInitializedProcessWorkingSolutionDuringMove() {
        Score bestScore = SimpleScore.of(0);
        Score moveScore = SimpleScore.of((-1));
        doProcessWorkingSolutionDuringMove(bestScore, moveScore, false);
    }

    @Test
    public void improvedUninitializedProcessWorkingSolutionDuringMove() {
        Score bestScore = SimpleScore.ofUninitialized((-1), 0);
        SimpleScore moveScore = SimpleScore.of((-2));
        doProcessWorkingSolutionDuringMove(bestScore, moveScore, true);
    }

    @Test
    public void improvedInitializedProcessWorkingSolutionDuringMove() {
        Score bestScore = SimpleScore.of((-2));
        Score moveScore = SimpleScore.of((-1));
        doProcessWorkingSolutionDuringMove(bestScore, moveScore, true);
    }
}

