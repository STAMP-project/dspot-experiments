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
    public void testCommentBlock_literalMutationString6727_failAssert0_add8552_failAssert0_literalMutationString10416_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                {
                    File root = getRoot("comment.html");
                    MustacheFactory c = new DefaultMustacheFactory(root);
                    c.compile("g|azou|gm9wo");
                    Mustache m = c.compile("comment.html");
                    StringWriter sw = new StringWriter();
                    Map scope = new HashMap();
                    scope.put("ignored", "ignored");
                    m.execute(sw, scope);
                    TestUtil.getContents(root, "Q)UFQ{02-/ ");
                    sw.toString();
                    org.junit.Assert.fail("testCommentBlock_literalMutationString6727 should have thrown FileNotFoundException");
                }
                org.junit.Assert.fail("testCommentBlock_literalMutationString6727_failAssert0_add8552 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString6727_failAssert0_add8552_failAssert0_literalMutationString10416 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString6727_failAssert0_literalMutationString7768_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("comment.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("Mvf`1WzZ.<kf");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                scope.put("ignored", "ignored");
                m.execute(sw, scope);
                TestUtil.getContents(root, "Q)UFQ{02-/ ");
                sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString6727 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString6727_failAssert0_literalMutationString7768 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template Mvf`1WzZ.<kf not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString6703_literalMutationString7156_failAssert0_literalMutationString10890_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commet.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("&I_Q]r>`!nNm");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testCommentBlock_literalMutationString6703__11 = scope.put("ignored", "ignored");
                Writer o_testCommentBlock_literalMutationString6703__12 = m.execute(sw, scope);
                String o_testCommentBlock_literalMutationString6703__13 = TestUtil.getContents(root, "");
                String o_testCommentBlock_literalMutationString6703__14 = sw.toString();
                org.junit.Assert.fail("testCommentBlock_literalMutationString6703_literalMutationString7156 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testCommentBlock_literalMutationString6703_literalMutationString7156_failAssert0_literalMutationString10890 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template &I_Q]r>`!nNm not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlocknull6739_literalMutationString7520_literalMutationString9665_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.ht|ml");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("g|azou|gm9wo");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentBlocknull6739__11 = scope.put(null, "ignored");
            Writer o_testCommentBlocknull6739__12 = m.execute(sw, scope);
            String o_testCommentBlocknull6739__13 = TestUtil.getContents(root, "comment.txt");
            String o_testCommentBlocknull6739__14 = sw.toString();
            org.junit.Assert.fail("testCommentBlocknull6739_literalMutationString7520_literalMutationString9665 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString6707_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("g|azou|gm9wo");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("ignored", "ignored");
            m.execute(sw, scope);
            TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_literalMutationString6707 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString6706_literalMutationString7679_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("g|azou|gm9wo");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentBlock_literalMutationString6706__11 = scope.put("ignored", "ignored");
            Writer o_testCommentBlock_literalMutationString6706__12 = m.execute(sw, scope);
            String o_testCommentBlock_literalMutationString6706__13 = TestUtil.getContents(root, "comment.txt");
            String o_testCommentBlock_literalMutationString6706__14 = sw.toString();
            org.junit.Assert.fail("testCommentBlock_literalMutationString6706_literalMutationString7679 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1_literalMutationString675_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("g|azou|gm9wo");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInline_literalMutationString1__11 = scope.put("title", "A Comedy of Errors");
            Writer o_testCommentInline_literalMutationString1__12 = m.execute(sw, scope);
            String o_testCommentInline_literalMutationString1__13 = TestUtil.getContents(root, "commentinline.txt");
            String o_testCommentInline_literalMutationString1__14 = sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString1_literalMutationString675 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString18_literalMutationString498_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("g|azou|gm9wo");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInline_literalMutationString18__11 = scope.put("tile", "A Comedy of Errors");
            Writer o_testCommentInline_literalMutationString18__12 = m.execute(sw, scope);
            String o_testCommentInline_literalMutationString18__13 = TestUtil.getContents(root, "commentinline.txt");
            String o_testCommentInline_literalMutationString18__14 = sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString18_literalMutationString498 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString10_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("]O6)f@b^:NrN[(cgh1");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("title", "A Comedy of Errors");
            m.execute(sw, scope);
            TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString10 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1_add1701_literalMutationString3665_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("commen:inline.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInline_literalMutationString1__11 = scope.put("title", "A Comedy of Errors");
            Writer o_testCommentInline_literalMutationString1_add1701__14 = m.execute(sw, scope);
            Writer o_testCommentInline_literalMutationString1__12 = m.execute(sw, scope);
            String o_testCommentInline_literalMutationString1__13 = TestUtil.getContents(root, "commentinline.txt");
            String o_testCommentInline_literalMutationString1__14 = sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString1_add1701_literalMutationString3665 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commen:inline.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1_literalMutationString675_failAssert0null6202_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("g|azou|gm9wo");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testCommentInline_literalMutationString1__11 = scope.put(null, "A Comedy of Errors");
                Writer o_testCommentInline_literalMutationString1__12 = m.execute(sw, scope);
                String o_testCommentInline_literalMutationString1__13 = TestUtil.getContents(root, "commentinline.txt");
                String o_testCommentInline_literalMutationString1__14 = sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString1_literalMutationString675 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString1_literalMutationString675_failAssert0null6202 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1_literalMutationString675_failAssert0_literalMutationString4205_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("g|azou|gm9wo");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testCommentInline_literalMutationString1__11 = scope.put("#z&?)", "A Comedy of Errors");
                Writer o_testCommentInline_literalMutationString1__12 = m.execute(sw, scope);
                String o_testCommentInline_literalMutationString1__13 = TestUtil.getContents(root, "commentinline.txt");
                String o_testCommentInline_literalMutationString1__14 = sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString1_literalMutationString675 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString1_literalMutationString675_failAssert0_literalMutationString4205 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString8_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("g|azou|gm9wo");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("title", "A Comedy of Errors");
            m.execute(sw, scope);
            TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString8 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString1_literalMutationString675_failAssert0_add5493_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                getRoot("");
                File root = getRoot("");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("g|azou|gm9wo");
                StringWriter sw = new StringWriter();
                Map scope = new HashMap();
                Object o_testCommentInline_literalMutationString1__11 = scope.put("title", "A Comedy of Errors");
                Writer o_testCommentInline_literalMutationString1__12 = m.execute(sw, scope);
                String o_testCommentInline_literalMutationString1__13 = TestUtil.getContents(root, "commentinline.txt");
                String o_testCommentInline_literalMutationString1__14 = sw.toString();
                org.junit.Assert.fail("testCommentInline_literalMutationString1_literalMutationString675 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testCommentInline_literalMutationString1_literalMutationString675_failAssert0_add5493 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString7_literalMutationString441_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("g|azou|gm9wo");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            Object o_testCommentInline_literalMutationString7__11 = scope.put("title", "A Comedy of Errors");
            Writer o_testCommentInline_literalMutationString7__12 = m.execute(sw, scope);
            String o_testCommentInline_literalMutationString7__13 = TestUtil.getContents(root, "commentinline.txt");
            String o_testCommentInline_literalMutationString7__14 = sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString7_literalMutationString441 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template g|azou|gm9wo not found", expected.getMessage());
        }
    }

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

