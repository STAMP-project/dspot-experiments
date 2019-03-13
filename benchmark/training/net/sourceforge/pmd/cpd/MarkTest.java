/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import net.sourceforge.pmd.cpd.SourceCode.StringCodeLoader;
import org.junit.Assert;
import org.junit.Test;


public class MarkTest {
    @Test
    public void testSimple() {
        String filename = "/var/Foo.java";
        int beginLine = 1;
        TokenEntry token = new TokenEntry("public", "/var/Foo.java", 1);
        Mark mark = new Mark(token);
        int lineCount = 10;
        mark.setLineCount(lineCount);
        String codeFragment = "code fragment";
        mark.setSourceCode(new SourceCode(new StringCodeLoader(codeFragment)));
        Assert.assertEquals(token, mark.getToken());
        Assert.assertEquals(filename, mark.getFilename());
        Assert.assertEquals(beginLine, mark.getBeginLine());
        Assert.assertEquals(lineCount, mark.getLineCount());
        Assert.assertEquals(((beginLine + lineCount) - 1), mark.getEndLine());
        Assert.assertEquals(codeFragment, mark.getSourceCodeSlice());
    }
}

