package org.mockserver.serialization;


import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.JsonBody;
import org.mockserver.model.JsonPathBody;
import org.mockserver.model.JsonSchemaBody;
import org.mockserver.model.NottableString;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;
import org.mockserver.model.RegexBody;
import org.mockserver.model.StringBody;
import org.mockserver.model.XPathBody;
import org.mockserver.model.XmlBody;
import org.mockserver.model.XmlSchemaBody;
import org.mockserver.serialization.model.BodyDTO;
import org.mockserver.serialization.model.BodyWithContentTypeDTO;
import org.mockserver.serialization.model.HttpRequestDTO;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpRequestSerializerIntegrationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldValidateHeaderValueIsList() {
        // given
        String requestBytes = ((((((("{" + (NEW_LINE)) + "    \"headers\" : {") + (NEW_LINE)) + "        \"someHeaderName\" : \"someHeaderValue\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(((((((((((((((((((((((((((((((((((("1 error:" + (NEW_LINE)) + " - for field \"/headers\" only one of the following example formats is allowed: ") + (NEW_LINE)) + (NEW_LINE)) + "    \"/headers\" : {") + (NEW_LINE)) + "        \"exampleHeaderName\" : [ \"exampleHeaderValue\" ]") + (NEW_LINE)) + "        \"exampleMultiValuedHeaderName\" : [ \"exampleHeaderValueOne\", \"exampleHeaderValueTwo\" ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + (NEW_LINE)) + "   or:") + (NEW_LINE)) + (NEW_LINE)) + "    \"/headers\" : [") + (NEW_LINE)) + "        {") + (NEW_LINE)) + "            \"name\" : \"exampleHeaderName\",") + (NEW_LINE)) + "            \"values\" : [ \"exampleHeaderValue\" ]") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        {") + (NEW_LINE)) + "            \"name\" : \"exampleMultiValuedHeaderName\",") + (NEW_LINE)) + "            \"values\" : [ \"exampleHeaderValueOne\", \"exampleHeaderValueTwo\" ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    ]"));
        // when
        deserialize(requestBytes);
    }

    @Test
    public void shouldValidateHeaderNameValid() {
        // given
        String requestBytes = ((((((("{" + (NEW_LINE)) + "    \"headers\" : {") + (NEW_LINE)) + "        \"someHea derName\" : [ \"someHeaderValue\" ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(((((((((((((((((((((((((((((((((((("1 error:" + (NEW_LINE)) + " - for field \"/headers\" only one of the following example formats is allowed: ") + (NEW_LINE)) + (NEW_LINE)) + "    \"/headers\" : {") + (NEW_LINE)) + "        \"exampleHeaderName\" : [ \"exampleHeaderValue\" ]") + (NEW_LINE)) + "        \"exampleMultiValuedHeaderName\" : [ \"exampleHeaderValueOne\", \"exampleHeaderValueTwo\" ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + (NEW_LINE)) + "   or:") + (NEW_LINE)) + (NEW_LINE)) + "    \"/headers\" : [") + (NEW_LINE)) + "        {") + (NEW_LINE)) + "            \"name\" : \"exampleHeaderName\",") + (NEW_LINE)) + "            \"values\" : [ \"exampleHeaderValue\" ]") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        {") + (NEW_LINE)) + "            \"name\" : \"exampleMultiValuedHeaderName\",") + (NEW_LINE)) + "            \"values\" : [ \"exampleHeaderValueOne\", \"exampleHeaderValueTwo\" ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    ]"));
        // when
        deserialize(requestBytes);
    }

    @Test
    public void shouldValidateQueryStringParameterValueIsList() {
        // given
        String requestBytes = ((((((("{" + (NEW_LINE)) + "    \"queryStringParameters\" : {") + (NEW_LINE)) + "        \"someParameterName\" : \"someParameterValue\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(((((((((((((((((((((((((((((((((((("1 error:" + (NEW_LINE)) + " - for field \"/queryStringParameters\" only one of the following example formats is allowed: ") + (NEW_LINE)) + (NEW_LINE)) + "    \"/queryStringParameters\" : {") + (NEW_LINE)) + "        \"exampleParameterName\" : [ \"exampleParameterValue\" ]") + (NEW_LINE)) + "        \"exampleMultiValuedParameterName\" : [ \"exampleParameterValueOne\", \"exampleParameterValueTwo\" ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + (NEW_LINE)) + "   or:") + (NEW_LINE)) + (NEW_LINE)) + "    \"/queryStringParameters\" : [") + (NEW_LINE)) + "        {") + (NEW_LINE)) + "            \"name\" : \"exampleParameterName\",") + (NEW_LINE)) + "            \"values\" : [ \"exampleParameterValue\" ]") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        {") + (NEW_LINE)) + "            \"name\" : \"exampleMultiValuedParameterName\",") + (NEW_LINE)) + "            \"values\" : [ \"exampleParameterValueOne\", \"exampleParameterValueTwo\" ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    ]"));
        // when
        deserialize(requestBytes);
    }

    @Test
    public void shouldValidateQueryStringParameterNameValid() {
        // given
        String requestBytes = ((((((("{" + (NEW_LINE)) + "    \"queryStringParameters\" : {") + (NEW_LINE)) + "        \"somePara meterName\" : [ \"someParameterValue\" ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(((((((((((((((((((((((((((((((((((("1 error:" + (NEW_LINE)) + " - for field \"/queryStringParameters\" only one of the following example formats is allowed: ") + (NEW_LINE)) + (NEW_LINE)) + "    \"/queryStringParameters\" : {") + (NEW_LINE)) + "        \"exampleParameterName\" : [ \"exampleParameterValue\" ]") + (NEW_LINE)) + "        \"exampleMultiValuedParameterName\" : [ \"exampleParameterValueOne\", \"exampleParameterValueTwo\" ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + (NEW_LINE)) + "   or:") + (NEW_LINE)) + (NEW_LINE)) + "    \"/queryStringParameters\" : [") + (NEW_LINE)) + "        {") + (NEW_LINE)) + "            \"name\" : \"exampleParameterName\",") + (NEW_LINE)) + "            \"values\" : [ \"exampleParameterValue\" ]") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        {") + (NEW_LINE)) + "            \"name\" : \"exampleMultiValuedParameterName\",") + (NEW_LINE)) + "            \"values\" : [ \"exampleParameterValueOne\", \"exampleParameterValueTwo\" ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    ]"));
        // when
        deserialize(requestBytes);
    }

    @Test
    public void shouldValidateCookieNameValid() {
        // given
        String requestBytes = ((((((("{" + (NEW_LINE)) + "    \"cookies\" : {") + (NEW_LINE)) + "        \"someCoo kieName\" : \"someCookieValue\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(((((((((((((((((((((((((((((((((((("1 error:" + (NEW_LINE)) + " - for field \"/cookies\" only one of the following example formats is allowed: ") + (NEW_LINE)) + (NEW_LINE)) + "    \"/cookies\" : {") + (NEW_LINE)) + "        \"exampleCookieNameOne\" : \"exampleCookieValueOne\"") + (NEW_LINE)) + "        \"exampleCookieNameTwo\" : \"exampleCookieValueTwo\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + (NEW_LINE)) + "   or:") + (NEW_LINE)) + (NEW_LINE)) + "    \"/cookies\" : [") + (NEW_LINE)) + "        {") + (NEW_LINE)) + "            \"name\" : \"exampleCookieNameOne\",") + (NEW_LINE)) + "            \"values\" : \"exampleCookieValueOne\"") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        {") + (NEW_LINE)) + "            \"name\" : \"exampleCookieNameTwo\",") + (NEW_LINE)) + "            \"values\" : \"exampleCookieValueTwo\"") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    ]"));
        // when
        deserialize(requestBytes);
    }

    @Test
    public void shouldValidateExtraFields() {
        // given
        String requestBytes = ((((("{" + (NEW_LINE)) + "    \"path\": \"somePath\",") + (NEW_LINE)) + "    \"extra_field\": \"extra_value\"") + (NEW_LINE)) + "}";
        // then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage((("1 error:" + (NEW_LINE)) + " - object instance has properties which are not allowed by the schema: [\"extra_field\"]"));
        // when
        deserialize(requestBytes);
    }

    @Test
    public void shouldDeserializeCompleteObject() {
        // given
        String requestBytes = ((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"someMethod\",") + (NEW_LINE)) + "  \"keepAlive\" : false,") + (NEW_LINE)) + "  \"queryStringParameters\" : [ {") + (NEW_LINE)) + "    \"name\" : \"queryParameterName\",") + (NEW_LINE)) + "    \"values\" : [ \"queryParameterValue\" ]") + (NEW_LINE)) + "  } ],") + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"STRING\",") + (NEW_LINE)) + "    \"string\" : \"someBody\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : [ {") + (NEW_LINE)) + "    \"name\" : \"someCookieName\",") + (NEW_LINE)) + "    \"value\" : \"someCookieValue\"") + (NEW_LINE)) + "  } ],") + (NEW_LINE)) + "  \"headers\" : [ {") + (NEW_LINE)) + "    \"name\" : \"someHeaderName\",") + (NEW_LINE)) + "    \"values\" : [ \"someHeaderValue\" ]") + (NEW_LINE)) + "  } ],") + (NEW_LINE)) + "  \"path\" : \"somePath\",") + (NEW_LINE)) + "  \"secure\" : true") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setMethod(NottableString.string("someMethod")).setPath(NottableString.string("somePath")).setQueryStringParameters(new Parameters().withEntries(Parameter.param("queryParameterName", "queryParameterValue"))).setBody(new org.mockserver.serialization.model.StringBodyDTO(StringBody.exact("someBody"))).setHeaders(new Headers().withEntries(Header.header("someHeaderName", "someHeaderValue"))).setCookies(new Cookies().withEntries(Cookie.cookie("someCookieName", "someCookieValue"))).setSecure(true).setKeepAlive(false).buildObject(), httpRequest);
    }

    @Test
    public void shouldDeserializeStringBodyShorthand() {
        // given
        String requestBytes = ((("{" + (NEW_LINE)) + "  \"body\" : \"somebody\"") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setBody(BodyDTO.createDTO(StringBody.exact("somebody"))).buildObject(), httpRequest);
    }

    @Test
    public void shouldDeserializeStringBodyWithType() {
        // given
        String requestBytes = ((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"STRING\",") + (NEW_LINE)) + "    \"string\" : \"somebody\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setBody(BodyDTO.createDTO(StringBody.exact("somebody"))).buildObject(), httpRequest);
    }

    @Test
    public void shouldDeserializeRegexBody() {
        // given
        String requestBytes = ((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"REGEX\",") + (NEW_LINE)) + "    \"regex\" : \"some[a-z]{3}\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setBody(BodyDTO.createDTO(RegexBody.regex("some[a-z]{3}"))).buildObject(), httpRequest);
    }

    @Test
    public void shouldDeserializeJsonBody() {
        // given
        String requestBytes = ((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"JSON\",") + (NEW_LINE)) + "    \"json\" : \"{ \\\"key\\\": \\\"value\\\" }\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        HttpRequest expected = new HttpRequestDTO().setBody(BodyDTO.createDTO(JsonBody.json("{ \"key\": \"value\" }"))).buildObject();
        Assert.assertEquals(expected, httpRequest);
    }

    @Test
    public void shouldDeserializeJsonSchemaBody() {
        // given
        String requestBytes = ((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"JSON_SCHEMA\",") + (NEW_LINE)) + "    \"jsonSchema\" : \"{ \\\"key\\\": \\\"value\\\" }\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setBody(BodyDTO.createDTO(JsonSchemaBody.jsonSchema("{ \"key\": \"value\" }"))).buildObject(), httpRequest);
    }

    @Test
    public void shouldDeserializeJsonPathBody() {
        // given
        String requestBytes = ((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"JSON_PATH\",") + (NEW_LINE)) + "    \"jsonPath\" : \"$..book[?(@.price <= $[\'expensive\'])]\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setBody(BodyDTO.createDTO(JsonPathBody.jsonPath("$..book[?(@.price <= $['expensive'])]"))).buildObject(), httpRequest);
    }

    @Test
    public void shouldDeserializeXmlBody() {
        // given
        String requestBytes = ((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"XML\",") + (NEW_LINE)) + "    \"xml\" : \"<some><xml></xml></some>\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setBody(BodyDTO.createDTO(XmlBody.xml("<some><xml></xml></some>"))).buildObject(), httpRequest);
    }

    @Test
    public void shouldDeserializeXmlSchemaBody() {
        // given
        String xmlSchema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ((((((((((((((((((("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">" + "    <!-- XML Schema Generated from XML Document on Wed Jun 28 2017 21:52:45 GMT+0100 (BST) -->") + "    <!-- with XmlGrid.net Free Online Service http://xmlgrid.net -->") + "    <xs:element name=\"notes\">") + "        <xs:complexType>") + "            <xs:sequence>") + "                <xs:element name=\"note\" maxOccurs=\"unbounded\">") + "                    <xs:complexType>") + "                        <xs:sequence>") + "                            <xs:element name=\"to\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + "                            <xs:element name=\"from\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + "                            <xs:element name=\"heading\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + "                            <xs:element name=\"body\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + "                        </xs:sequence>") + "                    </xs:complexType>") + "                </xs:element>") + "            </xs:sequence>") + "        </xs:complexType>") + "    </xs:element>") + "</xs:schema>");
        String requestBytes = ((((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"XML_SCHEMA\",") + (NEW_LINE)) + "    \"xmlSchema\" : \"") + (StringEscapeUtils.escapeJava(xmlSchema))) + "\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setBody(BodyDTO.createDTO(XmlSchemaBody.xmlSchema(xmlSchema))).buildObject(), httpRequest);
    }

    @Test
    public void shouldDeserializeXpathBody() {
        // given
        String requestBytes = ((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"XPATH\",") + (NEW_LINE)) + "    \"xpath\" : \"/element[key = \'some_key\' and value = \'some_value\']\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setBody(BodyDTO.createDTO(XPathBody.xpath("/element[key = 'some_key' and value = 'some_value']"))).buildObject(), httpRequest);
    }

    @Test
    public void shouldDeserializeParameterBody() {
        // given
        String requestBytes = ((((((((((((((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "    \"parameters\" : [ {") + (NEW_LINE)) + "      \"name\" : \"nameOne\",") + (NEW_LINE)) + "      \"values\" : [ \"valueOne\" ]") + (NEW_LINE)) + "    }, {") + (NEW_LINE)) + "      \"name\" : \"nameTwo\",") + (NEW_LINE)) + "      \"values\" : [ \"valueTwo_One\", \"valueTwo_Two\" ]") + (NEW_LINE)) + "    } ]") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setBody(BodyDTO.createDTO(ParameterBody.params(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo_One", "valueTwo_Two")))).buildObject(), httpRequest);
    }

    @Test
    public void shouldDeserializeBinaryBody() throws IOException {
        // given
        String requestBytes = ((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"BINARY\",") + (NEW_LINE)) + "    \"base64Bytes\" : \"iVBORw0KGgoAAAANSUhEUgAAAJUAAACVCAYAAABRorhPAAAbcUlEQVR4nO2daXSVRZrHK4mXJQQSsgGKIgaUzaiobW+IaGwRsBsbkEWardtWDgdF5CAKNjjTLuwgCZCQQCCQfSMsAcKSACEJWwC1e45Kz9gzZ6b7zDk9019m5lvN869UJXXfvDe5yXuTu6Tq+D9Jbm7ihfvjqaeerRjnnNlJW2FSEaR7SL1JfUlRexnbVDZyJL+yZAk/M2kSz2GMH5Y6IpUrlSeVL1UgVdhFqnzkEX6NXtfFl1/mub16ffMMY/QfG0EaRrqXlEiKJQ0gRco/l0v+OcPVn9vT34+RZ3n+RttQ4U3oTxqYytjWgvh43rBsGb84ezYvSkhwg8qfYJVGRfGaF1/kZ554AsB/8xRjP6HX/DBpOGkoaZABKwChwr/4XS7XzhNPPslvvv8+r5k5s9liWaFSYOUzd7C6Ciro+P3386uLFvHLP/85L4yL+248YxPoNY8iPUS634DlX6jCJVS91PanoMK/+u2M7crt149ffv11XvXCCzyvT5/AAeuBBwRUZ59+mh9i7NsnGJtIr3k0KUmCNdiA1b1QKbB0qPpIqGJICdJHeXArY2nHHnuM3/n4Y+FnFQwYEBD+FVRGr6X21Vd59fPP84LY2LsSrDEGLP9DFSH/otUWiDcgTr4hD5BGbmJsb/HQobz2V7/ip599NmAc96LwcOG8N9DrqiZLCov1OGPPSbBGGLD8A5XuV9lugazJTxm9kbGMEvJlrr/3Hr9C/kxxYmJAgAUV9+7Na372M15FPuDhXr2+M2D5HyrdWmEL7GdnrUjjNrlcmceSk/m1d97h56dNCxiLBZ146CF+7Te/4ZdfeYXnDRhw14DlH6jas1ZuvhXpEVLyZ4xl5tGxvnbhQn5+6lThYwVCqAGqGDRIxLAQbjBbof+hUtZKD4Q2hxdI97GmOBCO7Y8BrBP0xt1ev57XLlggToWBcCKEjiYk8Lo5cwRcBfHxxmL5ASpHYBXExPBLc+fyM9J5DwRrBef95MiRvG72bH7+pz81FstPUDkCq5zewMa1a3k9+TOIvAcCWFBJ377CWp374Q95blSUcd79AFWnwfo8PDzzKB3r63/7W342JSVgLBYkcoUE+6UpU/iRPn0MWH6AypHFKhg4kNe/+Sa/OHMmL4qL61KwCjqgsuhoEW448/jjZiv0E1Sdt1gREVknxo/njR9+yC+RP3M4LMznYOmwqN+RZ5H1d0PHhg7lDXSgQLgBkXcDVvdD1XmwEG6IjBTO+9nnn+d5LpfPwNIhUb9D/W5rvEz/f4ifIcBPDB8uUjpnf/ADY7H8AZV8bqfBOjZ2rAg3COedtkWnoQbdOuXJ3wGIUDlx0KIcDeI8C7Sl/fvzy7/4Bb9Ap9X8mBhjsbobKqdglQ4bxq8sXMirnnvOkbWyAnVEg+kAKUvTflI26ZBmsdx+P1msylGjRNkMarJywsONxepuqJyCVTxkCL++YoVIRJckJnYYLDugDmkwZZDSSTV08vxDerr4OkuCpSyW1VqpcINw3lHoFxFhwOpuqJyAtcnlyjpOp64bK1fyatp2OmKxrD6UDtQ+0l5SGuk8AaXW1xpYB+UWqVsrHVj4WChNRk2W2Qr9AJUTsITz3q+fsFbnXnqJF0RFeQWWFagcDag9pFTSOQ2o/757txmsffK5OfL/4clvO3bvvfzS1KmiusE4736AyilYsFgo9KtbvJgXEGTtgaWHDA5Ly5Mlt7s0C1Cn6fM0OhD89fp18XXD++8L/0r3rTz5beX0c1dmzuQXAbzJFXY/VE7BQhpHlCbT6autE2Eecz/p6VZqt2XL+5eqKr6RHtuC7xEg3xYW8gP00ZstEFK5wvp58/gFOlQYi+UHqJyCVU6+DJop6n/9a+G824GVpwGlfKn90kpd0ID6L7nlNdKWt5m+t420U4IHALO92AKVUOgHa3X2mWf4kb59TUqnu6FyAtbGiIisY2PG8IalS/l5ehPttkEdqsMaVBc1oKro8y/IIv2H3PLyU1L4VnrODulvpcufOcjswwt2gJ0cMYJfI9gvTZvGcyMjDVjdDZUTsGCxUNyH4GgNnQoRINXBUp+raDnAqCBnWi1sf9vpMVinnfSz5bNmiY81ZAEvkvbS58paHWCt41Z2BwKl8thYYbFMoZ+foHIC1sbw8MyT48fzW+vW8Vrys3LvucdtK1RQ5bCWUMJlgqmeoEmT2xws0yZAQlbqf//2t2bo8Hk+QbhHA0v5V3q03c56QRWDB/N6ek2wWMZ59wNUTsCCxcpHXyFyheQk55LTnKu98SqnpyLo+5l7SAFbXQ7Bo4CqJuBgtfD1N+Sw75LP3adthTlewAXnHTXvOBWe+9GPjMXyB1ROwaoYNYrf/ugj3vDGG7xYK5vRk8WwVtmkTA0sQPM9nf6wTpEVo9/FPyPBgQdYO+Rz8FwVFFVW65AGV6t0jlQpAQ9rdX7ChB7fTOEXqJyCVU6WoX7JElHdkKtBpcN1UIKlUjRp4qW2hBUAFE6CWLfoI7bHI2TJ0snHwnP3SiB1uHJswGpV6IehIJMn88MuV4913v0GlROwNiJXmJjIry1bJrbDYlma7Mm/ypKQ/F1apQra9hRQ+Ho7gXRQbo1/oRPidmm1ABfiWP9GIB6l7ytfS69qsJ4KSyIjm4eC9NSt0K9QOQFrs8uVhS6dxtWr+cUZM1rlCnWwlH9VRGD8n+akI8Swnx7L0nwtLFgzRN33aJH3PxKEKp1zpA2oIMxuuLp4cVOucODAHrcV+h0qJ2DBYuWTZcCJ8BxZhwL6XIdKL31RaZtMAgVpm+NkrRBmyNaAOk/Oe93nnwvYEHb4iwTqKwJKxbKs6RxPAVJUkKJ6tOqpp3qcxQoIqJyCdTw5md/ZsEFE3otkw6p+ItQdd8CBuNQX0odSQFUSaJ/QY9BWLVB6h4DaJbdPlc7xBiqoLCaG1/7yl7wmJaVHlSYHDFROwSoZMoTXzp8vcoXWUINeqJcpHff9BM7fZeoGSWblvOtAKQf+IB0M9niAqi2w3IaC0KGip1isgILKKVjl5MvcXLVKVDeUDBrkdhpUYGVLOOBjFZOl0iPuOzSgsJQVgx+G52Yy9xyhDlZbgDUPBaGt8Ejv3iF/Kgw4qJyAtQldOo8+yq8tX86rp05tFWpQjns2c6+3wva2W/OhFFj/VFjIL5GfBfAAlZ7K0U+B7eUJRa4wKanHDAUJSKicgAWLheK+OtSWT5vGi8ivseYHVRxLBwtlMFi3actrIGcdK4u2PaR4UllL3EqvbffUOOEJLMxu6AlDQQIWKqdgVY4fz2//7ne8bsECnk/bjzVPaAULPtYVskqACGEEaCdriVcBvHTmHhC1wuUp2u4GVmIir587V1SRhmquMKChcgpWYXR0U67QptBPgZXNWhx4QHOYrBPWDbJWaRKo3RKqPdJildBWmGGxXHbNEx4L/UaM4HWvvRayQ0ECHiqnYFU8/DC/tXZtU66Qth8dLOvJEBYon6D6T7JSAGevBGm3hAsqkAFU1Lcr65XJmG1gtK2TIbp0MLfh3I9/zPP69w8pixUUUDkBa5MEC/VY51NSWpXM6LEsBVchgQUrpNq6dsttME+LyKN54jJtl8rfsp4MvemorqTXJQr9QmwoSNBA5RSsothYfvWtt/jlWbN4cXx8s3NtV+GgW64MCVWBBhQceXVSPEsnQ2tw1A4sT3BhanKoDQUJKqicgLU5IiILzvutNWv4ZfJn9FObtdJB3xJVPEsBdVqWzVwgK4UFi2UH1uEOgIWUztWFC0WuMBQi70EHlROwYLHyyZepJef93KRJzadCa627AisbINBWqICqkpH3/TK9A6mGimtkvTxZrHbDDWFh/PiwYbx2+vSQGAoSlFA5BevEuHH8yw0beAP5M8UyjqWDpYccAEiDDIAi8n5QyxcW01aKtA6WKKFhLY0UVovljfPePBTkueeCuhM6aKFyChZSOohhIdzgqTPH2kNYqtW3nyTI/hHPo8ew/lhY2JTqYU2nRLuqhvZav1SuUAwFod97KCwsKC1WUEPlFKyy++7jN959V8BVJvsK9Q4da9nMFelHwa9C8lmvw8Lne2mrRIfOSbJgaYy5tdR7W9nglivEBQJBWEEa9FA5AWuLy5V1EkNBVqzgNcjJaVuV2gJVrlBVj+IUaN0GjxNkJ0h6oR8i8nr3s7fxK6VgHgoSElA5AQsWqxC5QrJWF156iReRX6M77GobBFiIRSHgiVIYfRuEb6UWivwq5NfV9D29+9ku4t4WYBVDhogYVhWdWoPJeQ8ZqJyChXADnPd6OtqjglTfBnWwVAK6lrY5nAbpZ5tPgKhuAGynZXc0tssM5t7y1Vajqh1Y5XFxIqUTTENBQgoqp2CVxMfz2nnz+NkJE1r5V1bHXZXMbBd/BU1lMnoJMsIQh8ii6WkcHSxvy2aah4LMnSvav4LBYoUcVE7BqkhK4rc++IA3kD9TkpDQCqxDNmABIARB0SyxWzZLoA5eVTdkSLBU8tkOrvYslrhAYPJkMRQk0Gc3hCRUTsDaHB6eeWLMGJHSufDii7YWSwcLzruaLgOwkB/8grWUzKgKh3QJlze9hJ7gErlCXCAwdWpAgxWyUDkBCxYLQVFUNlyaPp0Xx8Z6BEvlB8+Qc36TnHS9ogFKlTqakiJ8LHy0dkHbxbLaGrwW6LnCkIbKCVjIFZ4iq3N77VpeO2cOzyPfxs7H0pPPemXDHgkVOp6/lI2rauFrvZpUWS1vC/0wKhLNFOICgQB03kMeKkdgIdyAGaTIFSJ1cs89bmDpeUIrXGp4rQIKDjxCDbfl12gPsw4F8WSx7HKFGAqC9i9c0hRoFqtHQOUUrBNjx/Iv16/nV2k7LKUjvp7O0dM6eqA0XW55CigMXUN1Q6X0vzAfCyfHMnqOPh+rI4V+GAoCa4VTYSAFSHsMVE7BqsBQkEWL+DlceaJZE2u+UHfiVVqniMDRqxuw9tDvuyWtFtI6sFhtFfrZ+lm4QAC5wsWLRRwrUFI6PQoqp2CVJibyG8uX87r580WuUA9iKrD06HuNtErY8tQsLKyzBNtNzc9CCOIUfV8fE2ltAWtrqp+4QEAOBQmEmyl6HFROwNrqcmVV0qmrcdUqfnnGDLc323rThBpm+5XFST9HQN2QjyECj+FrCrYjZL1UoFTv0vE08lsHC/VYgTIUpEdC5QQsWKyCPn2EtbpAR/ti8msUVHp1QzZrCZAiCY2m1AyCplEDCiOMkOa5KLfJCtom7Qau5WjSh69Zg6aoIMXgNX9fINBjoXIK1snk5KZCvyVLeHFUVKsiP2tKByGEyxIe5bgDqF30Ud1AkSPTOipAquBSymZtB0zhZ2EoyBWyotgO/VWa3KOhcgpW6aBBwmKdsyn00+/HUT2FCDFcR4CUQMIcd73V/rrsM7RG35UymXeReFXoh+G21ZMm+cVi9XionIJVQb7MrdWrhcWC857PWlusbNYSeVdB0QxtoNqXclyRtXEVOkZbIqYsNxJ0VeTMWwfderJYqtDv7NNPd3v7l4HKIVhbIiKyTo4bx68vW8arX365VajBGhxVo4wa5bwGNKXqaR2lfQTdn+XgW33dLSzkh+h7dhbLGnYQFwj4YSiIgcoHYMFiFQ8YIKzVJTp9lZBfY3fFiTXyfpUsELY6vRM61WLFUAGBrbGSrJSCDEDqeUO7djAVyxJDQSZP7tahIAYqH4G1JTw889T48fwOhoKQn1XgctmGGzzlDPdKqHSg8HE/Oe9oplDDQv6VwAJoypnPbmcbFGBhKMi8ed02FMRA5UOwYLFQjnwFucKJE3l+WFgrsOymzuhjI2u1JtU06dAjnQOwUFKDMhuUKeunRE/+lVuhX1ISv0LWDrMbutpiGai6AKzjdPq6s26dyBWWxce7WQ9r9B1A7GctTrxqXC2WlzIp6WDtkgDi2t5bcitsrzZLRd5hrcRQkC70sQxUXQXWyJH8KjnJF154oRkm/Q22hh1gdY7JBDS2PQUT7iZMJYuFz5XVwlZYLp8LCPVre9sDy+0CgV69uuRUaKDqQrBKyUpdX7qUX5k9m5fbWCyVhFa9hQdktzO2vm0SqENPPim+xsctEjQkqAEXgqmVMhmtXzSul9DY+VnohO7KQj8DVReChVwhnPfba9bwWnrz9TfWaq3UFnhThhpwj85V+TkWSmUQgVedOpg8A8AQQLW7U0c/FdqBJS4QWLSoS4aCGKi6GCxR6BcZyetef11shUW9e7eyVnplA06BNzSYsNRI7lMSKCSgcakASmcQkcesd/ha+p063tTAAyzMbkCA1JcWy0DVTWBVPvoo/2rDBuFnlUZHN7/Bdr6VqK2SoQX0Fm62AIUW+/PylIgFuHBSVDeBIaBaiutRbLZDK1iYj1X76qtizruvqhsMVN0I1tGhQ3n9ggVN4QbG3KobrGCpFns45VUWoDYTQP+uzXtX154AvjuyCgIXD2TTY3opTVu5wgZ6XdVkSX1hsQxU3QxWBYHVuHIlbyB/pjwxsdla6U67brEK5NW9AArzG7ZpQKEuCzMc8BHXn+g3g6FVTG2HbV0qoGJZbkNBHJ4KDVTdDJZw3pOT+c133uEXX3nFbRv01LCK2VholIAl+meZqkHlKEYZ/QMJH29oQB2UJ8WdchstkVuhXceOXvOOZgqRKyTn3Ukcy0DlB7BErrBfPzG3oWbKFF5Cfo3uuNv5WPCVSmRsClfHfUZf/94CFNYJeXETmixSpV+GWFYZgeXNWKOKQYOcXyBgoPIfWKfpjYPz3kBwFfXp4xZxt9ZjKR8LC3ErXDD+KT12U7NQShn0PP3iJkBlZ608Na0ejY/ndbNnC7g6lSs0UPkXrFLcNIHhG88+6+ZAWytIVX4Qub8/y0sub2lAAaR00v/Q55Dyu/C9XHpctYFlMy+6deRQkLo5czo3FMRA5X+wjicl8Tsffij8mbKEhFYWSy/0syaeAc0BggZb4cewRHKLVN8Tzj1rmUVq163jMVdI1hPWCg2ruVFR3jvvBir/g7U1PDzz5KhR/Nqbb/Jq1JYzZguWSueou3RgsZQDDx/LGmr4E30fzRU5MjShuqGtgdG2Ujr6UJAjfft6B5aBKjDA2oL5WDEx/Bptb7UzZvCyuDiPYFkdeHGRuOZDwUIdw0A2+Ri+Vm32HbkNTPlYZdHRHcsVGqgCB6ytERFZp1Hot3at8GcKwsJalcvYxbIytAYKAJRJAG3UrBbASpUR91TWemZWe/XurYaCuOcKkyRYg5rBMlAFFliwWEXIFZLzfmHSJF7kctmCpYcc0IT6V2mR4EMhpPAnLZ6FLRC5wzMy3qVfN4db7C/T49nMvt7dbSjI8OFiKAgGr1ksljtYBqrABKty7Fj+1fr1Tc57bKytn6WDlUXgIIqOyoVq6cQDLAAGVWqD2VDZAB9LdU/j1rDD9Jhy4NsaDlIaFSWCoxdwWo2JufsEYxPpNY8mPUQaKv48BqrABevYgw+KGBZugNBPZXYt9hnS+gAW1ZyKlA6AOqnlDnFShNVSI41EHbzMESprpYcb7MYYNV8gQH4WZjdIsEbJ13+vgSrAwSofPJjffPttXj9/vmhgsIKlF/mphtXv5dYHi3VeCz3A10IEvlGbmQWrZb1IILcNS6WEcIPIFZIPCLDGMzaBXvPDpGEGqgAHaxtyhRgKsnKlqH0qZO4WS6/HUmAh8v691jOogEIcS0/pwIE/NmtWcwyrI1CJXCFZUnWBQEFMzHdPMfYThhOhgSrwwRLhBuQK6fQF61BCfo1d5F2BhbDBLmmxVEHfJxpQOBWe16bNHJQzHKxQtQWUfipEDAvVDfSz3zzD2DMGqiACC0nerz/+WPgzmKLnCSyVK0SoQQVHGzWgkBf8VHPoj8ppM52BCsJw2yszZwrgi+LivjVQBRlYqMHC8A3k5NRWaA2Q6klobG2XtCrRPAII0XcU9SlLdVhe75vN3E9/3kIlCv1wpTBKpulQYaAKQrCODx/ObxMo8GdUoZ+3YOFmihpt66uVrfd2FQzeQqUX+mFMpIEqCMFC5P3U2LGi/UuUp7RjsdSp0DrwAw0WKh+YzbwIJ3ghDAUxUAUpWLBYpQMG8Kvy9FVG21lbzrvqgEbLfB1ZpyPagDVrDbs3J7+2ZKAKAHXaYoWHZ57B7V/r1vGG+fN5sUzpWMHSh4FkstZD1BRQnibHGKiCVJ0GC+GGqChxyxacZDjNOljW2ymyWcuYRz2R7O09zwaqIJMTsFCP9dVHH/Hrb7whWux1sJTVUtNmlPRaKm/uHjRQBamcgHUiKUmMvUZjqF2u0JN8CZSBKkDlBKzyhAR+c9kyUY9VLkuTvZVTmAxUAa7OgrXd5cqC83579WoR5fYVKAaqEJETi1USGSki3DUpKWLYmYHKyCdgnU5O5l9v2NBU6BcdbaAy8g1YorYcV55MnGigMvIdWMfvv5/feu89Ud2AQj8DlZFjsOC8YytsfPttUftkoDLyCViwWGhagLW6NGVKl/pYBqoglBOwUFOOLh308KFUxUBl5BisbQQWrm9DrlAV+hmojHwCFio1v1y7VuQKMTrIQGXkGKzt4eECLECFAKmBysgnYMFilcfG8htvvSXurfGVxTJQhYg6C9YOlysLzvudDz7gda+9ZqAy8g1YsFjID+L6NpTNFPfqZaAy8g1Yp8eNa8kV0gnRQGXkE7BwtQgG9aM02UBl5DOwKoYM4Y0rVgi4OpMrNFCFsDoLFpz3M489xm+9+65o/zJQGfkELFgszGuAtcKFk7gj0EBl5BOwxFAQ3P61aBFHNamBysgnYCEoinADRjIaqIx8BhYuQ7qzZo0INxxNSDBQGTkHazuGgowZw28sXSrmUBmojHwCliibiY4W1qp2+nQx7MxAZeQYLFisKjkUBH5WUUSEgcrIMVjJ2xnbJ/oK587l1bhAwAKWgaqHqxNgPUIaR2BlnBo9WpQmi6EgcXEGKqNOg/UgaSRp9E7G9uJUKIaCkMUyUBl1Fqx7SQ+wpmtDRqYylob84M3ly0WbPcINBiqjjoKVwJquYhsq4Xow3eVKReT99qpV4kIkA5VRR8GKIcVJqzVYWq6h6YztgPOOFnsDlVFHwYpiTZdFDpRwJUjAErMY24rqBsdQfRKk+tSizzR9btFGTZs0bda0RWqrpm1S2zXtkNop9YXULk2pUmlSu6X2SO2VSpfKkNonlSmVJbVf6oBUttRBqUNSOVKHm97atsDqx1rgguUaqJSNvxqnUJkV0ssOrF6kPqwJrkjWBBfUXyrKQGVWe8sOLBdrgguWSwcsUnxuoDLLi2UFywqXAqxJBiqzvFxhzDNc7jJQmdWBFcZaw6UU0SwDlVmdWFa43GWgMssHy0BlVhcvA5VZPl8GKrN8vvydZzIKPfn9BRiFnvz+AoxCT35/AUahJ7+/AKPQ0/8DgPjYCIvPEYAAAAAASUVORK5CYII=\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setBody(BodyWithContentTypeDTO.createDTO(BinaryBody.binary(IOUtils.toByteArray(openStreamToFileFromClassPathOrPath("org/mockserver/serialization/forkme_right_red.png"))))).buildObject(), httpRequest);
    }

    @Test
    public void shouldDeserializePartialObject() {
        // given
        String requestBytes = ((("{" + (NEW_LINE)) + "    \"path\": \"somePath\"") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setPath(NottableString.string("somePath")).buildObject(), httpRequest);
    }

    @Test
    public void shouldDeserializeAsHttpRequestField() {
        // given
        String requestBytes = ((((((((((((((("{" + (NEW_LINE)) + "    \"httpRequest\": {") + (NEW_LINE)) + "        \"path\": \"somePath\",") + (NEW_LINE)) + "        \"queryStringParameters\" : [ {") + (NEW_LINE)) + "            \"name\" : \"queryParameterName\",") + (NEW_LINE)) + "            \"values\" : [ \"queryParameterValue\" ]") + (NEW_LINE)) + "        } ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "}";
        // when
        HttpRequest httpRequest = new HttpRequestSerializer(new MockServerLogger()).deserialize(requestBytes);
        // then
        Assert.assertEquals(new HttpRequestDTO().setPath(NottableString.string("somePath")).setQueryStringParameters(new Parameters().withEntries(Parameter.param("queryParameterName", "queryParameterValue"))).buildObject(), httpRequest);
    }

    @Test
    public void shouldSerializeCompleteObject() {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setMethod(NottableString.string("someMethod")).setPath(NottableString.string("somePath")).setQueryStringParameters(new Parameters().withEntries(Parameter.param("queryParameterName", "queryParameterValue"))).setBody(new org.mockserver.serialization.model.StringBodyDTO(StringBody.exact("someBody"))).setHeaders(new Headers().withEntries(Header.header("headerName", "headerValue"))).setCookies(new Cookies().withEntries(Cookie.cookie("cookieName", "cookieValue"))).buildObject());
        // then
        Assert.assertEquals((((((((((((((((((((((((((("{" + (NEW_LINE)) + "  \"method\" : \"someMethod\",") + (NEW_LINE)) + "  \"path\" : \"somePath\",") + (NEW_LINE)) + "  \"queryStringParameters\" : {") + (NEW_LINE)) + "    \"queryParameterName\" : [ \"queryParameterValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"headerName\" : [ \"headerValue\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"cookies\" : {") + (NEW_LINE)) + "    \"cookieName\" : \"cookieValue\"") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : \"someBody\"") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializeArray() {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequest[]{ new HttpRequestDTO().setMethod(NottableString.string("some_method_one")).setPath(NottableString.string("some_path_one")).setBody(BodyDTO.createDTO(new StringBody("some_body_one"))).setHeaders(new Headers().withEntries(Header.header("some_header_name_one", "some_header_value_one"))).buildObject(), new HttpRequestDTO().setMethod(NottableString.string("some_method_two")).setPath(NottableString.string("some_path_two")).setBody(BodyDTO.createDTO(new StringBody("some_body_two"))).setHeaders(new Headers().withEntries(Header.header("some_header_name_two", "some_header_value_two"))).buildObject() });
        // then
        Assert.assertEquals((((((((((((((((((((((((((((("[ {" + (NEW_LINE)) + "  \"method\" : \"some_method_one\",") + (NEW_LINE)) + "  \"path\" : \"some_path_one\",") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"some_header_name_one\" : [ \"some_header_value_one\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : \"some_body_one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"method\" : \"some_method_two\",") + (NEW_LINE)) + "  \"path\" : \"some_path_two\",") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"some_header_name_two\" : [ \"some_header_value_two\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : \"some_body_two\"") + (NEW_LINE)) + "} ]"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializeList() {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(Arrays.asList(new HttpRequestDTO().setMethod(NottableString.string("some_method_one")).setPath(NottableString.string("some_path_one")).setBody(BodyDTO.createDTO(new StringBody("some_body_one"))).setHeaders(new Headers().withEntries(Header.header("some_header_name_one", "some_header_value_one"))).buildObject(), new HttpRequestDTO().setMethod(NottableString.string("some_method_two")).setPath(NottableString.string("some_path_two")).setBody(BodyDTO.createDTO(new StringBody("some_body_two"))).setHeaders(new Headers().withEntries(Header.header("some_header_name_two", "some_header_value_two"))).buildObject()));
        // then
        Assert.assertEquals((((((((((((((((((((((((((((("[ {" + (NEW_LINE)) + "  \"method\" : \"some_method_one\",") + (NEW_LINE)) + "  \"path\" : \"some_path_one\",") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"some_header_name_one\" : [ \"some_header_value_one\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : \"some_body_one\"") + (NEW_LINE)) + "}, {") + (NEW_LINE)) + "  \"method\" : \"some_method_two\",") + (NEW_LINE)) + "  \"path\" : \"some_path_two\",") + (NEW_LINE)) + "  \"headers\" : {") + (NEW_LINE)) + "    \"some_header_name_two\" : [ \"some_header_value_two\" ]") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"body\" : \"some_body_two\"") + (NEW_LINE)) + "} ]"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializeStringBody() {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setBody(BodyDTO.createDTO(StringBody.exact("somebody"))).buildObject());
        // then
        Assert.assertEquals((((("{" + (NEW_LINE)) + "  \"body\" : \"somebody\"") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializeRegexBody() {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setBody(BodyDTO.createDTO(RegexBody.regex("some[a-z]{3}"))).buildObject());
        // then
        Assert.assertEquals((((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"REGEX\",") + (NEW_LINE)) + "    \"regex\" : \"some[a-z]{3}\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializeJsonBody() {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setBody(BodyDTO.createDTO(JsonBody.json("{ \"key\": \"value\" }"))).buildObject());
        // then
        Assert.assertEquals((((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"JSON\",") + (NEW_LINE)) + "    \"json\" : \"{ \\\"key\\\": \\\"value\\\" }\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializeJsonSchemaBody() {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setBody(BodyDTO.createDTO(JsonSchemaBody.jsonSchema("{ \"key\": \"value\" }"))).buildObject());
        // then
        Assert.assertEquals((((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"JSON_SCHEMA\",") + (NEW_LINE)) + "    \"jsonSchema\" : \"{ \\\"key\\\": \\\"value\\\" }\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializeJsonPathBody() {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setBody(BodyDTO.createDTO(JsonPathBody.jsonPath("$..book[?(@.price <= $['expensive'])]"))).buildObject());
        // then
        Assert.assertEquals((((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"JSON_PATH\",") + (NEW_LINE)) + "    \"jsonPath\" : \"$..book[?(@.price <= $[\'expensive\'])]\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializeXmlBody() {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setBody(BodyDTO.createDTO(XmlBody.xml("<some><xml></xml></some>"))).buildObject());
        // then
        Assert.assertEquals((((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"XML\",") + (NEW_LINE)) + "    \"xml\" : \"<some><xml></xml></some>\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializeXmlSchemaBody() {
        // given
        String xmlSchema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ((((((((((((((((((("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">" + "    <!-- XML Schema Generated from XML Document on Wed Jun 28 2017 21:52:45 GMT+0100 (BST) -->") + "    <!-- with XmlGrid.net Free Online Service http://xmlgrid.net -->") + "    <xs:element name=\"notes\">") + "        <xs:complexType>") + "            <xs:sequence>") + "                <xs:element name=\"note\" maxOccurs=\"unbounded\">") + "                    <xs:complexType>") + "                        <xs:sequence>") + "                            <xs:element name=\"to\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + "                            <xs:element name=\"from\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + "                            <xs:element name=\"heading\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + "                            <xs:element name=\"body\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + "                        </xs:sequence>") + "                    </xs:complexType>") + "                </xs:element>") + "            </xs:sequence>") + "        </xs:complexType>") + "    </xs:element>") + "</xs:schema>");
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setBody(BodyDTO.createDTO(XmlSchemaBody.xmlSchema(xmlSchema))).buildObject());
        // then
        Assert.assertEquals((((((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"XML_SCHEMA\",") + (NEW_LINE)) + "    \"xmlSchema\" : \"") + (StringEscapeUtils.escapeJava(xmlSchema))) + "\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializeXpathBody() {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setBody(BodyDTO.createDTO(XPathBody.xpath("/element[key = 'some_key' and value = 'some_value']"))).buildObject());
        // then
        Assert.assertEquals((((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"XPATH\",") + (NEW_LINE)) + "    \"xpath\" : \"/element[key = \'some_key\' and value = \'some_value\']\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializeParameterBody() {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setBody(BodyDTO.createDTO(ParameterBody.params(new Parameter("nameOne", "valueOne"), new Parameter("nameTwo", "valueTwo_One", "valueTwo_Two")))).buildObject());
        // then
        Assert.assertEquals((((((((((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"PARAMETERS\",") + (NEW_LINE)) + "    \"parameters\" : {") + (NEW_LINE)) + "      \"nameOne\" : [ \"valueOne\" ],") + (NEW_LINE)) + "      \"nameTwo\" : [ \"valueTwo_One\", \"valueTwo_Two\" ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializeBinaryBody() throws IOException {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setBody(BodyWithContentTypeDTO.createDTO(BinaryBody.binary(IOUtils.toByteArray(openStreamToFileFromClassPathOrPath("org/mockserver/serialization/forkme_right_red.png"))))).buildObject());
        // then
        Assert.assertEquals((((((((((("{" + (NEW_LINE)) + "  \"body\" : {") + (NEW_LINE)) + "    \"type\" : \"BINARY\",") + (NEW_LINE)) + "    \"base64Bytes\" : \"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\"") + (NEW_LINE)) + "  }") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }

    @Test
    public void shouldSerializePartialHttpRequest() {
        // when
        String jsonHttpRequest = new HttpRequestSerializer(new MockServerLogger()).serialize(new HttpRequestDTO().setPath(NottableString.string("somePath")).buildObject());
        // then
        Assert.assertEquals((((("{" + (NEW_LINE)) + "  \"path\" : \"somePath\"") + (NEW_LINE)) + "}"), jsonHttpRequest);
    }
}

