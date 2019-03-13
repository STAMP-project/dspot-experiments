/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.IOException;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;
import org.junit.Assert;
import org.junit.Test;


public class MatlabTokenizerTest extends AbstractTokenizerTest {
    private static final String FILENAME = "sample-matlab.m";

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 3925;
        super.tokenizeTest();
    }

    @Test
    public void testIgnoreBetweenSpecialComments() throws IOException {
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader((((((((((((((("% CPD-OFF" + (PMD.EOL)) + "function g = vec(op, y)") + (PMD.EOL)) + "  opy = op(y);") + (PMD.EOL)) + "  if ( any(size(opy) > 1) )") + (PMD.EOL)) + "    g = @loopWrapperArray;") + (PMD.EOL)) + "  end") + (PMD.EOL)) + "  % CPD-ON") + (PMD.EOL)) + "end")));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens);
        TokenEntry.getEOF();
        Assert.assertEquals(2, tokens.size());// 2 tokens: "end" + EOF

    }
}

