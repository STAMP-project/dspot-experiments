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
public class DataFlowGraphTest {
    private MockTimingSource mTestTimingSource;

    private DataFlowGraph mDataFlowGraph;

    @Test
    public void testSimpleGraph() {
        SettableNode source = new SettableNode();
        SimpleNode middle = new SimpleNode();
        OutputOnlyNode destination = new OutputOnlyNode();
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(source, middle);
        binding.addBinding(middle, destination);
        binding.activate();
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(0.0F);
        assertThat(getValue()).isEqualTo(0.0F);
        source.setValue(37);
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(37.0F);
        assertThat(getValue()).isEqualTo(37.0F);
    }

    @Test
    public void testSimpleUpdatingGraph() {
        NumFramesNode source = new NumFramesNode();
        SimpleNode middle = new SimpleNode();
        OutputOnlyNode destination = new OutputOnlyNode();
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(source, middle);
        binding.addBinding(middle, destination);
        binding.activate();
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(1.0F);
        assertThat(getValue()).isEqualTo(1.0F);
        mTestTimingSource.step(39);
        assertThat(getValue()).isEqualTo(40.0F);
        assertThat(getValue()).isEqualTo(40.0F);
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(41.0F);
        assertThat(getValue()).isEqualTo(41.0F);
    }

    @Test
    public void testGraphWithMultipleOutputs() {
        NumFramesNode source = new NumFramesNode();
        SimpleNode middle = new SimpleNode();
        OutputOnlyNode dest1 = new OutputOnlyNode();
        OutputOnlyNode dest2 = new OutputOnlyNode();
        OutputOnlyNode dest3 = new OutputOnlyNode();
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(source, middle);
        binding.addBinding(middle, dest1);
        binding.addBinding(middle, dest2);
        binding.addBinding(source, dest3);
        binding.activate();
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(1.0F);
        assertThat(getValue()).isEqualTo(1.0F);
        assertThat(getValue()).isEqualTo(1.0F);
        mTestTimingSource.step(39);
        assertThat(getValue()).isEqualTo(40.0F);
        assertThat(getValue()).isEqualTo(40.0F);
        assertThat(getValue()).isEqualTo(40.0F);
    }

    @Test
    public void testRebindNode() {
        SettableNode source = new SettableNode();
        SimpleNode middle = new SimpleNode();
        OutputOnlyNode destination = new OutputOnlyNode();
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(source, middle);
        binding.addBinding(middle, destination);
        binding.activate();
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(0.0F);
        SettableNode newSource = new SettableNode();
        GraphBinding secondBinding = GraphBinding.create(mDataFlowGraph);
        secondBinding.addBinding(newSource, destination);
        secondBinding.activate();
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(0.0F);
        newSource.setValue(11);
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(11.0F);
    }

    @Test
    public void testMultipleInputs() {
        AdditionNode dest = new AdditionNode();
        SettableNode a = new SettableNode();
        SettableNode b = new SettableNode();
        a.setValue(1776);
        b.setValue(1812);
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(a, dest, "a");
        binding.addBinding(b, dest, "b");
        binding.activate();
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(3588.0F);
    }

    @Test(expected = DetectedCycleException.class)
    public void testSimpleCycle() {
        SimpleNode node1 = new SimpleNode();
        SimpleNode node2 = new SimpleNode();
        SimpleNode node3 = new SimpleNode();
        SimpleNode node4 = new SimpleNode();
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(node1, node2);
        binding.addBinding(node2, node3);
        binding.addBinding(node3, node1);
        binding.addBinding(node1, node4);
        binding.activate();
        mTestTimingSource.step(1);
    }

    @Test(expected = DetectedCycleException.class)
    public void testCycleWithoutLeaves() {
        SimpleNode node1 = new SimpleNode();
        SimpleNode node2 = new SimpleNode();
        SimpleNode node3 = new SimpleNode();
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(node1, node2);
        binding.addBinding(node2, node3);
        binding.addBinding(node3, node1);
        binding.activate();
        mTestTimingSource.step(1);
    }
}

