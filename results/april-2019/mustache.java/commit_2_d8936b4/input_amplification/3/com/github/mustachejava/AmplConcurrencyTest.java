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

    public void testSimple_literalMutationString2() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testSimple_literalMutationString2__7 = m.execute(sw, new java.lang.Object() {
            java.lang.String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimple_literalMutationString2__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimple_literalMutationString2__7)).toString());
        java.lang.String o_testSimple_literalMutationString2__14 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString2__14);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimple_literalMutationString2__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimple_literalMutationString2__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString2__14);
    }

    public void testSimple_literalMutationString366968() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testSimple_literalMutationString366968__7 = m.execute(sw, new java.lang.Object() {
            java.lang.String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimple_literalMutationString366968__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimple_literalMutationString366968__7)).toString());
        java.lang.String o_testSimple_literalMutationString366968__14 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString366968__14);
        sw.toString();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimple_literalMutationString366968__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimple_literalMutationString366968__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString366968__14);
    }

    public void testSimpleI18N_literalMutationString135866() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("simple.html");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimpleI18N_literalMutationString135866__9 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135866__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135866__9)).toString());
            java.lang.String o_testSimpleI18N_literalMutationString135866__16 = com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString135866__16);
            sw.toString();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135866__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135866__9)).toString());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString135866__16);
        }
        {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimpleI18N_literalMutationString135866__26 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135866__26)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135866__26)).toString());
            java.lang.String o_testSimpleI18N_literalMutationString135866__33 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString135866__33);
            sw.toString();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135866__26)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135866__26)).toString());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString135866__33);
        }
    }

    public void testSimpleI18N_literalMutationString135835() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.KOREAN));
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimpleI18N_literalMutationString135835__9 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135835__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135835__9)).toString());
            java.lang.String o_testSimpleI18N_literalMutationString135835__16 = com.github.mustachejava.TestUtil.getContents(this.root, "simple_ko.txt");
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString135835__16);
            sw.toString();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135835__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135835__9)).toString());
            junit.framework.TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString135835__16);
        }
        {
            com.github.mustachejava.MustacheFactory c = new com.github.mustachejava.DefaultMustacheFactory(new com.github.mustachejava.AmplInterpreterTest.LocalizedMustacheResolver(this.root, java.util.Locale.JAPANESE));
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("simple.html");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testSimpleI18N_literalMutationString135835__26 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135835__26)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135835__26)).toString());
            java.lang.String o_testSimpleI18N_literalMutationString135835__33 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString135835__33);
            sw.toString();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135835__26)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((java.io.StringWriter) (o_testSimpleI18N_literalMutationString135835__26)).toString());
            junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString135835__33);
        }
    }

    public void testRecurision_literalMutationString383416_add383738() throws java.io.IOException {
        java.io.StringWriter o_testRecurision_literalMutationString383416_add383738__1 = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecurision_literalMutationString383416_add383738__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecurision_literalMutationString383416_add383738__1)).toString());
        java.io.StringWriter sw = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        java.lang.String o_testRecurision_literalMutationString383416__11 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString383416__11);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecurision_literalMutationString383416_add383738__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecurision_literalMutationString383416_add383738__1)).toString());
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString383416__11);
    }

    public void testRecurision_literalMutationString383416_add383740_add387001() throws java.io.IOException {
        java.io.StringWriter o_testRecurision_literalMutationString383416_add383740_add387001__1 = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecurision_literalMutationString383416_add383740_add387001__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecurision_literalMutationString383416_add383740_add387001__1)).toString());
        java.io.StringWriter sw = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        java.lang.String o_testRecurision_literalMutationString383416__11 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString383416__11);
        sw.toString();
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecurision_literalMutationString383416_add383740_add387001__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecurision_literalMutationString383416_add383740_add387001__1)).toString());
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString383416__11);
    }

    public void testRecursionWithInheritance_literalMutationString651376_add651697() throws java.io.IOException {
        java.io.StringWriter o_testRecursionWithInheritance_literalMutationString651376_add651697__1 = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651697__1)).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651697__1)).getBuffer())).toString());
        java.io.StringWriter sw = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        java.lang.String o_testRecursionWithInheritance_literalMutationString651376__11 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString651376__11);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651697__1)).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651697__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString651376__11);
    }

    public void testRecursionWithInheritance_literalMutationString651376_add651699_add654855() throws java.io.IOException {
        java.io.StringWriter o_testRecursionWithInheritance_literalMutationString651376_add651699_add654855__1 = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651699_add654855__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651699_add654855__1)).toString());
        java.io.StringWriter sw = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        java.lang.String o_testRecursionWithInheritance_literalMutationString651376__11 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString651376__11);
        sw.toString();
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651699_add654855__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651699_add654855__1)).toString());
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString651376__11);
    }

    public void testRecursionWithInheritance_literalMutationString651376_remove651782_add654990() throws java.io.IOException {
        java.io.StringWriter o_testRecursionWithInheritance_literalMutationString651376_remove651782_add654990__1 = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_remove651782_add654990__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_remove651782_add654990__1)).toString());
        java.io.StringWriter sw = execute("", new java.lang.Object() {
            java.lang.Object value = new java.lang.Object() {
                boolean value = false;
            };
        });
        java.lang.String o_testRecursionWithInheritance_literalMutationString651376__11 = com.github.mustachejava.TestUtil.getContents(this.root, "recursion.txt");
        junit.framework.TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString651376__11);
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_remove651782_add654990__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_remove651782_add654990__1)).toString());
    }

    public void testPartialRecursionWithInheritance_literalMutationString378072_add378393() throws java.io.IOException {
        java.io.StringWriter o_testPartialRecursionWithInheritance_literalMutationString378072_add378393__1 = execute("", new java.lang.Object() {
            java.lang.Object test = new java.lang.Object() {
                boolean test = false;
            };
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString378072_add378393__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString378072_add378393__1)).toString());
        java.io.StringWriter sw = execute("", new java.lang.Object() {
            java.lang.Object test = new java.lang.Object() {
                boolean test = false;
            };
        });
        java.lang.String o_testPartialRecursionWithInheritance_literalMutationString378072__11 = com.github.mustachejava.TestUtil.getContents(this.root, "recursive_partial_inheritance.txt");
        junit.framework.TestCase.assertEquals("TEST\n  TEST\n\n\n", o_testPartialRecursionWithInheritance_literalMutationString378072__11);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString378072_add378393__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString378072_add378393__1)).toString());
        junit.framework.TestCase.assertEquals("TEST\n  TEST\n\n\n", o_testPartialRecursionWithInheritance_literalMutationString378072__11);
    }

    public void testChainedInheritance_literalMutationString568044_add568365() throws java.io.IOException {
        java.io.StringWriter o_testChainedInheritance_literalMutationString568044_add568365__1 = execute("", new java.lang.Object() {
            java.lang.Object test = new java.lang.Object() {
                boolean test = false;
            };
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testChainedInheritance_literalMutationString568044_add568365__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testChainedInheritance_literalMutationString568044_add568365__1)).toString());
        java.io.StringWriter sw = execute("", new java.lang.Object() {
            java.lang.Object test = new java.lang.Object() {
                boolean test = false;
            };
        });
        java.lang.String o_testChainedInheritance_literalMutationString568044__11 = com.github.mustachejava.TestUtil.getContents(this.root, "page.txt");
        junit.framework.TestCase.assertEquals("<main id=\"content\" role=\"main\">\n  This is the page content\n\n</main>\n", o_testChainedInheritance_literalMutationString568044__11);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testChainedInheritance_literalMutationString568044_add568365__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testChainedInheritance_literalMutationString568044_add568365__1)).toString());
        junit.framework.TestCase.assertEquals("<main id=\"content\" role=\"main\">\n  This is the page content\n\n</main>\n", o_testChainedInheritance_literalMutationString568044__11);
    }

    public void testSimplePragma_literalMutationString421691() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testSimplePragma_literalMutationString421691__7 = m.execute(sw, new java.lang.Object() {
            java.lang.String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimplePragma_literalMutationString421691__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimplePragma_literalMutationString421691__7)).toString());
        java.lang.String o_testSimplePragma_literalMutationString421691__14 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString421691__14);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimplePragma_literalMutationString421691__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimplePragma_literalMutationString421691__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString421691__14);
    }

    public void testMultipleWrappers_literalMutationString513621() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testMultipleWrappers_literalMutationString513621__7 = m.execute(sw, new java.lang.Object() {
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
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testMultipleWrappers_literalMutationString513621__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testMultipleWrappers_literalMutationString513621__7)).toString());
        java.lang.String o_testMultipleWrappers_literalMutationString513621__23 = com.github.mustachejava.TestUtil.getContents(this.root, "simplerewrap.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString513621__23);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testMultipleWrappers_literalMutationString513621__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testMultipleWrappers_literalMutationString513621__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString513621__23);
    }

    public void testBrokenSimple_literalMutationNumber357668_literalMutationString358352_literalMutationString359011() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "ChFis";

                int value = 10001;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_249 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_249);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357664_literalMutationString357957_remove366200() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357664_literalMutationString357957__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chr is";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_278 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_278);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357823_remove366196() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357823__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 5000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_258 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_258);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357819_literalMutationString359176() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chrs";

                int value = 0;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_314 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_314);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357819() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 0;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8)).toString());
            java.lang.String String_314 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_314);
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationBoolean357678_literalMutationString357907() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = false;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8)).toString());
            java.lang.String String_315 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_315);
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationNumber357668_literalMutationString358352() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10001;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8)).toString());
            java.lang.String String_249 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_249);
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationString357816_remove366194() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357654_literalMutationString357816__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Z?C+*";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_190 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_190);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationString357813_remove366206() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357654_literalMutationString357813__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_345 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_345);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationNumber357668_literalMutationString358352_add365025() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.DefaultMustacheFactory o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352_add365025__2 = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352_add365025__2)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352_add365025__2)).getRecursionLimit())));
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10001;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_249 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_249);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352_add365025__2)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352_add365025__2)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357823() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357823__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 5000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357823__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357823__8)).toString());
            java.lang.String String_258 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_258);
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357823__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357823__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357819_add365155() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 0;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_314 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_314);
            o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8.toString();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_314);
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357828() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357828__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.0)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357828__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357828__8)).toString());
            java.lang.String String_259 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_259);
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357828__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357828__8)).toString());
        }
    }

    public void testBrokenSimple_add357679_literalMutationString358477_remove366199() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.DefaultMustacheFactory o_testBrokenSimple_add357679_literalMutationString358477__2 = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getRecursionLimit())));
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_add357679_literalMutationString358477__9 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_265 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_265);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357659_literalMutationString357982() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357659_literalMutationString357982__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357659_literalMutationString357982__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357659_literalMutationString357982__8)).toString());
            java.lang.String String_262 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_262);
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357659_literalMutationString357982__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357659_literalMutationString357982__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationNumber357674_literalMutationString358303() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationNumber357674_literalMutationString358303__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.0)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationNumber357674_literalMutationString358303__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationNumber357674_literalMutationString358303__8)).toString());
            java.lang.String String_327 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_327);
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationNumber357674_literalMutationString358303__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationNumber357674_literalMutationString358303__8)).toString());
        }
    }

    public void testBrokenSimple_add357679_literalMutationString358477() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.DefaultMustacheFactory o_testBrokenSimple_add357679_literalMutationString358477__2 = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getExecutorService());
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_add357679_literalMutationString358477__9 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_add357679_literalMutationString358477__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_add357679_literalMutationString358477__9)).toString());
            java.lang.String String_265 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_265);
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_add357679_literalMutationString358477__9)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_add357679_literalMutationString358477__9)).toString());
        }
    }

    public void testBrokenSimple_add357682_literalMutationString358451_remove366204() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_add357682_literalMutationString358451__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            sw.toString();
            java.lang.String String_330 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_330);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationString357813() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357654_literalMutationString357813__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357813__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357813__8)).toString());
            java.lang.String String_345 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_345);
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357813__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357813__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationString357816() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357654_literalMutationString357816__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Z?C+*";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357816__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357816__8)).toString());
            java.lang.String String_190 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_190);
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357816__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357816__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationBoolean357678_literalMutationString357907_remove366202() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = false;
            });
            java.lang.String String_315 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_315);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357659_literalMutationString357982_remove366198() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357659_literalMutationString357982__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_262 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_262);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357828_remove366197() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357828__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.0)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_259 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_259);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357819_remove366201() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 0;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_314 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_314);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationBoolean357678_literalMutationString357907_literalMutationNumber359210() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 5000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = false;
            });
            java.lang.String String_315 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_315);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationNumber357668_literalMutationString358352_remove366195() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10001;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_249 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_249);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationBoolean357678_literalMutationString357907_add365165() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = false;
            });
            o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8.toString();
            java.lang.String String_315 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_315);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_add357682_literalMutationString358451() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_add357682_literalMutationString358451__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_add357682_literalMutationString358451__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_add357682_literalMutationString358451__8)).toString());
            sw.toString();
            java.lang.String String_330 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_330);
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_add357682_literalMutationString358451__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_add357682_literalMutationString358451__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString357664_literalMutationString357957() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationString357664_literalMutationString357957__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chr is";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357664_literalMutationString357957__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357664_literalMutationString357957__8)).toString());
            java.lang.String String_278 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_278);
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357664_literalMutationString357957__8)).getBuffer())).toString());
            junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testBrokenSimple_literalMutationString357664_literalMutationString357957__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationNumber357674_literalMutationString358303_remove366203() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        {
            com.github.mustachejava.MustacheFactory c = createMustacheFactory();
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            com.github.mustachejava.Mustache m = c.compile("");
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testBrokenSimple_literalMutationNumber357674_literalMutationString358303__8 = m.execute(sw, new java.lang.Object() {
                java.lang.String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.0)));
                }

                boolean in_ca = true;
            });
            java.lang.String String_327 = "Should have failed: " + (sw.toString());
            junit.framework.TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_327);
            junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
            junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
            junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
            junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        }
    }

    public void testIsNotEmpty_literalMutationString174270_remove174986_add179436() throws java.io.IOException {
        java.lang.Object object = new java.lang.Object() {
            java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
        };
        java.io.StringWriter o_testIsNotEmpty_literalMutationString174270_remove174986_add179436__7 = execute("", object);
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testIsNotEmpty_literalMutationString174270_remove174986_add179436__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testIsNotEmpty_literalMutationString174270_remove174986_add179436__7)).toString());
        java.io.StringWriter sw = execute("", object);
        java.lang.String o_testIsNotEmpty_literalMutationString174270__9 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
        junit.framework.TestCase.assertEquals("Is not empty\n", o_testIsNotEmpty_literalMutationString174270__9);
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testIsNotEmpty_literalMutationString174270_remove174986_add179436__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testIsNotEmpty_literalMutationString174270_remove174986_add179436__7)).toString());
    }

    public void testImmutableList_literalMutationString280187_add280823() throws java.io.IOException {
        java.lang.Object object = new java.lang.Object() {
            java.util.List<java.lang.String> people = java.util.Collections.singletonList("Test");
        };
        java.io.StringWriter o_testImmutableList_literalMutationString280187_add280823__7 = execute("", java.util.Collections.singletonList(object));
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testImmutableList_literalMutationString280187_add280823__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testImmutableList_literalMutationString280187_add280823__7)).toString());
        java.io.StringWriter sw = execute("", java.util.Collections.singletonList(object));
        java.lang.String o_testImmutableList_literalMutationString280187__10 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
        junit.framework.TestCase.assertEquals("Is not empty\n", o_testImmutableList_literalMutationString280187__10);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testImmutableList_literalMutationString280187_add280823__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testImmutableList_literalMutationString280187_add280823__7)).toString());
        junit.framework.TestCase.assertEquals("Is not empty\n", o_testImmutableList_literalMutationString280187__10);
    }

    public void testImmutableList_literalMutationString280187_literalMutationString280399_add285347() throws java.io.IOException {
        java.lang.Object object = new java.lang.Object() {
            java.util.List<java.lang.String> people = java.util.Collections.singletonList("Tst");
        };
        java.io.StringWriter o_testImmutableList_literalMutationString280187_literalMutationString280399_add285347__7 = execute("", java.util.Collections.singletonList(object));
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testImmutableList_literalMutationString280187_literalMutationString280399_add285347__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testImmutableList_literalMutationString280187_literalMutationString280399_add285347__7)).toString());
        java.io.StringWriter sw = execute("", java.util.Collections.singletonList(object));
        java.lang.String o_testImmutableList_literalMutationString280187__10 = com.github.mustachejava.TestUtil.getContents(this.root, "isempty.txt");
        junit.framework.TestCase.assertEquals("Is not empty\n", o_testImmutableList_literalMutationString280187__10);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testImmutableList_literalMutationString280187_literalMutationString280399_add285347__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testImmutableList_literalMutationString280187_literalMutationString280399_add285347__7)).toString());
        junit.framework.TestCase.assertEquals("Is not empty\n", o_testImmutableList_literalMutationString280187__10);
    }

    public void testSecurity_literalMutationString447137() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testSecurity_literalMutationString447137__7 = m.execute(sw, new java.lang.Object() {
            java.lang.String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;

            private java.lang.String test = "Test";
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSecurity_literalMutationString447137__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSecurity_literalMutationString447137__7)).toString());
        java.lang.String o_testSecurity_literalMutationString447137__15 = com.github.mustachejava.TestUtil.getContents(this.root, "security.txt");
        junit.framework.TestCase.assertEquals("", o_testSecurity_literalMutationString447137__15);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSecurity_literalMutationString447137__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSecurity_literalMutationString447137__7)).toString());
        junit.framework.TestCase.assertEquals("", o_testSecurity_literalMutationString447137__15);
    }

    public void testIdentitySimple_literalMutationString603279_add604862() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache o_testIdentitySimple_literalMutationString603279_add604862__3 = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (o_testIdentitySimple_literalMutationString603279_add604862__3)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (o_testIdentitySimple_literalMutationString603279_add604862__3)).getName());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        m.identity(sw);
        java.lang.String o_testIdentitySimple_literalMutationString603279__8 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.html").replaceAll("\\s+", "");
        junit.framework.TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603279__8);
        java.lang.String o_testIdentitySimple_literalMutationString603279__10 = sw.toString().replaceAll("\\s+", "");
        junit.framework.TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString603279__10);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (o_testIdentitySimple_literalMutationString603279_add604862__3)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (o_testIdentitySimple_literalMutationString603279_add604862__3)).getName());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603279__8);
    }

    public void testIdentitySimple_literalMutationString603279() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        m.identity(sw);
        java.lang.String o_testIdentitySimple_literalMutationString603279__8 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.html").replaceAll("\\s+", "");
        junit.framework.TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603279__8);
        java.lang.String o_testIdentitySimple_literalMutationString603279__10 = sw.toString().replaceAll("\\s+", "");
        junit.framework.TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString603279__10);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603279__8);
    }

    public void testIdentitySimple_literalMutationString603294_add604835_literalMutationString607008() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        m.identity(sw);
        m.identity(sw);
        java.lang.String o_testIdentitySimple_literalMutationString603294__8 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.html").replaceAll("\\bs+", "");
        junit.framework.TestCase.assertEquals("Hello {{name}}\n  You have just won ${{value}}!\n\n        {{#test}}\n        {{/test}}\n{{#in_ca}}\nWell, ${{ taxed_value }},  after taxes.{{fred}}\n{{/in_ca}}", o_testIdentitySimple_literalMutationString603294__8);
        java.lang.String o_testIdentitySimple_literalMutationString603294__10 = sw.toString().replaceAll("\\s+", "");
        junit.framework.TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.htmlbox.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString603294__10);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Hello {{name}}\n  You have just won ${{value}}!\n\n        {{#test}}\n        {{/test}}\n{{#in_ca}}\nWell, ${{ taxed_value }},  after taxes.{{fred}}\n{{/in_ca}}", o_testIdentitySimple_literalMutationString603294__8);
    }

    public void testIdentitySimple_literalMutationString603305_add605027_literalMutationString606662() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache o_testIdentitySimple_literalMutationString603305_add605027__3 = c.compile("simple.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (o_testIdentitySimple_literalMutationString603305_add605027__3)).isRecursive());
        junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (o_testIdentitySimple_literalMutationString603305_add605027__3)).getName());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        m.identity(sw);
        java.lang.String o_testIdentitySimple_literalMutationString603305__8 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.html").replaceAll("\\s+", "");
        junit.framework.TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603305__8);
        java.lang.String o_testIdentitySimple_literalMutationString603305__10 = sw.toString().replaceAll("\\+", "");
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", o_testIdentitySimple_literalMutationString603305__10);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (o_testIdentitySimple_literalMutationString603305_add605027__3)).isRecursive());
        junit.framework.TestCase.assertEquals("simple.html", ((com.github.mustachejava.codes.DefaultMustache) (o_testIdentitySimple_literalMutationString603305_add605027__3)).getName());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603305__8);
    }

    public void testIdentitySimple_literalMutationString603306_literalMutationString603767() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        m.identity(sw);
        java.lang.String o_testIdentitySimple_literalMutationString603306__8 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.html").replaceAll("\\s+", "");
        junit.framework.TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603306__8);
        java.lang.String o_testIdentitySimple_literalMutationString603306__10 = sw.toString().replaceAll("\\s+", "");
        junit.framework.TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString603306__10);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603306__8);
    }

    public void testIdentitySimple_literalMutationString603279_literalMutationString603610() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        m.identity(sw);
        java.lang.String o_testIdentitySimple_literalMutationString603279__8 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.html").replaceAll("\\s+", "D");
        junit.framework.TestCase.assertEquals("HelloD{{name}}DYouDhaveDjustDwonD${{value}}!D{{#test}}D{{/test}}D{{#in_ca}}DWell,D${{Dtaxed_valueD}},DafterDtaxes.{{fred}}D{{/in_ca}}", o_testIdentitySimple_literalMutationString603279__8);
        java.lang.String o_testIdentitySimple_literalMutationString603279__10 = sw.toString().replaceAll("\\s+", "");
        junit.framework.TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString603279__10);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("HelloD{{name}}DYouDhaveDjustDwonD${{value}}!D{{#test}}D{{/test}}D{{#in_ca}}DWell,D${{Dtaxed_valueD}},DafterDtaxes.{{fred}}D{{/in_ca}}", o_testIdentitySimple_literalMutationString603279__8);
    }

    public void testProperties_literalMutationString501845() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testProperties_literalMutationString501845__7 = m.execute(sw, new java.lang.Object() {
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
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testProperties_literalMutationString501845__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testProperties_literalMutationString501845__7)).toString());
        java.lang.String o_testProperties_literalMutationString501845__22 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString501845__22);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testProperties_literalMutationString501845__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testProperties_literalMutationString501845__7)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString501845__22);
    }

    public void testSimpleWithMap_literalMutationString113226_literalMutationString114118_add131714() throws com.github.mustachejava.MustacheException, java.io.IOException, java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        java.io.StringWriter o_testSimpleWithMap_literalMutationString113226_literalMutationString114118_add131714__1 = execute("", new java.util.HashMap<java.lang.String, java.lang.Object>() {
            {
                java.lang.Object o_testSimpleWithMap_literalMutationString113226__8 = put("name", "Chris");
                java.lang.Object o_testSimpleWithMap_literalMutationString113226__9 = put("", 10000);
                java.lang.Object o_testSimpleWithMap_literalMutationString113226__10 = put("taxed_value", 6000);
                java.lang.Object o_testSimpleWithMap_literalMutationString113226__11 = put("in_ca", true);
            }
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleWithMap_literalMutationString113226_literalMutationString114118_add131714__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleWithMap_literalMutationString113226_literalMutationString114118_add131714__1)).toString());
        java.io.StringWriter sw = execute("", new java.util.HashMap<java.lang.String, java.lang.Object>() {
            {
                java.lang.Object o_testSimpleWithMap_literalMutationString113226__8 = put("name", "Chris");
                java.lang.Object o_testSimpleWithMap_literalMutationString113226__9 = put("", 10000);
                java.lang.Object o_testSimpleWithMap_literalMutationString113226__10 = put("taxed_value", 6000);
                java.lang.Object o_testSimpleWithMap_literalMutationString113226__11 = put("in_ca", true);
            }
        });
        java.lang.String o_testSimpleWithMap_literalMutationString113226__12 = com.github.mustachejava.TestUtil.getContents(this.root, "simple.txt");
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleWithMap_literalMutationString113226__12);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSimpleWithMap_literalMutationString113226_literalMutationString114118_add131714__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSimpleWithMap_literalMutationString113226_literalMutationString114118_add131714__1)).toString());
        junit.framework.TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleWithMap_literalMutationString113226__12);
    }

    public void testPartialWithTF_literalMutationString388818() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testPartialWithTF_literalMutationString388818__7 = m.execute(sw, new java.lang.Object() {
            public com.github.mustachejava.TemplateFunction i() {
                return ( s) -> s;
            }
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialWithTF_literalMutationString388818__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialWithTF_literalMutationString388818__7)).toString());
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialWithTF_literalMutationString388818__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialWithTF_literalMutationString388818__7)).toString());
    }

    public void testPartialWithTF_literalMutationString388818_add388954() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testPartialWithTF_literalMutationString388818_add388954__7 = m.execute(sw, new java.lang.Object() {
            public com.github.mustachejava.TemplateFunction i() {
                return ( s) -> s;
            }
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialWithTF_literalMutationString388818_add388954__7)).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialWithTF_literalMutationString388818_add388954__7)).getBuffer())).toString());
        java.io.Writer o_testPartialWithTF_literalMutationString388818__7 = m.execute(sw, new java.lang.Object() {
            public com.github.mustachejava.TemplateFunction i() {
                return ( s) -> s;
            }
        });
        sw.toString();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialWithTF_literalMutationString388818_add388954__7)).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialWithTF_literalMutationString388818_add388954__7)).getBuffer())).toString());
    }

    public void testPartialWithTF_remove388829_literalMutationString388900_add391181() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testPartialWithTF_remove388829_literalMutationString388900_add391181__7 = m.execute(sw, new java.lang.Object() {
            public com.github.mustachejava.TemplateFunction i() {
                return ( s) -> s;
            }
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialWithTF_remove388829_literalMutationString388900_add391181__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialWithTF_remove388829_literalMutationString388900_add391181__7)).toString());
        java.io.Writer o_testPartialWithTF_remove388829__7 = m.execute(sw, new java.lang.Object() {
            public com.github.mustachejava.TemplateFunction i() {
                return ( s) -> s;
            }
        });
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testPartialWithTF_remove388829_literalMutationString388900_add391181__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testPartialWithTF_remove388829_literalMutationString388900_add391181__7)).toString());
    }

    public void testComplexParallel_literalMutationString193496_literalMutationString193572_add197292() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        com.github.mustachejava.Mustache m = c.compile("?");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("?", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testComplexParallel_literalMutationString193496_literalMutationString193572_add197292__7 = m.execute(sw, new com.github.mustachejava.ParallelComplexObject());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testComplexParallel_literalMutationString193496_literalMutationString193572_add197292__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testComplexParallel_literalMutationString193496_literalMutationString193572_add197292__7)).toString());
        m.execute(sw, new com.github.mustachejava.ParallelComplexObject()).close();
        java.lang.String o_testComplexParallel_literalMutationString193496__10 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
        junit.framework.TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testComplexParallel_literalMutationString193496__10);
        sw.toString();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("?", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testComplexParallel_literalMutationString193496_literalMutationString193572_add197292__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testComplexParallel_literalMutationString193496_literalMutationString193572_add197292__7)).toString());
        junit.framework.TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testComplexParallel_literalMutationString193496__10);
    }

    public void testSerialCallable_literalMutationString223135_add223388() throws com.github.mustachejava.MustacheException, java.io.IOException {
        java.io.StringWriter o_testSerialCallable_literalMutationString223135_add223388__1 = execute("", new com.github.mustachejava.ParallelComplexObject());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSerialCallable_literalMutationString223135_add223388__1)).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSerialCallable_literalMutationString223135_add223388__1)).getBuffer())).toString());
        java.io.StringWriter sw = execute("", new com.github.mustachejava.ParallelComplexObject());
        java.lang.String o_testSerialCallable_literalMutationString223135__4 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
        junit.framework.TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString223135__4);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSerialCallable_literalMutationString223135_add223388__1)).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSerialCallable_literalMutationString223135_add223388__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString223135__4);
    }

    public void testSerialCallable_literalMutationString223135_add223389_add226367() throws com.github.mustachejava.MustacheException, java.io.IOException {
        java.io.StringWriter o_testSerialCallable_literalMutationString223135_add223389_add226367__1 = execute("", new com.github.mustachejava.ParallelComplexObject());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSerialCallable_literalMutationString223135_add223389_add226367__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSerialCallable_literalMutationString223135_add223389_add226367__1)).toString());
        java.io.StringWriter sw = execute("", new com.github.mustachejava.ParallelComplexObject());
        java.lang.String o_testSerialCallable_literalMutationString223135_add223389__4 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
        junit.framework.TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString223135_add223389__4);
        java.lang.String o_testSerialCallable_literalMutationString223135__4 = com.github.mustachejava.TestUtil.getContents(this.root, "complex.txt");
        junit.framework.TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString223135__4);
        sw.toString();
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testSerialCallable_literalMutationString223135_add223389_add226367__1)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testSerialCallable_literalMutationString223135_add223389_add226367__1)).toString());
        junit.framework.TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString223135_add223389__4);
        junit.framework.TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString223135__4);
    }

    public void testReadme_literalMutationString83270() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadme_literalMutationString83270__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadme_literalMutationString83270__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadme_literalMutationString83270__9)).toString());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadme_literalMutationString83270__13 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString83270__13);
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testReadme_literalMutationString83270__9)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testReadme_literalMutationString83270__9)).toString());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString83270__13);
    }

    public void testReadmeSerial_add21832_literalMutationNumber22022_remove30539() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory o_testReadmeSerial_add21832__1 = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getRecursionLimit())));
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add21832__10 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_add21832__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21832__14);
        java.lang.String String_44 = "Should be a little bit more than 4 seconds: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_44);
        boolean boolean_45 = (diff > 3999) && (diff < 3000);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21832__14);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_44);
    }

    public void testReadmeSerial_add21835_add22549_remove30546() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add21835__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        ((java.io.StringWriter) (o_testReadmeSerial_add21835__9)).getBuffer().toString();
        java.io.Writer o_testReadmeSerial_add21835__11 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_add21835__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21835__15);
        java.lang.String String_38 = "Should be a little bit more than 4 seconds: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 8001", String_38);
        boolean boolean_39 = (diff > 3999) && (diff < 6000);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21835__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 8001", String_38);
    }

    public void testReadmeSerial_add21834_literalMutationNumber21922_remove30536() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.lang.System.currentTimeMillis();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add21834__10 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_add21834__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21834__14);
        java.lang.String String_36 = "Should be a little bit more than 4 seconds: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_36);
        boolean boolean_37 = (diff > 3999) && (diff < 0);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21834__14);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_36);
    }

    public void testReadmeSerial_add21832_literalMutationNumber22022() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory o_testReadmeSerial_add21832__1 = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getExecutorService());
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add21832__10 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_add21832__14 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21832__14);
        sw.toString();
        java.lang.String String_44 = "Should be a little bit more than 4 seconds: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_44);
        boolean boolean_45 = (diff > 3999) && (diff < 3000);
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21832__14);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_44);
    }

    public void testReadmeSerial_add21835_literalMutationString21944_add29077() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        java.io.Writer o_testReadmeSerial_add21835__9 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        java.io.Writer o_testReadmeSerial_add21835__11 = m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        java.lang.System.currentTimeMillis();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeSerial_add21835__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21835__15);
        sw.toString();
        java.lang.String String_38 = "]OyQrTEs{etwelU_f0.in76])=&7.4.F[h_S3n]kc@-9" + diff;
        junit.framework.TestCase.assertEquals("]OyQrTEs{etwelU_f0.in76])=&7.4.F[h_S3n]kc@-98001", String_38);
        boolean boolean_39 = (diff > 3999) && (diff < 6000);
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21835__15);
        junit.framework.TestCase.assertEquals("]OyQrTEs{etwelU_f0.in76])=&7.4.F[h_S3n]kc@-98001", String_38);
    }

    public void testReadmeParallel_add573404_add574319_remove582550() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.lang.System.currentTimeMillis();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add573404__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573404__15);
        sw.toString();
        java.lang.String String_395 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_395);
        boolean boolean_396 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573404__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_395);
    }

    public void testReadmeParallel_add573409_remove574513() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
        sw.toString();
        java.lang.String o_testReadmeParallel_add573409__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573409__15);
        java.lang.String String_393 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_393);
        boolean boolean_394 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573409__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_393);
    }

    public void testReadmeParallel_add573409_add574299() throws com.github.mustachejava.MustacheException, java.io.IOException {
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
        sw.toString();
        sw.toString();
        sw.toString();
        java.lang.String o_testReadmeParallel_add573409__15 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573409__15);
        java.lang.String String_393 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_393);
        boolean boolean_394 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573409__15);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_393);
    }

    public void testReadmeParallel_add573406_add574207() throws com.github.mustachejava.MustacheException, java.io.IOException {
        com.github.mustachejava.MustacheFactory c = initParallel();
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        com.github.mustachejava.Mustache o_testReadmeParallel_add573406_add574207__3 = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeParallel_add573406_add574207__3)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeParallel_add573406_add574207__3)).getName());
        com.github.mustachejava.Mustache m = c.compile("items2.html");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        long start = java.lang.System.currentTimeMillis();
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context());
        m.execute(sw, new com.github.mustachejava.AmplInterpreterTest.Context()).close();
        long diff = (java.lang.System.currentTimeMillis()) - start;
        java.lang.String o_testReadmeParallel_add573406__16 = com.github.mustachejava.TestUtil.getContents(this.root, "items.txt");
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573406__16);
        sw.toString();
        java.lang.String String_387 = "Should be a little bit more than 1 second: " + diff;
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_387);
        boolean boolean_388 = (diff > 999) && (diff < 2000);
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        junit.framework.TestCase.assertFalse(((java.util.concurrent.ExecutorService) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (c)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeParallel_add573406_add574207__3)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (o_testReadmeParallel_add573406_add574207__3)).getName());
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (m)).isRecursive());
        junit.framework.TestCase.assertEquals("items2.html", ((com.github.mustachejava.codes.DefaultMustache) (m)).getName());
        junit.framework.TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573406__16);
        junit.framework.TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_387);
    }

    public void testRelativePathsTemplateFunction_literalMutationString465035_add465408_add468367() throws java.io.IOException {
        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (mf)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (mf)).getRecursionLimit())));
        com.github.mustachejava.Mustache compile = mf.compile("");
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (compile)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (compile)).getName());
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.Writer o_testRelativePathsTemplateFunction_literalMutationString465035_add465408_add468367__7 = compile.execute(sw, new java.lang.Object() {
            java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                @java.lang.Override
                public java.lang.String apply(java.lang.String s) {
                    return s;
                }
            };
        });
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRelativePathsTemplateFunction_literalMutationString465035_add465408_add468367__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRelativePathsTemplateFunction_literalMutationString465035_add465408_add468367__7)).toString());
        compile.execute(sw, new java.lang.Object() {
            java.util.function.Function i = new com.github.mustachejava.TemplateFunction() {
                @java.lang.Override
                public java.lang.String apply(java.lang.String s) {
                    return s;
                }
            };
        }).close();
        java.lang.String o_testRelativePathsTemplateFunction_literalMutationString465035__19 = com.github.mustachejava.TestUtil.getContents(this.root, "relative/paths.txt");
        junit.framework.TestCase.assertEquals("test", o_testRelativePathsTemplateFunction_literalMutationString465035__19);
        sw.toString();
        sw.toString();
        junit.framework.TestCase.assertNull(((com.github.mustachejava.DefaultMustacheFactory) (mf)).getExecutorService());
        junit.framework.TestCase.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory) (mf)).getRecursionLimit())));
        junit.framework.TestCase.assertFalse(((com.github.mustachejava.codes.DefaultMustache) (compile)).isRecursive());
        junit.framework.TestCase.assertEquals("", ((com.github.mustachejava.codes.DefaultMustache) (compile)).getName());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.lang.StringBuffer) (((java.io.StringWriter) (o_testRelativePathsTemplateFunction_literalMutationString465035_add465408_add468367__7)).getBuffer())).toString());
        junit.framework.TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((java.io.StringWriter) (o_testRelativePathsTemplateFunction_literalMutationString465035_add465408_add468367__7)).toString());
        junit.framework.TestCase.assertEquals("test", o_testRelativePathsTemplateFunction_literalMutationString465035__19);
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInv&alidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInv&alidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0_literalMutationString404993_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), " does not exist");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0_literalMutationString404993 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[ does not exist:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0_literalMutationString404291_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0_literalMutationString404291 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402492_failAssert0null406351_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402492 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402492_failAssert0null406351 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0_add406018_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "_8l#Yri.a;]#NdGCH[{wuF");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0_add406018 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[_8l#Yri.a;]#NdGCH[{wuF:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0null402650_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0null402650 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0_literalMutationString403532_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooloPng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0_literalMutationString403532 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooloPng @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0_add406049_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolonfg}}"), "testInvalidNelimiters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolonfg}}"), "testInvalidNelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0_add406049 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolonfg @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0_literalMutationString404191_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0_literalMutationString404191 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0null406353_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0null406353 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_add402625_failAssert0null406336_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402625 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402625_failAssert0null406336 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_add406146_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimitrs");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_add406146 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidNelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_add406147_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_add406147 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0null406319_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0null406319 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_add406145_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_add406145 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0null406283_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolon#g}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0null406283 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolon#g @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0null406318_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=tRoolong}}"), null);
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0null406318 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0null402644_failAssert0_add405727_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0null402644 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0null402644_failAssert0_add405727 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0_literalMutationString403740_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0_literalMutationString403740 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull402363_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull402363 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470_failAssert0_add406025_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimitekrs");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimitekrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470_failAssert0_add406025 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitekrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0null402650_failAssert0_add405765_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0null402650 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0null402650_failAssert0_add405765 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolon#g}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolon#g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402606_failAssert0_add405944_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402606 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402606_failAssert0_add405944 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong} @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402652_failAssert0null406259_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652_failAssert0null406259 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0null402643_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0null402643 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402547_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402547 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0_add405933_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=boolong}}"), "testInvalidDelimiters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0_add405933 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0null402650_failAssert0_add405763_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0null402650 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0null402650_failAssert0_add405763 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0_literalMutationString404869_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0_literalMutationString404869 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong} @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0_add405932_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0_add405932 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0null406261_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooCong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0null406261 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooCong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402492_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:X8rOcm62BQ_eh)i[U");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402492 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:X8rOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0null406305_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0null406305 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402491_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402491 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402483_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "}3&Qjleqn|WheGP z0W%");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402483 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[}3&Qjleqn|WheGP z0W%:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402490_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402490 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0null402642_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0null402642 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0_literalMutationString405117_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInv&alidDelqmitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0_literalMutationString405117 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInv&alidDelqmitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402653_failAssert0_add405758_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653_failAssert0_add405758 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0_add406116_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "w");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0_add406116 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[w:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402623_failAssert0null406314_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=boolong}}"), "testInvalidDelimiters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402623 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402623_failAssert0null406314 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_add402628_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_add402628 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_add402614_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_add402614 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_add402637_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_add402637 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402485_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolon}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402485 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolon @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402446_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "BUrn8m}]m[!>lQg5Ukh*E");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402446 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[BUrn8m}]m[!>lQg5Ukh*E:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_add402619_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidNelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402619 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_add402635_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_add402635 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402444_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402444 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull402363_failAssert0_add402603_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull402363 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull402363_failAssert0_add402603 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402556_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("9*5)@^/3]>3$"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402556 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0_add406093_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0_add406093 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong} @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_add402611_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402611 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402458_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "pkage1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402458 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[pkage1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402558_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{E{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402558 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0_add405731_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0_add405731 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402563_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInv?lidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402563 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInv?lidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_add406034_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliDelimitrs");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_add406034 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402452_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolog}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402452 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolog @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_add402634_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_add402634 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_add402632_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_add402632 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402450_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolo|g}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402450 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolo|g @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402454_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "pQge1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402454 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[pQge1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull402363_failAssert0_add402601_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull402363 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull402363_failAssert0_add402601 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_add405767_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=tooCong}}"), "testInvaliGdDelimiters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooCong}}"), "testInvaliGdDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_add405767 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooCong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0_add406102_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("kQ:(qTuJ ;4c"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0_add406102 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0null406368_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("kQ:(qTuJ ;4c"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0null406368 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402525_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=totolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402525 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =totolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402538_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), "testInvaldDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402538 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvaldDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0null406365_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0null406365 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong} @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_add402620_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidNelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidNelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402620 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402523_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=oolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402523 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =oolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0null402646_failAssert0_literalMutationString403527_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=too{long}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0null402646 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0null402646_failAssert0_literalMutationString403527 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =too{long @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402506_failAssert0null406390_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402506 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402506_failAssert0null406390 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_literalMutationString404673_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliDelimBitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_literalMutationString404673 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliDelimBitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0_literalMutationString404712_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toIlong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0_literalMutationString404712 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toIlong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_add402608_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_add402608 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0_add405737_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0_add405737 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402449_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=to[olong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402449 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =to[olong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402518_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "testInvalidDelimters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402518 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402516_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402516 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0null406308_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0null406308 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402514_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "teptInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402514 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[teptInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimitrs");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402478_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402478 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402551_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402551 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0null406374_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0null406374 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0null402643_failAssert0_add405747_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0null402643 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0null402643_failAssert0_add405747 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0_add406150_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInv&alidDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0_add406150 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInv&alidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402466_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402466 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402536_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402536 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0_add405845_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolon#g}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0_add405845 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolon#g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0_add405882_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0_add405882 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooCong}}"), "testInvaliGdDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooCong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_literalMutationString403608_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooCoing}}"), "testInvaliGdDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_literalMutationString403608 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooCoing @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402471_failAssert0_literalMutationString405362_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402471 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402471_failAssert0_literalMutationString405362 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "_8l#Yri.a;]#NdGCH[{wuF");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[_8l#Yri.a;]#NdGCH[{wuF:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0_literalMutationString404907_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "testYnvalidDelimiters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("kQ:(qTuJ ;4c"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0_literalMutationString404907 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testYnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402604_failAssert0null406317_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402604 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402604_failAssert0null406317 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402653_failAssert0_literalMutationString403593_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653_failAssert0_literalMutationString403593 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0_literalMutationString403886_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolon#g}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0_literalMutationString403886 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolon#g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0_literalMutationString404969_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolo2ng}}"), "w");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0_literalMutationString404969 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolo2ng @[w:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402606_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402606 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402471_failAssert0null406408_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402471 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402471_failAssert0null406408 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0_literalMutationString404607_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=Otoolong}}"), "_8l#Yri.a;]#NdGCH[{wuF");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0_literalMutationString404607 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =Otoolong @[_8l#Yri.a;]#NdGCH[{wuF:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0_add405919_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidNelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0_add405919 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "w");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[w:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull402363_failAssert0_literalMutationString402434_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=Xtoolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull402363 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull402363_failAssert0_literalMutationString402434 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =Xtoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0null402646_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0null402646 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_add402626_failAssert0null406337_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402626 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402626_failAssert0null406337 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402604_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402604 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull402363_failAssert0_literalMutationString402434_failAssert0_add405823_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=Xtoolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimitersnull402363 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull402363_failAssert0_literalMutationString402434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull402363_failAssert0_literalMutationString402434_failAssert0_add405823 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =Xtoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("kQ:(qTuJ ;4c"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_literalMutationString405101_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvxliGdDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_literalMutationString405101 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvxliGdDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0_literalMutationString404266_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "teIstInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0_literalMutationString404266 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[teIstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0_add406047_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0_add406047 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0null402644_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0null402644 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0_add405939_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0_add405939 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimitekrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitekrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0_add405801_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0_add405801 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_literalMutationString405102_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimitars");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_literalMutationString405102 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitars:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402653_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0null406272_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0null406272 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_literalMutationString405103_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_literalMutationString405103 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402652_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0null406297_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0null406297 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolonfg}}"), "testInvalidNelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolonfg @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402507_failAssert0null406355_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402507 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402507_failAssert0null406355 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0null406376_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0null406376 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402503_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402503 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402653_failAssert0null406260_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653_failAssert0null406260 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_add402627_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402627 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402507_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInva`lidNelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402507 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInva`lidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402623_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=boolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402623 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402506_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "]u=+CGP1Vio&z8?jBi Tq");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402506 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[]u=+CGP1Vio&z8?jBi Tq:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402624_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402624 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470_failAssert0null406346_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470_failAssert0null406346 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_add402625_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402625 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_add402626_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402626 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_add402615_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_add402615 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0_literalMutationString404730_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolonfg}}"), "testInvaldNelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0_literalMutationString404730 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolonfg @[testInvaldNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402623_failAssert0null406313_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=boolong}}"), null);
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402623 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402623_failAssert0null406313 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0null406306_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0null406306 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_add402613_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_add402613 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402447_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402447 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_add402629_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=tooong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_add402629 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0_literalMutationString404136_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj]XhrOcm62BQ_eh)i[U");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0_literalMutationString404136 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj]XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0_literalMutationString404166_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0_literalMutationString404166 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402443_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "test]InvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402443 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[test]InvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402549_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testIn]validDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402549 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testIn]validDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_add402636_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_add402636 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_literalMutationString404665_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvaliDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_literalMutationString404665 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvaliDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402486_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooloBng}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402486 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooloBng @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402483_failAssert0null406266_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402483 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402483_failAssert0null406266 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402540_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), "7.Y(5wVqRGHx!>ch&bz*{");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402540 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[7.Y(5wVqRGHx!>ch&bz*{:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402459_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "pge1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402459 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[pge1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0null406386_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0null406386 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0_add406122_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0_add406122 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402557_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{9=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402557 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_add402610_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402610 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402491_failAssert0null406389_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402491 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402491_failAssert0null406389 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0null406344_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0null406344 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402568_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402568 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402457_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "6_nZGj5wp");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402457 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[6_nZGj5wp:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0_add406148_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInv&alidDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0_add406148 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInv&alidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402553_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader(""), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402553 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_add406035_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_add406035 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_add402630_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_add402630 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402441_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402441 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402624_failAssert0null406315_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402624 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402624_failAssert0null406315 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0_add405730_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0_add405730 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402652_failAssert0_literalMutationString403566_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("page1.txt"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652_failAssert0_literalMutationString403566 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402559_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402559 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_add402633_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_add402633 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0null402651_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0null402651 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402606_failAssert0null406320_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402606 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402606_failAssert0null406320 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402604_failAssert0_add405937_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402604 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402604_failAssert0_add405937 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_add402631_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_add402631 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_add406033_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_add406033 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0_add405732_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0_add405732 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402490_failAssert0null406388_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402490 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402490_failAssert0null406388 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0null406349_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0null406349 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402526_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402526 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull402363_failAssert0_add402602_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull402363 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull402363_failAssert0_add402602 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402535_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), "testInvalidjDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402535 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidjDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_add405766_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooCong}}"), "testInvaliGdDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_add405766 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooCong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0_literalMutationString404038_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvaliGdDelimit*ers");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0_literalMutationString404038 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimit*ers:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_add405768_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooCong}}"), "testInvaliGdDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_add405768 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooCong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0null406369_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("kQ:(qTuJ ;4c"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0null406369 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402539_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402539 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0null402646_failAssert0_add405733_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0null402646 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0null402646_failAssert0_add405733 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_add402609_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_add402609 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_add402627_failAssert0null406339_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402627 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402627_failAssert0null406339 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0_add405736_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0_add405736 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_add402607_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_add402607 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402547_failAssert0null406367_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402547 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402547_failAssert0null406367 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0_add405902_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0_add405902 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0_literalMutationString404144_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "]h(dWj83+273hjj*gI.(_");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0_literalMutationString404144 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[]h(dWj83+273hjj*gI.(_:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0null406307_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0null406307 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402517_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "tes[tInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402517 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[tes[tInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0null402648_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0null402648 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402652_failAssert0_add405752_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652_failAssert0_add405752 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402503_failAssert0null406270_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402503 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402503_failAssert0null406270 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402552_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), ":;$=&lFSYVm- S&QceXJ ");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402552 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[:;$=&lFSYVm- S&QceXJ :1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402513_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolon}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402513 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolon @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0null402649_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0null402649 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402550_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    createMustacheFactory();
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimites");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402550 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimites:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0() throws java.lang.Exception {
        try {
            {
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidNelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0null406354_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolonfg}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0null406354 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolonfg @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0null406310_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0null406310 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402515_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402515 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0_add406019_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "_8l#Yri.a;]#NdGCH[{wuF");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "_8l#Yri.a;]#NdGCH[{wuF");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0_add406019 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[_8l#Yri.a;]#NdGCH[{wuF:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_add402638_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_add402638 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402475_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402475 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0_add405907_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0_add405907 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0null406385_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0null406385 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0null406316_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0null406316 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0_add405911_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    {
                        com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                        mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0_add405911 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0() throws java.lang.Exception {
        try {
            {
                createMustacheFactory();
                com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402537_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=tooong}}"), "testInvalidDelim%ters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402537 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelim%ters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402511_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    com.github.mustachejava.MustacheFactory mf = createMustacheFactory();
                    com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader("{{=boolo[g}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402511 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =boolo[g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_add308805_literalMutationString309144_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=## #=}}{{##={{ }}=####";
            com.github.mustachejava.Mustache o_testOutputDelimiters_add308805__2 = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testOutputDelimiters_add308805__11 = mustache.execute(sw, new java.lang.Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_add308805_literalMutationString309144 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309527_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##=}x{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309527 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##=}x{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0_literalMutationString312692_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    java.lang.String template = "{{=## ##=}x{{##={{ }}=###";
                    com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    mustache.execute(sw, new java.lang.Object[0]);
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0_literalMutationString312692 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0_add309531_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=# ##=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0_add309531 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0_add309530_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=# ##=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0_add309530 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309527_failAssert0_literalMutationNumber312722_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    java.lang.String template = "{{=## ##=}x{{##={{ }}=####";
                    com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    mustache.execute(sw, new java.lang.Object[0]);
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309527 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309527_failAssert0_literalMutationNumber312722 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_literalMutationString309226_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##=}x{{##={{ (}}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_literalMutationString309226 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{( @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationNumber308800_add309404_literalMutationString310895_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=## ##=}}{{##={{ }}=m###";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
            mustache.getName();
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testOutputDelimiters_literalMutationNumber308800__8 = mustache.execute(sw, new java.lang.Object[1]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationNumber308800_add309404_literalMutationString310895 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: ={{}}=m @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309527_failAssert0_add314152_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    java.lang.String template = "{{=## ##=}x{{##={{ }}=####";
                    com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    mustache.execute(sw, new java.lang.Object[0]);
                    mustache.execute(sw, new java.lang.Object[0]);
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309527 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309527_failAssert0_add314152 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0_literalMutationNumber309254_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=# ##=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0_literalMutationNumber309254 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0null309621_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=# ##=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(null, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0null309621 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationNumber308800null309585_literalMutationString310151_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=## ##=}}{{##={{=}}=####";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), null);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testOutputDelimiters_literalMutationNumber308800__8 = mustache.execute(sw, new java.lang.Object[1]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationNumber308800null309585_literalMutationString310151 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: ={{=}}= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0_add314150_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    java.lang.String template = "{{=## ##=}x{{##={{ }}=####";
                    com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    mustache.execute(sw, new java.lang.Object[0]);
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0_add314150 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=# ##=}}{{##={{ }}=####";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            mustache.execute(sw, new java.lang.Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimitersnull308810_literalMutationString308880_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=## #=}}{{##={{ }}=####";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), null);
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testOutputDelimitersnull308810__8 = mustache.execute(sw, new java.lang.Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimitersnull308810_literalMutationString308880 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =###= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_add308805_literalMutationString309144_failAssert0_add314037_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## #=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache o_testOutputDelimiters_add308805__2 = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_testOutputDelimiters_add308805__11 = mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_add308805_literalMutationString309144 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_add308805_literalMutationString309144_failAssert0_add314037 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0_add309528_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=# ##=}}{{##={{ }}=####";
                new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0_add309528 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0null309618_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##=}x{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(null, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0null309618 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_add308806_literalMutationNumber309121_failAssert0_literalMutationString312900_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=# ##=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.Writer o_testOutputDelimiters_add308806__8 = mustache.execute(sw, new java.lang.Object[0]);
                java.io.Writer o_testOutputDelimiters_add308806__9 = mustache.execute(sw, new java.lang.Object[-1]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_add308806_literalMutationNumber309121 should have thrown NegativeArraySizeException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_add308806_literalMutationNumber309121_failAssert0_literalMutationString312900 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=## ##=}x{{##={{ }}=####";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            mustache.execute(sw, new java.lang.Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_literalMutationNumber309235_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##=}x{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[-1]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_literalMutationNumber309235 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0null309620_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=# ##=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), null);
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0null309620 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =###= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0null314814_failAssert0() throws java.lang.Exception {
        try {
            {
                {
                    java.lang.String template = "{{=## ##=}x{{##={{ }}=####";
                    com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                    java.io.StringWriter sw = new java.io.StringWriter();
                    mustache.execute(null, new java.lang.Object[0]);
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0null314814 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308799_literalMutationString309090_failAssert0() throws java.lang.Exception {
        try {
            java.lang.String template = "{{=## ##=}}{{##=*{{ }}=####";
            com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "fest");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.Writer o_testOutputDelimiters_literalMutationString308799__8 = mustache.execute(sw, new java.lang.Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308799_literalMutationString309090 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =*{{}}= @[fest:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0_literalMutationNumber309251_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=# ##=}}{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), "test");
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[1]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0_literalMutationNumber309251 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0null309617_failAssert0() throws java.lang.Exception {
        try {
            {
                java.lang.String template = "{{=## ##=}x{{##={{ }}=####";
                com.github.mustachejava.Mustache mustache = new com.github.mustachejava.DefaultMustacheFactory().compile(new java.io.StringReader(template), null);
                java.io.StringWriter sw = new java.io.StringWriter();
                mustache.execute(sw, new java.lang.Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0null309617 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException expected) {
            junit.framework.TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[null:1]", expected.getMessage());
        }
    }
}

