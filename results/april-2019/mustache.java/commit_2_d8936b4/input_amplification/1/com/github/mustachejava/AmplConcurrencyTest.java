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

    public void testSimple_literalMutationString4674() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testSimple_literalMutationString4674__7 = m.execute(sw, new java.lang.Object() {
            java.lang.String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimple_literalMutationString4674__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimple_literalMutationString4674__7)).toString());
        java.lang.String o_testSimple_literalMutationString4674__14 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString4674__14);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimple_literalMutationString4674__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimple_literalMutationString4674__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString4674__14);
    }

    public void testSimple_literalMutationString1() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testSimple_literalMutationString1__7 = m.execute(sw, new java.lang.Object() {
            java.lang.String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimple_literalMutationString1__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimple_literalMutationString1__7)).toString());
        java.lang.String o_testSimple_literalMutationString1__14 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString1__14);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimple_literalMutationString1__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimple_literalMutationString1__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString1__14);
    }

    public void testSimpleI18N_literalMutationString1746() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("simple.html");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimpleI18N_literalMutationString1746__9 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1746__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1746__9)).toString());
            java.lang.String o_testSimpleI18N_literalMutationString1746__16 = com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString1746__16);
            sw.toString();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1746__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1746__9)).toString());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString1746__16);
        }
        {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimpleI18N_literalMutationString1746__26 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1746__26)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1746__26)).toString());
            java.lang.String o_testSimpleI18N_literalMutationString1746__33 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString1746__33);
            sw.toString();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1746__26)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1746__26)).toString());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString1746__33);
        }
    }

    public void testSimpleI18N_literalMutationString1714() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimpleI18N_literalMutationString1714__9 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1714__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1714__9)).toString());
            java.lang.String o_testSimpleI18N_literalMutationString1714__16 = com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString1714__16);
            sw.toString();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1714__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1714__9)).toString());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString1714__16);
        }
        {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("simple.html");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimpleI18N_literalMutationString1714__26 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1714__26)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1714__26)).toString());
            java.lang.String o_testSimpleI18N_literalMutationString1714__33 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString1714__33);
            sw.toString();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1714__26)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString1714__26)).toString());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString1714__33);
        }
    }

    public void testSimplePragma_literalMutationString5407() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testSimplePragma_literalMutationString5407__7 = m.execute(sw, new java.lang.Object() {
            java.lang.String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimplePragma_literalMutationString5407__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimplePragma_literalMutationString5407__7)).toString());
        java.lang.String o_testSimplePragma_literalMutationString5407__14 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString5407__14);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimplePragma_literalMutationString5407__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimplePragma_literalMutationString5407__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString5407__14);
    }

    public void testMultipleWrappers_literalMutationString6801() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testMultipleWrappers_literalMutationString6801__7 = m.execute(sw, new java.lang.Object() {
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
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testMultipleWrappers_literalMutationString6801__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testMultipleWrappers_literalMutationString6801__7)).toString());
        java.lang.String o_testMultipleWrappers_literalMutationString6801__23 = com.github.mustachejava.TestUtil.getContents(this.root, "simplerewrap.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString6801__23);
        sw.toString();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testMultipleWrappers_literalMutationString6801__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testMultipleWrappers_literalMutationString6801__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString6801__23);
    }

    public void testSecurity_literalMutationString5737() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testSecurity_literalMutationString5737__7 = m.execute(sw, new java.lang.Object() {
            java.lang.String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;

            private java.lang.String test = "Test";
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSecurity_literalMutationString5737__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSecurity_literalMutationString5737__7)).toString());
        java.lang.String o_testSecurity_literalMutationString5737__15 = com.github.mustachejava.TestUtil.getContents(this.root, "security.txt");
        junit.framework.TestCase.assertEquals("", o_testSecurity_literalMutationString5737__15);
        sw.toString();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSecurity_literalMutationString5737__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSecurity_literalMutationString5737__7)).toString());
        junit.framework.TestCase.assertEquals("", o_testSecurity_literalMutationString5737__15);
    }

    public void testIdentitySimple_literalMutationString8136() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        m.identity(sw);
        java.lang.String o_testIdentitySimple_literalMutationString8136__8 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.html").replaceAll("\\s+", "");
        junit.framework.TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString8136__8);
        java.lang.String o_testIdentitySimple_literalMutationString8136__10 = sw.toString().replaceAll("\\s+", "");
        junit.framework.TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString8136__10);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString8136__8);
    }

    public void testProperties_literalMutationString6643() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testProperties_literalMutationString6643__7 = m.execute(sw, new java.lang.Object() {
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
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testProperties_literalMutationString6643__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testProperties_literalMutationString6643__7)).toString());
        java.lang.String o_testProperties_literalMutationString6643__22 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString6643__22);
        sw.toString();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testProperties_literalMutationString6643__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testProperties_literalMutationString6643__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString6643__22);
    }

    public void testPartialWithTF_literalMutationString4956() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testPartialWithTF_literalMutationString4956__7 = m.execute(sw, new java.lang.Object() {
            public com.github.mustachejava.TemplateFunction i() {
                return ( s) -> s;
            }
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialWithTF_literalMutationString4956__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialWithTF_literalMutationString4956__7)).toString());
        sw.toString();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialWithTF_literalMutationString4956__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialWithTF_literalMutationString4956__7)).toString());
    }

    public void testReadme_literalMutationString1068() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadme_literalMutationString1068__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadme_literalMutationString1068__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadme_literalMutationString1068__9)).toString());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadme_literalMutationString1068__13 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString1068__13);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadme_literalMutationString1068__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadme_literalMutationString1068__9)).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString1068__13);
    }

    public void testReadmeSerial_literalMutationString292() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_literalMutationString292__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadmeSerial_literalMutationString292__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadmeSerial_literalMutationString292__9)).toString());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_literalMutationString292__13 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString292__13);
        sw.toString();
        java.lang.String String_20 = "Should be a little bit more than 4 seconds: " + diff;
        boolean boolean_21 = (diff > 3999) && (diff < 6000);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadmeSerial_literalMutationString292__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadmeSerial_literalMutationString292__9)).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString292__13);
    }

    public void testReadmeSerial_add305() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.lang.System.currentTimeMillis();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add305__10 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadmeSerial_add305__10)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((java.io.StringWriter) (o_testReadmeSerial_add305__10)).toString());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_add305__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add305__14);
        sw.toString();
        java.lang.String String_38 = "Should be a little bit more than 4 seconds: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4003", String_38);
        boolean boolean_39 = (diff > 3999) && (diff < 6000);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadmeSerial_add305__10)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", ((java.io.StringWriter) (o_testReadmeSerial_add305__10)).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add305__14);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4003", String_38);
    }

    public void testReadmeParallel_add7805() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
        java.lang.String o_testReadmeParallel_add7805__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add7805__14);
        java.lang.String o_testReadmeParallel_add7805__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add7805__15);
        sw.toString();
        java.lang.String String_189 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_189);
        boolean boolean_190 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add7805__14);
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add7805__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_189);
    }

    public void testReadmeParallel_add7806() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
        java.lang.String o_testReadmeParallel_add7806__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add7806__15);
        sw.toString();
        java.lang.String String_191 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_191);
        boolean boolean_192 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add7806__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_191);
    }

    public void testInvalidDelimiters_add5144_failAssert0() throws java.lang.Exception {
        try {
            {
                createMustacheFactory();
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add5144 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull5146_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull5146 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5140_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5140 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5142_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "tBstInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5142 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[tBstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5143_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "txestInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5143 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[txestInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5141_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "(KJ! U$ITqI`N>U&&o3V,");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5141 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[(KJ! U$ITqI`N>U&&o3V,:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5136_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolo;ng}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5136 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolo;ng @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5135_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=;oolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5135 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =;oolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5138_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5138 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5134_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5134 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString5139_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimites");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString5139 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimites:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add5145_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add5145 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString3965_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=## ##=}}{{##={{ }}=#r##";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            mustache.execute(sw, new java.lang.Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString3965 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: ={{}}=#r @[test:1]", expected.getMessage());
        }
    }
}

