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
package com.hazelcast.query.impl;


import Comparison.GREATER;
import Comparison.GREATER_OR_EQUAL;
import Comparison.LESS;
import Comparison.LESS_OR_EQUAL;
import Comparison.NOT_EQUAL;
import Index.OperationSource.USER;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.map.impl.record.DataRecordFactory;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.Records;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.partition.strategy.DefaultPartitioningStrategy;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(QuickTest.class)
public class IndexTest {
    @Parameterized.Parameter(0)
    public IndexCopyBehavior copyBehavior;

    static final short FACTORY_ID = 1;

    final InternalSerializationService ss = new DefaultSerializationServiceBuilder().addPortableFactory(IndexTest.FACTORY_ID, new IndexTest.TestPortableFactory()).build();

    private PartitioningStrategy partitionStrategy = new DefaultPartitioningStrategy();

    final DataRecordFactory recordFactory = new DataRecordFactory(new MapConfig(), ss, partitionStrategy);

    @Test
    public void testBasics() {
        testIt(true);
        testIt(false);
    }

    @Test
    public void testRemoveEnumIndex() {
        Indexes is = Indexes.newBuilder(ss, copyBehavior).build();
        is.addOrGetIndex("favoriteCity", false);
        Data key = ss.toData(1);
        Data value = ss.toData(new IndexTest.SerializableWithEnum(IndexTest.SerializableWithEnum.City.ISTANBUL));
        is.putEntry(new QueryEntry(ss, key, value, newExtractor()), null, USER);
        Assert.assertNotNull(is.getIndex("favoriteCity"));
        Record record = recordFactory.newRecord(key, value);
        is.removeEntry(key, Records.getValueOrCachedValue(record, ss), USER);
        Assert.assertEquals(0, is.getIndex("favoriteCity").getRecords(IndexTest.SerializableWithEnum.City.ISTANBUL).size());
    }

    @Test
    public void testUpdateEnumIndex() {
        Indexes is = Indexes.newBuilder(ss, copyBehavior).build();
        is.addOrGetIndex("favoriteCity", false);
        Data key = ss.toData(1);
        Data value = ss.toData(new IndexTest.SerializableWithEnum(IndexTest.SerializableWithEnum.City.ISTANBUL));
        is.putEntry(new QueryEntry(ss, key, value, newExtractor()), null, USER);
        Data newValue = ss.toData(new IndexTest.SerializableWithEnum(IndexTest.SerializableWithEnum.City.KRAKOW));
        is.putEntry(new QueryEntry(ss, key, newValue, newExtractor()), value, USER);
        Assert.assertEquals(0, is.getIndex("favoriteCity").getRecords(IndexTest.SerializableWithEnum.City.ISTANBUL).size());
        Assert.assertEquals(1, is.getIndex("favoriteCity").getRecords(IndexTest.SerializableWithEnum.City.KRAKOW).size());
    }

