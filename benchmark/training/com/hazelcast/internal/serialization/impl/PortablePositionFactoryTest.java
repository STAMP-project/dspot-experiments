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
package com.hazelcast.internal.serialization.impl;


import com.hazelcast.nio.serialization.FieldDefinition;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
@SuppressWarnings("ConstantConditions")
public class PortablePositionFactoryTest extends HazelcastTestSupport {
    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(PortablePositionFactory.class);
    }

    @Test
    public void nilNonAnyPosition_nonLeaf() {
        PortablePosition p = PortablePositionFactory.nilNotLeafPosition();
        Assert.assertTrue(p.isNull());
        Assert.assertFalse(p.isEmpty());
        Assert.assertFalse(p.isAny());
        Assert.assertFalse(p.isLeaf());
        Assert.assertFalse(p.isMultiPosition());
    }

    @Test
    public void nilNonAnyPosition_nonLeaf_cached() {
        PortablePosition p1 = PortablePositionFactory.nilNotLeafPosition();
        PortablePosition p2 = PortablePositionFactory.nilNotLeafPosition();
        Assert.assertSame(p1, p2);
    }

    @Test
    public void nilAnyPosition_leaf() {
        PortablePosition p = PortablePositionFactory.nilAnyPosition(true);
        Assert.assertTrue(p.isNull());
        Assert.assertFalse(p.isEmpty());
        Assert.assertTrue(p.isAny());
        Assert.assertTrue(p.isLeaf());
        Assert.assertFalse(p.isMultiPosition());
    }

    @Test
    public void nilAnyPositionNonLeaf_cached() {
        PortablePosition p1 = PortablePositionFactory.nilAnyPosition(false);
        PortablePosition p2 = PortablePositionFactory.nilAnyPosition(false);
        Assert.assertSame(p1, p2);
    }

    @Test
    public void nilAnyPositionLeaf_cached() {
        PortablePosition p1 = PortablePositionFactory.nilAnyPosition(true);
        PortablePosition p2 = PortablePositionFactory.nilAnyPosition(true);
        Assert.assertSame(p1, p2);
    }

    @Test
    public void nilAnyPosition_nonLeaf() {
        PortablePosition p = PortablePositionFactory.nilAnyPosition(false);
        Assert.assertTrue(p.isNull());
        Assert.assertFalse(p.isEmpty());
        Assert.assertTrue(p.isAny());
        Assert.assertFalse(p.isLeaf());
        Assert.assertFalse(p.isMultiPosition());
    }

    @Test
    public void emptyAnyPosition_leaf() {
        PortablePosition p = PortablePositionFactory.emptyAnyPosition(true);
        Assert.assertFalse(p.isNull());
        Assert.assertTrue(p.isEmpty());
        Assert.assertTrue(p.isAny());
        Assert.assertTrue(p.isLeaf());
        Assert.assertFalse(p.isMultiPosition());
    }

    @Test
    public void emptyAnyPosition_nonLeaf() {
        PortablePosition p = PortablePositionFactory.emptyAnyPosition(false);
        Assert.assertTrue(p.isNull());// automatically nullified, since empty and nonLeaf

        Assert.assertTrue(p.isEmpty());
        Assert.assertTrue(p.isAny());
        Assert.assertFalse(p.isLeaf());
        Assert.assertFalse(p.isMultiPosition());
    }

    @Test
    public void emptyAnyPositionNonLeaf_cached() {
        PortablePosition p1 = PortablePositionFactory.emptyAnyPosition(false);
        PortablePosition p2 = PortablePositionFactory.emptyAnyPosition(false);
        Assert.assertSame(p1, p2);
    }

    @Test
    public void emptyAnyPositionLeaf_cached() {
        PortablePosition p1 = PortablePositionFactory.emptyAnyPosition(true);
        PortablePosition p2 = PortablePositionFactory.emptyAnyPosition(true);
        Assert.assertSame(p1, p2);
    }

    @Test
    public void nilNotLeaf() {
        PortablePosition p = PortablePositionFactory.nil(false);
        Assert.assertTrue(p.isNull());
        Assert.assertFalse(p.isLeaf());
        Assert.assertFalse(p.isAny());
        Assert.assertFalse(p.isMultiPosition());
    }

    @Test
    public void nilLeaf() {
        PortablePosition p = PortablePositionFactory.nil(true);
        Assert.assertTrue(p.isNull());
        Assert.assertTrue(p.isLeaf());
        Assert.assertFalse(p.isAny());
        Assert.assertFalse(p.isMultiPosition());
    }

    @Test
    public void nilNotLeaf_any() {
        PortablePosition p = PortablePositionFactory.nil(true, true);
        Assert.assertTrue(p.isNull());
        Assert.assertTrue(p.isLeaf());
        Assert.assertTrue(p.isAny());
        Assert.assertFalse(p.isMultiPosition());
    }

    @Test
    public void nilLeaf_any() {
        PortablePosition p = PortablePositionFactory.nil(false, true);
        Assert.assertTrue(p.isNull());
        Assert.assertFalse(p.isLeaf());
        Assert.assertTrue(p.isAny());
        Assert.assertFalse(p.isMultiPosition());
    }

    @Test
    public void empty_nonLeaf_nonAny() {
        PortablePosition p = PortablePositionFactory.empty(false, false);
        Assert.assertTrue(p.isEmpty());
        Assert.assertTrue(p.isNull());// nullified automatically

        Assert.assertFalse(p.isLeaf());
        Assert.assertFalse(p.isAny());
        Assert.assertEquals(0, p.getLen());
    }

    @Test
    public void empty_nonLeaf_any() {
        PortablePosition p = PortablePositionFactory.empty(false, true);
        Assert.assertTrue(p.isEmpty());
        Assert.assertTrue(p.isNull());// nullified automatically

        Assert.assertFalse(p.isLeaf());
        Assert.assertTrue(p.isAny());
        Assert.assertEquals(0, p.getLen());
    }

    @Test
    public void empty_leaf_nonAny() {
        PortablePosition p = PortablePositionFactory.empty(true, false);
        Assert.assertTrue(p.isEmpty());
        Assert.assertFalse(p.isNull());
        Assert.assertTrue(p.isLeaf());
        Assert.assertFalse(p.isAny());
        Assert.assertEquals(0, p.getLen());
    }

    @Test
    public void empty_leaf_any() {
        PortablePosition p = PortablePositionFactory.empty(true, true);
        Assert.assertTrue(p.isEmpty());
        Assert.assertFalse(p.isNull());
        Assert.assertTrue(p.isLeaf());
        Assert.assertTrue(p.isAny());
        Assert.assertEquals(0, p.getLen());
    }

    @Test
    public void createSinglePrimitivePosition() {
        // GIVEN
        FieldDefinition fd = new FieldDefinitionImpl(1, "field", FieldType.PORTABLE, 0);
        int streamPosition = 100;
        int index = 1;
        boolean leaf = true;
        // WHEN
        PortablePosition p = PortablePositionFactory.createSinglePrimitivePosition(fd, streamPosition, index, leaf);
        // THEN
        Assert.assertFalse(p.isNull());
        Assert.assertFalse(p.isEmpty());
        Assert.assertFalse(p.isNullOrEmpty());
        Assert.assertEquals(leaf, p.isLeaf());
        Assert.assertFalse(p.isAny());
        Assert.assertEquals(fd.getType(), p.getType());
        Assert.assertEquals((-1), p.getLen());
        Assert.assertEquals((-1), p.getClassId());
        Assert.assertEquals((-1), p.getFactoryId());
        Assert.assertEquals(index, p.getIndex());
        Assert.assertEquals(streamPosition, p.getStreamPosition());
        Assert.assertFalse(p.isMultiPosition());
        PortablePositionFactoryTest.assertAsMultiPositionThrowsException(p);
    }

    @Test
    public void createSinglePrimitivePosition_withoutFieldDefinition() {
        // GIVEN
        int streamPosition = 100;
        int index = 1;
        boolean leaf = true;
        // WHEN
        PortablePosition p = PortablePositionFactory.createSinglePrimitivePosition(null, streamPosition, index, leaf);
        // THEN
        Assert.assertFalse(p.isNull());
        Assert.assertFalse(p.isEmpty());
        Assert.assertFalse(p.isNullOrEmpty());
        Assert.assertEquals(leaf, p.isLeaf());
        Assert.assertFalse(p.isAny());
        Assert.assertNull(p.getType());
        Assert.assertEquals((-1), p.getLen());
        Assert.assertEquals((-1), p.getClassId());
        Assert.assertEquals((-1), p.getFactoryId());
        Assert.assertEquals(index, p.getIndex());
        Assert.assertEquals(streamPosition, p.getStreamPosition());
        Assert.assertFalse(p.isMultiPosition());
        PortablePositionFactoryTest.assertAsMultiPositionThrowsException(p);
    }

    @Test
    public void createSinglePortablePosition() {
        // GIVEN
        FieldDefinition fd = new FieldDefinitionImpl(1, "field", FieldType.PORTABLE, 0);
        int streamPosition = 100;
        int factoryId = 123;
        int classId = 546;
        boolean nil = false;
        boolean leaf = true;
        // WHEN
        PortablePosition p = PortablePositionFactory.createSinglePortablePosition(fd, streamPosition, factoryId, classId, nil, leaf);
        // THEN
        Assert.assertFalse(p.isNull());
        Assert.assertFalse(p.isEmpty());
        Assert.assertFalse(p.isNullOrEmpty());
        Assert.assertEquals(nil, p.isNull());
        Assert.assertEquals(leaf, p.isLeaf());
        Assert.assertFalse(p.isAny());
        Assert.assertEquals(fd.getType(), p.getType());
        Assert.assertEquals((-1), p.getLen());
        Assert.assertEquals(classId, p.getClassId());
        Assert.assertEquals(factoryId, p.getFactoryId());
        Assert.assertEquals((-1), p.getIndex());
        Assert.assertEquals(streamPosition, p.getStreamPosition());
        Assert.assertFalse(p.isMultiPosition());
        PortablePositionFactoryTest.assertAsMultiPositionThrowsException(p);
    }

    @Test
    public void createSinglePortablePosition_withIndex() {
        // GIVEN
        FieldDefinition fd = new FieldDefinitionImpl(1, "field", FieldType.PORTABLE, 0);
        int streamPosition = 100;
        int factoryId = 123;
        int classId = 546;
        int index = 27;
        int len = 30;
        boolean leaf = true;
        // WHEN
        PortablePosition p = PortablePositionFactory.createSinglePortablePosition(fd, streamPosition, factoryId, classId, index, len, leaf);
        // THEN
        Assert.assertFalse(p.isNull());
        Assert.assertFalse(p.isEmpty());
        Assert.assertFalse(p.isNullOrEmpty());
        Assert.assertFalse(p.isNull());
        Assert.assertEquals(leaf, p.isLeaf());
        Assert.assertFalse(p.isAny());
        Assert.assertEquals(fd.getType(), p.getType());
        Assert.assertEquals(len, p.getLen());
        Assert.assertEquals(classId, p.getClassId());
        Assert.assertEquals(factoryId, p.getFactoryId());
        Assert.assertEquals(index, p.getIndex());
        Assert.assertEquals(streamPosition, p.getStreamPosition());
        Assert.assertFalse(p.isMultiPosition());
    }

    @Test
    public void createSinglePortablePosition_withIndex_nullifiedDueIndexOutOfBound() {
        // GIVEN
        FieldDefinition fd = new FieldDefinitionImpl(1, "field", FieldType.PORTABLE, 0);
        int streamPosition = 100;
        int factoryId = 123;
        int classId = 546;
        int index = 1;
        int len = 0;
        boolean leaf = true;
        // WHEN
        PortablePosition p = PortablePositionFactory.createSinglePortablePosition(fd, streamPosition, factoryId, classId, index, len, leaf);
        // THEN
        Assert.assertTrue(p.isNull());// nullified!

    }

    @Test
    public void createMultiPosition_withEmptyPositionList() {
        // GIVEN
        List<PortablePosition> list = Collections.emptyList();
        // WHEN
        PortablePosition m = PortablePositionFactory.createMultiPosition(list);
        // THEN
        Assert.assertTrue(m.isMultiPosition());
        Assert.assertEquals(0, m.asMultiPosition().size());
        Assert.assertNull(m.getType());
    }

    @Test
    public void createMultiPosition_withOnePosition() {
        // GIVEN
        PortablePosition p = PortablePositionFactory.nilNotLeafPosition();
        // WHEN
        PortablePosition m = PortablePositionFactory.createMultiPosition(p);
        // THEN
        Assert.assertTrue(m.isMultiPosition());
        Assert.assertEquals(1, m.asMultiPosition().size());
        Assert.assertEquals(p, m.asMultiPosition().get(0));
        Assert.assertEquals(p.getType(), m.getType());
    }

    @Test
    public void createMultiPosition_withMorePositions() {
        // GIVEN
        PortablePosition p1 = PortablePositionFactory.nilNotLeafPosition();
        PortablePosition p2 = PortablePositionFactory.nil(true);
        // WHEN
        PortablePosition m = PortablePositionFactory.createMultiPosition(Arrays.asList(p1, p2));
        // THEN
        Assert.assertTrue(m.isMultiPosition());
        Assert.assertEquals(2, m.asMultiPosition().size());
        Assert.assertEquals(p1, m.asMultiPosition().get(0));
        Assert.assertEquals(p2, m.asMultiPosition().get(1));
        Assert.assertEquals(p1.getType(), m.getType());
    }
}

