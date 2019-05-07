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
            Mustache m = c.compile("!9Te0m8rd<T2_O>?aZhdy$oyupUkSHK#x");
            StringWriter sw = new StringWriter();
            m.execute(sw, Collections.emptyList());
            TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !9Te0m8rd<T2_O>?aZhdy$oyupUkSHK#x not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString9_literalMutationString313_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("5G>vb%*)9?CiJGi_04_3; s");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString9__9 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_literalMutationString9__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString9_literalMutationString313 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5G>vb%*)9?CiJGi_04_3; s not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString531_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("!9Te0m8rd<T2_O>?aZhdy$oyupUkSHK#x");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtndCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_literalMutationString531 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !9Te0m8rd<T2_O>?aZhdy$oyupUkSHK#x not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString9_literalMutationString313_failAssert0_add3583_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("5G>vb%*)9?CiJGi_04_3; s");
                StringWriter sw = new StringWriter();
                Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString9__9 = m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                String o_testInlineCommentWithinExtendCodeBlock_literalMutationString9__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString9_literalMutationString313 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString9_literalMutationString313_failAssert0_add3583 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5G>vb%*)9?CiJGi_04_3; s not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString2_literalMutationString277_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtenduodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("[(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v");
            StringWriter sw = new StringWriter();
            Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString2__9 = m.execute(sw, Collections.emptyList());
            String o_testInlineCommentWithinExtendCodeBlock_literalMutationString2__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString2_literalMutationString277 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template [(ZeCG4J/Kc$ ;G8-[lt9:Kb!x$,A7,1v not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString9_literalMutationString313_failAssert0_literalMutationString2681_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("5G>vb%*)9?CiJGi_04_3; s");
                StringWriter sw = new StringWriter();
                Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString9__9 = m.execute(sw, Collections.emptyList());
                String o_testInlineCommentWithinExtendCodeBlock_literalMutationString9__11 = TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString9_literalMutationString313 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString9_literalMutationString313_failAssert0_literalMutationString2681 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 5G>vb%*)9?CiJGi_04_3; s not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString1_literalMutationString297_failAssert0_literalMutationString2752_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("JW,{c1>c)E+P+^?NUW[%[EuMEbH1A[>2]");
                StringWriter sw = new StringWriter();
                Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString1__9 = m.execute(sw, Collections.emptyList());
                String o_testInlineCommentWithinExtendCodeBlock_literalMutationString1__11 = TestUtil.getContents(root, "commentWithinxtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString1_literalMutationString297 should have thrown FileNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString1_literalMutationString297_failAssert0_literalMutationString2752 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template JW,{c1>c)E+P+^?NUW[%[EuMEbH1A[>2] not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString9null1016_failAssert0_literalMutationString2173_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("A Comedy of Errors");
                StringWriter sw = new StringWriter();
                Writer o_testInlineCommentWithinExtendCodeBlock_literalMutationString9__9 = m.execute(sw, Collections.emptyList());
                String o_testInlineCommentWithinExtendCodeBlock_literalMutationString9__11 = TestUtil.getContents(root, null);
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString9null1016 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString9null1016_failAssert0_literalMutationString2173 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template A Comedy of Errors not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_add893_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("!9Te0m8rd<T2_O>?aZhdy$oyupUkSHK#x");
                StringWriter sw = new StringWriter();
                Collections.emptyList();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0_add893 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !9Te0m8rd<T2_O>?aZhdy$oyupUkSHK#x not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0null1074_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                File root = getRoot("commentWithinExtendCodeBlock.html");
                MustacheFactory c = new DefaultMustacheFactory(root);
                Mustache m = c.compile("!9Te0m8rd<T2_O>?aZhdy$oyupUkSHK#x");
                StringWriter sw = new StringWriter();
                m.execute(sw, Collections.emptyList());
                TestUtil.getContents(null, "commentWithinExtendCodeBlock.txt");
                sw.toString();
                org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11 should have thrown MustacheNotFoundException");
            }
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString11_failAssert0null1074 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template !9Te0m8rd<T2_O>?aZhdy$oyupUkSHK#x not found", expected.getMessage());
        }
    }

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

