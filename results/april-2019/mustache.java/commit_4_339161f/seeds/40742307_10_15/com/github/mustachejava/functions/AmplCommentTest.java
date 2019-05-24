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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class AmplCommentTest {
    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString179_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("comm ent.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("ignored", "ignored");
            m.execute(sw, scope);
            TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_literalMutationString179 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comm ent.html not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString12_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("grJrlX+t)Bqx4Snv][");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("title", "A Comedy of Errors");
            m.execute(sw, scope);
            TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString12 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template grJrlX+t)Bqx4Snv][ not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString11_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("commentinlin}e.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("title", "A Comedy of Errors");
            m.execute(sw, scope);
            TestUtil.getContents(root, "commentinline.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentInline_literalMutationString11 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template commentinlin}e.html not found", expected.getMessage());
        }
    }

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

