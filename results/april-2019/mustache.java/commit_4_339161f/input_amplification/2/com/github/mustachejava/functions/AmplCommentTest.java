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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class AmplCommentTest {
    @Test(timeout = 10000)
    public void testCommentBlock_add2590_literalMutationString2833_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("!i_1nlEt.`Xs");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentBlock_add2590__11 = scope.put("ignored", "ignored");
            Writer o_testCommentBlock_add2590__12 = m.execute(sw, scope);
            String o_testCommentBlock_add2590__13 = TestUtil.getContents(root, "comment.txt");
            String o_testCommentBlock_add2590__14 = TestUtil.getContents(root, "comment.txt");
            String o_testCommentBlock_add2590__15 = sw.toString();
            org.junit.Assert.fail("testCommentBlock_add2590_literalMutationString2833 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !i_1nlEt.`Xs not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString2566_failAssert0_literalMutationString3997_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("`2#_e3J@d$9 ");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "comment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString2566 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString2566_failAssert0_literalMutationString3997 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString2567_failAssert0_literalMutationString3652_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("!q$ #Lc/G.nMw");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "comment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString2567 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString2567_failAssert0_literalMutationString3652 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString2566_failAssert0null4853_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("`2#_e3J@d$9 ");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(null, scope);
                TestUtil.getContents(root, "comment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString2566 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString2566_failAssert0null4853 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString2566_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("`2#_e3J@d$9 ");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("ignored", "ignored");
            m.execute(sw, scope);
            TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_literalMutationString2566 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString2566_failAssert0_add4498_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("`2#_e3J@d$9 ");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "comment.txt");
                TestUtil.getContents(root, "comment.txt");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString2566 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString2566_failAssert0_add4498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testCommentInlinenull42_literalMutationString901_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("commentinl ne.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInlinenull42__11 = scope.put("title", null);
            Writer o_testCommentInlinenull42__12 = m.execute(sw, scope);
            String o_testCommentInlinenull42__13 = TestUtil.getContents(root, "commentinline.txt");
            String o_testCommentInlinenull42__14 = sw.toString();
            org.junit.Assert.fail("testCommentInlinenull42_literalMutationString901 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentinl ne.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString10_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("+>Pcak(B]MR[%&eHd[");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("title", "A Comedy of Errors");
            m.execute(sw, scope);
            TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString10 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template +>Pcak(B]MR[%&eHd[ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString17_literalMutationString1008_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile(">V55UBrXprg2J@,A5H");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInline_literalMutationString17__11 = scope.put("tile", "A Comedy of Errors");
            Writer o_testCommentInline_literalMutationString17__12 = m.execute(sw, scope);
            String o_testCommentInline_literalMutationString17__13 = TestUtil.getContents(root, "commentinline.txt");
            String o_testCommentInline_literalMutationString17__14 = sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString17_literalMutationString1008 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template >V55UBrXprg2J@,A5H not found", expected.getMessage());
        }
    }

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

