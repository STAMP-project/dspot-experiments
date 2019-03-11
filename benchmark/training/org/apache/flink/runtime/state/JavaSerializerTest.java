/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.state;


import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.flink.api.common.typeutils.SerializerTestBase;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * A test that verifies that the {@link JavaSerializer} properly handles class loading.
 */
public class JavaSerializerTest extends SerializerTestBase<Serializable> {
    /**
     * Class loader for the object that is not in the test class path
     */
    private static final ClassLoader CLASS_LOADER = new URLClassLoader(new URL[0], JavaSerializerTest.class.getClassLoader());

    /**
     * An object that is not in the test class path
     */
    private static final Serializable OBJECT_OUT_OF_CLASSPATH = CommonTestUtils.createObjectForClassNotInClassPath(JavaSerializerTest.CLASS_LOADER);

    // ------------------------------------------------------------------------
    private ClassLoader originalClassLoader;

    // ------------------------------------------------------------------------
    @Test
    public void guardTest() {
        // make sure that this test's assumptions hold
        try {
            Class.forName(JavaSerializerTest.OBJECT_OUT_OF_CLASSPATH.getClass().getName());
            Assert.fail("Test ineffective: The test class that should not be on the classpath is actually on the classpath.");
        } catch (ClassNotFoundException e) {
            // expected
        }
    }
}

