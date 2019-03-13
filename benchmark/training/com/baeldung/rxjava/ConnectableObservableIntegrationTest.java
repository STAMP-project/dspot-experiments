package com.baeldung.rxjava;


import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.junit.Test;
import rx.Observable;
import rx.observables.ConnectableObservable;


public class ConnectableObservableIntegrationTest {
    @Test
    public void givenConnectableObservable_whenConnect_thenGetMessage() throws InterruptedException {
        String[] result = new String[]{ "" };
        ConnectableObservable<Long> connectable = Observable.interval(500, TimeUnit.MILLISECONDS).publish();
        connectable.subscribe(( i) -> result[0] += i);
        Assert.assertFalse(result[0].equals("01"));
        connectable.connect();
        await().until(() -> assertTrue(result[0].equals("01")));
    }
}

