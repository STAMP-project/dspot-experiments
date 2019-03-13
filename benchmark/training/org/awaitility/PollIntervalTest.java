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


import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.awaitility.classes.FakeRepository;
import org.awaitility.core.ConditionEvaluationLogger;
import org.awaitility.pollinterval.FibonacciPollInterval;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for await().until(Runnable) using AssertionCondition.
 *
 * @author Marcin Zaj?czkowski, 2014-03-28
 * @author Johan Haleby
 */
public class PollIntervalTest {
    private FakeRepository fakeRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(timeout = 2000)
    public void fibonacciPollInterval() {
        perform();
        Awaitility.await().with().conditionEvaluationListener(new ConditionEvaluationLogger()).pollInterval(new FibonacciPollInterval()).untilAsserted(() -> Assertions.assertThat(fakeRepository.getValue()).isEqualTo(1));
    }

    @Test(timeout = 2000)
    public void fibonacciPollIntervalStaticallyImported() {
        perform();
        Awaitility.await().with().conditionEvaluationListener(new ConditionEvaluationLogger()).pollInterval(fibonacci().with().offset(10).and().timeUnit(TimeUnit.MILLISECONDS)).untilAsserted(() -> Assertions.assertThat(fakeRepository.getValue()).isEqualTo(1));
    }

    @Test(timeout = 2000)
    public void inlinePollInterval() {
        perform();
        Awaitility.await().with().conditionEvaluationListener(new ConditionEvaluationLogger()).pollInterval(( __, previous) -> previous.multiply(2).plus(1)).untilAsserted(() -> Assertions.assertThat(fakeRepository.getValue()).isEqualTo(1));
    }

    @Test(timeout = 2000)
    public void iterativePollInterval() {
        perform();
        Awaitility.await().with().conditionEvaluationListener(new ConditionEvaluationLogger()).pollInterval(iterative(( duration) -> duration.multiply(2), Duration.FIVE_HUNDRED_MILLISECONDS)).untilAsserted(() -> Assertions.assertThat(fakeRepository.getValue()).isEqualTo(1));
    }

    @Test(timeout = 2000)
    public void fixedPollInterval() {
        perform();
        Awaitility.await().with().conditionEvaluationListener(new ConditionEvaluationLogger()).pollInterval(fixed(Duration.TWO_HUNDRED_MILLISECONDS)).untilAsserted(() -> Assertions.assertThat(fakeRepository.getValue()).isEqualTo(1));
    }
}

