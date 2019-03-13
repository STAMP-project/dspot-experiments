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


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Map;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class BoundedRangePredicateQueriesTest extends HazelcastTestSupport {
    private static final int MIN = -100;

    private static final int MAX = +100;

    // +1 for inclusive +100 bound and one more +2 for nulls
    private static final int TOTAL = ((BoundedRangePredicateQueriesTest.MAX) - (BoundedRangePredicateQueriesTest.MIN)) + 3;

    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    private IMap<Integer, BoundedRangePredicateQueriesTest.Person> map;

    @Test
    public void testClosedRange() {
        Predicate expected = new BoundedRangePredicateQueriesTest.PersonPredicate(new com.hazelcast.util.function.Predicate<Integer>() {
            @Override
            public boolean test(Integer value) {
                return ((value != null) && (value >= 1)) && (value <= 10);
            }
        });
        assertPredicate(expected, new BoundedRangePredicate("age", 1, true, 10, true));
        assertPredicate(expected, new BoundedRangePredicate("height", 1, true, 10, true));
        assertPredicate(expected, new BoundedRangePredicate("weight", 1, true, 10, true));
    }

    @Test
    public void testLeftOpenRange() {
        Predicate expected = new BoundedRangePredicateQueriesTest.PersonPredicate(new com.hazelcast.util.function.Predicate<Integer>() {
            @Override
            public boolean test(Integer value) {
                return ((value != null) && (value > 1)) && (value <= 10);
            }
        });
        assertPredicate(expected, new BoundedRangePredicate("age", 1, false, 10, true));
        assertPredicate(expected, new BoundedRangePredicate("height", 1, false, 10, true));
        assertPredicate(expected, new BoundedRangePredicate("weight", 1, false, 10, true));
    }

    @Test
    public void testRightOpenRange() {
        Predicate expected = new BoundedRangePredicateQueriesTest.PersonPredicate(new com.hazelcast.util.function.Predicate<Integer>() {
            @Override
            public boolean test(Integer value) {
                return ((value != null) && (value >= 1)) && (value < 10);
            }
        });
        assertPredicate(expected, new BoundedRangePredicate("age", 1, true, 10, false));
        assertPredicate(expected, new BoundedRangePredicate("height", 1, true, 10, false));
        assertPredicate(expected, new BoundedRangePredicate("weight", 1, true, 10, false));
    }

    @Test
    public void testOpenRange() {
        Predicate expected = new BoundedRangePredicateQueriesTest.PersonPredicate(new com.hazelcast.util.function.Predicate<Integer>() {
            @Override
            public boolean test(Integer value) {
                return ((value != null) && (value > 1)) && (value < 10);
            }
        });
        assertPredicate(expected, new BoundedRangePredicate("age", 1, false, 10, false));
        assertPredicate(expected, new BoundedRangePredicate("height", 1, false, 10, false));
        assertPredicate(expected, new BoundedRangePredicate("weight", 1, false, 10, false));
    }

    @Test
    public void testDegenerateRange() {
        Predicate expected = new BoundedRangePredicateQueriesTest.PersonPredicate(new com.hazelcast.util.function.Predicate<Integer>() {
            @Override
            public boolean test(Integer value) {
                return ((value != null) && (value >= 1)) && (value <= 1);
            }
        });
        assertPredicate(expected, new BoundedRangePredicate("age", 1, true, 1, true));
        assertPredicate(expected, new BoundedRangePredicate("height", 1, true, 1, true));
        assertPredicate(expected, new BoundedRangePredicate("weight", 1, true, 1, true));
    }

    @Test
    public void testEmptyRanges() {
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("age", 1, true, 1, false));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("height", 1, true, 1, false));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("weight", 1, true, 1, false));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("age", 1, false, 1, true));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("height", 1, false, 1, true));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("weight", 1, false, 1, true));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("age", 1, false, 1, false));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("height", 1, false, 1, false));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("weight", 1, false, 1, false));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("age", 1, true, 0, true));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("height", 1, true, 0, true));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("weight", 1, true, 0, true));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("age", (+10), false, (-10), false));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("height", (+10), false, (-10), false));
        assertPredicate(Predicates.alwaysFalse(), new BoundedRangePredicate("weight", (+10), false, (-10), false));
    }

    private static class PersonPredicate implements Predicate<Integer, BoundedRangePredicateQueriesTest.Person> {
        private final com.hazelcast.util.function.Predicate<Integer> predicate;

        public PersonPredicate(com.hazelcast.util.function.Predicate<Integer> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean apply(Map.Entry<Integer, BoundedRangePredicateQueriesTest.Person> entry) {
            BoundedRangePredicateQueriesTest.Person person = entry.getValue();
            return ((predicate.test(person.age)) && (predicate.test(person.height))) && (predicate.test(person.weight));
        }
    }

    public static class Person implements Serializable {
        public final Integer age;

        public final Integer height;

        public final Integer weight;

        public Person(Integer value) {
            this.age = value;
            this.height = value;
            this.weight = value;
        }
    }
}

