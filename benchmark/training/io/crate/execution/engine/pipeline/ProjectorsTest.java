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
package io.crate.execution.engine.pipeline;


import RowGranularity.DOC;
import io.crate.breaker.RamAccountingContext;
import io.crate.execution.dsl.projection.FilterProjection;
import io.crate.execution.dsl.projection.GroupProjection;
import io.crate.execution.engine.aggregation.GroupingProjector;
import io.crate.expression.symbol.AggregateMode;
import io.crate.expression.symbol.InputColumn;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.RowGranularity;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.elasticsearch.common.breaker.CircuitBreaker;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ProjectorsTest extends CrateDummyClusterServiceUnitTest {
    private RamAccountingContext RAM_ACCOUNTING_CONTEXT = new RamAccountingContext("dummy", new org.elasticsearch.common.breaker.NoopCircuitBreaker(CircuitBreaker.FIELDDATA));

    private ProjectionToProjectorVisitor projectorFactory;

    @Test
    public void testProjectionsWithCorrectGranularityAreApplied() {
        GroupProjection groupProjection = new GroupProjection(new ArrayList(), new ArrayList(), AggregateMode.ITER_FINAL, RowGranularity.SHARD);
        FilterProjection filterProjection = new FilterProjection(new InputColumn(0), Collections.emptyList());
        filterProjection.requiredGranularity(DOC);
        Projectors projectors = new Projectors(Arrays.asList(filterProjection, groupProjection), UUID.randomUUID(), CoordinatorTxnCtx.systemTransactionContext(), RAM_ACCOUNTING_CONTEXT, projectorFactory);
        assertThat(projectors.providesIndependentScroll(), Matchers.is(true));
        assertThat(projectors.projectors.size(), Matchers.is(1));
        assertThat(projectors.projectors.get(0), Matchers.instanceOf(GroupingProjector.class));
    }
}

