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
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.CompositeValue;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.ObjectTestUtils;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CompositeRangePredicateTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    private Random random;

    private IMap<Integer, CompositeRangePredicateTest.Person> map;

    @Test
    public void testNoComparison() {
        Assert.assertEquals(0, map.getLocalMapStats().getIndexedQueryCount());
        for (int i = 0; i < 100; ++i) {
            final Integer age = randomQueryAge();
            final Long height = randomQueryHeight(true);
            int prefixLength = (random.nextInt(2)) + 1;
            final CompositeValue from;
            final CompositeValue to;
            final Predicate expected;
            switch (prefixLength) {
                case 1 :
                    from = CompositeRangePredicateTest.value(age, CompositeValue.NEGATIVE_INFINITY, CompositeValue.NEGATIVE_INFINITY);
                    to = CompositeRangePredicateTest.value(age, CompositeValue.POSITIVE_INFINITY, CompositeValue.POSITIVE_INFINITY);
                    expected = new Predicate<Integer, CompositeRangePredicateTest.Person>() {
                        @Override
                        public boolean apply(Map.Entry<Integer, CompositeRangePredicateTest.Person> mapEntry) {
                            return ObjectTestUtils.equals(mapEntry.getValue().age, age);
                        }
                    };
                    break;
                case 2 :
                    from = CompositeRangePredicateTest.value(age, height, CompositeValue.NEGATIVE_INFINITY);
                    to = CompositeRangePredicateTest.value(age, height, CompositeValue.POSITIVE_INFINITY);
                    expected = new Predicate<Integer, CompositeRangePredicateTest.Person>() {
                        @Override
                        public boolean apply(Map.Entry<Integer, CompositeRangePredicateTest.Person> mapEntry) {
                            return (ObjectTestUtils.equals(mapEntry.getValue().age, age)) && (ObjectTestUtils.equals(mapEntry.getValue().height, height));
                        }
                    };
                    break;
                default :
                    throw new IllegalStateException();
            }
            assertPredicate(expected, CompositeRangePredicateTest.predicate(from, false, to, false, prefixLength, "age", "height", "__key"));
        }
        Assert.assertEquals(100, map.getLocalMapStats().getIndexedQueryCount());
    }

    @Test
    public void testComparison() {
        Assert.assertEquals(0, map.getLocalMapStats().getIndexedQueryCount());
        for (int i = 0; i < 100; ++i) {
            final Integer age = randomQueryAge();
            final Long height = randomQueryHeight(true);
            int prefixLength = (random.nextInt(2)) + 1;
            final CompositeValue from;
            final boolean fromInclusive;
            final CompositeValue to;
            final boolean toInclusive;
            final Predicate expected;
            switch (prefixLength) {
                case 1 :
                    final Long heightFrom = randomQueryHeight(true);
                    final boolean heightFromInclusive = (heightFrom != null) && (random.nextBoolean());
                    final Long heightTo = randomQueryHeight((heightFrom != null));
                    final boolean heightToInclusive = (heightTo != null) && (random.nextBoolean());
                    from = CompositeRangePredicateTest.value(age, (heightFrom != null ? heightFrom : AbstractIndex.NULL), (heightFromInclusive ? CompositeValue.NEGATIVE_INFINITY : CompositeValue.POSITIVE_INFINITY));
                    fromInclusive = false;
                    to = CompositeRangePredicateTest.value(age, (heightTo != null ? heightTo : CompositeValue.POSITIVE_INFINITY), (heightToInclusive ? CompositeValue.POSITIVE_INFINITY : CompositeValue.NEGATIVE_INFINITY));
                    toInclusive = false;
                    expected = new Predicate<Integer, CompositeRangePredicateTest.Person>() {
                        @SuppressWarnings("RedundantIfStatement")
                        @Override
                        public boolean apply(Map.Entry<Integer, CompositeRangePredicateTest.Person> mapEntry) {
                            CompositeRangePredicateTest.Person value = mapEntry.getValue();
                            if (!(ObjectTestUtils.equals(value.age, age))) {
                                return false;
                            }
                            if ((value.height) == null) {
                                return false;
                            }
                            if (heightFrom != null) {
                                if (heightFromInclusive) {
                                    if ((value.height) < heightFrom) {
                                        return false;
                                    }
                                } else {
                                    if ((value.height) <= heightFrom) {
                                        return false;
                                    }
                                }
                            }
                            if (heightTo != null) {
                                if (heightToInclusive) {
                                    if ((value.height) > heightTo) {
                                        return false;
                                    }
                                } else {
                                    if ((value.height) >= heightTo) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                    };
                    break;
                case 2 :
                    final Integer keyFrom = randomQueryKey(true);
                    final boolean keyFromInclusive = (keyFrom != null) && (random.nextBoolean());
                    final Integer keyTo = randomQueryKey((keyFrom != null));
                    final boolean keyToInclusive = (keyTo != null) && (random.nextBoolean());
                    from = CompositeRangePredicateTest.value(age, height, (keyFrom != null ? keyFrom : AbstractIndex.NULL));
                    fromInclusive = keyFromInclusive;
                    to = CompositeRangePredicateTest.value(age, height, (keyTo != null ? keyTo : CompositeValue.POSITIVE_INFINITY));
                    toInclusive = keyToInclusive;
                    expected = new Predicate<Integer, CompositeRangePredicateTest.Person>() {
                        @SuppressWarnings("RedundantIfStatement")
                        @Override
                        public boolean apply(Map.Entry<Integer, CompositeRangePredicateTest.Person> mapEntry) {
                            CompositeRangePredicateTest.Person value = mapEntry.getValue();
                            int key = mapEntry.getKey();
                            if (!(ObjectTestUtils.equals(value.age, age))) {
                                return false;
                            }
                            if (!(ObjectTestUtils.equals(value.height, height))) {
                                return false;
                            }
                            if (keyFrom != null) {
                                if (keyFromInclusive) {
                                    if (key < keyFrom) {
                                        return false;
                                    }
                                } else {
                                    if (key <= keyFrom) {
                                        return false;
                                    }
                                }
                            }
                            if (keyTo != null) {
                                if (keyToInclusive) {
                                    if (key > keyTo) {
                                        return false;
                                    }
                                } else {
                                    if (key >= keyTo) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                    };
                    break;
                default :
                    throw new IllegalStateException();
            }
            assertPredicate(expected, CompositeRangePredicateTest.predicate(from, fromInclusive, to, toInclusive, prefixLength, "age", "height", "__key"));
        }
        Assert.assertEquals(100, map.getLocalMapStats().getIndexedQueryCount());
    }

    public static class Person implements Serializable {
        public final Integer age;

        public final Long height;

        public Person(Integer age, Long height) {
            this.age = age;
            this.height = height;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CompositeRangePredicateTest.Person person = ((CompositeRangePredicateTest.Person) (o));
            if ((age) != null ? !(age.equals(person.age)) : (person.age) != null) {
                return false;
            }
            return (height) != null ? height.equals(person.height) : (person.height) == null;
        }

        @Override
        public String toString() {
            return (((("Person{" + "age=") + (age)) + ", height=") + (height)) + '}';
        }
    }
}

