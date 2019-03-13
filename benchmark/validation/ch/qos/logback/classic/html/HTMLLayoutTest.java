/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
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
package ch.qos.logback.classic.html;


import CoreConstants.LINE_SEPARATOR;
import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.DummyThrowableProxy;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.testUtil.StringListAppender;
import ch.qos.logback.core.util.StatusPrinter;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import static DefaultThrowableRenderer.TRACE_PREFIX;


public class HTMLLayoutTest {
    LoggerContext lc;

    Logger root;

    HTMLLayout layout;

    @Test
    public void testHeader() throws Exception {
        String header = layout.getFileHeader();
        // System.out.println(header);
        Document doc = parseOutput((header + "</body></html>"));
        Element rootElement = doc.getRootElement();
        Assert.assertNotNull(rootElement.element("body"));
    }

    @Test
    public void testPresentationHeader() throws Exception {
        String header = layout.getFileHeader();
        String presentationHeader = layout.getPresentationHeader();
        header = header + presentationHeader;
        // System.out.println(header);
        Document doc = parseOutput((header + "</table></body></html>"));
        Element rootElement = doc.getRootElement();
        Element bodyElement = rootElement.element("body");
        Element tableElement = bodyElement.element("table");
        Element trElement = tableElement.element("tr");
        List<Element> elementList = trElement.elements();
        Assert.assertEquals("Level", elementList.get(0).getText());
        Assert.assertEquals("Thread", elementList.get(1).getText());
        Assert.assertEquals("Message", elementList.get(2).getText());
    }

    @Test
    public void testAppendThrowable() throws Exception {
        StringBuilder buf = new StringBuilder();
        DummyThrowableProxy tp = new DummyThrowableProxy();
        tp.setClassName("test1");
        tp.setMessage("msg1");
        StackTraceElement ste1 = new StackTraceElement("c1", "m1", "f1", 1);
        StackTraceElement ste2 = new StackTraceElement("c2", "m2", "f2", 2);
        StackTraceElementProxy[] stepArray = new StackTraceElementProxy[]{ new StackTraceElementProxy(ste1), new StackTraceElementProxy(ste2) };
        tp.setStackTraceElementProxyArray(stepArray);
        DefaultThrowableRenderer renderer = ((DefaultThrowableRenderer) (layout.getThrowableRenderer()));
        renderer.render(buf, tp);
        System.out.println(buf.toString());
        String[] result = buf.toString().split(LINE_SEPARATOR);
        System.out.println(result[0]);
        Assert.assertEquals("test1: msg1", result[0]);
        Assert.assertEquals(((TRACE_PREFIX) + "at c1.m1(f1:1)"), result[1]);
    }

    @Test
    public void testDoLayout() throws Exception {
        ILoggingEvent le = createLoggingEvent();
        String result = layout.getFileHeader();
        result += layout.getPresentationHeader();
        result += layout.doLayout(le);
        result += layout.getPresentationFooter();
        result += layout.getFileFooter();
        Document doc = parseOutput(result);
        Element rootElement = doc.getRootElement();
        rootElement.toString();
        // the rest of this test is very dependent of the output generated
        // by HTMLLayout. Given that the XML parser already verifies
        // that the result conforms to xhtml-strict, we may want to
        // skip the assertions below. However, the assertions below are another
        // *independent* way to check the output format.
        // head, body
        Assert.assertEquals(2, rootElement.elements().size());
        Element bodyElement = ((Element) (rootElement.elements().get(1)));
        Element tableElement = ((Element) (bodyElement.elements().get(3)));
        Assert.assertEquals("table", tableElement.getName());
        Element trElement = ((Element) (tableElement.elements().get(1)));
        {
            Element tdElement = ((Element) (trElement.elements().get(0)));
            Assert.assertEquals("DEBUG", tdElement.getText());
        }
        {
            Element tdElement = ((Element) (trElement.elements().get(1)));
            String regex = ClassicTestConstants.NAKED_MAIN_REGEX;
            System.out.println(tdElement.getText());
            Assert.assertTrue(tdElement.getText().matches(regex));
        }
        {
            Element tdElement = ((Element) (trElement.elements().get(2)));
            Assert.assertEquals("test message", tdElement.getText());
        }
    }

    @Test
    public void layoutWithException() throws Exception {
        layout.setPattern("%level %thread %msg %ex");
        LoggingEvent le = createLoggingEvent();
        le.setThrowableProxy(new ThrowableProxy(new Exception("test Exception")));
        String result = layout.doLayout(le);
        String stringToParse = layout.getFileHeader();
        stringToParse = stringToParse + (layout.getPresentationHeader());
        stringToParse += result;
        stringToParse += "</table></body></html>";
        // System.out.println(stringToParse);
        Document doc = parseOutput(stringToParse);
        Element rootElement = doc.getRootElement();
        Element bodyElement = rootElement.element("body");
        Element tableElement = bodyElement.element("table");
        List<Element> trElementList = tableElement.elements();
        Element exceptionRowElement = trElementList.get(2);
        Element exceptionElement = exceptionRowElement.element("td");
        Assert.assertEquals(3, tableElement.elements().size());
        Assert.assertTrue(exceptionElement.getText().contains("java.lang.Exception: test Exception"));
    }

    @Test
    public void testConversionRuleSupportInHtmlLayout() throws JoranException {
        configure(((ClassicTestConstants.JORAN_INPUT_PREFIX) + "conversionRule/htmlLayout0.xml"));
        root.getAppender("LIST");
        String msg = "Simon says";
        root.debug(msg);
        StringListAppender<ILoggingEvent> sla = ((StringListAppender<ILoggingEvent>) (root.getAppender("LIST")));
        Assert.assertNotNull(sla);
        StatusPrinter.print(lc);
        Assert.assertEquals(1, sla.strList.size());
        Assert.assertFalse(sla.strList.get(0).contains("PARSER_ERROR"));
    }
}

