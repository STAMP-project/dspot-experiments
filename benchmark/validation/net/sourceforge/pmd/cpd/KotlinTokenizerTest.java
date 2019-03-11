/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.IOException;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class KotlinTokenizerTest extends AbstractTokenizerTest {
    private final String filename;

    private final int nExpectedTokens;

    public KotlinTokenizerTest(String filename, int nExpectedTokens) {
        this.filename = filename;
        this.nExpectedTokens = nExpectedTokens;
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = nExpectedTokens;
        super.tokenizeTest();
    }
}

