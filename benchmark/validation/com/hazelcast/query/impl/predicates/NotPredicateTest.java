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


import TruePredicate.INSTANCE;
import Warning.NONFINAL_FIELDS;
import Warning.STRICT_INHERITANCE;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NotPredicateTest {
    private SerializationService serializationService;

    @Test
    public void negate_thenReturnInnerPredicate() {
        Predicate inner = Mockito.mock(Predicate.class);
        NotPredicate notPredicate = new NotPredicate(inner);
        Predicate negate = notPredicate.negate();
        Assert.assertThat(negate, CoreMatchers.sameInstance(inner));
    }

    @Test
    public void apply() {
        apply(INSTANCE, false);
        apply(FalsePredicate.INSTANCE, true);
    }

    @Test
    public void serialize() {
        NotPredicate notPredicate = new NotPredicate(TruePredicate.INSTANCE);
        Data data = serializationService.toData(notPredicate);
        Object result = serializationService.toObject(data);
        NotPredicate found = HazelcastTestSupport.assertInstanceOf(NotPredicate.class, result);
        HazelcastTestSupport.assertInstanceOf(TruePredicate.class, found.predicate);
    }

    @Test
    public void accept_whenNullPredicate_thenReturnItself() {
        Visitor mockVisitor = PredicateTestUtils.createPassthroughVisitor();
        Indexes mockIndexes = Mockito.mock(Indexes.class);
        NotPredicate notPredicate = new NotPredicate(null);
        NotPredicate result = ((NotPredicate) (notPredicate.accept(mockVisitor, mockIndexes)));
        Assert.assertThat(result, CoreMatchers.sameInstance(notPredicate));
    }

    @Test
    public void accept_whenPredicateChangedOnAccept_thenReturnAndNewNotPredicate() {
        Visitor mockVisitor = PredicateTestUtils.createPassthroughVisitor();
        Indexes mockIndexes = Mockito.mock(Indexes.class);
        Predicate transformed = Mockito.mock(Predicate.class);
        Predicate predicate = PredicateTestUtils.createMockVisitablePredicate(transformed);
        NotPredicate notPredicate = new NotPredicate(predicate);
        NotPredicate result = ((NotPredicate) (notPredicate.accept(mockVisitor, mockIndexes)));
        Assert.assertThat(result, CoreMatchers.not(CoreMatchers.sameInstance(notPredicate)));
        Assert.assertThat(result.predicate, CoreMatchers.equalTo(transformed));
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier.forClass(NotPredicate.class).suppress(NONFINAL_FIELDS, STRICT_INHERITANCE).allFieldsShouldBeUsed().verify();
    }
}

