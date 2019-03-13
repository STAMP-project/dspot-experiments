package com.blankj.utilcode.util;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2018/05/21
 *     desc  :
 * </pre>
 */
public class ThreadUtilsTest extends BaseTest {
    @Test
    public void executeByFixed() throws Exception {
        asyncTest(10, new ThreadUtilsTest.TestRunnable<String>() {
            @Override
            public void run(final int index, CountDownLatch latch) {
                final ThreadUtilsTest.TestTask<String> task = new ThreadUtilsTest.TestTask<String>(latch) {
                    @Override
                    public String doInBackground() throws Throwable {
                        Thread.sleep((500 + (index * 10)));
                        if (index < 4) {
                            return ((Thread.currentThread()) + " :") + index;
                        } else
                            if (index < 7) {
                                cancel();
                                return null;
                            } else {
                                throw new NullPointerException(String.valueOf(index));
                            }

                    }

                    @Override
                    void onTestSuccess(String result) {
                        System.out.println(result);
                    }
                };
                ThreadUtils.executeByFixed(3, task);
            }
        });
    }

    @Test
    public void executeByFixedWithDelay() throws Exception {
        asyncTest(10, new ThreadUtilsTest.TestRunnable<String>() {
            @Override
            public void run(final int index, CountDownLatch latch) {
                final ThreadUtilsTest.TestTask<String> task = new ThreadUtilsTest.TestTask<String>(latch) {
                    @Override
                    public String doInBackground() throws Throwable {
                        Thread.sleep(500);
                        if (index < 4) {
                            return ((Thread.currentThread()) + " :") + index;
                        } else
                            if (index < 7) {
                                cancel();
                                return null;
                            } else {
                                throw new NullPointerException(String.valueOf(index));
                            }

                    }

                    @Override
                    void onTestSuccess(String result) {
                        System.out.println(result);
                    }
                };
                ThreadUtils.executeByFixedWithDelay(3, task, (500 + (index * 10)), TimeUnit.MILLISECONDS);
            }
        });
    }

    @Test
    public void executeByFixedAtFixRate() throws Exception {
        asyncTest(10, new ThreadUtilsTest.TestRunnable<String>() {
            @Override
            public void run(final int index, CountDownLatch latch) {
                final ThreadUtilsTest.TestScheduledTask<String> task = new ThreadUtilsTest.TestScheduledTask<String>(latch, 3) {
                    @Override
                    public String doInBackground() throws Throwable {
                        Thread.sleep((500 + (index * 10)));
                        if (index < 4) {
                            return ((Thread.currentThread()) + " :") + index;
                        } else
                            if (index < 7) {
                                cancel();
                                return null;
                            } else {
                                throw new NullPointerException(String.valueOf(index));
                            }

                    }

                    @Override
                    void onTestSuccess(String result) {
                        System.out.println(result);
                    }
                };
                ThreadUtils.executeByFixedAtFixRate(3, task, (3000 + (index * 10)), TimeUnit.MILLISECONDS);
            }
        });
    }

    @Test
    public void executeBySingle() throws Exception {
        asyncTest(10, new ThreadUtilsTest.TestRunnable<String>() {
            @Override
            public void run(final int index, CountDownLatch latch) {
                final ThreadUtilsTest.TestTask<String> task = new ThreadUtilsTest.TestTask<String>(latch) {
                    @Override
                    public String doInBackground() throws Throwable {
                        Thread.sleep(200);
                        if (index < 4) {
                            return ((Thread.currentThread()) + " :") + index;
                        } else
                            if (index < 7) {
                                cancel();
                                return null;
                            } else {
                                throw new NullPointerException(String.valueOf(index));
                            }

                    }

                    @Override
                    void onTestSuccess(String result) {
                        System.out.println(result);
                    }
                };
                ThreadUtils.executeBySingle(task);
            }
        });
    }

