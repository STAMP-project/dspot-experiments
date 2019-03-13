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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.data.schema;


import Category.ARRAY;
import Category.MAP;
import Category.PRIMITIVE;
import Category.STRUCT;
import junit.framework.TestCase;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHCatSchemaUtils extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TestHCatSchemaUtils.class);

    public void testSimpleOperation() throws Exception {
        String typeString = "struct<name:string,studentid:int," + ((("contact:struct<phNo:string,email:string>," + "currently_registered_courses:array<string>,") + "current_grades:map<string,string>,") + "phNos:array<struct<phNo:string,type:string>>,blah:array<int>>");
        TypeInfo ti = TypeInfoUtils.getTypeInfoFromTypeString(typeString);
        HCatSchema hsch = HCatSchemaUtils.getHCatSchemaFromTypeString(typeString);
        TestHCatSchemaUtils.LOG.info("Type name : {}", ti.getTypeName());
        TestHCatSchemaUtils.LOG.info("HCatSchema : {}", hsch);
        TestCase.assertEquals(hsch.size(), 1);
        // Looks like HCatFieldSchema.getTypeString() lower-cases its results
        TestCase.assertEquals(ti.getTypeName().toLowerCase(), hsch.get(0).getTypeString());
        TestCase.assertEquals(hsch.get(0).getTypeString(), typeString.toLowerCase());
    }

    public void testHCatFieldSchemaConversion() throws Exception {
        FieldSchema stringFieldSchema = new FieldSchema("name1", serdeConstants.STRING_TYPE_NAME, "comment1");
        HCatFieldSchema stringHCatFieldSchema = HCatSchemaUtils.getHCatFieldSchema(stringFieldSchema);
        TestCase.assertEquals(stringHCatFieldSchema.getName(), "name1");
        TestCase.assertEquals(stringHCatFieldSchema.getCategory(), PRIMITIVE);
        TestCase.assertEquals(stringHCatFieldSchema.getComment(), "comment1");
        FieldSchema listFieldSchema = new FieldSchema("name1", "array<tinyint>", "comment1");
        HCatFieldSchema listHCatFieldSchema = HCatSchemaUtils.getHCatFieldSchema(listFieldSchema);
        TestCase.assertEquals(listHCatFieldSchema.getName(), "name1");
        TestCase.assertEquals(listHCatFieldSchema.getCategory(), ARRAY);
        TestCase.assertEquals(listHCatFieldSchema.getComment(), "comment1");
        FieldSchema mapFieldSchema = new FieldSchema("name1", "map<string,int>", "comment1");
        HCatFieldSchema mapHCatFieldSchema = HCatSchemaUtils.getHCatFieldSchema(mapFieldSchema);
        TestCase.assertEquals(mapHCatFieldSchema.getName(), "name1");
        TestCase.assertEquals(mapHCatFieldSchema.getCategory(), MAP);
        TestCase.assertEquals(mapHCatFieldSchema.getComment(), "comment1");
        FieldSchema structFieldSchema = new FieldSchema("name1", "struct<s:string,i:tinyint>", "comment1");
        HCatFieldSchema structHCatFieldSchema = HCatSchemaUtils.getHCatFieldSchema(structFieldSchema);
        TestCase.assertEquals(structHCatFieldSchema.getName(), "name1");
        TestCase.assertEquals(structHCatFieldSchema.getCategory(), STRUCT);
        TestCase.assertEquals(structHCatFieldSchema.getComment(), "comment1");
    }
}

