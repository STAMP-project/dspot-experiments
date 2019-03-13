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
package io.crate.planner.statement;


import DocSysColumns.DOC;
import io.crate.execution.dsl.phases.RoutedCollectPhase;
import io.crate.execution.dsl.projection.WriterProjection;
import io.crate.metadata.Reference;
import io.crate.planner.Merge;
import io.crate.planner.node.dql.Collect;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import org.hamcrest.Matchers;
import org.junit.Test;

import static java.util.stream.Collectors.toSet;


public class CopyToPlannerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testCopyToWithColumnsReferenceRewrite() {
        Merge plan = plan("copy users (name) to directory '/tmp'");
        Collect innerPlan = ((Collect) (plan.subPlan()));
        RoutedCollectPhase node = ((RoutedCollectPhase) (innerPlan.collectPhase()));
        Reference nameRef = ((Reference) (node.toCollect().get(0)));
        assertThat(nameRef.column().name(), Matchers.is(DOC.name()));
        assertThat(nameRef.column().path().get(0), Matchers.is("name"));
    }

    @Test
    public void testCopyToWithPartitionedGeneratedColumn() {
        // test that generated partition column is NOT exported
        Merge plan = plan("copy parted_generated to directory '/tmp'");
        Collect innerPlan = ((Collect) (plan.subPlan()));
        RoutedCollectPhase node = ((RoutedCollectPhase) (innerPlan.collectPhase()));
        WriterProjection projection = ((WriterProjection) (node.projections().get(0)));
        assertThat(projection.overwrites().size(), Matchers.is(0));
    }

    @Test
    public void testCopyToWithPartitionInWhereClauseRoutesToPartitionIndexOnly() {
        Merge merge = plan("copy parted where date = 1395874800000 to directory '/tmp/foo'");
        Collect collect = ((Collect) (merge.subPlan()));
        String expectedIndex = asIndexName();
        assertThat(routing().locations().values().stream().flatMap(( shardsByIndices) -> shardsByIndices.keySet().stream()).collect(toSet()), Matchers.contains(expectedIndex));
    }

    @Test
    public void testCopyToWithInvalidPartitionInWhereClause() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Given partition ident does not match partition evaluated from where clause");
        plan("copy parted partition (date=1395874800000) where date = 1395961200000 to directory '/tmp/foo'");
    }
}

