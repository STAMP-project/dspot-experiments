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


package ch.qos.logback.core.joran.event;


/**
 * Test whether SaxEventRecorder does a good job.
 *
 * @author Ceki Gulcu
 */
public class AmplSaxEventRecorderTest {
    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    ch.qos.logback.core.status.StatusChecker statusChecker = new ch.qos.logback.core.status.StatusChecker(context);

    javax.xml.parsers.SAXParser createParser() throws java.lang.Exception {
        javax.xml.parsers.SAXParserFactory spf = javax.xml.parsers.SAXParserFactory.newInstance();
        return spf.newSAXParser();
    }

    public java.util.List<ch.qos.logback.core.joran.event.SaxEvent> doTest(java.lang.String filename) throws java.lang.Exception {
        ch.qos.logback.core.joran.event.SaxEventRecorder recorder = new ch.qos.logback.core.joran.event.SaxEventRecorder(context);
        java.io.FileInputStream fis = new java.io.FileInputStream((((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "input/joran/") + filename));
        recorder.recordEvents(fis);
        return recorder.getSaxEventList();
    }

    public void dump(java.util.List<ch.qos.logback.core.joran.event.SaxEvent> seList) {
        for (ch.qos.logback.core.joran.event.SaxEvent se : seList) {
            java.lang.System.out.println(se);
        }
    }

    @org.junit.Test
    public void test1() throws java.lang.Exception {
        java.util.List<ch.qos.logback.core.joran.event.SaxEvent> seList = doTest("event1.xml");
        org.junit.Assert.assertTrue(((statusChecker.getHighestLevel(0)) == (ch.qos.logback.core.status.Status.INFO)));
        // dump(seList);
        org.junit.Assert.assertEquals(11, seList.size());
    }

    @org.junit.Test
    public void test2() throws java.lang.Exception {
        java.util.List<ch.qos.logback.core.joran.event.SaxEvent> seList = doTest("ampEvent.xml");
        ch.qos.logback.core.status.StatusManager sm = context.getStatusManager();
        org.junit.Assert.assertTrue(((statusChecker.getHighestLevel(0)) == (ch.qos.logback.core.status.Status.INFO)));
        // dump(seList);
        org.junit.Assert.assertEquals(3, seList.size());
        ch.qos.logback.core.joran.event.BodyEvent be = ((ch.qos.logback.core.joran.event.BodyEvent) (seList.get(1)));
        org.junit.Assert.assertEquals("xxx & yyy", be.getText());
    }

    @org.junit.Test
    public void test3() throws java.lang.Exception {
        java.util.List<ch.qos.logback.core.joran.event.SaxEvent> seList = doTest("inc.xml");
        ch.qos.logback.core.status.StatusManager sm = context.getStatusManager();
        org.junit.Assert.assertTrue(((statusChecker.getHighestLevel(0)) == (ch.qos.logback.core.status.Status.INFO)));
        // dump(seList);
        org.junit.Assert.assertEquals(4, seList.size());
        ch.qos.logback.core.joran.event.StartEvent se = ((ch.qos.logback.core.joran.event.StartEvent) (seList.get(1)));
        org.xml.sax.Attributes attr = se.getAttributes();
        org.junit.Assert.assertNotNull(attr);
        org.junit.Assert.assertEquals("1", attr.getValue("increment"));
    }

    @org.junit.Test
    public void bodyWithSpacesAndQuotes() throws java.lang.Exception {
        java.util.List<ch.qos.logback.core.joran.event.SaxEvent> seList = doTest("spacesAndQuotes.xml");
        org.junit.Assert.assertEquals(3, seList.size());
        ch.qos.logback.core.joran.event.BodyEvent be = ((ch.qos.logback.core.joran.event.BodyEvent) (seList.get(1)));
        org.junit.Assert.assertEquals("[x][x] \"xyz\"%n", be.getText());
    }
}

