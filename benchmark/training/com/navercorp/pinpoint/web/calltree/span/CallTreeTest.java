/**
 * Copyright 2015 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.calltree.span;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author jaehong.kim
 */
public class CallTreeTest {
    private static final boolean SYNC = false;

    private static final boolean ASYNC = true;

    private SpanCallTree callTree = new SpanCallTree(makeSpanAlign());

    private List<String> expectResult = new ArrayList<String>();

    @Test
    public void add() {
        expectResult.clear();
        expectResult.add("#");
        expectResult.add("##");
        expectResult.add("###");
        expectResult.add("####");
        expectResult.add("#####");
        callTree.add(1, makeSpanAlign(CallTreeTest.SYNC, ((short) (0))));
        callTree.add(2, makeSpanAlign(CallTreeTest.SYNC, ((short) (1))));
        callTree.add(3, makeSpanAlign(CallTreeTest.SYNC, ((short) (2))));
        callTree.add(4, makeSpanAlign(CallTreeTest.SYNC, ((short) (3))));
        assertDepth("add", callTree, expectResult);
    }

    @Test
    public void addAndSort() {
        expectResult.clear();
        expectResult.add("#");
        expectResult.add("##");
        expectResult.add("###");
        expectResult.add("####");
        expectResult.add("#####");
        callTree.add(1, makeSpanAlign(CallTreeTest.SYNC, ((short) (0))));
        callTree.add(2, makeSpanAlign(CallTreeTest.SYNC, ((short) (1))));
        callTree.add(3, makeSpanAlign(CallTreeTest.SYNC, ((short) (2))));
        callTree.add(4, makeSpanAlign(CallTreeTest.SYNC, ((short) (3))));
        assertDepth("addAndSort", callTree, expectResult);
        callTree.sort();
        assertDepth("addAndSort", callTree, expectResult);
    }

