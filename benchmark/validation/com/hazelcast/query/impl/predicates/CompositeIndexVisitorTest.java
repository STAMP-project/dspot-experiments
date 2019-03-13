/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.query.impl.predicates;


import com.hazelcast.query.Predicates;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.CompositeValue;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CompositeIndexVisitorTest extends VisitorTestSupport {
    private static final Comparable N_INF = CompositeValue.NEGATIVE_INFINITY;

    private static final Comparable P_INF = CompositeValue.POSITIVE_INFINITY;

    private CompositeIndexVisitor visitor;

    private Indexes indexes;

    private InternalIndex o123;

    private InternalIndex u321;

    private InternalIndex o567;

    @Test
    public void testUnoptimizablePredicates() {
        check(VisitorTestSupport.same());
        check(VisitorTestSupport.same(), new VisitorTestSupport.CustomPredicate());
        check(VisitorTestSupport.same(), new VisitorTestSupport.CustomPredicate(), Predicates.equal("a100", 100));
        check(VisitorTestSupport.same(), Predicates.equal("a100", 100), Predicates.greaterThan("a100", 100));
        check(VisitorTestSupport.same(), Predicates.equal("a100", 100), Predicates.equal("a101", 101));
        check(VisitorTestSupport.same(), Predicates.greaterThan("a100", 100), Predicates.lessEqual("a101", 101), Predicates.equal("a102", 102));
        check(VisitorTestSupport.same(), Predicates.equal("a1", 1));
        check(VisitorTestSupport.same(), Predicates.equal("a1", 1), Predicates.equal("a1", 11));
        check(VisitorTestSupport.same(), Predicates.greaterThan("a1", 1), Predicates.lessThan("a1", 1));
        check(VisitorTestSupport.same(), Predicates.equal("a1", 1), Predicates.between("a100", 100, 200));
        check(VisitorTestSupport.same(), Predicates.equal("a1", 1), Predicates.equal("a1", 11), Predicates.between("a100", 100, 200));
    }

    @Test
    public void testOptimizablePredicates() {
        check(range(o123, 1, 2, CompositeIndexVisitorTest.N_INF, 1, 2, CompositeIndexVisitorTest.P_INF), Predicates.equal("a1", 1), Predicates.equal("a2", 2));
        check(range(o123, 1, 2, CompositeIndexVisitorTest.P_INF, 1, CompositeIndexVisitorTest.P_INF, CompositeIndexVisitorTest.P_INF), Predicates.equal("a1", 1), Predicates.greaterThan("a2", 2));
        check(range(o123, 1, 2, CompositeIndexVisitorTest.N_INF, 1, CompositeIndexVisitorTest.P_INF, CompositeIndexVisitorTest.P_INF), Predicates.equal("a1", 1), Predicates.greaterEqual("a2", 2));
        check(range(o123, 1, AbstractIndex.NULL, CompositeIndexVisitorTest.N_INF, 1, 2, CompositeIndexVisitorTest.N_INF), Predicates.equal("a1", 1), Predicates.lessThan("a2", 2));
        check(range(o123, 1, AbstractIndex.NULL, CompositeIndexVisitorTest.N_INF, 1, 2, CompositeIndexVisitorTest.P_INF), Predicates.equal("a1", 1), Predicates.lessEqual("a2", 2));
        check(range(o123, 1, 2, CompositeIndexVisitorTest.N_INF, 1, 22, CompositeIndexVisitorTest.P_INF), Predicates.equal("a1", 1), Predicates.between("a2", 2, 22));
        check(eq(u321, 3, 2, 1), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.equal("a3", 3));
        check(eq(o567, 5, 6, 7), Predicates.equal("a5", 5), Predicates.equal("a6", 6), Predicates.equal("a7", 7));
        check(VisitorTestSupport.and(eq(u321, 3, 2, 1), VisitorTestSupport.ref(3)), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.equal("a3", 3), new VisitorTestSupport.CustomPredicate());
        check(VisitorTestSupport.and(eq(u321, 3, 2, 1), VisitorTestSupport.ref(3)), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.equal("a3", 3), Predicates.greaterThan("a5", 5));
        check(VisitorTestSupport.and(eq(o567, 5, 6, 7), VisitorTestSupport.ref(3)), Predicates.equal("a5", 5), Predicates.equal("a6", 6), Predicates.equal("a7", 7), Predicates.greaterThan("a1", 1));
        check(VisitorTestSupport.and(range(false, true, o567, 5, 6, AbstractIndex.NULL, 5, 6, 7), VisitorTestSupport.ref(3)), Predicates.equal("a5", 5), Predicates.equal("a6", 6), Predicates.lessEqual("a7", 7), Predicates.greaterThan("a1", 1));
        check(range(false, false, o123, 1, 2, 3, 1, 2, CompositeIndexVisitorTest.P_INF), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.greaterThan("a3", 3));
        check(range(true, false, o123, 1, 2, 3, 1, 2, CompositeIndexVisitorTest.P_INF), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.greaterEqual("a3", 3));
        check(range(false, false, o123, 1, 2, AbstractIndex.NULL, 1, 2, 3), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.lessThan("a3", 3));
        check(range(false, true, o123, 1, 2, AbstractIndex.NULL, 1, 2, 3), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.lessEqual("a3", 3));
        check(range(true, true, o123, 1, 2, 3, 1, 2, 33), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.between("a3", 3, 33));
        check(VisitorTestSupport.and(range(o123, 1, 2, CompositeIndexVisitorTest.N_INF, 1, 2, CompositeIndexVisitorTest.P_INF), VisitorTestSupport.ref(2)), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.equal("a100", 100));
        check(VisitorTestSupport.and(range(false, false, o123, 1, 2, AbstractIndex.NULL, 1, 2, 3), VisitorTestSupport.ref(3)), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.lessThan("a3", 3), Predicates.equal("a100", 100));
        check(VisitorTestSupport.and(range(o123, 1, 2, CompositeIndexVisitorTest.N_INF, 1, 2, CompositeIndexVisitorTest.P_INF), VisitorTestSupport.ref(2), VisitorTestSupport.ref(3)), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.equal("a100", 100), new VisitorTestSupport.CustomPredicate());
        check(VisitorTestSupport.and(range(o123, 1, 2, CompositeIndexVisitorTest.N_INF, 1, 2, CompositeIndexVisitorTest.P_INF), range(o567, 5, 6, CompositeIndexVisitorTest.N_INF, 5, 6, CompositeIndexVisitorTest.P_INF)), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.equal("a5", 5), Predicates.equal("a6", 6));
        check(VisitorTestSupport.and(eq(u321, 3, 2, 1), range(o567, 5, 6, CompositeIndexVisitorTest.N_INF, 5, 6, CompositeIndexVisitorTest.P_INF)), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.equal("a3", 3), Predicates.equal("a5", 5), Predicates.equal("a6", 6));
        check(VisitorTestSupport.and(eq(u321, 3, 2, 1), eq(o567, 5, 6, 7)), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.equal("a3", 3), Predicates.equal("a5", 5), Predicates.equal("a6", 6), Predicates.equal("a7", 7));
        check(VisitorTestSupport.and(eq(u321, 3, 2, 1), eq(o567, 5, 6, 7), VisitorTestSupport.ref(0)), new VisitorTestSupport.CustomPredicate(), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.equal("a3", 3), Predicates.equal("a5", 5), Predicates.equal("a6", 6), Predicates.equal("a7", 7));
        check(VisitorTestSupport.and(eq(u321, 3, 2, 1), eq(o567, 5, 6, 7), VisitorTestSupport.ref(0)), Predicates.equal("a100", 100), Predicates.equal("a1", 1), Predicates.equal("a2", 2), Predicates.equal("a3", 3), Predicates.equal("a5", 5), Predicates.equal("a6", 6), Predicates.equal("a7", 7));
    }
}

