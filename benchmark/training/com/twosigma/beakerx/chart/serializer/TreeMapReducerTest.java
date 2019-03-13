/**
 * Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.chart.serializer;


import java.util.List;
import net.sf.jtreemap.swing.TreeMapNode;
import org.junit.Test;


public class TreeMapReducerTest {
    public static final int LIMIT = 1000;

    private TreeMapReducer sut;

    @Test
    public void shouldNotLimitTree() {
        // given
        TreeMapNode root = createTree(250, 150, 100, 100);
        // when
        TreeMapNode limitedRoot = sut.limitTreeMap(root);
        // then
        assertThat(TreeMapNodeCounter.countAllNodes(limitedRoot)).isEqualTo(601);
    }

    @Test
    public void shouldLimitTree() {
        // given
        TreeMapNode root = createTree(100, 500, 200, 600);
        // when
        TreeMapNode limitedRoot = sut.limitTreeMap(root);
        // then
        assertThat(TreeMapNodeCounter.countAllNodes(limitedRoot)).isEqualTo(((TreeMapReducerTest.LIMIT) + 5));
        List<TreeMapNode> children1 = getLastLayerChildrenForChild(0, limitedRoot);
        assertThat(children1.size()).isEqualTo(351);
        List<TreeMapNode> children2 = getLastLayerChildrenForChild(1, limitedRoot);
        assertThat(children2.size()).isEqualTo(351);
    }
}

