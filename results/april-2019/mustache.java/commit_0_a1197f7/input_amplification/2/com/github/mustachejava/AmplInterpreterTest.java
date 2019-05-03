package com.github.mustachejava;


import com.github.mustachejava.codes.CommentCode;
import com.github.mustachejava.codes.DefaultCode;
import com.github.mustachejava.codes.DefaultMustache;
import com.github.mustachejava.codes.ExtendCode;
import com.github.mustachejava.codes.PartialCode;
import com.github.mustachejava.functions.CommentFunction;
import com.github.mustachejava.reflect.SimpleObjectHandler;
import com.github.mustachejava.resolver.DefaultResolver;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import junit.framework.TestCase;


@SuppressWarnings("unused")
public class AmplInterpreterTest extends TestCase {
    protected File root;

    public void testSimple_literalMutationString70864() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSimple_literalMutationString70864__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString70864__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString70864__7)).toString());
        String o_testSimple_literalMutationString70864__14 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString70864__14);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString70864__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString70864__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString70864__14);
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

    public void testSimpleI18N_literalMutationString31621() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("simple.html");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString31621__9 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString31621__9)).getBuffer())).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString31621__9)).toString());
            String o_testSimpleI18N_literalMutationString31621__16 = TestUtil.getContents(root, "simple_ko.txt");
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString31621__16);
            sw.toString();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString31621__9)).getBuffer())).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString31621__9)).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString31621__16);
        }
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString31621__26 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString31621__26)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString31621__26)).toString());
            String o_testSimpleI18N_literalMutationString31621__33 = TestUtil.getContents(this.root, "simple.txt");
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString31621__33);
            sw.toString();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString31621__26)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString31621__26)).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString31621__33);
        }
    }

    public void testSimpleI18N_literalMutationString31590() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString31590__9 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString31590__9)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString31590__9)).toString());
            String o_testSimpleI18N_literalMutationString31590__16 = TestUtil.getContents(root, "simple_ko.txt");
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString31590__16);
            sw.toString();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString31590__9)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString31590__9)).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString31590__16);
        }
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("simple.html");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString31590__26 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString31590__26)).getBuffer())).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString31590__26)).toString());
            String o_testSimpleI18N_literalMutationString31590__33 = TestUtil.getContents(this.root, "simple.txt");
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString31590__33);
            sw.toString();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString31590__26)).getBuffer())).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString31590__26)).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString31590__33);
        }
    }

    private DefaultMustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory(root);
    }

    public void testRecurision_literalMutationString73513_add73858() throws IOException {
        StringWriter o_testRecurision_literalMutationString73513_add73858__1 = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecurision_literalMutationString73513_add73858__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecurision_literalMutationString73513_add73858__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        String o_testRecurision_literalMutationString73513__11 = TestUtil.getContents(root, "recursion.txt");
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString73513__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecurision_literalMutationString73513_add73858__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecurision_literalMutationString73513_add73858__1)).toString());
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString73513__11);
    }

    public void testRecursionWithInheritance_literalMutationString121455_add121800() throws IOException {
        StringWriter o_testRecursionWithInheritance_literalMutationString121455_add121800__1 = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecursionWithInheritance_literalMutationString121455_add121800__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecursionWithInheritance_literalMutationString121455_add121800__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        String o_testRecursionWithInheritance_literalMutationString121455__11 = TestUtil.getContents(root, "recursion.txt");
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString121455__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecursionWithInheritance_literalMutationString121455_add121800__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecursionWithInheritance_literalMutationString121455_add121800__1)).toString());
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString121455__11);
    }

    public void testPartialRecursionWithInheritance_literalMutationString69961_add70306() throws IOException {
        StringWriter o_testPartialRecursionWithInheritance_literalMutationString69961_add70306__1 = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString69961_add70306__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString69961_add70306__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        String o_testPartialRecursionWithInheritance_literalMutationString69961__11 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
        TestCase.assertEquals("TEST\n  TEST\n\n\n", o_testPartialRecursionWithInheritance_literalMutationString69961__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString69961_add70306__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString69961_add70306__1)).toString());
        TestCase.assertEquals("TEST\n  TEST\n\n\n", o_testPartialRecursionWithInheritance_literalMutationString69961__11);
    }

    public void testChainedInheritance_literalMutationString101699_add102044() throws IOException {
        StringWriter o_testChainedInheritance_literalMutationString101699_add102044__1 = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testChainedInheritance_literalMutationString101699_add102044__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testChainedInheritance_literalMutationString101699_add102044__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        String o_testChainedInheritance_literalMutationString101699__11 = TestUtil.getContents(root, "page.txt");
        TestCase.assertEquals("<main id=\"content\" role=\"main\">\n  This is the page content\n\n</main>\n", o_testChainedInheritance_literalMutationString101699__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testChainedInheritance_literalMutationString101699_add102044__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testChainedInheritance_literalMutationString101699_add102044__1)).toString());
        TestCase.assertEquals("<main id=\"content\" role=\"main\">\n  This is the page content\n\n</main>\n", o_testChainedInheritance_literalMutationString101699__11);
    }

    public void testSimplePragma_literalMutationString79636() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSimplePragma_literalMutationString79636__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimplePragma_literalMutationString79636__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimplePragma_literalMutationString79636__7)).toString());
        String o_testSimplePragma_literalMutationString79636__14 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString79636__14);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimplePragma_literalMutationString79636__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimplePragma_literalMutationString79636__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString79636__14);
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

    public void testMultipleWrappers_literalMutationString94151() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testMultipleWrappers_literalMutationString94151__7 = m.execute(sw, new Object() {
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
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleWrappers_literalMutationString94151__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleWrappers_literalMutationString94151__7)).toString());
        String o_testMultipleWrappers_literalMutationString94151__23 = TestUtil.getContents(root, "simplerewrap.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString94151__23);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleWrappers_literalMutationString94151__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleWrappers_literalMutationString94151__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString94151__23);
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

    public void testIdentitySimple_literalMutationString106572_literalMutationString106980() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString106572__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString106572__8);
        String o_testIdentitySimple_literalMutationString106572__10 = sw.toString().replaceAll("\\s+", "");
        TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiffext.mustache.childdiffext.mustache.grandchilddiffext.mustache.parentdiffext.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonissue_201main.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString106572__10);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString106572__8);
    }

    public void testIdentitySimple_literalMutationString106549_add108356() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("?5I!:S.C4-!");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("?5I!:S.C4-!", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString106549_add108356__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString106549_add108356__8);
        String o_testIdentitySimple_literalMutationString106549__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString106549__8);
        String o_testIdentitySimple_literalMutationString106549__10 = sw.toString().replaceAll("\\s+", "");
        TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiffext.mustache.childdiffext.mustache.grandchilddiffext.mustache.parentdiffext.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonissue_201main.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString106549__10);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("?5I!:S.C4-!", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString106549_add108356__8);
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString106549__8);
    }

    public void testIdentitySimple_literalMutationString106546_add108310() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString106546__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString106546__8);
        String o_testIdentitySimple_literalMutationString106546__10 = sw.toString().replaceAll("\\s+", "");
        TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiffext.mustache.childdiffext.mustache.grandchilddiffext.mustache.parentdiffext.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonissue_201main.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.htmlbox.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiffext.mustache.childdiffext.mustache.grandchilddiffext.mustache.parentdiffext.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonissue_201main.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString106546__10);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString106546__8);
    }

    public void testIdentitySimple_literalMutationString106546() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString106546__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString106546__8);
        String o_testIdentitySimple_literalMutationString106546__10 = sw.toString().replaceAll("\\s+", "");
        TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiffext.mustache.childdiffext.mustache.grandchilddiffext.mustache.parentdiffext.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonissue_201main.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString106546__10);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString106546__8);
    }

    public void testIdentitySimple_literalMutationString106549() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("?5I!:S.C4-!");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("?5I!:S.C4-!", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString106549__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString106549__8);
        String o_testIdentitySimple_literalMutationString106549__10 = sw.toString().replaceAll("\\s+", "");
        TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiffext.mustache.childdiffext.mustache.grandchilddiffext.mustache.parentdiffext.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonissue_201main.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString106549__10);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("?5I!:S.C4-!", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString106549__8);
    }

    public void testProperties_literalMutationString90340() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testProperties_literalMutationString90340__7 = m.execute(sw, new Object() {
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
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testProperties_literalMutationString90340__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testProperties_literalMutationString90340__7)).toString());
        String o_testProperties_literalMutationString90340__22 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString90340__22);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testProperties_literalMutationString90340__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testProperties_literalMutationString90340__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString90340__22);
    }

    public void testPartialWithTF_literalMutationString74418() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testPartialWithTF_literalMutationString74418__7 = m.execute(sw, new Object() {
            public TemplateFunction i() {
                return ( s) -> s;
            }
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString74418__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString74418__7)).toString());
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString74418__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString74418__7)).toString());
    }

    public void testSerialCallable_literalMutationString49333_add49604() throws MustacheException, IOException {
        StringWriter o_testSerialCallable_literalMutationString49333_add49604__1 = execute("", new ParallelComplexObject());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSerialCallable_literalMutationString49333_add49604__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSerialCallable_literalMutationString49333_add49604__1)).toString());
        StringWriter sw = execute("", new ParallelComplexObject());
        String o_testSerialCallable_literalMutationString49333__4 = TestUtil.getContents(root, "complex.txt");
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString49333__4);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSerialCallable_literalMutationString49333_add49604__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSerialCallable_literalMutationString49333_add49604__1)).toString());
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString49333__4);
    }

    public void testReadme_literalMutationString17179() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadme_literalMutationString17179__9 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadme_literalMutationString17179__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadme_literalMutationString17179__9)).toString());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadme_literalMutationString17179__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString17179__13);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadme_literalMutationString17179__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadme_literalMutationString17179__9)).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString17179__13);
    }

    public void testReadmeSerial_add2303_add3151() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache o_testReadmeSerial_add2303__3 = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeSerial_add2303__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeSerial_add2303__3)).getName());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        System.currentTimeMillis();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add2303__10 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add2303__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2303__14);
        sw.toString();
        String String_17 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_17);
        boolean boolean_18 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeSerial_add2303__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeSerial_add2303__3)).getName());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2303__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_17);
    }

    public void testReadmeSerial_literalMutationString2290() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_literalMutationString2290__9 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_literalMutationString2290__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadmeSerial_literalMutationString2290__9)).toString());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_literalMutationString2290__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString2290__13);
        sw.toString();
        String String_39 = "Should be a little bit more than 4 seconds: " + diff;
        boolean boolean_40 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_literalMutationString2290__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadmeSerial_literalMutationString2290__9)).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString2290__13);
    }

    public void testReadmeParallel_add102633_add103411() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        System.currentTimeMillis();
        System.currentTimeMillis();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add102633__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add102633__15);
        sw.toString();
        String String_123 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_123);
        boolean boolean_124 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add102633__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_123);
    }

    public void testReadmeParallel_add102628_remove103731() throws MustacheException, IOException {
        DefaultMustacheFactory o_testReadmeParallel_add102628__1 = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (o_testReadmeParallel_add102628__1)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (o_testReadmeParallel_add102628__1)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testReadmeParallel_add102628__1)).getRecursionLimit())));
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add102628__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add102628__15);
        String String_131 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_131);
        boolean boolean_132 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (o_testReadmeParallel_add102628__1)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (o_testReadmeParallel_add102628__1)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testReadmeParallel_add102628__1)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add102628__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_131);
    }

    public void testReadmeParallel_add102631_remove103724() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add102631__17 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add102631__17);
        sw.toString();
        String String_127 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_127);
        boolean boolean_128 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add102631__17);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_127);
    }

    public void testReadmeParallel_add102633_remove103719() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add102633__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add102633__15);
        sw.toString();
        String String_123 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_123);
        boolean boolean_124 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add102633__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_123);
    }

    public void testReadmeParallel_add102633_remove103720() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        System.currentTimeMillis();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add102633__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add102633__15);
        String String_123 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_123);
        boolean boolean_124 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add102633__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_123);
    }

    public void testReadmeParallel_add102631_remove103725() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add102631__17 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add102631__17);
        sw.toString();
        String String_127 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_127);
        boolean boolean_128 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add102631__17);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_127);
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

    public void testVariableInhertiance_remove3897_failAssert0null5585_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3897 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3897_failAssert0null5585 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3852_failAssert0_add5244_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            args.indexOf("=");
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(-1, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852_failAssert0_add5244 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3823_failAssert0_literalMutationNumber4737_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = -2; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823_failAssert0_literalMutationNumber4737 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-2", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3897_failAssert0null5580_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3897 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3897_failAssert0null5580 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3916_failAssert0_add5416_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        mustache.getCodes();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3916 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3916_failAssert0_add5416 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3916_failAssert0_add5417_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3916 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3916_failAssert0_add5417 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3822_failAssert0_literalMutationString4658_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("`");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822_failAssert0_literalMutationString4658 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3912_failAssert0_add5555_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3912 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3912_failAssert0_add5555 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3911_failAssert0_literalMutationNumber4982_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == 0) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3911 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3911_failAssert0_literalMutationNumber4982 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3911_failAssert0_literalMutationNumber4980_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == 2) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3911 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3911_failAssert0_literalMutationNumber4980 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3840_failAssert0_add5388_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("?");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840_failAssert0_add5388 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3839_failAssert0_add5175_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            args.indexOf("{{#qualification}}\n");
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839_failAssert0_add5175 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3852_failAssert0_literalMutationString4448_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(-1, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "{{#qualification}}\n") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852_failAssert0_literalMutationString4448 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3828_failAssert0null5771_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, -1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(null);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828_failAssert0null5771 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3839_failAssert0null5646_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(null, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839_failAssert0null5646 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3852_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(-1, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3839_failAssert0_add5171_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839_failAssert0_add5171 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3924_failAssert0_literalMutationNumber4872_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3924 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3924_failAssert0_literalMutationNumber4872 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3827_failAssert0_literalMutationString4380_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827_failAssert0_literalMutationString4380 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3913_failAssert0_add5505_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3913 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3913_failAssert0_add5505 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3911_failAssert0_add5511_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        partial.getCodes();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3911 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3911_failAssert0_add5511 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3912_failAssert0null5984_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(null);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3912 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3912_failAssert0null5984 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3912_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiancenull3912 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3911_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(null);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiancenull3911 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3913_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiancenull3913 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3897_failAssert0_literalMutationString4144_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("^");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3897 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3897_failAssert0_literalMutationString4144 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3897_failAssert0null5590_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3897 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3897_failAssert0null5590 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3897_failAssert0_literalMutationNumber4133_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3897 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3897_failAssert0_literalMutationNumber4133 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3913_failAssert0null5925_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3913 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3913_failAssert0null5925 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3839_failAssert0_add5168_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            comments.size();
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839_failAssert0_add5168 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3916_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf(null);
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiancenull3916 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3852_failAssert0null5708_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(-1, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852_failAssert0null5708 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3852_failAssert0null5706_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(-1, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(null, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852_failAssert0null5706 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3840_failAssert0null5814_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("?");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840_failAssert0null5814 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3827_failAssert0_literalMutationNumber4394_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827_failAssert0_literalMutationNumber4394 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3913_failAssert0_add5484_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3913 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3913_failAssert0_add5484 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3840_failAssert0_literalMutationNumber4718_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("?");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(1, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840_failAssert0_literalMutationNumber4718 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3897_failAssert0_add5103_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            comments.size();
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3897 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3897_failAssert0_add5103 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3822_failAssert0_add5354_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 1; i < (comments.size()); i++) {
                                                comments.get(i);
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822_failAssert0_add5354 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3852_failAssert0null5709_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(-1, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852_failAssert0null5709 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3912_failAssert0null5969_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3912 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3912_failAssert0null5969 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3912_failAssert0_literalMutationNumber5051_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3912 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3912_failAssert0_literalMutationNumber5051 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3827_failAssert0_literalMutationString4386_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827_failAssert0_literalMutationString4386 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3913_failAssert0_add5487_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3913 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3913_failAssert0_add5487 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3839_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("{{#qualification}}\n");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3912_failAssert0_literalMutationNumber5057_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3912 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3912_failAssert0_literalMutationNumber5057 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3840_failAssert0_literalMutationString4717_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("?");
                            if (index == (-1)) {
                                throw new MustacheException("qR-.&4c5!Z*x7Dr ^3DNX#dEme<Kcab6z&BZfDL=n8a6[+^JG3Q");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840_failAssert0_literalMutationString4717 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("qR-.&4c5!Z*x7Dr ^3DNX#dEme<Kcab6z&BZfDL=n8a6[+^JG3Q", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3912_failAssert0_literalMutationNumber5054_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3912 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3912_failAssert0_literalMutationNumber5054 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3827_failAssert0_add5218_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            comments.size();
                                            System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827_failAssert0_add5218 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3911_failAssert0null5941_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(null, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3911 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3911_failAssert0null5941 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3839_failAssert0null5653_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839_failAssert0null5653 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3897_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_remove3897 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3840_failAssert0_add5375_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            comments.size();
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("?");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840_failAssert0_add5375 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3912_failAssert0_add5570_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3912 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3912_failAssert0_add5570 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3840_failAssert0null5828_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("?");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840_failAssert0null5828 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3911_failAssert0_add5521_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            args.substring(0, index);
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3911 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3911_failAssert0_add5521 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3913_failAssert0null5914_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3913 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3913_failAssert0null5914 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3897_failAssert0_add5107_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            args.indexOf("=");
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3897 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3897_failAssert0_add5107 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3823_failAssert0null5842_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = -1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823_failAssert0null5842 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3911_failAssert0_add5520_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            args.indexOf("=");
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3911 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3911_failAssert0_add5520 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3839_failAssert0null5650_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839_failAssert0null5650 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3897_failAssert0_add5109_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            args.substring((index + 1));
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3897 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3897_failAssert0_add5109 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3827_failAssert0null5685_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827_failAssert0null5685 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3827_failAssert0null5681_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827_failAssert0null5681 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3822_failAssert0null5800_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822_failAssert0null5800 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3840_failAssert0_add5376_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            comments.size();
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("?");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840_failAssert0_add5376 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3839_failAssert0_literalMutationNumber4275_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839_failAssert0_literalMutationNumber4275 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3912_failAssert0_add5573_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3912 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3912_failAssert0_add5573 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3912_failAssert0_add5571_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                mf.compile("issue_201/chat.html");
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3912 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3912_failAssert0_add5571 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3839_failAssert0_add5163_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        mustache.getCodes();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839_failAssert0_add5163 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3828_failAssert0null5756_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, -1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828_failAssert0null5756 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3822_failAssert0null5801_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822_failAssert0null5801 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3822_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 1; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3916_failAssert0null5856_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3916 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3916_failAssert0null5856 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3823_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = -1; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3916_failAssert0null5865_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(null, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3916 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3916_failAssert0null5865 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3916_failAssert0_literalMutationString4806_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Td<1g!CstMU2!&WI5=H@[+qFnh!}4?&Cmh(Nm+>!i|hl{b#oF<h");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3916 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3916_failAssert0_literalMutationString4806 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3823_failAssert0_literalMutationNumber4765_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = -1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(-1, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823_failAssert0_literalMutationNumber4765 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3823_failAssert0null5839_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = -1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823_failAssert0null5839 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3823_failAssert0null5834_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = -1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823_failAssert0null5834 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3916_failAssert0_literalMutationNumber4813_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 2));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3916 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3916_failAssert0_literalMutationNumber4813 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3822_failAssert0null5808_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822_failAssert0null5808 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3827_failAssert0_add5221_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            args.indexOf("=");
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827_failAssert0_add5221 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3852_failAssert0_add5233_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(-1, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852_failAssert0_add5233 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3916_failAssert0null5870_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(null);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3916 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3916_failAssert0null5870 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3911_failAssert0null5935_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3911 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3911_failAssert0null5935 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3839_failAssert0null5642_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3839_failAssert0null5642 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3852_failAssert0_add5234_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(-1, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3852_failAssert0_add5234 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3840_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("?");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3827_failAssert0null5689_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827_failAssert0null5689 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3828_failAssert0_add5305_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        comments.isEmpty();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, -1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828_failAssert0_add5305 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3911_failAssert0null5933_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3911 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3911_failAssert0null5933 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3827_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3828_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, -1, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3828_failAssert0_add5306_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            comments.size();
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, -1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828_failAssert0_add5306 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3828_failAssert0_add5307_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            comments.size();
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, -1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828_failAssert0_add5307 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3822_failAssert0_add5363_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822_failAssert0_add5363 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3823_failAssert0_add5408_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = -1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823_failAssert0_add5408 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3840_failAssert0null5822_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("?");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840_failAssert0null5822 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3823_failAssert0_add5405_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = -1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            args.indexOf("=");
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823_failAssert0_add5405 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3913_failAssert0_literalMutationString4938_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("{{#qualification}}\n");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3913 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3913_failAssert0_literalMutationString4938 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3823_failAssert0_add5403_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = -1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3823_failAssert0_add5403 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3913_failAssert0null5916_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3913 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3913_failAssert0null5916 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3916_failAssert0_add5425_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            comments.size();
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3916 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3916_failAssert0_add5425 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3840_failAssert0_literalMutationNumber4725_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("?");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 0));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3840_failAssert0_literalMutationNumber4725 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3827_failAssert0_add5220_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3827_failAssert0_add5220 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3911_failAssert0_add5508_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        mustache.getCodes();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3911 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3911_failAssert0_add5508 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3828_failAssert0_literalMutationNumber4552_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, -1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828_failAssert0_literalMutationNumber4552 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3912_failAssert0null5975_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3912 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3912_failAssert0null5975 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3822_failAssert0_add5348_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3822_failAssert0_add5348 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3828_failAssert0_literalMutationString4587_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, -1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3828_failAssert0_literalMutationString4587 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3916_failAssert0_add5420_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        comments.isEmpty();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3916 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3916_failAssert0_add5420 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
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

    public void testTemplateFunction_literalMutationString116069() throws IOException {
        MustacheFactory mf = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache m = mf.compile(new StringReader(("{{#i}}{{?{test}}}{{f}}{{/i}}" + "{{#comment}}comment{{/comment}}")), "testTemplateFunction");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("testTemplateFunction", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.execute(sw, new Object() {
            Function i = new TemplateFunction() {
                @Override
                public String apply(String s) {
                    return s.replace("test", "test2");
                }
            };

            String test2 = "test";

            Function f = ( o) -> null;

            CommentFunction comment = new CommentFunction();
        }).close();
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("testTemplateFunction", ((DefaultMustache) (m)).getName());
    }

    private static class SuperClass {
        String values = "value";
    }

    public void testIssue191_literalMutationString122360() throws IOException {
        MustacheFactory mustacheFactory = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (mustacheFactory)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (mustacheFactory)).getRecursionLimit())));
        Mustache mustache = mustacheFactory.compile("");
        TestCase.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (mustache)).getName());
        StringWriter stringWriter = new StringWriter();
        Writer o_testIssue191_literalMutationString122360__7 = mustache.execute(stringWriter, ImmutableMap.of("title", "Some title!"));
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testIssue191_literalMutationString122360__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testIssue191_literalMutationString122360__7)).toString());
        String o_testIssue191_literalMutationString122360__9 = TestUtil.getContents(root, "templates/someTemplate.txt");
        TestCase.assertEquals("<!DOCTYPE html>\n<html>\n<head>\n    <title>Some title!</title>\n</head>\n<body>\n<h1>This is mustacheee</h1>", o_testIssue191_literalMutationString122360__9);
        stringWriter.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (mustacheFactory)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (mustacheFactory)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (mustache)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testIssue191_literalMutationString122360__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testIssue191_literalMutationString122360__7)).toString());
        TestCase.assertEquals("<!DOCTYPE html>\n<html>\n<head>\n    <title>Some title!</title>\n</head>\n<body>\n<h1>This is mustacheee</h1>", o_testIssue191_literalMutationString122360__9);
    }

    public void testIssue191_literalMutationString122360_add123640() throws IOException {
        MustacheFactory mustacheFactory = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (mustacheFactory)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (mustacheFactory)).getExecutorService());
        Mustache mustache = mustacheFactory.compile("");
        TestCase.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (mustache)).getName());
        StringWriter stringWriter = new StringWriter();
        Writer o_testIssue191_literalMutationString122360_add123640__7 = mustache.execute(stringWriter, ImmutableMap.of("title", "Some title!"));
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testIssue191_literalMutationString122360_add123640__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testIssue191_literalMutationString122360_add123640__7)).toString());
        Writer o_testIssue191_literalMutationString122360__7 = mustache.execute(stringWriter, ImmutableMap.of("title", "Some title!"));
        String o_testIssue191_literalMutationString122360__9 = TestUtil.getContents(root, "templates/someTemplate.txt");
        TestCase.assertEquals("<!DOCTYPE html>\n<html>\n<head>\n    <title>Some title!</title>\n</head>\n<body>\n<h1>This is mustacheee</h1>", o_testIssue191_literalMutationString122360__9);
        stringWriter.toString();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (mustacheFactory)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (mustacheFactory)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (mustache)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (mustache)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testIssue191_literalMutationString122360_add123640__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiffext.mustache.child\ndiffext.mustache.grandchild\ndiffext.mustache.parent\ndiffext.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nissue_201\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testIssue191_literalMutationString122360_add123640__7)).toString());
        TestCase.assertEquals("<!DOCTYPE html>\n<html>\n<head>\n    <title>Some title!</title>\n</head>\n<body>\n<h1>This is mustacheee</h1>", o_testIssue191_literalMutationString122360__9);
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

