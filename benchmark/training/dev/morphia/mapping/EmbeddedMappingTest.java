/**
 * Copyright (c) 2008-2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.morphia.mapping;


import com.mongodb.DBObject;
import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;
import dev.morphia.query.FindOptions;
import dev.morphia.query.ValidationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class EmbeddedMappingTest extends TestBase {
    @Test
    public void mapGenericEmbeds() {
        getMorphia().map(EmbeddedMappingTest.AuditEntry.class, EmbeddedMappingTest.Delta.class);
        final EmbeddedMappingTest.AuditEntry<String> entry = new EmbeddedMappingTest.AuditEntry<String>();
        final HashMap<String, Object> before = new HashMap<String, Object>();
        final HashMap<String, Object> after = new HashMap<String, Object>();
        before.put("before", 42);
        after.put("after", 84);
        entry.delta = new EmbeddedMappingTest.Delta<String>(before, after);
        getDs().save(entry);
        final EmbeddedMappingTest.AuditEntry fetched = getDs().find(EmbeddedMappingTest.AuditEntry.class).filter("id = ", entry.id).find(new FindOptions().limit(1)).next();
        Assert.assertEquals(entry, fetched);
    }

    @Test
    public void testNestedInterfaces() {
        getMorphia().map(EmbeddedMappingTest.WithNested.class, EmbeddedMappingTest.NestedImpl.class);
        getDs().ensureIndexes();
        final List<DBObject> indexInfo = getDs().getCollection(EmbeddedMappingTest.WithNested.class).getIndexInfo();
        boolean indexFound = false;
        for (DBObject dbObject : indexInfo) {
            indexFound |= "nested.field.fail".equals(keySet().iterator().next());
        }
        Assert.assertTrue("Should find the nested field index", indexFound);
        EmbeddedMappingTest.WithNested nested = new EmbeddedMappingTest.WithNested();
        nested.nested = new EmbeddedMappingTest.NestedImpl("nested value");
        getDs().save(nested);
        EmbeddedMappingTest.WithNested found;
        try {
            getDs().find(EmbeddedMappingTest.WithNested.class).field("nested.field").equal("nested value").find(new FindOptions().limit(1)).next();
        } catch (ValidationException ignore) {
            Assert.fail("Should be able to resolve against the implementations found.");
        }
        found = getDs().find(EmbeddedMappingTest.WithNested.class).disableValidation().field("nested.field").equal("nested value").find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(found);
        Assert.assertEquals(nested, found);
        found = getDs().find(EmbeddedMappingTest.WithNested.class).disableValidation().field("nested.field.fails").equal("nested value").find(new FindOptions().limit(1)).tryNext();
        Assert.assertNull(found);
    }

    @Test
    public void validateNestedInterfaces() {
        getMorphia().map(EmbeddedMappingTest.WithNestedValidated.class, EmbeddedMappingTest.Nested.class, EmbeddedMappingTest.NestedImpl.class, EmbeddedMappingTest.AnotherNested.class);
        try {
            getDs().ensureIndexes();
        } catch (MappingException e) {
            Assert.assertEquals(("Could not resolve path 'nested.field.fail' against 'dev.morphia.mapping" + ".EmbeddedMappingTest$WithNestedValidated'."), e.getMessage());
        }
        final List<DBObject> indexInfo = getDs().getCollection(EmbeddedMappingTest.WithNestedValidated.class).getIndexInfo();
        boolean indexFound = false;
        for (DBObject dbObject : indexInfo) {
            indexFound |= "nested.field.fail".equals(keySet().iterator().next());
        }
        Assert.assertFalse("Should not find the nested field index", indexFound);
    }

    public interface Nested {}

    @Entity(value = "audit", noClassnameStored = true)
    public static class AuditEntry<T> {
        @Id
        private ObjectId id;

        @Embedded
        private EmbeddedMappingTest.Delta<T> delta;

        @Override
        public int hashCode() {
            int result = ((id) != null) ? id.hashCode() : 0;
            result = (31 * result) + (delta.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final EmbeddedMappingTest.AuditEntry<?> that = ((EmbeddedMappingTest.AuditEntry<?>) (o));
            if ((id) != null ? !(id.equals(that.id)) : (that.id) != null) {
                return false;
            }
            return delta.equals(that.delta);
        }
    }

    @Embedded
    public static class Delta<T> {
        private Map<String, Object> before;

        private Map<String, Object> after;

        private Delta() {
        }

        public Delta(final Map<String, Object> before, final Map<String, Object> after) {
            this.before = before;
            this.after = after;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final EmbeddedMappingTest.Delta<?> delta = ((EmbeddedMappingTest.Delta<?>) (o));
            if (!(before.equals(delta.before))) {
                return false;
            }
            return after.equals(delta.after);
        }

        @Override
        public int hashCode() {
            int result = before.hashCode();
            result = (31 * result) + (after.hashCode());
            return result;
        }
    }

    @Embedded
    public static class NestedImpl implements EmbeddedMappingTest.Nested {
        private String field;

        public NestedImpl() {
        }

        public NestedImpl(final String field) {
            this.field = field;
        }

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final EmbeddedMappingTest.NestedImpl nested = ((EmbeddedMappingTest.NestedImpl) (o));
            return (field) != null ? field.equals(nested.field) : (nested.field) == null;
        }

        @Override
        public int hashCode() {
            return (field) != null ? field.hashCode() : 0;
        }
    }

    @Embedded
    public static class AnotherNested implements EmbeddedMappingTest.Nested {
        private Long value;
    }

    @Indexes({ @Index(fields = { @Field("nested.field.fail") }, options = @IndexOptions(disableValidation = true, sparse = true)) })
    public static class WithNested {
        @Id
        private ObjectId id;

        private EmbeddedMappingTest.Nested nested;

        @Override
        public boolean equals(final Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final EmbeddedMappingTest.WithNested that = ((EmbeddedMappingTest.WithNested) (o));
            if ((id) != null ? !(id.equals(that.id)) : (that.id) != null) {
                return false;
            }
            return (nested) != null ? nested.equals(that.nested) : (that.nested) == null;
        }

        @Override
        public int hashCode() {
            int result = ((id) != null) ? id.hashCode() : 0;
            result = (31 * result) + ((nested) != null ? nested.hashCode() : 0);
            return result;
        }
    }

    @Indexes(@Index(fields = { @Field("nested.field.fail") }))
    public static class WithNestedValidated {
        @Id
        private ObjectId id;

        private EmbeddedMappingTest.Nested nested;
    }
}

