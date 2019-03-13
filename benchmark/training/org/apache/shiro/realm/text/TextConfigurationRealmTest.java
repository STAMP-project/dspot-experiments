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
package org.apache.shiro.realm.text;


import java.text.ParseException;
import java.util.Arrays;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Assert;
import org.junit.Test;


public class TextConfigurationRealmTest {
    private TextConfigurationRealmTest.TestRealm realm;

    /* Tests that roles and account can't be tested while the realm is being loaded. */
    @Test
    public void testRoleAndUserAccount() throws InterruptedException {
        setUpForReadConfigurationTest();
        executeTest(new Runnable() {
            public void run() {
                Assert.assertTrue("role not found when it was expected", roleExists("role1"));
                Assert.assertTrue("user not found when it was expected", accountExists("user1"));
            }
        });
    }

    /* Tests that roles can't be read while the realm is being loaded. */
    @Test
    public void testHasRole() throws InterruptedException {
        setUpForReadConfigurationTest();
        executeTest(new Runnable() {
            public void run() {
                PrincipalCollection principalCollection = new SimplePrincipalCollection("user1", "realm1");
                Assert.assertTrue("principal doesn't have role when it should", realm.hasRole(principalCollection, "role2"));
                Assert.assertTrue("principal doesn't have all roles when it should", realm.hasAllRoles(principalCollection, Arrays.asList(new String[]{ "role1", "role2" })));
            }
        });
    }

    /* Tests that roles can't be checked while the realm is being loaded. */
    @Test
    public void testCheckRole() throws InterruptedException {
        setUpForReadConfigurationTest();
        executeTest(new Runnable() {
            public void run() {
                PrincipalCollection principalCollection = new SimplePrincipalCollection("user1", "realm1");
                try {
                    realm.checkRoles(principalCollection, new String[]{ "role1", "role2" });
                } catch (AuthorizationException ae) {
                    Assert.fail("principal doesn't have all roles when it should");
                }
            }
        });
    }

    /* Tests that a principal's permissions can't be checked while the realm is being loaded. */
    @Test
    public void testCheckPermission() throws InterruptedException {
        setUpForReadConfigurationTest();
        executeTest(new Runnable() {
            public void run() {
                PrincipalCollection principalCollection = new SimplePrincipalCollection("user1", "realm1");
                try {
                    realm.checkPermission(principalCollection, "role1_permission1");
                    realm.checkPermissions(principalCollection, new String[]{ "role1_permission1", "role2_permission2" });
                } catch (AuthorizationException ae) {
                    Assert.fail("principal doesn't have permission when it should");
                }
            }
        });
    }

    /* Tests that a principal's permissions can't be checked while the realm is being loaded. */
    @Test
    public void testIsPermitted() throws InterruptedException {
        setUpForReadConfigurationTest();
        executeTest(new Runnable() {
            public void run() {
                PrincipalCollection principalCollection = new SimplePrincipalCollection("user1", "realm1");
                Assert.assertTrue("permission not permitted when it should be", realm.isPermitted(principalCollection, "role1_permission1"));
                Assert.assertTrue("permission not permitted when it should be", realm.isPermittedAll(principalCollection, new String[]{ "role1_permission1", "role2_permission2" }));
            }
        });
    }

    /* Test that role definitions cannot be updated when a read thread holds the realm's lock. */
    @Test
    public void testProcessRoleDefinitions() throws InterruptedException {
        realm = new TextConfigurationRealmTest.TestRealm() {
            public void test(Thread runnable) throws InterruptedException {
                // While the realm's lock is held by this thread role definitions cannot be processed
                // Obtain the realm's locks
                ROLES_LOCK.writeLock().lock();
                try {
                    runnable.start();
                    Thread.sleep(500);
                    // No role until lock is released and role definitions are processed
                    Assert.assertFalse("role exists when it shouldn't", roleExists("role1"));
                } finally {
                    ROLES_LOCK.writeLock().unlock();
                }
            }
        };
        // A thread to process new role definitions
        TextConfigurationRealmTest.TestThread testThread = new TextConfigurationRealmTest.TestThread(new Runnable() {
            public void run() {
                try {
                    processRoleDefinitions();
                } catch (ParseException e) {
                    Assert.fail("Unable to parse role definitions");
                }
            }
        });
        setRoles();
        Thread testTask = new Thread(testThread);
        realm.test(testTask);
        testTask.join(500);
        Assert.assertTrue("role doesn't exist when it should", roleExists("role1"));
        testThread.test();
    }

    /* Test that user definitions cannot be updated when a read thread holds the realm's lock. */
    @Test
    public void testProcessUserDefinitions() throws InterruptedException {
        realm = new TextConfigurationRealmTest.TestRealm() {
            public void test(Thread runnable) throws InterruptedException {
                // While the realm's lock is held by this thread user definitions cannot be processed
                // Obtain the realm's locks
                USERS_LOCK.writeLock().lock();
                try {
                    runnable.start();
                    Thread.sleep(500);
                    // No account until lock is released and user definitions are processed
                    Assert.assertFalse("account exists when it shouldn't", accountExists("user1"));
                } finally {
                    USERS_LOCK.writeLock().unlock();
                }
            }
        };
        TextConfigurationRealmTest.TestThread testThread = new TextConfigurationRealmTest.TestThread(new Runnable() {
            public void run() {
                try {
                    processUserDefinitions();
                } catch (ParseException e) {
                    Assert.fail("Unable to parse user definitions");
                }
            }
        });
        setUsers();
        Thread testTask = new Thread(testThread);
        realm.test(testTask);
        testTask.join(500);
        Assert.assertTrue("account doesn't exist when it should", accountExists("user1"));
        testThread.test();
    }

    /* A Class that captures a thread's assertion error. */
    private class TestThread implements Runnable {
        private Runnable test;

        private volatile AssertionError ae;

        public TestThread(Runnable test) {
            this.test = test;
        }

        public void run() {
            try {
                test.run();
            } catch (AssertionError ae) {
                this.ae = ae;
            }
        }

        public void test() {
            if ((ae) != null)
                throw ae;

        }
    }

    /* Provides an additional method that has access to the realm's lock for mutual exclusion. */
    private abstract class TestRealm extends TextConfigurationRealm {
        public abstract void test(Thread runnable) throws InterruptedException;
    }
}

