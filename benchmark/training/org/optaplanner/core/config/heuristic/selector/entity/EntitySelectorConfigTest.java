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
package org.optaplanner.core.config.heuristic.selector.entity;


import SelectionCacheType.JUST_IN_TIME;
import SelectionCacheType.PHASE;
import SelectionCacheType.STEP;
import SelectionOrder.ORIGINAL;
import SelectionOrder.RANDOM;
import SelectionOrder.SHUFFLED;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.config.heuristic.selector.AbstractSelectorConfigTest;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.FromSolutionEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.ShufflingEntitySelector;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class EntitySelectorConfigTest extends AbstractSelectorConfigTest {
    @Test
    public void phaseOriginal() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(PHASE);
        entitySelectorConfig.setSelectionOrder(ORIGINAL);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(FromSolutionEntitySelector.class, entitySelector);
        PlannerAssert.assertNotInstanceOf(ShufflingEntitySelector.class, entitySelector);
        Assert.assertEquals(PHASE, entitySelector.getCacheType());
    }

    @Test
    public void stepOriginal() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(STEP);
        entitySelectorConfig.setSelectionOrder(ORIGINAL);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(FromSolutionEntitySelector.class, entitySelector);
        PlannerAssert.assertNotInstanceOf(ShufflingEntitySelector.class, entitySelector);
        Assert.assertEquals(STEP, entitySelector.getCacheType());
    }

    @Test
    public void justInTimeOriginal() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(JUST_IN_TIME);
        entitySelectorConfig.setSelectionOrder(ORIGINAL);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(FromSolutionEntitySelector.class, entitySelector);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, entitySelector.getCacheType());
    }

    @Test
    public void phaseRandom() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(PHASE);
        entitySelectorConfig.setSelectionOrder(RANDOM);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(FromSolutionEntitySelector.class, entitySelector);
        PlannerAssert.assertNotInstanceOf(ShufflingEntitySelector.class, entitySelector);
        Assert.assertEquals(PHASE, entitySelector.getCacheType());
    }

    @Test
    public void stepRandom() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(STEP);
        entitySelectorConfig.setSelectionOrder(RANDOM);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(FromSolutionEntitySelector.class, entitySelector);
        PlannerAssert.assertNotInstanceOf(ShufflingEntitySelector.class, entitySelector);
        Assert.assertEquals(STEP, entitySelector.getCacheType());
    }

    @Test
    public void justInTimeRandom() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(JUST_IN_TIME);
        entitySelectorConfig.setSelectionOrder(RANDOM);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(FromSolutionEntitySelector.class, entitySelector);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, entitySelector.getCacheType());
    }

    @Test
    public void phaseShuffled() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(PHASE);
        entitySelectorConfig.setSelectionOrder(SHUFFLED);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(ShufflingEntitySelector.class, entitySelector);
        PlannerAssert.assertInstanceOf(FromSolutionEntitySelector.class, getChildEntitySelector());
        Assert.assertEquals(PHASE, entitySelector.getCacheType());
    }

    @Test
    public void stepShuffled() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(STEP);
        entitySelectorConfig.setSelectionOrder(SHUFFLED);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(ShufflingEntitySelector.class, entitySelector);
        PlannerAssert.assertInstanceOf(FromSolutionEntitySelector.class, getChildEntitySelector());
        Assert.assertEquals(STEP, entitySelector.getCacheType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void justInTimeShuffled() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(JUST_IN_TIME);
        entitySelectorConfig.setSelectionOrder(SHUFFLED);
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(buildHeuristicConfigPolicy(), JUST_IN_TIME, RANDOM);
    }
}

