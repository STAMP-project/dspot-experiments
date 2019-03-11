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
package ch.qos.logback.core.joran.action;


import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.util.StatusPrinter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test {@link PropertyAction}.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class PropertyActionTest {
    Context context;

    InterpretationContext ec;

    PropertyAction propertyAction;

    DummyAttributes atts = new DummyAttributes();

    @Test
    public void nameValuePair() {
        atts.setValue("name", "v1");
        atts.setValue("value", "work");
        propertyAction.begin(ec, null, atts);
        Assert.assertEquals("work", ec.getProperty("v1"));
    }

    @Test
    public void nameValuePairWithPrerequisiteSubsitution() {
        context.putProperty("w", "wor");
        atts.setValue("name", "v1");
        atts.setValue("value", "${w}k");
        propertyAction.begin(ec, null, atts);
        Assert.assertEquals("work", ec.getProperty("v1"));
    }

    @Test
    public void noValue() {
        atts.setValue("name", "v1");
        propertyAction.begin(ec, null, atts);
        Assert.assertEquals(1, context.getStatusManager().getCount());
        Assert.assertTrue(checkError());
    }

    @Test
    public void noName() {
        atts.setValue("value", "v1");
        propertyAction.begin(ec, null, atts);
        Assert.assertEquals(1, context.getStatusManager().getCount());
        Assert.assertTrue(checkError());
    }

    @Test
    public void noAttributes() {
        propertyAction.begin(ec, null, atts);
        Assert.assertEquals(1, context.getStatusManager().getCount());
        Assert.assertTrue(checkError());
        StatusPrinter.print(context);
    }

    @Test
    public void testFileNotLoaded() {
        atts.setValue("file", "toto");
        atts.setValue("value", "work");
        propertyAction.begin(ec, null, atts);
        Assert.assertEquals(1, context.getStatusManager().getCount());
        Assert.assertTrue(checkError());
    }

    @Test
    public void testLoadFileWithPrerequisiteSubsitution() {
        context.putProperty("STEM", ((CoreTestConstants.TEST_SRC_PREFIX) + "input/joran"));
        atts.setValue("file", "${STEM}/propertyActionTest.properties");
        propertyAction.begin(ec, null, atts);
        Assert.assertEquals("tata", ec.getProperty("v1"));
        Assert.assertEquals("toto", ec.getProperty("v2"));
    }

    @Test
    public void testLoadFile() {
        atts.setValue("file", ((CoreTestConstants.TEST_SRC_PREFIX) + "input/joran/propertyActionTest.properties"));
        propertyAction.begin(ec, null, atts);
        Assert.assertEquals("tata", ec.getProperty("v1"));
        Assert.assertEquals("toto", ec.getProperty("v2"));
    }

    @Test
    public void testLoadResource() {
        atts.setValue("resource", "asResource/joran/propertyActionTest.properties");
        propertyAction.begin(ec, null, atts);
        Assert.assertEquals("tata", ec.getProperty("r1"));
        Assert.assertEquals("toto", ec.getProperty("r2"));
    }

    @Test
    public void testLoadResourceWithPrerequisiteSubsitution() {
        context.putProperty("STEM", "asResource/joran");
        atts.setValue("resource", "${STEM}/propertyActionTest.properties");
        propertyAction.begin(ec, null, atts);
        Assert.assertEquals("tata", ec.getProperty("r1"));
        Assert.assertEquals("toto", ec.getProperty("r2"));
    }

    @Test
    public void testLoadNotPossible() {
        atts.setValue("file", "toto");
        propertyAction.begin(ec, null, atts);
        Assert.assertEquals(1, context.getStatusManager().getCount());
        Assert.assertTrue(checkFileErrors());
    }
}

