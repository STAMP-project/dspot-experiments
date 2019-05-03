package com.github.mustachejava;


import com.github.mustachejava.codes.DefaultMustache;
import com.github.mustachejava.reflect.SimpleObjectHandler;
import com.github.mustachejava.resolver.DefaultResolver;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import junit.framework.TestCase;


@SuppressWarnings("unused")
public class AmplInterpreterTest extends TestCase {
    protected File root;

    public void testSimple_literalMutationString2() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSimple_literalMutationString2__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString2__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString2__7)).toString());
        String o_testSimple_literalMutationString2__14 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString2__14);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString2__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString2__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString2__14);
    }

    public void testSimple_literalMutationString366968() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSimple_literalMutationString366968__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString366968__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString366968__7)).toString());
        String o_testSimple_literalMutationString366968__14 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString366968__14);
        sw.toString();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString366968__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString366968__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString366968__14);
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

    public void testSimpleI18N_literalMutationString135866() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("simple.html");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString135866__9 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString135866__9)).getBuffer())).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString135866__9)).toString());
            String o_testSimpleI18N_literalMutationString135866__16 = TestUtil.getContents(root, "simple_ko.txt");
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString135866__16);
            sw.toString();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString135866__9)).getBuffer())).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString135866__9)).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString135866__16);
        }
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString135866__26 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString135866__26)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString135866__26)).toString());
            String o_testSimpleI18N_literalMutationString135866__33 = TestUtil.getContents(this.root, "simple.txt");
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString135866__33);
            sw.toString();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString135866__26)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString135866__26)).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString135866__33);
        }
    }

    public void testSimpleI18N_literalMutationString135835() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString135835__9 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString135835__9)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString135835__9)).toString());
            String o_testSimpleI18N_literalMutationString135835__16 = TestUtil.getContents(root, "simple_ko.txt");
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString135835__16);
            sw.toString();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString135835__9)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString135835__9)).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString135835__16);
        }
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("simple.html");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString135835__26 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString135835__26)).getBuffer())).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString135835__26)).toString());
            String o_testSimpleI18N_literalMutationString135835__33 = TestUtil.getContents(this.root, "simple.txt");
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString135835__33);
            sw.toString();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString135835__26)).getBuffer())).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString135835__26)).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString135835__33);
        }
    }

    private DefaultMustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory(root);
    }

    public void testRecurision_literalMutationString383416_add383738() throws IOException {
        StringWriter o_testRecurision_literalMutationString383416_add383738__1 = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecurision_literalMutationString383416_add383738__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecurision_literalMutationString383416_add383738__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        String o_testRecurision_literalMutationString383416__11 = TestUtil.getContents(root, "recursion.txt");
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString383416__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecurision_literalMutationString383416_add383738__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecurision_literalMutationString383416_add383738__1)).toString());
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString383416__11);
    }

    public void testRecurision_literalMutationString383416_add383740_add387001() throws IOException {
        StringWriter o_testRecurision_literalMutationString383416_add383740_add387001__1 = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecurision_literalMutationString383416_add383740_add387001__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecurision_literalMutationString383416_add383740_add387001__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        String o_testRecurision_literalMutationString383416__11 = TestUtil.getContents(root, "recursion.txt");
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString383416__11);
        sw.toString();
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecurision_literalMutationString383416_add383740_add387001__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecurision_literalMutationString383416_add383740_add387001__1)).toString());
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString383416__11);
    }

    public void testRecursionWithInheritance_literalMutationString651376_add651697() throws IOException {
        StringWriter o_testRecursionWithInheritance_literalMutationString651376_add651697__1 = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651697__1)).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651697__1)).getBuffer())).toString());
        StringWriter sw = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        String o_testRecursionWithInheritance_literalMutationString651376__11 = TestUtil.getContents(root, "recursion.txt");
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString651376__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651697__1)).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651697__1)).getBuffer())).toString());
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString651376__11);
    }

    public void testRecursionWithInheritance_literalMutationString651376_add651699_add654855() throws IOException {
        StringWriter o_testRecursionWithInheritance_literalMutationString651376_add651699_add654855__1 = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651699_add654855__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651699_add654855__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        String o_testRecursionWithInheritance_literalMutationString651376__11 = TestUtil.getContents(root, "recursion.txt");
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString651376__11);
        sw.toString();
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651699_add654855__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_add651699_add654855__1)).toString());
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString651376__11);
    }

    public void testRecursionWithInheritance_literalMutationString651376_remove651782_add654990() throws IOException {
        StringWriter o_testRecursionWithInheritance_literalMutationString651376_remove651782_add654990__1 = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_remove651782_add654990__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_remove651782_add654990__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        String o_testRecursionWithInheritance_literalMutationString651376__11 = TestUtil.getContents(this.root, "recursion.txt");
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString651376__11);
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_remove651782_add654990__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecursionWithInheritance_literalMutationString651376_remove651782_add654990__1)).toString());
    }

    public void testPartialRecursionWithInheritance_literalMutationString378072_add378393() throws IOException {
        StringWriter o_testPartialRecursionWithInheritance_literalMutationString378072_add378393__1 = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString378072_add378393__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString378072_add378393__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        String o_testPartialRecursionWithInheritance_literalMutationString378072__11 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
        TestCase.assertEquals("TEST\n  TEST\n\n\n", o_testPartialRecursionWithInheritance_literalMutationString378072__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString378072_add378393__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString378072_add378393__1)).toString());
        TestCase.assertEquals("TEST\n  TEST\n\n\n", o_testPartialRecursionWithInheritance_literalMutationString378072__11);
    }

    public void testChainedInheritance_literalMutationString568044_add568365() throws IOException {
        StringWriter o_testChainedInheritance_literalMutationString568044_add568365__1 = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testChainedInheritance_literalMutationString568044_add568365__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testChainedInheritance_literalMutationString568044_add568365__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        String o_testChainedInheritance_literalMutationString568044__11 = TestUtil.getContents(root, "page.txt");
        TestCase.assertEquals("<main id=\"content\" role=\"main\">\n  This is the page content\n\n</main>\n", o_testChainedInheritance_literalMutationString568044__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testChainedInheritance_literalMutationString568044_add568365__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testChainedInheritance_literalMutationString568044_add568365__1)).toString());
        TestCase.assertEquals("<main id=\"content\" role=\"main\">\n  This is the page content\n\n</main>\n", o_testChainedInheritance_literalMutationString568044__11);
    }

    public void testSimplePragma_literalMutationString421691() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSimplePragma_literalMutationString421691__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimplePragma_literalMutationString421691__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimplePragma_literalMutationString421691__7)).toString());
        String o_testSimplePragma_literalMutationString421691__14 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString421691__14);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimplePragma_literalMutationString421691__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimplePragma_literalMutationString421691__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString421691__14);
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

    public void testMultipleWrappers_literalMutationString513621() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testMultipleWrappers_literalMutationString513621__7 = m.execute(sw, new Object() {
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
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleWrappers_literalMutationString513621__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleWrappers_literalMutationString513621__7)).toString());
        String o_testMultipleWrappers_literalMutationString513621__23 = TestUtil.getContents(root, "simplerewrap.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString513621__23);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleWrappers_literalMutationString513621__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleWrappers_literalMutationString513621__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString513621__23);
    }

    public void testBrokenSimple_literalMutationNumber357668_literalMutationString358352_literalMutationString359011() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8 = m.execute(sw, new Object() {
                String name = "ChFis";

                int value = 10001;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String String_249 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_249);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357664_literalMutationString357957_remove366200() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357664_literalMutationString357957__8 = m.execute(sw, new Object() {
                String name = "Chr is";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String String_278 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_278);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357823_remove366196() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357823__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 5000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String String_258 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_258);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357819_literalMutationString359176() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8 = m.execute(sw, new Object() {
                String name = "Chrs";

                int value = 0;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String String_314 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_314);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357819() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 0;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8)).toString());
            String String_314 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_314);
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationBoolean357678_literalMutationString357907() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = false;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8)).toString());
            String String_315 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_315);
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationNumber357668_literalMutationString358352() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10001;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8)).toString());
            String String_249 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_249);
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationString357816_remove366194() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357654_literalMutationString357816__8 = m.execute(sw, new Object() {
                String name = "Z?C+*";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String String_190 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_190);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationString357813_remove366206() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357654_literalMutationString357813__8 = m.execute(sw, new Object() {
                String name = "";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String String_345 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_345);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationNumber357668_literalMutationString358352_add365025() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            DefaultMustacheFactory o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352_add365025__2 = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352_add365025__2)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352_add365025__2)).getRecursionLimit())));
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10001;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String String_249 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_249);
            TestCase.assertNull(((DefaultMustacheFactory) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352_add365025__2)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352_add365025__2)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357823() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357823__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 5000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357823__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357823__8)).toString());
            String String_258 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_258);
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357823__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357823__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357819_add365155() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 0;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String String_314 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_314);
            o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8.toString();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_314);
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357828() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357828__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.0)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357828__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357828__8)).toString());
            String String_259 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_259);
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357828__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationNumber357828__8)).toString());
        }
    }

    public void testBrokenSimple_add357679_literalMutationString358477_remove366199() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            DefaultMustacheFactory o_testBrokenSimple_add357679_literalMutationString358477__2 = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getRecursionLimit())));
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_add357679_literalMutationString358477__9 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String String_265 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_265);
            TestCase.assertNull(((DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357659_literalMutationString357982() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357659_literalMutationString357982__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357659_literalMutationString357982__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357659_literalMutationString357982__8)).toString());
            String String_262 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_262);
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357659_literalMutationString357982__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357659_literalMutationString357982__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationNumber357674_literalMutationString358303() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationNumber357674_literalMutationString358303__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.0)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationNumber357674_literalMutationString358303__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationNumber357674_literalMutationString358303__8)).toString());
            String String_327 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_327);
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationNumber357674_literalMutationString358303__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationNumber357674_literalMutationString358303__8)).toString());
        }
    }

    public void testBrokenSimple_add357679_literalMutationString358477() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            DefaultMustacheFactory o_testBrokenSimple_add357679_literalMutationString358477__2 = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getExecutorService());
            MustacheFactory c = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_add357679_literalMutationString358477__9 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_add357679_literalMutationString358477__9)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_add357679_literalMutationString358477__9)).toString());
            String String_265 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_265);
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (o_testBrokenSimple_add357679_literalMutationString358477__2)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_add357679_literalMutationString358477__9)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_add357679_literalMutationString358477__9)).toString());
        }
    }

    public void testBrokenSimple_add357682_literalMutationString358451_remove366204() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_add357682_literalMutationString358451__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            sw.toString();
            String String_330 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_330);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationString357813() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357654_literalMutationString357813__8 = m.execute(sw, new Object() {
                String name = "";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357813__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357813__8)).toString());
            String String_345 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_345);
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357813__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357813__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationString357816() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357654_literalMutationString357816__8 = m.execute(sw, new Object() {
                String name = "Z?C+*";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357816__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357816__8)).toString());
            String String_190 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_190);
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357816__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357654_literalMutationString357816__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationBoolean357678_literalMutationString357907_remove366202() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = false;
            });
            String String_315 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_315);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357659_literalMutationString357982_remove366198() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357659_literalMutationString357982__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String String_262 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_262);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357828_remove366197() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357828__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.0)));
                }

                boolean in_ca = true;
            });
            String String_259 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_259);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationString357654_literalMutationNumber357819_remove366201() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357654_literalMutationNumber357819__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 0;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String String_314 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_314);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationBoolean357678_literalMutationString357907_literalMutationNumber359210() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 5000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = false;
            });
            String String_315 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_315);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationNumber357668_literalMutationString358352_remove366195() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationNumber357668_literalMutationString358352__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10001;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String String_249 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_249);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_literalMutationBoolean357678_literalMutationString357907_add365165() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = false;
            });
            o_testBrokenSimple_literalMutationBoolean357678_literalMutationString357907__8.toString();
            String String_315 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_315);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testBrokenSimple_add357682_literalMutationString358451() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_add357682_literalMutationString358451__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_add357682_literalMutationString358451__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_add357682_literalMutationString358451__8)).toString());
            sw.toString();
            String String_330 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_330);
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_add357682_literalMutationString358451__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_add357682_literalMutationString358451__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString357664_literalMutationString357957() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString357664_literalMutationString357957__8 = m.execute(sw, new Object() {
                String name = "Chr is";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357664_literalMutationString357957__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357664_literalMutationString357957__8)).toString());
            String String_278 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_278);
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString357664_literalMutationString357957__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString357664_literalMutationString357957__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationNumber357674_literalMutationString358303_remove366203() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationNumber357674_literalMutationString358303__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.0)));
                }

                boolean in_ca = true;
            });
            String String_327 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_327);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        }
    }

    public void testIsNotEmpty_literalMutationString174270_remove174986_add179436() throws IOException {
        Object object = new Object() {
            List<String> people = Collections.singletonList("Test");
        };
        StringWriter o_testIsNotEmpty_literalMutationString174270_remove174986_add179436__7 = execute("", object);
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testIsNotEmpty_literalMutationString174270_remove174986_add179436__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testIsNotEmpty_literalMutationString174270_remove174986_add179436__7)).toString());
        StringWriter sw = execute("", object);
        String o_testIsNotEmpty_literalMutationString174270__9 = TestUtil.getContents(this.root, "isempty.txt");
        TestCase.assertEquals("Is not empty\n", o_testIsNotEmpty_literalMutationString174270__9);
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testIsNotEmpty_literalMutationString174270_remove174986_add179436__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testIsNotEmpty_literalMutationString174270_remove174986_add179436__7)).toString());
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

    public void testImmutableList_literalMutationString280187_add280823() throws IOException {
        Object object = new Object() {
            List<String> people = Collections.singletonList("Test");
        };
        StringWriter o_testImmutableList_literalMutationString280187_add280823__7 = execute("", Collections.singletonList(object));
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testImmutableList_literalMutationString280187_add280823__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testImmutableList_literalMutationString280187_add280823__7)).toString());
        StringWriter sw = execute("", Collections.singletonList(object));
        String o_testImmutableList_literalMutationString280187__10 = TestUtil.getContents(root, "isempty.txt");
        TestCase.assertEquals("Is not empty\n", o_testImmutableList_literalMutationString280187__10);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testImmutableList_literalMutationString280187_add280823__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testImmutableList_literalMutationString280187_add280823__7)).toString());
        TestCase.assertEquals("Is not empty\n", o_testImmutableList_literalMutationString280187__10);
    }

    public void testImmutableList_literalMutationString280187_literalMutationString280399_add285347() throws IOException {
        Object object = new Object() {
            List<String> people = Collections.singletonList("Tst");
        };
        StringWriter o_testImmutableList_literalMutationString280187_literalMutationString280399_add285347__7 = execute("", Collections.singletonList(object));
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testImmutableList_literalMutationString280187_literalMutationString280399_add285347__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testImmutableList_literalMutationString280187_literalMutationString280399_add285347__7)).toString());
        StringWriter sw = execute("", Collections.singletonList(object));
        String o_testImmutableList_literalMutationString280187__10 = TestUtil.getContents(root, "isempty.txt");
        TestCase.assertEquals("Is not empty\n", o_testImmutableList_literalMutationString280187__10);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testImmutableList_literalMutationString280187_literalMutationString280399_add285347__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testImmutableList_literalMutationString280187_literalMutationString280399_add285347__7)).toString());
        TestCase.assertEquals("Is not empty\n", o_testImmutableList_literalMutationString280187__10);
    }

    public void testSecurity_literalMutationString447137() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSecurity_literalMutationString447137__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;

            private String test = "Test";
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSecurity_literalMutationString447137__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSecurity_literalMutationString447137__7)).toString());
        String o_testSecurity_literalMutationString447137__15 = TestUtil.getContents(root, "security.txt");
        TestCase.assertEquals("", o_testSecurity_literalMutationString447137__15);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSecurity_literalMutationString447137__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSecurity_literalMutationString447137__7)).toString());
        TestCase.assertEquals("", o_testSecurity_literalMutationString447137__15);
    }

    public void testIdentitySimple_literalMutationString603279_add604862() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache o_testIdentitySimple_literalMutationString603279_add604862__3 = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (o_testIdentitySimple_literalMutationString603279_add604862__3)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (o_testIdentitySimple_literalMutationString603279_add604862__3)).getName());
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString603279__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603279__8);
        String o_testIdentitySimple_literalMutationString603279__10 = sw.toString().replaceAll("\\s+", "");
        TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString603279__10);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (o_testIdentitySimple_literalMutationString603279_add604862__3)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (o_testIdentitySimple_literalMutationString603279_add604862__3)).getName());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603279__8);
    }

    public void testIdentitySimple_literalMutationString603279() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString603279__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603279__8);
        String o_testIdentitySimple_literalMutationString603279__10 = sw.toString().replaceAll("\\s+", "");
        TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString603279__10);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603279__8);
    }

    public void testIdentitySimple_literalMutationString603294_add604835_literalMutationString607008() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString603294__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\bs+", "");
        TestCase.assertEquals("Hello {{name}}\n  You have just won ${{value}}!\n\n        {{#test}}\n        {{/test}}\n{{#in_ca}}\nWell, ${{ taxed_value }},  after taxes.{{fred}}\n{{/in_ca}}", o_testIdentitySimple_literalMutationString603294__8);
        String o_testIdentitySimple_literalMutationString603294__10 = sw.toString().replaceAll("\\s+", "");
        TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.htmlbox.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString603294__10);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Hello {{name}}\n  You have just won ${{value}}!\n\n        {{#test}}\n        {{/test}}\n{{#in_ca}}\nWell, ${{ taxed_value }},  after taxes.{{fred}}\n{{/in_ca}}", o_testIdentitySimple_literalMutationString603294__8);
    }

    public void testIdentitySimple_literalMutationString603305_add605027_literalMutationString606662() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache o_testIdentitySimple_literalMutationString603305_add605027__3 = c.compile("simple.html");
        TestCase.assertFalse(((DefaultMustache) (o_testIdentitySimple_literalMutationString603305_add605027__3)).isRecursive());
        TestCase.assertEquals("simple.html", ((DefaultMustache) (o_testIdentitySimple_literalMutationString603305_add605027__3)).getName());
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString603305__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603305__8);
        String o_testIdentitySimple_literalMutationString603305__10 = sw.toString().replaceAll("\\+", "");
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", o_testIdentitySimple_literalMutationString603305__10);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (o_testIdentitySimple_literalMutationString603305_add605027__3)).isRecursive());
        TestCase.assertEquals("simple.html", ((DefaultMustache) (o_testIdentitySimple_literalMutationString603305_add605027__3)).getName());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603305__8);
    }

    public void testIdentitySimple_literalMutationString603306_literalMutationString603767() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString603306__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603306__8);
        String o_testIdentitySimple_literalMutationString603306__10 = sw.toString().replaceAll("\\s+", "");
        TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString603306__10);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString603306__8);
    }

    public void testIdentitySimple_literalMutationString603279_literalMutationString603610() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString603279__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "D");
        TestCase.assertEquals("HelloD{{name}}DYouDhaveDjustDwonD${{value}}!D{{#test}}D{{/test}}D{{#in_ca}}DWell,D${{Dtaxed_valueD}},DafterDtaxes.{{fred}}D{{/in_ca}}", o_testIdentitySimple_literalMutationString603279__8);
        String o_testIdentitySimple_literalMutationString603279__10 = sw.toString().replaceAll("\\s+", "");
        TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString603279__10);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("HelloD{{name}}DYouDhaveDjustDwonD${{value}}!D{{#test}}D{{/test}}D{{#in_ca}}DWell,D${{Dtaxed_valueD}},DafterDtaxes.{{fred}}D{{/in_ca}}", o_testIdentitySimple_literalMutationString603279__8);
    }

    public void testProperties_literalMutationString501845() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testProperties_literalMutationString501845__7 = m.execute(sw, new Object() {
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
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testProperties_literalMutationString501845__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testProperties_literalMutationString501845__7)).toString());
        String o_testProperties_literalMutationString501845__22 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString501845__22);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testProperties_literalMutationString501845__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testProperties_literalMutationString501845__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString501845__22);
    }

    public void testSimpleWithMap_literalMutationString113226_literalMutationString114118_add131714() throws MustacheException, IOException, InterruptedException, ExecutionException {
        StringWriter o_testSimpleWithMap_literalMutationString113226_literalMutationString114118_add131714__1 = execute("", new HashMap<String, Object>() {
            {
                Object o_testSimpleWithMap_literalMutationString113226__8 = put("name", "Chris");
                Object o_testSimpleWithMap_literalMutationString113226__9 = put("", 10000);
                Object o_testSimpleWithMap_literalMutationString113226__10 = put("taxed_value", 6000);
                Object o_testSimpleWithMap_literalMutationString113226__11 = put("in_ca", true);
            }
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleWithMap_literalMutationString113226_literalMutationString114118_add131714__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleWithMap_literalMutationString113226_literalMutationString114118_add131714__1)).toString());
        StringWriter sw = execute("", new HashMap<String, Object>() {
            {
                Object o_testSimpleWithMap_literalMutationString113226__8 = put("name", "Chris");
                Object o_testSimpleWithMap_literalMutationString113226__9 = put("", 10000);
                Object o_testSimpleWithMap_literalMutationString113226__10 = put("taxed_value", 6000);
                Object o_testSimpleWithMap_literalMutationString113226__11 = put("in_ca", true);
            }
        });
        String o_testSimpleWithMap_literalMutationString113226__12 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleWithMap_literalMutationString113226__12);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleWithMap_literalMutationString113226_literalMutationString114118_add131714__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleWithMap_literalMutationString113226_literalMutationString114118_add131714__1)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleWithMap_literalMutationString113226__12);
    }

    public void testPartialWithTF_literalMutationString388818() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testPartialWithTF_literalMutationString388818__7 = m.execute(sw, new Object() {
            public TemplateFunction i() {
                return ( s) -> s;
            }
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString388818__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString388818__7)).toString());
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString388818__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString388818__7)).toString());
    }

    public void testPartialWithTF_literalMutationString388818_add388954() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testPartialWithTF_literalMutationString388818_add388954__7 = m.execute(sw, new Object() {
            public TemplateFunction i() {
                return ( s) -> s;
            }
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString388818_add388954__7)).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString388818_add388954__7)).getBuffer())).toString());
        Writer o_testPartialWithTF_literalMutationString388818__7 = m.execute(sw, new Object() {
            public TemplateFunction i() {
                return ( s) -> s;
            }
        });
        sw.toString();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString388818_add388954__7)).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString388818_add388954__7)).getBuffer())).toString());
    }

    public void testPartialWithTF_remove388829_literalMutationString388900_add391181() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testPartialWithTF_remove388829_literalMutationString388900_add391181__7 = m.execute(sw, new Object() {
            public TemplateFunction i() {
                return ( s) -> s;
            }
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_remove388829_literalMutationString388900_add391181__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_remove388829_literalMutationString388900_add391181__7)).toString());
        Writer o_testPartialWithTF_remove388829__7 = m.execute(sw, new Object() {
            public TemplateFunction i() {
                return ( s) -> s;
            }
        });
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_remove388829_literalMutationString388900_add391181__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_remove388829_literalMutationString388900_add391181__7)).toString());
    }

    public void testComplexParallel_literalMutationString193496_literalMutationString193572_add197292() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        Mustache m = c.compile("?");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("?", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testComplexParallel_literalMutationString193496_literalMutationString193572_add197292__7 = m.execute(sw, new ParallelComplexObject());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testComplexParallel_literalMutationString193496_literalMutationString193572_add197292__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testComplexParallel_literalMutationString193496_literalMutationString193572_add197292__7)).toString());
        m.execute(sw, new ParallelComplexObject()).close();
        String o_testComplexParallel_literalMutationString193496__10 = TestUtil.getContents(root, "complex.txt");
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testComplexParallel_literalMutationString193496__10);
        sw.toString();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("?", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testComplexParallel_literalMutationString193496_literalMutationString193572_add197292__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testComplexParallel_literalMutationString193496_literalMutationString193572_add197292__7)).toString());
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testComplexParallel_literalMutationString193496__10);
    }

    public void testSerialCallable_literalMutationString223135_add223388() throws MustacheException, IOException {
        StringWriter o_testSerialCallable_literalMutationString223135_add223388__1 = execute("", new ParallelComplexObject());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSerialCallable_literalMutationString223135_add223388__1)).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSerialCallable_literalMutationString223135_add223388__1)).getBuffer())).toString());
        StringWriter sw = execute("", new ParallelComplexObject());
        String o_testSerialCallable_literalMutationString223135__4 = TestUtil.getContents(root, "complex.txt");
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString223135__4);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSerialCallable_literalMutationString223135_add223388__1)).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSerialCallable_literalMutationString223135_add223388__1)).getBuffer())).toString());
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString223135__4);
    }

    public void testSerialCallable_literalMutationString223135_add223389_add226367() throws MustacheException, IOException {
        StringWriter o_testSerialCallable_literalMutationString223135_add223389_add226367__1 = execute("", new ParallelComplexObject());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSerialCallable_literalMutationString223135_add223389_add226367__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSerialCallable_literalMutationString223135_add223389_add226367__1)).toString());
        StringWriter sw = execute("", new ParallelComplexObject());
        String o_testSerialCallable_literalMutationString223135_add223389__4 = TestUtil.getContents(root, "complex.txt");
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString223135_add223389__4);
        String o_testSerialCallable_literalMutationString223135__4 = TestUtil.getContents(root, "complex.txt");
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString223135__4);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSerialCallable_literalMutationString223135_add223389_add226367__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSerialCallable_literalMutationString223135_add223389_add226367__1)).toString());
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString223135_add223389__4);
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString223135__4);
    }

    public void testReadme_literalMutationString83270() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadme_literalMutationString83270__9 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadme_literalMutationString83270__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadme_literalMutationString83270__9)).toString());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadme_literalMutationString83270__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString83270__13);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadme_literalMutationString83270__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadme_literalMutationString83270__9)).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString83270__13);
    }

    public void testReadmeSerial_add21832_literalMutationNumber22022_remove30539() throws MustacheException, IOException {
        DefaultMustacheFactory o_testReadmeSerial_add21832__1 = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getRecursionLimit())));
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add21832__10 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add21832__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21832__14);
        String String_44 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_44);
        boolean boolean_45 = (diff > 3999) && (diff < 3000);
        TestCase.assertNull(((DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21832__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_44);
    }

    public void testReadmeSerial_add21835_add22549_remove30546() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add21835__9 = m.execute(sw, new AmplInterpreterTest.Context());
        ((StringWriter) (o_testReadmeSerial_add21835__9)).getBuffer().toString();
        Writer o_testReadmeSerial_add21835__11 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add21835__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21835__15);
        String String_38 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 8001", String_38);
        boolean boolean_39 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21835__15);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 8001", String_38);
    }

    public void testReadmeSerial_add21834_literalMutationNumber21922_remove30536() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        System.currentTimeMillis();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add21834__10 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add21834__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21834__14);
        String String_36 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_36);
        boolean boolean_37 = (diff > 3999) && (diff < 0);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21834__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_36);
    }

    public void testReadmeSerial_add21832_literalMutationNumber22022() throws MustacheException, IOException {
        DefaultMustacheFactory o_testReadmeSerial_add21832__1 = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getExecutorService());
        MustacheFactory c = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add21832__10 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add21832__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21832__14);
        sw.toString();
        String String_44 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_44);
        boolean boolean_45 = (diff > 3999) && (diff < 3000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (o_testReadmeSerial_add21832__1)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21832__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_44);
    }

    public void testReadmeSerial_add21835_literalMutationString21944_add29077() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add21835__9 = m.execute(sw, new AmplInterpreterTest.Context());
        Writer o_testReadmeSerial_add21835__11 = m.execute(sw, new AmplInterpreterTest.Context());
        System.currentTimeMillis();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add21835__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21835__15);
        sw.toString();
        String String_38 = "]OyQrTEs{etwelU_f0.in76])=&7.4.F[h_S3n]kc@-9" + diff;
        TestCase.assertEquals("]OyQrTEs{etwelU_f0.in76])=&7.4.F[h_S3n]kc@-98001", String_38);
        boolean boolean_39 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add21835__15);
        TestCase.assertEquals("]OyQrTEs{etwelU_f0.in76])=&7.4.F[h_S3n]kc@-98001", String_38);
    }

    public void testReadmeParallel_add573404_add574319_remove582550() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        System.currentTimeMillis();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add573404__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573404__15);
        sw.toString();
        String String_395 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_395);
        boolean boolean_396 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573404__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_395);
    }

    public void testReadmeParallel_add573409_remove574513() throws MustacheException, IOException {
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
        long diff = (System.currentTimeMillis()) - start;
        sw.toString();
        String o_testReadmeParallel_add573409__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573409__15);
        String String_393 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_393);
        boolean boolean_394 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573409__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_393);
    }

    public void testReadmeParallel_add573409_add574299() throws MustacheException, IOException {
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
        long diff = (System.currentTimeMillis()) - start;
        sw.toString();
        sw.toString();
        sw.toString();
        String o_testReadmeParallel_add573409__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573409__15);
        String String_393 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_393);
        boolean boolean_394 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573409__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_393);
    }

    public void testReadmeParallel_add573406_add574207() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache o_testReadmeParallel_add573406_add574207__3 = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeParallel_add573406_add574207__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeParallel_add573406_add574207__3)).getName());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context());
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add573406__16 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573406__16);
        sw.toString();
        String String_387 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_387);
        boolean boolean_388 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeParallel_add573406_add574207__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeParallel_add573406_add574207__3)).getName());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add573406__16);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_387);
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

    public void testRelativePathsTemplateFunction_literalMutationString465035_add465408_add468367() throws IOException {
        MustacheFactory mf = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        Mustache compile = mf.compile("");
        TestCase.assertFalse(((DefaultMustache) (compile)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (compile)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testRelativePathsTemplateFunction_literalMutationString465035_add465408_add468367__7 = compile.execute(sw, new Object() {
            Function i = new TemplateFunction() {
                @Override
                public String apply(String s) {
                    return s;
                }
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRelativePathsTemplateFunction_literalMutationString465035_add465408_add468367__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRelativePathsTemplateFunction_literalMutationString465035_add465408_add468367__7)).toString());
        compile.execute(sw, new Object() {
            Function i = new TemplateFunction() {
                @Override
                public String apply(String s) {
                    return s;
                }
            };
        }).close();
        String o_testRelativePathsTemplateFunction_literalMutationString465035__19 = TestUtil.getContents(root, "relative/paths.txt");
        TestCase.assertEquals("test", o_testRelativePathsTemplateFunction_literalMutationString465035__19);
        sw.toString();
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (compile)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (compile)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRelativePathsTemplateFunction_literalMutationString465035_add465408_add468367__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRelativePathsTemplateFunction_literalMutationString465035_add465408_add468367__7)).toString());
        TestCase.assertEquals("test", o_testRelativePathsTemplateFunction_literalMutationString465035__19);
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

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInv&alidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInv&alidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0_literalMutationString404993_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), " does not exist");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0_literalMutationString404993 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[ does not exist:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0_literalMutationString404291_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimters");
                        Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0_literalMutationString404291 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402492_failAssert0null406351_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402492 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402492_failAssert0null406351 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0_add406018_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "_8l#Yri.a;]#NdGCH[{wuF");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0_add406018 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[_8l#Yri.a;]#NdGCH[{wuF:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0null402650_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0null402650 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0_literalMutationString403532_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tooloPng}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0_literalMutationString403532 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooloPng @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0_add406049_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolonfg}}"), "testInvalidNelimiters");
                        Mustache m = mf.compile(new StringReader("{{=toolonfg}}"), "testInvalidNelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0_add406049 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolonfg @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0_literalMutationString404191_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0_literalMutationString404191 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0null406353_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0null406353 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_add402625_failAssert0null406336_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402625 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402625_failAssert0null406336 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_add406146_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimitrs");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_add406146 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidNelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_add406147_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_add406147 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0null406319_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0null406319 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_add406145_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_add406145 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0null406283_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolon#g}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0null406283 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolon#g @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0null406318_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=tRoolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0null406318 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0null402644_failAssert0_add405727_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0null402644 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0null402644_failAssert0_add405727 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0_literalMutationString403740_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0_literalMutationString403740 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull402363_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull402363 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470_failAssert0_add406025_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimitekrs");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimitekrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470_failAssert0_add406025 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitekrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0null402650_failAssert0_add405765_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tooong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0null402650 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0null402650_failAssert0_add405765 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon#g}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolon#g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402606_failAssert0_add405944_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402606 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402606_failAssert0_add405944 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong} @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402652_failAssert0null406259_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652_failAssert0null406259 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0null402643_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0null402643 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402547_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402547 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0_add405933_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=boolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=boolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0_add405933 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0null402650_failAssert0_add405763_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tooong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0null402650 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0null402650_failAssert0_add405763 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0_literalMutationString404869_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0_literalMutationString404869 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong} @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0_add405932_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=boolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0_add405932 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0null406261_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tooCong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0null406261 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooCong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402492_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "Jpj:X8rOcm62BQ_eh)i[U");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402492 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:X8rOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0null406305_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0null406305 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402491_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402491 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402483_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "}3&Qjleqn|WheGP z0W%");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402483 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[}3&Qjleqn|WheGP z0W%:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402490_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402490 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0null402642_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0null402642 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0_literalMutationString405117_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInv&alidDelqmitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0_literalMutationString405117 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInv&alidDelqmitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402653_failAssert0_add405758_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653_failAssert0_add405758 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0_add406116_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "w");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0_add406116 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[w:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402623_failAssert0null406314_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=boolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=boolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402623 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402623_failAssert0null406314 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_add402628_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_add402628 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_add402614_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_add402614 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_add402637_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_add402637 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402485_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolon}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402485 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolon @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402446_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "BUrn8m}]m[!>lQg5Ukh*E");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402446 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[BUrn8m}]m[!>lQg5Ukh*E:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_add402619_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidNelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402619 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_add402635_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_add402635 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402444_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402444 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull402363_failAssert0_add402603_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull402363 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull402363_failAssert0_add402603 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402556_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("9*5)@^/3]>3$"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402556 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0_add406093_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0_add406093 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong} @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_add402611_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402611 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402458_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "pkage1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402458 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[pkage1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402558_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{E{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402558 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0_add405731_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0_add405731 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402563_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInv?lidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402563 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInv?lidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_add406034_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvaliDelimitrs");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_add406034 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402452_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolog}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402452 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolog @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_add402634_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_add402634 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_add402632_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_add402632 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402450_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolo|g}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402450 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolo|g @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402454_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "pQge1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402454 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[pQge1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull402363_failAssert0_add402601_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull402363 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull402363_failAssert0_add402601 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_add405767_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=tooCong}}"), "testInvaliGdDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=tooCong}}"), "testInvaliGdDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_add405767 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooCong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0_add406102_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("kQ:(qTuJ ;4c"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0_add406102 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0null406368_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("kQ:(qTuJ ;4c"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0null406368 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402525_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=totolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402525 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =totolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402538_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvaldDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402538 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvaldDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0null406365_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402546_failAssert0null406365 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong} @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_add402620_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidNelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidNelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402620 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402523_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=oolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402523 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =oolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0null402646_failAssert0_literalMutationString403527_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=too{long}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0null402646 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0null402646_failAssert0_literalMutationString403527 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =too{long @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402506_failAssert0null406390_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402506 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402506_failAssert0null406390 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_literalMutationString404673_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliDelimBitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_literalMutationString404673 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliDelimBitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0_literalMutationString404712_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toIlong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0_literalMutationString404712 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toIlong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_add402608_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_add402608 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0_add405737_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0_add405737 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402449_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=to[olong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402449 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =to[olong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402518_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=boolong}}"), "testInvalidDelimters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402518 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402516_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=boolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402516 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0null406308_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0null406308 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402514_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=boolong}}"), "teptInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402514 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[teptInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402478_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402478 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402551_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402551 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0null406374_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0null406374 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0null402643_failAssert0_add405747_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0null402643 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0null402643_failAssert0_add405747 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0_add406150_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInv&alidDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0_add406150 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInv&alidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402466_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402466 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402536_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402536 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0_add405845_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolon#g}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0_add405845 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolon#g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0_add405882_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0_add405882 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooCong}}"), "testInvaliGdDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooCong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_literalMutationString403608_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tooCoing}}"), "testInvaliGdDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_literalMutationString403608 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooCoing @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=boolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402471_failAssert0_literalMutationString405362_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402471 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402471_failAssert0_literalMutationString405362 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "_8l#Yri.a;]#NdGCH[{wuF");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[_8l#Yri.a;]#NdGCH[{wuF:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=boolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0_literalMutationString404907_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testYnvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("kQ:(qTuJ ;4c"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0_literalMutationString404907 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testYnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402604_failAssert0null406317_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402604 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402604_failAssert0null406317 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402653_failAssert0_literalMutationString403593_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653_failAssert0_literalMutationString403593 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0_literalMutationString403886_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tolon#g}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402543_failAssert0_literalMutationString403886 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolon#g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0_literalMutationString404969_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolo2ng}}"), "w");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0_literalMutationString404969 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolo2ng @[w:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402606_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402606 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402471_failAssert0null406408_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402471 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402471_failAssert0null406408 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0_literalMutationString404607_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=Otoolong}}"), "_8l#Yri.a;]#NdGCH[{wuF");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0_literalMutationString404607 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Otoolong @[_8l#Yri.a;]#NdGCH[{wuF:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0_add405919_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidNelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0_add405919 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "w");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402528 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[w:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull402363_failAssert0_literalMutationString402434_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Xtoolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull402363 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull402363_failAssert0_literalMutationString402434 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Xtoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0null402646_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0null402646 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_add402626_failAssert0null406337_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402626 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402626_failAssert0null406337 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402604_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402604 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull402363_failAssert0_literalMutationString402434_failAssert0_add405823_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=Xtoolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimitersnull402363 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull402363_failAssert0_literalMutationString402434 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull402363_failAssert0_literalMutationString402434_failAssert0_add405823 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Xtoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("kQ:(qTuJ ;4c"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_literalMutationString405101_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvxliGdDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_literalMutationString405101 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvxliGdDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0_literalMutationString404266_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=boolong}}"), "teIstInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0_literalMutationString404266 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[teIstInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0_add406047_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0_add406047 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0null402644_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0null402644 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0_add405939_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402605_failAssert0_add405939 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimitekrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitekrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0_add405801_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0_add405801 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_literalMutationString405102_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimitars");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_literalMutationString405102 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimitars:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402653_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0null406272_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0null406272 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_literalMutationString405103_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0_literalMutationString405103 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402652_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), null);
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0null406297_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0null406297 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolonfg}}"), "testInvalidNelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolonfg @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402502 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402507_failAssert0null406355_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402507 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402507_failAssert0null406355 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0null406376_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0null406376 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402504 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402503_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402503 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402653_failAssert0null406260_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402653_failAssert0null406260 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_add402627_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402627 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402507_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInva`lidNelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402507 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInva`lidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402623_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=boolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=boolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402623 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402506_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "]u=+CGP1Vio&z8?jBi Tq");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402506 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[]u=+CGP1Vio&z8?jBi Tq:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402624_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=boolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402624 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470_failAssert0null406346_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402470_failAssert0null406346 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_add402625_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402625 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_add402626_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402626 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_add402615_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_add402615 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0_literalMutationString404730_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolonfg}}"), "testInvaldNelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0_literalMutationString404730 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolonfg @[testInvaldNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402623_failAssert0null406313_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=boolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=boolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402623 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402623_failAssert0null406313 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0null406306_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0null406306 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_add402613_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_add402613 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402447_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402447 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_add402629_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=tooong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_add402629 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0_literalMutationString404136_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "Jpj]XhrOcm62BQ_eh)i[U");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0_literalMutationString404136 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj]XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0_literalMutationString404166_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                        Mustache m = mf.compile(new StringReader("{{=tRolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0_literalMutationString404166 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402443_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "test]InvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402443 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[test]InvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402549_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testIn]validDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402549 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testIn]validDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_add402636_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_add402636 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_literalMutationString404665_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testInvaliDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_literalMutationString404665 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvaliDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402486_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooloBng}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402486 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooloBng @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402483_failAssert0null406266_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402483 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402483_failAssert0null406266 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402540_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "7.Y(5wVqRGHx!>ch&bz*{");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402540 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[7.Y(5wVqRGHx!>ch&bz*{:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402459_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "pge1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402459 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[pge1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0null406386_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0null406386 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0_add406122_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402527_failAssert0_add406122 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402557_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{9=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402557 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_add402610_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402610 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402491_failAssert0null406389_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402491 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402491_failAssert0null406389 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0null406344_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0null406344 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402568_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402568 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402457_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "6_nZGj5wp");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_literalMutationString402457 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[6_nZGj5wp:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0_add406148_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInv&alidDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402482_failAssert0_add406148 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInv&alidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402553_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader(""), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402553 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_add406035_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_add406035 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_add402630_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_add402630 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402441_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tRolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_literalMutationString402441 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402624_failAssert0null406315_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=boolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402624 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402624_failAssert0null406315 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0_add405730_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0_add405730 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402652_failAssert0_literalMutationString403566_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("page1.txt"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652_failAssert0_literalMutationString403566 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402559_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402559 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_add402633_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_add402633 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0null402651_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0null402651 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402606_failAssert0null406320_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402606 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402606_failAssert0null406320 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402352_failAssert0_add402604_failAssert0_add405937_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("{{=tRoolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402604 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402352_failAssert0_add402604_failAssert0_add405937 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tRoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_add402631_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_add402631 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_add406033_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliDelimitrs");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0_add406033 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0_add405732_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0null402645_failAssert0_add405732 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402490_failAssert0null406388_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402490 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_literalMutationString402490_failAssert0null406388 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0null406349_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402480_failAssert0null406349 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402526_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_literalMutationString402526 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull402363_failAssert0_add402602_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), null);
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull402363 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull402363_failAssert0_add402602 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402535_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvalidjDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402535 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidjDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_add405766_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tooCong}}"), "testInvaliGdDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_add405766 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooCong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0_literalMutationString404038_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvaliGdDelimit*ers");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_add402612_failAssert0_literalMutationString404038 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvaliGdDelimit*ers:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_add405768_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=tooCong}}"), "testInvaliGdDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402463_failAssert0_add405768 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooCong @[testInvaliGdDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0null406369_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                        Mustache m = mf.compile(new StringReader("kQ:(qTuJ ;4c"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_literalMutationString402570_failAssert0null406369 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402539_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402539 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0null402646_failAssert0_add405733_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0null402646 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0null402646_failAssert0_add405733 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_add402609_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_add402609 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0_add402627_failAssert0null406339_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402627 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0_add402627_failAssert0null406339 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0_add405736_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0null402647_failAssert0_add405736 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402356_failAssert0_add402607_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402356_failAssert0_add402607 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402547_failAssert0null406367_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402547 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402547_failAssert0null406367 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0_add405902_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402618_failAssert0_add405902 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0_literalMutationString404144_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "]h(dWj83+273hjj*gI.(_");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0_literalMutationString404144 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[]h(dWj83+273hjj*gI.(_:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0null406307_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0null406307 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402517_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=boolong}}"), "tes[tInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402517 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[tes[tInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0null402648_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=boolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0null402648 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0null402652_failAssert0_add405752_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), null);
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0null402652_failAssert0_add405752 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402503_failAssert0null406270_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402503 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402503_failAssert0null406270 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402552_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), ":;$=&lFSYVm- S&QceXJ ");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402552 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:;$=&lFSYVm- S&QceXJ :1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402513_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=boolon}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402513 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolon @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402355_failAssert0null402649_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402355_failAssert0null402649 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0_literalMutationString402550_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimites");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361_failAssert0_literalMutationString402550 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimites:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidNelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidNelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0null406354_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolonfg}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_literalMutationString402501_failAssert0null406354 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolonfg @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0null406310_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402358_failAssert0_add402621_failAssert0null406310 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402515_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=boolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402515 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0_add406019_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "_8l#Yri.a;]#NdGCH[{wuF");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "_8l#Yri.a;]#NdGCH[{wuF");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402469_failAssert0_add406019 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[_8l#Yri.a;]#NdGCH[{wuF:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402362_failAssert0_add402638_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add402362 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402362_failAssert0_add402638 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402475_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402357_failAssert0_literalMutationString402475 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0_add405907_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402616_failAssert0_add405907 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0null406385_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402359_failAssert0_literalMutationString402467_failAssert0null406385 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0null406316_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        createMustacheFactory();
                        MustacheFactory mf = createMustacheFactory();
                        Mustache m = mf.compile(new StringReader("{{=boolong}}"), null);
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_add402622_failAssert0null406316 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0_add405911_failAssert0() throws Exception {
        try {
            {
                {
                    {
                        MustacheFactory mf = createMustacheFactory();
                        mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                        mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                        Mustache m = mf.compile(new StringReader("{{=toolong}}"), "Jpj:XhrOcm62BQ_eh)i[U");
                    }
                    junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402360_failAssert0_add402617_failAssert0_add405911 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[Jpj:XhrOcm62BQ_eh)i[U:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add402361_failAssert0() throws Exception {
        try {
            {
                createMustacheFactory();
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add402361 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402537_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvalidDelim%ters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402354_failAssert0_literalMutationString402537 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelim%ters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402511_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=boolo[g}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString402350_failAssert0_literalMutationString402511 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =boolo[g @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    private static class SuperClass {
        String values = "value";
    }

    public void testOutputDelimiters_add308805_literalMutationString309144_failAssert0() throws Exception {
        try {
            String template = "{{=## #=}}{{##={{ }}=####";
            Mustache o_testOutputDelimiters_add308805__2 = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimiters_add308805__11 = mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_add308805_literalMutationString309144 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309527_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##=}x{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309527 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##=}x{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0_literalMutationString312692_failAssert0() throws Exception {
        try {
            {
                {
                    String template = "{{=## ##=}x{{##={{ }}=###";
                    Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                    StringWriter sw = new StringWriter();
                    mustache.execute(sw, new Object[0]);
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0_literalMutationString312692 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0_add309531_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0_add309531 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0_add309530_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0_add309530 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309527_failAssert0_literalMutationNumber312722_failAssert0() throws Exception {
        try {
            {
                {
                    String template = "{{=## ##=}x{{##={{ }}=####";
                    Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                    StringWriter sw = new StringWriter();
                    mustache.execute(sw, new Object[0]);
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309527 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309527_failAssert0_literalMutationNumber312722 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_literalMutationString309226_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##=}x{{##={{ (}}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_literalMutationString309226 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{( @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationNumber308800_add309404_literalMutationString310895_failAssert0() throws Exception {
        try {
            String template = "{{=## ##=}}{{##={{ }}=m###";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            mustache.getName();
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimiters_literalMutationNumber308800__8 = mustache.execute(sw, new Object[1]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationNumber308800_add309404_literalMutationString310895 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={{}}=m @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309527_failAssert0_add314152_failAssert0() throws Exception {
        try {
            {
                {
                    String template = "{{=## ##=}x{{##={{ }}=####";
                    Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                    StringWriter sw = new StringWriter();
                    mustache.execute(sw, new Object[0]);
                    mustache.execute(sw, new Object[0]);
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309527 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309527_failAssert0_add314152 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0_literalMutationNumber309254_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0_literalMutationNumber309254 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0null309621_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(null, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0null309621 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationNumber308800null309585_literalMutationString310151_failAssert0() throws Exception {
        try {
            String template = "{{=## ##=}}{{##={{=}}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimiters_literalMutationNumber308800__8 = mustache.execute(sw, new Object[1]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationNumber308800null309585_literalMutationString310151 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={{=}}= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0_add314150_failAssert0() throws Exception {
        try {
            {
                {
                    String template = "{{=## ##=}x{{##={{ }}=####";
                    Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                    StringWriter sw = new StringWriter();
                    mustache.execute(sw, new Object[0]);
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0_add314150 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0() throws Exception {
        try {
            String template = "{{=# ##=}}{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimitersnull308810_literalMutationString308880_failAssert0() throws Exception {
        try {
            String template = "{{=## #=}}{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimitersnull308810__8 = mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimitersnull308810_literalMutationString308880 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_add308805_literalMutationString309144_failAssert0_add314037_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## #=}}{{##={{ }}=####";
                Mustache o_testOutputDelimiters_add308805__2 = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                Writer o_testOutputDelimiters_add308805__11 = mustache.execute(sw, new Object[0]);
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_add308805_literalMutationString309144 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_add308805_literalMutationString309144_failAssert0_add314037 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0_add309528_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                new DefaultMustacheFactory().compile(new StringReader(template), "test");
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0_add309528 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0null309618_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##=}x{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(null, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0null309618 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_add308806_literalMutationNumber309121_failAssert0_literalMutationString312900_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                Writer o_testOutputDelimiters_add308806__8 = mustache.execute(sw, new Object[0]);
                Writer o_testOutputDelimiters_add308806__9 = mustache.execute(sw, new Object[-1]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_add308806_literalMutationNumber309121 should have thrown NegativeArraySizeException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_add308806_literalMutationNumber309121_failAssert0_literalMutationString312900 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0() throws Exception {
        try {
            String template = "{{=## ##=}x{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_literalMutationNumber309235_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##=}x{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[-1]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_literalMutationNumber309235 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0null309620_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0null309620 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0null314814_failAssert0() throws Exception {
        try {
            {
                {
                    String template = "{{=## ##=}x{{##={{ }}=####";
                    Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                    StringWriter sw = new StringWriter();
                    mustache.execute(null, new Object[0]);
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0_add309526_failAssert0null314814 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308799_literalMutationString309090_failAssert0() throws Exception {
        try {
            String template = "{{=## ##=}}{{##=*{{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "fest");
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimiters_literalMutationString308799__8 = mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308799_literalMutationString309090 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =*{{}}= @[fest:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308792_failAssert0_literalMutationNumber309251_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[1]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308792_failAssert0_literalMutationNumber309251 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString308790_failAssert0null309617_failAssert0() throws Exception {
        try {
            {
                String template = "{{=## ##=}x{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString308790_failAssert0null309617 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=}x{{##={{ @[null:1]", expected.getMessage());
        }
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

