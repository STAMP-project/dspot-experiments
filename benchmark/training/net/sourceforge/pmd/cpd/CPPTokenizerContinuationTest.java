/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.lang.cpp.CppTokenManager;
import net.sourceforge.pmd.lang.cpp.ast.Token;
import org.junit.Assert;
import org.junit.Test;


public class CPPTokenizerContinuationTest {
    @Test
    public void parseWithContinuation() throws Exception {
        String code = load("cpp_with_continuation.cpp");
        Tokens tokens = parse(code);
        if ((tokens.size()) < 52) {
            printTokens(tokens);
            Assert.fail(("Not enough tokens - probably parsing error. Tokens: " + (tokens.size())));
        }
        Assert.assertEquals("static", findByLine(8, tokens).get(0).toString());
        Assert.assertEquals("int", findByLine(8, tokens).get(1).toString());
        // special case, if the continuation is *within* a token
        // see also test #testContinuationIntraToken
        TokenEntry tokenEntry = findByLine(8, tokens).get(2);
        Assert.assertEquals("ab", tokenEntry.toString());
        Assert.assertEquals("int", findByLine(12, tokens).get(0).toString());
        Assert.assertEquals("main", findByLine(12, tokens).get(1).toString());
        Assert.assertEquals("(", findByLine(12, tokens).get(2).toString());
        Assert.assertEquals(")", findByLine(12, tokens).get(3).toString());
        Assert.assertEquals("{", findByLine(13, tokens).get(0).toString());
        Assert.assertEquals("\"world!\\n\"", findByLine(16, tokens).get(0).toString());
        Assert.assertEquals("\"3 Hello, \\world!\\n\"", findByLine(22, tokens).get(4).toString());
        Assert.assertEquals("}", findByLine(29, tokens).get(0).toString());
    }

    /**
     * Verifies the begin/end of a token. Uses the underlaying JavaCC Token and
     * not TokenEntry.
     */
    @Test
    public void parseWithContinuationCppTokenManager() throws Exception {
        String code = load("cpp_with_continuation.cpp");
        CppTokenManager tokenManager = new CppTokenManager(new StringReader(code));
        List<Token> tokens = new ArrayList<>();
        Token token = ((Token) (tokenManager.getNextToken()));
        while (!(token.image.isEmpty())) {
            tokens.add(token);
            token = ((Token) (tokenManager.getNextToken()));
        } 
        Assert.assertEquals(51, tokens.size());
        assertToken(tokens.get(2), "ab", 8, 12, 9, 1);
        assertToken(tokens.get(22), "\"2 Hello, world!\\n\"", 18, 16, 19, 9);
    }

    @Test
    public void testContinuationIntraToken() throws Exception {
        Tokens tokens = parse(load("cpp_continuation_intra_token.cpp"));
        Assert.assertEquals(7, tokens.size());
    }

    @Test
    public void testContinuationInterToken() throws Exception {
        Tokens tokens = parse(load("cpp_continuation_inter_token.cpp"));
        Assert.assertEquals(17, tokens.size());
    }
}

