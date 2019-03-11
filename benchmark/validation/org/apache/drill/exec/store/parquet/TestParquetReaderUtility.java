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
package org.apache.drill.exec.store.parquet;


import java.util.Map;
import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.format.SchemaElement;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.junit.Assert;
import org.junit.Test;


public class TestParquetReaderUtility {
    private static final String path = "src/test/resources/store/parquet/complex/complex.parquet";

    private static ParquetMetadata footer;

    @Test
    public void testSchemaElementsMap() {
        Map<String, SchemaElement> schemaElements = ParquetReaderUtility.getColNameToSchemaElementMapping(TestParquetReaderUtility.footer);
        Assert.assertEquals("Schema elements map size must be 14", schemaElements.size(), 14);
        SchemaElement schemaElement = schemaElements.get("`marketing_info`.`camp_id`");
        Assert.assertNotNull("Schema element must be not null", schemaElement);
        Assert.assertEquals("Schema element must be named 'camp_id'", schemaElement.getName(), "camp_id");
        schemaElement = schemaElements.get("`marketing_info`");
        Assert.assertNotNull("Schema element must be not null", schemaElement);
        Assert.assertEquals("Schema element name match lookup key", schemaElement.getName(), "marketing_info");
    }

    @Test
    public void testColumnDescriptorMap() {
        Map<String, ColumnDescriptor> colDescMap = ParquetReaderUtility.getColNameToColumnDescriptorMapping(TestParquetReaderUtility.footer);
        Assert.assertEquals("Column descriptors map size must be 11", colDescMap.size(), 11);
        Assert.assertNotNull("column descriptor lookup must return not null", colDescMap.get("`marketing_info`.`camp_id`"));
        Assert.assertNull("column descriptor lookup must return null on GroupType column", colDescMap.get("`marketing_info`"));
    }
}

