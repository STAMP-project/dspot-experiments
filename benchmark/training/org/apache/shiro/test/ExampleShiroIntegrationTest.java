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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.test;


import org.apache.shiro.subject.Subject;
import org.junit.Test;


/**
 * Simple example test class to be used to show how one might write Shiro-compatible unit tests.
 *
 * @since 1.2
 */
public class ExampleShiroIntegrationTest extends AbstractShiroTest {
    @Test
    public void testSimple() {
        // 1.  Build the Subject instance for the test to run:
        Subject subjectUnderTest = new Subject.Builder(AbstractShiroTest.getSecurityManager()).buildSubject();
        // 2. Bind the subject to the current thread:
        setSubject(subjectUnderTest);
        // perform test logic here.  Any call to
        // SecurityUtils.getSubject() directly (or nested in the
        // call stack) will work properly.
    }
}

