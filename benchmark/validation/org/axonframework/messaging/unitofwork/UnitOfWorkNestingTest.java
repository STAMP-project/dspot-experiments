/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.messaging.unitofwork;


import UnitOfWork.Phase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.axonframework.utils.MockException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class UnitOfWorkNestingTest {
    private List<UnitOfWorkNestingTest.PhaseTransition> phaseTransitions = new ArrayList<>();

    private UnitOfWork<?> outer;

    private UnitOfWork<?> middle;

    private UnitOfWork<?> inner;

    @Test
    public void testInnerUnitOfWorkNotifiedOfOuterCommitFailure() {
        outer.onPrepareCommit(( u) -> {
            inner.start();
            inner.commit();
        });
        outer.onCommit(( u) -> {
            throw new MockException();
        });
        outer.onCommit(( u) -> phaseTransitions.add(new org.axonframework.messaging.unitofwork.PhaseTransition(u, UnitOfWork.Phase.COMMIT, "x")));
        outer.start();
        try {
            outer.commit();
        } catch (MockException e) {
            // ok
        }
        Assert.assertFalse("The UnitOfWork hasn't been correctly cleared", CurrentUnitOfWork.isStarted());
        Assert.assertEquals(Arrays.asList(new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.PREPARE_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.PREPARE_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.COMMIT), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.COMMIT, "x"), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.ROLLBACK), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.ROLLBACK), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.CLEANUP), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.CLEANUP)), phaseTransitions);
    }

    @Test
    public void testInnerUnitOfWorkNotifiedOfOuterPrepareCommitFailure() {
        outer.onPrepareCommit(( u) -> {
            inner.start();
            inner.commit();
        });
        outer.onPrepareCommit(( u) -> {
            throw new MockException();
        });
        outer.start();
        try {
            outer.commit();
        } catch (MockException e) {
            // ok
        }
        Assert.assertFalse("The UnitOfWork hasn't been correctly cleared", CurrentUnitOfWork.isStarted());
        Assert.assertEquals(Arrays.asList(new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.PREPARE_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.PREPARE_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.ROLLBACK), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.ROLLBACK), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.CLEANUP), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.CLEANUP)), phaseTransitions);
    }

    @Test
    public void testInnerUnitOfWorkNotifiedOfOuterCommit() {
        outer.onPrepareCommit(( u) -> {
            inner.start();
            inner.commit();
        });
        outer.start();
        outer.commit();
        Assert.assertFalse("The UnitOfWork hasn't been correctly cleared", CurrentUnitOfWork.isStarted());
        Assert.assertEquals(Arrays.asList(new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.PREPARE_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.PREPARE_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.COMMIT), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.AFTER_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.AFTER_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.CLEANUP), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.CLEANUP)), phaseTransitions);
    }

    @Test
    public void testInnerUnitRollbackDoesNotAffectOuterCommit() {
        outer.onPrepareCommit(( u) -> {
            inner.start();
            inner.rollback(new MockException());
        });
        outer.start();
        outer.commit();
        Assert.assertFalse("The UnitOfWork hasn't been correctly cleared", CurrentUnitOfWork.isStarted());
        Assert.assertEquals(Arrays.asList(new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.PREPARE_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.ROLLBACK), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.COMMIT), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.AFTER_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.CLEANUP), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.CLEANUP)), phaseTransitions);
    }

    @Test
    public void testRollbackOfMiddleUnitOfWorkRollsBackInner() {
        outer.onPrepareCommit(( u) -> {
            middle.start();
            inner.start();
            inner.commit();
            middle.rollback();
        });
        outer.start();
        outer.commit();
        Assert.assertTrue("The middle UnitOfWork hasn't been correctly marked as rolled back", middle.isRolledBack());
        Assert.assertTrue("The inner UnitOfWork hasn't been correctly marked as rolled back", inner.isRolledBack());
        Assert.assertFalse("The out UnitOfWork has been incorrectly marked as rolled back", outer.isRolledBack());
        Assert.assertFalse("The UnitOfWork hasn't been correctly cleared", CurrentUnitOfWork.isStarted());
        Assert.assertEquals(// important that the inner has been given a rollback signal
        Arrays.asList(new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.PREPARE_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.PREPARE_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.ROLLBACK), new UnitOfWorkNestingTest.PhaseTransition(middle, Phase.ROLLBACK), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.COMMIT), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.AFTER_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.CLEANUP), new UnitOfWorkNestingTest.PhaseTransition(middle, Phase.CLEANUP), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.CLEANUP)), phaseTransitions);
    }

    @Test
    public void testInnerUnitCommitFailureDoesNotAffectOuterCommit() {
        outer.onPrepareCommit(( u) -> {
            inner.start();
            inner.onCommit(( uow) -> {
                throw new MockException();
            });
            // commits are invoked in reverse order, so we expect to see this one, but not the previously registered handler
            inner.onCommit(( uow) -> phaseTransitions.add(new org.axonframework.messaging.unitofwork.PhaseTransition(inner, UnitOfWork.Phase.COMMIT, "x")));
            try {
                inner.commit();
            } catch ( e) {
                // ok
            }
        });
        outer.start();
        outer.commit();
        Assert.assertFalse("The UnitOfWork hasn't been correctly cleared", CurrentUnitOfWork.isStarted());
        Assert.assertEquals(Arrays.asList(new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.PREPARE_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.PREPARE_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.COMMIT, "x"), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.ROLLBACK), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.COMMIT), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.AFTER_COMMIT), new UnitOfWorkNestingTest.PhaseTransition(inner, Phase.CLEANUP), new UnitOfWorkNestingTest.PhaseTransition(outer, Phase.CLEANUP)), phaseTransitions);
    }

    private static class PhaseTransition {
        private final Phase phase;

        private final UnitOfWork<?> unitOfWork;

        private final String id;

        public PhaseTransition(UnitOfWork<?> unitOfWork, UnitOfWork.Phase phase) {
            this(unitOfWork, phase, "");
        }

        public PhaseTransition(UnitOfWork<?> unitOfWork, UnitOfWork.Phase phase, String id) {
            this.unitOfWork = unitOfWork;
            this.phase = phase;
            this.id = ((id.length()) > 0) ? " " + id : id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            UnitOfWorkNestingTest.PhaseTransition that = ((UnitOfWorkNestingTest.PhaseTransition) (o));
            return ((Objects.equals(phase, that.phase)) && (Objects.equals(unitOfWork, that.unitOfWork))) && (Objects.equals(id, that.id));
        }

        @Override
        public int hashCode() {
            return Objects.hash(phase, unitOfWork, id);
        }

        @Override
        public String toString() {
            return (((unitOfWork) + " ") + (phase)) + (id);
        }
    }
}

