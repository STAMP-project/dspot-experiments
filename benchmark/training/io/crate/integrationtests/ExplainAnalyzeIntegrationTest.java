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
package io.crate.integrationtests;


import ESIntegTestCase.ClusterScope;
import java.util.List;
import java.util.Map;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;


@ClusterScope(numDataNodes = 2)
public class ExplainAnalyzeIntegrationTest extends SQLTransportIntegrationTest {
    @Test
    public void testExplainAnalyzeReportsExecutionTimesOnBothNodesInclQueryBreakdown() {
        execute("explain analyze select * from locations where name like 'a%' or name = 'foo' order by date desc");
        Map<String, Object> analysis = ((Map<String, Object>) (response.rows()[0][0]));
        Map<String, Object> executeAnalysis = ((Map<String, Object>) (analysis.get("Execute")));
        assertThat(executeAnalysis, Matchers.is(Matchers.notNullValue()));
        assertTrue(executeAnalysis.keySet().contains("Total"));
        Map<String, Map<String, Object>> phasesAnalysis = ((Map<String, Map<String, Object>>) (executeAnalysis.get("Phases")));
        assertThat(phasesAnalysis, Matchers.is(Matchers.notNullValue()));
        assertThat(phasesAnalysis.size(), Matchers.is(3));
        DiscoveryNodes nodes = clusterService().state().nodes();
        for (DiscoveryNode discoveryNode : nodes) {
            if (discoveryNode.isDataNode()) {
                Object actual = executeAnalysis.get(discoveryNode.getId());
                assertThat(actual, Matchers.instanceOf(Map.class));
                Map<String, Object> timings = ((Map) (actual));
                assertThat(timings, Matchers.hasKey("QueryBreakdown"));
                Map<String, Object> queryBreakdown = ((Map) (((List) (timings.get("QueryBreakdown"))).get(0)));
                assertThat(queryBreakdown, Matchers.hasEntry("QueryName", "BooleanQuery"));
            }
        }
    }

    @Test
    public void testExplainSelectWithoutJobExecutionContexts() {
        execute("explain analyze select 1");
        Map<String, Object> analysis = ((Map<String, Object>) (response.rows()[0][0]));
        Map<String, Object> executeAnalysis = ((Map<String, Object>) (analysis.get("Execute")));
        assertTrue(executeAnalysis.keySet().contains("Total"));
        DiscoveryNodes nodes = clusterService().state().nodes();
        List<Matcher<String>> nodeIds = new java.util.ArrayList(nodes.getSize());
        for (DiscoveryNode discoveryNode : nodes) {
            nodeIds.add(Matchers.is(discoveryNode.getId()));
        }
        assertThat(executeAnalysis.keySet(), Matchers.hasItems(Matchers.is("Total"), Matchers.anyOf(nodeIds.toArray(new Matcher[]{  }))));
    }
}

