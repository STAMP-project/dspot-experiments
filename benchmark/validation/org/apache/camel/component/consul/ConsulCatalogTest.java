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
package org.apache.camel.component.consul;


import ConsulCatalogActions.LIST_DATACENTERS;
import ConsulCatalogActions.LIST_NODES;
import ConsulConstants.CONSUL_ACTION;
import com.orbitz.consul.model.health.Node;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ConsulCatalogTest extends ConsulTestSupport {
    @Test
    public void testListDatacenters() throws Exception {
        List<String> ref = getConsul().catalogClient().getDatacenters();
        List<String> res = fluentTemplate().withHeader(CONSUL_ACTION, LIST_DATACENTERS).to("direct:consul").request(List.class);
        Assert.assertFalse(ref.isEmpty());
        Assert.assertFalse(res.isEmpty());
        Assert.assertEquals(ref, res);
    }

    @Test
    public void testListNodes() throws Exception {
        List<Node> ref = getConsul().catalogClient().getNodes().getResponse();
        List<Node> res = fluentTemplate().withHeader(CONSUL_ACTION, LIST_NODES).to("direct:consul").request(List.class);
        Assert.assertFalse(ref.isEmpty());
        Assert.assertFalse(res.isEmpty());
        Assert.assertEquals(ref, res);
    }
}

