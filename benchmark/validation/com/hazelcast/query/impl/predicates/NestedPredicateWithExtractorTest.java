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
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.query.extractor.ValueCollector;
import com.hazelcast.query.extractor.ValueExtractor;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class NestedPredicateWithExtractorTest extends HazelcastTestSupport {
    private IMap<Integer, NestedPredicateWithExtractorTest.Body> map;

    private static int bodyExtractorExecutions;

    private static int limbExtractorExecutions;

    @Test
    public void singleAttributeQuery_predicates() {
        // GIVEN
        map.put(1, new NestedPredicateWithExtractorTest.Body("body1", new NestedPredicateWithExtractorTest.Limb("hand")));
        map.put(2, new NestedPredicateWithExtractorTest.Body("body2", new NestedPredicateWithExtractorTest.Limb("leg")));
        // WHEN
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate predicate = e.get("name").equal("body1");
        Collection<NestedPredicateWithExtractorTest.Body> values = map.values(predicate);
        // THEN
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("body1", values.toArray(new NestedPredicateWithExtractorTest.Body[0])[0].getName());
        Assert.assertEquals(2, NestedPredicateWithExtractorTest.bodyExtractorExecutions);
        Assert.assertEquals(0, NestedPredicateWithExtractorTest.limbExtractorExecutions);
    }

    @Test
    public void singleAttributeQuery_distributedSql() {
        // GIVEN
        map.put(1, new NestedPredicateWithExtractorTest.Body("body1", new NestedPredicateWithExtractorTest.Limb("hand")));
        map.put(2, new NestedPredicateWithExtractorTest.Body("body2", new NestedPredicateWithExtractorTest.Limb("leg")));
        // WHEN
        Collection<NestedPredicateWithExtractorTest.Body> values = map.values(new SqlPredicate("name == 'body1'"));
        // THEN
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("body1", values.toArray(new NestedPredicateWithExtractorTest.Body[0])[0].getName());
        Assert.assertEquals(2, NestedPredicateWithExtractorTest.bodyExtractorExecutions);
        Assert.assertEquals(0, NestedPredicateWithExtractorTest.limbExtractorExecutions);
    }

    @Test
    public void nestedAttributeQuery_predicates() {
        // GIVEN
        map.put(1, new NestedPredicateWithExtractorTest.Body("body1", new NestedPredicateWithExtractorTest.Limb("hand")));
        map.put(2, new NestedPredicateWithExtractorTest.Body("body2", new NestedPredicateWithExtractorTest.Limb("leg")));
        // WHEN
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate predicate = e.get("limbname").equal("leg");
        Collection<NestedPredicateWithExtractorTest.Body> values = map.values(predicate);
        // THEN
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("body2", values.toArray(new NestedPredicateWithExtractorTest.Body[0])[0].getName());
        Assert.assertEquals(0, NestedPredicateWithExtractorTest.bodyExtractorExecutions);
        Assert.assertEquals(2, NestedPredicateWithExtractorTest.limbExtractorExecutions);
    }

    @Test
    public void nestedAttributeQuery_customPredicates_entryProcessor() {
        // GIVEN
        map.put(1, new NestedPredicateWithExtractorTest.Body("body1", new NestedPredicateWithExtractorTest.Limb("hand")));
        map.put(2, new NestedPredicateWithExtractorTest.Body("body2", new NestedPredicateWithExtractorTest.Limb("leg")));
        // WHEN
        Map<Integer, Object> result = map.executeOnEntries(new NestedPredicateWithExtractorTest.ExtractProcessor(), new NestedPredicateWithExtractorTest.CustomPredicate());
        NestedPredicateWithExtractorTest.Body resultBody = ((NestedPredicateWithExtractorTest.Body) (result.values().iterator().next()));
        // THEN
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("body1", resultBody.getName());
    }

    private static final class ExtractProcessor implements EntryProcessor<Integer, NestedPredicateWithExtractorTest.Body> {
        @Override
        public NestedPredicateWithExtractorTest.Body process(Map.Entry<Integer, NestedPredicateWithExtractorTest.Body> entry) {
            return entry.getValue();
        }

        @Override
        public EntryBackupProcessor<Integer, NestedPredicateWithExtractorTest.Body> getBackupProcessor() {
            return null;
        }
    }

    private static final class CustomPredicate extends AbstractPredicate {
        public CustomPredicate() {
            super("limbname");
        }

        @Override
        protected boolean applyForSingleAttributeValue(Comparable attributeValue) {
            return attributeValue.equals("hand");
        }

        @Override
        public int getId() {
            return 0;
        }
    }

    @Test
    public void nestedAttributeQuery_distributedSql() {
        // GIVEN
        map.put(1, new NestedPredicateWithExtractorTest.Body("body1", new NestedPredicateWithExtractorTest.Limb("hand")));
        map.put(2, new NestedPredicateWithExtractorTest.Body("body2", new NestedPredicateWithExtractorTest.Limb("leg")));
        // WHEN
        Collection<NestedPredicateWithExtractorTest.Body> values = map.values(new SqlPredicate("limbname == 'leg'"));
        // THEN
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("body2", values.toArray(new NestedPredicateWithExtractorTest.Body[0])[0].getName());
        Assert.assertEquals(0, NestedPredicateWithExtractorTest.bodyExtractorExecutions);
        Assert.assertEquals(2, NestedPredicateWithExtractorTest.limbExtractorExecutions);
    }

    public static class BodyNameExtractor extends ValueExtractor<NestedPredicateWithExtractorTest.Body, Object> {
        @Override
        public void extract(NestedPredicateWithExtractorTest.Body target, Object arguments, ValueCollector collector) {
            (NestedPredicateWithExtractorTest.bodyExtractorExecutions)++;
            collector.addObject(target.getName());
        }
    }

    public static class LimbNameExtractor extends ValueExtractor<NestedPredicateWithExtractorTest.Body, Object> {
        @Override
        public void extract(NestedPredicateWithExtractorTest.Body target, Object arguments, ValueCollector collector) {
            (NestedPredicateWithExtractorTest.limbExtractorExecutions)++;
            collector.addObject(target.getLimb().getName());
        }
    }

    private static class Body implements Serializable {
        private final String name;

        private final NestedPredicateWithExtractorTest.Limb limb;

        Body(String name, NestedPredicateWithExtractorTest.Limb limb) {
            this.name = name;
            this.limb = limb;
        }

        String getName() {
            return name;
        }

        NestedPredicateWithExtractorTest.Limb getLimb() {
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
            NestedPredicateWithExtractorTest.Body body = ((NestedPredicateWithExtractorTest.Body) (o));
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
            NestedPredicateWithExtractorTest.Limb limb = ((NestedPredicateWithExtractorTest.Limb) (o));
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

