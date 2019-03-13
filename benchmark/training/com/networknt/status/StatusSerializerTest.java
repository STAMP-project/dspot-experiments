/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.status;


import com.networknt.config.Config;
import org.junit.Assert;
import org.junit.Test;


public class StatusSerializerTest {
    private static Config config = null;

    private static final String homeDir = System.getProperty("user.home");

    @Test
    public void testToString() {
        Status status = new Status("ERR10001");
        System.out.println(status);
        // test with ErrorStatusRootStatusSerializer
        Assert.assertEquals("{ \"error\" : {\"statusCode\":401,\"code\":\"ERR10001\",\"message\":\"AUTH_TOKEN_EXPIRED\",\"description\":\"Jwt token in authorization header expired\"} }", status.toString());
    }

    @Test
    public void testToStringWithArgs() {
        Status status = new Status("ERR11000", "parameter name", "original url");
        System.out.println(status);
        // test with ErrorStatusRootStatusSerializer
        Assert.assertEquals("{ \"error\" : {\"statusCode\":400,\"code\":\"ERR11000\",\"message\":\"VALIDATOR_REQUEST_PARAMETER_QUERY_MISSING\",\"description\":\"Query parameter parameter name is required on path original url but not found in request.\"} }", status.toString());
    }
}

