package com.baeldung.rxjava;


import junit.framework.Assert;
import org.junit.Test;
import rx.Observable;
import rx.Single;


public class SingleUnitTest {
    @Test
    public void givenSingleObservable_whenSuccess_thenGetMessage() throws InterruptedException {
        String[] result = new String[]{ "" };
        Single<String> single = Observable.just("Hello").toSingle().doOnSuccess(( i) -> result[0] += i).doOnError(( error) -> {
            throw new RuntimeException(error.getMessage());
        });
        single.subscribe();
        Assert.assertTrue(result[0].equals("Hello"));
    }
}

