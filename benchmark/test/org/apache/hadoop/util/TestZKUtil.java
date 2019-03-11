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
package org.apache.hadoop.util;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.ZKUtil.ZKAuthInfo;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.junit.Assert;
import org.junit.Test;


public class TestZKUtil {
    private static final String TEST_ROOT_DIR = GenericTestUtils.getTempPath("TestZKUtil");

    private static final File TEST_FILE = new File(TestZKUtil.TEST_ROOT_DIR, "test-file");

    /**
     * A path which is expected not to exist
     */
    private static final String BOGUS_FILE = new File("/xxxx-this-does-not-exist").getPath();

    @Test
    public void testEmptyACL() {
        List<ACL> result = ZKUtil.parseACLs("");
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testNullACL() {
        List<ACL> result = ZKUtil.parseACLs(null);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testInvalidACLs() {
        TestZKUtil.badAcl("a:b", "ACL 'a:b' not of expected form scheme:id:perm");// not enough parts

        TestZKUtil.badAcl("a", "ACL 'a' not of expected form scheme:id:perm");// not enough parts

        TestZKUtil.badAcl("password:foo:rx", "Invalid permission 'x' in permission string 'rx'");
    }

    @Test
    public void testRemoveSpecificPerms() {
        int perms = Perms.ALL;
        int remove = Perms.CREATE;
        int newPerms = ZKUtil.removeSpecificPerms(perms, remove);
        Assert.assertEquals("Removal failed", 0, (newPerms & (Perms.CREATE)));
    }

    @Test
    public void testGoodACLs() {
        List<ACL> result = ZKUtil.parseACLs("sasl:hdfs/host1@MY.DOMAIN:cdrwa, sasl:hdfs/host2@MY.DOMAIN:ca");
        ACL acl0 = result.get(0);
        Assert.assertEquals((((((Perms.CREATE) | (Perms.DELETE)) | (Perms.READ)) | (Perms.WRITE)) | (Perms.ADMIN)), acl0.getPerms());
        Assert.assertEquals("sasl", acl0.getId().getScheme());
        Assert.assertEquals("hdfs/host1@MY.DOMAIN", acl0.getId().getId());
        ACL acl1 = result.get(1);
        Assert.assertEquals(((Perms.CREATE) | (Perms.ADMIN)), acl1.getPerms());
        Assert.assertEquals("sasl", acl1.getId().getScheme());
        Assert.assertEquals("hdfs/host2@MY.DOMAIN", acl1.getId().getId());
    }

    @Test
    public void testEmptyAuth() {
        List<ZKAuthInfo> result = ZKUtil.parseAuth("");
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testNullAuth() {
        List<ZKAuthInfo> result = ZKUtil.parseAuth(null);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testGoodAuths() {
        List<ZKAuthInfo> result = ZKUtil.parseAuth("scheme:data,\n   scheme2:user:pass");
        Assert.assertEquals(2, result.size());
        ZKAuthInfo auth0 = result.get(0);
        Assert.assertEquals("scheme", auth0.getScheme());
        Assert.assertEquals("data", new String(auth0.getAuth()));
        ZKAuthInfo auth1 = result.get(1);
        Assert.assertEquals("scheme2", auth1.getScheme());
        Assert.assertEquals("user:pass", new String(auth1.getAuth()));
    }

    @Test
    public void testConfIndirection() throws IOException {
        Assert.assertNull(ZKUtil.resolveConfIndirection(null));
        Assert.assertEquals("x", ZKUtil.resolveConfIndirection("x"));
        TestZKUtil.TEST_FILE.getParentFile().mkdirs();
        Files.write("hello world", TestZKUtil.TEST_FILE, Charsets.UTF_8);
        Assert.assertEquals("hello world", ZKUtil.resolveConfIndirection(("@" + (TestZKUtil.TEST_FILE.getAbsolutePath()))));
        try {
            ZKUtil.resolveConfIndirection(("@" + (TestZKUtil.BOGUS_FILE)));
            Assert.fail("Did not throw for non-existent file reference");
        } catch (FileNotFoundException fnfe) {
            Assert.assertTrue(fnfe.getMessage().startsWith(TestZKUtil.BOGUS_FILE));
        }
    }
}

