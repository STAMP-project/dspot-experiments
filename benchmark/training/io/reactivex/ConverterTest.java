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
package io.reactivex;


import BackpressureStrategy.MISSING;
import io.reactivex.exceptions.TestException;
import org.junit.Assert;
import org.junit.Test;


public final class ConverterTest {
    @Test
    public void flowableConverterThrows() {
        try {
            Flowable.just(1).as(new FlowableConverter<Integer, Integer>() {
                @Override
                public Integer apply(Flowable<Integer> v) {
                    throw new TestException("Forced failure");
                }
            });
            Assert.fail("Should have thrown!");
        } catch (TestException ex) {
            Assert.assertEquals("Forced failure", ex.getMessage());
        }
    }

    @Test
    public void observableConverterThrows() {
        try {
            Observable.just(1).as(new ObservableConverter<Integer, Integer>() {
                @Override
                public Integer apply(Observable<Integer> v) {
                    throw new TestException("Forced failure");
                }
            });
            Assert.fail("Should have thrown!");
        } catch (TestException ex) {
            Assert.assertEquals("Forced failure", ex.getMessage());
        }
    }

    @Test
    public void singleConverterThrows() {
        try {
            Single.just(1).as(new SingleConverter<Integer, Integer>() {
                @Override
                public Integer apply(Single<Integer> v) {
                    throw new TestException("Forced failure");
                }
            });
            Assert.fail("Should have thrown!");
        } catch (TestException ex) {
            Assert.assertEquals("Forced failure", ex.getMessage());
        }
    }

    @Test
    public void maybeConverterThrows() {
        try {
            Maybe.just(1).as(new MaybeConverter<Integer, Integer>() {
                @Override
                public Integer apply(Maybe<Integer> v) {
                    throw new TestException("Forced failure");
                }
            });
            Assert.fail("Should have thrown!");
        } catch (TestException ex) {
            Assert.assertEquals("Forced failure", ex.getMessage());
        }
    }

    @Test
    public void completableConverterThrows() {
        try {
            Completable.complete().as(new CompletableConverter<Completable>() {
                @Override
                public Completable apply(Completable v) {
                    throw new TestException("Forced failure");
                }
            });
            Assert.fail("Should have thrown!");
        } catch (TestException ex) {
            Assert.assertEquals("Forced failure", ex.getMessage());
        }
    }

    // Test demos for signature generics in compose() methods. Just needs to compile.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void observableGenericsSignatureTest() {
        ConverterTest.A<String, Integer> a = new ConverterTest.A<String, Integer>() {};
        Observable.just(a).as(((ObservableConverter) (ConverterTest.testObservableConverterCreator())));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void singleGenericsSignatureTest() {
        ConverterTest.A<String, Integer> a = new ConverterTest.A<String, Integer>() {};
        Single.just(a).as(((SingleConverter) (ConverterTest.<String>testSingleConverterCreator())));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void maybeGenericsSignatureTest() {
        ConverterTest.A<String, Integer> a = new ConverterTest.A<String, Integer>() {};
        Maybe.just(a).as(((MaybeConverter) (ConverterTest.<String>testMaybeConverterCreator())));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void flowableGenericsSignatureTest() {
        ConverterTest.A<String, Integer> a = new ConverterTest.A<String, Integer>() {};
        Flowable.just(a).as(((FlowableConverter) (ConverterTest.<String>testFlowableConverterCreator())));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void parallelFlowableGenericsSignatureTest() {
        ConverterTest.A<String, Integer> a = new ConverterTest.A<String, Integer>() {};
        Flowable.just(a).parallel().as(((ParallelFlowableConverter) (ConverterTest.<String>testParallelFlowableConverterCreator())));
    }

    @Test
    public void compositeTest() {
        ConverterTest.CompositeConverter converter = new ConverterTest.CompositeConverter();
        Flowable.just(1).as(converter).test().assertValue(1);
        Observable.just(1).as(converter).test().assertValue(1);
        Maybe.just(1).as(converter).test().assertValue(1);
        Single.just(1).as(converter).test().assertValue(1);
        Completable.complete().as(converter).test().assertComplete();
        Flowable.just(1).parallel().as(converter).test().assertValue(1);
    }

    interface A<T, R> {}

    interface B<T> {}

    static class CompositeConverter implements CompletableConverter<Flowable<Integer>> , FlowableConverter<Integer, Observable<Integer>> , MaybeConverter<Integer, Flowable<Integer>> , ObservableConverter<Integer, Flowable<Integer>> , ParallelFlowableConverter<Integer, Flowable<Integer>> , SingleConverter<Integer, Flowable<Integer>> {
        @Override
        public io.reactivex.parallel.Flowable<Integer> apply(ParallelFlowable<Integer> upstream) {
            return upstream.sequential();
        }

        @Override
        public io.reactivex.parallel.Flowable<Integer> apply(Completable upstream) {
            return upstream.toFlowable();
        }

        @Override
        public Observable<Integer> apply(Flowable<Integer> upstream) {
            return upstream.toObservable();
        }

        @Override
        public io.reactivex.parallel.Flowable<Integer> apply(Maybe<Integer> upstream) {
            return upstream.toFlowable();
        }

        @Override
        public io.reactivex.parallel.Flowable<Integer> apply(Observable<Integer> upstream) {
            return upstream.toFlowable(MISSING);
        }

        @Override
        public io.reactivex.parallel.Flowable<Integer> apply(Single<Integer> upstream) {
            return upstream.toFlowable();
        }
    }
}

