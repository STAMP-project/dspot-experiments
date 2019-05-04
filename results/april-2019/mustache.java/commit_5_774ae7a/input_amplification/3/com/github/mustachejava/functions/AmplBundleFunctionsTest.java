package com.github.mustachejava.functions;


import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.MustacheNotFoundException;
import com.github.mustachejava.TestUtil;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class AmplBundleFunctionsTest {
    private static File root;

    private static final String BUNDLE = "com.github.mustachejava.functions.translatebundle";

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7504_failAssert0null9858_failAssert0_add16720_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("vhnMj00&y_`G");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString7504 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0null9858 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0null9858_failAssert0_add16720 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vhnMj00&y_`G not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_add7532_remove9588_literalMutationString10659_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache o_testPostLabels_add7532__3 = c.compile("WS.p#1 `|r<&");
            Mustache m = c.compile("bundles.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_add7532__10 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_add7532__12 = scope.put("replaceMe", "replaced");
            Writer o_testPostLabels_add7532__13 = m.execute(sw, scope);
            String o_testPostLabels_add7532__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            org.junit.Assert.fail("testPostLabels_add7532_remove9588_literalMutationString10659 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template WS.p#1 `|r<& not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7504_failAssert0_literalMutationString8794_failAssert0_literalMutationString14979_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("vhnMj00&y_`G");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("commentWithinxtendCodeBlock.txt", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("repaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString7504 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0_literalMutationString8794 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0_literalMutationString8794_failAssert0_literalMutationString14979 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vhnMj00&y_`G not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7515_literalMutationString8293_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("?[HJ3>,^jGPz");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_literalMutationString7515__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_literalMutationString7515__11 = scope.put("repaceMe", "replaced");
            Writer o_testPostLabels_literalMutationString7515__12 = m.execute(sw, scope);
            String o_testPostLabels_literalMutationString7515__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString7515_literalMutationString8293 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?[HJ3>,^jGPz not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7522_add9096_literalMutationString12636_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bundles<.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_literalMutationString7522__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_literalMutationString7522__11 = scope.put("replaceMe", "commentWithinxtendCodeBlock.txt");
            Writer o_testPostLabels_literalMutationString7522__12 = m.execute(sw, scope);
            o_testPostLabels_literalMutationString7522__12.toString();
            String o_testPostLabels_literalMutationString7522__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString7522_add9096_literalMutationString12636 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles<.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7504_failAssert0null9858_failAssert0null18174_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("vhnMj00&y_`G");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString7504 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0null9858 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0null9858_failAssert0null18174 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vhnMj00&y_`G not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_add7534_remove9584_literalMutationString12802_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("=o#Mm0oa[j]|");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_add7534__10 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_add7534__12 = scope.put("replaceMe", "replaced");
            Writer o_testPostLabels_add7534__13 = m.execute(sw, scope);
            String o_testPostLabels_add7534__14 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_add7534_remove9584_literalMutationString12802 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template =o#Mm0oa[j]| not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7502() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map<String, Object> scope = new HashMap<>();
        Object o_testPostLabels_literalMutationString7502__9 = scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
        Assert.assertNull(o_testPostLabels_literalMutationString7502__9);
        Object o_testPostLabels_literalMutationString7502__11 = scope.put("replaceMe", "replaced");
        Assert.assertNull(o_testPostLabels_literalMutationString7502__11);
        Writer o_testPostLabels_literalMutationString7502__12 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPostLabels_literalMutationString7502__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPostLabels_literalMutationString7502__12)).toString());
        String o_testPostLabels_literalMutationString7502__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
        Assert.assertEquals("Translation bundles work!\nPost translate text works, too\n", o_testPostLabels_literalMutationString7502__13);
        sw.toString();
        Assert.assertNull(o_testPostLabels_literalMutationString7502__9);
        Assert.assertNull(o_testPostLabels_literalMutationString7502__11);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPostLabels_literalMutationString7502__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPostLabels_literalMutationString7502__12)).toString());
        Assert.assertEquals("Translation bundles work!\nPost translate text works, too\n", o_testPostLabels_literalMutationString7502__13);
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7504_failAssert0null9858_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("vhnMj00&y_`G");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(null, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString7504 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0null9858 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vhnMj00&y_`G not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7506_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("bundles>.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString7506 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7504_failAssert0_add9494_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("vhnMj00&y_`G");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString7504 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0_add9494 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vhnMj00&y_`G not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7504_failAssert0_literalMutationString8794_failAssert0null18391_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("vhnMj00&y_`G");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("repaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString7504 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0_literalMutationString8794 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0_literalMutationString8794_failAssert0null18391 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vhnMj00&y_`G not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7504_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("vhnMj00&y_`G");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            scope.put("replaceMe", "replaced");
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString7504 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vhnMj00&y_`G not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7504_failAssert0_literalMutationString8794_failAssert0_add16976_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    c.compile("vhnMj00&y_`G");
                    Mustache m = c.compile("vhnMj00&y_`G");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("repaceMe", "replaced");
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString7504 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0_literalMutationString8794 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0_literalMutationString8794_failAssert0_add16976 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vhnMj00&y_`G not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7506_failAssert0_literalMutationString9010_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bundles>.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "repslaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString7506 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString7506_failAssert0_literalMutationString9010 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7504_failAssert0_literalMutationString8794_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("vhnMj00&y_`G");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("repaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString7504 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0_literalMutationString8794 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vhnMj00&y_`G not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7506_failAssert0_add9552_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bundles>.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("replaceMe", "replaced");
                scope.put("replaceMe", "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString7506 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString7506_failAssert0_add9552 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabelsnull7546_failAssert0_literalMutationString8691_failAssert0_literalMutationString15037_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("bundles html");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "b#ndles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabelsnull7546 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testPostLabelsnull7546_failAssert0_literalMutationString8691 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testPostLabelsnull7546_failAssert0_literalMutationString8691_failAssert0_literalMutationString15037 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7504_failAssert0null9858_failAssert0_literalMutationString14099_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("vhnMj00&y_`G");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("replaceMe", "replaced");
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testPostLabels_literalMutationString7504 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0null9858 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString7504_failAssert0null9858_failAssert0_literalMutationString14099 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template vhnMj00&y_`G not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7509_literalMutationString8120_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile(";sh#m]Al>DG-");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostLabels_literalMutationString7509__9 = scope.put("commentWithinxtendCodeBlock.txt", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Object o_testPostLabels_literalMutationString7509__11 = scope.put("replaceMe", "replaced");
            Writer o_testPostLabels_literalMutationString7509__12 = m.execute(sw, scope);
            String o_testPostLabels_literalMutationString7509__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostLabels_literalMutationString7509_literalMutationString8120 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ;sh#m]Al>DG- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostLabels_literalMutationString7506_failAssert0null9905_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("bundles>.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslate(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put(null, "replaced");
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_post_labels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostLabels_literalMutationString7506 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostLabels_literalMutationString7506_failAssert0null9905 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("cj<N#4Lh^Uz,%");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4Lh^Uz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString11null983_literalMutationString2679_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("r;G9h]]&P)]9");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testPostNullLabels_literalMutationString11__9 = scope.put(null, BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            Writer o_testPostNullLabels_literalMutationString11__11 = m.execute(sw, scope);
            String o_testPostNullLabels_literalMutationString11__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString11null983_literalMutationString2679 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template r;G9h]]&P)]9 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add896_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("cj<N#4LhUz,%");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add896 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4LhUz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0_add5935_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("cj<N#4Lh^Uz,%");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0_add5935 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4Lh^Uz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_literalMutationString540_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("cj<N#4LhUz,%");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "commentWithinxtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString540 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4LhUz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0_add5931_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("cj<N#4Lh^Uz,%");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US);
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0_add5931 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4Lh^Uz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add896_failAssert0null7021_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("cj<N#4LhUz,%");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put(null, BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add896 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add896_failAssert0null7021 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4LhUz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add896_failAssert0_literalMutationString4611_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("cj<N#4LhUz,%");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "O9KAF?;xo5R@H.S=Ol&QGI");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add896 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add896_failAssert0_literalMutationString4611 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4LhUz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0null1071_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("cj<N#4LhUz,%");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(null, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0null1071 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4LhUz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0_literalMutationString4419_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("cj<ON#4Lh^Uz,%");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0_literalMutationString4419 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<ON#4Lh^Uz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString2() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map<String, Object> scope = new HashMap<>();
        Object o_testPostNullLabels_literalMutationString2__9 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
        Assert.assertNull(o_testPostNullLabels_literalMutationString2__9);
        Writer o_testPostNullLabels_literalMutationString2__11 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPostNullLabels_literalMutationString2__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPostNullLabels_literalMutationString2__11)).toString());
        String o_testPostNullLabels_literalMutationString2__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
        Assert.assertEquals("Translation bundles work!\n\n", o_testPostNullLabels_literalMutationString2__13);
        sw.toString();
        Assert.assertNull(o_testPostNullLabels_literalMutationString2__9);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testPostNullLabels_literalMutationString2__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testPostNullLabels_literalMutationString2__11)).toString());
        Assert.assertEquals("Translation bundles work!\n\n", o_testPostNullLabels_literalMutationString2__13);
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0_literalMutationString4431_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("cj<N#4Lh^Uz,%");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0_literalMutationString4431 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4Lh^Uz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
            Mustache m = c.compile("cj<N#4LhUz,%");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
            sw.toString();
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4LhUz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0null6974_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("cj<N#4Lh^Uz,%");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0null6974 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4Lh^Uz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0null1071_failAssert0_add5357_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("cj<N#4LhUz,%");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0null1071 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0null1071_failAssert0_add5357 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4LhUz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString12_literalMutationString203_failAssert0_literalMutationString4142_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("sb0.N}|BeW}B0H|B[6R`:#,h($c>rP0");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPostNullLabels_literalMutationString12__9 = scope.put("srans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                Writer o_testPostNullLabels_literalMutationString12__11 = m.execute(sw, scope);
                String o_testPostNullLabels_literalMutationString12__13 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString12_literalMutationString203 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString12_literalMutationString203_failAssert0_literalMutationString4142 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sb0.N}|BeW}B0H|B[6R`:#,h($c>rP0 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_remove26_literalMutationString126_failAssert0_literalMutationString3563_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("ezo&NT%Dq7N,");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                Object o_testPostNullLabels_remove26__9 = scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                String o_testPostNullLabels_remove26__11 = TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulll{bels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_remove26_literalMutationString126 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_remove26_literalMutationString126_failAssert0_literalMutationString3563 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ezo&NT%Dq7N, not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add891_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                Mustache m = c.compile("cj<N#4LhUz,%");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                sw.toString();
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add891 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4LhUz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_add896_failAssert0_add6001_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("cj<N#4LhUz,%");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(sw, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add896 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_add896_failAssert0_add6001 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4LhUz,% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0null6972_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplBundleFunctionsTest.root);
                    Mustache m = c.compile("cj<N#4Lh^Uz,%");
                    StringWriter sw = new StringWriter();
                    Map<String, Object> scope = new HashMap<>();
                    scope.put("trans", BundleFunctions.newPostTranslateNullableLabel(AmplBundleFunctionsTest.BUNDLE, Locale.US));
                    m.execute(null, scope);
                    TestUtil.getContents(AmplBundleFunctionsTest.root, "bundles_nulllabels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testPostNullLabels_literalMutationString1 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testPostNullLabels_literalMutationString1_failAssert0_literalMutationString528_failAssert0null6972 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template cj<N#4Lh^Uz,% not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplBundleFunctionsTest.root = new File(compiler, "src/test/resources/functions");
    }
}

