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

    public void testSimple_literalMutationString92377_failAssert0_add94370_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("w0lzR+[KFs!");
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
                junit.framework.TestCase.fail("testSimple_literalMutationString92377 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString92377_failAssert0_add94370 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template w0lzR+[KFs! not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString92375() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testSimple_literalMutationString92375__7 = m.execute(sw, new java.lang.Object() {
            java.lang.String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimple_literalMutationString92375__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimple_literalMutationString92375__7)).toString());
        java.lang.String o_testSimple_literalMutationString92375__14 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString92375__14);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimple_literalMutationString92375__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimple_literalMutationString92375__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString92375__14);
    }

    public void testSimple_literalMutationString92377_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("w0lzR+[KFs!");
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
            junit.framework.TestCase.fail("testSimple_literalMutationString92377 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template w0lzR+[KFs! not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationNumber92398_literalMutationString92745_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("simp]le.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimple_literalMutationNumber92398__7 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.8)));
                }

                boolean in_ca = true;
            });
            java.lang.String o_testSimple_literalMutationNumber92398__15 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimple_literalMutationNumber92398_literalMutationString92745 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simp]le.html not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString30364_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("}TZJ(:k>A;?");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString30364 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template }TZJ(:k>A;? not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString30331_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                com.github.mustachejava.Mustache m = c.compile("`9#drg$5!Nw");
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
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString30331 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `9#drg$5!Nw not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString30330() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimpleI18N_literalMutationString30330__9 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30330__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30330__9)).toString());
            java.lang.String o_testSimpleI18N_literalMutationString30330__16 = com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString30330__16);
            sw.toString();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30330__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30330__9)).toString());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString30330__16);
        }
        {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("simple.html");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimpleI18N_literalMutationString30330__26 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30330__26)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30330__26)).toString());
            java.lang.String o_testSimpleI18N_literalMutationString30330__33 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString30330__33);
            sw.toString();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30330__26)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30330__26)).toString());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString30330__33);
        }
    }

    public void testSimpleI18N_literalMutationNumber30342_literalMutationString33541_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
                com.github.mustachejava.Mustache m = c.compile("s}mple.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_testSimpleI18N_literalMutationNumber30342__9 = m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 0;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                java.lang.String o_testSimpleI18N_literalMutationNumber30342__17 = com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
                sw.toString();
            }
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
                com.github.mustachejava.Mustache m = c.compile("simple.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_testSimpleI18N_literalMutationNumber30342__27 = m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                java.lang.String o_testSimpleI18N_literalMutationNumber30342__34 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber30342_literalMutationString33541 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template s}mple.html not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString30362() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("simple.html");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimpleI18N_literalMutationString30362__9 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30362__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30362__9)).toString());
            java.lang.String o_testSimpleI18N_literalMutationString30362__16 = com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString30362__16);
            sw.toString();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30362__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30362__9)).toString());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString30362__16);
        }
        {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimpleI18N_literalMutationString30362__26 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30362__26)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30362__26)).toString());
            java.lang.String o_testSimpleI18N_literalMutationString30362__33 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString30362__33);
            sw.toString();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30362__26)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString30362__26)).toString());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString30362__33);
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0null111643_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", null).replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0null111643 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0null111640_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0null111640 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0null111639_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0null111639 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_add111443_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_add111443 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationString110726_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", "X").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationString110726 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationBoolean110760_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = false;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationBoolean110760 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_add111450_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_add111450 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationString110719_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public java.lang.String filterText(java.lang.String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("page1.txt", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                com.github.mustachejava.Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationString110719 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
            com.github.mustachejava.Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
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
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationNumber110752_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 5000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationNumber110752 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_add111449_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
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
                com.github.mustachejava.Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
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
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_add111449 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_literalMutationString95206_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("reczursion]html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_literalMutationString95206 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template reczursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_literalMutationString95207_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("Wt9[6M|06TON Q", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_literalMutationString95207 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Wt9[6M|06TON Q not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_literalMutationString95313_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("#&o)#*`-%spC0.", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursioAn.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_literalMutationString95313 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0null95486_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion]html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0null95486 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_literalMutationString95211_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion]html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursionEtxt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_literalMutationString95211 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_literalMutationString95308_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("#&o)#*`-`%spC0.", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_literalMutationString95308 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #&o)#*`-`%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95053_failAssert0_literalMutationString95346_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("duu<.pq2mrp&z", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95053 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95053_failAssert0_literalMutationString95346 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template duu<.pq2mrp&z not found", expected.getMessage());
        }
    }

    public void testRecurisionnull95067_failAssert0_literalMutationString95360_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("`0(v4x|5D,*Sz#", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecurisionnull95067 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testRecurisionnull95067_failAssert0_literalMutationString95360 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `0(v4x|5D,*Sz# not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("#&o)#*`-%spC0.", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_literalMutationString95310_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("#&o)#*`-%spC0.", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_literalMutationString95310 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_literalMutationString95314_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("#&o)#*`-%spC0.", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.gxt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_literalMutationString95314 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_add95396_failAssert0() throws java.io.IOException {
        try {
            {
                execute("recursion]html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                java.io.StringWriter sw = execute("recursion]html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_add95396 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_add95429_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("#&o)#*`-%spC0.", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_add95429 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95058_failAssert0_literalMutationString95257_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("/BP!WMGh*C mV9", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "r3cursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95058 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95058_failAssert0_literalMutationString95257 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /BP!WMGh*C mV9 not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_add95430_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("#&o)#*`-%spC0.", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_add95430 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_add95431_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("#&o)#*`-%spC0.", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_add95431 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_add95397_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion]html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_add95397 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_add95399_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion]html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_add95399 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_add95428_failAssert0() throws java.io.IOException {
        try {
            {
                execute("#&o)#*`-%spC0.", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                java.io.StringWriter sw = execute("#&o)#*`-%spC0.", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_add95428 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95056_failAssert0_literalMutationString95267_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(".[fg,?XjY],|Y1", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "rejcursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95056 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95056_failAssert0_literalMutationString95267 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .[fg,?XjY],|Y1 not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95048_failAssert0_literalMutationString95331_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("Dk|P%R]g^KcsN6&", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95048 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95048_failAssert0_literalMutationString95331 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Dk|P%R]g^KcsN6& not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("recursion]html", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0null95502_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("#&o)#*`-%spC0.", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0null95502 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_literalMutationString95212_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion]html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "T,y&!TV!`Vw05");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_literalMutationString95212 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_add95398_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion]html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_add95398 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95049_add95369() throws java.io.IOException {
        java.io.StringWriter o_testRecurision_literalMutationString95049_add95369__1 = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecurision_literalMutationString95049_add95369__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecurision_literalMutationString95049_add95369__1)).toString());
        java.io.StringWriter sw = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        java.lang.String o_testRecurision_literalMutationString95049__11 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString95049__11);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecurision_literalMutationString95049_add95369__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecurision_literalMutationString95049_add95369__1)).toString());
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString95049__11);
    }

    public void testRecursionWithInheritance_literalMutationString138668_add138989() throws java.io.IOException {
        java.io.StringWriter o_testRecursionWithInheritance_literalMutationString138668_add138989__1 = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString138668_add138989__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString138668_add138989__1)).toString());
        java.io.StringWriter sw = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        java.lang.String o_testRecursionWithInheritance_literalMutationString138668__11 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString138668__11);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString138668_add138989__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString138668_add138989__1)).toString());
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString138668__11);
    }

    public void testRecursionWithInheritance_literalMutationString138670_failAssert0_literalMutationString138839_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("p@tb+=Mj$mwt0;h4qG0:dN.2b(t^}l", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138670 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138670_failAssert0_literalMutationString138839 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template p@tb+=Mj$mwt0;h4qG0:dN.2b(t^}l not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_add138681_literalMutationString138788_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter o_testRecursionWithInheritance_add138681__1 = execute("recursion_with_inheritance.html", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            java.io.StringWriter sw = execute("ZZwJGX<1&J(Lf|yo|7fo:+8&DWJFC!B", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            java.lang.String o_testRecursionWithInheritance_add138681__20 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_add138681_literalMutationString138788 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ZZwJGX<1&J(Lf|yo|7fo:+8&DWJFC!B not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_add139040_failAssert0() throws java.io.IOException {
        try {
            {
                execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                java.io.StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_add139040 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_add139042_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_add139042 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138675_failAssert0_literalMutationString138967_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursi]n_with_inheritance.html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138675 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138675_failAssert0_literalMutationString138967 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursi]n_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_add139043_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_add139043 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_add138683_literalMutationString138746_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("*P7%s>4A+KX^Zs**nM8&^?)n*fDVM90", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            sw.toString();
            java.lang.String o_testRecursionWithInheritance_add138683__12 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_add138683_literalMutationString138746 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template *P7%s>4A+KX^Zs**nM8&^?)n*fDVM90 not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_literalMutationString138905_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("^#$WPm7L#u$j*8gG;]Id}/4%|8]7SQ", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_literalMutationString138905 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138678_failAssert0_literalMutationString138957_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recur}ion_with_inheritance.html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recuVsion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138678 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138678_failAssert0_literalMutationString138957 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recur}ion_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_literalMutationString138903_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("^#$WPm7L#u$j*8gG;Ku]Id}/4%|8]7SQ", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_literalMutationString138903 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;Ku]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138677_failAssert0_literalMutationString138863_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("JpEd&S;Vsy* Q4t>ECPF(egWA^4b!b&", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138677 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138677_failAssert0_literalMutationString138863 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template JpEd&S;Vsy* Q4t>ECPF(egWA^4b!b& not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_literalMutationString138912_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursiontxt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_literalMutationString138912 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0null139118_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0null139118 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138680_failAssert0_literalMutationString138849_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursion_wit]_inheritance.html", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursioln.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138680 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138680_failAssert0_literalMutationString138849 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursion_wit]_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_add138682_literalMutationString138761_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("6{t]kv[am=QfOIEy;=#,!=L{4Snk%AD", new java.lang.Object() {
                java.lang.Object value = new java.lang.Object() {
                    boolean value = false;
                };
            });
            java.lang.String o_testRecursionWithInheritance_add138682__11 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            java.lang.String o_testRecursionWithInheritance_add138682__12 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_add138682_literalMutationString138761 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 6{t]kv[am=QfOIEy;=#,!=L{4Snk%AD not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_add139041_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new java.lang.Object() {
                    java.lang.Object value = new java.lang.Object() {
                        boolean value = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_add139041 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_remove91485_literalMutationString91615_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("K5;W@evixY#IO[$L3!@{S&f,8Go)8tbMu4", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            java.lang.String o_testPartialRecursionWithInheritance_remove91485__10 = com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_remove91485_literalMutationString91615 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91474_failAssert0_literalMutationString91628_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("}v{B]5JA$OI_V_+GKFI&lSs%:0 S<^ZwWm", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91474 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91474_failAssert0_literalMutationString91628 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template }v{B]5JA$OI_V_+GKFI&lSs%:0 S<^ZwWm not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91834_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91834 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91836_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91836 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0() throws java.io.IOException {
        try {
            java.io.StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new java.lang.Object() {
                java.lang.Object test = new java.lang.Object() {
                    boolean test = false;
                };
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91682_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91682 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91833_failAssert0() throws java.io.IOException {
        try {
            {
                execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                java.io.StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91833 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91469_add91790() throws java.io.IOException {
        java.io.StringWriter o_testPartialRecursionWithInheritance_literalMutationString91469_add91790__1 = execute("", new java.lang.Object() {
            java.lang.Object test = new java.lang.Object() {
                boolean test = false;
            };
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString91469_add91790__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString91469_add91790__1)).toString());
        java.io.StringWriter sw = execute("", new java.lang.Object() {
            java.lang.Object test = new java.lang.Object() {
                boolean test = false;
            };
        });
        java.lang.String o_testPartialRecursionWithInheritance_literalMutationString91469__11 = com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
        junit.framework.TestCase.assertEquals("TEST\n  TEST\n\n\n", o_testPartialRecursionWithInheritance_literalMutationString91469__11);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString91469_add91790__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString91469_add91790__1)).toString());
        junit.framework.TestCase.assertEquals("TEST\n  TEST\n\n\n", o_testPartialRecursionWithInheritance_literalMutationString91469__11);
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0null91915_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0null91915 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91470_failAssert0_literalMutationString91740_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(" does not exist", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91470_failAssert0_literalMutationString91740 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91471_failAssert0_literalMutationString91665_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("N.{anU>.u}6Sh>rY}Z|!fI{Ftiuq%=|zW", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91471 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91471_failAssert0_literalMutationString91665 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template N.{anU>.u}6Sh>rY}Z|!fI{Ftiuq%=|zW not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91473_failAssert0_literalMutationString91652_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("-gqUp,-4D v Nw*%u{h#<WC*R}T(r7:UUa]", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91473 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91473_failAssert0_literalMutationString91652 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template -gqUp,-4D v Nw*%u{h#<WC*R}T(r7:UUa] not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91680_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[U3{M[;/X9h", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91680 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[U3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91684_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partBial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91684 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91479_failAssert0_literalMutationString91718_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("recursi^e_partial_inheritance.html", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91479 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91479_failAssert0_literalMutationString91718 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template recursi^e_partial_inheritance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91480_failAssert0_literalMutationString91641_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("S5-mpPHyv`BuXXJY%&in:I47fYeJ>$5-`.", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritanceDtxt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91480 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91480_failAssert0_literalMutationString91641 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template S5-mpPHyv`BuXXJY%&in:I47fYeJ>$5-`. not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91678_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(".Q3ZzI]z7)z=F 5VJrs`>A&IO[9w2Zv{}0", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91678 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .Q3ZzI]z7)z=F 5VJrs`>A&IO[9w2Zv{}0 not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91835_failAssert0() throws java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new java.lang.Object() {
                    java.lang.Object test = new java.lang.Object() {
                        boolean test = false;
                    };
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91835 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101123_failAssert0_add103072_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("simplepragma.h%ml");
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
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123_failAssert0_add103072 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplepragma.h%ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101123_failAssert0_add103070_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                c.compile("simplepragma.h%ml");
                com.github.mustachejava.Mustache m = c.compile("simplepragma.h%ml");
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
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123_failAssert0_add103070 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplepragma.h%ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101122_failAssert0_literalMutationString102301_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("MQch#QT-z_1?+i^W[");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chis";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101122 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101122_failAssert0_literalMutationString102301 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template MQch#QT-z_1?+i^W[ not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationNumber101131_literalMutationString101452_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("K!@&`n<cGMg^wk)vl");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimplePragma_literalMutationNumber101131__7 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 0;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String o_testSimplePragma_literalMutationNumber101131__15 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationNumber101131_literalMutationString101452 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template K!@&`n<cGMg^wk)vl not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationNumber101133_literalMutationString101273_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("simp>lepragma.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimplePragma_literalMutationNumber101133__7 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 9999;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String o_testSimplePragma_literalMutationNumber101133__15 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationNumber101133_literalMutationString101273 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simp>lepragma.html not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101122_failAssert0_add103088_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                c.compile("MQch#QT-z_1?+i^W[");
                com.github.mustachejava.Mustache m = c.compile("MQch#QT-z_1?+i^W[");
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
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101122 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101122_failAssert0_add103088 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template MQch#QT-z_1?+i^W[ not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101123_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("simplepragma.h%ml");
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
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplepragma.h%ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101123_failAssert0null103282_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("simplepragma.h%ml");
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
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123_failAssert0null103282 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplepragma.h%ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101145_failAssert0_literalMutationString102141_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("O(;GP-T(!Pz[].dZQ");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "Zimple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101145 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101145_failAssert0_literalMutationString102141 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template O(;GP-T(!Pz[].dZQ not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101118() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testSimplePragma_literalMutationString101118__7 = m.execute(sw, new java.lang.Object() {
            java.lang.String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimplePragma_literalMutationString101118__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimplePragma_literalMutationString101118__7)).toString());
        java.lang.String o_testSimplePragma_literalMutationString101118__14 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString101118__14);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimplePragma_literalMutationString101118__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimplePragma_literalMutationString101118__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString101118__14);
    }

    public void testSimplePragma_literalMutationString101123_failAssert0_literalMutationString102230_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("simplepragma.h%ml");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simlple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123_failAssert0_literalMutationString102230 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplepragma.h%ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101122_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("MQch#QT-z_1?+i^W[");
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
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101122 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template MQch#QT-z_1?+i^W[ not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101119_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("simplepragma`.html");
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
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101119 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplepragma`.html not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101123_failAssert0_literalMutationString102228_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("simplepragma.h%ml");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simplk.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123_failAssert0_literalMutationString102228 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplepragma.h%ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101119_failAssert0_add103111_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                createMustacheFactory();
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("simplepragma`.html");
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
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101119 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101119_failAssert0_add103111 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplepragma`.html not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101119_failAssert0_literalMutationBoolean102438_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("simplepragma`.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = false;
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101119 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101119_failAssert0_literalMutationBoolean102438 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simplepragma`.html not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString117242_failAssert0_literalMutationString119459_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("XHqg3>LBL");
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
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString117242 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString117242_failAssert0_literalMutationString119459 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template XHqg3>LBL not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString117241() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testMultipleWrappers_literalMutationString117241__7 = m.execute(sw, new java.lang.Object() {
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
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testMultipleWrappers_literalMutationString117241__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testMultipleWrappers_literalMutationString117241__7)).toString());
        java.lang.String o_testMultipleWrappers_literalMutationString117241__23 = com.github.mustachejava.TestUtil.getContents(this.root, "simplerewrap.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString117241__23);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testMultipleWrappers_literalMutationString117241__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testMultipleWrappers_literalMutationString117241__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString117241__23);
    }

    public void testMultipleWrappers_literalMutationString117243_failAssert0_literalMutationString119205_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("X?`oO?p;SJB");
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
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString117243 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString117243_failAssert0_literalMutationString119205 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template X?`oO?p;SJB not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString3840_failAssert0_literalMutationString6391_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                com.github.mustachejava.Mustache m = c.compile("XgdI1tl@2XrA{T5P");
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
                        return "yu?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840_failAssert0_literalMutationString6391 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testNestedLatches_literalMutationString3859_literalMutationString4654_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            com.github.mustachejava.Mustache m = c.compile("latchedtes^.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer execute = m.execute(sw, new java.lang.Object() {
                java.util.concurrent.Callable<java.lang.Object> nest = () -> {
                    java.lang.Thread.sleep(300);
                    return "How";
                };

                java.util.concurrent.Callable<java.lang.Object> nested = () -> {
                    java.lang.Thread.sleep(200);
                    return "";
                };

                java.util.concurrent.Callable<java.lang.Object> nestest = () -> {
                    java.lang.Thread.sleep(100);
                    return "you?";
                };
            });
            execute.close();
            sw.toString();
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3859_literalMutationString4654 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template latchedtes^.html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString3838_failAssert0_add7774_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                c.compile("lat`hedtest.html");
                com.github.mustachejava.Mustache m = c.compile("lat`hedtest.html");
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
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString3838 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3838_failAssert0_add7774 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template lat`hedtest.html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString3840_failAssert0null8313_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                com.github.mustachejava.Mustache m = c.compile("XgdI1tl@2XrA{T5P");
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
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840_failAssert0null8313 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testNestedLatches_literalMutationString3840_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            com.github.mustachejava.Mustache m = c.compile("XgdI1tl@2XrA{T5P");
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
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testNestedLatches_literalMutationString3838_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            com.github.mustachejava.Mustache m = c.compile("lat`hedtest.html");
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
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3838 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template lat`hedtest.html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString3837_failAssert0_literalMutationString6439_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                com.github.mustachejava.Mustache m = c.compile(" does not exist");
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
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString3837 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3837_failAssert0_literalMutationString6439 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString3840_failAssert0_add7763_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                java.util.concurrent.Executors.newCachedThreadPool();
                c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                com.github.mustachejava.Mustache m = c.compile("XgdI1tl@2XrA{T5P");
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
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840_failAssert0_add7763 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testNestedLatches_literalMutationString3838_failAssert0_literalMutationString6437_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                com.github.mustachejava.Mustache m = c.compile("lat`hedtest.html");
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
                        return "mou?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString3838 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3838_failAssert0_literalMutationString6437 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template lat`hedtest.html not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38861_failAssert0null39669_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList(null);
                };
                java.io.StringWriter sw = execute("^]($i&*lB9b0", object);
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861_failAssert0null39669 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^]($i&*lB9b0 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38861_failAssert0null39671_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("^]($i&*lB9b0", null);
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861_failAssert0null39671 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^]($i&*lB9b0 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38860_failAssert0_literalMutationString39242_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("i8empty.h`tml", object);
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38860 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38860_failAssert0_literalMutationString39242 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template i8empty.h`tml not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38861_failAssert0_add39524_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("^]($i&*lB9b0", object);
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861_failAssert0_add39524 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^]($i&*lB9b0 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38853_literalMutationString38973_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("T)st");
            };
            java.io.StringWriter sw = execute("ATjJh/>oxs}i", object);
            java.lang.String o_testIsNotEmpty_literalMutationString38853__9 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38853_literalMutationString38973 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ATjJh/>oxs}i not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38861_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("^]($i&*lB9b0", object);
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^]($i&*lB9b0 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38863_failAssert0_literalMutationString39227_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("@&pqq_s+0[=Vu", object);
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38863 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38863_failAssert0_literalMutationString39227 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template @&pqq_s+0[=Vu not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38859_add39453() throws java.io.IOException {
        java.lang.Object object = new java.lang.Object() {
            java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
        };
        java.io.StringWriter o_testIsNotEmpty_literalMutationString38859_add39453__7 = execute("", object);
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testIsNotEmpty_literalMutationString38859_add39453__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testIsNotEmpty_literalMutationString38859_add39453__7)).toString());
        java.io.StringWriter sw = execute("", object);
        java.lang.String o_testIsNotEmpty_literalMutationString38859__9 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
        junit.framework.TestCase.assertEquals("Is not empty\n", o_testIsNotEmpty_literalMutationString38859__9);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testIsNotEmpty_literalMutationString38859_add39453__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testIsNotEmpty_literalMutationString38859_add39453__7)).toString());
        junit.framework.TestCase.assertEquals("Is not empty\n", o_testIsNotEmpty_literalMutationString38859__9);
    }

    public void testIsNotEmpty_literalMutationString38855_literalMutationString39056_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Te?st");
            };
            java.io.StringWriter sw = execute("|+rdw7hT)tGD", object);
            java.lang.String o_testIsNotEmpty_literalMutationString38855__9 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38855_literalMutationString39056 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template |+rdw7hT)tGD not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38861_failAssert0_literalMutationString39278_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("^]E($i&*lB9b0", object);
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861_failAssert0_literalMutationString39278 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^]E($i&*lB9b0 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38861_failAssert0_add39522_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("^]($i&*lB9b0", object);
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861_failAssert0_add39522 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ^]($i&*lB9b0 not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString60421_literalMutationString60608_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Tmest");
            };
            java.io.StringWriter sw = execute("uY <TjFFl1!l", java.util.Collections.singletonList(object));
            java.lang.String o_testImmutableList_literalMutationString60421__10 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString60421_literalMutationString60608 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template uY <TjFFl1!l not found", expected.getMessage());
        }
    }

    public void testImmutableList_add60436_literalMutationString60778_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.util.List<java.lang.Object> o_testImmutableList_add60436__7 = java.util.Collections.singletonList(object);
            java.io.StringWriter sw = execute("w0{!b9)%XXP%", java.util.Collections.singletonList(object));
            java.lang.String o_testImmutableList_add60436__11 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_add60436_literalMutationString60778 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template w0{!b9)%XXP% not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString60424_failAssert0_add61140_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("`7!c,/#KP%t?", java.util.Collections.singletonList(object));
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString60424 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString60424_failAssert0_add61140 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `7!c,/#KP%t? not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString60424_failAssert0_literalMutationString60883_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("`7!c,/#nP%t?", java.util.Collections.singletonList(object));
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString60424 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString60424_failAssert0_literalMutationString60883 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `7!c,/#nP%t? not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString60424_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
            };
            java.io.StringWriter sw = execute("`7!c,/#KP%t?", java.util.Collections.singletonList(object));
            com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString60424 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `7!c,/#KP%t? not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString60427_failAssert0_literalMutationString60939_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("CKzfk^<kPoC", java.util.Collections.singletonList(object));
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString60427 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString60427_failAssert0_literalMutationString60939 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template CKzfk^<kPoC not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString60424_failAssert0null61304_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute("`7!c,/#KP%t?", java.util.Collections.singletonList(null));
                com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString60424 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString60424_failAssert0null61304 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `7!c,/#KP%t? not found", expected.getMessage());
        }
    }

    public void testImmutableListnull60440_literalMutationString60655_failAssert0() throws java.io.IOException {
        try {
            java.lang.Object object = new java.lang.Object() {
                java.util.List<java.lang.String> people = java.util.Collections.singletonList(null);
            };
            java.io.StringWriter sw = execute("1--1Y>(1k@7Y", java.util.Collections.singletonList(object));
            java.lang.String o_testImmutableListnull60440__10 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableListnull60440_literalMutationString60655 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testImmutableListnull60443_failAssert0_literalMutationString61009_failAssert0() throws java.io.IOException {
        try {
            {
                java.lang.Object object = new java.lang.Object() {
                    java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
                };
                java.io.StringWriter sw = execute(">q/m[mum]@/t", java.util.Collections.singletonList(object));
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testImmutableListnull60443 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testImmutableListnull60443_failAssert0_literalMutationString61009 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >q/m[mum]@/t not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106531_failAssert0_literalMutationString108099_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("se>urity.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private java.lang.String test = "page1.txt";
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106531 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106531_failAssert0_literalMutationString108099 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template se>urity.html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106529_failAssert0_literalMutationString108359_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("security.^html");
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
                com.github.mustachejava.TestUtil.getContents(this.root, "securi#y.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106529 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106529_failAssert0_literalMutationString108359 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template security.^html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106529_failAssert0null109358_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("security.^html");
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
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106529 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106529_failAssert0null109358 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template security.^html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106531_failAssert0_add109095_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                createMustacheFactory();
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("se>urity.html");
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
                junit.framework.TestCase.fail("testSecurity_literalMutationString106531 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106531_failAssert0_add109095 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template se>urity.html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106529_failAssert0_literalMutationString108328_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("securiSty.^html");
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
                junit.framework.TestCase.fail("testSecurity_literalMutationString106529 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106529_failAssert0_literalMutationString108328 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template securiSty.^html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106528_failAssert0null109351_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("&cV:b>D[ompZg");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.lang.Object() {
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
                junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528_failAssert0null109351 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106528_failAssert0_add109126_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                c.compile("&cV:b>D[ompZg");
                com.github.mustachejava.Mustache m = c.compile("&cV:b>D[ompZg");
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
                junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528_failAssert0_add109126 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106553_literalMutationString107549_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("(SlQY|!3;--wE");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSecurity_literalMutationString106553__7 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private java.lang.String test = "";
            });
            java.lang.String o_testSecurity_literalMutationString106553__15 = com.github.mustachejava.TestUtil.getContents(this.root, "security.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString106553_literalMutationString107549 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template (SlQY|!3;--wE not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106528_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("&cV:b>D[ompZg");
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
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106529_failAssert0_add109137_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                createMustacheFactory();
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("security.^html");
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
                junit.framework.TestCase.fail("testSecurity_literalMutationString106529 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106529_failAssert0_add109137 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template security.^html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106531_failAssert0null109337_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("se>urity.html");
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
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106531 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106531_failAssert0null109337 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template se>urity.html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106529_failAssert0_add109138_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                c.compile("security.^html");
                com.github.mustachejava.Mustache m = c.compile("security.^html");
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
                junit.framework.TestCase.fail("testSecurity_literalMutationString106529 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106529_failAssert0_add109138 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template security.^html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106531_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("se>urity.html");
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
            junit.framework.TestCase.fail("testSecurity_literalMutationString106531 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template se>urity.html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106527() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testSecurity_literalMutationString106527__7 = m.execute(sw, new java.lang.Object() {
            java.lang.String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;

            private java.lang.String test = "Test";
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSecurity_literalMutationString106527__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSecurity_literalMutationString106527__7)).toString());
        java.lang.String o_testSecurity_literalMutationString106527__15 = com.github.mustachejava.TestUtil.getContents(this.root, "security.txt");
        junit.framework.TestCase.assertEquals("", o_testSecurity_literalMutationString106527__15);
        sw.toString();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSecurity_literalMutationString106527__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSecurity_literalMutationString106527__7)).toString());
        junit.framework.TestCase.assertEquals("", o_testSecurity_literalMutationString106527__15);
    }

    public void testSecurity_literalMutationString106528_failAssert0_literalMutationString108262_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("&cV:b>D[ompZg");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private java.lang.String test = "Test";
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528_failAssert0_literalMutationString108262 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106528_failAssert0_literalMutationString108266_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("&cV:b>D[ompZg");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String name = "Chvris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private java.lang.String test = "Test";
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528_failAssert0_literalMutationString108266 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106529_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("security.^html");
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
            junit.framework.TestCase.fail("testSecurity_literalMutationString106529 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template security.^html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106528_failAssert0_add109130_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("&cV:b>D[ompZg");
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
                junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528_failAssert0_add109130 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106528_failAssert0null109352_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("&cV:b>D[ompZg");
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
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528_failAssert0null109352 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString113426_failAssert0_add115540_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("#}p3PUrWuJ&");
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
                junit.framework.TestCase.fail("testProperties_literalMutationString113426 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString113426_failAssert0_add115540 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #}p3PUrWuJ& not found", expected.getMessage());
        }
    }

    public void testProperties_remove113463_literalMutationString114445_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("J9:C+(O@a7!");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testProperties_remove113463__7 = m.execute(sw, new java.lang.Object() {
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
            java.lang.String o_testProperties_remove113463__21 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testProperties_remove113463_literalMutationString114445 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
        }
    }

    public void testProperties_literalMutationString113426_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("#}p3PUrWuJ&");
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
            junit.framework.TestCase.fail("testProperties_literalMutationString113426 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #}p3PUrWuJ& not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString113426_failAssert0_literalMutationString114520_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("#}p3PUrWuJ&");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    java.lang.String getName() {
                        return "Ch|is";
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
                junit.framework.TestCase.fail("testProperties_literalMutationString113426 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString113426_failAssert0_literalMutationString114520 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template #}p3PUrWuJ& not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString113425() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testProperties_literalMutationString113425__7 = m.execute(sw, new java.lang.Object() {
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
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testProperties_literalMutationString113425__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testProperties_literalMutationString113425__7)).toString());
        java.lang.String o_testProperties_literalMutationString113425__22 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString113425__22);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testProperties_literalMutationString113425__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testProperties_literalMutationString113425__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString113425__22);
    }

    public void testProperties_literalMutationString113450_failAssert0_literalMutationString114762_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("simp{le.html");
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
                com.github.mustachejava.TestUtil.getContents(this.root, "simpl<e.txt");
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString113450 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString113450_failAssert0_literalMutationString114762 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template simp{le.html not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString23910_literalMutationString24391_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            java.io.StringWriter sw = execute("s:mple.html", new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    java.lang.Object o_testSimpleWithMap_literalMutationString23910__8 = put("name", "Chris");
                    java.lang.Object o_testSimpleWithMap_literalMutationString23910__9 = put("value", 10000);
                    java.lang.Object o_testSimpleWithMap_literalMutationString23910__10 = put("page1.txt", 6000);
                    java.lang.Object o_testSimpleWithMap_literalMutationString23910__11 = put("in_ca", true);
                }
            });
            java.lang.String o_testSimpleWithMap_literalMutationString23910__12 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString23910_literalMutationString24391 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template s:mple.html not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString23897_literalMutationString24611_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            java.io.StringWriter sw = execute("wLa&3HRp%`o", new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    java.lang.Object o_testSimpleWithMap_literalMutationString23897__8 = put("name", "Chris");
                    java.lang.Object o_testSimpleWithMap_literalMutationString23897__9 = put("vlue", 10000);
                    java.lang.Object o_testSimpleWithMap_literalMutationString23897__10 = put("taxed_value", 6000);
                    java.lang.Object o_testSimpleWithMap_literalMutationString23897__11 = put("in_ca", true);
                }
            });
            java.lang.String o_testSimpleWithMap_literalMutationString23897__12 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString23897_literalMutationString24611 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template wLa&3HRp%`o not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_add23934_literalMutationString27055_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            java.io.StringWriter sw = execute("o!vp#[pb>w1", new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    java.lang.Object o_testSimpleWithMap_add23934__8 = put("name", "Chris");
                    java.lang.Object o_testSimpleWithMap_add23934__9 = put("name", "Chris");
                    java.lang.Object o_testSimpleWithMap_add23934__10 = put("value", 10000);
                    java.lang.Object o_testSimpleWithMap_add23934__11 = put("taxed_value", 6000);
                    java.lang.Object o_testSimpleWithMap_add23934__12 = put("in_ca", true);
                }
            });
            java.lang.String o_testSimpleWithMap_add23934__13 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_add23934_literalMutationString27055 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template o!vp#[pb>w1 not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString23930_failAssert0_literalMutationString28011_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        try {
            {
                java.io.StringWriter sw = execute("lIV?>vW%L$T", new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString23930 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString23930_failAssert0_literalMutationString28011 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template lIV?>vW%L$T not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95955_failAssert0_literalMutationString96068_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile(".UO.-0reyG2)mQP}*;_8EElfkUhH:5");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95955 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95955_failAssert0_literalMutationString96068 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .UO.-0reyG2)mQP}*;_8EElfkUhH:5 not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95954_failAssert0_literalMutationString96047_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("{*<Zv8(CDjk*<;w/M#&<J4Xd_#4fn");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95954 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95954_failAssert0_literalMutationString96047 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template {*<Zv8(CDjk*<;w/M#&<J4Xd_#4fn not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95954_failAssert0_literalMutationString96049_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("partiali:ntempatefunction.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95954 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95954_failAssert0_literalMutationString96049 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template partiali:ntempatefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTFnull95964_failAssert0_literalMutationString96080_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("6NVS*>#5mE(.X##)^I<DXxW-g$Cbsw");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTFnull95964 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTFnull95964_failAssert0_literalMutationString96080 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 6NVS*>#5mE(.X##)^I<DXxW-g$Cbsw not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add95958_literalMutationString96019_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache o_testPartialWithTF_add95958__3 = c.compile("partialintemplatefunction.html");
            com.github.mustachejava.Mustache m = c.compile("partialintemplatefunctio[n.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_add95958__8 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add95958_literalMutationString96019 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template partialintemplatefunctio[n.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96055_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("1X8&`T+_*D]5*fbKoc1?{G|8A`s=7u");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96055 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1X8&`T+_*D]5*fbKoc1?{G|8A`s=7u not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96053_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("/ml6k7!M$u{h#XpDI9{xqeK)9gy&gj");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96053 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /ml6k7!M$u{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add95958_literalMutationString96017_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache o_testPartialWithTF_add95958__3 = c.compile("partialintemplatefunction.html");
            com.github.mustachejava.Mustache m = c.compile("/mWpKsDrYvzDI[V7BcfiB.f>(?vgrj");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_add95958__8 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add95958_literalMutationString96017 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /mWpKsDrYvzDI[V7BcfiB.f>(?vgrj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95953_failAssert0_literalMutationString96060_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95953 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95953_failAssert0_literalMutationString96060 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95951_add96087() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testPartialWithTF_literalMutationString95951_add96087__7 = m.execute(sw, new java.lang.Object() {
            public com.github.mustachejava.TemplateFunction i() {
                return ( s) -> s;
            }
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialWithTF_literalMutationString95951_add96087__7)).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialWithTF_literalMutationString95951_add96087__7)).getBuffer())).toString());
        java.io.Writer o_testPartialWithTF_literalMutationString95951__7 = m.execute(sw, new java.lang.Object() {
            public com.github.mustachejava.TemplateFunction i() {
                return ( s) -> s;
            }
        });
        sw.toString();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialWithTF_literalMutationString95951_add96087__7)).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialWithTF_literalMutationString95951_add96087__7)).getBuffer())).toString());
    }

    public void testPartialWithTF_add95958_literalMutationString96009_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache o_testPartialWithTF_add95958__3 = c.compile("B1qCfB0]De/ym1%_O.;{gtdojuHL{2");
            com.github.mustachejava.Mustache m = c.compile("partialintemplatefunction.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_add95958__8 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add95958_literalMutationString96009 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template B1qCfB0]De/ym1%_O.;{gtdojuHL{2 not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_remove95962_literalMutationString96034_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("partialintemplatefunction.[html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_remove95962__7 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_remove95962_literalMutationString96034 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template partialintemplatefunction.[html not found", expected.getMessage());
        }
    }

    public void testPartialWithTFnull95964_failAssert0_literalMutationString96079_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("partialintemplatefunction.ht[ml");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTFnull95964 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTFnull95964_failAssert0_literalMutationString96079 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template partialintemplatefunction.ht[ml not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_add96224_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                com.github.mustachejava.Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_add96224 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96056_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("/ml6k7!M$J{#XpDI9{xqeK)9gy&gj");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96056 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /ml6k7!M$J{#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96052_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{zxqeK)9gy&gj");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96052 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{zxqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95952_failAssert0_literalMutationString96074_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("partialiFn<templatefunction.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95952 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95952_failAssert0_literalMutationString96074 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template partialiFn<templatefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95951() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testPartialWithTF_literalMutationString95951__7 = m.execute(sw, new java.lang.Object() {
            public com.github.mustachejava.TemplateFunction i() {
                return ( s) -> s;
            }
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialWithTF_literalMutationString95951__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialWithTF_literalMutationString95951__7)).toString());
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialWithTF_literalMutationString95951__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialWithTF_literalMutationString95951__7)).toString());
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_add96227_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_add96227 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_add96225_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
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
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_add96225 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95952_failAssert0_literalMutationString96071_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("8TSijT0B%+n|8$V[8t6d8FQ+9qw*43f");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95952 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95952_failAssert0_literalMutationString96071 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 8TSijT0B%+n|8$V[8t6d8FQ+9qw*43f not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95953_failAssert0_literalMutationString96062_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("/!j;}C5up");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95953 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95953_failAssert0_literalMutationString96062 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /!j;}C5up not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_add96223_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                createMustacheFactory();
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_add96223 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add95960_literalMutationString96032_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("pa}rtialintemplatefunction.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_add95960__7 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add95960_literalMutationString96032 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template pa}rtialintemplatefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add95957_literalMutationString96025_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory o_testPartialWithTF_add95957__1 = createMustacheFactory();
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("`tdYKM(JOCqX7$(k={m)JwH54Q&:.,");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_add95957__8 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add95957_literalMutationString96025 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `tdYKM(JOCqX7$(k={m)JwH54Q&:., not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add95959_literalMutationString96007_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("i*&oUH{aRA/24$),9E@g:ljR?D3xdi");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_add95959__7 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            java.io.Writer o_testPartialWithTF_add95959__14 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add95959_literalMutationString96007 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template i*&oUH{aRA/24$),9E@g:ljR?D3xdi not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_remove95962_literalMutationString96036_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("NJZV *,B[N:%IWFqsGrAjwrz`W(mzM");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testPartialWithTF_remove95962__7 = m.execute(sw, new java.lang.Object() {
                public com.github.mustachejava.TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_remove95962_literalMutationString96036 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template NJZV *,B[N:%IWFqsGrAjwrz`W(mzM not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_remove95961_literalMutationString96043_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("D$ftQUA(k, # Bn<|FSu0[+>/c([-!");
            java.io.StringWriter sw = new java.io.StringWriter();
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_remove95961_literalMutationString96043 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template D$ftQUA(k, # Bn<|FSu0[+>/c([-! not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0null96285_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0null96285 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_add96226_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.lang.Object() {
                    public com.github.mustachejava.TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_add96226 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0_add146226_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("com plex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                createMustacheFactory().compile("complex.html");
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144908_failAssert0_add146226 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com plex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0_add146225_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("com plex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144908_failAssert0_add146225 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com plex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0null149372_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(".9m7se)D;[e[");
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
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147452_failAssert0null149372 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0_literalMutationString145452_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("45]_R11%HewT");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144919_failAssert0_literalMutationString145452 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0_literalMutationString147944_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(".9m7se)D;[e[");
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
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147452_failAssert0_literalMutationString147944 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile(".9m7se)D;[e[");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147465_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("0=ANDO]TejGV");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString147465 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 0=ANDO]TejGV not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144909_failAssert0_literalMutationString145204_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("complex. html");
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
                junit.framework.TestCase.fail("testComplex_literalMutationString144909 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144909_failAssert0_literalMutationString145204 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex. html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147467_failAssert0_literalMutationString148114_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                m = createMustacheFactory().compile("2V-e6x6fvC,`");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147467 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147467_failAssert0_literalMutationString148114 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 2V-e6x6fvC,` not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147465_failAssert0null149410_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                m = createMustacheFactory().compile("0=ANDO]TejGV");
                m.execute(null, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147465 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147465_failAssert0null149410 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 0=ANDO]TejGV not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147465_failAssert0_add148860_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                c.compile("complex.html");
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
                m = createMustacheFactory().compile("0=ANDO]TejGV");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147465 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147465_failAssert0_add148860 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 0=ANDO]TejGV not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0_add148795_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(".9m7se)D;[e[");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147452_failAssert0_add148795 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0null146891_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(".gU[j[4>bs^N");
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
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144906_failAssert0null146891 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147465_failAssert0_add148864_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("0=ANDO]TejGV");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147465 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147465_failAssert0_add148864 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 0=ANDO]TejGV not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0null146845_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                m = createMustacheFactory().compile("45]_R11%HewT");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144919_failAssert0null146845 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0_literalMutationString145477_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                m = createMustacheFactory().compile("45]_R11%HewT");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144919_failAssert0_literalMutationString145477 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplexnull147505_failAssert0_literalMutationString148256_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                m = createMustacheFactory().compile("l{%nj|p@I/##");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testComplexnull147505 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull147505_failAssert0_literalMutationString148256 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template l{%nj|p@I/## not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            m = createMustacheFactory().compile("45]_R11%HewT");
            m.execute(sw, o);
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0_literalMutationString145568_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(".|gU[j[4>bs^N");
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
                junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144906_failAssert0_literalMutationString145568 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .|gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile(".gU[j[4>bs^N");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144905_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile("]omplex.html");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString144905 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]omplex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0_add146301_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("45]_R11%HewT");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144919_failAssert0_add146301 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile("com plex.html");
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
            junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com plex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0_literalMutationString145578_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(".gU[j[4>bs^N");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144906_failAssert0_literalMutationString145578 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144905_failAssert0null146927_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("]omplex.html");
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
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144905 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144905_failAssert0null146927 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]omplex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0_literalMutationString145355_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("com plex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "[Djs1Zk_Z(+");
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
                junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144908_failAssert0_literalMutationString145355 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com plex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0_add146372_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(".gU[j[4>bs^N");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
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
                junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144906_failAssert0_add146372 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144905_failAssert0null146925_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("]omplex.html");
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
                m.execute(null, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144905 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144905_failAssert0null146925 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]omplex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0_add146376_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(".gU[j[4>bs^N");
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
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144906_failAssert0_add146376 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144905_failAssert0_add146441_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("]omplex.html");
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
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144905 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144905_failAssert0_add146441 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]omplex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0_add148791_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(".9m7se)D;[e[");
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
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147452_failAssert0_add148791 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0null146851_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                m = createMustacheFactory().compile("45]_R11%HewT");
                m.execute(null, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144919_failAssert0null146851 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144905_failAssert0_literalMutationString145658_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("]omplex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ComplexObject());
                jg.writeEndObject();
                jg.flush();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("compl]x.html");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144905 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144905_failAssert0_literalMutationString145658 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]omplex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0null146896_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(".gU[j[4>bs^N");
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
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144906_failAssert0null146896 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0_literalMutationString145347_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("tv)z[/|*80DLZ");
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
                junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144908_failAssert0_literalMutationString145347 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template tv)z[/|*80DLZ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0_add146296_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.fasterxml.jackson.databind.JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                java.lang.Object o = com.github.mustachejavabenchmarks.JsonInterpreterTest.toObject(jsonNode);
                sw = new java.io.StringWriter();
                m = createMustacheFactory().compile("45]_R11%HewT");
                m.execute(sw, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144919_failAssert0_add146296 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0null149365_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(".9m7se)D;[e[");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new com.github.mustachejava.ComplexObject());
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
                junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147452_failAssert0null149365 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0_literalMutationString147956_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(".9m7se)D;[e[");
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
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147452_failAssert0_literalMutationString147956 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0null146805_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("com plex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new com.github.mustachejava.ComplexObject());
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
                junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144908_failAssert0null146805 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com plex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0null146809_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("com plex.html");
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
                m.execute(null, o);
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144908_failAssert0null146809 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com plex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144905_failAssert0_literalMutationString145650_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile("]omplex.html");
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
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144905 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144905_failAssert0_literalMutationString145650 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ]omplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42531_failAssert0_literalMutationString42823_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("`l;Tv+z}E=w:");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42531 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42531_failAssert0_literalMutationString42823 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template `l;Tv+z}E=w: not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0null43204_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("com}lex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0null43204 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_add42534_literalMutationString42640_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory o_testComplexParallel_add42534__1 = initParallel();
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("[6W]^uCA:$7%");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            java.lang.String o_testComplexParallel_add42534__11 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_add42534_literalMutationString42640 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [6W]^uCA:$7% not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0_add43099_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("com}lex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject());
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0_add43099 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0_literalMutationString42806_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("c=m}lex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0_literalMutationString42806 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template c=m}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42525_failAssert0_add43089_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                initParallel();
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("H;-lxyWP[r&!");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525_failAssert0_add43089 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template H;-lxyWP[r&! not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0_literalMutationString42816_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("com}lex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "compex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0_literalMutationString42816 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42525_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("H;-lxyWP[r&!");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template H;-lxyWP[r&! not found", expected.getMessage());
        }
    }

    public void testComplexParallel_add42536_literalMutationString42666_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("nx]ZOftBz##y");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            java.lang.String o_testComplexParallel_add42536__13 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_add42536_literalMutationString42666 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template nx]ZOftBz##y not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0_add43100_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("com}lex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0_add43100 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("com}lex.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42525_failAssert0null43201_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("H;-lxyWP[r&!");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(null, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525_failAssert0null43201 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template H;-lxyWP[r&! not found", expected.getMessage());
        }
    }

    public void testComplexParallel_remove42540_literalMutationString42692_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("n2OGu,b^uE>I");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.lang.String o_testComplexParallel_remove42540__7 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_remove42540_literalMutationString42692 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template n2OGu,b^uE>I not found", expected.getMessage());
        }
    }

    public void testComplexParallel_remove42540_literalMutationString42694_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile(" omplex.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.lang.String o_testComplexParallel_remove42540__7 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_remove42540_literalMutationString42694 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  omplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_add42538_literalMutationString42609_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("a=rhh`u`xwy[");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
            java.lang.String o_testComplexParallel_add42538__10 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            java.lang.String o_testComplexParallel_add42538__11 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_add42538_literalMutationString42609 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template a=rhh`u`xwy[ not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0_literalMutationString42810_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("com}lex.hml");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0_literalMutationString42810 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com}lex.hml not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42525_failAssert0_add43090_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                c.compile("H;-lxyWP[r&!");
                com.github.mustachejava.Mustache m = c.compile("H;-lxyWP[r&!");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525_failAssert0_add43090 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template H;-lxyWP[r&! not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42525_failAssert0_literalMutationString42803_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("H;-lxyWP[r&!");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.xt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525_failAssert0_literalMutationString42803 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template H;-lxyWP[r&! not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0null43205_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("com}lex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0null43205 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0_add43096_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                initParallel();
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("com}lex.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0_add43096 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_add47753_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("1^Yvtbf`[k-R", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_add47753 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_add47751_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("1^Yvtbf`[k-R", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_add47751 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47469_failAssert0_literalMutationString47618_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute(" does not exist", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47469 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47469_failAssert0_literalMutationString47618 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter sw = execute("1^Yvtbf`[k-R", new com.github.mustachejava.ParallelComplexObject());
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47477_failAssert0_literalMutationString47702_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("yJ>+G(EovsC3", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complexstxt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47477 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47477_failAssert0_literalMutationString47702 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template yJ>+G(EovsC3 not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47469_failAssert0_literalMutationString47621_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("<CB0G`-DC", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47469 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47469_failAssert0_literalMutationString47621 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template <CB0G`-DC not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47587_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("complex.h`tml", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47587 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47468_literalMutationString47523_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter sw = execute("`", new com.github.mustachejava.ParallelComplexObject());
            java.lang.String o_testSerialCallable_literalMutationString47468__4 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47468_literalMutationString47523 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template ` not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_add47744_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("complex.h`tml", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_add47744 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_add47752_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("1^Yvtbf`[k-R", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_add47752 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47608_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("R^Yvtbf`[k-R", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47608 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template R^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0null47814_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("1^Yvtbf`[k-R", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0null47814 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallablenull47484_failAssert0_literalMutationString47710_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("4&]Q5@]s%(Ia", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallablenull47484 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSerialCallablenull47484_failAssert0_literalMutationString47710 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 4&]Q5@]s%(Ia not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47588_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("complex.h`tml", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "compleZx.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47588 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_add47745_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("complex.h`tml", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_add47745 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47473_failAssert0_literalMutationString47645_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("comp]lex.htl", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47473 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47473_failAssert0_literalMutationString47645 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template comp]lex.htl not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47614_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("1^Yvtbf`[k-R", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "D#>?yh`KbTq");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47614 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47468_add47721() throws com.github.mustachejava.MustacheException, java.io.IOException {
        java.io.StringWriter o_testSerialCallable_literalMutationString47468_add47721__1 = execute("", new com.github.mustachejava.ParallelComplexObject());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSerialCallable_literalMutationString47468_add47721__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSerialCallable_literalMutationString47468_add47721__1)).toString());
        java.io.StringWriter sw = execute("", new com.github.mustachejava.ParallelComplexObject());
        java.lang.String o_testSerialCallable_literalMutationString47468__4 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
        junit.framework.TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString47468__4);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSerialCallable_literalMutationString47468_add47721__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSerialCallable_literalMutationString47468_add47721__1)).toString());
        junit.framework.TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString47468__4);
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47610_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("1^Yvtbf`[k-R", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47610 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_add47743_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("complex.h`tml", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_add47743 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47474_failAssert0_literalMutationString47681_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("complex`html", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47474 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47474_failAssert0_literalMutationString47681 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex`html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            java.io.StringWriter sw = execute("complex.h`tml", new com.github.mustachejava.ParallelComplexObject());
            com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47474_failAssert0_literalMutationString47677_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("fO.f#9583>+d", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47474 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47474_failAssert0_literalMutationString47677 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template fO.f#9583>+d not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47585_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("compl4x.h`tml", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47585 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template compl4x.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47589_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("complex.h`tml", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.xt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47589 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47615_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("1^Yvtbf`[k-R", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "/omplex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47615 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47611_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("1^Yvtbf`[k-R", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "cmplex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47611 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0null47810_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                java.io.StringWriter sw = execute("complex.h`tml", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0null47810 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_add47750_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                execute("1^Yvtbf`[k-R", new com.github.mustachejava.ParallelComplexObject());
                java.io.StringWriter sw = execute("1^Yvtbf`[k-R", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_add47750 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_add47742_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                execute("complex.h`tml", new com.github.mustachejava.ParallelComplexObject());
                java.io.StringWriter sw = execute("complex.h`tml", new com.github.mustachejava.ParallelComplexObject());
                com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_add47742 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52507_failAssert0null59931_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                        put("foo", null);
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507_failAssert0null59931 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52488_failAssert0_add58717_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                            appendText(execute);
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488_failAssert0_add58717 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52577_failAssert0_literalMutationString56404_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("H")) {
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
                        put("fo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52577 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52577_failAssert0_literalMutationString56404 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52587_failAssert0_literalMutationString57435_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "t) D57");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simle.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52587 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52587_failAssert0_literalMutationString57435 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template t) D57.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52507_failAssert0_add58654_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507_failAssert0_add58654 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0_add58740_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                            sw.toString();
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
                        put("foo", ".d]EMK");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583_failAssert0_add58740 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52521_failAssert0_literalMutationString53356_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                                throw new com.github.mustachejava.MustacheException((" w|7U}M-xG0(Q(O*RAyTjlP]bCd.07u&nyL(vkk" + (name)));
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
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ foo].html}}"), "test.html");
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521_failAssert0_literalMutationString53356 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52499_failAssert0_add58626_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499_failAssert0_add58626 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52498_failAssert0_add57971_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498_failAssert0_add57971 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52581_failAssert0_literalMutationString54697_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "F", "]");
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
                        put("foo", "page1.txt");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52581 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52581_failAssert0_literalMutationString54697 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0_literalMutationString57578_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                                throw new com.github.mustachejava.MustacheException(("Faled to parse partial name template: " + (name)));
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
                        put("foo", ".d]EMK");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583_failAssert0_literalMutationString57578 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52584_failAssert0_literalMutationString56731_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                        put("foo", "s^mple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584_failAssert0_literalMutationString56731 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                @java.lang.Override
                public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                    return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                        @java.lang.Override
                        public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                            if (variable.startsWith("D")) {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0null60013_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put(null, 6000);
                        put("in_ca", true);
                        put("foo", ".d]EMK");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583_failAssert0null60013 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52497_failAssert0null58899_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497_failAssert0null58899 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52497_failAssert0_add57790_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                            sw.toString();
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497_failAssert0_add57790 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52521_failAssert0_literalMutationString53348_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ foo].html}}"), "test.html");
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521_failAssert0_literalMutationString53348 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52499_failAssert0_literalMutationString57023_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("")) {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499_failAssert0_literalMutationString57023 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52590_failAssert0_literalMutationNumber53547_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.TestUtil.getContents(this.root, "TkFe<_DizS");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52590 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52590_failAssert0_literalMutationNumber53547 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52498_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52521_failAssert0null58834_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put(null, 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521_failAssert0null58834 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0null59248_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("D")) {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489_failAssert0null59248 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52584_failAssert0_add58551_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "s^mple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584_failAssert0_add58551 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template s^mple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0_add58088_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("D")) {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489_failAssert0_add58088 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0_literalMutationString57632_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("Bn_ca", true);
                        put("foo", ".d]EMK");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583_failAssert0_literalMutationString57632 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52488_failAssert0null59963_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488_failAssert0null59963 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52498_failAssert0_literalMutationString54379_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                            partial = df.compile(new java.io.StringReader(name), "page1.txt", "[", "]");
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498_failAssert0_literalMutationString54379 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52499_failAssert0null59847_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499_failAssert0null59847 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52488_failAssert0_literalMutationNumber57523_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                        put("taxed_value", 3000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488_failAssert0_literalMutationNumber57523 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52499_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52497_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0_literalMutationString54925_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("D")) {
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
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "te!t.html");
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489_failAssert0_literalMutationString54925 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52584_failAssert0null59759_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "s^mple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584_failAssert0null59759 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template s^mple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52488_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0_add58101_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("D")) {
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
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489_failAssert0_add58101 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52498_failAssert0null59116_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498_failAssert0null59116 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", ".d]EMK");
                }
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52584_failAssert0_literalMutationNumber56720_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                    list.add(new com.github.mustachejava.codes.PartialCode(partialTC, df, variable.substring(2).trim()) {
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
                        put("foo", "s^mple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584_failAssert0_literalMutationNumber56720 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template s^mple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52584_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "s^mple");
                }
            });
            com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template s^mple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0null60014_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ [foo].html}}"), "test.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, new java.util.HashMap<java.lang.String, java.lang.Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", null);
                        put("in_ca", true);
                        put("foo", ".d]EMK");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583_failAssert0null60014 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52521_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ foo].html}}"), "test.html");
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52584_failAssert0_add58537_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                            df.compile(new java.io.StringReader(name), "__dynpartial__", "[", "]");
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
                        put("foo", "s^mple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584_failAssert0_add58537 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template s^mple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52507_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0_add58731_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("+")) {
                                    tc.line();
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
                        put("foo", ".d]EMK");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583_failAssert0_add58731 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0_literalMutationString54973_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("D")) {
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
                        put("f]oo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489_failAssert0_literalMutationString54973 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0null59270_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(this.root) {
                    @java.lang.Override
                    public com.github.mustachejava.MustacheVisitor createMustacheVisitor() {
                        return new com.github.mustachejava.DefaultMustacheVisitor(this) {
                            @java.lang.Override
                            public void partial(com.github.mustachejava.TemplateContext tc, java.lang.String variable) {
                                if (variable.startsWith("D")) {
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
                        put("taxed_value", null);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489_failAssert0null59270 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52497_failAssert0_literalMutationNumber53704_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                        put("value", 10001);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497_failAssert0_literalMutationNumber53704 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52507_failAssert0_literalMutationString57262_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                            partial = df.compile(new java.io.StringReader(name), "__dynpartial__", "page1.txt", "]");
                                            if ((partial) == null) {
                                                throw new com.github.mustachejava.MustacheException(("8SmTJJgF&_Bo/KU_=CWKp!Y%]<*lM:VqgMn_H*k" + (name)));
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507_failAssert0_literalMutationString57262 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52521_failAssert0_add57715_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                                            sw.toString();
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
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ foo].html}}"), "test.html");
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521_failAssert0_add57715 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52521_failAssert0_add57711_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
                com.github.mustachejava.Mustache m = c.compile(new java.io.StringReader("{{>+ foo].html}}"), "test.html");
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
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521_failAssert0_add57711 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template foo].html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0null16630_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("os)Y{d4Yos");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0null16630 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15922_add16277() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadme_literalMutationString15922_add16277__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadme_literalMutationString15922_add16277__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadme_literalMutationString15922_add16277__9)).toString());
        java.io.Writer o_testReadme_literalMutationString15922__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadme_literalMutationString15922__13 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString15922__13);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadme_literalMutationString15922_add16277__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadme_literalMutationString15922_add16277__9)).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString15922__13);
    }

    public void testReadme_literalMutationString15927_failAssert0null16629_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("os)Y{d4Yos");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(null, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0null16629 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_remove15941_literalMutationString16110_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("items. html");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            long diff = (java.lang.System.currentTimeMillis()) - start;
            java.lang.String o_testReadme_remove15941__11 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_remove15941_literalMutationString16110 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template items. html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0_add16488_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("os)Y{d4Yos");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0_add16488 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15924_failAssert0_literalMutationString16121_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("(P#!}q:J;");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15924 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15924_failAssert0_literalMutationString16121 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template (P#!}q:J; not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0_add16486_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("os)Y{d4Yos");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0_add16486 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0_add16481_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                createMustacheFactory();
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("os)Y{d4Yos");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0_add16481 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("os)Y{d4Yos");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0_literalMutationString16133_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("os)Y{,d4Yos");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0_literalMutationString16133 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template os)Y{,d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0_literalMutationString16140_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("os)Y{d4Yos");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "it;ms.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0_literalMutationString16140 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_add15934_literalMutationString16014_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory o_testReadme_add15934__1 = createMustacheFactory();
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("Rd[]A@Kk-<");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            java.io.Writer o_testReadme_add15934__10 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            java.lang.String o_testReadme_add15934__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_add15934_literalMutationString16014 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template Rd[]A@Kk-< not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15922() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadme_literalMutationString15922__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadme_literalMutationString15922__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadme_literalMutationString15922__9)).toString());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadme_literalMutationString15922__13 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString15922__13);
        sw.toString();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadme_literalMutationString15922__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadme_literalMutationString15922__9)).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString15922__13);
    }

    public void testReadme_literalMutationString15927_failAssert0_literalMutationString16129_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("os)Y{04Yos");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0_literalMutationString16129 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template os)Y{04Yos not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add2330_add3140() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        m.getName();
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add2330__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        sw.toString();
        java.lang.String o_testReadmeSerial_add2330__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2330__14);
        sw.toString();
        java.lang.String String_43 = "Should be a little bit more than 4 seconds: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_43);
        boolean boolean_44 = (diff > 3999) && (diff < 6000);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2330__14);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_43);
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0null3384_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("C<F7cF@g!PR");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                java.lang.String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0null3384 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0_literalMutationString2792_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("C<F7cF@g!PR");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_17 = "" + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0_literalMutationString2792 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2313() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_literalMutationString2313__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadmeSerial_literalMutationString2313__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadmeSerial_literalMutationString2313__9)).toString());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_literalMutationString2313__13 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString2313__13);
        sw.toString();
        java.lang.String String_5 = "Should be a little bit more than 4 seconds: " + diff;
        boolean boolean_6 = (diff > 3999) && (diff < 6000);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadmeSerial_literalMutationString2313__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadmeSerial_literalMutationString2313__9)).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString2313__13);
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0_add3260_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                c.compile("C<F7cF@g!PR");
                com.github.mustachejava.Mustache m = c.compile("C<F7cF@g!PR");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0_add3260 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0null3383_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("C<F7cF@g!PR");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(null, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0null3383 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0_literalMutationString2785_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("C<E7cF@g!PR");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0_literalMutationString2785 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template C<E7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerialnull2333_failAssert0_literalMutationString3011_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("!W)bfl&TZ>P");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(null, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_33 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_34 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerialnull2333 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmeSerialnull2333_failAssert0_literalMutationString3011 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template !W)bfl&TZ>P not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add2328_remove3338() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add2328__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_add2328__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2328__14);
        sw.toString();
        java.lang.String String_45 = "Should be a little bit more than 4 seconds: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_45);
        boolean boolean_46 = (diff > 3999) && (diff < 6000);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2328__14);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_45);
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            com.github.mustachejava.Mustache m = c.compile("C<F7cF@g!PR");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_17 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_18 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0_add3266_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("C<F7cF@g!PR");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0_add3266 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0_add3265_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("C<F7cF@g!PR");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                sw.toString();
                java.lang.String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0_add3265 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2318_failAssert0_literalMutationString2636_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("item%s2.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                java.lang.String String_7 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_8 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2318 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2318_failAssert0_literalMutationString2636 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template item%s2.html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2313_add3045() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_literalMutationString2313_add3045__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadmeSerial_literalMutationString2313_add3045__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadmeSerial_literalMutationString2313_add3045__9)).toString());
        java.io.Writer o_testReadmeSerial_literalMutationString2313__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_literalMutationString2313__13 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString2313__13);
        sw.toString();
        java.lang.String String_5 = "Should be a little bit more than 4 seconds: " + diff;
        boolean boolean_6 = (diff > 3999) && (diff < 6000);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadmeSerial_literalMutationString2313_add3045__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadmeSerial_literalMutationString2313_add3045__9)).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString2313__13);
    }

    public void testReadmeSerial_add2324_literalMutationNumber2591() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory o_testReadmeSerial_add2324__1 = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeSerial_add2324__1)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeSerial_add2324__1)).getRecursionLimit())));
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add2324__10 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_add2324__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2324__14);
        sw.toString();
        java.lang.String String_47 = "Should be a little bit more than 4 seconds: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_47);
        boolean boolean_48 = (diff > 0) && (diff < 6000);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeSerial_add2324__1)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeSerial_add2324__1)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2324__14);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_47);
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0_literalMutationString2787_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = createMustacheFactory();
                com.github.mustachejava.Mustache m = c.compile("C<F7cF@g!PR");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "=$JI<B[&L");
                sw.toString();
                java.lang.String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0_literalMutationString2787 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0null125924_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("yd3<&,9SXV<");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(null, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_141 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_142 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834_failAssert0null125924 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124842_failAssert0_literalMutationString125248_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("_tb&b3^._T!");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, ">tems.txt");
                sw.toString();
                java.lang.String String_127 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_128 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124842 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124842_failAssert0_literalMutationString125248 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template _tb&b3^._T! not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0_literalMutationNumber125417_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("yd3<&,9SXV<");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_141 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_142 = (diff > 499) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834_failAssert0_literalMutationNumber125417 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add124847_literalMutationString125004_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("{cXl3dA0]>U");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
            long diff = (java.lang.System.currentTimeMillis()) - start;
            java.lang.String o_testReadmeParallel_add124847__16 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_151 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_152 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_add124847_literalMutationString125004 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template {cXl3dA0]>U not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add124848_remove125871() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
        java.lang.System.currentTimeMillis();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add124848__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add124848__15);
        java.lang.String String_163 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_163);
        boolean boolean_164 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add124848__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_163);
    }

    public void testReadmeParallel_add124847_remove125860() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add124847__16 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add124847__16);
        sw.toString();
        java.lang.String String_151 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1", String_151);
        boolean boolean_152 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add124847__16);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1", String_151);
    }

    public void testReadmeParallel_add124843_literalMutationString125065_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory o_testReadmeParallel_add124843__1 = initParallel();
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("}BrIzy)kMG^");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
            long diff = (java.lang.System.currentTimeMillis()) - start;
            java.lang.String o_testReadmeParallel_add124843__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_155 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_156 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_add124843_literalMutationString125065 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template }BrIzy)kMG^ not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory c = initParallel();
            com.github.mustachejava.Mustache m = c.compile("yd3<&,9SXV<");
            java.io.StringWriter sw = new java.io.StringWriter();
            long start = java.lang.System.currentTimeMillis();
            m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
            long diff = (java.lang.System.currentTimeMillis()) - start;
            com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
            sw.toString();
            java.lang.String String_141 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_142 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0_add125815_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("yd3<&,9SXV<");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                sw.toString();
                java.lang.String String_141 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_142 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834_failAssert0_add125815 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0_literalMutationString125401_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("yd3<&,9SXV<");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                java.lang.String String_141 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_142 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834_failAssert0_literalMutationString125401 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add124846_literalMutationNumber125053() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add124846__17 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add124846__17);
        sw.toString();
        java.lang.String String_153 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 2001", String_153);
        boolean boolean_154 = (diff > 998) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add124846__17);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 2001", String_153);
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0_add125812_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("yd3<&,9SXV<");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
                sw.toString();
                java.lang.String String_141 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_142 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834_failAssert0_add125812 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0null125925_failAssert0() throws com.github.mustachejava.MustacheException, java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory c = initParallel();
                com.github.mustachejava.Mustache m = c.compile("yd3<&,9SXV<");
                java.io.StringWriter sw = new java.io.StringWriter();
                long start = java.lang.System.currentTimeMillis();
                m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
                long diff = (java.lang.System.currentTimeMillis()) - start;
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                java.lang.String String_141 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_142 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834_failAssert0null125925 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45728_add46426() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DeferringMustacheFactory) (mf)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DeferringMustacheFactory) (mf)).getRecursionLimit())));
        mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
        java.lang.Object context = new java.lang.Object() {
            java.lang.String title = "Deferred";

            java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

            java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
        };
        com.github.mustachejava.Mustache m = mf.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testDeferred_literalMutationString45728_add46426__15 = m.execute(sw, context);
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testDeferred_literalMutationString45728_add46426__15)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testDeferred_literalMutationString45728_add46426__15)).toString());
        m.execute(sw, context).close();
        java.lang.String o_testDeferred_literalMutationString45728__17 = com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
        junit.framework.TestCase.assertEquals("<html>\n<head><title>Deferred</title></head>\n<body>\n<div id=\"1\"></div>\n<script>document.getElementById(\"1\").innerHTML=\"I am calculated\\n\\\"later\\\" and divs\\nare written out &lt;\\nnow\";</script>\n</body>\n</html>", o_testDeferred_literalMutationString45728__17);
        sw.toString();
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DeferringMustacheFactory) (mf)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DeferringMustacheFactory) (mf)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DeferringMustacheFactory) (mf)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testDeferred_literalMutationString45728_add46426__15)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testDeferred_literalMutationString45728_add46426__15)).toString());
        junit.framework.TestCase.assertEquals("<html>\n<head><title>Deferred</title></head>\n<body>\n<div id=\"1\"></div>\n<script>document.getElementById(\"1\").innerHTML=\"I am calculated\\n\\\"later\\\" and divs\\nare written out &lt;\\nnow\";</script>\n</body>\n</html>", o_testDeferred_literalMutationString45728__17);
    }

    public void testDeferred_literalMutationString45733_failAssert0_literalMutationString46190_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile("<3q|gP.t:H>B&");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45733 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45733_failAssert0_literalMutationString46190 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template <3q|gP.t:H>B& not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45733_failAssert0_literalMutationString46192_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile("<3q|gP.t:H>B&");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45733 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45733_failAssert0_literalMutationString46192 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template <3q|gP.t:H>B& not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45733_failAssert0null46972_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile("<3q|gP.t:H>B&");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45733 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45733_failAssert0null46972 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template <3q|gP.t:H>B& not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45739_failAssert0_literalMutationString46205_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile("defe<red.html");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "f&XAe@?+)F](");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45739 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45739_failAssert0_literalMutationString46205 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template defe<red.html not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45733_failAssert0_add46729_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile("<3q|gP.t:H>B&");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45733 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45733_failAssert0_add46729 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template <3q|gP.t:H>B& not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45726_literalMutationString45893_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
            mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            java.lang.Object context = new java.lang.Object() {
                java.lang.String title = "Q^5_02r,";

                java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
            };
            com.github.mustachejava.Mustache m = mf.compile("PV}wPt4DA=r#]");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, context).close();
            java.lang.String o_testDeferred_literalMutationString45726__17 = com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString45726_literalMutationString45893 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template PV}wPt4DA=r#] not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45733_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
            mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
            java.lang.Object context = new java.lang.Object() {
                java.lang.String title = "Deferred";

                java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
            };
            com.github.mustachejava.Mustache m = mf.compile("<3q|gP.t:H>B&");
            java.io.StringWriter sw = new java.io.StringWriter();
            m.execute(sw, context).close();
            com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString45733 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template <3q|gP.t:H>B& not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45733_failAssert0_add46733_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile("<3q|gP.t:H>B&");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context);
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45733 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45733_failAssert0_add46733 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template <3q|gP.t:H>B& not found", expected.getMessage());
        }
    }

    public void testDeferrednull45753_failAssert0_literalMutationString46363_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile(" 8Pv/2 TxR{B[");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, null);
                sw.toString();
                junit.framework.TestCase.fail("testDeferrednull45753 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDeferrednull45753_failAssert0_literalMutationString46363 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  8Pv/2 TxR{B[ not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45737_failAssert0_literalMutationString46242_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DeferringMustacheFactory(this.root);
                mf.setExecutorService(java.util.concurrent.Executors.newCachedThreadPool());
                java.lang.Object context = new java.lang.Object() {
                    java.lang.String title = "Deferred";

                    java.lang.Object deferred = new com.github.mustachejava.DeferringMustacheFactory.DeferredCallable();

                    java.lang.Object deferredpartial = com.github.mustachejava.DeferringMustacheFactory.DEFERRED;
                };
                com.github.mustachejava.Mustache m = mf.compile("d4flfrn:H<Uq4");
                java.io.StringWriter sw = new java.io.StringWriter();
                m.execute(sw, context).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "deferr^d.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45737 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45737_failAssert0_literalMutationString46242 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template d4flfrn:H<Uq4 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0null112876_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0null112876 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112154_failAssert0_literalMutationString112410_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("relative/functi[onaths.html");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112154 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112154_failAssert0_literalMutationString112410 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template relative/functi[onaths.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112154_failAssert0_literalMutationString112412_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("bp.7X d[(2*s#S7]&DM<sre0gz");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112154 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112154_failAssert0_literalMutationString112412 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template bp.7X d[(2*s#S7]&DM<sre0gz not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_add112743_failAssert0() throws java.io.IOException {
        try {
            {
                createMustacheFactory();
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_add112743 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112152_failAssert0_literalMutationString112386_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile(" does not exist");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112152 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112152_failAssert0_literalMutationString112386 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_add112746_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
                java.io.StringWriter sw = new java.io.StringWriter();
                compile.execute(sw, new java.lang.Object() {
                    java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                        @java.lang.Override
                        public java.lang.String apply(java.lang.String s) {
                            return s;
                        }
                    };
                });
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_add112746 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_add112747_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_add112747 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_add112163_literalMutationString112250_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
            com.github.mustachejava.Mustache o_testRelativePathsTemplateFunction_add112163__3 = mf.compile("relative/functionpaths.html");
            com.github.mustachejava.Mustache compile = mf.compile("re ative/functionpaths.html");
            java.io.StringWriter sw = new java.io.StringWriter();
            compile.execute(sw, new java.lang.Object() {
                java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                    @java.lang.Override
                    public java.lang.String apply(java.lang.String s) {
                        return s;
                    }
                };
            }).close();
            java.lang.String o_testRelativePathsTemplateFunction_add112163__20 = com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_add112163_literalMutationString112250 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template re ative/functionpaths.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_add112162_literalMutationString112261_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.DefaultMustacheFactory o_testRelativePathsTemplateFunction_add112162__1 = createMustacheFactory();
            com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
            com.github.mustachejava.Mustache compile = mf.compile(">[ZtF5f9wKrK=eUyE}{vA$0fAQD");
            java.io.StringWriter sw = new java.io.StringWriter();
            compile.execute(sw, new java.lang.Object() {
                java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                    @java.lang.Override
                    public java.lang.String apply(java.lang.String s) {
                        return s;
                    }
                };
            }).close();
            java.lang.String o_testRelativePathsTemplateFunction_add112162__20 = com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_add112162_literalMutationString112261 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template >[ZtF5f9wKrK=eUyE}{vA$0fAQD not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_literalMutationString112486_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
                java.io.StringWriter sw = new java.io.StringWriter();
                compile.execute(sw, new java.lang.Object() {
                    java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                        @java.lang.Override
                        public java.lang.String apply(java.lang.String s) {
                            return s;
                        }
                    };
                }).close();
                com.github.mustachejava.TestUtil.getContents(this.root, "relative/pbths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_literalMutationString112486 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0() throws java.io.IOException {
        try {
            com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
            com.github.mustachejava.Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
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
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0null112877_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0null112877 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_literalMutationString112479_failAssert0() throws java.io.IOException {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache compile = mf.compile("&lz9n%)E*x#&u3 wU -OE&6_UDH");
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
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_literalMutationString112479 should have thrown MustacheNotFoundException");
        } catch (com.github.mustachejava.MustacheNotFoundException expected) {
            junit.framework.TestCase.assertEquals("Template &lz9n%)E*x#&u3 wU -OE&6_UDH not found", expected.getMessage());
        }
    }
}

