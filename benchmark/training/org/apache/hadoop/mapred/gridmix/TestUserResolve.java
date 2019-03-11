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
package org.apache.hadoop.mapred.gridmix;


import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;


public class TestUserResolve {
    private static Path rootDir = null;

    private static Configuration conf = null;

    private static FileSystem fs = null;

    /**
     * Validate the behavior of {@link RoundRobinUserResolver} for different
     * user resource files like
     * <li> Empty user resource file
     * <li> Non existent user resource file
     * <li> User resource file with valid content
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRoundRobinResolver() throws Exception {
        final UserResolver rslv = new RoundRobinUserResolver();
        Path usersFilePath = new Path(TestUserResolve.rootDir, "users");
        URI userRsrc = new URI(usersFilePath.toString());
        // Check if the error message is as expected for non existent
        // user resource file.
        TestUserResolve.fs.delete(usersFilePath, false);
        String expectedErrorMsg = ("File " + userRsrc) + " does not exist";
        validateBadUsersFile(rslv, userRsrc, expectedErrorMsg);
        // Check if the error message is as expected for empty user resource file
        TestUserResolve.writeUserList(usersFilePath, "");// creates empty users file

        expectedErrorMsg = RoundRobinUserResolver.buildEmptyUsersErrorMsg(userRsrc);
        validateBadUsersFile(rslv, userRsrc, expectedErrorMsg);
        // Create user resource file with valid content like older users list file
        // with usernames and groups
        TestUserResolve.writeUserList(usersFilePath, "user0,groupA,groupB,groupC\nuser1,groupA,groupC\n");
        validateValidUsersFile(rslv, userRsrc);
        // Create user resource file with valid content with
        // usernames with groups and without groups
        TestUserResolve.writeUserList(usersFilePath, "user0,groupA,groupB\nuser1,");
        validateValidUsersFile(rslv, userRsrc);
        // Create user resource file with valid content with
        // usernames without groups
        TestUserResolve.writeUserList(usersFilePath, "user0\nuser1");
        validateValidUsersFile(rslv, userRsrc);
    }

    @Test
    public void testSubmitterResolver() throws Exception {
        final UserResolver rslv = new SubmitterUserResolver();
        Assert.assertFalse(rslv.needsTargetUsersList());
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        Assert.assertEquals(ugi, rslv.getTargetUgi(((UserGroupInformation) (null))));
    }
}

