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

    public void testSimple_literalMutationString4064_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("<2ZF:H-X&hs");
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
            junit.framework.TestCase.fail("testSimple_literalMutationString4064 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template <2ZF:H-X&hs not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString1509_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("2AEl^W^e=]O");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1509 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 2AEl^W^e=]O not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString1478_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                com.github.mustachejava.Mustache m = c.compile("<imple.html");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1478 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template <imple.html not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString1477_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                com.github.mustachejava.Mustache m = c.compile("F^F%&MV|yT7");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString1477 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template F^F%&MV|yT7 not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString5123_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
            com.github.mustachejava.Mustache m = c.compile("#vy|_&A3eu}Rx`Vdt$z");
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
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString5123 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #vy|_&A3eu}Rx`Vdt$z not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString4211_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("#qfy#[CUT*Fj)#", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString4211 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #qfy#[CUT*Fj)# not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString7035_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("%V^A{[| 7uOMW#Gsr;26h_YN@4M#]ji", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString7035 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString3998_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("r{#kyHY`lVDdk<#.ev6 Z >m$w8YyZy]A=", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString3998 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template r{#kyHY`lVDdk<#.ev6 Z >m$w8YyZy]A= not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString4592_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("simpleprag:a.html");
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
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString4592 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simpleprag:a.html not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString6035_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("xzYa?@(u`f8");
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
            junit.framework.TestCase.assertEquals("Template xzYa?@(u`f8 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString1777_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("ubl[5isTjC;s", object);
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString1777 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ubl[5isTjC;s not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString3113_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("}=iZNT@qOmUt", java.util.Collections.singletonList(object));
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString3113 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template }=iZNT@qOmUt not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString4919_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("7|u8&Sp`JtKE/");
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
            junit.framework.TestCase.fail("testSecurity_literalMutationString4919 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testProperties_literalMutationString5482_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("simple.h|ml");
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
            junit.framework.TestCase.assertEquals("Template simple.h|ml not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString5481_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("Rne3_hRF,k%");
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
            junit.framework.TestCase.fail("testProperties_literalMutationString5481 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Rne3_hRF,k% not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString1182_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            java.io.StringWriter sw = execute("z2Uh][U&Q)&", new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                }
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString1182 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template z2Uh][U&Q)& not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString1184_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            java.io.StringWriter sw = execute("si%ple.html", new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                }
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString1184 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template si%ple.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString4281_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("dR`[_5ed50a(**wf <5sHh(Dts!wGm");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString4281 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template dR`[_5ed50a(**wf <5sHh(Dts!wGm not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7571_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("Tk$_qc}-=UYN");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7571 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Tk$_qc}-=UYN not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7570_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("comple .html");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7570 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template comple .html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7757_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("comple`.html");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7757 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template comple`.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7755_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile(":KTT_LdFF3^W");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString7755 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template :KTT_LdFF3^W not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7744_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile("]dU]QbYFQRHh");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString7744 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]dU]QbYFQRHh not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString7558_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile("pTv[uc;f6|p:");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString7558 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template pTv[uc;f6|p: not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString2012_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("VD[S&(>UgG&h");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString2012 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template VD[S&(>UgG&h not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString2343_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter sw = execute("!W0x5Set%5-Y", new com.github.mustachejava.ParallelComplexObject());
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString2343 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template !W0x5Set%5-Y not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString2661_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString2661 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString2695_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}"), "test.html");
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString2695 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simple.html} not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString2662_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                        @java.lang.Override
                        public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                            if (variable.startsWith("6")) {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString2662 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString145_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("8Rf8nq`h4e=");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_21 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_22 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString145 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 8Rf8nq`h4e= not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString6422_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("Iq3E[m0> ;k");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_143 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_144 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString6422 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Iq3E[m0> ;k not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add6435() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
        java.lang.System.currentTimeMillis();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add6435__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6435__15);
        java.lang.String o_testReadmeParallel_add6435__16 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6435__16);
        java.lang.String String_135 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_135);
        boolean boolean_136 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6435__15);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6435__16);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_135);
    }

    public void testReadmeParallel_add6430() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory o_testReadmeParallel_add6430__1 = initParallel();
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeParallel_add6430__1)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeParallel_add6430__1)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeParallel_add6430__1)).getRecursionLimit())));
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
        java.lang.String o_testReadmeParallel_add6430__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6430__15);
        java.lang.String o_testReadmeParallel_add6430__16 = sw.toString();
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6430__16);
        java.lang.String String_127 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_127);
        boolean boolean_128 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeParallel_add6430__1)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeParallel_add6430__1)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeParallel_add6430__1)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6430__15);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6430__16);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_127);
    }

    public void testDeferred_literalMutationString2238_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
            mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            java.lang.Object context = new java.lang.Object() {
                java.lang.String title = "Deferred";

                java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
            };
            com.github.mustachejava.Mustache m = mf.compile("deferred.h{tml");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, context).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString2238 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template deferred.h{tml not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString5386_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
            com.github.mustachejava.Mustache compile = mf.compile("[:Cn$ ,j!-n<d07<>a8XyWNp{>?");
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
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString5386 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [:Cn$ ,j!-n<d07<>a8XyWNp{>? not found", expected.getMessage());
        }
    }
}

