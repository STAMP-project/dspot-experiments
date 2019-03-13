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
package org.apache.flink.formats.avro;


import AvroKryoSerializerUtils.AvroSchemaSerializer;
import GenericData.Record;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.FileReader;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.util.Utf8;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.GenericTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.api.java.typeutils.runtime.kryo.Serializers;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.core.memory.DataOutputViewStreamWrapper;
import org.apache.flink.formats.avro.generated.Colors;
import org.apache.flink.formats.avro.generated.User;
import org.apache.flink.formats.avro.typeutils.AvroTypeInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the avro input format.
 * (The testcase is mostly the getting started tutorial of avro)
 * http://avro.apache.org/docs/current/gettingstartedjava.html
 */
public class AvroRecordInputFormatTest {
    public File testFile;

    static final String TEST_NAME = "Alyssa";

    static final String TEST_ARRAY_STRING_1 = "ELEMENT 1";

    static final String TEST_ARRAY_STRING_2 = "ELEMENT 2";

    static final boolean TEST_ARRAY_BOOLEAN_1 = true;

    static final boolean TEST_ARRAY_BOOLEAN_2 = false;

    static final Colors TEST_ENUM_COLOR = Colors.GREEN;

    static final String TEST_MAP_KEY1 = "KEY 1";

    static final long TEST_MAP_VALUE1 = 8546456L;

    static final String TEST_MAP_KEY2 = "KEY 2";

    static final long TEST_MAP_VALUE2 = 17554L;

    static final int TEST_NUM = 239;

    static final String TEST_STREET = "Baker Street";

    static final String TEST_CITY = "London";

    static final String TEST_STATE = "London";

    static final String TEST_ZIP = "NW1 6XE";

    private Schema userSchema = new User().getSchema();

    /**
     * Test if the AvroInputFormat is able to properly read data from an Avro file.
     */
    @Test
    public void testDeserialization() throws IOException {
        Configuration parameters = new Configuration();
        AvroInputFormat<User> format = new AvroInputFormat(new Path(testFile.getAbsolutePath()), User.class);
        format.configure(parameters);
        FileInputSplit[] splits = format.createInputSplits(1);
        Assert.assertEquals(splits.length, 1);
        format.open(splits[0]);
        User u = format.nextRecord(null);
        Assert.assertNotNull(u);
        String name = u.getName().toString();
        Assert.assertNotNull("empty record", name);
        Assert.assertEquals("name not equal", AvroRecordInputFormatTest.TEST_NAME, name);
        // check arrays
        List<CharSequence> sl = u.getTypeArrayString();
        Assert.assertEquals("element 0 not equal", AvroRecordInputFormatTest.TEST_ARRAY_STRING_1, sl.get(0).toString());
        Assert.assertEquals("element 1 not equal", AvroRecordInputFormatTest.TEST_ARRAY_STRING_2, sl.get(1).toString());
        List<Boolean> bl = u.getTypeArrayBoolean();
        Assert.assertEquals("element 0 not equal", AvroRecordInputFormatTest.TEST_ARRAY_BOOLEAN_1, bl.get(0));
        Assert.assertEquals("element 1 not equal", AvroRecordInputFormatTest.TEST_ARRAY_BOOLEAN_2, bl.get(1));
        // check enums
        Colors enumValue = u.getTypeEnum();
        Assert.assertEquals("enum not equal", AvroRecordInputFormatTest.TEST_ENUM_COLOR, enumValue);
        // check maps
        Map<CharSequence, Long> lm = u.getTypeMap();
        Assert.assertEquals("map value of key 1 not equal", AvroRecordInputFormatTest.TEST_MAP_VALUE1, lm.get(new Utf8(AvroRecordInputFormatTest.TEST_MAP_KEY1)).longValue());
        Assert.assertEquals("map value of key 2 not equal", AvroRecordInputFormatTest.TEST_MAP_VALUE2, lm.get(new Utf8(AvroRecordInputFormatTest.TEST_MAP_KEY2)).longValue());
        Assert.assertFalse("expecting second element", format.reachedEnd());
        Assert.assertNotNull("expecting second element", format.nextRecord(u));
        Assert.assertNull(format.nextRecord(u));
        Assert.assertTrue(format.reachedEnd());
        format.close();
    }

