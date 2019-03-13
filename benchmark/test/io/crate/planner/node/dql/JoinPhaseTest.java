/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
package io.crate.planner.node.dql;


import DataTypes.INTEGER;
import DataTypes.LONG;
import DataTypes.STRING;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import io.crate.execution.dsl.phases.HashJoinPhase;
import io.crate.execution.dsl.phases.MergePhase;
import io.crate.execution.dsl.phases.NestedLoopPhase;
import io.crate.execution.dsl.projection.TopNProjection;
import io.crate.expression.symbol.Literal;
import io.crate.expression.symbol.Symbol;
import io.crate.planner.node.dql.join.JoinType;
import io.crate.test.integration.CrateUnitTest;
import io.crate.types.DataTypes;
import java.util.Arrays;
import java.util.UUID;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


public class JoinPhaseTest extends CrateUnitTest {
    private TopNProjection topNProjection;

    private UUID jobId;

    private MergePhase mp1;

    private MergePhase mp2;

    private Symbol joinCondition;

    @Test
    public void testNestedLoopSerialization() throws Exception {
        NestedLoopPhase node = new NestedLoopPhase(jobId, 1, "nestedLoop", ImmutableList.of(topNProjection), mp1, mp2, 2, 3, Sets.newHashSet("node1", "node2"), JoinType.FULL, joinCondition, ImmutableList.of(LONG, STRING, new io.crate.types.ArrayType(DataTypes.INTEGER)), 32L, 100000, true);
        BytesStreamOutput output = new BytesStreamOutput();
        node.writeTo(output);
        StreamInput input = output.bytes().streamInput();
        NestedLoopPhase node2 = new NestedLoopPhase(input);
        assertThat(node.nodeIds(), CoreMatchers.is(node2.nodeIds()));
        assertThat(node.jobId(), CoreMatchers.is(node2.jobId()));
        assertThat(node.joinCondition(), CoreMatchers.is(node2.joinCondition()));
        assertThat(node.type(), CoreMatchers.is(node2.type()));
        assertThat(node.nodeIds(), CoreMatchers.is(node2.nodeIds()));
        assertThat(node.jobId(), CoreMatchers.is(node2.jobId()));
        assertThat(node.name(), CoreMatchers.is(node2.name()));
        assertThat(node.outputTypes(), CoreMatchers.is(node2.outputTypes()));
        assertThat(node.joinType(), CoreMatchers.is(node2.joinType()));
        assertThat(node.joinCondition(), CoreMatchers.is(node2.joinCondition()));
        assertThat(node.estimatedRowsSizeLeft, CoreMatchers.is(32L));
        assertThat(node.estimatedNumberOfRowsLeft, CoreMatchers.is(100000L));
        assertThat(node.blockNestedLoop, CoreMatchers.is(true));
    }

    @Test
    public void testHashJoinSerialization() throws Exception {
        HashJoinPhase node = new HashJoinPhase(jobId, 1, "nestedLoop", ImmutableList.of(topNProjection), mp1, mp2, 2, 3, Sets.newHashSet("node1", "node2"), joinCondition, Arrays.asList(Literal.of("testLeft"), Literal.of(10)), Arrays.asList(Literal.of("testRight"), Literal.of(20)), Arrays.asList(STRING, INTEGER), 111, 222);
        BytesStreamOutput output = new BytesStreamOutput();
        node.writeTo(output);
        StreamInput input = output.bytes().streamInput();
        HashJoinPhase node2 = new HashJoinPhase(input);
        assertThat(node.nodeIds(), CoreMatchers.is(node2.nodeIds()));
        assertThat(node.jobId(), CoreMatchers.is(node2.jobId()));
        assertThat(node.joinCondition(), CoreMatchers.is(node2.joinCondition()));
        assertThat(node.type(), CoreMatchers.is(node2.type()));
        assertThat(node.nodeIds(), CoreMatchers.is(node2.nodeIds()));
        assertThat(node.jobId(), CoreMatchers.is(node2.jobId()));
        assertThat(node.name(), CoreMatchers.is(node2.name()));
        assertThat(node.outputTypes(), CoreMatchers.is(node2.outputTypes()));
        assertThat(node.joinType(), CoreMatchers.is(node2.joinType()));
        assertThat(node.joinCondition(), CoreMatchers.is(node2.joinCondition()));
        assertThat(node.leftJoinConditionInputs(), CoreMatchers.is(node2.leftJoinConditionInputs()));
        assertThat(node.rightJoinConditionInputs(), CoreMatchers.is(node2.rightJoinConditionInputs()));
        assertThat(node.numLeftOutputs(), CoreMatchers.is(node2.numLeftOutputs()));
        assertThat(node.numRightOutputs(), CoreMatchers.is(node2.numRightOutputs()));
        assertThat(node.leftOutputTypes(), CoreMatchers.is(node2.leftOutputTypes()));
        assertThat(node.estimatedRowSizeForLeft(), CoreMatchers.is(node2.estimatedRowSizeForLeft()));
        assertThat(node.numberOfRowsForLeft(), CoreMatchers.is(node2.numberOfRowsForLeft()));
    }
}

