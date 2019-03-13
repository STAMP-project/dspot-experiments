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


import android.view.animation.Interpolator;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class InterpolatorNodeTest {
    private MockTimingSource mTestTimingSource;

    private DataFlowGraph mDataFlowGraph;

    @Test
    public void testInterpolatorNode() {
        SettableNode settableNode = new SettableNode();
        InterpolatorNode interpolatorNode = new InterpolatorNode(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
        SimpleNode middle = new SimpleNode();
        OutputOnlyNode destination = new OutputOnlyNode();
        GraphBinding binding = GraphBinding.create(mDataFlowGraph);
        binding.addBinding(settableNode, interpolatorNode);
        binding.addBinding(interpolatorNode, middle);
        binding.addBinding(middle, destination);
        binding.activate();
        settableNode.setValue(0);
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(0.0F);
        settableNode.setValue(0.5F);
        mTestTimingSource.step(1);
        assertThat(((getValue()) < 1)).isTrue();
        assertThat(((getValue()) > 0)).isTrue();
        settableNode.setValue(1.0F);
        mTestTimingSource.step(1);
        assertThat(getValue()).isEqualTo(1.0F);
    }
}

