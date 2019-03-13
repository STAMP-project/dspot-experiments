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
package org.apache.camel.coap;


import CoAPConstants.METHOD_RESTRICT_ALL;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.junit.Test;


public class CoAPMethodRestrictTest extends CoAPTestSupport {
    @Test
    public void testDefaultCoAPMethodRestrict() {
        NetworkConfig.createStandardWithoutFile();
        // All request methods should be valid on this endpoint
        assertCoAPMethodRestrictResponse("/test", METHOD_RESTRICT_ALL, "GET: /test");
    }

    @Test
    public void testSpecifiedCoAPMethodRestrict() {
        NetworkConfig.createStandardWithoutFile();
        // Only GET is valid for /test/a
        assertCoAPMethodRestrictResponse("/test/a", "GET", "GET: /test/a");
        // Only DELETE is valid for /test/a/b
        assertCoAPMethodRestrictResponse("/test/a/b", "DELETE", "DELETE: /test/a/b");
        // Only DELETE & GET are valid for /test/a/b/c
        assertCoAPMethodRestrictResponse("/test/a/b/c", "DELETE,GET", "DELETE & GET: /test/a/b/c");
        // Only GET is valid for /test/b
        assertCoAPMethodRestrictResponse("/test/b", "GET", "GET: /test/b");
    }
}

