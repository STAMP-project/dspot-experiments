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
package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.DefaultSubChainSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;


public class SubChainChangeMoveSelectorTest {
    @Test(expected = IllegalStateException.class)
    public void differentValueDescriptorException() {
        SubChainSelector subChainSelector = Mockito.mock(DefaultSubChainSelector.class);
        GenuineVariableDescriptor descriptor = TestdataEntity.buildVariableDescriptorForValue();
        Mockito.when(subChainSelector.getVariableDescriptor()).thenReturn(descriptor);
        EntityIndependentValueSelector valueSelector = Mockito.mock(EntityIndependentValueSelector.class);
        GenuineVariableDescriptor otherDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        Mockito.when(valueSelector.getVariableDescriptor()).thenReturn(otherDescriptor);
        SubChainChangeMoveSelector testedSelector = new SubChainChangeMoveSelector(subChainSelector, valueSelector, true, true);
    }

    @Test(expected = IllegalStateException.class)
    public void determinedSelectionWithNeverEndingChainSelector() {
        SubChainSelector subChainSelector = Mockito.mock(DefaultSubChainSelector.class);
        Mockito.when(subChainSelector.isNeverEnding()).thenReturn(true);
        GenuineVariableDescriptor descriptor = TestdataEntity.buildVariableDescriptorForValue();
        Mockito.when(subChainSelector.getVariableDescriptor()).thenReturn(descriptor);
        EntityIndependentValueSelector valueSelector = Mockito.mock(EntityIndependentValueSelector.class);
        Mockito.when(valueSelector.getVariableDescriptor()).thenReturn(descriptor);
        SubChainChangeMoveSelector testedSelector = new SubChainChangeMoveSelector(subChainSelector, valueSelector, false, true);
    }

    @Test(expected = IllegalStateException.class)
    public void determinedSelectionWithNeverEndingValueSelector() {
        SubChainSelector subChainSelector = Mockito.mock(DefaultSubChainSelector.class);
        GenuineVariableDescriptor descriptor = TestdataEntity.buildVariableDescriptorForValue();
        Mockito.when(subChainSelector.getVariableDescriptor()).thenReturn(descriptor);
        EntityIndependentValueSelector valueSelector = Mockito.mock(EntityIndependentValueSelector.class);
        Mockito.when(valueSelector.isNeverEnding()).thenReturn(true);
        Mockito.when(valueSelector.getVariableDescriptor()).thenReturn(descriptor);
        SubChainChangeMoveSelector testedSelector = new SubChainChangeMoveSelector(subChainSelector, valueSelector, false, true);
    }

    @Test
    public void isCountable() {
        SubChainSelector subChainSelector = Mockito.mock(DefaultSubChainSelector.class);
        GenuineVariableDescriptor descriptor = TestdataEntity.buildVariableDescriptorForValue();
        Mockito.when(subChainSelector.getVariableDescriptor()).thenReturn(descriptor);
        EntityIndependentValueSelector valueSelector = Mockito.mock(EntityIndependentValueSelector.class);
        Mockito.when(valueSelector.getVariableDescriptor()).thenReturn(descriptor);
        SubChainChangeMoveSelector testedSelector = new SubChainChangeMoveSelector(subChainSelector, valueSelector, true, true);
        Mockito.when(subChainSelector.isCountable()).thenReturn(false);
        Mockito.when(valueSelector.isCountable()).thenReturn(true);
        Assert.assertEquals(false, testedSelector.isCountable());
        Mockito.when(subChainSelector.isCountable()).thenReturn(true);
        Mockito.when(valueSelector.isCountable()).thenReturn(false);
        Assert.assertEquals(false, testedSelector.isCountable());
        Mockito.when(subChainSelector.isCountable()).thenReturn(true);
        Mockito.when(valueSelector.isCountable()).thenReturn(true);
        Assert.assertEquals(true, testedSelector.isCountable());
        Mockito.when(subChainSelector.isCountable()).thenReturn(false);
        Mockito.when(valueSelector.isCountable()).thenReturn(false);
        Assert.assertEquals(false, testedSelector.isCountable());
    }

    @Test
    public void getSize() {
        SubChainSelector subChainSelector = Mockito.mock(DefaultSubChainSelector.class);
        GenuineVariableDescriptor descriptor = TestdataEntity.buildVariableDescriptorForValue();
        Mockito.when(subChainSelector.getVariableDescriptor()).thenReturn(descriptor);
        EntityIndependentValueSelector valueSelector = Mockito.mock(EntityIndependentValueSelector.class);
        Mockito.when(valueSelector.getVariableDescriptor()).thenReturn(descriptor);
        SubChainChangeMoveSelector testedSelector = new SubChainChangeMoveSelector(subChainSelector, valueSelector, true, true);
        Mockito.when(subChainSelector.getSize()).thenReturn(1L);
        Mockito.when(valueSelector.getSize()).thenReturn(2L);
        Assert.assertEquals(2, testedSelector.getSize());
        Mockito.when(subChainSelector.getSize()).thenReturn(100L);
        Mockito.when(valueSelector.getSize()).thenReturn(200L);
        Assert.assertEquals(20000, testedSelector.getSize());
    }
}

