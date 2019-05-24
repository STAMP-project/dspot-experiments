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
            Mustache m = c.compile("0xCY&+S&s7%p");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("ignored", "ignored");
            m.execute(sw, scope);
            TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_literalMutationString179 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 0xCY&+S&s7%p not found", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testCommentBlock_literalMutationString180_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("comment.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("comme`nt.html");
            StringWriter sw = new StringWriter();
            Map scope = new HashMap();
            scope.put("ignored", "ignored");
            m.execute(sw, scope);
            TestUtil.getContents(root, "comment.txt");
            sw.toString();
            org.junit.Assert.fail("testCommentBlock_literalMutationString180 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template comme`nt.html not found", expected.getMessage());
        }
    }

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

