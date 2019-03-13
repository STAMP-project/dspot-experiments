/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.test.context.junit4;


import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.annotation.Timed;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.util.ClassUtils;


/**
 * Verifies proper handling of the following in conjunction with the
 * {@link SpringRunner}:
 * <ul>
 * <li>Spring's {@link Repeat @Repeat}</li>
 * <li>Spring's {@link Timed @Timed}</li>
 * </ul>
 *
 * @author Sam Brannen
 * @since 3.0
 */
@RunWith(Parameterized.class)
public class RepeatedSpringRunnerTests {
    protected static final AtomicInteger invocationCount = new AtomicInteger();

    private final Class<?> testClass;

    private final int expectedFailureCount;

    private final int expectedStartedCount;

    private final int expectedFinishedCount;

    private final int expectedInvocationCount;

    public RepeatedSpringRunnerTests(String testClassName, int expectedFailureCount, int expectedTestStartedCount, int expectedTestFinishedCount, int expectedInvocationCount) throws Exception {
        this.testClass = ClassUtils.forName((((getClass().getName()) + ".") + testClassName), getClass().getClassLoader());
        this.expectedFailureCount = expectedFailureCount;
        this.expectedStartedCount = expectedTestStartedCount;
        this.expectedFinishedCount = expectedTestFinishedCount;
        this.expectedInvocationCount = expectedInvocationCount;
    }

    @Test
    public void assertRepetitions() throws Exception {
        RepeatedSpringRunnerTests.invocationCount.set(0);
        JUnitTestingUtils.runTestsAndAssertCounters(getRunnerClass(), this.testClass, expectedStartedCount, expectedFailureCount, expectedFinishedCount, 0, 0);
        Assert.assertEquals((("invocations for [" + (testClass)) + "]:"), expectedInvocationCount, RepeatedSpringRunnerTests.invocationCount.get());
    }

    @TestExecutionListeners({  })
    public abstract static class AbstractRepeatedTestCase {
        protected void incrementInvocationCount() throws IOException {
            RepeatedSpringRunnerTests.invocationCount.incrementAndGet();
        }
    }

    public static final class NonAnnotatedRepeatedTestCase extends RepeatedSpringRunnerTests.AbstractRepeatedTestCase {
        @Test
        @Timed(millis = 10000)
        public void nonAnnotated() throws Exception {
            incrementInvocationCount();
        }
    }

    public static final class DefaultRepeatValueRepeatedTestCase extends RepeatedSpringRunnerTests.AbstractRepeatedTestCase {
        @Test
        @Repeat
        @Timed(millis = 10000)
        public void defaultRepeatValue() throws Exception {
            incrementInvocationCount();
        }
    }

    public static final class NegativeRepeatValueRepeatedTestCase extends RepeatedSpringRunnerTests.AbstractRepeatedTestCase {
        @Test
        @Repeat(-5)
        @Timed(millis = 10000)
        public void negativeRepeatValue() throws Exception {
            incrementInvocationCount();
        }
    }

    public static final class RepeatedFiveTimesRepeatedTestCase extends RepeatedSpringRunnerTests.AbstractRepeatedTestCase {
        @Test
        @Repeat(5)
        public void repeatedFiveTimes() throws Exception {
            incrementInvocationCount();
        }
    }

    @Repeat(5)
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface RepeatedFiveTimes {}

    public static final class RepeatedFiveTimesViaMetaAnnotationRepeatedTestCase extends RepeatedSpringRunnerTests.AbstractRepeatedTestCase {
        @Test
        @RepeatedSpringRunnerTests.RepeatedFiveTimes
        public void repeatedFiveTimes() throws Exception {
            incrementInvocationCount();
        }
    }

    /**
     * Unit tests for claims raised in <a href="https://jira.spring.io/browse/SPR-6011" target="_blank">SPR-6011</a>.
     */
    @Ignore("TestCase classes are run manually by the enclosing test class")
    public static final class TimedRepeatedTestCase extends RepeatedSpringRunnerTests.AbstractRepeatedTestCase {
        @Test
        @Timed(millis = 1000)
        @Repeat(5)
        public void repeatedFiveTimesButDoesNotExceedTimeout() throws Exception {
            incrementInvocationCount();
        }

        @Test
        @Timed(millis = 10)
        @Repeat(1)
        public void singleRepetitionExceedsTimeout() throws Exception {
            incrementInvocationCount();
            Thread.sleep(15);
        }

        @Test
        @Timed(millis = 20)
        @Repeat(4)
        public void firstRepetitionOfManyExceedsTimeout() throws Exception {
            incrementInvocationCount();
            Thread.sleep(25);
        }

        @Test
        @Timed(millis = 100)
        @Repeat(10)
        public void collectiveRepetitionsExceedTimeout() throws Exception {
            incrementInvocationCount();
            Thread.sleep(11);
        }
    }
}

