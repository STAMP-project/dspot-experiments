/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package io.reactivex.internal.operators.observable;


import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.operators.observable.BlockingObservableIterable.BlockingObservableIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import org.junit.Assert;
import org.junit.Test;


public class BlockingObservableToIteratorTest {
    @Test
    public void testToIterator() {
        Observable<String> obs = just("one", "two", "three");
        Iterator<String> it = obs.blockingIterable().iterator();
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("one", it.next());
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("two", it.next());
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("three", it.next());
        Assert.assertEquals(false, it.hasNext());
    }

    @Test(expected = TestException.class)
    public void testToIteratorWithException() {
        Observable<String> obs = unsafeCreate(new io.reactivex.ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                observer.onNext("one");
                observer.onError(new TestException());
            }
        });
        Iterator<String> it = obs.blockingIterable().iterator();
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("one", it.next());
        Assert.assertEquals(true, it.hasNext());
        it.next();
    }

    @Test
    public void dispose() {
        BlockingObservableIterator<Integer> it = new BlockingObservableIterator<Integer>(128);
        Assert.assertFalse(it.isDisposed());
        it.dispose();
        Assert.assertTrue(it.isDisposed());
    }

    @Test
    public void interruptWait() {
        BlockingObservableIterator<Integer> it = new BlockingObservableIterator<Integer>(128);
        try {
            Thread.currentThread().interrupt();
            it.hasNext();
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof InterruptedException));
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void emptyThrowsNoSuch() {
        BlockingObservableIterator<Integer> it = new BlockingObservableIterator<Integer>(128);
        it.onComplete();
        it.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void remove() {
        BlockingObservableIterator<Integer> it = new BlockingObservableIterator<Integer>(128);
        it.remove();
    }
}

