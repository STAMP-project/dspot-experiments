package com.sleekbyte.tailor.functional;


import com.sleekbyte.tailor.Tailor;
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
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for parse failures.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ParseFailureTest extends RuleTest {
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
        Assert.assertThat(summary, Matchers.containsString("skipped 1"));
        // Ensure number of warnings in summary equals actual number of warnings in the output
        Assert.assertThat(summary, Matchers.containsString("0 violations"));
        Assert.assertArrayEquals(outContent.toString(Charset.defaultCharset().name()), this.expectedMessages.toArray(), actualOutput.toArray());
    }
}

