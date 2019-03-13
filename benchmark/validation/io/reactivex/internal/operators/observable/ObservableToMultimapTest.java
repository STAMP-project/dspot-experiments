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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableToMultimapTest {
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
    public void testToMultimapObservable() {
        Observable<String> source = just("a", "b", "cc", "dd");
        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc).toObservable();
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(1, Arrays.asList("a", "b"));
        expected.put(2, Arrays.asList("cc", "dd"));
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectObserver, Mockito.times(1)).onNext(expected);
        Mockito.verify(objectObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testToMultimapWithValueSelectorObservable() {
        Observable<String> source = just("a", "b", "cc", "dd");
        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, duplicate).toObservable();
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(1, Arrays.asList("aa", "bb"));
        expected.put(2, Arrays.asList("cccc", "dddd"));
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectObserver, Mockito.times(1)).onNext(expected);
        Mockito.verify(objectObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testToMultimapWithMapFactoryObservable() {
        Observable<String> source = Observable.just("a", "b", "cc", "dd", "eee", "fff");
        Callable<Map<Integer, Collection<String>>> mapFactory = new Callable<Map<Integer, Collection<String>>>() {
            @Override
            public Map<Integer, Collection<String>> call() {
                return new LinkedHashMap<Integer, Collection<String>>() {
                    private static final long serialVersionUID = -2084477070717362859L;

                    @Override
                    protected boolean removeEldestEntry(Map.Entry<Integer, Collection<String>> eldest) {
                        return (size()) > 2;
                    }
                };
            }
        };
        Function<String, String> identity = new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        };
        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, identity, mapFactory, new Function<Integer, Collection<String>>() {
            @Override
            public Collection<String> apply(Integer v) {
                return new ArrayList<String>();
            }
        }).toObservable();
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(2, Arrays.asList("cc", "dd"));
        expected.put(3, Arrays.asList("eee", "fff"));
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectObserver, Mockito.times(1)).onNext(expected);
        Mockito.verify(objectObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testToMultimapWithCollectionFactoryObservable() {
        Observable<String> source = just("cc", "dd", "eee", "eee");
        Function<Integer, Collection<String>> collectionFactory = new Function<Integer, Collection<String>>() {
            @Override
            public Collection<String> apply(Integer t1) {
                if (t1 == 2) {
                    return new ArrayList<String>();
                } else {
                    return new HashSet<String>();
                }
            }
        };
        Function<String, String> identity = new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        };
        Callable<Map<Integer, Collection<String>>> mapSupplier = new Callable<Map<Integer, Collection<String>>>() {
            @Override
            public Map<Integer, Collection<String>> call() {
                return new HashMap<Integer, Collection<String>>();
            }
        };
        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, identity, mapSupplier, collectionFactory).toObservable();
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(2, Arrays.asList("cc", "dd"));
        expected.put(3, new HashSet<String>(Arrays.asList("eee")));
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectObserver, Mockito.times(1)).onNext(expected);
        Mockito.verify(objectObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testToMultimapWithErrorObservable() {
        Observable<String> source = just("a", "b", "cc", "dd");
        Function<String, Integer> lengthFuncErr = new Function<String, Integer>() {
            @Override
            public Integer apply(String t1) {
                if ("b".equals(t1)) {
                    throw new RuntimeException("Forced Failure");
                }
                return t1.length();
            }
        };
        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFuncErr).toObservable();
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(1, Arrays.asList("a", "b"));
        expected.put(2, Arrays.asList("cc", "dd"));
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectObserver, Mockito.never()).onNext(expected);
        Mockito.verify(objectObserver, Mockito.never()).onComplete();
    }

    @Test
    public void testToMultimapWithErrorInValueSelectorObservable() {
        Observable<String> source = just("a", "b", "cc", "dd");
        Function<String, String> duplicateErr = new Function<String, String>() {
            @Override
            public String apply(String t1) {
                if ("b".equals(t1)) {
                    throw new RuntimeException("Forced failure");
                }
                return t1 + t1;
            }
        };
        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, duplicateErr).toObservable();
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(1, Arrays.asList("aa", "bb"));
        expected.put(2, Arrays.asList("cccc", "dddd"));
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectObserver, Mockito.never()).onNext(expected);
        Mockito.verify(objectObserver, Mockito.never()).onComplete();
    }

    @Test
    public void testToMultimapWithMapThrowingFactoryObservable() {
        Observable<String> source = Observable.just("a", "b", "cc", "dd", "eee", "fff");
        Callable<Map<Integer, Collection<String>>> mapFactory = new Callable<Map<Integer, Collection<String>>>() {
            @Override
            public Map<Integer, Collection<String>> call() {
                throw new RuntimeException("Forced failure");
            }
        };
        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        }, mapFactory).toObservable();
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(2, Arrays.asList("cc", "dd"));
        expected.put(3, Arrays.asList("eee", "fff"));
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectObserver, Mockito.never()).onNext(expected);
        Mockito.verify(objectObserver, Mockito.never()).onComplete();
    }

    @Test
    public void testToMultimapWithThrowingCollectionFactoryObservable() {
        Observable<String> source = just("cc", "cc", "eee", "eee");
        Function<Integer, Collection<String>> collectionFactory = new Function<Integer, Collection<String>>() {
            @Override
            public Collection<String> apply(Integer t1) {
                if (t1 == 2) {
                    throw new RuntimeException("Forced failure");
                } else {
                    return new HashSet<String>();
                }
            }
        };
        Function<String, String> identity = new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        };
        Callable<Map<Integer, Collection<String>>> mapSupplier = new Callable<Map<Integer, Collection<String>>>() {
            @Override
            public Map<Integer, Collection<String>> call() {
                return new HashMap<Integer, Collection<String>>();
            }
        };
        Observable<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, identity, mapSupplier, collectionFactory).toObservable();
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(2, Arrays.asList("cc", "dd"));
        expected.put(3, Collections.singleton("eee"));
        mapped.subscribe(objectObserver);
        Mockito.verify(objectObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(objectObserver, Mockito.never()).onNext(expected);
        Mockito.verify(objectObserver, Mockito.never()).onComplete();
    }

    @Test
    public void testToMultimap() {
        Observable<String> source = just("a", "b", "cc", "dd");
        Single<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc);
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(1, Arrays.asList("a", "b"));
        expected.put(2, Arrays.asList("cc", "dd"));
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(singleObserver, Mockito.times(1)).onSuccess(expected);
    }

    @Test
    public void testToMultimapWithValueSelector() {
        Observable<String> source = just("a", "b", "cc", "dd");
        Single<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, duplicate);
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(1, Arrays.asList("aa", "bb"));
        expected.put(2, Arrays.asList("cccc", "dddd"));
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(singleObserver, Mockito.times(1)).onSuccess(expected);
    }

    @Test
    public void testToMultimapWithMapFactory() {
        Observable<String> source = Observable.just("a", "b", "cc", "dd", "eee", "fff");
        Callable<Map<Integer, Collection<String>>> mapFactory = new Callable<Map<Integer, Collection<String>>>() {
            @Override
            public Map<Integer, Collection<String>> call() {
                return new LinkedHashMap<Integer, Collection<String>>() {
                    private static final long serialVersionUID = -2084477070717362859L;

                    @Override
                    protected boolean removeEldestEntry(Map.Entry<Integer, Collection<String>> eldest) {
                        return (size()) > 2;
                    }
                };
            }
        };
        Function<String, String> identity = new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        };
        Single<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, identity, mapFactory, new Function<Integer, Collection<String>>() {
            @Override
            public Collection<String> apply(Integer v) {
                return new ArrayList<String>();
            }
        });
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(2, Arrays.asList("cc", "dd"));
        expected.put(3, Arrays.asList("eee", "fff"));
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(singleObserver, Mockito.times(1)).onSuccess(expected);
    }

    @Test
    public void testToMultimapWithCollectionFactory() {
        Observable<String> source = just("cc", "dd", "eee", "eee");
        Function<Integer, Collection<String>> collectionFactory = new Function<Integer, Collection<String>>() {
            @Override
            public Collection<String> apply(Integer t1) {
                if (t1 == 2) {
                    return new ArrayList<String>();
                } else {
                    return new HashSet<String>();
                }
            }
        };
        Function<String, String> identity = new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        };
        Callable<Map<Integer, Collection<String>>> mapSupplier = new Callable<Map<Integer, Collection<String>>>() {
            @Override
            public Map<Integer, Collection<String>> call() {
                return new HashMap<Integer, Collection<String>>();
            }
        };
        Single<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, identity, mapSupplier, collectionFactory);
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(2, Arrays.asList("cc", "dd"));
        expected.put(3, new HashSet<String>(Arrays.asList("eee")));
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(singleObserver, Mockito.times(1)).onSuccess(expected);
    }

    @Test
    public void testToMultimapWithError() {
        Observable<String> source = just("a", "b", "cc", "dd");
        Function<String, Integer> lengthFuncErr = new Function<String, Integer>() {
            @Override
            public Integer apply(String t1) {
                if ("b".equals(t1)) {
                    throw new RuntimeException("Forced Failure");
                }
                return t1.length();
            }
        };
        Single<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFuncErr);
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(1, Arrays.asList("a", "b"));
        expected.put(2, Arrays.asList("cc", "dd"));
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(singleObserver, Mockito.never()).onSuccess(expected);
    }

    @Test
    public void testToMultimapWithErrorInValueSelector() {
        Observable<String> source = just("a", "b", "cc", "dd");
        Function<String, String> duplicateErr = new Function<String, String>() {
            @Override
            public String apply(String t1) {
                if ("b".equals(t1)) {
                    throw new RuntimeException("Forced failure");
                }
                return t1 + t1;
            }
        };
        Single<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, duplicateErr);
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(1, Arrays.asList("aa", "bb"));
        expected.put(2, Arrays.asList("cccc", "dddd"));
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(singleObserver, Mockito.never()).onSuccess(expected);
    }

    @Test
    public void testToMultimapWithMapThrowingFactory() {
        Observable<String> source = Observable.just("a", "b", "cc", "dd", "eee", "fff");
        Callable<Map<Integer, Collection<String>>> mapFactory = new Callable<Map<Integer, Collection<String>>>() {
            @Override
            public Map<Integer, Collection<String>> call() {
                throw new RuntimeException("Forced failure");
            }
        };
        Single<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        }, mapFactory);
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(2, Arrays.asList("cc", "dd"));
        expected.put(3, Arrays.asList("eee", "fff"));
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(singleObserver, Mockito.never()).onSuccess(expected);
    }

    @Test
    public void testToMultimapWithThrowingCollectionFactory() {
        Observable<String> source = just("cc", "cc", "eee", "eee");
        Function<Integer, Collection<String>> collectionFactory = new Function<Integer, Collection<String>>() {
            @Override
            public Collection<String> apply(Integer t1) {
                if (t1 == 2) {
                    throw new RuntimeException("Forced failure");
                } else {
                    return new HashSet<String>();
                }
            }
        };
        Function<String, String> identity = new Function<String, String>() {
            @Override
            public String apply(String v) {
                return v;
            }
        };
        Callable<Map<Integer, Collection<String>>> mapSupplier = new Callable<Map<Integer, Collection<String>>>() {
            @Override
            public Map<Integer, Collection<String>> call() {
                return new HashMap<Integer, Collection<String>>();
            }
        };
        Single<Map<Integer, Collection<String>>> mapped = source.toMultimap(lengthFunc, identity, mapSupplier, collectionFactory);
        Map<Integer, Collection<String>> expected = new HashMap<Integer, Collection<String>>();
        expected.put(2, Arrays.asList("cc", "dd"));
        expected.put(3, Collections.singleton("eee"));
        mapped.subscribe(singleObserver);
        Mockito.verify(singleObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(singleObserver, Mockito.never()).onSuccess(expected);
    }
}

