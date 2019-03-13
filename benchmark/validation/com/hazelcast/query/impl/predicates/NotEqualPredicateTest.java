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
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NotEqualPredicateTest {
    @Test
    public void negate_thenReturnEqualPredicate() {
        NotEqualPredicate notEqualPredicate = new NotEqualPredicate("foo", 1);
        EqualPredicate negate = ((EqualPredicate) (notEqualPredicate.negate()));
        Assert.assertEquals("foo", negate.attributeName);
        Assert.assertEquals(1, negate.value);
    }

    @Test
    public void hasDefaultConstructor() {
        // it's needed for serialization, it will fail when default constructor is removed by mistake
        new NotEqualPredicate();
    }

    @Test
    public void apply_givenAttributeValueIsNull_whenEntryHasTheAttributeNull_thenReturnFalse() {
        NotEqualPredicate name = new NotEqualPredicate("name", null);
        QueryableEntry mockEntry = newMockEntry(null);
        boolean result = name.apply(mockEntry);
        Assert.assertFalse(result);
    }

    @Test
    public void apply_givenAttributeValueIsNull_whenEntryHasTheAttributeIsNotNull_thenReturnTrue() {
        NotEqualPredicate name = new NotEqualPredicate("name", null);
        QueryableEntry mockEntry = newMockEntry("foo");
        boolean result = name.apply(mockEntry);
        Assert.assertTrue(result);
    }

    @Test
    public void apply_givenAttributeValueIsFoo_whenEntryHasEqualAttribute_thenReturnFalse() {
        NotEqualPredicate name = new NotEqualPredicate("name", "foo");
        QueryableEntry mockEntry = newMockEntry("foo");
        boolean result = name.apply(mockEntry);
        Assert.assertFalse(result);
    }

    @Test
    public void isIndexed_givenAttributeNameIsFoo_whenTheFooFieldIsIndexed_returnFalse() {
        // see https://github.com/hazelcast/hazelcast/pull/5847
        String fieldName = "name";
        NotEqualPredicate name = new NotEqualPredicate(fieldName, "foo");
        QueryContext queryContext = newMockContextWithIndex(fieldName);
        boolean indexed = name.isIndexed(queryContext);
        Assert.assertFalse(indexed);
    }

    @Test
    public void toString_containsAttributeName() {
        String fieldName = "name";
        NotEqualPredicate predicate = new NotEqualPredicate(fieldName, "foo");
        String result = predicate.toString();
        Assert.assertThat(result, Matchers.containsString(fieldName));
    }

    @Test
    public void getId_isConstant() {
        NotEqualPredicate predicate = new NotEqualPredicate("bar", "foo");
        int id = predicate.getId();
        // make sure the ID has not been changed by accident
        Assert.assertEquals(id, 9);
    }

    @Test
    public void filter_givenAttributeNameIsFoo_whenTheFooFieldIsIndex_thenReturnsNullAndDoesNotTouchQueryContext() {
        /**
         * see {@link #isIndexed_givenAttributeNameIsFoo_whenTheFooFieldIsIndexed_returnFalse()}
         */
        String fieldName = "foo";
        NotEqualPredicate predicate = new NotEqualPredicate(fieldName, "foo");
        QueryContext queryContext = newMockContextWithIndex(fieldName);
        Set<QueryableEntry> filter = predicate.filter(queryContext);
        Assert.assertNull(filter);
        Mockito.verifyZeroInteractions(queryContext);
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier.forClass(NotEqualPredicate.class).suppress(NONFINAL_FIELDS, STRICT_INHERITANCE).withRedefinedSuperclass().allFieldsShouldBeUsed().verify();
    }
}

