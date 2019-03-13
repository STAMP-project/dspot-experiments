/**
 * Copyright 2013 the original author or authors.
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
package org.springframework.batch.core.job.flow.support;


import java.util.Comparator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.job.flow.State;
import org.springframework.batch.core.job.flow.StateSupport;


public class DefaultStateTransitionComparatorTests {
    private State state = new StateSupport("state1");

    private Comparator<StateTransition> comparator;

    @Test
    public void testSimpleOrderingEqual() {
        StateTransition transition = StateTransition.createStateTransition(state, "CONTIN???LE", "start");
        Assert.assertEquals(0, comparator.compare(transition, transition));
    }

    @Test
    public void testSimpleOrderingMoreGeneral() {
        StateTransition transition = StateTransition.createStateTransition(state, "CONTIN???LE", "start");
        StateTransition other = StateTransition.createStateTransition(state, "CONTINUABLE", "start");
        Assert.assertEquals(1, comparator.compare(transition, other));
        Assert.assertEquals((-1), comparator.compare(other, transition));
    }

    @Test
    public void testSimpleOrderingMostGeneral() {
        StateTransition transition = StateTransition.createStateTransition(state, "*", "start");
        StateTransition other = StateTransition.createStateTransition(state, "CONTINUABLE", "start");
        Assert.assertEquals(1, comparator.compare(transition, other));
        Assert.assertEquals((-1), comparator.compare(other, transition));
    }

    @Test
    public void testSubstringAndWildcard() {
        StateTransition transition = StateTransition.createStateTransition(state, "CONTIN*", "start");
        StateTransition other = StateTransition.createStateTransition(state, "CONTINUABLE", "start");
        Assert.assertEquals(1, comparator.compare(transition, other));
        Assert.assertEquals((-1), comparator.compare(other, transition));
    }

    @Test
    public void testSimpleOrderingMostToNextGeneral() {
        StateTransition transition = StateTransition.createStateTransition(state, "*", "start");
        StateTransition other = StateTransition.createStateTransition(state, "C?", "start");
        Assert.assertEquals(1, comparator.compare(transition, other));
        Assert.assertEquals((-1), comparator.compare(other, transition));
    }

    @Test
    public void testSimpleOrderingAdjacent() {
        StateTransition transition = StateTransition.createStateTransition(state, "CON*", "start");
        StateTransition other = StateTransition.createStateTransition(state, "CON?", "start");
        Assert.assertEquals(1, comparator.compare(transition, other));
        Assert.assertEquals((-1), comparator.compare(other, transition));
    }
}

