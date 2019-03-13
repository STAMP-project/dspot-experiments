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


import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OrToInVisitorTest {
    private OrToInVisitor visitor;

    private Indexes indexes;

    @Test
    public void whenEmptyPredicate_thenReturnItself() {
        OrPredicate or = new OrPredicate(null);
        OrPredicate result = ((OrPredicate) (visitor.visit(or, indexes)));
        Assert.assertThat(or, Matchers.equalTo(result));
    }

    @Test
    public void whenThresholdNotExceeded_thenReturnItself() {
        Predicate p1 = Predicates.equal("age", 1);
        Predicate p2 = Predicates.equal("age", 2);
        OrPredicate or = ((OrPredicate) (Predicates.or(p1, p2)));
        OrPredicate result = ((OrPredicate) (visitor.visit(or, indexes)));
        Assert.assertThat(or, Matchers.equalTo(result));
    }

    @Test
    public void whenThresholdExceeded_noCandidatesFound_thenReturnItself() {
        // (age != 1 or age != 2 or age != 3 or age != 4 or age != 5)
        Predicate p1 = Predicates.notEqual("age", 1);
        Predicate p2 = Predicates.notEqual("age", 2);
        Predicate p3 = Predicates.notEqual("age", 3);
        Predicate p4 = Predicates.notEqual("age", 4);
        Predicate p5 = Predicates.notEqual("age", 5);
        OrPredicate or = ((OrPredicate) (Predicates.or(p1, p2, p3, p4, p5)));
        OrPredicate result = ((OrPredicate) (visitor.visit(or, indexes)));
        Assert.assertThat(or, Matchers.equalTo(result));
    }

    @Test
    public void whenThresholdExceeded_noEnoughCandidatesFound_thenReturnItself() {
        // (age != 1 or age != 2 or age != 3 or age != 4 or age != 5)
        Predicate p1 = Predicates.equal("age", 1);
        Predicate p2 = Predicates.equal("age", 2);
        Predicate p3 = Predicates.equal("age", 3);
        Predicate p4 = Predicates.equal("age", 4);
        Predicate p5 = Predicates.notEqual("age", 5);
        OrPredicate or = ((OrPredicate) (Predicates.or(p1, p2, p3, p4, p5)));
        OrPredicate result = ((OrPredicate) (visitor.visit(or, indexes)));
        Assert.assertThat(or, Matchers.equalTo(result));
    }

    @Test
    public void whenThresholdExceeded_thenRewriteToInPredicate() {
        // (age = 1 or age = 2 or age = 3 or age = 4 or age = 5)  -->  (age in (1, 2, 3, 4, 5))
        Predicate p1 = Predicates.equal("age", 1);
        Predicate p2 = Predicates.equal("age", 2);
        Predicate p3 = Predicates.equal("age", 3);
        Predicate p4 = Predicates.equal("age", 4);
        Predicate p5 = Predicates.equal("age", 5);
        OrPredicate or = ((OrPredicate) (Predicates.or(p1, p2, p3, p4, p5)));
        InPredicate result = ((InPredicate) (visitor.visit(or, indexes)));
        Comparable[] values = result.values;
        Assert.assertThat(values, Matchers.arrayWithSize(5));
        Assert.assertThat(values, Matchers.is(Matchers.<Comparable>arrayContainingInAnyOrder(1, 2, 3, 4, 5)));
    }

    @Test
    public void whenThresholdExceeded_thenRewriteToOrPredicate() {
        // (age = 1 or age = 2 or age = 3 or age = 4 or age = 5 or age != 6)  -->  (age in (1, 2, 3, 4, 5) or age != 6)
        Predicate p1 = Predicates.equal("age", 1);
        Predicate p2 = Predicates.equal("age", 2);
        Predicate p3 = Predicates.equal("age", 3);
        Predicate p4 = Predicates.equal("age", 4);
        Predicate p5 = Predicates.equal("age", 5);
        Predicate p6 = Predicates.notEqual("age", 6);
        OrPredicate or = ((OrPredicate) (Predicates.or(p1, p2, p3, p4, p5, p6)));
        OrPredicate result = ((OrPredicate) (visitor.visit(or, indexes)));
        Predicate[] predicates = result.predicates;
        for (Predicate predicate : predicates) {
            if (predicate instanceof InPredicate) {
                Comparable[] values = ((InPredicate) (predicate)).values;
                Assert.assertThat(values, Matchers.arrayWithSize(5));
                Assert.assertThat(values, Matchers.is(Matchers.<Comparable>arrayContainingInAnyOrder(1, 2, 3, 4, 5)));
            } else {
                Assert.assertThat(predicate, Matchers.instanceOf(NotEqualPredicate.class));
            }
        }
    }
}

