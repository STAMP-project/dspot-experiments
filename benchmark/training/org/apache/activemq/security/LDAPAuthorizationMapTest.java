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


import java.util.HashSet;
import java.util.Set;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This test assumes setup like in file 'AMQauth.ldif'. Contents of this file is attached below in comments.
 *
 * @author ngcutura
 */
@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP") })
@ApplyLdifFiles("org/apache/activemq/security/AMQauth.ldif")
public class LDAPAuthorizationMapTest extends AbstractLdapTestUnit {
    private static LDAPAuthorizationMap authMap;

    public static LdapServer ldapServer;

    @Test
    public void testOpen() throws Exception {
        DirContext ctx = LDAPAuthorizationMapTest.authMap.open();
        HashSet<String> set = new HashSet<String>();
        NamingEnumeration<NameClassPair> list = ctx.list("ou=destinations,o=ActiveMQ,ou=system");
        while (list.hasMore()) {
            NameClassPair ncp = list.next();
            set.add(ncp.getName());
        } 
        Assert.assertTrue(set.contains("ou=topics"));
        Assert.assertTrue(set.contains("ou=queues"));
    }

    /* Test method for 'org.apache.activemq.security.LDAPAuthorizationMap.getAdminACLs(ActiveMQDestination)' */
    @Test
    public void testGetAdminACLs() {
        ActiveMQDestination q1 = new ActiveMQQueue("queue1");
        Set<GroupPrincipal> aclsq1 = LDAPAuthorizationMapTest.authMap.getAdminACLs(q1);
        Assert.assertEquals(1, aclsq1.size());
        Assert.assertTrue(aclsq1.contains(new GroupPrincipal("role1")));
        ActiveMQDestination t1 = new ActiveMQTopic("topic1");
        Set<GroupPrincipal> aclst1 = LDAPAuthorizationMapTest.authMap.getAdminACLs(t1);
        Assert.assertEquals(1, aclst1.size());
        Assert.assertTrue(aclst1.contains(new GroupPrincipal("role1")));
    }

    /* Test method for 'org.apache.activemq.security.LDAPAuthorizationMap.getReadACLs(ActiveMQDestination)' */
    @Test
    public void testGetReadACLs() {
        ActiveMQDestination q1 = new ActiveMQQueue("queue1");
        Set<GroupPrincipal> aclsq1 = LDAPAuthorizationMapTest.authMap.getReadACLs(q1);
        Assert.assertEquals(1, aclsq1.size());
        Assert.assertTrue(aclsq1.contains(new GroupPrincipal("role1")));
        ActiveMQDestination t1 = new ActiveMQTopic("topic1");
        Set<GroupPrincipal> aclst1 = LDAPAuthorizationMapTest.authMap.getReadACLs(t1);
        Assert.assertEquals(1, aclst1.size());
        Assert.assertTrue(aclst1.contains(new GroupPrincipal("role2")));
    }

    /* Test method for 'org.apache.activemq.security.LDAPAuthorizationMap.getWriteACLs(ActiveMQDestination)' */
    @Test
    public void testGetWriteACLs() {
        ActiveMQDestination q1 = new ActiveMQQueue("queue1");
        Set<GroupPrincipal> aclsq1 = LDAPAuthorizationMapTest.authMap.getWriteACLs(q1);
        Assert.assertEquals(2, aclsq1.size());
        Assert.assertTrue(aclsq1.contains(new GroupPrincipal("role1")));
        Assert.assertTrue(aclsq1.contains(new GroupPrincipal("role2")));
        ActiveMQDestination t1 = new ActiveMQTopic("topic1");
        Set<GroupPrincipal> aclst1 = LDAPAuthorizationMapTest.authMap.getWriteACLs(t1);
        Assert.assertEquals(1, aclst1.size());
        Assert.assertTrue(aclst1.contains(new GroupPrincipal("role3")));
    }

    @Test
    public void testComposite() {
        ActiveMQDestination q1 = new ActiveMQQueue("queue1,topic://topic1");
        Set<GroupPrincipal> aclsq1 = LDAPAuthorizationMapTest.authMap.getWriteACLs(q1);
        Assert.assertEquals(0, aclsq1.size());
    }

    @Test
    public void testAdvisory() {
        ActiveMQDestination dest = AdvisorySupport.getConnectionAdvisoryTopic();
        Set<GroupPrincipal> acls = LDAPAuthorizationMapTest.authMap.getWriteACLs(dest);
        Assert.assertEquals(1, acls.size());
        Assert.assertTrue(acls.contains(new GroupPrincipal("role3")));
    }

    @Test
    public void testTemp() {
        Set<GroupPrincipal> acls = LDAPAuthorizationMapTest.authMap.getTempDestinationAdminACLs();
        Assert.assertEquals(1, acls.size());
        Assert.assertTrue(acls.contains(new GroupPrincipal("role1")));
    }
}

