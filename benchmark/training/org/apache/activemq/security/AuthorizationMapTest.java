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


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.filter.DestinationMapEntry;
import org.apache.activemq.jaas.GroupPrincipal;


/**
 *
 */
public class AuthorizationMapTest extends TestCase {
    static final GroupPrincipal USERS = new GroupPrincipal("users");

    static final GroupPrincipal ADMINS = new GroupPrincipal("admins");

    static final GroupPrincipal TEMP_DESTINATION_ADMINS = new GroupPrincipal("tempDestAdmins");

    public void testAuthorizationMap() {
        AuthorizationMap map = createAuthorizationMap();
        Set<?> readACLs = map.getReadACLs(new ActiveMQQueue("USERS.FOO.BAR"));
        TestCase.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.ADMINS));
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
    }

    public void testComposite() {
        AuthorizationMap map = createAuthorizationMap();
        addABEntry(map);
        Set<?> readACLs = map.getReadACLs(new ActiveMQQueue("USERS.FOO.BAR,DENIED"));
        TestCase.assertEquals(("set size: " + readACLs), 1, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.ADMINS));
        readACLs = map.getReadACLs(new ActiveMQQueue("USERS.FOO.BAR,USERS.BAR.FOO"));
        TestCase.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        readACLs = map.getReadACLs(new ActiveMQQueue("QUEUEA,QUEUEB"));
        TestCase.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
    }

    public void testAuthorizationMapWithTempDest() {
        AuthorizationMap map = createAuthorizationMapWithTempDest();
        Set<?> readACLs = map.getReadACLs(new ActiveMQQueue("USERS.FOO.BAR"));
        TestCase.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.ADMINS));
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        Set<?> tempAdminACLs = map.getTempDestinationAdminACLs();
        TestCase.assertEquals(("set size: " + tempAdminACLs), 1, tempAdminACLs.size());
        TestCase.assertTrue("Contains users group", tempAdminACLs.contains(AuthorizationMapTest.TEMP_DESTINATION_ADMINS));
    }

    public void testWildcards() {
        AuthorizationMap map = createWildcardAuthorizationMap();
        Set<?> readACLs = map.getReadACLs(new ActiveMQQueue("USERS.FOO.BAR"));
        TestCase.assertEquals(("set size: " + readACLs), 1, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.ADMINS));
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        Set<?> writeAcls = map.getWriteACLs(new ActiveMQQueue("USERS.FOO.BAR"));
        TestCase.assertEquals(("set size: " + writeAcls), 1, writeAcls.size());
        TestCase.assertTrue("Contains users group", writeAcls.contains(AuthorizationMapTest.ADMINS));
        TestCase.assertTrue("Contains users group", writeAcls.contains(AuthorizationMapTest.USERS));
        Set<?> adminAcls = map.getAdminACLs(new ActiveMQQueue("USERS.FOO.BAR"));
        TestCase.assertEquals(("set size: " + adminAcls), 1, adminAcls.size());
        TestCase.assertTrue("Contains users group", adminAcls.contains(AuthorizationMapTest.ADMINS));
        TestCase.assertFalse("Contains users group", adminAcls.contains(AuthorizationMapTest.USERS));
        Set<?> tempAdminACLs = map.getTempDestinationAdminACLs();
        TestCase.assertEquals(("set size: " + tempAdminACLs), 1, tempAdminACLs.size());
        TestCase.assertTrue("Contains users group", tempAdminACLs.contains(AuthorizationMapTest.TEMP_DESTINATION_ADMINS));
    }

    public void testWildcardSubscriptions() {
        final GroupPrincipal USERSA = new GroupPrincipal("usersA");
        DefaultAuthorizationMap map = new DefaultAuthorizationMap();
        List<DestinationMapEntry> entries = new ArrayList<>();
        entries.add(createEntry("A", "usersA", null, null));
        map.setAuthorizationEntries(entries);
        Set<?> readACLs = map.getReadACLs(new ActiveMQQueue(">"));
        TestCase.assertEquals(("set size: " + readACLs), 0, readACLs.size());
        readACLs = map.getReadACLs(new ActiveMQQueue("A"));
        TestCase.assertEquals(("set size: " + readACLs), 1, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(USERSA));
        entries.add(createEntry("USERS.>", "users", null, null));
        map.setAuthorizationEntries(entries);
        readACLs = map.getReadACLs(new ActiveMQQueue(">"));
        TestCase.assertEquals(("set size: " + readACLs), 0, readACLs.size());
        readACLs = map.getReadACLs(new ActiveMQQueue("A"));
        TestCase.assertEquals(("set size: " + readACLs), 1, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(USERSA));
        readACLs = map.getReadACLs(new ActiveMQQueue("USERS.>"));
        TestCase.assertEquals(("set size: " + readACLs), 1, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        readACLs = map.getReadACLs(new ActiveMQQueue("USERS.FOO.>"));
        TestCase.assertEquals(("set size: " + readACLs), 1, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        readACLs = map.getReadACLs(new ActiveMQQueue("USERS.TEST"));
        TestCase.assertEquals(("set size: " + readACLs), 1, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        entries.add(createEntry("USERS.A.>", "usersA", null, null));
        map.setAuthorizationEntries(entries);
        readACLs = map.getReadACLs(new ActiveMQQueue(">"));
        TestCase.assertEquals(("set size: " + readACLs), 0, readACLs.size());
        readACLs = map.getReadACLs(new ActiveMQQueue("A"));
        TestCase.assertEquals(("set size: " + readACLs), 1, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(USERSA));
        readACLs = map.getReadACLs(new ActiveMQQueue("USERS.>"));
        TestCase.assertEquals(("set size: " + readACLs), 1, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        readACLs = map.getReadACLs(new ActiveMQQueue("USERS.FOO.>"));
        TestCase.assertEquals(("set size: " + readACLs), 1, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        readACLs = map.getReadACLs(new ActiveMQQueue("USERS.TEST"));
        TestCase.assertEquals(("set size: " + readACLs), 1, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        readACLs = map.getReadACLs(new ActiveMQQueue("USERS.A.>"));
        TestCase.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        TestCase.assertTrue("Contains users group", readACLs.contains(USERSA));
        entries.add(createEntry(">", "admins", null, null));
        map.setAuthorizationEntries(entries);
        readACLs = map.getReadACLs(new ActiveMQQueue(">"));
        TestCase.assertEquals(("set size: " + readACLs), 1, readACLs.size());
        TestCase.assertTrue("Contains admins group", readACLs.contains(AuthorizationMapTest.ADMINS));
        readACLs = map.getReadACLs(new ActiveMQQueue("A"));
        TestCase.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(USERSA));
        TestCase.assertTrue("Contains admins group", readACLs.contains(AuthorizationMapTest.ADMINS));
        readACLs = map.getReadACLs(new ActiveMQQueue("USERS.>"));
        TestCase.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        TestCase.assertTrue("Contains admins group", readACLs.contains(AuthorizationMapTest.ADMINS));
        readACLs = map.getReadACLs(new ActiveMQQueue("USERS.FOO.>"));
        TestCase.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        TestCase.assertTrue("Contains admins group", readACLs.contains(AuthorizationMapTest.ADMINS));
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        readACLs = map.getReadACLs(new ActiveMQQueue("USERS.TEST"));
        TestCase.assertEquals(("set size: " + readACLs), 2, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        TestCase.assertTrue("Contains admins group", readACLs.contains(AuthorizationMapTest.ADMINS));
        readACLs = map.getReadACLs(new ActiveMQQueue("USERS.A.>"));
        TestCase.assertEquals(("set size: " + readACLs), 3, readACLs.size());
        TestCase.assertTrue("Contains users group", readACLs.contains(AuthorizationMapTest.USERS));
        TestCase.assertTrue("Contains users group", readACLs.contains(USERSA));
        TestCase.assertTrue("Contains admins group", readACLs.contains(AuthorizationMapTest.ADMINS));
    }
}

