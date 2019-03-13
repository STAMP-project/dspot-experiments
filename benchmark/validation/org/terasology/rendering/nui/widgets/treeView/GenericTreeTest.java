/**
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.nui.widgets.treeView;


import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class GenericTreeTest {
    private List<GenericTree<Integer>> nodes = Lists.newArrayList();

    @Test
    public void testGetParent() {
        Assert.assertNull(nodes.get(0).getParent());
        Assert.assertEquals(nodes.get(0), nodes.get(4).getParent());
        Assert.assertEquals(nodes.get(9), nodes.get(10).getParent());
    }

    @Test
    public void testGetChildren() {
        Assert.assertEquals(Lists.newArrayList(), nodes.get(10).getChildren());
        Assert.assertEquals(Arrays.asList(nodes.get(6), nodes.get(9)), nodes.get(5).getChildren());
    }

    @Test
    public void testContainsChild() {
        Assert.assertTrue(nodes.get(0).containsChild(nodes.get(1)));
        Assert.assertTrue(nodes.get(0).containsChild(nodes.get(4)));
        Assert.assertTrue(nodes.get(9).containsChild(nodes.get(10)));
        Assert.assertFalse(nodes.get(7).containsChild(nodes.get(3)));
    }

    @Test
    public void testDepthFirstIterator() {
        List<GenericTree<Integer>> expected = Arrays.asList(nodes.get(0), nodes.get(1), nodes.get(2), nodes.get(3), nodes.get(7), nodes.get(4), nodes.get(8), nodes.get(5), nodes.get(6), nodes.get(9), nodes.get(10));
        List<GenericTree<Integer>> actual = Lists.newArrayList();
        Iterator i = nodes.get(0).getDepthFirstIterator(false);
        while (i.hasNext()) {
            actual.add(((GenericTree<Integer>) (i.next())));
        } 
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDepthFirstIteratorIterateExpandedOnly() {
        List<GenericTree<Integer>> expected = Arrays.asList(nodes.get(0), nodes.get(1), nodes.get(2), nodes.get(4), nodes.get(5), nodes.get(6), nodes.get(9));
        List<GenericTree<Integer>> actual = Lists.newArrayList();
        Iterator i = nodes.get(0).getDepthFirstIterator(true);
        while (i.hasNext()) {
            actual.add(((GenericTree<Integer>) (i.next())));
        } 
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNodeDepth() {
        Assert.assertEquals(0, nodes.get(0).getDepth());
        Assert.assertEquals(1, nodes.get(1).getDepth());
        Assert.assertEquals(2, nodes.get(8).getDepth());
        Assert.assertEquals(3, nodes.get(10).getDepth());
    }

    @Test
    public void testGetRoot() {
        for (GenericTree<Integer> node : nodes) {
            Assert.assertEquals(nodes.get(0), node.getRoot());
        }
    }
}

