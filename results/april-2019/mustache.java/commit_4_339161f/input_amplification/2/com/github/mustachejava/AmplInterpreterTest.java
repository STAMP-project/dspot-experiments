package com.github.mustachejava;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.github.mustachejava.codes.DefaultCode;
import com.github.mustachejava.codes.DefaultMustache;
import com.github.mustachejava.codes.PartialCode;
import com.github.mustachejava.reflect.SimpleObjectHandler;
import com.github.mustachejava.resolver.DefaultResolver;
import com.github.mustachejava.util.CapturingMustacheVisitor;
import com.github.mustachejavabenchmarks.JsonCapturer;
import com.github.mustachejavabenchmarks.JsonInterpreterTest;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import junit.framework.TestCase;


@SuppressWarnings("unused")
public class AmplInterpreterTest extends TestCase {
    protected File root;

    public void testSimple_literalMutationString92377_failAssert0_add94370_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("w0lzR+[KFs!");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString92377 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString92377_failAssert0_add94370 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w0lzR+[KFs! not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString92375() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSimple_literalMutationString92375__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString92375__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString92375__7)).toString());
        String o_testSimple_literalMutationString92375__14 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString92375__14);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString92375__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString92375__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString92375__14);
    }

    public void testSimple_literalMutationString92377_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("w0lzR+[KFs!");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimple_literalMutationString92377 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w0lzR+[KFs! not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationNumber92398_literalMutationString92745_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("simp]le.html");
            StringWriter sw = new StringWriter();
            Writer o_testSimple_literalMutationNumber92398__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.8)));
                }

                boolean in_ca = true;
            });
            String o_testSimple_literalMutationNumber92398__15 = TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimple_literalMutationNumber92398_literalMutationString92745 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simp]le.html not found", expected.getMessage());
        }
    }

    private static class LocalizedMustacheResolver extends DefaultResolver {
        private final Locale locale;

        LocalizedMustacheResolver(File root, Locale locale) {
            super(root);
            this.locale = locale;
        }

        @Override
        public Reader getReader(String resourceName) {
            int index = resourceName.lastIndexOf('.');
            String newResourceName;
            if (index == (-1)) {
                newResourceName = resourceName;
            } else {
                newResourceName = (((resourceName.substring(0, index)) + "_") + (locale.toLanguageTag())) + (resourceName.substring(index));
            }
            Reader reader = super.getReader(newResourceName);
            if (reader == null) {
                reader = super.getReader(resourceName);
            }
            return reader;
        }
    }

    public void testSimpleI18N_literalMutationString30364_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                Mustache m = c.compile("simple.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple_ko.txt");
                sw.toString();
            }
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.JAPANESE));
                Mustache m = c.compile("}TZJ(:k>A;?");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString30364 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template }TZJ(:k>A;? not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString30331_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                Mustache m = c.compile("`9#drg$5!Nw");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple_ko.txt");
                sw.toString();
            }
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.JAPANESE));
                Mustache m = c.compile("simple.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationString30331 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `9#drg$5!Nw not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString30330() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString30330__9 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString30330__9)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString30330__9)).toString());
            String o_testSimpleI18N_literalMutationString30330__16 = TestUtil.getContents(root, "simple_ko.txt");
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString30330__16);
            sw.toString();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString30330__9)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString30330__9)).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString30330__16);
        }
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("simple.html");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString30330__26 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString30330__26)).getBuffer())).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString30330__26)).toString());
            String o_testSimpleI18N_literalMutationString30330__33 = TestUtil.getContents(this.root, "simple.txt");
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString30330__33);
            sw.toString();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString30330__26)).getBuffer())).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString30330__26)).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString30330__33);
        }
    }

    public void testSimpleI18N_literalMutationNumber30342_literalMutationString33541_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                Mustache m = c.compile("s}mple.html");
                StringWriter sw = new StringWriter();
                Writer o_testSimpleI18N_literalMutationNumber30342__9 = m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 0;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String o_testSimpleI18N_literalMutationNumber30342__17 = TestUtil.getContents(root, "simple_ko.txt");
                sw.toString();
            }
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
                Mustache m = c.compile("simple.html");
                StringWriter sw = new StringWriter();
                Writer o_testSimpleI18N_literalMutationNumber30342__27 = m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String o_testSimpleI18N_literalMutationNumber30342__34 = TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber30342_literalMutationString33541 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s}mple.html not found", expected.getMessage());
        }
    }

    public void testSimpleI18N_literalMutationString30362() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("simple.html");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString30362__9 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString30362__9)).getBuffer())).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString30362__9)).toString());
            String o_testSimpleI18N_literalMutationString30362__16 = TestUtil.getContents(root, "simple_ko.txt");
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString30362__16);
            sw.toString();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString30362__9)).getBuffer())).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString30362__9)).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString30362__16);
        }
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString30362__26 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString30362__26)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString30362__26)).toString());
            String o_testSimpleI18N_literalMutationString30362__33 = TestUtil.getContents(this.root, "simple.txt");
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString30362__33);
            sw.toString();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString30362__26)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString30362__26)).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString30362__33);
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0null111643_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", null).replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0null111643 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0null111640_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll(null, "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0null111640 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0null111639_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0null111639 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_add111443_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended.replaceAll("^[\t ]+", "");
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_add111443 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationString110726_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", "X").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationString110726 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationBoolean110760_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = false;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationBoolean110760 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_add111450_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_add111450 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationString110719_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("page1.txt", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationString110719 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public String filterText(String appended, boolean startOfLine) {
                    if (startOfLine) {
                        appended = appended.replaceAll("^[\t ]+", "");
                    }
                    return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                }
            };
            Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestUtil.getContents(root, "simplefiltered.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationNumber110752_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 5000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_literalMutationNumber110752 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString109870_failAssert0_add111449_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("_VK9fo.WHg+&9|vq<!Q");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString109870_failAssert0_add111449 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _VK9fo.WHg+&9|vq<!Q not found", expected.getMessage());
        }
    }

    private DefaultMustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory(root);
    }

    public void testRecurision_literalMutationString95051_failAssert0_literalMutationString95206_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("reczursion]html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_literalMutationString95206 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template reczursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_literalMutationString95207_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("Wt9[6M|06TON Q", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_literalMutationString95207 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Wt9[6M|06TON Q not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_literalMutationString95313_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("#&o)#*`-%spC0.", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursioAn.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_literalMutationString95313 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0null95486_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion]html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0null95486 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_literalMutationString95211_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion]html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursionEtxt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_literalMutationString95211 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_literalMutationString95308_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("#&o)#*`-`%spC0.", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_literalMutationString95308 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #&o)#*`-`%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95053_failAssert0_literalMutationString95346_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("duu<.pq2mrp&z", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95053 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95053_failAssert0_literalMutationString95346 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template duu<.pq2mrp&z not found", expected.getMessage());
        }
    }

    public void testRecurisionnull95067_failAssert0_literalMutationString95360_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("`0(v4x|5D,*Sz#", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecurisionnull95067 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testRecurisionnull95067_failAssert0_literalMutationString95360 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `0(v4x|5D,*Sz# not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("#&o)#*`-%spC0.", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_literalMutationString95310_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("#&o)#*`-%spC0.", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_literalMutationString95310 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_literalMutationString95314_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("#&o)#*`-%spC0.", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.gxt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_literalMutationString95314 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_add95396_failAssert0() throws IOException {
        try {
            {
                execute("recursion]html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                StringWriter sw = execute("recursion]html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_add95396 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_add95429_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("#&o)#*`-%spC0.", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_add95429 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95058_failAssert0_literalMutationString95257_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("/BP!WMGh*C mV9", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "r3cursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95058 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95058_failAssert0_literalMutationString95257 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /BP!WMGh*C mV9 not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_add95430_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("#&o)#*`-%spC0.", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_add95430 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_add95431_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("#&o)#*`-%spC0.", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_add95431 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_add95397_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion]html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_add95397 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_add95399_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion]html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_add95399 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0_add95428_failAssert0() throws IOException {
        try {
            {
                execute("#&o)#*`-%spC0.", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                StringWriter sw = execute("#&o)#*`-%spC0.", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0_add95428 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95056_failAssert0_literalMutationString95267_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute(".[fg,?XjY],|Y1", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "rejcursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95056 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95056_failAssert0_literalMutationString95267 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .[fg,?XjY],|Y1 not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95048_failAssert0_literalMutationString95331_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("Dk|P%R]g^KcsN6&", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95048 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95048_failAssert0_literalMutationString95331 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Dk|P%R]g^KcsN6& not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("recursion]html", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95052_failAssert0null95502_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("#&o)#*`-%spC0.", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95052 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95052_failAssert0null95502 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #&o)#*`-%spC0. not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_literalMutationString95212_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion]html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "T,y&!TV!`Vw05");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_literalMutationString95212 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95051_failAssert0_add95398_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion]html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString95051 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString95051_failAssert0_add95398 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion]html not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString95049_add95369() throws IOException {
        StringWriter o_testRecurision_literalMutationString95049_add95369__1 = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecurision_literalMutationString95049_add95369__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecurision_literalMutationString95049_add95369__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        String o_testRecurision_literalMutationString95049__11 = TestUtil.getContents(root, "recursion.txt");
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString95049__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecurision_literalMutationString95049_add95369__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecurision_literalMutationString95049_add95369__1)).toString());
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString95049__11);
    }

    public void testRecursionWithInheritance_literalMutationString138668_add138989() throws IOException {
        StringWriter o_testRecursionWithInheritance_literalMutationString138668_add138989__1 = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecursionWithInheritance_literalMutationString138668_add138989__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecursionWithInheritance_literalMutationString138668_add138989__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        String o_testRecursionWithInheritance_literalMutationString138668__11 = TestUtil.getContents(root, "recursion.txt");
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString138668__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecursionWithInheritance_literalMutationString138668_add138989__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecursionWithInheritance_literalMutationString138668_add138989__1)).toString());
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString138668__11);
    }

    public void testRecursionWithInheritance_literalMutationString138670_failAssert0_literalMutationString138839_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("p@tb+=Mj$mwt0;h4qG0:dN.2b(t^}l", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138670 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138670_failAssert0_literalMutationString138839 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template p@tb+=Mj$mwt0;h4qG0:dN.2b(t^}l not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_add138681_literalMutationString138788_failAssert0() throws IOException {
        try {
            StringWriter o_testRecursionWithInheritance_add138681__1 = execute("recursion_with_inheritance.html", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            StringWriter sw = execute("ZZwJGX<1&J(Lf|yo|7fo:+8&DWJFC!B", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            String o_testRecursionWithInheritance_add138681__20 = TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_add138681_literalMutationString138788 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ZZwJGX<1&J(Lf|yo|7fo:+8&DWJFC!B not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_add139040_failAssert0() throws IOException {
        try {
            {
                execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_add139040 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_add139042_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_add139042 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138675_failAssert0_literalMutationString138967_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursi]n_with_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138675 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138675_failAssert0_literalMutationString138967 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursi]n_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_add139043_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_add139043 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_add138683_literalMutationString138746_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("*P7%s>4A+KX^Zs**nM8&^?)n*fDVM90", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            sw.toString();
            String o_testRecursionWithInheritance_add138683__12 = TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_add138683_literalMutationString138746 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template *P7%s>4A+KX^Zs**nM8&^?)n*fDVM90 not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_literalMutationString138905_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("^#$WPm7L#u$j*8gG;]Id}/4%|8]7SQ", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_literalMutationString138905 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138678_failAssert0_literalMutationString138957_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recur}ion_with_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recuVsion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138678 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138678_failAssert0_literalMutationString138957 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recur}ion_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_literalMutationString138903_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("^#$WPm7L#u$j*8gG;Ku]Id}/4%|8]7SQ", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_literalMutationString138903 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;Ku]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138677_failAssert0_literalMutationString138863_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("JpEd&S;Vsy* Q4t>ECPF(egWA^4b!b&", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138677 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138677_failAssert0_literalMutationString138863 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template JpEd&S;Vsy* Q4t>ECPF(egWA^4b!b& not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_literalMutationString138912_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursiontxt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_literalMutationString138912 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0null139118_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0null139118 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138680_failAssert0_literalMutationString138849_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursion_wit]_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursioln.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138680 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138680_failAssert0_literalMutationString138849 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursion_wit]_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_add138682_literalMutationString138761_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("6{t]kv[am=QfOIEy;=#,!=L{4Snk%AD", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            String o_testRecursionWithInheritance_add138682__11 = TestUtil.getContents(root, "recursion.txt");
            String o_testRecursionWithInheritance_add138682__12 = TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_add138682_literalMutationString138761 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 6{t]kv[am=QfOIEy;=#,!=L{4Snk%AD not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString138672_failAssert0_add139041_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString138672_failAssert0_add139041 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^#$WPm7L#u$j*8gG;K]Id}/4%|8]7SQ not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_remove91485_literalMutationString91615_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("K5;W@evixY#IO[$L3!@{S&f,8Go)8tbMu4", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            String o_testPartialRecursionWithInheritance_remove91485__10 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_remove91485_literalMutationString91615 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91474_failAssert0_literalMutationString91628_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("}v{B]5JA$OI_V_+GKFI&lSs%:0 S<^ZwWm", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91474 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91474_failAssert0_literalMutationString91628 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template }v{B]5JA$OI_V_+GKFI&lSs%:0 S<^ZwWm not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91834_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91834 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91836_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91836 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91682_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91682 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91833_failAssert0() throws IOException {
        try {
            {
                execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91833 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91469_add91790() throws IOException {
        StringWriter o_testPartialRecursionWithInheritance_literalMutationString91469_add91790__1 = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString91469_add91790__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString91469_add91790__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        String o_testPartialRecursionWithInheritance_literalMutationString91469__11 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
        TestCase.assertEquals("TEST\n  TEST\n\n\n", o_testPartialRecursionWithInheritance_literalMutationString91469__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString91469_add91790__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString91469_add91790__1)).toString());
        TestCase.assertEquals("TEST\n  TEST\n\n\n", o_testPartialRecursionWithInheritance_literalMutationString91469__11);
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0null91915_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0null91915 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91470_failAssert0_literalMutationString91740_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute(" does not exist", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91470_failAssert0_literalMutationString91740 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91471_failAssert0_literalMutationString91665_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("N.{anU>.u}6Sh>rY}Z|!fI{Ftiuq%=|zW", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91471 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91471_failAssert0_literalMutationString91665 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template N.{anU>.u}6Sh>rY}Z|!fI{Ftiuq%=|zW not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91473_failAssert0_literalMutationString91652_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("-gqUp,-4D v Nw*%u{h#<WC*R}T(r7:UUa]", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91473 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91473_failAssert0_literalMutationString91652 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template -gqUp,-4D v Nw*%u{h#<WC*R}T(r7:UUa] not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91680_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[U3{M[;/X9h", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91680 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[U3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91684_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partBial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91684 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91479_failAssert0_literalMutationString91718_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recursi^e_partial_inheritance.html", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91479 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91479_failAssert0_literalMutationString91718 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recursi^e_partial_inheritance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91480_failAssert0_literalMutationString91641_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("S5-mpPHyv`BuXXJY%&in:I47fYeJ>$5-`.", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritanceDtxt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91480 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91480_failAssert0_literalMutationString91641 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template S5-mpPHyv`BuXXJY%&in:I47fYeJ>$5-`. not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91678_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute(".Q3ZzI]z7)z=F 5VJrs`>A&IO[9w2Zv{}0", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_literalMutationString91678 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .Q3ZzI]z7)z=F 5VJrs`>A&IO[9w2Zv{}0 not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91835_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("@haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString91472_failAssert0_add91835 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @haEglAUo>@+zWbc!]L^f$`5[3{M[;/X9h not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101123_failAssert0_add103072_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("simplepragma.h%ml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123_failAssert0_add103072 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplepragma.h%ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101123_failAssert0_add103070_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("simplepragma.h%ml");
                Mustache m = c.compile("simplepragma.h%ml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123_failAssert0_add103070 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplepragma.h%ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101122_failAssert0_literalMutationString102301_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("MQch#QT-z_1?+i^W[");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chis";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101122 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101122_failAssert0_literalMutationString102301 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template MQch#QT-z_1?+i^W[ not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationNumber101131_literalMutationString101452_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("K!@&`n<cGMg^wk)vl");
            StringWriter sw = new StringWriter();
            Writer o_testSimplePragma_literalMutationNumber101131__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 0;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String o_testSimplePragma_literalMutationNumber101131__15 = TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationNumber101131_literalMutationString101452 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template K!@&`n<cGMg^wk)vl not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationNumber101133_literalMutationString101273_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("simp>lepragma.html");
            StringWriter sw = new StringWriter();
            Writer o_testSimplePragma_literalMutationNumber101133__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 9999;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String o_testSimplePragma_literalMutationNumber101133__15 = TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationNumber101133_literalMutationString101273 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simp>lepragma.html not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101122_failAssert0_add103088_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("MQch#QT-z_1?+i^W[");
                Mustache m = c.compile("MQch#QT-z_1?+i^W[");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101122 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101122_failAssert0_add103088 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template MQch#QT-z_1?+i^W[ not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101123_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("simplepragma.h%ml");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplepragma.h%ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101123_failAssert0null103282_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("simplepragma.h%ml");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123_failAssert0null103282 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplepragma.h%ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101145_failAssert0_literalMutationString102141_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("O(;GP-T(!Pz[].dZQ");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "Zimple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101145 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101145_failAssert0_literalMutationString102141 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template O(;GP-T(!Pz[].dZQ not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101118() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSimplePragma_literalMutationString101118__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimplePragma_literalMutationString101118__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimplePragma_literalMutationString101118__7)).toString());
        String o_testSimplePragma_literalMutationString101118__14 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString101118__14);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimplePragma_literalMutationString101118__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimplePragma_literalMutationString101118__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString101118__14);
    }

    public void testSimplePragma_literalMutationString101123_failAssert0_literalMutationString102230_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("simplepragma.h%ml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simlple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123_failAssert0_literalMutationString102230 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplepragma.h%ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101122_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("MQch#QT-z_1?+i^W[");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101122 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template MQch#QT-z_1?+i^W[ not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101119_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("simplepragma`.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101119 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplepragma`.html not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101123_failAssert0_literalMutationString102228_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("simplepragma.h%ml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplk.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101123_failAssert0_literalMutationString102228 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplepragma.h%ml not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101119_failAssert0_add103111_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                createMustacheFactory();
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("simplepragma`.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101119 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101119_failAssert0_add103111 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplepragma`.html not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString101119_failAssert0_literalMutationBoolean102438_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("simplepragma`.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = false;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimplePragma_literalMutationString101119 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString101119_failAssert0_literalMutationBoolean102438 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simplepragma`.html not found", expected.getMessage());
        }
    }

    private class OkGenerator {
        public boolean isItOk() {
            return true;
        }
    }

    private String getOutput(final boolean setObjectHandler) {
        final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
        if (setObjectHandler) {
            mustacheFactory.setObjectHandler(new SimpleObjectHandler());
        }
        final Mustache defaultMustache = mustacheFactory.compile(new StringReader("{{#okGenerator.isItOk}}{{okGenerator.isItOk}}{{/okGenerator.isItOk}}"), "Test template");
        final Map<String, Object> params = new HashMap<>();
        params.put("okGenerator", new AmplInterpreterTest.OkGenerator());
        final Writer writer = new StringWriter();
        defaultMustache.execute(writer, params);
        return writer.toString();
    }

    public void testMultipleWrappers_literalMutationString117242_failAssert0_literalMutationString119459_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("XHqg3>LBL");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    Object o = new Object() {
                        int taxed_value() {
                            return ((int) ((value) - ((value) * 0.4)));
                        }

                        String fred = "test";
                    };

                    Object in_ca = Arrays.asList(o, new Object() {
                        int taxed_value = ((int) ((value) - ((value) * 0.2)));
                    }, o);
                });
                TestUtil.getContents(root, "simplerewrap.txt");
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString117242 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString117242_failAssert0_literalMutationString119459 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template XHqg3>LBL not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString117241() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testMultipleWrappers_literalMutationString117241__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            Object o = new Object() {
                int taxed_value() {
                    return ((int) ((value) - ((value) * 0.4)));
                }

                String fred = "test";
            };

            Object in_ca = Arrays.asList(o, new Object() {
                int taxed_value = ((int) ((value) - ((value) * 0.2)));
            }, o);
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleWrappers_literalMutationString117241__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleWrappers_literalMutationString117241__7)).toString());
        String o_testMultipleWrappers_literalMutationString117241__23 = TestUtil.getContents(root, "simplerewrap.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString117241__23);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleWrappers_literalMutationString117241__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleWrappers_literalMutationString117241__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString117241__23);
    }

    public void testMultipleWrappers_literalMutationString117243_failAssert0_literalMutationString119205_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("X?`oO?p;SJB");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    Object o = new Object() {
                        int taxed_value() {
                            return ((int) ((value) - ((value) * 0.4)));
                        }

                        String fred = "test";
                    };

                    Object in_ca = Arrays.asList(o, new Object() {
                        int taxed_value = ((int) ((value) - ((value) * 0.2)));
                    }, o);
                });
                TestUtil.getContents(root, "simplerewrap.txt");
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString117243 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString117243_failAssert0_literalMutationString119205 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template X?`oO?p;SJB not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString3840_failAssert0_literalMutationString6391_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(Executors.newCachedThreadPool());
                Mustache m = c.compile("XgdI1tl@2XrA{T5P");
                StringWriter sw = new StringWriter();
                Writer execute = m.execute(sw, new Object() {
                    Callable<Object> nest = () -> {
                        Thread.sleep(300);
                        return "How";
                    };

                    Callable<Object> nested = () -> {
                        Thread.sleep(200);
                        return "are";
                    };

                    Callable<Object> nestest = () -> {
                        Thread.sleep(100);
                        return "yu?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840_failAssert0_literalMutationString6391 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testNestedLatches_literalMutationString3859_literalMutationString4654_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(Executors.newCachedThreadPool());
            Mustache m = c.compile("latchedtes^.html");
            StringWriter sw = new StringWriter();
            Writer execute = m.execute(sw, new Object() {
                Callable<Object> nest = () -> {
                    Thread.sleep(300);
                    return "How";
                };

                Callable<Object> nested = () -> {
                    Thread.sleep(200);
                    return "";
                };

                Callable<Object> nestest = () -> {
                    Thread.sleep(100);
                    return "you?";
                };
            });
            execute.close();
            sw.toString();
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3859_literalMutationString4654 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template latchedtes^.html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString3838_failAssert0_add7774_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(Executors.newCachedThreadPool());
                c.compile("lat`hedtest.html");
                Mustache m = c.compile("lat`hedtest.html");
                StringWriter sw = new StringWriter();
                Writer execute = m.execute(sw, new Object() {
                    Callable<Object> nest = () -> {
                        Thread.sleep(300);
                        return "How";
                    };

                    Callable<Object> nested = () -> {
                        Thread.sleep(200);
                        return "are";
                    };

                    Callable<Object> nestest = () -> {
                        Thread.sleep(100);
                        return "you?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString3838 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3838_failAssert0_add7774 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template lat`hedtest.html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString3840_failAssert0null8313_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(Executors.newCachedThreadPool());
                Mustache m = c.compile("XgdI1tl@2XrA{T5P");
                StringWriter sw = new StringWriter();
                Writer execute = m.execute(null, new Object() {
                    Callable<Object> nest = () -> {
                        Thread.sleep(300);
                        return "How";
                    };

                    Callable<Object> nested = () -> {
                        Thread.sleep(200);
                        return "are";
                    };

                    Callable<Object> nestest = () -> {
                        Thread.sleep(100);
                        return "you?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840_failAssert0null8313 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testNestedLatches_literalMutationString3840_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(Executors.newCachedThreadPool());
            Mustache m = c.compile("XgdI1tl@2XrA{T5P");
            StringWriter sw = new StringWriter();
            Writer execute = m.execute(sw, new Object() {
                Callable<Object> nest = () -> {
                    Thread.sleep(300);
                    return "How";
                };

                Callable<Object> nested = () -> {
                    Thread.sleep(200);
                    return "are";
                };

                Callable<Object> nestest = () -> {
                    Thread.sleep(100);
                    return "you?";
                };
            });
            execute.close();
            sw.toString();
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testNestedLatches_literalMutationString3838_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory c = createMustacheFactory();
            c.setExecutorService(Executors.newCachedThreadPool());
            Mustache m = c.compile("lat`hedtest.html");
            StringWriter sw = new StringWriter();
            Writer execute = m.execute(sw, new Object() {
                Callable<Object> nest = () -> {
                    Thread.sleep(300);
                    return "How";
                };

                Callable<Object> nested = () -> {
                    Thread.sleep(200);
                    return "are";
                };

                Callable<Object> nestest = () -> {
                    Thread.sleep(100);
                    return "you?";
                };
            });
            execute.close();
            sw.toString();
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3838 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template lat`hedtest.html not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString3837_failAssert0_literalMutationString6439_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(Executors.newCachedThreadPool());
                Mustache m = c.compile(" does not exist");
                StringWriter sw = new StringWriter();
                Writer execute = m.execute(sw, new Object() {
                    Callable<Object> nest = () -> {
                        Thread.sleep(300);
                        return "How";
                    };

                    Callable<Object> nested = () -> {
                        Thread.sleep(200);
                        return "are";
                    };

                    Callable<Object> nestest = () -> {
                        Thread.sleep(100);
                        return "you?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString3837 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3837_failAssert0_literalMutationString6439 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testNestedLatches_literalMutationString3840_failAssert0_add7763_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                Executors.newCachedThreadPool();
                c.setExecutorService(Executors.newCachedThreadPool());
                Mustache m = c.compile("XgdI1tl@2XrA{T5P");
                StringWriter sw = new StringWriter();
                Writer execute = m.execute(sw, new Object() {
                    Callable<Object> nest = () -> {
                        Thread.sleep(300);
                        return "How";
                    };

                    Callable<Object> nested = () -> {
                        Thread.sleep(200);
                        return "are";
                    };

                    Callable<Object> nestest = () -> {
                        Thread.sleep(100);
                        return "you?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3840_failAssert0_add7763 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testNestedLatches_literalMutationString3838_failAssert0_literalMutationString6437_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory c = createMustacheFactory();
                c.setExecutorService(Executors.newCachedThreadPool());
                Mustache m = c.compile("lat`hedtest.html");
                StringWriter sw = new StringWriter();
                Writer execute = m.execute(sw, new Object() {
                    Callable<Object> nest = () -> {
                        Thread.sleep(300);
                        return "How";
                    };

                    Callable<Object> nested = () -> {
                        Thread.sleep(200);
                        return "are";
                    };

                    Callable<Object> nestest = () -> {
                        Thread.sleep(100);
                        return "mou?";
                    };
                });
                execute.close();
                sw.toString();
                junit.framework.TestCase.fail("testNestedLatches_literalMutationString3838 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testNestedLatches_literalMutationString3838_failAssert0_literalMutationString6437 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template lat`hedtest.html not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38861_failAssert0null39669_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList(null);
                };
                StringWriter sw = execute("^]($i&*lB9b0", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861_failAssert0null39669 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^]($i&*lB9b0 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38861_failAssert0null39671_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("^]($i&*lB9b0", null);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861_failAssert0null39671 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^]($i&*lB9b0 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38860_failAssert0_literalMutationString39242_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("i8empty.h`tml", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38860 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38860_failAssert0_literalMutationString39242 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template i8empty.h`tml not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38861_failAssert0_add39524_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("^]($i&*lB9b0", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861_failAssert0_add39524 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^]($i&*lB9b0 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38853_literalMutationString38973_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("T)st");
            };
            StringWriter sw = execute("ATjJh/>oxs}i", object);
            String o_testIsNotEmpty_literalMutationString38853__9 = TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38853_literalMutationString38973 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ATjJh/>oxs}i not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38861_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("^]($i&*lB9b0", object);
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^]($i&*lB9b0 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38863_failAssert0_literalMutationString39227_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("@&pqq_s+0[=Vu", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38863 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38863_failAssert0_literalMutationString39227 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @&pqq_s+0[=Vu not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38859_add39453() throws IOException {
        Object object = new Object() {
            List<String> people = Collections.singletonList("Test");
        };
        StringWriter o_testIsNotEmpty_literalMutationString38859_add39453__7 = execute("", object);
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testIsNotEmpty_literalMutationString38859_add39453__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testIsNotEmpty_literalMutationString38859_add39453__7)).toString());
        StringWriter sw = execute("", object);
        String o_testIsNotEmpty_literalMutationString38859__9 = TestUtil.getContents(root, "isempty.txt");
        TestCase.assertEquals("Is not empty\n", o_testIsNotEmpty_literalMutationString38859__9);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testIsNotEmpty_literalMutationString38859_add39453__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testIsNotEmpty_literalMutationString38859_add39453__7)).toString());
        TestCase.assertEquals("Is not empty\n", o_testIsNotEmpty_literalMutationString38859__9);
    }

    public void testIsNotEmpty_literalMutationString38855_literalMutationString39056_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Te?st");
            };
            StringWriter sw = execute("|+rdw7hT)tGD", object);
            String o_testIsNotEmpty_literalMutationString38855__9 = TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38855_literalMutationString39056 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template |+rdw7hT)tGD not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38861_failAssert0_literalMutationString39278_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("^]E($i&*lB9b0", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861_failAssert0_literalMutationString39278 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^]E($i&*lB9b0 not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString38861_failAssert0_add39522_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("^]($i&*lB9b0", object);
                TestUtil.getContents(root, "isempty.txt");
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString38861_failAssert0_add39522 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^]($i&*lB9b0 not found", expected.getMessage());
        }
    }

    private StringWriter execute(String name, Object object) {
        MustacheFactory c = createMustacheFactory();
        Mustache m = c.compile(name);
        StringWriter sw = new StringWriter();
        m.execute(sw, object);
        return sw;
    }

    private StringWriter execute(String name, List<Object> objects) {
        MustacheFactory c = createMustacheFactory();
        Mustache m = c.compile(name);
        StringWriter sw = new StringWriter();
        m.execute(sw, objects);
        return sw;
    }

    public void testImmutableList_literalMutationString60421_literalMutationString60608_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Tmest");
            };
            StringWriter sw = execute("uY <TjFFl1!l", Collections.singletonList(object));
            String o_testImmutableList_literalMutationString60421__10 = TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString60421_literalMutationString60608 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template uY <TjFFl1!l not found", expected.getMessage());
        }
    }

    public void testImmutableList_add60436_literalMutationString60778_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            List<Object> o_testImmutableList_add60436__7 = Collections.singletonList(object);
            StringWriter sw = execute("w0{!b9)%XXP%", Collections.singletonList(object));
            String o_testImmutableList_add60436__11 = TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_add60436_literalMutationString60778 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w0{!b9)%XXP% not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString60424_failAssert0_add61140_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("`7!c,/#KP%t?", Collections.singletonList(object));
                TestUtil.getContents(root, "isempty.txt");
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString60424 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString60424_failAssert0_add61140 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `7!c,/#KP%t? not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString60424_failAssert0_literalMutationString60883_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("`7!c,/#nP%t?", Collections.singletonList(object));
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString60424 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString60424_failAssert0_literalMutationString60883 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `7!c,/#nP%t? not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString60424_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("`7!c,/#KP%t?", Collections.singletonList(object));
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString60424 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `7!c,/#KP%t? not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString60427_failAssert0_literalMutationString60939_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("CKzfk^<kPoC", Collections.singletonList(object));
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString60427 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString60427_failAssert0_literalMutationString60939 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template CKzfk^<kPoC not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString60424_failAssert0null61304_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("`7!c,/#KP%t?", Collections.singletonList(null));
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString60424 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString60424_failAssert0null61304 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `7!c,/#KP%t? not found", expected.getMessage());
        }
    }

    public void testImmutableListnull60440_literalMutationString60655_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList(null);
            };
            StringWriter sw = execute("1--1Y>(1k@7Y", Collections.singletonList(object));
            String o_testImmutableListnull60440__10 = TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableListnull60440_literalMutationString60655 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testImmutableListnull60443_failAssert0_literalMutationString61009_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute(">q/m[mum]@/t", Collections.singletonList(object));
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testImmutableListnull60443 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testImmutableListnull60443_failAssert0_literalMutationString61009 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >q/m[mum]@/t not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106531_failAssert0_literalMutationString108099_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("se>urity.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "page1.txt";
                });
                TestUtil.getContents(root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106531 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106531_failAssert0_literalMutationString108099 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template se>urity.html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106529_failAssert0_literalMutationString108359_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("security.^html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, "securi#y.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106529 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106529_failAssert0_literalMutationString108359 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template security.^html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106529_failAssert0null109358_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("security.^html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106529 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106529_failAssert0null109358 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template security.^html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106531_failAssert0_add109095_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                createMustacheFactory();
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("se>urity.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106531 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106531_failAssert0_add109095 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template se>urity.html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106529_failAssert0_literalMutationString108328_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("securiSty.^html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106529 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106529_failAssert0_literalMutationString108328 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template securiSty.^html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106528_failAssert0null109351_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("&cV:b>D[ompZg");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528_failAssert0null109351 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106528_failAssert0_add109126_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("&cV:b>D[ompZg");
                Mustache m = c.compile("&cV:b>D[ompZg");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528_failAssert0_add109126 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106553_literalMutationString107549_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("(SlQY|!3;--wE");
            StringWriter sw = new StringWriter();
            Writer o_testSecurity_literalMutationString106553__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private String test = "";
            });
            String o_testSecurity_literalMutationString106553__15 = TestUtil.getContents(root, "security.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString106553_literalMutationString107549 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template (SlQY|!3;--wE not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106528_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("&cV:b>D[ompZg");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private String test = "Test";
            });
            TestUtil.getContents(root, "security.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106529_failAssert0_add109137_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                createMustacheFactory();
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("security.^html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106529 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106529_failAssert0_add109137 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template security.^html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106531_failAssert0null109337_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("se>urity.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106531 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106531_failAssert0null109337 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template se>urity.html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106529_failAssert0_add109138_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("security.^html");
                Mustache m = c.compile("security.^html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106529 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106529_failAssert0_add109138 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template security.^html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106531_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("se>urity.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private String test = "Test";
            });
            TestUtil.getContents(root, "security.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString106531 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template se>urity.html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106527() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSecurity_literalMutationString106527__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;

            private String test = "Test";
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSecurity_literalMutationString106527__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSecurity_literalMutationString106527__7)).toString());
        String o_testSecurity_literalMutationString106527__15 = TestUtil.getContents(root, "security.txt");
        TestCase.assertEquals("", o_testSecurity_literalMutationString106527__15);
        sw.toString();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSecurity_literalMutationString106527__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSecurity_literalMutationString106527__7)).toString());
        TestCase.assertEquals("", o_testSecurity_literalMutationString106527__15);
    }

    public void testSecurity_literalMutationString106528_failAssert0_literalMutationString108262_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("&cV:b>D[ompZg");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528_failAssert0_literalMutationString108262 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106528_failAssert0_literalMutationString108266_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("&cV:b>D[ompZg");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chvris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528_failAssert0_literalMutationString108266 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106529_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("security.^html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private String test = "Test";
            });
            TestUtil.getContents(root, "security.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationString106529 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template security.^html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106528_failAssert0_add109130_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("&cV:b>D[ompZg");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, "security.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528_failAssert0_add109130 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationString106528_failAssert0null109352_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("&cV:b>D[ompZg");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;

                    private String test = "Test";
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSecurity_literalMutationString106528 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSecurity_literalMutationString106528_failAssert0null109352 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template &cV:b>D[ompZg not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString113426_failAssert0_add115540_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("#}p3PUrWuJ&");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String getName() {
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
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString113426 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString113426_failAssert0_add115540 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #}p3PUrWuJ& not found", expected.getMessage());
        }
    }

    public void testProperties_remove113463_literalMutationString114445_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("J9:C+(O@a7!");
            StringWriter sw = new StringWriter();
            Writer o_testProperties_remove113463__7 = m.execute(sw, new Object() {
                String getName() {
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
            String o_testProperties_remove113463__21 = TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testProperties_remove113463_literalMutationString114445 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testProperties_literalMutationString113426_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("#}p3PUrWuJ&");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String getName() {
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
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testProperties_literalMutationString113426 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #}p3PUrWuJ& not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString113426_failAssert0_literalMutationString114520_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("#}p3PUrWuJ&");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String getName() {
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
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString113426 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString113426_failAssert0_literalMutationString114520 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #}p3PUrWuJ& not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString113425() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testProperties_literalMutationString113425__7 = m.execute(sw, new Object() {
            String getName() {
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
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testProperties_literalMutationString113425__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testProperties_literalMutationString113425__7)).toString());
        String o_testProperties_literalMutationString113425__22 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString113425__22);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testProperties_literalMutationString113425__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testProperties_literalMutationString113425__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString113425__22);
    }

    public void testProperties_literalMutationString113450_failAssert0_literalMutationString114762_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("simp{le.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String getName() {
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
                TestUtil.getContents(root, "simpl<e.txt");
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString113450 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString113450_failAssert0_literalMutationString114762 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simp{le.html not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString23910_literalMutationString24391_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            StringWriter sw = execute("s:mple.html", new HashMap<String, Object>() {
                {
                    Object o_testSimpleWithMap_literalMutationString23910__8 = put("name", "Chris");
                    Object o_testSimpleWithMap_literalMutationString23910__9 = put("value", 10000);
                    Object o_testSimpleWithMap_literalMutationString23910__10 = put("page1.txt", 6000);
                    Object o_testSimpleWithMap_literalMutationString23910__11 = put("in_ca", true);
                }
            });
            String o_testSimpleWithMap_literalMutationString23910__12 = TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString23910_literalMutationString24391 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s:mple.html not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString23897_literalMutationString24611_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            StringWriter sw = execute("wLa&3HRp%`o", new HashMap<String, Object>() {
                {
                    Object o_testSimpleWithMap_literalMutationString23897__8 = put("name", "Chris");
                    Object o_testSimpleWithMap_literalMutationString23897__9 = put("vlue", 10000);
                    Object o_testSimpleWithMap_literalMutationString23897__10 = put("taxed_value", 6000);
                    Object o_testSimpleWithMap_literalMutationString23897__11 = put("in_ca", true);
                }
            });
            String o_testSimpleWithMap_literalMutationString23897__12 = TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString23897_literalMutationString24611 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template wLa&3HRp%`o not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_add23934_literalMutationString27055_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            StringWriter sw = execute("o!vp#[pb>w1", new HashMap<String, Object>() {
                {
                    Object o_testSimpleWithMap_add23934__8 = put("name", "Chris");
                    Object o_testSimpleWithMap_add23934__9 = put("name", "Chris");
                    Object o_testSimpleWithMap_add23934__10 = put("value", 10000);
                    Object o_testSimpleWithMap_add23934__11 = put("taxed_value", 6000);
                    Object o_testSimpleWithMap_add23934__12 = put("in_ca", true);
                }
            });
            String o_testSimpleWithMap_add23934__13 = TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_add23934_literalMutationString27055 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template o!vp#[pb>w1 not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString23930_failAssert0_literalMutationString28011_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                StringWriter sw = execute("lIV?>vW%L$T", new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                    }
                });
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString23930 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString23930_failAssert0_literalMutationString28011 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template lIV?>vW%L$T not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95955_failAssert0_literalMutationString96068_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile(".UO.-0reyG2)mQP}*;_8EElfkUhH:5");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95955 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95955_failAssert0_literalMutationString96068 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .UO.-0reyG2)mQP}*;_8EElfkUhH:5 not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95954_failAssert0_literalMutationString96047_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("{*<Zv8(CDjk*<;w/M#&<J4Xd_#4fn");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95954 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95954_failAssert0_literalMutationString96047 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {*<Zv8(CDjk*<;w/M#&<J4Xd_#4fn not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95954_failAssert0_literalMutationString96049_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("partiali:ntempatefunction.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95954 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95954_failAssert0_literalMutationString96049 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partiali:ntempatefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTFnull95964_failAssert0_literalMutationString96080_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("6NVS*>#5mE(.X##)^I<DXxW-g$Cbsw");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTFnull95964 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTFnull95964_failAssert0_literalMutationString96080 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 6NVS*>#5mE(.X##)^I<DXxW-g$Cbsw not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add95958_literalMutationString96019_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache o_testPartialWithTF_add95958__3 = c.compile("partialintemplatefunction.html");
            Mustache m = c.compile("partialintemplatefunctio[n.html");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add95958__8 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add95958_literalMutationString96019 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialintemplatefunctio[n.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96055_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("1X8&`T+_*D]5*fbKoc1?{G|8A`s=7u");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96055 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1X8&`T+_*D]5*fbKoc1?{G|8A`s=7u not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96053_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/ml6k7!M$u{h#XpDI9{xqeK)9gy&gj");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96053 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /ml6k7!M$u{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add95958_literalMutationString96017_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache o_testPartialWithTF_add95958__3 = c.compile("partialintemplatefunction.html");
            Mustache m = c.compile("/mWpKsDrYvzDI[V7BcfiB.f>(?vgrj");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add95958__8 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add95958_literalMutationString96017 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /mWpKsDrYvzDI[V7BcfiB.f>(?vgrj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95953_failAssert0_literalMutationString96060_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile(" does not exist");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95953 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95953_failAssert0_literalMutationString96060 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95951_add96087() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testPartialWithTF_literalMutationString95951_add96087__7 = m.execute(sw, new Object() {
            public TemplateFunction i() {
                return ( s) -> s;
            }
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString95951_add96087__7)).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString95951_add96087__7)).getBuffer())).toString());
        Writer o_testPartialWithTF_literalMutationString95951__7 = m.execute(sw, new Object() {
            public TemplateFunction i() {
                return ( s) -> s;
            }
        });
        sw.toString();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString95951_add96087__7)).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString95951_add96087__7)).getBuffer())).toString());
    }

    public void testPartialWithTF_add95958_literalMutationString96009_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache o_testPartialWithTF_add95958__3 = c.compile("B1qCfB0]De/ym1%_O.;{gtdojuHL{2");
            Mustache m = c.compile("partialintemplatefunction.html");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add95958__8 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add95958_literalMutationString96009 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template B1qCfB0]De/ym1%_O.;{gtdojuHL{2 not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_remove95962_literalMutationString96034_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("partialintemplatefunction.[html");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_remove95962__7 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_remove95962_literalMutationString96034 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialintemplatefunction.[html not found", expected.getMessage());
        }
    }

    public void testPartialWithTFnull95964_failAssert0_literalMutationString96079_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("partialintemplatefunction.ht[ml");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTFnull95964 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialWithTFnull95964_failAssert0_literalMutationString96079 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialintemplatefunction.ht[ml not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_add96224_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_add96224 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96056_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/ml6k7!M$J{#XpDI9{xqeK)9gy&gj");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96056 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /ml6k7!M$J{#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96052_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{zxqeK)9gy&gj");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_literalMutationString96052 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{zxqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95952_failAssert0_literalMutationString96074_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("partialiFn<templatefunction.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95952 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95952_failAssert0_literalMutationString96074 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialiFn<templatefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95951() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testPartialWithTF_literalMutationString95951__7 = m.execute(sw, new Object() {
            public TemplateFunction i() {
                return ( s) -> s;
            }
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString95951__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString95951__7)).toString());
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString95951__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString95951__7)).toString());
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_add96227_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_add96227 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_add96225_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_add96225 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95952_failAssert0_literalMutationString96071_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("8TSijT0B%+n|8$V[8t6d8FQ+9qw*43f");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95952 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95952_failAssert0_literalMutationString96071 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 8TSijT0B%+n|8$V[8t6d8FQ+9qw*43f not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95953_failAssert0_literalMutationString96062_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/!j;}C5up");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95953 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95953_failAssert0_literalMutationString96062 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /!j;}C5up not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_add96223_failAssert0() throws MustacheException, IOException {
        try {
            {
                createMustacheFactory();
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_add96223 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add95960_literalMutationString96032_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("pa}rtialintemplatefunction.html");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add95960__7 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add95960_literalMutationString96032 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template pa}rtialintemplatefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add95957_literalMutationString96025_failAssert0() throws MustacheException, IOException {
        try {
            DefaultMustacheFactory o_testPartialWithTF_add95957__1 = createMustacheFactory();
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("`tdYKM(JOCqX7$(k={m)JwH54Q&:.,");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add95957__8 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add95957_literalMutationString96025 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `tdYKM(JOCqX7$(k={m)JwH54Q&:., not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add95959_literalMutationString96007_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("i*&oUH{aRA/24$),9E@g:ljR?D3xdi");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add95959__7 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            Writer o_testPartialWithTF_add95959__14 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add95959_literalMutationString96007 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template i*&oUH{aRA/24$),9E@g:ljR?D3xdi not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_remove95962_literalMutationString96036_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("NJZV *,B[N:%IWFqsGrAjwrz`W(mzM");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_remove95962__7 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_remove95962_literalMutationString96036 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template NJZV *,B[N:%IWFqsGrAjwrz`W(mzM not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_remove95961_literalMutationString96043_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("D$ftQUA(k, # Bn<|FSu0[+>/c([-!");
            StringWriter sw = new StringWriter();
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_remove95961_literalMutationString96043 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template D$ftQUA(k, # Bn<|FSu0[+>/c([-! not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0null96285_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0null96285 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString95956_failAssert0_add96226_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("/ml6k7!M$J{h#XpDI9{xqeK)9gy&gj");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString95956_failAssert0_add96226 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template /ml6k7!M$J{h#XpDI9{xqeK)9gy&gj not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0_add146226_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("com plex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                createMustacheFactory().compile("complex.html");
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144908_failAssert0_add146226 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com plex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0_add146225_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("com plex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                JsonInterpreterTest.toObject(jsonNode);
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144908_failAssert0_add146225 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com plex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0null149372_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(".9m7se)D;[e[");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147452_failAssert0null149372 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0_literalMutationString145452_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("45]_R11%HewT");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144919_failAssert0_literalMutationString145452 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0_literalMutationString147944_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(".9m7se)D;[e[");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147452_failAssert0_literalMutationString147944 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile(".9m7se)D;[e[");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("complex.html");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147465_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("complex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("0=ANDO]TejGV");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString147465 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 0=ANDO]TejGV not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144909_failAssert0_literalMutationString145204_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex. html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144909 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144909_failAssert0_literalMutationString145204 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex. html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147467_failAssert0_literalMutationString148114_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("2V-e6x6fvC,`");
                m.execute(sw, o);
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147467 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147467_failAssert0_literalMutationString148114 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 2V-e6x6fvC,` not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147465_failAssert0null149410_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("0=ANDO]TejGV");
                m.execute(null, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147465 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147465_failAssert0null149410 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 0=ANDO]TejGV not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147465_failAssert0_add148860_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                c.compile("complex.html");
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("0=ANDO]TejGV");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147465 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147465_failAssert0_add148860 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 0=ANDO]TejGV not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0_add148795_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(".9m7se)D;[e[");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                JsonInterpreterTest.toObject(jsonNode);
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147452_failAssert0_add148795 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0null146891_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(".gU[j[4>bs^N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, null);
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144906_failAssert0null146891 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147465_failAssert0_add148864_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("0=ANDO]TejGV");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147465 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147465_failAssert0_add148864 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 0=ANDO]TejGV not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0null146845_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("45]_R11%HewT");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144919_failAssert0null146845 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0_literalMutationString145477_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("45]_R11%HewT");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144919_failAssert0_literalMutationString145477 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplexnull147505_failAssert0_literalMutationString148256_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("l{%nj|p@I/##");
                m.execute(sw, o);
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testComplexnull147505 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull147505_failAssert0_literalMutationString148256 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template l{%nj|p@I/## not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("complex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("45]_R11%HewT");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0_literalMutationString145568_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(".|gU[j[4>bs^N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144906_failAssert0_literalMutationString145568 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .|gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile(".gU[j[4>bs^N");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("complex.html");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144905_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("]omplex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("complex.html");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString144905 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]omplex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0_add146301_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                JsonInterpreterTest.toObject(jsonNode);
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("45]_R11%HewT");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144919_failAssert0_add146301 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("com plex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("complex.html");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com plex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0_literalMutationString145578_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(".gU[j[4>bs^N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144906_failAssert0_literalMutationString145578 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144905_failAssert0null146927_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("]omplex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, null);
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144905 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144905_failAssert0null146927 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]omplex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0_literalMutationString145355_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("com plex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "[Djs1Zk_Z(+");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144908_failAssert0_literalMutationString145355 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com plex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0_add146372_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(".gU[j[4>bs^N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144906_failAssert0_add146372 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144905_failAssert0null146925_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("]omplex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(null, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144905 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144905_failAssert0null146925 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]omplex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0_add146376_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(".gU[j[4>bs^N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                jf.createJsonParser(json.toString()).readValueAsTree();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144906_failAssert0_add146376 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144905_failAssert0_add146441_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("]omplex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144905 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144905_failAssert0_add146441 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]omplex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0_add148791_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(".9m7se)D;[e[");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147452_failAssert0_add148791 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0null146851_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("45]_R11%HewT");
                m.execute(null, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144919_failAssert0null146851 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144905_failAssert0_literalMutationString145658_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("]omplex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("compl]x.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144905 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144905_failAssert0_literalMutationString145658 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]omplex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144906_failAssert0null146896_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(".gU[j[4>bs^N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, null);
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144906_failAssert0null146896 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .gU[j[4>bs^N not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0_literalMutationString145347_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("tv)z[/|*80DLZ");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144908_failAssert0_literalMutationString145347 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template tv)z[/|*80DLZ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144919_failAssert0_add146296_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("45]_R11%HewT");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144919 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144919_failAssert0_add146296 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 45]_R11%HewT not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0null149365_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(".9m7se)D;[e[");
                StringWriter sw = new StringWriter();
                m.execute(null, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147452_failAssert0null149365 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString147452_failAssert0_literalMutationString147956_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile(".9m7se)D;[e[");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString147452 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString147452_failAssert0_literalMutationString147956 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .9m7se)D;[e[ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0null146805_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("com plex.html");
                StringWriter sw = new StringWriter();
                m.execute(null, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144908_failAssert0null146805 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com plex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144908_failAssert0null146809_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("com plex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(null, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144908 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144908_failAssert0null146809 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com plex.html not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString144905_failAssert0_literalMutationString145650_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("]omplex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString144905 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString144905_failAssert0_literalMutationString145650 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]omplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42531_failAssert0_literalMutationString42823_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("`l;Tv+z}E=w:");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42531 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42531_failAssert0_literalMutationString42823 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template `l;Tv+z}E=w: not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0null43204_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("com}lex.html");
                StringWriter sw = new StringWriter();
                m.execute(null, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0null43204 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_add42534_literalMutationString42640_failAssert0() throws MustacheException, IOException {
        try {
            DefaultMustacheFactory o_testComplexParallel_add42534__1 = initParallel();
            MustacheFactory c = initParallel();
            Mustache m = c.compile("[6W]^uCA:$7%");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            String o_testComplexParallel_add42534__11 = TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_add42534_literalMutationString42640 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [6W]^uCA:$7% not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0_add43099_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("com}lex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject());
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0_add43099 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0_literalMutationString42806_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("c=m}lex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0_literalMutationString42806 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template c=m}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42525_failAssert0_add43089_failAssert0() throws MustacheException, IOException {
        try {
            {
                initParallel();
                MustacheFactory c = initParallel();
                Mustache m = c.compile("H;-lxyWP[r&!");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525_failAssert0_add43089 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template H;-lxyWP[r&! not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0_literalMutationString42816_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("com}lex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "compex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0_literalMutationString42816 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42525_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("H;-lxyWP[r&!");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template H;-lxyWP[r&! not found", expected.getMessage());
        }
    }

    public void testComplexParallel_add42536_literalMutationString42666_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("nx]ZOftBz##y");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            m.execute(sw, new ParallelComplexObject()).close();
            String o_testComplexParallel_add42536__13 = TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_add42536_literalMutationString42666 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template nx]ZOftBz##y not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0_add43100_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("com}lex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0_add43100 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("com}lex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42525_failAssert0null43201_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("H;-lxyWP[r&!");
                StringWriter sw = new StringWriter();
                m.execute(null, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525_failAssert0null43201 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template H;-lxyWP[r&! not found", expected.getMessage());
        }
    }

    public void testComplexParallel_remove42540_literalMutationString42692_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("n2OGu,b^uE>I");
            StringWriter sw = new StringWriter();
            String o_testComplexParallel_remove42540__7 = TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_remove42540_literalMutationString42692 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template n2OGu,b^uE>I not found", expected.getMessage());
        }
    }

    public void testComplexParallel_remove42540_literalMutationString42694_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile(" omplex.html");
            StringWriter sw = new StringWriter();
            String o_testComplexParallel_remove42540__7 = TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_remove42540_literalMutationString42694 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  omplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_add42538_literalMutationString42609_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("a=rhh`u`xwy[");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            String o_testComplexParallel_add42538__10 = TestUtil.getContents(root, "complex.txt");
            String o_testComplexParallel_add42538__11 = TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_add42538_literalMutationString42609 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template a=rhh`u`xwy[ not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0_literalMutationString42810_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("com}lex.hml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0_literalMutationString42810 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com}lex.hml not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42525_failAssert0_add43090_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                c.compile("H;-lxyWP[r&!");
                Mustache m = c.compile("H;-lxyWP[r&!");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525_failAssert0_add43090 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template H;-lxyWP[r&! not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42525_failAssert0_literalMutationString42803_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("H;-lxyWP[r&!");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.xt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42525_failAssert0_literalMutationString42803 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template H;-lxyWP[r&! not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0null43205_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("com}lex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0null43205 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString42526_failAssert0_add43096_failAssert0() throws MustacheException, IOException {
        try {
            {
                initParallel();
                MustacheFactory c = initParallel();
                Mustache m = c.compile("com}lex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString42526_failAssert0_add43096 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template com}lex.html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_add47753_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("1^Yvtbf`[k-R", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_add47753 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_add47751_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("1^Yvtbf`[k-R", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_add47751 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47469_failAssert0_literalMutationString47618_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute(" does not exist", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47469 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47469_failAssert0_literalMutationString47618 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute("1^Yvtbf`[k-R", new ParallelComplexObject());
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47477_failAssert0_literalMutationString47702_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("yJ>+G(EovsC3", new ParallelComplexObject());
                TestUtil.getContents(root, "complexstxt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47477 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47477_failAssert0_literalMutationString47702 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template yJ>+G(EovsC3 not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47469_failAssert0_literalMutationString47621_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("<CB0G`-DC", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47469 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47469_failAssert0_literalMutationString47621 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <CB0G`-DC not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47587_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex.h`tml", new ParallelComplexObject());
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47587 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47468_literalMutationString47523_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute("`", new ParallelComplexObject());
            String o_testSerialCallable_literalMutationString47468__4 = TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47468_literalMutationString47523 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ` not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_add47744_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex.h`tml", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_add47744 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_add47752_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("1^Yvtbf`[k-R", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_add47752 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47608_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("R^Yvtbf`[k-R", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47608 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template R^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0null47814_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("1^Yvtbf`[k-R", new ParallelComplexObject());
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0null47814 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallablenull47484_failAssert0_literalMutationString47710_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("4&]Q5@]s%(Ia", new ParallelComplexObject());
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallablenull47484 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSerialCallablenull47484_failAssert0_literalMutationString47710 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 4&]Q5@]s%(Ia not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47588_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex.h`tml", new ParallelComplexObject());
                TestUtil.getContents(root, "compleZx.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47588 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_add47745_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex.h`tml", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_add47745 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47473_failAssert0_literalMutationString47645_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("comp]lex.htl", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47473 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47473_failAssert0_literalMutationString47645 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template comp]lex.htl not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47614_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("1^Yvtbf`[k-R", new ParallelComplexObject());
                TestUtil.getContents(root, "D#>?yh`KbTq");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47614 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47468_add47721() throws MustacheException, IOException {
        StringWriter o_testSerialCallable_literalMutationString47468_add47721__1 = execute("", new ParallelComplexObject());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSerialCallable_literalMutationString47468_add47721__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSerialCallable_literalMutationString47468_add47721__1)).toString());
        StringWriter sw = execute("", new ParallelComplexObject());
        String o_testSerialCallable_literalMutationString47468__4 = TestUtil.getContents(root, "complex.txt");
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString47468__4);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSerialCallable_literalMutationString47468_add47721__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSerialCallable_literalMutationString47468_add47721__1)).toString());
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString47468__4);
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47610_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("1^Yvtbf`[k-R", new ParallelComplexObject());
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47610 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_add47743_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex.h`tml", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_add47743 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47474_failAssert0_literalMutationString47681_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex`html", new ParallelComplexObject());
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47474 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47474_failAssert0_literalMutationString47681 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex`html not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute("complex.h`tml", new ParallelComplexObject());
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47474_failAssert0_literalMutationString47677_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("fO.f#9583>+d", new ParallelComplexObject());
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47474 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47474_failAssert0_literalMutationString47677 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template fO.f#9583>+d not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47585_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("compl4x.h`tml", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47585 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template compl4x.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47589_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex.h`tml", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.xt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_literalMutationString47589 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47615_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("1^Yvtbf`[k-R", new ParallelComplexObject());
                TestUtil.getContents(root, "/omplex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47615 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47611_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("1^Yvtbf`[k-R", new ParallelComplexObject());
                TestUtil.getContents(root, "cmplex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_literalMutationString47611 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0null47810_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("complex.h`tml", new ParallelComplexObject());
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0null47810 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47472_failAssert0_add47750_failAssert0() throws MustacheException, IOException {
        try {
            {
                execute("1^Yvtbf`[k-R", new ParallelComplexObject());
                StringWriter sw = execute("1^Yvtbf`[k-R", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47472_failAssert0_add47750 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1^Yvtbf`[k-R not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString47470_failAssert0_add47742_failAssert0() throws MustacheException, IOException {
        try {
            {
                execute("complex.h`tml", new ParallelComplexObject());
                StringWriter sw = execute("complex.h`tml", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString47470_failAssert0_add47742 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h`tml not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52507_failAssert0null59931_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "page1.txt", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", null);
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507_failAssert0null59931 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52488_failAssert0_add58717_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488_failAssert0_add58717 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52577_failAssert0_literalMutationString56404_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("H")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("fo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52577 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52577_failAssert0_literalMutationString56404 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52587_failAssert0_literalMutationString57435_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "t) D57");
                    }
                });
                TestUtil.getContents(root, "simle.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52587 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52587_failAssert0_literalMutationString57435 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template t) D57.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52507_failAssert0_add58654_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                variable.startsWith("+");
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "page1.txt", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507_failAssert0_add58654 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0_add58740_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            sw.toString();
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", ".d]EMK");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583_failAssert0_add58740 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52521_failAssert0_literalMutationString53356_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException((" w|7U}M-xG0(Q(O*RAyTjlP]bCd.07u&nyL(vkk" + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521_failAssert0_literalMutationString53356 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52499_failAssert0_add58626_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499_failAssert0_add58626 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52498_failAssert0_add57971_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498_failAssert0_add57971 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52581_failAssert0_literalMutationString54697_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "F", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "page1.txt");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52581 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52581_failAssert0_literalMutationString54697 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0_literalMutationString57578_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Faled to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", ".d]EMK");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583_failAssert0_literalMutationString57578 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52584_failAssert0_literalMutationString56731_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "page1.txt", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "s^mple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584_failAssert0_literalMutationString56731 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("D")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
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
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0null60013_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put(null, 6000);
                        put("in_ca", true);
                        put("foo", ".d]EMK");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583_failAssert0null60013 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52497_failAssert0null58899_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497_failAssert0null58899 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52497_failAssert0_add57790_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            sw.toString();
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497_failAssert0_add57790 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52521_failAssert0_literalMutationString53348_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "page1.txt", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521_failAssert0_literalMutationString53348 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52499_failAssert0_literalMutationString57023_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499_failAssert0_literalMutationString57023 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52590_failAssert0_literalMutationNumber53547_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "TkFe<_DizS");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52590 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52590_failAssert0_literalMutationNumber53547 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52498_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("+")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
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
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52521_failAssert0null58834_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put(null, 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521_failAssert0null58834 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0null59248_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("D")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489_failAssert0null59248 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52584_failAssert0_add58551_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "s^mple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584_failAssert0_add58551 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s^mple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0_add58088_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("D")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489_failAssert0_add58088 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0_literalMutationString57632_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("Bn_ca", true);
                        put("foo", ".d]EMK");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583_failAssert0_literalMutationString57632 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52488_failAssert0null59963_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488_failAssert0null59963 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52498_failAssert0_literalMutationString54379_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "page1.txt", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498_failAssert0_literalMutationString54379 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52499_failAssert0null59847_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499_failAssert0null59847 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52488_failAssert0_literalMutationNumber57523_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 3000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488_failAssert0_literalMutationNumber57523 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52499_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("+")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
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
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52499 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52497_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("+")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
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
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0_literalMutationString54925_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("D")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "te!t.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489_failAssert0_literalMutationString54925 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52584_failAssert0null59759_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "s^mple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584_failAssert0null59759 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s^mple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52488_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("page1.txt")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
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
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52488 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0_add58101_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("D")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489_failAssert0_add58101 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52498_failAssert0null59116_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52498_failAssert0null59116 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("+")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
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
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", ".d]EMK");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52584_failAssert0_literalMutationNumber56720_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(2).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "s^mple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584_failAssert0_literalMutationNumber56720 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s^mple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52584_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("+")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
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
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "s^mple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s^mple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0null60014_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", null);
                        put("in_ca", true);
                        put("foo", ".d]EMK");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583_failAssert0null60014 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52521_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("+")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
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
            Mustache m = c.compile(new StringReader("{{>+ foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52584_failAssert0_add58537_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "s^mple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52584_failAssert0_add58537 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template s^mple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52507_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("+")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "page1.txt", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
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
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52583_failAssert0_add58731_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    tc.line();
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", ".d]EMK");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52583_failAssert0_add58731 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template .d]EMK.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0_literalMutationString54973_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("D")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("f]oo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489_failAssert0_literalMutationString54973 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52489_failAssert0null59270_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("D")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", null);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52489_failAssert0null59270 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationNumber52497_failAssert0_literalMutationNumber53704_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10001);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationNumber52497_failAssert0_literalMutationNumber53704 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52507_failAssert0_literalMutationString57262_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "page1.txt", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("8SmTJJgF&_Bo/KU_=CWKp!Y%]<*lM:VqgMn_H*k" + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52507_failAssert0_literalMutationString57262 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52521_failAssert0_add57715_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            sw.toString();
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521_failAssert0_add57715 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString52521_failAssert0_add57711_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
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
                Mustache m = c.compile(new StringReader("{{>+ foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString52521_failAssert0_add57711 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template foo].html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0null16630_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("os)Y{d4Yos");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0null16630 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15922_add16277() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadme_literalMutationString15922_add16277__9 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadme_literalMutationString15922_add16277__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadme_literalMutationString15922_add16277__9)).toString());
        Writer o_testReadme_literalMutationString15922__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadme_literalMutationString15922__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString15922__13);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadme_literalMutationString15922_add16277__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadme_literalMutationString15922_add16277__9)).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString15922__13);
    }

    public void testReadme_literalMutationString15927_failAssert0null16629_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("os)Y{d4Yos");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0null16629 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_remove15941_literalMutationString16110_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("items. html");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            long diff = (System.currentTimeMillis()) - start;
            String o_testReadme_remove15941__11 = TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_remove15941_literalMutationString16110 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items. html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0_add16488_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("os)Y{d4Yos");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0_add16488 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15924_failAssert0_literalMutationString16121_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("(P#!}q:J;");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15924 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15924_failAssert0_literalMutationString16121 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template (P#!}q:J; not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0_add16486_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("os)Y{d4Yos");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0_add16486 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0_add16481_failAssert0() throws MustacheException, IOException {
        try {
            {
                createMustacheFactory();
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("os)Y{d4Yos");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0_add16481 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("os)Y{d4Yos");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0_literalMutationString16133_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("os)Y{,d4Yos");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0_literalMutationString16133 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template os)Y{,d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15927_failAssert0_literalMutationString16140_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("os)Y{d4Yos");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "it;ms.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0_literalMutationString16140 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template os)Y{d4Yos not found", expected.getMessage());
        }
    }

    public void testReadme_add15934_literalMutationString16014_failAssert0() throws MustacheException, IOException {
        try {
            DefaultMustacheFactory o_testReadme_add15934__1 = createMustacheFactory();
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("Rd[]A@Kk-<");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            Writer o_testReadme_add15934__10 = m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            String o_testReadme_add15934__14 = TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_add15934_literalMutationString16014 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Rd[]A@Kk-< not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString15922() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadme_literalMutationString15922__9 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadme_literalMutationString15922__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadme_literalMutationString15922__9)).toString());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadme_literalMutationString15922__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString15922__13);
        sw.toString();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadme_literalMutationString15922__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadme_literalMutationString15922__9)).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString15922__13);
    }

    public void testReadme_literalMutationString15927_failAssert0_literalMutationString16129_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("os)Y{04Yos");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString15927 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString15927_failAssert0_literalMutationString16129 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template os)Y{04Yos not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add2330_add3140() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        m.getName();
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add2330__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        sw.toString();
        String o_testReadmeSerial_add2330__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2330__14);
        sw.toString();
        String String_43 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_43);
        boolean boolean_44 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2330__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_43);
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0null3384_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("C<F7cF@g!PR");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0null3384 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0_literalMutationString2792_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("C<F7cF@g!PR");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_17 = "" + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0_literalMutationString2792 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2313() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_literalMutationString2313__9 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_literalMutationString2313__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadmeSerial_literalMutationString2313__9)).toString());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_literalMutationString2313__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString2313__13);
        sw.toString();
        String String_5 = "Should be a little bit more than 4 seconds: " + diff;
        boolean boolean_6 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_literalMutationString2313__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadmeSerial_literalMutationString2313__9)).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString2313__13);
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0_add3260_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("C<F7cF@g!PR");
                Mustache m = c.compile("C<F7cF@g!PR");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0_add3260 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0null3383_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("C<F7cF@g!PR");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0null3383 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0_literalMutationString2785_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("C<E7cF@g!PR");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0_literalMutationString2785 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C<E7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerialnull2333_failAssert0_literalMutationString3011_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("!W)bfl&TZ>P");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_33 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_34 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerialnull2333 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmeSerialnull2333_failAssert0_literalMutationString3011 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template !W)bfl&TZ>P not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add2328_remove3338() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add2328__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add2328__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2328__14);
        sw.toString();
        String String_45 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_45);
        boolean boolean_46 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2328__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_45);
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("C<F7cF@g!PR");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_17 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_18 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0_add3266_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("C<F7cF@g!PR");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0_add3266 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0_add3265_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("C<F7cF@g!PR");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                sw.toString();
                String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0_add3265 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2318_failAssert0_literalMutationString2636_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("item%s2.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "");
                sw.toString();
                String String_7 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_8 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2318 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2318_failAssert0_literalMutationString2636 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template item%s2.html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString2313_add3045() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_literalMutationString2313_add3045__9 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_literalMutationString2313_add3045__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadmeSerial_literalMutationString2313_add3045__9)).toString());
        Writer o_testReadmeSerial_literalMutationString2313__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_literalMutationString2313__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString2313__13);
        sw.toString();
        String String_5 = "Should be a little bit more than 4 seconds: " + diff;
        boolean boolean_6 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_literalMutationString2313_add3045__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadmeSerial_literalMutationString2313_add3045__9)).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString2313__13);
    }

    public void testReadmeSerial_add2324_literalMutationNumber2591() throws MustacheException, IOException {
        DefaultMustacheFactory o_testReadmeSerial_add2324__1 = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (o_testReadmeSerial_add2324__1)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testReadmeSerial_add2324__1)).getRecursionLimit())));
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add2324__10 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add2324__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2324__14);
        sw.toString();
        String String_47 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_47);
        boolean boolean_48 = (diff > 0) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (o_testReadmeSerial_add2324__1)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testReadmeSerial_add2324__1)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2324__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_47);
    }

    public void testReadmeSerial_literalMutationString2316_failAssert0_literalMutationString2787_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("C<F7cF@g!PR");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "=$JI<B[&L");
                sw.toString();
                String String_17 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_18 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString2316_failAssert0_literalMutationString2787 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C<F7cF@g!PR not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0null125924_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("yd3<&,9SXV<");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_141 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_142 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834_failAssert0null125924 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124842_failAssert0_literalMutationString125248_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("_tb&b3^._T!");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, ">tems.txt");
                sw.toString();
                String String_127 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_128 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124842 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124842_failAssert0_literalMutationString125248 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _tb&b3^._T! not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0_literalMutationNumber125417_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("yd3<&,9SXV<");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_141 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_142 = (diff > 499) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834_failAssert0_literalMutationNumber125417 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add124847_literalMutationString125004_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("{cXl3dA0]>U");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            m.execute(sw, new AmplInterpreterTest.Context()).close();
            long diff = (System.currentTimeMillis()) - start;
            String o_testReadmeParallel_add124847__16 = TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_151 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_152 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_add124847_literalMutationString125004 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {cXl3dA0]>U not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add124848_remove125871() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        System.currentTimeMillis();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add124848__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add124848__15);
        String String_163 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_163);
        boolean boolean_164 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add124848__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_163);
    }

    public void testReadmeParallel_add124847_remove125860() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add124847__16 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add124847__16);
        sw.toString();
        String String_151 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1", String_151);
        boolean boolean_152 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add124847__16);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1", String_151);
    }

    public void testReadmeParallel_add124843_literalMutationString125065_failAssert0() throws MustacheException, IOException {
        try {
            DefaultMustacheFactory o_testReadmeParallel_add124843__1 = initParallel();
            MustacheFactory c = initParallel();
            Mustache m = c.compile("}BrIzy)kMG^");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context()).close();
            long diff = (System.currentTimeMillis()) - start;
            String o_testReadmeParallel_add124843__15 = TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_155 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_156 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_add124843_literalMutationString125065 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template }BrIzy)kMG^ not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("yd3<&,9SXV<");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context()).close();
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_141 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_142 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0_add125815_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("yd3<&,9SXV<");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                sw.toString();
                String String_141 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_142 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834_failAssert0_add125815 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0_literalMutationString125401_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("yd3<&,9SXV<");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "");
                sw.toString();
                String String_141 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_142 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834_failAssert0_literalMutationString125401 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add124846_literalMutationNumber125053() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add124846__17 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add124846__17);
        sw.toString();
        String String_153 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 2001", String_153);
        boolean boolean_154 = (diff > 998) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add124846__17);
        TestCase.assertEquals("Should be a little bit more than 1 second: 2001", String_153);
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0_add125812_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("yd3<&,9SXV<");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_141 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_142 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834_failAssert0_add125812 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString124834_failAssert0null125925_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("yd3<&,9SXV<");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                String String_141 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_142 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString124834_failAssert0null125925 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template yd3<&,9SXV< not found", expected.getMessage());
        }
    }

    static class Context {
        List<AmplInterpreterTest.Context.Item> items() {
            return Arrays.asList(new AmplInterpreterTest.Context.Item("Item 1", "$19.99", Arrays.asList(new AmplInterpreterTest.Context.Feature("New!"), new AmplInterpreterTest.Context.Feature("Awesome!"))), new AmplInterpreterTest.Context.Item("Item 2", "$29.99", Arrays.asList(new AmplInterpreterTest.Context.Feature("Old."), new AmplInterpreterTest.Context.Feature("Ugly."))));
        }

        static class Item {
            Item(String name, String price, List<AmplInterpreterTest.Context.Feature> features) {
                this.name = name;
                this.price = price;
                this.features = features;
            }

            String name;

            String price;

            List<AmplInterpreterTest.Context.Feature> features;
        }

        static class Feature {
            Feature(String description) {
                this.description = description;
            }

            String description;

            Callable<String> desc() throws InterruptedException {
                return () -> {
                    Thread.sleep(1000);
                    return description;
                };
            }
        }
    }

    public void testDeferred_literalMutationString45728_add46426() throws IOException {
        DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
        TestCase.assertNull(((DeferringMustacheFactory) (mf)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DeferringMustacheFactory) (mf)).getRecursionLimit())));
        mf.setExecutorService(Executors.newCachedThreadPool());
        Object context = new Object() {
            String title = "Deferred";

            Object deferred = new DeferringMustacheFactory.DeferredCallable();

            Object deferredpartial = DeferringMustacheFactory.DEFERRED;
        };
        Mustache m = mf.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testDeferred_literalMutationString45728_add46426__15 = m.execute(sw, context);
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testDeferred_literalMutationString45728_add46426__15)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testDeferred_literalMutationString45728_add46426__15)).toString());
        m.execute(sw, context).close();
        String o_testDeferred_literalMutationString45728__17 = TestUtil.getContents(root, "deferred.txt");
        TestCase.assertEquals("<html>\n<head><title>Deferred</title></head>\n<body>\n<div id=\"1\"></div>\n<script>document.getElementById(\"1\").innerHTML=\"I am calculated\\n\\\"later\\\" and divs\\nare written out &lt;\\nnow\";</script>\n</body>\n</html>", o_testDeferred_literalMutationString45728__17);
        sw.toString();
        TestCase.assertFalse(((ExecutorService) (((DeferringMustacheFactory) (mf)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DeferringMustacheFactory) (mf)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DeferringMustacheFactory) (mf)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testDeferred_literalMutationString45728_add46426__15)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testDeferred_literalMutationString45728_add46426__15)).toString());
        TestCase.assertEquals("<html>\n<head><title>Deferred</title></head>\n<body>\n<div id=\"1\"></div>\n<script>document.getElementById(\"1\").innerHTML=\"I am calculated\\n\\\"later\\\" and divs\\nare written out &lt;\\nnow\";</script>\n</body>\n</html>", o_testDeferred_literalMutationString45728__17);
    }

    public void testDeferred_literalMutationString45733_failAssert0_literalMutationString46190_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile("<3q|gP.t:H>B&");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45733 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45733_failAssert0_literalMutationString46190 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <3q|gP.t:H>B& not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45733_failAssert0_literalMutationString46192_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile("<3q|gP.t:H>B&");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45733 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45733_failAssert0_literalMutationString46192 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <3q|gP.t:H>B& not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45733_failAssert0null46972_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile("<3q|gP.t:H>B&");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45733 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45733_failAssert0null46972 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <3q|gP.t:H>B& not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45739_failAssert0_literalMutationString46205_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile("defe<red.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, "f&XAe@?+)F](");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45739 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45739_failAssert0_literalMutationString46205 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template defe<red.html not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45733_failAssert0_add46729_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile("<3q|gP.t:H>B&");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45733 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45733_failAssert0_add46729 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <3q|gP.t:H>B& not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45726_literalMutationString45893_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "Q^5_02r,";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache m = mf.compile("PV}wPt4DA=r#]");
            StringWriter sw = new StringWriter();
            m.execute(sw, context).close();
            String o_testDeferred_literalMutationString45726__17 = TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString45726_literalMutationString45893 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template PV}wPt4DA=r#] not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45733_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "Deferred";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache m = mf.compile("<3q|gP.t:H>B&");
            StringWriter sw = new StringWriter();
            m.execute(sw, context).close();
            TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString45733 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <3q|gP.t:H>B& not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45733_failAssert0_add46733_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile("<3q|gP.t:H>B&");
                StringWriter sw = new StringWriter();
                m.execute(sw, context);
                m.execute(sw, context).close();
                TestUtil.getContents(root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45733 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45733_failAssert0_add46733 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <3q|gP.t:H>B& not found", expected.getMessage());
        }
    }

    public void testDeferrednull45753_failAssert0_literalMutationString46363_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile(" 8Pv/2 TxR{B[");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testDeferrednull45753 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDeferrednull45753_failAssert0_literalMutationString46363 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  8Pv/2 TxR{B[ not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString45737_failAssert0_literalMutationString46242_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile("d4flfrn:H<Uq4");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, "deferr^d.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString45737 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString45737_failAssert0_literalMutationString46242 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template d4flfrn:H<Uq4 not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0null112876_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
                StringWriter sw = new StringWriter();
                compile.execute(null, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0null112876 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112154_failAssert0_literalMutationString112410_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("relative/functi[onaths.html");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112154 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112154_failAssert0_literalMutationString112410 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template relative/functi[onaths.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112154_failAssert0_literalMutationString112412_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("bp.7X d[(2*s#S7]&DM<sre0gz");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112154 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112154_failAssert0_literalMutationString112412 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template bp.7X d[(2*s#S7]&DM<sre0gz not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_add112743_failAssert0() throws IOException {
        try {
            {
                createMustacheFactory();
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_add112743 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112152_failAssert0_literalMutationString112386_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile(" does not exist");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112152 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112152_failAssert0_literalMutationString112386 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_add112746_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                });
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_add112746 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_add112747_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_add112747 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_add112163_literalMutationString112250_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache o_testRelativePathsTemplateFunction_add112163__3 = mf.compile("relative/functionpaths.html");
            Mustache compile = mf.compile("re ative/functionpaths.html");
            StringWriter sw = new StringWriter();
            compile.execute(sw, new Object() {
                Function i = new TemplateFunction() {
                    @Override
                    public String apply(String s) {
                        return s;
                    }
                };
            }).close();
            String o_testRelativePathsTemplateFunction_add112163__20 = TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_add112163_literalMutationString112250 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template re ative/functionpaths.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_add112162_literalMutationString112261_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory o_testRelativePathsTemplateFunction_add112162__1 = createMustacheFactory();
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile(">[ZtF5f9wKrK=eUyE}{vA$0fAQD");
            StringWriter sw = new StringWriter();
            compile.execute(sw, new Object() {
                Function i = new TemplateFunction() {
                    @Override
                    public String apply(String s) {
                        return s;
                    }
                };
            }).close();
            String o_testRelativePathsTemplateFunction_add112162__20 = TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_add112162_literalMutationString112261 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >[ZtF5f9wKrK=eUyE}{vA$0fAQD not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_literalMutationString112486_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/pbths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_literalMutationString112486 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
            StringWriter sw = new StringWriter();
            compile.execute(sw, new Object() {
                Function i = new TemplateFunction() {
                    @Override
                    public String apply(String s) {
                        return s;
                    }
                };
            }).close();
            TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0null112877_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("RYB9La2U^=]Y.WW7UDPX*1uPf8:");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0null112877 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template RYB9La2U^=]Y.WW7UDPX*1uPf8: not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_literalMutationString112479_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("&lz9n%)E*x#&u3 wU -OE&6_UDH");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString112150_failAssert0_literalMutationString112479 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template &lz9n%)E*x#&u3 wU -OE&6_UDH not found", expected.getMessage());
        }
    }

    private static class AccessTrackingMap extends HashMap<String, String> {
        Set<String> accessed = new HashSet<>();

        @Override
        public String get(Object key) {
            accessed.add(((String) (key)));
            return super.get(key);
        }

        void check() {
            Set<String> keyset = new HashSet<>(keySet());
            keyset.removeAll(accessed);
            if (!(keyset.isEmpty())) {
                throw new MustacheException("All keys in the map were not accessed");
            }
        }
    }

    private AmplInterpreterTest.AccessTrackingMap createBaseMap() {
        AmplInterpreterTest.AccessTrackingMap accessTrackingMap = new AmplInterpreterTest.AccessTrackingMap();
        accessTrackingMap.put("first", "Sam");
        accessTrackingMap.put("last", "Pullara");
        return accessTrackingMap;
    }

    private static class SuperClass {
        String values = "value";
    }

    private DefaultMustacheFactory initParallel() {
        DefaultMustacheFactory cf = createMustacheFactory();
        cf.setExecutorService(Executors.newCachedThreadPool());
        return cf;
    }

    protected void setUp() throws Exception {
        super.setUp();
        File file = new File("src/test/resources");
        root = (new File(file, "simple.html").exists()) ? file : new File("../src/test/resources");
    }
}

