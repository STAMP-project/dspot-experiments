/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.TestObjectPrime;
import com.github.dozermapper.core.vo.map.CustomMap;
import com.github.dozermapper.core.vo.map.CustomMapIF;
import com.github.dozermapper.core.vo.map.MapTestObject;
import com.github.dozermapper.core.vo.map.MapTestObjectPrime;
import com.github.dozermapper.core.vo.map.MapToMap;
import com.github.dozermapper.core.vo.map.MapToMapPrime;
import com.github.dozermapper.core.vo.map.MapToProperty;
import com.github.dozermapper.core.vo.map.NestedObj;
import com.github.dozermapper.core.vo.map.NestedObjPrime;
import com.github.dozermapper.core.vo.map.PropertyToMap;
import com.github.dozermapper.core.vo.map.SimpleObj;
import com.github.dozermapper.core.vo.map.SimpleObjPrime;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;


public class MapTypeTest extends AbstractFunctionalTest {
    @Test
    public void testMapToVo() {
        // Test simple Map --> Vo with custom mappings defined.
        mapper = getMapper("mappings/mapMapping2.xml");
        NestedObj nestedObj = newInstance(NestedObj.class);
        nestedObj.setField1("nestedfield1value");
        Map<String, Serializable> src = newInstance(HashMap.class);
        src.put("field1", "mapnestedfield1value");
        src.put("nested", nestedObj);
        SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class, "caseA");
        Assert.assertEquals(src.get("field1"), result.getField1());
        Assert.assertEquals(nestedObj.getField1(), result.getNested().getField1());
    }

    @Test
    public void testMapToVoSimple() {
        mapper = getMapper();
        NestedObj nestedObj = newInstance(NestedObj.class);
        nestedObj.setField1("nestedfield1value");
        Map<String, Serializable> src = newInstance(HashMap.class);
        src.put("field1", "mapnestedfield1value");
        SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class);
        Assert.assertEquals(src.get("field1"), result.getField1());
    }

    @Test
    public void testMapToVoWithRenameField() {
        // Test simple Map --> Vo with custom mappings defined.
        mapper = getMapper("mappings/mapMapping2.xml");
        NestedObj nestedObj = newInstance(NestedObj.class);
        nestedObj.setField1("nestedfield1value");
        Map<String, Object> src = new HashMap<>();
        src.put("first", "mapnestedfield1value");
        src.put("nested", nestedObj);
        SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class, "caseC");
        Assert.assertEquals(src.get("first"), result.getField1());
        Assert.assertEquals(nestedObj.getField1(), result.getNested().getField1());
    }

    @Test
    public void testMapToVoWithRenameFieldReverse() {
        // Test simple Map --> Vo with custom mappings defined.
        mapper = getMapper("mappings/mapMapping2.xml");
        NestedObj nestedObj = newInstance(NestedObj.class);
        nestedObj.setField1("nestedfield1value");
        Map<String, Object> src = new HashMap<>();
        src.put("first", "mapnestedfield1value");
        src.put("nested", nestedObj);
        SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class, "caseD");
        Assert.assertEquals(src.get("first"), result.getField1());
        Assert.assertEquals(nestedObj.getField1(), result.getNested().getField1());
    }

    @Test
    public void testMapToVo_CustomMappings() {
        // Test simple Map --> Vo with custom mappings defined.
        mapper = getMapper("mappings/mapMapping2.xml");
        Map<String, String> src = newInstance(HashMap.class);
        src.put("field1", "field1value");
        src.put("field2", "field2value");
        SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class, "caseB");
        Assert.assertNull(result.getField1());
        Assert.assertEquals(src.get("field2"), result.getField2());
    }

    @Test
    public void testMapToVoUsingMapId() {
        // Simple map --> vo using a map-id
        mapper = super.getMapper("mappings/mapMapping.xml");
        Map<String, String> src = newInstance(HashMap.class);
        src.put("field1", "field1value");
        src.put("field2", "field2value");
        NestedObjPrime dest = mapper.map(src, NestedObjPrime.class, "caseB");
        Assert.assertEquals(src.get("field1"), dest.getField1());
        Assert.assertEquals(src.get("field2"), dest.getField2());
    }

    @Test
    public void testMapToVoUsingMapId_FieldExclude() {
        // Simple map --> vo using a map-id
        mapper = super.getMapper("mappings/mapMapping.xml");
        Map<String, String> src = newInstance(HashMap.class);
        src.put("field1", "field1value");
        src.put("field2", "field2value");
        NestedObjPrime dest = mapper.map(src, NestedObjPrime.class, "caseC");
        Assert.assertNull("field was excluded and should be null", dest.getField1());
        Assert.assertEquals(src.get("field2"), dest.getField2());
    }

    @Test
    public void testNestedMapToVoUsingMapId() {
        // Another test for nested Map --> Vo using <field map-id=....>
        mapper = super.getMapper("mappings/mapMapping.xml");
        SimpleObj src = newInstance(SimpleObj.class);
        src.setField1("field1");
        NestedObj nested = newInstance(NestedObj.class);
        nested.setField1("nestedfield1");
        src.setNested(nested);
        Map<String, String> nested2 = newInstance(HashMap.class);
        nested2.put("field1", "field1MapValue");
        src.setNested2(nested2);
        SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class, "caseA2");
        Assert.assertNull(result.getNested2().getField1());// field was excluded

        Assert.assertEquals(src.getField1(), result.getField1());
        Assert.assertEquals(src.getNested().getField1(), result.getNested().getField1());
    }

    @Test
    public void testMapToVo_NoCustomMappings() {
        // Test simple Map --> Vo without any custom mappings defined.
        NestedObj nestedObj = newInstance(NestedObj.class);
        nestedObj.setField1("nestedfield1value");
        Map<String, Serializable> src = newInstance(HashMap.class);
        src.put("field1", "mapnestedfield1value");
        src.put("nested", nestedObj);
        SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class);
        Assert.assertEquals(src.get("field1"), result.getField1());
        Assert.assertEquals(nestedObj.getField1(), result.getNested().getField1());
    }

    @Test
    public void testVoToMap_NoCustomMappings() {
        // Test simple Vo --> Map without any custom mappings defined.
        SimpleObjPrime src = newInstance(SimpleObjPrime.class);
        src.setField1("someValueField1");
        src.setField2("someValueField2");
        src.setSimpleobjprimefield("someOtherValue");
        NestedObjPrime nested = newInstance(NestedObjPrime.class);
        nested.setField1("field1Value");
        nested.setField2("field2Value");
        src.setNested(nested);
        NestedObjPrime nested2 = newInstance(NestedObjPrime.class);
        src.setNested2(nested2);
        // Map complex object to HashMap
        Map<?, ?> destMap = newInstance(HashMap.class);
        mapper.map(src, destMap);
        // Map HashMap back to new instance of the complex object
        SimpleObjPrime mappedSrc = mapper.map(destMap, SimpleObjPrime.class);
        // Remapped complex type should equal original src if all fields were mapped both ways.
        Assert.assertEquals(src, mappedSrc);
    }

    @Test
    public void testMapToMap() {
        Mapper mapper = getMapper("mappings/mapInterfaceMapping.xml", "mappings/testDozerBeanMapping.xml");
        TestObject to = newInstance(TestObject.class);
        to.setOne("one");
        TestObject to2 = newInstance(TestObject.class);
        to2.setTwo(new Integer(2));
        Map<String, TestObject> map = newInstance(HashMap.class);
        map.put("to", to);
        map.put("to2", to2);
        Map<String, TestObject> map2 = newInstance(HashMap.class);
        map2.put("to", to);
        map2.put("to2", to2);
        MapToMap mtm = new MapToMap(map, map2);
        MapToMapPrime mtmp = mapper.map(mtm, MapToMapPrime.class);
        Assert.assertEquals("one", ((TestObject) (mtmp.getStandardMap().get("to"))).getOne());
        Assert.assertEquals(2, ((TestObject) (mtmp.getStandardMap().get("to2"))).getTwo().intValue());
        // verify that we transformed from object to object prime
        Assert.assertEquals("one", ((TestObjectPrime) (mtmp.getStandardMapWithHint().get("to"))).getOnePrime());
        Assert.assertEquals(2, ((TestObjectPrime) (mtmp.getStandardMapWithHint().get("to2"))).getTwoPrime().intValue());
    }

    @Test
    public void testMapToMapExistingDestination() {
        Mapper mapper = getMapper("mappings/mapInterfaceMapping.xml", "mappings/testDozerBeanMapping.xml");
        TestObject to = newInstance(TestObject.class);
        to.setOne("one");
        TestObject to2 = newInstance(TestObject.class);
        to2.setTwo(new Integer(2));
        Map<String, TestObject> map = newInstance(HashMap.class);
        map.put("to", to);
        map.put("to2", to2);
        MapToMap mtm = newInstance(MapToMap.class);
        mtm.setStandardMap(map);
        // create an existing map and set a value so we can test if it exists after
        // mapping
        MapToMapPrime mtmp = newInstance(MapToMapPrime.class);
        Map<String, Serializable> map2 = newInstance(Hashtable.class);
        map2.put("toDest", to);
        mtmp.setStandardMap(map2);
        mapper.map(mtm, mtmp);
        Assert.assertEquals("one", ((TestObject) (mtmp.getStandardMap().get("to"))).getOne());
        Assert.assertEquals(2, ((TestObject) (mtmp.getStandardMap().get("to2"))).getTwo().intValue());
        Assert.assertEquals("one", ((TestObject) (mtmp.getStandardMap().get("toDest"))).getOne());
    }

    @Test
    public void testPropertyClassLevelMap() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        PropertyToMap ptm = newInstance(PropertyToMap.class);
        ptm.setStringProperty("stringPropertyValue");
        ptm.addStringProperty2("stringProperty2Value");
        Map<?, ?> map = mapper.map(ptm, HashMap.class, "myTestMapping");
        Assert.assertEquals("stringPropertyValue", map.get("stringProperty"));
        Assert.assertEquals("stringProperty2Value", map.get("myStringProperty"));
        CustomMapIF customMap = mapper.map(ptm, CustomMap.class, "myCustomTestMapping");
        Assert.assertEquals("stringPropertyValue", customMap.getValue("stringProperty"));
        Assert.assertEquals("stringProperty2Value", customMap.getValue("myStringProperty"));
        CustomMapIF custom = newInstance(CustomMap.class);
        custom.putValue("myKey", "myValue");
        mapper.map(ptm, custom, "myCustomTestMapping");
        Assert.assertEquals("stringPropertyValue", custom.getValue("stringProperty"));
        Assert.assertEquals("myValue", custom.getValue("myKey"));
    }

    @Test
    public void testPropertyClassLevelMap2() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        PropertyToMap ptm = newInstance(PropertyToMap.class);
        ptm.setStringProperty("stringPropertyValue");
        ptm.addStringProperty2("stringProperty2Value");
        CustomMapIF customMap = mapper.map(ptm, CustomMap.class, "myCustomTestMapping");
        Assert.assertEquals("stringPropertyValue", customMap.getValue("stringProperty"));
        Assert.assertEquals("stringProperty2Value", customMap.getValue("myStringProperty"));
    }

    @Test
    public void testPropertyClassLevelMapBack() {
        // Map Back
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        Map<String, Object> map = newInstance(HashMap.class);
        map.put("stringProperty", "stringPropertyValue");
        map.put("integerProperty", new Integer("567"));
        PropertyToMap property = mapper.map(map, PropertyToMap.class, "myTestMapping");
        Assert.assertEquals("stringPropertyValue", property.getStringProperty());
        CustomMapIF custom = newInstance(CustomMap.class);
        custom.putValue("stringProperty", "stringPropertyValue");
        PropertyToMap property2 = mapper.map(custom, PropertyToMap.class, "myCustomTestMapping");
        Assert.assertEquals("stringPropertyValue", property2.getStringProperty());
        map.put("stringProperty3", "myValue");
        mapper.map(map, property, "myTestMapping");
        Assert.assertEquals("myValue", property.getStringProperty3());
    }

    @Test
    public void testPropertyToMap() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        PropertyToMap ptm = newInstance(PropertyToMap.class);
        ptm.setStringProperty("stringPropertyValue");
        ptm.addStringProperty2("stringProperty2Value");
        ptm.setStringProperty6("string6Value");
        Map<String, Object> hashMap = newInstance(HashMap.class);
        hashMap.put("reverseMapString", "reverseMapStringValue");
        hashMap.put("reverseMapInteger", new Integer("567"));
        ptm.setReverseMap(hashMap);
        MapToProperty mtp = mapper.map(ptm, MapToProperty.class);
        Assert.assertTrue(mtp.getHashMap().containsKey("stringProperty"));
        Assert.assertTrue(mtp.getHashMap().containsValue("stringPropertyValue"));
        Assert.assertTrue(mtp.getHashMap().containsKey("myStringProperty"));
        Assert.assertTrue(mtp.getHashMap().containsValue("stringProperty2Value"));
        Assert.assertFalse(mtp.getHashMap().containsValue("nullStringProperty"));
        Assert.assertTrue(mtp.getNullHashMap().containsValue("string6Value"));
        Assert.assertEquals("reverseMapStringValue", mtp.getReverseMapString());
        Assert.assertEquals(((Integer) (hashMap.get("reverseMapInteger"))).toString(), mtp.getReverseMapInteger());
        // Map Back
        PropertyToMap dest = mapper.map(mtp, PropertyToMap.class);
        Assert.assertTrue(dest.getStringProperty().equals("stringPropertyValue"));
        Assert.assertTrue(dest.getStringProperty2().equals("stringProperty2Value"));
        Assert.assertTrue(dest.getReverseMap().containsKey("reverseMapString"));
        Assert.assertTrue(dest.getReverseMap().containsValue("reverseMapStringValue"));
        Assert.assertNull(dest.getNullStringProperty());
    }

    @Test
    public void testPropertyToCustomMap() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        PropertyToMap ptm = newInstance(PropertyToMap.class);
        ptm.setStringProperty3("stringProperty3Value");
        ptm.setStringProperty4("stringProperty4Value");
        ptm.setStringProperty5("stringProperty5Value");
        MapToProperty mtp = mapper.map(ptm, MapToProperty.class);
        Assert.assertEquals("stringProperty3Value", mtp.getCustomMap().getValue("myCustomProperty"));
        Assert.assertEquals("stringProperty5Value", mtp.getCustomMap().getValue("stringProperty5"));
        Assert.assertEquals("stringProperty4Value", mtp.getNullCustomMap().getValue("myCustomNullProperty"));
        Assert.assertEquals("stringProperty5Value", mtp.getCustomMapWithDiffSetMethod().getValue("stringProperty5"));
        // Map Back
        PropertyToMap dest = mapper.map(mtp, PropertyToMap.class);
        Assert.assertEquals("stringProperty3Value", dest.getStringProperty3());
        Assert.assertEquals("stringProperty4Value", dest.getStringProperty4());
        Assert.assertEquals("stringProperty5Value", dest.getStringProperty5());
    }

    @Test
    public void testPropertyToClassLevelMap() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        MapTestObject mto = newInstance(MapTestObject.class);
        PropertyToMap ptm = newInstance(PropertyToMap.class);
        Map<String, String> map = newInstance(HashMap.class);
        map.put("reverseClassLevelMapString", "reverseClassLevelMapStringValue");
        mto.setPropertyToMapMapReverse(map);
        ptm.setStringProperty("stringPropertyValue");
        ptm.addStringProperty2("stringProperty2Value");
        ptm.setStringProperty3("stringProperty3Value");
        ptm.setStringProperty4("stringProperty4Value");
        ptm.setStringProperty5("stringProperty5Value");
        mto.setPropertyToMap(ptm);
        PropertyToMap ptm2 = newInstance(PropertyToMap.class);
        ptm2.setStringProperty("stringPropertyValue");
        mto.setPropertyToMapToNullMap(ptm2);
        MapTestObjectPrime mtop = mapper.map(mto, MapTestObjectPrime.class);
        Assert.assertTrue(mtop.getPropertyToMapMap().containsKey("stringProperty"));
        Assert.assertTrue(mtop.getPropertyToMapMap().containsKey("myStringProperty"));
        Assert.assertTrue(mtop.getPropertyToMapMap().containsKey("stringProperty3"));
        Assert.assertTrue(mtop.getPropertyToMapMap().containsKey("stringProperty4"));
        Assert.assertTrue(mtop.getPropertyToMapMap().containsKey("stringProperty5"));
        Assert.assertTrue(mtop.getPropertyToMapMap().containsKey("nullStringProperty"));
        Assert.assertTrue(mtop.getPropertyToMapMap().containsValue("stringPropertyValue"));
        Assert.assertTrue(mtop.getPropertyToMapMap().containsValue("stringProperty2Value"));
        Assert.assertTrue(mtop.getPropertyToMapMap().containsValue("stringProperty3Value"));
        Assert.assertTrue(mtop.getPropertyToMapMap().containsValue("stringProperty4Value"));
        Assert.assertTrue(mtop.getPropertyToMapMap().containsValue("stringProperty5Value"));
        Assert.assertFalse(mtop.getPropertyToMapMap().containsValue("nullStringProperty"));
        Assert.assertFalse(mtop.getPropertyToMapMap().containsKey("excludeMe"));
        Assert.assertEquals("reverseClassLevelMapStringValue", mtop.getPropertyToMapReverse().getReverseClassLevelMapString());
        Assert.assertTrue(mtop.getNullPropertyToMapMap().containsKey("stringProperty"));
        Assert.assertEquals("stringPropertyValue", mtop.getNullPropertyToMapMap().get("stringProperty"));
        // Map Back
        MapTestObject mto2 = mapper.map(mtop, MapTestObject.class);
        Assert.assertEquals("stringPropertyValue", mto2.getPropertyToMap().getStringProperty());
        Assert.assertEquals("stringProperty2Value", mto2.getPropertyToMap().getStringProperty2());
        Assert.assertEquals("stringProperty3Value", mto2.getPropertyToMap().getStringProperty3());
        Assert.assertEquals("stringProperty4Value", mto2.getPropertyToMap().getStringProperty4());
        Assert.assertEquals("stringProperty5Value", mto2.getPropertyToMap().getStringProperty5());
        Assert.assertTrue(mto2.getPropertyToMapMapReverse().containsKey("reverseClassLevelMapString"));
        Assert.assertEquals("reverseClassLevelMapStringValue", mto2.getPropertyToMapMapReverse().get("reverseClassLevelMapString"));
    }

    @Test
    public void testPropertyToCustomClassLevelMap() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        MapTestObject mto = newInstance(MapTestObject.class);
        PropertyToMap ptm = newInstance(PropertyToMap.class);
        ptm.setStringProperty("stringPropertyValue");
        ptm.setStringProperty2("stringProperty2Value");
        mto.setPropertyToCustomMap(ptm);
        CustomMapIF customMap = newInstance(CustomMap.class);
        customMap.putValue("stringProperty", "stringPropertyValue");
        mto.setPropertyToCustomMapMapWithInterface(customMap);
        MapTestObjectPrime mtop = mapper.map(mto, MapTestObjectPrime.class);
        Assert.assertEquals("stringPropertyValue", mtop.getPropertyToCustomMapMap().getValue("stringProperty"));
        Assert.assertNull(mtop.getPropertyToCustomMapMap().getValue("excludeMe"));
        Assert.assertEquals("stringProperty2Value", mtop.getPropertyToCustomMapMap().getValue("myStringProperty"));
        Assert.assertEquals("stringPropertyValue", mtop.getPropertyToCustomMapWithInterface().getStringProperty());
        // Map Back
        MapTestObject mto2 = mapper.map(mtop, MapTestObject.class);
        Assert.assertEquals("stringPropertyValue", mto2.getPropertyToCustomMap().getStringProperty());
        Assert.assertEquals("stringProperty2Value", mto2.getPropertyToCustomMap().getStringProperty2());
        Assert.assertNull(mto2.getPropertyToCustomMap().getExcludeMe());
        Assert.assertEquals("stringPropertyValue", mto2.getPropertyToCustomMapMapWithInterface().getValue("stringProperty"));
    }

    @Test
    public void testMapGetSetMethod_ClassLevel() {
        runMapGetSetMethodTest("useCase1");
    }

    @Test
    public void testMapGetSetMethod_FieldLevel() {
        runMapGetSetMethodTest("useCase2");
    }

    @Test
    public void testDateFormat_CustomMapType() throws Exception {
        // Test that date format works for mapping between String and Custom Map Type
        mapper = getMapper("mappings/mapMapping3.xml");
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String dateStr = "10/15/2005";
        CustomMap src = newInstance(CustomMap.class);
        src.putValue("fieldA", dateStr);
        com.github.dozermapper.core.vo.SimpleObj dest = mapper.map(src, com.github.dozermapper.core.vo.SimpleObj.class);
        Assert.assertNotNull("dest field should not be null", dest.getField5());
        Assert.assertEquals("dest field contains wrong date value", df.parse(dateStr), dest.getField5().getTime());
        CustomMap remappedSrc = mapper.map(dest, CustomMap.class);
        Assert.assertEquals("remapped src field contains wrong date string", dateStr, remappedSrc.getValue("fieldA"));
    }

    @Test
    public void testMapType_NestedMapToVo_NoCustomMappings() {
        // Simple test checking that Maps get mapped to a VO without any custom mappings or map-id.
        // Should behave like Vo --> Vo, matching on common attr(key) names.
        Map<String, String> nested2 = newInstance(HashMap.class);
        nested2.put("field1", "mapnestedfield1");
        nested2.put("field2", null);
        SimpleObj src = newInstance(SimpleObj.class);
        src.setNested2(nested2);
        SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class);
        Assert.assertNotNull(result.getNested2());
        Assert.assertEquals(nested2.get("field1"), result.getNested2().getField1());
        SimpleObj result2 = mapper.map(result, SimpleObj.class);
        Assert.assertEquals(src, result2);
    }

    @Test
    public void testMapType_MapToVo_CustomMapping_NoMapId() {
        // Test nested Map --> Vo using custom mappings without map-id
        mapper = getMapper("mappings/mapMapping3.xml");
        NestedObj nested = newInstance(NestedObj.class);
        nested.setField1("field1Value");
        Map<String, String> nested2 = newInstance(HashMap.class);
        nested2.put("field1", "mapnestedfield1value");
        nested2.put("field2", "mapnestedfield2value");
        SimpleObj src = newInstance(SimpleObj.class);
        src.setNested2(nested2);
        SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class);
        Assert.assertNull(result.getNested2().getField1());// field exclude in mappings file

        Assert.assertEquals(nested2.get("field2"), result.getNested2().getField2());
    }

    @Test
    public void testMapToVoUsingMapInterface() {
        // Test simple Map --> Vo with custom mappings defined.
        mapper = getMapper("mappings/mapMapping5.xml");
        Map<String, String> src = newInstance(HashMap.class);
        src.put("stringValue", "somevalue");
        SimpleObj dest = mapper.map(src, SimpleObj.class, "test-id");
        Assert.assertEquals("wrong value found for field1", "somevalue", dest.getField1());
    }

    @Test
    public void testMapToVoOverwritesExistingValue() {
        mapper = getMapper("mappings/mapMapping5.xml");
        Map<String, String> src = newInstance(HashMap.class);
        src.put("stringValue", "overwritten");
        SimpleObj dest = new SimpleObj();
        dest.setField1("existingValue");
        mapper.map(src, dest, "test-id");
        Assert.assertEquals("overwritten", dest.getField1());
    }

    @Test
    public void testTreeMap() {
        TreeMap map = new TreeMap();
        map.put("a", "b");
        TreeMap result = mapper.map(map, TreeMap.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }
}

