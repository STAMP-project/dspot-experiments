/**
 * This file is part of dependency-check-core.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2013 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.xml.suppression;


import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;


/**
 *
 *
 * @author Jeremy Long
 */
public class PropertyTypeTest extends BaseTest {
    /**
     * Test of set and getValue method, of class PropertyType.
     */
    @Test
    public void testSetGetValue() {
        PropertyType instance = new PropertyType();
        String expResult = "test";
        instance.setValue(expResult);
        String result = instance.getValue();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of isRegex method, of class PropertyType.
     */
    @Test
    public void testIsRegex() {
        PropertyType instance = new PropertyType();
        Assert.assertFalse(instance.isRegex());
        instance.setRegex(true);
        Assert.assertTrue(instance.isRegex());
    }

    /**
     * Test of isCaseSensitive method, of class PropertyType.
     */
    @Test
    public void testIsCaseSensitive() {
        PropertyType instance = new PropertyType();
        Assert.assertFalse(instance.isCaseSensitive());
        instance.setCaseSensitive(true);
        Assert.assertTrue(instance.isCaseSensitive());
    }

    /**
     * Test of matches method, of class PropertyType.
     */
    @Test
    public void testMatches() {
        String text = "Simple";
        PropertyType instance = new PropertyType();
        instance.setValue("simple");
        Assert.assertTrue(instance.matches(text));
        instance.setCaseSensitive(true);
        Assert.assertFalse(instance.matches(text));
        instance.setValue("s.*le");
        instance.setRegex(true);
        Assert.assertFalse(instance.matches(text));
        instance.setCaseSensitive(false);
        Assert.assertTrue(instance.matches(text));
    }
}

