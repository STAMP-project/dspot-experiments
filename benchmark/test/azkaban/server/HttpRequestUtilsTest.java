/**
 * Copyright 2015 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.server;


import ExecutionOptions.FLOW_PRIORITY;
import ExecutionOptions.USE_EXECUTOR;
import Type.ADMIN;
import azkaban.executor.ExecutableFlow;
import azkaban.executor.ExecutorManagerException;
import azkaban.user.User;
import azkaban.user.UserManager;
import azkaban.user.UserManagerException;
import azkaban.utils.TestUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for HttpRequestUtils
 */
public final class HttpRequestUtilsTest {
    /* Test that flow properties are removed for non-admin user */
    @Test
    public void TestFilterNonAdminOnlyFlowParams() throws ExecutorManagerException, UserManagerException, IOException {
        final ExecutableFlow flow = HttpRequestUtilsTest.createExecutableFlow();
        final UserManager manager = TestUtils.createTestXmlUserManager();
        final User user = manager.getUser("testUser", "testUser");
        HttpRequestUtils.filterAdminOnlyFlowParams(manager, flow.getExecutionOptions(), user);
        Assert.assertFalse(flow.getExecutionOptions().getFlowParameters().containsKey(FLOW_PRIORITY));
        Assert.assertFalse(flow.getExecutionOptions().getFlowParameters().containsKey(USE_EXECUTOR));
    }

    /* Test that flow properties are retained for admin user */
    @Test
    public void TestFilterAdminOnlyFlowParams() throws ExecutorManagerException, UserManagerException, IOException {
        final ExecutableFlow flow = HttpRequestUtilsTest.createExecutableFlow();
        final UserManager manager = TestUtils.createTestXmlUserManager();
        final User user = manager.getUser("testAdmin", "testAdmin");
        HttpRequestUtils.filterAdminOnlyFlowParams(manager, flow.getExecutionOptions(), user);
        Assert.assertTrue(flow.getExecutionOptions().getFlowParameters().containsKey(FLOW_PRIORITY));
        Assert.assertTrue(flow.getExecutionOptions().getFlowParameters().containsKey(USE_EXECUTOR));
    }

    /* Test exception, if param is a valid integer */
    @Test
    public void testvalidIntegerParam() throws ExecutorManagerException {
        final Map<String, String> params = new HashMap<>();
        params.put("param1", "123");
        HttpRequestUtils.validateIntegerParam(params, "param1");
    }

    /* Test exception, if param is not a valid integer */
    @Test(expected = ExecutorManagerException.class)
    public void testInvalidIntegerParam() throws ExecutorManagerException {
        final Map<String, String> params = new HashMap<>();
        params.put("param1", "1dff2");
        HttpRequestUtils.validateIntegerParam(params, "param1");
    }

    /* Verify permission for admin user */
    @Test
    public void testHasAdminPermission() throws UserManagerException {
        final UserManager manager = TestUtils.createTestXmlUserManager();
        final User adminUser = manager.getUser("testAdmin", "testAdmin");
        Assert.assertTrue(HttpRequestUtils.hasPermission(manager, adminUser, ADMIN));
    }

    /* verify permission for non-admin user */
    @Test
    public void testHasOrdinaryPermission() throws UserManagerException {
        final UserManager manager = TestUtils.createTestXmlUserManager();
        final User testUser = manager.getUser("testUser", "testUser");
        Assert.assertFalse(HttpRequestUtils.hasPermission(manager, testUser, ADMIN));
    }
}

