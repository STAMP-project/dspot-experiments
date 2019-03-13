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
package com.facebook.litho.animation;


import OutputUnitType.HOST;
import android.view.View;
import com.facebook.litho.OutputUnitsAffinityGroup;
import com.facebook.litho.dataflow.DataFlowGraph;
import com.facebook.litho.dataflow.GraphBinding;
import com.facebook.litho.dataflow.MockTimingSource;
import com.facebook.litho.dataflow.OutputOnlyNode;
import com.facebook.litho.dataflow.SettableNode;
import com.facebook.litho.dataflow.SimpleNode;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class AnimatedPropertyNodeTest {
    private MockTimingSource mTestTimingSource;

    private DataFlowGraph mDataFlowGraph;

    @Test
    public void testViewPropertyNodeWithInput() {
        View view = new View(application);
        OutputUnitsAffinityGroup<Object> group = new OutputUnitsAffinityGroup();
        group.add(HOST, view);
        SettableNode source = new SettableNode();
        SimpleNode middle = new SimpleNode();
        AnimatedPropertyNode destination = new AnimatedPropertyNode(group, AnimatedProperties.SCALE);
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(source, middle);
        binding.addBinding(middle, destination);
        binding.activate();
        mTestTimingSource.step(1);
        assertThat(view.getScaleX()).isEqualTo(0.0F);
        source.setValue(37);
        mTestTimingSource.step(1);
        assertThat(view.getScaleX()).isEqualTo(37.0F);
    }

    @Test
    public void testViewPropertyNodeWithInputAndOutput() {
        View view = new View(application);
        OutputUnitsAffinityGroup<Object> group = new OutputUnitsAffinityGroup();
        group.add(HOST, view);
        SettableNode source = new SettableNode();
        AnimatedPropertyNode animatedNode = new AnimatedPropertyNode(group, AnimatedProperties.SCALE);
        OutputOnlyNode destination = new OutputOnlyNode();
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(source, animatedNode);
        binding.addBinding(animatedNode, destination);
        binding.activate();
        mTestTimingSource.step(1);
        assertThat(view.getScaleX()).isEqualTo(0.0F);
        assertThat(getValue()).isEqualTo(0.0F);
        source.setValue(123);
        mTestTimingSource.step(1);
        assertThat(view.getScaleX()).isEqualTo(123.0F);
        assertThat(getValue()).isEqualTo(123.0F);
    }

    @Test
    public void testSettingMountContentOnNodeWithValue() {
        View view1 = new View(application);
        OutputUnitsAffinityGroup<Object> group1 = new OutputUnitsAffinityGroup();
        group1.add(HOST, view1);
        View view2 = new View(application);
        OutputUnitsAffinityGroup<Object> group2 = new OutputUnitsAffinityGroup();
        group2.add(HOST, view2);
        SettableNode source = new SettableNode();
        AnimatedPropertyNode animatedNode = new AnimatedPropertyNode(group1, AnimatedProperties.SCALE);
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(source, animatedNode);
        binding.activate();
        mTestTimingSource.step(1);
        assertThat(view1.getScaleX()).isEqualTo(0.0F);
        source.setValue(123);
        mTestTimingSource.step(1);
        assertThat(view1.getScaleX()).isEqualTo(123.0F);
        assertThat(view2.getScaleX()).isEqualTo(1.0F);
        animatedNode.setMountContentGroup(group2);
        assertThat(view2.getScaleX()).isEqualTo(123.0F);
    }
}

