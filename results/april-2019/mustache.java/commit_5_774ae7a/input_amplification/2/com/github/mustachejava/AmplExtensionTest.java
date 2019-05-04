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
    public void testSubnull6593_literalMutationString6857_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("! qnz6=0");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testSubnull6593__9 = scope.put("name", null);
            Object o_testSubnull6593__10 = scope.put("randomid", "asdlkfj");
            Writer o_testSubnull6593__11 = m.execute(sw, scope);
            String o_testSubnull6593__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSubnull6593_literalMutationString6857 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ! qnz6=0 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString6550_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("`#v/@xt ");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("name", "Sam");
            scope.put("randomid", "asdlkfj");
            m.execute(sw, scope);
            TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
            sw.toString();
            org.junit.Assert.fail("testSub_literalMutationString6550 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `#v/@xt  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString6550_failAssert0_add9394_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("`#v/@xt ");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("randomid", "asdlkfj");
                scope.put("randomid", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString6550 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString6550_failAssert0_add9394 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `#v/@xt  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString6546() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Map scope = new HashMap();
        Object o_testSub_literalMutationString6546__9 = scope.put("name", "Sam");
        Assert.assertNull(o_testSub_literalMutationString6546__9);
        Object o_testSub_literalMutationString6546__10 = scope.put("randomid", "asdlkfj");
        Assert.assertNull(o_testSub_literalMutationString6546__10);
        Writer o_testSub_literalMutationString6546__11 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSub_literalMutationString6546__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSub_literalMutationString6546__11)).toString());
        String o_testSub_literalMutationString6546__12 = TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
        Assert.assertEquals("<html>\n<head>\n<title>Subtitle</title>\n<script src=\"jquery.min.js\"></script>\n</head>\n<body>\n<div class=\"body\">\n<div id=\"asdlkfj\">\nHello, Sam!\n</div>\n</div>\n<div class=\"footer\">\n</div>\n</body>\n</html>", o_testSub_literalMutationString6546__12);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertNull(o_testSub_literalMutationString6546__9);
        Assert.assertNull(o_testSub_literalMutationString6546__10);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testSub_literalMutationString6546__11)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testSub_literalMutationString6546__11)).toString());
        Assert.assertEquals("<html>\n<head>\n<title>Subtitle</title>\n<script src=\"jquery.min.js\"></script>\n</head>\n<body>\n<div class=\"body\">\n<div id=\"asdlkfj\">\nHello, Sam!\n</div>\n</div>\n<div class=\"footer\">\n</div>\n</body>\n</html>", o_testSub_literalMutationString6546__12);
    }

    @Test(timeout = 10000)
    public void testSub_literalMutationString6550_failAssert0_literalMutationString8373_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("`#v/@xt ");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("name", "Sam");
                scope.put("page1.txt", "asdlkfj");
                m.execute(sw, scope);
                TestUtil.getContents(AmplExtensionTest.root, "sub.txt");
                sw.toString();
                org.junit.Assert.fail("testSub_literalMutationString6550 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSub_literalMutationString6550_failAssert0_literalMutationString8373 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `#v/@xt  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add5596_literalMutationString5700_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache o_testMultipleExtensions_add5596__3 = c.compile("V=/6`#K(?FGwG^q_=yzg69%");
            Mustache m = c.compile("multipleextensions.html");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleExtensions_add5596__8 = m.execute(sw, new Object());
            String o_testMultipleExtensions_add5596__10 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_add5596_literalMutationString5700 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template V=/6`#K(?FGwG^q_=yzg69% not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5589_failAssert0_literalMutationString5777_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions>.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "mulLipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589_failAssert0_literalMutationString5777 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5589_failAssert0_literalMutationString5774_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions>.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multiplee!xtensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589_failAssert0_literalMutationString5774 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5586_failAssert0_literalMutationString5802_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "X8(>i0QLEQ|Na5l_*ok<jA");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586_failAssert0_literalMutationString5802 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5587_failAssert0_literalMutationString5876_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextenson .html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5587 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5587_failAssert0_literalMutationString5876 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextenson .html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensionsnull5603_failAssert0_literalMutationString5751_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("Z6-:rq/GL#8YTv&(1iR#K/1");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensionsnull5603 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensionsnull5603_failAssert0_literalMutationString5751 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Z6-:rq/GL#8YTv&(1iR#K/1 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5590_failAssert0_literalMutationString5831_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("LwqY*d6G[aO8n|(<9I>y]$!");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5590 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5590_failAssert0_literalMutationString5831 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template LwqY*d6G[aO8n|(<9I>y]$! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5586_failAssert0_literalMutationString5795_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFs3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586_failAssert0_literalMutationString5795 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFs3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5586_failAssert0_literalMutationString5797_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586_failAssert0_literalMutationString5797 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5594_failAssert0_literalMutationString5840_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("O@)@{T3[#kI-vWCKD7;P[:-");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "Ofg0Wvq)hEKs.mcH$Q]U6`");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5594 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5594_failAssert0_literalMutationString5840 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template O@)@{T3[#kI-vWCKD7;P[:- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_add5597_literalMutationString5688_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("zDM2KlT07d%@qHU;uJHecV#");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleExtensions_add5597__7 = m.execute(sw, new Object());
            Writer o_testMultipleExtensions_add5597__9 = m.execute(sw, new Object());
            String o_testMultipleExtensions_add5597__11 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_add5597_literalMutationString5688 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template zDM2KlT07d%@qHU;uJHecV# not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5586_failAssert0_add6029_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586_failAssert0_add6029 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5589_failAssert0null6106_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions>.html");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589_failAssert0null6106 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensionsnull5602_failAssert0_literalMutationString5736_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("g&%#E-YpN8=G*Cn02Ry>-^$");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensionsnull5602 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testMultipleExtensionsnull5602_failAssert0_literalMutationString5736 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g&%#E-YpN8=G*Cn02Ry>-^$ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5589_failAssert0_add6023_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions>.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589_failAssert0_add6023 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5589_failAssert0null6107_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions>.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589_failAssert0null6107 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5586_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5586_failAssert0_add6030_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586_failAssert0_add6030 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5592_failAssert0_literalMutationString5780_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipl^extensions.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "page1.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5592 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5592_failAssert0_literalMutationString5780 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipl^extensions.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5586_failAssert0_add6031_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586_failAssert0_add6031 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5589_failAssert0_add6021_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions>.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589_failAssert0_add6021 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5586_failAssert0_add6033_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586_failAssert0_add6033 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5589_failAssert0_add6020_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions>.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589_failAssert0_add6020 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5586_failAssert0_literalMutationString5791_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYb@otsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586_failAssert0_literalMutationString5791 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYb@otsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5586_failAssert0_add6032_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586_failAssert0_add6032 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5593_failAssert0_literalMutationString5850_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions.h|ml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "mltipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5593 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5593_failAssert0_literalMutationString5850 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions.h|ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5586_failAssert0null6113_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, null);
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586_failAssert0null6113 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5589_failAssert0_literalMutationString5773_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("multipleextensions>.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589_failAssert0_literalMutationString5773 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5589_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("multipleextensions>.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object());
            TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
            sw.toString();
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5589 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template multipleextensions>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5585() throws MustacheException, IOException, InterruptedException, ExecutionException {
        MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("");
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        Writer o_testMultipleExtensions_literalMutationString5585__7 = m.execute(sw, new Object());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleExtensions_literalMutationString5585__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleExtensions_literalMutationString5585__7)).toString());
        String o_testMultipleExtensions_literalMutationString5585__9 = TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
        Assert.assertEquals("foobar", o_testMultipleExtensions_literalMutationString5585__9);
        sw.toString();
        Assert.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        Assert.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Assert.assertFalse(((DefaultMustache) (m)).isRecursive());
        Assert.assertEquals("", ((DefaultMustache) (m)).getName());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testMultipleExtensions_literalMutationString5585__7)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testMultipleExtensions_literalMutationString5585__7)).toString());
        Assert.assertEquals("foobar", o_testMultipleExtensions_literalMutationString5585__9);
    }

    @Test(timeout = 10000)
    public void testMultipleExtensions_literalMutationString5586_failAssert0null6112_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("-DG^_<CFsw3r[e8NYbEotsy");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object());
                TestUtil.getContents(AmplExtensionTest.root, "multipleextensions.txt");
                sw.toString();
                org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testMultipleExtensions_literalMutationString5586_failAssert0null6112 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -DG^_<CFsw3r[e8NYbEotsy not found", expected.getMessage());
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
    public void testSubBlockCaching_literalMutationString28_failAssert0_literalMutationString2402_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                m = c.compile("c[c&=M-zk($_3?gQQmc");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "*:s=Lld/(_ x!ORL*y");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString28 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString28_failAssert0_literalMutationString2402 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template c[c&=M-zk($_3?gQQmc not found", expected.getMessage());
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
    public void testSubBlockCaching_remove54_literalMutationString312_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            Writer o_testSubBlockCaching_remove54__7 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove54__12 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("subblockchild2.ht<ml");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_remove54__18 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove54__23 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
            sw.toString();
            m = c.compile("subblockchild1.html");
            sw = new StringWriter();
            Writer o_testSubBlockCaching_remove54__29 = m.execute(sw, new Object() {});
            String o_testSubBlockCaching_remove54__33 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            String o_testSubBlockCaching_remove54__35 = TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_remove54_literalMutationString312 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template subblockchild2.ht<ml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString18_failAssert0null4990_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("A0Yt@}XY] &yec*mAg]");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString18 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString18_failAssert0null4990 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A0Yt@}XY] &yec*mAg] not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString18_failAssert0_add4146_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("A0Yt@}XY] &yec*mAg]");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString18 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString18_failAssert0_add4146 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A0Yt@}XY] &yec*mAg] not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString28_failAssert0_add4246_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("c[c&=M-zk($_3?gQQmc");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString28 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString28_failAssert0_add4246 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template c[c&=M-zk($_3?gQQmc not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4_failAssert0_literalMutationString1807_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("#|+j^,pi4ZW!0^5@t*x");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "suqblockchild1.txt");
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
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4_failAssert0_literalMutationString1807 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #|+j^,pi4ZW!0^5@t*x not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4_failAssert0null4921_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("#|+j^,pi4ZW!0^5@t*x");
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
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4_failAssert0null4921 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #|+j^,pi4ZW!0^5@t*x not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString28_failAssert0null5057_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
                m = c.compile("c[c&=M-zk($_3?gQQmc");
                sw = new StringWriter();
                m.execute(null, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString28 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString28_failAssert0null5057 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template c[c&=M-zk($_3?gQQmc not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4_failAssert0_add4048_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("#|+j^,pi4ZW!0^5@t*x");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("subblockchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.txt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString4 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4_failAssert0_add4048 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #|+j^,pi4ZW!0^5@t*x not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString4_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("#|+j^,pi4ZW!0^5@t*x");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString4 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #|+j^,pi4ZW!0^5@t*x not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString18_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
            Mustache m = c.compile("subblockchild1.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            m = c.compile("A0Yt@}XY] &yec*mAg]");
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
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString18 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A0Yt@}XY] &yec*mAg] not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSubBlockCaching_literalMutationString28_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
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
            m = c.compile("c[c&=M-zk($_3?gQQmc");
            sw = new StringWriter();
            m.execute(sw, new Object() {});
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
            sw.toString();
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString28 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template c[c&=M-zk($_3?gQQmc not found", expected.getMessage());
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
    public void testSubBlockCaching_literalMutationString24_failAssert0_literalMutationString2338_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(AmplExtensionTest.root);
                Mustache m = c.compile("subblockchild1.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                m = c.compile("s:ubblockchild2.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild2.ptxt");
                sw.toString();
                m = c.compile("subblockchild1.html");
                sw = new StringWriter();
                m.execute(sw, new Object() {});
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                TestUtil.getContents(AmplExtensionTest.root, "subblockchild1.txt");
                sw.toString();
                org.junit.Assert.fail("testSubBlockCaching_literalMutationString24 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testSubBlockCaching_literalMutationString24_failAssert0_literalMutationString2338 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template s:ubblockchild2.html not found", expected.getMessage());
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File file = new File("compiler/src/test/resources");
        AmplExtensionTest.root = (new File(file, "sub.html").exists()) ? file : new File("src/test/resources");
    }
}

