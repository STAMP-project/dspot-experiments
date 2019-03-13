/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.data.input;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.druid.data.input.avro.AvroParseSpec;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.data.input.schemarepo.Avro1124RESTRepositoryClientWrapper;
import org.apache.druid.data.input.schemarepo.Avro1124SubjectAndIdConverter;
import org.apache.druid.java.util.common.parsers.JSONPathFieldType;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.junit.Assert;
import org.junit.Test;
import org.schemarepo.InMemoryRepository;
import org.schemarepo.Repository;
import org.schemarepo.SchemaValidationException;
import org.schemarepo.api.TypedSchemaRepository;
import org.schemarepo.api.converter.AvroSchemaConverter;
import org.schemarepo.api.converter.IdentityConverter;
import org.schemarepo.api.converter.IntegerConverter;


public class AvroStreamInputRowParserTest {
    public static final String EVENT_TYPE = "eventType";

    public static final String ID = "id";

    public static final String SOME_OTHER_ID = "someOtherId";

    public static final String IS_VALID = "isValid";

    public static final String TOPIC = "aTopic";

    public static final String EVENT_TYPE_VALUE = "type-a";

    public static final long ID_VALUE = 1976491L;

    public static final long SOME_OTHER_ID_VALUE = 6568719896L;

    public static final float SOME_FLOAT_VALUE = 0.23555F;

    public static final int SOME_INT_VALUE = 1;

    public static final long SOME_LONG_VALUE = 679865987569912369L;

    public static final DateTime DATE_TIME = new DateTime(2015, 10, 25, 19, 30, ISOChronology.getInstanceUTC());

    public static final List<String> DIMENSIONS = Arrays.asList(AvroStreamInputRowParserTest.EVENT_TYPE, AvroStreamInputRowParserTest.ID, AvroStreamInputRowParserTest.SOME_OTHER_ID, AvroStreamInputRowParserTest.IS_VALID);

    public static final List<String> DIMENSIONS_SCHEMALESS = Arrays.asList("nested", AvroStreamInputRowParserTest.SOME_OTHER_ID, "someStringArray", "someIntArray", "someFloat", "someUnion", AvroStreamInputRowParserTest.EVENT_TYPE, AvroStreamInputRowParserTest.ID, "someBytes", "someLong", "someInt", "timestamp");

    public static final AvroParseSpec PARSE_SPEC = new AvroParseSpec(new TimestampSpec("nested", "millis", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(AvroStreamInputRowParserTest.DIMENSIONS), Collections.emptyList(), null), new org.apache.druid.java.util.common.parsers.JSONPathSpec(true, ImmutableList.of(new org.apache.druid.java.util.common.parsers.JSONPathFieldSpec(JSONPathFieldType.PATH, "nested", "someRecord.subLong"))));

    public static final AvroParseSpec PARSE_SPEC_SCHEMALESS = new AvroParseSpec(new TimestampSpec("nested", "millis", null), new DimensionsSpec(null, null, null), new org.apache.druid.java.util.common.parsers.JSONPathSpec(true, ImmutableList.of(new org.apache.druid.java.util.common.parsers.JSONPathFieldSpec(JSONPathFieldType.PATH, "nested", "someRecord.subLong"))));

    public static final MyFixed SOME_FIXED_VALUE = new MyFixed(ByteBuffer.allocate(16).array());

    private static final long SUB_LONG_VALUE = 1543698L;

    private static final int SUB_INT_VALUE = 4892;

    public static final MySubRecord SOME_RECORD_VALUE = MySubRecord.newBuilder().setSubInt(AvroStreamInputRowParserTest.SUB_INT_VALUE).setSubLong(AvroStreamInputRowParserTest.SUB_LONG_VALUE).build();

    public static final List<CharSequence> SOME_STRING_ARRAY_VALUE = Arrays.asList(((CharSequence) ("8")), "4", "2", "1");

    public static final List<Integer> SOME_INT_ARRAY_VALUE = Arrays.asList(1, 2, 4, 8);

    public static final Map<CharSequence, Integer> SOME_INT_VALUE_MAP_VALUE = Maps.asMap(new HashSet<CharSequence>(Arrays.asList("8", "2", "4", "1")), new Function<CharSequence, Integer>() {
        @Nullable
        @Override
        public Integer apply(@Nullable
        CharSequence input) {
            return Integer.parseInt(input.toString());
        }
    });

    public static final Map<CharSequence, CharSequence> SOME_STRING_VALUE_MAP_VALUE = Maps.asMap(new HashSet<CharSequence>(Arrays.asList("8", "2", "4", "1")), new Function<CharSequence, CharSequence>() {
        @Nullable
        @Override
        public CharSequence apply(@Nullable
        CharSequence input) {
            return input.toString();
        }
    });

    public static final String SOME_UNION_VALUE = "string as union";

    public static final ByteBuffer SOME_BYTES_VALUE = ByteBuffer.allocate(8);

