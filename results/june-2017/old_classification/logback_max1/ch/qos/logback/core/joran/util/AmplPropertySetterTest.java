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


package ch.qos.logback.core.joran.util;


public class AmplPropertySetterTest {
    ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry defaultComponentRegistry = new ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry();

    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context);

    ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();

    ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);

    @org.junit.Before
    public void setUp() {
        setter.setContext(context);
    }

    @org.junit.After
    public void tearDown() {
    }

    @org.junit.Test
    public void testCanAggregateComponent() {
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY, setter.computeAggregationType("door"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("count"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("Count"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("name"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("Name"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("Duration"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("fs"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("open"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("Open"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY_COLLECTION, setter.computeAggregationType("Window"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY_COLLECTION, setter.computeAggregationType("adjective"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("filterReply"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, setter.computeAggregationType("houseColor"));
    }

    @org.junit.Test
    public void testSetProperty() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    @org.junit.Test
    public void testSetCamelProperty() {
        setter.setProperty("camelCase", "trot");
        org.junit.Assert.assertEquals("trot", house.getCamelCase());
        setter.setProperty("camelCase", "gh");
        org.junit.Assert.assertEquals("gh", house.getCamelCase());
    }

    @org.junit.Test
    public void testSetComplexProperty() {
        ch.qos.logback.core.joran.util.Door door = new ch.qos.logback.core.joran.util.Door();
        setter.setComplexProperty("door", door);
        org.junit.Assert.assertEquals(door, house.getDoor());
    }

    @org.junit.Test
    public void testgetClassNameViaImplicitRules() {
        java.lang.Class<?> compClass = setter.getClassNameViaImplicitRules("door", ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY, defaultComponentRegistry);
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.Door.class, compClass);
    }

    @org.junit.Test
    public void testgetComplexPropertyColleClassNameViaImplicitRules() {
        java.lang.Class<?> compClass = setter.getClassNameViaImplicitRules("window", ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY_COLLECTION, defaultComponentRegistry);
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.Window.class, compClass);
    }

    @org.junit.Test
    public void testPropertyCollection() {
        setter.addBasicProperty("adjective", "nice");
        setter.addBasicProperty("adjective", "big");
        org.junit.Assert.assertEquals(2, house.adjectiveList.size());
        org.junit.Assert.assertEquals("nice", house.adjectiveList.get(0));
        org.junit.Assert.assertEquals("big", house.adjectiveList.get(1));
    }

    @org.junit.Test
    public void testComplexCollection() {
        ch.qos.logback.core.joran.util.Window w1 = new ch.qos.logback.core.joran.util.Window();
        w1.handle = 10;
        ch.qos.logback.core.joran.util.Window w2 = new ch.qos.logback.core.joran.util.Window();
        w2.handle = 20;
        setter.addComplexProperty("window", w1);
        setter.addComplexProperty("window", w2);
        org.junit.Assert.assertEquals(2, house.windowList.size());
        org.junit.Assert.assertEquals(10, house.windowList.get(0).handle);
        org.junit.Assert.assertEquals(20, house.windowList.get(1).handle);
    }

    @org.junit.Test
    public void testSetComplexWithCamelCaseName() {
        ch.qos.logback.core.joran.util.SwimmingPool pool = new ch.qos.logback.core.joran.util.SwimmingPoolImpl();
        setter.setComplexProperty("swimmingPool", pool);
        org.junit.Assert.assertEquals(pool, house.getSwimmingPool());
    }

    @org.junit.Test
    public void testDuration() {
        setter.setProperty("duration", "1.4 seconds");
        org.junit.Assert.assertEquals(1400, house.getDuration().getMilliseconds());
    }

    @org.junit.Test
    public void testFileSize() {
        setter.setProperty("fs", "2 kb");
        org.junit.Assert.assertEquals((2 * 1024), house.getFs().getSize());
    }

    @org.junit.Test
    public void testFilterReply() {
        // test case reproducing bug #52
        setter.setProperty("filterReply", "ACCEPT");
        org.junit.Assert.assertEquals(ch.qos.logback.core.spi.FilterReply.ACCEPT, house.getFilterReply());
    }

    @org.junit.Test
    public void testEnum() {
        setter.setProperty("houseColor", "BLUE");
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.HouseColor.BLUE, house.getHouseColor());
    }

    @org.junit.Test
    public void testDefaultClassAnnonation() {
        java.lang.reflect.Method relevantMethod = setter.getRelevantMethod("SwimmingPool", ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY);
        org.junit.Assert.assertNotNull(relevantMethod);
        java.lang.Class<?> spClass = setter.getDefaultClassNameByAnnonation("SwimmingPool", relevantMethod);
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.SwimmingPoolImpl.class, spClass);
        java.lang.Class<?> classViaImplicitRules = setter.getClassNameViaImplicitRules("SwimmingPool", ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY, defaultComponentRegistry);
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.SwimmingPoolImpl.class, classViaImplicitRules);
    }

    @org.junit.Test
    public void testDefaultClassAnnotationForLists() {
        java.lang.reflect.Method relevantMethod = setter.getRelevantMethod("LargeSwimmingPool", ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY_COLLECTION);
        org.junit.Assert.assertNotNull(relevantMethod);
        java.lang.Class<?> spClass = setter.getDefaultClassNameByAnnonation("LargeSwimmingPool", relevantMethod);
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.LargeSwimmingPoolImpl.class, spClass);
        java.lang.Class<?> classViaImplicitRules = setter.getClassNameViaImplicitRules("LargeSwimmingPool", ch.qos.logback.core.util.AggregationType.AS_COMPLEX_PROPERTY_COLLECTION, defaultComponentRegistry);
        org.junit.Assert.assertEquals(ch.qos.logback.core.joran.util.LargeSwimmingPoolImpl.class, classViaImplicitRules);
    }

    @org.junit.Test
    public void charset() {
        setter.setProperty("charset", "UTF-8");
        org.junit.Assert.assertEquals(java.nio.charset.Charset.forName("UTF-8"), house.getCharset());
        house.setCharset(null);
        setter.setProperty("charset", "UTF");
        org.junit.Assert.assertNull(house.getCharset());
        ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context);
        checker.containsException(java.nio.charset.UnsupportedCharsetException.class);
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    @org.junit.Test
    public void bridgeMethodsShouldBeIgnored() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf54_failAssert11() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_25 = new ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.util.AggregationType vc_22 = (ch.qos.logback.core.util.AggregationType)null;
            // StatementAdderOnAssert create null value
            java.lang.String vc_20 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            orangeSetter.getClassNameViaImplicitRules(vc_20, vc_22, vc_25);
            // MethodAssertGenerator build local variable
            Object o_16_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf54 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf109() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_50 = (java.lang.Object)null;
        // StatementAdderOnAssert create null value
        java.lang.String vc_48 = (java.lang.String)null;
        // StatementAdderMethod cloned existing statement
        orangeSetter.addComplexProperty(vc_48, vc_50);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf122_failAssert29() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_57 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            java.lang.reflect.Method vc_54 = (java.lang.reflect.Method)null;
            // StatementAdderMethod cloned existing statement
            orangeSetter.invokeMethodWithSingleParameterOnThisObject(vc_54, vc_57);
            // MethodAssertGenerator build local variable
            Object o_14_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf122 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf146() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_69 = new java.lang.String();
        // StatementAdderOnAssert create null value
        java.lang.String vc_66 = (java.lang.String)null;
        // StatementAdderMethod cloned existing statement
        orangeSetter.setProperty(vc_66, vc_69);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf145() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create null value
        java.lang.String vc_68 = (java.lang.String)null;
        // StatementAdderOnAssert create null value
        java.lang.String vc_66 = (java.lang.String)null;
        // StatementAdderMethod cloned existing statement
        orangeSetter.setProperty(vc_66, vc_68);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf134() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_63 = new java.lang.Object();
        // StatementAdderOnAssert create null value
        java.lang.String vc_60 = (java.lang.String)null;
        // StatementAdderMethod cloned existing statement
        orangeSetter.setComplexProperty(vc_60, vc_63);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf37() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create null value
        java.lang.reflect.Method vc_16 = (java.lang.reflect.Method)null;
        // StatementAdderOnAssert create null value
        java.lang.String vc_14 = (java.lang.String)null;
        // AssertGenerator replace invocation
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf37__12 = // StatementAdderMethod cloned existing statement
orangeSetter.getByConcreteType(vc_14, vc_16);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf37__12);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf133() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_62 = (java.lang.Object)null;
        // StatementAdderOnAssert create null value
        java.lang.String vc_60 = (java.lang.String)null;
        // StatementAdderMethod cloned existing statement
        orangeSetter.setComplexProperty(vc_60, vc_62);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf111() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create null value
        java.lang.Object vc_50 = (java.lang.Object)null;
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_49 = new java.lang.String();
        // StatementAdderMethod cloned existing statement
        orangeSetter.addComplexProperty(vc_49, vc_50);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf110() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_51 = new java.lang.Object();
        // StatementAdderOnAssert create null value
        java.lang.String vc_48 = (java.lang.String)null;
        // StatementAdderMethod cloned existing statement
        orangeSetter.addComplexProperty(vc_48, vc_51);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName(), "Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName(), "Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName(), "ch.qos.logback.core.joran.util.Citrus");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString(), "public abstract class ch.qos.logback.core.joran.util.Citrus<T>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName(), "ch.qos.logback.core.joran.util.Citrus<java.lang.Integer>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString(), "public class ch.qos.logback.core.joran.util.Orange");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName(), "ch.qos.logback.core.joran.util");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext());
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf91_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.util.AggregationType vc_40 = (ch.qos.logback.core.util.AggregationType)null;
            // StatementAdderOnAssert create null value
            java.lang.String vc_38 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            orangeSetter.getRelevantMethod(vc_38, vc_40);
            // MethodAssertGenerator build local variable
            Object o_14_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf91 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf73() {
        ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
        ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME));
        // StatementAdderOnAssert create null value
        java.lang.reflect.Method vc_30 = (java.lang.reflect.Method)null;
        // StatementAdderOnAssert create null value
        java.lang.String vc_28 = (java.lang.String)null;
        // AssertGenerator replace invocation
        java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf73__12 = // StatementAdderMethod cloned existing statement
