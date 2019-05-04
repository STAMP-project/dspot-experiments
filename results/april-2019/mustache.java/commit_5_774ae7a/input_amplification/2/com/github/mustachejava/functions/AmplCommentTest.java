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
    public void testInlineCommentWithinExtendCodeBlocknull29_failAssert0_literalMutationString429_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("bI_d(}=o1Ihi%TCSK,GFMPp<Jb-i!NwdN");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(null, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlocknull29 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlocknull29_failAssert0_literalMutationString429 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template bI_d(}=o1Ihi%TCSK,GFMPp<Jb-i!NwdN not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0null1064_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentWithinExt]endCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, null);
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0null1064 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExt]endCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("commentWithinExt]endCodeBlock.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, Collections.emptyList());
            TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExt]endCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_add930_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("i2@sxS($<9$T4bXzesi<&g-WNb,LXZU? ");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_add930 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i2@sxS($<9$T4bXzesi<&g-WNb,LXZU?  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_add876_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                getRoot("commentWithinExtendCodeBlock.html");
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentWithinExt]endCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_add876 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExt]endCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString10_failAssert0_literalMutationString516_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("cwommentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("comme]tWithinExtendCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString10 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString10_failAssert0_literalMutationString516 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comme]tWithinExtendCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString611_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("i2@sxS(<9$T4bXzesi<&g-WNb,LXZU? ");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString611 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i2@sxS(<9$T4bXzesi<&g-WNb,LXZU?  not found", expected.getMessage());
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
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString10_failAssert0_add895_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("comme]tWithinExtendCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString10 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString10_failAssert0_add895 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comme]tWithinExtendCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_add24_literalMutationString379_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("LWJcvvu8(PmRfYf}^NJMq4Y+ T}dLQ&?g");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_add24__9 = m.execute(sw, Collections.emptyList());
            sw.toString();
            String o_testInlineCommentWithinExtendCodeBlock_add24__12 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_add24_literalMutationString379 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template LWJcvvu8(PmRfYf}^NJMq4Y+ T}dLQ&?g not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("i2@sxS($<9$T4bXzesi<&g-WNb,LXZU? ");
            StringWriter sw = new StringWriter();
            m.execute(sw, Collections.emptyList());
            TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i2@sxS($<9$T4bXzesi<&g-WNb,LXZU?  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString10_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("comme]tWithinExtendCodeBlock.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, Collections.emptyList());
            TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString10 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comme]tWithinExtendCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_literalMutationString487_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentWthinExt]endCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_literalMutationString487 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWthinExt]endCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString4_literalMutationString255_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWitFinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("fPw#b@ByP.bRrDAE !rxKQ)s(Bqv|9>B^");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString4__9 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_literalMutationString4__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString4_literalMutationString255 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template fPw#b@ByP.bRrDAE !rxKQ)s(Bqv|9>B^ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_literalMutationString479_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentWithinExt]endCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_literalMutationString479 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExt]endCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_literalMutationString480_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("VvY_SF;HS89JXebhu7.PYv_0tPJ#I@rL.");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentWithinExt]endCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_literalMutationString480 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExt]endCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString10_failAssert0_add890_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                getRoot("commentWithinExtendCodeBlock.html");
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("comme]tWithinExtendCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString10 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString10_failAssert0_add890 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comme]tWithinExtendCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_add928_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("i2@sxS($<9$T4bXzesi<&g-WNb,LXZU? ");
                StringWriter sw = new StringWriter();
                Collections.emptyList();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_add928 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i2@sxS($<9$T4bXzesi<&g-WNb,LXZU?  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_add877_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                c.compile("commentWithinExt]endCodeBlock.html");
                Mustache m = c.compile("commentWithinExt]endCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_add877 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExt]endCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0null1099_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("i2@sxS($<9$T4bXzesi<&g-WNb,LXZU? ");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, null);
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0null1099 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i2@sxS($<9$T4bXzesi<&g-WNb,LXZU?  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_add878_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentWithinExt]endCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_add878 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExt]endCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString10_failAssert0null1072_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("comme]tWithinExtendCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(null, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString10 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString10_failAssert0null1072 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comme]tWithinExtendCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_add22_literalMutationString291_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("A [,68H-;w;Q+v!cX=y!WS5wfB xJ(KJ!");
            StringWriter sw = new StringWriter();
            List<Object> o_testInlineCommentWithinExtendCodeBlock_add22__9 = Collections.emptyList();
            Writer o_testInlineCommentWithinExtendCodeBlock_add22__10 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_add22__12 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_add22_literalMutationString291 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A [,68H-;w;Q+v!cX=y!WS5wfB xJ(KJ! not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_add24_literalMutationString380_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("commentWithinExtendCodeBloc`.html");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_add24__9 = m.execute(sw, Collections.emptyList());
            sw.toString();
            String o_testInlineCommentWithinExtendCodeBlock_add24__12 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_add24_literalMutationString380 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExtendCodeBloc`.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0null1063_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentWithinExt]endCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(null, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0null1063 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExt]endCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString607_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodelock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("i2@sxS($<9$T4bXzesi<&g-WNb,LXZU? ");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString607 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i2@sxS($<9$T4bXzesi<&g-WNb,LXZU?  not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString10_failAssert0_literalMutationString532_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("comme]tWithinExtendCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "vST}!G30F*uc([}!2XbQs |)TDG+L,kg");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString10 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString10_failAssert0_literalMutationString532 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comme]tWithinExtendCodeBlock.html not found", expected.getMessage());
        }
    }

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

