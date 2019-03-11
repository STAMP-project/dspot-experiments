/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.tests.java.util;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;
import junit.framework.TestCase;


public class PropertyResourceBundleTest extends TestCase {
    static PropertyResourceBundle prb;

    /**
     * java.util.PropertyResourceBundle#getKeys()
     */
    public void test_getKeys() {
        Enumeration keyEnum = PropertyResourceBundleTest.prb.getKeys();
        Vector<Object> test = new Vector<Object>();
        int keyCount = 0;
        while (keyEnum.hasMoreElements()) {
            test.addElement(keyEnum.nextElement());
            keyCount++;
        } 
        TestCase.assertEquals("Returned the wrong number of keys", 2, keyCount);
        TestCase.assertTrue("Returned the wrong keys", ((test.contains("p1")) && (test.contains("p2"))));
    }

    /**
     * java.util.PropertyResourceBundle#handleGetObject(java.lang.String)
     */
    public void test_handleGetObjectLjava_lang_String() {
        // Test for method java.lang.Object
        // java.util.PropertyResourceBundle.handleGetObject(java.lang.String)
        try {
            TestCase.assertTrue("Returned incorrect objects", ((PropertyResourceBundleTest.prb.getObject("p1").equals("one")) && (PropertyResourceBundleTest.prb.getObject("p2").equals("two"))));
        } catch (MissingResourceException e) {
            TestCase.fail("Threw MisingResourceException for a key contained in the bundle");
        }
        try {
            PropertyResourceBundleTest.prb.getObject("Not in the bundle");
        } catch (MissingResourceException e) {
            return;
        }
        TestCase.fail("Failed to throw MissingResourceException for object not in the bundle");
    }

    /**
     * {@link java.util.PropertyResourceBundle#Enumeration}
     */
    public void test_access$0_Enumeration() throws IOException {
        class MockResourceBundle extends PropertyResourceBundle {
            MockResourceBundle(InputStream stream) throws IOException {
                super(stream);
            }

            @Override
            protected void setParent(ResourceBundle bundle) {
                super.setParent(bundle);
            }
        }
        InputStream localStream = new ByteArrayInputStream("p3=three\np4=four".getBytes());
        MockResourceBundle localPrb = new MockResourceBundle(localStream);
        localPrb.setParent(PropertyResourceBundleTest.prb);
        Enumeration<String> keys = localPrb.getKeys();
        Vector<String> contents = new Vector<String>();
        while (keys.hasMoreElements()) {
            contents.add(keys.nextElement());
        } 
        TestCase.assertEquals("did not get the right number of properties", 4, contents.size());
        TestCase.assertTrue("did not get the parent property p1", contents.contains("p1"));
        TestCase.assertTrue("did not get the parent property p2", contents.contains("p2"));
        TestCase.assertTrue("did not get the local property p3", contents.contains("p3"));
        TestCase.assertTrue("did not get the local property p4", contents.contains("p4"));
    }
}

