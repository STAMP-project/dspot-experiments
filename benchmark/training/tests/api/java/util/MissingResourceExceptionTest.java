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
package tests.api.java.util;


import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import junit.framework.TestCase;


public class MissingResourceExceptionTest extends TestCase {
    /**
     * java.util.MissingResourceException#MissingResourceException(java.lang.String,
     *        java.lang.String, java.lang.String)
     */
    public void test_ConstructorLjava_lang_StringLjava_lang_StringLjava_lang_String() {
        // Test for method java.util.MissingResourceException(java.lang.String,
        // java.lang.String, java.lang.String)
        TestCase.assertNotNull(new MissingResourceException("Detail string", "Class name string", "Key string"));
        TestCase.assertNotNull(new MissingResourceException(null, "Class name string", "Key string"));
        TestCase.assertNotNull(new MissingResourceException("Detail string", null, "Key string"));
        TestCase.assertNotNull(new MissingResourceException("Detail string", "Class name string", null));
        try {
            ResourceBundle.getBundle("Non-ExistentBundle");
        } catch (MissingResourceException e) {
            return;
        }
        TestCase.fail("Failed to generate expected exception");
    }

    /**
     * java.util.MissingResourceException#getClassName()
     */
    public void test_getClassName() {
        // Test for method java.lang.String
        // java.util.MissingResourceException.getClassName()
        try {
            ResourceBundle.getBundle("Non-ExistentBundle");
        } catch (MissingResourceException e) {
            TestCase.assertEquals("Returned incorrect class name", (("Non-ExistentBundle" + '_') + (Locale.getDefault())), e.getClassName());
        }
    }

    /**
     * java.util.MissingResourceException#getKey()
     */
    public void test_getKey() {
        // Test for method java.lang.String
        // java.util.MissingResourceException.getKey()
        try {
            ResourceBundle.getBundle("Non-ExistentBundle");
        } catch (MissingResourceException e) {
            TestCase.assertTrue("Returned incorrect class name", e.getKey().equals(""));
        }
    }
}

