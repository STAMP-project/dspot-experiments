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
import org.awaitility.classes.Asynch;
import org.awaitility.classes.FakeRepository;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class AwaitilityIgnoreExceptionsTest {
    private FakeRepository fakeRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(timeout = 2000)
    public void exceptionsDuringEvaluationAreIgnoredUponRequest() {
        new Asynch(fakeRepository).perform();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).and().ignoreExceptions().until(conditionsThatIsThrowingAnExceptionForATime(IllegalArgumentException.class));
    }

    @Test(timeout = 2000)
    public void throwablesDuringEvaluationAreIgnoredUponRequest() {
        new Asynch(fakeRepository).perform();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).and().ignoreExceptions().until(conditionsThatIsThrowingAnExceptionForATime(AssertionError.class));
    }

    @Test(timeout = 2000)
    public void exceptionsOnlySpecifiedExceptionsAreIgnored() {
        new Asynch(fakeRepository).perform();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).and().ignoreException(IllegalArgumentException.class).until(conditionsThatIsThrowingAnExceptionForATime(IllegalArgumentException.class));
    }

    @Test(timeout = 2000)
    public void ignoreExceptionWorksWithThrowable() {
        new Asynch(fakeRepository).perform();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).and().ignoreException(AssertionError.class).until(conditionsThatIsThrowingAnExceptionForATime(AssertionError.class));
    }

    @Test(timeout = 2000)
    public void exceptionsOnlySpecifiedExceptionsAreIgnoredWhenUsingShortcut() {
        new Asynch(fakeRepository).perform();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).and().ignoreExceptionsInstanceOf(RuntimeException.class).until(conditionsThatIsThrowingAnExceptionForATime(IllegalArgumentException.class));
    }

    @Test(timeout = 2000)
    public void exceptionsDuringEvaluationAreIgnoredWhenSetAsDefault() {
        new Asynch(fakeRepository).perform();
        Awaitility.ignoreExceptionsByDefault();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).until(conditionsThatIsThrowingAnExceptionForATime(IllegalArgumentException.class));
    }

    @Test(timeout = 2000)
    public void throwablesDuringEvaluationAreIgnoredWhenSetAsDefault() {
        new Asynch(fakeRepository).perform();
        Awaitility.ignoreExceptionsByDefault();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).until(conditionsThatIsThrowingAnExceptionForATime(AssertionError.class));
    }

    @Test(timeout = 2000)
    public void exceptionIgnoringWorksForHamcrestMatchers() {
        new Asynch(fakeRepository).perform();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).with().ignoreExceptionsMatching(Matchers.instanceOf(RuntimeException.class)).until(conditionsThatIsThrowingAnExceptionForATime(IllegalArgumentException.class));
    }

    @Test(timeout = 2000)
    public void assertionErrorIgnoringWorksForHamcrestMatchers() {
        new Asynch(fakeRepository).perform();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).with().ignoreExceptionsMatching(Matchers.instanceOf(AssertionError.class)).until(conditionsThatIsThrowingAnExceptionForATime(AssertionError.class));
    }

    @Test(timeout = 2000)
    public void exceptionIgnoringWorksForHamcrestMatchersStatically() {
        new Asynch(fakeRepository).perform();
        Awaitility.ignoreExceptionsByDefaultMatching(Matchers.instanceOf(RuntimeException.class));
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).until(conditionsThatIsThrowingAnExceptionForATime(IllegalArgumentException.class));
    }

    @Test(timeout = 2000)
    public void noIgnoredExceptionsHavePrecedenceOverStaticallyDefinedExceptionIgnorer() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Repository value is not 1");
        new Asynch(fakeRepository).perform();
        Awaitility.ignoreExceptionsByDefaultMatching(Matchers.instanceOf(RuntimeException.class));
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).with().ignoreNoExceptions().until(conditionsThatIsThrowingAnExceptionForATime(IllegalArgumentException.class));
    }

    @Test(timeout = 2000)
    public void exceptionIgnoringWorksForPredicates() {
        new Asynch(fakeRepository).perform();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).with().ignoreExceptionsMatching(new org.awaitility.core.Predicate<Throwable>() {
            public boolean matches(Throwable e) {
                return e instanceof RuntimeException;
            }
        }).until(conditionsThatIsThrowingAnExceptionForATime(IllegalArgumentException.class));
    }

    @Test(timeout = 2000)
    public void exceptionIgnoringWorksForPredicatesStatically() {
        new Asynch(fakeRepository).perform();
        Awaitility.ignoreExceptionsByDefaultMatching(new org.awaitility.core.Predicate<Throwable>() {
            public boolean matches(Throwable e) {
                return e instanceof RuntimeException;
            }
        });
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).until(conditionsThatIsThrowingAnExceptionForATime(IllegalArgumentException.class));
    }

    @Test(timeout = 2000)
    public void exceptionsDuringEvaluationAreReportedByDefault() {
        exception.expect(RuntimeException.class);
        exception.expectMessage(Matchers.is("Repository value is not 1"));
        new Asynch(fakeRepository).perform();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).with().until(conditionsThatIsThrowingAnExceptionForATime(RuntimeException.class));
    }
}

