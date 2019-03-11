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
package org.apache.flink.formats.avro.typeutils;


import Types.INT;
import Types.STRING;
import java.io.File;
import java.util.Arrays;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.avro.AvroInputFormat;
import org.apache.flink.formats.avro.generated.User;
import org.apache.flink.test.util.MultipleProgramsTestBase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for the {@link AvroInputFormat} reading Pojos.
 */
@RunWith(Parameterized.class)
public class AvroTypeExtractionTest extends MultipleProgramsTestBase {
    public AvroTypeExtractionTest(TestExecutionMode mode) {
        super(mode);
    }

    private File inFile;

    private String resultPath;

    private String expected;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testSimpleAvroRead() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        Path in = new Path(inFile.getAbsoluteFile().toURI());
        AvroInputFormat<User> users = new AvroInputFormat(in, User.class);
        DataSet<User> usersDS = env.createInput(users).map(( value) -> value);
        usersDS.writeAsText(resultPath);
        env.execute("Simple Avro read job");
        expected = "{\"name\": \"Alyssa\", \"favorite_number\": 256, \"favorite_color\": null, \"type_long_test\": null, " + ((((((((((("\"type_double_test\": 123.45, \"type_null_test\": null, \"type_bool_test\": true, \"type_array_string\": [\"ELEMENT 1\", \"ELEMENT 2\"], " + "\"type_array_boolean\": [true, false], \"type_nullable_array\": null, \"type_enum\": \"GREEN\", \"type_map\": {\"KEY 2\": 17554, \"KEY 1\": 8546456}, ") + "\"type_fixed\": null, \"type_union\": null, \"type_nested\": {\"num\": 239, \"street\": \"Baker Street\", \"city\": \"London\", \"state\": \"London\", \"zip\": \"NW1 6XE\"}, ") + "\"type_bytes\": {\"bytes\": \"\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\"}, \"type_date\": 2014-03-01, \"type_time_millis\": 12:12:12.000, ") + "\"type_time_micros\": 123456, \"type_timestamp_millis\": 2014-03-01T12:12:12.321Z, \"type_timestamp_micros\": 123456, ") + "\"type_decimal_bytes\": {\"bytes\": \"\\u0007\u00d0\"}, \"type_decimal_fixed\": [7, -48]}\n") + "{\"name\": \"Charlie\", \"favorite_number\": null, \"favorite_color\": \"blue\", \"type_long_test\": 1337, \"type_double_test\": 1.337, ") + "\"type_null_test\": null, \"type_bool_test\": false, \"type_array_string\": [], \"type_array_boolean\": [], \"type_nullable_array\": null, ") + "\"type_enum\": \"RED\", \"type_map\": {}, \"type_fixed\": null, \"type_union\": null, \"type_nested\": {\"num\": 239, \"street\": \"Baker Street\", ") + "\"city\": \"London\", \"state\": \"London\", \"zip\": \"NW1 6XE\"}, \"type_bytes\": {\"bytes\": \"\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\"}, ") + "\"type_date\": 2014-03-01, \"type_time_millis\": 12:12:12.000, \"type_time_micros\": 123456, \"type_timestamp_millis\": 2014-03-01T12:12:12.321Z, ") + "\"type_timestamp_micros\": 123456, \"type_decimal_bytes\": {\"bytes\": \"\\u0007\u00d0\"}, \"type_decimal_fixed\": [7, -48]}\n");
    }

    @Test
    public void testSerializeWithAvro() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().enableForceAvro();
        Path in = new Path(inFile.getAbsoluteFile().toURI());
        AvroInputFormat<User> users = new AvroInputFormat(in, User.class);
        DataSet<User> usersDS = env.createInput(users).map(((MapFunction<User, User>) (( value) -> {
            Map<CharSequence, Long> ab = new HashMap<>(1);
            ab.put("hehe", 12L);
            value.setTypeMap(ab);
            return value;
        })));
        usersDS.writeAsText(resultPath);
        env.execute("Simple Avro read job");
        expected = "{\"name\": \"Alyssa\", \"favorite_number\": 256, \"favorite_color\": null, \"type_long_test\": null, \"type_double_test\": 123.45, \"type_null_test\": null, " + (((((((((("\"type_bool_test\": true, \"type_array_string\": [\"ELEMENT 1\", \"ELEMENT 2\"], \"type_array_boolean\": [true, false], \"type_nullable_array\": null, " + "\"type_enum\": \"GREEN\", \"type_map\": {\"hehe\": 12}, \"type_fixed\": null, \"type_union\": null, \"type_nested\": {\"num\": 239, \"street\": \"Baker Street\", ") + "\"city\": \"London\", \"state\": \"London\", \"zip\": \"NW1 6XE\"}, \"type_bytes\": {\"bytes\": \"\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\"}, ") + "\"type_date\": 2014-03-01, \"type_time_millis\": 12:12:12.000, \"type_time_micros\": 123456, \"type_timestamp_millis\": 2014-03-01T12:12:12.321Z, \"type_timestamp_micros\": 123456, ") + "\"type_decimal_bytes\": {\"bytes\": \"\\u0007\u00d0\"}, \"type_decimal_fixed\": [7, -48]}\n") + "{\"name\": \"Charlie\", \"favorite_number\": null, \"favorite_color\": \"blue\", \"type_long_test\": 1337, \"type_double_test\": 1.337, \"type_null_test\": null, ") + "\"type_bool_test\": false, \"type_array_string\": [], \"type_array_boolean\": [], \"type_nullable_array\": null, \"type_enum\": \"RED\", \"type_map\": {\"hehe\": 12}, ") + "\"type_fixed\": null, \"type_union\": null, \"type_nested\": {\"num\": 239, \"street\": \"Baker Street\", \"city\": \"London\", \"state\": \"London\", \"zip\": \"NW1 6XE\"}, ") + "\"type_bytes\": {\"bytes\": \"\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\"}, \"type_date\": 2014-03-01, \"type_time_millis\": 12:12:12.000, ") + "\"type_time_micros\": 123456, \"type_timestamp_millis\": 2014-03-01T12:12:12.321Z, \"type_timestamp_micros\": 123456, \"type_decimal_bytes\": {\"bytes\": \"\\u0007\u00d0\"}, ") + "\"type_decimal_fixed\": [7, -48]}\n");
    }

    @Test
    public void testKeySelection() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().enableObjectReuse();
        Path in = new Path(inFile.getAbsoluteFile().toURI());
        AvroInputFormat<User> users = new AvroInputFormat(in, User.class);
        DataSet<User> usersDS = env.createInput(users);
        DataSet<Tuple2<String, Integer>> res = usersDS.groupBy("name").reduceGroup(((GroupReduceFunction<User, Tuple2<String, Integer>>) (( values, out) -> {
            for (User u : values) {
                out.collect(new Tuple2<>(u.getName().toString(), 1));
            }
        }))).returns(Types.TUPLE(STRING, INT));
        res.writeAsText(resultPath);
        env.execute("Avro Key selection");
        expected = "(Alyssa,1)\n(Charlie,1)\n";
    }

    @Test
    public void testWithAvroGenericSer() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().enableForceAvro();
        Path in = new Path(inFile.getAbsoluteFile().toURI());
        AvroInputFormat<User> users = new AvroInputFormat(in, User.class);
        DataSet<User> usersDS = env.createInput(users);
        DataSet<Tuple2<String, Integer>> res = usersDS.groupBy(((KeySelector<User, String>) (( value) -> String.valueOf(value.getName())))).reduceGroup(((GroupReduceFunction<User, Tuple2<String, Integer>>) (( values, out) -> {
            for (User u : values) {
                out.collect(new Tuple2<>(u.getName().toString(), 1));
            }
        }))).returns(Types.TUPLE(STRING, INT));
        res.writeAsText(resultPath);
        env.execute("Avro Key selection");
        expected = "(Charlie,1)\n(Alyssa,1)\n";
    }

    @Test
    public void testWithKryoGenericSer() throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().enableForceKryo();
        Path in = new Path(inFile.getAbsoluteFile().toURI());
        AvroInputFormat<User> users = new AvroInputFormat(in, User.class);
        DataSet<User> usersDS = env.createInput(users);
        DataSet<Tuple2<String, Integer>> res = usersDS.groupBy(((KeySelector<User, String>) (( value) -> String.valueOf(value.getName())))).reduceGroup(((GroupReduceFunction<User, Tuple2<String, Integer>>) (( values, out) -> {
            for (User u : values) {
                out.collect(new Tuple2<>(u.getName().toString(), 1));
            }
        }))).returns(Types.TUPLE(STRING, INT));
        res.writeAsText(resultPath);
        env.execute("Avro Key selection");
        expected = "(Charlie,1)\n(Alyssa,1)\n";
    }

    /**
     * Test some know fields for grouping on.
     */
    @Test
    public void testAllFields() throws Exception {
        for (String fieldName : Arrays.asList("name", "type_enum", "type_double_test")) {
            testField(fieldName);
        }
    }
}

