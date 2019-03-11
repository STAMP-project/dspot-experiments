package com.baeldung.rxjava;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;
import rx.Observable.Operator;
import rx.Observable.Transformer;


public class RxJavaCustomOperatorUnitTest {
    @Test
    public void whenUseCleanStringOperator_thenSuccess() {
        final List<String> list = Arrays.asList("john_1", "tom-3");
        final List<String> results = new ArrayList<>();
        final Observable<String> observable = Observable.from(list).lift(toCleanString());
        // when
        observable.subscribe(results::add);
        // then
        Assert.assertThat(results, Matchers.notNullValue());
        Assert.assertThat(results, Matchers.hasSize(2));
        Assert.assertThat(results, Matchers.hasItems("john1", "tom3"));
    }

    @Test
    public void whenUseToLengthOperator_thenSuccess() {
        final List<String> list = Arrays.asList("john", "tom");
        final List<Integer> results = new ArrayList<>();
        final Observable<Integer> observable = Observable.from(list).compose(toLength());
        // when
        observable.subscribe(results::add);
        // then
        Assert.assertThat(results, Matchers.notNullValue());
        Assert.assertThat(results, Matchers.hasSize(2));
        Assert.assertThat(results, Matchers.hasItems(4, 3));
    }

    @Test
    public void whenUseFunctionOperator_thenSuccess() {
        final Operator<String, String> cleanStringFn = ( subscriber) -> new Subscriber<String>(subscriber) {
            @Override
            public void onCompleted() {
                if (!(subscriber.isUnsubscribed())) {
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!(subscriber.isUnsubscribed())) {
                    subscriber.onError(t);
                }
            }

            @Override
            public void onNext(String str) {
                if (!(subscriber.isUnsubscribed())) {
                    final String result = str.replaceAll("[^A-Za-z0-9]", "");
                    subscriber.onNext(result);
                }
            }
        };
        final List<String> results = new ArrayList<>();
        Observable.from(Arrays.asList("ap_p-l@e", "or-an?ge")).lift(cleanStringFn).subscribe(results::add);
        Assert.assertThat(results, Matchers.notNullValue());
        Assert.assertThat(results, Matchers.hasSize(2));
        Assert.assertThat(results, Matchers.hasItems("apple", "orange"));
    }

    @Test
    public void whenUseFunctionTransformer_thenSuccess() {
        final Transformer<String, Integer> toLengthFn = ( source) -> source.map(String::length);
        final List<Integer> results = new ArrayList<>();
        Observable.from(Arrays.asList("apple", "orange")).compose(toLengthFn).subscribe(results::add);
        Assert.assertThat(results, Matchers.notNullValue());
        Assert.assertThat(results, Matchers.hasSize(2));
        Assert.assertThat(results, Matchers.hasItems(5, 6));
    }
}

