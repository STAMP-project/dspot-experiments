package cucumber.runtime;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class ShellwordsTest {
    @Test
    public void parses_single_quoted_strings() {
        Assert.assertEquals(Arrays.asList("--name", "The Fox"), Shellwords.parse("--name 'The Fox'"));
    }

    @Test
    public void parses_double_quoted_strings() {
        Assert.assertEquals(Arrays.asList("--name", "The Fox"), Shellwords.parse("--name \"The Fox\""));
    }

    @Test
    public void parses_both_single_and_double_quoted_strings() {
        Assert.assertEquals(Arrays.asList("--name", "The Fox", "--fur", "Brown White"), Shellwords.parse("--name \"The Fox\" --fur \'Brown White\'"));
    }

    @Test
    public void can_quote_both_single_and_double_quotes() {
        Assert.assertEquals(Arrays.asList("'", "\""), Shellwords.parse("\"\'\" \'\"\'"));
    }
}

