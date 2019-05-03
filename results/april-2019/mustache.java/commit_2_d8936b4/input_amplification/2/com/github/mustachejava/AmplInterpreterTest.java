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
import junit.framework.TestCase;


@SuppressWarnings("unused")
public class AmplInterpreterTest extends TestCase {
    protected File root;

    public void testSimple_literalMutationString102432() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSimple_literalMutationString102432__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString102432__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString102432__7)).toString());
        String o_testSimple_literalMutationString102432__14 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString102432__14);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString102432__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString102432__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString102432__14);
    }

    public void testSimple_literalMutationString3() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSimple_literalMutationString3__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString3__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString3__7)).toString());
        String o_testSimple_literalMutationString3__14 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString3__14);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString3__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString3__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString3__14);
    }

    public void testSimple_literalMutationString102432_add103991() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSimple_literalMutationString102432_add103991__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString102432_add103991__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString102432_add103991__7)).toString());
        Writer o_testSimple_literalMutationString102432__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        String o_testSimple_literalMutationString102432__14 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString102432__14);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimple_literalMutationString102432_add103991__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimple_literalMutationString102432_add103991__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimple_literalMutationString102432__14);
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

    public void testSimpleI18N_literalMutationString34202() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString34202__9 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString34202__9)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString34202__9)).toString());
            String o_testSimpleI18N_literalMutationString34202__16 = TestUtil.getContents(root, "simple_ko.txt");
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString34202__16);
            sw.toString();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString34202__9)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString34202__9)).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString34202__16);
        }
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("simple.html");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString34202__26 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString34202__26)).getBuffer())).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString34202__26)).toString());
            String o_testSimpleI18N_literalMutationString34202__33 = TestUtil.getContents(this.root, "simple.txt");
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString34202__33);
            sw.toString();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString34202__26)).getBuffer())).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString34202__26)).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString34202__33);
        }
    }

    public void testSimpleI18N_literalMutationString34233() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("simple.html");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString34233__9 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString34233__9)).getBuffer())).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString34233__9)).toString());
            String o_testSimpleI18N_literalMutationString34233__16 = TestUtil.getContents(root, "simple_ko.txt");
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString34233__16);
            sw.toString();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("simple.html", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString34233__9)).getBuffer())).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", ((StringWriter) (o_testSimpleI18N_literalMutationString34233__9)).toString());
            TestCase.assertEquals("\uc548\ub155\ud558\uc138\uc694 Chris\n\u00a0\u00a0 \ub2f9\uc2e0\uc740 10000\ub2ec\ub7ec\ub97c \uc6d0\ud588\ub2e4!\n\n\uc74c, 6000\ub2ec\ub7ec, \uc138\uae08 \ud6c4.\n", o_testSimpleI18N_literalMutationString34233__16);
        }
        {
            MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testSimpleI18N_literalMutationString34233__26 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString34233__26)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString34233__26)).toString());
            String o_testSimpleI18N_literalMutationString34233__33 = TestUtil.getContents(this.root, "simple.txt");
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString34233__33);
            sw.toString();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimpleI18N_literalMutationString34233__26)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimpleI18N_literalMutationString34233__26)).toString());
            TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimpleI18N_literalMutationString34233__33);
        }
    }

    private DefaultMustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory(root);
    }

    public void testRecurision_literalMutationString105986_add106331() throws IOException {
        StringWriter o_testRecurision_literalMutationString105986_add106331__1 = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecurision_literalMutationString105986_add106331__1)).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecurision_literalMutationString105986_add106331__1)).getBuffer())).toString());
        StringWriter sw = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        String o_testRecurision_literalMutationString105986__11 = TestUtil.getContents(root, "recursion.txt");
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString105986__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecurision_literalMutationString105986_add106331__1)).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecurision_literalMutationString105986_add106331__1)).getBuffer())).toString());
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecurision_literalMutationString105986__11);
    }

    public void testRecursionWithInheritance_literalMutationString163557_add163902() throws IOException {
        StringWriter o_testRecursionWithInheritance_literalMutationString163557_add163902__1 = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecursionWithInheritance_literalMutationString163557_add163902__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecursionWithInheritance_literalMutationString163557_add163902__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object value = new Object() {
                boolean value = false;
            };
        });
        String o_testRecursionWithInheritance_literalMutationString163557__11 = TestUtil.getContents(root, "recursion.txt");
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString163557__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRecursionWithInheritance_literalMutationString163557_add163902__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRecursionWithInheritance_literalMutationString163557_add163902__1)).toString());
        TestCase.assertEquals("Test\n  Test\n\n", o_testRecursionWithInheritance_literalMutationString163557__11);
    }

    public void testPartialRecursionWithInheritance_literalMutationString105082_add105426() throws IOException {
        StringWriter o_testPartialRecursionWithInheritance_literalMutationString105082_add105426__1 = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString105082_add105426__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString105082_add105426__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        String o_testPartialRecursionWithInheritance_literalMutationString105082__11 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
        TestCase.assertEquals("TEST\n  TEST\n\n\n", o_testPartialRecursionWithInheritance_literalMutationString105082__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString105082_add105426__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialRecursionWithInheritance_literalMutationString105082_add105426__1)).toString());
        TestCase.assertEquals("TEST\n  TEST\n\n\n", o_testPartialRecursionWithInheritance_literalMutationString105082__11);
    }

    public void testChainedInheritance_literalMutationString140752_add141101() throws IOException {
        StringWriter o_testChainedInheritance_literalMutationString140752_add141101__1 = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testChainedInheritance_literalMutationString140752_add141101__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testChainedInheritance_literalMutationString140752_add141101__1)).toString());
        StringWriter sw = execute("", new Object() {
            Object test = new Object() {
                boolean test = false;
            };
        });
        String o_testChainedInheritance_literalMutationString140752__11 = TestUtil.getContents(root, "page.txt");
        TestCase.assertEquals("<main id=\"content\" role=\"main\">\n  This is the page content\n\n</main>\n", o_testChainedInheritance_literalMutationString140752__11);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testChainedInheritance_literalMutationString140752_add141101__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testChainedInheritance_literalMutationString140752_add141101__1)).toString());
        TestCase.assertEquals("<main id=\"content\" role=\"main\">\n  This is the page content\n\n</main>\n", o_testChainedInheritance_literalMutationString140752__11);
    }

    public void testSimplePragma_literalMutationString113820() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSimplePragma_literalMutationString113820__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimplePragma_literalMutationString113820__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimplePragma_literalMutationString113820__7)).toString());
        String o_testSimplePragma_literalMutationString113820__14 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString113820__14);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSimplePragma_literalMutationString113820__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSimplePragma_literalMutationString113820__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testSimplePragma_literalMutationString113820__14);
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

    public void testMultipleWrappers_literalMutationString131761() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testMultipleWrappers_literalMutationString131761__7 = m.execute(sw, new Object() {
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
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleWrappers_literalMutationString131761__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleWrappers_literalMutationString131761__7)).toString());
        String o_testMultipleWrappers_literalMutationString131761__23 = TestUtil.getContents(root, "simplerewrap.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString131761__23);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleWrappers_literalMutationString131761__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleWrappers_literalMutationString131761__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.test\nWell, $8000,  after taxes.\nWell, $6000,  after taxes.test\n", o_testMultipleWrappers_literalMutationString131761__23);
    }

    public void testBrokenSimple_literalMutationNumber101130_literalMutationString101346() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationNumber101130_literalMutationString101346__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 5000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationNumber101130_literalMutationString101346__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationNumber101130_literalMutationString101346__8)).toString());
            String String_193 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_193);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationNumber101130_literalMutationString101346__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationNumber101130_literalMutationString101346__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString101114_literalMutationString101867() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString101114_literalMutationString101867__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationString101867__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationString101867__8)).toString());
            String String_314 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_314);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationString101867__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationString101867__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString101114_literalMutationString101872() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString101114_literalMutationString101872__8 = m.execute(sw, new Object() {
                String name = "o#LR#";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationString101872__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationString101872__8)).toString());
            String String_258 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_258);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationString101872__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationString101872__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString101114_literalMutationNumber101879() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString101114_literalMutationNumber101879__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 9999;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationNumber101879__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationNumber101879__8)).toString());
            String String_259 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_259);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationNumber101879__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationNumber101879__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString101114_literalMutationNumber101877() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString101114_literalMutationNumber101877__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 0;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationNumber101877__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationNumber101877__8)).toString());
            String String_190 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_190);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationNumber101877__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationNumber101877__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationNumber101133_literalMutationString101570() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationNumber101133_literalMutationString101570__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * -0.6)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationNumber101133_literalMutationString101570__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationNumber101133_literalMutationString101570__8)).toString());
            String String_196 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_196);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationNumber101133_literalMutationString101570__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationNumber101133_literalMutationString101570__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationNumber101132_literalMutationString101620() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationNumber101132_literalMutationString101620__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 1.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationNumber101132_literalMutationString101620__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationNumber101132_literalMutationString101620__8)).toString());
            String String_319 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_319);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationNumber101132_literalMutationString101620__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationNumber101132_literalMutationString101620__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString101114_literalMutationNumber101884() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString101114_literalMutationNumber101884__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.0)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationNumber101884__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationNumber101884__8)).toString());
            String String_345 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_345);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationNumber101884__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString101114_literalMutationNumber101884__8)).toString());
        }
    }

    public void testBrokenSimple_literalMutationString101116_literalMutationString101645() throws MustacheException, IOException, InterruptedException, ExecutionException {
        {
            MustacheFactory c = createMustacheFactory();
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            Mustache m = c.compile("");
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            StringWriter sw = new StringWriter();
            Writer o_testBrokenSimple_literalMutationString101116_literalMutationString101645__8 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString101116_literalMutationString101645__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString101116_literalMutationString101645__8)).toString());
            String String_352 = "Should have failed: " + (sw.toString());
            TestCase.assertEquals("Should have failed: box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", String_352);
            TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
            TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
            TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
            TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testBrokenSimple_literalMutationString101116_literalMutationString101645__8)).getBuffer())).toString());
            TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testBrokenSimple_literalMutationString101116_literalMutationString101645__8)).toString());
        }
    }

    public void testIsNotEmpty_literalMutationString45962_add46584() throws IOException {
        Object object = new Object() {
            List<String> people = Collections.singletonList("Test");
        };
        StringWriter o_testIsNotEmpty_literalMutationString45962_add46584__7 = execute("", object);
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testIsNotEmpty_literalMutationString45962_add46584__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testIsNotEmpty_literalMutationString45962_add46584__7)).toString());
        StringWriter sw = execute("", object);
        String o_testIsNotEmpty_literalMutationString45962__9 = TestUtil.getContents(root, "isempty.txt");
        TestCase.assertEquals("Is not empty\n", o_testIsNotEmpty_literalMutationString45962__9);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testIsNotEmpty_literalMutationString45962_add46584__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testIsNotEmpty_literalMutationString45962_add46584__7)).toString());
        TestCase.assertEquals("Is not empty\n", o_testIsNotEmpty_literalMutationString45962__9);
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

    public void testSecurity_literalMutationString119201() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSecurity_literalMutationString119201__7 = m.execute(sw, new Object() {
            String name = "Chris";

            int value = 10000;

            int taxed_value() {
                return ((int) ((this.value) - ((this.value) * 0.4)));
            }

            boolean in_ca = true;

            private String test = "Test";
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSecurity_literalMutationString119201__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSecurity_literalMutationString119201__7)).toString());
        String o_testSecurity_literalMutationString119201__15 = TestUtil.getContents(root, "security.txt");
        TestCase.assertEquals("", o_testSecurity_literalMutationString119201__15);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSecurity_literalMutationString119201__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSecurity_literalMutationString119201__7)).toString());
        TestCase.assertEquals("", o_testSecurity_literalMutationString119201__15);
    }

    public void testIdentitySimple_literalMutationString147576_add149478() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString147576_add149478__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString147576_add149478__8);
        String o_testIdentitySimple_literalMutationString147576__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString147576__8);
        String o_testIdentitySimple_literalMutationString147576__10 = sw.toString().replaceAll("\\s+", "");
        TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString147576__10);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString147576_add149478__8);
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString147576__8);
    }

    public void testIdentitySimple_literalMutationString147576() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.identity(sw);
        String o_testIdentitySimple_literalMutationString147576__8 = TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString147576__8);
        String o_testIdentitySimple_literalMutationString147576__10 = sw.toString().replaceAll("\\s+", "");
        TestCase.assertEquals("box.htmlclassloader.htmlclient.htmlclient.txtcomcompiletest.mustachecomplex.htmlcomplex.txtdiv.htmlfallbackfdbcli.mustachefdbcli.txtfdbcli2.mustachefdbcli2.txtfdbcli3.mustachefdbcli3.txtfollow.htmlfollownomenu.htmlfollownomenu.txtfunctionshogan.jsonmain.htmlmethod.htmlmultiple_recursive_partials.htmlmultipleextensions.htmlmultipleextensions.txtnested_inheritance.htmlnested_inheritance.txtnested_partials_template.htmloverrideextension.htmlparentreplace.htmlpartialintemplatefunction.htmlpartialsub.htmlpartialsubpartial.htmlpartialsubpartial.txtpartialsuper.htmlpathpretranslate.htmlpsauxwww.mustachepsauxwww.txtrelativereplace.htmlreplace.txtsinglereplace.htmlspecsub.htmlsub.txtsubblockchild1.htmlsubblockchild1.txtsubblockchild2.htmlsubblockchild2.txtsubblocksuper.htmlsubsub.htmlsubsub.txtsubsubchild1.htmlsubsubchild1.txtsubsubchild2.htmlsubsubchild2.txtsubsubchild3.htmlsubsubchild3.txtsubsubmiddle.htmlsubsubsuper.htmlsuper.htmltemplate.htmltemplate.mustachetemplates_filepathtemplates.jartoomany.htmltweetbox.htmluninterestingpartial.html", o_testIdentitySimple_literalMutationString147576__10);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Hello{{name}}Youhavejustwon${{value}}!{{#test}}{{/test}}{{#in_ca}}Well,${{taxed_value}},aftertaxes.{{fred}}{{/in_ca}}", o_testIdentitySimple_literalMutationString147576__8);
    }

    public void testProperties_literalMutationString128890() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testProperties_literalMutationString128890__7 = m.execute(sw, new Object() {
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
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testProperties_literalMutationString128890__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testProperties_literalMutationString128890__7)).toString());
        String o_testProperties_literalMutationString128890__22 = TestUtil.getContents(root, "simple.txt");
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString128890__22);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testProperties_literalMutationString128890__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testProperties_literalMutationString128890__7)).toString());
        TestCase.assertEquals("Hello Chris\n  You have just won $10000!\n\nWell, $6000,  after taxes.\n", o_testProperties_literalMutationString128890__22);
    }

    public void testPartialWithTF_literalMutationString106891_add107146() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testPartialWithTF_literalMutationString106891_add107146__7 = m.execute(sw, new Object() {
            public TemplateFunction i() {
                return ( s) -> s;
            }
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString106891_add107146__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString106891_add107146__7)).toString());
        Writer o_testPartialWithTF_literalMutationString106891__7 = m.execute(sw, new Object() {
            public TemplateFunction i() {
                return ( s) -> s;
            }
        });
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString106891_add107146__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString106891_add107146__7)).toString());
    }

    public void testPartialWithTF_literalMutationString106891() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testPartialWithTF_literalMutationString106891__7 = m.execute(sw, new Object() {
            public TemplateFunction i() {
                return ( s) -> s;
            }
        });
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString106891__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString106891__7)).toString());
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPartialWithTF_literalMutationString106891__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPartialWithTF_literalMutationString106891__7)).toString());
    }

    public void testComplexParallel_add49868_literalMutationString49969() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testComplexParallel_add49868_literalMutationString49969__7 = m.execute(sw, new ParallelComplexObject());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testComplexParallel_add49868_literalMutationString49969__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testComplexParallel_add49868_literalMutationString49969__7)).toString());
        m.execute(sw, new ParallelComplexObject()).close();
        String o_testComplexParallel_add49868__12 = TestUtil.getContents(root, "complex.txt");
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testComplexParallel_add49868__12);
        sw.toString();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testComplexParallel_add49868_literalMutationString49969__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testComplexParallel_add49868_literalMutationString49969__7)).toString());
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testComplexParallel_add49868__12);
    }

    public void testSerialCallable_literalMutationString56060_add56331() throws MustacheException, IOException {
        StringWriter o_testSerialCallable_literalMutationString56060_add56331__1 = execute("", new ParallelComplexObject());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSerialCallable_literalMutationString56060_add56331__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSerialCallable_literalMutationString56060_add56331__1)).toString());
        StringWriter sw = execute("", new ParallelComplexObject());
        String o_testSerialCallable_literalMutationString56060__4 = TestUtil.getContents(root, "complex.txt");
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString56060__4);
        sw.toString();
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSerialCallable_literalMutationString56060_add56331__1)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSerialCallable_literalMutationString56060_add56331__1)).toString());
        TestCase.assertEquals("<h1>Colors</h1>\n  <ul>\n      <li><strong>red</strong></li>\n      <li><a href=\"#Green\">green</a></li>\n      <li><a href=\"#Blue\">blue</a></li>\n  </ul>\n  <p>The list is not empty.</p>\n", o_testSerialCallable_literalMutationString56060__4);
    }

    public void testReadme_literalMutationString19779() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadme_literalMutationString19779__9 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadme_literalMutationString19779__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadme_literalMutationString19779__9)).toString());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadme_literalMutationString19779__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString19779__13);
        sw.toString();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadme_literalMutationString19779__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadme_literalMutationString19779__9)).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadme_literalMutationString19779__13);
    }

    public void testReadmeSerial_add4961_add5732() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache o_testReadmeSerial_add4961_add5732__3 = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeSerial_add4961_add5732__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeSerial_add4961_add5732__3)).getName());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add4961__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        sw.toString();
        String o_testReadmeSerial_add4961__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4961__14);
        sw.toString();
        String String_24 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_24);
        boolean boolean_25 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeSerial_add4961_add5732__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeSerial_add4961_add5732__3)).getName());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4961__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_24);
    }

    public void testReadmeSerial_literalMutationString4944() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_literalMutationString4944__9 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadmeSerial_literalMutationString4944__9)).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_literalMutationString4944__9)).getBuffer())).toString());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_literalMutationString4944__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString4944__13);
        sw.toString();
        String String_50 = "Should be a little bit more than 4 seconds: " + diff;
        boolean boolean_51 = (diff > 3999) && (diff < 6000);
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadmeSerial_literalMutationString4944__9)).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadmeSerial_literalMutationString4944__9)).getBuffer())).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_literalMutationString4944__13);
    }

    public void testReadmeParallel_add141681_literalMutationString141904() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeParallel_add141681_literalMutationString141904__9 = m.execute(sw, new AmplInterpreterTest.Context());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadmeParallel_add141681_literalMutationString141904__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadmeParallel_add141681_literalMutationString141904__9)).toString());
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add141681__16 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add141681__16);
        sw.toString();
        String String_369 = "Should be a little bit more than 1 second: " + diff;
        boolean boolean_370 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testReadmeParallel_add141681_literalMutationString141904__9)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testReadmeParallel_add141681_literalMutationString141904__9)).toString());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add141681__16);
    }

    public void testReadmeParallel_add141678_literalMutationNumber141965() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache o_testReadmeParallel_add141678__3 = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeParallel_add141678__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeParallel_add141678__3)).getName());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add141678__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add141678__15);
        sw.toString();
        String String_371 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_371);
        boolean boolean_372 = (diff > 999) && (diff < 0);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeParallel_add141678__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeParallel_add141678__3)).getName());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add141678__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_371);
    }

    public void testReadmeParallel_add141684_remove142863() throws MustacheException, IOException {
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
        sw.toString();
        String o_testReadmeParallel_add141684__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add141684__15);
        String String_375 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_375);
        boolean boolean_376 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add141684__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_375);
    }

    public void testReadmeParallel_add141682_remove142848() throws MustacheException, IOException {
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
        String o_testReadmeParallel_add141682__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add141682__15);
        sw.toString();
        String String_365 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_365);
        boolean boolean_366 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add141682__15);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_365);
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

    public void testRelativePathsSameDir_literalMutationString161250_add161879() throws IOException {
        MustacheFactory mf = createMustacheFactory();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        Mustache compile = mf.compile("");
        TestCase.assertFalse(((DefaultMustache) (compile)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (compile)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testRelativePathsSameDir_literalMutationString161250_add161879__7 = compile.execute(sw, "");
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRelativePathsSameDir_literalMutationString161250_add161879__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRelativePathsSameDir_literalMutationString161250_add161879__7)).toString());
        compile.execute(sw, "").close();
        String o_testRelativePathsSameDir_literalMutationString161250__9 = TestUtil.getContents(root, "relative/paths.txt");
        TestCase.assertEquals("test", o_testRelativePathsSameDir_literalMutationString161250__9);
        sw.toString();
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (mf)).getRecursionLimit())));
        TestCase.assertNull(((DefaultMustacheFactory) (mf)).getExecutorService());
        TestCase.assertFalse(((DefaultMustache) (compile)).isRecursive());
        TestCase.assertEquals("", ((DefaultMustache) (compile)).getName());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testRelativePathsSameDir_literalMutationString161250_add161879__7)).getBuffer())).toString());
        TestCase.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testRelativePathsSameDir_literalMutationString161250_add161879__7)).toString());
        TestCase.assertEquals("test", o_testRelativePathsSameDir_literalMutationString161250__9);
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

    public void testInvalidDelimitersnull109693_failAssert0_literalMutationString109765_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Aoolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull109693 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull109693_failAssert0_literalMutationString109765 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Aoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109686_failAssert0_add109956_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "F4|Rp4<kE/S:p^qI(I3A!");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686_failAssert0_add109956 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[F4|Rp4<kE/S:p^qI(I3A!:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109683_failAssert0null109977_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Utoolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683_failAssert0null109977 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Utoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109684_failAssert0_add109960_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684_failAssert0_add109960 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull109693_failAssert0_literalMutationString109764_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Ktoolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull109693 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull109693_failAssert0_literalMutationString109764 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Ktoolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull109693_failAssert0_literalMutationString109763_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull109693 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull109693_failAssert0_literalMutationString109763 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109684_failAssert0_add109961_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=tooong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684_failAssert0_add109961 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109691_failAssert0_add109935_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109691 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109691_failAssert0_add109935 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109690_failAssert0_add109946_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDAelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDAelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690_failAssert0_add109946 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDAelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109690_failAssert0_add109945_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDAelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690_failAssert0_add109945 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDAelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109687_failAssert0_literalMutationString109897_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolo&g}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687_failAssert0_literalMutationString109897 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolo&g @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109691_failAssert0_add109934_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109691 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109691_failAssert0_add109934 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull109693_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull109693 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109686_failAssert0_add109954_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "F4|Rp4<kE/S:p^qI(I3A!");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686_failAssert0_add109954 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[F4|Rp4<kE/S:p^qI(I3A!:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109684_failAssert0_add109962_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684_failAssert0_add109962 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109691_failAssert0_add109937_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109691 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109691_failAssert0_add109937 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109686_failAssert0_add109955_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "F4|Rp4<kE/S:p^qI(I3A!");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "F4|Rp4<kE/S:p^qI(I3A!");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686_failAssert0_add109955 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[F4|Rp4<kE/S:p^qI(I3A!:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109691_failAssert0_add109936_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109691 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109691_failAssert0_add109936 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109844_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109844 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109685_failAssert0_add109964_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685_failAssert0_add109964 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109685_failAssert0_add109963_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685_failAssert0_add109963 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109845_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109845 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109869_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolyng}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109869 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolyng @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109691_failAssert0() throws Exception {
        try {
            {
                createMustacheFactory();
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109691 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109680_failAssert0null109980_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolyng}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680_failAssert0null109980 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolyng @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109868_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolyng}}"), "testInva(lidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109868 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolyng @[testInva(lidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109683_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=Utoolong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Utoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109841_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolonHg}}"), "testInvalidDelim+ters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109841 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolonHg @[testInvalidDelim+ters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0null109974_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0null109974 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109842_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}"), "testInvalidDelim+ters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109842 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong} @[testInvalidDelim+ters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109685_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109684_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109862_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolyn}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109862 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolyn @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0null109973_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), null);
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0null109973 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_add109939_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_add109939 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109687_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109686_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "F4|Rp4<kE/S:p^qI(I3A!");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[F4|Rp4<kE/S:p^qI(I3A!:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109864_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=itoolyng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109864 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =itoolyng @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109867_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tgolyng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109867 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tgolyng @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109689_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelim+ters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelim+ters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109688_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109685_failAssert0_add109965_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685_failAssert0_add109965 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_add109938_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_add109938 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_literalMutationString109794_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("Nd$u3mQk0uS/"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_literalMutationString109794 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109689_failAssert0_add109953_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelim+ters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689_failAssert0_add109953 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelim+ters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_literalMutationString109793_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=t7olong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_literalMutationString109793 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_literalMutationString109797_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDe8limiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_literalMutationString109797 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109684_failAssert0_literalMutationString109879_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=dooong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684_failAssert0_literalMutationString109879 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =dooong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull109693_failAssert0_add109933_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull109693 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull109693_failAssert0_add109933 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull109693_failAssert0_add109932_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), null);
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull109693 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull109693_failAssert0_add109932 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109687_failAssert0_literalMutationString109906_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "L;EUTd(nm");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687_failAssert0_literalMutationString109906 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[L;EUTd(nm:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109872_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolyng}}"), ":B04$4.1kwd<hCACdY]eE");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109872 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolyng @[:B04$4.1kwd<hCACdY]eE:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109813_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testnvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109813 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testnvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109684_failAssert0null109981_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684_failAssert0null109981 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109812_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "6DR+[:aohQu#EELe4qPE");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109812 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[6DR+[:aohQu#EELe4qPE:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109689_failAssert0_add109951_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelim+ters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689_failAssert0_add109951 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelim+ters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109680_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolyng}}"), "testInvalidDelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolyng @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_add109940_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_add109940 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_literalMutationString109778_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader(""), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_literalMutationString109778 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109689_failAssert0_add109952_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelim+ters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelim+ters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689_failAssert0_add109952 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelim+ters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109810_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "tIstInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109810 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[tIstInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109811_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDeli|mitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109811 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDeli|mitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109686_failAssert0_literalMutationString109860_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "F4|Rp 4<kE/S:p^qI(I3A!");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686_failAssert0_literalMutationString109860 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[F4|Rp 4<kE/S:p^qI(I3A!:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109688_failAssert0_add109943_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688_failAssert0_add109943 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109688_failAssert0_add109942_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688_failAssert0_add109942 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109686_failAssert0_literalMutationString109861_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "+[.Y9+U;%;-,&Qk3|]+)&");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686_failAssert0_literalMutationString109861 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[+[.Y9+U;%;-,&Qk3|]+)&:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109688_failAssert0_add109944_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688_failAssert0_add109944 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109690_failAssert0_add109947_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDAelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690_failAssert0_add109947 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDAelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109839_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=to/long}}"), "testInvalidDelim+ters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109839 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =to/long @[testInvalidDelim+ters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109690_failAssert0null109976_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690_failAssert0null109976 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109683_failAssert0_literalMutationString109828_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=UtooloIng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683_failAssert0_literalMutationString109828 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =UtooloIng @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109687_failAssert0_literalMutationString109905_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), " does not exist");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687_failAssert0_literalMutationString109905 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[ does not exist:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109687_failAssert0_literalMutationString109904_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "pfge1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687_failAssert0_literalMutationString109904 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[pfge1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimitersnull109693_failAssert0_add109931_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimitersnull109693 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimitersnull109693_failAssert0_add109931 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109687_failAssert0null109983_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687_failAssert0null109983 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109686_failAssert0null109979_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686_failAssert0null109979 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109687_failAssert0_literalMutationString109902_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.twxt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687_failAssert0_literalMutationString109902 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.twxt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109690_failAssert0_literalMutationString109814_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{==toolong}}"), "testInvalidDAelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690_failAssert0_literalMutationString109814 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ==toolong @[testInvalidDAelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109687_failAssert0_literalMutationString109903_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.xt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687_failAssert0_literalMutationString109903 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.xt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_literalMutationString109784_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_literalMutationString109784 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109683_failAssert0_add109949_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=Utoolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=Utoolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683_failAssert0_add109949 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Utoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109691_failAssert0_literalMutationString109770_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolog}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109691 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109691_failAssert0_literalMutationString109770 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolog @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109683_failAssert0_literalMutationString109830_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=UtWolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683_failAssert0_literalMutationString109830 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =UtWolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109691_failAssert0_literalMutationString109772_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "te,stInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109691 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109691_failAssert0_literalMutationString109772 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[te,stInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109683_failAssert0_literalMutationString109832_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Utoolong}}"), "testInvalidLelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683_failAssert0_literalMutationString109832 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Utoolong @[testInvalidLelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_literalMutationString109786_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInval{dDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_literalMutationString109786 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInval{dDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_literalMutationString109783_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_literalMutationString109783 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong} @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109680_failAssert0_add109959_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolyng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680_failAssert0_add109959 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolyng @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109683_failAssert0_literalMutationString109834_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Utoolong}}"), "(W^y_xV`PIq;(Huz c2kp");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683_failAssert0_literalMutationString109834 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Utoolong @[(W^y_xV`PIq;(Huz c2kp:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109684_failAssert0_literalMutationString109881_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvalidelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684_failAssert0_literalMutationString109881 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109680_failAssert0_add109958_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolyng}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolyng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680_failAssert0_add109958 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolyng @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109683_failAssert0_literalMutationString109833_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Utoolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683_failAssert0_literalMutationString109833 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Utoolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109684_failAssert0_literalMutationString109880_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684_failAssert0_literalMutationString109880 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109690_failAssert0_literalMutationString109820_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690_failAssert0_literalMutationString109820 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109685_failAssert0_literalMutationString109893_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685_failAssert0_literalMutationString109893 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109688_failAssert0null109975_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688_failAssert0null109975 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109805_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), "testInvalidDelimitrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109805 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[testInvalidDelimitrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109691_failAssert0_literalMutationString109775_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelqmiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109691 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109691_failAssert0_literalMutationString109775 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelqmiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109685_failAssert0_literalMutationString109894_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685_failAssert0_literalMutationString109894 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109683_failAssert0_add109948_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Utoolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683_failAssert0_add109948 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Utoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109691_failAssert0_literalMutationString109776_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109691 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109691_failAssert0_literalMutationString109776 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109691_failAssert0_literalMutationString109777_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "vF6_lS*v8mDIZPLS1+HyH");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109691 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109691_failAssert0_literalMutationString109777 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[vF6_lS*v8mDIZPLS1+HyH:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109684_failAssert0_literalMutationString109884_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooong}}"), "testInvalidxDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109684_failAssert0_literalMutationString109884 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooong @[testInvalidxDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_add109941_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_add109941 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109685_failAssert0_literalMutationString109892_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "p");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685_failAssert0_literalMutationString109892 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[p:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109873_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolyng}}"), "testInvalidDelimiQers");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680_failAssert0_literalMutationString109873 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolyng @[testInvalidDelimiQers:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109680_failAssert0_add109957_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolyng}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109680_failAssert0_add109957 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolyng @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109685_failAssert0_literalMutationString109891_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tooNong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685_failAssert0_literalMutationString109891 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tooNong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109808_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109808 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109809_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109688_failAssert0_literalMutationString109809 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109689_failAssert0null109978_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689_failAssert0null109978 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109685_failAssert0null109982_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685_failAssert0null109982 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109686_failAssert0_literalMutationString109857_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "F4|Rp4<kE/S:^qI(I3A!");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686_failAssert0_literalMutationString109857 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[F4|Rp4<kE/S:^qI(I3A!:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109691_failAssert0null109972_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), null);
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109691 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109691_failAssert0null109972 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[null:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109685_failAssert0_literalMutationString109889_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tolong}}"), "");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109685_failAssert0_literalMutationString109889 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tolong @[:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109691_failAssert0_literalMutationString109769_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Btoolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109691 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109691_failAssert0_literalMutationString109769 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Btoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109686_failAssert0_literalMutationString109855_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=tool]ong}}"), "F4|Rp4<kE/S:p^qI(I3A!");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686_failAssert0_literalMutationString109855 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =tool]ong @[F4|Rp4<kE/S:p^qI(I3A!:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109686_failAssert0_literalMutationString109851_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolopg}}"), "F4|Rp4<kE/S:p^qI(I3A!");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686_failAssert0_literalMutationString109851 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolopg @[F4|Rp4<kE/S:p^qI(I3A!:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109686_failAssert0_literalMutationString109852_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=oolong}}"), "F4|Rp4<kE/S:p^qI(I3A!");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109686_failAssert0_literalMutationString109852 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =oolong @[F4|Rp4<kE/S:p^qI(I3A!:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109847_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "$K!N*5bpF&%]SoWu1Guf8");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109847 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[$K!N*5bpF&%]SoWu1Guf8:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109846_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelim+tes");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109689_failAssert0_literalMutationString109846 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelim+tes:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109690_failAssert0() throws Exception {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDAelimiters");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDAelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_literalMutationString109801_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "#<%e4z`|B BmLIn65k]Q,");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_literalMutationString109801 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_add109692_failAssert0_literalMutationString109800_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "testInvalidDelimiters");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "te/tInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_add109692 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_add109692_failAssert0_literalMutationString109800 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109690_failAssert0_literalMutationString109821_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidDAelimitenrs");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690_failAssert0_literalMutationString109821 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidDAelimitenrs:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109690_failAssert0_literalMutationString109822_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690_failAssert0_literalMutationString109822 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109687_failAssert0_add109966_failAssert0() throws Exception {
        try {
            {
                {
                    createMustacheFactory();
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687_failAssert0_add109966 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109683_failAssert0_literalMutationString109835_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Utoolong}}"), "testnvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683_failAssert0_literalMutationString109835 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Utoolong @[testnvalidDelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109690_failAssert0_literalMutationString109824_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInva1idDAelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690_failAssert0_literalMutationString109824 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInva1idDAelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109687_failAssert0_add109968_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687_failAssert0_add109968 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109687_failAssert0_add109967_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "page1.txt");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109687_failAssert0_add109967 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109690_failAssert0_literalMutationString109823_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=toolong}}"), "testInvalidAelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109690_failAssert0_literalMutationString109823 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =toolong @[testInvalidAelimiters:1]", expected.getMessage());
        }
    }

    public void testInvalidDelimiters_literalMutationString109683_failAssert0_add109950_failAssert0() throws Exception {
        try {
            {
                {
                    MustacheFactory mf = createMustacheFactory();
                    Mustache m = mf.compile(new StringReader("{{=Utoolong}}"), "testInvalidDelimiters");
                }
                junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testInvalidDelimiters_literalMutationString109683_failAssert0_add109950 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =Utoolong @[testInvalidDelimiters:1]", expected.getMessage());
        }
    }

    private static class SuperClass {
        String values = "value";
    }

    public void testOutputDelimiters_literalMutationString74913_failAssert0_literalMutationNumber75374_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[-1]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913_failAssert0_literalMutationNumber75374 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString74913_failAssert0_literalMutationNumber75373_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[1]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913_failAssert0_literalMutationNumber75373 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString74914_literalMutationString75107_failAssert0() throws Exception {
        try {
            String template = "{{=## ##=}}{{##={{ }}=#H##";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "");
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimiters_literalMutationString74914__8 = mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74914_literalMutationString75107 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={{}}=#H @[:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString74913_failAssert0_add75668_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                new DefaultMustacheFactory().compile(new StringReader(template), "test");
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913_failAssert0_add75668 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString74913_failAssert0() throws Exception {
        try {
            String template = "{{=# ##=}}{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString74916_literalMutationString75187_failAssert0() throws Exception {
        try {
            String template = "{{=## `#=}}{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "page1.txt");
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimiters_literalMutationString74916__8 = mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74916_literalMutationString75187 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={{}}=#### @[page1.txt:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString74917_literalMutationString75089_failAssert0() throws Exception {
        try {
            String template = "{{=## ##=}}{{##={a{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "tst");
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimiters_literalMutationString74917__8 = mustache.execute(sw, new Object[0]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74917_literalMutationString75089 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={a{}}= @[tst:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationNumber74920_literalMutationString75203_failAssert0() throws Exception {
        try {
            String template = "{{=## ##=g}}{{##={{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimiters_literalMutationNumber74920__8 = mustache.execute(sw, new Object[1]);
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationNumber74920_literalMutationString75203 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =####=g @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString74913_failAssert0_add75670_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913_failAssert0_add75670 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_add74927_literalMutationString75027_failAssert0() throws Exception {
        try {
            String template = "{{=## ##=}}{{##={{ }}0=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            Writer o_testOutputDelimiters_add74927__8 = mustache.execute(sw, new Object[0]);
            sw.toString();
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_add74927_literalMutationString75027 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={{}}0= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString74913_failAssert0null75762_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), null);
                StringWriter sw = new StringWriter();
                mustache.execute(sw, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913_failAssert0null75762 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[null:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_literalMutationString74913_failAssert0null75763_failAssert0() throws Exception {
        try {
            {
                String template = "{{=# ##=}}{{##={{ }}=####";
                Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
                StringWriter sw = new StringWriter();
                mustache.execute(null, new Object[0]);
                sw.toString();
                junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testOutputDelimiters_literalMutationString74913_failAssert0null75763 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: =###= @[test:1]", expected.getMessage());
        }
    }

    public void testOutputDelimiters_remove74928_literalMutationString75015_failAssert0() throws Exception {
        try {
            String template = "{{=## ##=}}{{##={X{ }}=####";
            Mustache mustache = new DefaultMustacheFactory().compile(new StringReader(template), "test");
            StringWriter sw = new StringWriter();
            sw.toString();
            junit.framework.TestCase.fail("testOutputDelimiters_remove74928_literalMutationString75015 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Invalid delimiter string: ={X{}}= @[test:1]", expected.getMessage());
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

