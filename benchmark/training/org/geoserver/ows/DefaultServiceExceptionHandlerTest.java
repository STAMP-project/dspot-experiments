/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.TestCase;
import org.apache.xpath.XPathAPI;
import org.geoserver.platform.ServiceException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class DefaultServiceExceptionHandlerTest extends TestCase {
    private DefaultServiceExceptionHandler handler;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private Request requestInfo;

    public void testHandleServiceException() throws Exception {
        ServiceException exception = new ServiceException("hello service exception");
        exception.setCode("helloCode");
        exception.setLocator("helloLocator");
        exception.getExceptionText().add("helloText");
        handler.handleServiceException(exception, requestInfo);
        InputStream input = new ByteArrayInputStream(response.getContentAsString().getBytes());
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        Document doc = docBuilderFactory.newDocumentBuilder().parse(input);
        TestCase.assertEquals("ows:ExceptionReport", doc.getDocumentElement().getNodeName());
    }

    public void testHandleServiceExceptionEncoding() throws Exception {
        String message = "foo & <foo> \"foo\'s\"";
        ServiceException exception = new ServiceException(message);
        exception.setLocator("test-locator");
        handler.handleServiceException(exception, requestInfo);
        InputStream input = new ByteArrayInputStream(response.getContentAsString().getBytes());
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        Document doc = docBuilderFactory.newDocumentBuilder().parse(input);
        Node exceptionText = XPathAPI.selectSingleNode(doc, "ows:ExceptionReport/ows:Exception/ows:ExceptionText/text()");
        TestCase.assertNotNull(exceptionText);
        TestCase.assertEquals("round-tripped through character entities", message, exceptionText.getTextContent());
    }

    public void testHandleServiceExceptionCauses() throws Exception {
        // create a stack of three exceptions
        IllegalArgumentException illegalArgument = new IllegalArgumentException("Illegal argument here");
        IOException ioException = new IOException("I/O exception here");
        ioException.initCause(illegalArgument);
        ServiceException serviceException = new ServiceException("hello service exception");
        serviceException.setCode("helloCode");
        serviceException.setLocator("helloLocator");
        serviceException.getExceptionText().add("helloText");
        serviceException.initCause(ioException);
        handler.handleServiceException(serviceException, requestInfo);
        InputStream input = new ByteArrayInputStream(response.getContentAsString().getBytes());
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        Document doc = docBuilderFactory.newDocumentBuilder().parse(input);
        Node exceptionTextNode = XPathAPI.selectSingleNode(doc, "ows:ExceptionReport/ows:Exception/ows:ExceptionText/text()");
        TestCase.assertNotNull(exceptionTextNode);
        // normalise whitespace
        String exceptionText = exceptionTextNode.getNodeValue().replaceAll("\\s+", " ");
        TestCase.assertTrue(((exceptionText.indexOf(illegalArgument.getMessage())) != (-1)));
        TestCase.assertTrue(((exceptionText.indexOf(ioException.getMessage())) != (-1)));
        TestCase.assertTrue(((exceptionText.indexOf(serviceException.getMessage())) != (-1)));
    }

    public void testHandleServiceExceptionNullMessages() throws Exception {
        // create a stack of three exceptions
        NullPointerException npe = new NullPointerException();
        ServiceException serviceException = new ServiceException("hello service exception");
        serviceException.setCode("helloCode");
        serviceException.setLocator("helloLocator");
        serviceException.getExceptionText().add("NullPointerException");
        serviceException.initCause(npe);
        handler.handleServiceException(serviceException, requestInfo);
        InputStream input = new ByteArrayInputStream(response.getContentAsString().getBytes());
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        Document doc = docBuilderFactory.newDocumentBuilder().parse(input);
        Node exceptionTextNode = XPathAPI.selectSingleNode(doc, "ows:ExceptionReport/ows:Exception/ows:ExceptionText/text()");
        TestCase.assertNotNull(exceptionTextNode);
        // normalise whitespace
        String exceptionText = exceptionTextNode.getNodeValue().replaceAll("\\s+", " ");
        // used to contain an extra " null" at the end
        TestCase.assertEquals("hello service exception NullPointerException", exceptionText);
    }
}

