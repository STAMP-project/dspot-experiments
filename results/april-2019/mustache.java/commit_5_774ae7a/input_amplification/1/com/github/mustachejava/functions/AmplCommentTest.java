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
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class AmplCommentTest {
    @Test(timeout = 10000)
    public void testInlineCommentWithinExtendCodeBlock_literalMutationString10_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            File root = getRoot("commentWithinExtendCodeBlock.html");
            MustacheFactory c = new DefaultMustacheFactory(root);
            Mustache m = c.compile("01yCi*OdwpauR%h1,xavU[1Rvnj|}8wu]");
            StringWriter sw = new StringWriter();
            m.execute(sw, Collections.emptyList());
            TestUtil.getContents(root, "commentWithinExtendCodeBlock.txt");
            sw.toString();
            org.junit.Assert.fail("testInlineCommentWithinExtendCodeBlock_literalMutationString10 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            Assert.assertEquals("Template 01yCi*OdwpauR%h1,xavU[1Rvnj|}8wu] not found", expected.getMessage());
        }
    }

    private File getRoot(String fileName) {
        File file = new File("compiler/src/test/resources/functions");
        return new File(file, fileName).exists() ? file : new File("src/test/resources/functions");
    }
}

