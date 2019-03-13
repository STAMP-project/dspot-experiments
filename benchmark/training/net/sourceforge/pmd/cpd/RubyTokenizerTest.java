/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.IOException;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;
import org.junit.Test;


public class RubyTokenizerTest extends AbstractTokenizerTest {
    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 30;
        super.tokenizeTest();
    }
}

