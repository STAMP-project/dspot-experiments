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


import GridRestCommand.VERSION;
import org.junit.Test;


/**
 * Test REST with enabled authentication and token.
 */
public class JettyRestProcessorAuthenticationWithTokenSelfTest extends JettyRestProcessorAuthenticationAbstractTest {
    /**
     *
     */
    private String tok = "";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testInvalidSessionToken() throws Exception {
        tok = null;
        String ret = content(null, VERSION);
        assertResponseContainsError(ret, "Failed to handle request - session token not found or invalid");
        tok = "InvalidToken";
        ret = content(null, VERSION);
        assertResponseContainsError(ret, "Failed to handle request - session token not found or invalid");
        tok = "26BE027D32CC42329DEC92D517B44E9E";
        ret = content(null, VERSION);
        assertResponseContainsError(ret, "Failed to handle request - unknown session token (maybe expired session)");
        tok = null;// Cleanup token for next tests.

    }
}

