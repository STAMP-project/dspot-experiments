package com.github.mustachejava;


import java.io.IOException;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Inspired by an unconfirmed bug report.
 */
public class AmplConcurrencyTest {
    private static class TestObject {
        final int a;

        final int b;

        final int c;

        int aa() throws InterruptedException {
            Thread.sleep(AmplConcurrencyTest.r.nextInt(10));
            return a;
        }

        int bb() throws InterruptedException {
            Thread.sleep(AmplConcurrencyTest.r.nextInt(10));
            return b;
        }

        int cc() throws InterruptedException {
            Thread.sleep(AmplConcurrencyTest.r.nextInt(10));
            return c;
        }

        Callable<Integer> calla() throws InterruptedException {
            return () -> {
                Thread.sleep(AmplConcurrencyTest.r.nextInt(10));
                return a;
            };
        }

        Callable<Integer> callb() throws InterruptedException {
            return () -> {
                Thread.sleep(AmplConcurrencyTest.r.nextInt(10));
                return b;
            };
        }

        Callable<Integer> callc() throws InterruptedException {
            return () -> {
                Thread.sleep(AmplConcurrencyTest.r.nextInt(10));
                return c;
            };
        }

        private TestObject(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    static Random r = new SecureRandom();

    // Alternate render
    static String render(AmplConcurrencyTest.TestObject to) {
        return ((((to.a) + ":") + (to.b)) + ":") + (to.c);
    }

    private AtomicInteger render(Mustache test, ExecutorService es) throws InterruptedException {
        final AtomicInteger total = new AtomicInteger();
        final Semaphore semaphore = new Semaphore(100);
        for (int i = 0; i < 100000; i++) {
            semaphore.acquire();
            es.submit(() -> {
                try {
                    AmplConcurrencyTest.TestObject testObject = new AmplConcurrencyTest.TestObject(AmplConcurrencyTest.r.nextInt(), AmplConcurrencyTest.r.nextInt(), AmplConcurrencyTest.r.nextInt());
                    StringWriter sw = new StringWriter();
                    test.execute(sw, testObject).close();
                    if (!(AmplConcurrencyTest.render(testObject).equals(sw.toString()))) {
                        total.incrementAndGet();
                    }
                } catch (IOException e) {
                    // Can't fail
                    e.printStackTrace();
                    System.exit(1);
                } finally {
                    semaphore.release();
                }
            });
        }
        // Wait for them all to complete
        semaphore.acquire(100);
        return total;
    }
}

