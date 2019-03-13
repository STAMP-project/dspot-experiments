/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ApexTokenizerTest {
    @Test
    public void testTokenize() throws IOException {
        Tokens tokens = tokenize(load("Simple.cls"));
        if ((tokens.size()) != 28) {
            printTokens(tokens);
        }
        Assert.assertEquals(28, tokens.size());
        Assert.assertEquals("someparam", findTokensByLine(8, tokens).get(0).toString());
    }

    @Test
    public void testTokenizeCaseSensitive() throws IOException {
        Tokens tokens = tokenize(load("Simple.cls"), true);
        if ((tokens.size()) != 28) {
            printTokens(tokens);
        }
        Assert.assertEquals(28, tokens.size());
        Assert.assertEquals("someParam", findTokensByLine(8, tokens).get(0).toString());
    }

    /**
     * Comments are ignored since using ApexLexer.
     */
    @Test
    public void testTokenizeWithComments() throws IOException {
        Tokens tokens = tokenize(load("issue427/SFDCEncoder.cls"));
        Assert.assertEquals(17, tokens.size());
        Tokens tokens2 = tokenize(load("issue427/SFDCEncoderConstants.cls"));
        Assert.assertEquals(17, tokens2.size());
    }
}