orangeSetter.getDefaultClassNameByAnnonation(vc_28, vc_30);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_bridgeMethodsShouldBeIgnored_cf73__12);
        org.junit.Assert.assertEquals(ch.qos.logback.core.util.AggregationType.AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME));
        ch.qos.logback.core.util.StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf29_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // StatementAdderOnAssert create null value
            java.lang.String vc_10 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            orangeSetter.computeAggregationType(vc_10);
            // MethodAssertGenerator build local variable
            Object o_12_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf29 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored_cf85 */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf85_cf1624_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // AssertGenerator replace invocation
            java.lang.Object o_bridgeMethodsShouldBeIgnored_cf85__8 = // StatementAdderMethod cloned existing statement
orangeSetter.getObj();
            // MethodAssertGenerator build local variable
            Object o_10_0 = o_bridgeMethodsShouldBeIgnored_cf85__8.equals(orange);
            // StatementAdderOnAssert create null value
            java.lang.String vc_430 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            orangeSetter.computeAggregationType(vc_430);
            // MethodAssertGenerator build local variable
            Object o_16_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf85_cf1624 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored_cf39 */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf39_cf535_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // StatementAdderOnAssert create null value
            java.lang.reflect.Method vc_16 = (java.lang.reflect.Method)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_15 = new java.lang.String();
            // AssertGenerator replace invocation
            java.lang.Class<?> o_bridgeMethodsShouldBeIgnored_cf39__12 = // StatementAdderMethod cloned existing statement
