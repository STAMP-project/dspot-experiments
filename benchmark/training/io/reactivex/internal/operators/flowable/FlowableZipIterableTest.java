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
package io.reactivex.internal.operators.flowable;


import io.reactivex.Consumer;
import io.reactivex.Flowable;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.util.CrashingIterable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableZipIterableTest {
    BiFunction<String, String, String> concat2Strings;

    PublishProcessor<String> s1;

    PublishProcessor<String> s2;

    Flowable<String> zipped;

    Subscriber<String> subscriber;

    InOrder inOrder;

    BiFunction<Object, Object, String> zipr2 = new BiFunction<Object, Object, String>() {
        @Override
        public String apply(Object t1, Object t2) {
            return ("" + t1) + t2;
        }
    };

    Function3<Object, Object, Object, String> zipr3 = new Function3<Object, Object, Object, String>() {
        @Override
        public String apply(Object t1, Object t2, Object t3) {
            return (("" + t1) + t2) + t3;
        }
    };

    @Test
    public void testZipIterableSameSize() {
        PublishProcessor<String> r1 = PublishProcessor.create();
        /* define a Subscriber to receive aggregated events */
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        InOrder io = Mockito.inOrder(subscriber);
        Iterable<String> r2 = Arrays.asList("1", "2", "3");
        r1.zipWith(r2, zipr2).subscribe(subscriber);
        r1.onNext("one-");
        r1.onNext("two-");
        r1.onNext("three-");
        r1.onComplete();
        io.verify(subscriber).onNext("one-1");
        io.verify(subscriber).onNext("two-2");
        io.verify(subscriber).onNext("three-3");
        io.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testZipIterableEmptyFirstSize() {
        PublishProcessor<String> r1 = PublishProcessor.create();
        /* define a Subscriber to receive aggregated events */
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        InOrder io = Mockito.inOrder(subscriber);
        Iterable<String> r2 = Arrays.asList("1", "2", "3");
        r1.zipWith(r2, zipr2).subscribe(subscriber);
        r1.onComplete();
        io.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testZipIterableEmptySecond() {
        PublishProcessor<String> r1 = PublishProcessor.create();
        /* define a Subscriber to receive aggregated events */
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        InOrder io = Mockito.inOrder(subscriber);
        Iterable<String> r2 = Arrays.asList();
        r1.zipWith(r2, zipr2).subscribe(subscriber);
        r1.onNext("one-");
        r1.onNext("two-");
        r1.onNext("three-");
        r1.onComplete();
        io.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testZipIterableFirstShorter() {
        PublishProcessor<String> r1 = PublishProcessor.create();
        /* define a Subscriber to receive aggregated events */
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        InOrder io = Mockito.inOrder(subscriber);
        Iterable<String> r2 = Arrays.asList("1", "2", "3");
        r1.zipWith(r2, zipr2).subscribe(subscriber);
        r1.onNext("one-");
        r1.onNext("two-");
        r1.onComplete();
        io.verify(subscriber).onNext("one-1");
        io.verify(subscriber).onNext("two-2");
        io.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testZipIterableSecondShorter() {
        PublishProcessor<String> r1 = PublishProcessor.create();
        /* define a Subscriber to receive aggregated events */
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        InOrder io = Mockito.inOrder(subscriber);
        Iterable<String> r2 = Arrays.asList("1", "2");
        r1.zipWith(r2, zipr2).subscribe(subscriber);
        r1.onNext("one-");
        r1.onNext("two-");
        r1.onNext("three-");
        r1.onComplete();
        io.verify(subscriber).onNext("one-1");
        io.verify(subscriber).onNext("two-2");
        io.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testZipIterableFirstThrows() {
        PublishProcessor<String> r1 = PublishProcessor.create();
        /* define a Subscriber to receive aggregated events */
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        InOrder io = Mockito.inOrder(subscriber);
        Iterable<String> r2 = Arrays.asList("1", "2", "3");
        r1.zipWith(r2, zipr2).subscribe(subscriber);
        r1.onNext("one-");
        r1.onNext("two-");
        r1.onError(new TestException());
        io.verify(subscriber).onNext("one-1");
        io.verify(subscriber).onNext("two-2");
        io.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testZipIterableIteratorThrows() {
        PublishProcessor<String> r1 = PublishProcessor.create();
        /* define a Subscriber to receive aggregated events */
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        InOrder io = Mockito.inOrder(subscriber);
        Iterable<String> r2 = new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                throw new TestException();
            }
        };
        r1.zipWith(r2, zipr2).subscribe(subscriber);
        r1.onNext("one-");
        r1.onNext("two-");
        r1.onError(new TestException());
        io.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
    }

    @Test
    public void testZipIterableHasNextThrows() {
        PublishProcessor<String> r1 = PublishProcessor.create();
        /* define a Subscriber to receive aggregated events */
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        InOrder io = Mockito.inOrder(subscriber);
        Iterable<String> r2 = new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    int count;

                    @Override
                    public boolean hasNext() {
                        if ((count) == 0) {
                            return true;
                        }
                        throw new TestException();
                    }

                    @Override
                    public String next() {
                        (count)++;
                        return "1";
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }
        };
        r1.zipWith(r2, zipr2).subscribe(subscriber);
        r1.onNext("one-");
        r1.onError(new TestException());
        io.verify(subscriber).onNext("one-1");
        io.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testZipIterableNextThrows() {
        PublishProcessor<String> r1 = PublishProcessor.create();
        /* define a Subscriber to receive aggregated events */
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        InOrder io = Mockito.inOrder(subscriber);
        Iterable<String> r2 = new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    @Override
                    public boolean hasNext() {
                        return true;
                    }

                    @Override
                    public String next() {
                        throw new TestException();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }
        };
        r1.zipWith(r2, zipr2).subscribe(subscriber);
        r1.onError(new TestException());
        io.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    Consumer<String> printer = new Consumer<String>() {
        @Override
        public void accept(String pv) {
            System.out.println(pv);
        }
    };

    static final class SquareStr implements Function<Integer, String> {
        final AtomicInteger counter = new AtomicInteger();

        @Override
        public String apply(Integer t1) {
            counter.incrementAndGet();
            System.out.println(((((("Omg I'm calculating so hard: " + t1) + "*") + t1) + "=") + (t1 * t1)));
            return " " + (t1 * t1);
        }
    }

    @Test
    public void testTake2() {
        Flowable<Integer> f = Flowable.just(1, 2, 3, 4, 5);
        Iterable<String> it = Arrays.asList("a", "b", "c", "d", "e");
        FlowableZipIterableTest.SquareStr squareStr = new FlowableZipIterableTest.SquareStr();
        f.map(squareStr).zipWith(it, concat2Strings).take(2).subscribe(printer);
        Assert.assertEquals(2, squareStr.counter.get());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.just(1).zipWith(Arrays.asList(1), new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Integer>, Flowable<Object>>() {
            @Override
            public Flowable<Object> apply(Flowable<Integer> f) throws Exception {
                return f.zipWith(Arrays.asList(1), new BiFunction<Integer, Integer, Object>() {
                    @Override
                    public Object apply(Integer a, Integer b) throws Exception {
                        return a + b;
                    }
                });
            }
        });
    }

    @Test
    public void iteratorThrows() {
        Flowable.just(1).zipWith(new CrashingIterable(100, 1, 100), new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }).test().assertFailureAndMessage(TestException.class, "hasNext()");
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            zipWith(Arrays.asList(1), new BiFunction<Integer, Integer, Object>() {
                @Override
                public Object apply(Integer a, Integer b) throws Exception {
                    return a + b;
                }
            }).test().assertResult(2);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

