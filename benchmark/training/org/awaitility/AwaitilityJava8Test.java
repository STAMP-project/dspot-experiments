/**
 * Copyright 2016 the original author or authors.
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


import Duration.ONE_HUNDRED_MILLISECONDS;
import Duration.TWO_SECONDS;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.Assertions;
import org.awaitility.classes.FakeRepository;
import org.awaitility.classes.FakeRepositoryList;
import org.awaitility.core.ConditionEvaluationLogger;
import org.awaitility.core.ConditionTimeoutException;
import org.awaitility.core.ThrowingRunnable;
import org.awaitility.support.CountDown;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for await().until(Runnable) using AssertionCondition.
 *
 * @author Marcin Zaj?czkowski, 2014-03-28
 * @author Johan Haleby
 */
public class AwaitilityJava8Test {
    private FakeRepository fakeRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(timeout = 2000)
    public void awaitAssertJAssertionAsLambda() {
        perform();
        await().untilAsserted(() -> Assertions.assertThat(fakeRepository.getValue()).isEqualTo(1));
    }

    @Test(timeout = 2000)
    public void awaitUsingLambdaVersionOfCallableBoolean() {
        perform();
        await().until(() -> (fakeRepository.getValue()) == 1);
    }

    @SuppressWarnings("Convert2Lambda")
    @Test(timeout = 2000)
    public void awaitAssertJAssertionAsAnonymousClass() {
        perform();
        await().untilAsserted(new ThrowingRunnable() {
            @Override
            public void run() {
                Assertions.assertThat(fakeRepository.getValue()).isEqualTo(1);
            }
        });
    }

    @Test(timeout = 2000)
    public void awaitAssertJAssertionDisplaysOriginalErrorMessageAndTimeoutWhenConditionTimeoutExceptionOccurs() {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage(startsWith(("Assertion condition defined as a lambda expression in " + (AwaitilityJava8Test.class.getName()))));
        exception.expectMessage(endsWith("expected:<[1]> but was:<[0]> within 120 milliseconds."));
        perform();
        await().atMost(120, TimeUnit.MILLISECONDS).untilAsserted(() -> Assertions.assertThat(fakeRepository.getValue()).isEqualTo(1));
    }

    @Test(timeout = 2000)
    public void awaitJUnitAssertionAsLambda() {
        perform();
        await().untilAsserted(() -> assertEquals(1, fakeRepository.getValue()));
    }

    @Test(timeout = 2000)
    public void awaitJUnitAssertionDisplaysOriginalErrorMessageAndTimeoutWhenConditionTimeoutExceptionOccurs() {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage(startsWith(("Assertion condition defined as a lambda expression in " + (AwaitilityJava8Test.class.getName()))));
        exception.expectMessage(endsWith("expected:<1> but was:<0> within 120 milliseconds."));
        await().atMost(120, TimeUnit.MILLISECONDS).untilAsserted(() -> assertEquals(1, fakeRepository.getValue()));
    }

    /**
     * See <a href="https://github.com/awaitility/awaitility/issues/108">issue 108</a>
     */
    @Test(timeout = 2000)
    public void doesntRepeatAliasInLambdaConditionsForAssertConditions() {
        try {
            with().pollInterval(10, TimeUnit.MILLISECONDS).then().await("my alias").atMost(120, TimeUnit.MILLISECONDS).untilAsserted(() -> assertEquals(1, fakeRepository.getValue()));
            Assert.fail("Should throw ConditionTimeoutException");
        } catch (ConditionTimeoutException e) {
            Assert.assertThat(AwaitilityJava8Test.countOfOccurrences(e.getMessage(), "my alias")).isEqualTo(1);
        }
    }

    /**
     * See <a href="https://github.com/awaitility/awaitility/issues/108">issue 108</a>
     */
    @Test(timeout = 2000)
    public void doesntRepeatAliasInLambdaConditionsForCallableConditions() {
        try {
            with().pollInterval(10, TimeUnit.MILLISECONDS).then().await("my alias").atMost(120, TimeUnit.MILLISECONDS).until(() -> (fakeRepository.getValue()) == 1);
            Assert.fail("Should throw ConditionTimeoutException");
        } catch (ConditionTimeoutException e) {
            Assert.assertThat(AwaitilityJava8Test.countOfOccurrences(e.getMessage(), "my alias")).isEqualTo(1);
        }
    }

    @Test(timeout = 2000)
    public void lambdaErrorMessageLooksAlrightWhenUsingMethodReferences() {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage("Lambda expression in org.awaitility.AwaitilityJava8Test that uses org.awaitility.classes.FakeRepository: expected <1> but was <0> within 120 milliseconds.");
        await().atMost(120, TimeUnit.MILLISECONDS).until(fakeRepository::getValue, equalTo(1));
    }

