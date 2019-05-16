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

    public void testSimple_literalMutationString75045_failAssert0null77169_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("w35He[LVCSr");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString75045 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString75045_failAssert0null77169 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template w35He[LVCSr not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString75045_failAssert0_add76988_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("w35He[LVCSr");
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
                junit.framework.TestCase.fail("testSimple_literalMutationString75045 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString75045_failAssert0_add76988 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template w35He[LVCSr not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString75045_failAssert0_literalMutationString76118_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("w35He[LVCSr");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.tvxt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString75045 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString75045_failAssert0_literalMutationString76118 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template w35He[LVCSr not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString75045_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("w35He[LVCSr");
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
            junit.framework.TestCase.fail("testSimple_literalMutationString75045 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template w35He[LVCSr not found", expected.getMessage());
        }
    }

    public void testSimple_add75072_literalMutationString75351_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.DefaultMustacheFactory o_testSimple_add75072__1 = createMustacheFactory();
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("+:Bo!>OPs^F");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimple_add75072__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String o_testSimple_add75072__15 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            java.lang.String o_testSimple_add75072__16 = sw.toString();
            junit.framework.TestCase.fail("testSimple_add75072_literalMutationString75351 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +:Bo!>OPs^F not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString27796_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                com.github.mustachejava.Mustache m = c.compile("simple.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
                sw.toString();
            }
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
                com.github.mustachejava.Mustache m = c.compile("Cdn#M<X|q[v");
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
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString27796 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Cdn#M<X|q[v not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString27796_failAssert0null35571_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                    com.github.mustachejava.Mustache m = c.compile("simple.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
                    sw.toString();
                }
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
                    com.github.mustachejava.Mustache m = c.compile("Cdn#M<X|q[v");
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
                }
                junit.framework.TestCase.fail("testSimpleI18N_literalMutationString27796 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString27796_failAssert0null35571 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Cdn#M<X|q[v not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString27796_failAssert0_add34927_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                    com.github.mustachejava.Mustache m = c.compile("simple.html");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    m.execute(sw, new java.lang.Object() {
                        java.lang.String name = "Chris";

                        int value = 10000;

                        int taxed_value() {
                            return ((int) ((this.value) - ((this.value) * 0.4)));
                        }

                        boolean in_ca = true;
                    });
                    com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
                    sw.toString();
                }
                {
                    com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
                    com.github.mustachejava.Mustache m = c.compile("Cdn#M<X|q[v");
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
                }
                junit.framework.TestCase.fail("testSimpleI18N_literalMutationString27796 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString27796_failAssert0_add34927 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Cdn#M<X|q[v not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString27767_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                com.github.mustachejava.Mustache m = c.compile("9]}Pr=xc^uU");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
                sw.toString();
            }
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
                com.github.mustachejava.Mustache m = c.compile("simple.html");
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
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString27767 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 9]}Pr=xc^uU not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90193_failAssert0_literalMutationString90576_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("r]-sIF!}]1&kk++S*r=");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193_failAssert0_literalMutationString90576 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template r]-sIF!}]1&kk++S*r= not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90193_failAssert0_literalMutationString90608_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("r]-sIF!}]1&kk++S*r=");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "ChIis";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193_failAssert0_literalMutationString90608 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template r]-sIF!}]1&kk++S*r= not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90193_failAssert0_add91650_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("r]-sIF!}]1&kk++S*r=");
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
                com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193_failAssert0_add91650 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template r]-sIF!}]1&kk++S*r= not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90215_failAssert0_literalMutationString91430_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("iRl6eJfYutBW![&ANu`");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90215 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90215_failAssert0_literalMutationString91430 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template iRl6eJfYutBW![&ANu` not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90193_failAssert0null91813_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll(null, "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("r]-sIF!}]1&kk++S*r=");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193_failAssert0null91813 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template r]-sIF!}]1&kk++S*r= not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90220_failAssert0_literalMutationString91200_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("DjjyY^-xXOeGV^DI<LF");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simpefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90220 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90220_failAssert0_literalMutationString91200 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template DjjyY^-xXOeGV^DI<LF not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90193_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
            com.github.mustachejava.Mustache m = c.compile("r]-sIF!}]1&kk++S*r=");
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
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template r]-sIF!}]1&kk++S*r= not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString90193_failAssert0_add91646_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
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
                com.github.mustachejava.Mustache m = c.compile("r]-sIF!}]1&kk++S*r=");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString90193_failAssert0_add91646 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template r]-sIF!}]1&kk++S*r= not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString77438_failAssert0_literalMutationBoolean77597_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("D00|)`p4k7o+[&", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = true;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString77438 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString77438_failAssert0_literalMutationBoolean77597 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template D00|)`p4k7o+[& not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString77438_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("D00|)`p4k7o+[&", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString77438 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template D00|)`p4k7o+[& not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationBoolean77443_failAssert0_literalMutationString77733_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("xitZ3[wY>/yr1h", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = true;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationBoolean77443 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationBoolean77443_failAssert0_literalMutationString77733 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testRecurision_literalMutationString77442_failAssert0_literalMutationString77633_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("r:cursio.html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString77442 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString77442_failAssert0_literalMutationString77633 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template r:cursio.html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString77448_failAssert0_literalMutationString77605_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("NWN(B.%w(%agh>", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString77448 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString77448_failAssert0_literalMutationString77605 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template NWN(B.%w(%agh> not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString77438_failAssert0_add77786_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("D00|)`p4k7o+[&", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString77438 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString77438_failAssert0_add77786 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template D00|)`p4k7o+[& not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116556_failAssert0_add116913_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursi%n_with_inheritance.html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556_failAssert0_add116913 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursi%n_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116557_failAssert0null116987_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(">jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557_failAssert0null116987 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116557_failAssert0_add116919_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(">jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557_failAssert0_add116919 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116557_failAssert0_add116918_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(">jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557_failAssert0_add116918 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritancenull116572_failAssert0_literalMutationString116867_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion_with_inhe]ritance.html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritancenull116572 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritancenull116572_failAssert0_literalMutationString116867 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion_with_inhe]ritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116555_failAssert0_literalMutationString116712_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("WJH(R_3fIFA0n2I#Yb<&9vL>S0iC2h)k", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116555 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116555_failAssert0_literalMutationString116712 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template WJH(R_3fIFA0n2I#Yb<&9vL>S0iC2h)k not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116557_failAssert0_literalMutationString116769_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(">jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "redursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557_failAssert0_literalMutationString116769 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116557_failAssert0_literalMutationString116768_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(">jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "* XN](<g/3S1E");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557_failAssert0_literalMutationString116768 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116556_failAssert0_literalMutationString116758_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursi%n_with_inheritance.html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "&Z:cz#v3RIOUm");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556_failAssert0_literalMutationString116758 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursi%n_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116556_failAssert0null116985_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursi%n_with_inheritance.html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556_failAssert0null116985 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursi%n_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116557_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute(">jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116557 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >jY&Pb<]riUjv9n&hAHC_qA?7HF#}&b not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString116556_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("recursi%n_with_inheritance.html", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString116556 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursi%n_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_remove116570_literalMutationString116695_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("A)G%1!#:( ,}:Kkm]J/#,0+k%$ ,8*(", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            java.lang.String o_testRecursionWithInheritance_remove116570__10 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            java.lang.String o_testRecursionWithInheritance_remove116570__11 = sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_remove116570_literalMutationString116695 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testRecursionWithInheritance_literalMutationBoolean116559_failAssert0_literalMutationString116802_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("wwrXw3S)%Z!=Hp,@AwdmW]%a@EcrN*;", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = true;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationBoolean116559 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationBoolean116559_failAssert0_literalMutationString116802 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template wwrXw3S)%Z!=Hp,@AwdmW]%a@EcrN*; not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74379_failAssert0_literalMutationString74600_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("[AGDJ@5 *a$S;ikzv[*4%-}<} ZWUv+0/p", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_4partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74379 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74379_failAssert0_literalMutationString74600 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_add74732_failAssert0() throws java.io.IOException {
        try {
            {
                execute("u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n&", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                java.io.StringWriter sw = execute("u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n&", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_add74732 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n& not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_add74733_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n&", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_add74733 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n& not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_literalMutationString74587_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("u)[XmoZNbho8M#YG6;A[5(?Ev,ImVPGo]n&", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_literalMutationString74587 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template u)[XmoZNbho8M#YG6;A[5(?Ev,ImVPGo]n& not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74368_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n&", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n& not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_remove74384_literalMutationString74496_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("<#?DiG?fGM[HC`f*AuiNj`QL@Lqc>waL(.", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.lang.String o_testPartialRecursionWithInheritance_remove74384__10 = com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            java.lang.String o_testPartialRecursionWithInheritance_remove74384__11 = sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_remove74384_literalMutationString74496 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template <#?DiG?fGM[HC`f*AuiNj`QL@Lqc>waL(. not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74368_failAssert0null74803_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n&", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368_failAssert0null74803 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n& not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_literalMutationString74595_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n&", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "F!F6FRvt^u<!2PoBIkLmZ0v{eRjRX+]TS");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString74368_failAssert0_literalMutationString74595 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template u)[XmoZNbho8M#YG6;A[5(?Ev,IVPGo]n& not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString82492_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("AwmL4/d|q&+zPt3>f");
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
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString82492 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testSimplePragma_literalMutationString82490_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("sim[lepragma.html");
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
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString82490 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template sim[lepragma.html not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString82499_literalMutationString83228_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("s:implepragma.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimplePragma_literalMutationString82499__7 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Ch<ris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String o_testSimplePragma_literalMutationString82499__14 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            java.lang.String o_testSimplePragma_literalMutationString82499__15 = sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString82499_literalMutationString83228 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template s:implepragma.html not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString96576_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("(@,U|y9Ak!n");
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
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString96576 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template (@,U|y9Ak!n not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString3232_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            com.github.mustachejava.Mustache m = c.compile("@xl+Y)]GWMwTIPF2");
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
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3232 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template @xl+Y)]GWMwTIPF2 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString35934_failAssert0null36711_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList(null);
                };
                java.io.StringWriter sw = execute("IX@{$(@!}+lu", object);
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934_failAssert0null36711 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template IX@{$(@!}+lu not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString35934_failAssert0_literalMutationString36298_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("p:|P[(1jN54{", object);
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934_failAssert0_literalMutationString36298 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template p:|P[(1jN54{ not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString35934_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("IX@{$(@!}+lu", object);
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template IX@{$(@!}+lu not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_add35947_literalMutationString36113_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("is<empty.html", object);
            sw.toString();
            java.lang.String o_testIsNotEmpty_add35947__10 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            java.lang.String o_testIsNotEmpty_add35947__11 = sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_add35947_literalMutationString36113 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template is<empty.html not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString35934_failAssert0_add36577_failAssert0() throws java.io.IOException {
        try {
            {
                java.util.Collections.singletonList("Test");
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("IX@{$(@!}+lu", object);
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString35934_failAssert0_add36577 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template IX@{$(@!}+lu not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString51915_literalMutationString52173_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("page1.txt");
            };
            java.io.StringWriter sw = execute("87-44HsftRG{", java.util.Collections.singletonList(object));
            java.lang.String o_testImmutableList_literalMutationString51915__10 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            java.lang.String o_testImmutableList_literalMutationString51915__11 = sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString51915_literalMutationString52173 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 87-44HsftRG{ not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString51922_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("(#9:<eP/GIH4", java.util.Collections.singletonList(object));
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString51922 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testImmutableList_literalMutationString51922_failAssert0_literalMutationString52368_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("(#9:<eP/GIH4", java.util.Collections.singletonList(object));
                com.github.mustachejava.TestUtil.getContents(this.root, "isemptytxt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString51922 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString51922_failAssert0_literalMutationString52368 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testImmutableList_literalMutationString51922_failAssert0_add52624_failAssert0() throws java.io.IOException {
        try {
            {
                java.util.Collections.singletonList("Test");
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("(#9:<eP/GIH4", java.util.Collections.singletonList(object));
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString51922 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString51922_failAssert0_add52624 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testImmutableList_literalMutationString51914_literalMutationString52156_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Qest");
            };
            java.io.StringWriter sw = execute("]sempty.html", java.util.Collections.singletonList(object));
            java.lang.String o_testImmutableList_literalMutationString51914__10 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            java.lang.String o_testImmutableList_literalMutationString51914__11 = sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString51914_literalMutationString52156 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]sempty.html not found", expected.getMessage());
        }
    }

    public void testSecurity_remove87161_literalMutationString88216_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile(":j!(i%D99+Oy=");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.lang.String o_testSecurity_remove87161__7 = com.github.mustachejava.TestUtil.getContents(this.root, "security.txt");
            java.lang.String o_testSecurity_remove87161__8 = sw.toString();
            junit.framework.TestCase.fail("testSecurity_remove87161_literalMutationString88216 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template :j!(i%D99+Oy= not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString87123_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("1*3]/o>pXj|oc");
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
            junit.framework.TestCase.fail("testSecurity_literalMutationString87123 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testProperties_literalMutationString93201_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("$zi@B!;C]$S");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
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
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testProperties_literalMutationString93201 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template $zi@B!;C]$S not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString93201_failAssert0null95557_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("$zi@B!;C]$S");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.lang.Object() {
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
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString93201 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString93201_failAssert0null95557 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template $zi@B!;C]$S not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString93201_failAssert0_literalMutationString94589_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("$zi@B!C]$S");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
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
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString93201 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString93201_failAssert0_literalMutationString94589 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template $zi@B!C]$S not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString93201_failAssert0_add95390_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("$zi@B!;C]$S");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
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
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString93201 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString93201_failAssert0_add95390 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template $zi@B!;C]$S not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_remove21732_literalMutationString23464_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            java.io.StringWriter sw = execute("J.I Lc(ru:.", new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    java.lang.Object o_testSimpleWithMap_remove21732__8 = put("name", "Chris");
                    java.lang.Object o_testSimpleWithMap_remove21732__9 = put("taxed_value", 6000);
                    java.lang.Object o_testSimpleWithMap_remove21732__10 = put("in_ca", true);
                }
            });
            java.lang.String o_testSimpleWithMap_remove21732__11 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            java.lang.String o_testSimpleWithMap_remove21732__12 = sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_remove21732_literalMutationString23464 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template J.I Lc(ru:. not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString21717_failAssert0_literalMutationString25471_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                java.io.StringWriter sw = execute("t/6eME!b( {", new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString21717 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString21717_failAssert0_literalMutationString25471 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testSimpleWithMap_literalMutationString21671_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            java.io.StringWriter sw = execute("simple.ht]ml", new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                }
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString21671 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simple.ht]ml not found", expected.getMessage());
        }
    }

    public void testPartialWithTFnull78129_failAssert0_literalMutationString78244_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile(">O u^D-ZzW?#qL]b72!>!!hu5#^G+s");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTFnull78129 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTFnull78129_failAssert0_literalMutationString78244 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >O u^D-ZzW?#qL]b72!>!!hu5#^G+s not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0null78447_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("($PdwlVjpC$r#tiH[Ajp^`^86P!#o.");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118_failAssert0null78447 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ($PdwlVjpC$r#tiH[Ajp^`^86P!#o. not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78119_failAssert0_literalMutationString78219_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("parti}lintemplatKefunction.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78119 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78119_failAssert0_literalMutationString78219 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template parti}lintemplatKefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0_add78398_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                createMustacheFactory();
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("($PdwlVjpC$r#tiH[Ajp^`^86P!#o.");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118_failAssert0_add78398 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ($PdwlVjpC$r#tiH[Ajp^`^86P!#o. not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78119_failAssert0_literalMutationString78217_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("[2qgDIC}H]I(e]zUqM-i %Iy1&<2R8C");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78119 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78119_failAssert0_literalMutationString78217 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [2qgDIC}H]I(e]zUqM-i %Iy1&<2R8C not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add78123_literalMutationString78182_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache o_testPartialWithTF_add78123__3 = c.compile("partialintemplatefunction.html");
            com.github.mustachejava.Mustache m = c.compile("W[r?#*(THgt^K|nTv) w=O!|_>{IrE");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_add78123__8 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            java.lang.String o_testPartialWithTF_add78123__15 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add78123_literalMutationString78182 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template W[r?#*(THgt^K|nTv) w=O!|_>{IrE not found", expected.getMessage());
        }
    }

    public void testPartialWithTFnull78129_failAssert0_literalMutationString78245_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("partiali<templatefunction.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTFnull78129 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTFnull78129_failAssert0_literalMutationString78245 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template partiali<templatefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("($PdwlVjpC$r#tiH[Ajp^`^86P!#o.");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ($PdwlVjpC$r#tiH[Ajp^`^86P!#o. not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0_add78399_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                c.compile("($PdwlVjpC$r#tiH[Ajp^`^86P!#o.");
                com.github.mustachejava.Mustache m = c.compile("($PdwlVjpC$r#tiH[Ajp^`^86P!#o.");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118_failAssert0_add78399 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ($PdwlVjpC$r#tiH[Ajp^`^86P!#o. not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0_add78400_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("($PdwlVjpC$r#tiH[Ajp^`^86P!#o.");
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
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118_failAssert0_add78400 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ($PdwlVjpC$r#tiH[Ajp^`^86P!#o. not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0_literalMutationString78231_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("($PdwlVjpC$r#tiH[Ajp^`^86Pm#o.");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118_failAssert0_literalMutationString78231 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ($PdwlVjpC$r#tiH[Ajp^`^86Pm#o. not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78118_failAssert0_literalMutationString78233_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("Cz]<o^1%FBgZyrMowO(77Rix6U{(k8");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78118_failAssert0_literalMutationString78233 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Cz]<o^1%FBgZyrMowO(77Rix6U{(k8 not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78117_failAssert0_literalMutationString78211_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("`J!IhrKf6");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78117 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78117_failAssert0_literalMutationString78211 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `J!IhrKf6 not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78117_failAssert0_literalMutationString78212_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78117 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78117_failAssert0_literalMutationString78212 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString78120_failAssert0_literalMutationString78237_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("v]3L>M+^p4&.q:_ORW-TR8!(ip%`N");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78120 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString78120_failAssert0_literalMutationString78237 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template v]3L>M+^p4&.q:_ORW-TR8!(ip%`N not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_remove78126_literalMutationString78203_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("-dO9F,Mm*+?`{LBWe(]:.7Qtp1|0u>");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.lang.String o_testPartialWithTF_remove78126__7 = sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_remove78126_literalMutationString78203 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -dO9F,Mm*+?`{LBWe(]:.7Qtp1|0u> not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121506_failAssert0_literalMutationString121999_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("comp{ex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121506 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121506_failAssert0_literalMutationString121999 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template comp{ex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121499_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile("AlUw#pDJ#Z,n");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString121499 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template AlUw#pDJ#Z,n not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString123837_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile("7SzWAlh|C(ye");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString123837 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 7SzWAlh|C(ye not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString123842_failAssert0_literalMutationString124481_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("Yc7,D[D,:zJ]H");
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
                junit.framework.TestCase.fail("testComplex_literalMutationString123842 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString123842_failAssert0_literalMutationString124481 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Yc7,D[D,:zJ]H not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString123853_failAssert0_add125143_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                jf.createJsonParser(json.toString()).readValueAsTree();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("complex.h[tml");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString123853 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString123853_failAssert0_add125143 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex.h[tml not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121513_failAssert0_literalMutationString121831_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("co%mplex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("co!plex.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121513 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121513_failAssert0_literalMutationString121831 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template co%mplex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121517_failAssert0_literalMutationString122061_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                m = createMustacheFactory().compile("& 0g<(IN0KSt");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.tt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121517 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121517_failAssert0_literalMutationString122061 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template & 0g<(IN0KSt not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121516_failAssert0_add122975_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                m = createMustacheFactory().compile("b`FV)LG`?tgm");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121516 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121516_failAssert0_add122975 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template b`FV)LG`?tgm not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121516_failAssert0_add122974_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                m = createMustacheFactory().compile("b`FV)LG`?tgm");
                m.execute(sw, o);
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121516 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121516_failAssert0_add122974 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template b`FV)LG`?tgm not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121516_failAssert0_literalMutationString122193_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                m = createMustacheFactory().compile("b`FV)LG`?tgm");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "((,fpq}G*m;");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121516 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121516_failAssert0_literalMutationString122193 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template b`FV)LG`?tgm not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121516_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("b`FV)LG`?tgm");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString121516 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template b`FV)LG`?tgm not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString123844_failAssert0_literalMutationString124411_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("compl[ex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString123844 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString123844_failAssert0_literalMutationString124411 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template compl[ex.html not found", expected.getMessage());
        }
    }

    public void testComplexnull123888_failAssert0_literalMutationString124888_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("C&TZkzg:%koi");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexnull123888 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull123888_failAssert0_literalMutationString124888 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template C&TZkzg:%koi not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString123849_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("Y7_G1$%<xGl`");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString123849 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Y7_G1$%<xGl` not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString123853_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("complex.h[tml");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString123853 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex.h[tml not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString121516_failAssert0_literalMutationString122189_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                m = createMustacheFactory().compile("b`FV)LG`?tgm");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "comple|x.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString121516 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString121516_failAssert0_literalMutationString122189 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template b`FV)LG`?tgm not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0_literalMutationString39221_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("30+r <{dzN,|");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0_literalMutationString39221 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 30+r <{dzN,| not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39032_failAssert0_add39569_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("co[mplex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject());
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032_failAssert0_add39569 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template co[mplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0_add39552_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("jlOVLSA?Y<RV");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0_add39552 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0_add39550_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("jlOVLSA?Y<RV");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0_add39550 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("jlOVLSA?Y<RV");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39032_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("co[mplex.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template co[mplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39032_failAssert0null39682_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("co[mplex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032_failAssert0null39682 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template co[mplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39032_failAssert0_literalMutationString39265_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("co[mplex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "compl4ex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39032_failAssert0_literalMutationString39265 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template co[mplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39035_failAssert0_literalMutationString39272_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("complex:.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39035 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39035_failAssert0_literalMutationString39272 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex:.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0null39674_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("jlOVLSA?Y<RV");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0null39674 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0null39673_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("jlOVLSA?Y<RV");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0null39673 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0_literalMutationString39223_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("jOVLSA?Y<RV");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0_literalMutationString39223 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template jOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0_add39546_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                initParallel();
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("jlOVLSA?Y<RV");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0_add39546 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39029_failAssert0_literalMutationString39235_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("RV^:E|_N6");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39029 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39029_failAssert0_literalMutationString39235 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template RV^:E|_N6 not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString39033_failAssert0_literalMutationString39228_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("jlOVLSA?Y<RV");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "bfGxMkh]&Cz");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString39033_failAssert0_literalMutationString39228 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template jlOVLSA?Y<RV not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43350_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter sw = execute(">if>G?19BchB", new com.github.mustachejava.ParallelComplexObject());
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >if>G?19BchB not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add43357_literalMutationString43440_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter o_testSerialCallable_add43357__1 = execute("complex.html", new com.github.mustachejava.ParallelComplexObject());
            java.io.StringWriter sw = execute(",2l-=KW>A0n8", new com.github.mustachejava.ParallelComplexObject());
            java.lang.String o_testSerialCallable_add43357__6 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            java.lang.String o_testSerialCallable_add43357__7 = sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_add43357_literalMutationString43440 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ,2l-=KW>A0n8 not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43350_failAssert0_add43636_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(">if>G?19BchB", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350_failAssert0_add43636 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >if>G?19BchB not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43350_failAssert0_add43635_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(">if>G?19BchB", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350_failAssert0_add43635 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >if>G?19BchB not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43350_failAssert0null43685_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(">if>G?19BchB", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350_failAssert0null43685 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >if>G?19BchB not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43350_failAssert0_literalMutationString43511_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(">if>G?19BchB", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350_failAssert0_literalMutationString43511 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >if>G?19BchB not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43346_failAssert0_literalMutationString43472_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(" does not exist", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString43346 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43346_failAssert0_literalMutationString43472 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testSerialCallable_add43359_literalMutationString43401_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter sw = execute("CMsS!CUDc )(", new com.github.mustachejava.ParallelComplexObject());
            sw.toString();
            java.lang.String o_testSerialCallable_add43359__5 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            java.lang.String o_testSerialCallable_add43359__6 = sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_add43359_literalMutationString43401 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template CMsS!CUDc )( not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString43350_failAssert0_literalMutationString43508_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(">if>G?19Bc8hB", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString43350_failAssert0_literalMutationString43508 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >if>G?19Bc8hB not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47859_failAssert0null51335_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                    super.partial(null, variable);
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859_failAssert0null51335 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0_literalMutationString48872_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("}")) {
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
                        put("va5ue", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851_failAssert0_literalMutationString48872 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47861_failAssert0_literalMutationString48762_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                        put("name", "page1.txt");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861_failAssert0_literalMutationString48762 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47861_failAssert0null51226_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                        put("value", null);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861_failAssert0null51226 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47942_failAssert0_literalMutationNumber48617_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                        put("foo", "seimple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47942 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47942_failAssert0_literalMutationNumber48617 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + seimple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47859_failAssert0_literalMutationString49141_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                            partial = df.compile(new java.io.StringReader(name), "", "[", "]");
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859_failAssert0_literalMutationString49141 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47861_failAssert0_add50659_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861_failAssert0_add50659 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47859_failAssert0_add50743_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859_failAssert0_add50743 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47861_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47861 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0null51234_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("}")) {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851_failAssert0null51234 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                        @java.lang.Override
                        public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                            if (variable.startsWith("}")) {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0_literalMutationString48844_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("}")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("FaiSed to parse partial name template: " + (name)));
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851_failAssert0_literalMutationString48844 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0null51240_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("}")) {
                                    com.github.mustachejava.TemplateContext partialTC = new com.github.mustachejava.TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @java.lang.Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new java.io.StringReader(name), null, "[", "]");
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851_failAssert0null51240 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47859_failAssert0null51321_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859_failAssert0null51321 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0_add50686_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("}")) {
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
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851_failAssert0_add50686 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47859_failAssert0_add50750_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                            dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859_failAssert0_add50750 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString47851_failAssert0_add50665_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                variable.startsWith("}");
                                if (variable.startsWith("}")) {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString47851_failAssert0_add50665 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber47859_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber47859 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString14235_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("V|+eKL?p+)");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString14235 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template V|+eKL?p+) not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString14235_failAssert0null14934_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("V|+eKL?p+)");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(null, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString14235 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString14235_failAssert0null14934 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template V|+eKL?p+) not found", expected.getMessage());
        }
    }

    public void testReadme_add14247_literalMutationString14311_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("iG&M[I9hAL");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            java.io.Writer o_testReadme_add14247__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            java.io.Writer o_testReadme_add14247__11 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            java.lang.String o_testReadme_add14247__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            java.lang.String o_testReadme_add14247__16 = sw.toString();
            junit.framework.TestCase.fail("testReadme_add14247_literalMutationString14311 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template iG&M[I9hAL not found", expected.getMessage());
        }
    }

    public void testReadmenull14254_failAssert0_literalMutationString14556_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("item[.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testReadmenull14254 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmenull14254_failAssert0_literalMutationString14556 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template item[.html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString14235_failAssert0_add14819_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("V|+eKL?p+)");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString14235 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString14235_failAssert0_add14819 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template V|+eKL?p+) not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2001_failAssert0_literalMutationString2546_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("items%.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_37 = "Should be a little bit more than 48seconds: " + diff;
                boolean boolean_38 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001_failAssert0_literalMutationString2546 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template items%.html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2000_failAssert0_literalMutationString2589_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                java.lang.String String_41 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_42 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2000 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2000_failAssert0_literalMutationString2589 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2003_failAssert0null2985_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("v)LA|PjLTh9");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                java.lang.String String_23 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_24 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003_failAssert0null2985 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template v)LA|PjLTh9 not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2003_failAssert0_literalMutationNumber2344_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("v)LA|PjLTh9");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_23 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_24 = (diff > 3999) && (diff < 0);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003_failAssert0_literalMutationNumber2344 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template v)LA|PjLTh9 not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2001_failAssert0_add2915_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("items%.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                sw.toString();
                java.lang.String String_37 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_38 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001_failAssert0_add2915 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template items%.html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2003_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("v)LA|PjLTh9");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_23 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_24 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template v)LA|PjLTh9 not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2006_failAssert0_literalMutationString2412_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("$T_H--aJni>");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "itemsvtxt");
                sw.toString();
                java.lang.String String_29 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_30 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2006 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2006_failAssert0_literalMutationString2412 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template $T_H--aJni> not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2001_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("items%.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_37 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_38 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template items%.html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2001_failAssert0null3005_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("items%.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(null, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_37 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_38 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2001_failAssert0null3005 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template items%.html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add2016_literalMutationString2202_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("$_8K%!{;g(#");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            java.io.Writer o_testReadmeSerial_add2016__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            java.lang.String o_testReadmeSerial_add2016__13 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            java.lang.String o_testReadmeSerial_add2016__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            java.lang.String o_testReadmeSerial_add2016__15 = sw.toString();
            java.lang.String String_13 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_14 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_add2016_literalMutationString2202 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template $_8K%!{;g(# not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2003_failAssert0_add2863_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("v)LA|PjLTh9");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                sw.toString();
                java.lang.String String_23 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_24 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2003_failAssert0_add2863 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template v)LA|PjLTh9 not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0_literalMutationNumber103957_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("W)g%tpQx^Wu");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 1999);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575_failAssert0_literalMutationNumber103957 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0_add104517_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("W)g%tpQx^Wu");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575_failAssert0_add104517 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("ite|ms2.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_156 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("W)g%tpQx^Wu");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_139 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_140 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0_literalMutationString103938_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("W)g%tpQx^Wu");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                java.lang.String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575_failAssert0_literalMutationString103938 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0null104684_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("ite|ms2.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(null, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0null104684 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0null104685_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("ite|ms2.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0null104685 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0_literalMutationNumber104168_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("ite|ms2.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 1000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0_literalMutationNumber104168 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add103587_remove104624() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
        java.lang.String o_testReadmeParallel_add103587__17 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103587__17);
        java.lang.String o_testReadmeParallel_add103587__18 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103587__18);
        java.lang.String String_123 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_123);
        boolean boolean_124 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103587__17);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103587__18);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_123);
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0_literalMutationNumber104161_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("ite|ms2.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 998) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0_literalMutationNumber104161 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0_add104572_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("ite|ms2.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.lang.System.currentTimeMillis();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0_add104572 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallelnull103594_failAssert0_literalMutationString104235_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("XF8`i=yZNlz");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(null, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_163 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_164 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallelnull103594 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmeParallelnull103594_failAssert0_literalMutationString104235 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template XF8`i=yZNlz not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0null104663_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("W)g%tpQx^Wu");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(null, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575_failAssert0null104663 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0null104664_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("W)g%tpQx^Wu");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                java.lang.String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575_failAssert0null104664 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0_add104573_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("ite|ms2.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0_add104573 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0_add104574_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("ite|ms2.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0_add104574 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add103589_remove104626() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
        java.lang.String o_testReadmeParallel_add103589__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103589__15);
        java.lang.String o_testReadmeParallel_add103589__16 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103589__16);
        java.lang.String String_125 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_125);
        boolean boolean_126 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103589__15);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103589__16);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_125);
    }

    public void testReadmeParallel_literalMutationString103575_failAssert0_add104514_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("W)g%tpQx^Wu");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_139 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_140 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103575_failAssert0_add104514 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template W)g%tpQx^Wu not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString103576_failAssert0_literalMutationString104155_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("ite|ms2.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_155 = "page1.txt" + diff;
                boolean boolean_156 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString103576_failAssert0_literalMutationString104155 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ite|ms2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add103585_literalMutationNumber103836() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        com.github.mustachejava.Mustache o_testReadmeParallel_add103585__3 = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeParallel_add103585__3)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeParallel_add103585__3)).getName());
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add103585__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103585__15);
        java.lang.String o_testReadmeParallel_add103585__16 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103585__16);
        java.lang.String String_135 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_135);
        boolean boolean_136 = (diff > 1000) && (diff < 2000);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeParallel_add103585__3)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeParallel_add103585__3)).getName());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103585__15);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add103585__16);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_135);
    }

    public void testDeferred_literalMutationString41857_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
            mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            java.lang.Object context = new java.lang.Object() {
                java.lang.String title = "Deferred";

                java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
            };
            com.github.mustachejava.Mustache m = mf.compile(";hm51Ny&9DX|d");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, context).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString41857 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ;hm51Ny&9DX|d not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString41857_failAssert0null43070_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile(";hm51Ny&9DX|d");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString41857 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString41857_failAssert0null43070 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ;hm51Ny&9DX|d not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString41857_failAssert0_literalMutationString42284_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile(";hm51NI&9DX|d");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString41857 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString41857_failAssert0_literalMutationString42284 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ;hm51NI&9DX|d not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString41857_failAssert0_add42845_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile(";hm51Ny&9DX|d");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString41857 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString41857_failAssert0_add42845 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ;hm51Ny&9DX|d not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString41866_failAssert0_literalMutationString42388_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile("&JPsJH}$z][wT");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "defergred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString41866 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString41866_failAssert0_literalMutationString42388 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template &JPsJH}$z][wT not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_add92209_literalMutationString92345_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
            com.github.mustachejava.Mustache compile = mf.compile("so}skeDd$t7{[t&vz9}!b1gYRzr");
            java.io.StringWriter sw = new java.io.StringWriter();
            compile.execute(sw, new java.lang.Object() {
                java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                    @java.lang.Override
                    public java.lang.String apply(java.lang.String s) {
                        return s;
                    }
                };
            }).close();
            java.lang.String o_testRelativePathsTemplateFunction_add92209__19 = com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
            java.lang.String o_testRelativePathsTemplateFunction_add92209__20 = com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
            java.lang.String o_testRelativePathsTemplateFunction_add92209__21 = sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_add92209_literalMutationString92345 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template so}skeDd$t7{[t&vz9}!b1gYRzr not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString92195_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
            com.github.mustachejava.Mustache compile = mf.compile("1m0a$@xkeYti%+%?cWN%w$> AQR");
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
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString92195 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1m0a$@xkeYti%+%?cWN%w$> AQR not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString92195_failAssert0_add92755_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("1m0a$@xkeYti%+%?cWN%w$> AQR");
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
                com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString92195 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString92195_failAssert0_add92755 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1m0a$@xkeYti%+%?cWN%w$> AQR not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_remove92211_literalMutationString92393_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
            com.github.mustachejava.Mustache compile = mf.compile("M`#![4.lumpuIr9y%TFQ7*&3R;[");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.lang.String o_testRelativePathsTemplateFunction_remove92211__7 = com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
            java.lang.String o_testRelativePathsTemplateFunction_remove92211__8 = sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_remove92211_literalMutationString92393 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template M`#![4.lumpuIr9y%TFQ7*&3R;[ not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunctionnull92215_failAssert0_literalMutationString92548_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("n8^.OxPvQC)m]jtaN+n0O^vIxxm");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunctionnull92215 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunctionnull92215_failAssert0_literalMutationString92548 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template n8^.OxPvQC)m]jtaN+n0O^vIxxm not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString92195_failAssert0_literalMutationString92513_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("1m0a$@xkeYti%+%?WN%w$> AQR");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString92195 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString92195_failAssert0_literalMutationString92513 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1m0a$@xkeYti%+%?WN%w$> AQR not found", expected.getMessage());
        }
    }
}

