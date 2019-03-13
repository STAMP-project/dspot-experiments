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


import com.github.dozermapper.core.CustomFieldMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.functional_tests.support.SampleDefaultBeanFactory;
import com.github.dozermapper.core.functional_tests.support.TestCustomFieldMapper;
import com.github.dozermapper.core.util.CollectionUtils;
import com.github.dozermapper.core.vo.AnotherTestObject;
import com.github.dozermapper.core.vo.AnotherTestObjectPrime;
import com.github.dozermapper.core.vo.FieldValue;
import com.github.dozermapper.core.vo.Fruit;
import com.github.dozermapper.core.vo.InsideTestObject;
import com.github.dozermapper.core.vo.MethodFieldTestObject;
import com.github.dozermapper.core.vo.MethodFieldTestObject2;
import com.github.dozermapper.core.vo.NoReadMethod;
import com.github.dozermapper.core.vo.NoReadMethodPrime;
import com.github.dozermapper.core.vo.NoVoidSetters;
import com.github.dozermapper.core.vo.NoWriteMethod;
import com.github.dozermapper.core.vo.NoWriteMethodPrime;
import com.github.dozermapper.core.vo.PrimitiveArrayObj;
import com.github.dozermapper.core.vo.PrimitiveArrayObjPrime;
import com.github.dozermapper.core.vo.SimpleObj;
import com.github.dozermapper.core.vo.SimpleObjPrime;
import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.TestObjectPrime;
import com.github.dozermapper.core.vo.TestObjectPrime2;
import com.github.dozermapper.core.vo.context.ContextMapping;
import com.github.dozermapper.core.vo.context.ContextMappingNested;
import com.github.dozermapper.core.vo.context.ContextMappingNestedPrime;
import com.github.dozermapper.core.vo.context.ContextMappingPrime;
import com.github.dozermapper.core.vo.iface.ApplicationUser;
import com.github.dozermapper.core.vo.iface.UpdateMember;
import com.github.dozermapper.core.vo.index.Mccoy;
import com.github.dozermapper.core.vo.index.MccoyPrime;
import com.github.dozermapper.core.vo.isaccessible.Foo;
import com.github.dozermapper.core.vo.isaccessible.FooPrime;
import com.github.dozermapper.core.vo.isaccessible.PrivateConstructorBean;
import com.github.dozermapper.core.vo.isaccessible.PrivateConstructorBeanPrime;
import com.github.dozermapper.core.vo.orphan.Child;
import com.github.dozermapper.core.vo.orphan.ChildPrime;
import com.github.dozermapper.core.vo.orphan.Parent;
import com.github.dozermapper.core.vo.orphan.ParentPrime;
import com.github.dozermapper.core.vo.perf.MyClassA;
import com.github.dozermapper.core.vo.perf.MyClassB;
import com.github.dozermapper.core.vo.set.NamesArray;
import com.github.dozermapper.core.vo.set.NamesCollection;
import com.github.dozermapper.core.vo.set.NamesSet;
import com.github.dozermapper.core.vo.set.NamesSortedSet;
import com.github.dozermapper.core.vo.set.SomeDTO;
import com.github.dozermapper.core.vo.set.SomeOtherDTO;
import com.github.dozermapper.core.vo.set.SomeVO;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GranularDozerBeanMapperTest extends AbstractFunctionalTest {
    private static final Logger LOG = LoggerFactory.getLogger(GranularDozerBeanMapperTest.class);

    @Test
    public void testFieldAccessible() {
        Mapper mapper = getMapper("mappings/fieldAttributeMapping.xml");
        TestObject to = newInstance(TestObject.class);
        to.setFieldAccessible("fieldAccessible");
        to.setFieldAccessiblePrimInt(2);
        InsideTestObject ito = newInstance(InsideTestObject.class);
        ito.setLabel("label");
        to.setFieldAccessibleComplexType(ito);
        String[] stringArray = new String[]{ "one", "two" };
        to.setFieldAccessibleArrayToList(stringArray);
        TestObjectPrime top = mapper.map(to, TestObjectPrime.class);
        Assert.assertEquals("fieldAccessible", TestObjectPrime.fieldAccessible);
        Assert.assertEquals("label", top.fieldAccessibleComplexType.getLabelPrime());
        Assert.assertEquals(2, top.fieldAccessiblePrimInt);
        Assert.assertEquals("one", top.fieldAccessibleArrayToList.get(0));
        Assert.assertEquals("two", top.fieldAccessibleArrayToList.get(1));
        // Map Back
        TestObject toDest = mapper.map(top, TestObject.class);
        Assert.assertEquals("fieldAccessible", toDest.getFieldAccessible());
        Assert.assertEquals("label", toDest.getFieldAccessibleComplexType().getLabel());
        Assert.assertEquals(2, toDest.getFieldAccessiblePrimInt());
        Assert.assertEquals("one", toDest.getFieldAccessibleArrayToList()[0]);
        Assert.assertEquals("two", toDest.getFieldAccessibleArrayToList()[1]);
    }

    @Test
    public void testOverloadGetSetMethods() {
        Mapper mapper = getMapper("mappings/fieldAttributeMapping.xml");
        TestObject to = newInstance(TestObject.class);
        Date date = new Date();
        to.setOverloadGetField(date);
        TestObjectPrime top = mapper.map(to, TestObjectPrime.class);
        GranularDozerBeanMapperTest.LOG.error("time1 {}", date.getTime());
        GranularDozerBeanMapperTest.LOG.error("time2 {}", top.getOverloadSetField().getTime());
        Assert.assertEquals(date, top.getOverloadSetField());
        // Map Back
        TestObject toDest = mapper.map(top, TestObject.class);
        Assert.assertEquals(date, toDest.getOverloadGetField());
    }

    @Test
    public void testFieldCreateMethod() {
        Mapper mapper = getMapper("mappings/fieldAttributeMapping.xml");
        TestObject to = newInstance(TestObject.class);
        InsideTestObject ito = newInstance(InsideTestObject.class);
        // we did not set any values. this will be set in the 'createMethod'
        to.setCreateMethodType(ito);
        TestObjectPrime top = mapper.map(to, TestObjectPrime.class);
        Assert.assertEquals("myField", top.getCreateMethodType().getMyField());
        // Map Back
        TestObject toDest = mapper.map(top, TestObject.class);
        Assert.assertEquals("testCreateMethod", toDest.getCreateMethodType().getTestCreateMethod());
    }

    @Test
    public void testIntegerToString() {
        Mapper mapper = getMapper("mappings/fieldAttributeMapping.xml");
        TestObject to = newInstance(TestObject.class);
        Integer[] array = new Integer[]{ new Integer(1), new Integer(2) };
        to.setArrayForLists(array);
        TestObjectPrime top = mapper.map(to, TestObjectPrime.class);
        Assert.assertEquals("1", top.getListForArray().get(0));
    }

    @Test
    public void testMapNull_MappingLevel() {
        Mapper mapper = getMapper("mappings/nullFieldMapping.xml");
        // check that null does not override an existing value when map-null="false"
        AnotherTestObject src = newInstance(AnotherTestObject.class);
        src.setField3(null);
        src.setField4(null);
        AnotherTestObjectPrime dest = newInstance(AnotherTestObjectPrime.class);
        dest.setTo(newInstance(TestObject.class));
        dest.setField3("555");
        dest.getTo().setOne("4641");
        // dest field should remain unchanged
        mapper.map(src, dest);
        Assert.assertEquals("invalid dest field value", "555", dest.getField3());
        Assert.assertEquals("invalid dest field value2", "4641", dest.getTo().getOne());
    }

    @Test
    public void testMapNull_MappingLevel2() {
        Mapper mapper = getMapper("mappings/nullFieldMapping.xml");
        // Reverse mapping
        AnotherTestObjectPrime src = newInstance(AnotherTestObjectPrime.class);
        src.setTo(newInstance(TestObject.class));
        src.setField3(null);
        src.getTo().setOne(null);
        AnotherTestObject dest = newInstance(AnotherTestObject.class);
        dest.setField3("555");
        dest.setField4("4641");
        // dest field should remain unchanged
        mapper.map(src, dest);
        Assert.assertEquals("invalid dest field value", "555", dest.getField3());
        Assert.assertEquals("invalid dest field value2", "4641", dest.getField4());
    }

    @Test
    public void testMapEmptyString_MappingLevel() {
        Mapper mapper = getMapper("mappings/nullFieldMapping.xml");
        // check that "" does not override an existing value when
        // map-empty-string="false"
        AnotherTestObject src = newInstance(AnotherTestObject.class);
        src.setField3("");
        src.setField4("");
        AnotherTestObjectPrime dest = newInstance(AnotherTestObjectPrime.class);
        dest.setTo(newInstance(TestObject.class));
        dest.setField3("555");
        dest.getTo().setOne("4641");
        // dest field should remain unchanged
        mapper.map(src, dest);
        Assert.assertEquals("invalid dest field value", "555", dest.getField3());
        Assert.assertEquals("invalid dest field value2", "4641", dest.getTo().getOne());
    }

    @Test
    public void testMapEmptyString_MappingLevel2() {
        Mapper mapper = getMapper("mappings/nullFieldMapping.xml");
        // reverse mapping
        AnotherTestObjectPrime src = newInstance(AnotherTestObjectPrime.class);
        src.setTo(newInstance(TestObject.class));
        src.setField3("");
        src.getTo().setOne("");
        AnotherTestObject dest = newInstance(AnotherTestObject.class);
        dest.setField3("555");
        dest.setField4("4641");
        // dest field should remain unchanged
        mapper.map(src, dest);
        Assert.assertEquals("invalid dest field value", "555", dest.getField3());
        Assert.assertEquals("invalid dest field value2", "4641", dest.getField4());
    }

    @Test
    public void testMapNull_ClassLevel() {
        Mapper mapper = getMapper("mappings/nullFieldMapping.xml");
        // check that null does not override an existing value when map-null="false"
        TestObject src = newInstance(TestObject.class);
        src.setOne(null);
        TestObjectPrime2 dest = newInstance(TestObjectPrime2.class);
        dest.setOne("555");
        // dest field should remain unchanged
        mapper.map(src, dest);
        Assert.assertNotNull("dest should not be null", dest.getOne());
        Assert.assertEquals("invalid dest field value", "555", dest.getOne());
        // reverse mapping
        TestObjectPrime2 src2 = newInstance(TestObjectPrime2.class);
        src2.setOne(null);
        TestObject dest2 = newInstance(TestObject.class);
        dest2.setOne("555");
        // dest field should NOT remain unchanged
        mapper.map(src2, dest2);
        Assert.assertNull("dest should be null", dest2.getOne());
    }

    @Test
    public void testMapEmptyString_ClassLevel() {
        Mapper mapper = getMapper("mappings/nullFieldMapping.xml");
        // check that "" does not override an existing value when
        // map-empty-string="false"
        TestObject src = newInstance(TestObject.class);
        src.setOne("");
        TestObjectPrime2 dest = newInstance(TestObjectPrime2.class);
        dest.setOne("555");
        // dest field should remain unchanged
        mapper.map(src, dest);
        Assert.assertNotNull("dest should not be null", dest.getOne());
        Assert.assertEquals("invalid dest field value", "555", dest.getOne());
        // reverse mapping
        TestObjectPrime2 src2 = newInstance(TestObjectPrime2.class);
        src2.setOne("");
        TestObject dest2 = newInstance(TestObject.class);
        dest2.setOne("555");
        // dest field should NOT remain unchanged
        mapper.map(src2, dest2);
        Assert.assertNotNull("dest should not be null", dest2.getOne());
        Assert.assertEquals("dest should be an empty string", "", dest2.getOne());
    }

    @Test
    public void testContextMappingWithNestedContext() {
        Mapper mapper = getMapper("mappings/contextMapping.xml");
        ContextMappingNested cmn = newInstance(ContextMappingNested.class);
        cmn.setLoanNo("loanNoNested");
        List<ContextMappingNested> list = newInstance(ArrayList.class);
        list.add(cmn);
        ContextMapping cm = newInstance(ContextMapping.class);
        cm.setLoanNo("loanNo");
        cm.setContextList(list);
        ContextMappingPrime cmpA = mapper.map(cm, ContextMappingPrime.class, "caseA");
        Assert.assertNull(cmpA.getLoanNo());
        Assert.assertNull(((ContextMappingNestedPrime) (cmpA.getContextList().get(0))).getLoanNo());
        ContextMappingPrime cmpB = mapper.map(cm, ContextMappingPrime.class, "caseB");
        Assert.assertEquals("loanNo", cmpB.getLoanNo());
        Assert.assertEquals("loanNoNested", ((ContextMappingNested) (cmpB.getContextList().get(0))).getLoanNo());
        ContextMappingNestedPrime cmn2 = newInstance(ContextMappingNestedPrime.class);
        cmn2.setLoanNo("loanNoNested");
        List<ContextMappingNestedPrime> list2 = newInstance(ArrayList.class);
        list2.add(cmn2);
        ContextMappingPrime prime = newInstance(ContextMappingPrime.class);
        prime.setLoanNo("loanNo");
        prime.setContextList(list2);
        ContextMapping cmDest = mapper.map(prime, ContextMapping.class, "caseA");
        Assert.assertNull(cmDest.getLoanNo());
        Assert.assertNull(((ContextMappingNested) (cmDest.getContextList().get(0))).getLoanNo());
        ContextMapping cmpBDest = mapper.map(prime, ContextMapping.class, "caseB");
        Assert.assertEquals("loanNo", cmpBDest.getLoanNo());
        Assert.assertEquals("loanNoNested", ((ContextMappingNestedPrime) (cmpBDest.getContextList().get(0))).getLoanNo());
    }

    @Test
    public void testArrayToSortedSet() {
        NamesArray from = newInstance(NamesArray.class);
        String[] names = new String[]{ "John", "Bill", "Tony", "Fred", "Bruce" };
        from.setNames(names);
        NamesSortedSet to = mapper.map(from, NamesSortedSet.class);
        Assert.assertNotNull(to);
        Assert.assertEquals(names.length, to.getNames().size());
    }

    @Test
    public void testSortedSetToArray() {
        NamesSortedSet from = newInstance(NamesSortedSet.class);
        NamesArray to = null;
        SortedSet<String> names = newInstance(TreeSet.class);
        names.add("Jen");
        names.add("Sue");
        names.add("Sally");
        names.add("Jill");
        from.setNames(names);
        to = mapper.map(from, NamesArray.class);
        Assert.assertNotNull(to);
        Assert.assertEquals(names.size(), to.getNames().length);
    }

    @Test
    public void testSetToSortedSet() {
        NamesSet from = newInstance(NamesSet.class);
        NamesSortedSet to = null;
        Set<String> names = newInstance(HashSet.class);
        names.add("Red");
        names.add("Blue");
        names.add("Green");
        from.setNames(names);
        to = mapper.map(from, NamesSortedSet.class);
        Assert.assertNotNull(to);
        Assert.assertEquals(names.size(), to.getNames().size());
    }

    /**
     * When the source type is a Set and the destination is a Collection we expect that the
     * mapper narrows to the Set type instead of using a List by default.
     * <p>
     * issue https://github.com/DozerMapper/dozer/issues/287
     */
    @Test
    public void testSetToCollection() {
        NamesSet from = newInstance(NamesSet.class);
        NamesCollection to;
        Set<String> names = newInstance(HashSet.class);
        names.add("Red");
        names.add("Blue");
        names.add("Green");
        names.add("Green");
        from.setNames(names);
        to = mapper.map(from, NamesCollection.class);
        Assert.assertNotNull(to);
        Assert.assertTrue(((to.getNames()) instanceof Set));
        Assert.assertThat(to.getNames().size(), Is.is(3));
        Assert.assertEquals(names.size(), to.getNames().size());
    }

    @Test
    public void testSortedSetToSet() {
        NamesSortedSet from = newInstance(NamesSortedSet.class);
        NamesSet to = null;
        SortedSet<String> names = newInstance(TreeSet.class);
        names.add("Bone");
        names.add("White");
        names.add("Beige");
        names.add("Ivory");
        names.add("Cream");
        names.add("Off white");
        from.setNames(names);
        to = mapper.map(from, NamesSet.class);
        Assert.assertNotNull(to);
        Assert.assertEquals(names.size(), to.getNames().size());
    }

    @Test
    public void testSetPrivateField() {
        mapper = getMapper("mappings/isaccessiblemapping.xml");
        Foo src = newInstance(Foo.class);
        List<String> list = ((ArrayList<String>) (newInstance(ArrayList.class)));
        list.add("test1");
        list.add("test2");
        src.setCategories(list);
        FooPrime dest = mapper.map(src, FooPrime.class);
        Assert.assertNotNull(dest);
    }

    @Test
    public void testStringToIndexedSet_UsingHint() {
        mapper = getMapper("mappings/indexMapping.xml");
        Mccoy src = newInstance(Mccoy.class);
        src.setStringProperty(String.valueOf(System.currentTimeMillis()));
        src.setField2("someValue");
        MccoyPrime dest = mapper.map(src, MccoyPrime.class, "usingDestHint");
        Set<?> destSet = dest.getFieldValueObjects();
        Assert.assertNotNull("dest set should not be null", destSet);
        Assert.assertEquals("dest set should contain 1 entry", 1, destSet.size());
        Object entry = destSet.iterator().next();
        Assert.assertTrue("dest set entry should be instance of FieldValue", (entry instanceof FieldValue));
        Assert.assertEquals("invalid value for dest object", src.getStringProperty(), ((FieldValue) (entry)).getValue("theKey"));
    }

    @Test
    public void testPrimitiveArrayToList() {
        mapper = getMapper("mappings/primitiveArrayToListMapping.xml");
        int[] i = new int[]{ 1, 2, 3 };
        PrimitiveArrayObj src = newInstance(PrimitiveArrayObj.class);
        src.setField1(i);
        PrimitiveArrayObjPrime dest = mapper.map(src, PrimitiveArrayObjPrime.class);
        Assert.assertNotNull("dest list field should not be null", dest.getField1());
        Assert.assertEquals("invalid dest field size", i.length, dest.getField1().size());
        List<?> srcObjectList = CollectionUtils.convertPrimitiveArrayToList(i);
        Assert.assertEquals("invalid dest field value", srcObjectList, dest.getField1());
    }

    @Test
    public void testPrimitiveArrayToList_UsingHint() {
        mapper = getMapper("mappings/primitiveArrayToListMapping.xml");
        int[] srcArray = new int[]{ 1, 2, 3 };
        PrimitiveArrayObj src = newInstance(PrimitiveArrayObj.class);
        src.setField1(srcArray);
        PrimitiveArrayObjPrime dest = mapper.map(src, PrimitiveArrayObjPrime.class, "primitiveToArrayUsingHint");
        Assert.assertNotNull("dest list field should not be null", dest.getField1());
        Assert.assertEquals("invalid dest field size", srcArray.length, dest.getField1().size());
        for (int i = 0; i < (srcArray.length); i++) {
            String srcValue = String.valueOf(srcArray[i]);
            String resultValue = ((String) (dest.getField1().get(i)));
            Assert.assertEquals("invalid result entry value", srcValue, resultValue);
        }
    }

    @Test
    public void testInterface() {
        mapper = getMapper("mappings/interfaceMapping.xml");
        ApplicationUser user = newInstance(ApplicationUser.class);
        user.setSubscriberNumber("123");
        // Mapping works
        UpdateMember destObject = mapper.map(user, UpdateMember.class);
        Assert.assertEquals("invalid value for subsriber #", user.getSubscriberNumber(), destObject.getSubscriberKey().getSubscriberNumber());
        // Clear value
        destObject = new UpdateMember();
        // Mapping doesn't work
        mapper.map(user, destObject);
        Assert.assertNotNull("dest field should not be null", destObject.getSubscriberKey());
        Assert.assertEquals("invalid value for subsriber #", user.getSubscriberNumber(), destObject.getSubscriberKey().getSubscriberNumber());
    }

    @Test
    public void testCustomFieldMapper() {
        CustomFieldMapper customFieldMapper = new TestCustomFieldMapper();
        mapper = DozerBeanMapperBuilder.create().withCustomFieldMapper(customFieldMapper).build();
        String currentTime = String.valueOf(System.currentTimeMillis());
        SimpleObj src = newInstance(SimpleObj.class);
        src.setField1(currentTime);
        src.setField6(("field6Value" + currentTime));
        SimpleObjPrime dest = mapper.map(src, SimpleObjPrime.class);
        Assert.assertNotNull("dest field1 should not be null", dest.getField1());
        Assert.assertNotNull("dest field6 should not be null", dest.getField6());
        Assert.assertEquals("dest field1 should have been set by custom field mapper", TestCustomFieldMapper.fieldValue, dest.getField1());
        Assert.assertEquals("dest field6 should NOT have been set by custom field mapper", src.getField6(), dest.getField6());
    }

    @Test
    public void testPrivateConstructor() {
        PrivateConstructorBean src = PrivateConstructorBean.newInstance();
        src.setField1("someValue");
        PrivateConstructorBeanPrime dest = mapper.map(src, PrivateConstructorBeanPrime.class);
        Assert.assertNotNull("dest bean should not be null", dest);
        Assert.assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
    }

    /* Bug #1549738 */
    @Test
    public void testSetMapping_UppercaseFieldNameInXML() {
        // For some reason the resulting SomeVO contains a Set with 4 objects. 2 SomeOtherDTO's and 2 SomeOtherVO's. I
        // believe it
        // should only contain 2 SomeOtherVO's. It has something to do with specifying the field name starting with cap in
        // the mapping file. If
        // you change the field mapping to start with lower case it seems to map correctly.
        Mapper mapper = getMapper("mappings/setMappingWithUpperCaseFieldName.xml");
        SomeDTO someDto = newInstance(SomeDTO.class);
        someDto.setField1(new Integer("1"));
        SomeOtherDTO someOtherDto = newInstance(SomeOtherDTO.class);
        someOtherDto.setOtherField2(someDto);
        someOtherDto.setOtherField3("value1");
        SomeDTO someDto2 = newInstance(SomeDTO.class);
        someDto2.setField1(new Integer("2"));
        SomeOtherDTO someOtherDto2 = newInstance(SomeOtherDTO.class);
        someOtherDto2.setOtherField2(someDto2);
        someOtherDto2.setOtherField3("value2");
        SomeDTO src = newInstance(SomeDTO.class);
        src.setField2(new SomeOtherDTO[]{ someOtherDto2, someOtherDto });
        SomeVO dest = mapper.map(src, SomeVO.class);
        Assert.assertEquals("incorrect resulting set size", src.getField2().length, dest.getField2().size());
        // TODO: add more asserts
    }

    @Test
    public void testGlobalBeanFactoryAppliedToDefaultMappings() {
        mapper = getMapper("mappings/global-configuration.xml");
        TestObjectPrime dest = mapper.map(newInstance(TestObject.class), TestObjectPrime.class);
        Assert.assertNotNull("created by factory name should not be null", dest.getCreatedByFactoryName());
        Assert.assertEquals(SampleDefaultBeanFactory.class.getName(), dest.getCreatedByFactoryName());
    }

    @Test
    public void testStringToDateMapping() throws Exception {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS");
        String dateStr = "01/29/1975 10:45:13:25";
        TestObject sourceObj = newInstance(TestObject.class);
        sourceObj.setDateStr(dateStr);
        TestObjectPrime result = mapper.map(sourceObj, TestObjectPrime.class);
        Assert.assertEquals(df.parse(dateStr), result.getDateFromStr());
        Assert.assertEquals(dateStr, df.format(result.getDateFromStr()));
        TestObject result2 = mapper.map(result, TestObject.class);
        Assert.assertEquals(df.format(result.getDateFromStr()), result2.getDateStr());
        Assert.assertEquals(result.getDateFromStr(), df.parse(result2.getDateStr()));
    }

    @Test
    public void testMethodMapping() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        MethodFieldTestObject sourceObj = newInstance(MethodFieldTestObject.class);
        sourceObj.setIntegerStr("1500");
        sourceObj.setPriceItem("3500");
        sourceObj.setFieldOne("fieldOne");
        MethodFieldTestObject2 result = mapper.map(sourceObj, MethodFieldTestObject2.class);
        Assert.assertEquals("invalid result object size", 1, result.getIntegerList().size());
        Assert.assertEquals("invalid result object value", 3500, result.getTotalPrice());
        Assert.assertEquals("invalid result object value", "fieldOne", result.getFieldOne());
        // map back
        MethodFieldTestObject result2 = mapper.map(result, MethodFieldTestObject.class);
        // if no exceptions we thrown we are good. stopOnErrors = true. both values will be null
        // since this is a one-way mapping we shouldn't have a value
        Assert.assertNull(result2.getFieldOne());
    }

    @Test
    public void testNoReadMethod() {
        // If the field doesnt have a getter/setter, dont add it is a default field to be mapped.
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        NoReadMethod src = newInstance(NoReadMethod.class);
        src.setNoReadMethod("somevalue");
        NoReadMethodPrime dest = mapper.map(src, NoReadMethodPrime.class);
        Assert.assertNull("field should be null because no read method exists for field", dest.getXXXXX());
    }

    @Test
    public void testNoReadMethodSameClassTypes() {
        // If the field doesnt have a getter/setter, dont add it is a default field to be mapped.
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        NoReadMethod src = newInstance(NoReadMethod.class);
        src.setNoReadMethod("somevalue");
        NoReadMethod dest = mapper.map(src, NoReadMethod.class);
        Assert.assertNull("field should be null because no read method exists for field", dest.getXXXXX());
    }

    @Test
    public void testNoReadMethod_GetterOnlyWithParams() {
        // Dont use getter methods that have a param when discovering default fields to be mapped.
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        NoReadMethod src = newInstance(NoReadMethod.class);
        src.setOtherNoReadMethod("someValue");
        NoReadMethod dest = mapper.map(src, NoReadMethod.class);
        Assert.assertNull("field should be null because no read method exists for field", dest.getOtherNoReadMethod((-1)));
    }

    @Test
    public void testNoWriteMethod() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        NoWriteMethod src = newInstance(NoWriteMethod.class);
        src.setXXXXXX("someValue");
        NoWriteMethodPrime dest = mapper.map(src, NoWriteMethodPrime.class);
        Assert.assertNull("field should be null because no write method exists for field", dest.getNoWriteMethod());
    }

    @Test
    public void testNoWriteMethodSameClassTypes() {
        // When mapping between identical types, if the field doesnt have a getter/setter, dont
        // add it is a default field to be mapped.
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        NoWriteMethod src = newInstance(NoWriteMethod.class);
        src.setXXXXXX("someValue");
        mapper.map(newInstance(NoReadMethod.class), NoReadMethod.class);
        NoWriteMethod dest = mapper.map(src, NoWriteMethod.class);
        Assert.assertNull("field should be null because no write method exists for field", dest.getNoWriteMethod());
    }

    @Test
    public void testNoVoidSetters() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        NoVoidSetters src = newInstance(NoVoidSetters.class);
        src.setDescription("someValue");
        src.setI(1);
        NoVoidSetters dest = mapper.map(src, NoVoidSetters.class);
        Assert.assertEquals("invalid dest field value", src.getDescription(), dest.getDescription());
        Assert.assertEquals("invalid dest field value", src.getI(), dest.getI());
    }

    @Test
    public void testNullField() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        AnotherTestObject src = newInstance(AnotherTestObject.class);
        src.setField2(null);
        AnotherTestObjectPrime dest = newInstance(AnotherTestObjectPrime.class);
        dest.setField2(Integer.valueOf("555"));
        // check that null overrides an existing value
        mapper.map(src, dest);
        Assert.assertNull("dest field should be null", dest.getField2());
    }

    @Test
    public void testNullField2() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        // Test that String --> String with an empty String input value results
        // in the destination field being an empty String and not null.
        String input = "";
        TestObject src = newInstance(TestObject.class);
        src.setOne(input);
        TestObjectPrime dest = mapper.map(src, TestObjectPrime.class);
        Assert.assertNotNull("dest field should not be null", dest.getOnePrime());
        Assert.assertEquals("invalid dest field value", input, dest.getOnePrime());
    }

    @Test
    public void testNullToPrimitive() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        AnotherTestObject src = newInstance(AnotherTestObject.class);
        AnotherTestObjectPrime prime = newInstance(AnotherTestObjectPrime.class);
        TestObject to = newInstance(TestObject.class);
        to.setThePrimitive(AnotherTestObjectPrime.DEFAULT_FIELD1);
        prime.setTo(to);
        mapper.map(src, prime);
        // check primitive on deep field
        // primitive should still be default
        Assert.assertEquals("invalid field value", AnotherTestObjectPrime.DEFAULT_FIELD1, prime.getField1());
        Assert.assertEquals("invalid field value", AnotherTestObjectPrime.DEFAULT_FIELD1, prime.getTo().getThePrimitive());
    }

    @Test
    public void testGlobalRelationshipType() {
        mapper = getMapper("mappings/relationship-type-global-configuration.xml");
        TestObject src = new TestObject();
        src.setHintList(new ArrayList<>(Collections.singletonList("a")));
        TestObjectPrime dest = new TestObjectPrime();
        dest.setHintList(new ArrayList<>(Arrays.asList("a", "b")));
        mapper.map(src, dest);
        Assert.assertEquals("wrong # of elements in dest list for non-cumulative mapping", 2, dest.getHintList().size());
    }

    @Test
    public void testClassMapRelationshipType() {
        mapper = getMapper("mappings/relationshipTypeMapping.xml");
        TestObject src = new TestObject();
        src.setHintList(new ArrayList<>(Arrays.asList("a")));
        TestObjectPrime dest = new TestObjectPrime();
        dest.setHintList(new ArrayList<>(Arrays.asList("a", "b")));
        mapper.map(src, dest);
        Assert.assertEquals("wrong # of elements in dest list for non-cumulative mapping", 2, dest.getHintList().size());
    }

    @Test
    public void testRemoveOrphans() {
        mapper = getMapper("mappings/removeOrphansMapping.xml");
        MyClassA myClassA = new MyClassA();
        MyClassB myClassB = new MyClassB();
        Fruit apple = new Fruit();
        apple.setName("Apple");
        Fruit banana = new Fruit();
        banana.setName("Banana");
        Fruit grape = new Fruit();
        grape.setName("Grape");
        Fruit orange = new Fruit();
        orange.setName("Orange");
        Fruit kiwiFruit = new Fruit();
        kiwiFruit.setName("Kiwi Fruit");
        List<Fruit> srcFruits = new ArrayList<>();
        srcFruits.add(apple);
        srcFruits.add(banana);
        srcFruits.add(kiwiFruit);
        List<Fruit> destFruits = new ArrayList<>();
        destFruits.add(grape);// not in src

        destFruits.add(banana);// shared with src fruits

        destFruits.add(orange);// not in src

        myClassA.setAStringList(srcFruits);
        myClassB.setAStringList(destFruits);
        mapper.map(myClassA, myClassB, "testRemoveOrphansOnList");
        Assert.assertEquals(3, myClassB.getAStringList().size());
        Assert.assertTrue(myClassB.getAStringList().contains(apple));
        Assert.assertTrue(myClassB.getAStringList().contains(banana));
        Assert.assertTrue(myClassB.getAStringList().contains(kiwiFruit));
        Assert.assertFalse(myClassB.getAStringList().contains(grape));
        Assert.assertFalse(myClassB.getAStringList().contains(orange));
    }

    @Test
    public void testOrphanRemovalSet() {
        mapper = getMapper("mappings/removeOrphansMapping.xml");
        Parent parent = new Parent(new Long(1), "parent");
        Child child1 = new Child(new Long(1), "child1");
        Set<Child> childrenSet = new HashSet<>();
        childrenSet.add(child1);
        parent.setChildrenSet(childrenSet);
        ParentPrime parentPrime = mapper.map(parent, ParentPrime.class);
        // Make sure the first one was mapped ok.
        Assert.assertEquals(parent.getChildrenSet().size(), parentPrime.getChildrenSet().size());
        ChildPrime child2 = new ChildPrime(new Long(2L), "child2");
        parentPrime.getChildrenSet().add(child2);
        mapper.map(parentPrime, parent);
        // Make sure adding one works ok.
        Assert.assertEquals(parentPrime.getChildrenSet().size(), parent.getChildrenSet().size());
        parentPrime.getChildrenSet().clear();
        mapper.map(parentPrime, parent);
        // Make sure REMOVING them (the orphan children) works ok.
        Assert.assertEquals(parentPrime.getChildrenSet().size(), parent.getChildrenSet().size());
    }

    @Test
    public void testOrphanRemovalList() {
        mapper = getMapper("mappings/removeOrphansMapping.xml");
        Parent parent = new Parent(new Long(1), "parent");
        Child child1 = new Child(new Long(1), "child1");
        List<Child> childrenList = new ArrayList<>();
        childrenList.add(child1);
        parent.setChildrenList(childrenList);
        ParentPrime parentPrime = mapper.map(parent, ParentPrime.class);
        // Make sure the first one was mapped ok.
        Assert.assertEquals(parent.getChildrenList().size(), parentPrime.getChildrenList().size());
        ChildPrime child2 = new ChildPrime(new Long(2L), "child2");
        parentPrime.getChildrenList().add(child2);
        mapper.map(parentPrime, parent);
        // Make sure adding one works ok.
        Assert.assertEquals(parentPrime.getChildrenList().size(), parent.getChildrenList().size());
        parentPrime.getChildrenList().clear();
        mapper.map(parentPrime, parent);
        // Make sure REMOVING them (the orphan children) works ok.
        Assert.assertEquals(parentPrime.getChildrenList().size(), parent.getChildrenList().size());
    }
}

