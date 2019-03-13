/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.xmlsecurity;


import XmlSignatureConstants.HEADER_CONTENT_REFERENCE_URI;
import XmlSignatureConstants.HEADER_MESSAGE_IS_PLAIN_TEXT;
import XmlSignatureConstants.HEADER_OMIT_XML_DECLARATION;
import XmlSignatureConstants.HEADER_PLAIN_TEXT_ENCODING;
import XmlSignatureConstants.HEADER_TRANSFORM_METHODS;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import javax.xml.xpath.XPathExpressionException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.xmlsecurity.api.XmlSignatureException;
import org.apache.camel.component.xmlsecurity.api.XmlSignatureFormatException;
import org.apache.camel.component.xmlsecurity.api.XmlSignatureHelper;
import org.apache.camel.component.xmlsecurity.api.XmlSignatureInvalidContentHashException;
import org.apache.camel.component.xmlsecurity.api.XmlSignatureInvalidException;
import org.apache.camel.component.xmlsecurity.api.XmlSignatureInvalidKeyException;
import org.apache.camel.component.xmlsecurity.api.XmlSignatureInvalidValueException;
import org.apache.camel.support.processor.validation.SchemaValidationException;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;


public class XmlSignatureTest extends CamelTestSupport {
    protected static String payload;

    private static boolean includeNewLine = true;

    private KeyPair keyPair;

