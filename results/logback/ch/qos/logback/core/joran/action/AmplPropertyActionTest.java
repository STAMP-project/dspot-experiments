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


/**
 * Test {@link PropertyAction}.
 * @author Ceki G&uuml;lc&uuml;
 */
public class AmplPropertyActionTest {
    ch.qos.logback.core.Context context;

    ch.qos.logback.core.joran.spi.InterpretationContext ec;

    ch.qos.logback.core.joran.action.PropertyAction propertyAction;

    ch.qos.logback.core.joran.action.DummyAttributes atts = new ch.qos.logback.core.joran.action.DummyAttributes();

    @org.junit.Before
    public void setUp() throws java.lang.Exception {
        context = new ch.qos.logback.core.ContextBase();
        ec = new ch.qos.logback.core.joran.spi.InterpretationContext(context, null);
        propertyAction = new ch.qos.logback.core.joran.action.PropertyAction();
        propertyAction.setContext(context);
    }

    @org.junit.After
    public void tearDown() throws java.lang.Exception {
        context = null;
        propertyAction = null;
        atts = null;
    }

    @org.junit.Test
    public void nameValuePair() {
        atts.setValue("name", "v1");
        atts.setValue("value", "work");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals("work", ec.getProperty("v1"));
    }

    @org.junit.Test
    public void nameValuePairWithPrerequisiteSubsitution() {
        context.putProperty("w", "wor");
        atts.setValue("name", "v1");
        atts.setValue("value", "${w}k");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals("work", ec.getProperty("v1"));
    }

    @org.junit.Test
    public void noValue() {
        atts.setValue("name", "v1");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        org.junit.Assert.assertTrue(checkError());
    }

    @org.junit.Test
    public void noName() {
        atts.setValue("value", "v1");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        org.junit.Assert.assertTrue(checkError());
    }

    @org.junit.Test
    public void noAttributes() {
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        org.junit.Assert.assertTrue(checkError());
        ch.qos.logback.core.util.StatusPrinter.print(context);
    }

    @org.junit.Test
    public void testFileNotLoaded() {
        atts.setValue("file", "toto");
        atts.setValue("value", "work");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        org.junit.Assert.assertTrue(checkError());
    }

