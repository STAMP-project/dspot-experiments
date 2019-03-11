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
package org.janusgraph.diskstorage.cql;


import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.Iterator;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import org.janusgraph.diskstorage.Entry;
import org.janusgraph.diskstorage.EntryMetaData;
import org.janusgraph.diskstorage.StaticBuffer;
import org.janusgraph.diskstorage.keycolumnvalue.SliceQuery;
import org.janusgraph.diskstorage.util.BufferUtil;
import org.janusgraph.diskstorage.util.RecordIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class CQLResultSetKeyIteratorTest {
    private static final SliceQuery ALL_COLUMNS = new SliceQuery(BufferUtil.zeroBuffer(1), BufferUtil.oneBuffer(128));

    @Test
    public void testIterator() throws IOException {
        final Array<Row> rows = Array.rangeClosed(1, 100).map(( idx) -> {
            final Row row = mock(.class);
            when(row.getBytes("key")).thenReturn(ByteBuffer.wrap(Integer.toString((idx / 5)).getBytes()));
            when(row.getBytes("column1")).thenReturn(ByteBuffer.wrap(Integer.toString((idx % 5)).getBytes()));
            when(row.getBytes("value")).thenReturn(ByteBuffer.wrap(Integer.toString(idx).getBytes()));
            return row;
        });
        final ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.iterator()).thenReturn(rows.iterator());
        final CQLColValGetter getter = new CQLColValGetter(new EntryMetaData[0]);
        try (final CQLResultSetKeyIterator resultSetKeyIterator = new CQLResultSetKeyIterator(CQLResultSetKeyIteratorTest.ALL_COLUMNS, getter, resultSet)) {
            int i = 0;
            while (resultSetKeyIterator.hasNext()) {
                final StaticBuffer next = resultSetKeyIterator.next();
                final RecordIterator<Entry> entries = resultSetKeyIterator.getEntries();
                while (entries.hasNext()) {
                    final Row row = rows.get((i++));
                    final Entry entry = entries.next();
                    Assertions.assertEquals(row.getBytes("key"), next.asByteBuffer());
                    Assertions.assertEquals(row.getBytes("column1"), entry.getColumn().asByteBuffer());
                    Assertions.assertEquals(row.getBytes("value"), entry.getValue().asByteBuffer());
                } 
            } 
        }
    }

    @Test
    public void testEmpty() throws IOException {
        final Array<Row> rows = Array.empty();
        final ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.iterator()).thenReturn(rows.iterator());
        final CQLColValGetter getter = new CQLColValGetter(new EntryMetaData[0]);
        try (final CQLResultSetKeyIterator resultSetKeyIterator = new CQLResultSetKeyIterator(CQLResultSetKeyIteratorTest.ALL_COLUMNS, getter, resultSet)) {
            Assertions.assertFalse(resultSetKeyIterator.hasNext());
        }
    }

    @Test
    public void testUneven() throws IOException {
        final Array<Tuple2<ByteBuffer, Array<Tuple2<ByteBuffer, ByteBuffer>>>> keysMap = generateRandomKeysMap();
        final ResultSet resultSet = generateMockedResultSet(keysMap);
        final CQLColValGetter getter = new CQLColValGetter(new EntryMetaData[0]);
        try (final CQLResultSetKeyIterator resultSetKeyIterator = new CQLResultSetKeyIterator(CQLResultSetKeyIteratorTest.ALL_COLUMNS, getter, resultSet)) {
            final Iterator<Tuple2<ByteBuffer, Array<Tuple2<ByteBuffer, ByteBuffer>>>> iterator = keysMap.iterator();
            while (resultSetKeyIterator.hasNext()) {
                final StaticBuffer next = resultSetKeyIterator.next();
                try (final RecordIterator<Entry> entries = resultSetKeyIterator.getEntries()) {
                    final Tuple2<ByteBuffer, Array<Tuple2<ByteBuffer, ByteBuffer>>> current = iterator.next();
                    final ByteBuffer currentKey = current._1;
                    final Array<Tuple2<ByteBuffer, ByteBuffer>> columnValues = current._2;
                    final Iterator<Tuple2<ByteBuffer, ByteBuffer>> columnIterator = columnValues.iterator();
                    while (entries.hasNext()) {
                        final Entry entry = entries.next();
                        final Tuple2<ByteBuffer, ByteBuffer> columnAndValue = columnIterator.next();
                        Assertions.assertEquals(currentKey, next.asByteBuffer());
                        Assertions.assertEquals(columnAndValue._1, entry.getColumn().asByteBuffer());
                        Assertions.assertEquals(columnAndValue._2, entry.getValue().asByteBuffer());
                        Assertions.assertEquals(columnIterator.hasNext(), entries.hasNext());
                    } 
                }
            } 
        }
    }

    @Test
    public void testPartialIterateColumns() throws IOException {
        final Random random = new Random();
        final Array<Tuple2<ByteBuffer, Array<Tuple2<ByteBuffer, ByteBuffer>>>> keysMap = generateRandomKeysMap();
        final ResultSet resultSet = generateMockedResultSet(keysMap);
        final CQLColValGetter getter = new CQLColValGetter(new EntryMetaData[0]);
        try (final CQLResultSetKeyIterator resultSetKeyIterator = new CQLResultSetKeyIterator(CQLResultSetKeyIteratorTest.ALL_COLUMNS, getter, resultSet)) {
            final Iterator<Tuple2<ByteBuffer, Array<Tuple2<ByteBuffer, ByteBuffer>>>> iterator = keysMap.iterator();
            while (resultSetKeyIterator.hasNext()) {
                final StaticBuffer next = resultSetKeyIterator.next();
                try (final RecordIterator<Entry> entries = resultSetKeyIterator.getEntries()) {
                    final Tuple2<ByteBuffer, Array<Tuple2<ByteBuffer, ByteBuffer>>> current = iterator.next();
                    final ByteBuffer currentKey = current._1;
                    final Array<Tuple2<ByteBuffer, ByteBuffer>> columnValues = current._2;
                    final Iterator<Tuple2<ByteBuffer, ByteBuffer>> columnIterator = columnValues.iterator();
                    while (entries.hasNext()) {
                        final Entry entry = entries.next();
                        final Tuple2<ByteBuffer, ByteBuffer> columnAndValue = columnIterator.next();
                        Assertions.assertEquals(currentKey, next.asByteBuffer());
                        Assertions.assertEquals(columnAndValue._1, entry.getColumn().asByteBuffer());
                        Assertions.assertEquals(columnAndValue._2, entry.getValue().asByteBuffer());
                        Assertions.assertEquals(columnIterator.hasNext(), entries.hasNext());
                        // 10% of the time, don't complete the iteration
                        if ((random.nextInt(10)) == 0) {
                            break;
                        }
                    } 
                }
            } 
        }
    }

    @Test
    public void testNoIterateColumns() throws IOException {
        final Array<Tuple2<ByteBuffer, Array<Tuple2<ByteBuffer, ByteBuffer>>>> keysMap = generateRandomKeysMap();
        final ResultSet resultSet = generateMockedResultSet(keysMap);
        final CQLColValGetter getter = new CQLColValGetter(new EntryMetaData[0]);
        try (final CQLResultSetKeyIterator resultSetKeyIterator = new CQLResultSetKeyIterator(CQLResultSetKeyIteratorTest.ALL_COLUMNS, getter, resultSet)) {
            final Iterator<Tuple2<ByteBuffer, Array<Tuple2<ByteBuffer, ByteBuffer>>>> iterator = keysMap.iterator();
            while (resultSetKeyIterator.hasNext()) {
                final StaticBuffer next = resultSetKeyIterator.next();
                Assertions.assertEquals(iterator.next()._1, next.asByteBuffer());
            } 
        }
    }
}

