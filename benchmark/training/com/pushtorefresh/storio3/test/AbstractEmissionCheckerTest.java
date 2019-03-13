package com.pushtorefresh.storio3.test;


import BackpressureStrategy.MISSING;
import android.support.annotation.NonNull;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;


public class AbstractEmissionCheckerTest {
    @SuppressWarnings("unchecked")
    @Test
    public void verifySubscribeBehavior() {
        final AtomicBoolean onSubscribeWasCalled = new AtomicBoolean(false);
        final Flowable<String> flowable = Flowable.create(new io.reactivex.FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull
            FlowableEmitter<String> emitter) throws Exception {
                onSubscribeWasCalled.set(true);
                emitter.onNext("test_value");
                emitter.onComplete();
            }
        }, MISSING);
        AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(new LinkedList<String>()) {
            @NonNull
            @Override
            public Disposable subscribe() {
                return flowable.subscribe();
            }
        };
        // Should not subscribe before manual call to subscribe
        assertThat(onSubscribeWasCalled.get()).isFalse();
        Disposable disposable = emissionChecker.subscribe();
        // Should subscribe to flowable
        assertThat(onSubscribeWasCalled.get()).isTrue();
        disposable.dispose();
    }

    @Test
    public void shouldAssertThatNextExpectedValueReceived() {
        Queue<String> expectedValues = new LinkedList<String>();
        expectedValues.add("test_value");
        AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @NonNull
            @Override
            public Disposable subscribe() {
                return Flowable.just("test_value").subscribe(new io.reactivex.functions.Consumer<String>() {
                    @Override
                    public void accept(@NonNull
                    String s) throws Exception {
                        onNextObtained(s);
                    }
                });
            }
        };
        Disposable disposable = emissionChecker.subscribe();
        // Should not throw exception
        emissionChecker.awaitNextExpectedValue();
        disposable.dispose();
    }

    @Test
    public void shouldNotAssertThatNextExpectedValueReceivedInCaseOfAnotherValue() {
        Queue<String> expectedValues = new LinkedList<String>();
        expectedValues.add("expected_value");
        AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @NonNull
            @Override
            public Disposable subscribe() {
                return Flowable.just("another_value").subscribeOn(io.reactivex.schedulers.Schedulers.computation()).subscribe(new io.reactivex.functions.Consumer<String>() {
                    @Override
                    public void accept(@NonNull
                    String s) throws Exception {
                        onNextObtained(s);
                    }
                });
            }
        };
        Disposable disposable = emissionChecker.subscribe();
        try {
            emissionChecker.awaitNextExpectedValue();
            failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (AssertionError expected) {
            // it's okay
        } finally {
            disposable.dispose();
        }
    }

    @Test
    public void shouldNotAssertThatNextExpectedValueReceivedBecauseOfTimeout() {
        Queue<String> expectedValues = new LinkedList<String>();
        expectedValues.add("expected_value");
        AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @Override
            protected long timeoutMillis() {
                return 1000;
            }

            @NonNull
            @Override
            public Disposable subscribe() {
                return // ha!
                Flowable.just("expected_value").delay(2, SECONDS).subscribe(new io.reactivex.functions.Consumer<String>() {
                    @Override
                    public void accept(@NonNull
                    String s) throws Exception {
                        onNextObtained(s);
                    }
                });
            }
        };
        Disposable disposable = emissionChecker.subscribe();
        try {
            emissionChecker.awaitNextExpectedValue();
            failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (AssertionError expected) {
            // it's okay
        } finally {
            disposable.dispose();
        }
    }

    @Test
    public void shouldAssertThatNoExpectedValuesLeft() {
        Queue<String> expectedValues = new LinkedList<String>();
        expectedValues.add("1");
        expectedValues.add("2");
        expectedValues.add("3");
        final PublishProcessor<String> publishProcessor = PublishProcessor.create();
        AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @NonNull
            @Override
            public Disposable subscribe() {
                return publishProcessor.subscribe(new io.reactivex.functions.Consumer<String>() {
                    @Override
                    public void accept(@NonNull
                    String s) throws Exception {
                        onNextObtained(s);
                    }
                });
            }
        };
        Disposable disposable = emissionChecker.subscribe();
        publishProcessor.onNext("1");
        // "1"
        emissionChecker.awaitNextExpectedValue();
        publishProcessor.onNext("2");
        // "2"
        emissionChecker.awaitNextExpectedValue();
        publishProcessor.onNext("3");
        // "3"
        emissionChecker.awaitNextExpectedValue();
        // Should not throw exception
        emissionChecker.assertThatNoExpectedValuesLeft();
        disposable.dispose();
    }

    @Test
    public void shouldNotAssertThatNoExpectedValuesLeft() {
        Queue<String> expectedValues = new LinkedList<String>();
        expectedValues.add("expected_value");
        AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @NonNull
            @Override
            public Disposable subscribe() {
                return Flowable.just("expected_value").subscribe();// Don't pass value to emission checker

            }
        };
        Disposable disposable = emissionChecker.subscribe();
        try {
            emissionChecker.assertThatNoExpectedValuesLeft();
            failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (AssertionError expected) {
            // it's okay, we didn't call emissionChecker.awaitNextExpectedValue()
        } finally {
            disposable.dispose();
        }
    }

    @Test
    public void shouldStoreItemsInQueueAndThenAwaitNextExpectedValues() {
        final Queue<String> expectedValues = new LinkedList<String>();
        expectedValues.add("1");
        expectedValues.add("2");
        expectedValues.add("3");
        final PublishProcessor<String> publishProcessor = PublishProcessor.create();
        final AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @NonNull
            @Override
            public Disposable subscribe() {
                return publishProcessor.subscribe(new io.reactivex.functions.Consumer<String>() {
                    @Override
                    public void accept(@NonNull
                    String s) throws Exception {
                        onNextObtained(s);
                    }
                });
            }
        };
        final Disposable disposable = emissionChecker.subscribe();
        // Notice: We emit several values before awaiting any of them
        publishProcessor.onNext("1");
        publishProcessor.onNext("2");
        publishProcessor.onNext("3");
        // Now we should successfully await all these items one by one
        emissionChecker.awaitNextExpectedValue();
        emissionChecker.awaitNextExpectedValue();
        emissionChecker.awaitNextExpectedValue();
        emissionChecker.assertThatNoExpectedValuesLeft();
        disposable.dispose();
    }

    @Test
    public void shouldThrowExceptionBecauseFlowableEmittedUnexpectedItemAfterExpectedSequence() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        final Queue<String> expectedValues = new LinkedList<String>();
        expectedValues.add("1");
        expectedValues.add("2");
        expectedValues.add("3");
        final PublishProcessor<String> publishProcessor = PublishProcessor.create();
        final AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @NonNull
            @Override
            public Disposable subscribe() {
                return publishProcessor.subscribe(new io.reactivex.functions.Consumer<String>() {
                    @Override
                    public void accept(@NonNull
                    String s) throws Exception {
                        onNextObtained(s);
                    }
                });
            }
        };
        final Disposable disposable = emissionChecker.subscribe();
        publishProcessor.onNext("1");
        publishProcessor.onNext("2");
        publishProcessor.onNext("3");
        emissionChecker.awaitNextExpectedValue();
        emissionChecker.awaitNextExpectedValue();
        emissionChecker.awaitNextExpectedValue();
        emissionChecker.assertThatNoExpectedValuesLeft();
        assertThat(errors).isEmpty();
        publishProcessor.onNext("4");
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getCause()).hasMessage("Received emission, but no more emissions were expected: obtained 4, expectedValues = [], obtainedValues = []");
        disposable.dispose();
    }
}

