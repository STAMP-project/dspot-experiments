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
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class AmplCommentTest {
    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString506_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("(F?&>Rq+YUq8v}5sL!4$#O{/vN)Gl0p+bz");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString506 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template (F?&>Rq+YUq8v}5sL!4$#O{/vN)Gl0p+bz not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_remove25_add639_literalMutationString2162_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache o_testInlineCommentWithinExtendCodeBlock_remove25_add639__5 = c.compile("commentWithinExtendCodeBlock.html");
            Mustache m = c.compile("bmtD[9YDjjHjswlz>8xe]8LFMv5W+4?_a");
            StringWriter sw = new StringWriter();
            String o_testInlineCommentWithinExtendCodeBlock_remove25__9 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_remove25_add639_literalMutationString2162 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bmtD[9YDjjHjswlz>8xe]8LFMv5W+4?_a not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_literalMutationString624_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("bundles_post_labels.txt");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile(":|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_literalMutationString624 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile(":|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B");
            StringWriter sw = new StringWriter();
            m.execute(sw, Collections.emptyList());
            TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105_failAssert0_add5462_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("commentWithinExtendCodeBlock.html");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    c.compile(":|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B");
                    Mustache m = c.compile(":|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, Collections.emptyList());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105_failAssert0_add5462 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add937_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile(":|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add937 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile(":|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, null);
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString14_failAssert0_literalMutationString580_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("?G.pDm*MG/Tv?{/,1ZD8N|</!+2<1]fvM");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinxtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString14 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString14_failAssert0_literalMutationString580 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template ?G.pDm*MG/Tv?{/,1ZD8N|</!+2<1]fvM not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_add22_literalMutationString309_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("commentWit{inExtendCodeBlock.html");
            StringWriter sw = new StringWriter();
            List<Object> o_testInlineCommentWithinExtendCodeBlock_add22__9 = Collections.emptyList();
            Writer o_testInlineCommentWithinExtendCodeBlock_add22__10 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_add22__12 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_add22_literalMutationString309 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWit{inExtendCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105_failAssert0_literalMutationString3079_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("(cc@8ds^|ph8l:x7i9fRUovI)blU,S{O)");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile(":|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, Collections.emptyList());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105_failAssert0_literalMutationString3079 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlocknull30_failAssert0_add874_failAssert0_literalMutationString3806_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("commentWithinExtendCodeBlock.html");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile("L7IN%ooCYexSOl5@6Fg!^$NE{Q[@??iF+");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, Collections.emptyList());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    sw.toString();
                    org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlocknull30 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlocknull30_failAssert0_add874 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlocknull30_failAssert0_add874_failAssert0_literalMutationString3806 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105_failAssert0null6757_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("commentWithinExtendCodeBlock.html");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile(":|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B");
                    StringWriter sw = new StringWriter();
                    m.execute(null, Collections.emptyList());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105_failAssert0null6757 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_add24_literalMutationString289_literalMutationString2650_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("{xsB-KxDLHOLdtS9ld4ElCx");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_add24__9 = m.execute(sw, Collections.emptyList());
            sw.toString();
            String o_testInlineCommentWithinExtendCodeBlock_add24__12 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_add24_literalMutationString289_literalMutationString2650 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {xsB-KxDLHOLdtS9ld4ElCx not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105_failAssert0null6758_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("commentWithinExtendCodeBlock.html");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile(":|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, Collections.emptyList());
                    TestUtil.getContents(null, null);
                    sw.toString();
                    org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105_failAssert0null6758 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105_failAssert0_add5467_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("commentWithinExtendCodeBlock.html");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    Mustache m = c.compile(":|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, Collections.emptyList());
                    TestUtil.getContents(root, null);
                    sw.toString();
                    org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
                }
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0null1105_failAssert0_add5467 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_add20_literalMutationString374_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache o_testInlineCommentWithinExtendCodeBlock_add20__5 = c.compile("iGYpX8(>i0QLEQ|Na5l_*ok<jAcNlmN=-");
            Mustache m = c.compile("commentWithinExtendCodeBlock.html");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_add20__10 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_add20__12 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_add20_literalMutationString374 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template iGYpX8(>i0QLEQ|Na5l_*ok<jAcNlmN=- not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add938_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile(":|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString12_failAssert0_add938 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template :|7b$7yY00^BF4,``]B;0AtC_-u]]GF]B not found", expected.getMessage());
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
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString8_literalMutationString273_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("A Comedy of Errors");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString8__9 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_literalMutationString8__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8_literalMutationString273 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A Comedy of Errors not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString6_add730_literalMutationString2198_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodenBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("{;Cy5;C65S}?Q[J+.#Af2(I#]Z_Tu_w/g");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString6__9 = m.execute(sw, Collections.emptyList());
            ((StringWriter) (o_testInlineCommentWithinExtendCodeBlock_literalMutationString6__9)).getBuffer().toString();
            String o_testInlineCommentWithinExtendCodeBlock_literalMutationString6__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString6_add730_literalMutationString2198 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {;Cy5;C65S}?Q[J+.#Af2(I#]Z_Tu_w/g not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_add21_literalMutationString398_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("n|(<9I>y]$!7amCx[iE+0Cy+kO@)@{T3[");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_add21__9 = m.execute(sw, Collections.emptyList());
            Writer o_testInlineCommentWithinExtendCodeBlock_add21__11 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_add21__13 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_add21_literalMutationString398 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template n|(<9I>y]$!7amCx[iE+0Cy+kO@)@{T3[ not found", expected.getMessage());
        }
    }

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

