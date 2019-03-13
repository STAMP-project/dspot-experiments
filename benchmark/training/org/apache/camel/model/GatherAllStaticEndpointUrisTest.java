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
package org.apache.camel.model;


import java.util.Set;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class GatherAllStaticEndpointUrisTest extends ContextTestSupport {
    @Test
    public void testGatherAllStaticEndpointUris() throws Exception {
        RouteDefinition route = context.getRouteDefinition("foo");
        Set<String> uris = RouteDefinitionHelper.gatherAllStaticEndpointUris(context, route, true, true);
        Assert.assertNotNull(uris);
        Assert.assertEquals(3, uris.size());
        RouteDefinition route2 = context.getRouteDefinition("bar");
        Set<String> uris2 = RouteDefinitionHelper.gatherAllStaticEndpointUris(context, route2, true, true);
        Assert.assertNotNull(uris2);
        Assert.assertEquals(2, uris2.size());
        Set<String> uris2out = RouteDefinitionHelper.gatherAllStaticEndpointUris(context, route2, false, true);
        Assert.assertNotNull(uris2out);
        Assert.assertEquals(1, uris2out.size());
        String json = context.createRouteStaticEndpointJson(null);
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("{ \"uri\": \"direct://foo\" }"));
        Assert.assertTrue(json.contains("{ \"uri\": \"seda://bar\" }"));
    }
}

