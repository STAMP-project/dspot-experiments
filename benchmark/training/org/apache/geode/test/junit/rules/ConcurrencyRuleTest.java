/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geode.test.junit.rules;


import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import junitparams.JUnitParamsRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JUnitParamsRunner.class)
public class ConcurrencyRuleTest {
    private final AtomicBoolean invoked = new AtomicBoolean();

    private final AtomicBoolean retVal = new AtomicBoolean();

    private final AtomicInteger iterations = new AtomicInteger(0);

    private final int stopIteration = 2;

    private final Integer expectedRetVal = Integer.valueOf(72);

    private final Throwable expectedException = new IllegalStateException("Oh boy, here I go testin' again");

    private final IllegalStateException expectedExceptionWithCause = new IllegalStateException("Oh boy, here I go testin' again");

    {
        expectedExceptionWithCause.initCause(new NullPointerException());
    }

    private final Callable<Boolean> callWithEventuallyCorrectRetVal = () -> {
        invoked.set(true);
        return retVal.get();
    };

    private final Callable<Integer> callWithRetVal = () -> {
        invoked.set(Boolean.TRUE);
        return Integer.valueOf(72);
    };

    private final Callable<Integer> callWithRetValAndRepeatCount = () -> {
        iterations.incrementAndGet();
        return Integer.valueOf(72);
    };

    private final Callable<Integer> callWithRetValAndRepeatCountAndOneWrongValue = () -> {
        int currentIteration = iterations.incrementAndGet();
        if (currentIteration == (stopIteration)) {
            return Integer.valueOf(3);
        }
        return Integer.valueOf(72);
    };

    private final Callable<Void> callWithExceptionAndCause = () -> {
        Exception e = new IllegalStateException("Oh boy, here I go testin' again");
        e.initCause(new NullPointerException());
        throw e;
    };

    private final Callable<Void> callWithExceptionAndRepeatCount = () -> {
        iterations.incrementAndGet();
        throw new IllegalStateException("Oh boy, here I go testin' again");
    };

    private final Callable<Integer> callWithOneExceptionAndRepeatCount = () -> {
        int currentIteration = iterations.incrementAndGet();
        if (currentIteration == (stopIteration)) {
            throw new IllegalStateException("Oh boy, here I go testin' again");
        }
        return Integer.valueOf(72);
    };

    @Rule
    public ConcurrencyRule concurrencyRule = new ConcurrencyRule();

    @Test
    public void failsWhenMultipleReturnValuesExpected_ExceptionAndReturn() {
        try {
            Throwable thrown = catchThrowable(() -> concurrencyRule.add(callWithRetVal).expectException(expectedException).expectValue(expectedRetVal));
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Specify only one expected outcome.");
        } finally {
            concurrencyRule.clear();
        }
    }

    @Test
    public void failsWhenMultipleReturnValuesExpected_ExceptionAndType() {
        try {
            Throwable thrown = catchThrowable(() -> concurrencyRule.add(callWithRetVal).expectException(expectedException).expectExceptionType(expectedException.getClass()));
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Specify only one expected outcome.");
        } finally {
            concurrencyRule.clear();
        }
    }

    @Test
    public void failsWhenMultipleReturnValuesExpected_ResultAndType() {
        try {
            Throwable thrown = catchThrowable(() -> concurrencyRule.add(callWithRetVal).expectValue(expectedRetVal).expectExceptionType(expectedException.getClass()));
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Specify only one expected outcome.");
        } finally {
            concurrencyRule.clear();
        }
    }

    @Test
    public void failsWhenMultipleReturnValuesExpected_ResultAndCauseType() {
        try {
            Throwable thrown = catchThrowable(() -> concurrencyRule.add(callWithRetVal).expectValue(expectedRetVal).expectExceptionCauseType(expectedException.getClass()));
            assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Specify only one expected outcome.");
        } finally {
            concurrencyRule.clear();
        }
    }

    @Test
    public void clearEmptiesThreadsToRun() {
        final AtomicBoolean b1 = new AtomicBoolean(Boolean.FALSE);
        final AtomicBoolean b2 = new AtomicBoolean(Boolean.FALSE);
        final AtomicBoolean b3 = new AtomicBoolean(Boolean.FALSE);
        final AtomicBoolean b4 = new AtomicBoolean(Boolean.FALSE);
        Callable c1 = () -> {
            b1.set(true);
            return null;
        };
        Callable c2 = () -> {
            b2.set(true);
            return null;
        };
        Callable c3 = () -> {
            b3.set(true);
            return null;
        };
        Callable c4 = () -> {
            b4.set(true);
            return null;
        };
        // submit some threads and check they did what they're supposed to
        concurrencyRule.add(c1);
        concurrencyRule.add(c2);
        concurrencyRule.add(c3).expectExceptionType(IllegalArgumentException.class);
        Throwable thrown = catchThrowable(() -> concurrencyRule.executeInParallel());
        assertThat(thrown).isInstanceOf(AssertionError.class);
        assertThat(b1).isTrue();
        assertThat(b2).isTrue();
        assertThat(b3).isTrue();
        assertThat(b4).isFalse();
        // reset the booleans
        b1.set(false);
        b2.set(false);
        b3.set(false);
        b4.set(false);
        // empty the list
        concurrencyRule.clear();
        // submit some more threads and check that ONLY those were executed
        concurrencyRule.add(c3);
        concurrencyRule.add(c4);
        assertThat(catchThrowable(() -> concurrencyRule.executeInParallel())).isNull();
        assertThat(b1).isFalse();
        assertThat(b2).isFalse();
        assertThat(b3).isTrue();
        assertThat(b4).isTrue();
    }

    @Test
    public void afterFailsIfThreadsWereNotRun() {
        Callable<Integer> c1 = () -> {
            return 2;
        };
        Callable<String> c2 = () -> {
            return "some string";
        };
        concurrencyRule.add(c1).expectValue(2);
        concurrencyRule.add(c1).expectValue(2).repeatForIterations(5);
        concurrencyRule.executeInParallel();
        concurrencyRule.add(c1).expectValue(3);
        concurrencyRule.add(c2).expectValue("some string");
        assertThatThrownBy(() -> concurrencyRule.after()).isInstanceOf(IllegalStateException.class).withFailMessage("exception should have been thrown");
        concurrencyRule.clear();// so that this test's after succeeds

    }

    @SuppressWarnings("unused")
    private enum Execution {

        EXECUTE_IN_SERIES(( concurrencyRule) -> {
            concurrencyRule.executeInSeries();
        }),
        EXECUTE_IN_PARALLEL(( concurrencyRule) -> {
            concurrencyRule.executeInParallel();
        });
        private final Consumer<ConcurrencyRule> execution;

        Execution(Consumer<ConcurrencyRule> execution) {
            this.execution = execution;
        }

        void execute(ConcurrencyRule concurrencyRule) {
            execution.accept(concurrencyRule);
        }
    }
}

