package liquibase.util;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class BooleanParserTest {
    private String input;

    private boolean expected;

    public BooleanParserTest(String input, boolean expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void checkParseBoolean() {
        Assert.assertEquals((((("Value '" + (input)) + "' should be treated as '") + (expected)) + "'"), expected, BooleanParser.parseBoolean(input));
    }
}

