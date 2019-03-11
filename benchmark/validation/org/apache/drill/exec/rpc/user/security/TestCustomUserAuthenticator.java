/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.rpc.user.security;


import org.apache.drill.categories.SecurityTest;
import org.apache.drill.exec.rpc.RpcException;
import org.apache.drill.exec.rpc.user.security.testing.UserAuthenticatorTestImpl;
import org.apache.drill.test.BaseTestQuery;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(SecurityTest.class)
public class TestCustomUserAuthenticator extends BaseTestQuery {
    @Test
    public void positiveUserAuth() throws Exception {
        TestCustomUserAuthenticator.runTest(UserAuthenticatorTestImpl.TEST_USER_1, UserAuthenticatorTestImpl.TEST_USER_1_PASSWORD);
        TestCustomUserAuthenticator.runTest(UserAuthenticatorTestImpl.TEST_USER_2, UserAuthenticatorTestImpl.TEST_USER_2_PASSWORD);
    }

    @Test
    public void negativeUserAuth() throws Exception {
        TestCustomUserAuthenticator.negativeAuthHelper(UserAuthenticatorTestImpl.TEST_USER_1, "blah.. blah..");
        TestCustomUserAuthenticator.negativeAuthHelper(UserAuthenticatorTestImpl.TEST_USER_2, "blah.. blah..");
        TestCustomUserAuthenticator.negativeAuthHelper("invalidUserName", "blah.. blah..");
    }

    @Test
    public void emptyPassword() throws Exception {
        try {
            TestCustomUserAuthenticator.runTest(UserAuthenticatorTestImpl.TEST_USER_2, "");
            Assert.fail("Expected an exception.");
        } catch (RpcException e) {
            final String exMsg = e.getMessage();
            Assert.assertThat(exMsg, StringContains.containsString("Insufficient credentials"));
        } catch (Exception e) {
            Assert.fail("Expected an RpcException.");
        }
    }

    @Test
    public void positiveUserAuthAfterNegativeUserAuth() throws Exception {
        TestCustomUserAuthenticator.negativeAuthHelper("blah.. blah..", "blah.. blah..");
        TestCustomUserAuthenticator.runTest(UserAuthenticatorTestImpl.TEST_USER_2, UserAuthenticatorTestImpl.TEST_USER_2_PASSWORD);
    }
}

