/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import org.junit.Assert;
import org.junit.Test;


public class TokenEntryTest {
    @Test
    public void testSimple() {
        TokenEntry.clearImages();
        TokenEntry mark = new TokenEntry("public", "/var/Foo.java", 1);
        Assert.assertEquals(1, mark.getBeginLine());
        Assert.assertEquals("/var/Foo.java", mark.getTokenSrcID());
        Assert.assertEquals(0, mark.getIndex());
    }
}

