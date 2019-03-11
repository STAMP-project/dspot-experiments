/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.engine.collect.sources;


import io.crate.test.integration.CrateUnitTest;
import java.util.Collection;
import java.util.List;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.hamcrest.Matchers;
import org.junit.Test;


public class NodeStatsCollectSourceTest extends CrateUnitTest {
    private Collection<DiscoveryNode> discoveryNodes;

    @Test
    public void testFilterNodesById() throws Exception {
        List<DiscoveryNode> discoveryNodes = filterNodes("id = 'node-3'");
        assertThat(discoveryNodes.size(), Matchers.is(1));
        assertThat(discoveryNodes.get(0).getId(), Matchers.is("node-3"));
        // Filter for two nodes by id
        discoveryNodes = filterNodes("id = 'node-3' or id ='node-2' or id='unknown'");
        assertThat(discoveryNodes.size(), Matchers.is(2));
        assertThat(discoveryNodes.get(0).getId(), Matchers.is("node-2"));
        assertThat(discoveryNodes.get(1).getId(), Matchers.is("node-3"));
    }

    @Test
    public void testFilterNodesByName() throws Exception {
        List<DiscoveryNode> discoveryNodes = filterNodes("name = 'Arthur'");
        assertThat(discoveryNodes.size(), Matchers.is(1));
        assertThat(discoveryNodes.get(0).getId(), Matchers.is("node-3"));
        // Filter for two nodes by id
        discoveryNodes = filterNodes("name = 'Arthur' or name ='Trillian' or name='unknown'");
        assertThat(discoveryNodes.size(), Matchers.is(2));
        assertThat(discoveryNodes.get(0).getId(), Matchers.is("node-2"));
        assertThat(discoveryNodes.get(1).getId(), Matchers.is("node-3"));
    }

    @Test
    public void testMixedFilter() throws Exception {
        List<DiscoveryNode> discoveryNodes = filterNodes("name = 'Arthur' or id ='node-2' or name='unknown'");
        assertThat(discoveryNodes.size(), Matchers.is(2));
        assertThat(discoveryNodes.get(0).getId(), Matchers.is("node-2"));
        assertThat(discoveryNodes.get(1).getId(), Matchers.is("node-3"));
        discoveryNodes = filterNodes("name = 'Arthur' or id ='node-2' or hostname='unknown'");
        assertThat(discoveryNodes.size(), Matchers.is(3));
        discoveryNodes = filterNodes("name = 'Arthur' or id ='node-2' and hostname='unknown'");
        assertThat(discoveryNodes.size(), Matchers.is(2));
        assertThat(discoveryNodes.get(0).getId(), Matchers.is("node-2"));
        assertThat(discoveryNodes.get(1).getId(), Matchers.is("node-3"));
        discoveryNodes = filterNodes("name = 'Arthur' and id = 'node-2'");
        assertThat(discoveryNodes.size(), Matchers.is(0));
    }

    @Test
    public void testNoLocalInfoInWhereClause() throws Exception {
        List<DiscoveryNode> discoveryNodes = filterNodes("hostname = 'localhost'");
        assertThat(discoveryNodes.size(), Matchers.is(3));
    }
}

