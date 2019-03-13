package cucumber.runtime.snippets;


import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;


public class ArgumentPatternTest {
    private Pattern singleDigit = Pattern.compile("(\\d)");

    private ArgumentPattern argumentPattern = new ArgumentPattern(singleDigit);

    @Test
    public void replacesMatchWithoutEscapedNumberClass() {
        Assert.assertEquals("(\\d)", argumentPattern.replaceMatchesWithGroups("1"));
    }

    @Test
    public void replacesMultipleMatchesWithPattern() {
        Assert.assertEquals("(\\d)(\\d)", argumentPattern.replaceMatchesWithGroups("13"));
    }

    @Test
    public void replaceMatchWithSpace() throws Exception {
        Assert.assertEquals(" ", argumentPattern.replaceMatchesWithSpace("4"));
    }
}

