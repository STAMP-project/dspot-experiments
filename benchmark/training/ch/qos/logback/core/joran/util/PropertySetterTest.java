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


import AggregationType.AS_BASIC_PROPERTY;
import AggregationType.AS_BASIC_PROPERTY_COLLECTION;
import AggregationType.AS_COMPLEX_PROPERTY;
import AggregationType.AS_COMPLEX_PROPERTY_COLLECTION;
import FilterReply.ACCEPT;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.testUtil.StatusChecker;
import ch.qos.logback.core.util.StatusPrinter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import org.junit.Assert;
import org.junit.Test;


public class PropertySetterTest {
    DefaultNestedComponentRegistry defaultComponentRegistry = new DefaultNestedComponentRegistry();

    Context context = new ContextBase();

    StatusChecker checker = new StatusChecker(context);

    House house = new House();

    PropertySetter setter = new PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);

    @Test
    public void testCanAggregateComponent() {
        Assert.assertEquals(AS_COMPLEX_PROPERTY, setter.computeAggregationType("door"));
        Assert.assertEquals(AS_BASIC_PROPERTY, setter.computeAggregationType("count"));
        Assert.assertEquals(AS_BASIC_PROPERTY, setter.computeAggregationType("Count"));
        Assert.assertEquals(AS_BASIC_PROPERTY, setter.computeAggregationType("name"));
        Assert.assertEquals(AS_BASIC_PROPERTY, setter.computeAggregationType("Name"));
        Assert.assertEquals(AS_BASIC_PROPERTY, setter.computeAggregationType("Duration"));
        Assert.assertEquals(AS_BASIC_PROPERTY, setter.computeAggregationType("fs"));
        Assert.assertEquals(AS_BASIC_PROPERTY, setter.computeAggregationType("open"));
        Assert.assertEquals(AS_BASIC_PROPERTY, setter.computeAggregationType("Open"));
        Assert.assertEquals(AS_COMPLEX_PROPERTY_COLLECTION, setter.computeAggregationType("Window"));
        Assert.assertEquals(AS_BASIC_PROPERTY_COLLECTION, setter.computeAggregationType("adjective"));
        Assert.assertEquals(AS_BASIC_PROPERTY, setter.computeAggregationType("filterReply"));
        Assert.assertEquals(AS_BASIC_PROPERTY, setter.computeAggregationType("houseColor"));
    }

    @Test
    public void testSetProperty() {
        {
            House house = new House();
            PropertySetter setter = new PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("count", "10");
            setter.setProperty("temperature", "33.1");
            setter.setProperty("name", "jack");
            setter.setProperty("open", "true");
            Assert.assertEquals(10, house.getCount());
            Assert.assertEquals(33.1, ((double) (house.getTemperature())), 0.01);
            Assert.assertEquals("jack", house.getName());
            Assert.assertTrue(house.isOpen());
        }
        {
            House house = new House();
            PropertySetter setter = new PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), house);
            setter.setProperty("Count", "10");
            setter.setProperty("Name", "jack");
            setter.setProperty("Open", "true");
            Assert.assertEquals(10, house.getCount());
            Assert.assertEquals("jack", house.getName());
            Assert.assertTrue(house.isOpen());
        }
    }

    @Test
    public void testSetCamelProperty() {
        setter.setProperty("camelCase", "trot");
        Assert.assertEquals("trot", house.getCamelCase());
        setter.setProperty("camelCase", "gh");
        Assert.assertEquals("gh", house.getCamelCase());
    }

    @Test
    public void testSetComplexProperty() {
        Door door = new Door();
        setter.setComplexProperty("door", door);
        Assert.assertEquals(door, house.getDoor());
    }

    @Test
    public void testgetClassNameViaImplicitRules() {
        Class<?> compClass = setter.getClassNameViaImplicitRules("door", AS_COMPLEX_PROPERTY, defaultComponentRegistry);
        Assert.assertEquals(Door.class, compClass);
    }

    @Test
    public void testgetComplexPropertyColleClassNameViaImplicitRules() {
        Class<?> compClass = setter.getClassNameViaImplicitRules("window", AS_COMPLEX_PROPERTY_COLLECTION, defaultComponentRegistry);
        Assert.assertEquals(Window.class, compClass);
    }

    @Test
    public void testPropertyCollection() {
        setter.addBasicProperty("adjective", "nice");
        setter.addBasicProperty("adjective", "big");
        Assert.assertEquals(2, house.adjectiveList.size());
        Assert.assertEquals("nice", house.adjectiveList.get(0));
        Assert.assertEquals("big", house.adjectiveList.get(1));
    }

    @Test
    public void testComplexCollection() {
        Window w1 = new Window();
        w1.handle = 10;
        Window w2 = new Window();
        w2.handle = 20;
        setter.addComplexProperty("window", w1);
        setter.addComplexProperty("window", w2);
        Assert.assertEquals(2, house.windowList.size());
        Assert.assertEquals(10, house.windowList.get(0).handle);
        Assert.assertEquals(20, house.windowList.get(1).handle);
    }

    @Test
    public void testSetComplexWithCamelCaseName() {
        SwimmingPool pool = new SwimmingPoolImpl();
        setter.setComplexProperty("swimmingPool", pool);
        Assert.assertEquals(pool, house.getSwimmingPool());
    }

    @Test
    public void testDuration() {
        setter.setProperty("duration", "1.4 seconds");
        Assert.assertEquals(1400, house.getDuration().getMilliseconds());
    }

    @Test
    public void testFileSize() {
        setter.setProperty("fs", "2 kb");
        Assert.assertEquals((2 * 1024), house.getFs().getSize());
    }

    @Test
    public void testFilterReply() {
        // test case reproducing bug #52
        setter.setProperty("filterReply", "ACCEPT");
        Assert.assertEquals(ACCEPT, house.getFilterReply());
    }

    @Test
    public void testEnum() {
        setter.setProperty("houseColor", "BLUE");
        Assert.assertEquals(HouseColor.BLUE, house.getHouseColor());
    }

    @Test
    public void testDefaultClassAnnonation() {
        Method relevantMethod = setter.getRelevantMethod("SwimmingPool", AS_COMPLEX_PROPERTY);
        Assert.assertNotNull(relevantMethod);
        Class<?> spClass = setter.getDefaultClassNameByAnnonation("SwimmingPool", relevantMethod);
        Assert.assertEquals(SwimmingPoolImpl.class, spClass);
        Class<?> classViaImplicitRules = setter.getClassNameViaImplicitRules("SwimmingPool", AS_COMPLEX_PROPERTY, defaultComponentRegistry);
        Assert.assertEquals(SwimmingPoolImpl.class, classViaImplicitRules);
    }

    @Test
    public void testDefaultClassAnnotationForLists() {
        Method relevantMethod = setter.getRelevantMethod("LargeSwimmingPool", AS_COMPLEX_PROPERTY_COLLECTION);
        Assert.assertNotNull(relevantMethod);
        Class<?> spClass = setter.getDefaultClassNameByAnnonation("LargeSwimmingPool", relevantMethod);
        Assert.assertEquals(LargeSwimmingPoolImpl.class, spClass);
        Class<?> classViaImplicitRules = setter.getClassNameViaImplicitRules("LargeSwimmingPool", AS_COMPLEX_PROPERTY_COLLECTION, defaultComponentRegistry);
        Assert.assertEquals(LargeSwimmingPoolImpl.class, classViaImplicitRules);
    }

    @Test
    public void charset() {
        setter.setProperty("charset", "UTF-8");
        Assert.assertEquals(Charset.forName("UTF-8"), house.getCharset());
        house.setCharset(null);
        setter.setProperty("charset", "UTF");
        Assert.assertNull(house.getCharset());
        StatusChecker checker = new StatusChecker(context);
        containsException(UnsupportedCharsetException.class);
    }

    // see also http://jira.qos.ch/browse/LOGBACK-1164
    @Test
    public void bridgeMethodsShouldBeIgnored() {
        Orange orange = new Orange();
        PropertySetter orangeSetter = new PropertySetter(new ch.qos.logback.core.joran.util.beans.BeanDescriptionCache(context), orange);
        Assert.assertEquals(AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(Citrus.PRECARP_PROPERTY_NAME));
        Assert.assertEquals(AS_BASIC_PROPERTY, orangeSetter.computeAggregationType(Citrus.PREFIX_PROPERTY_NAME));
        StatusPrinter.print(context);
        checker.assertIsWarningOrErrorFree();
    }
}

