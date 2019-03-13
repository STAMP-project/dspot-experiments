/**
 * Copyright 2017 the original author or authors.
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
package org.awaitility.proxy;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.awaitility.Duration;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class AwaitilityProxyTest {
    private FakeRepository fakeRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(timeout = 2000)
    public void awaitUsingCallTo() throws Exception {
        perform();
        await().untilCall(AwaitilityClassProxy.to(fakeRepository).getValue(), greaterThan(0));
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000)
    public void givenInstancePassedToCallToIsAFinalClassThenInterfaceProxyingIsUsed() throws Exception {
        fakeRepository = new FinalFakeRepositoryImpl();
        perform();
        await().untilCall(AwaitilityClassProxy.to(fakeRepository).getValue(), greaterThan(0));
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(expected = CannotCreateProxyException.class)
    public void givenInstancePassedToCallToIsAFinalClassWithNoInterfacesThenExceptionIsThrown() throws Exception {
        AwaitilityClassProxy.to(new FinalClass());
    }

    @Test(timeout = 2000, expected = ConditionTimeoutException.class)
    public void waitAtMostWorks() throws Exception {
        new AssertExceptionThrownInAnotherThreadButNeverCaughtByAnyThreadTest() {
            @Override
            public void testLogic() {
                new ExceptionThrowingAsynch(new IllegalStateException("Illegal state!")).perform();
                await().atMost(Duration.ONE_SECOND).untilCall(AwaitilityClassProxy.to(fakeRepository).getValue(), equalTo(1));
                waitAtMost(Duration.ONE_SECOND).and().dontCatchUncaughtExceptions().untilCall(AwaitilityClassProxy.to(fakeRepository).getValue(), equalTo(1));
                await().atMost(Duration.ONE_SECOND).untilCall(AwaitilityClassProxy.to(fakeRepository).getValue(), equalTo(1));
                await().untilCall(AwaitilityClassProxy.to(fakeRepository).getValue(), equalTo(1));
            }
        };
    }

    @Test(timeout = 4000)
    public void awaitWithTimeout() throws Exception {
        perform();
        await().untilCall(AwaitilityClassProxy.to(fakeRepository).getValue(), greaterThan(0));
    }

    @Test(timeout = 2000)
    public void awaitWithAliasDisplaysAliasWhenConditionTimeoutExceptionAndConditionIsCallTo() throws Exception {
        String alias = "test";
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage("Condition with alias 'test' didn't complete within 120 milliseconds because org.awaitility.classes.FakeRepositoryImpl.getValue() expected a value greater than <0> but <0> was");
        await(alias).atMost(120, TimeUnit.MILLISECONDS).untilCall(AwaitilityClassProxy.to(fakeRepository).getValue(), greaterThan(0));
    }

    @Test(timeout = 2000)
    public void awaitDisplaysMethodInvocationNameAndMatcherNameWhenUsingCallToAndConditionTimeoutExceptionOccurs() throws Exception {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage(((FakeRepositoryImpl.class.getName()) + ".getValue() expected a value greater than <0> but <0> was equal to <0> within 50 milliseconds."));
        perform();
        await().untilCall(AwaitilityClassProxy.to(fakeRepository).getValue(), greaterThan(0));
    }

    @Test(timeout = 2000)
    public void awaitDisplaysLastPollResultOnTimeout() throws Exception {
        FakeObjectRepository fakeObjectRepository = new FakeObjectRepository();
        Object actualObject = fakeObjectRepository.getObject();
        Object expectedObject = new Object();
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage(String.format("%s.getObject() expected <%s> but was <%s> within 50 milliseconds.", FakeObjectRepository.class.getName(), expectedObject.toString(), actualObject.toString()));
        await().untilCall(AwaitilityClassProxy.to(fakeObjectRepository).getObject(), is(expectedObject));
    }

    @Test(timeout = 2000)
    public void throwsConditionConditionTimeoutExceptionOnTimeout() throws Exception {
        perform();
        try {
            await().atMost(500, TimeUnit.MILLISECONDS).untilCall(AwaitilityClassProxy.to(fakeRepository).getValue(), greaterThan(2));
            Assert.fail("Should throw timeout exception");
        } catch (ConditionTimeoutException e) {
            Assert.assertEquals(0, fakeRepository.getValue());
        }
    }

    @Test(timeout = 5000)
    public void awaitUsingCallToMultipleThreads_githubIssue28() throws Exception {
        final AtomicInteger errorCount = new AtomicInteger(0);
        int threadsCount = 16;
        final CountDownLatch allThreadsDone = new CountDownLatch(threadsCount);
        for (int i = 0; i < threadsCount; i++) {
            new Thread() {
                public void run() {
                    try {
                        await().untilCall(AwaitilityClassProxy.to(fakeRepository).getValue(), equalTo(0));
                    } catch (IllegalStateException ex) {
                        if (ex.getMessage().contains("No method call has been recorded. Perhaps the method was final?")) {
                            errorCount.incrementAndGet();
                        }
                    } finally {
                        allThreadsDone.countDown();
                    }
                }
            }.start();
        }
        allThreadsDone.await();
        Assert.assertTrue((("Racy method recording got mixed up: " + (errorCount.get())) + " errors"), ((errorCount.get()) == 0));
    }

    @Test(timeout = 2000)
    public void returnsResultAfterProxyCall() throws Exception {
        perform();
        int value = await().untilCall(AwaitilityClassProxy.to(fakeRepository).getValue(), greaterThan(0));
        Assert.assertEquals(1, value);
    }
}

