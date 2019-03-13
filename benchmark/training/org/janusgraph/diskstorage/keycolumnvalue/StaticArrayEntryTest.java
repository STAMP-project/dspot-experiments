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
package org.janusgraph.diskstorage.keycolumnvalue;


import EntryMetaData.TTL;
import StaticArrayEntry.GetColVal;
import StaticBuffer.STATIC_FACTORY;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.janusgraph.diskstorage.util.StaticArrayBuffer;
import org.janusgraph.diskstorage.util.StaticArrayEntry;
import org.janusgraph.diskstorage.util.WriteByteBuffer;
import org.janusgraph.graphdb.relations.RelationCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static EntryMetaData.TIMESTAMP;
import static EntryMetaData.TTL;
import static EntryMetaData.VISIBILITY;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class StaticArrayEntryTest {
    private static final RelationCache cache = new RelationCache(Direction.OUT, 5, 105, "Hello");

    private static final EntryMetaData[] metaSchema = new EntryMetaData[]{ TIMESTAMP, TTL, VISIBILITY };

    private static final Map<EntryMetaData, Object> metaData = new EntryMetaData.Map() {
        {
            put(EntryMetaData.TIMESTAMP, 101L);
            put(TTL, 42);
            put(EntryMetaData.VISIBILITY, "SOS/K5a-89 SOS/sdf3");
        }
    };

    @Test
    public void testArrayBuffer() {
        WriteBuffer wb = new WriteByteBuffer(128);
        wb.putInt(1).putInt(2).putInt(3).putInt(4);
        int valuePos = wb.getPosition();
        wb.putInt(5).putInt(6);
        Entry entry = new StaticArrayEntry(wb.getStaticBuffer(), valuePos);
        Assertions.assertEquals((4 * 4), entry.getValuePosition());
        Assertions.assertEquals((6 * 4), entry.length());
        Assertions.assertTrue(entry.hasValue());
        for (int i = 1; i <= 6; i++)
            Assertions.assertEquals(i, entry.getInt(((i - 1) * 4)));

        ReadBuffer rb = entry.asReadBuffer();
        for (int i = 1; i <= 6; i++)
            Assertions.assertEquals(i, rb.getInt());

        Assertions.assertFalse(rb.hasRemaining());
        Assertions.assertNull(entry.getCache());
        entry.setCache(StaticArrayEntryTest.cache);
        Assertions.assertEquals(StaticArrayEntryTest.cache, entry.getCache());
        rb = entry.getColumnAs(STATIC_FACTORY).asReadBuffer();
        for (int i = 1; i <= 4; i++)
            Assertions.assertEquals(i, rb.getInt());

        Assertions.assertFalse(rb.hasRemaining());
        rb = entry.getValueAs(STATIC_FACTORY).asReadBuffer();
        for (int i = 5; i <= 6; i++)
            Assertions.assertEquals(i, rb.getInt());

        Assertions.assertFalse(rb.hasRemaining());
    }

    @Test
    public void testReadWrite() {
        WriteBuffer b = new WriteByteBuffer(10);
        for (int i = 1; i < 4; i++)
            b.putByte(((byte) (i)));

        for (int i = 1; i < 4; i++)
            b.putShort(((short) (i)));

        for (int i = 1; i < 4; i++)
            b.putInt(i);

        for (int i = 1; i < 4; i++)
            b.putLong(i);

        for (int i = 1; i < 4; i++)
            b.putFloat(i);

        for (int i = 1; i < 4; i++)
            b.putDouble(i);

        for (int i = 101; i < 104; i++)
            b.putChar(((char) (i)));

        ReadBuffer r = b.getStaticBuffer().asReadBuffer();
        Assertions.assertEquals(1, r.getByte());
        Assertions.assertTrue(Arrays.equals(new byte[]{ 2, 3 }, r.getBytes(2)));
        Assertions.assertEquals(1, r.getShort());
        Assertions.assertTrue(Arrays.equals(new short[]{ 2, 3 }, r.getShorts(2)));
        Assertions.assertEquals(1, r.getInt());
        Assertions.assertEquals(2, r.getInt());
        Assertions.assertTrue(Arrays.equals(new int[]{ 3 }, r.getInts(1)));
        Assertions.assertEquals(1, r.getLong());
        Assertions.assertTrue(Arrays.equals(new long[]{ 2, 3 }, r.getLongs(2)));
        Assertions.assertEquals(1.0, r.getFloat(), 1.0E-5);
        Assertions.assertTrue(Arrays.equals(new float[]{ 2.0F, 3.0F }, r.getFloats(2)));
        Assertions.assertEquals(1, r.getDouble(), 1.0E-4);
        Assertions.assertTrue(Arrays.equals(new double[]{ 2.0, 3.0 }, r.getDoubles(2)));
        Assertions.assertEquals(((char) (101)), r.getChar());
        Assertions.assertEquals(((char) (102)), r.getChar());
        Assertions.assertTrue(Arrays.equals(new char[]{ ((char) (103)) }, r.getChars(1)));
    }

    @Test
    public void testInversion() {
        WriteBuffer wb = new WriteByteBuffer(20);
        wb.putInt(1).putInt(2).putInt(3).putInt(4);
        Entry entry = new StaticArrayEntry(wb.getStaticBufferFlipBytes(4, (2 * 4)), (3 * 4));
        ReadBuffer rb = entry.asReadBuffer();
        Assertions.assertEquals(1, rb.getInt());
        Assertions.assertEquals(2, rb.subrange(4, true).getInt());
        Assertions.assertEquals((~2), rb.getInt());
        Assertions.assertEquals(3, rb.getInt());
        Assertions.assertEquals(4, rb.getInt());
        rb.movePositionTo(entry.getValuePosition());
        Assertions.assertEquals(4, rb.getInt());
    }

    @Test
    public void testEntryList() {
        final Map<Integer, Long> entries = generateRandomEntries();
        EntryList[] el = generateEntryListArray(entries, "INSTANCE");
        for (final EntryList anEl : el) {
            Assertions.assertEquals(entries.size(), anEl.size());
            int num = 0;
            for (final Entry e : anEl) {
                StaticArrayEntryTest.checkEntry(e, entries);
                Assertions.assertFalse(e.hasMetaData());
                Assertions.assertTrue(e.getMetaData().isEmpty());
                Assertions.assertNull(e.getCache());
                e.setCache(StaticArrayEntryTest.cache);
                num++;
            }
            Assertions.assertEquals(entries.size(), num);
            final Iterator<Entry> iterator = anEl.reuseIterator();
            num = 0;
            while (iterator.hasNext()) {
                final Entry e = iterator.next();
                StaticArrayEntryTest.checkEntry(e, entries);
                Assertions.assertFalse(e.hasMetaData());
                Assertions.assertTrue(e.getMetaData().isEmpty());
                Assertions.assertEquals(StaticArrayEntryTest.cache, e.getCache());
                num++;
            } 
            Assertions.assertEquals(entries.size(), num);
        }
    }

    /**
     * Copied from above - the only difference is using schema instances and checking the schema
     */
    @Test
    public void testEntryListWithMetaSchema() {
        final Map<Integer, Long> entries = generateRandomEntries();
        EntryList[] el = generateEntryListArray(entries, "SCHEMA_INSTANCE");
        for (final EntryList anEl : el) {
            // System.out.println("Iteration: " + i);
            Assertions.assertEquals(entries.size(), anEl.size());
            int num = 0;
            for (final Entry e : anEl) {
                StaticArrayEntryTest.checkEntry(e, entries);
                Assertions.assertTrue(e.hasMetaData());
                Assertions.assertFalse(e.getMetaData().isEmpty());
                Assertions.assertEquals(StaticArrayEntryTest.metaData, e.getMetaData());
                Assertions.assertNull(e.getCache());
                e.setCache(StaticArrayEntryTest.cache);
                num++;
            }
            Assertions.assertEquals(entries.size(), num);
            final Iterator<Entry> iter = anEl.reuseIterator();
            num = 0;
            while (iter.hasNext()) {
                final Entry e = iter.next();
                Assertions.assertTrue(e.hasMetaData());
                Assertions.assertFalse(e.getMetaData().isEmpty());
                Assertions.assertEquals(StaticArrayEntryTest.metaData, e.getMetaData());
                Assertions.assertEquals(StaticArrayEntryTest.cache, e.getCache());
                StaticArrayEntryTest.checkEntry(e, entries);
                num++;
            } 
            Assertions.assertEquals(entries.size(), num);
        }
    }

    @Test
    public void testTTLMetadata() {
        WriteBuffer wb = new WriteByteBuffer(128);
        wb.putInt(1).putInt(2).putInt(3).putInt(4);
        int valuePos = wb.getPosition();
        wb.putInt(5).putInt(6);
        StaticArrayEntry entry = new StaticArrayEntry(wb.getStaticBuffer(), valuePos);
        entry.setMetaData(TTL, 42);
        Assertions.assertEquals(42, entry.getMetaData().get(TTL));
    }

    private enum BBEntryGetter implements GetColVal<Map.Entry<Integer, Long>, ByteBuffer> {

        INSTANCE,
        SCHEMA_INSTANCE;
        @Override
        public ByteBuffer getColumn(Map.Entry<Integer, Long> element) {
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(element.getKey()).flip();
            return b;
        }

        @Override
        public ByteBuffer getValue(Map.Entry<Integer, Long> element) {
            ByteBuffer b = ByteBuffer.allocate(8);
            b.putLong(element.getValue()).flip();
            return b;
        }

        @Override
        public EntryMetaData[] getMetaSchema(Map.Entry<Integer, Long> element) {
            if ((this) == (StaticArrayEntryTest.BBEntryGetter.INSTANCE))
                return StaticArrayEntry.EMPTY_SCHEMA;
            else
                return StaticArrayEntryTest.metaSchema;

        }

        @Override
        public Object getMetaData(Map.Entry<Integer, Long> element, EntryMetaData meta) {
            if ((this) == (StaticArrayEntryTest.BBEntryGetter.INSTANCE))
                throw new UnsupportedOperationException(("Unsupported meta data: " + meta));
            else
                return StaticArrayEntryTest.metaData.get(meta);

        }
    }

    private enum ByteEntryGetter implements GetColVal<Map.Entry<Integer, Long>, byte[]> {

        INSTANCE,
        SCHEMA_INSTANCE;
        @Override
        public byte[] getColumn(Map.Entry<Integer, Long> element) {
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(element.getKey());
            return b.array();
        }

        @Override
        public byte[] getValue(Map.Entry<Integer, Long> element) {
            ByteBuffer b = ByteBuffer.allocate(8);
            b.putLong(element.getValue());
            return b.array();
        }

        @Override
        public EntryMetaData[] getMetaSchema(Map.Entry<Integer, Long> element) {
            if ((this) == (StaticArrayEntryTest.ByteEntryGetter.INSTANCE))
                return StaticArrayEntry.EMPTY_SCHEMA;
            else
                return StaticArrayEntryTest.metaSchema;

        }

        @Override
        public Object getMetaData(Map.Entry<Integer, Long> element, EntryMetaData meta) {
            if ((this) == (StaticArrayEntryTest.ByteEntryGetter.INSTANCE))
                throw new UnsupportedOperationException(("Unsupported meta data: " + meta));
            else
                return StaticArrayEntryTest.metaData.get(meta);

        }
    }

    private enum StaticEntryGetter implements GetColVal<Map.Entry<Integer, Long>, StaticBuffer> {

        INSTANCE,
        SCHEMA_INSTANCE;
        @Override
        public StaticBuffer getColumn(Map.Entry<Integer, Long> element) {
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(element.getKey());
            return StaticArrayBuffer.of(b.array());
        }

        @Override
        public StaticBuffer getValue(Map.Entry<Integer, Long> element) {
            ByteBuffer b = ByteBuffer.allocate(8);
            b.putLong(element.getValue());
            return StaticArrayBuffer.of(b.array());
        }

        @Override
        public EntryMetaData[] getMetaSchema(Map.Entry<Integer, Long> element) {
            if ((this) == (StaticArrayEntryTest.StaticEntryGetter.INSTANCE))
                return StaticArrayEntry.EMPTY_SCHEMA;
            else
                return StaticArrayEntryTest.metaSchema;

        }

        @Override
        public Object getMetaData(Map.Entry<Integer, Long> element, EntryMetaData meta) {
            if ((this) == (StaticArrayEntryTest.StaticEntryGetter.INSTANCE))
                throw new UnsupportedOperationException(("Unsupported meta data: " + meta));
            else
                return StaticArrayEntryTest.metaData.get(meta);

        }
    }
}

