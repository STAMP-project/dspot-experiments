package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;


/**
 *
 *
 * @author jamesdbloom
 */
public class XPathMatcherTest {
    @Test
    public void shouldMatchMatchingXPath() {
        String matched = "" + ((("<element>" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>");
        Assert.assertTrue(matches(null, matched));
        Assert.assertTrue(matches(null, matched));
        Assert.assertTrue(matches(null, matched));
        Assert.assertTrue(matches(null, matched));
    }

    @Test
    public void shouldNotMatchMatchingXPathWithNot() {
        String matched = "" + ((("<element>" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>");
        Assert.assertFalse(matches(null, matched));
        Assert.assertFalse(matches(null, matched));
        Assert.assertFalse(matches(null, matched));
        Assert.assertFalse(matches(null, matched));
    }

    @Test
    public void shouldMatchMatchingString() {
        Assert.assertTrue(matches(null, "some_value"));
        Assert.assertFalse(matches(null, "some_other_value"));
    }

    @Test
    public void shouldNotMatchNullExpectation() {
        Assert.assertFalse(matches(null, "some_value"));
    }

    @Test
    public void shouldNotMatchEmptyExpectation() {
        Assert.assertFalse(matches(null, "some_value"));
    }

    @Test
    public void shouldNotMatchNotMatchingXPath() {
        String matched = "" + ((("<element>" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>");
        Assert.assertFalse(matches(null, matched));
        Assert.assertFalse(matches(null, matched));
        Assert.assertFalse(matches(null, matched));
        Assert.assertFalse(matches(null, matched));
    }

    @Test
    public void shouldMatchNotMatchingXPathWithNot() {
        String matched = "" + ((("<element>" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>");
        Assert.assertTrue(matches(null, matched));
        Assert.assertTrue(matches(null, matched));
        Assert.assertTrue(matches(null, matched));
        Assert.assertTrue(matches(null, matched));
    }

    @Test
    public void shouldNotMatchNullTest() {
        Assert.assertFalse(new XPathMatcher(new MockServerLogger(), "some_value").matches(null, null));
    }

    @Test
    public void shouldNotMatchEmptyTest() {
        Assert.assertFalse(matches(null, ""));
    }

    @Test
    public void showHaveCorrectEqualsBehaviour() {
        MockServerLogger mockServerLogger = new MockServerLogger();
        Assert.assertEquals(new XPathMatcher(mockServerLogger, "some_value"), new XPathMatcher(mockServerLogger, "some_value"));
    }
}

