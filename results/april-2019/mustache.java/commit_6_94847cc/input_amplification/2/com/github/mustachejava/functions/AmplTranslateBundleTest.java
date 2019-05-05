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


public class AmplTranslateBundleTest {
    private static File root;

    private static final String BUNDLE = "com.github.mustachejava.functions.translatebundle";

    @Test(timeout = 10000)
    public void testTranslation_remove25_literalMutationString238_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Template ^Vt`/%HTO-M? not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_remove25__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            String o_testTranslation_remove25__11 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_remove25_literalMutationString238 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template ^Vt`/%HTO-M? not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_remove25_literalMutationString241_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("transl{tebundle.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_remove25__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            String o_testTranslation_remove25__11 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_remove25_literalMutationString241 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template transl{tebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_add21_literalMutationString138_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("transla`ebundle.html");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_add21__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslation_add21__11 = m.execute(sw, scope);
            Writer o_testTranslation_add21__12 = m.execute(sw, scope);
            String o_testTranslation_add21__13 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_add21_literalMutationString138 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template transla`ebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString14_failAssert0_literalMutationString473_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("9N)sxoO2VeD#McU//x!^");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "Template ^Vt`/%HTO-M? not found");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString14 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString14_failAssert0_literalMutationString473 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 9N)sxoO2VeD#McU//x!^ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_add21_literalMutationString136_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Template ^Vt`/%HTO-M? not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_add21__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslation_add21__11 = m.execute(sw, scope);
            Writer o_testTranslation_add21__12 = m.execute(sw, scope);
            String o_testTranslation_add21__13 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_add21_literalMutationString136 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template ^Vt`/%HTO-M? not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString1_failAssert0null1014_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("BJ%B]`Xum#:(pD/(8$(n");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put(null, new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString1_failAssert0null1014 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template BJ%B]`Xum#:(pD/(8$(n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString1_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("BJ%B]`Xum#:(pD/(8$(n");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString1 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template BJ%B]`Xum#:(pD/(8$(n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Template ^Vt`/%HTO-M? not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            m.execute(sw, scope);
            TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template ^Vt`/%HTO-M? not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_remove24_literalMutationString226_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Template ^Vt`/%HTO-M? not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Writer o_testTranslation_remove24__9 = m.execute(sw, scope);
            String o_testTranslation_remove24__10 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_remove24_literalMutationString226 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template ^Vt`/%HTO-M? not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString1_failAssert0_add855_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("BJ%B]`Xum#:(pD/(8$(n");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString1_failAssert0_add855 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template BJ%B]`Xum#:(pD/(8$(n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString1_failAssert0_add851_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("BJ%B]`Xum#:(pD/(8$(n");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString1_failAssert0_add851 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template BJ%B]`Xum#:(pD/(8$(n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_add848_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template ^Vt`/%HTO-M? not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_add848 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template ^Vt`/%HTO-M? not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_add23_literalMutationString118_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("Template ^Vt`/%HTO-M? not found");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_add23__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslation_add23__11 = m.execute(sw, scope);
            sw.toString();
            String o_testTranslation_add23__13 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_add23_literalMutationString118 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template ^Vt`/%HTO-M? not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_literalMutationString549_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template ^Vt`/%HTO-M? not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_literalMutationString549 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template ^Vt`/%HTO-M? not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0null1009_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template ^Vt`/%HTO-M? not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put(null, new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0null1009 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template ^Vt`/%HTO-M? not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslationnull28_failAssert0_literalMutationString392_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("tran]slatebundle.html");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(null, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslationnull28 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testTranslationnull28_failAssert0_literalMutationString392 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template tran]slatebundle.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString1_failAssert0_literalMutationString571_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("BJ%B]`Xum#:(pD/(8$(n");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("NXO${", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString1_failAssert0_literalMutationString571 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template BJ%B]`Xum#:(pD/(8$(n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_literalMutationString560_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template ^Vt`/%HTO-M? not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundlef.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_literalMutationString560 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template ^Vt`/%HTO-M? not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString3_failAssert0_add847_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template ^Vt`/%HTO-M? not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString3 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString3_failAssert0_add847 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template ^Vt`/%HTO-M? not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString2() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map<String, Object> scope = new HashMap<>();
        Object o_testTranslation_literalMutationString2__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
        Assert.assertNull(o_testTranslation_literalMutationString2__9);
        Writer o_testTranslation_literalMutationString2__11 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testTranslation_literalMutationString2__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testTranslation_literalMutationString2__11)).toString());
        String o_testTranslation_literalMutationString2__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
        Assert.assertEquals("Translation bundles work!\nNotFound\n", o_testTranslation_literalMutationString2__12);
        sw.toString();
        Assert.assertNull(o_testTranslation_literalMutationString2__9);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testTranslation_literalMutationString2__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testTranslation_literalMutationString2__11)).toString());
        Assert.assertEquals("Translation bundles work!\nNotFound\n", o_testTranslation_literalMutationString2__12);
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString15_failAssert0_literalMutationString580_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("Template ^Vt`/%HTO-M? not found");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundlG.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString15 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString15_failAssert0_literalMutationString580 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Template ^Vt`/%HTO-M? not found not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_add22_literalMutationString204_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
            Mustache m = c.compile("OT[*rtZL)Y&[l-!|eGB/");
            StringWriter sw = new StringWriter();
            Map<String, Object> scope = new HashMap<>();
            Object o_testTranslation_add22__9 = scope.put("trans", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
            Writer o_testTranslation_add22__11 = m.execute(sw, scope);
            String o_testTranslation_add22__12 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            String o_testTranslation_add22__13 = TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
            sw.toString();
            org.junit.Assert.fail("testTranslation_add22_literalMutationString204 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template OT[*rtZL)Y&[l-!|eGB/ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTranslation_literalMutationString1_failAssert0_literalMutationString570_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplTranslateBundleTest.root);
                Mustache m = c.compile("BJ%B]`Xum#:(pD/(8$(n");
                StringWriter sw = new StringWriter();
                Map<String, Object> scope = new HashMap<>();
                scope.put("tranns", new TranslateBundleFunction(AmplTranslateBundleTest.BUNDLE, Locale.US));
                m.execute(sw, scope);
                TestUtil.getContents(AmplTranslateBundleTest.root, "translatebundle.txt");
                sw.toString();
                org.junit.Assert.fail("testTranslation_literalMutationString1 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testTranslation_literalMutationString1_failAssert0_literalMutationString570 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template BJ%B]`Xum#:(pD/(8$(n not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File compiler = (new File("compiler").exists()) ? new File("compiler") : new File(".");
        AmplTranslateBundleTest.root = new File(compiler, "src/test/resources/functions");
    }
}

