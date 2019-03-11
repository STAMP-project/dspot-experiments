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
package org.apache.flink.formats.json;


import java.util.Map;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.descriptors.Json;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.types.Row;
import org.apache.flink.util.TestLogger;
import org.junit.Test;


/**
 * Tests for the {@link JsonRowFormatFactory}.
 */
public class JsonRowFormatFactoryTest extends TestLogger {
    private static final String JSON_SCHEMA = "{" + (((((((((((((((("  'title': 'Fruit'," + "  'type': 'object',") + "  'properties': {") + "    'name': {") + "      'type': 'string'") + "    },") + "    'count': {") + "      'type': 'integer'") + "    },") + "    'time': {") + "      'description': 'row time',") + "      'type': 'string',") + "      'format': 'date-time'") + "    }") + "  },") + "  'required': ['name', 'count', 'time']") + "}");

    private static final TypeInformation<Row> SCHEMA = Types.ROW(new String[]{ "field1", "field2" }, new TypeInformation[]{ Types.BOOLEAN(), Types.INT() });

    @Test
    public void testSchema() {
        final Map<String, String> properties = JsonRowFormatFactoryTest.toMap(new Json().schema(JsonRowFormatFactoryTest.SCHEMA).failOnMissingField(false));
        testSchemaSerializationSchema(properties);
        testSchemaDeserializationSchema(properties);
    }

    @Test
    public void testJsonSchema() {
        final Map<String, String> properties = JsonRowFormatFactoryTest.toMap(new Json().jsonSchema(JsonRowFormatFactoryTest.JSON_SCHEMA).failOnMissingField(true));
        testJsonSchemaSerializationSchema(properties);
        testJsonSchemaDeserializationSchema(properties);
    }

    @Test
    public void testSchemaDerivation() {
        final Map<String, String> properties = JsonRowFormatFactoryTest.toMap(new Schema().field("field1", Types.BOOLEAN()).field("field2", Types.INT()).field("proctime", Types.SQL_TIMESTAMP()).proctime(), new Json().deriveSchema());
        testSchemaSerializationSchema(properties);
        testSchemaDeserializationSchema(properties);
    }
}

