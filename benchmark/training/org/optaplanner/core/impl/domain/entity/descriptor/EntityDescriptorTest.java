/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.domain.entity.descriptor;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.immovable.TestdataImmovableEntity;
import org.optaplanner.core.impl.testdata.domain.immovable.extended.TestdataExtendedImmovableEntity;
import org.optaplanner.core.impl.testdata.domain.immovable.extended.TestdataExtendedImmovableSolution;


public class EntityDescriptorTest {
    @Test
    public void movableEntitySelectionFilter() {
        ScoreDirector scoreDirector = Mockito.mock(ScoreDirector.class);
        EntityDescriptor entityDescriptor = TestdataImmovableEntity.buildEntityDescriptor();
        Assert.assertEquals(true, entityDescriptor.hasEffectiveMovableEntitySelectionFilter());
        SelectionFilter movableEntitySelectionFilter = entityDescriptor.getEffectiveMovableEntitySelectionFilter();
        Assert.assertNotNull(movableEntitySelectionFilter);
        Assert.assertEquals(true, movableEntitySelectionFilter.accept(scoreDirector, new TestdataImmovableEntity("e1", null, false, false)));
        Assert.assertEquals(false, movableEntitySelectionFilter.accept(scoreDirector, new TestdataImmovableEntity("e2", null, true, false)));
    }

    @Test
    public void extendedMovableEntitySelectionFilterUsedByChildSelector() {
        ScoreDirector scoreDirector = Mockito.mock(ScoreDirector.class);
        SolutionDescriptor solutionDescriptor = TestdataExtendedImmovableSolution.buildSolutionDescriptor();
        EntityDescriptor childEntityDescriptor = solutionDescriptor.findEntityDescriptor(TestdataExtendedImmovableEntity.class);
        Assert.assertEquals(true, childEntityDescriptor.hasEffectiveMovableEntitySelectionFilter());
        SelectionFilter childMovableEntitySelectionFilter = childEntityDescriptor.getEffectiveMovableEntitySelectionFilter();
        Assert.assertNotNull(childMovableEntitySelectionFilter);
        // No new TestdataImmovableEntity() because a child selector would never select a pure parent instance
        Assert.assertEquals(true, childMovableEntitySelectionFilter.accept(scoreDirector, new TestdataExtendedImmovableEntity("e3", null, false, false, null, false, false)));
        Assert.assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector, new TestdataExtendedImmovableEntity("e4", null, true, false, null, false, false)));
        Assert.assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector, new TestdataExtendedImmovableEntity("e5", null, false, true, null, false, false)));
        Assert.assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector, new TestdataExtendedImmovableEntity("e6", null, false, false, null, true, false)));
        Assert.assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector, new TestdataExtendedImmovableEntity("e7", null, false, false, null, false, true)));
        Assert.assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector, new TestdataExtendedImmovableEntity("e8", null, true, true, null, true, true)));
    }
}