    @Test
    public void addLevel() {
        expectResult.clear();
        expectResult.add("#");
        expectResult.add("##");
        expectResult.add("###");
        expectResult.add("####");
        expectResult.add("#####");
        expectResult.add("#####");
        expectResult.add("#####");
        expectResult.add("#####");
        callTree.add(1, makeSpanAlign(CallTreeTest.SYNC, ((short) (0))));
        callTree.add(2, makeSpanAlign(CallTreeTest.SYNC, ((short) (1))));
        callTree.add(3, makeSpanAlign(CallTreeTest.SYNC, ((short) (2))));
        callTree.add(4, makeSpanAlign(CallTreeTest.SYNC, ((short) (3))));
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (4))));
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (5))));
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (6))));
        assertDepth("addLevel", callTree, expectResult);
    }

    @Test
    public void addComplex() {
        expectResult.clear();
        expectResult.add("#");
        expectResult.add("##");
        expectResult.add("###");
        expectResult.add("####");
        expectResult.add("#####");
        expectResult.add("######");
        expectResult.add("######");
        expectResult.add("######");
        expectResult.add("######");
        expectResult.add("######");
        expectResult.add("#####");
        expectResult.add("#####");
        expectResult.add("######");
        expectResult.add("#######");
        expectResult.add("########");
        expectResult.add("########");
        expectResult.add("#####");
        expectResult.add("#####");
        expectResult.add("#####");
        callTree.add(1, makeSpanAlign(CallTreeTest.SYNC, ((short) (0))));
        callTree.add(2, makeSpanAlign(CallTreeTest.SYNC, ((short) (1))));
        callTree.add(3, makeSpanAlign(CallTreeTest.SYNC, ((short) (2))));
        callTree.add(4, makeSpanAlign(CallTreeTest.SYNC, ((short) (3))));
        callTree.add(5, makeSpanAlign(CallTreeTest.SYNC, ((short) (4))));
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (5))));
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (6))));
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (7))));
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (8))));
        callTree.add(4, makeSpanAlign(CallTreeTest.SYNC, ((short) (9))));
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (10))));
        callTree.add(5, makeSpanAlign(CallTreeTest.SYNC, ((short) (11))));
        callTree.add(6, makeSpanAlign(CallTreeTest.SYNC, ((short) (12))));
        callTree.add(7, makeSpanAlign(CallTreeTest.SYNC, ((short) (13))));
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (14))));
        callTree.add(4, makeSpanAlign(CallTreeTest.SYNC, ((short) (15))));
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (16))));
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (17))));
        assertDepth("addComplex", callTree, expectResult);
    }

    @Test
    public void addSubTree() {
        expectResult.clear();
        expectResult.add("#");
        expectResult.add("##");
        expectResult.add("###");
        expectResult.add("##");
        callTree.add(1, makeSpanAlign(CallTreeTest.SYNC, ((short) (0))));
        SpanCallTree subTree = new SpanCallTree(makeSpanAlign());
        callTree.add(subTree);
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (1))));
        assertDepth("addSubTree", callTree, expectResult);
    }

    @Test
    public void addNestedSubTree() {
        expectResult.clear();
        expectResult.add("#");
        expectResult.add("##");
        expectResult.add("###");
        expectResult.add("####");
        expectResult.add("#####");
        callTree.add(1, makeSpanAlign(CallTreeTest.SYNC, ((short) (0))));
        SpanCallTree subTree = new SpanCallTree(makeSpanAlign(CallTreeTest.ASYNC, ((short) (0))));
        subTree.add(1, makeSpanAlign(CallTreeTest.ASYNC, ((short) (1))));
        SpanCallTree subTree2 = new SpanCallTree(makeSpanAlign(CallTreeTest.ASYNC, ((short) (0))));
        subTree.add(subTree2);
        callTree.add(subTree);
        assertDepth("addNestedSubTree", callTree, expectResult);
    }

    @Test
    public void missing() {
        expectResult.clear();
        expectResult.add("#");
        try {
            callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (20))));
            callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (21))));
            callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (22))));
            callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (23))));
        } catch (Exception ignored) {
        }
        assertDepth("missing-case-1", callTree, expectResult);
    }

    @Test
    public void missingMiddleNodes() {
        expectResult.clear();
        expectResult.add("#");
        expectResult.add("##");
        expectResult.add("###");
        expectResult.add("####");
        try {
            callTree.add(1, makeSpanAlign(CallTreeTest.SYNC, ((short) (0))));
            callTree.add(2, makeSpanAlign(CallTreeTest.SYNC, ((short) (1))));
            callTree.add(3, makeSpanAlign(CallTreeTest.SYNC, ((short) (2))));
            // callTree.add(4, makeSpanAlign(SYNC, (short) 3));
            // callTree.add(5, makeSpanAlign(SYNC, (short) 4));
            // callTree.add(-1, makeSpanAlign(SYNC, (short) 5));
            callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (6))));
            callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (7))));
            callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (8))));
            callTree.add(4, makeSpanAlign(CallTreeTest.SYNC, ((short) (9))));
            callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (10))));
            callTree.add(5, makeSpanAlign(CallTreeTest.SYNC, ((short) (11))));
            callTree.add(6, makeSpanAlign(CallTreeTest.SYNC, ((short) (12))));
            callTree.add(7, makeSpanAlign(CallTreeTest.SYNC, ((short) (13))));
            callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (14))));
            callTree.add(4, makeSpanAlign(CallTreeTest.SYNC, ((short) (15))));
            callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (16))));
            callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (17))));
        } catch (Exception ignored) {
        }
        assertDepth("missing-case-2", callTree, expectResult);
    }

    @Test
    public void missingAsync() {
        expectResult.add("#");
        expectResult.add("##");
        callTree.add(1, makeSpanAlign(CallTreeTest.SYNC, ((short) (0)), 1, (-1)));
        try {
            SpanCallTree subTree = new SpanCallTree(makeSpanAlign());
            // subTree.add(1, makeSpanAlign(ASYNC, (short) 0, -1, 1));
            subTree.add(2, makeSpanAlign(CallTreeTest.ASYNC, ((short) (1)), 2, 1));
            callTree.add(subTree);
        } catch (Exception ignored) {
        }
        assertDepth("missing-case-3", callTree, expectResult);
    }

    @Test
    public void missingAsyncEvent() {
        expectResult.add("#");
        expectResult.add("##");
        expectResult.add("##");
        callTree.add(1, makeSpanAlign(CallTreeTest.SYNC, ((short) (0)), 1, (-1)));
        // callTree.add(makeSpanEventBo(ASYNC, (short) 0, 1));
        callTree.add((-1), makeSpanAlign(CallTreeTest.SYNC, ((short) (1)), (-1), (-1)));
        assertDepth("missing-case-5", callTree, expectResult);
    }

    @Test
    public void sort() {
        expectResult.add("#");
        expectResult.add("##");
        expectResult.add("###");// remote

        expectResult.add("####");// remote

        expectResult.add("###");
        expectResult.add("###");
        expectResult.add("##");
        Align root = makeSpanAlign(0, 10);
        SpanCallTree callTree = new SpanCallTree(root);
        callTree.add(1, makeSpanAlign(root.getSpanBo(), CallTreeTest.SYNC, ((short) (0)), (-1), (-1), 1, 1));
        Align remoteRoot = makeSpanAlign(4, 5);
        SpanCallTree subTree = new SpanCallTree(remoteRoot);
        subTree.add(1, makeSpanAlign(remoteRoot.getSpanBo(), CallTreeTest.SYNC, ((short) (0)), (-1), (-1), 1, 1));
        callTree.add(subTree);
        callTree.add(2, makeSpanAlign(root.getSpanBo(), CallTreeTest.SYNC, ((short) (1)), (-1), (-1), 2, 1));
        callTree.add((-1), makeSpanAlign(root.getSpanBo(), CallTreeTest.SYNC, ((short) (2)), (-1), (-1), 3, 1));
        callTree.add(1, makeSpanAlign(root.getSpanBo(), CallTreeTest.SYNC, ((short) (3)), (-1), (-1), 4, 1));
        assertDepth("before sort", callTree, expectResult);
        callTree.sort();
        expectResult.clear();
        expectResult.add("#");
        expectResult.add("##");
        expectResult.add("###");
        expectResult.add("###");
        expectResult.add("###");// remote

        expectResult.add("####");// remote

        expectResult.add("##");
        assertDepth("after sort", callTree, expectResult);
    }

    @Test
    public void sort2() {
        expectResult.add("#");
        expectResult.add("##");
        expectResult.add("###");// remote 1

        expectResult.add("####");// remote 1

        expectResult.add("###");// remote 2

        expectResult.add("###");
        Align root = makeSpanAlign(0, 10);
        SpanCallTree callTree = new SpanCallTree(root);
        callTree.add(1, makeSpanAlign(root.getSpanBo(), CallTreeTest.SYNC, ((short) (0)), (-1), (-1), 1, 1));
        Align remoteRoot1 = makeSpanAlign(4, 5);
        SpanCallTree subTree1 = new SpanCallTree(remoteRoot1);
        subTree1.add(1, makeSpanAlign(remoteRoot1.getSpanBo(), CallTreeTest.SYNC, ((short) (0)), (-1), (-1), 1, 1));
        callTree.add(subTree1);
        Align remoteRoot2 = makeSpanAlign(3, 4);
        SpanCallTree subTree2 = new SpanCallTree(remoteRoot2);
        callTree.add(subTree2);
        callTree.add(2, makeSpanAlign(root.getSpanBo(), CallTreeTest.SYNC, ((short) (1)), (-1), (-1), 2, 1));
        assertDepth("before sort", callTree, expectResult);
        callTree.sort();
        expectResult.clear();
        expectResult.add("#");
        expectResult.add("##");
        expectResult.add("###");
        expectResult.add("###");// remote 2

        expectResult.add("###");// remote 1

        expectResult.add("####");// remote 1

        assertDepth("after sort", callTree, expectResult);
    }
}

