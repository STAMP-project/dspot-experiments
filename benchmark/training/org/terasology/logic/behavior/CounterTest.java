/**
 * Copyright 2017 MovingBlocks
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
package org.terasology.logic.behavior;


import org.junit.Test;
import org.terasology.logic.behavior.core.BehaviorTreeBuilder;


public class CounterTest {
    private BehaviorTreeBuilder treeBuilder;

    @Test
    public void test() {
        assertRun("{ sequence:[ { print:{msg:A} } ] }", 1, "[A]");
        assertRun("{ sequence:[ { print:{msg:A} }, { print:{msg:B} }  ] }", 1, "[A][B]");
        assertRun("{ sequence:[ { print:{msg:A} }, failure, { print:{msg:B} }  ] }", 1, "[A]");
        assertRun("{ sequence:[ { counter:{ count=1, child:{ print:{msg:A} } } },{ print:{msg:B} } ] }", 1, "[A][B]");
        assertRun("{ sequence:[ { counter:{ count=2, child:{ print:{msg:A} } } },{ print:{msg:B} } ] }", 1, "[A]");
        assertRun("{ sequence:[ { counter:{ count=2, child:{ print:{msg:A} } } },{ print:{msg:B} } ] }", 2, "[A][A][B]");
        assertRun("{ sequence:[ { counter:{ count=2, child:{ print:{msg:A} } } },{ print:{msg:B} } ] }", 4, "[A][A][B][A][A][B]");
        assertRun("{ sequence:[ { counter:{ count=2, child:{ counter:{ count=2, child:{ print:{msg:A} } } } } },{ print:{msg:B} } ] }", 4, "[A][A][A][A][B]");
        assertRun("{ sequence:[ { timeout:{ time=1, child:{ print:{msg:A} } } },{ print:{msg:B} } ] }", 2, "[A][B][A][B]");
        assertRun("{ sequence:[ { timeout:{ time=2, child:{ print:{msg:A} } } },{ print:{msg:B} } ] }", 4, "[A][B][A][B][A][B][A][B]");
        assertRun("{ sequence:[ { timeout:{ time=1, child:{ timeout:{ time=2, child:{ print:{msg:A} } } } } },{ print:{msg:B} } ] }", 2, "[A][B][A][B]");
    }
}

