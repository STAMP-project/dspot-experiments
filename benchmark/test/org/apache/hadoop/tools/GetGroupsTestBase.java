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
package org.apache.hadoop.tools;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;


public abstract class GetGroupsTestBase {
    protected Configuration conf;

    private UserGroupInformation testUser1;

    private UserGroupInformation testUser2;

    @Test
    public void testNoUserGiven() throws Exception {
        String actualOutput = runTool(conf, new String[0], true);
        UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
        Assert.assertEquals("No user provided should default to current user", GetGroupsTestBase.getExpectedOutput(currentUser), actualOutput);
    }

    @Test
    public void testExistingUser() throws Exception {
        String actualOutput = runTool(conf, new String[]{ testUser1.getUserName() }, true);
        Assert.assertEquals("Show only the output of the user given", GetGroupsTestBase.getExpectedOutput(testUser1), actualOutput);
    }

    @Test
    public void testMultipleExistingUsers() throws Exception {
        String actualOutput = runTool(conf, new String[]{ testUser1.getUserName(), testUser2.getUserName() }, true);
        Assert.assertEquals("Show the output for both users given", ((GetGroupsTestBase.getExpectedOutput(testUser1)) + (GetGroupsTestBase.getExpectedOutput(testUser2))), actualOutput);
    }

    @Test
    public void testNonExistentUser() throws Exception {
        String actualOutput = runTool(conf, new String[]{ "does-not-exist" }, true);
        Assert.assertEquals("Show the output for only the user given, with no groups", GetGroupsTestBase.getExpectedOutput(UserGroupInformation.createRemoteUser("does-not-exist")), actualOutput);
    }

    @Test
    public void testMultipleNonExistingUsers() throws Exception {
        String actualOutput = runTool(conf, new String[]{ "does-not-exist1", "does-not-exist2" }, true);
        Assert.assertEquals("Show the output for only the user given, with no groups", ((GetGroupsTestBase.getExpectedOutput(UserGroupInformation.createRemoteUser("does-not-exist1"))) + (GetGroupsTestBase.getExpectedOutput(UserGroupInformation.createRemoteUser("does-not-exist2")))), actualOutput);
    }

    @Test
    public void testExistingInterleavedWithNonExistentUsers() throws Exception {
        String actualOutput = runTool(conf, new String[]{ "does-not-exist1", testUser1.getUserName(), "does-not-exist2", testUser2.getUserName() }, true);
        Assert.assertEquals("Show the output for only the user given, with no groups", ((((GetGroupsTestBase.getExpectedOutput(UserGroupInformation.createRemoteUser("does-not-exist1"))) + (GetGroupsTestBase.getExpectedOutput(testUser1))) + (GetGroupsTestBase.getExpectedOutput(UserGroupInformation.createRemoteUser("does-not-exist2")))) + (GetGroupsTestBase.getExpectedOutput(testUser2))), actualOutput);
    }
}

