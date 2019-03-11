/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
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
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
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
package org.janusgraph.graphdb.idmanagement;


import IDHandler.DirectionID;
import IDHandler.RelationTypeParse;
import IDManager.VertexIDType;
import IDManager.VertexIDType.UserEdgeLabel;
import IDManager.VertexIDType.UserPropertyKey;
import IDManager.VertexIDType.UserVertex;
import RelationCategory.RELATION;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import org.janusgraph.diskstorage.ReadBuffer;
import org.janusgraph.diskstorage.StaticBuffer;
import org.janusgraph.diskstorage.WriteBuffer;
import org.janusgraph.diskstorage.util.BufferUtil;
import org.janusgraph.diskstorage.util.WriteByteBuffer;
import org.janusgraph.graphdb.database.idassigner.placement.PartitionIDRange;
import org.janusgraph.graphdb.database.idhandling.IDHandler;
import org.janusgraph.graphdb.database.serialize.DataOutput;
import org.janusgraph.graphdb.database.serialize.Serializer;
import org.janusgraph.graphdb.database.serialize.StandardSerializer;
import org.janusgraph.graphdb.internal.RelationCategory;
import org.janusgraph.testutil.RandomGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static BaseKey.SchemaDefinitionProperty;
import static BaseKey.VertexExists;
import static BaseLabel.SchemaDefinitionEdge;
import static IDManager.MAX_PADDING_BITWIDTH;
import static IDManager.USERVERTEX_PADDING_BITWIDTH;
import static ImplicitKey.TIMESTAMP;
import static ImplicitKey.VISIBILITY;


public class IDManagementTest {
    private static final Random random = new Random();

    private static final VertexIDType[] USER_VERTEX_TYPES = new VertexIDType[]{ VertexIDType.NormalVertex, VertexIDType.PartitionedVertex, VertexIDType.UnmodifiableVertex };

