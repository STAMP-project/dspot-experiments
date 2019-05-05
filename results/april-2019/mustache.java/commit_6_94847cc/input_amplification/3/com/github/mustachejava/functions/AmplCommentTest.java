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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class AmplCommentTest {
    @Test(timeout = 10000)
    public void testCommentBlocknull19445_failAssert0_literalMutationString20553_failAssert0_literalMutationString26370_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("comment.html");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile("?<xLz&w>2!Ky");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("ignored", "ignored");
                    m.execute(sw, scope);
                    TestUtil.getContents(null, "comment.txt");
                    sw.toString();
                    org.junit.Assert.fail("testCommentBlocknull19445 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testCommentBlocknull19445_failAssert0_literalMutationString20553 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlocknull19445_failAssert0_literalMutationString20553_failAssert0_literalMutationString26370 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?<xLz&w>2!Ky not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString19417_literalMutationString20269_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("comme[nt.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentBlock_literalMutationString19417__11 = scope.put("ignred", "ignored");
            Writer o_testCommentBlock_literalMutationString19417__12 = m.execute(sw, scope);
            String o_testCommentBlock_literalMutationString19417__13 = TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_literalMutationString19417_literalMutationString20269 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comme[nt.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString19403_literalMutationString19913_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.tml");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("|*k7^nKVxN<!");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentBlock_literalMutationString19403__11 = scope.put("ignored", "ignored");
            Writer o_testCommentBlock_literalMutationString19403__12 = m.execute(sw, scope);
            String o_testCommentBlock_literalMutationString19403__13 = TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_literalMutationString19403_literalMutationString19913 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template |*k7^nKVxN<! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString19412_failAssert0_add21320_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("^jBOIXG56aXU");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "comment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString19412 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString19412_failAssert0_add21320 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^jBOIXG56aXU not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString19412_failAssert0_literalMutationString20684_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("^jBOIXG56aXU");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commen.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString19412 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString19412_failAssert0_literalMutationString20684 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^jBOIXG56aXU not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString19412_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("^jBOIXG56aXU");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("ignored", "ignored");
            m.execute(sw, scope);
            TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_literalMutationString19412 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ^jBOIXG56aXU not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString19407() throws MustacheException, IOException, InterruptedException, ExecutionException {
        File root = getRoot("comment.html");
        MustacheFactory c = new DefaultMustacheFactory(root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map scope = new HashMap();
        Object o_testCommentBlock_literalMutationString19407__11 = scope.put("ignored", "ignored");
        Assert.assertNull(o_testCommentBlock_literalMutationString19407__11);
        Writer o_testCommentBlock_literalMutationString19407__12 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testCommentBlock_literalMutationString19407__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testCommentBlock_literalMutationString19407__12)).toString());
        String o_testCommentBlock_literalMutationString19407__13 = TestUtil.getContents(root, "comment.txt");
        Assert.assertEquals("BeforeAfter\n", o_testCommentBlock_literalMutationString19407__13);
        sw.toString();
        Assert.assertNull(o_testCommentBlock_literalMutationString19407__11);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testCommentBlock_literalMutationString19407__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testCommentBlock_literalMutationString19407__12)).toString());
        Assert.assertEquals("BeforeAfter\n", o_testCommentBlock_literalMutationString19407__13);
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7914_literalMutationString8892_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("bundles_post_l}bels.txt");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInline_literalMutationString7914__11 = scope.put("title", "A Comedy of Errors");
            Writer o_testCommentInline_literalMutationString7914__12 = m.execute(sw, scope);
            String o_testCommentInline_literalMutationString7914__13 = TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString7914_literalMutationString8892 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bundles_post_l}bels.txt not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7922_literalMutationString8923_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("_@`vBf7r7:O#u2`d4M");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInline_literalMutationString7922__11 = scope.put("title", "");
            Writer o_testCommentInline_literalMutationString7922__12 = m.execute(sw, scope);
            String o_testCommentInline_literalMutationString7922__13 = TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString7922_literalMutationString8923 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template _@`vBf7r7:O#u2`d4M not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7912_failAssert0_literalMutationString9340_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.Yhtml");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("?&*W.&NM 5n[L!x)5_");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("title", "A Comedy of Errors");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString7912 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString7912_failAssert0_literalMutationString9340 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?&*W.&NM 5n[L!x)5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7926_literalMutationString8800_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("`mJ7ADO-zhuXH^0:)K");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInline_literalMutationString7926__11 = scope.put("title", "A Comedy of Erors");
            Writer o_testCommentInline_literalMutationString7926__12 = m.execute(sw, scope);
            String o_testCommentInline_literalMutationString7926__13 = TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString7926_literalMutationString8800 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template `mJ7ADO-zhuXH^0:)K not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7910_failAssert0_add9833_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commen^inline.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("title", "A Comedy of Errors");
                scope.put("title", "A Comedy of Errors");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString7910 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString7910_failAssert0_add9833 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commen^inline.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7915_failAssert0_literalMutationString9217_failAssert0_literalMutationString15248_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("commentinline.html");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile("btwO#+GBbblj cY!^ 6");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("title", "A Comedy of Errors");
                    m.execute(sw, scope);
                    TestUtil.getContents(root, "commentinline.txt");
                    sw.toString();
                    org.junit.Assert.fail("testCommentInline_literalMutationString7915 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testCommentInline_literalMutationString7915_failAssert0_literalMutationString9217 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString7915_failAssert0_literalMutationString9217_failAssert0_literalMutationString15248 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template btwO#+GBbblj cY!^ 6 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7911() throws MustacheException, IOException, InterruptedException, ExecutionException {
        File root = getRoot("commentinline.html");
        MustacheFactory c = new DefaultMustacheFactory(root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Map scope = new HashMap();
        Object o_testCommentInline_literalMutationString7911__11 = scope.put("title", "A Comedy of Errors");
        Assert.assertNull(o_testCommentInline_literalMutationString7911__11);
        Writer o_testCommentInline_literalMutationString7911__12 = m.execute(sw, scope);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testCommentInline_literalMutationString7911__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testCommentInline_literalMutationString7911__12)).toString());
        String o_testCommentInline_literalMutationString7911__13 = TestUtil.getContents(root, "commentinline.txt");
        Assert.assertEquals("<h1>A Comedy of Errors</h1>\n", o_testCommentInline_literalMutationString7911__13);
        sw.toString();
        Assert.assertNull(o_testCommentInline_literalMutationString7911__11);
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testCommentInline_literalMutationString7911__12)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testCommentInline_literalMutationString7911__12)).toString());
        Assert.assertEquals("<h1>A Comedy of Errors</h1>\n", o_testCommentInline_literalMutationString7911__13);
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7912_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("?&*W.&NM 5n[L!x)5_");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("title", "A Comedy of Errors");
            m.execute(sw, scope);
            TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString7912 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?&*W.&NM 5n[L!x)5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7912_failAssert0null10254_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("?&*W.&NM 5n[L!x)5_");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("title", "A Comedy of Errors");
                m.execute(sw, scope);
                TestUtil.getContents(root, null);
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString7912 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString7912_failAssert0null10254 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?&*W.&NM 5n[L!x)5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_remove7940_add9519_literalMutationString11661_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("g&s5E[d32:k+v8w=J|");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Writer o_testCommentInline_remove7940__11 = m.execute(sw, scope);
            String o_testCommentInline_remove7940__12 = TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            sw.toString();
            org.junit.Assert.fail("testCommentInline_remove7940_add9519_literalMutationString11661 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g&s5E[d32:k+v8w=J| not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7912_failAssert0_literalMutationString9340_failAssert0_add17132_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("commentinline.Yhtml");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile("?&*W.&NM 5n[L!x)5_");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("title", "A Comedy of Errors");
                    m.execute(sw, scope);
                    TestUtil.getContents(root, "commentinline.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testCommentInline_literalMutationString7912 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testCommentInline_literalMutationString7912_failAssert0_literalMutationString9340 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString7912_failAssert0_literalMutationString9340_failAssert0_add17132 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?&*W.&NM 5n[L!x)5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInlinenull7944_literalMutationString8105_failAssert0_literalMutationString15039_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("com]mentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentinline>.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testCommentInlinenull7944__11 = scope.put(null, "A Comedy of Errors");
                Writer o_testCommentInlinenull7944__12 = m.execute(sw, scope);
                String o_testCommentInlinenull7944__13 = TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInlinenull7944_literalMutationString8105 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInlinenull7944_literalMutationString8105_failAssert0_literalMutationString15039 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentinline>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInlinenull7944_literalMutationString8105_failAssert0_add17094_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentinline>.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put(null, "A Comedy of Errors");
                Object o_testCommentInlinenull7944__11 = scope.put(null, "A Comedy of Errors");
                Writer o_testCommentInlinenull7944__12 = m.execute(sw, scope);
                String o_testCommentInlinenull7944__13 = TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInlinenull7944_literalMutationString8105 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInlinenull7944_literalMutationString8105_failAssert0_add17094 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentinline>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInlinenull7944_literalMutationString8105_failAssert0_add17098_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentinline>.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testCommentInlinenull7944__11 = scope.put(null, "A Comedy of Errors");
                Writer o_testCommentInlinenull7944__12 = m.execute(sw, scope);
                String o_testCommentInlinenull7944__13 = TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInlinenull7944_literalMutationString8105 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInlinenull7944_literalMutationString8105_failAssert0_add17098 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentinline>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInlinenull7944_literalMutationString8105_failAssert0_literalMutationString15041_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("bundles_post_labels.txt");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentinline>.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testCommentInlinenull7944__11 = scope.put(null, "A Comedy of Errors");
                Writer o_testCommentInlinenull7944__12 = m.execute(sw, scope);
                String o_testCommentInlinenull7944__13 = TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInlinenull7944_literalMutationString8105 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInlinenull7944_literalMutationString8105_failAssert0_literalMutationString15041 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentinline>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7910_failAssert0_literalMutationString9238_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commen^inline.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("title", "K-Sa^&*[zfUP;Bd*!I");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString7910 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString7910_failAssert0_literalMutationString9238 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commen^inline.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInlinenull7944_literalMutationString8105_failAssert0null18638_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentinline>.html");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testCommentInlinenull7944__11 = scope.put(null, "A Comedy of Errors");
                Writer o_testCommentInlinenull7944__12 = m.execute(sw, scope);
                String o_testCommentInlinenull7944__13 = TestUtil.getContents(root, null);
                sw.toString();
                org.junit.Assert.fail("testCommentInlinenull7944_literalMutationString8105 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInlinenull7944_literalMutationString8105_failAssert0null18638 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentinline>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7910_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("commen^inline.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("title", "A Comedy of Errors");
            m.execute(sw, scope);
            TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString7910 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commen^inline.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7912_failAssert0_add9861_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("?&*W.&NM 5n[L!x)5_");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("title", "A Comedy of Errors");
                scope.put("title", "A Comedy of Errors");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString7912 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString7912_failAssert0_add9861 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?&*W.&NM 5n[L!x)5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_add7935_literalMutationString8304_literalMutationString13460_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache o_testCommentInline_add7935__5 = c.compile("#us}?u.8E915E5KF(e(-1S;");
            Mustache m = c.compile("commentinline.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInline_add7935__12 = scope.put("title", "A Comedy of Errors");
            Writer o_testCommentInline_add7935__13 = m.execute(sw, scope);
            String o_testCommentInline_add7935__14 = TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_add7935_literalMutationString8304_literalMutationString13460 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template #us}?u.8E915E5KF(e(-1S; not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7912_failAssert0_literalMutationString9340_failAssert0_literalMutationString15203_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("commentinline.Yhtml");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile("?&*W.&NM 5n[L!x)5_");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("title", "A Comedy of Errors");
                    m.execute(sw, scope);
                    TestUtil.getContents(root, "bundles_post_labels.txt");
                    sw.toString();
                    org.junit.Assert.fail("testCommentInline_literalMutationString7912 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testCommentInline_literalMutationString7912_failAssert0_literalMutationString9340 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString7912_failAssert0_literalMutationString9340_failAssert0_literalMutationString15203 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?&*W.&NM 5n[L!x)5_ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7906null10087_literalMutationString10937_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinlne.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("c ommentinline.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInline_literalMutationString7906__11 = scope.put("title", null);
            Writer o_testCommentInline_literalMutationString7906__12 = m.execute(sw, scope);
            String o_testCommentInline_literalMutationString7906__13 = TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString7906null10087_literalMutationString10937 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template c ommentinline.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInlinenull7944_literalMutationString8105_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("commentinline>.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInlinenull7944__11 = scope.put(null, "A Comedy of Errors");
            Writer o_testCommentInlinenull7944__12 = m.execute(sw, scope);
            String o_testCommentInlinenull7944__13 = TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInlinenull7944_literalMutationString8105 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentinline>.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add923_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add923 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add921_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9");
                StringWriter sw = new StringWriter();
                Collections.emptyList();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add921 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString576_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("comment6ithinExtendCodeBlock.}tml");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString576 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comment6ithinExtendCodeBlock.}tml not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString6null1023_failAssert0_literalMutationString4150_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("j72Z=0)pS0fx$.4=O2?= fL`c7<X,sEBY");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("[L%;cxp(j$7o)q))x@HEo|lc&XY1Z/S%N");
                StringWriter sw = new StringWriter();
                Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString6__9 = m.execute(null, Collections.emptyList());
                String o_testInlineCommentWithinExtendCodeBlock_literalMutationString6__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString6null1023 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString6null1023_failAssert0_literalMutationString4150 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [L%;cxp(j$7o)q))x@HEo|lc&XY1Z/S%N not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_literalMutationString589_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("]6^-Yp/(!67q SCB8%r#*,6[CXvs#4-Ph");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_literalMutationString589 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlocknull28_failAssert0_literalMutationString430_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentWithinExtendCode`lock.html");
                StringWriter sw = new StringWriter();
                m.execute(null, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlocknull28 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlocknull28_failAssert0_literalMutationString430 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExtendCode`lock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add923_failAssert0_add5775_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("commentWithinExtendCodeBlock.html");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile("mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, Collections.emptyList());
                    TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                    sw.toString();
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add923 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add923_failAssert0_add5775 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString13_failAssert0null1073_failAssert0_literalMutationString4290_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("commentWithinExtendCodeBlock.html");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile(">,mm^=aLu9]e !yuQ2}N]YfpTRo1uM_Cw");
                    StringWriter sw = new StringWriter();
                    m.execute(null, Collections.emptyList());
                    TestUtil.getContents(root, "");
                    sw.toString();
                    org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString13 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString13_failAssert0null1073 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString13_failAssert0null1073_failAssert0_literalMutationString4290 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >,mm^=aLu9]e !yuQ2}N]YfpTRo1uM_Cw not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString13_failAssert0_literalMutationString525_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("=bgeF+SP]+5q&)&7g.{(7pLVhWP^#woT|");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString13 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString13_failAssert0_literalMutationString525 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template =bgeF+SP]+5q&)&7g.{(7pLVhWP^#woT| not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString7() throws MustacheException, IOException, InterruptedException, ExecutionException {
        File root = getRoot("commentWithinExtendCodeBlock.html");
        MustacheFactory c = new DefaultMustacheFactory(root);
        Mustache m = c.compile("");
        StringWriter sw = new StringWriter();
        Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString7__9 = m.execute(sw, Collections.emptyList());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testInlineCommentWithinExtendCodeBlock_literalMutationString7__9)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testInlineCommentWithinExtendCodeBlock_literalMutationString7__9)).toString());
        String o_testInlineCommentWithinExtendCodeBlock_literalMutationString7__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
        Assert.assertEquals("<h1>Just for the fun of it</h1>\n", o_testInlineCommentWithinExtendCodeBlock_literalMutationString7__11);
        sw.toString();
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringBuffer) (((StringWriter) (o_testInlineCommentWithinExtendCodeBlock_literalMutationString7__9)).getBuffer())).toString());
        Assert.assertEquals("box.html\nclassloader.html\nclient.html\nclient.txt\ncom\ncompiletest.mustache\ncomplex.html\ncomplex.txt\ndiv.html\nfallback\nfdbcli.mustache\nfdbcli.txt\nfdbcli2.mustache\nfdbcli2.txt\nfdbcli3.mustache\nfdbcli3.txt\nfollow.html\nfollownomenu.html\nfollownomenu.txt\nfunctions\nhogan.json\nmain.html\nmethod.html\nmultiple_recursive_partials.html\nmultipleextensions.html\nmultipleextensions.txt\nnested_inheritance.html\nnested_inheritance.txt\nnested_partials_template.html\noverrideextension.html\nparentreplace.html\npartialintemplatefunction.html\npartialsub.html\npartialsubpartial.html\npartialsubpartial.txt\npartialsuper.html\npath\npretranslate.html\npsauxwww.mustache\npsauxwww.txt\nrelative\nreplace.html\nreplace.txt\nsinglereplace.html\nspec\nsub.html\nsub.txt\nsubblockchild1.html\nsubblockchild1.txt\nsubblockchild2.html\nsubblockchild2.txt\nsubblocksuper.html\nsubsub.html\nsubsub.txt\nsubsubchild1.html\nsubsubchild1.txt\nsubsubchild2.html\nsubsubchild2.txt\nsubsubchild3.html\nsubsubchild3.txt\nsubsubmiddle.html\nsubsubsuper.html\nsuper.html\ntemplate.html\ntemplate.mustache\ntemplates_filepath\ntemplates.jar\ntoomany.html\ntweetbox.html\nuninterestingpartial.html\n", ((StringWriter) (o_testInlineCommentWithinExtendCodeBlock_literalMutationString7__9)).toString());
        Assert.assertEquals("<h1>Just for the fun of it</h1>\n", o_testInlineCommentWithinExtendCodeBlock_literalMutationString7__11);
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9");
            StringWriter sw = new StringWriter();
            m.execute(sw, Collections.emptyList());
            TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString3_literalMutationString295_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("bundles_post_labels.txt");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("l9?+czV$ox.Ez(}dsKGF`%NIG(y,n4ZJz");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString3__9 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_literalMutationString3__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString3_literalMutationString295 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template l9?+czV$ox.Ez(}dsKGF`%NIG(y,n4ZJz not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1095_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, null);
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1095 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1093_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9");
                StringWriter sw = new StringWriter();
                m.execute(null, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1093 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add923_failAssert0null7050_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("commentWithinExtendCodeBlock.html");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile("mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9");
                    StringWriter sw = new StringWriter();
                    m.execute(null, Collections.emptyList());
                    TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add923 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add923_failAssert0null7050 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString4_literalMutationString258_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithi{ExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("commentWithinExtend>CodeBlock.html");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString4__9 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_literalMutationString4__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString4_literalMutationString258 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExtend>CodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add923_failAssert0_literalMutationString3517_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("commentWithinExtendCodeBlock.html");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile("mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-9");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, Collections.emptyList());
                    TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add923 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add923_failAssert0_literalMutationString3517 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-9 not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_literalMutationString599_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "odTU +X]#L>+}O`3fF!B$`dx;^L&[9N+");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_literalMutationString599 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template mef(OJKjX3rNL&`;f@`f#!Y|37cfZ=-+9 not found", expected.getMessage());
        }
    }

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

