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


import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.junit.Test;


public class PhreakNotNodeTest {
    BuildContext buildContext;

    NotNode notNode;

    JoinNode sinkNode;

    InternalWorkingMemory wm;

    BetaMemory bm;

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
    public void test1() {
        setupNotNode("!=");
        // @formatter:off
        test().left().insert(a0, a1, a2).result().insert(a2, a1, a0).left(a2, a1, a0).run().getActualResultLeftTuples().resetAll();
        test().left().delete(a2).right().insert(b1).result().delete().left(a1).right(b1).run().getActualResultLeftTuples().resetAll();
        // @formatter:on
    }

    @Test
    public void test2() {
        setupNotNode("<");
        // @formatter:off
        test().left().insert(a0, a1, a2).result().insert(a2, a1, a0).left(a2, a1, a0).run().getActualResultLeftTuples().resetAll();
        test().right().insert(b1).result().delete().left(a0, a1).right(b1).run().getActualResultLeftTuples().resetAll();
        // @formatter:on
    }
}

