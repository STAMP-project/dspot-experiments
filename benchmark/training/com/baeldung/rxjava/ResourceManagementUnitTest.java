package com.baeldung.rxjava;


import junit.framework.Assert;
import org.junit.Test;
import rx.Observable;


public class ResourceManagementUnitTest {
    @Test
    public void givenResource_whenUsingOberservable_thenCreatePrintDisposeResource() throws InterruptedException {
        String[] result = new String[]{ "" };
        Observable<Character> values = Observable.using(() -> "MyResource", ( r) -> Observable.create(( o) -> {
            for (Character c : r.toCharArray())
                o.onNext(c);

            o.onCompleted();
        }), ( r) -> System.out.println(("Disposed: " + r)));
        values.subscribe(( v) -> result[0] += v, ( e) -> result[0] += e);
        Assert.assertTrue(result[0].equals("MyResource"));
    }
}

