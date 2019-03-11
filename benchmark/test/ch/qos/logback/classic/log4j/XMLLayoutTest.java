/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 2014, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.log4j;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * A test for correct (well-formed, valid) log4j XML layout.
 *
 * @author Gabriel Corona
 */
public class XMLLayoutTest {
    private static final String DOCTYPE = "<!DOCTYPE log4j:eventSet SYSTEM \"http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/xml/doc-files/log4j.dtd\">";

    private static final String NAMESPACE = "http://jakarta.apache.org/log4j/";

    private static final String DTD_URI = "http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/xml/doc-files/log4j.dtd";

    private static final String MDC_KEY = "key <&>\'\"]]>";

    private static final String MDC_VALUE = "value <&>\'\"]]>";

    private static final String MESSAGE = "test message, <&>\'\"";

    private LoggerContext lc;

    private Logger root;

    private XMLLayout layout;

    @Test
    public void testDoLayout() throws Exception {
        ILoggingEvent le = createLoggingEvent();
        String result = (XMLLayoutTest.DOCTYPE) + "<log4j:eventSet xmlns:log4j='http://jakarta.apache.org/log4j/'>";
        if ((layout.getFileHeader()) != null) {
            result += layout.getFileHeader();
        }
        if ((layout.getPresentationHeader()) != null) {
            result += layout.getPresentationHeader();
        }
        result += layout.doLayout(le);
        if ((layout.getPresentationFooter()) != null) {
            result += layout.getPresentationFooter();
        }
        if ((layout.getFileFooter()) != null) {
            result += layout.getFileFooter();
        }
        result += "</log4j:eventSet>";
        Document document = parse(result);
        XPath xpath = this.newXPath();
        // Test log4j:event:
        NodeList eventNodes = ((NodeList) (xpath.compile("//log4j:event").evaluate(document, XPathConstants.NODESET)));
        Assert.assertEquals(1, eventNodes.getLength());
        // Test log4g:message:
        Assert.assertEquals(XMLLayoutTest.MESSAGE, xpath.compile("//log4j:message").evaluate(document, XPathConstants.STRING));
        // Test log4j:data:
        NodeList dataNodes = ((NodeList) (xpath.compile("//log4j:data").evaluate(document, XPathConstants.NODESET)));
        boolean foundMdc = false;
        for (int i = 0; i != (dataNodes.getLength()); ++i) {
            Node dataNode = dataNodes.item(i);
            if (dataNode.getAttributes().getNamedItem("name").getNodeValue().equals(XMLLayoutTest.MDC_KEY)) {
                foundMdc = true;
                Assert.assertEquals(XMLLayoutTest.MDC_VALUE, dataNode.getAttributes().getNamedItem("value").getNodeValue());
                break;
            }
        }
        Assert.assertTrue(foundMdc);
    }
}

