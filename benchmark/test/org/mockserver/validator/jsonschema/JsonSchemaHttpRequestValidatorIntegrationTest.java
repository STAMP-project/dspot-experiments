package org.mockserver.validator.jsonschema;


import java.nio.charset.StandardCharsets;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.JsonBody;
import org.mockserver.model.JsonSchemaBody;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;
import org.mockserver.model.RegexBody;
import org.mockserver.model.StringBody;
import org.mockserver.model.XPathBody;
import org.mockserver.model.XmlBody;
import org.mockserver.model.XmlSchemaBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class JsonSchemaHttpRequestValidatorIntegrationTest {
    private JsonSchemaValidator jsonSchemaValidator = new JsonSchemaHttpRequestValidator(new MockServerLogger());

    @Test
    public void shouldValidateValidCompleteRequestFromRawJson() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid((((((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "    \"method\" : \"someMethod\",") + (NEW_LINE)) + "    \"path\" : \"somePath\",") + (NEW_LINE)) + "    \"queryStringParameters\" : [ {") + (NEW_LINE)) + "      \"name\" : \"queryStringParameterNameOne\",") + (NEW_LINE)) + "      \"values\" : [ \"queryStringParameterValueOne_One\", \"queryStringParameterValueOne_Two\" ]") + (NEW_LINE)) + "    }, {") + (NEW_LINE)) + "      \"name\" : \"queryStringParameterNameTwo\",") + (NEW_LINE)) + "      \"values\" : [ \"queryStringParameterValueTwo_One\" ]") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    \"body\" : {") + (NEW_LINE)) + "      \"type\" : \"STRING\",") + (NEW_LINE)) + "      \"string\" : \"someBody\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"cookies\" : [ {") + (NEW_LINE)) + "      \"name\" : \"someCookieName\",") + (NEW_LINE)) + "      \"value\" : \"someCookieValue\"") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    \"headers\" : [ {") + (NEW_LINE)) + "      \"name\" : \"someHeaderName\",") + (NEW_LINE)) + "      \"values\" : [ \"someHeaderValue\" ]") + (NEW_LINE)) + "    } ]") + (NEW_LINE)) + "  }")), Is.is(""));
    }

    @Test
    public void shouldValidateValidCompleteRequestWithBinaryBody() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid(new org.mockserver.serialization.HttpRequestSerializer(new MockServerLogger()).serialize(HttpRequest.request().withBody(BinaryBody.binary("somebody".getBytes(StandardCharsets.UTF_8))))), Is.is(""));
    }

    @Test
    public void shouldValidateValidCompleteRequestWithJsonBody() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid(new org.mockserver.serialization.HttpRequestSerializer(new MockServerLogger()).serialize(HttpRequest.request().withBody(JsonBody.json((((((((((("{" + (NEW_LINE)) + "    \"id\": 1,") + (NEW_LINE)) + "    \"name\": \"A green door\",") + (NEW_LINE)) + "    \"price\": 12.50,") + (NEW_LINE)) + "    \"tags\": [\"home\", \"green\"]") + (NEW_LINE)) + "}"))))), Is.is(""));
    }

    @Test
    public void shouldValidateValidCompleteRequestWithJsonSchemaBody() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid(new org.mockserver.serialization.HttpRequestSerializer(new MockServerLogger()).serialize(HttpRequest.request().withBody(JsonSchemaBody.jsonSchema((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "    \"$schema\": \"http://json-schema.org/draft-04/schema#\",") + (NEW_LINE)) + "    \"title\": \"Product\",") + (NEW_LINE)) + "    \"description\": \"A product from Acme\'s catalog\",") + (NEW_LINE)) + "    \"type\": \"object\",") + (NEW_LINE)) + "    \"properties\": {") + (NEW_LINE)) + "        \"id\": {") + (NEW_LINE)) + "            \"description\": \"The unique identifier for a product\",") + (NEW_LINE)) + "            \"type\": \"integer\"") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"name\": {") + (NEW_LINE)) + "            \"description\": \"Name of the product\",") + (NEW_LINE)) + "            \"type\": \"string\"") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"price\": {") + (NEW_LINE)) + "            \"type\": \"number\",") + (NEW_LINE)) + "            \"minimum\": 0,") + (NEW_LINE)) + "            \"exclusiveMinimum\": true") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        \"tags\": {") + (NEW_LINE)) + "            \"type\": \"array\",") + (NEW_LINE)) + "            \"items\": {") + (NEW_LINE)) + "                \"type\": \"string\"") + (NEW_LINE)) + "            },") + (NEW_LINE)) + "            \"minItems\": 1,") + (NEW_LINE)) + "            \"uniqueItems\": true") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"required\": [\"id\", \"name\", \"price\"]") + (NEW_LINE)) + "}"))))), Is.is(""));
    }

    @Test
    public void shouldValidateValidCompleteRequestWithParameterBody() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid(new org.mockserver.serialization.HttpRequestSerializer(new MockServerLogger()).serialize(HttpRequest.request().withBody(ParameterBody.params(Parameter.param("paramOne", "paramOneValueOne"), Parameter.param("paramOne", "paramOneValueTwo"), Parameter.param("parameterRegexName.*", "parameterRegexValue.*"))))), Is.is(""));
    }

    @Test
    public void shouldValidateValidCompleteRequestWithRegexBody() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid(new org.mockserver.serialization.HttpRequestSerializer(new MockServerLogger()).serialize(HttpRequest.request().withBody(RegexBody.regex("[a-z]{1,3}")))), Is.is(""));
    }

    @Test
    public void shouldValidateValidCompleteRequestWithStringBody() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid(new org.mockserver.serialization.HttpRequestSerializer(new MockServerLogger()).serialize(HttpRequest.request().withBody(StringBody.exact("string_body")))), Is.is(""));
    }

    @Test
    public void shouldValidateValidCompleteRequestWithXPathBody() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid(new org.mockserver.serialization.HttpRequestSerializer(new MockServerLogger()).serialize(HttpRequest.request().withBody(XPathBody.xpath("/bookstore/book[year=2005]/price")))), Is.is(""));
    }

    @Test
    public void shouldValidateValidCompleteRequestWithXMLBody() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid(new org.mockserver.serialization.HttpRequestSerializer(new MockServerLogger()).serialize(HttpRequest.request().withBody(XmlBody.xml(((((((((((((((((("" + "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>") + (NEW_LINE)) + "<bookstore>") + (NEW_LINE)) + "  <book category=\"COOKING\" nationality=\"ITALIAN\">") + (NEW_LINE)) + "    <title lang=\"en\">Everyday Italian</title>") + (NEW_LINE)) + "    <author>Giada De Laurentiis</author>") + (NEW_LINE)) + "    <year>2005</year>") + (NEW_LINE)) + "    <price>30.00</price>") + (NEW_LINE)) + "  </book>") + (NEW_LINE)) + "</bookstore>"))))), Is.is(""));
    }

    @Test
    public void shouldValidateValidCompleteRequestWithXMLSchemaBody() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid(new org.mockserver.serialization.HttpRequestSerializer(new MockServerLogger()).serialize(HttpRequest.request().withBody(XmlSchemaBody.xmlSchema((((((((((((((((((((((((((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (NEW_LINE)) + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">") + (NEW_LINE)) + "    <!-- XML Schema Generated from XML Document on Wed Jun 28 2017 21:52:45 GMT+0100 (BST) -->") + (NEW_LINE)) + "    <!-- with XmlGrid.net Free Online Service http://xmlgrid.net -->") + (NEW_LINE)) + "    <xs:element name=\"notes\">") + (NEW_LINE)) + "        <xs:complexType>") + (NEW_LINE)) + "            <xs:sequence>") + (NEW_LINE)) + "                <xs:element name=\"note\" maxOccurs=\"unbounded\">") + (NEW_LINE)) + "                    <xs:complexType>") + (NEW_LINE)) + "                        <xs:sequence>") + (NEW_LINE)) + "                            <xs:element name=\"to\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"from\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"heading\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                            <xs:element name=\"body\" minOccurs=\"1\" maxOccurs=\"1\" type=\"xs:string\"></xs:element>") + (NEW_LINE)) + "                        </xs:sequence>") + (NEW_LINE)) + "                    </xs:complexType>") + (NEW_LINE)) + "                </xs:element>") + (NEW_LINE)) + "            </xs:sequence>") + (NEW_LINE)) + "        </xs:complexType>") + (NEW_LINE)) + "    </xs:element>") + (NEW_LINE)) + "</xs:schema>"))))), Is.is(""));
    }

    @Test
    public void shouldValidateValidCompleteRequestWithExpandedHeaderParametersAndCookies() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid((((((((((((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "    \"method\" : \"someMethod\",") + (NEW_LINE)) + "    \"path\" : \"somePath\",") + (NEW_LINE)) + "    \"queryStringParameters\" : [ {") + (NEW_LINE)) + "      \"name\" : \"queryStringParameterNameOne\",") + (NEW_LINE)) + "      \"values\" : [ \"queryStringParameterValueOne_One\", \"queryStringParameterValueOne_Two\" ]") + (NEW_LINE)) + "    }, {") + (NEW_LINE)) + "      \"name\" : \"queryStringParameterNameTwo\",") + (NEW_LINE)) + "      \"values\" : [ \"queryStringParameterValueTwo_One\" ]") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    \"body\" : {") + (NEW_LINE)) + "      \"type\" : \"STRING\",") + (NEW_LINE)) + "      \"string\" : \"someBody\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"cookies\" : [ {") + (NEW_LINE)) + "      \"name\" : \"someCookieName\",") + (NEW_LINE)) + "      \"value\" : \"someCookieValue\"") + (NEW_LINE)) + "    } ],") + (NEW_LINE)) + "    \"headers\" : [ {") + (NEW_LINE)) + "      \"name\" : \"someHeaderName\",") + (NEW_LINE)) + "      \"values\" : [ \"someHeaderValue\" ]") + (NEW_LINE)) + "    } ]") + (NEW_LINE)) + "  }")), Is.is(""));
    }

    @Test
    public void shouldValidateValidCompleteRequestWithCompactHeaderParametersAndCookies() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid((((((((((((((((((((((((((((((((((("{" + (NEW_LINE)) + "    \"method\" : \"someMethod\",") + (NEW_LINE)) + "    \"path\" : \"somePath\",") + (NEW_LINE)) + "    \"queryStringParameters\" : {") + (NEW_LINE)) + "      \"queryStringParameterNameOne\" : [ \"queryStringParameterValueOne_One\", \"queryStringParameterValueOne_Two\" ],") + (NEW_LINE)) + "      \"queryStringParameterNameTwo\" : [ \"queryStringParameterValueTwo_One\" ]") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"body\" : {") + (NEW_LINE)) + "      \"type\" : \"STRING\",") + (NEW_LINE)) + "      \"string\" : \"someBody\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"cookies\" : {") + (NEW_LINE)) + "      \"someCookieName\" : \"someCookieValue\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    \"headers\" : {") + (NEW_LINE)) + "      \"someHeaderName\" : [ \"someHeaderValue\" ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "  }")), Is.is(""));
    }

    @Test
    public void shouldValidateInvalidBodyFields() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid((((((((((("{" + (NEW_LINE)) + "    \"body\" : {") + (NEW_LINE)) + "      \"type\" : \"STRING\",") + (NEW_LINE)) + "      \"value\" : \"someBody\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "  }")), Is.is((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("1 error:" + (NEW_LINE)) + " - for field \"/body\" a plain string or one of the following example bodies must be specified ") + (NEW_LINE)) + "   {") + (NEW_LINE)) + "     \"not\": false,") + (NEW_LINE)) + "     \"type\": \"BINARY\",") + (NEW_LINE)) + "     \"base64Bytes\": \"\",") + (NEW_LINE)) + "     \"contentType\": \"\"") + (NEW_LINE)) + "   }, ") + (NEW_LINE)) + "   {") + (NEW_LINE)) + "     \"not\": false,") + (NEW_LINE)) + "     \"type\": \"JSON\",") + (NEW_LINE)) + "     \"json\": \"\",") + (NEW_LINE)) + "     \"contentType\": \"\",") + (NEW_LINE)) + "     \"matchType\": \"ONLY_MATCHING_FIELDS\"") + (NEW_LINE)) + "   },") + (NEW_LINE)) + "   {") + (NEW_LINE)) + "     \"not\": false,") + (NEW_LINE)) + "     \"type\": \"JSON_SCHEMA\",") + (NEW_LINE)) + "     \"jsonSchema\": \"\"") + (NEW_LINE)) + "   },") + (NEW_LINE)) + "   {") + (NEW_LINE)) + "     \"not\": false,") + (NEW_LINE)) + "     \"type\": \"JSON_PATH\",") + (NEW_LINE)) + "     \"jsonPath\": \"\"") + (NEW_LINE)) + "   },") + (NEW_LINE)) + "   {") + (NEW_LINE)) + "     \"not\": false,") + (NEW_LINE)) + "     \"type\": \"PARAMETERS\",") + (NEW_LINE)) + "     \"parameters\": {\"name\": \"value\"}") + (NEW_LINE)) + "   },") + (NEW_LINE)) + "   {") + (NEW_LINE)) + "     \"not\": false,") + (NEW_LINE)) + "     \"type\": \"REGEX\",") + (NEW_LINE)) + "     \"regex\": \"\"") + (NEW_LINE)) + "   },") + (NEW_LINE)) + "   {") + (NEW_LINE)) + "     \"not\": false,") + (NEW_LINE)) + "     \"type\": \"STRING\",") + (NEW_LINE)) + "     \"string\": \"\"") + (NEW_LINE)) + "   },") + (NEW_LINE)) + "   {") + (NEW_LINE)) + "     \"not\": false,") + (NEW_LINE)) + "     \"type\": \"XML\",") + (NEW_LINE)) + "     \"xml\": \"\",") + (NEW_LINE)) + "     \"contentType\": \"\"") + (NEW_LINE)) + "   },") + (NEW_LINE)) + "   {") + (NEW_LINE)) + "     \"not\": false,") + (NEW_LINE)) + "     \"type\": \"XML_SCHEMA\",") + (NEW_LINE)) + "     \"xmlSchema\": \"\"") + (NEW_LINE)) + "   },") + (NEW_LINE)) + "   {") + (NEW_LINE)) + "     \"not\": false,") + (NEW_LINE)) + "     \"type\": \"XPATH\",") + (NEW_LINE)) + "     \"xpath\": \"\"") + (NEW_LINE)) + "   }")));
    }

    @Test
    public void shouldValidateInvalidExtraField() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid((((((((((("{" + (NEW_LINE)) + "    \"invalidField\" : {") + (NEW_LINE)) + "      \"type\" : \"STRING\",") + (NEW_LINE)) + "      \"value\" : \"someBody\"") + (NEW_LINE)) + "    }") + (NEW_LINE)) + "  }")), Is.is((("1 error:" + (NEW_LINE)) + " - object instance has properties which are not allowed by the schema: [\"invalidField\"]")));
    }

    @Test
    public void shouldValidateMultipleInvalidFieldTypes() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid((((((("{" + (NEW_LINE)) + "    \"method\" : 100,") + (NEW_LINE)) + "    \"path\" : false") + (NEW_LINE)) + "  }")), Is.is((((("2 errors:" + (NEW_LINE)) + " - instance type (integer) does not match any allowed primitive type (allowed: [\"string\"]) for field \"/method\"") + (NEW_LINE)) + " - instance type (boolean) does not match any allowed primitive type (allowed: [\"string\"]) for field \"/path\"")));
    }

    @Test
    public void shouldValidateInvalidListItemType() {
        // when
        Assert.assertThat(jsonSchemaValidator.isValid((((("{" + (NEW_LINE)) + "    \"headers\" : [ \"invalidValueOne\", \"invalidValueTwo\" ]") + (NEW_LINE)) + "  }")), Is.is(((((((((((((((((((((((((((((((((((("1 error:" + (NEW_LINE)) + " - for field \"/headers\" only one of the following example formats is allowed: ") + (NEW_LINE)) + (NEW_LINE)) + "    \"/headers\" : {") + (NEW_LINE)) + "        \"exampleHeaderName\" : [ \"exampleHeaderValue\" ]") + (NEW_LINE)) + "        \"exampleMultiValuedHeaderName\" : [ \"exampleHeaderValueOne\", \"exampleHeaderValueTwo\" ]") + (NEW_LINE)) + "    }") + (NEW_LINE)) + (NEW_LINE)) + "   or:") + (NEW_LINE)) + (NEW_LINE)) + "    \"/headers\" : [") + (NEW_LINE)) + "        {") + (NEW_LINE)) + "            \"name\" : \"exampleHeaderName\",") + (NEW_LINE)) + "            \"values\" : [ \"exampleHeaderValue\" ]") + (NEW_LINE)) + "        },") + (NEW_LINE)) + "        {") + (NEW_LINE)) + "            \"name\" : \"exampleMultiValuedHeaderName\",") + (NEW_LINE)) + "            \"values\" : [ \"exampleHeaderValueOne\", \"exampleHeaderValueTwo\" ]") + (NEW_LINE)) + "        }") + (NEW_LINE)) + "    ]")));
    }
}

