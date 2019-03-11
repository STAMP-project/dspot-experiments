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
package org.apache.hadoop.hive.serde2;


import ObjectInspector.Category.STRUCT;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.apache.hadoop.hive.metastore.HiveMetaStoreUtils;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.mockito.Mockito;


public class TestSerdeWithFieldComments extends TestCase {
    public void testFieldComments() throws MetaException, SerDeException {
        StructObjectInspector mockSOI = Mockito.mock(StructObjectInspector.class);
        Mockito.when(mockSOI.getCategory()).thenReturn(STRUCT);
        List fieldRefs = new ArrayList<org.apache.hadoop.hive.serde2.objectinspector.StructField>();
        // Add field with a comment...
        fieldRefs.add(mockedStructField("first", "type name 1", "this is a comment"));
        // ... and one without
        fieldRefs.add(mockedStructField("second", "type name 2", null));
        Mockito.when(mockSOI.getAllStructFieldRefs()).thenReturn(fieldRefs);
        Deserializer mockDe = Mockito.mock(Deserializer.class);
        Mockito.when(mockDe.getObjectInspector()).thenReturn(mockSOI);
        List<FieldSchema> result = HiveMetaStoreUtils.getFieldsFromDeserializer("testTable", mockDe);
        TestCase.assertEquals(2, result.size());
        TestCase.assertEquals("first", result.get(0).getName());
        TestCase.assertEquals("this is a comment", result.get(0).getComment());
        TestCase.assertEquals("second", result.get(1).getName());
        TestCase.assertEquals("from deserializer", result.get(1).getComment());
    }
}

