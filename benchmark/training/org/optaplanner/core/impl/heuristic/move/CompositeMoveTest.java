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
package org.optaplanner.core.impl.heuristic.move;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;


public class CompositeMoveTest {
    @Test
    public void createUndoMove() {
        InnerScoreDirector<TestdataSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(TestdataSolution.buildSolutionDescriptor());
        DummyMove a = new DummyMove("a");
        DummyMove b = new DummyMove("b");
        DummyMove c = new DummyMove("c");
        CompositeMove<TestdataSolution> move = new CompositeMove(a, b, c);
        CompositeMove<TestdataSolution> undoMove = move.doMove(scoreDirector);
        PlannerAssert.assertAllCodesOfArray(move.getMoves(), "a", "b", "c");
        PlannerAssert.assertAllCodesOfArray(undoMove.getMoves(), "undo c", "undo b", "undo a");
    }

    @Test
    public void createUndoMoveWithNonDoableMove() {
        InnerScoreDirector<TestdataSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(TestdataSolution.buildSolutionDescriptor());
        DummyMove a = new DummyMove("a");
        DummyMove b = new NotDoableDummyMove("b");
        DummyMove c = new DummyMove("c");
        CompositeMove<TestdataSolution> move = new CompositeMove(a, b, c);
        CompositeMove<TestdataSolution> undoMove = move.doMove(scoreDirector);
        PlannerAssert.assertAllCodesOfArray(move.getMoves(), "a", "b", "c");
        PlannerAssert.assertAllCodesOfArray(undoMove.getMoves(), "undo c", "undo a");
        a = new NotDoableDummyMove("a");
        b = new DummyMove("b");
        c = new NotDoableDummyMove("c");
        move = new CompositeMove(a, b, c);
        undoMove = move.doMove(scoreDirector);
        PlannerAssert.assertAllCodesOfArray(move.getMoves(), "a", "b", "c");
        PlannerAssert.assertAllCodesOfArray(undoMove.getMoves(), "undo b");
    }

    @Test
    public void doMove() {
        InnerScoreDirector<TestdataSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(TestdataSolution.buildSolutionDescriptor());
        DummyMove a = Mockito.mock(DummyMove.class);
        Mockito.when(a.isMoveDoable(ArgumentMatchers.any())).thenReturn(true);
        DummyMove b = Mockito.mock(DummyMove.class);
        Mockito.when(b.isMoveDoable(ArgumentMatchers.any())).thenReturn(true);
        DummyMove c = Mockito.mock(DummyMove.class);
        Mockito.when(c.isMoveDoable(ArgumentMatchers.any())).thenReturn(true);
        CompositeMove<TestdataSolution> move = new CompositeMove(a, b, c);
        move.doMove(scoreDirector);
        Mockito.verify(a, Mockito.times(1)).doMove(scoreDirector);
        Mockito.verify(b, Mockito.times(1)).doMove(scoreDirector);
        Mockito.verify(c, Mockito.times(1)).doMove(scoreDirector);
    }

    @Test
    public void rebase() {
        GenuineVariableDescriptor<TestdataSolution> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", null);
        TestdataEntity e3 = new TestdataEntity("e3", v1);
        TestdataValue destinationV1 = new TestdataValue("v1");
        TestdataValue destinationV2 = new TestdataValue("v2");
        TestdataEntity destinationE1 = new TestdataEntity("e1", destinationV1);
        TestdataEntity destinationE2 = new TestdataEntity("e2", null);
        TestdataEntity destinationE3 = new TestdataEntity("e3", destinationV1);
        ScoreDirector<TestdataSolution> destinationScoreDirector = PlannerTestUtils.mockRebasingScoreDirector(variableDescriptor.getEntityDescriptor().getSolutionDescriptor(), new Object[][]{ new Object[]{ v1, destinationV1 }, new Object[]{ v2, destinationV2 }, new Object[]{ e1, destinationE1 }, new Object[]{ e2, destinationE2 }, new Object[]{ e3, destinationE3 } });
        ChangeMove<TestdataSolution> a = new ChangeMove(e1, variableDescriptor, v2);
        ChangeMove<TestdataSolution> b = new ChangeMove(e2, variableDescriptor, v1);
        CompositeMove<TestdataSolution> rebaseMove = new CompositeMove(a, b).rebase(destinationScoreDirector);
        Move<TestdataSolution>[] rebasedChildMoves = rebaseMove.getMoves();
        Assert.assertEquals(2, rebasedChildMoves.length);
        ChangeMove<TestdataSolution> rebasedA = ((ChangeMove<TestdataSolution>) (rebasedChildMoves[0]));
        Assert.assertSame(destinationE1, rebasedA.getEntity());
        Assert.assertSame(destinationV2, rebasedA.getToPlanningValue());
        ChangeMove<TestdataSolution> rebasedB = ((ChangeMove<TestdataSolution>) (rebasedChildMoves[1]));
        Assert.assertSame(destinationE2, rebasedB.getEntity());
        Assert.assertSame(destinationV1, rebasedB.getToPlanningValue());
    }

