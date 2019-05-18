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

    public void testSimple_literalMutationString183345_failAssert0_add185321_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("+ke$Lr*<U)z");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0_add185321 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0_add185320_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("+ke$Lr*<U)z");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0_add185320 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_add183376_literalMutationString183643_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("s>imple.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimple_add183376__7 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String o_testSimple_add183376__14 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            java.lang.String o_testSimple_add183376__15 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            java.lang.String o_testSimple_add183376__16 = sw.toString();
            junit.framework.TestCase.fail("testSimple_add183376_literalMutationString183643 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template s>imple.html not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0null185489_failAssert0_add189252_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("+ke$Lr*<U)z");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489_failAssert0_add189252 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0_literalMutationString184577_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("+k$Lr*<U)z");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0_literalMutationString184577 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +k$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0_literalMutationString184577_failAssert0_add189428_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("+k$Lr*<U)z");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0_literalMutationString184577 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0_literalMutationString184577_failAssert0_add189428 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +k$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0_literalMutationString184603_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("+ke$Lr*<U)z");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.xt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0_literalMutationString184603 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0null185489_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("+ke$Lr*<U)z");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0null185489_failAssert0_literalMutationNumber187654_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("+ke$Lr*<U)z");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 5000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489_failAssert0_literalMutationNumber187654 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0null185489_failAssert0null189679_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("+ke$Lr*<U)z");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(null, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489_failAssert0null189679 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0null185489_failAssert0_literalMutationNumber187656_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("+ke$Lr*<U)z");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 1.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489_failAssert0_literalMutationNumber187656 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0null185489_failAssert0_add189251_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("+ke$Lr*<U)z");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString183345_failAssert0null185489_failAssert0_add189251 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString183345_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("+ke$Lr*<U)z");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimple_literalMutationString183345 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +ke$Lr*<U)z not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationNumber71176_literalMutationString74181_failAssert0_add86352_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                    com.github.mustachejava.Mustache m = c.compile("simple.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.io.Writer o_testSimpleI18N_literalMutationNumber71176__9 = m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    java.lang.String o_testSimpleI18N_literalMutationNumber71176__16 = com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
                    java.lang.String o_testSimpleI18N_literalMutationNumber71176__17 = sw.toString();
                }
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
                    com.github.mustachejava.Mustache m = c.compile("#imple.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.io.Writer o_testSimpleI18N_literalMutationNumber71176__26 = m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.8)));
                        }

                        boolean in_ca = true;
                    });
                    java.lang.String o_testSimpleI18N_literalMutationNumber71176__34 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    java.lang.String o_testSimpleI18N_literalMutationNumber71176__35 = sw.toString();
                }
                junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71176_literalMutationString74181 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71176_literalMutationString74181_failAssert0_add86352 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #imple.html not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationNumber71176_literalMutationString74181_failAssert0_literalMutationNumber84794_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                    com.github.mustachejava.Mustache m = c.compile("simple.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.io.Writer o_testSimpleI18N_literalMutationNumber71176__9 = m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    java.lang.String o_testSimpleI18N_literalMutationNumber71176__16 = com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
                    java.lang.String o_testSimpleI18N_literalMutationNumber71176__17 = sw.toString();
                }
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
                    com.github.mustachejava.Mustache m = c.compile("#imple.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.io.Writer o_testSimpleI18N_literalMutationNumber71176__26 = m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * -0.19999999999999996)));
                        }

                        boolean in_ca = true;
                    });
                    java.lang.String o_testSimpleI18N_literalMutationNumber71176__34 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    java.lang.String o_testSimpleI18N_literalMutationNumber71176__35 = sw.toString();
                }
                junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71176_literalMutationString74181 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71176_literalMutationString74181_failAssert0_literalMutationNumber84794 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #imple.html not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationNumber71136null78823_failAssert0_literalMutationString83608_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                    com.github.mustachejava.Mustache m = c.compile("Ow}M[x#%Hk!");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.io.Writer o_testSimpleI18N_literalMutationNumber71136__9 = m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10001;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    java.lang.String o_testSimpleI18N_literalMutationNumber71136__17 = com.github.mustachejava.TestUtil.getContents(this.root, null);
                    java.lang.String o_testSimpleI18N_literalMutationNumber71136__18 = sw.toString();
                }
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
                    com.github.mustachejava.Mustache m = c.compile("simple.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.io.Writer o_testSimpleI18N_literalMutationNumber71136__27 = m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    java.lang.String o_testSimpleI18N_literalMutationNumber71136__34 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    java.lang.String o_testSimpleI18N_literalMutationNumber71136__35 = sw.toString();
                }
                junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71136null78823 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71136null78823_failAssert0_literalMutationString83608 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Ow}M[x#%Hk! not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationNumber71176_literalMutationString74181_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                com.github.mustachejava.Mustache m = c.compile("simple.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_testSimpleI18N_literalMutationNumber71176__9 = m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                java.lang.String o_testSimpleI18N_literalMutationNumber71176__16 = com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
                java.lang.String o_testSimpleI18N_literalMutationNumber71176__17 = sw.toString();
            }
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
                com.github.mustachejava.Mustache m = c.compile("#imple.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_testSimpleI18N_literalMutationNumber71176__26 = m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.8)));
                    }

                    boolean in_ca = true;
                });
                java.lang.String o_testSimpleI18N_literalMutationNumber71176__34 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                java.lang.String o_testSimpleI18N_literalMutationNumber71176__35 = sw.toString();
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber71176_literalMutationString74181 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #imple.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("simplefi:ltered.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_literalMutationString231134_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("simplefi:ltered.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefilteed.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_literalMutationString231134 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_add235650_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("simplefi:ltered.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_add235650 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0_add236115_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("lI(%t_o$J,/pLWotg2c");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "siVplefiltered.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "siVplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0_add236115 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0_add236113_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    c.compile("lI(%t_o$J,/pLWotg2c");
                    com.github.mustachejava.Mustache m = c.compile("lI(%t_o$J,/pLWotg2c");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "siVplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0_add236113 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testSimpleFiltered_literalMutationString227668_failAssert0null229340_failAssert0_add235912_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended.replaceAll("^[\t ]+", "");
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("F`66(tE`O<X%J2YTZo#");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(null, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0null229340 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0null229340_failAssert0_add235912 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template F`66(tE`O<X%J2YTZo# not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0null229539_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", null);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("simplefi:ltered.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0null229539 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227668_failAssert0null229340_failAssert0_literalMutationString232979_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("F`66(tE`O<X%J2YTZo#");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(null, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0null229340 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0null229340_failAssert0_literalMutationString232979 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template F`66(tE`O<X%J2YTZo# not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227667_failAssert0_literalMutationString228815_failAssert0_literalMutationString231881_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("page1.txt", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("$-4fM{ Sa");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227667 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227667_failAssert0_literalMutationString228815 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227667_failAssert0_literalMutationString228815_failAssert0_literalMutationString231881 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template $-4fM{ Sa not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0null236509_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll(null, " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("simplefi:ltered.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0null236509 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("lI(%t_o$J,/pLWotg2c");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "siVplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testSimpleFiltered_literalMutationString227668_failAssert0null229340_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("F`66(tE`O<X%J2YTZo#");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0null229340 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template F`66(tE`O<X%J2YTZo# not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227668_failAssert0_add229168_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("F`66(tE`O<X%J2YTZo#");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0_add229168 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template F`66(tE`O<X%J2YTZo# not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0_literalMutationNumber234391_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("lI(%t_o$J,/pLWotg2c");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 1.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "siVplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0_literalMutationNumber234391 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_literalMutationNumber231113_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("simplefi:ltered.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 0;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_literalMutationNumber231113 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227668_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                    if (startOfLine) {
                        appended = appended.replaceAll("^[\t ]+", "");
                    }
                    return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                }
            };
            com.github.mustachejava.Mustache m = c.compile("F`66(tE`O<X%J2YTZo#");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template F`66(tE`O<X%J2YTZo# not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227668_failAssert0_literalMutationString227946_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("", "\n");
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("F`66(tE`O<X%J2YTZo#");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227668_failAssert0_literalMutationString227946 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template F`66(tE`O<X%J2YTZo# not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                    if (startOfLine) {
                        appended = appended.replaceAll("^[\t ]+", "");
                    }
                    return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                }
            };
            com.github.mustachejava.Mustache m = c.compile("simplefi:ltered.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0null236506_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("simplefi:ltered.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0null236506 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0null237061_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", null);
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("lI(%t_o$J,/pLWotg2c");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "siVplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227692_failAssert0_literalMutationString228787_failAssert0null237061 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_literalMutationString229140_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("simplefi:ltered.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "C1ris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_literalMutationString229140 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_add235648_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                            if (startOfLine) {
                                appended = appended.replaceAll("^[\t ]+", "");
                            }
                            return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                        }
                    };
                    c.compile("simplefi:ltered.html");
                    com.github.mustachejava.Mustache m = c.compile("simplefi:ltered.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString227669_failAssert0_add229329_failAssert0_add235648 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplefi:ltered.html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189983_failAssert0_add190351_failAssert0_add192078_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("6SVVy^$cw#g7#!", new java.lang.Object() {
                        java.lang.Object value = new java.lang.Object() {
                            boolean value = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                    sw.toString();
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurision_literalMutationString189983 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationString189983_failAssert0_add190351 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189983_failAssert0_add190351_failAssert0_add192078 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 6SVVy^$cw#g7#! not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189983_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("6SVVy^$cw#g7#!", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString189983 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 6SVVy^$cw#g7#! not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189982_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("recursion.|tml", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString189982 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion.|tml not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189983_failAssert0_add190351_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("6SVVy^$cw#g7#!", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString189983 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189983_failAssert0_add190351 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 6SVVy^$cw#g7#! not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189989_failAssert0_literalMutationString190135_failAssert0_literalMutationString191334_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("= &:8I86m", new java.lang.Object() {
                        java.lang.Object value = new java.lang.Object() {
                            boolean value = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "recudsion.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurision_literalMutationString189989 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationString189989_failAssert0_literalMutationString190135 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189989_failAssert0_literalMutationString190135_failAssert0_literalMutationString191334 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template = &:8I86m not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189982_failAssert0_literalMutationBoolean190179_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion.|tml", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = true;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString189982 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189982_failAssert0_literalMutationBoolean190179 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion.|tml not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189981_failAssert0_add190363_failAssert0_literalMutationString191203_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    execute("Dd^z)qTUD", new java.lang.Object() {
                        java.lang.Object value = new java.lang.Object() {
                            boolean value = false;
                        };
                    });
                    java.io.StringWriter sw = execute("page1.txt", new java.lang.Object() {
                        java.lang.Object value = new java.lang.Object() {
                            boolean value = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurision_literalMutationString189981 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationString189981_failAssert0_add190363 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189981_failAssert0_add190363_failAssert0_literalMutationString191203 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Dd^z)qTUD not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189982_failAssert0_add190339_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion.|tml", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString189982 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189982_failAssert0_add190339 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion.|tml not found", expected.getMessage());
        }
    }

    public void testRecurisionnull189999_failAssert0_literalMutationString190299_failAssert0_literalMutationString191472_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("]m33pC#4EnM[{@", new java.lang.Object() {
                        java.lang.Object value = new java.lang.Object() {
                            boolean value = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurisionnull189999 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testRecurisionnull189999_failAssert0_literalMutationString190299 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testRecurisionnull189999_failAssert0_literalMutationString190299_failAssert0_literalMutationString191472 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]m33pC#4EnM[{@ not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189982_failAssert0_add190339_failAssert0_literalMutationString191244_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("recursion.|tml", new java.lang.Object() {
                        java.lang.Object value = new java.lang.Object() {
                            boolean value = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "recursion.tt");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testRecurision_literalMutationString189982 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecurision_literalMutationString189982_failAssert0_add190339 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189982_failAssert0_add190339_failAssert0_literalMutationString191244 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion.|tml not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189983_failAssert0_literalMutationString190224_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("6SVVy^$cw#g7#!", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "m}8q6rv_=x,d[");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString189983 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189983_failAssert0_literalMutationString190224 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 6SVVy^$cw#g7#! not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString189982_failAssert0null190412_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion.|tml", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString189982 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString189982_failAssert0null190412 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion.|tml not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString300699_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("`_tk5Wh>{ZoZzRYR#4t[0ff>M]z0s./", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testRecursionWithInheritancenull300717_failAssert0_literalMutationString301014_failAssert0_literalMutationString302423_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("pa}ge1.txt", new java.lang.Object() {
                        java.lang.Object value = new java.lang.Object() {
                            boolean value = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRecursionWithInheritancenull300717 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testRecursionWithInheritancenull300717_failAssert0_literalMutationString301014 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritancenull300717_failAssert0_literalMutationString301014_failAssert0_literalMutationString302423 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template pa}ge1.txt not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString300699_failAssert0_literalMutationString300863_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("`_tk5Wh>{ZoZzRYR#4t[0ff>M]z0s./", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "MA+:7S=fX&Vpb");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699_failAssert0_literalMutationString300863 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testRecursionWithInheritance_literalMutationString300699_failAssert0null301123_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("`_tk5Wh>{ZoZzRYR#4t[0ff>M]z0s./", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699_failAssert0null301123 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testRecursionWithInheritance_literalMutationString300705_failAssert0_add301065_failAssert0_literalMutationString302370_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("{SV,,T0c.e[bk&C[Us(lgu^&jZ}.WHz", new java.lang.Object() {
                        java.lang.Object value = new java.lang.Object() {
                            boolean value = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300705 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300705_failAssert0_add301065 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300705_failAssert0_add301065_failAssert0_literalMutationString302370 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template {SV,,T0c.e[bk&C[Us(lgu^&jZ}.WHz not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_remove300715_literalMutationString300831_failAssert0_literalMutationString302147_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion_w=t[h_inheritance.html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                java.lang.String o_testRecursionWithInheritance_remove300715__10 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                java.lang.String o_testRecursionWithInheritance_remove300715__11 = sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_remove300715_literalMutationString300831 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_remove300715_literalMutationString300831_failAssert0_literalMutationString302147 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion_w=t[h_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString300699_failAssert0_add301047_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("`_tk5Wh>{ZoZzRYR#4t[0ff>M]z0s./", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300699_failAssert0_add301047 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testRecursionWithInheritance_literalMutationString300706_failAssert0_literalMutationString300967_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recu{sion_with_inheritance.html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300706 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300706_failAssert0_literalMutationString300967 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recu{sion_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_add300713_literalMutationString300803_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("recursion_wit^h_inheritance.html", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            sw.toString();
            java.lang.String o_testRecursionWithInheritance_add300713__12 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            java.lang.String o_testRecursionWithInheritance_add300713__13 = sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_add300713_literalMutationString300803 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion_wit^h_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString300710_failAssert0_literalMutationString300929_failAssert0_literalMutationString302463_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute(" does not exist", new java.lang.Object() {
                        java.lang.Object value = new java.lang.Object() {
                            boolean value = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "recursiopn.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300710 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300710_failAssert0_literalMutationString300929 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300710_failAssert0_literalMutationString300929_failAssert0_literalMutationString302463 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString300700_failAssert0null301129_failAssert0_literalMutationString302260_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute(" does not exist", new java.lang.Object() {
                        java.lang.Object value = new java.lang.Object() {
                            boolean value = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300700 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300700_failAssert0null301129 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300700_failAssert0null301129_failAssert0_literalMutationString302260 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString300708_failAssert0_literalMutationString300907_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion_wi`h_inheritance.html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursio.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300708 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString300708_failAssert0_literalMutationString300907 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion_wi`h_inheritance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0null183039_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<%tBkMLUCo`}#>J", new java.lang.Object() {
                        java.lang.Object test = new java.lang.Object() {
                            boolean test = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0null183039 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("recursive_partial_inheritance.html", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.io.StringWriter sw = execute("recursive_partial_inher}tance.html", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.lang.String o_testPartialRecursionWithInheritance_add180506__20 = com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            java.lang.String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursive_partial_inher}tance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0_add182807_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("recursive_partial_inheritance.html", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                execute("recursive_partial_inher}tance.html", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                java.io.StringWriter sw = execute("recursive_partial_inher}tance.html", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                java.lang.String o_testPartialRecursionWithInheritance_add180506__20 = com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                java.lang.String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0_add182807 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursive_partial_inher}tance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0_literalMutationString181913_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd", new java.lang.Object() {
                        java.lang.Object test = new java.lang.Object() {
                            boolean test = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "recursive_parWtial_inheritancehtxt");
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0_literalMutationString181913 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180496_failAssert0_literalMutationString180712_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("page:.txt", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180496 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180496_failAssert0_literalMutationString180712 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template page:.txt not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0null180936_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<%tBkMLUCo`}#>J", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0null180936 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_remove180509_literalMutationString180636_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("(flv/-`j73P5fJF]wC$FxD(jnF*`@A_@/6", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.lang.String o_testPartialRecursionWithInheritance_remove180509__10 = com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            java.lang.String o_testPartialRecursionWithInheritance_remove180509__11 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_remove180509_literalMutationString180636 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<%tBkMLUCo`}#>J", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180500_failAssert0null180926_failAssert0_literalMutationString182105_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("$9oC0V(mvTQBD=!+F4co]h[QN`1UsUh@vZ", new java.lang.Object() {
                        java.lang.Object test = new java.lang.Object() {
                            boolean test = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0null180926 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0null180926_failAssert0_literalMutationString182105 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template $9oC0V(mvTQBD=!+F4co]h[QN`1UsUh@vZ not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0_literalMutationString181918_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<tBkMLUCo`}#>J", new java.lang.Object() {
                        java.lang.Object test = new java.lang.Object() {
                            boolean test = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0_literalMutationString181918 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationBoolean180499_failAssert0_literalMutationString180777_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("r^ecursive_partial_inheritance.html", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = true;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationBoolean180499 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationBoolean180499_failAssert0_literalMutationString180777 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template r^ecursive_partial_inheritance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0_literalMutationString182305_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("recursive_partial_inheritance.html", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                java.io.StringWriter sw = execute("recursive_partial_inher}tance.[tml", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                java.lang.String o_testPartialRecursionWithInheritance_add180506__20 = com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                java.lang.String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0_literalMutationString182305 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursive_partial_inher}tance.[tml not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0null183098_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("recursive_partial_inheritance.html", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                java.io.StringWriter sw = execute("recursive_partial_inher}tance.html", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                java.lang.String o_testPartialRecursionWithInheritance_add180506__20 = com.github.mustachejava.TestUtil.getContents(this.root, null);
                java.lang.String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180602_failAssert0null183098 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursive_partial_inher}tance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_add180506_literalMutationString180595_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("{lg>!5LRpZ|//-tlHoW9Lb[]sCdXc-JE3!", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.io.StringWriter sw = execute("recursive_partial_inheritance.html", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.lang.String o_testPartialRecursionWithInheritance_add180506__20 = com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            java.lang.String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_literalMutationString180595 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_add180506_remove180896_literalMutationString181321_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("k*v[!VO-avzd#X;MlZ)ej-R #CxM/y=5zx", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.io.StringWriter sw = execute("recursive_partial_inheritance.html", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.lang.String o_testPartialRecursionWithInheritance_add180506__20 = com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            java.lang.String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_remove180896_literalMutationString181321 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_add180873_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<%tBkMLUCo`}#>J", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_add180873 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_add180506_remove180897_literalMutationString181582_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("recursive_partial_inheritance.html", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.io.StringWriter sw = execute("XHLCnXXzML[S[!JorW;jh8rMd1!Q2OEmjC", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.lang.String o_testPartialRecursionWithInheritance_add180506__20 = com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            java.lang.String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_remove180897_literalMutationString181582 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template XHLCnXXzML[S[!JorW;jh8rMd1!Q2OEmjC not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_parWtial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<%tBkMLUCo`}#>J", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0null183037_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd", new java.lang.Object() {
                        java.lang.Object test = new java.lang.Object() {
                            boolean test = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0null183037 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0_add182656_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("d9/6UQ(wk1!4rH0hZY#<%tBkMLUCo`}#>J", new java.lang.Object() {
                        java.lang.Object test = new java.lang.Object() {
                            boolean test = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180498_failAssert0_literalMutationString180768_failAssert0_add182656 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_add180506_remove180895_literalMutationString181504_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter o_testPartialRecursionWithInheritance_add180506__1 = execute("PT<2F,@XS4;#%uR&s;ob(c]HU6W2m28hX9", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.io.StringWriter sw = execute("recursive_partial_inheritance.html", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.lang.String o_testPartialRecursionWithInheritance_add180506__20 = com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            java.lang.String o_testPartialRecursionWithInheritance_add180506__21 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add180506_remove180895_literalMutationString181504 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template PT<2F,@XS4;#%uR&s;ob(c]HU6W2m28hX9 not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180501_failAssert0null180924_failAssert0_literalMutationString182404_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("/)K6Egx9JMY_TQ!0b(/?%J6>hmJFs$cz)e", new java.lang.Object() {
                        java.lang.Object test = new java.lang.Object() {
                            boolean test = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180501 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180501_failAssert0null180924 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180501_failAssert0null180924_failAssert0_literalMutationString182404 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritancenull180512_failAssert0_add180888_failAssert0_literalMutationString181748_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("lRyO<!k.2qR)c>9uC:T(%9cqAW&3%Xeo:M", new java.lang.Object() {
                        java.lang.Object test = new java.lang.Object() {
                            boolean test = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritancenull180512 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritancenull180512_failAssert0_add180888 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritancenull180512_failAssert0_add180888_failAssert0_literalMutationString181748 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template lRyO<!k.2qR)c>9uC:T(%9cqAW&3%Xeo:M not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0_add182651_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd", new java.lang.Object() {
                        java.lang.Object test = new java.lang.Object() {
                            boolean test = false;
                        };
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "recursive_parWtial_inheritance.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "recursive_parWtial_inheritance.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString180500_failAssert0_literalMutationString180697_failAssert0_add182651 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template F[<|JVsNKQ#9}oc|pRQ[Ya!!Zft%zPTKrd not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chzis";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0null208050_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0null208050 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0_add207879_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_add207879 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_remove205946_literalMutationString206774_failAssert0_literalMutationString210076_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("l,C}1Mju}z}vSa{/g");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_testSimplePragma_remove205946__7 = m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                java.lang.String o_testSimplePragma_remove205946__13 = com.github.mustachejava.TestUtil.getContents(this.root, "simplo.txt");
                java.lang.String o_testSimplePragma_remove205946__14 = sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_remove205946_literalMutationString206774 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_remove205946_literalMutationString206774_failAssert0_literalMutationString210076 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0null212048_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chzis";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0null212048 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0_literalMutationString209876_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "jb>ty";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0_literalMutationString209876 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0_add211626_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("oFF&vnmG$&8VU1]`5");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chzis";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString205910_failAssert0_literalMutationString207092_failAssert0_add211626 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template oFF&vnmG$&8VU1]`5 not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257765_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("simple.h[ml");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                java.lang.Object o = new java.lang.Object() {
                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    java.lang.String fred = "test";
                };

                java.lang.Object in_ca = java.util.Arrays.asList(this.o, new java.lang.Object() {
                    int taxed_value = ((int) ((this.value) - ((this.value) * 0.2)));
                }, this.o);
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simplerewrap.txt");
            sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257765 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simple.h[ml not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257764_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("sbohEm2mxp}");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                java.lang.Object o = new java.lang.Object() {
                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    java.lang.String fred = "test";
                };

                java.lang.Object in_ca = java.util.Arrays.asList(this.o, new java.lang.Object() {
                    int taxed_value = ((int) ((this.value) - ((this.value) * 0.2)));
                }, this.o);
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simplerewrap.txt");
            sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template sbohEm2mxp} not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257764_failAssert0null261378_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("sbohEm2mxp}");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    java.lang.Object o = new java.lang.Object() {
                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        java.lang.String fred = "test";
                    };

                    java.lang.Object in_ca = java.util.Arrays.asList(this.o, new java.lang.Object() {
                        int taxed_value = ((int) ((this.value) - ((this.value) * 0.2)));
                    }, this.o);
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764_failAssert0null261378 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template sbohEm2mxp} not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257764_failAssert0_literalMutationString259919_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("sbohEm2mxp}");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    java.lang.Object o = new java.lang.Object() {
                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        java.lang.String fred = "tst";
                    };

                    java.lang.Object in_ca = java.util.Arrays.asList(this.o, new java.lang.Object() {
                        int taxed_value = ((int) ((this.value) - ((this.value) * 0.2)));
                    }, this.o);
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplerewrap.txt");
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764_failAssert0_literalMutationString259919 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template sbohEm2mxp} not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257771_remove261143_literalMutationString262463_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("si<ple.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testMultipleWrappers_literalMutationString257771__7 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chis";

                int value = 10000;

                java.lang.Object o = new java.lang.Object() {
                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    java.lang.String fred = "test";
                };

                java.lang.Object in_ca = java.util.Arrays.asList(this.o, new java.lang.Object() {
                    int taxed_value = ((int) ((this.value) - ((this.value) * 0.2)));
                }, this.o);
            });
            java.lang.String o_testMultipleWrappers_literalMutationString257771__23 = com.github.mustachejava.TestUtil.getContents(this.root, "simplerewrap.txt");
            java.lang.String o_testMultipleWrappers_literalMutationString257771__24 = sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257771_remove261143_literalMutationString262463 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template si<ple.html not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257764_failAssert0_add261066_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("sbohEm2mxp}");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    java.lang.Object o = new java.lang.Object() {
                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        java.lang.String fred = "test";
                    };

                    java.lang.Object in_ca = java.util.Arrays.asList(this.o, new java.lang.Object() {
                        int taxed_value = ((int) ((this.value) - ((this.value) * 0.2)));
                    }, this.o);
                });
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    java.lang.Object o = new java.lang.Object() {
                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        java.lang.String fred = "test";
                    };

                    java.lang.Object in_ca = java.util.Arrays.asList(this.o, new java.lang.Object() {
                        int taxed_value = ((int) ((this.value) - ((this.value) * 0.2)));
                    }, this.o);
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplerewrap.txt");
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257764_failAssert0_add261066 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template sbohEm2mxp} not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationNumber257792_literalMutationString258472_literalMutationString262087_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("TV 4&3F$djm");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testMultipleWrappers_literalMutationNumber257792__7 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Ch[is";

                int value = 10000;

                java.lang.Object o = new java.lang.Object() {
                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    java.lang.String fred = "test";
                };

                java.lang.Object in_ca = java.util.Arrays.asList(this.o, new java.lang.Object() {
                    int taxed_value = ((int) ((this.value) - ((this.value) * 0.0)));
                }, this.o);
            });
            java.lang.String o_testMultipleWrappers_literalMutationNumber257792__24 = com.github.mustachejava.TestUtil.getContents(this.root, "simplerewrap.txt");
            java.lang.String o_testMultipleWrappers_literalMutationNumber257792__25 = sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationNumber257792_literalMutationString258472_literalMutationString262087 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template TV 4&3F$djm not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257765_failAssert0_literalMutationString259820_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("simple.h[ml");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chxis";

                    int value = 10000;

                    java.lang.Object o = new java.lang.Object() {
                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        java.lang.String fred = "test";
                    };

                    java.lang.Object in_ca = java.util.Arrays.asList(this.o, new java.lang.Object() {
                        int taxed_value = ((int) ((this.value) - ((this.value) * 0.2)));
                    }, this.o);
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplerewrap.txt");
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257765 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257765_failAssert0_literalMutationString259820 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simple.h[ml not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString257765_failAssert0null261372_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("simple.h[ml");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    java.lang.Object o = new java.lang.Object() {
                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        java.lang.String fred = "test";
                    };

                    java.lang.Object in_ca = java.util.Arrays.asList(this.o, new java.lang.Object() {
                        int taxed_value = ((int) ((this.value) - ((this.value) * 0.2)));
                    }, this.o);
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257765 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString257765_failAssert0null261372 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simple.h[ml not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationNumber257794_literalMutationString258802_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("s^imple.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testMultipleWrappers_literalMutationNumber257794__7 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                java.lang.Object o = new java.lang.Object() {
                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    java.lang.String fred = "test";
                };

                java.lang.Object in_ca = java.util.Arrays.asList(this.o, new java.lang.Object() {
                    int taxed_value = ((int) ((this.value) - ((this.value) * 0.1)));
                }, this.o);
            });
            java.lang.String o_testMultipleWrappers_literalMutationNumber257794__24 = com.github.mustachejava.TestUtil.getContents(this.root, "simplerewrap.txt");
            java.lang.String o_testMultipleWrappers_literalMutationNumber257794__25 = sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationNumber257794_literalMutationString258802 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template s^imple.html not found", expected.getMessage());
        }
    }

    public void testNestedLatchesIterable_add170825_literalMutationString171145_failAssert0_literalMutationString178382_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                com.github.mustachejava.Mustache m = c.compile(" does not exist");
                java.io.StringWriter sw = new java.io.StringWriter();
                final java.lang.StringBuffer sb = new java.lang.StringBuffer();
                final java.util.concurrent.CountDownLatch cdl1 = new java.util.concurrent.CountDownLatch(1);
                final java.util.concurrent.CountDownLatch cdl2 = new java.util.concurrent.CountDownLatch(1);
                m.execute(sw, new java.lang.Object() {
                    java.lang.Iterable list = java.util.Arrays.asList(() -> {
                        cdl1.await();
                        sb.append("How");
                        return "How";
                    }, ((java.util.concurrent.Callable<java.lang.Object>) (() -> {
                        cdl2.await();
                        sb.append("are");
                        cdl1.countDown();
                        return "are";
                    })), () -> {
                        sb.append("you?");
                        cdl2.countDown();
                        return "you?";
                    });
                }).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "latchedtest.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "latchedtest.txt");
                sw.toString();
                sb.toString();
                junit.framework.TestCase.fail("testNestedLatchesIterable_add170825_literalMutationString171145 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatchesIterable_add170825_literalMutationString171145_failAssert0_literalMutationString178382 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11033_failAssert0_add14946_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                com.github.mustachejava.Mustache m = c.compile("U>5MBP=$slrccq3[");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer execute = m.execute(sw, new java.lang.Object() {
                    java.util.concurrent.Callable<java.lang.Object> nest = () -> {
                        java.lang.Thread.sleep(300);
                        return "How";
                    };

                    java.util.concurrent.Callable<java.lang.Object> nested = () -> {
                        java.lang.Thread.sleep(200);
                        return "are";
                    };

                    java.util.concurrent.Callable<java.lang.Object> nestest = () -> {
                        java.lang.Thread.sleep(100);
                        return "you?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033_failAssert0_add14946 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template U>5MBP=$slrccq3[ not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11033_failAssert0_literalMutationString13522_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                com.github.mustachejava.Mustache m = c.compile("U>5MBP=$slrccq3[");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer execute = m.execute(sw, new java.lang.Object() {
                    java.util.concurrent.Callable<java.lang.Object> nest = () -> {
                        java.lang.Thread.sleep(300);
                        return "Ahs";
                    };

                    java.util.concurrent.Callable<java.lang.Object> nested = () -> {
                        java.lang.Thread.sleep(200);
                        return "are";
                    };

                    java.util.concurrent.Callable<java.lang.Object> nestest = () -> {
                        java.lang.Thread.sleep(100);
                        return "you?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033_failAssert0_literalMutationString13522 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template U>5MBP=$slrccq3[ not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11043_literalMutationString12532_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            com.github.mustachejava.Mustache m = c.compile("u>36},DQq6o<D.L_");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer execute = m.execute(sw, new java.lang.Object() {
                java.util.concurrent.Callable<java.lang.Object> nest = () -> {
                    java.lang.Thread.sleep(300);
                    return "{A9";
                };

                java.util.concurrent.Callable<java.lang.Object> nested = () -> {
                    java.lang.Thread.sleep(200);
                    return "are";
                };

                java.util.concurrent.Callable<java.lang.Object> nestest = () -> {
                    java.lang.Thread.sleep(100);
                    return "you?";
                };
            });
            execute.close();
            java.lang.String o_testNestedLatches_literalMutationString11043__25 = sw.toString();
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11043_literalMutationString12532 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template u>36},DQq6o<D.L_ not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                com.github.mustachejava.Mustache m = c.compile("latchedt1est].html");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer execute = m.execute(sw, new java.lang.Object() {
                    java.util.concurrent.Callable<java.lang.Object> nest = () -> {
                        java.lang.Thread.sleep(300);
                        return "How";
                    };

                    java.util.concurrent.Callable<java.lang.Object> nested = () -> {
                        java.lang.Thread.sleep(200);
                        return "are";
                    };

                    java.util.concurrent.Callable<java.lang.Object> nestest = () -> {
                        java.lang.Thread.sleep(100);
                        return "you?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template latchedt1est].html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755_failAssert0_add21082_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                    c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                    com.github.mustachejava.Mustache m = c.compile("latchedt1est].html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.io.Writer execute = m.execute(sw, new java.lang.Object() {
                        java.util.concurrent.Callable<java.lang.Object> nest = () -> {
                            java.lang.Thread.sleep(300);
                            return "How";
                        };

                        java.util.concurrent.Callable<java.lang.Object> nested = () -> {
                            java.lang.Thread.sleep(200);
                            return "are";
                        };

                        java.util.concurrent.Callable<java.lang.Object> nestest = () -> {
                            java.lang.Thread.sleep(100);
                            return "you?";
                        };
                    });
                    execute.close();
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755_failAssert0_add21082 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template latchedt1est].html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11033_failAssert0null15454_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                com.github.mustachejava.Mustache m = c.compile("U>5MBP=$slrccq3[");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer execute = m.execute(null, new java.lang.Object() {
                    java.util.concurrent.Callable<java.lang.Object> nest = () -> {
                        java.lang.Thread.sleep(300);
                        return "How";
                    };

                    java.util.concurrent.Callable<java.lang.Object> nested = () -> {
                        java.lang.Thread.sleep(200);
                        return "are";
                    };

                    java.util.concurrent.Callable<java.lang.Object> nestest = () -> {
                        java.lang.Thread.sleep(100);
                        return "you?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033_failAssert0null15454 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template U>5MBP=$slrccq3[ not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755_failAssert0_literalMutationNumber18485_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                    c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                    com.github.mustachejava.Mustache m = c.compile("latchedt1est].html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    java.io.Writer execute = m.execute(sw, new java.lang.Object() {
                        java.util.concurrent.Callable<java.lang.Object> nest = () -> {
                            java.lang.Thread.sleep(300);
                            return "How";
                        };

                        java.util.concurrent.Callable<java.lang.Object> nested = () -> {
                            java.lang.Thread.sleep(100);
                            return "are";
                        };

                        java.util.concurrent.Callable<java.lang.Object> nestest = () -> {
                            java.lang.Thread.sleep(100);
                            return "you?";
                        };
                    });
                    execute.close();
                    sw.toString();
                    junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11032_failAssert0_literalMutationString13755_failAssert0_literalMutationNumber18485 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template latchedt1est].html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString11033_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            com.github.mustachejava.Mustache m = c.compile("U>5MBP=$slrccq3[");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer execute = m.execute(sw, new java.lang.Object() {
                java.util.concurrent.Callable<java.lang.Object> nest = () -> {
                    java.lang.Thread.sleep(300);
                    return "How";
                };

                java.util.concurrent.Callable<java.lang.Object> nested = () -> {
                    java.lang.Thread.sleep(200);
                    return "are";
                };

                java.util.concurrent.Callable<java.lang.Object> nestest = () -> {
                    java.lang.Thread.sleep(100);
                    return "you?";
                };
            });
            execute.close();
            sw.toString();
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString11033 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template U>5MBP=$slrccq3[ not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87210_failAssert0_add87893_failAssert0_literalMutationString89114_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.lang.Object object = new java.lang.Object() {
                        java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                    };
                    execute("`0qr:Z!&u", object);
                    java.io.StringWriter sw = execute("page1.txt", object);
                    com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87210 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87210_failAssert0_add87893 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87210_failAssert0_add87893_failAssert0_literalMutationString89114 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `0qr:Z!&u not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0_add90408_failAssert0() throws java.io.IOException {
        try {
            {
                java.util.Collections.singletonList("Tevst");
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Tevst");
                };
                java.io.StringWriter sw = execute("[Qa%0&zprtsx", object);
                java.lang.String o_testIsNotEmpty_literalMutationString87206__9 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                java.lang.String o_testIsNotEmpty_literalMutationString87206__10 = sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0_add90408 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [Qa%0&zprtsx not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0null90916_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Tevst");
                };
                java.io.StringWriter sw = execute("[Qa%0&zprtsx", null);
                java.lang.String o_testIsNotEmpty_literalMutationString87206__9 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                java.lang.String o_testIsNotEmpty_literalMutationString87206__10 = sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0null90916 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [Qa%0&zprtsx not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87207_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("z85`(vdwgDUK", object);
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87207 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z85`(vdwgDUK not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87214_failAssert0_literalMutationString87611_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("I!jUw4q4>GXp", object);
                com.github.mustachejava.TestUtil.getContents(this.root, "t;6y.BLRi_}");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87214 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87214_failAssert0_literalMutationString87611 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template I!jUw4q4>GXp not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_add87221_literalMutationString87384_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("IIM3qHQjzVq{", object);
            java.lang.String o_testIsNotEmpty_add87221__9 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            java.lang.String o_testIsNotEmpty_add87221__10 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            java.lang.String o_testIsNotEmpty_add87221__11 = sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_add87221_literalMutationString87384 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template IIM3qHQjzVq{ not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Tevst");
            };
            java.io.StringWriter sw = execute("[Qa%0&zprtsx", object);
            java.lang.String o_testIsNotEmpty_literalMutationString87206__9 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            java.lang.String o_testIsNotEmpty_literalMutationString87206__10 = sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [Qa%0&zprtsx not found", expected.getMessage());
        }
    }

    public void testIsNotEmptynull87224_add87846_literalMutationString88582_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList(null);
            };
            java.io.StringWriter o_testIsNotEmptynull87224_add87846__7 = execute(")O vhw}Ux$2&", object);
            java.io.StringWriter sw = execute("isempty.html", object);
            java.lang.String o_testIsNotEmptynull87224__9 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            java.lang.String o_testIsNotEmptynull87224__10 = sw.toString();
            junit.framework.TestCase.fail("testIsNotEmptynull87224_add87846_literalMutationString88582 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template )O vhw}Ux$2& not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87209_failAssert0_add87878_failAssert0_literalMutationString90082_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.lang.Object object = new java.lang.Object() {
                        java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                    };
                    java.io.StringWriter sw = execute("isemty.ht}ml", object);
                    com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87209 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87209_failAssert0_add87878 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87209_failAssert0_add87878_failAssert0_literalMutationString90082 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template isemty.ht}ml not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87205null87967_failAssert0_literalMutationString89816_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("vest");
                };
                java.io.StringWriter sw = execute("X!tvd{?yEQ.8", object);
                java.lang.String o_testIsNotEmpty_literalMutationString87205__9 = com.github.mustachejava.TestUtil.getContents(this.root, null);
                java.lang.String o_testIsNotEmpty_literalMutationString87205__10 = sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87205null87967 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87205null87967_failAssert0_literalMutationString89816 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template X!tvd{?yEQ.8 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0_literalMutationString89460_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Tevst");
                };
                java.io.StringWriter sw = execute("[Qa%0&zprtsx", object);
                java.lang.String o_testIsNotEmpty_literalMutationString87206__9 = com.github.mustachejava.TestUtil.getContents(this.root, "");
                java.lang.String o_testIsNotEmpty_literalMutationString87206__10 = sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87206_literalMutationString87508_failAssert0_literalMutationString89460 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [Qa%0&zprtsx not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString87207_failAssert0_add87879_failAssert0() throws java.io.IOException {
        try {
            {
                java.util.Collections.singletonList("Test");
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("z85`(vdwgDUK", object);
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87207 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString87207_failAssert0_add87879 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z85`(vdwgDUK not found", expected.getMessage());
        }
    }

    public void testImmutableList_add140633_literalMutationString140722_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.util.List<java.lang.Object> o_testImmutableList_add140633__7 = java.util.Collections.singletonList(object);
            java.io.StringWriter sw = execute("isempt|y.html", java.util.Collections.singletonList(object));
            java.lang.String o_testImmutableList_add140633__11 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            java.lang.String o_testImmutableList_add140633__12 = sw.toString();
            junit.framework.TestCase.fail("testImmutableList_add140633_literalMutationString140722 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template isempt|y.html not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140623_failAssert0_literalMutationString141105_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("]qmNxZO#WC4-", java.util.Collections.singletonList(object));
                com.github.mustachejava.TestUtil.getContents(this.root, "3:F3oU])5``");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString140623 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140623_failAssert0_literalMutationString141105 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]qmNxZO#WC4- not found", expected.getMessage());
        }
    }

    public void testImmutableList_add140632_literalMutationString140795_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter o_testImmutableList_add140632__7 = execute("#z&I+4x7{:k0", java.util.Collections.singletonList(object));
            java.io.StringWriter sw = execute("isempty.html", java.util.Collections.singletonList(object));
            java.lang.String o_testImmutableList_add140632__12 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            java.lang.String o_testImmutableList_add140632__13 = sw.toString();
            junit.framework.TestCase.fail("testImmutableList_add140632_literalMutationString140795 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #z&I+4x7{:k0 not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140623_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("]qmNxZO#WC4-", java.util.Collections.singletonList(object));
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140623 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]qmNxZO#WC4- not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140628_failAssert0_add141326_failAssert0_literalMutationString143453_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.util.Collections.singletonList("Test");
                    java.lang.Object object = new java.lang.Object() {
                        java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                    };
                    java.io.StringWriter sw = execute("isempty.[html", java.util.Collections.singletonList(object));
                    com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testImmutableList_literalMutationString140628 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testImmutableList_literalMutationString140628_failAssert0_add141326 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140628_failAssert0_add141326_failAssert0_literalMutationString143453 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template isempty.[html not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140623_failAssert0_literalMutationString141095_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Teast");
                };
                java.io.StringWriter sw = execute("]qmNxZO#WC4-", java.util.Collections.singletonList(object));
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString140623 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140623_failAssert0_literalMutationString141095 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]qmNxZO#WC4- not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140624_failAssert0_literalMutationString141117_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("vr0Ns_n}Om[", java.util.Collections.singletonList(object));
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString140624 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140624_failAssert0_literalMutationString141117 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template vr0Ns_n}Om[ not found", expected.getMessage());
        }
    }

    public void testImmutableList_add140632_add141244_literalMutationString141885_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter o_testImmutableList_add140632__7 = execute("2x`#o5G;H5x,", java.util.Collections.singletonList(object));
            ((java.io.StringWriter) (o_testImmutableList_add140632__7)).getBuffer().toString();
            java.io.StringWriter sw = execute("isempty.html", java.util.Collections.singletonList(object));
            java.lang.String o_testImmutableList_add140632__12 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            java.lang.String o_testImmutableList_add140632__13 = sw.toString();
            junit.framework.TestCase.fail("testImmutableList_add140632_add141244_literalMutationString141885 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 2x`#o5G;H5x, not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140623_failAssert0_add141339_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.util.Collections.singletonList(object);
                java.io.StringWriter sw = execute("]qmNxZO#WC4-", java.util.Collections.singletonList(object));
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString140623 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140623_failAssert0_add141339 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]qmNxZO#WC4- not found", expected.getMessage());
        }
    }

    public void testImmutableList_add140632_add141244_literalMutationString141892_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter o_testImmutableList_add140632__7 = execute("isempty.html", java.util.Collections.singletonList(object));
            ((java.io.StringWriter) (o_testImmutableList_add140632__7)).getBuffer().toString();
            java.io.StringWriter sw = execute("kvk$a)em%Jht", java.util.Collections.singletonList(object));
            java.lang.String o_testImmutableList_add140632__12 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            java.lang.String o_testImmutableList_add140632__13 = sw.toString();
            junit.framework.TestCase.fail("testImmutableList_add140632_add141244_literalMutationString141892 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template kvk$a)em%Jht not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString140628_failAssert0_literalMutationString141065_failAssert0_literalMutationString142903_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    java.lang.Object object = new java.lang.Object() {
                        java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                    };
                    java.io.StringWriter sw = execute(":LMi0j%9^q41", java.util.Collections.singletonList(object));
                    com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testImmutableList_literalMutationString140628 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testImmutableList_literalMutationString140628_failAssert0_literalMutationString141065 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString140628_failAssert0_literalMutationString141065_failAssert0_literalMutationString142903 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template :LMi0j%9^q41 not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString220094_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile(")Hb/ aesK*+tp");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private java.lang.String test = "Test";
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "security.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString220094 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testSecurity_literalMutationString220116_remove222742_literalMutationString224543_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("security.ht<ml");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSecurity_literalMutationString220116__7 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private java.lang.String test = "page1.txt";
            });
            java.lang.String o_testSecurity_literalMutationString220116__15 = com.github.mustachejava.TestUtil.getContents(this.root, "security.txt");
            java.lang.String o_testSecurity_literalMutationString220116__16 = sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString220116_remove222742_literalMutationString224543 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template security.ht<ml not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString220117_literalMutationString220743_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("3B||$2PI^3?ye");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSecurity_literalMutationString220117__7 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private java.lang.String test = "T&est";
            });
            java.lang.String o_testSecurity_literalMutationString220117__15 = com.github.mustachejava.TestUtil.getContents(this.root, "security.txt");
            java.lang.String o_testSecurity_literalMutationString220117__16 = sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString220117_literalMutationString220743 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 3B||$2PI^3?ye not found", expected.getMessage());
        }
    }

    public void testProperties_add241207_literalMutationString241337_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache o_testProperties_add241207__3 = c.compile("EFg]ACL# ??");
            com.github.mustachejava.Mustache m = c.compile("simple.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testProperties_add241207__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String getName() {
                    return "Chris";
                }

                int getValue() {
                    return 10000;
                }

                int taxed_value() {
                    return ((int) ((this.getValue()) - ((this.getValue()) * 0.4)));
                }

                boolean isIn_ca() {
                    return true;
                }
            });
            java.lang.String o_testProperties_add241207__23 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            java.lang.String o_testProperties_add241207__24 = sw.toString();
            junit.framework.TestCase.fail("testProperties_add241207_literalMutationString241337 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template EFg]ACL# ?? not found", expected.getMessage());
        }
    }

    public void testProperties_add241206_remove243407_literalMutationString244676_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.DefaultMustacheFactory o_testProperties_add241206__1 = createMustacheFactory();
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("Zx8(EE{meXr");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testProperties_add241206__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String getName() {
                    return "Chris";
                }

                int getValue() {
                    return 10000;
                }

                int taxed_value() {
                    return ((int) ((this.getValue()) - ((this.getValue()) * 0.4)));
                }

                boolean isIn_ca() {
                    return true;
                }
            });
            java.lang.String o_testProperties_add241206__23 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            java.lang.String o_testProperties_add241206__24 = sw.toString();
            junit.framework.TestCase.fail("testProperties_add241206_remove243407_literalMutationString244676 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Zx8(EE{meXr not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString56692_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            java.io.StringWriter sw = execute("T ln,&?]&>n", new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                }
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString56692 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template T ln,&?]&>n not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                com.github.mustachejava.Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192833null193136_failAssert0_literalMutationString193835_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache o_testPartialWithTF_add192833__3 = c.compile("YaUvnwp&s.,-nTpMB@^u72PP`|;i6J");
                com.github.mustachejava.Mustache m = c.compile("partialintemplatefunction.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_testPartialWithTF_add192833__8 = m.execute(null, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                java.lang.String o_testPartialWithTF_add192833__15 = sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_add192833null193136 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_add192833null193136_failAssert0_literalMutationString193835 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template YaUvnwp&s.,-nTpMB@^u72PP`|;i6J not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192828_failAssert0_literalMutationString192945_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile(" does not exist");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192828 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192828_failAssert0_literalMutationString192945 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0_add194111_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    com.github.mustachejava.Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        public com.github.mustachejava.TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0_add194111 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192834_literalMutationString192888_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("7]!0Zg[:gT QJx]X(x?%#bHJa>i kX");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_add192834__7 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            java.io.Writer o_testPartialWithTF_add192834__14 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            java.lang.String o_testPartialWithTF_add192834__21 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add192834_literalMutationString192888 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 7]!0Zg[:gT QJx]X(x?%#bHJa>i kX not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add192835_literalMutationString192903_failAssert0_literalMutationString193664_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("`#LZr_?AN7T8UF(%_E34>,+l;,Ov$pF");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_testPartialWithTF_add192835__7 = m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                java.lang.String o_testPartialWithTF_add192835__15 = sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_add192835_literalMutationString192903 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_add192835_literalMutationString192903_failAssert0_literalMutationString193664 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `#LZr_?AN7T8UF(%_E34>,+l;,Ov$pF not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0_literalMutationString193552_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("?`]e8TsQrZ6*TVjtGxLXLb||ETdBJ^?");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        public com.github.mustachejava.TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0_literalMutationString193552 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ?`]e8TsQrZ6*TVjtGxLXLb||ETdBJ^? not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add192833_literalMutationString192876_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache o_testPartialWithTF_add192833__3 = c.compile("HCrI*./sby K-&sl$xa:p}vk`Om)to");
            com.github.mustachejava.Mustache m = c.compile("partialintemplatefunction.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_add192833__8 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            java.lang.String o_testPartialWithTF_add192833__15 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192833_literalMutationString192876_failAssert0_add194136_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache o_testPartialWithTF_add192833__3 = c.compile("HCrI*./sby K-&sl$xa:p}vk`Om)to");
                com.github.mustachejava.Mustache m = c.compile("partialintemplatefunction.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                java.io.Writer o_testPartialWithTF_add192833__8 = m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                java.lang.String o_testPartialWithTF_add192833__15 = sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876_failAssert0_add194136 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192832_literalMutationString192898_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory o_testPartialWithTF_add192832__1 = createMustacheFactory();
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("%GgF MoRARw/@_w4w|9Q7YUTb`Ez.l");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_add192832__8 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            java.lang.String o_testPartialWithTF_add192832__15 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add192832_literalMutationString192898 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192835_literalMutationString192901_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("/]G=([#&3j9JZM[W%e&[D7Hx]sLBlR");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_add192835__7 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            java.lang.String o_testPartialWithTF_add192835__15 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add192835_literalMutationString192901 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192833_literalMutationString192876_failAssert0_literalMutationString193539_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache o_testPartialWithTF_add192833__3 = c.compile("HCrI*./sby K-&sl$xa:p}vkOm)to");
                com.github.mustachejava.Mustache m = c.compile("partialintemplatefunction.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_testPartialWithTF_add192833__8 = m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                java.lang.String o_testPartialWithTF_add192833__15 = sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876_failAssert0_literalMutationString193539 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("partialinte3mplatefunctio|.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template partialinte3mplatefunctio|.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0null194549_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("partialinte3mplatefunctio|.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(null, new java.lang.Object() {
                        public com.github.mustachejava.TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0null194549 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template partialinte3mplatefunctio|.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add192833_literalMutationString192876_failAssert0null194546_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache o_testPartialWithTF_add192833__3 = c.compile("HCrI*./sby K-&sl$xa:p}vk`Om)to");
                com.github.mustachejava.Mustache m = c.compile(null);
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_testPartialWithTF_add192833__8 = m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                java.lang.String o_testPartialWithTF_add192833__15 = sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192876_failAssert0null194546 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0_add194405_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(null, new java.lang.Object() {
                        public com.github.mustachejava.TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0_add194405 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0_add194406_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    com.github.mustachejava.Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(null, new java.lang.Object() {
                        public com.github.mustachejava.TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0_add194406 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0_add194141_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("partialinte3mplatefunctio|.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        public com.github.mustachejava.TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0_add194141 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template partialinte3mplatefunctio|.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0null194536_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    com.github.mustachejava.Mustache m = c.compile(null);
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        public com.github.mustachejava.TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0null194536 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192835_literalMutationString192903_failAssert0_literalMutationString193663_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("partial%ntemplatefunctionn.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_testPartialWithTF_add192835__7 = m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                java.lang.String o_testPartialWithTF_add192835__15 = sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_add192835_literalMutationString192903 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_add192835_literalMutationString192903_failAssert0_literalMutationString193663 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template partial%ntemplatefunctionn.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0_literalMutationString193771_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("K]&b@$aH|Wn4wsY%!o!v`SZdGH@6PM[");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(null, new java.lang.Object() {
                        public com.github.mustachejava.TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0null193156_failAssert0_literalMutationString193771 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_add192833_literalMutationString192885_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache o_testPartialWithTF_add192833__3 = c.compile("partialintemplatefunction.html");
            com.github.mustachejava.Mustache m = c.compile("iaT.$!$I(wL ^ Id)/OX%?c97]o,cH");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_add192833__8 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            java.lang.String o_testPartialWithTF_add192833__15 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add192833_literalMutationString192885 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0_literalMutationString193517_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    c.compile("K]&b@$aH|Wn4wsY!o!vSZdGH@6PM[");
                    com.github.mustachejava.Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        public com.github.mustachejava.TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193109_failAssert0_literalMutationString193517 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0_literalMutationString192942_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("K]&b@$aH|Wn4wsY!v!v`SZdGH@6PM[");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_literalMutationString192942 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTF_literalMutationString192831_failAssert0_add193110_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("K]&b@$aH|Wn4wsY!o!v`SZdGH@6PM[");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192831_failAssert0_add193110 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialWithTFnull192839_failAssert0_literalMutationString192953_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("1!oMMJ9h^Yc-Q`! 5(xnWn{bQ$(Jhy");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTFnull192839 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTFnull192839_failAssert0_literalMutationString192953 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1!oMMJ9h^Yc-Q`! 5(xnWn{bQ$(Jhy not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0_add194139_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    c.compile("partialinte3mplatefunctio|.html");
                    com.github.mustachejava.Mustache m = c.compile("partialinte3mplatefunctio|.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        public com.github.mustachejava.TemplateFunction i() {
                            return ( s) -> s;
                        }
                    });
                    sw.toString();
                    junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString192830_failAssert0_literalMutationString192923_failAssert0_add194139 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template partialinte3mplatefunctio|.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329012_failAssert0_literalMutationString329682_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("mz/|GePiHa@H");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329012 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329012_failAssert0_literalMutationString329682 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0_literalMutationString332073_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter json = new java.io.StringWriter();
                    com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                    final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                    jg.writeStartObject();
                    final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("complex.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new com.github.mustachejava.ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                    sw = new java.io.StringWriter();
                    m = createMustacheFactory().compile("z6w;<Z!R<q)C");
                    m.execute(sw, o);
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0_literalMutationString332073 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z6w;<Z!R<q)C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321214_failAssert0_add322438_failAssert0_literalMutationString323997_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter json = new java.io.StringWriter();
                    com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                    final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                    jg.writeStartObject();
                    final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("M:]0RfL?s/w ");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new com.github.mustachejava.ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                    sw = new java.io.StringWriter();
                    m = createMustacheFactory().compile("complx.html");
                    m.execute(sw, o);
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString321214 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString321214_failAssert0_add322438 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString321214_failAssert0_add322438_failAssert0_literalMutationString323997 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testComplex_literalMutationString329007_failAssert0_add330217_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("comple:x.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0_add330217 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_add330536_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_add330536 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329007_failAssert0_add330211_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                createMustacheFactory().compile("comple:x.html");
                m = createMustacheFactory().compile("comple:x.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0_add330211 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("z6w;<Z!R<q)9C");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z6w;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329007_failAssert0null330833_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("comple:x.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0null330833 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329007_failAssert0_literalMutationString329318_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("comple:x.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0_literalMutationString329318 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString328994_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter json = new java.io.StringWriter();
            com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
            final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                }
            };
            com.github.mustachejava.Mustache m = c.compile("ycl.MQ[]jfQo");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ComplexObject());
            jg.writeEndObject();
            jg.flush();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
            sw = new java.io.StringWriter();
            m = createMustacheFactory().compile("complex.html");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString328994 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ycl.MQ[]jfQo not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_literalMutationString329797_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329797 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329007_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter json = new java.io.StringWriter();
            com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
            final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                }
            };
            com.github.mustachejava.Mustache m = c.compile("complex.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ComplexObject());
            jg.writeEndObject();
            jg.flush();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
            sw = new java.io.StringWriter();
            m = createMustacheFactory().compile("comple:x.html");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321218_failAssert0_add322497_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile(",nH{z_-2i@G[");
                m.execute(sw, o);
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString321218 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString321218_failAssert0_add322497 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ,nH{z_-2i@G[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329007_failAssert0_literalMutationString329315_failAssert0_add335212_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter json = new java.io.StringWriter();
                    com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                    final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                    jg.writeStartObject();
                    final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("complex.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new com.github.mustachejava.ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                    sw = new java.io.StringWriter();
                    m = createMustacheFactory().compile("comple:x.html");
                    m.execute(sw, o);
                    com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                    sw.toString();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0_literalMutationString329315 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0_literalMutationString329315_failAssert0_add335212 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplexnull321254_failAssert0_literalMutationString322192_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.h%ml");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile(null);
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexnull321254 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull321254_failAssert0_literalMutationString322192 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex.h%ml not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329009_failAssert0_literalMutationString329712_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("(=Xh_kl06],1");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("page1.txt");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329009 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329009_failAssert0_literalMutationString329712 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template (=Xh_kl06],1 not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321218_failAssert0_literalMutationString321637_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile(",nH{z_-2i@G[");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString321218 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString321218_failAssert0_literalMutationString321637 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ,nH{z_-2i@G[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0_add334232_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter json = new java.io.StringWriter();
                    com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                    final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                    jg.writeStartObject();
                    final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("complex.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new com.github.mustachejava.ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                    sw = new java.io.StringWriter();
                    m = createMustacheFactory().compile("z6w;<Z!R<q)9C");
                    m.execute(sw, o);
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0_add334232 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z6w;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0null331012_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0_add334230_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter json = new java.io.StringWriter();
                    com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                    final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                    jg.writeStartObject();
                    final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("complex.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new com.github.mustachejava.ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                    sw = new java.io.StringWriter();
                    m = createMustacheFactory().compile("z6w;<Z!R<q)9C");
                    m.execute(sw, o);
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_literalMutationString329788_failAssert0_add334230 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z6w;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321218_failAssert0_literalMutationString321637_failAssert0_add327612_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter json = new java.io.StringWriter();
                    com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                    final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                    jg.writeStartObject();
                    final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("complex.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new com.github.mustachejava.ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    jf.createJsonParser(json.toString());
                    com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                    sw = new java.io.StringWriter();
                    m = createMustacheFactory().compile(",nH{z_-2i@G[");
                    m.execute(sw, o);
                    com.github.mustachejava.TestUtil.getContents(this.root, "");
                    sw.toString();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString321218 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString321218_failAssert0_literalMutationString321637 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString321218_failAssert0_literalMutationString321637_failAssert0_add327612 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ,nH{z_-2i@G[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0null331012_failAssert0null336006_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter json = new java.io.StringWriter();
                    com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                    final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                    jg.writeStartObject();
                    final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("complex.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new com.github.mustachejava.ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                    sw = new java.io.StringWriter();
                    m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                    m.execute(null, o);
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012_failAssert0null336006 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321203_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter json = new java.io.StringWriter();
            com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
            final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                }
            };
            com.github.mustachejava.Mustache m = c.compile("{AEcOm|mS17Q");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ComplexObject());
            jg.writeEndObject();
            jg.flush();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
            sw = new java.io.StringWriter();
            m = createMustacheFactory().compile("complex.html");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString321203 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template {AEcOm|mS17Q not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321218_failAssert0_add322500_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile(",nH{z_-2i@G[");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString321218 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString321218_failAssert0_add322500 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ,nH{z_-2i@G[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0null331012_failAssert0_literalMutationString332721_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter json = new java.io.StringWriter();
                    com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                    final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                    jg.writeStartObject();
                    final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("complex.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new com.github.mustachejava.ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                    sw = new java.io.StringWriter();
                    m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                    m.execute(sw, o);
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.tt");
                    sw.toString();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012_failAssert0_literalMutationString332721 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplexnull321253_failAssert0null323297_failAssert0_literalMutationString325942_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter json = new java.io.StringWriter();
                    com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                    final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                    jg.writeStartObject();
                    final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("ByR}i@Vd *W.");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new com.github.mustachejava.ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(null);
                    sw = new java.io.StringWriter();
                    m = createMustacheFactory().compile("complex.html");
                    m.execute(sw, o);
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexnull321253 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testComplexnull321253_failAssert0null323297 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull321253_failAssert0null323297_failAssert0_literalMutationString325942 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ByR}i@Vd *W. not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329007_failAssert0_literalMutationString329315_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("comple:x.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329007_failAssert0_literalMutationString329315 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template comple:x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter json = new java.io.StringWriter();
            com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
            final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                }
            };
            com.github.mustachejava.Mustache m = c.compile("complex.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ComplexObject());
            jg.writeEndObject();
            jg.flush();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
            sw = new java.io.StringWriter();
            m = createMustacheFactory().compile("z6;<Z!R<q)9C");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0null331012_failAssert0_add334692_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter json = new java.io.StringWriter();
                    com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                    final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                    jg.writeStartObject();
                    final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile("complex.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new com.github.mustachejava.ComplexObject());
                    jg.writeEndObject();
                    jg.flush();
                    jg.flush();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                    java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                    sw = new java.io.StringWriter();
                    m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                    m.execute(sw, o);
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0null331012_failAssert0_add334692 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString321218_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter json = new java.io.StringWriter();
            com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
            final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                }
            };
            com.github.mustachejava.Mustache m = c.compile("complex.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ComplexObject());
            jg.writeEndObject();
            jg.flush();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
            sw = new java.io.StringWriter();
            m = createMustacheFactory().compile(",nH{z_-2i@G[");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString321218 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ,nH{z_-2i@G[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString329008_failAssert0_add330547_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter json = new java.io.StringWriter();
                com.fasterxml.jackson.databind.MappingJsonFactory jf = new com.fasterxml.jackson.databind.MappingJsonFactory();
                final com.fasterxml.jackson.core.JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final com.github.mustachejavabenchmarks.JsonCapturer captured = new com.github.mustachejavabenchmarks.JsonCapturer(jg);
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.util.CapturingMustacheVisitor(this, captured);
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("complex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("z6;<Z!R<q)9C");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString329008 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString329008_failAssert0_add330547 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z6;<Z!R<q)9C not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97304_failAssert0_literalMutationString97540_failAssert0_literalMutationString98577_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = initParallel();
                    com.github.mustachejava.Mustache m = c.compile(" does not exist");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString97304 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97304_failAssert0_literalMutationString97540 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97304_failAssert0_literalMutationString97540_failAssert0_literalMutationString98577 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testComplexParallel_add97316_literalMutationString97386_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache o_testComplexParallel_add97316__3 = c.compile("complex.html");
            com.github.mustachejava.Mustache m = c.compile("G_6Y3I6!14K ");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            java.lang.String o_testComplexParallel_add97316__11 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            java.lang.String o_testComplexParallel_add97316__12 = sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_add97316_literalMutationString97386 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template G_6Y3I6!14K  not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97306_failAssert0null97951_failAssert0_literalMutationString98921_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = initParallel();
                    com.github.mustachejava.Mustache m = c.compile(" does not exist");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(null, new com.github.mustachejava.ParallelComplexObject()).close();
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testComplexParallel_literalMutationString97306 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97306_failAssert0null97951 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97306_failAssert0null97951_failAssert0_literalMutationString98921 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97308_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("w`%JTg?*(FnB");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template w`%JTg?*(FnB not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97308_failAssert0_add97862_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("w`%JTg?*(FnB");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308_failAssert0_add97862 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template w`%JTg?*(FnB not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97308_failAssert0null97966_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("w`%JTg?*(FnB");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308_failAssert0null97966 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template w`%JTg?*(FnB not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97309_failAssert0_literalMutationString97498_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("/`f&KMb;eJ3K");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97309 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97309_failAssert0_literalMutationString97498 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testComplexParallel_literalMutationString97308_failAssert0_literalMutationString97574_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("w`%JTg?*(FnB");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "A,^;pJvEJ,o");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97308_failAssert0_literalMutationString97574 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template w`%JTg?*(FnB not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97311_failAssert0_literalMutationString97483_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("6MjfTsl>}Cew");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "q7^}{:y-;c`");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97311 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97311_failAssert0_literalMutationString97483 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 6MjfTsl>}Cew not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString97307_failAssert0_literalMutationString97530_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("2>V_Ne%z>NH|&");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString97307 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString97307_failAssert0_literalMutationString97530 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 2>V_Ne%z>NH|& not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("-BbRi|JOmT%*", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0_add112303_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("coplex.[html", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0_add112303 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template coplex.[html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(";e0 SE`|5JY|", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ;e0 SE`|5JY| not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110520_failAssert0_literalMutationString110654_failAssert0_literalMutationString111591_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute(",Pz[].dZQM$c", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110520 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110520_failAssert0_literalMutationString110654 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110520_failAssert0_literalMutationString110654_failAssert0_literalMutationString111591 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ,Pz[].dZQM$c not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110674_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("-BbRi|<(OmT%*", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110674 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|<(OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0_add112322_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    execute(";e0 SE`|5JY|", new com.github.mustachejava.ParallelComplexObject());
                    java.io.StringWriter sw = execute(";e0 SE`|5JY|", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0_add112322 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ;e0 SE`|5JY| not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0null112753_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("-BbRi|JOmT%*", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0null112753 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_literalMutationString112021_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("-BbRi|JOmT%*", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "cAmplex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_literalMutationString112021 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_add112508_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    execute("-BbRi|JOmT%*", new com.github.mustachejava.ParallelComplexObject());
                    java.io.StringWriter sw = execute("-BbRi|JOmT%*", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_add112508 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_add112510_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("-BbRi|JOmT%*", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_add112510 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_literalMutationString112014_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("-BRi|JOmT%*", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_literalMutationString112014 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723_failAssert0_add112483_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("c7mpl x.html", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723_failAssert0_add112483 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template c7mpl x.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0_add112445_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    execute("-BbRi|<OmT%*", new com.github.mustachejava.ParallelComplexObject());
                    execute("-BbRi|<OmT%*", new com.github.mustachejava.ParallelComplexObject());
                    java.io.StringWriter sw = execute("-BbRi|<OmT%*", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0_add112445 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0null112665_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("coplex.[html", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0null112665 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template coplex.[html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723_failAssert0_literalMutationString111940_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("c7mpl x.html", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723_failAssert0_literalMutationString111940 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template c7mpl x.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0_literalMutationString111824_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    execute("-BbRi|<OmT%*", new com.github.mustachejava.ParallelComplexObject());
                    java.io.StringWriter sw = execute("", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0_literalMutationString111824 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("c7mpl x.html", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0_literalMutationString110723 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template c7mpl x.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110515_failAssert0null110858_failAssert0_literalMutationString112080_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("c7mpl>x.html", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0null110858 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110515_failAssert0null110858_failAssert0_literalMutationString112080 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template c7mpl>x.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("coplex.[html", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template coplex.[html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                execute("-BbRi|<OmT%*", new com.github.mustachejava.ParallelComplexObject());
                java.io.StringWriter sw = execute("-BbRi|<OmT%*", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110516_failAssert0_literalMutationString110746_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("Jf=nekdAn81|", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110516 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110516_failAssert0_literalMutationString110746 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Jf=nekdAn81| not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0_literalMutationString111520_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("coplex.[html", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "UyD(*0KgsrX");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110512_failAssert0_literalMutationString110660_failAssert0_literalMutationString111520 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template coplex.[html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0_literalMutationString111568_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute(";e0 SE]`|5JY|", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0_literalMutationString111568 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ;e0 SE]`|5JY| not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0null112719_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    execute("-BbRi|<OmT%*", new com.github.mustachejava.ParallelComplexObject());
                    java.io.StringWriter sw = execute("-BbRi|<OmT%*", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110797_failAssert0null112719 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110517_failAssert0_literalMutationString110707_failAssert0_literalMutationString112006_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("h`nXJSO6Nu<)", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.Itxt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110517 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110517_failAssert0_literalMutationString110707 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110517_failAssert0_literalMutationString110707_failAssert0_literalMutationString112006 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template h`nXJSO6Nu<) not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_add112509_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("-BbRi|JOmT%*", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_add112509 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallablenull110526_failAssert0_literalMutationString110755_failAssert0_literalMutationString111928_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("[=ZZ$as^g26E", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallablenull110526 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testSerialCallablenull110526_failAssert0_literalMutationString110755 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallablenull110526_failAssert0_literalMutationString110755_failAssert0_literalMutationString111928 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [=ZZ$as^g26E not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter sw = execute("-BbRi|<OmT%*", new com.github.mustachejava.ParallelComplexObject());
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110517_failAssert0_literalMutationString110707_failAssert0_literalMutationString112004_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute(";+tpO[4q%21D", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "complex.Itxt");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110517 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110517_failAssert0_literalMutationString110707 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110517_failAssert0_literalMutationString110707_failAssert0_literalMutationString112004 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ;+tpO[4q%21D not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0null110850_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("-BbRi|<OmT%*", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0null110850 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_add110800_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("-BbRi|<OmT%*", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_add110800 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|<OmT%* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0null112675_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute(";e0 SE`|5JY|", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110518_failAssert0_literalMutationString110686_failAssert0null112675 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ;e0 SE`|5JY| not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_literalMutationString112019_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    java.io.StringWriter sw = execute("-BbRi|JOmT%*", new com.github.mustachejava.ParallelComplexObject());
                    com.github.mustachejava.TestUtil.getContents(this.root, "");
                    sw.toString();
                    junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString110510_failAssert0_literalMutationString110672_failAssert0_literalMutationString112019 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -BbRi|JOmT%* not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120781_failAssert0_literalMutationString123451_failAssert0_literalMutationString133909_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "#", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("", "simple");
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "s|mple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120781 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120781_failAssert0_literalMutationString123451 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120781_failAssert0_literalMutationString123451_failAssert0_literalMutationString133909 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120699_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                        @java.lang.Override
                        public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                            if (variable.startsWith("+")) {
                                com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                    @java.lang.Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "9", "]");
                                        if ((partial) == null) {
                                            throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                    @java.lang.Override
                                    public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                        java.io.StringWriter sw = new java.io.StringWriter();
                                        partial.execute(sw, scopes);
                                        com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        java.io.Writer execute = mustache.execute(writer, scopes);
                                        return appendText(execute);
                                    }
                                });
                            } else {
                                super.partial(tc, variable);
                            }
                        }
                    };
                }
            };
            com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120699 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", null);
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + .html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationString121646_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.Mxt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationString121646 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_add135477_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        variable.substring(0).trim();
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_add135477 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_literalMutationString133880_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 0);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "sim]ple");
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_literalMutationString133880 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + sim]ple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartialnull120837_failAssert0_literalMutationString122255_failAssert0_literalMutationString129492_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "G", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(null, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "");
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartialnull120837 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDynamicPartialnull120837_failAssert0_literalMutationString122255 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartialnull120837_failAssert0_literalMutationString122255_failAssert0_literalMutationString129492 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0_literalMutationBoolean123555_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", false);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_literalMutationBoolean123555 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0null124288_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124288 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                        @java.lang.Override
                        public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                            if (variable.startsWith("+")) {
                                com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                    @java.lang.Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                    @java.lang.Override
                                    public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                        java.io.StringWriter sw = new java.io.StringWriter();
                                        partial.execute(sw, scopes);
                                        com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        java.io.Writer execute = mustache.execute(writer, scopes);
                                        return appendText(execute);
                                    }
                                });
                            } else {
                                super.partial(tc, variable);
                            }
                        }
                    };
                }
            };
            com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                        @java.lang.Override
                        public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                            if (variable.startsWith("+")) {
                                com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                    @java.lang.Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                    @java.lang.Override
                                    public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                        java.io.StringWriter sw = new java.io.StringWriter();
                                        partial.execute(sw, scopes);
                                        com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        java.io.Writer execute = mustache.execute(writer, scopes);
                                        return appendText(execute);
                                    }
                                });
                            } else {
                                super.partial(tc, variable);
                            }
                        }
                    };
                }
            };
            com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120679_failAssert0_add123642_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("page1.txt")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679_failAssert0_add123642 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_add135469_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        variable.substring(0).trim();
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                mustache.execute(writer, scopes);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_add135469 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0null140077_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 0);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", null);
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0null140077 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + .html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_add137291_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 0);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_add137291 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0null140071_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", null);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0null140071 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120679_failAssert0_add123642_failAssert0null138053_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("page1.txt")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 10000);
                            put(null, 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679_failAssert0_add123642 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679_failAssert0_add123642_failAssert0null138053 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120679_failAssert0null124243_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("page1.txt")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(null);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679_failAssert0null124243 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    variable.substring(0).trim();
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 0);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_literalMutationString133834_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("na1me", "Chris");
                            put("value", 0);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_literalMutationString133834 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0null139147_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", null);
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0null139147 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + .html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0_add136460_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    variable.startsWith("+");
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", null);
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0_add136460 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + .html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_add137273_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        variable.substring(0);
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 0);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_literalMutationNumber121605_failAssert0_add137273 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120699_failAssert0null124757_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "9", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120699 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120699_failAssert0null124757 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartialnull120848_failAssert0_literalMutationNumber122473_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartialnull120848 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDynamicPartialnull120848_failAssert0_literalMutationNumber122473 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_add123678_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_add123678 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120776_failAssert0_literalMutationString121684_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+K [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120776 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120776_failAssert0_literalMutationString121684 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template K simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0_add123677_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    tc.startOfLine();
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0_add123677 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120679_failAssert0_literalMutationString121366_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("page1.txt")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo]html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679_failAssert0_literalMutationString121366 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo]html.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_literalMutationString126580_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        variable.substring(0).trim();
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_literalMutationString126580 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120679_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                        @java.lang.Override
                        public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                            if (variable.startsWith("page1.txt")) {
                                com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                    @java.lang.Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                    @java.lang.Override
                                    public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                        java.io.StringWriter sw = new java.io.StringWriter();
                                        partial.execute(sw, scopes);
                                        com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        java.io.Writer execute = mustache.execute(writer, scopes);
                                        return appendText(execute);
                                    }
                                });
                            } else {
                                super.partial(tc, variable);
                            }
                        }
                    };
                }
            };
            com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120679 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0_literalMutationNumber130555_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("name", "Chris");
                            put("value", 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", null);
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120688_failAssert0null124315_failAssert0_literalMutationNumber130555 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + .html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_literalMutationString126527_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                        @java.lang.Override
                        public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                            return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                                @java.lang.Override
                                public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                    if (variable.startsWith("+")) {
                                        com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                        variable.substring(0).trim();
                                        list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(0).trim()) {
                                            @java.lang.Override
                                            public synchronized void init() {
                                                filterText();
                                                partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                                if ((partial) == null) {
                                                    throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                                }
                                            }

                                            java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                            @java.lang.Override
                                            public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                                java.io.StringWriter sw = new java.io.StringWriter();
                                                partial.execute(sw, scopes);
                                                com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                                java.io.Writer execute = mustache.execute(writer, scopes);
                                                return appendText(execute);
                                            }
                                        });
                                    } else {
                                        super.partial(tc, variable);
                                    }
                                }
                            };
                        }
                    };
                    com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                        {
                            put("nme", "Chris");
                            put("value", 10000);
                            put("taxed_value", 6000);
                            put("in_ca", true);
                            put("foo", "simple");
                        }
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber120689_failAssert0_add124150_failAssert0_literalMutationString126527 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString120699_failAssert0_add124092_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "9", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        java.util.concurrent.ConcurrentMap<java.lang.String, com.github.mustachejava.Mustache> dynamicaPartialCache = new java.util.concurrent.ConcurrentHashMap<>();

                                        @java.lang.Override
                                        public java.io.Writer execute(java.io.Writer writer, java.util.List<java.lang.Object> scopes) {
                                            java.io.StringWriter sw = new java.io.StringWriter();
                                            partial.execute(sw, scopes);
                                            com.github.mustachejava.Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            java.io.Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120699 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString120699_failAssert0_add124092 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38343_failAssert0_literalMutationString38611_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("?>C/5Dj}8Zw");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38343 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38343_failAssert0_literalMutationString38611 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testReadme_literalMutationString38344_failAssert0_add38920_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("|tems.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38344 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38344_failAssert0_add38920 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template |tems.html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38342_failAssert0_add38928_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                createMustacheFactory();
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("YwxBrK0_V>");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38342 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0_add38928 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template YwxBrK0_V> not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38341_failAssert0_literalMutationString38562_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile(" does not exist");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38341 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38341_failAssert0_literalMutationString38562 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38350_failAssert0null39057_failAssert0_literalMutationString40013_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("{y9ZXB^u{.");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    long start = java.lang.System.currentTimeMillis();
                    m.execute(null, new com.github.mustachejava.AmplInterpreterTest.Context());
                    long diff = (java.lang.System.currentTimeMillis()) - start;
                    com.github.mustachejava.TestUtil.getContents(this.root, "item.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38350 should have thrown FileNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38350_failAssert0null39057 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38350_failAssert0null39057_failAssert0_literalMutationString40013 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template {y9ZXB^u{. not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38342_failAssert0null39046_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("YwxBrK0_V>");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38342 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0null39046 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template YwxBrK0_V> not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38345_failAssert0_literalMutationString38640_failAssert0_literalMutationString40135_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("H}fJwO#W+");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    long start = java.lang.System.currentTimeMillis();
                    m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                    long diff = (java.lang.System.currentTimeMillis()) - start;
                    com.github.mustachejava.TestUtil.getContents(this.root, "iteDms.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38345 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38345_failAssert0_literalMutationString38640 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38345_failAssert0_literalMutationString38640_failAssert0_literalMutationString40135 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template H}fJwO#W+ not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38342_failAssert0_literalMutationString38599_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("YwxBAK0_V>");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38342 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0_literalMutationString38599 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template YwxBAK0_V> not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38344_failAssert0null39039_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("|tems.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(null, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString38344 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38344_failAssert0null39039 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template |tems.html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38342_failAssert0_add38928_failAssert0_add40672_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("YwxBrK0_V>");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    long start = java.lang.System.currentTimeMillis();
                    m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                    long diff = (java.lang.System.currentTimeMillis()) - start;
                    com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38342 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0_add38928 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0_add38928_failAssert0_add40672 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template YwxBrK0_V> not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38342_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("YwxBrK0_V>");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString38342 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template YwxBrK0_V> not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38344_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("|tems.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString38344 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template |tems.html not found", expected.getMessage());
        }
    }

    public void testReadme_add38354_literalMutationString38481_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("xsD:fLW=d>");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.lang.System.currentTimeMillis();
            long start = java.lang.System.currentTimeMillis();
            java.io.Writer o_testReadme_add38354__10 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            java.lang.String o_testReadme_add38354__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            java.lang.String o_testReadme_add38354__15 = sw.toString();
            junit.framework.TestCase.fail("testReadme_add38354_literalMutationString38481 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template xsD:fLW=d> not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString38342_failAssert0_add38928_failAssert0_literalMutationString39626_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                    com.github.mustachejava.Mustache m = c.compile("YwxBrK0_V>");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    long start = java.lang.System.currentTimeMillis();
                    m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                    long diff = (java.lang.System.currentTimeMillis()) - start;
                    com.github.mustachejava.TestUtil.getContents(this.root, "=9j!m#7nX");
                    sw.toString();
                    junit.framework.TestCase.fail("testReadme_literalMutationString38342 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0_add38928 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString38342_failAssert0_add38928_failAssert0_literalMutationString39626 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template YwxBrK0_V> not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString5776_failAssert0_literalMutationString6290_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("`-7OnlC$yi[");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_35 = "d-fD5Vc?FXt(-b`l}A)v%,52OUR8Dv`PX2M;3l;#P]Im" + diff;
                boolean boolean_36 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776_failAssert0_literalMutationString6290 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `-7OnlC$yi[ not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add5789_literalMutationNumber6005() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache o_testReadmeSerial_add5789__3 = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeSerial_add5789__3)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeSerial_add5789__3)).getName());
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add5789__10 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_add5789__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__14);
        java.lang.String o_testReadmeSerial_add5789__15 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__15);
        java.lang.String String_17 = "Should be a little bit more than 4 seconds: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_17);
        boolean boolean_18 = (diff > 3999) && (diff < 0);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeSerial_add5789__3)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeSerial_add5789__3)).getName());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__14);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_17);
    }

    public void testReadmeSerial_literalMutationString5776_failAssert0null6784_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("`-7OnlC$yi[");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                java.lang.String String_35 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_36 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776_failAssert0null6784 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `-7OnlC$yi[ not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString5776_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("`-7OnlC$yi[");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_35 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_36 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `-7OnlC$yi[ not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add5789() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache o_testReadmeSerial_add5789__3 = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeSerial_add5789__3)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeSerial_add5789__3)).getName());
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add5789__10 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadmeSerial_add5789__10)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((java.io.StringWriter) (o_testReadmeSerial_add5789__10)).toString());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_add5789__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__14);
        java.lang.String o_testReadmeSerial_add5789__15 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__15);
        java.lang.String String_17 = "Should be a little bit more than 4 seconds: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4002", String_17);
        boolean boolean_18 = (diff > 3999) && (diff < 6000);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeSerial_add5789__3)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeSerial_add5789__3)).getName());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadmeSerial_add5789__10)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((java.io.StringWriter) (o_testReadmeSerial_add5789__10)).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__14);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add5789__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4002", String_17);
    }

    public void testReadmeSerial_literalMutationString5776_failAssert0_add6685_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("`-7OnlC$yi[");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_35 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_36 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776_failAssert0_add6685 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `-7OnlC$yi[ not found", expected.getMessage());
        }
    }

    public void testReadmeSerialnull5798_failAssert0_literalMutationString6453_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("jX-#0kdpXI#");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                java.lang.String String_47 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_48 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerialnull5798 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmeSerialnull5798_failAssert0_literalMutationString6453 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template jX-#0kdpXI# not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString5776_failAssert0_literalMutationNumber6293_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("`-7OnlC$yi[");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_35 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_36 = (diff > 0) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776_failAssert0_literalMutationNumber6293 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `-7OnlC$yi[ not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString5776_failAssert0_add6688_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("`-7OnlC$yi[");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                sw.toString();
                java.lang.String String_35 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_36 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString5776_failAssert0_add6688 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `-7OnlC$yi[ not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add275035_add276003() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add275035__16 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275035__16);
        java.lang.String o_testReadmeParallel_add275035_add276003__21 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275035_add276003__21);
        java.lang.String String_135 = "Should be a little bit more than 1 second: " + diff;
        boolean boolean_136 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275035__16);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275035_add276003__21);
    }

    public void testReadmeParallel_literalMutationString275024_failAssert0_add276124_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                initParallel();
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("items2.h}tml");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024_failAssert0_add276124 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template items2.h}tml not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString275024_failAssert0_add276126_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("items2.h}tml");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.lang.System.currentTimeMillis();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024_failAssert0_add276126 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template items2.h}tml not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add275033_literalMutationString275307_remove280068() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add275033__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275033__15);
        java.lang.String o_testReadmeParallel_add275033__16 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275033__16);
        java.lang.String String_133 = "page1.txt" + diff;
        junit.framework.TestCase.assertEquals("page1.txt1002", String_133);
        boolean boolean_134 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275033__15);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add275033__16);
        junit.framework.TestCase.assertEquals("page1.txt1002", String_133);
    }

    public void testReadmeParallel_add275036null276197_failAssert0_literalMutationString278195_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("i{ems2.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                java.lang.System.currentTimeMillis();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                java.lang.String o_testReadmeParallel_add275036__15 = com.github.mustachejava.TestUtil.getContents(this.root, null);
                java.lang.String o_testReadmeParallel_add275036__16 = sw.toString();
                java.lang.String String_123 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_124 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_add275036null276197 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_add275036null276197_failAssert0_literalMutationString278195 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template i{ems2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString275024_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("items2.h}tml");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_156 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template items2.h}tml not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString275024_failAssert0_literalMutationString275651_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("items2.hk}tml");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString275024_failAssert0_literalMutationString275651 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template items2.hk}tml not found", expected.getMessage());
        }
    }

    public void testReadmeParallelnull275042_failAssert0_literalMutationString275773_failAssert0_literalMutationString277861_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = initParallel();
                    com.github.mustachejava.Mustache m = c.compile("zT&FVklr8{U");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    long start = java.lang.System.currentTimeMillis();
                    m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                    long diff = (java.lang.System.currentTimeMillis()) - start;
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    java.lang.String String_163 = "Should be a lit+tle bit more than 1 second: " + diff;
                    boolean boolean_164 = (diff > 999) && (diff < 2000);
                    junit.framework.TestCase.fail("testReadmeParallelnull275042 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testReadmeParallelnull275042_failAssert0_literalMutationString275773 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmeParallelnull275042_failAssert0_literalMutationString275773_failAssert0_literalMutationString277861 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template zT&FVklr8{U not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString105641_failAssert0_literalMutationString106054_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile("xW-g$Cbswk8M ");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "deferred[.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString105641 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString105641_failAssert0_literalMutationString106054 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template xW-g$Cbswk8M  not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString105641_failAssert0_add106619_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile("xW-g$Cbswk8M ");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context);
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString105641 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString105641_failAssert0_add106619 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template xW-g$Cbswk8M  not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString105641_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
            mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            java.lang.Object context = new java.lang.Object() {
                java.lang.String title = "Deferred";

                java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
            };
            com.github.mustachejava.Mustache m = mf.compile("xW-g$Cbswk8M ");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, context).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString105641 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template xW-g$Cbswk8M  not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString105641_failAssert0_literalMutationString106054_failAssert0_add109537_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                    java.util.concurrent.Executors.newCachedThreadPool();
                    mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                    java.lang.Object context = new java.lang.Object() {
                        java.lang.String title = "Deferred";

                        java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                        java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                    };
                    com.github.mustachejava.Mustache m = mf.compile("xW-g$Cbswk8M ");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, context).close();
                    com.github.mustachejava.TestUtil.getContents(this.root, "deferred[.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testDeferred_literalMutationString105641 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testDeferred_literalMutationString105641_failAssert0_literalMutationString106054 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString105641_failAssert0_literalMutationString106054_failAssert0_add109537 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template xW-g$Cbswk8M  not found", expected.getMessage());
        }
    }

    public void testDeferred_add105654_literalMutationString105777_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
            mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            java.lang.Object context = new java.lang.Object() {
                java.lang.String title = "Deferred";

                java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
            };
            com.github.mustachejava.Mustache m = mf.compile("nT[eK(S$9#`c!");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, context);
            m.execute(sw, context).close();
            java.lang.String o_testDeferred_add105654__18 = com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_add105654_literalMutationString105777 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template nT[eK(S$9#`c! not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237442_failAssert0null238127_failAssert0_literalMutationString239066_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache compile = mf.compile("1Czd4jXF27LBPQQX$Sf] ZQ&x<");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    compile.execute(sw, new java.lang.Object() {
                        java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                            @java.lang.Override
                            public java.lang.String apply(java.lang.String s) {
                                return s;
                            }
                        };
                    }).close();
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237442 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237442_failAssert0null238127 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237442_failAssert0null238127_failAssert0_literalMutationString239066 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1Czd4jXF27LBPQQX$Sf] ZQ&x< not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237443_failAssert0null238103_failAssert0_literalMutationString239301_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache compile = mf.compile("relative/f[ncti:npaths.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    compile.execute(sw, new java.lang.Object() {
                        java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                            @java.lang.Override
                            public java.lang.String apply(java.lang.String s) {
                                return s;
                            }
                        };
                    }).close();
                    com.github.mustachejava.TestUtil.getContents(this.root, null);
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237443 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237443_failAssert0null238103 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237443_failAssert0null238103_failAssert0_literalMutationString239301 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237440_failAssert0_add237943_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
                com.github.mustachejava.Mustache compile = mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
                java.io.StringWriter sw = new java.io.StringWriter();
                compile.execute(sw, new java.lang.Object() {
                    java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                        @java.lang.Override
                        public java.lang.String apply(java.lang.String s) {
                            return s;
                        }
                    };
                }).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0_add237943 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_add237451_literalMutationString237553_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory o_testRelativePathsTemplateFunction_add237451__1 = createMustacheFactory();
            com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
            com.github.mustachejava.Mustache compile = mf.compile("(.NF+qb]IpQn1J-,az,++rpEq0H");
            java.io.StringWriter sw = new java.io.StringWriter();
            compile.execute(sw, new java.lang.Object() {
                java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                    @java.lang.Override
                    public java.lang.String apply(java.lang.String s) {
                        return s;
                    }
                };
            }).close();
            java.lang.String o_testRelativePathsTemplateFunction_add237451__20 = com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
            java.lang.String o_testRelativePathsTemplateFunction_add237451__21 = sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_add237451_literalMutationString237553 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template (.NF+qb]IpQn1J-,az,++rpEq0H not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237440_failAssert0_literalMutationString237657_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
                java.io.StringWriter sw = new java.io.StringWriter();
                compile.execute(sw, new java.lang.Object() {
                    java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                        @java.lang.Override
                        public java.lang.String apply(java.lang.String s) {
                            return s;
                        }
                    };
                }).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths2txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0_literalMutationString237657 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096_failAssert0_literalMutationString239195_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache compile = mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    compile.execute(null, new java.lang.Object() {
                        java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                            @java.lang.Override
                            public java.lang.String apply(java.lang.String s) {
                                return s;
                            }
                        };
                    }).close();
                    com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096_failAssert0_literalMutationString239195 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
                java.io.StringWriter sw = new java.io.StringWriter();
                compile.execute(null, new java.lang.Object() {
                    java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                        @java.lang.Override
                        public java.lang.String apply(java.lang.String s) {
                            return s;
                        }
                    };
                }).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237440_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
            com.github.mustachejava.Mustache compile = mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
            java.io.StringWriter sw = new java.io.StringWriter();
            compile.execute(sw, new java.lang.Object() {
                java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                    @java.lang.Override
                    public java.lang.String apply(java.lang.String s) {
                        return s;
                    }
                };
            }).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096_failAssert0_add240039_failAssert0() throws java.io.IOException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache compile = mf.compile("Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    compile.execute(null, new java.lang.Object() {
                        java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                            @java.lang.Override
                            public java.lang.String apply(java.lang.String s) {
                                return s;
                            }
                        };
                    }).close();
                    com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
                    sw.toString();
                    junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440 should have thrown MustacheNotFoundException");
                }
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString237440_failAssert0null238096_failAssert0_add240039 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Ll:;K@jgZ_a[gq|lJ6i;^d[!&IE not found", expected.getMessage());
        }
    }
}

