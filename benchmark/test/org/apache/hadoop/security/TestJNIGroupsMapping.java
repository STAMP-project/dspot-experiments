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
package org.apache.hadoop.security;


import org.junit.Test;


public class TestJNIGroupsMapping {
    @Test
    public void testJNIGroupsMapping() throws Exception {
        // for the user running the test, check whether the
        // ShellBasedUnixGroupsMapping and the JniBasedUnixGroupsMapping
        // return the same groups
        String user = UserGroupInformation.getCurrentUser().getShortUserName();
        testForUser(user);
        // check for a dummy non-existent user (both the implementations should
        // return an empty list
        testForUser("fooBarBaz1234DoesNotExist");
    }
}

