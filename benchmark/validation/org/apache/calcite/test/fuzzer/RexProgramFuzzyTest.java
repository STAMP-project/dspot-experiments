/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.test.fuzzer;


import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.PriorityQueue;
import org.apache.calcite.plan.Strong;
import org.apache.calcite.test.RexProgramBuilderBase;
import org.apache.calcite.util.ImmutableBitSet;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Validates that {@link org.apache.calcite.rex.RexSimplify} is able to deal with
 * randomized {@link RexNode}.
 * Note: the default fuzzing time is 5 seconds to keep overall test duration reasonable.
 * The test starts from a random point every time, so the longer it runs the more errors it detects.
 *
 * <p>Note: The test is not included to {@link org.apache.calcite.test.CalciteSuite} since it would
 * fail every build (there are lots of issues with {@link org.apache.calcite.rex.RexSimplify})
 */
public class RexProgramFuzzyTest extends RexProgramBuilderBase {
    protected static final Logger LOGGER = LoggerFactory.getLogger(RexProgramFuzzyTest.class);

    private static final Duration TEST_DURATION = Duration.of(Integer.getInteger("rex.fuzzing.duration", 5), ChronoUnit.SECONDS);

    private static final long TEST_ITERATIONS = Long.getLong("rex.fuzzing.iterations", 18);

    // Stop fuzzing after detecting MAX_FAILURES errors
    private static final int MAX_FAILURES = Integer.getInteger("rex.fuzzing.max.failures", 1);

    // Number of slowest to simplify expressions to show
    private static final int TOPN_SLOWEST = Integer.getInteger("rex.fuzzing.max.slowest", 0);

    // 0 means use random seed
    // 42 is used to make sure tests pass in CI
    private static final long SEED = Long.getLong("rex.fuzzing.seed", 44);

    private PriorityQueue<SimplifyTask> slowestTasks;

    private long currentSeed = 0;

    private static final Strong STRONG = Strong.of(ImmutableBitSet.of());

    /**
     * A bounded variation of {@link PriorityQueue}
     *
     * @param <E>
     * 		the type of elements held in this collection
     */
    private static class TopN<E extends Comparable<E>> extends PriorityQueue<E> {
        private final int n;

        private TopN(int n) {
            this.n = n;
        }

        @Override
        public boolean offer(E o) {
            if ((size()) == (n)) {
                E peek = peek();
                if ((peek != null) && ((peek.compareTo(o)) > 0)) {
                    // If the smallest element in the queue exceeds the added one
                    // then just ignore the offer
                    return false;
                }
                // otherwise extract the smallest element, and offer a new one
                poll();
            }
            return super.offer(o);
        }

        @Override
        public Iterator<E> iterator() {
            throw new UnsupportedOperationException("Order of elements is not defined, please use .peek");
        }
    }

    /**
     * Verifies {@code IS TRUE(IS NULL(null))} kind of expressions up to 4 level deep.
     */
    @Test
    public void testNestedCalls() {
        nestedCalls(trueLiteral);
        nestedCalls(falseLiteral);
        nestedCalls(nullBool);
        nestedCalls(vBool());
        nestedCalls(vBoolNotNull());
    }

    @Test
    public void defaultFuzzTest() {
        try {
            runRexFuzzer(0, Duration.of(5, ChronoUnit.SECONDS), 1, 0, 0);
        } catch (Throwable e) {
            for (Throwable t = e; t != null; t = t.getCause()) {
                RexProgramFuzzyTest.trimStackTrace(t, 4);
            }
            RexProgramFuzzyTest.LOGGER.info("Randomized test identified a potential defect. Feel free to fix that issue", e);
        }
    }

    @Test
    public void testFuzzy() {
        runRexFuzzer(RexProgramFuzzyTest.SEED, RexProgramFuzzyTest.TEST_DURATION, RexProgramFuzzyTest.MAX_FAILURES, RexProgramFuzzyTest.TEST_ITERATIONS, RexProgramFuzzyTest.TOPN_SLOWEST);
    }
}

/**
 * End RexProgramFuzzyTest.java
 */
