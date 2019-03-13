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


import Warning.NONFINAL_FIELDS;
import Warning.STRICT_INHERITANCE;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OrPredicateTest {
    @Test
    public void negate_whenContainsNegatablePredicate_thenReturnAndPredicateWithNegationInside() {
        // ~(foo or bar)  -->  (~foo and ~bar)
        // this is testing the case where the inner predicate implements {@link Negatable}
        Predicate negated = Mockito.mock(Predicate.class);
        Predicate negatable = PredicateTestUtils.createMockNegatablePredicate(negated);
        OrPredicate or = ((OrPredicate) (Predicates.or(negatable)));
        AndPredicate result = ((AndPredicate) (or.negate()));
        Predicate[] inners = result.predicates;
        Assert.assertThat(inners, Matchers.arrayWithSize(1));
        Assert.assertThat(inners, Matchers.arrayContainingInAnyOrder(negated));
    }

    @Test
    public void negate_whenContainsNonNegatablePredicate_thenReturnAndPredicateWithNotInside() {
        // ~(foo or bar)  -->  (~foo and ~bar)
        // this is testing the case where the inner predicate does NOT implement {@link Negatable}
        Predicate nonNegatable = Mockito.mock(Predicate.class);
        OrPredicate or = ((OrPredicate) (Predicates.or(nonNegatable)));
        AndPredicate result = ((AndPredicate) (or.negate()));
        Predicate[] inners = result.predicates;
        Assert.assertThat(inners, Matchers.arrayWithSize(1));
        NotPredicate notPredicate = ((NotPredicate) (inners[0]));
        Assert.assertThat(nonNegatable, Matchers.sameInstance(notPredicate.predicate));
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier.forClass(OrPredicate.class).suppress(NONFINAL_FIELDS, STRICT_INHERITANCE).allFieldsShouldBeUsed().verify();
    }
}

