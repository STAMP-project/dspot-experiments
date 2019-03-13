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


import io.crate.execution.dsl.phases.FileUriCollectPhase;
import io.crate.execution.dsl.projection.SourceIndexWriterProjection;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.Reference;
import io.crate.planner.node.dql.Collect;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import java.util.List;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class CopyStatementPlannerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testCopyFromPlan() {
        Collect plan = plan("copy users from '/path/to/file.extension'");
        assertThat(plan.collectPhase(), Matchers.instanceOf(FileUriCollectPhase.class));
        FileUriCollectPhase collectPhase = ((FileUriCollectPhase) (plan.collectPhase()));
        assertThat(value(), Is.is("/path/to/file.extension"));
    }

    @Test
    public void testCopyFromNumReadersSetting() {
        Collect plan = plan("copy users from '/path/to/file.extension' with (num_readers=1)");
        assertThat(plan.collectPhase(), Matchers.instanceOf(FileUriCollectPhase.class));
        FileUriCollectPhase collectPhase = ((FileUriCollectPhase) (plan.collectPhase()));
        assertThat(collectPhase.nodeIds().size(), Is.is(1));
    }

    @Test
    public void testCopyFromPlanWithParameters() {
        Collect collect = plan(("copy users " + "from '/path/to/file.ext' with (bulk_size=30, compression='gzip', shared=true)"));
        assertThat(collect.collectPhase(), Matchers.instanceOf(FileUriCollectPhase.class));
        FileUriCollectPhase collectPhase = ((FileUriCollectPhase) (collect.collectPhase()));
        SourceIndexWriterProjection indexWriterProjection = ((SourceIndexWriterProjection) (collectPhase.projections().get(0)));
        assertThat(indexWriterProjection.bulkActions(), Is.is(30));
        assertThat(collectPhase.compression(), Is.is("gzip"));
        assertThat(collectPhase.sharedStorage(), Is.is(true));
        // verify defaults:
        collect = plan("copy users from '/path/to/file.ext'");
        collectPhase = ((FileUriCollectPhase) (collect.collectPhase()));
        assertThat(collectPhase.compression(), Is.is(Matchers.nullValue()));
        assertThat(collectPhase.sharedStorage(), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testIdIsNotCollectedOrUsedAsClusteredBy() {
        Collect collect = plan("copy t1 from '/path/file.ext'");
        SourceIndexWriterProjection projection = ((SourceIndexWriterProjection) (collect.collectPhase().projections().get(0)));
        assertThat(projection.clusteredBy(), Is.is(Matchers.nullValue()));
        List<Symbol> toCollectSymbols = collect.collectPhase().toCollect();
        assertThat(toCollectSymbols.size(), Is.is(1));
        assertThat(toCollectSymbols.get(0), Matchers.instanceOf(Reference.class));
        Reference refToCollect = ((Reference) (toCollectSymbols.get(0)));
        assertThat(refToCollect.column().fqn(), Is.is("_raw"));
    }

    @Test
    public void testCopyFromPlanWithInvalidParameters() {
        expectedException.expect(IllegalArgumentException.class);
        plan("copy users from '/path/to/file.ext' with (bulk_size=-28)");
    }

    @Test
    public void testNodeFiltersNoMatch() {
        Collect cm = plan("copy users from '/path' with (node_filters={name='foobar'})");
        assertThat(cm.collectPhase().nodeIds().size(), Is.is(0));
    }
}

