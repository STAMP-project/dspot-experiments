package com.github.mustachejava;


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

    static java.lang.String render(com.github.mustachejava.AmplConcurrencyTest.TestObject to) {
        return ((((to.a) + ":") + (to.b)) + ":") + (to.c);
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
                    e.printStackTrace();
                    java.lang.System.exit(1);
                } finally {
                    semaphore.release();
                }
            });
        }
        semaphore.acquire(100);
        return total;
    }

    public void testReadmeSerial_add4390_add5321() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add4390__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_add4390__13 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4390__13);
        java.lang.String o_testReadmeSerial_add4390__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4390__14);
        java.lang.String o_testReadmeSerial_add4390__15 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4390__15);
        java.lang.String String_42 = "Should be a little bit more than 4 seconds: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_42);
        boolean boolean_43 = (diff > 3999) && (diff < 6000);
        ((java.io.StringWriter) (o_testReadmeSerial_add4390__9)).getBuffer().toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4390__13);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4390__14);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4390__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_42);
    }

    public void testReadmeSerial_add4391_literalMutationString4557() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add4391__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        sw.toString();
        java.lang.String o_testReadmeSerial_add4391__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4391__14);
        java.lang.String o_testReadmeSerial_add4391__15 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4391__15);
        java.lang.String String_34 = "page1.txt" + diff;
        junit.framework.TestCase.assertEquals("page1.txt4001", String_34);
        boolean boolean_35 = (diff > 3999) && (diff < 6000);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4391__14);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4391__15);
        junit.framework.TestCase.assertEquals("page1.txt4001", String_34);
    }

    public void testReadmeParallel_add118792_literalMutationNumber118970() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add118792__17 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__17);
        java.lang.String o_testReadmeParallel_add118792__18 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nName: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__18);
        java.lang.String String_281 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 2002", String_281);
        boolean boolean_282 = (diff > 0) && (diff < 2000);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__17);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nName: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__18);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 2002", String_281);
    }

    public void testReadmeParallel_add118792_remove119885() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add118792__17 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__17);
        java.lang.String o_testReadmeParallel_add118792__18 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__18);
        java.lang.String String_281 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_281);
        boolean boolean_282 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__17);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118792__18);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_281);
    }

    public void testReadmeParallel_add118793_literalMutationNumber119041() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add118793__16 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118793__16);
        java.lang.String o_testReadmeParallel_add118793__17 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118793__17);
        java.lang.String String_285 = "Should be a little bit more than 1 second: " + diff;
        boolean boolean_286 = (diff > 1998) && (diff < 2000);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118793__16);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add118793__17);
    }

    public void testInvalidDelimiters_add90438_failAssert0_literalMutationString90642_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalid]Delimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438_failAssert0_literalMutationString90642 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalid]Delimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0_add90681_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "test}nvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0_add90681 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[test}nvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0_add90682_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "test}nvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "test}nvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0_add90682 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[test}nvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0null90728_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0null90728 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "GOl90pb>!jz|IT^nLhYQ4");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[GOl90pb>!jz|IT^nLhYQ4:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "test}nvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[test}nvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimitrs");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0_literalMutationString90607_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "GOl90pb>!jwz|IT^nLhYQ4");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436_failAssert0_literalMutationString90607 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[GOl90pb>!jwz|IT^nLhYQ4:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0_literalMutationString90608_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "9$}v]+Mj ^QN!eJ%oMm;!");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436_failAssert0_literalMutationString90608 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[9$}v]+Mj ^QN!eJ%oMm;!:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90430_failAssert0_add90689_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong5}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430_failAssert0_add90689 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong5 @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90428_failAssert0_literalMutationString90589_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolo1g}}"), "g2@tQ;O}0D&.IbvTrWmQe");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428_failAssert0_literalMutationString90589 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolo1g @[g2@tQ;O}0D&.IbvTrWmQe:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0_literalMutationString90628_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("={=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0_literalMutationString90628 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0null90719_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429_failAssert0null90719 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90438_failAssert0_literalMutationString90647_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidD`limiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438_failAssert0_literalMutationString90647 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidD`limiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90440_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90440 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0_literalMutationString90595_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooloag}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0_literalMutationString90595 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooloag @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0_literalMutationString90567_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toZolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434_failAssert0_literalMutationString90567 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toZolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0_literalMutationString90592_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=oolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0_literalMutationString90592 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =oolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0_literalMutationString90599_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0_literalMutationString90599 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90430_failAssert0_literalMutationString90550_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong5}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430_failAssert0_literalMutationString90550 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong5 @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0null90720_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0null90720 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0_literalMutationString90573_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434_failAssert0_literalMutationString90573 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0_add90708_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0_add90708 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0_add90686_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidD6elimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433_failAssert0_add90686 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidD6elimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0_add90685_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidD6elimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidD6elimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433_failAssert0_add90685 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidD6elimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0_add90683_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "test}nvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0_add90683 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[test}nvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0null90729_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0null90729 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0_literalMutationString90508_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tlong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429_failAssert0_literalMutationString90508 should have thrown AssertionFailedError");
        } catch (junit.framework.AssertionFailedError expected) {
            junit.framework.TestCase.assertEquals("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0_add90706_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0_add90706 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0_add90707_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0_add90707 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0_add90684_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidD6elimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433_failAssert0_add90684 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidD6elimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90438_failAssert0_add90710_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438_failAssert0_add90710 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90438_failAssert0_add90712_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438_failAssert0_add90712 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90428_failAssert0_add90697_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolo1g}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolo1g}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428_failAssert0_add90697 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolo1g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0_literalMutationString90632_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelmiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0_literalMutationString90632 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0_add90690_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435_failAssert0_add90690 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0_literalMutationString90524_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}"), "test}nvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0_literalMutationString90524 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong} @[test}nvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0null90727_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436_failAssert0null90727 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0_literalMutationString90516_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelmiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429_failAssert0_literalMutationString90516 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelmiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0_add90693_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434_failAssert0_add90693 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0_add90691_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435_failAssert0_add90691 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0_add90679_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429_failAssert0_add90679 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0null90726_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0null90726 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0_literalMutationString90526_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0_literalMutationString90526 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90438_failAssert0null90730_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438_failAssert0null90730 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0_add90704_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "GOl90pb>!jz|IT^nLhYQ4");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436_failAssert0_add90704 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[GOl90pb>!jz|IT^nLhYQ4:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0_add90692_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435_failAssert0_add90692 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0_add90678_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429_failAssert0_add90678 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0null90723_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435_failAssert0null90723 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0_literalMutationString90619_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439_failAssert0_literalMutationString90619 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0_add90703_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "GOl90pb>!jz|IT^nLhYQ4");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "GOl90pb>!jz|IT^nLhYQ4");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436_failAssert0_add90703 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[GOl90pb>!jz|IT^nLhYQ4:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0null90724_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434_failAssert0null90724 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0null90721_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433_failAssert0null90721 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0_add90701_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0_add90701 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90436_failAssert0_add90702_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "GOl90pb>!jz|IT^nLhYQ4");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90436_failAssert0_add90702 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[GOl90pb>!jz|IT^nLhYQ4:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0_add90700_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0_add90700 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90440_failAssert0_add90713_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull90440 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90440_failAssert0_add90713 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0_literalMutationString90538_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433_failAssert0_literalMutationString90538 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90428_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolo1g}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolo1g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0_literalMutationString90561_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435_failAssert0_literalMutationString90561 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90428_failAssert0_add90696_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolo1g}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428_failAssert0_add90696 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolo1g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90440_failAssert0_add90715_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull90440 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90440_failAssert0_add90715 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90437_failAssert0_literalMutationString90521_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tBolong}}"), "test}nvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90437_failAssert0_literalMutationString90521 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tBolong @[test}nvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90430_failAssert0null90722_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong5}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430_failAssert0null90722 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong5 @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90438_failAssert0_add90711_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438_failAssert0_add90711 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90428_failAssert0null90725_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolo1g}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90428_failAssert0null90725 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolo1g @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidD6elimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidD6elimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90430_failAssert0_add90688_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong5}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong5}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430_failAssert0_add90688 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong5 @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90433_failAssert0_literalMutationString90536_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=too=long}}"), "testInvalidD6elimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90433_failAssert0_literalMutationString90536 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =too=long @[testInvalidD6elimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90439_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90439 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0_add90699_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432_failAssert0_add90699 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0_add90695_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434_failAssert0_add90695 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90429_failAssert0_add90680_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90429_failAssert0_add90680 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90438_failAssert0() throws java.lang.Exception {
        try {
            {
                createMustacheFactory();
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90438 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90434_failAssert0_add90694_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90434_failAssert0_add90694 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90432_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90432 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90435_failAssert0_literalMutationString90556_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=oolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90435_failAssert0_literalMutationString90556 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =oolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90430_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong5}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90430 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong5 @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testEmptyMustachenull106457_failAssert0_literalMutationString106769_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader("{{=}}"), null);
                }
                junit.framework.TestCase.fail("testEmptyMustachenull106457 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testEmptyMustachenull106457_failAssert0_literalMutationString106769 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: = @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65423_failAssert0null66239_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##=}}{{##={{ }}=#f##";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), null);
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423_failAssert0null66239 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: ={{}}=#f @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65427_literalMutationString65693_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=#5# ##=}}{{##={{ }}=####";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "e;1H");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testOutputDelimiters_literalMutationString65427__8 = mustache.execute(sw, new java.lang.Object[0]);
            java.lang.String o_testOutputDelimiters_literalMutationString65427__9 = sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65427_literalMutationString65693 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =#5###= @[e;1H:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65422_failAssert0_add66166_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##M=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422_failAssert0_add66166 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####M= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65422_failAssert0null66242_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##M=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), null);
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422_failAssert0null66242 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####M= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65423_failAssert0_add66162_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##=}}{{##={{ }}=#f##";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423_failAssert0_add66162 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: ={{}}=#f @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65423_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=## ##=}}{{##={{ }}=#f##";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            mustache.execute(sw, new java.lang.Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: ={{}}=#f @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65422_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=## ##M=}}{{##={{ }}=####";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            mustache.execute(sw, new java.lang.Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####M= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65423_failAssert0_literalMutationNumber65858_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##=}}{{##={{ }}=#f##";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65423_failAssert0_literalMutationNumber65858 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: ={{}}=#f @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65422_failAssert0_literalMutationString65866_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##M=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), ">A L");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65422_failAssert0_literalMutationString65866 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####M= @[>A L:1]", expected.getMessage());
        }
    }
}