    /**
     * Test if the AvroInputFormat is able to properly read data from an Avro file.
     */
    @Test
    public void testDeserializationReuseAvroRecordFalse() throws IOException {
        Configuration parameters = new Configuration();
        AvroInputFormat<User> format = new AvroInputFormat(new Path(testFile.getAbsolutePath()), User.class);
        format.setReuseAvroValue(false);
        format.configure(parameters);
        FileInputSplit[] splits = format.createInputSplits(1);
        Assert.assertEquals(splits.length, 1);
        format.open(splits[0]);
        User u = format.nextRecord(null);
        Assert.assertNotNull(u);
        String name = u.getName().toString();
        Assert.assertNotNull("empty record", name);
        Assert.assertEquals("name not equal", AvroRecordInputFormatTest.TEST_NAME, name);
        // check arrays
        List<CharSequence> sl = u.getTypeArrayString();
        Assert.assertEquals("element 0 not equal", AvroRecordInputFormatTest.TEST_ARRAY_STRING_1, sl.get(0).toString());
        Assert.assertEquals("element 1 not equal", AvroRecordInputFormatTest.TEST_ARRAY_STRING_2, sl.get(1).toString());
        List<Boolean> bl = u.getTypeArrayBoolean();
        Assert.assertEquals("element 0 not equal", AvroRecordInputFormatTest.TEST_ARRAY_BOOLEAN_1, bl.get(0));
        Assert.assertEquals("element 1 not equal", AvroRecordInputFormatTest.TEST_ARRAY_BOOLEAN_2, bl.get(1));
        // check enums
        Colors enumValue = u.getTypeEnum();
        Assert.assertEquals("enum not equal", AvroRecordInputFormatTest.TEST_ENUM_COLOR, enumValue);
        // check maps
        Map<CharSequence, Long> lm = u.getTypeMap();
        Assert.assertEquals("map value of key 1 not equal", AvroRecordInputFormatTest.TEST_MAP_VALUE1, lm.get(new Utf8(AvroRecordInputFormatTest.TEST_MAP_KEY1)).longValue());
        Assert.assertEquals("map value of key 2 not equal", AvroRecordInputFormatTest.TEST_MAP_VALUE2, lm.get(new Utf8(AvroRecordInputFormatTest.TEST_MAP_KEY2)).longValue());
        Assert.assertFalse("expecting second element", format.reachedEnd());
        Assert.assertNotNull("expecting second element", format.nextRecord(u));
        Assert.assertNull(format.nextRecord(u));
        Assert.assertTrue(format.reachedEnd());
        format.close();
    }

    /**
     * Test if the Flink serialization is able to properly process GenericData.Record types.
     * Usually users of Avro generate classes (POJOs) from Avro schemas.
     * However, if generated classes are not available, one can also use GenericData.Record.
     * It is an untyped key-value record which is using a schema to validate the correctness of the data.
     *
     * <p>It is not recommended to use GenericData.Record with Flink. Use generated POJOs instead.
     */
    @Test
    public void testDeserializeToGenericType() throws IOException {
        DatumReader<GenericData.Record> datumReader = new org.apache.avro.generic.GenericDatumReader(userSchema);
        try (FileReader<GenericData.Record> dataFileReader = DataFileReader.openReader(testFile, datumReader)) {
            // initialize Record by reading it from disk (that's easier than creating it by hand)
            GenericData.Record rec = new GenericData.Record(userSchema);
            dataFileReader.next(rec);
            // check if record has been read correctly
            Assert.assertNotNull(rec);
            Assert.assertEquals("name not equal", AvroRecordInputFormatTest.TEST_NAME, rec.get("name").toString());
            Assert.assertEquals("enum not equal", AvroRecordInputFormatTest.TEST_ENUM_COLOR.toString(), rec.get("type_enum").toString());
            Assert.assertEquals(null, rec.get("type_long_test"));// it is null for the first record.

            // now serialize it with our framework:
            TypeInformation<GenericData.Record> te = TypeExtractor.createTypeInfo(Record.class);
            ExecutionConfig ec = new ExecutionConfig();
            Assert.assertEquals(GenericTypeInfo.class, te.getClass());
            Serializers.recursivelyRegisterType(te.getTypeClass(), ec, new HashSet());
            TypeSerializer<GenericData.Record> tser = te.createSerializer(ec);
            Assert.assertEquals(1, ec.getDefaultKryoSerializerClasses().size());
            Assert.assertTrue(((ec.getDefaultKryoSerializerClasses().containsKey(Schema.class)) && (ec.getDefaultKryoSerializerClasses().get(Schema.class).equals(AvroSchemaSerializer.class))));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (DataOutputViewStreamWrapper outView = new DataOutputViewStreamWrapper(out)) {
                tser.serialize(rec, outView);
            }
            GenericData.Record newRec;
            try (DataInputViewStreamWrapper inView = new DataInputViewStreamWrapper(new ByteArrayInputStream(out.toByteArray()))) {
                newRec = tser.deserialize(inView);
            }
            // check if it is still the same
            Assert.assertNotNull(newRec);
            Assert.assertEquals("enum not equal", AvroRecordInputFormatTest.TEST_ENUM_COLOR.toString(), newRec.get("type_enum").toString());
            Assert.assertEquals("name not equal", AvroRecordInputFormatTest.TEST_NAME, newRec.get("name").toString());
            Assert.assertEquals(null, newRec.get("type_long_test"));
        }
    }

