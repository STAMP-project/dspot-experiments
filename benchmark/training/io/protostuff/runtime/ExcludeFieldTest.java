/**
 * ========================================================================
 */
/**
 * Copyright 2007-2010 David Yu dyuproject@gmail.com
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * ========================================================================
 */
package io.protostuff.runtime;


import io.protostuff.Exclude;
import io.protostuff.Tag;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for runtime schemas to skip fields annotated with @Exclude and still allow backward-forward compatibility.
 *
 * @author Johannes Elgh
 * @unknown Jul 30, 2014
 */
public class ExcludeFieldTest {
    public static class Entity {
        int id;

        String name;

        @Exclude
        String alias;

        long timestamp;
    }

    public static class MuchExcludedEntity {
        @Exclude
        int id;

        @Exclude
        String name;

        String alias;

        @Exclude
        long timestamp;
    }

    public static class TaggedAndExcludedEntity {
        @Exclude
        int id;

        @Tag(4)
        String name;

        @Tag(2)
        String alias;

        @Exclude
        long timestamp;
    }

    @Test
    public void testIt() throws Exception {
        RuntimeSchema<ExcludeFieldTest.Entity> schema = ((RuntimeSchema<ExcludeFieldTest.Entity>) (RuntimeSchema.getSchema(ExcludeFieldTest.Entity.class)));
        Assert.assertTrue(((schema.getFieldCount()) == 3));
        Assert.assertEquals(schema.getFields().get(0).name, "id");
        Assert.assertEquals(schema.getFields().get(0).number, 1);
        Assert.assertEquals(schema.getFields().get(1).name, "name");
        Assert.assertEquals(schema.getFields().get(1).number, 2);
        Assert.assertEquals(schema.getFields().get(2).name, "timestamp");
        Assert.assertEquals(schema.getFields().get(2).number, 3);
        Assert.assertTrue(((schema.getFieldNumber("alias")) == 0));
        Assert.assertNull(schema.getFieldByName("alias"));
    }

    @Test
    public void testMuchExcludedEntity() throws Exception {
        RuntimeSchema<ExcludeFieldTest.MuchExcludedEntity> schema = ((RuntimeSchema<ExcludeFieldTest.MuchExcludedEntity>) (RuntimeSchema.getSchema(ExcludeFieldTest.MuchExcludedEntity.class)));
        Assert.assertTrue(((schema.getFieldCount()) == 1));
        Assert.assertTrue(((schema.getFieldNumber("id")) == 0));
        Assert.assertNull(schema.getFieldByName("id"));
        Assert.assertTrue(((schema.getFieldNumber("name")) == 0));
        Assert.assertNull(schema.getFieldByName("name"));
        Assert.assertEquals(schema.getFields().get(0).name, "alias");
        Assert.assertEquals(schema.getFields().get(0).number, 1);
        Assert.assertTrue(((schema.getFieldNumber("timestamp")) == 0));
        Assert.assertNull(schema.getFieldByName("timestamp"));
    }

    @Test
    public void testTaggedAndExcludedEntity() throws Exception {
        RuntimeSchema<ExcludeFieldTest.TaggedAndExcludedEntity> schema = ((RuntimeSchema<ExcludeFieldTest.TaggedAndExcludedEntity>) (RuntimeSchema.getSchema(ExcludeFieldTest.TaggedAndExcludedEntity.class)));
        Assert.assertEquals(2, schema.getFieldCount());
        Assert.assertEquals(0, schema.getFieldNumber("id"));
        Assert.assertNull(schema.getFieldByName("id"));
        Assert.assertEquals(schema.getFields().get(0).name, "alias");
        Assert.assertEquals(schema.getFields().get(0).number, 2);
        Assert.assertEquals("name", schema.getFields().get(1).name);
        Assert.assertEquals(4, schema.getFields().get(1).number);
        Assert.assertTrue(((schema.getFieldNumber("timestamp")) == 0));
        Assert.assertNull(schema.getFieldByName("timestamp"));
    }

    static final class Empty {}

    @Test
    public void testEmptyMessage() {
        Assert.assertNotNull(RuntimeSchema.getSchema(ExcludeFieldTest.Empty.class));
    }
}

