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


import junit.framework.TestCase;


public class OutOfMemoryErrorTest extends TestCase {
    /**
     * java.lang.OutOfMemoryError#OutOfMemoryError()
     */
    public void test_Constructor() {
        // Test for method java.lang.OutOfMemoryError()
        Error e = new OutOfMemoryError();
        TestCase.assertNull(e.getCause());
        TestCase.assertNull(e.getMessage());
    }

    /**
     * java.lang.OutOfMemoryError#OutOfMemoryError(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        // Test for method java.lang.OutOfMemoryError(java.lang.String)
        Error e = new OutOfMemoryError(null);
        TestCase.assertNull(e.getMessage());
        TestCase.assertNull(e.getCause());
        e = new OutOfMemoryError("msg");
        TestCase.assertEquals("msg", e.getMessage());
        TestCase.assertNull(e.getCause());
    }
}

