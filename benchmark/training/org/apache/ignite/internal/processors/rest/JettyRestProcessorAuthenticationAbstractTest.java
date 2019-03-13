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
package org.apache.ignite.internal.processors.rest;


import GridRestCommand.ADD_USER;
import GridRestCommand.AUTHENTICATE;
import GridRestCommand.REMOVE_USER;
import GridRestCommand.UPDATE_USER;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.Callable;
import org.apache.ignite.internal.processors.authentication.IgniteAccessControlException;
import org.apache.ignite.internal.processors.authentication.IgniteAuthenticationProcessor;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Test REST with enabled authentication.
 */
public abstract class JettyRestProcessorAuthenticationAbstractTest extends JettyRestProcessorUnsignedSelfTest {
    /**
     *
     */
    protected static final String DFLT_USER = "ignite";

    /**
     *
     */
    protected static final String DFLT_PWD = "ignite";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAuthenticationCommand() throws Exception {
        String ret = content(null, AUTHENTICATE);
        assertResponseSucceeded(ret, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAddUpdateRemoveUser() throws Exception {
        // Add user.
        String ret = content(null, ADD_USER, "user", "user1", "password", "password1");
        JsonNode res = validateJsonResponse(ret);
        assertTrue(res.asBoolean());
        IgniteAuthenticationProcessor auth = grid(0).context().authentication();
        assertNotNull(auth.authenticate("user1", "password1"));
        // Update user password.
        ret = content(null, UPDATE_USER, "user", "user1", "password", "password2");
        res = validateJsonResponse(ret);
        assertTrue(res.asBoolean());
        assertNotNull(auth.authenticate("user1", "password2"));
        // Remove user.
        ret = content(null, REMOVE_USER, "user", "user1");
        res = validateJsonResponse(ret);
        assertTrue(res.asBoolean());
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                grid(0).context().authentication().authenticate("user1", "password1");
                return null;
            }
        }, IgniteAccessControlException.class, "The user name or password is incorrect");
    }
}

