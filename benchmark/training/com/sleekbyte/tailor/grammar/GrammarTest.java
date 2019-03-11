package com.sleekbyte.tailor.grammar;


import com.sleekbyte.tailor.Tailor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.hamcrest.text.IsEmptyString;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GrammarTest {
    private static final String TEST_INPUT_DIR = "src/test/swift/com/sleekbyte/tailor/grammar/";

    private File[] swiftFiles;

    @Test
    public void testGrammar() throws UnsupportedEncodingException {
        for (File swiftFile : swiftFiles) {
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errContent, false, Charset.defaultCharset().name()));
            String[] command = new String[]{ "--debug", (GrammarTest.TEST_INPUT_DIR) + (swiftFile.getName()) };
            Tailor.main(command);
            Assert.assertThat(errContent.toString(Charset.defaultCharset().name()), IsEmptyString.isEmptyString());
            System.setErr(null);
        }
    }
}

