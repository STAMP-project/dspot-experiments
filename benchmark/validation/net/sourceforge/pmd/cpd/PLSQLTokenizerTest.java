/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.IOException;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;
import org.junit.Assert;
import org.junit.Test;


public class PLSQLTokenizerTest extends AbstractTokenizerTest {
    private static final String FILENAME = "sample-plsql.sql";

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 1422;
        super.tokenizeTest();
    }

    @Test
    public void testIgnoreBetweenSpecialComments() throws IOException {
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader((((((((((((("-- CPD-OFF" + (PMD.EOL)) + "CREATE OR REPLACE") + (PMD.EOL)) + "PACKAGE \"test_schema\".\"BANK_DATA\"") + (PMD.EOL)) + "IS") + (PMD.EOL)) + "pi      CONSTANT NUMBER := 3.1415;") + (PMD.EOL)) + "--CPD-ON") + (PMD.EOL)) + "END;")));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens);
        TokenEntry.getEOF();
        Assert.assertEquals(3, tokens.size());// 3 tokens: "END" + ";" + EOF

    }
}

