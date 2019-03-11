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
package ch.qos.logback.core.spi;


import ch.qos.logback.core.Appender;
import ch.qos.logback.core.helpers.NOPAppender;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test case verifies all the methods of AppenderAttableImpl work properly.
 *
 * @author Ralph Goers
 */
public class AppenderAttachableImplTest {
    private AppenderAttachableImpl<AppenderAttachableImplTest.TestEvent> aai;

    @Test
    public void testAddAppender() throws Exception {
        AppenderAttachableImplTest.TestEvent event = new AppenderAttachableImplTest.TestEvent();
        NOPAppender<AppenderAttachableImplTest.TestEvent> ta = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        ta.start();
        aai.addAppender(ta);
        ta = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        ta.setName("test");
        ta.start();
        aai.addAppender(ta);
        int size = aai.appendLoopOnAppenders(event);
        Assert.assertTrue("Incorrect number of appenders", (size == 2));
    }

    @Test
    public void testIteratorForAppenders() throws Exception {
        NOPAppender<AppenderAttachableImplTest.TestEvent> ta = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        ta.start();
        aai.addAppender(ta);
        NOPAppender<AppenderAttachableImplTest.TestEvent> tab = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        tab.setName("test");
        tab.start();
        aai.addAppender(tab);
        Iterator<Appender<AppenderAttachableImplTest.TestEvent>> iter = aai.iteratorForAppenders();
        int size = 0;
        while (iter.hasNext()) {
            ++size;
            Appender<AppenderAttachableImplTest.TestEvent> app = iter.next();
            Assert.assertTrue("Bad Appender", ((app == ta) || (app == tab)));
        } 
        Assert.assertTrue("Incorrect number of appenders", (size == 2));
    }

    @Test
    public void getGetAppender() throws Exception {
        NOPAppender<AppenderAttachableImplTest.TestEvent> test = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        test.setName("test");
        test.start();
        aai.addAppender(test);
        NOPAppender<AppenderAttachableImplTest.TestEvent> testOther = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        testOther.setName("testOther");
        testOther.start();
        aai.addAppender(testOther);
        Appender<AppenderAttachableImplTest.TestEvent> a = aai.getAppender("testOther");
        Assert.assertNotNull("Could not find appender", a);
        Assert.assertTrue("Wrong appender", (a == testOther));
        a = aai.getAppender("test");
        Assert.assertNotNull("Could not find appender", a);
        Assert.assertTrue("Wrong appender", (a == test));
        a = aai.getAppender("NotThere");
        Assert.assertNull("Appender was returned", a);
    }

    @Test
    public void testIsAttached() throws Exception {
        NOPAppender<AppenderAttachableImplTest.TestEvent> ta = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        ta.start();
        aai.addAppender(ta);
        NOPAppender<AppenderAttachableImplTest.TestEvent> tab = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        tab.setName("test");
        tab.start();
        aai.addAppender(tab);
        Assert.assertTrue("Appender is not attached", aai.isAttached(ta));
        Assert.assertTrue("Appender is not attached", aai.isAttached(tab));
    }

    @Test
    public void testDetachAndStopAllAppenders() throws Exception {
        NOPAppender<AppenderAttachableImplTest.TestEvent> ta = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        ta.start();
        aai.addAppender(ta);
        NOPAppender<AppenderAttachableImplTest.TestEvent> tab = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        tab.setName("test");
        tab.start();
        aai.addAppender(tab);
        Assert.assertTrue("Appender was not started", tab.isStarted());
        aai.detachAndStopAllAppenders();
        Assert.assertNull("Appender was not removed", aai.getAppender("test"));
        Assert.assertFalse("Appender was not stopped", tab.isStarted());
    }

    @Test
    public void testDetachAppender() throws Exception {
        NOPAppender<AppenderAttachableImplTest.TestEvent> ta = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        ta.start();
        aai.addAppender(ta);
        NOPAppender<AppenderAttachableImplTest.TestEvent> tab = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        tab.setName("test");
        tab.start();
        aai.addAppender(tab);
        Assert.assertTrue("Appender not detached", aai.detachAppender(tab));
        Assert.assertNull("Appender was not removed", aai.getAppender("test"));
        Assert.assertFalse("Appender detach error", aai.detachAppender(tab));
    }

    @Test
    public void testDetachAppenderByName() throws Exception {
        NOPAppender<AppenderAttachableImplTest.TestEvent> ta = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        ta.setName("test1");
        ta.start();
        aai.addAppender(ta);
        NOPAppender<AppenderAttachableImplTest.TestEvent> tab = new NOPAppender<AppenderAttachableImplTest.TestEvent>();
        tab.setName("test");
        tab.start();
        aai.addAppender(tab);
        Assert.assertTrue(aai.detachAppender("test"));
        Assert.assertTrue(aai.detachAppender("test1"));
        Assert.assertFalse(aai.detachAppender("test1"));
    }

    private static class TestEvent {}
}

