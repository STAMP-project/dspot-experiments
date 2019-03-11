/**
 * Copyright (C) 2007 The Android Open Source Project
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
 */
package org.apache.harmony.tests.javax.xml.parsers;


import javax.xml.parsers.FactoryConfigurationError;
import junit.framework.TestCase;


public class FactoryConfigurationErrorTest extends TestCase {
    public void test_Constructor() {
        FactoryConfigurationError fce = new FactoryConfigurationError();
        TestCase.assertNull(fce.getMessage());
        TestCase.assertNull(fce.getLocalizedMessage());
        TestCase.assertNull(fce.getCause());
    }

    public void test_ConstructorLjava_lang_Exception() {
        Exception e = new Exception();
        // case 1: Try to create FactoryConfigurationError
        // which is based on Exception without parameters.
        FactoryConfigurationError fce = new FactoryConfigurationError(e);
        TestCase.assertNotNull(fce.getMessage());
        TestCase.assertNotNull(fce.getLocalizedMessage());
        TestCase.assertEquals(e.getCause(), fce.getCause());
        // case 2: Try to create FactoryConfigurationError
        // which is based on Exception with String parameter.
        e = new Exception("test message");
        fce = new FactoryConfigurationError(e);
        TestCase.assertEquals(e.toString(), fce.getMessage());
        TestCase.assertEquals(e.toString(), fce.getLocalizedMessage());
        TestCase.assertEquals(e.getCause(), fce.getCause());
    }

    public void test_ConstructorLjava_lang_ExceptionLjava_lang_String() {
        Exception e = new Exception();
        // case 1: Try to create FactoryConfigurationError
        // which is based on Exception without parameters.
        FactoryConfigurationError fce = new FactoryConfigurationError(e, "msg");
        TestCase.assertNotNull(fce.getMessage());
        TestCase.assertNotNull(fce.getLocalizedMessage());
        TestCase.assertEquals(e.getCause(), fce.getCause());
        // case 2: Try to create FactoryConfigurationError
        // which is based on Exception with String parameter.
        e = new Exception("test message");
        fce = new FactoryConfigurationError(e, "msg");
        TestCase.assertEquals("msg", fce.getMessage());
        TestCase.assertEquals("msg", fce.getLocalizedMessage());
        TestCase.assertEquals(e.getCause(), fce.getCause());
    }

    public void test_ConstructorLjava_lang_String() {
        FactoryConfigurationError fce = new FactoryConfigurationError("Oops!");
        TestCase.assertEquals("Oops!", fce.getMessage());
        TestCase.assertEquals("Oops!", fce.getLocalizedMessage());
        TestCase.assertNull(fce.getCause());
    }

    public void test_getException() {
        FactoryConfigurationError fce = new FactoryConfigurationError();
        TestCase.assertNull(fce.getException());
        fce = new FactoryConfigurationError("test");
        TestCase.assertNull(fce.getException());
        Exception e = new Exception("msg");
        fce = new FactoryConfigurationError(e);
        TestCase.assertEquals(e, fce.getException());
        NullPointerException npe = new NullPointerException();
        fce = new FactoryConfigurationError(npe);
        TestCase.assertEquals(npe, fce.getException());
    }

    public void test_getMessage() {
        TestCase.assertNull(new FactoryConfigurationError().getMessage());
        TestCase.assertEquals("msg1", new FactoryConfigurationError("msg1").getMessage());
        TestCase.assertEquals(new Exception("msg2").toString(), new FactoryConfigurationError(new Exception("msg2")).getMessage());
        TestCase.assertEquals(new NullPointerException().toString(), new FactoryConfigurationError(new NullPointerException()).getMessage());
    }
}

