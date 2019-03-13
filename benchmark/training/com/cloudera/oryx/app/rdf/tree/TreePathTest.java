/**
 * Copyright (c) 2015, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.rdf.tree;


import TreePath.EMPTY;
import com.cloudera.oryx.common.OryxTest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;


public final class TreePathTest extends OryxTest {
    private static final TreePath LRL = EMPTY.extendLeft().extendRight().extendLeft();

    private static final TreePath LRR = EMPTY.extendLeft().extendRight().extendRight();

    private static final TreePath LR = EMPTY.extendLeft().extendRight();

    private static final TreePath R = EMPTY.extendRight();

    @Test
    public void testEquals() {
        assertEquals(TreePathTest.LRL, EMPTY.extendLeft().extendRight().extendLeft());
        assertNotEquals(EMPTY, TreePathTest.LRL);
        Collection<TreePath> paths = new HashSet<>();
        paths.add(TreePathTest.LRL);
        paths.add(TreePathTest.LRR);
        paths.add(TreePathTest.LRR);
        assertEquals(2, paths.size());
    }

    @Test
    public void testToString() {
        assertEquals("", EMPTY.toString());
        assertEquals("010", TreePathTest.LRL.toString());
        assertEquals("011", TreePathTest.LRR.toString());
    }

    @Test
    public void testOrder() {
        List<TreePath> paths = new java.util.ArrayList(Arrays.asList(TreePathTest.LRL, TreePathTest.R, TreePathTest.LR, TreePathTest.LRR));
        Collections.sort(paths);
        assertEquals(Arrays.asList(TreePathTest.LRL, TreePathTest.LR, TreePathTest.LRR, TreePathTest.R), paths);
    }
}