    @Test
    public void testIndex() throws QueryException {
        Indexes is = Indexes.newBuilder(ss, copyBehavior).build();
        Index dIndex = is.addOrGetIndex("d", false);
        Index boolIndex = is.addOrGetIndex("bool", false);
        Index strIndex = is.addOrGetIndex("str", false);
        for (int i = 0; i < 1000; i++) {
            Data key = ss.toData(i);
            Data value = ss.toData(new IndexTest.MainPortable(((i % 2) == 0), (-10.34), ("joe" + i)));
            is.putEntry(new QueryEntry(ss, key, value, newExtractor()), null, USER);
        }
        Assert.assertEquals(1000, dIndex.getRecords((-10.34)).size());
        Assert.assertEquals(1, strIndex.getRecords("joe23").size());
        Assert.assertEquals(500, boolIndex.getRecords(true).size());
        clearIndexes(dIndex, boolIndex, strIndex);
        for (int i = 0; i < 1000; i++) {
            Data key = ss.toData(i);
            Data value = ss.toData(new IndexTest.MainPortable(false, 11.34, "joe"));
            is.putEntry(new QueryEntry(ss, key, value, newExtractor()), null, USER);
        }
        Assert.assertEquals(0, dIndex.getRecords((-10.34)).size());
        Assert.assertEquals(0, strIndex.getRecords("joe23").size());
        Assert.assertEquals(1000, strIndex.getRecords("joe").size());
        Assert.assertEquals(1000, boolIndex.getRecords(false).size());
        Assert.assertEquals(0, boolIndex.getRecords(true).size());
        clearIndexes(dIndex, boolIndex, strIndex);
        for (int i = 0; i < 1000; i++) {
            Data key = ss.toData(i);
            Data value = ss.toData(new IndexTest.MainPortable(false, ((-1) * (i + 1)), ("joe" + i)));
            is.putEntry(new QueryEntry(ss, key, value, newExtractor()), null, USER);
        }
        Assert.assertEquals(0, dIndex.getRecords(1.0, true, 1001.0, true).size());
        Assert.assertEquals(1000, dIndex.getRecords((-1001.0), true, (-1.0), true).size());
        Assert.assertEquals(0, dIndex.getRecords((-1.0), true, (-1001.0), true).size());
        clearIndexes(dIndex, boolIndex, strIndex);
        for (int i = 0; i < 1000; i++) {
            Data key = ss.toData(i);
            Data value = ss.toData(new IndexTest.MainPortable(false, (1 * (i + 1)), ("joe" + i)));
            is.putEntry(new QueryEntry(ss, key, value, newExtractor()), null, USER);
        }
        Assert.assertEquals(1000, dIndex.getRecords(1.0, true, 1001.0, true).size());
        Assert.assertEquals(0, dIndex.getRecords((-1.0), true, (-1001.0), true).size());
        Assert.assertEquals(400, dIndex.getRecords(GREATER, 600.0).size());
        Assert.assertEquals(401, dIndex.getRecords(GREATER_OR_EQUAL, 600.0).size());
        Assert.assertEquals(9, dIndex.getRecords(LESS, 10.0).size());
        Assert.assertEquals(10, dIndex.getRecords(LESS_OR_EQUAL, 10.0).size());
        Assert.assertEquals(1, is.query(new com.hazelcast.query.impl.predicates.AndPredicate(new EqualPredicate("d", 1.0), new EqualPredicate("bool", "false"))).size());
        Assert.assertEquals(1, is.query(new com.hazelcast.query.impl.predicates.AndPredicate(new EqualPredicate("d", 1), new EqualPredicate("bool", Boolean.FALSE))).size());
        Assert.assertEquals(1, is.query(new com.hazelcast.query.impl.predicates.AndPredicate(new EqualPredicate("d", "1"), new EqualPredicate("bool", false))).size());
    }

    @Test
    public void testIndexWithNull() throws QueryException {
        Indexes is = Indexes.newBuilder(ss, copyBehavior).build();
        Index strIndex = is.addOrGetIndex("str", true);
        Data value = ss.toData(new IndexTest.MainPortable(false, 1, null));
        Data key1 = ss.toData(0);
        is.putEntry(new QueryEntry(ss, key1, value, newExtractor()), null, USER);
        value = ss.toData(new IndexTest.MainPortable(false, 2, null));
        Data key2 = ss.toData(1);
        is.putEntry(new QueryEntry(ss, key2, value, newExtractor()), null, USER);
        for (int i = 2; i < 1000; i++) {
            Data key = ss.toData(i);
            value = ss.toData(new IndexTest.MainPortable(false, (1 * (i + 1)), ("joe" + i)));
            is.putEntry(new QueryEntry(ss, key, value, newExtractor()), null, USER);
        }
        Assert.assertEquals(2, strIndex.getRecords(((Comparable) (null))).size());
        Assert.assertEquals(998, strIndex.getRecords(NOT_EQUAL, null).size());
    }

    private class TestPortableFactory implements PortableFactory {
        public Portable create(int classId) {
            switch (classId) {
                case IndexTest.MainPortable.CLASS_ID :
                    return new IndexTest.MainPortable();
                default :
                    return null;
            }
        }

        public int getFactoryId() {
            return IndexTest.FACTORY_ID;
        }
    }

    private static final class SerializableWithEnum implements DataSerializable {
        enum City {

            ISTANBUL,
            RIZE,
            KRAKOW,
            TRABZON;}

        private IndexTest.SerializableWithEnum.City favoriteCity;

        private SerializableWithEnum() {
        }

        private SerializableWithEnum(IndexTest.SerializableWithEnum.City favoriteCity) {
            this.favoriteCity = favoriteCity;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeObject(favoriteCity);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            favoriteCity = in.readObject();
        }
    }

