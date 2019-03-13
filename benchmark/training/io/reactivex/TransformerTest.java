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


import io.reactivex.exceptions.TestException;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;


public class TransformerTest {
    @Test
    public void flowableTransformerThrows() {
        try {
            Flowable.just(1).compose(new FlowableTransformer<Integer, Integer>() {
                @Override
                public Publisher<Integer> apply(Flowable<Integer> v) {
                    throw new TestException("Forced failure");
                }
            });
            Assert.fail("Should have thrown!");
        } catch (TestException ex) {
            Assert.assertEquals("Forced failure", ex.getMessage());
        }
    }

    @Test
    public void observableTransformerThrows() {
        try {
            Observable.just(1).compose(new ObservableTransformer<Integer, Integer>() {
                @Override
                public Observable<Integer> apply(Observable<Integer> v) {
                    throw new TestException("Forced failure");
                }
            });
            Assert.fail("Should have thrown!");
        } catch (TestException ex) {
            Assert.assertEquals("Forced failure", ex.getMessage());
        }
    }

    @Test
    public void singleTransformerThrows() {
        try {
            Single.just(1).compose(new SingleTransformer<Integer, Integer>() {
                @Override
                public Single<Integer> apply(Single<Integer> v) {
                    throw new TestException("Forced failure");
                }
            });
            Assert.fail("Should have thrown!");
        } catch (TestException ex) {
            Assert.assertEquals("Forced failure", ex.getMessage());
        }
    }

    @Test
    public void maybeTransformerThrows() {
        try {
            Maybe.just(1).compose(new MaybeTransformer<Integer, Integer>() {
                @Override
                public Maybe<Integer> apply(Maybe<Integer> v) {
                    throw new TestException("Forced failure");
                }
            });
            Assert.fail("Should have thrown!");
        } catch (TestException ex) {
            Assert.assertEquals("Forced failure", ex.getMessage());
        }
    }

    @Test
    public void completableTransformerThrows() {
        try {
            Completable.complete().compose(new CompletableTransformer() {
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
    @Test
    public void observableGenericsSignatureTest() {
        TransformerTest.A<String, Integer> a = new TransformerTest.A<String, Integer>() {};
        Observable.just(a).compose(TransformerTest.<String>testObservableTransformerCreator());
    }

    @Test
    public void singleGenericsSignatureTest() {
        TransformerTest.A<String, Integer> a = new TransformerTest.A<String, Integer>() {};
        Single.just(a).compose(TransformerTest.<String>testSingleTransformerCreator());
    }

    @Test
    public void maybeGenericsSignatureTest() {
        TransformerTest.A<String, Integer> a = new TransformerTest.A<String, Integer>() {};
        Maybe.just(a).compose(TransformerTest.<String>testMaybeTransformerCreator());
    }

    @Test
    public void flowableGenericsSignatureTest() {
        TransformerTest.A<String, Integer> a = new TransformerTest.A<String, Integer>() {};
        Flowable.just(a).compose(TransformerTest.<String>testFlowableTransformerCreator());
    }

    interface A<T, R> {}

    interface B<T> {}
}

