/**
 * Copyright 2016 Victor Albertos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rx_cache2.internal;


import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.rx_cache2.ActionsList;
import io.rx_cache2.EvictProvider;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ActionsListTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ProcessorProviders processProvider;

    @Test
    public void Add_All() {
        checkInitialState();
        addAll(10);
    }

    @Test
    public void Add_All_First() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).addAllFirst(Arrays.asList(new Mock("11"), new Mock("12"))).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(12));
        MatcherAssert.assertThat(mocks.get(0).getMessage(), CoreMatchers.is("11"));
        MatcherAssert.assertThat(mocks.get(1).getMessage(), CoreMatchers.is("12"));
    }

    @Test
    public void Add_All_Last() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).addAllLast(Arrays.asList(new Mock("11"), new Mock("12"))).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(12));
        MatcherAssert.assertThat(mocks.get(10).getMessage(), CoreMatchers.is("11"));
        MatcherAssert.assertThat(mocks.get(11).getMessage(), CoreMatchers.is("12"));
    }

    @Test
    public void Add_First() {
        checkInitialState();
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).addFirst(new Mock("1")).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(mocks.get(0).getMessage(), CoreMatchers.is("1"));
    }

    @Test
    public void Add_Last() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).addLast(new Mock("11")).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(11));
        MatcherAssert.assertThat(mocks.get(10).getMessage(), CoreMatchers.is("11"));
    }

    @Test
    public void Add() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).add(new ActionsList.Func2() {
            @Override
            public boolean call(int position, int count) {
                return position == 5;
            }
        }, new Mock("6_added")).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(11));
        MatcherAssert.assertThat(mocks.get(5).getMessage(), CoreMatchers.is("6_added"));
    }

    @Test
    public void EvictFirst() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictFirst().toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(9));
        MatcherAssert.assertThat(mocks.get(0).getMessage(), CoreMatchers.is("1"));
    }

    @Test
    public void EvictFirstN() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictFirstN(4).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(6));
        MatcherAssert.assertThat(mocks.get(0).getMessage(), CoreMatchers.is("4"));
    }

    @Test
    public void EvictFirstExposingCount() {
        checkInitialState();
        addAll(10);
        // do not evict
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictFirst(new ActionsList.Func1Count() {
            @Override
            public boolean call(int count) {
                return count > 10;
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(10));
        // evict
        testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictFirst(new ActionsList.Func1Count() {
            @Override
            public boolean call(int count) {
                return count > 9;
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(9));
        MatcherAssert.assertThat(mocks.get(0).getMessage(), CoreMatchers.is("1"));
    }

    @Test
    public void EvictFirstNExposingCount() {
        checkInitialState();
        addAll(10);
        // do not evict
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictFirstN(new ActionsList.Func1Count() {
            @Override
            public boolean call(int count) {
                return count > 10;
            }
        }, 5).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(10));
        // evict
        testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictFirstN(new ActionsList.Func1Count() {
            @Override
            public boolean call(int count) {
                return count > 9;
            }
        }, 5).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(5));
        MatcherAssert.assertThat(mocks.get(0).getMessage(), CoreMatchers.is("5"));
        MatcherAssert.assertThat(mocks.get(1).getMessage(), CoreMatchers.is("6"));
    }

    @Test
    public void EvictLast() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictLast().toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(9));
        MatcherAssert.assertThat(mocks.get(8).getMessage(), CoreMatchers.is("8"));
    }

    @Test
    public void EvictLastN() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictLastN(4).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(6));
        MatcherAssert.assertThat(mocks.get(0).getMessage(), CoreMatchers.is("0"));
    }

    @Test
    public void EvictLastExposingCount() {
        checkInitialState();
        addAll(10);
        // do not evict
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictLast(new ActionsList.Func1Count() {
            @Override
            public boolean call(int count) {
                return count > 10;
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(10));
        // evict
        testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictLast(new ActionsList.Func1Count() {
            @Override
            public boolean call(int count) {
                return count > 9;
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(9));
        MatcherAssert.assertThat(mocks.get(8).getMessage(), CoreMatchers.is("8"));
    }

    @Test
    public void EvictLastNExposingCount() {
        checkInitialState();
        addAll(10);
        // do not evict
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictLastN(new ActionsList.Func1Count() {
            @Override
            public boolean call(int count) {
                return count > 10;
            }
        }, 5).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(10));
        // evict
        testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictLastN(new ActionsList.Func1Count() {
            @Override
            public boolean call(int count) {
                return count > 9;
            }
        }, 5).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(5));
        MatcherAssert.assertThat(mocks.get(0).getMessage(), CoreMatchers.is("0"));
        MatcherAssert.assertThat(mocks.get(1).getMessage(), CoreMatchers.is("1"));
    }

    @Test
    public void EvictExposingElementCurrentIteration() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        evict(new ActionsList.Func1Element<Mock>() {
            @Override
            public boolean call(Mock element) {
                return element.getMessage().equals("3");
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(9));
        MatcherAssert.assertThat(mocks.get(3).getMessage(), CoreMatchers.is("4"));
    }

    @Test
    public void EvictExposingCountAndPositionAndElementCurrentIteration() {
        checkInitialState();
        addAll(10);
        // do not evict
        TestObserver<List<Mock>> testObserver = new TestObserver();
        evict(new ActionsList.Func3<Mock>() {
            @Override
            public boolean call(int position, int count, Mock element) {
                return (count > 10) && (element.getMessage().equals("3"));
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(10));
        MatcherAssert.assertThat(mocks.get(3).getMessage(), CoreMatchers.is("3"));
        // evict
        testObserver = new TestObserver();
        evict(new ActionsList.Func3<Mock>() {
            @Override
            public boolean call(int position, int count, Mock element) {
                return (count > 9) && (element.getMessage().equals("3"));
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(9));
        MatcherAssert.assertThat(mocks.get(3).getMessage(), CoreMatchers.is("4"));
    }

    @Test
    public void EvictIterable() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictIterable(new ActionsList.Func3<Mock>() {
            @Override
            public boolean call(int position, int count, Mock element) {
                return (element.getMessage().equals("2")) || (element.getMessage().equals("3"));
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(8));
        MatcherAssert.assertThat(mocks.get(2).getMessage(), CoreMatchers.is("4"));
        MatcherAssert.assertThat(mocks.get(3).getMessage(), CoreMatchers.is("5"));
    }

    @Test
    public void EvictAll() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictAll().toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(0));
    }

    @Test
    public void EvictAllKeepingFirstN() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictAllKeepingFirstN(3).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(mocks.get(0).getMessage(), CoreMatchers.is("0"));
    }

    @Test
    public void EvictAllKeepingLastN() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).evictAllKeepingLastN(7).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(7));
        MatcherAssert.assertThat(mocks.get(0).getMessage(), CoreMatchers.is("3"));
    }

    @Test
    public void UpdateExposingElementCurrentIteration() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).update(new ActionsList.Func1Element<Mock>() {
            @Override
            public boolean call(Mock element) {
                return element.getMessage().equals("5");
            }
        }, new ActionsList.Replace<Mock>() {
            @Override
            public Mock call(Mock element) {
                element.setMessage("5_updated");
                return element;
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(10));
        MatcherAssert.assertThat(mocks.get(5).getMessage(), CoreMatchers.is("5_updated"));
    }

    @Test
    public void UpdateExposingCountAndPositionAndElementCurrentIteration() {
        checkInitialState();
        addAll(10);
        // do not evict
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).update(new ActionsList.Func3<Mock>() {
            @Override
            public boolean call(int position, int count, Mock element) {
                return (count > 10) && (element.getMessage().equals("5"));
            }
        }, new ActionsList.Replace<Mock>() {
            @Override
            public Mock call(Mock element) {
                element.setMessage("5_updated");
                return element;
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(10));
        MatcherAssert.assertThat(mocks.get(5).getMessage(), CoreMatchers.is("5"));
        // evict
        testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).update(new ActionsList.Func3<Mock>() {
            @Override
            public boolean call(int position, int count, Mock element) {
                return (count > 9) && (element.getMessage().equals("5"));
            }
        }, new ActionsList.Replace<Mock>() {
            @Override
            public Mock call(Mock element) {
                element.setMessage("5_updated");
                return element;
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(10));
        MatcherAssert.assertThat(mocks.get(5).getMessage(), CoreMatchers.is("5_updated"));
    }

    @Test
    public void UpdateIterableExposingElementCurrentIteration() {
        checkInitialState();
        addAll(10);
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).updateIterable(new ActionsList.Func1Element<Mock>() {
            @Override
            public boolean call(Mock element) {
                return (element.getMessage().equals("5")) || (element.getMessage().equals("6"));
            }
        }, new ActionsList.Replace<Mock>() {
            @Override
            public Mock call(Mock element) {
                element.setMessage("5_or_6_updated");
                return element;
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(10));
        MatcherAssert.assertThat(mocks.get(5).getMessage(), CoreMatchers.is("5_or_6_updated"));
        MatcherAssert.assertThat(mocks.get(6).getMessage(), CoreMatchers.is("5_or_6_updated"));
    }

    @Test
    public void UpdateIterableExposingCountAndPositionAndElementCurrentIteration() {
        checkInitialState();
        addAll(10);
        // do not evict
        TestObserver<List<Mock>> testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).updateIterable(new ActionsList.Func3<Mock>() {
            @Override
            public boolean call(int position, int count, Mock element) {
                return (count > 10) && ((element.getMessage().equals("5")) || (element.getMessage().equals("6")));
            }
        }, new ActionsList.Replace<Mock>() {
            @Override
            public Mock call(Mock element) {
                element.setMessage("5_or_6_updated");
                return element;
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Mock> mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(10));
        MatcherAssert.assertThat(mocks.get(5).getMessage(), CoreMatchers.is("5"));
        MatcherAssert.assertThat(mocks.get(6).getMessage(), CoreMatchers.is("6"));
        // evict
        testObserver = new TestObserver();
        ActionsList.with(evict(), cache()).updateIterable(new ActionsList.Func3<Mock>() {
            @Override
            public boolean call(int position, int count, Mock element) {
                return (count > 9) && ((element.getMessage().equals("5")) || (element.getMessage().equals("6")));
            }
        }, new ActionsList.Replace<Mock>() {
            @Override
            public Mock call(Mock element) {
                element.setMessage("5_or_6_updated");
                return element;
            }
        }).toObservable().subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        mocks = testObserver.values().get(0);
        MatcherAssert.assertThat(mocks.size(), CoreMatchers.is(10));
        MatcherAssert.assertThat(mocks.get(5).getMessage(), CoreMatchers.is("5_or_6_updated"));
        MatcherAssert.assertThat(mocks.get(6).getMessage(), CoreMatchers.is("5_or_6_updated"));
    }

    public interface ProvidersActions {
        Observable<List<Mock>> mocks(Observable<List<Mock>> mocks, EvictProvider evictProvider);
    }
}

