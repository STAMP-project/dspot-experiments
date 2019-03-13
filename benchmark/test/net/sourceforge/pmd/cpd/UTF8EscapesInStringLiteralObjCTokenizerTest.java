/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.IOException;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;
import org.junit.Test;


// Tests if the ObjectiveC tokenizer supports UTF-8 escapes in string literals
public class UTF8EscapesInStringLiteralObjCTokenizerTest extends AbstractTokenizerTest {
    private static final String FILENAME = "FileWithUTF8EscapeInStringLiteral.m";

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 45;
        super.tokenizeTest();
    }
}

