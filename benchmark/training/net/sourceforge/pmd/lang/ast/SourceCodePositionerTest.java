/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link SourceCodePositioner}.
 */
public class SourceCodePositionerTest {
    private static final String SOURCE_CODE = "abcd\ndefghi\n\njklmn\nopq";

    /**
     * Tests whether the lines and columns are calculated correctly.
     */
    @Test
    public void testLineNumberFromOffset() {
        SourceCodePositioner positioner = new SourceCodePositioner(SourceCodePositionerTest.SOURCE_CODE);
        int offset;
        offset = SourceCodePositionerTest.SOURCE_CODE.indexOf('a');
        Assert.assertEquals(1, positioner.lineNumberFromOffset(offset));
        Assert.assertEquals(1, positioner.columnFromOffset(1, offset));
        offset = SourceCodePositionerTest.SOURCE_CODE.indexOf('b');
        Assert.assertEquals(1, positioner.lineNumberFromOffset(offset));
        Assert.assertEquals(2, positioner.columnFromOffset(1, offset));
        offset = SourceCodePositionerTest.SOURCE_CODE.indexOf('e');
        Assert.assertEquals(2, positioner.lineNumberFromOffset(offset));
        Assert.assertEquals(2, positioner.columnFromOffset(2, offset));
        offset = SourceCodePositionerTest.SOURCE_CODE.indexOf('q');
        Assert.assertEquals(5, positioner.lineNumberFromOffset(offset));
        Assert.assertEquals(3, positioner.columnFromOffset(5, offset));
    }
}

