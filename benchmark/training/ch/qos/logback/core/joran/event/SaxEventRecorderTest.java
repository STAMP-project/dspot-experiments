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


import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.testUtil.StatusChecker;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.Attributes;


/**
 * Test whether SaxEventRecorder does a good job.
 *
 * @author Ceki Gulcu
 */
public class SaxEventRecorderTest {
    Context context = new ContextBase();

    StatusChecker statusChecker = new StatusChecker(context);

    @Test
    public void test1() throws Exception {
        List<SaxEvent> seList = doTest("event1.xml");
        Assert.assertTrue(((getHighestLevel(0)) == (Status.INFO)));
        // dump(seList);
        Assert.assertEquals(11, seList.size());
    }

    @Test
    public void test2() throws Exception {
        List<SaxEvent> seList = doTest("ampEvent.xml");
        Assert.assertTrue(((getHighestLevel(0)) == (Status.INFO)));
        // dump(seList);
        Assert.assertEquals(3, seList.size());
        BodyEvent be = ((BodyEvent) (seList.get(1)));
        Assert.assertEquals("xxx & yyy", be.getText());
    }

    @Test
    public void test3() throws Exception {
        List<SaxEvent> seList = doTest("inc.xml");
        Assert.assertTrue(((getHighestLevel(0)) == (Status.INFO)));
        // dump(seList);
        Assert.assertEquals(4, seList.size());
        StartEvent se = ((StartEvent) (seList.get(1)));
        Attributes attr = se.getAttributes();
        Assert.assertNotNull(attr);
        Assert.assertEquals("1", attr.getValue("increment"));
    }

    @Test
    public void bodyWithSpacesAndQuotes() throws Exception {
        List<SaxEvent> seList = doTest("spacesAndQuotes.xml");
        Assert.assertEquals(3, seList.size());
        BodyEvent be = ((BodyEvent) (seList.get(1)));
        Assert.assertEquals("[x][x] \"xyz\"%n", be.getText());
    }
}

