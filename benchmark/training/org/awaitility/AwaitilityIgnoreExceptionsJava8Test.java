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


import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.awaitility.classes.FakeRepository;
import org.awaitility.core.CheckedExceptionRethrower;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class AwaitilityIgnoreExceptionsJava8Test {
    private FakeRepository fakeRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(timeout = 2000)
    public void exceptionIgnoringWorksWithPredicates() {
        perform();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).and().ignoreExceptionsMatching(( e) -> e.getMessage().endsWith("is not 1")).until(() -> {
            if ((fakeRepository.getValue()) != 1) {
                throw new IllegalArgumentException("Repository value is not 1");
            }
            return true;
        });
    }

    @Test(timeout = 2000)
    public void exceptionIgnoringWorksWithPredicatesStatically() {
        perform();
        Awaitility.ignoreExceptionsByDefaultMatching(( e) -> e instanceof RuntimeException);
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> {
            if ((fakeRepository.getValue()) != 1) {
                throw new IllegalArgumentException("Repository value is not 1");
            }
            return true;
        });
    }

    @Test(timeout = 2000L)
    public void untilAssertedCanIgnoreThrowable() throws Exception {
        perform();
        AtomicInteger counter = new AtomicInteger(0);
        Awaitility.given().ignoreExceptionsMatching(Objects::nonNull).await().atMost(1000, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            if ((counter.incrementAndGet()) < 3) {
                CheckedExceptionRethrower.safeRethrow(new Throwable("Test"));
            }
            assertThat(fakeRepository.getValue()).isEqualTo(1);
        });
    }

    /* Note that this might actually be a bug/limitation. The reason is that when the exception is
    caught and the condition reevaluated and thus end up in an infinite loop that will be
    broken by the timeout. What ought to happen is that Awaitility somehow remembers that
    the condition was fulfilled even though an exception is thrown later in the condition?
    Or perhaps this behavior is correct?
     */
    @Test(timeout = 2000L, expected = ConditionTimeoutException.class)
    public void cannotHandleExceptionsThrownAfterAStatementIsFulfilled() throws Exception {
        perform();
        Awaitility.given().ignoreExceptionsMatching(Objects::nonNull).await().atMost(800, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            assertThat(fakeRepository.getValue()).isEqualTo(1);
            CheckedExceptionRethrower.safeRethrow(new Throwable("Test"));
        });
    }
}