    private static final class MainPortable implements Portable {
        public static final int CLASS_ID = 1;

        byte b;

        boolean bool;

        char c;

        short s;

        int i;

        long l;

        float f;

        double d;

        String str;

        private MainPortable() {
        }

        private MainPortable(boolean bool, double d, String str) {
            this.bool = bool;
            this.d = d;
            this.str = str;
        }

        private MainPortable(byte b, boolean bool, char c, short s, int i, long l, float f, double d, String str) {
            this.b = b;
            this.bool = bool;
            this.c = c;
            this.s = s;
            this.i = i;
            this.l = l;
            this.f = f;
            this.d = d;
            this.str = str;
        }

        public int getClassId() {
            return IndexTest.MainPortable.CLASS_ID;
        }

        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeByte("b", b);
            writer.writeBoolean("bool", bool);
            writer.writeChar("c", c);
            writer.writeShort("s", s);
            writer.writeInt("i", i);
            writer.writeLong("l", l);
            writer.writeFloat("f", f);
            writer.writeDouble("d", d);
            writer.writeUTF("str", str);
        }

        public void readPortable(PortableReader reader) throws IOException {
            b = reader.readByte("b");
            bool = reader.readBoolean("bool");
            c = reader.readChar("c");
            s = reader.readShort("s");
            i = reader.readInt("i");
            l = reader.readLong("l");
            f = reader.readFloat("f");
            d = reader.readDouble("d");
            str = reader.readUTF("str");
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            IndexTest.MainPortable that = ((IndexTest.MainPortable) (o));
            if ((b) != (that.b)) {
                return false;
            }
            if ((bool) != (that.bool)) {
                return false;
            }
            if ((c) != (that.c)) {
                return false;
            }
            if ((Double.compare(that.d, d)) != 0) {
                return false;
            }
            if ((Float.compare(that.f, f)) != 0) {
                return false;
            }
            if ((i) != (that.i)) {
                return false;
            }
            if ((l) != (that.l)) {
                return false;
            }
            if ((s) != (that.s)) {
                return false;
            }
            return (str) != null ? str.equals(that.str) : (that.str) == null;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = ((int) (b));
            result = (31 * result) + (bool ? 1 : 0);
            result = (31 * result) + ((int) (c));
            result = (31 * result) + ((int) (s));
            result = (31 * result) + (i);
            result = (31 * result) + ((int) ((l) ^ ((l) >>> 32)));
            result = (31 * result) + ((f) != (+0.0F) ? Float.floatToIntBits(f) : 0);
            temp = ((d) != (+0.0)) ? Double.doubleToLongBits(d) : 0L;
            result = (31 * result) + ((int) (temp ^ (temp >>> 32)));
            result = (31 * result) + ((str) != null ? str.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return ((((((((((((((((((("MainPortable{" + "b=") + (b)) + ", bool=") + (bool)) + ", c=") + (c)) + ", s=") + (s)) + ", i=") + (i)) + ", l=") + (l)) + ", f=") + (f)) + ", d=") + (d)) + ", str='") + (str)) + '\'') + '}';
        }

        public int getFactoryId() {
            return IndexTest.FACTORY_ID;
        }
    }

    class QueryRecord extends QueryableEntry {
        Data key;

        Comparable attributeValue;

        QueryRecord(Data key, Comparable attributeValue) {
            this.key = key;
            this.attributeValue = attributeValue;
        }

        @Override
        public Comparable getAttributeValue(String attributeName) throws QueryException {
            return attributeValue;
        }

        @Override
        public Object getTargetObject(boolean key) {
            return key ? true : attributeValue;
        }

        public Data getKeyData() {
            return key;
        }

        public Data getValueData() {
            return null;
        }

        public long getCreationTime() {
            return 0;
        }

        public long getLastAccessTime() {
            return 0;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return attributeValue;
        }

        public Object setValue(Object value) {
            return null;
        }

        public void changeAttribute(Comparable newAttributeValue) {
            this.attributeValue = newAttributeValue;
        }

        public void writeData(ObjectDataOutput out) throws IOException {
        }

        public void readData(ObjectDataInput in) throws IOException {
        }

        public Record toRecord() {
            Record<Data> record = recordFactory.newRecord(key, attributeValue);
            return record;
        }
    }
}

