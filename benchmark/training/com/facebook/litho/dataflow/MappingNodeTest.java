/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho.dataflow;


import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class MappingNodeTest {
    private MockTimingSource mTestTimingSource;

    private DataFlowGraph mDataFlowGraph;

    @Test
    public void testMappingNodeWithinRange() {
        SettableNode settableNode = new SettableNode();
        MappingNode mappingNode = new MappingNode();
        SimpleNode middle = new SimpleNode();
        OutputOnlyNode destination = new OutputOnlyNode();
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(settableNode, mappingNode);
        binding.addBinding(mappingNode, middle);
        binding.addBinding(new ConstantNode(0.0F), mappingNode, MappingNode.INITIAL_INPUT);
        binding.addBinding(new ConstantNode(100.0F), mappingNode, MappingNode.END_INPUT);
        binding.addBinding(middle, destination);
        binding.activate();
        settableNode.setValue(0);
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(0.0F);
        settableNode.setValue(0.2F);
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(20.0F);
        settableNode.setValue(0.7F);
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(70.0F);
        settableNode.setValue(1.0F);
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(100.0F);
    }

    @Test
    public void testMappingNodeOutsideRange() {
        SettableNode settableNode = new SettableNode();
        MappingNode mappingNode = new MappingNode();
        SimpleNode middle = new SimpleNode();
        OutputOnlyNode destination = new OutputOnlyNode();
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(settableNode, mappingNode);
        binding.addBinding(mappingNode, middle);
        binding.addBinding(new ConstantNode(0.0F), mappingNode, MappingNode.INITIAL_INPUT);
        binding.addBinding(new ConstantNode(100.0F), mappingNode, MappingNode.END_INPUT);
        binding.addBinding(middle, destination);
        binding.activate();
        settableNode.setValue((-0.1F));
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo((-10.0F));
        settableNode.setValue(0.0F);
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(0.0F);
        settableNode.setValue(0.5F);
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(50.0F);
        settableNode.setValue(1.5F);
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(150.0F);
    }
}

