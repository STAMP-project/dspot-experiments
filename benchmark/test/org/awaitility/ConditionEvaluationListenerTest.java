/**
 * Copyright 2015 the original author or authors.
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
package org.awaitility;


import Duration.ONE_SECOND;
import Duration.TEN_SECONDS;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.awaitility.core.EvaluatedCondition;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ConditionEvaluationListenerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(expected = RuntimeException.class)
    public void listenerExceptionsAreNotCaught() {
        Awaitility.with().catchUncaughtExceptions().conditionEvaluationListener(new org.awaitility.core.ConditionEvaluationListener<Integer>() {
            public void conditionEvaluated(EvaluatedCondition<Integer> condition) {
                throw new RuntimeException();
            }
        }).until(new ConditionEvaluationListenerTest.CountDown(10), is(equalTo(0)));
    }

    @Test(timeout = 2000)
    public void settingDefaultHandlerWillImpactAllAwaitStatements() {
        final ConditionEvaluationListenerTest.CountDown globalCountDown = new ConditionEvaluationListenerTest.CountDown(20);
        org.awaitility.core.ConditionEvaluationListener defaultConditionEvaluationListener = new org.awaitility.core.ConditionEvaluationListener<Integer>() {
            public void conditionEvaluated(EvaluatedCondition<Integer> condition) {
                try {
                    if (!(condition.isSatisfied())) {
                        globalCountDown.call();
                    }
                } catch (Exception ignored) {
                }
            }
        };
        Awaitility.setDefaultConditionEvaluationListener(defaultConditionEvaluationListener);
        Awaitility.with().until(new ConditionEvaluationListenerTest.CountDown(5), is(equalTo(0)));
        MatcherAssert.assertThat(globalCountDown.get(), is(equalTo(15)));
        Awaitility.with().until(new ConditionEvaluationListenerTest.CountDown(5), is(equalTo(0)));
        MatcherAssert.assertThat(globalCountDown.get(), is(equalTo(10)));
    }

    @Test(timeout = 2000)
    public void defaultHandlerCanBeDisabledPerAwaitStatement() {
        final ConditionEvaluationListenerTest.CountDown globalCountDown = new ConditionEvaluationListenerTest.CountDown(20);
        org.awaitility.core.ConditionEvaluationListener defaultConditionEvaluationListener = new org.awaitility.core.ConditionEvaluationListener<Integer>() {
            public void conditionEvaluated(EvaluatedCondition<Integer> condition) {
                try {
                    if (!(condition.isSatisfied())) {
                        globalCountDown.call();
                    }
                } catch (Exception ignored) {
                }
            }
        };
        Awaitility.setDefaultConditionEvaluationListener(defaultConditionEvaluationListener);
        Awaitility.with().until(new ConditionEvaluationListenerTest.CountDown(5), is(equalTo(0)));
        MatcherAssert.assertThat(globalCountDown.get(), is(equalTo(15)));
        Awaitility.with().conditionEvaluationListener(null).until(new ConditionEvaluationListenerTest.CountDown(5), is(equalTo(0)));
        MatcherAssert.assertThat(globalCountDown.get(), is(equalTo(15)));
        Awaitility.with().until(new ConditionEvaluationListenerTest.CountDown(5), is(equalTo(0)));
        MatcherAssert.assertThat(globalCountDown.get(), is(equalTo(10)));
    }

    @Test(timeout = 2000)
    public void afterAwaitilityResetNoDefaultHandlerIsSet() {
        final ConditionEvaluationListenerTest.CountDown globalCountDown = new ConditionEvaluationListenerTest.CountDown(20);
        org.awaitility.core.ConditionEvaluationListener defaultConditionEvaluationListener = new org.awaitility.core.ConditionEvaluationListener<Integer>() {
            public void conditionEvaluated(EvaluatedCondition<Integer> condition) {
                try {
                    if (!(condition.isSatisfied())) {
                        globalCountDown.call();
                    }
                } catch (Exception ignored) {
                }
            }
        };
        Awaitility.setDefaultConditionEvaluationListener(defaultConditionEvaluationListener);
        Awaitility.with().until(new ConditionEvaluationListenerTest.CountDown(5), is(equalTo(0)));
        MatcherAssert.assertThat(globalCountDown.get(), is(equalTo(15)));
        Awaitility.reset();
        Awaitility.with().until(new ConditionEvaluationListenerTest.CountDown(5), is(equalTo(0)));
        MatcherAssert.assertThat(globalCountDown.get(), is(equalTo(15)));
    }

    @Test(timeout = 10000)
    public void conditionResultsCanBeLoggedToSystemOut() {
        Awaitility.with().conditionEvaluationListener(new org.awaitility.core.ConditionEvaluationListener<Integer>() {
            public void conditionEvaluated(EvaluatedCondition<Integer> condition) {
                if (condition.isSatisfied()) {
                    System.out.printf("%s (in %ds)%n", condition.getDescription(), ((condition.getElapsedTimeInMS()) / 1000));
                } else {
                    System.out.printf("%s (elapsed time %ds, remaining time %ds)%n", condition.getDescription(), ((condition.getElapsedTimeInMS()) / 1000), ((condition.getRemainingTimeInMS()) / 1000));
                }
            }
        }).pollInterval(ONE_SECOND).atMost(TEN_SECONDS).until(new ConditionEvaluationListenerTest.CountDown(5), is(equalTo(0)));
    }

    @Test(timeout = 2000)
    public void conditionResultsCanBeBuffered() {
        final List<String> buffer = new ArrayList<String>();
        Awaitility.with().conditionEvaluationListener(new org.awaitility.core.ConditionEvaluationListener<Integer>() {
            public void conditionEvaluated(EvaluatedCondition<Integer> condition) {
                String msg = String.format("%s (elapsed time %ds, remaining time %ds)%n", condition.getDescription(), ((condition.getElapsedTimeInMS()) / 1000), ((condition.getRemainingTimeInMS()) / 1000));
                buffer.add(msg);
            }
        }).until(new ConditionEvaluationListenerTest.CountDown(5), is(equalTo(0)));
        MatcherAssert.assertThat(buffer.size(), is(equalTo((5 + 1))));
    }

    @Test(timeout = 2000)
    public void expectedMismatchMessageForComplexMatchers() {
        final ConditionEvaluationListenerTest.ValueHolder<String> lastMismatchMessage = new ConditionEvaluationListenerTest.ValueHolder<String>();
        Awaitility.with().conditionEvaluationListener(new org.awaitility.core.ConditionEvaluationListener<ConditionEvaluationListenerTest.CountDownBean>() {
            public void conditionEvaluated(EvaluatedCondition<ConditionEvaluationListenerTest.CountDownBean> condition) {
                if (!(condition.isSatisfied())) {
                    lastMismatchMessage.value = condition.getDescription();
                }
            }
        }).until(new ConditionEvaluationListenerTest.CountDownProvider(new ConditionEvaluationListenerTest.CountDownBean(10, 20)), samePropertyValuesAs(new ConditionEvaluationListenerTest.CountDownBean(10, 10)));
        String expectedMismatchMessage = String.format("%s expected same property values as CountDownBean [countDown: <10>, secondCountDown: <10>] but secondCountDown was <11>", ConditionEvaluationListenerTest.CountDownProvider.class.getName());
        MatcherAssert.assertThat(lastMismatchMessage.value, is(equalTo(expectedMismatchMessage)));
    }

    @Test(timeout = 2000)
    public void expectedMismatchMessage() {
        final ConditionEvaluationListenerTest.ValueHolder<String> lastMismatchMessage = new ConditionEvaluationListenerTest.ValueHolder<String>();
        Awaitility.with().conditionEvaluationListener(new org.awaitility.core.ConditionEvaluationListener<Integer>() {
            public void conditionEvaluated(EvaluatedCondition<Integer> condition) {
                if (!(condition.isSatisfied())) {
                    lastMismatchMessage.value = condition.getDescription();
                }
            }
        }).until(new ConditionEvaluationListenerTest.CountDown(10), is(equalTo(5)));
        String expectedMismatchMessage = String.format("%s expected <5> but was <6>", ConditionEvaluationListenerTest.CountDown.class.getName());
        MatcherAssert.assertThat(lastMismatchMessage.value, is(equalTo(expectedMismatchMessage)));
    }

    @Test(timeout = 2000)
    public void expectedMatchMessage() {
        final ConditionEvaluationListenerTest.ValueHolder<String> lastMatchMessage = new ConditionEvaluationListenerTest.ValueHolder<String>();
        Awaitility.with().conditionEvaluationListener(new org.awaitility.core.ConditionEvaluationListener<Integer>() {
            public void conditionEvaluated(EvaluatedCondition<Integer> condition) {
                lastMatchMessage.value = condition.getDescription();
            }
        }).until(new ConditionEvaluationListenerTest.CountDown(10), is(equalTo(5)));
        String expectedMatchMessage = String.format("%s reached its end value of <5>", ConditionEvaluationListenerTest.CountDown.class.getName());
        MatcherAssert.assertThat(lastMatchMessage.value, is(equalTo(expectedMatchMessage)));
    }

    @Test(timeout = 2000)
    public void awaitingForeverReturnsLongMaxValueAsRemainingTime() {
        final Set<Long> remainingTimes = new HashSet<Long>();
        final Set<Long> elapsedTimes = new HashSet<Long>();
        Awaitility.with().conditionEvaluationListener(new org.awaitility.core.ConditionEvaluationListener<Integer>() {
            public void conditionEvaluated(EvaluatedCondition<Integer> condition) {
                remainingTimes.add(condition.getRemainingTimeInMS());
                elapsedTimes.add(condition.getElapsedTimeInMS());
            }
        }).forever().until(new ConditionEvaluationListenerTest.CountDown(10), is(equalTo(5)));
        MatcherAssert.assertThat(remainingTimes, everyItem(is(Long.MAX_VALUE)));
        MatcherAssert.assertThat(elapsedTimes, everyItem(is(not(Long.MAX_VALUE))));
    }

    private static class CountDown implements Callable<Integer> {
        private int countDown;

        private CountDown(int countDown) {
            this.countDown = countDown;
        }

        public Integer call() throws Exception {
            return (countDown)--;
        }

        public Integer get() {
            return countDown;
        }
    }

    public static class CountDownBean {
        private int countDown;

        private int secondCountDown;

        private CountDownBean(int countDown, int secondCountDown) {
            this.countDown = countDown;
            this.secondCountDown = secondCountDown;
        }

        public int getCountDown() {
            return countDown;
        }

        public void setCountDown(int countDown) {
            this.countDown = countDown;
        }

        public int getSecondCountDown() {
            return secondCountDown;
        }

        public void setSecondCountDown(int secondCountDown) {
            this.secondCountDown = secondCountDown;
        }
    }

    private static class CountDownProvider implements Callable<ConditionEvaluationListenerTest.CountDownBean> {
        private final ConditionEvaluationListenerTest.CountDownBean countDown;

        private CountDownProvider(ConditionEvaluationListenerTest.CountDownBean countDown) {
            this.countDown = countDown;
        }

        public ConditionEvaluationListenerTest.CountDownBean call() throws Exception {
            countDown.setSecondCountDown(((countDown.getSecondCountDown()) - 1));
            return get();
        }

        public ConditionEvaluationListenerTest.CountDownBean get() {
            return countDown;
        }
    }

    private static class ValueHolder<T> {
        T value;
    }
}

