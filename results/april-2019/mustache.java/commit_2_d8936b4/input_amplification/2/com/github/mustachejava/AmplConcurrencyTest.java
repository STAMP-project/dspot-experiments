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

    public void testReadmeSerial_add4467_remove5307() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add4467__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        sw.toString();
        java.lang.String o_testReadmeSerial_add4467__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4467__14);
        java.lang.String String_42 = "Should be a little bit more than 4 seconds: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_42);
        boolean boolean_43 = (diff > 3999) && (diff < 6000);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4467__14);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_42);
    }

    public void testReadmeParallel_add119158_add119925() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        ((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService().isTerminated();
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        java.lang.System.currentTimeMillis();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add119158__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119158__15);
        sw.toString();
        java.lang.String String_281 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_281);
        boolean boolean_282 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119158__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_281);
    }

    public void testReadmeParallel_add119159_remove120199() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add119159__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119159__14);
        java.lang.String o_testReadmeParallel_add119159__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119159__15);
        java.lang.String String_291 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_291);
        boolean boolean_292 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119159__14);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119159__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_291);
    }

    public void testReadmeParallel_add119158_remove120189() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        java.lang.System.currentTimeMillis();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add119158__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119158__15);
        java.lang.String String_281 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_281);
        boolean boolean_282 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add119158__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_281);
    }

    public void testInvalidDelimiters_literalMutationString90813_failAssert0_literalMutationString90993_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=to|olong}}"), "GCM#{boqzVzsmX?@M@,(>");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813_failAssert0_literalMutationString90993 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =to|olong @[GCM#{boqzVzsmX?@M@,(>:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0_literalMutationString91007_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaQidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822_failAssert0_literalMutationString91007 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaQidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90813_failAssert0_add91088_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=to|olong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813_failAssert0_add91088 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =to|olong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0_literalMutationString91004_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822_failAssert0_literalMutationString91004 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90819_failAssert0_add91068_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819_failAssert0_add91068 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90818_failAssert0_add91064_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818_failAssert0_add91064 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0_add91085_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "#(DCr+ <!((mv<4cKfgz5");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816_failAssert0_add91085 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[#(DCr+ <!((mv<4cKfgz5:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90813_failAssert0_add91087_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=to|olong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=to|olong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813_failAssert0_add91087 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =to|olong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90812_failAssert0_literalMutationString90910_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "te1stInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812_failAssert0_literalMutationString90910 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[te1stInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0() throws java.lang.Exception {
        try {
            {
                createMustacheFactory();
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0_add91091_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822_failAssert0_add91091 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90824_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90824 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0_add91093_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0_add91093 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90812_failAssert0_add91066_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812_failAssert0_add91066 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0_add91092_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822_failAssert0_add91092 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90812_failAssert0_add91067_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812_failAssert0_add91067 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90819_failAssert0_add91069_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819_failAssert0_add91069 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0_add91094_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0_add91094 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90819_failAssert0_literalMutationString90923_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "test[InvalidDelimters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819_failAssert0_literalMutationString90923 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[test[InvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0_add91095_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0_add91095 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90824_failAssert0_add91097_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull90824 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90824_failAssert0_add91097 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90818_failAssert0null91103_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818_failAssert0null91103 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0_literalMutationString90945_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0_literalMutationString90945 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0_add91082_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=too1ong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815_failAssert0_add91082 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =too1ong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90819_failAssert0_add91070_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819_failAssert0_add91070 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0_add91081_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=too1ong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=too1ong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815_failAssert0_add91081 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =too1ong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0_add91080_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=too1ong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815_failAssert0_add91080 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =too1ong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0_literalMutationString91024_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{u{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0_literalMutationString91024 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0_literalMutationString90955_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "EUB.B#K}6-<x(X$&VTm>T");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821_failAssert0_literalMutationString90955 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[EUB.B#K}6-<x(X$&VTm>T:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0_add91074_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0_add91074 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90812_failAssert0null91104_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812_failAssert0null91104 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90824_failAssert0_add91099_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull90824 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90824_failAssert0_add91099 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull90824_failAssert0_add91098_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull90824 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull90824_failAssert0_add91098 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0_literalMutationString91016_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0_literalMutationString91016 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0_literalMutationString90958_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvali2dDeli#iters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821_failAssert0_literalMutationString90958 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvali2dDeli#iters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0null91113_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0null91113 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0null91112_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822_failAssert0null91112 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0_literalMutationString90969_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=too1ong}}"), "testInvalidDelPmiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815_failAssert0_literalMutationString90969 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =too1ong @[testInvalidDelPmiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0_add91075_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0_add91075 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0_literalMutationString90946_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "#");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0_literalMutationString90946 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[#:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90823_failAssert0null91114_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90823 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90823_failAssert0null91114 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0_add91076_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0_add91076 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90820_failAssert0null91106_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820_failAssert0null91106 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0_literalMutationString90947_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0_literalMutationString90947 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90813_failAssert0null91111_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=to|olong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813_failAssert0null91111 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =to|olong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90813_failAssert0_literalMutationString90986_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=to|ol@ng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813_failAssert0_literalMutationString90986 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =to|ol@ng @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90820_failAssert0_add91073_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "tebstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820_failAssert0_add91073 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[tebstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0null91108_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821_failAssert0null91108 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90820_failAssert0_add91072_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "tebstInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "tebstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820_failAssert0_add91072 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[tebstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90820_failAssert0_add91071_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "tebstInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820_failAssert0_add91071 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[tebstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0null91110_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816_failAssert0null91110 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0_literalMutationString90967_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=too1ong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815_failAssert0_literalMutationString90967 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =too1ong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90819_failAssert0null91105_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819_failAssert0null91105 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90819_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90819 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add90822_failAssert0_add91089_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add90822 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add90822_failAssert0_add91089 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0null91109_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=too1ong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815_failAssert0null91109 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =too1ong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90818_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90815_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=too1ong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90815 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =too1ong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "#(DCr+ <!((mv<4cKfgz5");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[#(DCr+ <!((mv<4cKfgz5:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0_add91077_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDeli#iters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821_failAssert0_add91077 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDeli#iters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90820_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "tebstInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[tebstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0_add91084_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "#(DCr+ <!((mv<4cKfgz5");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "#(DCr+ <!((mv<4cKfgz5");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816_failAssert0_add91084 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[#(DCr+ <!((mv<4cKfgz5:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0_add91078_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDeli#iters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDeli#iters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821_failAssert0_add91078 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDeli#iters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0_literalMutationString90981_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816_failAssert0_literalMutationString90981 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDeli#iters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDeli#iters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90820_failAssert0_literalMutationString90935_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90820_failAssert0_literalMutationString90935 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90818_failAssert0_literalMutationString90902_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "pae1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818_failAssert0_literalMutationString90902 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[pae1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90818_failAssert0_add91063_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818_failAssert0_add91063 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90813_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=to|olong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90813 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =to|olong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90812_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90812 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90821_failAssert0_add91079_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDeli#iters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90821_failAssert0_add91079 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDeli#iters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90818_failAssert0_add91062_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90818_failAssert0_add91062 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0_add91083_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "#(DCr+ <!((mv<4cKfgz5");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816_failAssert0_add91083 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[#(DCr+ <!((mv<4cKfgz5:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90817_failAssert0null91107_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90817_failAssert0null91107 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString90816_failAssert0_literalMutationString90983_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "#2} Y(sb[{s;c$?qqpe1^");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString90816_failAssert0_literalMutationString90983 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[#2} Y(sb[{s;c$?qqpe1^:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65569_failAssert0_literalMutationString65964_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##=}}{{##={ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "tet");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569_failAssert0_literalMutationString65964 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: ={}}= @[tet:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65572_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=#]# ##=}}{{##={{ }}=####";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            mustache.execute(sw, new java.lang.Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =#]###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65574_literalMutationString65744_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=## ##=}}{{##={{W}}=####";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testOutputDelimiters_literalMutationString65574__8 = mustache.execute(sw, new java.lang.Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65574_literalMutationString65744 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: ={{W}}= @[:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65572_failAssert0_add66300_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=#]# ##=}}{{##={{ }}=####";
                new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572_failAssert0_add66300 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =#]###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65569_failAssert0null66390_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##=}}{{##={ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), null);
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569_failAssert0null66390 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: ={}}= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65569_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=## ##=}}{{##={ }}=####";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            mustache.execute(sw, new java.lang.Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: ={}}= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65569_failAssert0_add66294_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##=}}{{##={ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65569_failAssert0_add66294 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: ={}}= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65572_failAssert0_literalMutationString65998_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=#]# ##=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "]?ya");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572_failAssert0_literalMutationString65998 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =#]###= @[]?ya:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString65572_failAssert0null66396_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=#]# ##=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), null);
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString65572_failAssert0null66396 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =#]###= @[null:1]", expected.getMessage());
        }
    }
}

