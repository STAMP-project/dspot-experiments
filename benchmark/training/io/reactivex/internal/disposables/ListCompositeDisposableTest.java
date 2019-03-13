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
package io.reactivex.internal.disposables;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ListCompositeDisposableTest {
    @Test
    public void constructorAndAddVarargs() {
        Disposable d1 = Disposables.empty();
        Disposable d2 = Disposables.empty();
        ListCompositeDisposable lcd = new ListCompositeDisposable(d1, d2);
        lcd.clear();
        Assert.assertFalse(lcd.isDisposed());
        Assert.assertTrue(d1.isDisposed());
        Assert.assertTrue(d2.isDisposed());
        d1 = Disposables.empty();
        d2 = Disposables.empty();
        lcd.addAll(d1, d2);
        lcd.dispose();
        Assert.assertTrue(lcd.isDisposed());
        Assert.assertTrue(d1.isDisposed());
        Assert.assertTrue(d2.isDisposed());
    }

    @Test
    public void constructorIterable() {
        Disposable d1 = Disposables.empty();
        Disposable d2 = Disposables.empty();
        ListCompositeDisposable lcd = new ListCompositeDisposable(Arrays.asList(d1, d2));
        lcd.clear();
        Assert.assertFalse(lcd.isDisposed());
        Assert.assertTrue(d1.isDisposed());
        Assert.assertTrue(d2.isDisposed());
        d1 = Disposables.empty();
        d2 = Disposables.empty();
        lcd.add(d1);
        lcd.addAll(d2);
        lcd.dispose();
        Assert.assertTrue(lcd.isDisposed());
        Assert.assertTrue(d1.isDisposed());
        Assert.assertTrue(d2.isDisposed());
    }

    @Test
    public void empty() {
        ListCompositeDisposable lcd = new ListCompositeDisposable();
        Assert.assertFalse(lcd.isDisposed());
        lcd.clear();
        Assert.assertFalse(lcd.isDisposed());
        lcd.dispose();
        lcd.dispose();
        lcd.clear();
        Assert.assertTrue(lcd.isDisposed());
    }

    @Test
    public void afterDispose() {
        ListCompositeDisposable lcd = new ListCompositeDisposable();
        lcd.dispose();
        Disposable d = Disposables.empty();
        Assert.assertFalse(lcd.add(d));
        Assert.assertTrue(d.isDisposed());
        d = Disposables.empty();
        Assert.assertFalse(lcd.addAll(d));
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void disposeThrows() {
        Disposable d = new Disposable() {
            @Override
            public void dispose() {
                throw new TestException();
            }

            @Override
            public boolean isDisposed() {
                return false;
            }
        };
        ListCompositeDisposable lcd = new ListCompositeDisposable(d, d);
        try {
            lcd.dispose();
            Assert.fail("Should have thrown!");
        } catch (CompositeException ex) {
            List<Throwable> list = ex.getExceptions();
            TestHelper.assertError(list, 0, TestException.class);
            TestHelper.assertError(list, 1, TestException.class);
        }
        lcd = new ListCompositeDisposable(d);
        try {
            lcd.dispose();
            Assert.fail("Should have thrown!");
        } catch (TestException ex) {
            // expected
        }
    }

    @Test
    public void remove() {
        ListCompositeDisposable lcd = new ListCompositeDisposable();
        Disposable d = Disposables.empty();
        lcd.add(d);
        Assert.assertTrue(lcd.delete(d));
        Assert.assertFalse(d.isDisposed());
        lcd.add(d);
        Assert.assertTrue(lcd.remove(d));
        Assert.assertTrue(d.isDisposed());
        Assert.assertFalse(lcd.remove(d));
        Assert.assertFalse(lcd.delete(d));
        lcd = new ListCompositeDisposable();
        Assert.assertFalse(lcd.remove(d));
        Assert.assertFalse(lcd.delete(d));
    }

    @Test
    public void disposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final ListCompositeDisposable cd = new ListCompositeDisposable();
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
            final ListCompositeDisposable cd = new ListCompositeDisposable();
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
            final ListCompositeDisposable cd = new ListCompositeDisposable();
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
            final ListCompositeDisposable cd = new ListCompositeDisposable();
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
            final ListCompositeDisposable cd = new ListCompositeDisposable();
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
            final ListCompositeDisposable cd = new ListCompositeDisposable();
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
            final ListCompositeDisposable cd = new ListCompositeDisposable();
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
            final ListCompositeDisposable cd = new ListCompositeDisposable();
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
            final ListCompositeDisposable cd = new ListCompositeDisposable();
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
            final ListCompositeDisposable cd = new ListCompositeDisposable();
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
            final ListCompositeDisposable cd = new ListCompositeDisposable();
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
}