    @Test
    public void buildEmptyMove() {
        PlannerAssert.assertInstanceOf(NoChangeMove.class, CompositeMove.buildMove(new ArrayList()));
        PlannerAssert.assertInstanceOf(NoChangeMove.class, CompositeMove.buildMove());
    }

    @Test
    public void buildOneElemMove() {
        DummyMove tmpMove = new DummyMove();
        Move<TestdataSolution> move = CompositeMove.buildMove(Collections.singletonList(tmpMove));
        PlannerAssert.assertInstanceOf(DummyMove.class, move);
        move = CompositeMove.buildMove(tmpMove);
        PlannerAssert.assertInstanceOf(DummyMove.class, move);
    }

    @Test
    public void buildTwoElemMove() {
        DummyMove first = new DummyMove();
        NoChangeMove<TestdataSolution> second = new NoChangeMove();
        Move<TestdataSolution> move = CompositeMove.buildMove(Arrays.asList(first, second));
        PlannerAssert.assertInstanceOf(CompositeMove.class, move);
        PlannerAssert.assertInstanceOf(DummyMove.class, getMoves()[0]);
        PlannerAssert.assertInstanceOf(NoChangeMove.class, getMoves()[1]);
        move = CompositeMove.buildMove(first, second);
        PlannerAssert.assertInstanceOf(CompositeMove.class, move);
        PlannerAssert.assertInstanceOf(DummyMove.class, getMoves()[0]);
        PlannerAssert.assertInstanceOf(NoChangeMove.class, getMoves()[1]);
    }

    @Test
    public void isMoveDoable() {
        InnerScoreDirector<TestdataSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(TestdataSolution.buildSolutionDescriptor());
        DummyMove first = new DummyMove();
        DummyMove second = new DummyMove();
        Move<TestdataSolution> move = CompositeMove.buildMove(first, second);
        Assert.assertEquals(true, move.isMoveDoable(scoreDirector));
        first = new DummyMove();
        second = new NotDoableDummyMove();
        move = CompositeMove.buildMove(first, second);
        Assert.assertEquals(true, move.isMoveDoable(scoreDirector));
        first = new NotDoableDummyMove();
        second = new DummyMove();
        move = CompositeMove.buildMove(first, second);
        Assert.assertEquals(true, move.isMoveDoable(scoreDirector));
        first = new NotDoableDummyMove();
        second = new NotDoableDummyMove();
        move = CompositeMove.buildMove(first, second);
        Assert.assertEquals(false, move.isMoveDoable(scoreDirector));
    }

    @Test
    public void equals() {
        DummyMove first = new DummyMove();
        NoChangeMove<TestdataSolution> second = new NoChangeMove();
        Move<TestdataSolution> move = CompositeMove.buildMove(Arrays.asList(first, second));
        Move<TestdataSolution> other = CompositeMove.buildMove(first, second);
        Assert.assertTrue(move.equals(other));
        move = CompositeMove.buildMove(first, second);
        other = CompositeMove.buildMove(second, first);
        Assert.assertFalse(move.equals(other));
        Assert.assertFalse(move.equals(new DummyMove()));
        Assert.assertTrue(move.equals(move));
    }

    @Test
    public void interconnectedChildMoves() {
        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", v2);
        solution.setEntityList(Arrays.asList(e1, e2));
        GenuineVariableDescriptor<TestdataSolution> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        SwapMove<TestdataSolution> first = new SwapMove(Collections.singletonList(variableDescriptor), e1, e2);
        ChangeMove<TestdataSolution> second = new ChangeMove(e1, variableDescriptor, v3);
        Move<TestdataSolution> move = CompositeMove.buildMove(first, second);
        Assert.assertSame(v1, e1.getValue());
        Assert.assertSame(v2, e2.getValue());
        ScoreDirector<TestdataSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(variableDescriptor.getEntityDescriptor().getSolutionDescriptor());
        Move<TestdataSolution> undoMove = move.doMove(scoreDirector);
        Assert.assertSame(v3, e1.getValue());
        Assert.assertSame(v1, e2.getValue());
        undoMove.doMove(scoreDirector);
        Assert.assertSame(v1, e1.getValue());
        Assert.assertSame(v2, e2.getValue());
    }
}

