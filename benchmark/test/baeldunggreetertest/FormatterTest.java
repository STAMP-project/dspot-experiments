package baeldunggreetertest;


import baeldunggreeter.Formatter;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;


public class FormatterTest {
    @Test
    public void testFormatter() {
        String dateRegex1 = "^((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])$";
        String dateString = Formatter.getFormattedDate();
        Assert.assertTrue(Pattern.matches(dateRegex1, dateString));
    }
}

