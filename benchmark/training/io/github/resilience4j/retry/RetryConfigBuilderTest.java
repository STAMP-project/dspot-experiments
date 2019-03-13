/**
 * Copyright 2016 Robert Winkler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.github.resilience4j.retry;


import RetryConfig.Builder;
import java.time.Duration;
import java.util.function.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class RetryConfigBuilderTest {
    private static final Predicate<Throwable> TEST_PREDICATE = ( e) -> "test".equals(e.getMessage());

    @Test(expected = IllegalArgumentException.class)
    public void zeroMaxAttemptsShouldFail() {
        RetryConfig.custom().maxAttempts(0).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroWaitIntervalShouldFail() {
        RetryConfig.custom().waitDuration(Duration.ofMillis(0)).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void waitIntervalUnderTenMillisShouldFail() {
        RetryConfig.custom().waitDuration(Duration.ofMillis(5)).build();
    }

    @Test
    public void waitIntervalOfTenMillisShouldSucceed() {
        RetryConfig config = RetryConfig.custom().waitDuration(Duration.ofMillis(10)).build();
        Assertions.assertThat(config).isNotNull();
    }

    @Test
    public void waitIntervalOverTenMillisShouldSucceed() {
        RetryConfig config = RetryConfig.custom().waitDuration(Duration.ofSeconds(10)).build();
        Assertions.assertThat(config).isNotNull();
    }

    private static class ExtendsException extends Exception {
        ExtendsException() {
        }

        ExtendsException(String message) {
            super(message);
        }
    }

    private static class ExtendsRuntimeException extends RuntimeException {}

    private static class ExtendsExtendsException extends RetryConfigBuilderTest.ExtendsException {}

    private static class ExtendsException2 extends Exception {}

    private static class ExtendsError extends Error {}

    @Test
    public void shouldUseIgnoreExceptionToBuildPredicate() {
        RetryConfig retryConfig = RetryConfig.custom().ignoreExceptions(RuntimeException.class, RetryConfigBuilderTest.ExtendsExtendsException.class).build();
        final Predicate<? super Throwable> failurePredicate = retryConfig.getExceptionPredicate();
        then(failurePredicate.test(new Exception())).isEqualTo(true);// not explicitly excluded

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsError())).isEqualTo(true);// not explicitly excluded

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsException())).isEqualTo(true);// not explicitly excluded

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsException2())).isEqualTo(true);// not explicitly excluded

        then(failurePredicate.test(new RuntimeException())).isEqualTo(false);// explicitly excluded

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsRuntimeException())).isEqualTo(false);// inherits excluded from ExtendsException

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsExtendsException())).isEqualTo(false);// explicitly excluded

    }

    @Test
    public void shouldUseRecordExceptionToBuildPredicate() {
        RetryConfig retryConfig = RetryConfig.custom().retryExceptions(RuntimeException.class, RetryConfigBuilderTest.ExtendsExtendsException.class).build();
        final Predicate<? super Throwable> failurePredicate = retryConfig.getExceptionPredicate();
        then(failurePredicate.test(new Exception())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsError())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsException())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsException2())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new RuntimeException())).isEqualTo(true);// explicitly included

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsRuntimeException())).isEqualTo(true);// inherits included from ExtendsException

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsExtendsException())).isEqualTo(true);// explicitly included

    }

    @Test
    public void shouldUseIgnoreExceptionOverRecordToBuildPredicate() {
        RetryConfig retryConfig = RetryConfig.custom().retryExceptions(RuntimeException.class, RetryConfigBuilderTest.ExtendsExtendsException.class).ignoreExceptions(RetryConfigBuilderTest.ExtendsException.class, RetryConfigBuilderTest.ExtendsRuntimeException.class).build();
        final Predicate<? super Throwable> failurePredicate = retryConfig.getExceptionPredicate();
        then(failurePredicate.test(new Exception())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsError())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsException())).isEqualTo(false);// explicitly excluded

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsException2())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new RuntimeException())).isEqualTo(true);// explicitly included

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsRuntimeException())).isEqualTo(false);// explicitly excluded

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsExtendsException())).isEqualTo(false);// inherits excluded from ExtendsException

    }

    @Test
    public void shouldUseBothRecordToBuildPredicate() {
        RetryConfig retryConfig = // 3
        // 2
        // 1
        RetryConfig.custom().retryOnException(RetryConfigBuilderTest.TEST_PREDICATE).retryExceptions(RuntimeException.class, RetryConfigBuilderTest.ExtendsExtendsException.class).ignoreExceptions(RetryConfigBuilderTest.ExtendsException.class, RetryConfigBuilderTest.ExtendsRuntimeException.class).build();
        final Predicate<? super Throwable> failurePredicate = retryConfig.getExceptionPredicate();
        then(failurePredicate.test(new Exception())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new Exception("test"))).isEqualTo(true);// explicitly included by 1

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsError())).isEqualTo(false);// ot explicitly included

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsException())).isEqualTo(false);// explicitly excluded by 3

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsException("test"))).isEqualTo(false);// explicitly excluded by 3 even if included by 1

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsException2())).isEqualTo(false);// not explicitly included

        then(failurePredicate.test(new RuntimeException())).isEqualTo(true);// explicitly included by 2

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsRuntimeException())).isEqualTo(false);// explicitly excluded by 3

        then(failurePredicate.test(new RetryConfigBuilderTest.ExtendsExtendsException())).isEqualTo(false);// inherits excluded from ExtendsException by 3

    }

    @Test
    public void builderMakePredicateShouldBuildPredicateAcceptingChildClass() {
        final Predicate<Throwable> predicate = Builder.makePredicate(RuntimeException.class);
        then(predicate.test(new RuntimeException())).isEqualTo(true);
        then(predicate.test(new Exception())).isEqualTo(false);
        then(predicate.test(new Throwable())).isEqualTo(false);
        then(predicate.test(new IllegalArgumentException())).isEqualTo(true);
        then(predicate.test(new RuntimeException() {})).isEqualTo(true);
        then(predicate.test(new Exception() {})).isEqualTo(false);
    }

    @Test
    public void shouldBuilderCreateConfigEveryTime() {
        final RetryConfig.Builder builder = RetryConfig.custom();
        builder.maxAttempts(5);
        final RetryConfig config1 = builder.build();
        builder.maxAttempts(3);
        final RetryConfig config2 = builder.build();
        assertThat(config2.getMaxAttempts()).isEqualTo(3);
        assertThat(config1.getMaxAttempts()).isEqualTo(5);
    }
}

