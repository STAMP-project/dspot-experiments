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
package io.crate.planner.operators;


import Plan.StatementType.INSERT;
import Plan.StatementType.SELECT;
import StatementClassifier.Classification;
import io.crate.planner.Plan;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Test;


public class StatementClassifierTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testClassifySelectStatements() {
        LogicalPlan plan = e.logicalPlan("SELECT 1");
        StatementClassifier.Classification classification = StatementClassifier.classify(plan);
        assertThat(classification.type(), Matchers.is(SELECT));
        assertThat(classification.labels(), Matchers.contains("Collect"));
        plan = e.logicalPlan("SELECT * FROM users WHERE id = 1");
        classification = StatementClassifier.classify(plan);
        assertThat(classification.type(), Matchers.is(SELECT));
        assertThat(classification.labels(), Matchers.contains("Get"));
        plan = e.logicalPlan("SELECT * FROM users ORDER BY id");
        classification = StatementClassifier.classify(plan);
        assertThat(classification.type(), Matchers.is(SELECT));
        assertThat(classification.labels(), Matchers.contains("Collect", "FetchOrEval", "Order"));
        plan = e.logicalPlan("SELECT a.id, b.id FROM users a, users b WHERE a.id = b.id");
        classification = StatementClassifier.classify(plan);
        assertThat(classification.type(), Matchers.is(SELECT));
        assertThat(classification.labels(), Matchers.contains("Collect", "HashJoin"));
        plan = e.logicalPlan("SELECT a.id, b.id FROM users a, users b WHERE a.id > b.id");
        classification = StatementClassifier.classify(plan);
        assertThat(classification.type(), Matchers.is(SELECT));
        assertThat(classification.labels(), Matchers.contains("Collect", "NestedLoopJoin"));
        plan = e.logicalPlan("SELECT id FROM users UNION ALL SELECT id FROM users");
        classification = StatementClassifier.classify(plan);
        assertThat(classification.type(), Matchers.is(SELECT));
        assertThat(classification.labels(), Matchers.contains("Collect", "Union"));
        plan = e.logicalPlan("SELECT count(*) FROM users");
        classification = StatementClassifier.classify(plan);
        assertThat(classification.type(), Matchers.is(SELECT));
        assertThat(classification.labels(), Matchers.contains("Count"));
        plan = e.logicalPlan("SELECT count(*), name FROM users GROUP BY 2");
        classification = StatementClassifier.classify(plan);
        assertThat(classification.type(), Matchers.is(SELECT));
        assertThat(classification.labels(), Matchers.contains("Collect", "FetchOrEval", "GroupHashAggregate"));
        plan = e.logicalPlan("SELECT * FROM users WHERE id = (SELECT 1) OR name = (SELECT 'Arthur')");
        classification = StatementClassifier.classify(plan);
        assertThat(classification.type(), Matchers.is(SELECT));
        assertThat(classification.labels(), Matchers.contains("Collect", "MultiPhase"));
    }

    @Test
    public void testClassifyInsertStatements() {
        Plan plan = e.plan("INSERT INTO users (id, name) VALUES (1, 'foo')");
        StatementClassifier.Classification classification = StatementClassifier.classify(plan);
        assertThat(classification.type(), Matchers.is(INSERT));
        assertThat(classification.labels(), Matchers.is(Collections.emptySet()));
        plan = e.logicalPlan("INSERT INTO users (id, name) (SELECT id, name FROM users)");
        classification = StatementClassifier.classify(plan);
        assertThat(classification.type(), Matchers.is(INSERT));
        assertThat(classification.labels(), Matchers.contains("Collect"));
        plan = e.logicalPlan("INSERT INTO users (id, name) (SELECT * FROM unnest([1], ['foo']))");
        classification = StatementClassifier.classify(plan);
        assertThat(classification.type(), Matchers.is(INSERT));
        assertThat(classification.labels(), Matchers.contains("Collect"));
    }
}

