/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.phreak;


import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.junit.Test;


public class PhreakJoinNodeTest {
    BuildContext buildContext;

    JoinNode joinNode;

    JoinNode sinkNode;

    InternalWorkingMemory wm;

    BetaMemory bm;

    SegmentMemory smem;

    BetaMemory bm0;

    SegmentMemory smem0;

    A a0 = A.a(0);

    A a1 = A.a(1);

    A a2 = A.a(2);

    A a3 = A.a(3);

    A a4 = A.a(4);

    B b0 = B.b(0);

    B b1 = B.b(1);

    B b2 = B.b(2);

    B b3 = B.b(3);

    B b4 = B.b(4);

    @Test
    public void testInsertDelete() {
        setupJoinNode();
        // @formatter:off
        test().left().insert(a0, a1).right().insert(b0, b1).result().insert(Pair.t(a1, b0), Pair.t(a0, b1)).left(a0, a1).right(b0, b1).run().getActualResultLeftTuples().resetAll();
        test().right().insert(b3).result().insert(Pair.t(a1, b3), Pair.t(a0, b3)).left(a0, a1).right(b0, b1, b3).run().getActualResultLeftTuples().resetAll();
        test().left().insert(a2).delete(a1).right().insert(b4).delete(b0).result().insert(Pair.t(a0, b4), Pair.t(a2, b1), Pair.t(a2, b3), Pair.t(a2, b4)).delete().left(a0, a2).right(b1, b3, b4).run().getActualResultLeftTuples().resetAll();
        // @formatter:on
    }

    @Test
    public void testStagedInsertDelete() {
        setupJoinNode();
        // @formatter:off
        test().left().insert(a0, a1).right().insert(b0, b1).preStaged(smem0).insert().delete().update().postStaged(smem0).insert(Pair.t(a1, b0), Pair.t(a0, b1)).delete().update().run();
        test().left().delete(a1).result().insert().delete().update().preStaged(smem0).insert(Pair.t(a0, b1)).delete().update().postStaged(smem0).insert(Pair.t(a0, b1)).delete().update().run();
        test().left().update(a0).result().update().preStaged(smem0).insert().delete().update().postStaged(smem0).insert(Pair.t(a0, b1)).delete().update().run();
        // @formatter:on
    }

    @Test
    public void testStagedUpdate() {
        setupJoinNode();
        // @formatter:off
        test().left().insert(a0, a1).right().insert(b0, b1, b2).preStaged(smem0).insert().delete().update().postStaged(smem0).insert(Pair.t(a1, b2), Pair.t(a1, b0), Pair.t(a0, b2), Pair.t(a0, b1)).delete().update().run();
        InternalFactHandle fh = ((InternalFactHandle) (wm.getFactHandle(a0)));
        wm.getObjectStore().updateHandle(fh, a2);
        test().left().update(a2).preStaged(smem0).insert(Pair.t(a1, b2), Pair.t(a1, b0)).delete().update().postStaged(smem0).insert(Pair.t(a2, b1), Pair.t(a2, b0), Pair.t(a1, b2), Pair.t(a1, b0)).delete().update().run();
        // @formatter:on
    }
}

