/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.IOException;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;
import org.junit.Assert;
import org.junit.Test;


public class PythonTokenizerTest extends AbstractTokenizerTest {
    private static final String FILENAME = "sample-python.py";

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 1218;
        super.tokenizeTest();
    }

    @Test
    public void testIgnoreBetweenSpecialComments() throws IOException {
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(((((((((((((("import logging" + (PMD.EOL)) + "# CPD-OFF") + (PMD.EOL)) + "logger = logging.getLogger('django.request')") + (PMD.EOL)) + "class BaseHandler(object):") + (PMD.EOL)) + "    def __init__(self):") + (PMD.EOL)) + "        self._request_middleware = None") + (PMD.EOL)) + "        # CPD-ON") + (PMD.EOL))));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens);
        TokenEntry.getEOF();
        Assert.assertEquals(3, tokens.size());// 3 tokens: "import" + "logging" + EOF

    }
}

