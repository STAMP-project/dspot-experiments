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
package ch.qos.logback.core.joran.event.stax;


import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.testUtil.StatusChecker;
import java.util.List;
import javax.xml.stream.events.Attribute;
import org.junit.Assert;
import org.junit.Test;


public class StaxEventRecorderTest {
    Context context = new ContextBase();

    StatusChecker statusChecker = new StatusChecker(context);

    @Test
    public void testParsingOfXMLWithAttributesAndBodyText() throws Exception {
        List<StaxEvent> seList = doTest("event1.xml");
        Assert.assertTrue(((getHighestLevel(0)) == (Status.INFO)));
        // dump(seList);
        Assert.assertEquals(11, seList.size());
        Assert.assertEquals("test", seList.get(0).getName());
        Assert.assertEquals("badBegin", seList.get(1).getName());
        StartEvent startEvent = ((StartEvent) (seList.get(7)));
        Assert.assertEquals("John Doe", startEvent.getAttributeByName("name").getValue());
        Assert.assertEquals("XXX&", getText());
    }

    @Test
    public void testProcessingOfTextWithEntityCharacters() throws Exception {
        List<StaxEvent> seList = doTest("ampEvent.xml");
        Assert.assertTrue(((getHighestLevel(0)) == (Status.INFO)));
        // dump(seList);
        Assert.assertEquals(3, seList.size());
        BodyEvent be = ((BodyEvent) (seList.get(1)));
        Assert.assertEquals("xxx & yyy", be.getText());
    }

    @Test
    public void testAttributeProcessing() throws Exception {
        List<StaxEvent> seList = doTest("inc.xml");
        Assert.assertTrue(((getHighestLevel(0)) == (Status.INFO)));
        Assert.assertEquals(4, seList.size());
        StartEvent se = ((StartEvent) (seList.get(1)));
        Attribute attr = se.getAttributeByName("increment");
        Assert.assertNotNull(attr);
        Assert.assertEquals("1", attr.getValue());
    }

    @Test
    public void bodyWithSpacesAndQuotes() throws Exception {
        List<StaxEvent> seList = doTest("spacesAndQuotes.xml");
        Assert.assertEquals(3, seList.size());
        BodyEvent be = ((BodyEvent) (seList.get(1)));
        Assert.assertEquals("[x][x] \"xyz\"%n", be.getText());
    }
}

