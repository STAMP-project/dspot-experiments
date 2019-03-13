/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.vector.complex.writer;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.TestBuilder;
import org.junit.Test;


public class TestComplexTypeReader extends BaseTestQuery {
    // Repeated map (map) -> json.
    @Test
    public void testX() throws Exception {
        BaseTestQuery.test("select convert_to(z[0], 'JSON') from cp.`jsoninput/input2.json`;");
    }

    // map -> json.
    @Test
    public void testX2() throws Exception {
        BaseTestQuery.test("select convert_to(x, 'JSON') from cp.`jsoninput/input2.json`;");
    }

    // Map (mapfield) -> json.
    @Test
    public void testX3() throws Exception {
        BaseTestQuery.test("select convert_to(tbl.x.y, 'JSON') from cp.`jsoninput/input2.json` tbl;");
    }

    // float value -> json
    @Test
    public void testX4() throws Exception {
        BaseTestQuery.test("select convert_to(`float`, 'JSON') from cp.`jsoninput/input2.json`;");
    }

    // integer value -> json
    @Test
    public void testX5() throws Exception {
        BaseTestQuery.test("select convert_to(`integer`, 'JSON') from cp.`jsoninput/input2.json`;");
    }

    // repeated map -> json.
    @Test
    public void testX6() throws Exception {
        BaseTestQuery.test("select convert_to(z, 'JSON')  from cp.`jsoninput/input2.json`;");
    }

    // repeated list (Repeated BigInt) -> json
    @Test
    public void testX7() throws Exception {
        BaseTestQuery.test("select convert_to(rl[1], 'JSON') from cp.`jsoninput/input2.json`;");
    }

    // repeated list (Repeated BigInt) -> json
    @Test
    public void testX8() throws Exception {
        BaseTestQuery.test("select convert_to(rl[0][1], 'JSON') from cp.`jsoninput/input2.json`;");
    }

    // repeated list -> json
    @Test
    public void testX9() throws Exception {
        BaseTestQuery.test("select convert_to(rl, 'JSON') from cp.`jsoninput/input2.json`;");
    }

    @Test
    public void testY() throws Exception {
        BaseTestQuery.test("select z[0] from cp.`jsoninput/input2.json`;");
    }

    @Test
    public void testY2() throws Exception {
        BaseTestQuery.test("select x from cp.`jsoninput/input2.json`;");
    }

    @Test
    public void testY3() throws Exception {
        BaseTestQuery.test("select tbl.x.y from cp.`jsoninput/input2.json` tbl;");
    }

    @Test
    public void testY6() throws Exception {
        BaseTestQuery.test("select z  from cp.`jsoninput/input2.json`;");
    }

    // repeated list (Repeated BigInt)
    @Test
    public void testZ() throws Exception {
        BaseTestQuery.test("select rl[1] from cp.`jsoninput/input2.json`;");
    }

    // repeated list (Repeated BigInt ( BigInt) ) )
    @Test
    public void testZ1() throws Exception {
        BaseTestQuery.test("select rl[0][1] from cp.`jsoninput/input2.json`;");
    }

    // repeated list (Repeated BigInt ( BigInt) ) ). The first index is out of boundary
    @Test
    public void testZ2() throws Exception {
        BaseTestQuery.test("select rl[1000][1] from cp.`jsoninput/input2.json`;");
    }

    // repeated list (Repeated BigInt ( BigInt) ) ). The second index is out of boundary
    @Test
    public void testZ3() throws Exception {
        BaseTestQuery.test("select rl[0][1000] from cp.`jsoninput/input2.json`;");
    }

    // repeated list. The repeated list is asked for twice, hence requires copying (evaluation in ProjectRecordBatch)
    @Test
    public void testZ4() throws Exception {
        BaseTestQuery.test("select rl, rl from cp.`jsoninput/input2.json`;");
    }

    // repeated map  --> Json.  It will go beyond the buffer of size 256 allocated in setup.
    @Test
    public void testA0() throws Exception {
        BaseTestQuery.test("  select convert_to(types, 'JSON') from cp.`jsoninput/vvtypes.json`;");
    }

    // repeated map (map) --> Json.
    @Test
    public void testA1() throws Exception {
        BaseTestQuery.test("  select convert_to(types[1], 'JSON') from cp.`jsoninput/vvtypes.json`;");
    }

    // repeated map (map (repeated map) ) --> Json.
    @Test
    public void testA2() throws Exception {
        BaseTestQuery.test("  select convert_to(types[1].minor, 'JSON') from cp.`jsoninput/vvtypes.json`;");
    }

    // repeated map (map( repeated map (map (varchar)))) --> Json.
    @Test
    public void testA3() throws Exception {
        BaseTestQuery.test("  select convert_to(types[1].minor[0].valueHolder, 'JSON') from cp.`jsoninput/vvtypes.json`;");
    }

    // Two complex type functions in SELECT clause : repeated map (map) --> Json,
    @Test
    public void testA4() throws Exception {
        BaseTestQuery.test("  select convert_to(types[1], 'JSON'), convert_to(modes[2], 'JSON') from cp.`jsoninput/vvtypes.json`;");
    }

    // repeated map (map) .
    @Test
    public void testB1() throws Exception {
        BaseTestQuery.test("  select types[1] from cp.`jsoninput/vvtypes.json`;");
    }

    // repeated map (map (repeated map) ).
    @Test
    public void testB2() throws Exception {
        BaseTestQuery.test("  select types[1].minor from cp.`jsoninput/vvtypes.json`;");
    }

    // repeated map (map( repeated map (map (varchar)))).
    @Test
    public void testB3() throws Exception {
        BaseTestQuery.test("  select types[1].minor[0].valueholder from cp.`jsoninput/vvtypes.json`;");
    }