    @Test
    public void EntityIDTest() {
        testEntityID(12, 2341, 1234123, 1235123);
        testEntityID(16, 64000, 582919, 583219);
        testEntityID(4, 14, 1, 1000);
        testEntityID(10, 1, 903392, 903592);
        testEntityID(0, 0, 242342, 249342);
        try {
            testEntityID(0, 1, 242342, 242345);
            Assertions.fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            testEntityID(0, 0, (-11), (-10));
            Assertions.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void edgeTypeIDTest() {
        int partitionBits = 16;
        IDManager eid = new IDManager(partitionBits);
        int trails = 1000000;
        Assertions.assertEquals(eid.getPartitionBound(), (1L << partitionBits));
        Serializer serializer = new StandardSerializer();
        for (int t = 0; t < trails; t++) {
            long count = RandomGenerator.randomLong(1, IDManager.getSchemaCountBound());
            long id;
            IDHandler.DirectionID dirID;
            RelationCategory type;
            if ((Math.random()) < 0.5) {
                id = IDManager.getSchemaId(UserEdgeLabel, count);
                Assertions.assertTrue(eid.isEdgeLabelId(id));
                Assertions.assertFalse(IDManager.isSystemRelationTypeId(id));
                type = RelationCategory.EDGE;
                if ((Math.random()) < 0.5)
                    dirID = DirectionID.EDGE_IN_DIR;
                else
                    dirID = DirectionID.EDGE_OUT_DIR;

            } else {
                type = RelationCategory.PROPERTY;
                id = IDManager.getSchemaId(UserPropertyKey, count);
                Assertions.assertTrue(eid.isPropertyKeyId(id));
                Assertions.assertFalse(IDManager.isSystemRelationTypeId(id));
                dirID = DirectionID.PROPERTY_DIR;
            }
            Assertions.assertTrue(eid.isRelationTypeId(id));
            StaticBuffer b = IDHandler.getRelationType(id, dirID, false);
            // System.out.println(dirID);
            // System.out.println(getBinary(id));
            // System.out.println(getBuffer(b.asReadBuffer()));
            ReadBuffer rb = b.asReadBuffer();
            IDHandler.RelationTypeParse parse = IDHandler.readRelationType(rb);
            Assertions.assertEquals(id, parse.typeId);
            Assertions.assertEquals(dirID, parse.dirID);
            Assertions.assertFalse(rb.hasRemaining());
            // Inline edge type
            WriteBuffer wb = new WriteByteBuffer(9);
            IDHandler.writeInlineRelationType(wb, id);
            long newId = IDHandler.readInlineRelationType(wb.getStaticBuffer().asReadBuffer());
            Assertions.assertEquals(id, newId);
            // Compare to Kryo
            DataOutput out = serializer.getDataOutput(10);
            IDHandler.writeRelationType(out, id, dirID, false);
            Assertions.assertEquals(b, out.getStaticBuffer());
            // Make sure the bounds are right
            StaticBuffer[] bounds = IDHandler.getBounds(type, false);
            Assertions.assertTrue(((bounds[0].compareTo(b)) < 0));
            Assertions.assertTrue(((bounds[1].compareTo(b)) > 0));
            bounds = IDHandler.getBounds(RELATION, false);
            Assertions.assertTrue(((bounds[0].compareTo(b)) < 0));
            Assertions.assertTrue(((bounds[1].compareTo(b)) > 0));
        }
    }

    private static final SystemRelationType[] SYSTEM_TYPES = new SystemRelationType[]{ VertexExists, SchemaDefinitionProperty, SchemaDefinitionEdge, VISIBILITY, TIMESTAMP };

    @Test
    public void writingInlineEdgeTypes() {
        int numTries = 100;
        WriteBuffer out = new WriteByteBuffer((8 * numTries));
        for (SystemRelationType t : IDManagementTest.SYSTEM_TYPES) {
            IDHandler.writeInlineRelationType(out, t.longId());
        }
        for (long i = 1; i <= numTries; i++) {
            IDHandler.writeInlineRelationType(out, IDManager.getSchemaId(UserEdgeLabel, (i * 1000)));
        }
        ReadBuffer in = out.getStaticBuffer().asReadBuffer();
        for (SystemRelationType t : IDManagementTest.SYSTEM_TYPES) {
            Assertions.assertEquals(t, SystemTypeManager.getSystemType(IDHandler.readInlineRelationType(in)));
        }
        for (long i = 1; i <= numTries; i++) {
            Assertions.assertEquals((i * 1000), IDManager.stripEntireRelationTypePadding(IDHandler.readInlineRelationType(in)));
        }
    }

    @Test
    public void testDirectionPrefix() {
        for (RelationCategory type : RelationCategory.values()) {
            for (boolean system : new boolean[]{ true, false }) {
                StaticBuffer[] bounds = IDHandler.getBounds(type, system);
                Assertions.assertEquals(1, bounds[0].length());
                Assertions.assertEquals(1, bounds[1].length());
                Assertions.assertTrue(((bounds[0].compareTo(bounds[1])) < 0));
                Assertions.assertTrue(((bounds[1].compareTo(BufferUtil.oneBuffer(1))) < 0));
            }
        }
    }

    @Test
    public void testEdgeTypeWriting() {
        for (SystemRelationType t : IDManagementTest.SYSTEM_TYPES) {
            testEdgeTypeWriting(t.longId());
        }
        for (int i = 0; i < 1000; i++) {
            IDManager.VertexIDType type = ((IDManagementTest.random.nextDouble()) < 0.5) ? VertexIDType.UserPropertyKey : VertexIDType.UserEdgeLabel;
            testEdgeTypeWriting(IDManager.getSchemaId(type, IDManagementTest.random.nextInt(1000000000)));
        }
    }

    @Test
    public void testUserVertexBitWidth() {
        for (IDManager.VertexIDType type : VertexIDType.values()) {
            assert ((!(UserVertex.is(type.suffix()))) || (!(type.isProper()))) || ((type.offset()) == (USERVERTEX_PADDING_BITWIDTH));
            Assertions.assertTrue(((type.offset()) <= (MAX_PADDING_BITWIDTH)));
        }
    }

    @Test
    public void partitionIDRangeTest() {
        List<PartitionIDRange> result = PartitionIDRange.getIDRanges(16, ImmutableList.of(IDManagementTest.getKeyRange((120 << 16), 6, (140 << 16), 8)));
        Assertions.assertTrue(((result.size()) == 1));
        PartitionIDRange r = result.get(0);
        Assertions.assertEquals(121, r.getLowerID());
        Assertions.assertEquals(140, r.getUpperID());
        Assertions.assertEquals((1 << 16), r.getIdUpperBound());
        result = PartitionIDRange.getIDRanges(16, ImmutableList.of(IDManagementTest.getKeyRange((120 << 16), 0, (140 << 16), 0)));
        Assertions.assertTrue(((result.size()) == 1));
        r = result.get(0);
        Assertions.assertEquals(120, r.getLowerID());
        Assertions.assertEquals(140, r.getUpperID());
        result = PartitionIDRange.getIDRanges(8, ImmutableList.of(IDManagementTest.getKeyRange((250 << 24), 0, 0, 0)));
        Assertions.assertTrue(((result.size()) == 1));
        r = result.get(0);
        Assertions.assertEquals(250, r.getLowerID());
        Assertions.assertEquals(0, r.getUpperID());
        for (int i = 0; i < 255; i = i + 5) {
            result = PartitionIDRange.getIDRanges(8, ImmutableList.of(IDManagementTest.getKeyRange((i << 24), 0, (i << 24), 0)));
            Assertions.assertTrue(((result.size()) == 1));
            r = result.get(0);
            for (int j = 0; j < 255; j++)
                Assertions.assertTrue(r.contains(j));

        }
        result = PartitionIDRange.getIDRanges(8, ImmutableList.of(IDManagementTest.getKeyRange((1 << 24), 0, (1 << 24), 1)));
        Assertions.assertTrue(result.isEmpty());
        result = PartitionIDRange.getIDRanges(8, ImmutableList.of(IDManagementTest.getKeyRange((1 << 28), 6, (1 << 28), 8)));
        Assertions.assertTrue(result.isEmpty());
        result = PartitionIDRange.getIDRanges(8, ImmutableList.of(IDManagementTest.getKeyRange((33 << 24), 6, (34 << 24), 8)));
        Assertions.assertTrue(result.isEmpty());
    }
}

