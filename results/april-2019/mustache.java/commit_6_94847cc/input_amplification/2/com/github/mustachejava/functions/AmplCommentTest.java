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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class AmplCommentTest {
    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4424_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("AEH>%6h3Q(`J");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("ignored", "ignored");
            m.execute(sw, scope);
            TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_literalMutationString4424 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AEH>%6h3Q(`J not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4423_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("comment.%html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("ignored", "ignored");
            m.execute(sw, scope);
            TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_literalMutationString4423 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comment.%html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4424_failAssert0_add6361_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                getRoot("comment.html");
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("AEH>%6h3Q(`J");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "comment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString4424 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString4424_failAssert0_add6361 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AEH>%6h3Q(`J not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4424_failAssert0_add6364_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("AEH>%6h3Q(`J");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                m.execute(sw, scope);
                TestUtil.getContents(root, "comment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString4424 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString4424_failAssert0_add6364 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AEH>%6h3Q(`J not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4424_failAssert0_literalMutationString5842_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("AEH>%6h3Q(`J");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "com[ment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString4424 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString4424_failAssert0_literalMutationString5842 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AEH>%6h3Q(`J not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4424_failAssert0null6750_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("AEH>%6h3Q(`J");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put(null, "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "comment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString4424 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString4424_failAssert0null6750 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AEH>%6h3Q(`J not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4424_failAssert0null6752_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("AEH>%6h3Q(`J");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(null, scope);
                TestUtil.getContents(root, "comment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString4424 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString4424_failAssert0null6752 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AEH>%6h3Q(`J not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4424_failAssert0_literalMutationString5838_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("AEH>%6h3Q(`J");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "inored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "comment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString4424 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString4424_failAssert0_literalMutationString5838 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template AEH>%6h3Q(`J not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_add4443_literalMutationString4788_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            getRoot("comment.html");
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("^Vt`/%HTO-M?");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentBlock_add4443__12 = scope.put("ignored", "ignored");
            Writer o_testCommentBlock_add4443__13 = m.execute(sw, scope);
            String o_testCommentBlock_add4443__14 = TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_add4443_literalMutationString4788 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^Vt`/%HTO-M? not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4423_failAssert0_literalMutationString5883_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commen.%html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "comment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString4423 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString4423_failAssert0_literalMutationString5883 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commen.%html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4423_failAssert0_add6379_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("comment.%html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "comment.txt");
                TestUtil.getContents(root, "comment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString4423 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString4423_failAssert0_add6379 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comment.%html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4439_failAssert0_literalMutationString5794_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile(" i{7L/K&EdbS");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "cBomment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString4439 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString4439_failAssert0_literalMutationString5794 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template  i{7L/K&EdbS not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_add4444_literalMutationString4669_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache o_testCommentBlock_add4444__5 = c.compile("sa$KUyp0y,|3");
            Mustache m = c.compile("comment.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentBlock_add4444__12 = scope.put("ignored", "ignored");
            Writer o_testCommentBlock_add4444__13 = m.execute(sw, scope);
            String o_testCommentBlock_add4444__14 = TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_add4444_literalMutationString4669 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template sa$KUyp0y,|3 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4423_failAssert0null6771_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("comment.%html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, null);
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString4423 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString4423_failAssert0null6771 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comment.%html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString4419() throws MustacheException, IOException, InterruptedException, ExecutionException {
        File root = getRoot("comment.html");
        MustacheFactory c = new DefaultMustacheFactory(root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map scope = new HashMap();
        Object o_testCommentBlock_literalMutationString4419__11 = scope.put("ignored", "ignored");
        Assert.assertNull(o_testCommentBlock_literalMutationString4419__11);
        Writer o_testCommentBlock_literalMutationString4419__12 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testCommentBlock_literalMutationString4419__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testCommentBlock_literalMutationString4419__12)).toString());
        String o_testCommentBlock_literalMutationString4419__13 = TestUtil.getContents(root, "comment.txt");
        Assert.assertEquals("BeforeAfter\n", o_testCommentBlock_literalMutationString4419__13);
        sw.toString();
        Assert.assertNull(o_testCommentBlock_literalMutationString4419__11);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testCommentBlock_literalMutationString4419__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testCommentBlock_literalMutationString4419__12)).toString());
        Assert.assertEquals("BeforeAfter\n", o_testCommentBlock_literalMutationString4419__13);
    }

    @Test(timeout = 10000)
    public void testCommentBlock_remove4450_literalMutationString4867_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("|omment.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentBlock_remove4450__11 = scope.put("ignored", "ignored");
            String o_testCommentBlock_remove4450__12 = TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_remove4450_literalMutationString4867 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template |omment.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1585() throws MustacheException, IOException, InterruptedException, ExecutionException {
        File root = getRoot("commentinline.html");
        MustacheFactory c = new DefaultMustacheFactory(root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map scope = new HashMap();
        Object o_testCommentInline_literalMutationString1585__11 = scope.put("title", "A Comedy of Errors");
        Assert.assertNull(o_testCommentInline_literalMutationString1585__11);
        Writer o_testCommentInline_literalMutationString1585__12 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testCommentInline_literalMutationString1585__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testCommentInline_literalMutationString1585__12)).toString());
        String o_testCommentInline_literalMutationString1585__13 = TestUtil.getContents(root, "commentinline.txt");
        Assert.assertEquals("<h1>A Comedy of Errors</h1>\n", o_testCommentInline_literalMutationString1585__13);
        sw.toString();
        Assert.assertNull(o_testCommentInline_literalMutationString1585__11);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testCommentInline_literalMutationString1585__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testCommentInline_literalMutationString1585__12)).toString());
        Assert.assertEquals("<h1>A Comedy of Errors</h1>\n", o_testCommentInline_literalMutationString1585__13);
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1588_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("2#mhSpF]({0t30KY9n");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("title", "A Comedy of Errors");
            m.execute(sw, scope);
            TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString1588 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2#mhSpF]({0t30KY9n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1588_failAssert0_add3516_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("2#mhSpF]({0t30KY9n");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("title", "A Comedy of Errors");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString1588 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString1588_failAssert0_add3516 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2#mhSpF]({0t30KY9n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1588_failAssert0_add3513_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("2#mhSpF]({0t30KY9n");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("title", "A Comedy of Errors");
                scope.put("title", "A Comedy of Errors");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString1588 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString1588_failAssert0_add3513 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2#mhSpF]({0t30KY9n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1588_failAssert0_literalMutationString2939_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("2#mhSpF]({0t30KY9n");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("t|itle", "A Comedy of Errors");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString1588 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString1588_failAssert0_literalMutationString2939 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2#mhSpF]({0t30KY9n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1584_failAssert0_literalMutationString3021_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("V&b8r>Ja]34naxqh62");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("title", "A Comedy of Errors");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString1584 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString1584_failAssert0_literalMutationString3021 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template V&b8r>Ja]34naxqh62 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1601_literalMutationString2620_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("commen:tinline.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInline_literalMutationString1601__11 = scope.put("title", "A Comedy of rrors");
            Writer o_testCommentInline_literalMutationString1601__12 = m.execute(sw, scope);
            String o_testCommentInline_literalMutationString1601__13 = TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString1601_literalMutationString2620 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commen:tinline.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1588_failAssert0_literalMutationString2949_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("2#mhSpF]({0t30KY9n");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("title", "A Comedy of Errors");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commentinline.xt");
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString1588 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString1588_failAssert0_literalMutationString2949 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 2#mhSpF]({0t30KY9n not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString8() throws MustacheException, IOException, InterruptedException, ExecutionException {
        File root = getRoot("commentWithinExtendCodeBlock.html");
        MustacheFactory c = new DefaultMustacheFactory(root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString8__9 = m.execute(sw, Collections.emptyList());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testInlineCommentWithinExtendCodeBlock_literalMutationString8__9)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testInlineCommentWithinExtendCodeBlock_literalMutationString8__9)).toString());
        String o_testInlineCommentWithinExtendCodeBlock_literalMutationString8__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
        Assert.assertEquals("<h1>Just for the fun of it</h1>\n", o_testInlineCommentWithinExtendCodeBlock_literalMutationString8__11);
        sw.toString();
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testInlineCommentWithinExtendCodeBlock_literalMutationString8__9)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testInlineCommentWithinExtendCodeBlock_literalMutationString8__9)).toString());
        Assert.assertEquals("<h1>Just for the fun of it</h1>\n", o_testInlineCommentWithinExtendCodeBlock_literalMutationString8__11);
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_add20_literalMutationString194_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache o_testInlineCommentWithinExtendCodeBlock_add20__5 = c.compile("x7[c#GSl%;,uh[Or;Z(&{#3k.@p`B:dGr");
            Mustache m = c.compile("commentWithinExtendCodeBlock.html");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_add20__10 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_add20__12 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_add20_literalMutationString194 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template x7[c#GSl%;,uh[Or;Z(&{#3k.@p`B:dGr not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString9_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("-W0)7A3IA`eyMh<O f|<f*F!DW5(JGrg|");
            StringWriter sw = new StringWriter();
            m.execute(sw, Collections.emptyList());
            TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString9 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -W0)7A3IA`eyMh<O f|<f*F!DW5(JGrg| not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlocknull28_failAssert0_literalMutationString471_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("rE>|m!@I`wDF@5MM[CUBg1[AlQUgC.*>e");
                StringWriter sw = new StringWriter();
                m.execute(null, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlocknull28 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlocknull28_failAssert0_literalMutationString471 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString9_failAssert0_add887_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("-W0)7A3IA`eyMh<O f|<f*F!DW5(JGrg|");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString9 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString9_failAssert0_add887 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -W0)7A3IA`eyMh<O f|<f*F!DW5(JGrg| not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_add22_literalMutationString134_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("-wy=!D[RE;qK=BlhcZ0`qgt,Tn>J!1? #");
            StringWriter sw = new StringWriter();
            List<Object> o_testInlineCommentWithinExtendCodeBlock_add22__9 = Collections.emptyList();
            Writer o_testInlineCommentWithinExtendCodeBlock_add22__10 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_add22__12 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_add22_literalMutationString134 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -wy=!D[RE;qK=BlhcZ0`qgt,Tn>J!1? # not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString5_literalMutationString396_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("bundles_post_labels.txt");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("commentWithinExtendCodeBlock.%html");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString5__9 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_literalMutationString5__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString5_literalMutationString396 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExtendCodeBlock.%html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_add20_literalMutationString193_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache o_testInlineCommentWithinExtendCodeBlock_add20__5 = c.compile("comm>ntWithinExtendCodeBlock.html");
            Mustache m = c.compile("commentWithinExtendCodeBlock.html");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_add20__10 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_add20__12 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_add20_literalMutationString193 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comm>ntWithinExtendCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString9_failAssert0_literalMutationString498_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("bundles_post_labels.txt");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("-W0)7A3IA`eyMh<O f|<f*F!DW5(JGrg|");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString9 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString9_failAssert0_literalMutationString498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template -W0)7A3IA`eyMh<O f|<f*F!DW5(JGrg| not found", expected.getMessage());
        }
    }

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

