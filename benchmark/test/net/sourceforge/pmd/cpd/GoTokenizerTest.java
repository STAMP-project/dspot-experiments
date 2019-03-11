/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.IOException;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;
import org.junit.Test;


public class GoTokenizerTest extends AbstractTokenizerTest {
    private static final String FILENAME = "btrfs.go";

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 3517;
        super.tokenizeTest();
    }
}

