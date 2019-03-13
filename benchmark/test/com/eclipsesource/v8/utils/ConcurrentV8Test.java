/**
 * *****************************************************************************
 * Copyright (c) 2016 Brandon Sanders
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Brandon Sanders - initial API and implementation and/or initial documentation
 *    R. Ian Bull - additional test cases
 * ****************************************************************************
 */
package com.eclipsesource.v8.utils;


import com.eclipsesource.v8.V8;
import org.junit.Assert;
import org.junit.Test;


public class ConcurrentV8Test {
    public static class ExceptionThrower {
        public void whine() throws Exception {
            throw new Exception("Whaaa!");
        }
    }

    @Test
    public void testLockNotAquiredAfterCreation() {
        ConcurrentV8 concurrentV8 = new ConcurrentV8();
        Assert.assertFalse(concurrentV8.getV8().getLocker().hasLock());
        concurrentV8.release();
    }

    @Test
    public void testLockNotAcquiredAfterExecution() {
        ConcurrentV8 concurrentV8 = new ConcurrentV8();
        concurrentV8.run(new V8Runnable() {
            @Override
            public void run(final V8 runtime) {
                runtime.executeScript("foo = 7;");
            }
        });
        Assert.assertFalse(concurrentV8.getV8().getLocker().hasLock());
        concurrentV8.release();
    }

    @Test
    public void testLockNotAquiredAfterException() {
        ConcurrentV8 concurrentV8 = new ConcurrentV8();
        try {
            concurrentV8.run(new V8Runnable() {
                @Override
                public void run(final V8 runtime) {
                    runtime.executeScript("throw 'my exception';");
                }
            });
        } catch (Exception e) {
            // do nothing
        }
        Assert.assertFalse(concurrentV8.getV8().getLocker().hasLock());
        concurrentV8.release();
    }

    @Test
    public void testLockAquiredDuringExecution() {
        ConcurrentV8 concurrentV8 = new ConcurrentV8();
        try {
            concurrentV8.run(new V8Runnable() {
                @Override
                public void run(final V8 runtime) {
                    Assert.assertTrue(runtime.getLocker().hasLock());
                }
            });
        } catch (Exception e) {
            // do nothing
        }
        concurrentV8.release();
    }

    @Test
    public void testConstructorWithoutV8() {
        ConcurrentV8 concurrentV8 = new ConcurrentV8();
        Assert.assertNotNull(concurrentV8.getV8());
        concurrentV8.release();
    }

    @Test
    public void shouldShareV8AcrossThreads() {
        final ConcurrentV8 v8 = new ConcurrentV8();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                v8.run(new V8Runnable() {
                    @Override
                    public void run(final V8 v8) {
                        v8.executeVoidScript("var i = 3000;");
                    }
                });
            }
        });
        thread1.start();
        try {
            thread1.join();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                v8.run(new V8Runnable() {
                    @Override
                    public void run(final V8 v8) {
                        v8.executeVoidScript("i += 344;");
                    }
                });
            }
        });
        thread2.start();
        try {
            thread2.join();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        v8.run(new V8Runnable() {
            @Override
            public void run(final V8 v8) {
                Assert.assertEquals(3344, v8.executeIntegerScript("i"));
            }
        });
        v8.release();
    }

    @Test
    public void exceptionsShouldBeRaised() {
        ConcurrentV8 v8 = new ConcurrentV8();
        final ConcurrentV8Test.ExceptionThrower exceptionThrower = new ConcurrentV8Test.ExceptionThrower();
        v8.run(new V8Runnable() {
            @Override
            public void run(final V8 v8) {
                v8.registerJavaMethod(exceptionThrower, "whine", "whine", new Class[0]);
            }
        });
        try {
            v8.run(new V8Runnable() {
                @Override
                public void run(final V8 v8) {
                    v8.executeScript("whine();");
                }
            });
            Assert.fail("Regular concurrent V8 invocations should pass on exceptions.");
        } catch (Exception e) {
            Assert.assertEquals("Whaaa!", e.getCause().getMessage());
        }
        v8.release();
    }
}