    static {
        if ((TestSupport.getJavaMajorVersion()) >= 9) {
            XmlSignatureTest.includeNewLine = false;
        }
        XmlSignatureTest.payload = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (XmlSignatureTest.includeNewLine ? "\n" : "")) + "<root xmlns=\"http://test/test\"><test>Test Message</test></root>";
    }

    @Test
    public void testEnvelopingSignature() throws Exception {
        setupMock();
        sendBody("direct:enveloping", XmlSignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testEnvelopedSignatureWithTransformHeader() throws Exception {
        setupMock(XmlSignatureTest.payload);
        sendBody("direct:enveloped", XmlSignatureTest.payload, Collections.<String, Object>singletonMap(HEADER_TRANSFORM_METHODS, "http://www.w3.org/2000/09/xmldsig#enveloped-signature,http://www.w3.org/TR/2001/REC-xml-c14n-20010315"));
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testEnvelopingSignatureWithPlainText() throws Exception {
        String text = "plain test text";
        setupMock(text);
        sendBody("direct:plaintext", text);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testEnvelopingSignatureWithPlainTextSetByHeaders() throws Exception {
        String text = "plain test text";
        setupMock(text);
        Map<String, Object> headers = new TreeMap<>();
        headers.put(HEADER_MESSAGE_IS_PLAIN_TEXT, Boolean.TRUE);
        headers.put(HEADER_PLAIN_TEXT_ENCODING, "UTF-8");
        sendBody("direct:enveloping", text, headers);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExceptionSignatureForPlainTextWithWrongEncoding() throws Exception {
        String text = "plain test text";
        MockEndpoint mock = setupExceptionMock();
        Map<String, Object> headers = new TreeMap<>();
        headers.put(HEADER_MESSAGE_IS_PLAIN_TEXT, Boolean.TRUE);
        headers.put(HEADER_PLAIN_TEXT_ENCODING, "wrongEncoding");
        sendBody("direct:enveloping", text, headers);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureException.class, UnsupportedEncodingException.class);
    }

    @Test
    public void testEnvelopedSignature() throws Exception {
        setupMock(XmlSignatureTest.payload);
        sendBody("direct:enveloped", XmlSignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExceptionEnvelopedSignatureWithWrongParent() throws Exception {
        // payload root element renamed to a -> parent name in route definition
        // does not fit
        String payload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a xmlns=\"http://test/test\"><test>Test Message</test></a>";
        MockEndpoint mock = setupExceptionMock();
        sendBody("direct:enveloped", payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureFormatException.class, null);
    }

    @Test
    public void testExceptionEnvelopedSignatureWithPlainTextPayload() throws Exception {
        // payload root element renamed to a -> parent name in route definition
        // does not fit
        String payload = "plain text Message";
        Map<String, Object> headers = new HashMap<>(1);
        headers.put(HEADER_MESSAGE_IS_PLAIN_TEXT, Boolean.TRUE);
        MockEndpoint mock = setupExceptionMock();
        sendBody("direct:enveloped", payload, headers);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureFormatException.class, null);
    }

    /**
     * The parameter can also be configured via
     * {@link XmlSignatureConfiguration#setOmitXmlDeclaration(Boolean)}
     */
    @Test
    public void testOmitXmlDeclarationViaHeader() throws Exception {
        String payloadOut = "<root xmlns=\"http://test/test\"><test>Test Message</test></root>";
        setupMock(payloadOut);
        Map<String, Object> headers = new TreeMap<>();
        headers.put(HEADER_OMIT_XML_DECLARATION, Boolean.TRUE);
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleEnvelopedXmlSig.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:outputnodesearchelementname", payload, headers);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testkeyAccessorKeySelectorDefault() throws Exception {
        setupMock();
        sendBody("direct:keyAccessorKeySelectorDefault", XmlSignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSetCanonicalizationMethodInRouteDefinition() throws Exception {
        setupMock();
        sendBody("direct:canonicalization", XmlSignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSetDigestAlgorithmInRouteDefinition() throws Exception {
        setupMock();
        sendBody("direct:signaturedigestalgorithm", XmlSignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSetTransformMethodXpath2InRouteDefinition() throws Exception {
        // example from http://www.w3.org/TR/2002/REC-xmldsig-filter2-20021108/
        String payload = (((((((((((((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (XmlSignatureTest.includeNewLine ? "\n" : "")) + "<Document xmlns=\"http://test/test\">                             ") + "<ToBeSigned>                                                     ") + "   <!-- comment -->                                              ") + "   <Data>1</Data>                                                ") + "   <NotToBeSigned>                                               ") + "     <ReallyToBeSigned>                                          ") + "       <!-- comment -->                                          ") + "       <Data>2</Data>                                            ") + "     </ReallyToBeSigned>                                         ") + "   </NotToBeSigned>                                              ") + " </ToBeSigned>                                                   ") + " <ToBeSigned>                                                    ") + "   <Data>3</Data>                                                ") + "   <NotToBeSigned>                                               ") + "     <Data>4</Data>                                              ") + "   </NotToBeSigned>                                              ") + " </ToBeSigned>                                                   ") + "</Document>";
        setupMock(payload);
        sendBody("direct:transformsXPath2", payload);
        assertMockEndpointsSatisfied();
    }

    // Secure Validation is enabled and so this should fail
    @Test
    public void testSetTransformMethodXsltXpathInRouteDefinition() throws Exception {
        // byte[] encoded = Base64.encode("Test Message".getBytes("UTF-8"));
        // String contentBase64 = new String(encoded, "UTF-8");
        // String payload =
        // "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root xmlns=\"http://test/test\"><test></test></root>";
        MockEndpoint mock = setupExceptionMock();
        sendBody("direct:transformsXsltXPath", XmlSignatureTest.payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureException.class, null);
    }

    @Test
    public void testSetTransformMethodXsltXpathInRouteDefinitionSecValDisabled() throws Exception {
        setupMock();
        sendBody("direct:transformsXsltXPathSecureValDisabled", XmlSignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testProperties() throws Exception {
        setupMock();
        sendBody("direct:props", XmlSignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testVerifyOutputNodeSearchElementName() throws Exception {
        setupMock();
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleEnvelopedXmlSig.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:outputnodesearchelementname", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testVerifyExceptionOutputNodeSearchElementNameInvalidFormat1() throws Exception {
        XmlVerifierEndpoint endpoint = context.getEndpoint(("xmlsecurity:verify:outputnodesearchelementname?keySelector=#selectorKeyValue" + "&outputNodeSearchType=ElementName&outputNodeSearch={http://test/test}root&removeSignatureElements=true"), XmlVerifierEndpoint.class);
        endpoint.setOutputNodeSearch("{wrongformat");// closing '}' missing

        MockEndpoint mock = setupExceptionMock();
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleEnvelopedXmlSig.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:outputnodesearchelementname", payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureException.class, null);
    }

    @Test
    public void testVerifyExceptionOutputNodeSearchElementNameInvalidFormat2() throws Exception {
        context.getEndpoint(("xmlsecurity:verify:outputnodesearchelementname?keySelector=#selectorKeyValue" + "&outputNodeSearchType=ElementName&outputNodeSearch={http://test/test}root&removeSignatureElements=true"), XmlVerifierEndpoint.class).setOutputNodeSearch("{wrongformat}");
        // local name missing
        MockEndpoint mock = setupExceptionMock();
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleEnvelopedXmlSig.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:outputnodesearchelementname", payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureException.class, null);
    }

    @Test
    public void testExceptionVerifyOutputNodeSearchWrongElementName() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleEnvelopingDigSig.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:outputnodesearchelementname", payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureException.class, null);
    }

    @Test
    public void testExceptionVerifyOutputNodeSearchElementNameMoreThanOneOutputElement() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleEnvelopingDigSigWithSeveralElementsWithNameRoot.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:outputnodesearchelementname", payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureException.class, null);
    }

    @Test
    public void testVerifyOutputNodeSearchXPath() throws Exception {
        setupMock();
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleEnvelopedXmlSig.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:outputnodesearchxpath", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExceptionVerifyOutputNodeSearchXPathWithNoResultNode() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleEnvelopingDigSig.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:outputnodesearchxpath", payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureException.class, null);
    }

    @Test
    public void testExceptionVerifyOutputNodeSearchXPathMoreThanOneOutputElement() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleEnvelopingDigSigWithSeveralElementsWithNameRoot.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:outputnodesearchxpath", payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureException.class, null);
    }

    @Test
    public void testInvalidKeyException() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        // wrong key type
        setUpKeys("DSA", 512);
        context.getEndpoint("xmlsecurity:sign:signexceptioninvalidkey?signatureAlgorithm=http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", XmlSignerEndpoint.class).setKeyAccessor(XmlSignatureTest.getKeyAccessor(keyPair.getPrivate()));
        sendBody("direct:signexceptioninvalidkey", XmlSignatureTest.payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureInvalidKeyException.class, null);
    }

    @Test
    public void testSignatureFormatException() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        sendBody("direct:signexceptions", "wrongFormatedPayload");
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureFormatException.class, null);
    }

    @Test
    public void testNoSuchAlgorithmException() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        sendBody("direct:noSuchAlgorithmException", XmlSignatureTest.payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureException.class, NoSuchAlgorithmException.class);
    }

    @Test
    public void testVerifyFormatExceptionNoXml() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        sendBody("direct:verifyexceptions", "wrongFormatedPayload");
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureFormatException.class, null);
    }

    @Test
    public void testVerifyFormatExceptionNoXmlWithoutSignatureElement() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        sendBody("direct:verifyexceptions", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><NoSignature></NoSignature>");
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureFormatException.class, null);
    }

    @Test
    public void testVerifyInvalidContentHashException() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleDetached.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:invalidhash", payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureInvalidContentHashException.class, null);
    }

    @Test
    public void testVerifyMantifestInvalidContentHashException() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ManifestTest_TamperedContent.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:invalidhash", payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureInvalidContentHashException.class, null);
    }

    @Test
    public void testVerifySetCryptoContextProperties() throws Exception {
        // although the content referenced by the manifest was tempered, this is
        // not detected by
        // the core validation because the manifest validation is switched off
        // by the crypto context properties
        setupMock("some text tampered");
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ManifestTest_TamperedContent.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:cryptocontextprops", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testVerifySignatureInvalidValueException() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        setUpKeys("DSA", 512);
        context.getEndpoint("xmlsecurity:verify:verifyexceptions?keySelector=#selector", XmlVerifierEndpoint.class).setKeySelector(KeySelector.singletonKeySelector(keyPair.getPublic()));
        // payload needs DSA key
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleEnvelopingDigSig.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:verifyexceptions", payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureInvalidValueException.class, null);
    }

    @Test
    public void testVerifyInvalidKeyException() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleEnvelopingDigSig.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:verifyInvalidKeyException", payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureInvalidKeyException.class, null);
    }

    @Test
    public void testUriDereferencerAndBaseUri() throws Exception {
        setupMock();
        sendBody("direct:uridereferencer", XmlSignatureTest.payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testVerifyXmlSignatureChecker() throws Exception {
        MockEndpoint mock = setupExceptionMock();
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ExampleEnvelopedXmlSig.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:xmlSignatureChecker", payload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, XmlSignatureInvalidException.class, null);
    }

    @Test
    public void testVerifyValidationFailedHandler() throws Exception {
        setupMock("some text tampered");
        InputStream payload = XmlSignatureTest.class.getResourceAsStream("/org/apache/camel/component/xmlsecurity/ManifestTest_TamperedContent.xml");
        assertNotNull("Cannot load payload", payload);
        sendBody("direct:validationFailedHandler", payload);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testFurtherParameters() throws Exception {
        setupMock(XmlSignatureTest.payload);
        String payloadWithDTDoctype = "<?xml version=\'1.0\'?>" + ((("<!DOCTYPE Signature SYSTEM " + "\"src/test/resources/org/apache/camel/component/xmlsecurity/xmldsig-core-schema.dtd\" [ <!ENTITY dsig ") + "\"http://www.w3.org/2000/09/xmldsig#\"> ]>") + "<root xmlns=\"http://test/test\"><test>Test Message</test></root>");
        sendBody("direct:furtherparams", payloadWithDTDoctype);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testReferenceUriWithIdAttributeInTheEnvelopedCase() throws Exception {
        XmlSignerEndpoint endpoint = getDetachedSignerEndpoint();
        endpoint.setParentLocalName("root");
        endpoint.setParentNamespace("http://test");
        endpoint.setXpathsToIdAttributes(null);
        String detachedPayload = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (XmlSignatureTest.includeNewLine ? "\n" : "")) + "<ns:root xmlns:ns=\"http://test\"><a ID=\"myID\"><b>bValue</b></a></ns:root>";
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        sendBody("direct:detached", detachedPayload, Collections.singletonMap(HEADER_CONTENT_REFERENCE_URI, ((Object) ("#myID"))));
        assertMockEndpointsSatisfied();
        String expectedPartContent = "<ds:Reference URI=\"#myID\">";
        checkBodyContains(mock, expectedPartContent);
    }

    @Test
    public void testDetachedSignature() throws Exception {
        testDetachedSignatureInternal();
    }

    @Test
    public void testDetachedSignatureWitTransformHeader() throws Exception {
        testDetachedSignatureInternal(Collections.singletonMap(HEADER_TRANSFORM_METHODS, ((Object) ("http://www.w3.org/2000/09/xmldsig#enveloped-signature,http://www.w3.org/TR/2001/REC-xml-c14n-20010315"))));
    }

    @Test
    public void testSignatureIdAtributeNull() throws Exception {
        // the signature Id parameter must be empty, this is set in the URI
        // already
        Element sigEle = testDetachedSignatureInternal();
        Attr attr = sigEle.getAttributeNode("Id");
        assertNull("Signature element contains Id attribute", attr);
    }

    @Test
    public void testSignatureIdAttribute() throws Exception {
        String signatureId = "sigId";
        XmlSignerEndpoint endpoint = getDetachedSignerEndpoint();
        endpoint.setSignatureId(signatureId);
        Element sigEle = testDetachedSignatureInternal();
        String value = sigEle.getAttribute("Id");
        assertNotNull("Signature Id is null", value);
        assertEquals(signatureId, value);
    }

    @Test
    public void testSignatureIdAttributeGenerated() throws Exception {
        String signatureId = null;
        XmlSignerEndpoint endpoint = getDetachedSignerEndpoint();
        endpoint.setSignatureId(signatureId);
        Element sigEle = testDetachedSignatureInternal();
        String value = sigEle.getAttribute("Id");
        assertNotNull("Signature Id is null", value);
        assertTrue("Signature Id value does not start with '_'", value.startsWith("_"));
    }

    @Test
    public void testDetachedSignatureComplexSchema() throws Exception {
        String xpath1exp = "/ns:root/test/ns1:B/C/@ID";
        String xpath2exp = "/ns:root/test/@ID";
        testDetached2Xpaths(xpath1exp, xpath2exp);
    }

    /**
     * Checks that the processor sorts the xpath expressions in such a way that
     * elements with deeper hierarchy level are signed first.
     */
    @Test
    public void testDetachedSignatureWrongXPathOrder() throws Exception {
        String xpath2exp = "/ns:root/test/ns1:B/C/@ID";
        String xpath1exp = "/ns:root/test/@ID";
        testDetached2Xpaths(xpath1exp, xpath2exp);
    }

    @Test
    public void testExceptionEnvelopedAndDetached() throws Exception {
        String detachedPayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"// 
         + "<ns:root xmlns:ns=\"http://test\"><a ID=\"myID\"><b>bValue</b></a></ns:root>";
        XmlSignerEndpoint endpoint = getDetachedSignerEndpoint();
        String parentLocalName = "parent";
        endpoint.setParentLocalName(parentLocalName);
        MockEndpoint mock = setupExceptionMock();
        mock.expectedMessageCount(1);
        sendBody("direct:detached", detachedPayload);
        assertMockEndpointsSatisfied();
        XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, (("The configuration of the XML signer component is wrong. The parent local name " + parentLocalName) + " for an enveloped signature and the XPATHs to ID attributes for a detached signature are specified. You must not specify both parameters."), null);
    }

    @Test
    public void testExceptionSchemaValidation() throws Exception {
        String detachedPayload = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (XmlSignatureTest.includeNewLine ? "\n" : "")) + "<ns:root xmlns:ns=\"http://test\"><a ID=\"myID\"><error>bValue</error></a></ns:root>";
        MockEndpoint mock = setupExceptionMock();
        mock.expectedMessageCount(1);
        sendBody("direct:detached", detachedPayload);
        assertMockEndpointsSatisfied();
        checkThrownException(mock, SchemaValidationException.class, null);
    }

    @Test
    public void testEceptionDetachedNoXmlSchema() throws Exception {
        String detachedPayload = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (XmlSignatureTest.includeNewLine ? "\n" : "")) + "<ns:root xmlns:ns=\"http://test\"><a ID=\"myID\"><b>bValue</b></a></ns:root>";
        XmlSignerEndpoint endpoint = getDetachedSignerEndpoint();
        endpoint.setSchemaResourceUri(null);
        MockEndpoint mock = setupExceptionMock();
        mock.expectedMessageCount(1);
        sendBody("direct:detached", detachedPayload);
        assertMockEndpointsSatisfied();
        XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, "The configruation of the XML Signature component is wrong: No XML schema specified in the detached case", null);
    }

    @Test
    public void testExceptionDetachedXpathInvalid() throws Exception {
        String wrongXPath = "n1:p/a";// namespace prefix is not defined

        MockEndpoint mock = testXpath(wrongXPath);
        XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, (("The configured xpath expression " + wrongXPath) + " is invalid."), XPathExpressionException.class);
    }

    @Test
    public void testExceptionDetachedXPathNoIdAttribute() throws Exception {
        String value = "not id";
        String detachedPayload = ((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (XmlSignatureTest.includeNewLine ? "\n" : "")) + "<ns:root xmlns:ns=\"http://test\"><a ID=\"myID\" stringAttr=\"") + value) + "\"><b>bValue</b></a></ns:root>";
        String xPath = "a/@stringAttr";
        MockEndpoint mock = testXpath(xPath, detachedPayload);
        XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, (((("Wrong configured xpath expression for ID attributes: The evaluation of the xpath expression " + xPath) + " resulted in an attribute which is not of type ID. The attribute value is ") + value) + "."), null);
    }

    @Test
    public void testExceptionDetachedXpathNoAttribute() throws Exception {
        String xPath = "a";// Element a

        MockEndpoint mock = testXpath(xPath);
        XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, (("Wrong configured xpath expression for ID attributes: The evaluation of the xpath expression " + xPath) + " returned a node which was not of type Attribute."), null);
    }

    @Test
    public void testExceptionDetachedXPathNoResult() throws Exception {
        String xPath = "a/@stringAttr";// for this xpath there is no result

        MockEndpoint mock = testXpath(xPath);
        XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, (("No element to sign found in the detached case. No node found for the configured xpath expressions " + xPath) + ". Either the configuration of the XML signature component is wrong or the incoming message has not the correct structure."), null);
    }

    @Test
    public void testExceptionDetachedNoParent() throws Exception {
        String detachedPayload = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (XmlSignatureTest.includeNewLine ? "\n" : "")) + "<ns:root ID=\"rootId\" xmlns:ns=\"http://test\"><a ID=\"myID\"><b>bValue</b></a></ns:root>";
        String xPath = "//@ID";
        String localName = "root";
        String namespaceURI = "http://test";
        String referenceUri = "#rootId";
        MockEndpoint mock = testXpath(xPath, detachedPayload);
        XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, (((((("Either the configuration of the XML Signature component is wrong or the incoming document has an invalid structure: The element " + localName) + "{") + namespaceURI) + "} which is referenced by the reference URI ") + referenceUri) + " has no parent element. The element must have a parent element in the configured detached case."), null);
    }

    @Test
    public void testOutputXmlEncodingEnveloping() throws Exception {
        String inputEncoding = "UTF-8";
        String signerEncoding = "UTF-16";
        String outputEncoding = "ISO-8859-1";// latin 1

        String signerEndpointUri = getSignerEndpointURIEnveloping();
        String verifierEndpointUri = getVerifierEndpointURIEnveloping();
        String directStart = "direct:enveloping";
        checkOutputEncoding(inputEncoding, signerEncoding, outputEncoding, signerEndpointUri, verifierEndpointUri, directStart);
    }

    @Test
    public void testOutputXmlEncodingEnveloped() throws Exception {
        String inputEncoding = "UTF-8";
        String signerEncoding = "UTF-16";
        String outputEncoding = "ISO-8859-1";// latin 1

        String signerEndpointUri = getSignerEndpointURIEnveloped();
        String verifierEndpointUri = getVerifierEndpointURIEnveloped();
        String directStart = "direct:enveloped";
        checkOutputEncoding(inputEncoding, signerEncoding, outputEncoding, signerEndpointUri, verifierEndpointUri, directStart);
    }

    @Test
    public void testExceptionParentLocalNameAndXPathSet() throws Exception {
        XmlSignerEndpoint endpoint = getSignatureEncpointForSignException();
        MockEndpoint mock = setupExceptionMock();
        try {
            endpoint.setParentXpath(XmlSignatureTest.getNodeSerachXPath());
            endpoint.setParentLocalName("root");
            sendBody("direct:signexceptions", XmlSignatureTest.payload);
            assertMockEndpointsSatisfied();
            XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, ("The configuration of the XML signer component is wrong. "// 
             + "The parent local name root and the parent XPath //pre:root are specified. You must not specify both parameters."), null);
        } finally {
            endpoint.setParentXpath(null);
            endpoint.setParentLocalName(null);
        }
    }

    @Test
    public void testExceptionXpathsToIdAttributesNameAndXPathSet() throws Exception {
        XmlSignerEndpoint endpoint = getSignatureEncpointForSignException();
        MockEndpoint mock = setupExceptionMock();
        try {
            endpoint.setParentXpath(XmlSignatureTest.getNodeSerachXPath());
            List<XPathFilterParameterSpec> xpaths = Collections.singletonList(XmlSignatureHelper.getXpathFilter("/ns:root/a/@ID", null));
            endpoint.setXpathsToIdAttributes(xpaths);
            sendBody("direct:signexceptions", XmlSignatureTest.payload);
            assertMockEndpointsSatisfied();
            XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, ("The configuration of the XML signer component is wrong. "// 
             + "The parent XPath //pre:root for an enveloped signature and the XPATHs to ID attributes for a detached signature are specified. You must not specify both parameters."), null);
        } finally {
            endpoint.setParentXpath(null);
            endpoint.setXpathsToIdAttributes(null);
        }
    }

    @Test
    public void testExceptionInvalidParentXpath() throws Exception {
        XmlSignerEndpoint endpoint = getSignatureEncpointForSignException();
        MockEndpoint mock = setupExceptionMock();
        try {
            endpoint.setParentXpath(XmlSignatureHelper.getXpathFilter("//pre:root", null));// invalid xpath: namespace-prefix mapping is missing

            sendBody("direct:signexceptions", XmlSignatureTest.payload);
            assertMockEndpointsSatisfied();
            XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, "The parent XPath //pre:root is wrongly configured: The XPath //pre:root is invalid.", null);
        } finally {
            endpoint.setParentXpath(null);
        }
    }

    @Test
    public void testExceptionParentXpathWithNoResult() throws Exception {
        XmlSignerEndpoint endpoint = getSignatureEncpointForSignException();
        MockEndpoint mock = setupExceptionMock();
        try {
            endpoint.setParentXpath(XmlSignatureHelper.getXpathFilter("//root", null));// xpath with no result

            sendBody("direct:signexceptions", XmlSignatureTest.payload);
            assertMockEndpointsSatisfied();
            XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, "The parent XPath //root returned no result. Check the configuration of the XML signer component.", null);
        } finally {
            endpoint.setParentXpath(null);
        }
    }

    @Test
    public void testExceptionParentXpathWithNoElementResult() throws Exception {
        XmlSignerEndpoint endpoint = getSignatureEncpointForSignException();
        MockEndpoint mock = setupExceptionMock();
        try {
            String myPayload = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (XmlSignatureTest.includeNewLine ? "\n" : "")) + "<ns:root ID=\"rootId\" xmlns:ns=\"http://test\"></ns:root>";
            endpoint.setParentXpath(XmlSignatureHelper.getXpathFilter("/pre:root/@ID", Collections.singletonMap("pre", "http://test")));// xpath with no element result

            sendBody("direct:signexceptions", myPayload);
            assertMockEndpointsSatisfied();
            XmlSignatureTest.checkThrownException(mock, XmlSignatureException.class, "The parent XPath /pre:root/@ID returned no element. Check the configuration of the XML signer component.", null);
        } finally {
            endpoint.setParentXpath(null);
        }
    }

    @Test
    public void testEnvelopedSignatureWithParentXpath() throws Exception {
        String myPayload = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (XmlSignatureTest.includeNewLine ? "\n" : "")) + "<ns:root xmlns:ns=\"http://test\"><a>a1</a><a/><test>Test Message</test></ns:root>";
        setupMock(myPayload);
        sendBody("direct:envelopedParentXpath", myPayload);
        assertMockEndpointsSatisfied();
    }

    /**
     * KeySelector which retrieves the public key from the KeyValue element and
     * returns it. NOTE: If the key algorithm doesn't match signature algorithm,
     * then the public key will be ignored.
     */
    static class KeyValueKeySelector extends KeySelector {
        public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method, XMLCryptoContext context) throws KeySelectorException {
            if (keyInfo == null) {
                throw new KeySelectorException("Null KeyInfo object!");
            }
            SignatureMethod sm = ((SignatureMethod) (method));
            @SuppressWarnings("rawtypes")
            List list = keyInfo.getContent();
            for (int i = 0; i < (list.size()); i++) {
                XMLStructure xmlStructure = ((XMLStructure) (list.get(i)));
                if (xmlStructure instanceof KeyValue) {
                    PublicKey pk = null;
                    try {
                        pk = ((KeyValue) (xmlStructure)).getPublicKey();
                    } catch (KeyException ke) {
                        throw new KeySelectorException(ke);
                    }
                    // make sure algorithm is compatible with method
                    if (XmlSignatureTest.KeyValueKeySelector.algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                        return new XmlSignatureTest.SimpleKeySelectorResult(pk);
                    }
                }
            }
            throw new KeySelectorException("No KeyValue element found!");
        }

        static boolean algEquals(String algURI, String algName) {
            return ((algName.equalsIgnoreCase("DSA")) && (algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1))) || ((algName.equalsIgnoreCase("RSA")) && (algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)));
        }
    }

    private static class SimpleKeySelectorResult implements KeySelectorResult {
        private PublicKey pk;

        SimpleKeySelectorResult(PublicKey pk) {
            this.pk = pk;
        }

        public Key getKey() {
            return pk;
        }
    }
}

