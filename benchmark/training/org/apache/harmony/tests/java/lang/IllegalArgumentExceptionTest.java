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
package org.apache.harmony.tests.java.lang;


import com.google.j2objc.util.ReflectionUtil;
import junit.framework.TestCase;
import org.apache.harmony.testframework.serialization.SerializationTest;


public class IllegalArgumentExceptionTest extends TestCase {
    /**
     * java.lang.IllegalArgumentException#IllegalArgumentException()
     */
    public void test_Constructor() {
        IllegalArgumentException e = new IllegalArgumentException();
        TestCase.assertNull(e.getMessage());
        TestCase.assertNull(e.getLocalizedMessage());
        TestCase.assertNull(e.getCause());
    }

    /**
     * java.lang.IllegalArgumentException#IllegalArgumentException(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        IllegalArgumentException e = new IllegalArgumentException("fixture");
        TestCase.assertEquals("fixture", e.getMessage());
        TestCase.assertNull(e.getCause());
    }

    /**
     * {@link java.lang.IllegalArgumentException#IllegalArgumentException(Throwable)}
     */
    public void test_ConstructorLjava_lang_Throwable() {
        Throwable emptyThrowable = new Exception();
        IllegalArgumentException emptyException = new IllegalArgumentException(emptyThrowable);
        TestCase.assertEquals(emptyThrowable.getClass().getName(), emptyException.getMessage());
        TestCase.assertEquals(emptyThrowable.getClass().getName(), emptyException.getLocalizedMessage());
        TestCase.assertEquals(emptyThrowable.getClass().getName(), emptyException.getCause().toString());
        Throwable exception = new Exception("msg");
        IllegalArgumentException e = new IllegalArgumentException(exception);
        TestCase.assertEquals((((exception.getClass().getName()) + ": ") + "msg"), e.getMessage());
        TestCase.assertEquals(exception.getClass().getName(), emptyException.getLocalizedMessage());
        TestCase.assertEquals(exception.getClass().getName(), emptyException.getCause().toString());
    }

    /**
     * serialization/deserialization.
     */
    public void testSerializationSelf() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        SerializationTest.verifySelf(new IllegalArgumentException());
    }

    /**
     * serialization/deserialization compatibility with RI.
     */
    public void testSerializationCompatibility() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        SerializationTest.verifyGolden(this, new IllegalArgumentException());
    }
}

