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


import FalsePredicate.INSTANCE;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RangeVisitorTest extends VisitorTestSupport {
    private RangeVisitor visitor;

    private Indexes indexes;

    @Test
    public void testUnoptimizablePredicates() {
        check(VisitorTestSupport.same());
        check(VisitorTestSupport.same(), new VisitorTestSupport.CustomPredicate());
        check(VisitorTestSupport.same(), Predicates.equal("age", 0));
        check(VisitorTestSupport.same(), Predicates.equal("unindexed", 0), Predicates.greaterEqual("unindexed", 0));
        check(VisitorTestSupport.same(), Predicates.equal("noConverter", 0), Predicates.lessEqual("noConverter", 0));
        check(VisitorTestSupport.same(), Predicates.equal("name", "Alice"), Predicates.like("name", "Alice"));
        check(VisitorTestSupport.same(), Predicates.equal("age", 0), Predicates.equal("name", "Alice"));
        check(VisitorTestSupport.same(), Predicates.equal("age", 0), Predicates.equal("name", "Alice"), Predicates.greaterEqual("unindexed", true));
        check(VisitorTestSupport.same(), Predicates.between("name", "Alice", "Bob"));
    }

    @Test
    public void testUnsatisfiablePredicates() {
        check(Predicates.alwaysFalse(), Predicates.alwaysFalse());
        check(Predicates.alwaysFalse(), Predicates.alwaysFalse(), Predicates.equal("name", "Alice"), Predicates.equal("name", "Alice"));
        check(Predicates.alwaysFalse(), new VisitorTestSupport.CustomPredicate(), Predicates.alwaysFalse());
        check(Predicates.alwaysFalse(), Predicates.equal("name", "Alice"), Predicates.equal("name", "Bob"));
        check(Predicates.alwaysFalse(), new VisitorTestSupport.CustomPredicate(), Predicates.equal("name", "Alice"), Predicates.equal("name", "Bob"));
        check(Predicates.alwaysFalse(), Predicates.equal("age", 10), Predicates.greaterThan("age", "10"));
        check(Predicates.alwaysFalse(), Predicates.lessEqual("age", "8"), Predicates.equal("age", 9));
        check(Predicates.alwaysFalse(), Predicates.lessThan("age", 9), Predicates.equal("age", 9.0));
        check(Predicates.alwaysFalse(), Predicates.greaterEqual("age", "10"), Predicates.lessThan("age", 10.0));
        check(Predicates.alwaysFalse(), Predicates.greaterThan("age", "10"), Predicates.lessThan("age", 0));
        check(Predicates.alwaysFalse(), Predicates.equal("name", "Alice"), Predicates.equal("name", "Bob"), Predicates.greaterEqual("age", "10"), Predicates.equal("age", 9));
    }

    @Test
    public void testOptimizablePredicates() {
        check(Predicates.equal("age", 1), Predicates.equal("age", 1), Predicates.equal("age", "1"));
        check(Predicates.greaterThan("age", 1), Predicates.greaterEqual("age", 1.0), Predicates.greaterThan("age", "1"));
        check(Predicates.lessThan("age", 10), Predicates.lessEqual("age", 10.0), Predicates.lessThan("age", "10"));
        check(Predicates.greaterEqual("age", 10), Predicates.greaterEqual("age", 10.0), Predicates.greaterThan("age", "1"));
        check(Predicates.lessEqual("age", 10), Predicates.lessEqual("age", 10.0), Predicates.lessThan("age", "100"));
        check(Predicates.greaterThan("age", 1), Predicates.greaterThan("age", 1.0), Predicates.greaterThan("age", "1"));
        check(Predicates.greaterThan("age", 1), Predicates.greaterThan("age", (-100)), Predicates.greaterThan("age", 1));
        check(Predicates.lessThan("age", 10), Predicates.lessThan("age", 10.0), Predicates.lessThan("age", "10"));
        check(Predicates.lessThan("age", 10), Predicates.lessThan("age", 10.0), Predicates.lessThan("age", "100"));
        check(Predicates.between("age", 1, 10), Predicates.greaterEqual("age", 1), Predicates.lessEqual("age", 10));
        check(new BoundedRangePredicate("age", 1, false, 10, false), Predicates.greaterThan("age", 1), Predicates.lessThan("age", 10));
        check(new BoundedRangePredicate("age", 1, true, 10, false), Predicates.greaterEqual("age", 1), Predicates.lessThan("age", 10));
        check(new BoundedRangePredicate("age", 1, false, 10, true), Predicates.greaterThan("age", 1), Predicates.lessEqual("age", 10));
        check(VisitorTestSupport.and(VisitorTestSupport.ref(0), Predicates.equal("age", 1)), Predicates.equal("name", "Alice"), Predicates.equal("age", 1), Predicates.equal("age", "1"));
        check(VisitorTestSupport.and(VisitorTestSupport.ref(0), Predicates.equal("age", 1)), new VisitorTestSupport.CustomPredicate(), Predicates.equal("age", 1), Predicates.equal("age", "1"));
        check(VisitorTestSupport.and(VisitorTestSupport.ref(0), VisitorTestSupport.ref(1), Predicates.equal("age", 1)), new VisitorTestSupport.CustomPredicate(), Predicates.equal("unindexed", true), Predicates.equal("age", 1), Predicates.equal("age", "1"));
    }

    @Test
    public void testNullsHandling() {
        check(Predicates.alwaysFalse(), Predicates.equal("name", "Alice"), Predicates.equal("name", null));
        check(Predicates.alwaysFalse(), Predicates.lessThan("name", "Bob"), Predicates.equal("name", null));
        check(VisitorTestSupport.same(), Predicates.equal("name", null));
        check(VisitorTestSupport.same(), Predicates.equal("name", null), Predicates.equal("age", null));
        check(Predicates.equal("name", null), Predicates.equal("name", null), Predicates.equal("name", null));
    }

    @Test
    public void testDuplicatesHandling() {
        Predicate duplicate = new VisitorTestSupport.CustomPredicate();
        check(VisitorTestSupport.same(), duplicate, duplicate);
        check(VisitorTestSupport.same(), duplicate, duplicate, Predicates.equal("name", "Alice"));
        check(VisitorTestSupport.and(VisitorTestSupport.ref(0), VisitorTestSupport.ref(1), Predicates.equal("name", "Alice")), duplicate, duplicate, Predicates.equal("name", "Alice"), Predicates.equal("name", "Alice"));
        duplicate = Predicates.equal("unindexed", 1);
        check(VisitorTestSupport.same(), duplicate, duplicate);
        check(VisitorTestSupport.same(), duplicate, duplicate, Predicates.equal("name", "Alice"));
        check(VisitorTestSupport.and(VisitorTestSupport.ref(0), VisitorTestSupport.ref(1), Predicates.equal("name", "Alice")), duplicate, duplicate, Predicates.equal("name", "Alice"), Predicates.equal("name", "Alice"));
    }

    @Test
    public void testBetweenPredicateOptimization() {
        BetweenPredicate predicate = new BetweenPredicate("age", 20, 10);
        Assert.assertSame(INSTANCE, visitor.visit(predicate, indexes));
        predicate = new BetweenPredicate("noConverter", 20, 10);
        Assert.assertSame(predicate, visitor.visit(predicate, indexes));
        predicate = new BetweenPredicate("unindexed", "20", "10");
        Assert.assertSame(predicate, visitor.visit(predicate, indexes));
        predicate = new BetweenPredicate("age", "10", 20);
        Assert.assertSame(predicate, visitor.visit(predicate, indexes));
        predicate = new BetweenPredicate("age", "10", "10");
        VisitorTestSupport.assertEqualPredicate(new EqualPredicate("age", 10), visitor.visit(predicate, indexes));
    }
}

