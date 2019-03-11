package com.baeldung.rxjava;


import BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;
import rx.exceptions.MissingBackpressureException;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class RxJavaBackpressureLongRunningUnitTest {
    @Test
    public void givenColdObservable_shouldNotThrowException() {
        // given
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        // when
        Observable.range(1, 1000000).observeOn(Schedulers.computation()).subscribe(testSubscriber);
        // then
        testSubscriber.awaitTerminalEvent();
        Assert.assertTrue(((testSubscriber.getOnErrorEvents().size()) == 0));
    }

    @Test
    public void givenHotObservable_whenBackpressureNotDefined_shouldTrowException() {
        // given
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        PublishSubject<Integer> source = PublishSubject.create();
        source.observeOn(Schedulers.computation()).subscribe(testSubscriber);
        // when
        IntStream.range(0, 1000000).forEach(source::onNext);
        // then
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertError(MissingBackpressureException.class);
    }

    @Test
    public void givenHotObservable_whenWindowIsDefined_shouldNotThrowException() {
        // given
        TestSubscriber<Observable<Integer>> testSubscriber = new TestSubscriber();
        PublishSubject<Integer> source = PublishSubject.create();
        // when
        source.window(500).observeOn(Schedulers.computation()).subscribe(testSubscriber);
        IntStream.range(0, 1000).forEach(source::onNext);
        // then
        testSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS);
        Assert.assertTrue(((testSubscriber.getOnErrorEvents().size()) == 0));
    }

    @Test
    public void givenHotObservable_whenBufferIsDefined_shouldNotThrowException() {
        // given
        TestSubscriber<List<Integer>> testSubscriber = new TestSubscriber();
        PublishSubject<Integer> source = PublishSubject.create();
        // when
        source.buffer(1024).observeOn(Schedulers.computation()).subscribe(testSubscriber);
        IntStream.range(0, 1000).forEach(source::onNext);
        // then
        testSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS);
        Assert.assertTrue(((testSubscriber.getOnErrorEvents().size()) == 0));
    }

    @Test
    public void givenHotObservable_whenSkippingOperationIsDefined_shouldNotThrowException() {
        // given
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        PublishSubject<Integer> source = PublishSubject.create();
        // when
        // .throttleFirst(100, TimeUnit.MILLISECONDS)
        source.sample(100, TimeUnit.MILLISECONDS).observeOn(Schedulers.computation()).subscribe(testSubscriber);
        IntStream.range(0, 1000).forEach(source::onNext);
        // then
        testSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS);
        Assert.assertTrue(((testSubscriber.getOnErrorEvents().size()) == 0));
    }

    @Test
    public void givenHotObservable_whenOnBackpressureBufferDefined_shouldNotThrowException() {
        // given
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        // when
        Observable.range(1, 1000000).onBackpressureBuffer(16, () -> {
        }, ON_OVERFLOW_DROP_OLDEST).observeOn(Schedulers.computation()).subscribe(testSubscriber);
        // then
        testSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS);
        Assert.assertTrue(((testSubscriber.getOnErrorEvents().size()) == 0));
    }

    @Test
    public void givenHotObservable_whenOnBackpressureDropDefined_shouldNotThrowException() {
        // given
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        // when
        Observable.range(1, 1000000).onBackpressureDrop().observeOn(Schedulers.computation()).subscribe(testSubscriber);
        // then
        testSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS);
        Assert.assertTrue(((testSubscriber.getOnErrorEvents().size()) == 0));
    }
}