    @org.junit.Test
    public void testLoadFileWithPrerequisiteSubsitution() {
        context.putProperty("STEM", ((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "input/joran"));
        atts.setValue("file", "${STEM}/propertyActionTest.properties");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals("tata", ec.getProperty("v1"));
        org.junit.Assert.assertEquals("toto", ec.getProperty("v2"));
    }

    @org.junit.Test
    public void testLoadFile() {
        atts.setValue("file", ((ch.qos.logback.core.util.CoreTestConstants.TEST_SRC_PREFIX) + "input/joran/propertyActionTest.properties"));
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals("tata", ec.getProperty("v1"));
        org.junit.Assert.assertEquals("toto", ec.getProperty("v2"));
    }

    @org.junit.Test
    public void testLoadResource() {
        atts.setValue("resource", "asResource/joran/propertyActionTest.properties");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals("tata", ec.getProperty("r1"));
        org.junit.Assert.assertEquals("toto", ec.getProperty("r2"));
    }

    @org.junit.Test
    public void testLoadResourceWithPrerequisiteSubsitution() {
        context.putProperty("STEM", "asResource/joran");
        atts.setValue("resource", "${STEM}/propertyActionTest.properties");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals("tata", ec.getProperty("r1"));
        org.junit.Assert.assertEquals("toto", ec.getProperty("r2"));
    }

    @org.junit.Test
    public void testLoadNotPossible() {
        atts.setValue("file", "toto");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        org.junit.Assert.assertTrue(checkFileErrors());
    }

    private boolean checkError() {
        java.util.Iterator<ch.qos.logback.core.status.Status> it = context.getStatusManager().getCopyOfStatusList().iterator();
        ch.qos.logback.core.status.ErrorStatus es = ((ch.qos.logback.core.status.ErrorStatus) (it.next()));
        return ch.qos.logback.core.joran.action.PropertyAction.INVALID_ATTRIBUTES.equals(es.getMessage());
    }

    private boolean checkFileErrors() {
        java.util.Iterator<ch.qos.logback.core.status.Status> it = context.getStatusManager().getCopyOfStatusList().iterator();
        ch.qos.logback.core.status.ErrorStatus es1 = ((ch.qos.logback.core.status.ErrorStatus) (it.next()));
        return "Could not find properties file [toto].".equals(es1.getMessage());
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#nameValuePair */
    @org.junit.Test(timeout = 10000)
    public void nameValuePair_cf51() {
        atts.setValue("name", "v1");
        atts.setValue("value", "work");
        propertyAction.begin(ec, null, atts);
        // StatementAdderOnAssert create null value
        java.lang.String vc_24 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_24);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_22 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_22);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_21 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_21.end(vc_22, vc_24);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
        org.junit.Assert.assertEquals("work", ec.getProperty("v1"));
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#nameValuePair */
    @org.junit.Test(timeout = 10000)
    public void nameValuePair_cf33_failAssert14_add121() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            atts.setValue("name", "v1");
            atts.setValue("value", "work");
            propertyAction.begin(ec, null, atts);
            // StatementAdderOnAssert create null value
            org.xml.sax.Attributes vc_18 = (org.xml.sax.Attributes)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_18);
            // StatementAdderOnAssert create null value
            java.lang.String vc_16 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_16);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_14 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_14);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_13 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_13).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_13).getContext());
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_13.begin(vc_14, vc_16, vc_18);
            // StatementAdderMethod cloned existing statement
            vc_13.begin(vc_14, vc_16, vc_18);
            // MethodAssertGenerator build local variable
            Object o_14_0 = ec.getProperty("v1");
            org.junit.Assert.fail("nameValuePair_cf33 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#nameValuePair */
    @org.junit.Test(timeout = 10000)
    public void nameValuePair_cf52_cf508_cf2940() {
        atts.setValue("name", "v1");
        atts.setValue("value", "work");
        propertyAction.begin(ec, null, atts);
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1 = "v1";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1, "v1");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1, "v1");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1, "v1");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_22 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_22);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_22);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_22);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_21 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_21.end(vc_22, String_vc_1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
        // StatementAdderOnAssert create null value
        java.lang.String vc_100 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_100);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_100);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_98 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_98);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_98);
        // StatementAdderMethod cloned existing statement
        vc_21.end(vc_98, vc_100);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_25 = "v1";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_25, "v1");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_478 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_478);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_477 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_477).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_477).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_477.end(vc_478, String_vc_25);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_477).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_477).getStatusManager());
        org.junit.Assert.assertEquals("work", ec.getProperty("v1"));
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#nameValuePair */
    @org.junit.Test(timeout = 10000)
    public void nameValuePair_cf53_cf756_cf3039_failAssert36() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            atts.setValue("name", "v1");
            atts.setValue("value", "work");
            propertyAction.begin(ec, null, atts);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_25 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_25, "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_25, "");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_22 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_22);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_22);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_21 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
            // StatementAdderMethod cloned existing statement
            vc_21.end(vc_22, vc_25);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_21).getStatusManager());
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_7 = "v1";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_7, "v1");
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_135 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_135).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_135).getContext());
            // StatementAdderMethod cloned existing statement
            vc_135.end(vc_22, String_vc_7);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_135).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_135).getContext());
            // StatementAdderOnAssert create null value
            org.xml.sax.Attributes vc_496 = (org.xml.sax.Attributes)null;
            // StatementAdderMethod cloned existing statement
            vc_21.checkFileAttributeSanity(vc_496);
            // MethodAssertGenerator build local variable
            Object o_56_0 = ec.getProperty("v1");
            org.junit.Assert.fail("nameValuePair_cf53_cf756_cf3039 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#nameValuePairWithPrerequisiteSubsitution */
    @org.junit.Test(timeout = 10000)
    public void nameValuePairWithPrerequisiteSubsitution_cf5553() {
        context.putProperty("w", "wor");
        atts.setValue("name", "v1");
        atts.setValue("value", "${w}k");
        propertyAction.begin(ec, null, atts);
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_49 = "wor";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_49, "wor");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_934 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_934);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_933 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getContext());
        // StatementAdderMethod cloned existing statement
        vc_933.end(vc_934, String_vc_49);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getContext());
        org.junit.Assert.assertEquals("work", ec.getProperty("v1"));
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#nameValuePairWithPrerequisiteSubsitution */
    @org.junit.Test(timeout = 10000)
    public void nameValuePairWithPrerequisiteSubsitution_cf5552_cf5796() {
        context.putProperty("w", "wor");
        atts.setValue("name", "v1");
        atts.setValue("value", "${w}k");
        propertyAction.begin(ec, null, atts);
        // StatementAdderOnAssert create null value
        java.lang.String vc_936 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_936);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_936);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_934 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_934);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_934);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_933 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getContext());
        // StatementAdderMethod cloned existing statement
        vc_933.end(vc_934, vc_936);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getContext());
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_972 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_972);
        // StatementAdderMethod cloned existing statement
        vc_933.end(vc_972, vc_936);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_933).getContext());
        org.junit.Assert.assertEquals("work", ec.getProperty("v1"));
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#nameValuePairWithPrerequisiteSubsitution */
    @org.junit.Test(timeout = 10000)
    public void nameValuePairWithPrerequisiteSubsitution_cf5536_failAssert15_add5631() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            context.putProperty("w", "wor");
            // MethodCallAdder
            atts.setValue("name", "v1");
            atts.setValue("name", "v1");
            atts.setValue("value", "${w}k");
            propertyAction.begin(ec, null, atts);
            // StatementAdderOnAssert create null value
            org.xml.sax.Attributes vc_930 = (org.xml.sax.Attributes)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_930);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_48 = "w";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_48, "w");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_926 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_926);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_925 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_925).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_925).getStatusManager());
            // StatementAdderMethod cloned existing statement
            vc_925.begin(vc_926, String_vc_48, vc_930);
            // MethodAssertGenerator build local variable
            Object o_15_0 = ec.getProperty("v1");
            org.junit.Assert.fail("nameValuePairWithPrerequisiteSubsitution_cf5536 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#noAttributes */
    @org.junit.Test(timeout = 10000)
    public void noAttributes_cf11989() {
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_1963 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_1963, "");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_1960 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1960);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_1959 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
        // StatementAdderMethod cloned existing statement
        vc_1959.end(vc_1960, vc_1963);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
        org.junit.Assert.assertTrue(checkError());
        ch.qos.logback.core.util.StatusPrinter.print(context);
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#noAttributes */
    @org.junit.Test(timeout = 10000)
    public void noAttributes_cf11988_cf12126_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            propertyAction.begin(ec, null, atts);
            // MethodAssertGenerator build local variable
            Object o_2_0 = context.getStatusManager().getCount();
            // StatementAdderOnAssert create null value
            java.lang.String vc_1962 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1962);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_1960 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1960);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_1959 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
            // StatementAdderMethod cloned existing statement
            vc_1959.end(vc_1960, vc_1962);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
            // StatementAdderOnAssert create null value
            org.xml.sax.Attributes vc_1994 = (org.xml.sax.Attributes)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_1993 = new java.lang.String();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_1990 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_1989 = new ch.qos.logback.core.joran.action.PropertyAction();
            // StatementAdderMethod cloned existing statement
            vc_1989.begin(vc_1990, vc_1993, vc_1994);
            // MethodAssertGenerator build local variable
            Object o_35_0 = checkError();
            ch.qos.logback.core.util.StatusPrinter.print(context);
            org.junit.Assert.fail("noAttributes_cf11988_cf12126 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#noAttributes */
    @org.junit.Test(timeout = 10000)
    public void noAttributes_cf11988_cf12184_cf14864() {
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        // StatementAdderOnAssert create null value
        java.lang.String vc_1962 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1962);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1962);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1962);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_1960 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1960);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1960);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1960);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_1959 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
        // StatementAdderMethod cloned existing statement
        vc_1959.end(vc_1960, vc_1962);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
        // StatementAdderMethod cloned existing statement
        vc_1959.finish(vc_1960);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
        // StatementAdderMethod cloned existing statement
        vc_1959.finish(vc_1960);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_1959).getContext());
        org.junit.Assert.assertTrue(checkError());
        ch.qos.logback.core.util.StatusPrinter.print(context);
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#noName */
    @org.junit.Test(timeout = 10000)
    public void noName_cf15976() {
        atts.setValue("value", "v1");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_2723 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_2723, "");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_2720 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2720);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_2719 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_2719.end(vc_2720, vc_2723);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getStatusManager());
        org.junit.Assert.assertTrue(checkError());
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#noName */
    @org.junit.Test(timeout = 10000)
    public void noName_cf15974_add16059() {
        atts.setValue("value", "v1");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        // StatementAdderOnAssert create null value
        java.lang.String vc_2722 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2722);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2722);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_2720 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2720);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2720);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_2719 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getStatusManager());
        // StatementAdderMethod cloned existing statement
        // MethodCallAdder
        vc_2719.end(vc_2720, vc_2722);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_2719.end(vc_2720, vc_2722);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2719).getStatusManager());
        org.junit.Assert.assertTrue(checkError());
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#noName */
    @org.junit.Test(timeout = 10000)
    public void noName_cf15982_cf16736_failAssert38() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            atts.setValue("value", "v1");
            propertyAction.begin(ec, null, atts);
            // MethodAssertGenerator build local variable
            Object o_3_0 = context.getStatusManager().getCount();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_2726 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2726);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_2725 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2725).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2725).getStatusManager());
            // StatementAdderMethod cloned existing statement
            vc_2725.finish(vc_2726);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2725).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_2725).getStatusManager());
            // StatementAdderOnAssert create null value
            org.xml.sax.Attributes vc_2852 = (org.xml.sax.Attributes)null;
            // StatementAdderMethod cloned existing statement
            vc_2725.checkFileAttributeSanity(vc_2852);
            // MethodAssertGenerator build local variable
            Object o_26_0 = checkError();
            org.junit.Assert.fail("noName_cf15982_cf16736 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#noValue */
    @org.junit.Test(timeout = 10000)
    public void noValue_cf21789() {
        atts.setValue("name", "v1");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_3711 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_3711, "");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_3708 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3708);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_3707 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_3707.end(vc_3708, vc_3711);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        org.junit.Assert.assertTrue(checkError());
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#noValue */
    @org.junit.Test(timeout = 10000)
    public void noValue_cf21787_cf21875_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            atts.setValue("name", "v1");
            propertyAction.begin(ec, null, atts);
            // MethodAssertGenerator build local variable
            Object o_3_0 = context.getStatusManager().getCount();
            // StatementAdderOnAssert create null value
            java.lang.String vc_3710 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3710);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_3708 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3708);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_3707 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
            // StatementAdderMethod cloned existing statement
            vc_3707.end(vc_3708, vc_3710);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
            // StatementAdderOnAssert create null value
            org.xml.sax.Attributes vc_3726 = (org.xml.sax.Attributes)null;
            // StatementAdderMethod cloned existing statement
            vc_3707.checkFileAttributeSanity(vc_3726);
            // MethodAssertGenerator build local variable
            Object o_30_0 = checkError();
            org.junit.Assert.fail("noValue_cf21787_cf21875 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#noValue */
    @org.junit.Test(timeout = 10000)
    public void noValue_cf21788_cf22232() {
        atts.setValue("name", "v1");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_155 = "name";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_155, "name");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_155, "name");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_3708 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3708);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3708);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_3707 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_3707.end(vc_3708, String_vc_155);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_3787 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_3787, "");
        // StatementAdderMethod cloned existing statement
        vc_3707.end(vc_3708, vc_3787);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        org.junit.Assert.assertTrue(checkError());
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#noValue */
    @org.junit.Test(timeout = 10000)
    public void noValue_cf21788_add22089_cf26924() {
        // MethodCallAdder
        atts.setValue("name", "v1");
        atts.setValue("name", "v1");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_155 = "name";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_155, "name");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_155, "name");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_155, "name");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_3708 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3708);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3708);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3708);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_3707 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_3707.end(vc_3708, String_vc_155);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
        // StatementAdderOnAssert create null value
        java.lang.String vc_4584 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4584);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_4582 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4582);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_4581 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4581).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4581).getContext());
        // StatementAdderMethod cloned existing statement
        vc_4581.end(vc_4582, vc_4584);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4581).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4581).getContext());
        org.junit.Assert.assertTrue(checkError());
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#noValue */
    @org.junit.Test(timeout = 10000)
    public void noValue_cf21788_cf22232_cf22757_failAssert27() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            atts.setValue("name", "v1");
            propertyAction.begin(ec, null, atts);
            // MethodAssertGenerator build local variable
            Object o_3_0 = context.getStatusManager().getCount();
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_155 = "name";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_155, "name");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_155, "name");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_3708 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3708);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3708);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_3707 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
            // StatementAdderMethod cloned existing statement
            vc_3707.end(vc_3708, String_vc_155);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_3787 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_3787, "");
            // StatementAdderMethod cloned existing statement
            vc_3707.end(vc_3708, vc_3787);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_3707).getStatusManager());
            // StatementAdderOnAssert create null value
            org.xml.sax.Attributes vc_3878 = (org.xml.sax.Attributes)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.action.PropertyAction vc_3876 = (ch.qos.logback.core.joran.action.PropertyAction)null;
            // StatementAdderMethod cloned existing statement
            vc_3876.checkFileAttributeSanity(vc_3878);
            // MethodAssertGenerator build local variable
            Object o_54_0 = checkError();
            org.junit.Assert.fail("noValue_cf21788_cf22232_cf22757 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testFileNotLoaded */
    @org.junit.Test(timeout = 10000)
    public void testFileNotLoaded_cf27055() {
        atts.setValue("file", "toto");
        atts.setValue("value", "work");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_203 = "file";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_203, "file");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_4620 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4620);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_4619 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getContext());
        // StatementAdderMethod cloned existing statement
        vc_4619.end(vc_4620, String_vc_203);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getContext());
        org.junit.Assert.assertTrue(checkError());
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testFileNotLoaded */
    @org.junit.Test(timeout = 10000)
    public void testFileNotLoaded_cf27056_add27609() {
        atts.setValue("file", "toto");
        atts.setValue("value", "work");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_4623 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_4623, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_4623, "");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_4620 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4620);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4620);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_4619 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getContext());
        // StatementAdderMethod cloned existing statement
        // MethodCallAdder
        vc_4619.end(vc_4620, vc_4623);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getContext());
        // StatementAdderMethod cloned existing statement
        vc_4619.end(vc_4620, vc_4623);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4619).getContext());
        org.junit.Assert.assertTrue(checkError());
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testFileNotLoaded */
    @org.junit.Test(timeout = 10000)
    public void testFileNotLoaded_cf27040_failAssert15_add27135() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            atts.setValue("file", "toto");
            // MethodCallAdder
            atts.setValue("value", "work");
            atts.setValue("value", "work");
            propertyAction.begin(ec, null, atts);
            // MethodAssertGenerator build local variable
            Object o_4_0 = context.getStatusManager().getCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_4_0, 1);
            // StatementAdderOnAssert create null value
            org.xml.sax.Attributes vc_4616 = (org.xml.sax.Attributes)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4616);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_4615 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_4615, "");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_4612 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4612);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_4611 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4611).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4611).getStatusManager());
            // StatementAdderMethod cloned existing statement
            vc_4611.begin(vc_4612, vc_4615, vc_4616);
            // MethodAssertGenerator build local variable
            Object o_17_0 = checkError();
            org.junit.Assert.fail("testFileNotLoaded_cf27040 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testFileNotLoaded */
    @org.junit.Test(timeout = 10000)
    public void testFileNotLoaded_cf27062_add27834_cf30422_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            atts.setValue("file", "toto");
            // MethodCallAdder
            atts.setValue("value", "work");
            atts.setValue("value", "work");
            propertyAction.begin(ec, null, atts);
            // MethodAssertGenerator build local variable
            Object o_6_0 = context.getStatusManager().getCount();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_4626 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4626);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4626);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_4625 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4625).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4625).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4625).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4625).getContext());
            // StatementAdderMethod cloned existing statement
            vc_4625.finish(vc_4626);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4625).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4625).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4625).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_4625).getContext());
            // StatementAdderOnAssert create null value
            org.xml.sax.Attributes vc_5148 = (org.xml.sax.Attributes)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_5147 = new java.lang.String();
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_5143 = new ch.qos.logback.core.joran.action.PropertyAction();
            // StatementAdderMethod cloned existing statement
            vc_5143.begin(vc_4626, vc_5147, vc_5148);
            // MethodAssertGenerator build local variable
            Object o_43_0 = checkError();
            org.junit.Assert.fail("testFileNotLoaded_cf27062_add27834_cf30422 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testLoadNotPossible */
    @org.junit.Test(timeout = 10000)
    public void testLoadNotPossible_cf31545() {
        atts.setValue("file", "toto");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_241 = "file";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_241, "file");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_5342 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5342);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_5341 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getContext());
        // StatementAdderMethod cloned existing statement
        vc_5341.end(vc_5342, String_vc_241);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getContext());
        org.junit.Assert.assertTrue(checkFileErrors());
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testLoadNotPossible */
    @org.junit.Test(timeout = 10000)
    public void testLoadNotPossible_cf31528_failAssert14_literalMutation31608() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            atts.setValue("file", "toto");
            propertyAction.begin(ec, null, atts);
            // MethodAssertGenerator build local variable
            Object o_3_0 = context.getStatusManager().getCount();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_3_0, 1);
            // StatementAdderOnAssert create null value
            org.xml.sax.Attributes vc_5338 = (org.xml.sax.Attributes)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5338);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_240 = "r2";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_240, "r2");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_5334 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5334);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_5333 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5333).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5333).getContext());
            // StatementAdderMethod cloned existing statement
            vc_5333.begin(vc_5334, String_vc_240, vc_5338);
            // MethodAssertGenerator build local variable
            Object o_16_0 = checkFileErrors();
            org.junit.Assert.fail("testLoadNotPossible_cf31528 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testLoadNotPossible */
    @org.junit.Test(timeout = 10000)
    public void testLoadNotPossible_cf31544_cf31782() {
        atts.setValue("file", "toto");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals(1, context.getStatusManager().getCount());
        // StatementAdderOnAssert create null value
        java.lang.String vc_5344 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5344);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5344);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_5342 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5342);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_5342);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_5341 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getContext());
        // StatementAdderMethod cloned existing statement
        vc_5341.end(vc_5342, vc_5344);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getContext());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_243 = "toto";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_243, "toto");
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_5379 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5379).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5379).getContext());
        // StatementAdderMethod cloned existing statement
        vc_5379.end(vc_5342, String_vc_243);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5379).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5379).getContext());
        org.junit.Assert.assertTrue(checkFileErrors());
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testLoadNotPossible */
    @org.junit.Test(timeout = 10000)
    public void testLoadNotPossible_cf31546_cf32242_cf34686_failAssert30() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            atts.setValue("file", "toto");
            propertyAction.begin(ec, null, atts);
            // MethodAssertGenerator build local variable
            Object o_3_0 = context.getStatusManager().getCount();
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_5345 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_5345, "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_5345, "");
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_5342 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5342);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_5342);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_5341 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getContext());
            // StatementAdderMethod cloned existing statement
            vc_5341.end(vc_5342, vc_5345);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getContext());
            // StatementAdderMethod cloned existing statement
            vc_5341.finish(vc_5342);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_5341).getContext());
            // StatementAdderOnAssert create null value
            org.xml.sax.Attributes vc_5870 = (org.xml.sax.Attributes)null;
            // StatementAdderOnAssert create null value
            java.lang.String vc_5868 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_5341.begin(vc_5342, vc_5868, vc_5870);
            // MethodAssertGenerator build local variable
            Object o_50_0 = checkFileErrors();
            org.junit.Assert.fail("testLoadNotPossible_cf31546_cf32242_cf34686 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testLoadResource */
    @org.junit.Test(timeout = 10000)
    public void testLoadResource_cf37040() {
        atts.setValue("resource", "asResource/joran/propertyActionTest.properties");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals("tata", ec.getProperty("r1"));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_289 = "toto";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_289, "toto");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_6254 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_6254);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_6253 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_6253).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_6253).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_6253.end(vc_6254, String_vc_289);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_6253).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_6253).getStatusManager());
        org.junit.Assert.assertEquals("toto", ec.getProperty("r2"));
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testLoadResourceWithPrerequisiteSubsitution */
    @org.junit.Test(timeout = 10000)
    public void testLoadResourceWithPrerequisiteSubsitution_cf42547() {
        context.putProperty("STEM", "asResource/joran");
        atts.setValue("resource", "${STEM}/propertyActionTest.properties");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals("tata", ec.getProperty("r1"));
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_7169 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_7169, "");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_7166 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7166);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_7165 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        // StatementAdderMethod cloned existing statement
        vc_7165.end(vc_7166, vc_7169);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        org.junit.Assert.assertEquals("toto", ec.getProperty("r2"));
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testLoadResourceWithPrerequisiteSubsitution */
    @org.junit.Test(timeout = 10000)
    public void testLoadResourceWithPrerequisiteSubsitution_cf42546_cf43036() {
        context.putProperty("STEM", "asResource/joran");
        atts.setValue("resource", "${STEM}/propertyActionTest.properties");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals("tata", ec.getProperty("r1"));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_337 = "resource";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_337, "resource");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_337, "resource");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_7166 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7166);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7166);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_7165 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        // StatementAdderMethod cloned existing statement
        vc_7165.end(vc_7166, String_vc_337);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_341 = "tata";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_341, "tata");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_7242 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7242);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_7241 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7241).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7241).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_7241.end(vc_7242, String_vc_341);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7241).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7241).getStatusManager());
        org.junit.Assert.assertEquals("toto", ec.getProperty("r2"));
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testLoadResourceWithPrerequisiteSubsitution */
    @org.junit.Test(timeout = 10000)
    public void testLoadResourceWithPrerequisiteSubsitution_cf42545_cf42740_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            context.putProperty("STEM", "asResource/joran");
            atts.setValue("resource", "${STEM}/propertyActionTest.properties");
            propertyAction.begin(ec, null, atts);
            // MethodAssertGenerator build local variable
            Object o_4_0 = ec.getProperty("r1");
            // StatementAdderOnAssert create null value
            java.lang.String vc_7168 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7168);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_7166 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7166);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_7165 = new ch.qos.logback.core.joran.action.PropertyAction();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
            // StatementAdderMethod cloned existing statement
            vc_7165.end(vc_7166, vc_7168);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
            // StatementAdderOnAssert create null value
            org.xml.sax.Attributes vc_7200 = (org.xml.sax.Attributes)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.InterpretationContext vc_7196 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.action.PropertyAction vc_7195 = new ch.qos.logback.core.joran.action.PropertyAction();
            // StatementAdderMethod cloned existing statement
            vc_7195.begin(vc_7196, vc_7168, vc_7200);
            // MethodAssertGenerator build local variable
            Object o_34_0 = ec.getProperty("r2");
            org.junit.Assert.fail("testLoadResourceWithPrerequisiteSubsitution_cf42545_cf42740 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.action.PropertyActionTest#testLoadResourceWithPrerequisiteSubsitution */
    @org.junit.Test(timeout = 10000)
    public void testLoadResourceWithPrerequisiteSubsitution_cf42546_cf43037_cf44181() {
        context.putProperty("STEM", "asResource/joran");
        atts.setValue("resource", "${STEM}/propertyActionTest.properties");
        propertyAction.begin(ec, null, atts);
        org.junit.Assert.assertEquals("tata", ec.getProperty("r1"));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_337 = "resource";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_337, "resource");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_337, "resource");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_337, "resource");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_7166 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7166);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7166);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7166);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_7165 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        // StatementAdderMethod cloned existing statement
        vc_7165.end(vc_7166, String_vc_337);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_7245 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_7245, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_7245, "");
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.joran.spi.InterpretationContext vc_7242 = (ch.qos.logback.core.joran.spi.InterpretationContext)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7242);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7242);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.action.PropertyAction vc_7241 = new ch.qos.logback.core.joran.action.PropertyAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7241).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7241).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7241).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7241).getStatusManager());
        // StatementAdderMethod cloned existing statement
        vc_7241.end(vc_7242, vc_7245);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7241).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7241).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7241).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7241).getStatusManager());
        // StatementAdderOnAssert create null value
        java.lang.String vc_7434 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7434);
        // StatementAdderMethod cloned existing statement
        vc_7165.end(vc_7166, vc_7434);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.action.PropertyAction)vc_7165).getContext());
        org.junit.Assert.assertEquals("toto", ec.getProperty("r2"));
    }
}

