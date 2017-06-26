

package com.github.mustachejava;


/**
 * Inspired by an unconfirmed bug report.
 */
public class AmplConcurrencyTest {
    static java.util.Random r = new java.security.SecureRandom();

    private static class TestObject {
        final int a;

        final int b;

        final int c;

        int aa() throws java.lang.InterruptedException {
            java.lang.Thread.sleep(com.github.mustachejava.AmplConcurrencyTest.r.nextInt(10));
            return a;
        }

        int bb() throws java.lang.InterruptedException {
            java.lang.Thread.sleep(com.github.mustachejava.AmplConcurrencyTest.r.nextInt(10));
            return b;
        }

        int cc() throws java.lang.InterruptedException {
            java.lang.Thread.sleep(com.github.mustachejava.AmplConcurrencyTest.r.nextInt(10));
            return c;
        }

        java.util.concurrent.Callable<java.lang.Integer> calla() throws java.lang.InterruptedException {
            return () -> {
                java.lang.Thread.sleep(com.github.mustachejava.AmplConcurrencyTest.r.nextInt(10));
                return a;
            };
        }

        java.util.concurrent.Callable<java.lang.Integer> callb() throws java.lang.InterruptedException {
            return () -> {
                java.lang.Thread.sleep(com.github.mustachejava.AmplConcurrencyTest.r.nextInt(10));
                return b;
            };
        }

        java.util.concurrent.Callable<java.lang.Integer> callc() throws java.lang.InterruptedException {
            return () -> {
                java.lang.Thread.sleep(com.github.mustachejava.AmplConcurrencyTest.r.nextInt(10));
                return c;
            };
        }

        private TestObject(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    // Alternate render
    static java.lang.String render(com.github.mustachejava.AmplConcurrencyTest.TestObject to) {
        return ((((to.a) + ":") + (to.b)) + ":") + (to.c);
    }

    @org.junit.Test
    public void testConcurrentExecution() throws java.lang.InterruptedException {
        if (com.github.mustachejavabenchmarks.BenchmarkTest.skip())
            return ;
        
        java.lang.String template = "{{aa}}:{{bb}}:{{cc}}";
        final com.github.mustachejava.Mustache test = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
        java.util.concurrent.ExecutorService es = java.util.concurrent.Executors.newCachedThreadPool();
        final java.util.concurrent.atomic.AtomicInteger total = render(test, es);
        junit.framework.Assert.assertEquals(0, total.intValue());
    }

    private java.util.concurrent.atomic.AtomicInteger render(com.github.mustachejava.Mustache test, java.util.concurrent.ExecutorService es) throws java.lang.InterruptedException {
        final java.util.concurrent.atomic.AtomicInteger total = new java.util.concurrent.atomic.AtomicInteger();
        final java.util.concurrent.Semaphore semaphore = new java.util.concurrent.Semaphore(100);
        for (int i = 0; i < 100000; i++) {
            semaphore.acquire();
            es.submit(() -> {
                try {
                    com.github.mustachejava.AmplConcurrencyTest.TestObject testObject = new com.github.mustachejava.AmplConcurrencyTest.TestObject(com.github.mustachejava.AmplConcurrencyTest.r.nextInt(), com.github.mustachejava.AmplConcurrencyTest.r.nextInt(), com.github.mustachejava.AmplConcurrencyTest.r.nextInt());
                    java.io.StringWriter sw = new java.io.StringWriter();
                    test.execute(sw, testObject).close();
                    if (!(com.github.mustachejava.AmplConcurrencyTest.render(testObject).equals(sw.toString()))) {
                        total.incrementAndGet();
                    }
                } catch (java.io.IOException e) {
                    // Can't fail
                    e.printStackTrace();
                    java.lang.System.exit(1);
                } finally {
                    semaphore.release();
                }
            });
        }
        // Wait for them all to complete
        semaphore.acquire(100);
        return total;
    }

    @org.junit.Test
    public void testConcurrentExecutionWithConcurrentTemplate() throws java.lang.InterruptedException {
        if (com.github.mustachejavabenchmarks.BenchmarkTest.skip())
            return ;
        
        java.lang.String template = "{{calla}}:{{callb}}:{{callc}}";
        java.util.concurrent.ExecutorService es = java.util.concurrent.Executors.newCachedThreadPool();
        com.github.mustachejava.DefaultMustacheFactory dmf = new com.github.mustachejava.DefaultMustacheFactory();
        dmf.setExecutorService(es);
        final com.github.mustachejava.Mustache test = dmf.compile(new java.io.StringReader(template), "test");
        final java.util.concurrent.atomic.AtomicInteger total = render(test, es);
        junit.framework.Assert.assertEquals(0, total.intValue());
    }
}

