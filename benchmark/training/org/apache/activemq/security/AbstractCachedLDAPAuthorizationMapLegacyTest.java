/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.security;


import java.util.Set;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.util.Wait;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.shared.ldap.model.ldif.LdifEntry;
import org.apache.directory.shared.ldap.model.ldif.LdifReader;
import org.apache.directory.shared.ldap.model.message.ModifyRequest;
import org.apache.directory.shared.ldap.model.message.ModifyRequestImpl;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.Rdn;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractCachedLDAPAuthorizationMapLegacyTest extends AbstractLdapTestUnit {
    static final GroupPrincipal GUESTS = new GroupPrincipal("guests");

    static final GroupPrincipal USERS = new GroupPrincipal("users");

    static final GroupPrincipal ADMINS = new GroupPrincipal("admins");

    protected LdapConnection connection;

    protected SimpleCachedLDAPAuthorizationMap map;

    @Test
    public void testQuery() throws Exception {
        map.query();
        Set<?> readACLs = map.getReadACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        Assert.assertTrue("Contains admin group", readACLs.contains(AbstractCachedLDAPAuthorizationMapLegacyTest.ADMINS));
        Assert.assertTrue("Contains users group", readACLs.contains(AbstractCachedLDAPAuthorizationMapLegacyTest.USERS));
        Set<?> failedACLs = map.getReadACLs(new ActiveMQQueue("FAILED"));
        Assert.assertEquals(("set size: " + failedACLs), 0, failedACLs.size());
    }

    @Test
    public void testSynchronousUpdate() throws Exception {
        map.setRefreshInterval(1);
        map.query();
        Set<?> readACLs = map.getReadACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        Assert.assertTrue("Contains admin group", readACLs.contains(AbstractCachedLDAPAuthorizationMapLegacyTest.ADMINS));
        Assert.assertTrue("Contains users group", readACLs.contains(AbstractCachedLDAPAuthorizationMapLegacyTest.USERS));
        Set<?> failedACLs = map.getReadACLs(new ActiveMQQueue("FAILED"));
        Assert.assertEquals(("set size: " + failedACLs), 0, failedACLs.size());
        LdifReader reader = new LdifReader(getRemoveLdif());
        for (LdifEntry entry : reader) {
            connection.delete(entry.getDn());
        }
        reader.close();
        Assert.assertTrue("did not get expected size. ", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (map.getReadACLs(new ActiveMQQueue("TEST.FOO")).size()) == 0;
            }
        }));
        Assert.assertNull(map.getTempDestinationReadACLs());
        Assert.assertNull(map.getTempDestinationWriteACLs());
        Assert.assertNull(map.getTempDestinationAdminACLs());
    }

    @Test
    public void testWildcards() throws Exception {
        map.query();
        Set<?> fooACLs = map.getReadACLs(new ActiveMQQueue("FOO.1"));
        Assert.assertEquals(("set size: " + fooACLs), 2, fooACLs.size());
        Assert.assertTrue("Contains admin group", fooACLs.contains(AbstractCachedLDAPAuthorizationMapLegacyTest.ADMINS));
        Assert.assertTrue("Contains users group", fooACLs.contains(AbstractCachedLDAPAuthorizationMapLegacyTest.USERS));
        Set<?> barACLs = map.getReadACLs(new ActiveMQQueue("BAR.2"));
        Assert.assertEquals(("set size: " + barACLs), 2, barACLs.size());
        Assert.assertTrue("Contains admin group", barACLs.contains(AbstractCachedLDAPAuthorizationMapLegacyTest.ADMINS));
        Assert.assertTrue("Contains users group", barACLs.contains(AbstractCachedLDAPAuthorizationMapLegacyTest.USERS));
    }

    @Test
    public void testAdvisory() throws Exception {
        map.query();
        Set<?> readACLs = map.getReadACLs(new ActiveMQTopic("ActiveMQ.Advisory.Connection"));
        Assert.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        Assert.assertTrue("Contains admin group", readACLs.contains(AbstractCachedLDAPAuthorizationMapLegacyTest.ADMINS));
        Assert.assertTrue("Contains users group", readACLs.contains(AbstractCachedLDAPAuthorizationMapLegacyTest.USERS));
    }

    @Test
    public void testTemporary() throws Exception {
        map.query();
        Thread.sleep(1000);
        Set<?> readACLs = map.getTempDestinationReadACLs();
        Assert.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        Assert.assertTrue("Contains admin group", readACLs.contains(AbstractCachedLDAPAuthorizationMapLegacyTest.ADMINS));
        Assert.assertTrue("Contains users group", readACLs.contains(AbstractCachedLDAPAuthorizationMapLegacyTest.USERS));
    }

    @Test
    public void testAdd() throws Exception {
        map.query();
        Set<?> failedACLs = map.getReadACLs(new ActiveMQQueue("FAILED"));
        Assert.assertEquals(("set size: " + failedACLs), 0, failedACLs.size());
        LdifReader reader = new LdifReader(getAddLdif());
        for (LdifEntry entry : reader) {
            connection.add(entry.getEntry());
        }
        reader.close();
        Thread.sleep(2000);
        failedACLs = map.getReadACLs(new ActiveMQQueue("FAILED"));
        Assert.assertEquals(("set size: " + failedACLs), 2, failedACLs.size());
    }

    @Test
    public void testRemove() throws Exception {
        map.query();
        Set<?> failedACLs = map.getReadACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + failedACLs), 2, failedACLs.size());
        LdifReader reader = new LdifReader(getRemoveLdif());
        for (LdifEntry entry : reader) {
            connection.delete(entry.getDn());
        }
        reader.close();
        Thread.sleep(2000);
        failedACLs = map.getReadACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + failedACLs), 0, failedACLs.size());
        Assert.assertTrue((((map.getTempDestinationReadACLs()) == null) || (map.getTempDestinationReadACLs().isEmpty())));
        Assert.assertTrue((((map.getTempDestinationWriteACLs()) == null) || (map.getTempDestinationWriteACLs().isEmpty())));
        Assert.assertTrue((((map.getTempDestinationAdminACLs()) == null) || (map.getTempDestinationAdminACLs().isEmpty())));
    }

    @Test
    public void testRenameDestination() throws Exception {
        map.query();
        // Test for a destination rename
        Set<?> failedACLs = map.getReadACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + failedACLs), 2, failedACLs.size());
        connection.rename(new Dn(("cn=TEST.FOO," + (getQueueBaseDn()))), new Rdn("cn=TEST.BAR"));
        Thread.sleep(2000);
        failedACLs = map.getReadACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + failedACLs), 0, failedACLs.size());
        failedACLs = map.getReadACLs(new ActiveMQQueue("TEST.BAR"));
        Assert.assertEquals(("set size: " + failedACLs), 2, failedACLs.size());
    }

    @Test
    public void testRenamePermission() throws Exception {
        map.query();
        // Test for a permission rename
        connection.delete(new Dn(("cn=Read,cn=TEST.FOO," + (getQueueBaseDn()))));
        Thread.sleep(2000);
        Set<?> failedACLs = map.getReadACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + failedACLs), 0, failedACLs.size());
        failedACLs = map.getWriteACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + failedACLs), 2, failedACLs.size());
        connection.rename(new Dn(("cn=Write,cn=TEST.FOO," + (getQueueBaseDn()))), new Rdn("cn=Read"));
        Thread.sleep(2000);
        failedACLs = map.getReadACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + failedACLs), 2, failedACLs.size());
        failedACLs = map.getWriteACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + failedACLs), 0, failedACLs.size());
    }

    @Test
    public void testChange() throws Exception {
        map.query();
        // Change permission entry
        Set<?> failedACLs = map.getReadACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + failedACLs), 2, failedACLs.size());
        Dn dn = new Dn(("cn=read,cn=TEST.FOO," + (getQueueBaseDn())));
        ModifyRequest request = new ModifyRequestImpl();
        request.setName(dn);
        setupModifyRequest(request);
        connection.modify(request);
        Thread.sleep(2000);
        failedACLs = map.getReadACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + failedACLs), 1, failedACLs.size());
        // Change destination entry
        request = new ModifyRequestImpl();
        request.setName(new Dn(("cn=TEST.FOO," + (getQueueBaseDn()))));
        request.add("description", "This is a description!  In fact, it is a very good description.");
        connection.modify(request);
        Thread.sleep(2000);
        failedACLs = map.getReadACLs(new ActiveMQQueue("TEST.FOO"));
        Assert.assertEquals(("set size: " + failedACLs), 1, failedACLs.size());
    }

    @Test
    public void testRestartAsync() throws Exception {
        testRestart(false);
    }

    @Test
    public void testRestartSync() throws Exception {
        testRestart(true);
    }
}

