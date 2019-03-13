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
package org.apache.hadoop.hdfs.server.namenode;


import SingleUGIResolver.GID;
import SingleUGIResolver.GROUP;
import SingleUGIResolver.UID;
import SingleUGIResolver.USER;
import java.io.IOException;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Validate resolver assigning all paths to a single owner/group.
 */
public class TestSingleUGIResolver {
    @Rule
    public TestName name = new TestName();

    private static final int TESTUID = 10101;

    private static final int TESTGID = 10102;

    private static final String TESTUSER = "tenaqvyybdhragqvatbf";

    private static final String TESTGROUP = "tnyybcvatlnxf";

    private SingleUGIResolver ugi = new SingleUGIResolver();

    @Test
    public void testRewrite() {
        FsPermission p1 = new FsPermission(((short) (493)));
        TestSingleUGIResolver.match(ugi.resolve(TestSingleUGIResolver.file("dingo", "dingo", p1)), p1);
        TestSingleUGIResolver.match(ugi.resolve(TestSingleUGIResolver.file(TestSingleUGIResolver.TESTUSER, "dingo", p1)), p1);
        TestSingleUGIResolver.match(ugi.resolve(TestSingleUGIResolver.file("dingo", TestSingleUGIResolver.TESTGROUP, p1)), p1);
        TestSingleUGIResolver.match(ugi.resolve(TestSingleUGIResolver.file(TestSingleUGIResolver.TESTUSER, TestSingleUGIResolver.TESTGROUP, p1)), p1);
        FsPermission p2 = new FsPermission(((short) (32768)));
        TestSingleUGIResolver.match(ugi.resolve(TestSingleUGIResolver.file("dingo", "dingo", p2)), p2);
        TestSingleUGIResolver.match(ugi.resolve(TestSingleUGIResolver.file(TestSingleUGIResolver.TESTUSER, "dingo", p2)), p2);
        TestSingleUGIResolver.match(ugi.resolve(TestSingleUGIResolver.file("dingo", TestSingleUGIResolver.TESTGROUP, p2)), p2);
        TestSingleUGIResolver.match(ugi.resolve(TestSingleUGIResolver.file(TestSingleUGIResolver.TESTUSER, TestSingleUGIResolver.TESTGROUP, p2)), p2);
        Map<Integer, String> ids = ugi.ugiMap();
        Assert.assertEquals(2, ids.size());
        Assert.assertEquals(TestSingleUGIResolver.TESTUSER, ids.get(10101));
        Assert.assertEquals(TestSingleUGIResolver.TESTGROUP, ids.get(10102));
    }

    @Test
    public void testDefault() {
        String user;
        try {
            user = UserGroupInformation.getCurrentUser().getShortUserName();
        } catch (IOException e) {
            user = "hadoop";
        }
        Configuration conf = new Configuration(false);
        ugi.setConf(conf);
        Map<Integer, String> ids = ugi.ugiMap();
        Assert.assertEquals(2, ids.size());
        Assert.assertEquals(user, ids.get(0));
        Assert.assertEquals(user, ids.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUid() {
        Configuration conf = ugi.getConf();
        conf.setInt(UID, ((1 << 24) + 1));
        ugi.setConf(conf);
        ugi.resolve(TestSingleUGIResolver.file(TestSingleUGIResolver.TESTUSER, TestSingleUGIResolver.TESTGROUP, new FsPermission(((short) (511)))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidGid() {
        Configuration conf = ugi.getConf();
        conf.setInt(GID, ((1 << 24) + 1));
        ugi.setConf(conf);
        ugi.resolve(TestSingleUGIResolver.file(TestSingleUGIResolver.TESTUSER, TestSingleUGIResolver.TESTGROUP, new FsPermission(((short) (511)))));
    }

    @Test(expected = IllegalStateException.class)
    public void testDuplicateIds() {
        Configuration conf = new Configuration(false);
        conf.setInt(UID, 4344);
        conf.setInt(GID, 4344);
        conf.set(USER, TestSingleUGIResolver.TESTUSER);
        conf.set(GROUP, TestSingleUGIResolver.TESTGROUP);
        ugi.setConf(conf);
        ugi.ugiMap();
    }
}

