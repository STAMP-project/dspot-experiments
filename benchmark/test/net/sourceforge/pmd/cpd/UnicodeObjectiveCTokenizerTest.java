/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.IOException;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;
import org.junit.Assert;
import org.junit.Test;


// Tests if the ObjectiveC tokenizer supports identifiers with unicode characters
public class UnicodeObjectiveCTokenizerTest extends AbstractTokenizerTest {
    private static final String FILENAME = "NCClient.m";

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 10;
        super.tokenizeTest();
    }

    @Test
    public void testIgnoreBetweenSpecialComments() throws IOException {
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(((((((("// CPD-OFF" + (PMD.EOL)) + "static SecCertificateRef gN?ServerLogonCertificate;") + (PMD.EOL)) + "// CPD-ON") + (PMD.EOL)) + "@end") + (PMD.EOL))));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens);
        TokenEntry.getEOF();
        Assert.assertEquals(2, tokens.size());// 2 tokens: "@end" + EOF

    }
}

