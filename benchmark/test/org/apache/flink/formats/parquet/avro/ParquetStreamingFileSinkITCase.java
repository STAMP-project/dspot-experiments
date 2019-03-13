/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.formats.parquet.avro;


import java.io.File;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificData;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.parquet.generated.Address;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.test.util.AbstractTestBase;
import org.junit.Test;


/**
 * Simple integration test case for writing bulk encoded files with the
 * {@link StreamingFileSink} with Parquet.
 */
@SuppressWarnings("serial")
public class ParquetStreamingFileSinkITCase extends AbstractTestBase {
    @Test
    public void testWriteParquetAvroSpecific() throws Exception {
        final File folder = TEMPORARY_FOLDER.newFolder();
        final List<Address> data = Arrays.asList(new Address(1, "a", "b", "c", "12345"), new Address(2, "p", "q", "r", "12345"), new Address(3, "x", "y", "z", "12345"));
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(100);
        DataStream<Address> stream = env.addSource(new org.apache.flink.streaming.util.FiniteTestSource(data), TypeInformation.of(Address.class));
        stream.addSink(StreamingFileSink.forBulkFormat(Path.fromLocalFile(folder), ParquetAvroWriters.forSpecificRecord(Address.class)).build());
        env.execute();
        ParquetStreamingFileSinkITCase.validateResults(folder, SpecificData.get(), data);
    }

    @Test
    public void testWriteParquetAvroGeneric() throws Exception {
        final File folder = TEMPORARY_FOLDER.newFolder();
        final Schema schema = Address.getClassSchema();
        final Collection<GenericRecord> data = new ParquetStreamingFileSinkITCase.GenericTestDataCollection();
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(100);
        DataStream<GenericRecord> stream = env.addSource(new org.apache.flink.streaming.util.FiniteTestSource(data), new org.apache.flink.formats.avro.typeutils.GenericRecordAvroTypeInfo(schema));
        stream.addSink(StreamingFileSink.forBulkFormat(Path.fromLocalFile(folder), ParquetAvroWriters.forGenericRecord(schema)).build());
        env.execute();
        List<Address> expected = Arrays.asList(new Address(1, "a", "b", "c", "12345"), new Address(2, "x", "y", "z", "98765"));
        ParquetStreamingFileSinkITCase.validateResults(folder, SpecificData.get(), expected);
    }

    @Test
    public void testWriteParquetAvroReflect() throws Exception {
        final File folder = TEMPORARY_FOLDER.newFolder();
        final List<ParquetStreamingFileSinkITCase.Datum> data = Arrays.asList(new ParquetStreamingFileSinkITCase.Datum("a", 1), new ParquetStreamingFileSinkITCase.Datum("b", 2), new ParquetStreamingFileSinkITCase.Datum("c", 3));
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(100);
        DataStream<ParquetStreamingFileSinkITCase.Datum> stream = env.addSource(new org.apache.flink.streaming.util.FiniteTestSource(data), TypeInformation.of(ParquetStreamingFileSinkITCase.Datum.class));
        stream.addSink(StreamingFileSink.forBulkFormat(Path.fromLocalFile(folder), ParquetAvroWriters.forReflectRecord(ParquetStreamingFileSinkITCase.Datum.class)).build());
        env.execute();
        ParquetStreamingFileSinkITCase.validateResults(folder, ReflectData.get(), data);
    }

    private static class GenericTestDataCollection extends AbstractCollection<GenericRecord> implements Serializable {
        @Override
        public Iterator<GenericRecord> iterator() {
            final GenericRecord rec1 = new org.apache.avro.generic.GenericData.Record(Address.getClassSchema());
            rec1.put(0, 1);
            rec1.put(1, "a");
            rec1.put(2, "b");
            rec1.put(3, "c");
            rec1.put(4, "12345");
            final GenericRecord rec2 = new org.apache.avro.generic.GenericData.Record(Address.getClassSchema());
            rec2.put(0, 2);
            rec2.put(1, "x");
            rec2.put(2, "y");
            rec2.put(3, "z");
            rec2.put(4, "98765");
            return Arrays.asList(rec1, rec2).iterator();
        }

        @Override
        public int size() {
            return 2;
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Test datum.
     */
    public static class Datum implements Serializable {
        public String a;

        public int b;

        public Datum() {
        }

        public Datum(String a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ParquetStreamingFileSinkITCase.Datum datum = ((ParquetStreamingFileSinkITCase.Datum) (o));
            return ((b) == (datum.b)) && ((a) != null ? a.equals(datum.a) : (datum.a) == null);
        }

        @Override
        public int hashCode() {
            int result = ((a) != null) ? a.hashCode() : 0;
            result = (31 * result) + (b);
            return result;
        }
    }
}

