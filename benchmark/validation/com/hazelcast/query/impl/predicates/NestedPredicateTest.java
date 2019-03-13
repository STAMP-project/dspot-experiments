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


import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class NestedPredicateTest extends HazelcastTestSupport {
    private IMap<Integer, NestedPredicateTest.Body> map;

    @Test
    public void addingIndexes() {
        // single-attribute index
        map.addIndex("name", true);
        // nested-attribute index
        map.addIndex("limb.name", true);
    }

    @Test
    public void singleAttributeQuery_predicates() {
        // GIVEN
        map.put(1, new NestedPredicateTest.Body("body1", new NestedPredicateTest.Limb("hand")));
        map.put(2, new NestedPredicateTest.Body("body2", new NestedPredicateTest.Limb("leg")));
        // WHEN
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate predicate = e.get("name").equal("body1");
        Collection<NestedPredicateTest.Body> values = map.values(predicate);
        // THEN
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("body1", values.toArray(new NestedPredicateTest.Body[0])[0].getName());
    }

    @Test
    public void singleAttributeQuery_distributedSql() {
        // GIVEN
        map.put(1, new NestedPredicateTest.Body("body1", new NestedPredicateTest.Limb("hand")));
        map.put(2, new NestedPredicateTest.Body("body2", new NestedPredicateTest.Limb("leg")));
        // WHEN
        Collection<NestedPredicateTest.Body> values = map.values(new SqlPredicate("name == 'body1'"));
        // THEN
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("body1", values.toArray(new NestedPredicateTest.Body[0])[0].getName());
    }

    @Test
    public void nestedAttributeQuery_predicates() {
        // GIVEN
        map.put(1, new NestedPredicateTest.Body("body1", new NestedPredicateTest.Limb("hand")));
        map.put(2, new NestedPredicateTest.Body("body2", new NestedPredicateTest.Limb("leg")));
        // WHEN
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate predicate = e.get("limb.name").equal("leg");
        Collection<NestedPredicateTest.Body> values = map.values(predicate);
        // THEN
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("body2", values.toArray(new NestedPredicateTest.Body[0])[0].getName());
    }

    @Test
    public void nestedAttributeQuery_distributedSql() {
        // GIVEN
        map.put(1, new NestedPredicateTest.Body("body1", new NestedPredicateTest.Limb("hand")));
        map.put(2, new NestedPredicateTest.Body("body2", new NestedPredicateTest.Limb("leg")));
        // WHEN
        Collection<NestedPredicateTest.Body> values = map.values(new SqlPredicate("limb.name == 'leg'"));
        // THEN
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("body2", values.toArray(new NestedPredicateTest.Body[0])[0].getName());
    }

    private static class Body implements Serializable {
        private final String name;

        private final NestedPredicateTest.Limb limb;

        Body(String name, NestedPredicateTest.Limb limb) {
            this.name = name;
            this.limb = limb;
        }

        String getName() {
            return name;
        }

        NestedPredicateTest.Limb getLimb() {
            return limb;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            NestedPredicateTest.Body body = ((NestedPredicateTest.Body) (o));
            if ((name) != null ? !(name.equals(body.name)) : (body.name) != null) {
                return false;
            }
            return !((limb) != null ? !(limb.equals(body.limb)) : (body.limb) != null);
        }

        @Override
        public int hashCode() {
            int result = ((name) != null) ? name.hashCode() : 0;
            result = (31 * result) + ((limb) != null ? limb.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return ((((("Body{" + "name='") + (name)) + '\'') + ", limb=") + (limb)) + '}';
        }
    }

    private static class Limb implements Serializable {
        private final String name;

        Limb(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            NestedPredicateTest.Limb limb = ((NestedPredicateTest.Limb) (o));
            return !((name) != null ? !(name.equals(limb.name)) : (limb.name) != null);
        }

        @Override
        public int hashCode() {
            return (name) != null ? name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return ((("Limb{" + "name='") + (name)) + '\'') + '}';
        }
    }
}

