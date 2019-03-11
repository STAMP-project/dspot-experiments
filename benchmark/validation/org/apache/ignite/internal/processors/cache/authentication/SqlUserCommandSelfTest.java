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
package org.apache.ignite.internal.processors.cache.authentication;


import java.util.concurrent.Callable;
import org.apache.ignite.internal.processors.authentication.AuthorizationContext;
import org.apache.ignite.internal.processors.authentication.IgniteAccessControlException;
import org.apache.ignite.internal.processors.authentication.UserManagementException;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test for leaks JdbcConnection on SqlFieldsQuery execute.
 */
public class SqlUserCommandSelfTest extends GridCommonAbstractTest {
    /**
     * Nodes count.
     */
    private static final int NODES_COUNT = 3;

    /**
     * Client node.
     */
    private static final int CLI_NODE = (SqlUserCommandSelfTest.NODES_COUNT) - 1;

    /**
     * Authorization context for default user.
     */
    private AuthorizationContext actxDflt;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateUpdateDropUser() throws Exception {
        AuthorizationContext.context(actxDflt);
        for (int i = 0; i < (SqlUserCommandSelfTest.NODES_COUNT); ++i) {
            userSql(i, "CREATE USER test WITH PASSWORD 'test'");
            AuthorizationContext actx = grid(i).context().authentication().authenticate("TEST", "test");
            assertNotNull(actx);
            assertEquals("TEST", actx.userName());
            userSql(i, "ALTER USER test WITH PASSWORD 'newpasswd'");
            actx = grid(i).context().authentication().authenticate("TEST", "newpasswd");
            assertNotNull(actx);
            assertEquals("TEST", actx.userName());
            userSql(i, "DROP USER test");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateWithAlreadyExistUser() throws Exception {
        AuthorizationContext.context(actxDflt);
        userSql(0, "CREATE USER test WITH PASSWORD 'test'");
        for (int i = 0; i < (SqlUserCommandSelfTest.NODES_COUNT); ++i) {
            final int idx = i;
            GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    userSql(idx, "CREATE USER test WITH PASSWORD 'test'");
                    return null;
                }
            }, UserManagementException.class, "User already exists [login=TEST]");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAlterDropNotExistUser() throws Exception {
        AuthorizationContext.context(actxDflt);
        for (int i = 0; i < (SqlUserCommandSelfTest.NODES_COUNT); ++i) {
            final int idx = i;
            GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    userSql(idx, "ALTER USER test WITH PASSWORD 'test'");
                    return null;
                }
            }, UserManagementException.class, "User doesn't exist [userName=TEST]");
            GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    userSql(idx, "DROP USER test");
                    return null;
                }
            }, UserManagementException.class, "User doesn't exist [userName=TEST]");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNotAuthenticateOperation() throws Exception {
        for (int i = 0; i < (SqlUserCommandSelfTest.NODES_COUNT); ++i) {
            final int idx = i;
            GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    userSql(idx, "CREATE USER test WITH PASSWORD 'test'");
                    return null;
                }
            }, IgniteAccessControlException.class, "Operation not allowed: authorized context is empty");
            GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    userSql(idx, "ALTER USER test WITH PASSWORD 'test'");
                    return null;
                }
            }, IgniteAccessControlException.class, "Operation not allowed: authorized context is empty");
            GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    userSql(idx, "DROP USER test");
                    return null;
                }
            }, IgniteAccessControlException.class, "Operation not allowed: authorized context is empty");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNotAuthorizedOperation() throws Exception {
        AuthorizationContext.context(actxDflt);
        userSql(0, "CREATE USER user0 WITH PASSWORD 'user0'");
        AuthorizationContext actx = grid(0).context().authentication().authenticate("USER0", "user0");
        AuthorizationContext.context(actx);
        for (int i = 0; i < (SqlUserCommandSelfTest.NODES_COUNT); ++i) {
            final int idx = i;
            GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    userSql(idx, "CREATE USER test WITH PASSWORD 'test'");
                    return null;
                }
            }, IgniteAccessControlException.class, "User management operations are not allowed for user");
            GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    userSql(idx, "ALTER USER test WITH PASSWORD 'test'");
                    return null;
                }
            }, IgniteAccessControlException.class, "User management operations are not allowed for user");
            GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    userSql(idx, "DROP USER test");
                    return null;
                }
            }, IgniteAccessControlException.class, "User management operations are not allowed for user");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropDefaultUser() throws Exception {
        AuthorizationContext.context(actxDflt);
        for (int i = 0; i < (SqlUserCommandSelfTest.NODES_COUNT); ++i) {
            final int idx = i;
            GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    userSql(idx, "DROP USER \"ignite\"");
                    return null;
                }
            }, IgniteAccessControlException.class, "Default user cannot be removed");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQuotedUsername() throws Exception {
        AuthorizationContext.context(actxDflt);
        userSql(0, "CREATE USER \"test\" with password \'test\'");
        userSql(0, "CREATE USER \" test\" with password \'test\'");
        userSql(0, "CREATE USER \" test \" with password \'test\'");
        userSql(0, "CREATE USER \"test \" with password \'test\'");
        userSql(0, "CREATE USER \"111\" with password \'test\'");
    }
}