    // DRILL-1250
    // repeated scalar values evaluation.
    @Test
    public void test_repeatedList() throws Exception {
        BaseTestQuery.test("select l, l from cp.`jsoninput/input2.json`;");
    }

    @Test
    public void testKeyValueGen() throws Exception {
        BaseTestQuery.test("select kvgen(x) from cp.`jsoninput/input2.json`");
        BaseTestQuery.test("select kvgen(bigintegercol), kvgen(float8col) from cp.`jsoninput/input3.json`");
    }

    // Functions tests kvgen functionality where the 'value' part of the map is complex
    @Test
    public void testKVGenWithComplexValues() throws Exception {
        // test where 'value' is a list of integers
        BaseTestQuery.test("select kvgen(a) from cp.`jsoninput/kvgen_complex_input.json`");
        // test where 'value' is a repeated list of floats
        BaseTestQuery.test("select kvgen(c) from cp.`jsoninput/kvgen_complex_input.json`");
        // test where 'value' is a map
        BaseTestQuery.test("select kvgen(e) from cp.`jsoninput/kvgen_complex_input.json`");
        // test where 'value' is a repeated list of maps
        BaseTestQuery.test("select kvgen(i) from cp.`jsoninput/kvgen_complex_input.json`");
        // test where 'value' is a map that contains a list
        BaseTestQuery.test("select kvgen(m) from cp.`jsoninput/kvgen_complex_input.json`");
        // test where 'value' is a map that contains a map
        BaseTestQuery.test("select kvgen(p) from cp.`jsoninput/kvgen_complex_input.json`");
    }

    // Test SplitUpComplexExpressions rule which splits complex expression into multiple projects
    @Test
    public void testComplexAndSimpleColumnSelection() throws Exception {
        BaseTestQuery.test("select t.a.b, kvgen(t.a.c) from cp.`jsoninput/input4.json` t");
    }

    @Test
    public void testKVGenWithNullableInput() throws Exception {
        // Contents of the generated file:
        /* {"foo": {"obj":1, "bar":10}}
        {"foo": {"obj":2, "bar":20}}
        {"foo": null}
        {"foo": {"obj": null, "bar": 30}}
         */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ExecTest.dirTestWatcher.getRootDir(), "input_nested.json")))) {
            String[] fieldValue = new String[]{ "{\"obj\":1, \"bar\":10}", "{\"obj\":2, \"bar\":20}", null, "{\"obj\": null, \"bar\": 30}" };
            for (String value : fieldValue) {
                String entry = String.format("{\"foo\": %s}\n", value);
                writer.write(entry);
            }
        }
        BaseTestQuery.testBuilder().sqlQuery("select kvgen(foo) kv from dfs.`input_nested.json`").unOrdered().baselineColumns("kv").baselineValues(TestBuilder.listOf(TestBuilder.mapOf("key", "obj", "value", 1L), TestBuilder.mapOf("key", "bar", "value", 10L))).baselineValues(TestBuilder.listOf(TestBuilder.mapOf("key", "obj", "value", 2L), TestBuilder.mapOf("key", "bar", "value", 20L))).baselineValues(TestBuilder.listOf()).baselineValues(TestBuilder.listOf(TestBuilder.mapOf("key", "bar", "value", 30L))).go();
    }

    // DRILL-2872.
    @Test
    public void testRepeatedJson() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select cast(convert_to(interests, 'JSON') as varchar(0)) as interests from cp.`complex_student.json`").unOrdered().jsonBaselineFile("DRILL-2872-result.json").go();
    }

    // DRILL-4410
    // ListVector allocation
    @Test
    public void test_array() throws Exception {
        long numRecords = 100000;
        final String file1 = "arrays1.json";
        final String file2 = "arrays2.json";
        final Path path1 = ExecTest.dirTestWatcher.getRootDir().toPath().resolve(file1);
        final Path path2 = ExecTest.dirTestWatcher.getRootDir().toPath().resolve(file2);
        Files.createFile(path1);
        Files.createFile(path2);
        final String arrayString = "[ \"abcdef\", \"ghijkl\", \"mnopqr\", \"stuvwx\", \"yz1234\", \"567890\" ] ";
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path1.toFile(), true)))) {
            for (long i = 0; i < numRecords; i++) {
                out.println((((("{ \"id\" : " + i) + ", \"array\" : ") + arrayString) + "}"));
            }
        } catch (IOException e) {
            throw e;
        }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path2.toFile(), true)))) {
            for (long i = 0; i < numRecords; i++) {
                out.println((((("{ \"id\" : " + i) + ", \"array\" : ") + arrayString) + "}"));
            }
        } catch (IOException e) {
            throw e;
        }
        TestBuilder testBuilder = BaseTestQuery.testBuilder().sqlQuery(("select * from dfs.`%s` `arrays1` INNER JOIN dfs.`%s` `arrays2` ON " + "(`arrays1`.id = `arrays2`.id)"), file1, file2).unOrdered().baselineColumns("id", "id0", "array", "array0");
        for (long i = 0; i < numRecords; i++) {
            testBuilder.baselineValues(i, i, TestBuilder.listOf("abcdef", "ghijkl", "mnopqr", "stuvwx", "yz1234", "567890"), TestBuilder.listOf("abcdef", "ghijkl", "mnopqr", "stuvwx", "yz1234", "567890"));
        }
        testBuilder.go();
    }

    @Test
    public void testNonExistentFieldConverting() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery(("select employee_id, convert_to(`complex_field`, 'JSON') as complex_field from cp.`employee.json` " + "where employee_id = 1")).unOrdered().baselineColumns("employee_id", "complex_field").baselineValues(1L, null).build().run();
    }
}

