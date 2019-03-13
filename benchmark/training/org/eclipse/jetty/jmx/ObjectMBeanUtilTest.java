/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.jmx;


import com.acme.Derived;
import com.acme.DerivedExtended;
import com.acme.DerivedManaged;
import java.util.ArrayList;
import java.util.Arrays;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ObjectMBeanUtilTest {
    private ObjectMBean objectMBean;

    private DerivedExtended derivedExtended;

    private MBeanContainer container;

    private MBeanInfo objectMBeanInfo;

    private Attribute attribute;

    private ObjectMBean mBeanDerivedManaged;

    private DerivedManaged derivedManaged;

    @Test
    public void testBasicOperations() {
        Assertions.assertEquals(derivedExtended, objectMBean.getManagedObject(), "Managed objects should be equal");
        Assertions.assertNull(objectMBean.getObjectName(), "This method call always returns null in the actual code");
        Assertions.assertNull(objectMBean.getObjectNameBasis(), "This method call always returns null in the actual code");
        Assertions.assertNull(objectMBean.getObjectContextBasis(), "This method call always returns null in the actual code");
        Assertions.assertEquals(container, objectMBean.getMBeanContainer(), "Mbean container should be equal");
        Assertions.assertEquals("Test the mbean extended stuff", objectMBeanInfo.getDescription(), "Mbean description must be equal to : Test the mbean extended stuff");
    }

    @Test
    public void testGetAttributeMBeanException() throws Exception {
        Attribute attribute = new Attribute("doodle4", "charu");
        objectMBean.setAttribute(attribute);
        MBeanException e = Assertions.assertThrows(MBeanException.class, () -> objectMBean.getAttribute("doodle4"));
        Assertions.assertNotNull(e, "An InvocationTargetException must have occurred by now as doodle4() internally throwing exception");
    }

    @Test
    public void testGetAttributeAttributeNotFoundException() {
        AttributeNotFoundException e = Assertions.assertThrows(AttributeNotFoundException.class, () -> objectMBean.getAttribute("ffname"));
        Assertions.assertNotNull(e, "An AttributeNotFoundException must have occurred by now as there is no attribute with the name ffname in bean");
    }

    @Test
    public void testSetAttributeWithCorrectAttrName() throws Exception {
        Attribute attribute = new Attribute("fname", "charu");
        objectMBean.setAttribute(attribute);
        String value = ((String) (objectMBean.getAttribute("fname")));
        Assertions.assertEquals("charu", value, "Attribute(fname) value must be equal to charu");
    }

    @Test
    public void testSetAttributeNullCheck() throws Exception {
        objectMBean.setAttribute(null);
        AttributeNotFoundException e = Assertions.assertThrows(AttributeNotFoundException.class, () -> objectMBean.getAttribute(null));
        Assertions.assertNotNull(e, "An AttributeNotFoundException must have occurred by now as there is no attribute with the name null");
    }

    @Test
    public void testSetAttributeAttributeWithWrongAttrName() {
        attribute = new Attribute("fnameee", "charu");
        AttributeNotFoundException e = Assertions.assertThrows(AttributeNotFoundException.class, () -> objectMBean.setAttribute(attribute));
        Assertions.assertNotNull(e, ("An AttributeNotFoundException must have occurred by now as there is no attribute " + "with the name ffname in bean"));
    }

    @Test
    public void testSetAttributesWithCorrectValues() {
        AttributeList attributes = getAttributes("fname", "vijay");
        objectMBean.setAttributes(attributes);
        attributes = objectMBean.getAttributes(new String[]{ "fname" });
        Assertions.assertEquals(1, attributes.size());
        Assertions.assertEquals("vijay", ((Attribute) (attributes.get(0))).getValue(), "Fname value must be equal to vijay");
    }

    @Test
    public void testSetAttributesForArrayTypeAttribute() throws Exception {
        Derived[] deriveds = getArrayTypeAttribute();
        derivedManaged.setAddresses(deriveds);
        mBeanDerivedManaged.getMBeanInfo();
        Assertions.assertNotNull(mBeanDerivedManaged.getAttribute("addresses"), "Address object shouldn't be null");
    }

    @Test
    public void testSetAttributesForCollectionTypeAttribute() throws Exception {
        ArrayList<Derived> aliasNames = new ArrayList<>(Arrays.asList(getArrayTypeAttribute()));
        derivedManaged.setAliasNames(aliasNames);
        mBeanDerivedManaged.getMBeanInfo();
        Assertions.assertNotNull(mBeanDerivedManaged.getAttribute("aliasNames"), "Address object shouldn't be null");
        Assertions.assertNull(mBeanDerivedManaged.getAttribute("derived"), "Derived object shouldn't registered with container so its value will be null");
    }

    @Test
    public void testSetAttributesException() {
        AttributeList attributes = getAttributes("fnameee", "charu");
        attributes = objectMBean.setAttributes(attributes);
        // Original code eating the exception and returning zero size list
        Assertions.assertEquals(0, attributes.size(), "As there is no attribute with the name fnameee, this should return empty");
    }

    @Test
    public void testInvokeMBeanException() {
        ReflectionException e = Assertions.assertThrows(ReflectionException.class, () -> objectMBean.invoke("doodle2", new Object[0], new String[0]));
        Assertions.assertNotNull(e, "An ReflectionException must have occurred by now as doodle2() in Derived bean is private");
    }

    @Test
    public void testInvokeReflectionException() {
        MBeanException e = Assertions.assertThrows(MBeanException.class, () -> objectMBean.invoke("doodle1", new Object[0], new String[0]));
        Assertions.assertNotNull(e, "MBeanException is null");
    }

    @Test
    public void testInvoke() throws Exception {
        String value = ((String) (objectMBean.invoke("good", new Object[0], new String[0])));
        Assertions.assertEquals("not bad", value, "Method(good) invocation on objectMBean must return not bad");
    }

    @Test
    public void testInvokeNoSuchMethodException() {
        // DerivedMBean contains a managed method with the name good,
        // we must call this method without any arguments.
        ReflectionException e = Assertions.assertThrows(ReflectionException.class, () -> objectMBean.invoke("good", new Object[0], new String[]{ "int aone" }));
        Assertions.assertNotNull(e, "A ReflectionException must have occurred by now as we cannot call a method with wrong signature");
    }

    @Test
    public void testToAttributeName() {
        Assertions.assertEquals("fullName", MetaData.toAttributeName("isfullName"));
    }
}

