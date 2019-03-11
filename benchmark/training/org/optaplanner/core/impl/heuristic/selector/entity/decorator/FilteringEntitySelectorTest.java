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
package org.optaplanner.core.impl.heuristic.selector.entity.decorator;


import SelectionCacheType.JUST_IN_TIME;
import SelectionCacheType.PHASE;
import SelectionCacheType.SOLVER;
import SelectionCacheType.STEP;
import org.junit.Test;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;


public class FilteringEntitySelectorTest {
    @Test
    public void filterCacheTypeSolver() {
        filter(SOLVER, 1);
    }

    @Test
    public void filterCacheTypePhase() {
        filter(PHASE, 2);
    }

    @Test
    public void filterCacheTypeStep() {
        filter(STEP, 5);
    }

    @Test
    public void filterCacheTypeJustInTime() {
        filter(JUST_IN_TIME, 5);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listIteratorWithRandomSelection() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        EntitySelector entitySelector = new FilteringEntitySelector(childEntitySelector, null);
        entitySelector.listIterator();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void indexedListIteratorWithRandomSelection() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        EntitySelector entitySelector = new FilteringEntitySelector(childEntitySelector, null);
        entitySelector.listIterator(0);
    }
}

