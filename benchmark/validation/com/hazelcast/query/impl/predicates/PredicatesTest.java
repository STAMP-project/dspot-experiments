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


import PredicateDataSerializerHook.ILIKE_PREDICATE;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.monitor.impl.PerIndexStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryEntry;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class PredicatesTest extends HazelcastTestSupport {
    private static final String ATTRIBUTE = "DUMMY_ATTRIBUTE_IGNORED";

    private final InternalSerializationService ss = new DefaultSerializationServiceBuilder().build();

    static class ShouldExecuteOncePredicate<K, V> implements IndexAwarePredicate<K, V> {
        boolean executed;

        @Override
        public boolean apply(Map.Entry<K, V> mapEntry) {
            if (!(executed)) {
                executed = true;
                return true;
            }
            throw new RuntimeException();
        }

        @Override
        public Set<QueryableEntry<K, V>> filter(final QueryContext queryContext) {
            return null;
        }

        @Override
        public boolean isIndexed(final QueryContext queryContext) {
            return false;
        }
    }

    @Test
    public void testEqual() {
        assertPredicateTrue(Predicates.equal(PredicatesTest.ATTRIBUTE, "value"), "value");
        assertPredicateFalse(Predicates.equal(PredicatesTest.ATTRIBUTE, "value1"), "value");
        assertPredicateTrue(Predicates.equal(PredicatesTest.ATTRIBUTE, Boolean.TRUE), true);
        assertPredicateTrue(Predicates.equal(PredicatesTest.ATTRIBUTE, true), Boolean.TRUE);
        assertPredicateFalse(Predicates.equal(PredicatesTest.ATTRIBUTE, true), Boolean.FALSE);
        assertPredicateFalse(Predicates.equal(PredicatesTest.ATTRIBUTE, new BigDecimal("1.23E3")), new BigDecimal("1.23E2"));
        assertPredicateTrue(Predicates.equal(PredicatesTest.ATTRIBUTE, new BigDecimal("1.23E3")), new BigDecimal("1.23E3"));
        assertPredicateFalse(Predicates.equal(PredicatesTest.ATTRIBUTE, 15.22), 15.23);
        assertPredicateTrue(Predicates.equal(PredicatesTest.ATTRIBUTE, 15.22), 15.22);
        assertPredicateFalse(Predicates.equal(PredicatesTest.ATTRIBUTE, 16), 15);
    }

    @Test
    public void testAnd() {
        final Predicate and1 = Predicates.and(Predicates.greaterThan(PredicatesTest.ATTRIBUTE, 4), Predicates.lessThan(PredicatesTest.ATTRIBUTE, 6));
        assertPredicateTrue(and1, 5);
        final Predicate and2 = Predicates.and(Predicates.greaterThan(PredicatesTest.ATTRIBUTE, 5), Predicates.lessThan(PredicatesTest.ATTRIBUTE, 6));
        assertPredicateFalse(and2, 4);
        final Predicate and3 = Predicates.and(Predicates.greaterThan(PredicatesTest.ATTRIBUTE, 4), Predicates.lessThan(PredicatesTest.ATTRIBUTE, 6), Predicates.equal(PredicatesTest.ATTRIBUTE, 5));
        assertPredicateTrue(and3, 5);
        final Predicate and4 = Predicates.and(Predicates.greaterThan(PredicatesTest.ATTRIBUTE, 3), Predicates.lessThan(PredicatesTest.ATTRIBUTE, 6), Predicates.equal(PredicatesTest.ATTRIBUTE, 4));
        assertPredicateFalse(and4, 5);
    }

    @Test
    public void testOr() {
        final Predicate or1 = Predicates.or(Predicates.equal(PredicatesTest.ATTRIBUTE, 3), Predicates.equal(PredicatesTest.ATTRIBUTE, 4), Predicates.equal(PredicatesTest.ATTRIBUTE, 5));
        assertPredicateTrue(or1, 4);
        assertPredicateFalse(or1, 6);
    }

    @Test
    public void testGreaterEqual() {
        assertPredicateTrue(Predicates.greaterEqual(PredicatesTest.ATTRIBUTE, 5), 5);
    }

    @Test
    public void testLessThan() {
        assertPredicateTrue(Predicates.lessThan(PredicatesTest.ATTRIBUTE, 7), 6);
        assertPredicateFalse(Predicates.lessThan(PredicatesTest.ATTRIBUTE, 3), 4);
        assertPredicateFalse(Predicates.lessThan(PredicatesTest.ATTRIBUTE, 4), 4);
        assertPredicateTrue(Predicates.lessThan(PredicatesTest.ATTRIBUTE, "tc"), "bz");
        assertPredicateFalse(Predicates.lessThan(PredicatesTest.ATTRIBUTE, "gx"), "h0");
    }

    @Test
    public void testGreaterThan() {
        assertPredicateTrue(Predicates.greaterThan(PredicatesTest.ATTRIBUTE, 5), 6);
        assertPredicateFalse(Predicates.greaterThan(PredicatesTest.ATTRIBUTE, 5), 4);
        assertPredicateFalse(Predicates.greaterThan(PredicatesTest.ATTRIBUTE, 5), 5);
        assertPredicateTrue(Predicates.greaterThan(PredicatesTest.ATTRIBUTE, "aa"), "xa");
        assertPredicateFalse(Predicates.greaterThan(PredicatesTest.ATTRIBUTE, "da"), "cz");
        assertPredicateTrue(Predicates.greaterThan(PredicatesTest.ATTRIBUTE, new BigDecimal("1.23E2")), new BigDecimal("1.23E3"));
    }

    @Test
    public void testLessEqual() {
        assertPredicateTrue(Predicates.lessEqual(PredicatesTest.ATTRIBUTE, 4), 4);
    }

    @Test
    public void testPredicatesAgainstANullField() {
        assertFalse_withNullEntry(Predicates.lessEqual("nullField", 1));
        assertFalse_withNullEntry(Predicates.in("nullField", 1));
        assertFalse_withNullEntry(Predicates.lessThan("nullField", 1));
        assertFalse_withNullEntry(Predicates.greaterEqual("nullField", 1));
        assertFalse_withNullEntry(Predicates.greaterThan("nullField", 1));
        assertFalse_withNullEntry(Predicates.equal("nullField", 1));
        assertFalse_withNullEntry(Predicates.notEqual("nullField", null));
        assertFalse_withNullEntry(Predicates.between("nullField", 1, 1));
        assertTrue_withNullEntry(Predicates.like("nullField", null));
        assertTrue_withNullEntry(Predicates.ilike("nullField", null));
        assertTrue_withNullEntry(Predicates.regex("nullField", null));
        assertTrue_withNullEntry(Predicates.notEqual("nullField", 1));
    }

    @Test
    public void testBetween() {
        assertPredicateTrue(Predicates.between(PredicatesTest.ATTRIBUTE, 4, 6), 5);
        assertPredicateTrue(Predicates.between(PredicatesTest.ATTRIBUTE, 5, 6), 5);
        assertPredicateTrue(Predicates.between(PredicatesTest.ATTRIBUTE, "abc", "xyz"), "prs");
        assertPredicateFalse(Predicates.between(PredicatesTest.ATTRIBUTE, "klmn", "xyz"), "efgh");
        assertPredicateFalse(Predicates.between(PredicatesTest.ATTRIBUTE, 6, 7), 5);
    }

    @Test
    public void testIn() {
        assertPredicateTrue(Predicates.in(PredicatesTest.ATTRIBUTE, 4, 7, 8, 5), 5);
        assertPredicateTrue(Predicates.in(PredicatesTest.ATTRIBUTE, 5, 7, 8), 5);
        assertPredicateFalse(Predicates.in(PredicatesTest.ATTRIBUTE, 6, 7, 8), 5);
        assertPredicateFalse(Predicates.in(PredicatesTest.ATTRIBUTE, 6, 7, 8), 9);
    }

    @Test
    public void testLike() {
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "J%"), "Java");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "Ja%"), "Java");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "J_v_"), "Java");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "_av_"), "Java");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "_a__"), "Java");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "J%v_"), "Java");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "J%_"), "Java");
        assertPredicateFalse(Predicates.like(PredicatesTest.ATTRIBUTE, "java"), "Java");
        assertPredicateFalse(Predicates.like(PredicatesTest.ATTRIBUTE, "j%"), "Java");
        assertPredicateFalse(Predicates.like(PredicatesTest.ATTRIBUTE, "J_a"), "Java");
        assertPredicateFalse(Predicates.like(PredicatesTest.ATTRIBUTE, "J_ava"), "Java");
        assertPredicateFalse(Predicates.like(PredicatesTest.ATTRIBUTE, "J_a_a"), "Java");
        assertPredicateFalse(Predicates.like(PredicatesTest.ATTRIBUTE, "J_av__"), "Java");
        assertPredicateFalse(Predicates.like(PredicatesTest.ATTRIBUTE, "J_Va"), "Java");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "Java World"), "Java World");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "Java%ld"), "Java World");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "%World"), "Java World");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "Java_World"), "Java World");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "J.-*.*\\%"), "J.-*.*%");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "J\\_"), "J_");
        assertPredicateTrue(Predicates.like(PredicatesTest.ATTRIBUTE, "J%"), "Java");
    }

    @Test
    public void testILike() {
        assertPredicateFalse(Predicates.like(PredicatesTest.ATTRIBUTE, "JavaWorld"), "Java World");
        assertPredicateTrue(Predicates.ilike(PredicatesTest.ATTRIBUTE, "Java_World"), "java World");
        assertPredicateTrue(Predicates.ilike(PredicatesTest.ATTRIBUTE, "java%ld"), "Java World");
        assertPredicateTrue(Predicates.ilike(PredicatesTest.ATTRIBUTE, "%world"), "Java World");
        assertPredicateFalse(Predicates.ilike(PredicatesTest.ATTRIBUTE, "Java_World"), "gava World");
    }

    @Test
    public void testILike_Id() {
        ILikePredicate predicate = ((ILikePredicate) (Predicates.ilike(PredicatesTest.ATTRIBUTE, "Java_World")));
        Assert.assertThat(predicate.getId(), Matchers.allOf(Matchers.equalTo(6), Matchers.equalTo(ILIKE_PREDICATE)));
    }

    @Test
    public void testIsInstanceOf() {
        Assert.assertTrue(Predicates.instanceOf(Long.class).apply(new PredicatesTest.DummyEntry(1L)));
        Assert.assertFalse(Predicates.instanceOf(Long.class).apply(new PredicatesTest.DummyEntry("Java")));
        Assert.assertTrue(Predicates.instanceOf(Number.class).apply(new PredicatesTest.DummyEntry(4)));
    }

    @Test
    public void testCriteriaAPI() {
        Object value = new SampleTestObjects.Employee(12, "abc-123-xvz", 34, true, 10.0);
        EntryObject e = new PredicateBuilder().getEntryObject();
        EntryObject e2 = e.get("age");
        Predicate predicate = e2.greaterEqual(29).and(e2.lessEqual(36));
        Assert.assertTrue(predicate.apply(createEntry("1", value)));
        e = new PredicateBuilder().getEntryObject();
        Assert.assertTrue(e.get("id").equal(12).apply(createEntry("1", value)));
    }

    @Test(expected = NullPointerException.class)
    public void testBetweenNull() {
        Predicates.between(PredicatesTest.ATTRIBUTE, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testLessThanNull() {
        Predicates.lessThan(PredicatesTest.ATTRIBUTE, null);
    }

    @Test(expected = NullPointerException.class)
    public void testLessEqualNull() {
        Predicates.lessEqual(PredicatesTest.ATTRIBUTE, null);
    }

    @Test(expected = NullPointerException.class)
    public void testGreaterThanNull() {
        Predicates.greaterThan(PredicatesTest.ATTRIBUTE, null);
    }

    @Test(expected = NullPointerException.class)
    public void testGreaterEqualNull() {
        Predicates.greaterEqual(PredicatesTest.ATTRIBUTE, null);
    }

    @Test(expected = NullPointerException.class)
    public void testInNullWithNullArray() {
        Predicates.in(PredicatesTest.ATTRIBUTE, null);
    }

    @Test
    public void testNotEqualsPredicateDoesNotUseIndex() {
        Index dummyIndex = new com.hazelcast.query.impl.IndexImpl("foo", null, false, ss, Extractors.newBuilder(ss).build(), IndexCopyBehavior.COPY_ON_READ, PerIndexStats.EMPTY);
        QueryContext mockQueryContext = Mockito.mock(QueryContext.class);
        Mockito.when(mockQueryContext.getIndex(ArgumentMatchers.anyString())).thenReturn(dummyIndex);
        NotEqualPredicate p = new NotEqualPredicate("foo", "bar");
        boolean indexed = p.isIndexed(mockQueryContext);
        Assert.assertFalse(indexed);
    }

    private class DummyEntry extends QueryEntry {
        DummyEntry(Comparable attribute) {
            super(ss, TestUtil.toData("1"), attribute, Extractors.newBuilder(ss).build());
        }

        @Override
        public Comparable getAttributeValue(String attributeName) throws QueryException {
            return ((Comparable) (getValue()));
        }
    }

    private final class NullDummyEntry extends QueryableEntry {
        private Integer nullField;

        private NullDummyEntry() {
        }

        public Integer getNullField() {
            return nullField;
        }

        public void setNullField(Integer nullField) {
            this.nullField = nullField;
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public Object setValue(Object value) {
            return null;
        }

        @Override
        public Object getKey() {
            return 1;
        }

        @Override
        public Comparable getAttributeValue(String attributeName) throws QueryException {
            return null;
        }

        @Override
        protected Object getTargetObject(boolean key) {
            return null;
        }

        @Override
        public Data getKeyData() {
            return null;
        }

        @Override
        public Data getValueData() {
            return null;
        }
    }
}

