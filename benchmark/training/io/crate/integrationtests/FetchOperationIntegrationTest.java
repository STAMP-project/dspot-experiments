/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.integrationtests;


import ESIntegTestCase.ClusterScope;
import Row.EMPTY;
import io.crate.planner.Merge;
import io.crate.planner.node.dql.QueryThenFetch;
import io.crate.planner.operators.LogicalPlan;
import io.crate.testing.TestingRowConsumer;
import java.util.List;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


@ClusterScope(numDataNodes = 2, numClientNodes = 0)
public class FetchOperationIntegrationTest extends SQLTransportIntegrationTest {
    @SuppressWarnings("ConstantConditions")
    @Test
    public void testFetchProjection() throws Exception {
        setUpCharacters();
        SQLTransportIntegrationTest.PlanForNode plan = plan("select id, name, substr(name, 2) from characters order by id");
        QueryThenFetch qtf = ((QueryThenFetch) (((LogicalPlan) (plan.plan)).build(plan.plannerContext, null, (-1), 0, null, null, EMPTY, SubQueryResults.EMPTY)));
        assertThat(qtf.subPlan(), Matchers.instanceOf(Merge.class));
        Merge merge = ((Merge) (qtf.subPlan()));
        assertThat(nodeReaders(), Matchers.notNullValue());
        assertThat(readerIndices(), Matchers.notNullValue());
        TestingRowConsumer consumer = execute(plan);
        List<Object[]> result = consumer.getResult();
        assertThat(result.size(), Is.is(2));
        assertThat(result.get(0).length, Is.is(3));
        assertThat(result.get(0)[0], Is.is(1));
        assertThat(result.get(0)[1], Is.is("Arthur"));
        assertThat(result.get(0)[2], Is.is("rthur"));
        assertThat(result.get(1)[0], Is.is(2));
        assertThat(result.get(1)[1], Is.is("Ford"));
        assertThat(result.get(1)[2], Is.is("ord"));
    }
}

