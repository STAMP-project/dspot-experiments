/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.beans.tests.java.beans;


import java.beans.FeatureDescriptor;
import java.util.Enumeration;
import java.util.Hashtable;
import junit.framework.TestCase;


/**
 * Unit test for FeatureDescriptor.
 */
public class FeatureDescriptorTest extends TestCase {
    private FeatureDescriptor fd;

    public void testFeatureDescriptor() {
        TestCase.assertNull(fd.getDisplayName());
        TestCase.assertNull(fd.getName());
        TestCase.assertNull(fd.getShortDescription());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isExpert());
        TestCase.assertFalse(fd.isHidden());
        TestCase.assertFalse(fd.isPreferred());
    }

    public void testSetDisplayName() {
        String displayName = "FeatureDescriptor.displayName";
        fd.setDisplayName(displayName);
        TestCase.assertSame(displayName, fd.getDisplayName());
        TestCase.assertNull(fd.getName());
        TestCase.assertSame(displayName, fd.getShortDescription());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isExpert());
        TestCase.assertFalse(fd.isHidden());
        TestCase.assertFalse(fd.isPreferred());
    }

    public void testSetDisplayName_DisplayNameNull() {
        String displayName = null;
        fd.setDisplayName(displayName);
        TestCase.assertNull(fd.getDisplayName());
        TestCase.assertNull(fd.getName());
        TestCase.assertNull(fd.getShortDescription());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isExpert());
        TestCase.assertFalse(fd.isHidden());
        TestCase.assertFalse(fd.isPreferred());
    }

    public void testSetExpert_False() {
        fd.setExpert(false);
        TestCase.assertFalse(fd.isExpert());
        TestCase.assertNull(fd.getDisplayName());
        TestCase.assertNull(fd.getName());
        TestCase.assertNull(fd.getShortDescription());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isHidden());
        TestCase.assertFalse(fd.isPreferred());
    }

    public void testSetExpert_True() {
        fd.setExpert(true);
        TestCase.assertTrue(fd.isExpert());
        TestCase.assertNull(fd.getDisplayName());
        TestCase.assertNull(fd.getName());
        TestCase.assertNull(fd.getShortDescription());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isHidden());
        TestCase.assertFalse(fd.isPreferred());
    }

    public void testSetHidden_False() {
        fd.setHidden(false);
        TestCase.assertFalse(fd.isHidden());
        TestCase.assertNull(fd.getDisplayName());
        TestCase.assertNull(fd.getName());
        TestCase.assertNull(fd.getShortDescription());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isExpert());
        TestCase.assertFalse(fd.isPreferred());
    }

    public void testSetHidden_True() {
        fd.setHidden(true);
        TestCase.assertTrue(fd.isHidden());
        TestCase.assertNull(fd.getDisplayName());
        TestCase.assertNull(fd.getName());
        TestCase.assertNull(fd.getShortDescription());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isExpert());
        TestCase.assertFalse(fd.isPreferred());
    }

    public void testSetName() {
        String name = "FeatureDescriptor.name";
        fd.setName(name);
        TestCase.assertSame(name, fd.getName());
        TestCase.assertSame(name, fd.getDisplayName());
        TestCase.assertSame(name, fd.getShortDescription());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isExpert());
        TestCase.assertFalse(fd.isHidden());
        TestCase.assertFalse(fd.isPreferred());
    }

    public void testSetName_null() {
        fd.setName("FeatureDescriptor.name");
        fd.setName(null);
        TestCase.assertNull(fd.getName());
        TestCase.assertNull(fd.getDisplayName());
        TestCase.assertNull(fd.getShortDescription());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isExpert());
        TestCase.assertFalse(fd.isHidden());
        TestCase.assertFalse(fd.isPreferred());
    }

    public void testSetPreferred_False() {
        fd.setPreferred(false);
        TestCase.assertFalse(fd.isPreferred());
        TestCase.assertNull(fd.getDisplayName());
        TestCase.assertNull(fd.getName());
        TestCase.assertNull(fd.getShortDescription());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isExpert());
        TestCase.assertFalse(fd.isHidden());
    }

    public void testSetPreferred_True() {
        fd.setPreferred(true);
        TestCase.assertTrue(fd.isPreferred());
        TestCase.assertNull(fd.getDisplayName());
        TestCase.assertNull(fd.getName());
        TestCase.assertNull(fd.getShortDescription());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isExpert());
        TestCase.assertFalse(fd.isHidden());
    }

    public void testSetShortDescription() {
        String shortDesc = "FeatureDescriptor.ShortDescription";
        fd.setShortDescription(shortDesc);
        TestCase.assertSame(shortDesc, fd.getShortDescription());
        TestCase.assertNull(fd.getDisplayName());
        TestCase.assertNull(fd.getName());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isExpert());
        TestCase.assertFalse(fd.isHidden());
        TestCase.assertFalse(fd.isPreferred());
    }

    public void testSetShortDescription_ShortDescNull() {
        String shortDesc = "FeatureDescriptor.ShortDescription";
        fd.setShortDescription(shortDesc);
        TestCase.assertSame(shortDesc, fd.getShortDescription());
        fd.setShortDescription(null);
        TestCase.assertNull(fd.getShortDescription());
        TestCase.assertNull(fd.getDisplayName());
        TestCase.assertNull(fd.getName());
        TestCase.assertNotNull(fd.attributeNames());
        TestCase.assertFalse(fd.isExpert());
        TestCase.assertFalse(fd.isHidden());
        TestCase.assertFalse(fd.isPreferred());
    }

    public void testSetValue() {
        String[] attributeNames = new String[]{ "Blue", "Yellow", "Red" };
        Object[] values = new Object[]{ "Blue.value", "Yellow.value", "Red.value" };
        for (int i = 0; i < (attributeNames.length); i++) {
            fd.setValue(attributeNames[i], values[i]);
        }
        for (int i = 0; i < (attributeNames.length); i++) {
            TestCase.assertSame(values[i], fd.getValue(attributeNames[i]));
        }
    }

    public void testSetValue_ExistAttribute() {
        String attributeName = "blue";
        Object value = "Anyone";
        fd.setValue(attributeName, value);
        TestCase.assertSame(value, fd.getValue(attributeName));
        Object newValue = "Another";
        fd.setValue(attributeName, newValue);
        TestCase.assertSame(newValue, fd.getValue(attributeName));
    }

    public void testSetValue_ValueNull() {
        String attributeName = "blue";
        Object value = "Anyone";
        fd.setValue(attributeName, value);
        TestCase.assertSame(value, fd.getValue(attributeName));
        try {
            fd.setValue(null, value);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            fd.setValue(attributeName, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            fd.setValue(null, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testattributeNames() {
        TestCase.assertFalse(fd.attributeNames().hasMoreElements());
        String[] attributeNames = new String[]{ "Blue", "Yellow", "Red" };
        Object[] values = new Object[]{ "Blue.value", "Yellow.value", "Red.value" };
        for (int i = 0; i < (attributeNames.length); i++) {
            fd.setValue(attributeNames[i], values[i]);
        }
        Enumeration<String> names = fd.attributeNames();
        Hashtable<String, Object> table = new Hashtable<String, Object>();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            table.put(name, fd.getValue(name));
        } 
        TestCase.assertEquals(attributeNames.length, table.size());
        for (String element : attributeNames) {
            TestCase.assertTrue(table.containsKey(element));
        }
    }
}

