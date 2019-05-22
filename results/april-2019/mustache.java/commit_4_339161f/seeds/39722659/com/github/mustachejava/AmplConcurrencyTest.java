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

    public void testSimple_literalMutationString4066_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("#imple.html");
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
            junit.framework.TestCase.fail("testSimple_literalMutationString4066 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #imple.html not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString1479_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                com.github.mustachejava.Mustache m = c.compile("]^`2&CvM&sZ");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1479 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]^`2&CvM&sZ not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString1513_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("kW )y#w9r24");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1513 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template kW )y#w9r24 not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString5119_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
            com.github.mustachejava.Mustache m = c.compile("[}Iu_GAfO|-5v./!fV`");
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
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString5119 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testRecursionWithInheritance_literalMutationString7035_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("Cgo$nQnWg>sQR&H#C,`psl(r&+?}!i6", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString7035 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Cgo$nQnWg>sQR&H#C,`psl(r&+?}!i6 not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString4002_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("rzSbBEHJCODSO!d|SR{;HSaQ^^}B^_$kc!", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString4002 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template rzSbBEHJCODSO!d|SR{;HSaQ^^}B^_$kc! not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString3999_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("recursive_partial_inheritance.h^ml", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString3999 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursive_partial_inheritance.h^ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString4593_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("si:mplepragma.html");
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
            junit.framework.TestCase.assertEquals("Template si:mplepragma.html not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString4591_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("V?U3<T|pN:u/-!`!C");
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
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString4591 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testMultipleWrappers_literalMutationString6035_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("[,u(^F5ZXXf");
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
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString6035 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [,u(^F5ZXXf not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString223_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            com.github.mustachejava.Mustache m = c.compile("Z:Cfs:y<jJK8#IJo");
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
            junit.framework.TestCase.assertEquals("Template Z:Cfs:y<jJK8#IJo not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString1779_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("isempty.%html", object);
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString1779 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template isempty.%html not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString1780_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("+yM-)5!w98Q%", object);
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString1780 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +yM-)5!w98Q% not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString3119_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("x7cM{QPrU9IE", java.util.Collections.singletonList(object));
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString3119 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template x7cM{QPrU9IE not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString4920_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("`MUSwn[b%{Sc*");
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
            junit.framework.TestCase.assertEquals("Template `MUSwn[b%{Sc* not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString5482_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("simple.h]ml");
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
            junit.framework.TestCase.fail("testProperties_literalMutationString5482 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simple.h]ml not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString5483_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("zqe&xL{@ZM!");
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
            junit.framework.TestCase.fail("testProperties_literalMutationString5483 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template zqe&xL{@ZM! not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString1185_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            java.io.StringWriter sw = execute("+S>X#5la]H>", new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                }
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString1185 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template +S>X#5la]H> not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString4284_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("?a-<1O&z}kld.f<FkNyvXm)7DqRmjT");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString4284 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ?a-<1O&z}kld.f<FkNyvXm)7DqRmjT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7748_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile("f)sd%B-0[rTE");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString7748 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template f)sd%B-0[rTE not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7567_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("Jc (D#ZoC!,y");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7567 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Jc (D#ZoC!,y not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7743_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile("comple%x.html");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString7743 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template comple%x.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7760_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("`sj{v-#LCjcF");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7760 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `sj{v-#LCjcF not found", expected.getMessage());
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
            com.github.mustachejava.Mustache m = c.compile("j}rKG]eCY7ro");
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
            junit.framework.TestCase.assertEquals("Template j}rKG]eCY7ro not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString2014_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("4WnL:97S|T|e");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString2014 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 4WnL:97S|T|e not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString2016_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("compl{ex.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString2016 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template compl{ex.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString2345_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter sw = execute("fp?9Dj>KW11;", new com.github.mustachejava.ParallelComplexObject());
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString2345 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template fp?9Dj>KW11; not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString2665_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                        @java.lang.Override
                        public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                            if (variable.startsWith("-")) {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString2665 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString2683_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                        partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "page1.txt", "]");
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString2683 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString834_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("GQz][q?;OH");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString834 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template GQz][q?;OH not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString143_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("QgN:j}k8+X_");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_25 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_26 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString143 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template QgN:j}k8+X_ not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString6421_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("SZ9rdG]Ir|V");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_143 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_144 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString6421 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template SZ9rdG]Ir|V not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add6429() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory o_testReadmeParallel_add6429__1 = initParallel();
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeParallel_add6429__1)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeParallel_add6429__1)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeParallel_add6429__1)).getRecursionLimit())));
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
        java.lang.String o_testReadmeParallel_add6429__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6429__15);
        java.lang.String o_testReadmeParallel_add6429__16 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6429__16);
        java.lang.String String_135 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_135);
        boolean boolean_136 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeParallel_add6429__1)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeParallel_add6429__1)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeParallel_add6429__1)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6429__15);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6429__16);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_135);
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
            com.github.mustachejava.Mustache m = mf.compile("Q]jb_8&5?SZX@");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, context).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString2237 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Q]jb_8&5?SZX@ not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString5385_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
            com.github.mustachejava.Mustache compile = mf.compile("Tyiyd4TyY-&*K44q[Uvw.Ja8 tX");
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
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString5385 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Tyiyd4TyY-&*K44q[Uvw.Ja8 tX not found", expected.getMessage());
        }
    }
}

