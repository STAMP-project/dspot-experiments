package com.thinkaurelius.titan.diskstorage.keycolumnvalue;


import EntryMetaData.TTL;
import StaticArrayEntry.GetColVal;
import StaticBuffer.STATIC_FACTORY;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.thinkaurelius.titan.diskstorage.util.StaticArrayBuffer;
import com.thinkaurelius.titan.diskstorage.util.StaticArrayEntry;
import com.thinkaurelius.titan.diskstorage.util.StaticArrayEntryList;
import com.thinkaurelius.titan.diskstorage.util.WriteByteBuffer;
import com.thinkaurelius.titan.graphdb.relations.RelationCache;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.junit.Assert;
import org.junit.Test;

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
            put(EntryMetaData.TIMESTAMP, Long.valueOf(101));
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
        Assert.assertEquals((4 * 4), entry.getValuePosition());
        Assert.assertEquals((6 * 4), entry.length());
        Assert.assertTrue(entry.hasValue());
        for (int i = 1; i <= 6; i++)
            Assert.assertEquals(i, entry.getInt(((i - 1) * 4)));

        ReadBuffer rb = entry.asReadBuffer();
        for (int i = 1; i <= 6; i++)
            Assert.assertEquals(i, rb.getInt());

        Assert.assertFalse(rb.hasRemaining());
        Assert.assertNull(entry.getCache());
        entry.setCache(StaticArrayEntryTest.cache);
        Assert.assertEquals(StaticArrayEntryTest.cache, entry.getCache());
        rb = entry.getColumnAs(STATIC_FACTORY).asReadBuffer();
        for (int i = 1; i <= 4; i++)
            Assert.assertEquals(i, rb.getInt());

        Assert.assertFalse(rb.hasRemaining());
        rb = entry.getValueAs(STATIC_FACTORY).asReadBuffer();
        for (int i = 5; i <= 6; i++)
            Assert.assertEquals(i, rb.getInt());

        Assert.assertFalse(rb.hasRemaining());
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
        Assert.assertEquals(1, r.getByte());
        Assert.assertTrue(Arrays.equals(new byte[]{ 2, 3 }, r.getBytes(2)));
        Assert.assertEquals(1, r.getShort());
        Assert.assertTrue(Arrays.equals(new short[]{ 2, 3 }, r.getShorts(2)));
        Assert.assertEquals(1, r.getInt());
        Assert.assertEquals(2, r.getInt());
        Assert.assertTrue(Arrays.equals(new int[]{ 3 }, r.getInts(1)));
        Assert.assertEquals(1, r.getLong());
        Assert.assertTrue(Arrays.equals(new long[]{ 2, 3 }, r.getLongs(2)));
        Assert.assertEquals(1.0, r.getFloat(), 1.0E-5);
        Assert.assertTrue(Arrays.equals(new float[]{ 2.0F, 3.0F }, r.getFloats(2)));
        Assert.assertEquals(1, r.getDouble(), 1.0E-4);
        Assert.assertTrue(Arrays.equals(new double[]{ 2.0, 3.0 }, r.getDoubles(2)));
        Assert.assertEquals(((char) (101)), r.getChar());
        Assert.assertEquals(((char) (102)), r.getChar());
        Assert.assertTrue(Arrays.equals(new char[]{ ((char) (103)) }, r.getChars(1)));
    }

    @Test
    public void testInversion() {
        WriteBuffer wb = new WriteByteBuffer(20);
        wb.putInt(1).putInt(2).putInt(3).putInt(4);
        Entry entry = new StaticArrayEntry(wb.getStaticBufferFlipBytes(4, (2 * 4)), (3 * 4));
        ReadBuffer rb = entry.asReadBuffer();
        Assert.assertEquals(1, rb.getInt());
        Assert.assertEquals(2, rb.subrange(4, true).getInt());
        Assert.assertEquals((~2), rb.getInt());
        Assert.assertEquals(3, rb.getInt());
        Assert.assertEquals(4, rb.getInt());
        rb.movePositionTo(entry.getValuePosition());
        Assert.assertEquals(4, rb.getInt());
    }

    @Test
    public void testEntryList() {
        Map<Integer, Long> entries = new HashMap<Integer, Long>();
        for (int i = 0; i < 50; i++)
            entries.put(((i * 2) + 7), Math.round((((Math.random()) / 2) * (Long.MAX_VALUE))));

        EntryList[] el = new EntryList[7];
        el[0] = StaticArrayEntryList.ofBytes(entries.entrySet(), StaticArrayEntryTest.ByteEntryGetter.INSTANCE);
        el[1] = StaticArrayEntryList.ofByteBuffer(entries.entrySet(), StaticArrayEntryTest.BBEntryGetter.INSTANCE);
        el[2] = StaticArrayEntryList.ofStaticBuffer(entries.entrySet(), StaticArrayEntryTest.StaticEntryGetter.INSTANCE);
        el[3] = StaticArrayEntryList.ofByteBuffer(entries.entrySet().iterator(), StaticArrayEntryTest.BBEntryGetter.INSTANCE);
        el[4] = StaticArrayEntryList.ofStaticBuffer(entries.entrySet().iterator(), StaticArrayEntryTest.StaticEntryGetter.INSTANCE);
        el[5] = StaticArrayEntryList.of(Iterables.transform(entries.entrySet(), new Function<Map.Entry<Integer, Long>, Entry>() {
            @Nullable
            @Override
            public Entry apply(@Nullable
            Map.Entry<Integer, Long> entry) {
                return StaticArrayEntry.ofByteBuffer(entry, StaticArrayEntryTest.BBEntryGetter.INSTANCE);
            }
        }));
        el[6] = StaticArrayEntryList.of(Iterables.transform(entries.entrySet(), new Function<Map.Entry<Integer, Long>, Entry>() {
            @Nullable
            @Override
            public Entry apply(@Nullable
            Map.Entry<Integer, Long> entry) {
                return StaticArrayEntry.ofBytes(entry, StaticArrayEntryTest.ByteEntryGetter.INSTANCE);
            }
        }));
        for (int i = 0; i < (el.length); i++) {
            Assert.assertEquals(entries.size(), el[i].size());
            int num = 0;
            for (Entry e : el[i]) {
                StaticArrayEntryTest.checkEntry(e, entries);
                Assert.assertFalse(e.hasMetaData());
                Assert.assertTrue(e.getMetaData().isEmpty());
                Assert.assertNull(e.getCache());
                e.setCache(StaticArrayEntryTest.cache);
                num++;
            }
            Assert.assertEquals(entries.size(), num);
            Iterator<Entry> iter = el[i].reuseIterator();
            num = 0;
            while (iter.hasNext()) {
                Entry e = iter.next();
                StaticArrayEntryTest.checkEntry(e, entries);
                Assert.assertFalse(e.hasMetaData());
                Assert.assertTrue(e.getMetaData().isEmpty());
                Assert.assertEquals(StaticArrayEntryTest.cache, e.getCache());
                num++;
            } 
            Assert.assertEquals(entries.size(), num);
        }
    }

    /**
     * Copied from above - the only difference is using schema instances and checking the schema
     */
    @Test
    public void testEntryListWithMetaSchema() {
        Map<Integer, Long> entries = new HashMap<Integer, Long>();
        for (int i = 0; i < 50; i++)
            entries.put(((i * 2) + 7), Math.round((((Math.random()) / 2) * (Long.MAX_VALUE))));

        EntryList[] el = new EntryList[7];
        el[0] = StaticArrayEntryList.ofBytes(entries.entrySet(), StaticArrayEntryTest.ByteEntryGetter.SCHEMA_INSTANCE);
        el[1] = StaticArrayEntryList.ofByteBuffer(entries.entrySet(), StaticArrayEntryTest.BBEntryGetter.SCHEMA_INSTANCE);
        el[2] = StaticArrayEntryList.ofStaticBuffer(entries.entrySet(), StaticArrayEntryTest.StaticEntryGetter.SCHEMA_INSTANCE);
        el[3] = StaticArrayEntryList.ofByteBuffer(entries.entrySet().iterator(), StaticArrayEntryTest.BBEntryGetter.SCHEMA_INSTANCE);
        el[4] = StaticArrayEntryList.ofStaticBuffer(entries.entrySet().iterator(), StaticArrayEntryTest.StaticEntryGetter.SCHEMA_INSTANCE);
        el[5] = StaticArrayEntryList.of(Iterables.transform(entries.entrySet(), new Function<Map.Entry<Integer, Long>, Entry>() {
            @Nullable
            @Override
            public Entry apply(@Nullable
            Map.Entry<Integer, Long> entry) {
                return StaticArrayEntry.ofByteBuffer(entry, StaticArrayEntryTest.BBEntryGetter.SCHEMA_INSTANCE);
            }
        }));
        el[6] = StaticArrayEntryList.of(Iterables.transform(entries.entrySet(), new Function<Map.Entry<Integer, Long>, Entry>() {
            @Nullable
            @Override
            public Entry apply(@Nullable
            Map.Entry<Integer, Long> entry) {
                return StaticArrayEntry.ofBytes(entry, StaticArrayEntryTest.ByteEntryGetter.SCHEMA_INSTANCE);
            }
        }));
        for (int i = 0; i < (el.length); i++) {
            // System.out.println("Iteration: " + i);
            Assert.assertEquals(entries.size(), el[i].size());
            int num = 0;
            for (Entry e : el[i]) {
                StaticArrayEntryTest.checkEntry(e, entries);
                Assert.assertTrue(e.hasMetaData());
                Assert.assertFalse(e.getMetaData().isEmpty());
                Assert.assertEquals(StaticArrayEntryTest.metaData, e.getMetaData());
                Assert.assertNull(e.getCache());
                e.setCache(StaticArrayEntryTest.cache);
                num++;
            }
            Assert.assertEquals(entries.size(), num);
            Iterator<Entry> iter = el[i].reuseIterator();
            num = 0;
            while (iter.hasNext()) {
                Entry e = iter.next();
                Assert.assertTrue(e.hasMetaData());
                Assert.assertFalse(e.getMetaData().isEmpty());
                Assert.assertEquals(StaticArrayEntryTest.metaData, e.getMetaData());
                Assert.assertEquals(StaticArrayEntryTest.cache, e.getCache());
                StaticArrayEntryTest.checkEntry(e, entries);
                num++;
            } 
            Assert.assertEquals(entries.size(), num);
        }
    }

    @Test
    public void testTTLMetadata() throws Exception {
        WriteBuffer wb = new WriteByteBuffer(128);
        wb.putInt(1).putInt(2).putInt(3).putInt(4);
        int valuePos = wb.getPosition();
        wb.putInt(5).putInt(6);
        StaticArrayEntry entry = new StaticArrayEntry(wb.getStaticBuffer(), valuePos);
        entry.setMetaData(TTL, 42);
        Assert.assertEquals(42, entry.getMetaData().get(TTL));
    }

    private static enum BBEntryGetter implements GetColVal<Map.Entry<Integer, Long>, ByteBuffer> {

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

    private static enum ByteEntryGetter implements GetColVal<Map.Entry<Integer, Long>, byte[]> {

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

    private static enum StaticEntryGetter implements GetColVal<Map.Entry<Integer, Long>, StaticBuffer> {

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

