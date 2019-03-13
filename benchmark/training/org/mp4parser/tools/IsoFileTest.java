package org.mp4parser.tools;


import junit.framework.TestCase;
import org.mp4parser.IsoFile;


/**
 *
 */
public class IsoFileTest extends TestCase {
    public void testFourCC() {
        TestCase.assertEquals("AA\u0000\u0000", IsoFile.bytesToFourCC(new byte[]{ 65, 65 }));
        TestCase.assertEquals("AAAA", IsoFile.bytesToFourCC(new byte[]{ 65, 65, 65, 65, 65, 65 }));
        TestCase.assertEquals("AAAA", new String(IsoFile.fourCCtoBytes("AAAAAAA")));
        TestCase.assertEquals("AA\u0000\u0000", new String(IsoFile.fourCCtoBytes("AA")));
        TestCase.assertEquals("\u0000\u0000\u0000\u0000", new String(IsoFile.fourCCtoBytes(null)));
        TestCase.assertEquals("\u0000\u0000\u0000\u0000", new String(IsoFile.fourCCtoBytes("")));
        TestCase.assertEquals("\u0000\u0000\u0000\u0000", IsoFile.bytesToFourCC(null));
        TestCase.assertEquals("\u0000\u0000\u0000\u0000", IsoFile.bytesToFourCC(new byte[0]));
    }
}

