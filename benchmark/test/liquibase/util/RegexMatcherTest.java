package liquibase.util;


import java.util.regex.PatternSyntaxException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author lujop
 */
public class RegexMatcherTest {
    private RegexMatcher matcher;

    private static final String text = "Pulp Fiction\n" + ("Reservoir Dogs\n" + "Kill Bill\n");

    @Test(expected = PatternSyntaxException.class)
    public void testBadPatternFails() {
        matcher = new RegexMatcher(RegexMatcherTest.text, new String[]{ "a(j" });
    }

    @Test
    public void testMatchingInSequentialOrder() {
        matcher = new RegexMatcher(RegexMatcherTest.text, new String[]{ "Pulp", "Reservoir", "Kill" });
        Assert.assertTrue("All matched", matcher.allMatchedInSequentialOrder());
        matcher = new RegexMatcher(RegexMatcherTest.text, new String[]{ "Pulp", "ion" });
        Assert.assertTrue("All matched", matcher.allMatchedInSequentialOrder());
        matcher = new RegexMatcher(RegexMatcherTest.text, new String[]{ "Pu.p", "^Ki.+ll$" });
        Assert.assertTrue("All matched", matcher.allMatchedInSequentialOrder());
        matcher = new RegexMatcher(RegexMatcherTest.text, new String[]{ "pulP", "kiLL" });
        Assert.assertTrue("Case insensitive", matcher.allMatchedInSequentialOrder());
        matcher = new RegexMatcher(RegexMatcherTest.text, new String[]{ "Reservoir", "Pulp", "Dogs" });
        Assert.assertFalse("Not in order", matcher.allMatchedInSequentialOrder());
        matcher = new RegexMatcher(RegexMatcherTest.text, new String[]{ "Memento" });
        Assert.assertFalse("Not found", matcher.allMatchedInSequentialOrder());
    }
}