orangeSetter.getByConcreteType(vc_15, vc_16);
            // MethodAssertGenerator build local variable
            Object o_14_0 = o_bridgeMethodsShouldBeIgnored_cf39__12;
            // StatementAdderOnAssert create random local variable
            ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_165 = new ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.util.AggregationType vc_162 = (ch.qos.logback.core.util.AggregationType)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_161 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            orangeSetter.getClassNameViaImplicitRules(vc_161, vc_162, vc_165);
            // MethodAssertGenerator build local variable
            Object o_24_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf39_cf535 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored_cf145 */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf145_cf6925_failAssert33() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_122_1 = 1;
            // MethodAssertGenerator build local variable
            Object o_108_1 = 1025;
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // StatementAdderOnAssert create null value
            java.lang.String vc_68 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            java.lang.String vc_66 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            orangeSetter.setProperty(vc_66, vc_68);
            // MethodAssertGenerator build local variable
            Object o_14_0 = ((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager();
            // MethodAssertGenerator build local variable
            Object o_16_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor();
            // MethodAssertGenerator build local variable
            Object o_18_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType();
            // MethodAssertGenerator build local variable
            Object o_20_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive();
            // MethodAssertGenerator build local variable
            Object o_22_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass();
            // MethodAssertGenerator build local variable
            Object o_24_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass();
            // MethodAssertGenerator build local variable
            Object o_26_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners();
            // MethodAssertGenerator build local variable
            Object o_28_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray();
            // MethodAssertGenerator build local variable
            Object o_30_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass();
            // MethodAssertGenerator build local variable
            Object o_32_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName();
            // MethodAssertGenerator build local variable
            Object o_34_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle();
            // MethodAssertGenerator build local variable
            Object o_36_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass();
            // MethodAssertGenerator build local variable
            Object o_38_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum();
            // MethodAssertGenerator build local variable
            Object o_40_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName();
            // MethodAssertGenerator build local variable
            Object o_42_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor();
            // MethodAssertGenerator build local variable
            Object o_44_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion();
            // MethodAssertGenerator build local variable
            Object o_46_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus();
            // MethodAssertGenerator build local variable
            Object o_48_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor();
            // MethodAssertGenerator build local variable
            Object o_50_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants();
            // MethodAssertGenerator build local variable
            Object o_52_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners();
            // MethodAssertGenerator build local variable
            Object o_54_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass();
            // MethodAssertGenerator build local variable
            Object o_56_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName();
            // MethodAssertGenerator build local variable
            Object o_58_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface();
            // MethodAssertGenerator build local variable
            Object o_60_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface();
            // MethodAssertGenerator build local variable
            Object o_62_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic();
            // MethodAssertGenerator build local variable
            Object o_64_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic();
            // MethodAssertGenerator build local variable
            Object o_66_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName();
            // MethodAssertGenerator build local variable
            Object o_68_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants();
            // MethodAssertGenerator build local variable
            Object o_70_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor();
            // MethodAssertGenerator build local variable
            Object o_72_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass();
            // MethodAssertGenerator build local variable
            Object o_74_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass();
            // MethodAssertGenerator build local variable
            Object o_76_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion();
            // MethodAssertGenerator build local variable
            Object o_78_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive();
            // MethodAssertGenerator build local variable
            Object o_80_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed();
            // MethodAssertGenerator build local variable
            Object o_82_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass();
            // MethodAssertGenerator build local variable
            Object o_84_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray();
            // MethodAssertGenerator build local variable
            Object o_86_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName();
            // MethodAssertGenerator build local variable
            Object o_88_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass();
            // MethodAssertGenerator build local variable
            Object o_90_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod();
            // MethodAssertGenerator build local variable
            Object o_92_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation();
            // MethodAssertGenerator build local variable
            Object o_94_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType();
            // MethodAssertGenerator build local variable
            Object o_96_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass();
            // MethodAssertGenerator build local variable
            Object o_98_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName();
            // MethodAssertGenerator build local variable
            Object o_100_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod();
            // MethodAssertGenerator build local variable
            Object o_102_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation();
            // MethodAssertGenerator build local variable
            Object o_104_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle();
            // MethodAssertGenerator build local variable
            Object o_106_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus();
            // MethodAssertGenerator build local variable
            Object o_108_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers();
            // MethodAssertGenerator build local variable
            Object o_110_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName();
            // MethodAssertGenerator build local variable
            Object o_112_0 = ((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange);
            // MethodAssertGenerator build local variable
            Object o_114_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName();
            // MethodAssertGenerator build local variable
            Object o_116_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString();
            // MethodAssertGenerator build local variable
            Object o_118_0 = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName();
            // MethodAssertGenerator build local variable
            Object o_120_0 = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType();
            // MethodAssertGenerator build local variable
            Object o_122_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers();
            // MethodAssertGenerator build local variable
            Object o_124_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString();
            // MethodAssertGenerator build local variable
            Object o_126_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName();
            // MethodAssertGenerator build local variable
            Object o_128_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum();
            // MethodAssertGenerator build local variable
            Object o_130_0 = ((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.util.AggregationType vc_950 = (ch.qos.logback.core.util.AggregationType)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_949 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            orangeSetter.getRelevantMethod(vc_949, vc_950);
            // MethodAssertGenerator build local variable
            Object o_138_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf145_cf6925 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored_cf147 */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf147_cf7714_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_122_1 = 1;
            // MethodAssertGenerator build local variable
            Object o_108_1 = 1025;
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // StatementAdderOnAssert create null value
            java.lang.String vc_68 = (java.lang.String)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_67 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            orangeSetter.setProperty(vc_67, vc_68);
            // MethodAssertGenerator build local variable
            Object o_14_0 = ((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager();
            // MethodAssertGenerator build local variable
            Object o_16_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor();
            // MethodAssertGenerator build local variable
            Object o_18_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType();
            // MethodAssertGenerator build local variable
            Object o_20_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive();
            // MethodAssertGenerator build local variable
            Object o_22_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass();
            // MethodAssertGenerator build local variable
            Object o_24_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass();
            // MethodAssertGenerator build local variable
            Object o_26_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners();
            // MethodAssertGenerator build local variable
            Object o_28_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray();
            // MethodAssertGenerator build local variable
            Object o_30_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass();
            // MethodAssertGenerator build local variable
            Object o_32_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName();
            // MethodAssertGenerator build local variable
            Object o_34_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle();
            // MethodAssertGenerator build local variable
            Object o_36_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass();
            // MethodAssertGenerator build local variable
            Object o_38_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum();
            // MethodAssertGenerator build local variable
            Object o_40_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName();
            // MethodAssertGenerator build local variable
            Object o_42_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor();
            // MethodAssertGenerator build local variable
            Object o_44_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion();
            // MethodAssertGenerator build local variable
            Object o_46_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus();
            // MethodAssertGenerator build local variable
            Object o_48_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor();
            // MethodAssertGenerator build local variable
            Object o_50_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants();
            // MethodAssertGenerator build local variable
            Object o_52_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners();
            // MethodAssertGenerator build local variable
            Object o_54_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass();
            // MethodAssertGenerator build local variable
            Object o_56_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName();
            // MethodAssertGenerator build local variable
            Object o_58_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface();
            // MethodAssertGenerator build local variable
            Object o_60_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface();
            // MethodAssertGenerator build local variable
            Object o_62_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic();
            // MethodAssertGenerator build local variable
            Object o_64_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic();
            // MethodAssertGenerator build local variable
            Object o_66_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName();
            // MethodAssertGenerator build local variable
            Object o_68_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants();
            // MethodAssertGenerator build local variable
            Object o_70_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor();
            // MethodAssertGenerator build local variable
            Object o_72_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass();
            // MethodAssertGenerator build local variable
            Object o_74_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass();
            // MethodAssertGenerator build local variable
            Object o_76_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion();
            // MethodAssertGenerator build local variable
            Object o_78_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive();
            // MethodAssertGenerator build local variable
            Object o_80_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed();
            // MethodAssertGenerator build local variable
            Object o_82_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass();
            // MethodAssertGenerator build local variable
            Object o_84_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray();
            // MethodAssertGenerator build local variable
            Object o_86_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName();
            // MethodAssertGenerator build local variable
            Object o_88_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass();
            // MethodAssertGenerator build local variable
            Object o_90_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod();
            // MethodAssertGenerator build local variable
            Object o_92_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation();
            // MethodAssertGenerator build local variable
            Object o_94_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType();
            // MethodAssertGenerator build local variable
            Object o_96_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass();
            // MethodAssertGenerator build local variable
            Object o_98_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName();
            // MethodAssertGenerator build local variable
            Object o_100_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod();
            // MethodAssertGenerator build local variable
            Object o_102_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation();
            // MethodAssertGenerator build local variable
            Object o_104_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle();
            // MethodAssertGenerator build local variable
            Object o_106_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus();
            // MethodAssertGenerator build local variable
            Object o_108_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers();
            // MethodAssertGenerator build local variable
            Object o_110_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName();
            // MethodAssertGenerator build local variable
            Object o_112_0 = ((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange);
            // MethodAssertGenerator build local variable
            Object o_114_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName();
            // MethodAssertGenerator build local variable
            Object o_116_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString();
            // MethodAssertGenerator build local variable
            Object o_118_0 = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName();
            // MethodAssertGenerator build local variable
            Object o_120_0 = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType();
            // MethodAssertGenerator build local variable
            Object o_122_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers();
            // MethodAssertGenerator build local variable
            Object o_124_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString();
            // MethodAssertGenerator build local variable
            Object o_126_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName();
            // MethodAssertGenerator build local variable
            Object o_128_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum();
            // MethodAssertGenerator build local variable
            Object o_130_0 = ((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext();
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_1107 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            java.lang.reflect.Method vc_1104 = (java.lang.reflect.Method)null;
            // StatementAdderMethod cloned existing statement
            orangeSetter.invokeMethodWithSingleParameterOnThisObject(vc_1104, vc_1107);
            // MethodAssertGenerator build local variable
            Object o_138_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf147_cf7714 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored_cf133 */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf133_cf4764_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_122_1 = 1;
            // MethodAssertGenerator build local variable
            Object o_108_1 = 1025;
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_62 = (java.lang.Object)null;
            // StatementAdderOnAssert create null value
            java.lang.String vc_60 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            orangeSetter.setComplexProperty(vc_60, vc_62);
            // MethodAssertGenerator build local variable
            Object o_14_0 = ((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getStatusManager();
            // MethodAssertGenerator build local variable
            Object o_16_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingConstructor();
            // MethodAssertGenerator build local variable
            Object o_18_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getComponentType();
            // MethodAssertGenerator build local variable
            Object o_20_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isPrimitive();
            // MethodAssertGenerator build local variable
            Object o_22_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isLocalClass();
            // MethodAssertGenerator build local variable
            Object o_24_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isMemberClass();
            // MethodAssertGenerator build local variable
            Object o_26_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSigners();
            // MethodAssertGenerator build local variable
            Object o_28_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isArray();
            // MethodAssertGenerator build local variable
            Object o_30_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingClass();
            // MethodAssertGenerator build local variable
            Object o_32_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getName();
            // MethodAssertGenerator build local variable
            Object o_34_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationTitle();
            // MethodAssertGenerator build local variable
            Object o_36_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getDeclaringClass();
            // MethodAssertGenerator build local variable
            Object o_38_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isEnum();
            // MethodAssertGenerator build local variable
            Object o_40_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getTypeName();
            // MethodAssertGenerator build local variable
            Object o_42_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingConstructor();
            // MethodAssertGenerator build local variable
            Object o_44_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVersion();
            // MethodAssertGenerator build local variable
            Object o_46_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).desiredAssertionStatus();
            // MethodAssertGenerator build local variable
            Object o_48_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVendor();
            // MethodAssertGenerator build local variable
            Object o_50_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnumConstants();
            // MethodAssertGenerator build local variable
            Object o_52_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSigners();
            // MethodAssertGenerator build local variable
            Object o_54_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnonymousClass();
            // MethodAssertGenerator build local variable
            Object o_56_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSimpleName();
            // MethodAssertGenerator build local variable
            Object o_58_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isInterface();
            // MethodAssertGenerator build local variable
            Object o_60_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isInterface();
            // MethodAssertGenerator build local variable
            Object o_62_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isSynthetic();
            // MethodAssertGenerator build local variable
            Object o_64_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isSynthetic();
            // MethodAssertGenerator build local variable
            Object o_66_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getSimpleName();
            // MethodAssertGenerator build local variable
            Object o_68_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnumConstants();
            // MethodAssertGenerator build local variable
            Object o_70_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getSpecificationVendor();
            // MethodAssertGenerator build local variable
            Object o_72_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnonymousClass();
            // MethodAssertGenerator build local variable
            Object o_74_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isLocalClass();
            // MethodAssertGenerator build local variable
            Object o_76_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationVersion();
            // MethodAssertGenerator build local variable
            Object o_78_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isPrimitive();
            // MethodAssertGenerator build local variable
            Object o_80_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).isSealed();
            // MethodAssertGenerator build local variable
            Object o_82_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isMemberClass();
            // MethodAssertGenerator build local variable
            Object o_84_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isArray();
            // MethodAssertGenerator build local variable
            Object o_86_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getName();
            // MethodAssertGenerator build local variable
            Object o_88_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getDeclaringClass();
            // MethodAssertGenerator build local variable
            Object o_90_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getEnclosingMethod();
            // MethodAssertGenerator build local variable
            Object o_92_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).isAnnotation();
            // MethodAssertGenerator build local variable
            Object o_94_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getComponentType();
            // MethodAssertGenerator build local variable
            Object o_96_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingClass();
            // MethodAssertGenerator build local variable
            Object o_98_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getTypeName();
            // MethodAssertGenerator build local variable
            Object o_100_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getEnclosingMethod();
            // MethodAssertGenerator build local variable
            Object o_102_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isAnnotation();
            // MethodAssertGenerator build local variable
            Object o_104_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getImplementationTitle();
            // MethodAssertGenerator build local variable
            Object o_106_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).desiredAssertionStatus();
            // MethodAssertGenerator build local variable
            Object o_108_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getModifiers();
            // MethodAssertGenerator build local variable
            Object o_110_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getCanonicalName();
            // MethodAssertGenerator build local variable
            Object o_112_0 = ((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObj().equals(orange);
            // MethodAssertGenerator build local variable
            Object o_114_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).getCanonicalName();
            // MethodAssertGenerator build local variable
            Object o_116_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).toGenericString();
            // MethodAssertGenerator build local variable
            Object o_118_0 = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getTypeName();
            // MethodAssertGenerator build local variable
            Object o_120_0 = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getGenericSuperclass()).getOwnerType();
            // MethodAssertGenerator build local variable
            Object o_122_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getModifiers();
            // MethodAssertGenerator build local variable
            Object o_124_0 = ((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).toGenericString();
            // MethodAssertGenerator build local variable
            Object o_126_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getPackage()).getName();
            // MethodAssertGenerator build local variable
            Object o_128_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getObjClass()).getSuperclass()).isEnum();
            // MethodAssertGenerator build local variable
            Object o_130_0 = ((ch.qos.logback.core.joran.util.PropertySetter)orangeSetter).getContext();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.util.PropertySetter vc_734 = (ch.qos.logback.core.joran.util.PropertySetter)null;
            // StatementAdderMethod cloned existing statement
            vc_734.getObj();
            // MethodAssertGenerator build local variable
            Object o_136_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf133_cf4764 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored_cf53 */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf53_failAssert10_add8194() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
            ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
            // MethodAssertGenerator build local variable
            Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getModifiers(), 1025);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getSpecificationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getGenericSuperclass()).getOwnerType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getCanonicalName(), "ch.qos.logback.core.util.AggregationType");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getImplementationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getSpecificationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).toGenericString(), "public abstract class java.lang.Enum<E>");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getSpecificationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getTypeName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getImplementationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getCanonicalName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.util.AggregationType)o_6_0).ordinal(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getName(), "ch.qos.logback.core.util");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSimpleName(), "AggregationType");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getTypeName(), "ch.qos.logback.core.util.AggregationType");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getImplementationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getGenericSuperclass()).getTypeName(), "java.lang.Enum<ch.qos.logback.core.util.AggregationType>");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).toGenericString(), "public final enum ch.qos.logback.core.util.AggregationType");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.util.AggregationType)o_6_0).name(), "AS_BASIC_PROPERTY");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getName(), "ch.qos.logback.core.util.AggregationType");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getSimpleName(), "Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isArray());
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_24 = (ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.util.AggregationType vc_22 = (ch.qos.logback.core.util.AggregationType)null;
            // StatementAdderOnAssert create null value
            java.lang.String vc_20 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            orangeSetter.getClassNameViaImplicitRules(vc_20, vc_22, vc_24);
            // MethodAssertGenerator build local variable
            Object o_16_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
            ch.qos.logback.core.util.StatusPrinter.print(context);
            // MethodCallAdder
            checker.assertIsWarningOrErrorFree();
            checker.assertIsWarningOrErrorFree();
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf53 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored_cf53 */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf53_failAssert10_add8194_cf9358_failAssert36() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_69_1 = 1;
                // MethodAssertGenerator build local variable
                Object o_33_1 = 16401;
                // MethodAssertGenerator build local variable
                Object o_23_1 = 1025;
                ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
                ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
                // MethodAssertGenerator build local variable
                Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
                // MethodAssertGenerator build local variable
                Object o_11_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isArray();
                // MethodAssertGenerator build local variable
                Object o_15_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_17_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_19_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_21_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_23_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_25_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_27_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getSpecificationVersion();
                // MethodAssertGenerator build local variable
                Object o_29_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_31_0 = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getGenericSuperclass()).getOwnerType();
                // MethodAssertGenerator build local variable
                Object o_33_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_35_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_37_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_39_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getImplementationVendor();
                // MethodAssertGenerator build local variable
                Object o_41_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_43_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_45_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_47_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getSpecificationVendor();
                // MethodAssertGenerator build local variable
                Object o_49_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_51_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_53_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getSpecificationTitle();
                // MethodAssertGenerator build local variable
                Object o_55_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_57_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getImplementationVersion();
                // MethodAssertGenerator build local variable
                Object o_59_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_61_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).isSealed();
                // MethodAssertGenerator build local variable
                Object o_63_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_65_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_67_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_69_0 = ((ch.qos.logback.core.util.AggregationType)o_6_0).ordinal();
                // MethodAssertGenerator build local variable
                Object o_71_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getName();
                // MethodAssertGenerator build local variable
                Object o_73_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_75_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_77_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_79_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_81_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_83_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_85_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_87_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_89_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_91_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_93_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_95_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getImplementationTitle();
                // MethodAssertGenerator build local variable
                Object o_97_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_99_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_101_0 = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getGenericSuperclass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_103_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_105_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_107_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_109_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_111_0 = ((ch.qos.logback.core.util.AggregationType)o_6_0).name();
                // MethodAssertGenerator build local variable
                Object o_113_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getName();
                // MethodAssertGenerator build local variable
                Object o_115_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_117_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_119_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_121_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_123_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getName();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.util.AggregationType vc_1440 = (ch.qos.logback.core.util.AggregationType)null;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_159 = "ch.qos.logback.core.util";
                // StatementAdderMethod cloned existing statement
                orangeSetter.getRelevantMethod(String_vc_159, vc_1440);
                // MethodAssertGenerator build local variable
                Object o_131_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isArray();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_24 = (ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.util.AggregationType vc_22 = (ch.qos.logback.core.util.AggregationType)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_20 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                orangeSetter.getClassNameViaImplicitRules(vc_20, vc_22, vc_24);
                // MethodAssertGenerator build local variable
                Object o_16_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
                ch.qos.logback.core.util.StatusPrinter.print(context);
                // MethodCallAdder
                checker.assertIsWarningOrErrorFree();
                checker.assertIsWarningOrErrorFree();
                org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf53 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf53_failAssert10_add8194_cf9358 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored */
    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#bridgeMethodsShouldBeIgnored_cf53 */
    @org.junit.Test(timeout = 10000)
    public void bridgeMethodsShouldBeIgnored_cf53_failAssert10_add8194_cf9268_failAssert40() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_69_1 = 1;
                // MethodAssertGenerator build local variable
                Object o_33_1 = 16401;
                // MethodAssertGenerator build local variable
                Object o_23_1 = 1025;
                ch.qos.logback.core.joran.util.Orange orange = new ch.qos.logback.core.joran.util.Orange();
                ch.qos.logback.core.joran.util.PropertySetter orangeSetter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
                // MethodAssertGenerator build local variable
                Object o_6_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PRECARP_PROPERTY_NAME);
                // MethodAssertGenerator build local variable
                Object o_11_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isArray();
                // MethodAssertGenerator build local variable
                Object o_15_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_17_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_19_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_21_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_23_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_25_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_27_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getSpecificationVersion();
                // MethodAssertGenerator build local variable
                Object o_29_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_31_0 = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getGenericSuperclass()).getOwnerType();
                // MethodAssertGenerator build local variable
                Object o_33_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_35_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_37_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_39_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getImplementationVendor();
                // MethodAssertGenerator build local variable
                Object o_41_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_43_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_45_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_47_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getSpecificationVendor();
                // MethodAssertGenerator build local variable
                Object o_49_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_51_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_53_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getSpecificationTitle();
                // MethodAssertGenerator build local variable
                Object o_55_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_57_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getImplementationVersion();
                // MethodAssertGenerator build local variable
                Object o_59_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_61_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).isSealed();
                // MethodAssertGenerator build local variable
                Object o_63_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_65_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_67_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_69_0 = ((ch.qos.logback.core.util.AggregationType)o_6_0).ordinal();
                // MethodAssertGenerator build local variable
                Object o_71_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getName();
                // MethodAssertGenerator build local variable
                Object o_73_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_75_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_77_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_79_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_81_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_83_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_85_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_87_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_89_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_91_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_93_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_95_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getPackage()).getImplementationTitle();
                // MethodAssertGenerator build local variable
                Object o_97_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_99_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_101_0 = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getGenericSuperclass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_103_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_105_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_107_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_109_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_111_0 = ((ch.qos.logback.core.util.AggregationType)o_6_0).name();
                // MethodAssertGenerator build local variable
                Object o_113_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getName();
                // MethodAssertGenerator build local variable
                Object o_115_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_117_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_119_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_121_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_123_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).getName();
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_1411 = new java.lang.String();
                // StatementAdderMethod cloned existing statement
                orangeSetter.computeAggregationType(vc_1411);
                // MethodAssertGenerator build local variable
                Object o_129_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_6_0).getDeclaringClass()).getSuperclass()).isArray();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_24 = (ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.util.AggregationType vc_22 = (ch.qos.logback.core.util.AggregationType)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_20 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                orangeSetter.getClassNameViaImplicitRules(vc_20, vc_22, vc_24);
                // MethodAssertGenerator build local variable
                Object o_16_0 = orangeSetter.computeAggregationType(ch.qos.logback.core.joran.util.Citrus.PREFIX_PROPERTY_NAME);
                ch.qos.logback.core.util.StatusPrinter.print(context);
                // MethodCallAdder
                checker.assertIsWarningOrErrorFree();
                checker.assertIsWarningOrErrorFree();
                org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf53 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("bridgeMethodsShouldBeIgnored_cf53_failAssert10_add8194_cf9268 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#testSetProperty */
    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf93128() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            // StatementAdderOnAssert create null value
            java.lang.reflect.Method vc_22360 = (java.lang.reflect.Method)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_22360);
            // StatementAdderOnAssert create null value
            java.lang.String vc_22358 = (java.lang.String)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_22358);
            // AssertGenerator replace invocation
            java.lang.Class<?> o_testSetProperty_cf93128__36 = // StatementAdderMethod cloned existing statement