    /**
     * This test validates proper serialization with specific (generated POJO) types.
     */
    @Test
    public void testDeserializeToSpecificType() throws IOException {
        DatumReader<User> datumReader = new org.apache.avro.specific.SpecificDatumReader(userSchema);
        try (FileReader<User> dataFileReader = DataFileReader.openReader(testFile, datumReader)) {
            User rec = dataFileReader.next();
            // check if record has been read correctly
            Assert.assertNotNull(rec);
            Assert.assertEquals("name not equal", AvroRecordInputFormatTest.TEST_NAME, rec.get("name").toString());
            Assert.assertEquals("enum not equal", AvroRecordInputFormatTest.TEST_ENUM_COLOR.toString(), rec.get("type_enum").toString());
            // now serialize it with our framework:
            ExecutionConfig ec = new ExecutionConfig();
            TypeInformation<User> te = TypeExtractor.createTypeInfo(User.class);
            Assert.assertEquals(AvroTypeInfo.class, te.getClass());
            TypeSerializer<User> tser = te.createSerializer(ec);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (DataOutputViewStreamWrapper outView = new DataOutputViewStreamWrapper(out)) {
                tser.serialize(rec, outView);
            }
            User newRec;
            try (DataInputViewStreamWrapper inView = new DataInputViewStreamWrapper(new ByteArrayInputStream(out.toByteArray()))) {
                newRec = tser.deserialize(inView);
            }
            // check if it is still the same
            Assert.assertNotNull(newRec);
            Assert.assertEquals("name not equal", AvroRecordInputFormatTest.TEST_NAME, newRec.getName().toString());
            Assert.assertEquals("enum not equal", AvroRecordInputFormatTest.TEST_ENUM_COLOR.toString(), newRec.getTypeEnum().toString());
        }
    }

    /**
     * Test if the AvroInputFormat is able to properly read data from an Avro
     * file as a GenericRecord.
     */
    @Test
    public void testDeserializationGenericRecord() throws IOException {
        Configuration parameters = new Configuration();
        AvroInputFormat<GenericRecord> format = new AvroInputFormat(new Path(testFile.getAbsolutePath()), GenericRecord.class);
        doTestDeserializationGenericRecord(format, parameters);
    }

    /**
     * Test if the AvroInputFormat is able to properly read data from an avro
     * file as a GenericRecord.
     *
     * @throws IOException
     * 		if there is an error
     */
    @Test
    public void testDeserializationGenericRecordReuseAvroValueFalse() throws IOException {
        Configuration parameters = new Configuration();
        AvroInputFormat<GenericRecord> format = new AvroInputFormat(new Path(testFile.getAbsolutePath()), GenericRecord.class);
        format.configure(parameters);
        format.setReuseAvroValue(false);
        doTestDeserializationGenericRecord(format, parameters);
    }
}

