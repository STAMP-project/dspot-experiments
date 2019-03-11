/**
 * Copyright 2006-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item;


import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.SerializationUtils;


/**
 *
 *
 * @author Lucas Ward
 * @author Mahmoud Ben Hassine
 */
public class ExecutionContextTests {
    private ExecutionContext context;

    @Test
    public void testNormalUsage() {
        context.putString("1", "testString1");
        context.putString("2", "testString2");
        context.putLong("3", 3);
        context.putDouble("4", 4.4);
        context.putInt("5", 5);
        Assert.assertEquals("testString1", context.getString("1"));
        Assert.assertEquals("testString2", context.getString("2"));
        Assert.assertEquals("defaultString", context.getString("55", "defaultString"));
        Assert.assertEquals(4.4, context.getDouble("4"), 0);
        Assert.assertEquals(5.5, context.getDouble("55", 5.5), 0);
        Assert.assertEquals(3, context.getLong("3"));
        Assert.assertEquals(5, context.getLong("55", 5));
        Assert.assertEquals(5, context.getInt("5"));
        Assert.assertEquals(6, context.getInt("55", 6));
    }

    @Test
    public void testInvalidCast() {
        context.putLong("1", 1);
        try {
            context.getDouble("1");
            Assert.fail();
        } catch (ClassCastException ex) {
            // expected
        }
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(context.isEmpty());
        context.putString("1", "test");
        Assert.assertFalse(context.isEmpty());
    }

    @Test
    public void testDirtyFlag() {
        Assert.assertFalse(context.isDirty());
        context.putString("1", "test");
        Assert.assertTrue(context.isDirty());
        context.clearDirtyFlag();
        Assert.assertFalse(context.isDirty());
    }

    @Test
    public void testNotDirtyWithDuplicate() {
        context.putString("1", "test");
        Assert.assertTrue(context.isDirty());
        context.clearDirtyFlag();
        context.putString("1", "test");
        Assert.assertFalse(context.isDirty());
    }

    @Test
    public void testNotDirtyWithRemoveMissing() {
        context.putString("1", "test");
        Assert.assertTrue(context.isDirty());
        context.putString("1", null);// remove an item that was present

        Assert.assertTrue(context.isDirty());
        context.putString("1", null);// remove a non-existent item

        Assert.assertFalse(context.isDirty());
    }

    @Test
    public void testContains() {
        context.putString("1", "testString");
        Assert.assertTrue(context.containsKey("1"));
        Assert.assertTrue(context.containsValue("testString"));
    }

    @Test
    public void testEquals() {
        context.putString("1", "testString");
        ExecutionContext tempContext = new ExecutionContext();
        Assert.assertFalse(tempContext.equals(context));
        tempContext.putString("1", "testString");
        Assert.assertTrue(tempContext.equals(context));
    }

    /**
     * Putting null value is equivalent to removing the entry for the given key.
     */
    @Test
    public void testPutNull() {
        context.put("1", null);
        Assert.assertNull(context.get("1"));
        Assert.assertFalse(context.containsKey("1"));
    }

    @Test
    public void testGetNull() {
        Assert.assertNull(context.get("does not exist"));
    }

    @Test
    public void testSerialization() {
        ExecutionContextTests.TestSerializable s = new ExecutionContextTests.TestSerializable();
        s.value = 7;
        context.putString("1", "testString1");
        context.putString("2", "testString2");
        context.putLong("3", 3);
        context.putDouble("4", 4.4);
        context.put("5", s);
        context.putInt("6", 6);
        byte[] serialized = SerializationUtils.serialize(context);
        ExecutionContext deserialized = ((ExecutionContext) (SerializationUtils.deserialize(serialized)));
        Assert.assertEquals(context, deserialized);
        Assert.assertEquals(7, ((ExecutionContextTests.TestSerializable) (deserialized.get("5"))).value);
    }

    @Test
    public void testCopyConstructor() throws Exception {
        ExecutionContext context = new ExecutionContext();
        context.put("foo", "bar");
        ExecutionContext copy = new ExecutionContext(context);
        Assert.assertEquals(copy, context);
    }

    @Test
    public void testCopyConstructorNullInput() throws Exception {
        ExecutionContext context = new ExecutionContext(((ExecutionContext) (null)));
        Assert.assertTrue(context.isEmpty());
    }

    /**
     * Value object for testing serialization
     */
    @SuppressWarnings("serial")
    private static class TestSerializable implements Serializable {
        int value;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + (value);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            ExecutionContextTests.TestSerializable other = ((ExecutionContextTests.TestSerializable) (obj));
            if ((value) != (other.value)) {
                return false;
            }
            return true;
        }
    }
}

