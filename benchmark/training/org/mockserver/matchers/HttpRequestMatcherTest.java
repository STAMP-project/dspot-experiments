package org.mockserver.matchers;


import MatchType.ONLY_MATCHING_FIELDS;
import java.nio.charset.StandardCharsets;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.mock.HttpStateHandler;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.JsonBody;
import org.mockserver.model.JsonPathBody;
import org.mockserver.model.JsonSchemaBody;
import org.mockserver.model.ParameterBody;
import org.mockserver.model.RegexBody;
import org.mockserver.model.XPathBody;
import org.mockserver.model.XmlBody;
import org.mockserver.model.XmlSchemaBody;
import org.slf4j.LoggerFactory;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.Parameter.param;
import static org.mockserver.model.StringBody.exact;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpRequestMatcherTest {
    private HttpStateHandler httpStateHandler = Mockito.mock(HttpStateHandler.class);

    private MockServerLogger mockServerLogger = new MockServerLogger(LoggerFactory.getLogger(HttpRequestMatcherTest.class), httpStateHandler);

    @Test
    public void shouldAllowUseOfNotWithMatchingRequests() {
        // requests match - matcher HttpRequest notted
        Assert.assertFalse(new HttpRequestMatcher(org.mockserver.model.Not.not(new HttpRequest().withMethod("HEAD")), mockServerLogger).matches(null, new HttpRequest().withMethod("HEAD")));
        // requests match - matched HttpRequest notted
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withMethod("HEAD"), mockServerLogger).matches(null, org.mockserver.model.Not.not(new HttpRequest().withMethod("HEAD"))));
        // requests match - matcher HttpRequest notted & HttpRequestMatch notted
        Assert.assertTrue(NotMatcher.not(new HttpRequestMatcher(org.mockserver.model.Not.not(new HttpRequest().withMethod("HEAD")), mockServerLogger)).matches(null, new HttpRequest().withMethod("HEAD")));
        // requests match - matched HttpRequest notted & HttpRequestMatch notted
        Assert.assertTrue(NotMatcher.not(new HttpRequestMatcher(new HttpRequest().withMethod("HEAD"), mockServerLogger)).matches(null, org.mockserver.model.Not.not(new HttpRequest().withMethod("HEAD"))));
        // requests match - matcher HttpRequest notted & matched HttpRequest notted & HttpRequestMatch notted
        Assert.assertFalse(NotMatcher.not(new HttpRequestMatcher(org.mockserver.model.Not.not(new HttpRequest().withMethod("HEAD")), mockServerLogger)).matches(null, org.mockserver.model.Not.not(new HttpRequest().withMethod("HEAD"))));
    }

    @Test
    public void shouldAllowUseOfNotWithNonMatchingRequests() {
        // requests don't match - matcher HttpRequest notted
        Assert.assertTrue(new HttpRequestMatcher(org.mockserver.model.Not.not(new HttpRequest().withMethod("HEAD")), mockServerLogger).matches(null, new HttpRequest().withMethod("OPTIONS")));
        // requests don't match - matched HttpRequest notted
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withMethod("HEAD"), mockServerLogger).matches(null, org.mockserver.model.Not.not(new HttpRequest().withMethod("OPTIONS"))));
        // requests don't match - matcher HttpRequest notted & HttpRequestMatch notted
        Assert.assertFalse(NotMatcher.not(new HttpRequestMatcher(org.mockserver.model.Not.not(new HttpRequest().withMethod("HEAD")), mockServerLogger)).matches(null, new HttpRequest().withMethod("OPTIONS")));
        // requests don't match - matched HttpRequest notted & HttpRequestMatch notted
        Assert.assertFalse(NotMatcher.not(new HttpRequestMatcher(new HttpRequest().withMethod("HEAD"), mockServerLogger)).matches(null, org.mockserver.model.Not.not(new HttpRequest().withMethod("OPTIONS"))));
        // requests don't match - matcher HttpRequest notted & matched HttpRequest notted & HttpRequestMatch notted
        Assert.assertTrue(NotMatcher.not(new HttpRequestMatcher(org.mockserver.model.Not.not(new HttpRequest().withMethod("HEAD")), mockServerLogger)).matches(null, org.mockserver.model.Not.not(new HttpRequest().withMethod("OPTIONS"))));
    }

    @Test
    public void matchesMatchingKeepAlive() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withKeepAlive(true), mockServerLogger).matches(null, new HttpRequest().withKeepAlive(true)));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withKeepAlive(false), mockServerLogger).matches(null, new HttpRequest().withKeepAlive(false)));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withKeepAlive(null), mockServerLogger).matches(null, new HttpRequest().withKeepAlive(null)));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withKeepAlive(null), mockServerLogger).matches(null, new HttpRequest().withKeepAlive(false)));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withKeepAlive(null), mockServerLogger).matches(null, new HttpRequest()));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest(), mockServerLogger).matches(null, new HttpRequest().withKeepAlive(null)));
    }

    @Test
    public void doesNotMatchIncorrectKeepAlive() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withKeepAlive(true), mockServerLogger).matches(null, new HttpRequest().withKeepAlive(false)));
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withKeepAlive(false), mockServerLogger).matches(null, new HttpRequest().withKeepAlive(true)));
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withKeepAlive(true), mockServerLogger).matches(null, new HttpRequest().withKeepAlive(null)));
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withKeepAlive(false), mockServerLogger).matches(null, new HttpRequest().withKeepAlive(null)));
    }

    @Test
    public void matchesMatchingSsl() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withSecure(true), mockServerLogger).matches(null, new HttpRequest().withSecure(true)));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withSecure(false), mockServerLogger).matches(null, new HttpRequest().withSecure(false)));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withSecure(null), mockServerLogger).matches(null, new HttpRequest().withSecure(null)));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withSecure(null), mockServerLogger).matches(null, new HttpRequest().withSecure(false)));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withSecure(null), mockServerLogger).matches(null, new HttpRequest()));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest(), mockServerLogger).matches(null, new HttpRequest().withSecure(null)));
    }

    @Test
    public void doesNotMatchIncorrectSsl() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withSecure(true), mockServerLogger).matches(null, new HttpRequest().withSecure(false)));
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withSecure(false), mockServerLogger).matches(null, new HttpRequest().withSecure(true)));
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withSecure(true), mockServerLogger).matches(null, new HttpRequest().withSecure(null)));
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withSecure(false), mockServerLogger).matches(null, new HttpRequest().withSecure(null)));
    }

    @Test
    public void matchesMatchingMethod() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withMethod("HEAD"), mockServerLogger).matches(null, new HttpRequest().withMethod("HEAD")));
    }

    @Test
    public void matchesMatchingMethodRegex() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withMethod("P[A-Z]{2}"), mockServerLogger).matches(null, new HttpRequest().withMethod("PUT")));
    }

    @Test
    public void doesNotMatchIncorrectMethod() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withMethod("HEAD"), mockServerLogger).matches(null, new HttpRequest().withMethod("OPTIONS")));
    }

    @Test
    public void doesNotMatchIncorrectMethodRegex() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withMethod("P[A-Z]{2}"), mockServerLogger).matches(null, new HttpRequest().withMethod("POST")));
    }

    @Test
    public void matchesMatchingPath() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withPath("somePath"), mockServerLogger).matches(null, new HttpRequest().withPath("somePath")));
    }

    @Test
    public void doesNotMatchEncodedMatcherPath() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withPath("/dWM%2FdWM+ZA=="), mockServerLogger).matches(null, new HttpRequest().withPath("/dWM/dWM+ZA==")));
    }

    @Test
    public void doesNotMatchEncodedRequestPath() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withPath("/dWM/dWM+ZA=="), mockServerLogger).matches(null, new HttpRequest().withPath("/dWM%2FdWM+ZA==")));
    }

    @Test
    public void matchesMatchingEncodedMatcherAndRequestPath() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withPath("/dWM%2FdWM+ZA=="), mockServerLogger).matches(null, new HttpRequest().withPath("/dWM%2FdWM+ZA==")));
    }

    @Test
    public void matchesMatchingPathRegex() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withPath("someP[a-z]{3}"), mockServerLogger).matches(null, new HttpRequest().withPath("somePath")));
    }

    @Test
    public void doesNotMatchIncorrectPath() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withPath("somepath"), mockServerLogger).matches(null, new HttpRequest().withPath("pathsome")));
    }

    @Test
    public void doesNotMatchIncorrectPathRegex() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withPath("someP[a-z]{2}"), mockServerLogger).matches(null, new HttpRequest().withPath("somePath")));
    }

    @Test
    public void matchesMatchingQueryString() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("someKey", "someValue")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameter(new Parameter("someKey", "someValue"))));
    }

    @Test
    public void matchesMatchingQueryStringRegexKeyAndValue() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("someK[a-z]{2}", "someV[a-z]{4}")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameter(new Parameter("someKey", "someValue"))));
    }

    @Test
    public void matchesMatchingQueryStringRegexKey() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("someK[a-z]{2}", "someValue")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameter(new Parameter("someKey", "someValue"))));
    }

    @Test
    public void matchesMatchingQueryStringRegexValue() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("someKey", "someV[a-z]{4}")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameter(new Parameter("someKey", "someValue"))));
    }

    @Test
    public void doesNotMatchIncorrectQueryStringName() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("someKey", "someValue")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameter(new Parameter("someOtherKey", "someValue"))));
    }

    @Test
    public void doesNotMatchIncorrectQueryStringValue() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("someKey", "someValue")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameter(new Parameter("someKey", "someOtherValue"))));
    }

    @Test
    public void doesNotMatchIncorrectQueryStringRegexKeyAndValue() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("someK[a-z]{5}", "someV[a-z]{2}")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameter(new Parameter("someKey", "someValue"))));
    }

    @Test
    public void doesNotMatchIncorrectQueryStringRegexKey() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("someK[a-z]{5}", "someValue")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameter(new Parameter("someKey", "someValue"))));
    }

    @Test
    public void doesNotMatchIncorrectQueryStringRegexValue() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("someKey", "someV[a-z]{2}")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameter(new Parameter("someKey", "someValue"))));
    }

    @Test
    public void matchesMatchingQueryStringParameters() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("name", "value")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameters(new Parameter("name", "value"))));
    }

    @Test
    public void matchesMatchingQueryStringParametersWithRegex() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("name", "v[a-z]{4}")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameters(new Parameter("name", "value"))));
    }

    @Test
    public void queryStringParametersMatchesMatchingQueryString() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("nameOne", "valueOne")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameters(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"))));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("nameTwo", "valueTwo")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameters(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"))));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("nameTwo", "valueTwo", "valueThree")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameters(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"), new Parameter("nameTwo", "valueThree"))));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("nameTwo", "valueTwo")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameters(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"), new Parameter("nameTwo", "valueThree"))));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("nameTwo", "valueThree")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameters(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"), new Parameter("nameTwo", "valueThree"))));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withQueryStringParameters(new Parameter("nameTwo", "valueT[a-z]{0,10}")), mockServerLogger).matches(null, new HttpRequest().withQueryStringParameters(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"), new Parameter("nameTwo", "valueThree"))));
    }

    @Test
    public void bodyMatchesMatchingBodyParameters() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(ParameterBody.params(new Parameter("nameOne", "valueOne"))), mockServerLogger).matches(null, new HttpRequest().withBody(new ParameterBody(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo")))));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(new ParameterBody(new Parameter("nameTwo", "valueTwo"))), mockServerLogger).matches(null, new HttpRequest().withBody(new ParameterBody(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo")))));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(ParameterBody.params(new Parameter("nameTwo", "valueTwo", "valueThree"))), mockServerLogger).matches(null, new HttpRequest().withBody(new ParameterBody(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"), new Parameter("nameTwo", "valueThree")))));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(new ParameterBody(new Parameter("nameTwo", "valueTwo"))), mockServerLogger).matches(null, new HttpRequest().withBody(new ParameterBody(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"), new Parameter("nameTwo", "valueThree")))));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(ParameterBody.params(new Parameter("nameTwo", "valueThree"))), mockServerLogger).matches(null, new HttpRequest().withBody(new ParameterBody(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"), new Parameter("nameTwo", "valueThree")))));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(new ParameterBody(new Parameter("nameTwo", "valueT[a-z]{0,10}"))), mockServerLogger).matches(null, new HttpRequest().withBody(new ParameterBody(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"), new Parameter("nameTwo", "valueThree")))));
    }

    @Test
    public void bodyMatchesMatchingUrlEncodedBodyParameters() {
        // pass exact match
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(ParameterBody.params(param("name one", "value one"), param("nameTwo", "valueTwo"))), mockServerLogger).matches(null, new HttpRequest().withBody("name+one=value+one&nameTwo=valueTwo")));
        // ignore extra parameters
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(ParameterBody.params(param("name one", "value one"))), mockServerLogger).matches(null, new HttpRequest().withBody("name+one=value+one&nameTwo=valueTwo")));
        // matches multi-value parameters
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(ParameterBody.params(org.mockserver.model.Parameter.param("name one", "value one one", "value one two"))), mockServerLogger).matches(null, new HttpRequest().withBody("name+one=value+one+one&name+one=value+one+two")));
        // matches multi-value parameters (ignore extra values)
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(ParameterBody.params(param("name one", "value one one"))), mockServerLogger).matches(null, new HttpRequest().withBody("name+one=value+one+one&name+one=value+one+two")));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(ParameterBody.params(param("name one", "value one two"))), mockServerLogger).matches(null, new HttpRequest().withBody("name+one=value+one+one&name+one=value+one+two")));
        // matches using regex
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(ParameterBody.params(param("name one", "value [a-z]{0,10}"), param("nameTwo", "valueT[a-z]{0,10}"))), mockServerLogger).matches(null, new HttpRequest().withBody("name+one=value+one&nameTwo=valueTwo")));
        // fail no match
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(ParameterBody.params(param("name one", "value one"))), mockServerLogger).matches(null, new HttpRequest().withBody("name+one=value+two")));
    }

    @Test
    public void bodyMatchesParameterBodyDTO() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(ParameterBody.params(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"))), mockServerLogger).matches(null, new HttpRequest().withBody(new ParameterBodyDTO(ParameterBody.params(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"))).toString()).withMethod("PUT")));
    }

    @Test
    public void doesNotMatchIncorrectParameterName() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(new ParameterBody(new Parameter("name", "value"))), mockServerLogger).matches(null, new HttpRequest().withBody(new ParameterBody(new Parameter("name1", "value")))));
    }

    @Test
    public void doesNotMatchIncorrectParameterValue() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(new ParameterBody(new Parameter("name", "value"))), mockServerLogger).matches(null, new HttpRequest().withBody(new ParameterBody(new Parameter("name", "value1")))));
    }

    @Test
    public void doesNotMatchIncorrectParameterValueRegex() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(new ParameterBody(new Parameter("name", "va[0-9]{1}ue"))), mockServerLogger).matches(null, new HttpRequest().withBody(new ParameterBody(new Parameter("name", "value1")))));
    }

    @Test
    public void doesNotMatchBodyMatchesParameterBodyDTOIncorrectParameters() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(ParameterBody.params(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo"))), mockServerLogger).matches(null, new HttpRequest().withBody(new ParameterBodyDTO(ParameterBody.params(new Parameter("nameOne", "valueOne"))).toString())));
    }

    @Test
    public void matchesMatchingBody() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(new StringBody("somebody")), mockServerLogger).matches(null, new HttpRequest().withBody("somebody")));
    }

    @Test
    public void jsonBodyThatIsNotValidDTODoesNotThrowException() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(new StringBody("somebody")), mockServerLogger).matches(null, new HttpRequest().withBody("{\"method\":\"any\",\"service\":\"any_service\", \"parameters\": { \"applicationName\":\"name\",\"password\":\"pwd\",\"username\":\"user\" } }")));
    }

    @Test
    public void matchesMatchingBodyWithCharset() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(new StringBody("?????", StandardCharsets.UTF_16)), mockServerLogger).matches(null, new HttpRequest().withBody("?????", StandardCharsets.UTF_16)));
    }

    @Test
    public void doesNotMatchIncorrectBody() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(exact("somebody")), mockServerLogger).matches(null, new HttpRequest().withBody("bodysome")));
    }

    @Test
    public void matchesMatchingBodyRegex() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(RegexBody.regex("some[a-z]{4}")), mockServerLogger).matches(null, new HttpRequest().withBody("somebody")));
    }

    @Test
    public void doesNotMatchIncorrectBodyRegex() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(RegexBody.regex("some[a-z]{3}")), mockServerLogger).matches(null, new HttpRequest().withBody("bodysome")));
    }

    @Test
    public void matchesMatchingBodyXPath() {
        String matched = "" + ((("<element>" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>");
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(XPathBody.xpath("/element[key = 'some_key' and value = 'some_value']")), mockServerLogger).matches(null, new HttpRequest().withBody(matched).withMethod("PUT")));
    }

    @Test
    public void matchesMatchingBodyXPathBodyDTO() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(XPathBody.xpath("/element[key = 'some_key' and value = 'some_value']")), mockServerLogger).matches(null, new HttpRequest().withBody(new XPathBodyDTO(XPathBody.xpath("/element[key = 'some_key' and value = 'some_value']")).toString()).withMethod("PUT")));
    }

    @Test
    public void doesNotMatchIncorrectBodyXPath() {
        String matched = "" + (("<element>" + "   <key>some_key</key>") + "</element>");
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(XPathBody.xpath("/element[key = 'some_key' and value = 'some_value']")), mockServerLogger).matches(null, new HttpRequest().withBody(matched)));
    }

    @Test
    public void doesNotMatchIncorrectBodyXPathBodyDTO() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(XPathBody.xpath("/element[key = 'some_key' and value = 'some_value']")), mockServerLogger).matches(null, new HttpRequest().withBody(new XPathBodyDTO(XPathBody.xpath("/element[key = 'some_other_key' and value = 'some_value']")).toString())));
    }

    @Test
    public void matchesMatchingBodyXml() {
        String matched = "" + ((("<element attributeOne=\"one\" attributeTwo=\"two\">" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>");
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(XmlBody.xml(("" + ((("<element attributeTwo=\"two\" attributeOne=\"one\">" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>")))), mockServerLogger).matches(null, new HttpRequest().withBody(matched)));
    }

    @Test
    public void matchesMatchingBodyXmlBodyDTO() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(XmlBody.xml(("" + ((("<element attributeOne=\"one\" attributeTwo=\"two\">" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>")))), mockServerLogger).matches(null, new HttpRequest().withBody(new XmlBodyDTO(XmlBody.xml(("" + ((("<element attributeOne=\"one\" attributeTwo=\"two\">" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>")))).toString()).withMethod("PUT")));
    }

    @Test
    public void doesNotMatchIncorrectBodyXml() {
        String matched = "" + (("<element>" + "   <key>some_key</key>") + "</element>");
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(XmlBody.xml(("" + ((("<element>" + "   <key>some_key</key>") + "   <value>some_value</value>") + "</element>")))), mockServerLogger).matches(null, new HttpRequest().withBody(matched)));
    }

    @Test
    public void doesNotMatchIncorrectBodyXmlBodyDTO() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(XmlBody.xml(("" + (("<element>" + "   <key>some_key</key>") + "</element>")))), mockServerLogger).matches(null, new HttpRequest().withBody(new XmlBodyDTO(XmlBody.xml(("" + ((("<element>" + "   <value>some_value</value>") + "   <key>some_key</key>") + "</element>")))).toString())));
    }

    @Test
    public void matchesMatchingBodyByXmlSchema() {
        String matcher = ((((((((((((((((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (NEW_LINE)) + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">") + (NEW_LINE)) + "    <!-- XML Schema Generated from XML Document on Wed Jun 28 2017 21:52:45 GMT+0100 (BST) -->") + (NEW_LINE)) + "    <!-- with XmlGrid.net Free Online Service http://xmlgrid.net -->") + (NEW_LINE)) + "    <xs:element name=\"notes\">") + (NEW_LINE)) + "        <xs:complexType>") + (NEW_LINE)) + "            <xs:sequence>") + (NEW_LINE)) + "                <xs:element name=\"note\" maxOccurs=\"unbounded\">") + (NEW_LINE)) + "                    <xs:complexType>") + (NEW_LINE)) + "                        <xs:sequence>") + (NEW_LINE)) + "                            <xs:element name=\"to\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"from\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"heading\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"body\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                        </xs:sequence>") + (NEW_LINE)) + "                    </xs:complexType>") + (NEW_LINE)) + "                </xs:element>") + (NEW_LINE)) + "            </xs:sequence>") + (NEW_LINE)) + "        </xs:complexType>") + (NEW_LINE)) + "    </xs:element>") + (NEW_LINE)) + "</xs:schema>";
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(XmlSchemaBody.xmlSchema(matcher)), mockServerLogger).matches(null, new HttpRequest().withBody((((((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + (NEW_LINE)) + "<notes>") + (NEW_LINE)) + "    <note>") + (NEW_LINE)) + "        <to>Bob</to>") + (NEW_LINE)) + "        <from>Bill</from>") + (NEW_LINE)) + "        <heading>Reminder</heading>") + (NEW_LINE)) + "        <body>Buy Bread</body>") + (NEW_LINE)) + "    </note>") + (NEW_LINE)) + "    <note>") + (NEW_LINE)) + "        <to>Jack</to>") + (NEW_LINE)) + "        <from>Jill</from>") + (NEW_LINE)) + "        <heading>Reminder</heading>") + (NEW_LINE)) + "        <body>Wash Shirts</body>") + (NEW_LINE)) + "    </note>") + (NEW_LINE)) + "</notes>"))));
    }

    @Test
    public void matchesMatchingBodyXmlSchemaBodyDTO() {
        String matcher = ((((((((((((((((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (NEW_LINE)) + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">") + (NEW_LINE)) + "    <!-- XML Schema Generated from XML Document on Wed Jun 28 2017 21:52:45 GMT+0100 (BST) -->") + (NEW_LINE)) + "    <!-- with XmlGrid.net Free Online Service http://xmlgrid.net -->") + (NEW_LINE)) + "    <xs:element name=\"notes\">") + (NEW_LINE)) + "        <xs:complexType>") + (NEW_LINE)) + "            <xs:sequence>") + (NEW_LINE)) + "                <xs:element name=\"note\" maxOccurs=\"unbounded\">") + (NEW_LINE)) + "                    <xs:complexType>") + (NEW_LINE)) + "                        <xs:sequence>") + (NEW_LINE)) + "                            <xs:element name=\"to\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"from\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"heading\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"body\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                        </xs:sequence>") + (NEW_LINE)) + "                    </xs:complexType>") + (NEW_LINE)) + "                </xs:element>") + (NEW_LINE)) + "            </xs:sequence>") + (NEW_LINE)) + "        </xs:complexType>") + (NEW_LINE)) + "    </xs:element>") + (NEW_LINE)) + "</xs:schema>";
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(XmlSchemaBody.xmlSchema(matcher)), mockServerLogger).matches(null, new HttpRequest().withBody(XmlBody.xml((((((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + (NEW_LINE)) + "<notes>") + (NEW_LINE)) + "    <note>") + (NEW_LINE)) + "        <to>Bob</to>") + (NEW_LINE)) + "        <from>Bill</from>") + (NEW_LINE)) + "        <heading>Reminder</heading>") + (NEW_LINE)) + "        <body>Buy Bread</body>") + (NEW_LINE)) + "    </note>") + (NEW_LINE)) + "    <note>") + (NEW_LINE)) + "        <to>Jack</to>") + (NEW_LINE)) + "        <from>Jill</from>") + (NEW_LINE)) + "        <heading>Reminder</heading>") + (NEW_LINE)) + "        <body>Wash Shirts</body>") + (NEW_LINE)) + "    </note>") + (NEW_LINE)) + "</notes>")))));
    }

    @Test
    public void doesNotMatchIncorrectBodyByXmlSchema() {
        String matcher = ((((((((((((((((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (NEW_LINE)) + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">") + (NEW_LINE)) + "    <!-- XML Schema Generated from XML Document on Wed Jun 28 2017 21:52:45 GMT+0100 (BST) -->") + (NEW_LINE)) + "    <!-- with XmlGrid.net Free Online Service http://xmlgrid.net -->") + (NEW_LINE)) + "    <xs:element name=\"notes\">") + (NEW_LINE)) + "        <xs:complexType>") + (NEW_LINE)) + "            <xs:sequence>") + (NEW_LINE)) + "                <xs:element name=\"note\" maxOccurs=\"unbounded\">") + (NEW_LINE)) + "                    <xs:complexType>") + (NEW_LINE)) + "                        <xs:sequence>") + (NEW_LINE)) + "                            <xs:element name=\"to\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"from\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"heading\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"body\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                        </xs:sequence>") + (NEW_LINE)) + "                    </xs:complexType>") + (NEW_LINE)) + "                </xs:element>") + (NEW_LINE)) + "            </xs:sequence>") + (NEW_LINE)) + "        </xs:complexType>") + (NEW_LINE)) + "    </xs:element>") + (NEW_LINE)) + "</xs:schema>";
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(XmlSchemaBody.xmlSchema(matcher)), mockServerLogger).matches(null, new HttpRequest().withBody((((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + (NEW_LINE)) + "<notes>") + (NEW_LINE)) + "    <note>") + (NEW_LINE)) + "        <to>Bob</to>") + (NEW_LINE)) + "        <heading>Reminder</heading>") + (NEW_LINE)) + "        <body>Buy Bread</body>") + (NEW_LINE)) + "    </note>") + (NEW_LINE)) + "    <note>") + (NEW_LINE)) + "        <to>Jack</to>") + (NEW_LINE)) + "        <from>Jill</from>") + (NEW_LINE)) + "        <heading>Reminder</heading>") + (NEW_LINE)) + "        <body>Wash Shirts</body>") + (NEW_LINE)) + "    </note>") + (NEW_LINE)) + "</notes>"))));
    }

    @Test
    public void doesNotMatchIncorrectBodyXmlSchemaBodyDTO() {
        String matcher = ((((((((((((((((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (NEW_LINE)) + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">") + (NEW_LINE)) + "    <!-- XML Schema Generated from XML Document on Wed Jun 28 2017 21:52:45 GMT+0100 (BST) -->") + (NEW_LINE)) + "    <!-- with XmlGrid.net Free Online Service http://xmlgrid.net -->") + (NEW_LINE)) + "    <xs:element name=\"notes\">") + (NEW_LINE)) + "        <xs:complexType>") + (NEW_LINE)) + "            <xs:sequence>") + (NEW_LINE)) + "                <xs:element name=\"note\" maxOccurs=\"unbounded\">") + (NEW_LINE)) + "                    <xs:complexType>") + (NEW_LINE)) + "                        <xs:sequence>") + (NEW_LINE)) + "                            <xs:element name=\"to\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"from\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"heading\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"body\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                        </xs:sequence>") + (NEW_LINE)) + "                    </xs:complexType>") + (NEW_LINE)) + "                </xs:element>") + (NEW_LINE)) + "            </xs:sequence>") + (NEW_LINE)) + "        </xs:complexType>") + (NEW_LINE)) + "    </xs:element>") + (NEW_LINE)) + "</xs:schema>";
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(XmlSchemaBody.xmlSchema(matcher)), mockServerLogger).matches(null, new HttpRequest().withBody(XmlBody.xml((((((((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + (NEW_LINE)) + "<notes>") + (NEW_LINE)) + "    <note>") + (NEW_LINE)) + "        <to>Bob</to>") + (NEW_LINE)) + "        <from>Bill</from>") + (NEW_LINE)) + "        <from>Bill</from>") + (NEW_LINE)) + "        <heading>Reminder</heading>") + (NEW_LINE)) + "        <body>Buy Bread</body>") + (NEW_LINE)) + "    </note>") + (NEW_LINE)) + "    <note>") + (NEW_LINE)) + "        <to>Jack</to>") + (NEW_LINE)) + "        <from>Jill</from>") + (NEW_LINE)) + "        <heading>Reminder</heading>") + (NEW_LINE)) + "        <body>Wash Shirts</body>") + (NEW_LINE)) + "    </note>") + (NEW_LINE)) + "</notes>")))));
    }

    @Test
    public void matchesMatchingJSONBody() {
        String matched = "" + ((("{ " + "   \"some_field\": \"some_value\", ") + "   \"some_other_field\": \"some_other_value\" ") + "}");
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(JsonBody.json("{ \"some_field\": \"some_value\" }")), mockServerLogger).matches(null, new HttpRequest().withBody(matched).withMethod("PUT")));
    }

    @Test
    public void matchesMatchingJSONBodyWithCharset() {
        String matched = "" + ((("{ " + "   \"some_field\": \"\u6211\u8bf4\u4e2d\u56fd\u8bdd\", ") + "   \"some_other_field\": \"some_other_value\" ") + "}");
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(JsonBody.json("{ \"some_field\": \"\u6211\u8bf4\u4e2d\u56fd\u8bdd\" }", StandardCharsets.UTF_16, ONLY_MATCHING_FIELDS)), mockServerLogger).matches(null, new HttpRequest().withBody(matched, StandardCharsets.UTF_16).withMethod("PUT")));
    }

    @Test
    public void matchesMatchingJSONBodyDTO() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(JsonBody.json("{ \"some_field\": \"some_value\" }")), mockServerLogger).matches(null, new HttpRequest().withBody(new JsonBodyDTO(JsonBody.json("{ \"some_field\": \"some_value\" }")).toString()).withMethod("PUT")));
    }

    @Test
    public void doesNotMatchIncorrectJSONBody() {
        String matched = "" + ((("{ " + "   \"some_incorrect_field\": \"some_value\", ") + "   \"some_other_field\": \"some_other_value\" ") + "}");
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(JsonBody.json("{ \"some_field\": \"some_value\" }")), mockServerLogger).matches(null, new HttpRequest().withBody(matched)));
    }

    @Test
    public void doesNotMatchIncorrectJSONBodyDTO() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(JsonBody.json("{ \"some_field\": \"some_value\" }")), mockServerLogger).matches(null, new HttpRequest().withBody(new JsonBodyDTO(JsonBody.json("{ \"some_other_field\": \"some_value\" }")).toString())));
    }

    @Test
    public void matchesMatchingJSONSchemaBody() {
        String matched = (((((((((("" + "{") + (NEW_LINE)) + "    \"id\": 1,") + (NEW_LINE)) + "    \"name\": \"A green door\",") + (NEW_LINE)) + "    \"price\": 12.50,") + (NEW_LINE)) + "    \"tags\": [\"home\", \"green\"]") + (NEW_LINE)) + "}";
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(JsonSchemaBody.jsonSchema((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "    \"$schema\": \"http://json-schema.org/draft-04/schema#\",") + (NEW_LINE)) + "    \"title\": \"Product\",") + (NEW_LINE)) + "    \"description\": \"A product from Acme\'s catalog\",") + (NEW_LINE)) + "    \"type\": \"object\",") + (NEW_LINE)) + "    \"properties\": {") + (NEW_LINE)) + "        \"id\": {") + (NEW_LINE)) + "            \"description\": \"The unique identifier for a product\",") + (NEW_LINE)) + "            \"type\": \"integer\"") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"name\": {") + (NEW_LINE)) + "            \"description\": \"Name of the product\",") + (NEW_LINE)) + "            \"type\": \"string\"") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"price\": {") + (NEW_LINE)) + "            \"type\": \"number\",") + (NEW_LINE)) + "            \"minimum\": 0,") + (NEW_LINE)) + "            \"exclusiveMinimum\": true") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"tags\": {") + (NEW_LINE)) + "            \"type\": \"array\",") + (NEW_LINE)) + "            \"items\": {") + (NEW_LINE)) + "                \"type\": \"string\"") + (NEW_LINE)) + "            },") + (NEW_LINE)) + "            \"minItems\": 1,") + (NEW_LINE)) + "            \"uniqueItems\": true") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"required\": [\"id\", \"name\", \"price\"]") + (NEW_LINE)) + "}"))), mockServerLogger).matches(null, new HttpRequest().withBody(matched)));
    }

    @Test
    public void matchesMatchingJSONSchemaBodyDTO() {
        JsonSchemaBody jsonSchemaBody = JsonSchemaBody.jsonSchema((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "    \"$schema\": \"http://json-schema.org/draft-04/schema#\",") + (NEW_LINE)) + "    \"title\": \"Product\",") + (NEW_LINE)) + "    \"description\": \"A product from Acme\'s catalog\",") + (NEW_LINE)) + "    \"type\": \"object\",") + (NEW_LINE)) + "    \"properties\": {") + (NEW_LINE)) + "        \"id\": {") + (NEW_LINE)) + "            \"description\": \"The unique identifier for a product\",") + (NEW_LINE)) + "            \"type\": \"integer\"") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"name\": {") + (NEW_LINE)) + "            \"description\": \"Name of the product\",") + (NEW_LINE)) + "            \"type\": \"string\"") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"price\": {") + (NEW_LINE)) + "            \"type\": \"number\",") + (NEW_LINE)) + "            \"minimum\": 0,") + (NEW_LINE)) + "            \"exclusiveMinimum\": true") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"tags\": {") + (NEW_LINE)) + "            \"type\": \"array\",") + (NEW_LINE)) + "            \"items\": {") + (NEW_LINE)) + "                \"type\": \"string\"") + (NEW_LINE)) + "            },") + (NEW_LINE)) + "            \"minItems\": 1,") + (NEW_LINE)) + "            \"uniqueItems\": true") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"required\": [\"id\", \"name\", \"price\"]") + (NEW_LINE)) + "}"));
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(jsonSchemaBody), mockServerLogger).matches(null, new HttpRequest().withBody(new JsonSchemaBodyDTO(jsonSchemaBody).toString()).withMethod("PUT")));
    }

    @Test
    public void doesNotMatchIncorrectJSONSchemaBody() {
        String matched = (((((((((("" + "{") + (NEW_LINE)) + "    \"id\": 1,") + (NEW_LINE)) + "    \"name\": \"A green door\",") + (NEW_LINE)) + "    \"price\": 12.50,") + (NEW_LINE)) + "    \"tags\": []") + (NEW_LINE)) + "}";
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(JsonSchemaBody.jsonSchema((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "    \"$schema\": \"http://json-schema.org/draft-04/schema#\",") + (NEW_LINE)) + "    \"title\": \"Product\",") + (NEW_LINE)) + "    \"description\": \"A product from Acme\'s catalog\",") + (NEW_LINE)) + "    \"type\": \"object\",") + (NEW_LINE)) + "    \"properties\": {") + (NEW_LINE)) + "        \"id\": {") + (NEW_LINE)) + "            \"description\": \"The unique identifier for a product\",") + (NEW_LINE)) + "            \"type\": \"integer\"") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"name\": {") + (NEW_LINE)) + "            \"description\": \"Name of the product\",") + (NEW_LINE)) + "            \"type\": \"string\"") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"price\": {") + (NEW_LINE)) + "            \"type\": \"number\",") + (NEW_LINE)) + "            \"minimum\": 0,") + (NEW_LINE)) + "            \"exclusiveMinimum\": true") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"tags\": {") + (NEW_LINE)) + "            \"type\": \"array\",") + (NEW_LINE)) + "            \"items\": {") + (NEW_LINE)) + "                \"type\": \"string\"") + (NEW_LINE)) + "            },") + (NEW_LINE)) + "            \"minItems\": 1,") + (NEW_LINE)) + "            \"uniqueItems\": true") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"required\": [\"id\", \"name\", \"price\"]") + (NEW_LINE)) + "}"))), mockServerLogger).matches(null, new HttpRequest().withBody(matched)));
    }

    @Test
    public void matchesMatchingBodyJsonPath() {
        String matched = "" + ((((((((((((((((((((((("{\n" + "    \"store\": {\n") + "        \"book\": [\n") + "            {\n") + "                \"category\": \"reference\",\n") + "                \"author\": \"Nigel Rees\",\n") + "                \"title\": \"Sayings of the Century\",\n") + "                \"price\": 8.95\n") + "            },\n") + "            {\n") + "                \"category\": \"fiction\",\n") + "                \"author\": \"Herman Melville\",\n") + "                \"title\": \"Moby Dick\",\n") + "                \"isbn\": \"0-553-21311-3\",\n") + "                \"price\": 8.99\n") + "            }\n") + "        ],\n") + "        \"bicycle\": {\n") + "            \"color\": \"red\",\n") + "            \"price\": 19.95\n") + "        }\n") + "    },\n") + "    \"expensive\": 10\n") + "}");
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(JsonPathBody.jsonPath("$..book[?(@.price <= $['expensive'])]")), mockServerLogger).matches(null, new HttpRequest().withBody(matched).withMethod("PUT")));
    }

    @Test
    public void matchesMatchingBodyJsonPathBodyDTO() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(JsonPathBody.jsonPath("$..book[?(@.price > $['expensive'])]")), mockServerLogger).matches(null, new HttpRequest().withBody(new JsonPathBodyDTO(JsonPathBody.jsonPath("$..book[?(@.price > $['expensive'])]")).toString()).withMethod("PUT")));
    }

    @Test
    public void doesNotMatchIncorrectBodyJsonPath() {
        String matched = "" + ((((((((((((((((((((((("{\n" + "    \"store\": {\n") + "        \"book\": [\n") + "            {\n") + "                \"category\": \"reference\",\n") + "                \"author\": \"Nigel Rees\",\n") + "                \"title\": \"Sayings of the Century\",\n") + "                \"price\": 8.95\n") + "            },\n") + "            {\n") + "                \"category\": \"fiction\",\n") + "                \"author\": \"Herman Melville\",\n") + "                \"title\": \"Moby Dick\",\n") + "                \"isbn\": \"0-553-21311-3\",\n") + "                \"price\": 8.99\n") + "            }\n") + "        ],\n") + "        \"bicycle\": {\n") + "            \"color\": \"red\",\n") + "            \"price\": 19.95\n") + "        }\n") + "    },\n") + "    \"expensive\": 10\n") + "}");
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(JsonPathBody.jsonPath("$..book[?(@.price > $['expensive'])]")), mockServerLogger).matches(null, new HttpRequest().withBody(matched)));
    }

    @Test
    public void doesNotMatchIncorrectBodyJsonPathBodyDTO() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(XPathBody.xpath("$..book[?(@.price > $['expensive'])]")), mockServerLogger).matches(null, new HttpRequest().withBody(new JsonPathBodyDTO(JsonPathBody.jsonPath("$..book[?(@.price <= $['expensive'])]")).toString())));
    }

    @Test
    public void matchesMatchingBinaryBody() {
        byte[] matched = "some binary value".getBytes(StandardCharsets.UTF_8);
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(BinaryBody.binary("some binary value".getBytes(StandardCharsets.UTF_8))), mockServerLogger).matches(null, new HttpRequest().withBody(BinaryBody.binary(matched))));
    }

    @Test
    public void matchesMatchingBinaryBodyDTO() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withBody(BinaryBody.binary("some binary value".getBytes(StandardCharsets.UTF_8))), mockServerLogger).matches(null, new HttpRequest().withMethod("PUT").withBody(new BinaryBodyDTO(BinaryBody.binary("some binary value".getBytes(StandardCharsets.UTF_8))).toString())));
    }

    @Test
    public void doesNotMatchIncorrectBinaryBody() {
        byte[] matched = "some other binary value".getBytes(StandardCharsets.UTF_8);
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(BinaryBody.binary("some binary value".getBytes(StandardCharsets.UTF_8))), mockServerLogger).matches(null, new HttpRequest().withBody(BinaryBody.binary(matched))));
    }

    @Test
    public void doesNotMatchIncorrectBinaryBodyDTO() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withBody(BinaryBody.binary("some binary value".getBytes(StandardCharsets.UTF_8))), mockServerLogger).matches(null, new HttpRequest().withBody(new BinaryBodyDTO(BinaryBody.binary("some other binary value".getBytes(StandardCharsets.UTF_8))).toString())));
    }

    @Test
    public void matchesMatchingHeaders() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withHeaders(new Header("name", "value")), mockServerLogger).matches(null, new HttpRequest().withHeaders(new Header("name", "value"))));
    }

    @Test
    public void matchesMatchingHeadersWithRegex() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withHeaders(new Header("name", ".*")), mockServerLogger).matches(null, new HttpRequest().withHeaders(new Header("name", "value"))));
    }

    @Test
    public void doesNotMatchIncorrectHeaderName() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withHeaders(new Header("name", "value")), mockServerLogger).matches(null, new HttpRequest().withHeaders(new Header("name1", "value"))));
    }

    @Test
    public void doesNotMatchIncorrectHeaderValue() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withHeaders(new Header("name", "value")), mockServerLogger).matches(null, new HttpRequest().withHeaders(new Header("name", "value1"))));
    }

    @Test
    public void doesNotMatchIncorrectHeaderValueRegex() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withHeaders(new Header("name", "[0-9]{0,100}")), mockServerLogger).matches(null, new HttpRequest().withHeaders(new Header("name", "value1"))));
    }

    @Test
    public void matchesMatchingCookies() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withCookies(new Cookie("name", "value")), mockServerLogger).matches(null, new HttpRequest().withCookies(new Cookie("name", "value"))));
    }

    @Test
    public void matchesMatchingCookiesWithRegex() {
        Assert.assertTrue(new HttpRequestMatcher(new HttpRequest().withCookies(new Cookie("name", "[a-z]{0,20}lue")), mockServerLogger).matches(null, new HttpRequest().withCookies(new Cookie("name", "value"))));
    }

    @Test
    public void doesNotMatchIncorrectCookieName() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withCookies(new Cookie("name", "value")), mockServerLogger).matches(null, new HttpRequest().withCookies(new Cookie("name1", "value"))));
    }

    @Test
    public void doesNotMatchIncorrectCookieValue() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withCookies(new Cookie("name", "value")), mockServerLogger).matches(null, new HttpRequest().withCookies(new Cookie("name", "value1"))));
    }

    @Test
    public void doesNotMatchIncorrectCookieValueRegex() {
        Assert.assertFalse(new HttpRequestMatcher(new HttpRequest().withCookies(new Cookie("name", "[A-Z]{0,10}")), mockServerLogger).matches(null, new HttpRequest().withCookies(new Cookie("name", "value1"))));
    }

    @Test
    public void shouldReturnFormattedRequestWithStringBodyInToString() {
        TestCase.assertEquals((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"GET\",") + (NEW_LINE)) + "  \"path\" : \"/some/path\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"parameterOneName\" : [ \"parameterOneValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"name\" : [ \"value\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"name\" : \"[A-Z]{0,10}\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : \"some_body\"") + (NEW_LINE)) + "}"), new HttpRequestMatcher(request().withMethod("GET").withPath("/some/path").withQueryStringParameters(param("parameterOneName", "parameterOneValue")).withBody("some_body").withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "[A-Z]{0,10}")), mockServerLogger).toString());
    }
}

