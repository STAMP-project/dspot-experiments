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


import Duration.ONE_SECOND;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.awaitility.classes.Asynch;
import org.awaitility.classes.ExceptionThrowingFakeRepository;
import org.awaitility.classes.FakeRepository;
import org.awaitility.classes.FakeRepositoryEqualsOne;
import org.awaitility.classes.FakeRepositoryValue;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class AwaitilityTest {
    private FakeRepository fakeRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(timeout = 2000)
    public void awaitOperationBlocksAutomatically() {
        new Asynch(fakeRepository).perform();
        await().until(fakeRepositoryValueEqualsOne());
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000)
    public void awaitOperationSupportsSpecifyingPollIntervalUsingTimeunit() {
        new Asynch(fakeRepository).perform();
        await().until(fakeRepositoryValueEqualsOne());
        await().until(fakeRepositoryValueEqualsOne());
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000)
    public void awaitOperationSupportsSpecifyingPollInterval() {
        new Asynch(fakeRepository).perform();
        await().until(fakeRepositoryValueEqualsOne());
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000)
    public void awaitOperationSupportsSpecifyingZeroAsPollDelay() {
        new Asynch(fakeRepository).perform();
        await().until(fakeRepositoryValueEqualsOne());
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000)
    public void awaitOperationSupportsSpecifyingZeroAsPollInterval() {
        new Asynch(fakeRepository).perform();
        await().until(fakeRepositoryValueEqualsOne());
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000)
    public void awaitOperationDoesntSupportSpecifyingForeverAsPollDelay() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Cannot delay polling forever");
        new Asynch(fakeRepository).perform();
        await().until(fakeRepositoryValueEqualsOne());
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000)
    public void awaitOperationDoesntSupportSpecifyingForeverAsPollInterval() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Cannot use a fixed poll interval of length 'forever'");
        new Asynch(fakeRepository).perform();
        await().until(fakeRepositoryValueEqualsOne());
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000)
    public void awaitOperationSupportsSpecifyingPollDelay() {
        new Asynch(fakeRepository).perform();
        await().until(fakeRepositoryValueEqualsOne());
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000L, expected = ConditionTimeoutException.class)
    public void awaitOperationSupportsDefaultTimeout() {
        Awaitility.Awaitility.setDefaultTimeout(120, TimeUnit.MILLISECONDS);
        await().until(value(), greaterThan(0));
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000)
    public void foreverConditionSpecificationUsingUntilWithDirectBlock() {
        new Asynch(fakeRepository).perform();
        await().forever().until(fakeRepositoryValueEqualsOne());
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000)
    public void foreverConditionWithHamcrestMatchersWithDirectBlock() {
        new Asynch(fakeRepository).perform();
        await().forever().until(value(), equalTo(1));
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000)
    public void foreverConditionWithHamcrestCollectionMatchersWithDirectBlock() {
        new Asynch(fakeRepository).perform();
        await().forever().until(valueAsList(), hasItem(1));
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 3000, expected = ConditionTimeoutException.class)
    public void throwsTimeoutExceptionWhenDoneEarlierThanAtLeastConstraint() {
        new Asynch(fakeRepository).perform();
        await().atLeast(1, TimeUnit.SECONDS).and().atMost(2, TimeUnit.SECONDS).until(value(), equalTo(1));
    }

    @Test(timeout = 3000)
    public void doesNotThrowTimeoutExceptionWhenDoneLaterThanAtLeastConstraint() {
        new Asynch(fakeRepository).perform();
        await().atLeast(100, TimeUnit.NANOSECONDS).until(value(), equalTo(1));
    }

    @Test(timeout = 2000)
    public void specifyingDefaultPollIntervalImpactsAllSubsequentUndefinedPollIntervalStatements() {
        Awaitility.Awaitility.setDefaultPollInterval(20, TimeUnit.MILLISECONDS);
        new Asynch(fakeRepository).perform();
        await().until(value(), equalTo(1));
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000, expected = ConditionTimeoutException.class)
    public void conditionBreaksAfterDurationTimeout() {
        new Asynch(fakeRepository).perform();
        await().atMost(200, TimeUnit.MILLISECONDS).until(value(), equalTo(1));
        Assert.assertEquals(1, fakeRepository.getValue());
    }

    @Test(timeout = 2000, expected = IllegalStateException.class)
    public void uncaughtExceptionsArePropagatedToAwaitingThreadAndBreaksForeverBlockWhenSetToCatchAllUncaughtExceptions() {
        catchUncaughtExceptionsByDefault();
        new ExceptionThrowingAsynch(new IllegalStateException("Illegal state!")).perform();
        await().forever().until(value(), equalTo(1));
    }

    @Test(timeout = 2000, expected = ComparisonFailure.class)
    public void uncaughtThrowablesArePropagatedToAwaitingThreadAndBreaksForeverBlockWhenSetToCatchAllUncaughtExceptions() {
        new ExceptionThrowingAsynch(new ComparisonFailure("Message", "Something", "Something else")).perform();
        await().forever().until(value(), equalTo(1));
    }

    @Test(timeout = 2000)
    public void uncaughtThrowablesFromOtherThreadsCanBeIgnored() throws Exception {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final AtomicBoolean exceptionThrown = new AtomicBoolean(false);
        final ExecutorService es = Executors.newFixedThreadPool(5);
        final FakeRepository fakeRepository = new FakeRepository() {
            @Override
            public int getValue() {
                int value = atomicInteger.get();
                if (value < 3) {
                    exceptionThrown.set(true);
                    throw new IllegalArgumentException("Error!");
                }
                return value;
            }

            @Override
            public void setValue(int value) {
                atomicInteger.set(value);
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while ((atomicInteger.get()) < 3) {
                    try {
                        es.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(50);
                                    fakeRepository.setValue(atomicInteger.incrementAndGet());
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }).get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } 
            }
        });
        thread.start();
        try {
            await().until(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return (fakeRepository.getValue()) == 3;
                }
            });
            Assert.assertEquals(3, atomicInteger.get());
            Assert.assertTrue(exceptionThrown.get());
        } finally {
            thread.join();
            es.shutdownNow();
        }
    }

    @Test(timeout = 2000)
    public void ignoredExceptionsAreAddedToExceptionHierarchy() {
        try {
            await().ignoreExceptions().atMost(200, TimeUnit.MILLISECONDS).until(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    throw new Exception("Nested");
                }
            });
            Assert.fail();
        } catch (ConditionTimeoutException e) {
            Assert.assertNotNull(e.getCause());
            Assert.assertEquals("Nested", e.getCause().getMessage());
        }
    }

    @Test(timeout = 2000, expected = IllegalStateException.class)
    public void uncaughtExceptionsArePropagatedToAwaitingThreadAndBreaksForeverBlockWhenCatchingAllUncaughtExceptions() {
        new ExceptionThrowingAsynch(new IllegalStateException("Illegal state!")).perform();
        await().forever().until(value(), equalTo(1));
    }

    @Test(timeout = 2000, expected = ConditionTimeoutException.class)
    public void whenDontCatchUncaughtExceptionsIsSpecifiedThenExceptionsFromOtherThreadsAreNotCaught() throws Exception {
        new AssertExceptionThrownInAnotherThreadButNeverCaughtByAnyThreadTest() {
            @Override
            public void testLogic() {
                new ExceptionThrowingAsynch(new IllegalStateException("Illegal state!")).perform();
                await().atMost(Duration.ONE_SECOND).until(value(), equalTo(1));
            }
        };
    }

    @Test(timeout = 2000, expected = ConditionTimeoutException.class)
    public void whenDontCatchUncaughtExceptionsIsSpecifiedAndTheBuildOfTheAwaitStatementHasStartedThenExceptionsFromOtherThreadsAreNotCaught() throws Exception {
        new AssertExceptionThrownInAnotherThreadButNeverCaughtByAnyThreadTest() {
            @Override
            public void testLogic() {
                new ExceptionThrowingAsynch(new IllegalStateException("Illegal state!")).perform();
                await().and().dontCatchUncaughtExceptions().given().timeout(Duration.ONE_SECOND).until(value(), equalTo(1));
            }
        };
    }

    @Test(timeout = 2000, expected = ConditionTimeoutException.class)
    public void catchUncaughtExceptionsIsReset() throws Exception {
        new AssertExceptionThrownInAnotherThreadButNeverCaughtByAnyThreadTest() {
            @Override
            public void testLogic() {
                new ExceptionThrowingAsynch(new IllegalStateException("Illegal state!")).perform();
                await().atMost(ONE_SECOND).until(value(), equalTo(1));
            }
        };
    }

    @Test(timeout = 2000, expected = IllegalStateException.class)
    public void exceptionsInConditionsArePropagatedToAwaitingThreadAndBreaksForeverBlock() {
        final ExceptionThrowingFakeRepository repository = new ExceptionThrowingFakeRepository();
        new Asynch(repository).perform();
        await().until(new FakeRepositoryValue(repository), equalTo(1));
    }

    @Test(timeout = 2000)
    public void awaitWithAliasDisplaysAliasWhenConditionTimeoutExceptionOccurs() {
        String alias = "test";
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage("Condition with alias 'test' didn't complete within 120 milliseconds because org.awaitility.classes.FakeRepositoryValue expected a value greater than <0> but <0> was equal to <0>.");
        await(alias).atMost(120, TimeUnit.MILLISECONDS).until(value(), greaterThan(0));
    }

    @Test(timeout = 2000)
    public void awaitWithAliasDisplaysAliasWhenConditionTimeoutExceptionAndConditionIsACallableOccurs() {
        String alias = "test";
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage("Condition with alias \'test\' didn\'t complete within 120 milliseconds because condition returned by method \"awaitWithAliasDisplaysAliasWhenConditionTimeoutExceptionAndConditionIsACallableOccurs\" in class org.awaitility.AwaitilityTest was not fulfilled.");
        await(alias).atMost(120, TimeUnit.MILLISECONDS).until(new Callable<Boolean>() {
            public Boolean call() {
                return (fakeRepository.getValue()) > 0;
            }
        });
    }

    @Test(timeout = 2000)
    public void awaitDisplaysSupplierAndMatcherMismatchMessageWhenConditionTimeoutExceptionOccurs() {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage(((FakeRepositoryValue.class.getName()) + " expected a value greater than <0> but <0> was equal to <0> within 120 milliseconds."));
        await().atMost(120, TimeUnit.MILLISECONDS).until(value(), greaterThan(0));
    }

    @Test(timeout = 2000)
    public void awaitDisplaysCallableNameWhenConditionTimeoutExceptionOccurs() {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage(String.format("Condition %s was not fulfilled within 120 milliseconds.", FakeRepositoryEqualsOne.class.getName()));
        await().atMost(120, TimeUnit.MILLISECONDS).until(fakeRepositoryValueEqualsOne());
    }

    @Test(timeout = 2000)
    public void awaitDisplaysMethodDeclaringTheCallableWhenCallableIsAnonymousClassAndConditionTimeoutExceptionOccurs() {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage(String.format("Condition returned by method \"fakeRepositoryValueEqualsOneAsAnonymous\" in class %s was not fulfilled within 120 milliseconds.", AwaitilityTest.class.getName()));
        await().atMost(120, TimeUnit.MILLISECONDS).until(fakeRepositoryValueEqualsOneAsAnonymous());
    }

    @Test(timeout = 2000)
    public void awaitDisplaysMethodDeclaringTheSupplierWhenSupplierIsAnonymousClassAndConditionTimeoutExceptionOccurs() {
        exception.expect(ConditionTimeoutException.class);
        exception.expectMessage(String.format("%s.valueAsAnonymous Callable expected %s but was <0> within 120 milliseconds.", AwaitilityTest.class.getName(), equalTo(2).toString()));
        await().atMost(120, TimeUnit.MILLISECONDS).until(valueAsAnonymous(), equalTo(2));
    }

    @Test
    public void awaitilityThrowsIllegalStateExceptionWhenTimeoutIsLessThanPollDelay() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage(is("Timeout (10 seconds) must be greater than the poll delay (10 minutes)."));
        await().atMost(10, TimeUnit.SECONDS).until(fakeRepositoryValueEqualsOne());
    }

    @Test
    public void awaitilityThrowsIllegalStateExceptionWhenTimeoutIsEqualToPollDelay() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage(is("Timeout (200 milliseconds) must be greater than the poll delay (200 milliseconds)."));
        await().atMost(200, TimeUnit.MILLISECONDS).until(fakeRepositoryValueEqualsOne());
    }

    @Test(timeout = 2000L, expected = IllegalStateException.class)
    public void rethrowsExceptionsInCallable() {
        await().atMost(1, TimeUnit.SECONDS).until(new Callable<Boolean>() {
            public Boolean call() {
                throw new IllegalStateException("Hello");
            }
        });
    }
}