    private static final Pattern BRACES_AND_SPACE = Pattern.compile("[{} ]");

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    public void testSerde() throws IOException {
        Repository repository = new Avro1124RESTRepositoryClientWrapper("http://github.io");
        AvroStreamInputRowParser parser = new AvroStreamInputRowParser(AvroStreamInputRowParserTest.PARSE_SPEC, new org.apache.druid.data.input.avro.SchemaRepoBasedAvroBytesDecoder<String, Integer>(new Avro1124SubjectAndIdConverter(AvroStreamInputRowParserTest.TOPIC), repository));
        ByteBufferInputRowParser parser2 = jsonMapper.readValue(jsonMapper.writeValueAsString(parser), ByteBufferInputRowParser.class);
        Assert.assertEquals(parser, parser2);
    }

    @Test
    public void testParse() throws IOException, SchemaValidationException {
        // serde test
        Repository repository = new InMemoryRepository(null);
        AvroStreamInputRowParser parser = new AvroStreamInputRowParser(AvroStreamInputRowParserTest.PARSE_SPEC, new org.apache.druid.data.input.avro.SchemaRepoBasedAvroBytesDecoder<String, Integer>(new Avro1124SubjectAndIdConverter(AvroStreamInputRowParserTest.TOPIC), repository));
        ByteBufferInputRowParser parser2 = jsonMapper.readValue(jsonMapper.writeValueAsString(parser), ByteBufferInputRowParser.class);
        repository = getSchemaRepository();
        // prepare data
        GenericRecord someAvroDatum = AvroStreamInputRowParserTest.buildSomeAvroDatum();
        // encode schema id
        Avro1124SubjectAndIdConverter converter = new Avro1124SubjectAndIdConverter(AvroStreamInputRowParserTest.TOPIC);
        TypedSchemaRepository<Integer, Schema, String> repositoryClient = new TypedSchemaRepository<Integer, Schema, String>(repository, new IntegerConverter(), new AvroSchemaConverter(), new IdentityConverter());
        Integer id = repositoryClient.registerSchema(AvroStreamInputRowParserTest.TOPIC, SomeAvroDatum.getClassSchema());
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        converter.putSubjectAndId(id, byteBuffer);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(byteBuffer.array());
        // encode data
        DatumWriter<GenericRecord> writer = new org.apache.avro.specific.SpecificDatumWriter(someAvroDatum.getSchema());
        // write avro datum to bytes
        writer.write(someAvroDatum, EncoderFactory.get().directBinaryEncoder(out, null));
        InputRow inputRow = parser2.parseBatch(ByteBuffer.wrap(out.toByteArray())).get(0);
        AvroStreamInputRowParserTest.assertInputRowCorrect(inputRow, AvroStreamInputRowParserTest.DIMENSIONS, false);
    }

    @Test
    public void testParseSchemaless() throws IOException, SchemaValidationException {
        // serde test
        Repository repository = new InMemoryRepository(null);
        AvroStreamInputRowParser parser = new AvroStreamInputRowParser(AvroStreamInputRowParserTest.PARSE_SPEC_SCHEMALESS, new org.apache.druid.data.input.avro.SchemaRepoBasedAvroBytesDecoder<String, Integer>(new Avro1124SubjectAndIdConverter(AvroStreamInputRowParserTest.TOPIC), repository));
        ByteBufferInputRowParser parser2 = jsonMapper.readValue(jsonMapper.writeValueAsString(parser), ByteBufferInputRowParser.class);
        repository = getSchemaRepository();
        // prepare data
        GenericRecord someAvroDatum = AvroStreamInputRowParserTest.buildSomeAvroDatum();
        // encode schema id
        Avro1124SubjectAndIdConverter converter = new Avro1124SubjectAndIdConverter(AvroStreamInputRowParserTest.TOPIC);
        TypedSchemaRepository<Integer, Schema, String> repositoryClient = new TypedSchemaRepository<Integer, Schema, String>(repository, new IntegerConverter(), new AvroSchemaConverter(), new IdentityConverter());
        Integer id = repositoryClient.registerSchema(AvroStreamInputRowParserTest.TOPIC, SomeAvroDatum.getClassSchema());
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        converter.putSubjectAndId(id, byteBuffer);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(byteBuffer.array());
        // encode data
        DatumWriter<GenericRecord> writer = new org.apache.avro.specific.SpecificDatumWriter(someAvroDatum.getSchema());
        // write avro datum to bytes
        writer.write(someAvroDatum, EncoderFactory.get().directBinaryEncoder(out, null));
        InputRow inputRow = parser2.parseBatch(ByteBuffer.wrap(out.toByteArray())).get(0);
        AvroStreamInputRowParserTest.assertInputRowCorrect(inputRow, AvroStreamInputRowParserTest.DIMENSIONS_SCHEMALESS, false);
    }
}

