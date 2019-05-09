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
    public void testCommentBlock_literalMutationString3958_failAssert0_literalMutationString5378_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("XI%U^=`EY>I,");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString3958 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString3958_failAssert0_literalMutationString5378 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template XI%U^=`EY>I, not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString3945_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("{^__@:KEMTiR");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("ignored", "ignored");
            m.execute(sw, scope);
            TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_literalMutationString3945 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template {^__@:KEMTiR not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1348_failAssert0null3655_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("r-I&bl@v0gX^6#PG|h");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put(null, "A Comedy of Errors");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString1348 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString1348_failAssert0null3655 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template r-I&bl@v0gX^6#PG|h not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1348_failAssert0_add3273_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("r-I&bl@v0gX^6#PG|h");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("title", "A Comedy of Errors");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commentinline.txt");
                TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString1348 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString1348_failAssert0_add3273 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template r-I&bl@v0gX^6#PG|h not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1348_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("r-I&bl@v0gX^6#PG|h");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("title", "A Comedy of Errors");
            m.execute(sw, scope);
            TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString1348 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template r-I&bl@v0gX^6#PG|h not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1348_failAssert0_literalMutationString2676_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentinline.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("r-I&bl@v0gX^6#PG|h");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("title", "bundles_post_labels.txt");
                m.execute(sw, scope);
                TestUtil.getContents(root, "commentinline.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString1348 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString1348_failAssert0_literalMutationString2676 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template r-I&bl@v0gX^6#PG|h not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString623_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("bundles_post_labels.txt");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("b4uKv]/Be?Oz<`+Zr#^lEC{Pmq8Hat/5,");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString623 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template b4uKv]/Be?Oz<`+Zr#^lEC{Pmq8Hat/5, not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_add22_literalMutationString190_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("co mmentWithinExtendCodeBlock.html");
            StringWriter sw = new StringWriter();
            List<Object> o_testInlineCommentWithinExtendCodeBlock_add22__9 = Collections.emptyList();
            Writer o_testInlineCommentWithinExtendCodeBlock_add22__10 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_add22__12 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_add22_literalMutationString190 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template co mmentWithinExtendCodeBlock.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_add937_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("b4uKv]/Be?Oz<`+Zr#^lEC{Pmq8Hat/5,");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_add937 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template b4uKv]/Be?Oz<`+Zr#^lEC{Pmq8Hat/5, not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0null1105_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("b4uKv]/Be?Oz<`+Zr#^lEC{Pmq8Hat/5,");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, null);
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0null1105 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template b4uKv]/Be?Oz<`+Zr#^lEC{Pmq8Hat/5, not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("b4uKv]/Be?Oz<`+Zr#^lEC{Pmq8Hat/5,");
            StringWriter sw = new StringWriter();
            m.execute(sw, Collections.emptyList());
            TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template b4uKv]/Be?Oz<`+Zr#^lEC{Pmq8Hat/5, not found", expected.getMessage());
        }
    }

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

