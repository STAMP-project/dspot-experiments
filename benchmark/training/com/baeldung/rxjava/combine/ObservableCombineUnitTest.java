package com.baeldung.rxjava.combine;


import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;


public class ObservableCombineUnitTest {
    @Test
    public void givenTwoObservables_whenMerged_shouldEmitCombinedResults() {
        TestSubscriber<String> testSubscriber = new TestSubscriber();
        Observable.merge(Observable.from(Arrays.asList("Hello", "World")), Observable.from(Arrays.asList("I love", "RxJava"))).subscribe(testSubscriber);
        testSubscriber.assertValues("Hello", "World", "I love", "RxJava");
    }

    @Test
    public void givenTwoObservables_whenZipped_thenReturnCombinedResults() {
        TestSubscriber<String> testSubscriber = new TestSubscriber();
        Observable.zip(Observable.from(Arrays.asList("Simple", "Moderate", "Complex")), Observable.from(Arrays.asList("Solutions", "Success", "Hierarchy")), ( str1, str2) -> String.format("%s %s", str1, str2)).subscribe(testSubscriber);
        testSubscriber.assertValues("Simple Solutions", "Moderate Success", "Complex Hierarchy");
    }

    @Test
    public void givenMutipleObservablesOneThrows_whenMerged_thenCombineBeforePropagatingError() {
        TestSubscriber<String> testSubscriber = new TestSubscriber();
        Observable.mergeDelayError(Observable.from(Arrays.asList("hello", "world")), Observable.error(new RuntimeException("Some exception")), Observable.from(Arrays.asList("rxjava"))).subscribe(testSubscriber);
        testSubscriber.assertValues("hello", "world", "rxjava");
        testSubscriber.assertError(RuntimeException.class);
    }

    @Test
    public void givenAStream_whenZippedWithInterval_shouldDelayStreamEmmission() {
        TestSubscriber<String> testSubscriber = new TestSubscriber();
        Observable<String> data = Observable.just("one", "two", "three", "four", "five");
        Observable<Long> interval = Observable.interval(1L, TimeUnit.SECONDS);
        Observable.zip(data, interval, ( strData, tick) -> String.format("[%d]=%s", tick, strData)).toBlocking().subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(5);
        testSubscriber.assertValues("[0]=one", "[1]=two", "[2]=three", "[3]=four", "[4]=five");
    }
}

