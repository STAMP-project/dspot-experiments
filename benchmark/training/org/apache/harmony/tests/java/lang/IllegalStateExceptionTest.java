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


public class IllegalStateExceptionTest extends TestCase {
    /**
     * java.lang.IllegalStateException#IllegalStateException()
     */
    public void test_Constructor() {
        IllegalStateException e = new IllegalStateException();
        TestCase.assertNull(e.getMessage());
        TestCase.assertNull(e.getLocalizedMessage());
        TestCase.assertNull(e.getCause());
    }

    /**
     * java.lang.IllegalStateException#IllegalStateException(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        IllegalStateException e = new IllegalStateException("fixture");
        TestCase.assertEquals("fixture", e.getMessage());
        TestCase.assertNull(e.getCause());
    }

    /**
     * {@link java.land.IllegalStateException#IllIllegalStateException(java.lang.Throwable)}
     */
    public void test_ConstructorLjava_lang_Throwable() {
        Throwable emptyThrowable = new Exception();
        IllegalStateException emptyException = new IllegalStateException(emptyThrowable);
        TestCase.assertEquals(emptyThrowable.getClass().getName(), emptyException.getMessage());
        TestCase.assertEquals(emptyThrowable.getClass().getName(), emptyException.getLocalizedMessage());
        TestCase.assertEquals(emptyThrowable.getClass().getName(), emptyException.getCause().toString());
        Throwable throwable = new Exception("msg");
        IllegalStateException exception = new IllegalStateException(throwable);
        TestCase.assertEquals((((throwable.getClass().getName()) + ": ") + "msg"), exception.getMessage());
        TestCase.assertEquals(throwable.getClass().getName(), emptyException.getLocalizedMessage());
        TestCase.assertEquals(throwable.getClass().getName(), emptyException.getCause().toString());
    }

    /**
     * {@link java.land.IllegalStateException#IllIllegalStateException(java.lang.String, java.lang.Throwable)}
     */
    public void test_ConstructorLjava_lang_StringLjava_lang_Throwable() {
        Throwable emptyThrowable = new Exception();
        IllegalStateException emptyException = new IllegalStateException("msg", emptyThrowable);
        TestCase.assertEquals("msg", emptyException.getMessage());
        TestCase.assertEquals("msg", emptyException.getLocalizedMessage());
        TestCase.assertEquals(emptyThrowable.getClass().getName(), emptyException.getCause().toString());
        Throwable throwable = new Exception("msg_exception");
        IllegalStateException exception = new IllegalStateException("msg", throwable);
        TestCase.assertEquals("msg", exception.getMessage());
        TestCase.assertEquals("msg", exception.getLocalizedMessage());
        TestCase.assertEquals((((throwable.getClass().getName()) + ": ") + (throwable.getMessage())), exception.getCause().toString());
    }

    /**
     * serialization/deserialization.
     */
    public void testSerializationSelf() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        SerializationTest.verifySelf(new IllegalStateException());
    }

    /**
     * serialization/deserialization compatibility with RI.
     */
    public void testSerializationCompatibility() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        SerializationTest.verifyGolden(this, new IllegalStateException());
    }
}

