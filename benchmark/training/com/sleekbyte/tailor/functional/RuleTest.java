package com.sleekbyte.tailor.functional;


import com.sleekbyte.tailor.Tailor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Base class for functional rule tests.
 */
public abstract class RuleTest {
    protected static final String TEST_INPUT_DIR = "src/test/swift/com/sleekbyte/tailor/functional/";

    protected static final String NEWLINE_REGEX = "\\r?\\n";

    protected ByteArrayOutputStream outContent;

    protected File inputFile;

    protected List<String> expectedMessages;

    @Test
    public void testRule() throws IOException {
        String[] command = Stream.concat(Arrays.stream(this.getCommandArgs()), Arrays.stream(this.getDefaultArgs())).toArray(String[]::new);
        addAllExpectedMsgs();
        Tailor.main(command);
        List<String> actualOutput = new ArrayList<>();
        String[] msgs = outContent.toString(Charset.defaultCharset().name()).split(RuleTest.NEWLINE_REGEX);
        String summary = msgs[((msgs.length) - 1)];
        // Skip first four lines for progress and file header, last two lines for summary
        msgs = Arrays.copyOfRange(msgs, 4, ((msgs.length) - 2));
        for (String msg : msgs) {
            String truncatedMsg = msg.substring(msg.indexOf(inputFile.getName()));
            actualOutput.add(truncatedMsg);
        }
        // Ensure number of warnings in summary equals actual number of warnings in the output
        Assert.assertThat(summary, Matchers.containsString(((expectedMessages.size()) + " violation")));
        Assert.assertArrayEquals(outContent.toString(Charset.defaultCharset().name()), this.expectedMessages.toArray(), actualOutput.toArray());
    }
}

