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


import SelectionCacheType.PHASE;
import SelectionCacheType.SOLVER;
import SelectionCacheType.STEP;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.CachedListRandomIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public class CachingEntitySelectorTest {
    @Test
    public void originalSelectionCacheTypeSolver() {
        runOriginalSelection(SOLVER, 1);
    }

    @Test
    public void originalSelectionCacheTypePhase() {
        runOriginalSelection(PHASE, 2);
    }

    @Test
    public void originalSelectionCacheTypeStep() {
        runOriginalSelection(STEP, 5);
    }

    @Test(expected = IllegalStateException.class)
    public void listIteratorWithRandomSelection() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        CachingEntitySelector cachingEntitySelector = new CachingEntitySelector(childEntitySelector, SelectionCacheType.PHASE, true);
        cachingEntitySelector.listIterator();
    }

    @Test(expected = IllegalStateException.class)
    public void indexedListIteratorWithRandomSelection() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        CachingEntitySelector cachingEntitySelector = new CachingEntitySelector(childEntitySelector, SelectionCacheType.PHASE, true);
        cachingEntitySelector.listIterator(0);
    }

    @Test
    public void isNeverEnding() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        CachingEntitySelector cachingEntitySelector = new CachingEntitySelector(childEntitySelector, SelectionCacheType.PHASE, true);
        Assert.assertTrue(cachingEntitySelector.isNeverEnding());
        cachingEntitySelector = new CachingEntitySelector(childEntitySelector, SelectionCacheType.PHASE, false);
        Assert.assertFalse(cachingEntitySelector.isNeverEnding());
    }

    @Test
    public void iterator() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        Mockito.when(childEntitySelector.getSize()).thenReturn(1L);
        CachingEntitySelector cachingEntitySelector = new CachingEntitySelector(childEntitySelector, SelectionCacheType.PHASE, true);
        cachingEntitySelector.constructCache(null);
        PlannerAssert.assertInstanceOf(CachedListRandomIterator.class, cachingEntitySelector.iterator());
        cachingEntitySelector = new CachingEntitySelector(childEntitySelector, SelectionCacheType.PHASE, false);
        cachingEntitySelector.constructCache(null);
        PlannerAssert.assertNotInstanceOf(CachedListRandomIterator.class, cachingEntitySelector.iterator());
    }
}

