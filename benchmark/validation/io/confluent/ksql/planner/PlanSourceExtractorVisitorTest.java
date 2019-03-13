/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.planner;


import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.planner.plan.PlanNode;
import java.util.Set;
import org.apache.kafka.common.utils.Utils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class PlanSourceExtractorVisitorTest {
    private MetaStore metaStore;

    @Test
    @SuppressWarnings("unchecked")
    public void shouldExtractCorrectSourceForSimpleQuery() {
        final PlanNode planNode = buildLogicalPlan("select col0 from TEST2 limit 5;");
        final PlanSourceExtractorVisitor planSourceExtractorVisitor = new PlanSourceExtractorVisitor();
        planSourceExtractorVisitor.process(planNode, null);
        final Set<String> sourceNames = planSourceExtractorVisitor.getSourceNames();
        MatcherAssert.assertThat(sourceNames.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(sourceNames, CoreMatchers.equalTo(Utils.mkSet("TEST2")));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldExtractCorrectSourceForJoinQuery() {
        final PlanNode planNode = buildLogicalPlan(("SELECT t1.col1, t2.col1, t1.col4, t2.col2 FROM test1 t1 LEFT JOIN " + "test2 t2 ON t1.col1 = t2.col1;"));
        final PlanSourceExtractorVisitor planSourceExtractorVisitor = new PlanSourceExtractorVisitor();
        planSourceExtractorVisitor.process(planNode, null);
        final Set<String> sourceNames = planSourceExtractorVisitor.getSourceNames();
        MatcherAssert.assertThat(sourceNames, CoreMatchers.equalTo(Utils.mkSet("TEST1", "TEST2")));
    }
}

