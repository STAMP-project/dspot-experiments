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

    public void testSimple_literalMutationString4067_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("R[X=mN#c@2Z");
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
            junit.framework.TestCase.fail("testSimple_literalMutationString4067 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testSimpleI18N_literalMutationString1512_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("1{ s.liwWjB");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1512 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1{ s.liwWjB not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString1511_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("simp e.html");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1511 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simp e.html not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString1482_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                com.github.mustachejava.Mustache m = c.compile("(7lm0^d_+ y");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1482 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template (7lm0^d_+ y not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString5122_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
            com.github.mustachejava.Mustache m = c.compile("Bf%uv7bJL&0T?K/s!,r");
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
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString5122 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testRecurision_literalMutationString4212_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("recursion%html", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString4212 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion%html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString4216_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute(">R66J`2?z)?I)X", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString4216 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >R66J`2?z)?I)X not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString7038_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("recursion_with_inher|itance.html", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString7038 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion_with_inher|itance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString7039_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("s*KDjPt}zI9Xdj7d7P26D]M#)& xHd>", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString7039 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template s*KDjPt}zI9Xdj7d7P26D]M#)& xHd> not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString3998_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("4U-$d`OOI!hfKhH8I&;KHoH4tw?{JHFt+W", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString3998 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 4U-$d`OOI!hfKhH8I&;KHoH4tw?{JHFt+W not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString4593_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("Mnm(.90K) [,GdkS(");
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
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString4593 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Mnm(.90K) [,GdkS( not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString6034_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile(".}(AGYIPaTw");
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
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString6034 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .}(AGYIPaTw not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString223_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            com.github.mustachejava.Mustache m = c.compile(";?KH^yP.QQS3%w_?");
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
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString223 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ;?KH^yP.QQS3%w_? not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString1775_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("pPc!m#?-}B67", object);
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString1775 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template pPc!m#?-}B67 not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString3119_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("isempty.h>ml", java.util.Collections.singletonList(object));
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString3119 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template isempty.h>ml not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString3118_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("#B.7U]a)3]_`", java.util.Collections.singletonList(object));
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString3118 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #B.7U]a)3]_` not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString4920_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("@HdNZeP*wOs:0");
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
            junit.framework.TestCase.fail("testSecurity_literalMutationString4920 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template @HdNZeP*wOs:0 not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString5480_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("rT2x:|AMS6M");
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
            junit.framework.TestCase.fail("testProperties_literalMutationString5480 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template rT2x:|AMS6M not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString1187_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            java.io.StringWriter sw = execute(";830*)h:_D{", new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                }
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString1187 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ;830*)h:_D{ not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString4279_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile(":K**W|!!;NKXt3GZq^>z2X(iD119`!");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString4279 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template :K**W|!!;NKXt3GZq^>z2X(iD119`! not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7758_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("a9$6DyJ>d|J0");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7758 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template a9$6DyJ>d|J0 not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7572_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("9V w0K{!)d Y");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7572 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 9V w0K{!)d Y not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7746_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile("MAt:gc;y|y>7");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString7746 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template MAt:gc;y|y>7 not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7762_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("complex<.html");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7762 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex<.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7559_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile("t)%>w]gciWL]");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString7559 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template t)%>w]gciWL] not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString2012_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("Bp!)A$5_,kp>");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString2012 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Bp!)A$5_,kp> not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString2009_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("co:mplex.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString2009 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template co:mplex.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString2343_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter sw = execute(" x.uhyHN,<5c", new com.github.mustachejava.ParallelComplexObject());
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString2343 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  x.uhyHN,<5c not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString2681_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                        partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "y", "]");
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString2681 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber2672_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber2672 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString836_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("6TH(.*qGm[");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString836 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 6TH(.*qGm[ not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString6422_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("i]tems2.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_147 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_148 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString6422 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template i]tems2.html not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add6438() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        sw.toString();
        java.lang.String o_testReadmeParallel_add6438__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6438__15);
        java.lang.String o_testReadmeParallel_add6438__16 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6438__16);
        java.lang.String String_127 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_127);
        boolean boolean_128 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6438__15);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6438__16);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_127);
    }

    public void testDeferred_literalMutationString2233_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
            mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            java.lang.Object context = new java.lang.Object() {
                java.lang.String title = "Deferred";

                java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
            };
            com.github.mustachejava.Mustache m = mf.compile("deferr<ed.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, context).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString2233 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template deferr<ed.html not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString2237_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
            mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            java.lang.Object context = new java.lang.Object() {
                java.lang.String title = "Deferred";

                java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
            };
            com.github.mustachejava.Mustache m = mf.compile("Pd>G1n`ZnyD$:");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, context).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString2237 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Pd>G1n`ZnyD$: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString5382_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
            com.github.mustachejava.Mustache compile = mf.compile("<zk8Jty>@2n(_@&M/xI!cW])lSc");
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
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString5382 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }
}

