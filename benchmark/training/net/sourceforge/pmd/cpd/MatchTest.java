/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class MatchTest {
    @Test
    public void testSimple() {
        int lineCount1 = 10;
        String codeFragment1 = "code fragment";
        Mark mark1 = createMark("public", "/var/Foo.java", 1, lineCount1, codeFragment1);
        int lineCount2 = 20;
        String codeFragment2 = "code fragment 2";
        Mark mark2 = createMark("class", "/var/Foo.java", 1, lineCount2, codeFragment2);
        Match match = new Match(1, mark1, mark2);
        Assert.assertEquals(1, match.getTokenCount());
        // Returns the line count of the first mark
        Assert.assertEquals(lineCount1, match.getLineCount());
        // Returns the source code of the first mark
        Assert.assertEquals(codeFragment1, match.getSourceCodeSlice());
        Iterator<Mark> i = match.iterator();
        Mark occurrence1 = i.next();
        Mark occurrence2 = i.next();
        Assert.assertFalse(i.hasNext());
        Assert.assertEquals(mark1, occurrence1);
        Assert.assertEquals(lineCount1, occurrence1.getLineCount());
        Assert.assertEquals(codeFragment1, occurrence1.getSourceCodeSlice());
        Assert.assertEquals(mark2, occurrence2);
        Assert.assertEquals(lineCount2, occurrence2.getLineCount());
        Assert.assertEquals(codeFragment2, occurrence2.getSourceCodeSlice());
    }

    @Test
    public void testCompareTo() {
        Match m1 = new Match(1, new TokenEntry("public", "/var/Foo.java", 1), new TokenEntry("class", "/var/Foo.java", 1));
        Match m2 = new Match(2, new TokenEntry("Foo", "/var/Foo.java", 1), new TokenEntry("{", "/var/Foo.java", 1));
        Assert.assertTrue(((m2.compareTo(m1)) < 0));
    }
}