setter.getDefaultClassNameByAnnonation(vc_22358, vc_22360);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testSetProperty_cf93128__36);
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#testSetProperty */
    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf93063() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_3444 = "true";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_3444, "true");
            // AssertGenerator replace invocation
            ch.qos.logback.core.util.AggregationType o_testSetProperty_cf93063__34 = // StatementAdderMethod cloned existing statement
setter.computeAggregationType(String_vc_3444);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).toGenericString(), "public final enum ch.qos.logback.core.util.AggregationType");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getCanonicalName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getImplementationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getSpecificationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getTypeName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getTypeName(), "ch.qos.logback.core.util.AggregationType");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getImplementationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getSpecificationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getSimpleName(), "Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).name(), "NOT_FOUND");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).toGenericString(), "public abstract class java.lang.Enum<E>");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getSpecificationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSimpleName(), "AggregationType");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getGenericSuperclass()).getOwnerType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getName(), "ch.qos.logback.core.util");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getName(), "ch.qos.logback.core.util.AggregationType");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getGenericSuperclass()).getTypeName(), "java.lang.Enum<ch.qos.logback.core.util.AggregationType>");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getCanonicalName(), "ch.qos.logback.core.util.AggregationType");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getModifiers(), 1025);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getImplementationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getDeclaringClass());
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#testSetProperty */
    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf93078() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            // StatementAdderOnAssert create null value
            java.lang.reflect.Method vc_22346 = (java.lang.reflect.Method)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_22346);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_22345 = new java.lang.String();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_22345, "");
            // AssertGenerator replace invocation
            java.lang.Class<?> o_testSetProperty_cf93078__36 = // StatementAdderMethod cloned existing statement
