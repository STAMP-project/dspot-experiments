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
public class CompositeEqualPredicateTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    private Random random;

    private IMap<Integer, CompositeEqualPredicateTest.Person> map;

    @Test
    public void testUnordered() {
        map.addIndex("age, height", false);
        Assert.assertEquals(0, map.getLocalMapStats().getIndexedQueryCount());
        for (int i = 0; i < 100; ++i) {
            final Integer age = randomQueryAge();
            final Long height = randomQueryHeight();
            assertPredicate(new com.hazelcast.query.Predicate<Integer, CompositeEqualPredicateTest.Person>() {
                @Override
                public boolean apply(Map.Entry<Integer, CompositeEqualPredicateTest.Person> mapEntry) {
                    return (ObjectTestUtils.equals(mapEntry.getValue().age, age)) && (ObjectTestUtils.equals(mapEntry.getValue().height, height));
                }
            }, CompositeEqualPredicateTest.predicate(CompositeEqualPredicateTest.value(age, height), "age", "height"));
        }
        Assert.assertEquals(100, map.getLocalMapStats().getIndexedQueryCount());
    }

    @Test
    public void testOrdered() {
        map.addIndex("age, height", true);
        Assert.assertEquals(0, map.getLocalMapStats().getIndexedQueryCount());
        for (int i = 0; i < 100; ++i) {
            final Integer age = randomQueryAge();
            final Long height = randomQueryHeight();
            assertPredicate(new com.hazelcast.query.Predicate<Integer, CompositeEqualPredicateTest.Person>() {
                @Override
                public boolean apply(Map.Entry<Integer, CompositeEqualPredicateTest.Person> mapEntry) {
                    return (ObjectTestUtils.equals(mapEntry.getValue().age, age)) && (ObjectTestUtils.equals(mapEntry.getValue().height, height));
                }
            }, CompositeEqualPredicateTest.predicate(CompositeEqualPredicateTest.value(age, height), "age", "height"));
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
            CompositeEqualPredicateTest.Person person = ((CompositeEqualPredicateTest.Person) (o));
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