    @Test
    public void executeBySingleWithDelay() throws Exception {
        asyncTest(10, new ThreadUtilsTest.TestRunnable<String>() {
            @Override
            public void run(final int index, CountDownLatch latch) {
                final ThreadUtilsTest.TestTask<String> task = new ThreadUtilsTest.TestTask<String>(latch) {
                    @Override
                    public String doInBackground() throws Throwable {
                        Thread.sleep(500);
                        if (index < 4) {
                            return ((Thread.currentThread()) + " :") + index;
                        } else
                            if (index < 7) {
                                cancel();
                                return null;
                            } else {
                                throw new NullPointerException(String.valueOf(index));
                            }

                    }

                    @Override
                    void onTestSuccess(String result) {
                        System.out.println(result);
                    }
                };
                ThreadUtils.executeBySingleWithDelay(task, (500 + (index * 10)), TimeUnit.MILLISECONDS);
            }
        });
    }

    @Test
    public void executeBySingleAtFixRate() throws Exception {
        asyncTest(10, new ThreadUtilsTest.TestRunnable<String>() {
            @Override
            public void run(final int index, CountDownLatch latch) {
                final ThreadUtilsTest.TestScheduledTask<String> task = new ThreadUtilsTest.TestScheduledTask<String>(latch, 3) {
                    @Override
                    public String doInBackground() throws Throwable {
                        Thread.sleep((100 + (index * 10)));
                        if (index < 4) {
                            return ((Thread.currentThread()) + " :") + index;
                        } else
                            if (index < 7) {
                                cancel();
                                return null;
                            } else {
                                throw new NullPointerException(String.valueOf(index));
                            }

                    }

                    @Override
                    void onTestSuccess(String result) {
                        System.out.println(result);
                    }
                };
                ThreadUtils.executeBySingleAtFixRate(task, (2000 + (index * 10)), TimeUnit.MILLISECONDS);
            }
        });
    }

    @Test
    public void executeByIo() throws Exception {
        asyncTest(10, new ThreadUtilsTest.TestRunnable<String>() {
            @Override
            public void run(final int index, CountDownLatch latch) {
                final ThreadUtilsTest.TestTask<String> task = new ThreadUtilsTest.TestTask<String>(latch) {
                    @Override
                    public String doInBackground() throws Throwable {
                        Thread.sleep((500 + (index * 10)));
                        if (index < 4) {
                            return ((Thread.currentThread()) + " :") + index;
                        } else
                            if (index < 7) {
                                cancel();
                                return null;
                            } else {
                                throw new NullPointerException(String.valueOf(index));
                            }

                    }

                    @Override
                    void onTestSuccess(String result) {
                        System.out.println(result);
                    }
                };
                ThreadUtils.executeByIo(task);
            }
        });
    }

    @Test
    public void executeByIoWithDelay() throws Exception {
        asyncTest(10, new ThreadUtilsTest.TestRunnable<String>() {
            @Override
            public void run(final int index, CountDownLatch latch) {
                final ThreadUtilsTest.TestTask<String> task = new ThreadUtilsTest.TestTask<String>(latch) {
                    @Override
                    public String doInBackground() throws Throwable {
                        Thread.sleep(500);
                        if (index < 4) {
                            return ((Thread.currentThread()) + " :") + index;
                        } else
                            if (index < 7) {
                                cancel();
                                return null;
                            } else {
                                throw new NullPointerException(String.valueOf(index));
                            }

                    }

                    @Override
                    void onTestSuccess(String result) {
                        System.out.println(result);
                    }
                };
                ThreadUtils.executeByIoWithDelay(task, (500 + (index * 10)), TimeUnit.MILLISECONDS);
            }
        });
    }

    @Test
    public void executeByIoAtFixRate() throws Exception {
        asyncTest(10, new ThreadUtilsTest.TestRunnable<String>() {
            @Override
            public void run(final int index, CountDownLatch latch) {
                final ThreadUtilsTest.TestScheduledTask<String> task = new ThreadUtilsTest.TestScheduledTask<String>(latch, 3) {
                    @Override
                    public String doInBackground() throws Throwable {
                        Thread.sleep((100 + (index * 10)));
                        if (index < 4) {
                            return ((Thread.currentThread()) + " :") + index;
                        } else
                            if (index < 7) {
                                cancel();
                                return null;
                            } else {
                                throw new NullPointerException(String.valueOf(index));
                            }

                    }

                    @Override
                    void onTestSuccess(String result) {
                        System.out.println(result);
                    }
                };
                ThreadUtils.executeByIoAtFixRate(task, 1000, TimeUnit.MILLISECONDS);
            }
        });
    }

