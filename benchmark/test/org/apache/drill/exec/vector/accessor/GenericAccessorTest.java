/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.vector.accessor;


import UserBitShared.SerializedField;
import ValueVector.Accessor;
import org.apache.drill.categories.VectorTest;
import org.apache.drill.exec.vector.ValueVector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(VectorTest.class)
public class GenericAccessorTest {
    public static final Object NON_NULL_VALUE = "Non-null value";

    private GenericAccessor genericAccessor;

    private ValueVector valueVector;

    private Accessor accessor;

    private SerializedField metadata;

    @Test
    public void testIsNull() throws Exception {
        Assert.assertFalse(genericAccessor.isNull(0));
        Assert.assertTrue(genericAccessor.isNull(1));
    }

    @Test
    public void testGetObject() throws Exception {
        Assert.assertEquals(GenericAccessorTest.NON_NULL_VALUE, genericAccessor.getObject(0));
        Assert.assertNull(genericAccessor.getObject(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetObject_indexOutOfBounds() throws Exception {
        genericAccessor.getObject(2);
    }

    @Test
    public void testGetType() throws Exception {
        Assert.assertEquals(SerializedField.getDefaultInstance().getMajorType(), genericAccessor.getType());
    }
}

