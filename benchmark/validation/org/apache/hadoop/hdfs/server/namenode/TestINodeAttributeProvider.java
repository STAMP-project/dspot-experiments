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


import AclEntryType.GROUP;
import FsAction.ALL;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.XAttr;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestINodeAttributeProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TestINodeAttributeProvider.class);

    private MiniDFSCluster miniDFS;

    private static final Set<String> CALLED = new HashSet<String>();

    private static final short HDFS_PERMISSION = 511;

    private static final short PROVIDER_PERMISSION = 504;

    public static class MyAuthorizationProvider extends INodeAttributeProvider {
        public static class MyAccessControlEnforcer implements AccessControlEnforcer {
            AccessControlEnforcer ace;

            public MyAccessControlEnforcer(AccessControlEnforcer defaultEnforcer) {
                this.ace = defaultEnforcer;
            }

            @Override
            public void checkPermission(String fsOwner, String supergroup, UserGroupInformation ugi, INodeAttributes[] inodeAttrs, INode[] inodes, byte[][] pathByNameArr, int snapshotId, String path, int ancestorIndex, boolean doCheckOwner, FsAction ancestorAccess, FsAction parentAccess, FsAction access, FsAction subAccess, boolean ignoreEmptyDir) throws AccessControlException {
                if (((ancestorIndex > 1) && (inodes[1].getLocalName().equals("user"))) && (inodes[2].getLocalName().equals("acl"))) {
                    this.ace.checkPermission(fsOwner, supergroup, ugi, inodeAttrs, inodes, pathByNameArr, snapshotId, path, ancestorIndex, doCheckOwner, ancestorAccess, parentAccess, access, subAccess, ignoreEmptyDir);
                }
                TestINodeAttributeProvider.CALLED.add(((((("checkPermission|" + ancestorAccess) + "|") + parentAccess) + "|") + access));
            }
        }

        @Override
        public void start() {
            TestINodeAttributeProvider.CALLED.add("start");
        }

        @Override
        public void stop() {
            TestINodeAttributeProvider.CALLED.add("stop");
        }

        @Override
        public INodeAttributes getAttributes(String[] pathElements, final INodeAttributes inode) {
            TestINodeAttributeProvider.CALLED.add("getAttributes");
            final boolean useDefault = useDefault(pathElements);
            final boolean useNullAcl = useNullAclFeature(pathElements);
            return new INodeAttributes() {
                @Override
                public boolean isDirectory() {
                    return inode.isDirectory();
                }

                @Override
                public byte[] getLocalNameBytes() {
                    return inode.getLocalNameBytes();
                }

                @Override
                public String getUserName() {
                    return useDefault ? inode.getUserName() : "foo";
                }

                @Override
                public String getGroupName() {
                    return useDefault ? inode.getGroupName() : "bar";
                }

                @Override
                public FsPermission getFsPermission() {
                    return useDefault ? inode.getFsPermission() : new FsPermission(getFsPermissionShort());
                }

                @Override
                public short getFsPermissionShort() {
                    return useDefault ? inode.getFsPermissionShort() : ((short) (getPermissionLong()));
                }

                @Override
                public long getPermissionLong() {
                    return useDefault ? inode.getPermissionLong() : ((long) (TestINodeAttributeProvider.PROVIDER_PERMISSION));
                }

                @Override
                public AclFeature getAclFeature() {
                    AclFeature f;
                    if (useNullAcl) {
                        int[] entries = new int[0];
                        f = new AclFeature(entries);
                    } else
                        if (useDefault) {
                            f = inode.getAclFeature();
                        } else {
                            AclEntry acl = new AclEntry.Builder().setType(GROUP).setPermission(ALL).setName("xxx").build();
                            f = new AclFeature(AclEntryStatusFormat.toInt(Lists.newArrayList(acl)));
                        }

                    return f;
                }

                @Override
                public XAttrFeature getXAttrFeature() {
                    XAttrFeature x;
                    if (useDefault) {
                        x = inode.getXAttrFeature();
                    } else {
                        x = new XAttrFeature(ImmutableList.copyOf(Lists.newArrayList(new XAttr.Builder().setName("test").setValue(new byte[]{ 1, 2 }).build())));
                    }
                    return x;
                }

                @Override
                public long getModificationTime() {
                    return useDefault ? inode.getModificationTime() : 0;
                }

                @Override
                public long getAccessTime() {
                    return useDefault ? inode.getAccessTime() : 0;
                }
            };
        }

        @Override
        public AccessControlEnforcer getExternalAccessControlEnforcer(AccessControlEnforcer defaultEnforcer) {
            return new TestINodeAttributeProvider.MyAuthorizationProvider.MyAccessControlEnforcer(defaultEnforcer);
        }

        private boolean useDefault(String[] pathElements) {
            return ((pathElements.length) < 2) || (!((pathElements[0].equals("user")) && (pathElements[1].equals("authz"))));
        }

        private boolean useNullAclFeature(String[] pathElements) {
            return (((pathElements.length) > 2) && (pathElements[1].equals("user"))) && (pathElements[2].equals("acl"));
        }
    }

    @Test
    public void testDelegationToProvider() throws Exception {
        Assert.assertTrue(TestINodeAttributeProvider.CALLED.contains("start"));
        FileSystem fs = FileSystem.get(miniDFS.getConfiguration(0));
        final Path tmpPath = new Path("/tmp");
        final Path fooPath = new Path("/tmp/foo");
        fs.mkdirs(tmpPath);
        fs.setPermission(tmpPath, new FsPermission(TestINodeAttributeProvider.HDFS_PERMISSION));
        UserGroupInformation ugi = UserGroupInformation.createUserForTesting("u1", new String[]{ "g1" });
        ugi.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                FileSystem fs = FileSystem.get(miniDFS.getConfiguration(0));
                TestINodeAttributeProvider.CALLED.clear();
                fs.mkdirs(fooPath);
                Assert.assertTrue(TestINodeAttributeProvider.CALLED.contains("getAttributes"));
                Assert.assertTrue(TestINodeAttributeProvider.CALLED.contains("checkPermission|null|null|null"));
                Assert.assertTrue(TestINodeAttributeProvider.CALLED.contains("checkPermission|WRITE|null|null"));
                TestINodeAttributeProvider.CALLED.clear();
                fs.listStatus(fooPath);
                Assert.assertTrue(TestINodeAttributeProvider.CALLED.contains("getAttributes"));
                Assert.assertTrue(TestINodeAttributeProvider.CALLED.contains("checkPermission|null|null|READ_EXECUTE"));
                TestINodeAttributeProvider.CALLED.clear();
                fs.getAclStatus(fooPath);
                Assert.assertTrue(TestINodeAttributeProvider.CALLED.contains("getAttributes"));
                Assert.assertTrue(TestINodeAttributeProvider.CALLED.contains("checkPermission|null|null|null"));
                return null;
            }
        });
    }

    private class AssertHelper {
        private boolean bypass = true;

        AssertHelper(boolean bp) {
            bypass = bp;
        }

        public void doAssert(boolean x) {
            if (bypass) {
                Assert.assertFalse(x);
            } else {
                Assert.assertTrue(x);
            }
        }
    }

    @Test
    public void testAuthzDelegationToProvider() throws Exception {
        TestINodeAttributeProvider.LOG.info("Test not bypassing provider");
        String[] users = new String[]{ "u1" };
        testBypassProviderHelper(users, TestINodeAttributeProvider.PROVIDER_PERMISSION, false);
    }

    @Test
    public void testAuthzBypassingProvider() throws Exception {
        TestINodeAttributeProvider.LOG.info("Test bypassing provider");
        String[] users = new String[]{ "u2", "u3" };
        testBypassProviderHelper(users, TestINodeAttributeProvider.HDFS_PERMISSION, true);
    }

    /**
     * With the custom provider configured, verify file status attributes.
     * A superuser can bypass permission check while resolving paths. So,
     * verify file status for both superuser and non-superuser.
     */
    @Test
    public void testCustomProvider() throws Exception {
        final UserGroupInformation[] users = new UserGroupInformation[]{ UserGroupInformation.createUserForTesting(System.getProperty("user.name"), new String[]{ "supergroup" }), UserGroupInformation.createUserForTesting("normaluser", new String[]{ "normalusergroup" }) };
        for (final UserGroupInformation user : users) {
            user.doAs(((PrivilegedExceptionAction<Object>) (() -> {
                verifyFileStatus(user);
                return null;
            })));
        }
    }

    @Test
    public void testAclFeature() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createUserForTesting("testuser", new String[]{ "testgroup" });
        ugi.doAs(((PrivilegedExceptionAction<Object>) (() -> {
            FileSystem fs = miniDFS.getFileSystem();
            Path aclDir = new Path("/user/acl");
            fs.mkdirs(aclDir);
            Path aclChildDir = new Path(aclDir, "subdir");
            fs.mkdirs(aclChildDir);
            AclStatus aclStatus = fs.getAclStatus(aclDir);
            Assert.assertEquals(0, aclStatus.getEntries().size());
            return null;
        })));
    }
}

