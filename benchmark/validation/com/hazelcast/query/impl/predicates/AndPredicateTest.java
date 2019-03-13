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
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AndPredicateTest {
    @Test
    public void negate_whenContainsNegatablePredicate_thenReturnOrPredicateWithNegationInside() {
        // ~(foo and bar)  -->  (~foo or ~bar)
        // this is testing the case where the inner predicate implements {@link Negatable}
        Predicate negated = Mockito.mock(Predicate.class);
        Predicate negatable = PredicateTestUtils.createMockNegatablePredicate(negated);
        AndPredicate and = ((AndPredicate) (Predicates.and(negatable)));
        OrPredicate result = ((OrPredicate) (and.negate()));
        Predicate[] inners = result.predicates;
        Assert.assertThat(inners, Matchers.arrayWithSize(1));
        Assert.assertThat(inners, Matchers.arrayContainingInAnyOrder(negated));
    }

    @Test
    public void negate_whenContainsNonNegatablePredicate_thenReturnOrPredicateWithNotInside() {
        // ~(foo and bar)  -->  (~foo or ~bar)
        // this is testing the case where the inner predicate does NOT implement {@link Negatable}
        Predicate nonNegatable = Mockito.mock(Predicate.class);
        AndPredicate and = ((AndPredicate) (Predicates.and(nonNegatable)));
        OrPredicate result = ((OrPredicate) (and.negate()));
        Predicate[] inners = result.predicates;
        Assert.assertThat(inners, Matchers.arrayWithSize(1));
        NotPredicate notPredicate = ((NotPredicate) (inners[0]));
        Assert.assertThat(nonNegatable, CoreMatchers.sameInstance(notPredicate.predicate));
    }

    @Test
    public void accept_whenEmptyPredicate_thenReturnItself() {
        Visitor mockVisitor = PredicateTestUtils.createPassthroughVisitor();
        Indexes mockIndexes = Mockito.mock(Indexes.class);
        AndPredicate andPredicate = new AndPredicate(new Predicate[0]);
        AndPredicate result = ((AndPredicate) (andPredicate.accept(mockVisitor, mockIndexes)));
        Assert.assertThat(result, CoreMatchers.sameInstance(andPredicate));
    }

    @Test
    public void accept_whenInnerPredicateChangedOnAccept_thenReturnAndNewAndPredicate() {
        Visitor mockVisitor = PredicateTestUtils.createPassthroughVisitor();
        Indexes mockIndexes = Mockito.mock(Indexes.class);
        Predicate transformed = Mockito.mock(Predicate.class);
        Predicate innerPredicate = PredicateTestUtils.createMockVisitablePredicate(transformed);
        Predicate[] innerPredicates = new Predicate[1];
        innerPredicates[0] = innerPredicate;
        AndPredicate andPredicate = new AndPredicate(innerPredicates);
        AndPredicate result = ((AndPredicate) (andPredicate.accept(mockVisitor, mockIndexes)));
        Assert.assertThat(result, CoreMatchers.not(CoreMatchers.sameInstance(andPredicate)));
        Predicate[] newInnerPredicates = result.predicates;
        Assert.assertThat(newInnerPredicates, Matchers.arrayWithSize(1));
        Assert.assertThat(newInnerPredicates[0], CoreMatchers.equalTo(transformed));
    }

    @Test
    public void accept_whenVisitorReturnsNewInstance_thenReturnTheNewInstance() {
        Predicate delegate = Mockito.mock(Predicate.class);
        Visitor mockVisitor = PredicateTestUtils.createDelegatingVisitor(delegate);
        Indexes mockIndexes = Mockito.mock(Indexes.class);
        Predicate innerPredicate = Mockito.mock(Predicate.class);
        Predicate[] innerPredicates = new Predicate[1];
        innerPredicates[0] = innerPredicate;
        AndPredicate andPredicate = new AndPredicate(innerPredicates);
        Predicate result = andPredicate.accept(mockVisitor, mockIndexes);
        Assert.assertThat(result, CoreMatchers.sameInstance(delegate));
    }
}

