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
package ch.qos.logback.access.joran;


import ch.qos.logback.access.AccessTestConstants;
import ch.qos.logback.access.dummy.DummyAccessEventBuilder;
import ch.qos.logback.access.spi.AccessContext;
import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.testUtil.StringListAppender;
import org.junit.Assert;
import org.junit.Test;


public class JoranConfiguratorTest {
    AccessContext context = new AccessContext();

    @Test
    public void smoke() throws Exception {
        configure(((AccessTestConstants.TEST_DIR_PREFIX) + "input/joran/smoke.xml"));
        ListAppender<IAccessEvent> listAppender = ((ListAppender<IAccessEvent>) (context.getAppender("LIST")));
        IAccessEvent event = DummyAccessEventBuilder.buildNewAccessEvent();
        listAppender.doAppend(event);
        Assert.assertEquals(1, listAppender.list.size());
        Assert.assertEquals(1, listAppender.list.size());
        IAccessEvent ae = listAppender.list.get(0);
        Assert.assertNotNull(ae);
    }

    @Test
    public void defaultLayout() throws Exception {
        configure(((AccessTestConstants.TEST_DIR_PREFIX) + "input/joran/defaultLayout.xml"));
        StringListAppender<IAccessEvent> listAppender = ((StringListAppender<IAccessEvent>) (context.getAppender("STR_LIST")));
        IAccessEvent event = DummyAccessEventBuilder.buildNewAccessEvent();
        listAppender.doAppend(event);
        Assert.assertEquals(1, listAppender.strList.size());
        // the result contains a line separator at the end
        Assert.assertTrue(listAppender.strList.get(0).startsWith("testMethod"));
    }
}

