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
import org.junit.Test;


public class AmplCommentTest {
    @Test(timeout = 10000)
    public void testCommentInline_literalMutationString10_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentinline.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("z{<-X#h#@^`oERT#k/");
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

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

