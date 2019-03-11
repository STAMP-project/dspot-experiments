/**
 * Copyright 2014 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.parallel;


import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.impl.block.factory.ObjectIntProcedures;
import com.gs.collections.impl.block.procedure.DoNothingProcedure;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import org.junit.Test;


public class ObjectIntProcedureFJTaskRunnerTest {
    private ObjectIntProcedureFJTaskRunner<Integer, ObjectIntProcedure<Integer>> undertest;

    @Test
    public void taskCompletedUsingNonCombineOne() {
        Verify.assertThrows(ObjectIntProcedureFJTaskRunnerTest.CountDownCalledException.class, () -> this.undertest.taskCompleted(null));
    }

    @Test
    public void joinUsingNonCombineOne() {
        Verify.assertThrows(ObjectIntProcedureFJTaskRunnerTest.AwaitDownCalledException.class, () -> this.undertest.executeAndCombine(new com.gs.collections.impl.parallel.DoNothingExecutor(), new com.gs.collections.impl.parallel.PassThroughObjectIntProcedureFactory(), FastList.<Integer>newList()));
    }

    private static class DoNothingWithFalseCombineOneCombiner implements Combiner<ObjectIntProcedure<Integer>> {
        private static final long serialVersionUID = 1L;

        @Override
        public void combineAll(Iterable<ObjectIntProcedure<Integer>> thingsToCombine) {
        }

        @Override
        public void combineOne(ObjectIntProcedure<Integer> thingToCombine) {
        }

        @Override
        public boolean useCombineOne() {
            return false;
        }
    }

    private static class CountDownCalledException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private static class AwaitDownCalledException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private static final class MockLatch extends CountDownLatch {
        private MockLatch() {
            super(1);
        }

        @Override
        public void countDown() {
            throw new ObjectIntProcedureFJTaskRunnerTest.CountDownCalledException();
        }

        @Override
        public void await() {
            throw new ObjectIntProcedureFJTaskRunnerTest.AwaitDownCalledException();
        }
    }

    private static class DoNothingExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
        }
    }

    private static class PassThroughObjectIntProcedureFactory implements ObjectIntProcedureFactory<ObjectIntProcedure<Integer>> {
        @Override
        public ObjectIntProcedure<Integer> create() {
            return this.getPassThroughObjectIntProcedure();
        }

        private ObjectIntProcedure<Integer> getPassThroughObjectIntProcedure() {
            return ObjectIntProcedures.fromProcedure(DoNothingProcedure.DO_NOTHING);
        }
    }
}

