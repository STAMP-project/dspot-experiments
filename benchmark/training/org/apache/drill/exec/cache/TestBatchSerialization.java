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
package org.apache.drill.exec.cache;


import MinorType.BIGINT;
import MinorType.DATE;
import MinorType.DECIMAL18;
import MinorType.DECIMAL28SPARSE;
import MinorType.DECIMAL38SPARSE;
import MinorType.DECIMAL9;
import MinorType.FLOAT4;
import MinorType.FLOAT8;
import MinorType.INT;
import MinorType.INTERVAL;
import MinorType.INTERVALDAY;
import MinorType.INTERVALYEAR;
import MinorType.SMALLINT;
import MinorType.TIME;
import MinorType.TIMESTAMP;
import MinorType.TINYINT;
import MinorType.UINT1;
import MinorType.UINT2;
import MinorType.UINT4;
import MinorType.UINT8;
import MinorType.VARCHAR;
import MinorType.VARDECIMAL;
import java.io.IOException;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.DrillTest;
import org.apache.drill.test.OperatorFixture;
import org.junit.ClassRule;
import org.junit.Test;


public class TestBatchSerialization extends DrillTest {
    @ClassRule
    public static final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    public static OperatorFixture fixture;

    @Test
    public void testTypes() throws IOException {
        testType(TINYINT);
        testType(UINT1);
        testType(SMALLINT);
        testType(UINT2);
        testType(INT);
        testType(UINT4);
        testType(BIGINT);
        testType(UINT8);
        testType(FLOAT4);
        testType(FLOAT8);
        testType(DECIMAL9);
        testType(DECIMAL18);
        testType(DECIMAL28SPARSE);
        testType(DECIMAL38SPARSE);
        testType(VARDECIMAL);
        // testType(MinorType.DECIMAL28DENSE); No writer
        // testType(MinorType.DECIMAL38DENSE); No writer
        testType(DATE);
        testType(TIME);
        testType(TIMESTAMP);
        testType(INTERVAL);
        testType(INTERVALYEAR);
        testType(INTERVALDAY);
    }

    /**
     * Tests a map type and an SV2.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testMap() throws IOException {
        BatchSchema schema = new SchemaBuilder().add("top", INT).addMap("map").add("key", INT).add("value", VARCHAR).resumeSchema().build();
        verifySerialize(buildMapSet(schema).toIndirect(), buildMapSet(schema));
    }

    @Test
    public void testArray() throws IOException {
        BatchSchema schema = new SchemaBuilder().add("top", INT).addArray("arr", VARCHAR).build();
        verifySerialize(buildArraySet(schema).toIndirect(), buildArraySet(schema));
    }
}

