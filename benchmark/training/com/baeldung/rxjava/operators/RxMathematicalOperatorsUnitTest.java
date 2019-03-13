package com.baeldung.rxjava.operators;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.Test;
import rx.Observable;
import rx.observables.MathObservable;
import rx.observers.TestSubscriber;


public class RxMathematicalOperatorsUnitTest {
    @Test
    public void givenRangeNumericObservable_whenCalculatingAverage_ThenSuccessfull() {
        // given
        Observable<Integer> sourceObservable = Observable.range(1, 20);
        TestSubscriber<Integer> subscriber = TestSubscriber.create();
        // when
        MathObservable.averageInteger(sourceObservable).subscribe(subscriber);
        // then
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValue(10);
    }

    @Test
    public void givenRangeNumericObservable_whenCalculatingSum_ThenSuccessfull() {
        // given
        Observable<Integer> sourceObservable = Observable.range(1, 20);
        TestSubscriber<Integer> subscriber = TestSubscriber.create();
        // when
        MathObservable.sumInteger(sourceObservable).subscribe(subscriber);
        // then
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValue(210);
    }

    @Test
    public void givenRangeNumericObservable_whenCalculatingMax_ThenSuccessfullObtainingMaxValue() {
        // given
        Observable<Integer> sourceObservable = Observable.range(1, 20);
        TestSubscriber<Integer> subscriber = TestSubscriber.create();
        // when
        MathObservable.max(sourceObservable).subscribe(subscriber);
        // then
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValue(20);
    }

    @Test
    public void givenRangeNumericObservable_whenCalculatingMin_ThenSuccessfullObtainingMinValue() {
        // given
        Observable<Integer> sourceObservable = Observable.range(1, 20);
        TestSubscriber<Integer> subscriber = TestSubscriber.create();
        // when
        MathObservable.min(sourceObservable).subscribe(subscriber);
        // then
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValue(1);
    }

    @Test
    public void givenItemObservable_whenCalculatingMaxWithComparator_ThenSuccessfullObtainingMaxItem() {
        // given
        RxMathematicalOperatorsUnitTest.Item five = new RxMathematicalOperatorsUnitTest.Item(5);
        List<RxMathematicalOperatorsUnitTest.Item> list = Arrays.asList(new RxMathematicalOperatorsUnitTest.Item(1), new RxMathematicalOperatorsUnitTest.Item(2), new RxMathematicalOperatorsUnitTest.Item(3), new RxMathematicalOperatorsUnitTest.Item(4), five);
        Observable<RxMathematicalOperatorsUnitTest.Item> itemObservable = Observable.from(list);
        TestSubscriber<RxMathematicalOperatorsUnitTest.Item> subscriber = TestSubscriber.create();
        // when
        MathObservable.from(itemObservable).max(Comparator.comparing(RxMathematicalOperatorsUnitTest.Item::getId)).subscribe(subscriber);
        // then
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValue(five);
    }

    @Test
    public void givenItemObservable_whenCalculatingMinWithComparator_ThenSuccessfullObtainingMinItem() {
        // given
        RxMathematicalOperatorsUnitTest.Item one = new RxMathematicalOperatorsUnitTest.Item(1);
        List<RxMathematicalOperatorsUnitTest.Item> list = Arrays.asList(one, new RxMathematicalOperatorsUnitTest.Item(2), new RxMathematicalOperatorsUnitTest.Item(3), new RxMathematicalOperatorsUnitTest.Item(4), new RxMathematicalOperatorsUnitTest.Item(5));
        TestSubscriber<RxMathematicalOperatorsUnitTest.Item> subscriber = TestSubscriber.create();
        Observable<RxMathematicalOperatorsUnitTest.Item> itemObservable = Observable.from(list);
        // when
        MathObservable.from(itemObservable).min(Comparator.comparing(RxMathematicalOperatorsUnitTest.Item::getId)).subscribe(subscriber);
        // then
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValue(one);
    }

    class Item {
        private Integer id;

        public Item(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }
    }
}

