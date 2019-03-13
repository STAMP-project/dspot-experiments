/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.config.heuristic.selector.move;


import SelectionCacheType.JUST_IN_TIME;
import SelectionCacheType.PHASE;
import SelectionCacheType.STEP;
import SelectionOrder.ORIGINAL;
import SelectionOrder.RANDOM;
import SelectionOrder.SHUFFLED;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.AbstractSelectorConfigTest;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.CachingMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.ShufflingMoveSelector;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class MoveSelectorConfigTest extends AbstractSelectorConfigTest {
    @Test
    public void phaseOriginal() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType, boolean randomSelection) {
                Assert.assertEquals(PHASE, minimumCacheType);
                Assert.assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(PHASE);
        moveSelectorConfig.setSelectionOrder(ORIGINAL);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(CachingMoveSelector.class, moveSelector);
        PlannerAssert.assertNotInstanceOf(ShufflingMoveSelector.class, moveSelector);
        Assert.assertEquals(PHASE, moveSelector.getCacheType());
        Assert.assertSame(baseMoveSelector, getChildMoveSelector());
    }

    @Test
    public void stepOriginal() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType, boolean randomSelection) {
                Assert.assertEquals(STEP, minimumCacheType);
                Assert.assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(STEP);
        moveSelectorConfig.setSelectionOrder(ORIGINAL);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(CachingMoveSelector.class, moveSelector);
        PlannerAssert.assertNotInstanceOf(ShufflingMoveSelector.class, moveSelector);
        Assert.assertEquals(STEP, moveSelector.getCacheType());
        Assert.assertSame(baseMoveSelector, getChildMoveSelector());
    }

    @Test
    public void justInTimeOriginal() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType, boolean randomSelection) {
                Assert.assertEquals(JUST_IN_TIME, minimumCacheType);
                Assert.assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(JUST_IN_TIME);
        moveSelectorConfig.setSelectionOrder(ORIGINAL);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        Assert.assertSame(baseMoveSelector, moveSelector);
        Assert.assertEquals(JUST_IN_TIME, moveSelector.getCacheType());
    }

    @Test
    public void phaseRandom() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType, boolean randomSelection) {
                Assert.assertEquals(PHASE, minimumCacheType);
                Assert.assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(PHASE);
        moveSelectorConfig.setSelectionOrder(RANDOM);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(CachingMoveSelector.class, moveSelector);
        PlannerAssert.assertNotInstanceOf(ShufflingMoveSelector.class, moveSelector);
        Assert.assertEquals(PHASE, moveSelector.getCacheType());
        Assert.assertSame(baseMoveSelector, getChildMoveSelector());
    }

    @Test
    public void stepRandom() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType, boolean randomSelection) {
                Assert.assertEquals(STEP, minimumCacheType);
                Assert.assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(STEP);
        moveSelectorConfig.setSelectionOrder(RANDOM);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(CachingMoveSelector.class, moveSelector);
        PlannerAssert.assertNotInstanceOf(ShufflingMoveSelector.class, moveSelector);
        Assert.assertEquals(STEP, moveSelector.getCacheType());
        Assert.assertSame(baseMoveSelector, getChildMoveSelector());
    }

    @Test
    public void justInTimeRandom() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType, boolean randomSelection) {
                Assert.assertEquals(JUST_IN_TIME, minimumCacheType);
                Assert.assertEquals(true, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(JUST_IN_TIME);
        moveSelectorConfig.setSelectionOrder(RANDOM);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        Assert.assertSame(baseMoveSelector, moveSelector);
        Assert.assertEquals(JUST_IN_TIME, moveSelector.getCacheType());
    }

    @Test
    public void phaseShuffled() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType, boolean randomSelection) {
                Assert.assertEquals(PHASE, minimumCacheType);
                Assert.assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(PHASE);
        moveSelectorConfig.setSelectionOrder(SHUFFLED);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(ShufflingMoveSelector.class, moveSelector);
        Assert.assertEquals(PHASE, moveSelector.getCacheType());
        Assert.assertSame(baseMoveSelector, getChildMoveSelector());
    }

    @Test
    public void stepShuffled() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType, boolean randomSelection) {
                Assert.assertEquals(STEP, minimumCacheType);
                Assert.assertEquals(false, randomSelection);
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(STEP);
        moveSelectorConfig.setSelectionOrder(SHUFFLED);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(ShufflingMoveSelector.class, moveSelector);
        Assert.assertEquals(STEP, moveSelector.getCacheType());
        Assert.assertSame(baseMoveSelector, getChildMoveSelector());
    }

    @Test(expected = IllegalArgumentException.class)
    public void justInTimeShuffled() {
        final MoveSelector baseMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class);
        MoveSelectorConfig moveSelectorConfig = new MoveSelectorConfig() {
            @Override
            protected MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType, boolean randomSelection) {
                return baseMoveSelector;
            }
        };
        moveSelectorConfig.setCacheType(JUST_IN_TIME);
        moveSelectorConfig.setSelectionOrder(SHUFFLED);
        MoveSelector moveSelector = moveSelectorConfig.buildMoveSelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
    }
}