    @Test
    public void executeByCpu() throws Exception {
        asyncTest(10, new ThreadUtilsTest.TestRunnable<String>() {
            @Override
            public void run(final int index, CountDownLatch latch) {
                final ThreadUtilsTest.TestTask<String> task = new ThreadUtilsTest.TestTask<String>(latch) {
                    @Override
                    public String doInBackground() throws Throwable {
                        Thread.sleep((500 + (index * 10)));
                        if (index < 4) {
                            return ((Thread.currentThread()) + " :") + index;
                        } else
                            if (index < 7) {
                                cancel();
                                return null;
                            } else {
                                throw new NullPointerException(String.valueOf(index));
                            }

                    }

                    @Override
                    void onTestSuccess(String result) {
                        System.out.println(result);
                    }
                };
                ThreadUtils.executeByCpu(task);
            }
        });
    }

    @Test
    public void executeByCpuWithDelay() throws Exception {
        asyncTest(10, new ThreadUtilsTest.TestRunnable<String>() {
            @Override
            public void run(final int index, CountDownLatch latch) {
                final ThreadUtilsTest.TestTask<String> task = new ThreadUtilsTest.TestTask<String>(latch) {
                    @Override
                    public String doInBackground() throws Throwable {
                        Thread.sleep(500);
                        if (index < 4) {
                            return ((Thread.currentThread()) + " :") + index;
                        } else
                            if (index < 7) {
                                cancel();
                                return null;
                            } else {
                                throw new NullPointerException(String.valueOf(index));
                            }

                    }

                    @Override
                    void onTestSuccess(String result) {
                        System.out.println(result);
                    }
                };
                ThreadUtils.executeByCpuWithDelay(task, (500 + (index * 10)), TimeUnit.MILLISECONDS);
            }
        });
    }

    @Test
    public void executeByCpuAtFixRate() throws Exception {
        asyncTest(10, new ThreadUtilsTest.TestRunnable<String>() {
            @Override
            public void run(final int index, CountDownLatch latch) {
                final ThreadUtilsTest.TestScheduledTask<String> task = new ThreadUtilsTest.TestScheduledTask<String>(latch, 3) {
                    @Override
                    public String doInBackground() throws Throwable {
                        Thread.sleep((100 + (index * 10)));
                        if (index < 4) {
                            return ((Thread.currentThread()) + " :") + index;
                        } else
                            if (index < 7) {
                                cancel();
                                return null;
                            } else {
                                throw new NullPointerException(String.valueOf(index));
                            }

                    }

                    @Override
                    void onTestSuccess(String result) {
                        System.out.println(result);
                    }
                };
                ThreadUtils.executeByCpuAtFixRate(task, 1000, TimeUnit.MILLISECONDS);
            }
        });
    }

    abstract static class TestScheduledTask<T> extends ThreadUtils.Task<T> {
        private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

        private int mTimes;

        CountDownLatch mLatch;

        TestScheduledTask(final CountDownLatch latch, final int times) {
            mLatch = latch;
            mTimes = times;
        }

        abstract void onTestSuccess(T result);

        @Override
        public void onSuccess(T result) {
            onTestSuccess(result);
            if (((ThreadUtilsTest.TestScheduledTask.ATOMIC_INTEGER.addAndGet(1)) % (mTimes)) == 0) {
                mLatch.countDown();
            }
        }

        @Override
        public void onCancel() {
            System.out.println(((Thread.currentThread()) + " onCancel: "));
            mLatch.countDown();
        }

        @Override
        public void onFail(Throwable t) {
            System.out.println((((Thread.currentThread()) + " onFail: ") + t));
            mLatch.countDown();
        }
    }

    abstract static class TestTask<T> extends ThreadUtils.Task<T> {
        CountDownLatch mLatch;

        TestTask(final CountDownLatch latch) {
            mLatch = latch;
        }

        abstract void onTestSuccess(T result);

        @Override
        public void onSuccess(T result) {
            onTestSuccess(result);
            mLatch.countDown();
        }

        @Override
        public void onCancel() {
            System.out.println(((Thread.currentThread()) + " onCancel: "));
            mLatch.countDown();
        }

        @Override
        public void onFail(Throwable t) {
            System.out.println((((Thread.currentThread()) + " onFail: ") + t));
            mLatch.countDown();
        }
    }

    interface TestRunnable<T> {
        void run(final int index, CountDownLatch latch);
    }
}

