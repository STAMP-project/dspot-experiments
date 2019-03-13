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


import io.reactivex.SingleObserver;
import io.reactivex.functions.Function;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableToMapTest {
    Observer<Object> objectObserver;

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
    public void testToMapObservable() {
        Observable<String> source = just("a", "bb", "ccc", "dddd");
        Observable<Map<Integer, String>> mapped = source.toMap(lengthFunc).toObservable();
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(1, "a");
        expected.put(2, "bb");
        expected.put(3, "ccc");
        expected.put(4, "dddd");
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectObserver, Mockito.times(1)).onNext(expected);
        Mockito.verify(objectObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testToMapWithValueSelectorObservable() {
        Observable<String> source = just("a", "bb", "ccc", "dddd");
        Observable<Map<Integer, String>> mapped = source.toMap(lengthFunc, duplicate).toObservable();
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(1, "aa");
        expected.put(2, "bbbb");
        expected.put(3, "cccccc");
        expected.put(4, "dddddddd");
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectObserver, Mockito.times(1)).onNext(expected);
        Mockito.verify(objectObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testToMapWithErrorObservable() {
        Observable<String> source = just("a", "bb", "ccc", "dddd");
        Function<String, Integer> lengthFuncErr = new Function<String, Integer>() {
            @Override
            public Integer apply(String t1) {
                if ("bb".equals(t1)) {
                    throw new RuntimeException("Forced Failure");
                }
                return t1.length();
            }
        };
        Observable<Map<Integer, String>> mapped = source.toMap(lengthFuncErr).toObservable();
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(1, "a");
        expected.put(2, "bb");
        expected.put(3, "ccc");
        expected.put(4, "dddd");
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.never()).onNext(expected);
        Mockito.verify(objectObserver, Mockito.never()).onComplete();
        Mockito.verify(objectObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testToMapWithErrorInValueSelectorObservable() {
        Observable<String> source = just("a", "bb", "ccc", "dddd");
        Function<String, String> duplicateErr = new Function<String, String>() {
            @Override
            public String apply(String t1) {
                if ("bb".equals(t1)) {
                    throw new RuntimeException("Forced failure");
                }
                return t1 + t1;
            }
        };
        Observable<Map<Integer, String>> mapped = source.toMap(lengthFunc, duplicateErr).toObservable();
        Map<Integer, String> expected = new HashMap<Integer, String>();
        expected.put(1, "aa");
        expected.put(2, "bbbb");
        expected.put(3, "cccccc");
        expected.put(4, "dddddddd");
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.never()).onNext(expected);
        Mockito.verify(objectObserver, Mockito.never()).onComplete();
        Mockito.verify(objectObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testToMapWithFactoryObservable() {
        Observable<String> source = just("a", "bb", "ccc", "dddd");
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
        Observable<Map<Integer, String>> mapped = source.toMap(lengthFunc, new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        }, mapFactory).toObservable();
        Map<Integer, String> expected = new LinkedHashMap<Integer, String>();
        expected.put(2, "bb");
        expected.put(3, "ccc");
        expected.put(4, "dddd");
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectObserver, Mockito.times(1)).onNext(expected);
        Mockito.verify(objectObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testToMapWithErrorThrowingFactoryObservable() {
        Observable<String> source = just("a", "bb", "ccc", "dddd");
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
        Observable<Map<Integer, String>> mapped = source.toMap(lengthFunc, new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        }, mapFactory).toObservable();
        Map<Integer, String> expected = new LinkedHashMap<Integer, String>();
        expected.put(2, "bb");
        expected.put(3, "ccc");
        expected.put(4, "dddd");
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.never()).onNext(expected);
        Mockito.verify(objectObserver, Mockito.never()).onComplete();
        Mockito.verify(objectObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testToMap() {
        Observable<String> source = just("a", "bb", "ccc", "dddd");
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
        Observable<String> source = just("a", "bb", "ccc", "dddd");
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
        Observable<String> source = just("a", "bb", "ccc", "dddd");
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
        Observable<String> source = just("a", "bb", "ccc", "dddd");
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
        Observable<String> source = just("a", "bb", "ccc", "dddd");
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
        Observable<String> source = just("a", "bb", "ccc", "dddd");
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