setter.getByConcreteType(vc_22345, vc_22346);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testSetProperty_cf93078__36);
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#testSetProperty */
    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf93106_failAssert45() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                // MethodAssertGenerator build local variable
                Object o_11_0 = house.getCount();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((double) (house.getTemperature()));
                // MethodAssertGenerator build local variable
                Object o_15_0 = house.getName();
                // MethodAssertGenerator build local variable
                Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                // MethodAssertGenerator build local variable
                Object o_28_0 = house.getCount();
                // MethodAssertGenerator build local variable
                Object o_30_0 = house.getName();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_22354 = (ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.util.AggregationType vc_22352 = (ch.qos.logback.core.util.AggregationType)null;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_22351 = new java.lang.String();
                // StatementAdderMethod cloned existing statement
                setter.getClassNameViaImplicitRules(vc_22351, vc_22352, vc_22354);
                // MethodAssertGenerator build local variable
                Object o_40_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf93106 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#testSetProperty */
    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf93144() {
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            org.junit.Assert.assertEquals("jack", house.getName());
            org.junit.Assert.assertTrue(house.isOpen());
        }
        {
            ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
            ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            org.junit.Assert.assertEquals(10, house.getCount());
            org.junit.Assert.assertEquals("jack", house.getName());
            // AssertGenerator replace invocation
            java.lang.Object o_testSetProperty_cf93144__32 = // StatementAdderMethod cloned existing statement
setter.getObj();
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.util.House)o_testSetProperty_cf93144__32).isOpen());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testSetProperty_cf93144__32.equals(house));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House)o_testSetProperty_cf93144__32).getSwimmingPool());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House)o_testSetProperty_cf93144__32).getCharset());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House)o_testSetProperty_cf93144__32).getDoor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House)o_testSetProperty_cf93144__32).getCamelCase());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House)o_testSetProperty_cf93144__32).getDuration());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House)o_testSetProperty_cf93144__32).getHouseColor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House)o_testSetProperty_cf93144__32).getTemperature());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House)o_testSetProperty_cf93144__32).getFilterReply());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.util.House)o_testSetProperty_cf93144__32).getCount(), 10);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.joran.util.House)o_testSetProperty_cf93144__32).getFs());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.util.House)o_testSetProperty_cf93144__32).getName(), "jack");
            org.junit.Assert.assertTrue(house.isOpen());
        }
    }

    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#testSetProperty */
    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf93152_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                // MethodAssertGenerator build local variable
                Object o_11_0 = house.getCount();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((double) (house.getTemperature()));
                // MethodAssertGenerator build local variable
                Object o_15_0 = house.getName();
                // MethodAssertGenerator build local variable
                Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                // MethodAssertGenerator build local variable
                Object o_28_0 = house.getCount();
                // MethodAssertGenerator build local variable
                Object o_30_0 = house.getName();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.util.AggregationType vc_22370 = (ch.qos.logback.core.util.AggregationType)null;
                // StatementAdderOnAssert create null value
                java.lang.String vc_22368 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                setter.getRelevantMethod(vc_22368, vc_22370);
                // MethodAssertGenerator build local variable
                Object o_38_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf93152 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#testSetProperty */
    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf93062_failAssert12() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                // MethodAssertGenerator build local variable
                Object o_11_0 = house.getCount();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((double) (house.getTemperature()));
                // MethodAssertGenerator build local variable
                Object o_15_0 = house.getName();
                // MethodAssertGenerator build local variable
                Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                // MethodAssertGenerator build local variable
                Object o_28_0 = house.getCount();
                // MethodAssertGenerator build local variable
                Object o_30_0 = house.getName();
                // StatementAdderOnAssert create null value
                java.lang.String vc_22340 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                setter.computeAggregationType(vc_22340);
                // MethodAssertGenerator build local variable
                Object o_36_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf93062 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#testSetProperty */
    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf93078_cf94920_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                // MethodAssertGenerator build local variable
                Object o_11_0 = house.getCount();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((double) (house.getTemperature()));
                // MethodAssertGenerator build local variable
                Object o_15_0 = house.getName();
                // MethodAssertGenerator build local variable
                Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                // MethodAssertGenerator build local variable
                Object o_28_0 = house.getCount();
                // MethodAssertGenerator build local variable
                Object o_30_0 = house.getName();
                // StatementAdderOnAssert create null value
                java.lang.reflect.Method vc_22346 = (java.lang.reflect.Method)null;
                // MethodAssertGenerator build local variable
                Object o_34_0 = vc_22346;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_22345 = new java.lang.String();
                // MethodAssertGenerator build local variable
                Object o_38_0 = vc_22345;
                // AssertGenerator replace invocation
                java.lang.Class<?> o_testSetProperty_cf93078__36 = // StatementAdderMethod cloned existing statement
