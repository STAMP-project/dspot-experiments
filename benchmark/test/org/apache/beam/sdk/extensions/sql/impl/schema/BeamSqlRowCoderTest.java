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
package org.apache.beam.sdk.extensions.sql.impl.schema;


import SqlTypeName.BIGINT;
import SqlTypeName.BOOLEAN;
import SqlTypeName.DECIMAL;
import SqlTypeName.DOUBLE;
import SqlTypeName.FLOAT;
import SqlTypeName.INTEGER;
import SqlTypeName.SMALLINT;
import SqlTypeName.TIME;
import SqlTypeName.TIMESTAMP;
import SqlTypeName.TINYINT;
import SqlTypeName.VARCHAR;
import java.math.BigDecimal;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.extensions.sql.impl.utils.CalciteUtils;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.SchemaCoder;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.transforms.SerializableFunctions;
import org.apache.beam.sdk.values.Row;
import org.apache.calcite.rel.type.RelDataType;
import org.joda.time.DateTime;
import org.junit.Test;


/**
 * Tests for BeamSqlRowCoder.
 */
public class BeamSqlRowCoderTest {
    @Test
    public void encodeAndDecode() throws Exception {
        RelDataType relDataType = builder().add("col_tinyint", TINYINT).add("col_smallint", SMALLINT).add("col_integer", INTEGER).add("col_bigint", BIGINT).add("col_float", FLOAT).add("col_double", DOUBLE).add("col_decimal", DECIMAL).add("col_string_varchar", VARCHAR).add("col_time", TIME).add("col_timestamp", TIMESTAMP).add("col_boolean", BOOLEAN).build();
        Schema beamSchema = CalciteUtils.toSchema(relDataType);
        Row row = Row.withSchema(beamSchema).addValues(Byte.valueOf("1"), Short.valueOf("1"), 1, 1L, 1.1F, 1.1, BigDecimal.ZERO, "hello", DateTime.now(), DateTime.now(), true).build();
        Coder<Row> coder = SchemaCoder.of(beamSchema, SerializableFunctions.identity(), SerializableFunctions.identity());
        CoderProperties.coderDecodeEncodeEqual(coder, row);
    }
}

