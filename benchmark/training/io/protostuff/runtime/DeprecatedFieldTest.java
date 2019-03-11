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


import org.junit.Assert;
import org.junit.Test;


/**
 * Test for runtime schemas to skip deprecated field and still allow backward-forward compatibility.
 *
 * @author David Yu
 * @unknown Oct 28, 2010
 */
public class DeprecatedFieldTest {
    public static class Entity {
        int id;

        String name;

        @Deprecated
        String alias;

        long timestamp;
    }

    @Test
    public void testIt() throws Exception {
        RuntimeSchema<DeprecatedFieldTest.Entity> schema = ((RuntimeSchema<DeprecatedFieldTest.Entity>) (RuntimeSchema.getSchema(DeprecatedFieldTest.Entity.class)));
        Assert.assertTrue(((schema.getFields().size()) == 3));
        Assert.assertEquals(schema.getFields().get(0).name, "id");
        Assert.assertEquals(schema.getFields().get(0).number, 1);
        Assert.assertEquals(schema.getFields().get(1).name, "name");
        Assert.assertEquals(schema.getFields().get(1).number, 2);
        Assert.assertEquals(schema.getFields().get(2).name, "timestamp");
        Assert.assertEquals(schema.getFields().get(2).number, 4);
        Assert.assertTrue(((schema.getFieldNumber("alias")) == 0));
        Assert.assertNull(schema.getFieldByName("alias"));
    }
}

