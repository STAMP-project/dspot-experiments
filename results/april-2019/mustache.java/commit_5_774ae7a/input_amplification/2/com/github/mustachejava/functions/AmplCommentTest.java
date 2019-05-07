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
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class AmplCommentTest {
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
    public void testInlineCommentWithinExtendCodeBlock_add23_literalMutationString158_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("com%mentWithinExtendCodeBlock.html");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_add23__9 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_add23__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            String o_testInlineCommentWithinExtendCodeBlock_add23__12 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_add23_literalMutationString158 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template com%mentWithinExtendCodeBlock.html not found", expected.getMessage());
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
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_literalMutationString493_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("commentWithinExt]endCodeBlock.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "s9AUOLxY[s=ns5,U.Y[?;Ytm)DCeu_0C");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString8_failAssert0_literalMutationString493 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentWithinExt]endCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0null1097_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("i2@sxS($<9$T4bXzesi<&g-WNb,LXZU? ");
                StringWriter sw = new StringWriter();
                m.execute(null, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0null1097 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i2@sxS($<9$T4bXzesi<&g-WNb,LXZU?  not found", expected.getMessage());
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
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString602_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("i2@sxS($<9$T4bXzesi<&g-WNb,LXZU? ");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString602 should have thrown MustacheNotFoundException");
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
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_add926_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                c.compile("i2@sxS($<9$T4bXzesi<&g-WNb,LXZU? ");
                Mustache m = c.compile("i2@sxS($<9$T4bXzesi<&g-WNb,LXZU? ");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_add926 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template i2@sxS($<9$T4bXzesi<&g-WNb,LXZU?  not found", expected.getMessage());
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

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

