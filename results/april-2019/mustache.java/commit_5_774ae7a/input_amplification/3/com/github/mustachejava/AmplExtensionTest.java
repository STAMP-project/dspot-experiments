package com.github.mustachejava;


import com.github.mustachejava.codes.DefaultMustache;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class AmplExtensionTest {
    private static File root;

    @Test(timeout = 10000)
    public void testSub_literalMutationString24628() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Map scope = new HashMap();
        Object o_testSub_literalMutationString24628__9 = scope.put("name", "Sam");
        Assert.assertNull(o_testSub_literalMutationString24628__9);
        Object o_testSub_literalMutationString24628__10 = scope.put("randomid", "asdlkfj");
        Assert.assertNull(o_testSub_literalMutationString24628__10);
        Writer o_testSub_literalMutationString24628__11 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSub_literalMutationString24628__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSub_literalMutationString24628__11)).toString());
        String o_testSub_literalMutationString24628__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
        Assert.assertEquals("<html>\n<head>\n<title>Subtitle</title>\n<script src=\"jquery.min.js\"></script>\n</head>\n<body>\n<div class=\"body\">\n<div id=\"asdlkfj\">\nHello, Sam!\n</div>\n</div>\n<div class=\"footer\">\n</div>\n</body>\n</html>", o_testSub_literalMutationString24628__12);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertNull(o_testSub_literalMutationString24628__9);
        Assert.assertNull(o_testSub_literalMutationString24628__10);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSub_literalMutationString24628__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSub_literalMutationString24628__11)).toString());
        Assert.assertEquals("<html>\n<head>\n<title>Subtitle</title>\n<script src=\"jquery.min.js\"></script>\n</head>\n<body>\n<div class=\"body\">\n<div id=\"asdlkfj\">\nHello, Sam!\n</div>\n</div>\n<div class=\"footer\">\n</div>\n</body>\n</html>", o_testSub_literalMutationString24628__12);
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19331_failAssert0null19852_failAssert0_literalMutationString20716_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("|H_n#vE&(1%>G v>[2E|5PD");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331_failAssert0null19852 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331_failAssert0null19852_failAssert0_literalMutationString20716 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template |H_n#vE&(1%>G v>[2E|5PD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_add19763_failAssert0null24008_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                    Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19763 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19763_failAssert0null24008 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_add19765_failAssert0_add23249_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                    Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19765 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19765_failAssert0_add23249 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("&qCK|)A;LQG,^x&^8l)V6XC");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multiplextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template &qCK|)A;LQG,^x&^8l)V6XC not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add19335_literalMutationString19449_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testMultipleExtensions_add19335__3 = c.compile("multipleextensions.html");
            Mustache m = c.compile("multipleextensions.h]ml");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleExtensions_add19335__8 = m.execute(sw, new Object());
            String o_testMultipleExtensions_add19335__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_add19335_literalMutationString19449 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h]ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_add19765_failAssert0null24004_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19765 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19765_failAssert0null24004 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_add19764_failAssert0_literalMutationString22193_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19764 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19764_failAssert0_literalMutationString22193 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_add19765_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19765 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_add19763_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19763 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_add19764_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19764 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add19336_literalMutationString19433_failAssert0_literalMutationString22317_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("m[ltipleextensio=s.html");
                StringWriter sw = new StringWriter();
                Writer o_testMultipleExtensions_add19336__7 = m.execute(sw, new Object());
                Writer o_testMultipleExtensions_add19336__9 = m.execute(sw, new Object());
                String o_testMultipleExtensions_add19336__11 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_add19336_literalMutationString19433 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_add19336_literalMutationString19433_failAssert0_literalMutationString22317 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template m[ltipleextensio=s.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_add19764_failAssert0_add23539_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19764 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19764_failAssert0_add23539 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add19336null19825_failAssert0_literalMutationString22249_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("PK;Ig,[J>w+-d;9qVb!;?1}");
                StringWriter sw = new StringWriter();
                Writer o_testMultipleExtensions_add19336__7 = m.execute(null, new Object());
                Writer o_testMultipleExtensions_add19336__9 = m.execute(sw, new Object());
                String o_testMultipleExtensions_add19336__11 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_add19336null19825 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensions_add19336null19825_failAssert0_literalMutationString22249 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template PK;Ig,[J>w+-d;9qVb!;?1} not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19325_failAssert0null19857_failAssert0_literalMutationString21712_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(".[2g^3Eei6CMO{]CdEQjyOE");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19325 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19325_failAssert0null19857 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19325_failAssert0null19857_failAssert0_literalMutationString21712 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template .[2g^3Eei6CMO{]CdEQjyOE not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0null19849_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0null19849 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565_failAssert0_literalMutationString21888_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("&qCK|)A;LQG,^x&^8l)V6XC");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multiplextensions.tt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565_failAssert0_literalMutationString21888 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template &qCK|)A;LQG,^x&^8l)V6XC not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19328_failAssert0_literalMutationString19616_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("nW*XBtn$<0]6xd([$24)k]");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19328 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19328_failAssert0_literalMutationString19616 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template nW*XBtn$<0]6xd([$24)k] not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19331_failAssert0_literalMutationString19531_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("={6)6^yAO#Bd:v-4KjhL7s1");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331_failAssert0_literalMutationString19531 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ={6)6^yAO#Bd:v-4KjhL7s1 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0null19848_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0null19848 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19329_failAssert0_literalMutationString19510_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("W72ZD9A.i[3vq$`;Cb!kr:*");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19329 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19329_failAssert0_literalMutationString19510 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template W72ZD9A.i[3vq$`;Cb!kr:* not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add19335_literalMutationString19449_failAssert0_literalMutationString22291_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testMultipleExtensions_add19335__3 = c.compile("multipleextensions.html");
                Mustache m = c.compile("multipleextensions.h]ml");
                StringWriter sw = new StringWriter();
                Writer o_testMultipleExtensions_add19335__8 = m.execute(sw, new Object());
                String o_testMultipleExtensions_add19335__10 = TestUtil.getContents(AmplExtensionTest.root, "multipeextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_add19335_literalMutationString19449 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_add19335_literalMutationString19449_failAssert0_literalMutationString22291 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h]ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add19335_literalMutationString19449_failAssert0_literalMutationString22277_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testMultipleExtensions_add19335__3 = c.compile("");
                Mustache m = c.compile("multipleextensions.h]ml");
                StringWriter sw = new StringWriter();
                Writer o_testMultipleExtensions_add19335__8 = m.execute(sw, new Object());
                String o_testMultipleExtensions_add19335__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_add19335_literalMutationString19449 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_add19335_literalMutationString19449_failAssert0_literalMutationString22277 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h]ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_add19763_failAssert0_literalMutationString21624_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                    Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensi[ns.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19763 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19763_failAssert0_literalMutationString21624 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19331_failAssert0_literalMutationString19531_failAssert0_add23001_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("={6)6^yAO#Bd:v-4KjhL7s1");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331_failAssert0_literalMutationString19531 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331_failAssert0_literalMutationString19531_failAssert0_add23001 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ={6)6^yAO#Bd:v-4KjhL7s1 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add19335_literalMutationString19449_failAssert0null24178_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testMultipleExtensions_add19335__3 = c.compile("multipleextensions.html");
                Mustache m = c.compile("multipleextensions.h]ml");
                StringWriter sw = new StringWriter();
                Writer o_testMultipleExtensions_add19335__8 = m.execute(null, new Object());
                String o_testMultipleExtensions_add19335__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_add19335_literalMutationString19449 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_add19335_literalMutationString19449_failAssert0null24178 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h]ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add19335_literalMutationString19446_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testMultipleExtensions_add19335__3 = c.compile("multipleextensions.html");
            Mustache m = c.compile("K9A|fY%?VQF{@O^0k9D*5_|");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleExtensions_add19335__8 = m.execute(sw, new Object());
            String o_testMultipleExtensions_add19335__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_add19335_literalMutationString19446 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template K9A|fY%?VQF{@O^0k9D*5_| not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565_failAssert0_literalMutationString21879_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("&qCK|)A;LQjG,^x&^8l)V6XC");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multiplextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565_failAssert0_literalMutationString21879 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template &qCK|)A;LQjG,^x&^8l)V6XC not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add19335_literalMutationString19449_failAssert0_add23577_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testMultipleExtensions_add19335__3 = c.compile("multipleextensions.html");
                Mustache m = c.compile("multipleextensions.h]ml");
                StringWriter sw = new StringWriter();
                Writer o_testMultipleExtensions_add19335__8 = m.execute(sw, new Object());
                String o_testMultipleExtensions_add19335__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_add19335_literalMutationString19449 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_add19335_literalMutationString19449_failAssert0_add23577 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h]ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_literalMutationString19525_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "fr#.[qnT&gj$*4w}wEG;$i");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_literalMutationString19525 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19331_failAssert0_add19769_failAssert0_literalMutationString22102_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("z]#v((H49WH t(ww:7T{^d6");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331_failAssert0_add19769 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331_failAssert0_add19769_failAssert0_literalMutationString22102 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template z]#v((H49WH t(ww:7T{^d6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add19335_literalMutationString19449_failAssert0_add23574_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache o_testMultipleExtensions_add19335__3 = c.compile("multipleextensions.html");
                Mustache m = c.compile("multipleextensions.h]ml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                Writer o_testMultipleExtensions_add19335__8 = m.execute(sw, new Object());
                String o_testMultipleExtensions_add19335__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_add19335_literalMutationString19449 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_add19335_literalMutationString19449_failAssert0_add23574 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h]ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19323() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testMultipleExtensions_literalMutationString19323__7 = m.execute(sw, new Object());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleExtensions_literalMutationString19323__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleExtensions_literalMutationString19323__7)).toString());
        String o_testMultipleExtensions_literalMutationString19323__9 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
        Assert.assertEquals("foobar", o_testMultipleExtensions_literalMutationString19323__9);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleExtensions_literalMutationString19323__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleExtensions_literalMutationString19323__7)).toString());
        Assert.assertEquals("foobar", o_testMultipleExtensions_literalMutationString19323__9);
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19333_failAssert0_literalMutationString19586_failAssert0_literalMutationString21200_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("`&Fe1W`glmjTo}zFTIG?QQa");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "3_pv.T`U=}!$ciS@[*=_Ri.");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19333 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19333_failAssert0_literalMutationString19586 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19333_failAssert0_literalMutationString19586_failAssert0_literalMutationString21200 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `&Fe1W`glmjTo}zFTIG?QQa not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19331_failAssert0_literalMutationString19531_failAssert0_literalMutationString21040_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("={6)6^yAO#Bd:v-4Kj]hL7s1");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331_failAssert0_literalMutationString19531 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19331_failAssert0_literalMutationString19531_failAssert0_literalMutationString21040 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ={6)6^yAO#Bd:v-4Kj]hL7s1 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_literalMutationString19522_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_literalMutationString19522 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensionsnull19342_failAssert0_literalMutationString19481_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("${xTs=FNA#=9ZyJn+u9T3HD");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensionsnull19342 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensionsnull19342_failAssert0_literalMutationString19481 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ${xTs=FNA#=9ZyJn+u9T3HD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_add19764_failAssert0null24159_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19764 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19764_failAssert0null24159 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565_failAssert0null24076_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("&qCK|)A;LQG,^x&^8l)V6XC");
                    StringWriter sw = new StringWriter();
                    m.execute(null, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multiplextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565_failAssert0null24076 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template &qCK|)A;LQG,^x&^8l)V6XC not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565_failAssert0_add23398_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("&qCK|)A;LQG,^x&^8l)V6XC");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multiplextensions.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565_failAssert0_add23398 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template &qCK|)A;LQG,^x&^8l)V6XC not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565_failAssert0_add23396_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("&qCK|)A;LQG,^x&^8l)V6XC");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multiplextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19334_failAssert0_literalMutationString19565_failAssert0_add23396 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template &qCK|)A;LQG,^x&^8l)V6XC not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_add19765_failAssert0_literalMutationString21589_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile(">L`@]a{+KzA?#h@O?_bS/CD");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19765 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19765_failAssert0_literalMutationString21589 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?_bS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString19327_failAssert0_add19763_failAssert0_add23257_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                    Mustache m = c.compile(">L`@]a{+KzA?#h@O?ibS/CD");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object());
                    m.execute(sw, new Object());
                    TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                    sw.toString();
                    org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19763 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString19327_failAssert0_add19763_failAssert0_add23257 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >L`@]a{+KzA?#h@O?ibS/CD not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString8_failAssert0_literalMutationString1496_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("g5^) )D(L%9m@x5oOJX");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString8 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString8_failAssert0_literalMutationString1496 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g5^) )D(L%9m@x5oOJX not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString1() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString1__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString1__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString1__7)).toString());
        String o_testSubBlockCaching_literalMutationString1__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString1__12);
        sw.toString();
        sw.toString();
        sw.toString();
        sw.toString();
        m = c.compile("subblockchild2.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild2.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString1__18 = m.execute(sw, new Object() {});
        Assert.assertEquals("second\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString1__18)).getBuffer())).toString());
        Assert.assertEquals("second\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString1__18)).toString());
        String o_testSubBlockCaching_literalMutationString1__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString1__23);
        m = c.compile("subblockchild1.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString1__29 = m.execute(sw, new Object() {});
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString1__29)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString1__29)).toString());
        String o_testSubBlockCaching_literalMutationString1__34 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString1__34);
        String o_testSubBlockCaching_literalMutationString1__36 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString1__36);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString1__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString1__7)).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString1__12);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("second\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString1__18)).getBuffer())).toString());
        Assert.assertEquals("second\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString1__18)).toString());
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString1__23);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString1__29)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString1__29)).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString1__34);
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString1__36);
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0null5025_failAssert0_add14608_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("{!j(za.D7XoVR^QUzGQ");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0null5025 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0null5025_failAssert0_add14608 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {!j(za.D7XoVR^QUzGQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0null5025_failAssert0_literalMutationString7611_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("{!j(za.D7XoVR^QUzGQ");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile("s&};w$ 0o79 6b]w3j(");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0null5025 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0null5025_failAssert0_literalMutationString7611 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {!j(za.D7XoVR^QUzGQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString15_failAssert0null5141_failAssert0null17752_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subblockchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("q@B`Hgr4DEt9cV;r9%6");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(null, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString15_failAssert0null5141 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString15_failAssert0null5141_failAssert0null17752 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template q@B`Hgr4DEt9cV;r9%6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString30_failAssert0null4836_failAssert0_literalMutationString8188_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("r6/76<oD)^hwEt|E`vx");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile("subblockchil4d1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString30 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString30_failAssert0null4836 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString30_failAssert0null4836_failAssert0_literalMutationString8188 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template r6/76<oD)^hwEt|E`vx not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString25() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("subblockchild1.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString25__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString25__7)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString25__7)).toString());
        String o_testSubBlockCaching_literalMutationString25__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString25__12);
        sw.toString();
        sw.toString();
        sw.toString();
        sw.toString();
        m = c.compile("subblockchild2.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild2.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString25__18 = m.execute(sw, new Object() {});
        Assert.assertEquals("second\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString25__18)).getBuffer())).toString());
        Assert.assertEquals("second\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString25__18)).toString());
        String o_testSubBlockCaching_literalMutationString25__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString25__23);
        m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString25__29 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString25__29)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString25__29)).toString());
        String o_testSubBlockCaching_literalMutationString25__34 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString25__34);
        String o_testSubBlockCaching_literalMutationString25__36 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString25__36);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString25__7)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString25__7)).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString25__12);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("second\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString25__18)).getBuffer())).toString());
        Assert.assertEquals("second\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString25__18)).toString());
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString25__23);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString25__29)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString25__29)).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString25__34);
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString25__36);
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString2249_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("{!j(za.D7XoVR^QzGQ");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_literalMutationString2249 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {!j(za.D7XoVR^QzGQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCachingnull63_failAssert0null4798_failAssert0_literalMutationString8014_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("(n[nKDP`UR-uliT]f?>");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("subblockchild2.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCachingnull63 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testSubBlockCachingnull63_failAssert0null4798 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSubBlockCachingnull63_failAssert0null4798_failAssert0_literalMutationString8014 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template (n[nKDP`UR-uliT]f?> not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString15_failAssert0_add4372_failAssert0_literalMutationString12128_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subblockchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("q@B`Hgr4DEt9cV;r9%6");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString15_failAssert0_add4372 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString15_failAssert0_add4372_failAssert0_literalMutationString12128 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template q@B`Hgr4DEt9cV;r9%6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("{!j(za.D7XoVR^QUzGQ");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("subblockchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {!j(za.D7XoVR^QUzGQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString15_failAssert0null5141_failAssert0_literalMutationString8537_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subblockchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("q@B`Hgr4DEt9cV;r9%6");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "sub9lockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString15_failAssert0null5141 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString15_failAssert0null5141_failAssert0_literalMutationString8537 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template q@B`Hgr4DEt9cV;r9%6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_remove53_literalMutationString270_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubBlockCaching_remove53__7 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove53__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("6[S_9_/^I,G m&;9<_i");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_remove53__18 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove53__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            String o_testSubBlockCaching_remove53__29 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            String o_testSubBlockCaching_remove53__31 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_remove53_literalMutationString270 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 6[S_9_/^I,G m&;9<_i not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_remove49_literalMutationString396_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            String o_testSubBlockCaching_remove49__7 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("YI+z13@]uA||:bXssHQ");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_remove49__13 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove49__18 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_remove49__24 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove49__29 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            String o_testSubBlockCaching_remove49__31 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_remove49_literalMutationString396 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template YI+z13@]uA||:bXssHQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0_add4204_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("{!j(za.D7XoVR^QUzGQ");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0_add4204 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {!j(za.D7XoVR^QUzGQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString5_failAssert0null5025_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("{!j(za.D7XoVR^QUzGQ");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString5 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString5_failAssert0null5025 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {!j(za.D7XoVR^QUzGQ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString1_remove4427_add13249() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString1_remove4427_add13249__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString1_remove4427_add13249__7)).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString1_remove4427_add13249__7)).getBuffer())).toString());
        Writer o_testSubBlockCaching_literalMutationString1__7 = m.execute(sw, new Object() {});
        String o_testSubBlockCaching_literalMutationString1__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString1__12);
        sw.toString();
        sw.toString();
        m = c.compile("subblockchild2.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild2.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString1__18 = m.execute(sw, new Object() {});
        String o_testSubBlockCaching_literalMutationString1__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString1__23);
        m = c.compile("subblockchild1.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString1__29 = m.execute(sw, new Object() {});
        String o_testSubBlockCaching_literalMutationString1__34 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString1__34);
        String o_testSubBlockCaching_literalMutationString1__36 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString1__36);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString1_remove4427_add13249__7)).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\nbox.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString1_remove4427_add13249__7)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString1__12);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString1__23);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString1__34);
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString1__36);
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString32_failAssert0null4936_failAssert0_literalMutationString9062_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("#EI4.At!{kF[-I0E@|!");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile(null);
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString32 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString32_failAssert0null4936 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString32_failAssert0null4936_failAssert0_literalMutationString9062 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #EI4.At!{kF[-I0E@|! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString15_failAssert0_add4372_failAssert0_add16346_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subblockchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("q@B`Hgr4DEt9cV;r9%6");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                    sw.toString();
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString15_failAssert0_add4372 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString15_failAssert0_add4372_failAssert0_add16346 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template q@B`Hgr4DEt9cV;r9%6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString29_failAssert0_add4026_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("]p&KS5zAK>!L|5+l@Pv");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString29 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString29_failAssert0_add4026 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ]p&KS5zAK>!L|5+l@Pv not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString29_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("subblockchild2.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("]p&KS5zAK>!L|5+l@Pv");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString29 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ]p&KS5zAK>!L|5+l@Pv not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_remove53null4550_failAssert0_literalMutationString8038_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("I(Wtq9}})?1j;0(Tn9W");
                StringWriter sw = new StringWriter();
                Writer o_testSubBlockCaching_remove53__7 = m.execute(sw, new Object() {});
                String o_testSubBlockCaching_remove53__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                sw.toString();
                sw.toString();
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                Writer o_testSubBlockCaching_remove53__18 = m.execute(sw, new Object() {});
                String o_testSubBlockCaching_remove53__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                String o_testSubBlockCaching_remove53__29 = TestUtil.getContents(AmplExtensionTest.root, null);
                String o_testSubBlockCaching_remove53__31 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                org.junit.Assert.fail("testSubBlockCaching_remove53null4550 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testSubBlockCaching_remove53null4550_failAssert0_literalMutationString8038 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template I(Wtq9}})?1j;0(Tn9W not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString15_failAssert0_add4372_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("q@B`Hgr4DEt9cV;r9%6");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString15 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString15_failAssert0_add4372 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template q@B`Hgr4DEt9cV;r9%6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_remove53_add2829_literalMutationString6571_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("-lxyWP[r&!;HrHHmf[g");
            StringWriter sw = new StringWriter();
            Writer o_testSubBlockCaching_remove53__7 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove53__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("subblockchild2.html");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_remove53__18 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove53__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            String o_testSubBlockCaching_remove53__29 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            String o_testSubBlockCaching_remove53__31 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_remove53_add2829_literalMutationString6571 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -lxyWP[r&!;HrHHmf[g not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString15_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("q@B`Hgr4DEt9cV;r9%6");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString15 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template q@B`Hgr4DEt9cV;r9%6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString15_failAssert0null5141_failAssert0_add14998_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                    Mustache m = c.compile("subblockchild1.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    m = c.compile("q@B`Hgr4DEt9cV;r9%6");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, null);
                    sw.toString();
                    c.compile("subblockchild1.html");
                    m = c.compile("subblockchild1.html");
                    sw = new StringWriter();
                    m.execute(sw, new Object() {});
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                    sw.toString();
                    org.junit.Assert.fail("testSubBlockCaching_literalMutationString15 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString15_failAssert0null5141 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString15_failAssert0null5141_failAssert0_add14998 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template q@B`Hgr4DEt9cV;r9%6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString13() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("subblockchild1.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString13__7 = m.execute(sw, new Object() {});
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString13__7)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString13__7)).toString());
        String o_testSubBlockCaching_literalMutationString13__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString13__12);
        sw.toString();
        sw.toString();
        sw.toString();
        sw.toString();
        m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString13__18 = m.execute(sw, new Object() {});
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString13__18)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString13__18)).toString());
        String o_testSubBlockCaching_literalMutationString13__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString13__23);
        m = c.compile("subblockchild1.html");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        sw = new StringWriter();
        Writer o_testSubBlockCaching_literalMutationString13__29 = m.execute(sw, new Object() {});
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString13__29)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString13__29)).toString());
        String o_testSubBlockCaching_literalMutationString13__34 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString13__34);
        String o_testSubBlockCaching_literalMutationString13__36 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString13__36);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString13__7)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString13__7)).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString13__12);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString13__18)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString13__18)).toString());
        Assert.assertEquals("second\n\n", o_testSubBlockCaching_literalMutationString13__23);
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("subblockchild1.html", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("first\n\n", ((StringBuffer) (((StringWriter) (o_testSubBlockCaching_literalMutationString13__29)).getBuffer())).toString());
        Assert.assertEquals("first\n\n", ((StringWriter) (o_testSubBlockCaching_literalMutationString13__29)).toString());
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString13__34);
        Assert.assertEquals("first\n\n", o_testSubBlockCaching_literalMutationString13__36);
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_remove51_literalMutationString353_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubBlockCaching_remove51__7 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove51__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("Kn/-Xp&^.)4<q_&K(SF");
            sw = new StringWriter();
            String o_testSubBlockCaching_remove51__18 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_remove51__24 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove51__29 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            String o_testSubBlockCaching_remove51__31 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_remove51_literalMutationString353 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Kn/-Xp&^.)4<q_&K(SF not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString15_failAssert0null5141_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("q@B`Hgr4DEt9cV;r9%6");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString15 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString15_failAssert0null5141 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template q@B`Hgr4DEt9cV;r9%6 not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplExtensionTest.root = (new File(file, "sub.html").exists()) ? file : new File("src/test/resources");
    }
}

