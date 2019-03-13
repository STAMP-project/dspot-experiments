package picocli;


import CommandLine.VERSION;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class AutoCompleteHyphenatedCommandTest {
    @Test
    public void testCompletionScript() throws IOException {
        String actual = AutoComplete.bash("rcmd", new CommandLine.CommandLine(new HyphenatedCommand()));
        String expected = String.format(AutoCompleteTest.loadTextFromClasspath("/hyphenated_completion.bash"), VERSION);
        Assert.assertEquals(expected, actual);
    }
}

