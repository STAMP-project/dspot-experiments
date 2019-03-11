/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.rest;


import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.POST;
import java.io.IOException;
import org.junit.Test;


public class HealthCheckHandlerTest {
    private final RestServerState restServerState;

    private final String healthCheckUri = "/healthCheck";

    private final String goodStr = "GOOD";

    private final String badStr = "BAD";

    public HealthCheckHandlerTest() {
        this.restServerState = new RestServerState(healthCheckUri);
    }

    /**
     * Tests for the common case request handling flow for health check requests.
     *
     * @throws java.io.IOException
     * 		
     */
    @Test
    public void requestHandleWithHealthCheckRequestTest() throws IOException {
        // test with keep alive
        testHealthCheckRequest(GET, true, true);
        testHealthCheckRequest(GET, false, true);
        // test without keep alive
        testHealthCheckRequest(GET, true, false);
        testHealthCheckRequest(GET, false, false);
    }

    /**
     * Tests non health check requests handling
     *
     * @throws IOException
     * 		
     */
    @Test
    public void requestHandleWithNonHealthCheckRequestTest() throws IOException {
        testNonHealthCheckRequest(POST, "POST");
        testNonHealthCheckRequest(GET, "GET");
        testNonHealthCheckRequest(DELETE, "DELETE");
    }
}

