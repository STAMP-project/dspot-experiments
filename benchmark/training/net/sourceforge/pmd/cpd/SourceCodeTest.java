/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.File;
import java.util.ArrayList;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.SourceCode.FileCodeLoader;
import org.junit.Assert;
import org.junit.Test;


public class SourceCodeTest {
    private static final String BASE_RESOURCE_PATH = "src/test/resources/net/sourceforge/pmd/cpd/files/";

    private static final String SAMPLE_CODE = "Line 1\n" + (("Line 2\n" + "Line 3\n") + "Line 4\n");

    @Test
    public void testSimple() throws Exception {
        Tokenizer tokenizer = new AbstractTokenizer() {
            {
                this.stringToken = new ArrayList();
                this.ignorableCharacter = new ArrayList();
                this.ignorableStmt = new ArrayList();
            }
        };
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(SourceCodeTest.SAMPLE_CODE, "Foo.java"));
        Assert.assertEquals("Foo.java", sourceCode.getFileName());
        tokenizer.tokenize(sourceCode, new Tokens());
        Assert.assertEquals("Line 1", sourceCode.getSlice(1, 1));
        Assert.assertEquals("Line 2", sourceCode.getSlice(2, 2));
        Assert.assertEquals((("Line 1" + (PMD.EOL)) + "Line 2"), sourceCode.getSlice(1, 2));
    }

    @Test
    public void testEncodingDetectionFromBOM() throws Exception {
        FileCodeLoader loader = new SourceCode.FileCodeLoader(new File(((SourceCodeTest.BASE_RESOURCE_PATH) + "file_with_utf8_bom.java")), "ISO-8859-1");
        // The encoding detection is done when the reader is created
        loader.getReader();
        Assert.assertEquals("UTF-8", loader.getEncoding());
    }

    @Test
    public void testEncodingIsNotChangedWhenThereIsNoBOM() throws Exception {
        FileCodeLoader loader = new SourceCode.FileCodeLoader(new File(((SourceCodeTest.BASE_RESOURCE_PATH) + "file_with_ISO-8859-1_encoding.java")), "ISO-8859-1");
        // The encoding detection is done when the reader is created
        loader.getReader();
        Assert.assertEquals("ISO-8859-1", loader.getEncoding());
    }
}

