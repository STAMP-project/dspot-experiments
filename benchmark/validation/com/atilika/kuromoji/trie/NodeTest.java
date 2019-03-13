/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
/**
 * -*
 * Copyright ? 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the
 * License is distributed with this work in the LICENSE.md file.  You may
 * also obtain a copy of the License from
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atilika.kuromoji.trie;


import org.junit.Assert;
import org.junit.Test;


public class NodeTest {
    @Test
    public void testNode() {
        Trie trie = new Trie();
        Trie.Node node = trie.new Node('!');
        Assert.assertEquals('!', node.getKey());
        node = trie.new Node('1');
        Assert.assertEquals('1', node.getKey());
        node = trie.new Node('a');
        Assert.assertEquals('a', node.getKey());
        node = trie.new Node('?');
        Assert.assertEquals('?', node.getKey());
        node = trie.new Node('?');
        Assert.assertEquals('?', node.getKey());
        node = trie.new Node('?');
        Assert.assertEquals('?', node.getKey());
        node = trie.new Node('?');
        Assert.assertEquals('?', node.getKey());
    }

    @Test
    public void testAddChild() {
        Trie trie = new Trie();
        Trie.Node node = trie.new Node('a');
        Trie.Node returnedNode = node.addChild(trie.new Node('b'));
        Assert.assertEquals('b', returnedNode.getKey());
        Assert.assertEquals(1, node.getChildren().size());
        Assert.assertEquals('b', node.getChildren().get(0).getKey());
        returnedNode = node.addChild(trie.new Node('c'));
        Assert.assertEquals('c', returnedNode.getKey());
        Assert.assertEquals(2, node.getChildren().size());
        Assert.assertEquals('c', node.getChildren().get(1).getKey());
    }

    @Test
    public void testAdd() {
        Trie trie = new Trie();
        Trie.Node node = trie.new Node('a');
        node.add("");
        Assert.assertEquals(0, node.getChildren().size());
        node = trie.new Node('a');
        node.add("b");
        Assert.assertEquals(1, node.getChildren().size());
        Assert.assertEquals('b', node.getChildren().get(0).getKey());
        node = trie.new Node('a');
        node.add("bc");
        Trie.Node b = node.getChildren().get(0);
        Assert.assertEquals(1, node.getChildren().size());
        Assert.assertEquals('b', b.getKey());
        Assert.assertEquals(1, b.getChildren().size());
        Trie.Node c = b.getChildren().get(0);
        Assert.assertEquals('c', c.getKey());
        Assert.assertEquals(0, c.getChildren().size());
        node.add("bd");
        b = node.getChildren().get(0);
        Assert.assertEquals(1, node.getChildren().size());
        Assert.assertEquals('b', b.getKey());
        Assert.assertEquals(2, b.getChildren().size());
        c = b.getChildren().get(0);
        Assert.assertEquals('c', c.getKey());
        Assert.assertEquals(0, c.getChildren().size());
        Trie.Node d = b.getChildren().get(1);
        Assert.assertEquals('d', d.getKey());
        Assert.assertEquals(0, d.getChildren().size());
    }

    @Test
    public void testGetkey() {
        Trie trie = new Trie();
        Trie.Node node = trie.new Node('!');
        Assert.assertEquals('!', node.getKey());
        node = trie.new Node('1');
        Assert.assertEquals('1', node.getKey());
        node = trie.new Node('a');
        Assert.assertEquals('a', node.getKey());
        node = trie.new Node('?');
        Assert.assertEquals('?', node.getKey());
        node = trie.new Node('?');
        Assert.assertEquals('?', node.getKey());
        node = trie.new Node('?');
        Assert.assertEquals('?', node.getKey());
        node = trie.new Node('?');
        Assert.assertEquals('?', node.getKey());
    }

    @Test
    public void testHasSinglePath() {
        Trie trie = new Trie();
        Trie.Node node = trie.new Node('a');
        node.add("bcd");
        Assert.assertEquals(true, node.hasSinglePath());
        node.add("bce");
        Assert.assertEquals(false, node.hasSinglePath());
    }

    @Test
    public void testGetChildren() {
        Trie trie = new Trie();
        Trie.Node node = trie.new Node('a');
        node.add("bcd");
        node.add("bde");
        node.add("xyz");
        Assert.assertEquals(2, node.getChildren().size());
        Assert.assertEquals('b', node.getChildren().get(0).getKey());
        Assert.assertEquals('x', node.getChildren().get(1).getKey());
    }
}

