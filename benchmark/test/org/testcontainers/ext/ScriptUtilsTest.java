package org.testcontainers.ext;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ScriptUtilsTest {
    /* Test ScriptUtils script splitting with some ugly/hard-to-split cases */
    @Test
    public void testSplit() throws IOException {
        final String script = Resources.toString(Resources.getResource("splittable.sql"), Charsets.UTF_8);
        final List<String> statements = new ArrayList<>();
        ScriptUtils.splitSqlScript("resourcename", script, ";", "--", "/*", "*/", statements);
        Assert.assertEquals(7, statements.size());
        Assert.assertEquals("SELECT \"a /* string literal containing comment characters like -- here\"", statements.get(2));
        Assert.assertEquals("SELECT \"a \'quoting\' \\\"scenario ` involving BEGIN keyword\\\" here\"", statements.get(3));
        Assert.assertEquals("SELECT * from `bar`", statements.get(4));
        Assert.assertEquals("INSERT INTO bar (foo) VALUES ('hello world')", statements.get(6));
    }
}

