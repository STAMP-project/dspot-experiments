/**
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.lifecycle;


import Lifecycle.Event.ON_DESTROY;
import Lifecycle.Event.ON_RESUME;
import Lifecycle.State.DESTROYED;
import Lifecycle.State.RESUMED;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static Event.ON_CREATE;
import static Event.ON_DESTROY;
import static Event.ON_PAUSE;
import static Event.ON_RESUME;
import static Event.ON_START;
import static Event.ON_STOP;


@RunWith(JUnit4.class)
public class LifecycleRegistryTest {
    private LifecycleOwner mLifecycleOwner;

    private Lifecycle mLifecycle;

    private LifecycleRegistry mRegistry;

    @Test
    public void getCurrentState() {
        mRegistry.handleLifecycleEvent(ON_RESUME);
        MatcherAssert.assertThat(mRegistry.getCurrentState(), CoreMatchers.is(RESUMED));
        mRegistry.handleLifecycleEvent(ON_DESTROY);
        MatcherAssert.assertThat(mRegistry.getCurrentState(), CoreMatchers.is(DESTROYED));
    }

    @Test
    public void setCurrentState() {
        mRegistry.setCurrentState(RESUMED);
        MatcherAssert.assertThat(mRegistry.getCurrentState(), CoreMatchers.is(RESUMED));
        mRegistry.setCurrentState(DESTROYED);
        MatcherAssert.assertThat(mRegistry.getCurrentState(), CoreMatchers.is(DESTROYED));
    }

    @Test
    public void addRemove() {
        LifecycleObserver observer = Mockito.mock(LifecycleObserver.class);
        mRegistry.addObserver(observer);
        MatcherAssert.assertThat(mRegistry.getObserverCount(), CoreMatchers.is(1));
        mRegistry.removeObserver(observer);
        MatcherAssert.assertThat(mRegistry.getObserverCount(), CoreMatchers.is(0));
    }

    @Test
    public void addGenericAndObserve() {
        GenericLifecycleObserver generic = Mockito.mock(GenericLifecycleObserver.class);
        mRegistry.addObserver(generic);
        dispatchEvent(Event.ON_CREATE);
        Mockito.verify(generic).onStateChanged(mLifecycleOwner, Event.ON_CREATE);
        Mockito.reset(generic);
        dispatchEvent(Event.ON_CREATE);
        Mockito.verify(generic, Mockito.never()).onStateChanged(mLifecycleOwner, Event.ON_CREATE);
    }

    @Test
    public void addRegularClass() {
        LifecycleRegistryTest.TestObserver testObserver = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        mRegistry.addObserver(testObserver);
        dispatchEvent(Event.ON_START);
        Mockito.verify(testObserver, Mockito.never()).onStop();
        dispatchEvent(Event.ON_STOP);
        Mockito.verify(testObserver).onStop();
    }

    @Test
    public void add2RemoveOne() {
        LifecycleRegistryTest.TestObserver observer1 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        LifecycleRegistryTest.TestObserver observer2 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        LifecycleRegistryTest.TestObserver observer3 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        mRegistry.addObserver(observer1);
        mRegistry.addObserver(observer2);
        mRegistry.addObserver(observer3);
        dispatchEvent(Event.ON_CREATE);
        Mockito.verify(observer1).onCreate();
        Mockito.verify(observer2).onCreate();
        Mockito.verify(observer3).onCreate();
        Mockito.reset(observer1, observer2, observer3);
        mRegistry.removeObserver(observer2);
        dispatchEvent(Event.ON_START);
        Mockito.verify(observer1).onStart();
        Mockito.verify(observer2, Mockito.never()).onStart();
        Mockito.verify(observer3).onStart();
    }

    @Test
    public void removeWhileTraversing() {
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        LifecycleRegistryTest.TestObserver observer1 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            public void onCreate() {
                mRegistry.removeObserver(observer2);
            }
        });
        mRegistry.addObserver(observer1);
        mRegistry.addObserver(observer2);
        dispatchEvent(Event.ON_CREATE);
        Mockito.verify(observer2, Mockito.never()).onCreate();
        Mockito.verify(observer1).onCreate();
    }

    @Test
    public void constructionOrder() {
        fullyInitializeRegistry();
        final LifecycleRegistryTest.TestObserver observer = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        mRegistry.addObserver(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer).onCreate();
        inOrder.verify(observer).onStart();
        inOrder.verify(observer).onResume();
    }

    @Test
    public void constructionDestruction1() {
        fullyInitializeRegistry();
        final LifecycleRegistryTest.TestObserver observer = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onStart() {
                dispatchEvent(Event.ON_PAUSE);
            }
        });
        mRegistry.addObserver(observer);
        InOrder constructionOrder = Mockito.inOrder(observer);
        constructionOrder.verify(observer).onCreate();
        constructionOrder.verify(observer).onStart();
        constructionOrder.verify(observer, Mockito.never()).onResume();
    }

    @Test
    public void constructionDestruction2() {
        fullyInitializeRegistry();
        final LifecycleRegistryTest.TestObserver observer = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onStart() {
                dispatchEvent(Event.ON_PAUSE);
                dispatchEvent(Event.ON_STOP);
                dispatchEvent(Event.ON_DESTROY);
            }
        });
        mRegistry.addObserver(observer);
        InOrder orderVerifier = Mockito.inOrder(observer);
        orderVerifier.verify(observer).onCreate();
        orderVerifier.verify(observer).onStart();
        orderVerifier.verify(observer).onStop();
        orderVerifier.verify(observer).onDestroy();
        orderVerifier.verify(observer, Mockito.never()).onResume();
    }

    @Test
    public void twoObserversChangingState() {
        final LifecycleRegistryTest.TestObserver observer1 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onCreate() {
                dispatchEvent(Event.ON_START);
            }
        });
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        mRegistry.addObserver(observer1);
        mRegistry.addObserver(observer2);
        dispatchEvent(Event.ON_CREATE);
        Mockito.verify(observer1, Mockito.times(1)).onCreate();
        Mockito.verify(observer2, Mockito.times(1)).onCreate();
        Mockito.verify(observer1, Mockito.times(1)).onStart();
        Mockito.verify(observer2, Mockito.times(1)).onStart();
    }

    @Test
    public void addDuringTraversing() {
        final LifecycleRegistryTest.TestObserver observer3 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer1 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            public void onStart() {
                mRegistry.addObserver(observer3);
            }
        });
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        mRegistry.addObserver(observer1);
        mRegistry.addObserver(observer2);
        dispatchEvent(Event.ON_CREATE);
        dispatchEvent(Event.ON_START);
        InOrder inOrder = Mockito.inOrder(observer1, observer2, observer3);
        inOrder.verify(observer1).onCreate();
        inOrder.verify(observer2).onCreate();
        inOrder.verify(observer1).onStart();
        inOrder.verify(observer3).onCreate();
        inOrder.verify(observer2).onStart();
        inOrder.verify(observer3).onStart();
    }

    @Test
    public void addDuringAddition() {
        final LifecycleRegistryTest.TestObserver observer3 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            public void onCreate() {
                mRegistry.addObserver(observer3);
            }
        });
        final LifecycleRegistryTest.TestObserver observer1 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            public void onResume() {
                mRegistry.addObserver(observer2);
            }
        });
        mRegistry.addObserver(observer1);
        dispatchEvent(Event.ON_CREATE);
        dispatchEvent(Event.ON_START);
        dispatchEvent(Event.ON_RESUME);
        InOrder inOrder = Mockito.inOrder(observer1, observer2, observer3);
        inOrder.verify(observer1).onCreate();
        inOrder.verify(observer1).onStart();
        inOrder.verify(observer1).onResume();
        inOrder.verify(observer2).onCreate();
        inOrder.verify(observer2).onStart();
        inOrder.verify(observer2).onResume();
        inOrder.verify(observer3).onCreate();
        inOrder.verify(observer3).onStart();
        inOrder.verify(observer3).onResume();
    }

    @Test
    public void subscribeToDead() {
        dispatchEvent(Event.ON_CREATE);
        final LifecycleRegistryTest.TestObserver observer1 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        mRegistry.addObserver(observer1);
        Mockito.verify(observer1).onCreate();
        dispatchEvent(Event.ON_DESTROY);
        Mockito.verify(observer1).onDestroy();
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        mRegistry.addObserver(observer2);
        Mockito.verify(observer2, Mockito.never()).onCreate();
        Mockito.reset(observer1);
        dispatchEvent(Event.ON_CREATE);
        Mockito.verify(observer1).onCreate();
        Mockito.verify(observer2).onCreate();
    }

    @Test
    public void downEvents() {
        fullyInitializeRegistry();
        final LifecycleRegistryTest.TestObserver observer1 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        mRegistry.addObserver(observer1);
        mRegistry.addObserver(observer2);
        InOrder orderVerifier = Mockito.inOrder(observer1, observer2);
        dispatchEvent(Event.ON_PAUSE);
        orderVerifier.verify(observer2).onPause();
        orderVerifier.verify(observer1).onPause();
        dispatchEvent(Event.ON_STOP);
        orderVerifier.verify(observer2).onStop();
        orderVerifier.verify(observer1).onStop();
        dispatchEvent(Event.ON_DESTROY);
        orderVerifier.verify(observer2).onDestroy();
        orderVerifier.verify(observer1).onDestroy();
    }

    @Test
    public void downEventsAddition() {
        dispatchEvent(Event.ON_CREATE);
        dispatchEvent(Event.ON_START);
        final LifecycleRegistryTest.TestObserver observer1 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer3 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onStop() {
                mRegistry.addObserver(observer3);
            }
        });
        mRegistry.addObserver(observer1);
        mRegistry.addObserver(observer2);
        InOrder orderVerifier = Mockito.inOrder(observer1, observer2, observer3);
        dispatchEvent(Event.ON_STOP);
        orderVerifier.verify(observer2).onStop();
        orderVerifier.verify(observer3).onCreate();
        orderVerifier.verify(observer1).onStop();
        dispatchEvent(Event.ON_DESTROY);
        orderVerifier.verify(observer3).onDestroy();
        orderVerifier.verify(observer2).onDestroy();
        orderVerifier.verify(observer1).onDestroy();
    }

    @Test
    public void downEventsRemoveAll() {
        fullyInitializeRegistry();
        final LifecycleRegistryTest.TestObserver observer1 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer3 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onStop() {
                mRegistry.removeObserver(observer3);
                mRegistry.removeObserver(this);
                mRegistry.removeObserver(observer1);
                MatcherAssert.assertThat(mRegistry.getObserverCount(), CoreMatchers.is(0));
            }
        });
        mRegistry.addObserver(observer1);
        mRegistry.addObserver(observer2);
        mRegistry.addObserver(observer3);
        InOrder orderVerifier = Mockito.inOrder(observer1, observer2, observer3);
        dispatchEvent(Event.ON_PAUSE);
        orderVerifier.verify(observer3).onPause();
        orderVerifier.verify(observer2).onPause();
        orderVerifier.verify(observer1).onPause();
        dispatchEvent(Event.ON_STOP);
        orderVerifier.verify(observer3).onStop();
        orderVerifier.verify(observer2).onStop();
        orderVerifier.verify(observer1, Mockito.never()).onStop();
        dispatchEvent(Event.ON_PAUSE);
        orderVerifier.verify(observer3, Mockito.never()).onPause();
        orderVerifier.verify(observer2, Mockito.never()).onPause();
        orderVerifier.verify(observer1, Mockito.never()).onPause();
    }

    @Test
    public void deadParentInAddition() {
        fullyInitializeRegistry();
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer3 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        LifecycleRegistryTest.TestObserver observer1 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onStart() {
                mRegistry.removeObserver(this);
                MatcherAssert.assertThat(mRegistry.getObserverCount(), CoreMatchers.is(0));
                mRegistry.addObserver(observer2);
                mRegistry.addObserver(observer3);
            }
        });
        mRegistry.addObserver(observer1);
        InOrder inOrder = Mockito.inOrder(observer1, observer2, observer3);
        inOrder.verify(observer1).onCreate();
        inOrder.verify(observer1).onStart();
        inOrder.verify(observer2).onCreate();
        inOrder.verify(observer3).onCreate();
        inOrder.verify(observer2).onStart();
        inOrder.verify(observer2).onResume();
        inOrder.verify(observer3).onStart();
        inOrder.verify(observer3).onResume();
    }

    @Test
    public void deadParentWhileTraversing() {
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer3 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        LifecycleRegistryTest.TestObserver observer1 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onStart() {
                mRegistry.removeObserver(this);
                MatcherAssert.assertThat(mRegistry.getObserverCount(), CoreMatchers.is(0));
                mRegistry.addObserver(observer2);
                mRegistry.addObserver(observer3);
            }
        });
        InOrder inOrder = Mockito.inOrder(observer1, observer2, observer3);
        mRegistry.addObserver(observer1);
        dispatchEvent(Event.ON_CREATE);
        dispatchEvent(Event.ON_START);
        inOrder.verify(observer1).onCreate();
        inOrder.verify(observer1).onStart();
        inOrder.verify(observer2).onCreate();
        inOrder.verify(observer3).onCreate();
        inOrder.verify(observer2).onStart();
        inOrder.verify(observer3).onStart();
    }

    @Test
    public void removeCascade() {
        final LifecycleRegistryTest.TestObserver observer3 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer4 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onStart() {
                mRegistry.removeObserver(this);
            }
        });
        LifecycleRegistryTest.TestObserver observer1 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onResume() {
                mRegistry.removeObserver(this);
                mRegistry.addObserver(observer2);
                mRegistry.addObserver(observer3);
                mRegistry.addObserver(observer4);
            }
        });
        fullyInitializeRegistry();
        mRegistry.addObserver(observer1);
        InOrder inOrder = Mockito.inOrder(observer1, observer2, observer3, observer4);
        inOrder.verify(observer1).onCreate();
        inOrder.verify(observer1).onStart();
        inOrder.verify(observer1).onResume();
        inOrder.verify(observer2).onCreate();
        inOrder.verify(observer2).onStart();
        inOrder.verify(observer3).onCreate();
        inOrder.verify(observer3).onStart();
        inOrder.verify(observer4).onCreate();
        inOrder.verify(observer4).onStart();
        inOrder.verify(observer3).onResume();
        inOrder.verify(observer4).onResume();
    }

    @Test
    public void changeStateDuringDescending() {
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer1 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onPause() {
                // but tonight I bounce back
                mRegistry.handleLifecycleEvent(Event.ON_RESUME);
                mRegistry.addObserver(observer2);
            }
        });
        fullyInitializeRegistry();
        mRegistry.addObserver(observer1);
        mRegistry.handleLifecycleEvent(Event.ON_PAUSE);
        InOrder inOrder = Mockito.inOrder(observer1, observer2);
        inOrder.verify(observer1).onPause();
        inOrder.verify(observer2).onCreate();
        inOrder.verify(observer2).onStart();
        inOrder.verify(observer1).onResume();
        inOrder.verify(observer2).onResume();
    }

    @Test
    public void siblingLimitationCheck() {
        fullyInitializeRegistry();
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer3 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer1 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onStart() {
                mRegistry.addObserver(observer2);
            }

            @Override
            void onResume() {
                mRegistry.addObserver(observer3);
            }
        });
        mRegistry.addObserver(observer1);
        InOrder inOrder = Mockito.inOrder(observer1, observer2, observer3);
        inOrder.verify(observer1).onCreate();
        inOrder.verify(observer1).onStart();
        inOrder.verify(observer2).onCreate();
        inOrder.verify(observer1).onResume();
        inOrder.verify(observer3).onCreate();
        inOrder.verify(observer2).onStart();
        inOrder.verify(observer2).onResume();
        inOrder.verify(observer3).onStart();
        inOrder.verify(observer3).onResume();
    }

    @Test
    public void siblingRemovalLimitationCheck1() {
        fullyInitializeRegistry();
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer3 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer4 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer1 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onStart() {
                mRegistry.addObserver(observer2);
            }

            @Override
            void onResume() {
                mRegistry.removeObserver(observer2);
                mRegistry.addObserver(observer3);
                mRegistry.addObserver(observer4);
            }
        });
        mRegistry.addObserver(observer1);
        InOrder inOrder = Mockito.inOrder(observer1, observer2, observer3, observer4);
        inOrder.verify(observer1).onCreate();
        inOrder.verify(observer1).onStart();
        inOrder.verify(observer2).onCreate();
        inOrder.verify(observer1).onResume();
        inOrder.verify(observer3).onCreate();
        inOrder.verify(observer3).onStart();
        inOrder.verify(observer4).onCreate();
        inOrder.verify(observer4).onStart();
        inOrder.verify(observer3).onResume();
        inOrder.verify(observer4).onResume();
    }

    @Test
    public void siblingRemovalLimitationCheck2() {
        fullyInitializeRegistry();
        final LifecycleRegistryTest.TestObserver observer2 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer3 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onCreate() {
                mRegistry.removeObserver(observer2);
            }
        });
        final LifecycleRegistryTest.TestObserver observer4 = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        final LifecycleRegistryTest.TestObserver observer1 = Mockito.spy(new LifecycleRegistryTest.TestObserver() {
            @Override
            void onStart() {
                mRegistry.addObserver(observer2);
            }

            @Override
            void onResume() {
                mRegistry.addObserver(observer3);
                mRegistry.addObserver(observer4);
            }
        });
        mRegistry.addObserver(observer1);
        InOrder inOrder = Mockito.inOrder(observer1, observer2, observer3, observer4);
        inOrder.verify(observer1).onCreate();
        inOrder.verify(observer1).onStart();
        inOrder.verify(observer2).onCreate();
        inOrder.verify(observer1).onResume();
        inOrder.verify(observer3).onCreate();
        inOrder.verify(observer3).onStart();
        inOrder.verify(observer4).onCreate();
        inOrder.verify(observer4).onStart();
        inOrder.verify(observer3).onResume();
        inOrder.verify(observer4).onResume();
    }

    @Test
    public void sameObserverReAddition() {
        LifecycleRegistryTest.TestObserver observer = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        mRegistry.addObserver(observer);
        mRegistry.removeObserver(observer);
        mRegistry.addObserver(observer);
        dispatchEvent(Event.ON_CREATE);
        Mockito.verify(observer).onCreate();
    }

    @Test
    public void goneLifecycleOwner() {
        fullyInitializeRegistry();
        mLifecycleOwner = null;
        LifecycleRegistryTest.forceGc();
        LifecycleRegistryTest.TestObserver observer = Mockito.mock(LifecycleRegistryTest.TestObserver.class);
        mRegistry.addObserver(observer);
        Mockito.verify(observer, Mockito.never()).onCreate();
        Mockito.verify(observer, Mockito.never()).onStart();
        Mockito.verify(observer, Mockito.never()).onResume();
    }

    private abstract class TestObserver implements LifecycleObserver {
        @OnLifecycleEvent(ON_CREATE)
        void onCreate() {
        }

        @OnLifecycleEvent(ON_START)
        void onStart() {
        }

        @OnLifecycleEvent(ON_RESUME)
        void onResume() {
        }

        @OnLifecycleEvent(ON_PAUSE)
        void onPause() {
        }

        @OnLifecycleEvent(ON_STOP)
        void onStop() {
        }

        @OnLifecycleEvent(ON_DESTROY)
        void onDestroy() {
        }
    }
}

