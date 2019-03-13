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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.reflect;


import ReflectData.NS_MAP_KEY;
import ReflectData.NS_MAP_VALUE;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test serialization and de-serialization of non-string map-keys
 */
public class TestNonStringMapKeys {
    @Test
    public void testNonStringMapKeys() throws Exception {
        Company entityObj1 = buildCompany();
        Company entityObj2 = buildCompany();
        String testType = "NonStringKeysTest";
        Company[] entityObjs = new Company[]{ entityObj1, entityObj2 };
        byte[] bytes = testSerialization(testType, entityObj1, entityObj2);
        List<GenericRecord> records = ((List<GenericRecord>) (testGenericDatumRead(testType, bytes, entityObjs)));
        GenericRecord record = records.get(0);
        Object employees = record.get("employees");
        Assert.assertTrue("Unable to read 'employees' map", (employees instanceof GenericArray));
        GenericArray arrayEmployees = ((GenericArray) (employees));
        Object employeeRecord = arrayEmployees.get(0);
        Assert.assertTrue((employeeRecord instanceof GenericRecord));
        Object key = ((GenericRecord) (employeeRecord)).get(NS_MAP_KEY);
        Object value = ((GenericRecord) (employeeRecord)).get(NS_MAP_VALUE);
        Assert.assertTrue((key instanceof GenericRecord));
        Assert.assertTrue((value instanceof GenericRecord));
        // Map stored: 1:foo, 2:bar
        Object id = get("id");
        Object name = ((GenericRecord) (value)).get("name").toString();
        Assert.assertTrue((((id.equals(1)) && (name.equals("Foo"))) || ((id.equals(2)) && (name.equals("Bar")))));
        List<Company> records2 = ((List<Company>) (testReflectDatumRead(testType, bytes, entityObjs)));
        Company co = records2.get(0);
        log(("Read: " + co));
        Assert.assertNotNull(co.getEmployees());
        Assert.assertEquals(2, co.getEmployees().size());
        Iterator<Map.Entry<EmployeeId, EmployeeInfo>> itr = co.getEmployees().entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<EmployeeId, EmployeeInfo> e = itr.next();
            id = e.getKey().getId();
            name = e.getValue().getName();
            Assert.assertTrue((((id.equals(1)) && (name.equals("Foo"))) || ((id.equals(2)) && (name.equals("Bar")))));
        } 
        byte[] jsonBytes = testJsonEncoder(testType, entityObj1);
        Assert.assertNotNull("Unable to serialize using jsonEncoder", jsonBytes);
        GenericRecord jsonRecord = testJsonDecoder(testType, jsonBytes, entityObj1);
        Assert.assertEquals("JSON decoder output not same as Binary Decoder", record, jsonRecord);
    }

    @Test
    public void testNonStringMapKeysInNestedMaps() throws Exception {
        Company2 entityObj1 = buildCompany2();
        String testType = "NestedMapsTest";
        Company2[] entityObjs = new Company2[]{ entityObj1 };
        byte[] bytes = testSerialization(testType, entityObj1);
        List<GenericRecord> records = ((List<GenericRecord>) (testGenericDatumRead(testType, bytes, entityObjs)));
        GenericRecord record = records.get(0);
        Object employees = record.get("employees");
        Assert.assertTrue("Unable to read 'employees' map", (employees instanceof GenericArray));
        GenericArray employeesMapArray = ((GenericArray) (employees));
        Object employeeMapElement = employeesMapArray.get(0);
        Assert.assertTrue((employeeMapElement instanceof GenericRecord));
        Object key = ((GenericRecord) (employeeMapElement)).get(NS_MAP_KEY);
        Object value = ((GenericRecord) (employeeMapElement)).get(NS_MAP_VALUE);
        Assert.assertEquals(11, key);
        Assert.assertTrue((value instanceof GenericRecord));
        GenericRecord employeeInfo = ((GenericRecord) (value));
        Object name = employeeInfo.get("name").toString();
        Assert.assertEquals("Foo", name);
        Object companyMap = employeeInfo.get("companyMap");
        Assert.assertTrue((companyMap instanceof GenericArray));
        GenericArray companyMapArray = ((GenericArray) (companyMap));
        Object companyMapElement = companyMapArray.get(0);
        Assert.assertTrue((companyMapElement instanceof GenericRecord));
        key = ((GenericRecord) (companyMapElement)).get(NS_MAP_KEY);
        value = ((GenericRecord) (companyMapElement)).get(NS_MAP_VALUE);
        Assert.assertEquals(14, key);
        if (value instanceof Utf8)
            value = ((Utf8) (value)).toString();

        Assert.assertEquals("CompanyFoo", value);
        List<Company2> records2 = ((List<Company2>) (testReflectDatumRead(testType, bytes, entityObjs)));
        Company2 co = records2.get(0);
        log(("Read: " + co));
        Assert.assertNotNull(co.getEmployees());
        Assert.assertEquals(1, co.getEmployees().size());
        Iterator<Map.Entry<Integer, EmployeeInfo2>> itr = co.getEmployees().entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<Integer, EmployeeInfo2> e = itr.next();
            Integer id = e.getKey();
            name = e.getValue().getName();
            Assert.assertTrue(((id.equals(11)) && (name.equals("Foo"))));
            Assert.assertEquals("CompanyFoo", e.getValue().companyMap.values().iterator().next());
        } 
        byte[] jsonBytes = testJsonEncoder(testType, entityObj1);
        Assert.assertNotNull("Unable to serialize using jsonEncoder", jsonBytes);
        GenericRecord jsonRecord = testJsonDecoder(testType, jsonBytes, entityObj1);
        Assert.assertEquals("JSON decoder output not same as Binary Decoder", record, jsonRecord);
    }

    @Test
    public void testRecordNameInvariance() throws Exception {
        SameMapSignature entityObj1 = buildSameMapSignature();
        String testType = "RecordNameInvariance";
        SameMapSignature[] entityObjs = new SameMapSignature[]{ entityObj1 };
        byte[] bytes = testSerialization(testType, entityObj1);
        List<GenericRecord> records = ((List<GenericRecord>) (testGenericDatumRead(testType, bytes, entityObjs)));
        GenericRecord record = records.get(0);
        Object map1obj = record.get("map1");
        Assert.assertTrue("Unable to read map1", (map1obj instanceof GenericArray));
        GenericArray map1array = ((GenericArray) (map1obj));
        Object map1element = map1array.get(0);
        Assert.assertTrue((map1element instanceof GenericRecord));
        Object key = ((GenericRecord) (map1element)).get(NS_MAP_KEY);
        Object value = ((GenericRecord) (map1element)).get(NS_MAP_VALUE);
        Assert.assertEquals(1, key);
        Assert.assertEquals("Foo", value.toString());
        Object map2obj = record.get("map2");
        Assert.assertEquals(map1obj, map2obj);
        List<SameMapSignature> records2 = ((List<SameMapSignature>) (testReflectDatumRead(testType, bytes, entityObjs)));
        SameMapSignature entity = records2.get(0);
        log(("Read: " + entity));
        Assert.assertNotNull(entity.getMap1());
        Assert.assertEquals(1, entity.getMap1().size());
        Iterator<Map.Entry<Integer, String>> itr = entity.getMap1().entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<Integer, String> e = itr.next();
            key = e.getKey();
            value = e.getValue();
            Assert.assertEquals(1, key);
            Assert.assertEquals("Foo", value.toString());
        } 
        Assert.assertEquals(entity.getMap1(), entity.getMap2());
        Assert.assertEquals(entity.getMap1(), entity.getMap3());
        Assert.assertEquals(entity.getMap1(), entity.getMap4());
        ReflectData rdata = ReflectData.get();
        Schema schema = rdata.getSchema(SameMapSignature.class);
        Schema map1schema = schema.getField("map1").schema().getElementType();
        Schema map2schema = schema.getField("map2").schema().getElementType();
        Schema map3schema = schema.getField("map3").schema().getElementType();
        Schema map4schema = schema.getField("map4").schema().getElementType();
        log(("Schema for map1 = " + map1schema));
        log(("Schema for map2 = " + map2schema));
        log(("Schema for map3 = " + map3schema));
        log(("Schema for map4 = " + map4schema));
        Assert.assertEquals(map1schema.getFullName(), "org.apache.avro.reflect.PairIntegerString");
        Assert.assertEquals(map1schema, map2schema);
        Assert.assertEquals(map1schema, map3schema);
        Assert.assertEquals(map1schema, map4schema);
        byte[] jsonBytes = testJsonEncoder(testType, entityObj1);
        Assert.assertNotNull("Unable to serialize using jsonEncoder", jsonBytes);
        GenericRecord jsonRecord = testJsonDecoder(testType, jsonBytes, entityObj1);
        Assert.assertEquals("JSON decoder output not same as Binary Decoder", record.get("map1"), jsonRecord.get("map1"));
        Assert.assertEquals("JSON decoder output not same as Binary Decoder", record.get("map2"), jsonRecord.get("map2"));
    }
}