    @SuppressWarnings("Convert2MethodRef")
    @Test(timeout = 2000)
    public void lambdaErrorMessageLooksAlrightWhenUsingLambda() {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage("Lambda expression in org.awaitility.AwaitilityJava8Test: expected <1> but was <0> within 120 milliseconds.");
        await().atMost(120, TimeUnit.MILLISECONDS).until(() -> fakeRepository.getValue(), equalTo(1));
    }

    @SuppressWarnings({ "Convert2MethodRef", "CodeBlock2Expr" })
    @Test(timeout = 2000)
    public void lambdaErrorMessageLooksAlrightWhenUsingLambdaWithCurlyBraces() {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage("Lambda expression in org.awaitility.AwaitilityJava8Test: expected <1> but was <0> within 120 milliseconds.");
        await().atMost(120, TimeUnit.MILLISECONDS).until(() -> {
            return fakeRepository.getValue();
        }, equalTo(1));
    }

    @Test(timeout = 2000)
    public void lambdaErrorMessageLooksAlrightWhenAwaitUsingLambdaVersionOfCallableBoolean() {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage("Condition with lambda expression in org.awaitility.AwaitilityJava8Test was not fulfilled within 200 milliseconds.");
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> (fakeRepository.getValue()) == 2);
    }

    @Test(timeout = 10000)
    public void conditionResultsCanBeLoggedToSystemOut() {
        with().conditionEvaluationListener(( condition) -> System.out.printf("%s (elapsed time %dms, remaining time %dms)\n", condition.getDescription(), condition.getElapsedTimeInMS(), condition.getRemainingTimeInMS())).pollInterval(ONE_HUNDRED_MILLISECONDS).atMost(TWO_SECONDS).until(new CountDown(5), anyOf(is(0), lessThan(0)));
    }

    @Test(timeout = 10000)
    public void loggingIntermediaryHandlerLogsToSystemOut() {
        with().conditionEvaluationListener(new ConditionEvaluationLogger(TimeUnit.SECONDS)).pollInterval(ONE_HUNDRED_MILLISECONDS).atMost(TWO_SECONDS).until(new CountDown(5), is(equalTo(0)));
    }

    @Test
    public void canMakeUseOfThrowingMethodInAwaitilityToWrapRunnablesThatThrowsExceptions() {
        await().untilAsserted(() -> stringEquals("test", "test"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test(timeout = 2000L)
    public void includesCauseInStackTrace() {
        try {
            await().atMost(200, TimeUnit.MILLISECONDS).untilAsserted(() -> {
                assertNotNull("34");
                assertNotNull(null);
            });
            Assert.fail("Should throw ConditionTimeoutException");
        } catch (ConditionTimeoutException e) {
            Assert.assertThat(e.getCause().getClass().getName()).isEqualTo(AssertionError.class.getName());
        }
    }

    // This was previously a bug (https://github.com/awaitility/awaitility/issues/78)
    @Test(timeout = 2000L)
    public void throwsExceptionImmediatelyWhenCallableConditionThrowsAssertionError() throws Exception {
        // Given
        long timeStart = System.nanoTime();
        perform();
        // When
        final AtomicInteger counter = new AtomicInteger(0);
        try {
            await().atMost(1500, TimeUnit.MILLISECONDS).until(() -> {
                counter.incrementAndGet();
                assertTrue(((counter.get()) >= 2));
                return true;
            });
            Assert.fail("Expecting error");
        } catch (AssertionError ignored) {
            // expected
        }
        // Then
        long timeEnd = System.nanoTime();
        Assert.assertThat(TimeUnit.NANOSECONDS.toMillis((timeEnd - timeStart))).isLessThan(1500L);
    }

    // Asserts that https://github.com/awaitility/awaitility/issues/87 is resolved
    @Test
    public void errorMessageLooksOkForHamcrestLambdaExpressionsWhoseMismatchDescriptionOriginallyIsEmptyStringByHamcrest() throws Exception {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage(endsWith("expected a collection containing a string ending with \"hello\" but was <[]> within 50 milliseconds."));
        // Given
        FakeRepositoryList fakeRepositoryList = new FakeRepositoryList();
        new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            fakeRepositoryList.add("hello");
        });
        // When
        await().atMost(50, TimeUnit.MILLISECONDS).until(fakeRepositoryList::state, hasItem(endsWith("hello")));
    }

    // Asserts that https://github.com/awaitility/awaitility/issues/97 is resolved
    @Test(timeout = 2000L)
    public void longConditionThrowsConditionTimeoutException() throws Exception {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage("Condition with org.awaitility.AwaitilityJava8Test was not fulfilled within 50 milliseconds.");
        await().atMost(50, TimeUnit.MILLISECONDS).until(() -> {
            Thread.sleep(1000);
            return false;
        });
    }
}

