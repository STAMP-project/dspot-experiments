/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;
import org.junit.Test;


public class ScalaTokenizerTest extends AbstractTokenizerTest {
    private static final Charset ENCODING = StandardCharsets.UTF_8;

    private static final String FILENAME = "sample-LiftActor.scala";

    private File tempFile;

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 2591;
        super.tokenizeTest();
    }
}

