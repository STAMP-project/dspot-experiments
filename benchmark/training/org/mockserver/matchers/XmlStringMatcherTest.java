package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class XmlStringMatcherTest {
    @Test
    public void shouldMatchMatchingXML() {
        String matched = "" + ((("<element>" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>");
        Assert.assertTrue(matches(matched));
        Assert.assertTrue(matches(matched));
    }

    @Test
    public void shouldMatchMatchingXMLWithDifferentNamespaceOrders() {
        String matched = "" + (((((((((((("<?xml version=\"1.0\"?>\n" + "\n") + "<soap:Envelope\n") + "xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope/\"\n") + "soap:encodingStyle=\"http://www.w3.org/2003/05/soap-encoding\">\n") + "\n") + "<soap:Body xmlns:m=\"http://www.example.org/stock\">\n") + "  <m:GetStockPriceResponse>\n") + "    <m:Price>34.5</m:Price>\n") + "  </m:GetStockPriceResponse>\n") + "</soap:Body>\n") + "\n") + "</soap:Envelope>");
        Assert.assertTrue(matches(matched));
    }

    @Test
    public void shouldNotMatchMatchingXMLWithNot() {
        String matched = "" + ((("<element>" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>");
        Assert.assertFalse(matches(matched));
        Assert.assertFalse(matches(matched));
    }

    @Test
    public void shouldMatchMatchingXMLWithDifferentAttributeOrder() {
        String matched = "" + ((("<element attributeOne=\"one\" attributeTwo=\"two\">" + "   <key attributeOne=\"one\" attributeTwo=\"two\">some_key</key>") + "   <value>some_value</value>") + "</element>");
        Assert.assertTrue(matches(matched));
        Assert.assertTrue(matches(matched));
    }

    @Test
    public void shouldNotMatchInvalidXml() {
        Assert.assertFalse(matches("<element></element>"));
        Assert.assertFalse(matches("invalid_xml"));
    }

    @Test
    public void shouldNotMatchNullExpectation() {
        Assert.assertFalse(matches("some_value"));
        Assert.assertFalse(matches("some_value"));
    }

    @Test
    public void shouldNotMatchEmptyExpectation() {
        Assert.assertFalse(matches("some_value"));
    }

    @Test
    public void shouldNotMatchNotMatchingXML() {
        String matched = "" + ((("<element>" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>");
        Assert.assertFalse(matches(matched));
        Assert.assertFalse(matches(matched));
        Assert.assertFalse(matches(matched));
        Assert.assertFalse(matches(matched));
    }

    @Test
    public void shouldMatchNotMatchingXMLWithNot() {
        String matched = "" + ((("<element>" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>");
        Assert.assertTrue(matches(matched));
        Assert.assertTrue(matches(matched));
    }

    @Test
    public void shouldNotMatchXMLWithDifferentAttributes() {
        String matched = "" + ((("<element someAttribute=\"some_value\">" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>");
        Assert.assertFalse(matches(matched));
        Assert.assertFalse(matches(matched));
    }

    @Test
    public void shouldNotMatchNullTest() {
        Assert.assertFalse(new XmlStringMatcher(new MockServerLogger(), "some_value").matches(null, NottableString.string(null)));
        Assert.assertFalse(matches(((String) (null))));
    }

    @Test
    public void shouldMatchNullTest() {
        Assert.assertTrue(NotMatcher.not(new XmlStringMatcher(new MockServerLogger(), "some_value")).matches(null, NottableString.string(null)));
        Assert.assertTrue(matches(((String) (null))));
    }

    @Test
    public void shouldNotMatchEmptyTest() {
        Assert.assertFalse(matches(""));
    }

    @Test
    public void showHaveCorrectEqualsBehaviour() {
        MockServerLogger mockServerLogger = new MockServerLogger();
        Assert.assertEquals(new XmlStringMatcher(mockServerLogger, "some_value"), new XmlStringMatcher(mockServerLogger, "some_value"));
    }
}

