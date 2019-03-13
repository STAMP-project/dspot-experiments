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


import io.reactivex.SingleObserver;
import io.reactivex.functions.Function;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableToMapTest {
    Subscriber<Object> objectSubscriber;

    SingleObserver<Object> singleObserver;

    Function<String, Integer> lengthFunc = new Function<String, Integer>() {
        @Override
        public Integer apply(String t1) {
            return t1.length();
        }
    };

    Function<String, String> duplicate = new Function<String, String>() {
        @Override
        public String apply(String t1) {
            return t1 + t1;
        }
    };

    @Test
    public void testToMapFlowable() {
        Flowable<String> source = Flowable.just("a", "bb", "ccc", "dddd");
        Flowable<Map<Integer, String>> mapped = source.toMap(lengthFunc).toFlowable();
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(1, "a");
        expected.put(2, "bb");
        expected.put(3, "ccc");
        expected.put(4, "dddd");
        mapped.subscribe(objectSubscriber);
        Mockito.verify(objectSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectSubscriber, Mockito.times(1)).onNext(expected);
        Mockito.verify(objectSubscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testToMapWithValueSelectorFlowable() {
        Flowable<String> source = Flowable.just("a", "bb", "ccc", "dddd");
        Flowable<Map<Integer, String>> mapped = source.toMap(lengthFunc, duplicate).toFlowable();
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(1, "aa");
        expected.put(2, "bbbb");
        expected.put(3, "cccccc");
        expected.put(4, "dddddddd");
        mapped.subscribe(objectSubscriber);
        Mockito.verify(objectSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectSubscriber, Mockito.times(1)).onNext(expected);
        Mockito.verify(objectSubscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testToMapWithErrorFlowable() {
        Flowable<String> source = Flowable.just("a", "bb", "ccc", "dddd");
        Function<String, Integer> lengthFuncErr = new Function<String, Integer>() {
            @Override
            public Integer apply(String t1) {
                if ("bb".equals(t1)) {
                    throw new RuntimeException("Forced Failure");
                }
                return t1.length();
            }
        };
        Flowable<Map<Integer, String>> mapped = source.toMap(lengthFuncErr).toFlowable();
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(1, "a");
        expected.put(2, "bb");
        expected.put(3, "ccc");
        expected.put(4, "dddd");
        mapped.subscribe(objectSubscriber);
        Mockito.verify(objectSubscriber, Mockito.never()).onNext(expected);
        Mockito.verify(objectSubscriber, Mockito.never()).onComplete();
        Mockito.verify(objectSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testToMapWithErrorInValueSelectorFlowable() {
        Flowable<String> source = Flowable.just("a", "bb", "ccc", "dddd");
        Function<String, String> duplicateErr = new Function<String, String>() {
            @Override
            public String apply(String t1) {
                if ("bb".equals(t1)) {
                    throw new RuntimeException("Forced failure");
                }
                return t1 + t1;
            }
        };
        Flowable<Map<Integer, String>> mapped = source.toMap(lengthFunc, duplicateErr).toFlowable();
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(1, "aa");
        expected.put(2, "bbbb");
        expected.put(3, "cccccc");
        expected.put(4, "dddddddd");
        mapped.subscribe(objectSubscriber);
        Mockito.verify(objectSubscriber, Mockito.never()).onNext(expected);
        Mockito.verify(objectSubscriber, Mockito.never()).onComplete();
        Mockito.verify(objectSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testToMapWithFactoryFlowable() {
        Flowable<String> source = Flowable.just("a", "bb", "ccc", "dddd");
        Callable<Map<Integer, String>> mapFactory = new Callable<Map<Integer, String>>() {
            @Override
            public Map<Integer, String> call() {
                return new LinkedHashMap<Integer, String>() {
                    private static final long serialVersionUID = -3296811238780863394L;

                    @Override
                    protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
                        return (size()) > 3;
                    }
                };
            }
        };
        Function<String, Integer> lengthFunc = new Function<String, Integer>() {
            @Override
            public Integer apply(String t1) {
                return t1.length();
            }
        };
        Flowable<Map<Integer, String>> mapped = source.toMap(lengthFunc, new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        }, mapFactory).toFlowable();
        Map<Integer, String> expected = new LinkedHashMap<Integer, String>();
        expected.put(2, "bb");
        expected.put(3, "ccc");
        expected.put(4, "dddd");
        mapped.subscribe(objectSubscriber);
        Mockito.verify(objectSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectSubscriber, Mockito.times(1)).onNext(expected);
        Mockito.verify(objectSubscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testToMapWithErrorThrowingFactoryFlowable() {
        Flowable<String> source = Flowable.just("a", "bb", "ccc", "dddd");
        Callable<Map<Integer, String>> mapFactory = new Callable<Map<Integer, String>>() {
            @Override
            public Map<Integer, String> call() {
                throw new RuntimeException("Forced failure");
            }
        };
        Function<String, Integer> lengthFunc = new Function<String, Integer>() {
            @Override
            public Integer apply(String t1) {
                return t1.length();
            }
        };
        Flowable<Map<Integer, String>> mapped = source.toMap(lengthFunc, new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        }, mapFactory).toFlowable();
        Map<Integer, String> expected = new LinkedHashMap<Integer, String>();
        expected.put(2, "bb");
        expected.put(3, "ccc");
        expected.put(4, "dddd");
        mapped.subscribe(objectSubscriber);
        Mockito.verify(objectSubscriber, Mockito.never()).onNext(expected);
        Mockito.verify(objectSubscriber, Mockito.never()).onComplete();
        Mockito.verify(objectSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testToMap() {
        Flowable<String> source = Flowable.just("a", "bb", "ccc", "dddd");
        Single<Map<Integer, String>> mapped = source.toMap(lengthFunc);
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(1, "a");
        expected.put(2, "bb");
        expected.put(3, "ccc");
        expected.put(4, "dddd");
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(singleObserver, Mockito.times(1)).onSuccess(expected);
    }

    @Test
    public void testToMapWithValueSelector() {
        Flowable<String> source = Flowable.just("a", "bb", "ccc", "dddd");
        Single<Map<Integer, String>> mapped = source.toMap(lengthFunc, duplicate);
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(1, "aa");
        expected.put(2, "bbbb");
        expected.put(3, "cccccc");
        expected.put(4, "dddddddd");
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(singleObserver, Mockito.times(1)).onSuccess(expected);
    }

    @Test
    public void testToMapWithError() {
        Flowable<String> source = Flowable.just("a", "bb", "ccc", "dddd");
        Function<String, Integer> lengthFuncErr = new Function<String, Integer>() {
            @Override
            public Integer apply(String t1) {
                if ("bb".equals(t1)) {
                    throw new RuntimeException("Forced Failure");
                }
                return t1.length();
            }
        };
        Single<Map<Integer, String>> mapped = source.toMap(lengthFuncErr);
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(1, "a");
        expected.put(2, "bb");
        expected.put(3, "ccc");
        expected.put(4, "dddd");
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.never()).onSuccess(expected);
        Mockito.verify(singleObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testToMapWithErrorInValueSelector() {
        Flowable<String> source = Flowable.just("a", "bb", "ccc", "dddd");
        Function<String, String> duplicateErr = new Function<String, String>() {
            @Override
            public String apply(String t1) {
                if ("bb".equals(t1)) {
                    throw new RuntimeException("Forced failure");
                }
                return t1 + t1;
            }
        };
        Single<Map<Integer, String>> mapped = source.toMap(lengthFunc, duplicateErr);
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(1, "aa");
        expected.put(2, "bbbb");
        expected.put(3, "cccccc");
        expected.put(4, "dddddddd");
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.never()).onSuccess(expected);
        Mockito.verify(singleObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testToMapWithFactory() {
        Flowable<String> source = Flowable.just("a", "bb", "ccc", "dddd");
        Callable<Map<Integer, String>> mapFactory = new Callable<Map<Integer, String>>() {
            @Override
            public Map<Integer, String> call() {
                return new LinkedHashMap<Integer, String>() {
                    private static final long serialVersionUID = -3296811238780863394L;

                    @Override
                    protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
                        return (size()) > 3;
                    }
                };
            }
        };
        Function<String, Integer> lengthFunc = new Function<String, Integer>() {
            @Override
            public Integer apply(String t1) {
                return t1.length();
            }
        };
        Single<Map<Integer, String>> mapped = source.toMap(lengthFunc, new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        }, mapFactory);
        Map<Integer, String> expected = new LinkedHashMap<Integer, String>();
        expected.put(2, "bb");
        expected.put(3, "ccc");
        expected.put(4, "dddd");
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(singleObserver, Mockito.times(1)).onSuccess(expected);
    }

    @Test
    public void testToMapWithErrorThrowingFactory() {
        Flowable<String> source = Flowable.just("a", "bb", "ccc", "dddd");
        Callable<Map<Integer, String>> mapFactory = new Callable<Map<Integer, String>>() {
            @Override
            public Map<Integer, String> call() {
                throw new RuntimeException("Forced failure");
            }
        };
        Function<String, Integer> lengthFunc = new Function<String, Integer>() {
            @Override
            public Integer apply(String t1) {
                return t1.length();
            }
        };
        Single<Map<Integer, String>> mapped = source.toMap(lengthFunc, new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        }, mapFactory);
        Map<Integer, String> expected = new LinkedHashMap<Integer, String>();
        expected.put(2, "bb");
        expected.put(3, "ccc");
        expected.put(4, "dddd");
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.never()).onSuccess(expected);
        Mockito.verify(singleObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }
}