setter.getByConcreteType(vc_22345, vc_22346);
                // MethodAssertGenerator build local variable
                Object o_42_0 = o_testSetProperty_cf93078__36;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry vc_22704 = (ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.util.AggregationType vc_22702 = (ch.qos.logback.core.util.AggregationType)null;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_3501 = "10";
                // StatementAdderMethod cloned existing statement
                setter.getClassNameViaImplicitRules(String_vc_3501, vc_22702, vc_22704);
                // MethodAssertGenerator build local variable
                Object o_52_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf93078_cf94920 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#testSetProperty */
    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf93076_cf94599_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("count", "10");
                setter.setProperty("temperature", "33.1");
                setter.setProperty("name", "jack");
                setter.setProperty("open", "true");
                // MethodAssertGenerator build local variable
                Object o_11_0 = house.getCount();
                // MethodAssertGenerator build local variable
                Object o_13_0 = ((double) (house.getTemperature()));
                // MethodAssertGenerator build local variable
                Object o_15_0 = house.getName();
                // MethodAssertGenerator build local variable
                Object o_17_0 = house.isOpen();
            }
            {
                ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                setter.setProperty("Count", "10");
                setter.setProperty("Name", "jack");
                setter.setProperty("Open", "true");
                // MethodAssertGenerator build local variable
                Object o_28_0 = house.getCount();
                // MethodAssertGenerator build local variable
                Object o_30_0 = house.getName();
                // StatementAdderOnAssert create null value
                java.lang.reflect.Method vc_22346 = (java.lang.reflect.Method)null;
                // MethodAssertGenerator build local variable
                Object o_34_0 = vc_22346;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_3445 = "temperature";
                // MethodAssertGenerator build local variable
                Object o_38_0 = String_vc_3445;
                // AssertGenerator replace invocation
                java.lang.Class<?> o_testSetProperty_cf93076__36 = // StatementAdderMethod cloned existing statement
setter.getByConcreteType(String_vc_3445, vc_22346);
                // MethodAssertGenerator build local variable
                Object o_42_0 = o_testSetProperty_cf93076__36;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.util.AggregationType vc_22650 = (ch.qos.logback.core.util.AggregationType)null;
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_3492 = "33.1";
                // StatementAdderMethod cloned existing statement
                setter.getRelevantMethod(String_vc_3492, vc_22650);
                // MethodAssertGenerator build local variable
                Object o_50_0 = house.isOpen();
            }
            org.junit.Assert.fail("testSetProperty_cf93076_cf94599 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.util.PropertySetterTest#testSetProperty */
    @org.junit.Test(timeout = 10000)
    public void testSetProperty_cf93063_cf93897_failAssert9_literalMutation96028_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                    ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                    setter.setProperty("count", "10");
                    setter.setProperty("temperature", "33.1");
                    setter.setProperty("name", "jack");
                    setter.setProperty("open", "true");
                    // MethodAssertGenerator build local variable
                    Object o_11_0 = house.getCount();
                    // MethodAssertGenerator build local variable
                    Object o_13_0 = ((double) (house.getTemperature()));
                    // MethodAssertGenerator build local variable
                    Object o_15_0 = house.getName();
                    // MethodAssertGenerator build local variable
                    Object o_17_0 = house.isOpen();
                }
                {
                    // MethodAssertGenerator build local variable
                    Object o_146_1 = 1025;
                    // MethodAssertGenerator build local variable
                    Object o_110_1 = 0;
                    // MethodAssertGenerator build local variable
                    Object o_46_1 = 16401;
                    ch.qos.logback.core.joran.util.House house = new ch.qos.logback.core.joran.util.House();
                    ch.qos.logback.core.joran.util.PropertySetter setter = new ch.qos.logback.core.joran.util.PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
                    setter.setProperty("Count", "10");
                    setter.setProperty("Name", "jack");
                    setter.setProperty("Open", "true");
                    // MethodAssertGenerator build local variable
                    Object o_28_0 = house.getCount();
                    // MethodAssertGenerator build local variable
                    Object o_30_0 = house.getName();
                    // StatementAdderOnAssert create literal from method
                    java.lang.String String_vc_3444 = "";
                    // MethodAssertGenerator build local variable
                    Object o_34_0 = String_vc_3444;
                    // AssertGenerator replace invocation
                    ch.qos.logback.core.util.AggregationType o_testSetProperty_cf93063__34 = // StatementAdderMethod cloned existing statement
setter.computeAggregationType(String_vc_3444);
                    // MethodAssertGenerator build local variable
                    Object o_38_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).toGenericString();
                    // MethodAssertGenerator build local variable
                    Object o_40_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getEnclosingClass();
                    // MethodAssertGenerator build local variable
                    Object o_42_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getCanonicalName();
                    // MethodAssertGenerator build local variable
                    Object o_44_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isAnonymousClass();
                    // MethodAssertGenerator build local variable
                    Object o_46_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getModifiers();
                    // MethodAssertGenerator build local variable
                    Object o_48_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getEnclosingMethod();
                    // MethodAssertGenerator build local variable
                    Object o_50_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getEnumConstants();
                    // MethodAssertGenerator build local variable
                    Object o_52_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).desiredAssertionStatus();
                    // MethodAssertGenerator build local variable
                    Object o_54_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getEnclosingMethod();
                    // MethodAssertGenerator build local variable
                    Object o_56_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isArray();
                    // MethodAssertGenerator build local variable
                    Object o_58_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getImplementationVersion();
                    // MethodAssertGenerator build local variable
                    Object o_60_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isLocalClass();
                    // MethodAssertGenerator build local variable
                    Object o_62_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isLocalClass();
                    // MethodAssertGenerator build local variable
                    Object o_64_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getSpecificationVersion();
                    // MethodAssertGenerator build local variable
                    Object o_66_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getClassLoader();
                    // MethodAssertGenerator build local variable
                    Object o_68_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getTypeName();
                    // MethodAssertGenerator build local variable
                    Object o_70_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getTypeName();
                    // MethodAssertGenerator build local variable
                    Object o_72_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getImplementationVendor();
                    // MethodAssertGenerator build local variable
                    Object o_74_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getEnclosingConstructor();
                    // MethodAssertGenerator build local variable
                    Object o_76_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getEnclosingClass();
                    // MethodAssertGenerator build local variable
                    Object o_78_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getName();
                    // MethodAssertGenerator build local variable
                    Object o_80_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).isSealed();
                    // MethodAssertGenerator build local variable
                    Object o_82_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getSpecificationVendor();
                    // MethodAssertGenerator build local variable
                    Object o_84_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSigners();
                    // MethodAssertGenerator build local variable
                    Object o_86_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isArray();
                    // MethodAssertGenerator build local variable
                    Object o_88_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isSynthetic();
                    // MethodAssertGenerator build local variable
                    Object o_90_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getSimpleName();
                    // MethodAssertGenerator build local variable
                    Object o_92_0 = ((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).name();
                    // MethodAssertGenerator build local variable
                    Object o_94_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).toGenericString();
                    // MethodAssertGenerator build local variable
                    Object o_96_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isEnum();
                    // MethodAssertGenerator build local variable
                    Object o_98_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isEnum();
                    // MethodAssertGenerator build local variable
                    Object o_100_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getSpecificationTitle();
                    // MethodAssertGenerator build local variable
                    Object o_102_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSimpleName();
                    // MethodAssertGenerator build local variable
                    Object o_104_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isAnnotation();
                    // MethodAssertGenerator build local variable
                    Object o_106_0 = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getGenericSuperclass()).getOwnerType();
                    // MethodAssertGenerator build local variable
                    Object o_108_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getComponentType();
                    // MethodAssertGenerator build local variable
                    Object o_110_0 = ((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).ordinal();
                    // MethodAssertGenerator build local variable
                    Object o_112_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getName();
                    // MethodAssertGenerator build local variable
                    Object o_114_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getSigners();
                    // MethodAssertGenerator build local variable
                    Object o_116_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isInterface();
                    // MethodAssertGenerator build local variable
                    Object o_118_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getName();
                    // MethodAssertGenerator build local variable
                    Object o_120_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isSynthetic();
                    // MethodAssertGenerator build local variable
                    Object o_122_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isAnnotation();
                    // MethodAssertGenerator build local variable
                    Object o_124_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isMemberClass();
                    // MethodAssertGenerator build local variable
                    Object o_126_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isInterface();
                    // MethodAssertGenerator build local variable
                    Object o_128_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isAnonymousClass();
                    // MethodAssertGenerator build local variable
                    Object o_130_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getEnclosingConstructor();
                    // MethodAssertGenerator build local variable
                    Object o_132_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isMemberClass();
                    // MethodAssertGenerator build local variable
                    Object o_134_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getComponentType();
                    // MethodAssertGenerator build local variable
                    Object o_136_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).isPrimitive();
                    // MethodAssertGenerator build local variable
                    Object o_138_0 = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getGenericSuperclass()).getTypeName();
                    // MethodAssertGenerator build local variable
                    Object o_140_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).desiredAssertionStatus();
                    // MethodAssertGenerator build local variable
                    Object o_142_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getCanonicalName();
                    // MethodAssertGenerator build local variable
                    Object o_144_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).isPrimitive();
                    // MethodAssertGenerator build local variable
                    Object o_146_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getModifiers();
                    // MethodAssertGenerator build local variable
                    Object o_148_0 = ((java.lang.Class)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getSuperclass()).getDeclaringClass();
                    // MethodAssertGenerator build local variable
                    Object o_150_0 = ((java.lang.Package)((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getPackage()).getImplementationTitle();
                    // MethodAssertGenerator build local variable
                    Object o_152_0 = ((java.lang.Class)((ch.qos.logback.core.util.AggregationType)o_testSetProperty_cf93063__34).getDeclaringClass()).getDeclaringClass();
                    // StatementAdderOnAssert create null value
                    java.lang.String vc_22538 = (java.lang.String)null;
                    // StatementAdderOnAssert create random local variable
                    java.lang.String vc_22537 = new java.lang.String();
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.joran.util.PropertySetter vc_22534 = (ch.qos.logback.core.joran.util.PropertySetter)null;
                    // StatementAdderMethod cloned existing statement
                    vc_22534.setProperty(vc_22537, vc_22538);
                    // MethodAssertGenerator build local variable
                    Object o_162_0 = house.isOpen();
                }
                org.junit.Assert.fail("testSetProperty_cf93063_cf93897 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSetProperty_cf93063_cf93897_failAssert9_literalMutation96028 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }
}

