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
package org.optaplanner.core.config.heuristic.selector.value;


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
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.value.FromSolutionPropertyValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.ShufflingValueSelector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class ValueSelectorConfigTest extends AbstractSelectorConfigTest {
    @Test
    public void phaseOriginal() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor().findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(PHASE);
        valueSelectorConfig.setSelectionOrder(ORIGINAL);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(configPolicy, entityDescriptor, JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(FromSolutionPropertyValueSelector.class, valueSelector);
        PlannerAssert.assertNotInstanceOf(ShufflingValueSelector.class, valueSelector);
        Assert.assertEquals(PHASE, valueSelector.getCacheType());
    }

    @Test
    public void stepOriginal() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor().findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(STEP);
        valueSelectorConfig.setSelectionOrder(ORIGINAL);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(configPolicy, entityDescriptor, JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(FromSolutionPropertyValueSelector.class, valueSelector);
        PlannerAssert.assertNotInstanceOf(ShufflingValueSelector.class, valueSelector);
        // PHASE instead of STEP because these values are cacheable, so there's no reason not to cache them?
        Assert.assertEquals(PHASE, valueSelector.getCacheType());
    }

    @Test
    public void justInTimeOriginal() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor().findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(JUST_IN_TIME);
        valueSelectorConfig.setSelectionOrder(ORIGINAL);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(configPolicy, entityDescriptor, JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(FromSolutionPropertyValueSelector.class, valueSelector);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, valueSelector.getCacheType());
    }

    @Test
    public void phaseRandom() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor().findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(PHASE);
        valueSelectorConfig.setSelectionOrder(RANDOM);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(configPolicy, entityDescriptor, JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(FromSolutionPropertyValueSelector.class, valueSelector);
        PlannerAssert.assertNotInstanceOf(ShufflingValueSelector.class, valueSelector);
        Assert.assertEquals(PHASE, valueSelector.getCacheType());
    }

    @Test
    public void stepRandom() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor().findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(STEP);
        valueSelectorConfig.setSelectionOrder(RANDOM);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(configPolicy, entityDescriptor, JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(FromSolutionPropertyValueSelector.class, valueSelector);
        PlannerAssert.assertNotInstanceOf(ShufflingValueSelector.class, valueSelector);
        // PHASE instead of STEP because these values are cacheable, so there's no reason not to cache them?
        Assert.assertEquals(PHASE, valueSelector.getCacheType());
    }

    @Test
    public void justInTimeRandom() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor().findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(JUST_IN_TIME);
        valueSelectorConfig.setSelectionOrder(RANDOM);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(configPolicy, entityDescriptor, JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(FromSolutionPropertyValueSelector.class, valueSelector);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, valueSelector.getCacheType());
    }

    @Test
    public void phaseShuffled() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor().findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(PHASE);
        valueSelectorConfig.setSelectionOrder(SHUFFLED);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(configPolicy, entityDescriptor, JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(ShufflingValueSelector.class, valueSelector);
        PlannerAssert.assertInstanceOf(FromSolutionPropertyValueSelector.class, getChildValueSelector());
        Assert.assertEquals(PHASE, valueSelector.getCacheType());
    }

    @Test
    public void stepShuffled() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor().findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(STEP);
        valueSelectorConfig.setSelectionOrder(SHUFFLED);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(configPolicy, entityDescriptor, JUST_IN_TIME, RANDOM);
        PlannerAssert.assertInstanceOf(ShufflingValueSelector.class, valueSelector);
        PlannerAssert.assertInstanceOf(FromSolutionPropertyValueSelector.class, getChildValueSelector());
        Assert.assertEquals(STEP, valueSelector.getCacheType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void justInTimeShuffled() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor().findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setCacheType(JUST_IN_TIME);
        valueSelectorConfig.setSelectionOrder(SHUFFLED);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(configPolicy, entityDescriptor, JUST_IN_TIME, RANDOM);
    }
}

