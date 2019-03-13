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
package io.reactivex.disposables;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Action;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class CompositeDisposableTest {
    @Test
    public void testSuccess() {
        final AtomicInteger counter = new AtomicInteger();
        CompositeDisposable cd = new CompositeDisposable();
        cd.add(Disposables.fromRunnable(new Runnable() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        }));
        cd.add(Disposables.fromRunnable(new Runnable() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        }));
        cd.dispose();
        Assert.assertEquals(2, counter.get());
    }

    @Test(timeout = 1000)
    public void shouldUnsubscribeAll() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();
        final CompositeDisposable cd = new CompositeDisposable();
        final int count = 10;
        final CountDownLatch start = new CountDownLatch(1);
        for (int i = 0; i < count; i++) {
            cd.add(Disposables.fromRunnable(new Runnable() {
                @Override
                public void run() {
                    counter.incrementAndGet();
                }
            }));
        }
        final List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < count; i++) {
            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        start.await();
                        cd.dispose();
                    } catch (final InterruptedException e) {
                        Assert.fail(e.getMessage());
                    }
                }
            };
            t.start();
            threads.add(t);
        }
        start.countDown();
        for (final Thread t : threads) {
            t.join();
        }
        Assert.assertEquals(count, counter.get());
    }

    @Test
    public void testException() {
        final AtomicInteger counter = new AtomicInteger();
        CompositeDisposable cd = new CompositeDisposable();
        cd.add(Disposables.fromRunnable(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("failed on first one");
            }
        }));
        cd.add(Disposables.fromRunnable(new Runnable() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        }));
        try {
            cd.dispose();
            Assert.fail("Expecting an exception");
        } catch (RuntimeException e) {
            // we expect this
            Assert.assertEquals(e.getMessage(), "failed on first one");
        }
        // we should still have disposed to the second one
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testCompositeException() {
        final AtomicInteger counter = new AtomicInteger();
        CompositeDisposable cd = new CompositeDisposable();
        cd.add(Disposables.fromRunnable(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("failed on first one");
            }
        }));
        cd.add(Disposables.fromRunnable(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("failed on second one too");
            }
        }));
        cd.add(Disposables.fromRunnable(new Runnable() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        }));
        try {
            cd.dispose();
            Assert.fail("Expecting an exception");
        } catch (CompositeException e) {
            // we expect this
            Assert.assertEquals(e.getExceptions().size(), 2);
        }
        // we should still have disposed to the second one
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testRemoveUnsubscribes() {
        Disposable d1 = Disposables.empty();
        Disposable d2 = Disposables.empty();
        CompositeDisposable cd = new CompositeDisposable();
        cd.add(d1);
        cd.add(d2);
        cd.remove(d1);
        Assert.assertTrue(d1.isDisposed());
        Assert.assertFalse(d2.isDisposed());
    }

    @Test
    public void testClear() {
        Disposable d1 = Disposables.empty();
        Disposable d2 = Disposables.empty();
        CompositeDisposable cd = new CompositeDisposable();
        cd.add(d1);
        cd.add(d2);
        Assert.assertFalse(d1.isDisposed());
        Assert.assertFalse(d2.isDisposed());
        cd.clear();
        Assert.assertTrue(d1.isDisposed());
        Assert.assertTrue(d2.isDisposed());
        Assert.assertFalse(cd.isDisposed());
        Disposable d3 = Disposables.empty();
        cd.add(d3);
        cd.dispose();
        Assert.assertTrue(d3.isDisposed());
        Assert.assertTrue(cd.isDisposed());
    }

    @Test
    public void testUnsubscribeIdempotence() {
        final AtomicInteger counter = new AtomicInteger();
        CompositeDisposable cd = new CompositeDisposable();
        cd.add(Disposables.fromRunnable(new Runnable() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        }));
        cd.dispose();
        cd.dispose();
        cd.dispose();
        // we should have only disposed once
        Assert.assertEquals(1, counter.get());
    }

    @Test(timeout = 1000)
    public void testUnsubscribeIdempotenceConcurrently() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();
        final CompositeDisposable cd = new CompositeDisposable();
        final int count = 10;
        final CountDownLatch start = new CountDownLatch(1);
        cd.add(Disposables.fromRunnable(new Runnable() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        }));
        final List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < count; i++) {
            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        start.await();
                        cd.dispose();
                    } catch (final InterruptedException e) {
                        Assert.fail(e.getMessage());
                    }
                }
            };
            t.start();
            threads.add(t);
        }
        start.countDown();
        for (final Thread t : threads) {
            t.join();
        }
        // we should have only disposed once
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testTryRemoveIfNotIn() {
        CompositeDisposable cd = new CompositeDisposable();
        CompositeDisposable cd1 = new CompositeDisposable();
        CompositeDisposable cd2 = new CompositeDisposable();
        cd.add(cd1);
        cd.remove(cd1);
        cd.add(cd2);
        cd.remove(cd1);// try removing agian

    }

    @Test(expected = NullPointerException.class)
    public void testAddingNullDisposableIllegal() {
        CompositeDisposable cd = new CompositeDisposable();
        cd.add(null);
    }

    @Test
    public void initializeVarargs() {
        Disposable d1 = Disposables.empty();
        Disposable d2 = Disposables.empty();
        CompositeDisposable cd = new CompositeDisposable(d1, d2);
        Assert.assertEquals(2, cd.size());
        cd.clear();
        Assert.assertEquals(0, cd.size());
        Assert.assertTrue(d1.isDisposed());
        Assert.assertTrue(d2.isDisposed());
        Disposable d3 = Disposables.empty();
        Disposable d4 = Disposables.empty();
        cd = new CompositeDisposable(d3, d4);
        cd.dispose();
        Assert.assertTrue(d3.isDisposed());
        Assert.assertTrue(d4.isDisposed());
        Assert.assertEquals(0, cd.size());
    }

    @Test
    public void initializeIterable() {
        Disposable d1 = Disposables.empty();
        Disposable d2 = Disposables.empty();
        CompositeDisposable cd = new CompositeDisposable(Arrays.asList(d1, d2));
        Assert.assertEquals(2, cd.size());
        cd.clear();
        Assert.assertEquals(0, cd.size());
        Assert.assertTrue(d1.isDisposed());
        Assert.assertTrue(d2.isDisposed());
        Disposable d3 = Disposables.empty();
        Disposable d4 = Disposables.empty();
        cd = new CompositeDisposable(Arrays.asList(d3, d4));
        Assert.assertEquals(2, cd.size());
        cd.dispose();
        Assert.assertTrue(d3.isDisposed());
        Assert.assertTrue(d4.isDisposed());
        Assert.assertEquals(0, cd.size());
    }

    @Test
    public void addAll() {
        CompositeDisposable cd = new CompositeDisposable();
        Disposable d1 = Disposables.empty();
        Disposable d2 = Disposables.empty();
        Disposable d3 = Disposables.empty();
        cd.addAll(d1, d2);
        cd.addAll(d3);
        Assert.assertFalse(d1.isDisposed());
        Assert.assertFalse(d2.isDisposed());
        Assert.assertFalse(d3.isDisposed());
        cd.clear();
        Assert.assertTrue(d1.isDisposed());
        Assert.assertTrue(d2.isDisposed());
        d1 = Disposables.empty();
        d2 = Disposables.empty();
        cd = new CompositeDisposable();
        cd.addAll(d1, d2);
        Assert.assertFalse(d1.isDisposed());
        Assert.assertFalse(d2.isDisposed());
        cd.dispose();
        Assert.assertTrue(d1.isDisposed());
        Assert.assertTrue(d2.isDisposed());
        Assert.assertEquals(0, cd.size());
        cd.clear();
        Assert.assertEquals(0, cd.size());
    }

    @Test
    public void addAfterDisposed() {
        CompositeDisposable cd = new CompositeDisposable();
        cd.dispose();
        Disposable d1 = Disposables.empty();
        Assert.assertFalse(cd.add(d1));
        Assert.assertTrue(d1.isDisposed());
        d1 = Disposables.empty();
        Disposable d2 = Disposables.empty();
        Assert.assertFalse(cd.addAll(d1, d2));
        Assert.assertTrue(d1.isDisposed());
        Assert.assertTrue(d2.isDisposed());
    }

    @Test
    public void delete() {
        CompositeDisposable cd = new CompositeDisposable();
        Disposable d1 = Disposables.empty();
        Assert.assertFalse(cd.delete(d1));
        Disposable d2 = Disposables.empty();
        cd.add(d2);
        Assert.assertFalse(cd.delete(d1));
        cd.dispose();
        Assert.assertFalse(cd.delete(d1));
    }

    @Test
    public void disposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable cd = new CompositeDisposable();
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    cd.dispose();
                }
            };
            TestHelper.race(run, run);
        }
    }

    @Test
    public void addRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable cd = new CompositeDisposable();
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    cd.add(Disposables.empty());
                }
            };
            TestHelper.race(run, run);
        }
    }

    @Test
    public void addAllRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable cd = new CompositeDisposable();
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    cd.addAll(Disposables.empty());
                }
            };
            TestHelper.race(run, run);
        }
    }

    @Test
    public void removeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable cd = new CompositeDisposable();
            final Disposable d1 = Disposables.empty();
            cd.add(d1);
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    cd.remove(d1);
                }
            };
            TestHelper.race(run, run);
        }
    }

    @Test
    public void deleteRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable cd = new CompositeDisposable();
            final Disposable d1 = Disposables.empty();
            cd.add(d1);
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    cd.delete(d1);
                }
            };
            TestHelper.race(run, run);
        }
    }

    @Test
    public void clearRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable cd = new CompositeDisposable();
            final Disposable d1 = Disposables.empty();
            cd.add(d1);
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    cd.clear();
                }
            };
            TestHelper.race(run, run);
        }
    }

    @Test
    public void addDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable cd = new CompositeDisposable();
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    cd.dispose();
                }
            };
            Runnable run2 = new Runnable() {
                @Override
                public void run() {
                    cd.add(Disposables.empty());
                }
            };
            TestHelper.race(run, run2);
        }
    }

    @Test
    public void addAllDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable cd = new CompositeDisposable();
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    cd.dispose();
                }
            };
            Runnable run2 = new Runnable() {
                @Override
                public void run() {
                    cd.addAll(Disposables.empty());
                }
            };
            TestHelper.race(run, run2);
        }
    }

    @Test
    public void removeDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable cd = new CompositeDisposable();
            final Disposable d1 = Disposables.empty();
            cd.add(d1);
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    cd.dispose();
                }
            };
            Runnable run2 = new Runnable() {
                @Override
                public void run() {
                    cd.remove(d1);
                }
            };
            TestHelper.race(run, run2);
        }
    }

    @Test
    public void deleteDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable cd = new CompositeDisposable();
            final Disposable d1 = Disposables.empty();
            cd.add(d1);
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    cd.dispose();
                }
            };
            Runnable run2 = new Runnable() {
                @Override
                public void run() {
                    cd.delete(d1);
                }
            };
            TestHelper.race(run, run2);
        }
    }

    @Test
    public void clearDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable cd = new CompositeDisposable();
            final Disposable d1 = Disposables.empty();
            cd.add(d1);
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    cd.dispose();
                }
            };
            Runnable run2 = new Runnable() {
                @Override
                public void run() {
                    cd.clear();
                }
            };
            TestHelper.race(run, run2);
        }
    }

    @Test
    public void sizeDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable cd = new CompositeDisposable();
            final Disposable d1 = Disposables.empty();
            cd.add(d1);
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    cd.dispose();
                }
            };
            Runnable run2 = new Runnable() {
                @Override
                public void run() {
                    cd.size();
                }
            };
            TestHelper.race(run, run2);
        }
    }

    @Test
    public void disposeThrowsIAE() {
        CompositeDisposable cd = new CompositeDisposable();
        cd.add(Disposables.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                throw new IllegalArgumentException();
            }
        }));
        Disposable d1 = Disposables.empty();
        cd.add(d1);
        try {
            cd.dispose();
            Assert.fail("Failed to throw");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        Assert.assertTrue(d1.isDisposed());
    }

    @Test
    public void disposeThrowsError() {
        CompositeDisposable cd = new CompositeDisposable();
        cd.add(Disposables.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                throw new AssertionError();
            }
        }));
        Disposable d1 = Disposables.empty();
        cd.add(d1);
        try {
            cd.dispose();
            Assert.fail("Failed to throw");
        } catch (AssertionError ex) {
            // expected
        }
        Assert.assertTrue(d1.isDisposed());
    }

    @Test
    public void disposeThrowsCheckedException() {
        CompositeDisposable cd = new CompositeDisposable();
        cd.add(Disposables.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                throw new IOException();
            }
        }));
        Disposable d1 = Disposables.empty();
        cd.add(d1);
        try {
            cd.dispose();
            Assert.fail("Failed to throw");
        } catch (RuntimeException ex) {
            // expected
            if (!((ex.getCause()) instanceof IOException)) {
                Assert.fail(((ex.toString()) + " should have thrown RuntimeException(IOException)"));
            }
        }
        Assert.assertTrue(d1.isDisposed());
    }

    @Test
    public void disposeThrowsCheckedExceptionSneaky() {
        CompositeDisposable cd = new CompositeDisposable();
        cd.add(new Disposable() {
            @Override
            public void dispose() {
                CompositeDisposableTest.<RuntimeException>throwSneaky();
            }

            @Override
            public boolean isDisposed() {
                // TODO Auto-generated method stub
                return false;
            }
        });
        Disposable d1 = Disposables.empty();
        cd.add(d1);
        try {
            cd.dispose();
            Assert.fail("Failed to throw");
        } catch (RuntimeException ex) {
            // expected
            if (!((ex.getCause()) instanceof IOException)) {
                Assert.fail(((ex.toString()) + " should have thrown RuntimeException(IOException)"));
            }
        }
        Assert.assertTrue(d1.isDisposed());
    }
}

