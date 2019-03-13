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


import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.vo.Apple;
import com.github.dozermapper.core.vo.CustomConverterWrapper;
import com.github.dozermapper.core.vo.CustomConverterWrapperPrime;
import com.github.dozermapper.core.vo.DehydrateTestObject;
import com.github.dozermapper.core.vo.FurtherTestObject;
import com.github.dozermapper.core.vo.FurtherTestObjectPrime;
import com.github.dozermapper.core.vo.HintedOnly;
import com.github.dozermapper.core.vo.HydrateTestObject;
import com.github.dozermapper.core.vo.NoCustomMappingsObject;
import com.github.dozermapper.core.vo.NoCustomMappingsObjectPrime;
import com.github.dozermapper.core.vo.OneWayObject;
import com.github.dozermapper.core.vo.OneWayObjectPrime;
import com.github.dozermapper.core.vo.Orange;
import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.TestObjectPrime;
import com.github.dozermapper.core.vo.TheFirstSubClass;
import com.github.dozermapper.core.vo.Van;
import com.github.dozermapper.core.vo.WeirdGetter;
import com.github.dozermapper.core.vo.WeirdGetter2;
import com.github.dozermapper.core.vo.WeirdGetterPrime;
import com.github.dozermapper.core.vo.WeirdGetterPrime2;
import com.github.dozermapper.core.vo.deep.HomeDescription;
import com.github.dozermapper.core.vo.deep.House;
import com.github.dozermapper.core.vo.deep.Room;
import com.github.dozermapper.core.vo.deep.SrcNestedDeepObj;
import com.github.dozermapper.core.vo.self.Account;
import com.github.dozermapper.core.vo.self.SimpleAccount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;


public class MapperTest extends AbstractFunctionalTest {
    @Test
    public void testNoSourceValueIterateFieldMap() {
        DehydrateTestObject inputDto = newInstance(DehydrateTestObject.class);
        HydrateTestObject hto = mapper.map(inputDto, HydrateTestObject.class);
        Assert.assertEquals(testDataFactory.getExpectedTestNoSourceValueIterateFieldMapHydrateTestObject(), hto);
    }

    @Test
    public void testCustomGetterSetterMap() {
        WeirdGetter inputDto = newInstance(WeirdGetter.class);
        inputDto.placeValue("theValue");
        inputDto.setWildValue("wild");
        WeirdGetterPrime prime = mapper.map(inputDto, WeirdGetterPrime.class);
        Assert.assertNull(prime.getWildValue());// testing global wildcard, expect this to be null

        Assert.assertEquals(inputDto.buildValue(), prime.getValue());
        inputDto = mapper.map(prime, WeirdGetter.class);
        Assert.assertEquals(inputDto.buildValue(), prime.getValue());
        WeirdGetterPrime2 inputDto2 = newInstance(WeirdGetterPrime2.class);
        inputDto2.placeValue("theValue");
        WeirdGetter2 prime2 = mapper.map(inputDto2, WeirdGetter2.class);
        Assert.assertEquals(inputDto2.buildValue(), prime2.getValue());
        inputDto2 = mapper.map(prime2, WeirdGetterPrime2.class);
        Assert.assertEquals(inputDto2.buildValue(), prime2.getValue());
    }

    @Test
    public void testNoClassMappings() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        // Should attempt mapping even though it is not in the beanmapping.xml file
        NoCustomMappingsObjectPrime dest1 = mapper.map(testDataFactory.getInputTestNoClassMappingsNoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
        NoCustomMappingsObject source = mapper.map(dest1, NoCustomMappingsObject.class);
        NoCustomMappingsObjectPrime dest3 = mapper.map(source, NoCustomMappingsObjectPrime.class);
        Assert.assertEquals(dest1, dest3);
    }

    @Test
    public void testImplicitInnerObject() {
        // This tests that we implicitly map an inner object to an inner object without defining it in the mapping file
        TestObject to = newInstance(TestObject.class);
        to.setNoMappingsObj(testDataFactory.getInputTestNoClassMappingsNoCustomMappingsObject());
        TestObjectPrime dest2 = mapper.map(to, TestObjectPrime.class);
        TestObject source2 = mapper.map(dest2, TestObject.class);
        TestObjectPrime dest4 = mapper.map(source2, TestObjectPrime.class);
        Assert.assertEquals(dest2, dest4);
    }

    @Test
    public void testMapField() {
        NoCustomMappingsObjectPrime dest = mapper.map(testDataFactory.getInputTestMapFieldWithMapNoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
        NoCustomMappingsObject source = mapper.map(dest, NoCustomMappingsObject.class);
        NoCustomMappingsObjectPrime dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
        Assert.assertEquals(dest2, dest);
        dest = mapper.map(new NoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
        source = mapper.map(dest, NoCustomMappingsObject.class);
        dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
        Assert.assertEquals(dest2, dest);
        // empty Map
        dest = mapper.map(testDataFactory.getInputTestMapFieldWithEmptyMapNoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
        source = mapper.map(dest, NoCustomMappingsObject.class);
        dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
        Assert.assertEquals(dest2, dest);
    }

    @Test
    public void testSetField() {
        // basic set --> set
        NoCustomMappingsObjectPrime dest = mapper.map(testDataFactory.getInputTestSetFieldWithSetNoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
        NoCustomMappingsObject source = mapper.map(dest, NoCustomMappingsObject.class);
        NoCustomMappingsObjectPrime dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
        Assert.assertEquals(dest2, dest);
        // null set --> set
        dest = mapper.map(new NoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
        source = mapper.map(dest, NoCustomMappingsObject.class);
        dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
        Assert.assertEquals(dest2, dest);
        // empty set --> set
        dest = mapper.map(testDataFactory.getInputTestSetFieldWithSetEmptyCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
        source = mapper.map(dest, NoCustomMappingsObject.class);
        dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
        Assert.assertEquals(dest2, dest);
        // complex type set -->
        dest = mapper.map(testDataFactory.getInputTestSetFieldComplexSetNoCustomMappingsObject(), NoCustomMappingsObjectPrime.class);
        source = mapper.map(dest, NoCustomMappingsObject.class);
        dest2 = mapper.map(source, NoCustomMappingsObjectPrime.class);
        Assert.assertEquals(dest2, dest);
    }

    @Test
    public void testListField() {
        // test empty list --> empty list
        TestObjectPrime dest = mapper.map(testDataFactory.getInputTestListFieldEmptyListTestObject(), TestObjectPrime.class);
        TestObject source = mapper.map(dest, TestObject.class);
        TestObjectPrime dest2 = mapper.map(source, TestObjectPrime.class);
        Assert.assertEquals(dest2, dest);
        // test empty array --> empty list
        dest = mapper.map(testDataFactory.getInputTestListFieldArrayListTestObject(), TestObjectPrime.class);
        source = mapper.map(dest, TestObject.class);
        dest2 = mapper.map(source, TestObjectPrime.class);
        Assert.assertEquals(dest2, dest);
    }

    @Test
    public void testListUsingDestHint() {
        TestObjectPrime dest = mapper.map(testDataFactory.getInputTestListUsingDestHintTestObject(), TestObjectPrime.class);
        TestObject source = mapper.map(dest, TestObject.class);
        TestObjectPrime dest2 = mapper.map(source, TestObjectPrime.class);
        Assert.assertEquals(dest, dest2);
    }

    @Test
    public void testExcludeFields() {
        // Map
        TestObjectPrime prime = mapper.map(testDataFactory.getInputGeneralMappingTestObject(), TestObjectPrime.class);
        Assert.assertEquals("excludeMe", prime.getExcludeMe());
        Assert.assertEquals("excludeMeOneWay", prime.getExcludeMeOneWay());
        // map back
        TestObject to = mapper.map(prime, TestObject.class);
        Assert.assertNull(to.getExcludeMe());
        Assert.assertEquals("excludeMeOneWay", to.getExcludeMeOneWay());
    }

    @Test
    public void testGeneralMapping() {
        // Map
        TestObject to = testDataFactory.getInputGeneralMappingTestObject();
        TestObjectPrime prime = mapper.map(to, TestObjectPrime.class);
        // valdidate that we copied by object reference -
        TestObject source = mapper.map(prime, TestObject.class);
        TestObjectPrime prime2 = mapper.map(source, TestObjectPrime.class);
        Assert.assertEquals(prime2, prime);
    }

    @Test
    public void testMappingNoDestSpecified() {
        // Map
        House src = testDataFactory.getHouse();
        HomeDescription dest = mapper.map(src, HomeDescription.class);
        House src2 = mapper.map(dest, House.class);
        HomeDescription dest2 = mapper.map(src2, HomeDescription.class);
        long[] prim = new long[]{ 1, 2, 3, 1, 2, 3 };
        // cumulative relationship
        dest.setPrim(prim);
        Assert.assertEquals(dest, dest2);
        // By reference
        src = testDataFactory.getHouse();
        House houseClone = SerializationUtils.clone(src);
        dest = mapper.map(src, HomeDescription.class);
        mapper.map(dest, House.class);
        Assert.assertEquals(houseClone, src);
    }

    @Test
    public void testGeneralMappingPassByReference() {
        // Map
        TestObject to = testDataFactory.getInputGeneralMappingTestObject();
        TestObject toClone = SerializationUtils.clone(to);
        TestObjectPrime prime = mapper.map(to, TestObjectPrime.class);
        mapper.map(prime, to);
        // more objects should be added to the clone from the ArrayList
        TheFirstSubClass fsc = new TheFirstSubClass();
        fsc.setS("s");
        toClone.getHintList().add(fsc);
        toClone.getHintList().add(fsc);
        toClone.getEqualNamedList().add("1value");
        toClone.getEqualNamedList().add("2value");
        int[] pa = new int[]{ 0, 1, 2, 3, 4, 0, 1, 2, 3, 4 };
        int[] intArray = new int[]{ 1, 1, 1, 1 };
        Integer[] integerArray = new Integer[]{ new Integer(1), new Integer(1), new Integer(1), new Integer(1) };
        toClone.setAnArray(intArray);
        toClone.setArrayForLists(integerArray);
        toClone.setPrimArray(pa);
        toClone.setBlankDate(null);
        toClone.setBlankStringToLong(null);
        // since we copy by reference the attribute copyByReference we need to null it out. The clone method above creates
        // two versions of it...
        // which is incorrect
        to.setCopyByReference(null);
        toClone.setCopyByReference(null);
        to.setCopyByReferenceDeep(null);
        toClone.setCopyByReferenceDeep(null);
        to.setGlobalCopyByReference(null);
        toClone.setGlobalCopyByReference(null);
        // null out string array because we get NPE since a NULL value in the String []
        to.setStringArrayWithNullValue(null);
        toClone.setStringArrayWithNullValue(null);
        toClone.setExcludeMeOneWay("excludeMeOneWay");
        Assert.assertEquals(toClone, to);
    }

    @Test
    public void testLongToLongMapping() {
        // Map
        TestObject source = testDataFactory.getInputGeneralMappingTestObject();
        source.setAnotherLongValue(42);
        TestObjectPrime prime2 = mapper.map(source, TestObjectPrime.class);
        Long value = prime2.getTheLongValue();
        Assert.assertEquals(value.longValue(), 42);
    }

    @Test
    public void testNoWildcards() {
        // Map
        FurtherTestObjectPrime prime = mapper.map(testDataFactory.getInputTestNoWildcardsFurtherTestObject(), FurtherTestObjectPrime.class);
        FurtherTestObject source = mapper.map(prime, FurtherTestObject.class);
        FurtherTestObjectPrime prime2 = mapper.map(source, FurtherTestObjectPrime.class);
        Assert.assertEquals(prime2, prime);
    }

    @Test
    public void testHydrateAndMore() {
        HydrateTestObject dest = mapper.map(testDataFactory.getInputTestHydrateAndMoreDehydrateTestObject(), HydrateTestObject.class);
        // validate results
        Assert.assertEquals(testDataFactory.getExpectedTestHydrateAndMoreHydrateTestObject(), dest);
        // map it back
        DehydrateTestObject dhto = mapper.map(testDataFactory.getInputTestHydrateAndMoreHydrateTestObject(), DehydrateTestObject.class);
        Assert.assertEquals(testDataFactory.getExpectedTestHydrateAndMoreDehydrateTestObject(), dhto);
    }

    @Test
    public void testDeepProperties() {
        House src = testDataFactory.getHouse();
        HomeDescription dest = mapper.map(src, HomeDescription.class);
        House src2 = mapper.map(dest, House.class);
        HomeDescription dest2 = mapper.map(src2, HomeDescription.class);
        long[] prim = new long[]{ 1, 2, 3, 1, 2, 3 };
        // cumulative relationship
        dest.setPrim(prim);
        Assert.assertEquals(dest, dest2);
        // By reference
        src = testDataFactory.getHouse();
        House houseClone = SerializationUtils.clone(src);
        dest = mapper.map(src, HomeDescription.class);
        mapper.map(dest, src);
        // cumulative relationship
        int[] prims = new int[]{ 1, 2, 3, 1, 2, 3 };
        houseClone.getOwner().setPrim(prims);
        // add two more rooms
        Room room1 = new Room();
        room1.setName("Living");
        Room room2 = new Room();
        room2.setName("kitchen");
        Van van = new Van();
        van.setName("van2");
        houseClone.getRooms().add(room1);
        houseClone.getRooms().add(room2);
        houseClone.getCustomSetGetMethod().add(van);
        Assert.assertEquals(houseClone, src);
    }

    @Test
    public void testOneWayMapping() {
        // Map
        OneWayObject owo = newInstance(OneWayObject.class);
        OneWayObjectPrime owop = newInstance(OneWayObjectPrime.class);
        SrcNestedDeepObj nested = newInstance(SrcNestedDeepObj.class);
        nested.setSrc1("src1");
        owo.setNested(nested);
        owop.setOneWayPrimeField("oneWayField");
        owop.setSetOnlyField("setOnly");
        List<String> list = new ArrayList<>();
        list.add("stringToList");
        list.add("src1");
        owop.setStringList(list);
        owo.setOneWayField("oneWayField");
        owo.setStringToList("stringToList");
        OneWayObjectPrime prime = mapper.map(owo, OneWayObjectPrime.class);
        Assert.assertEquals(owop, prime);
        OneWayObject source = mapper.map(prime, OneWayObject.class);
        // should have not mapped this way
        Assert.assertEquals(null, source.getOneWayField());
    }

    @Test
    public void testHintedOnlyConverter() {
        String hintStr = "where's my hint?";
        CustomConverterWrapper source = newInstance(CustomConverterWrapper.class);
        HintedOnly hint = newInstance(HintedOnly.class);
        hint.setStr(hintStr);
        source.addHint(hint);
        CustomConverterWrapperPrime dest = mapper.map(source, CustomConverterWrapperPrime.class);
        String destHintStr = ((String) (dest.getNeedsHint().iterator().next()));
        Assert.assertNotNull(destHintStr);
        Assert.assertEquals(hintStr, destHintStr);
        CustomConverterWrapper sourcePrime = mapper.map(dest, CustomConverterWrapper.class);
        String sourcePrimeHintStr = ((HintedOnly) (sourcePrime.getNeedsHint().iterator().next())).getStr();
        Assert.assertNotNull(sourcePrimeHintStr);
        Assert.assertEquals(hintStr, sourcePrimeHintStr);
    }

    @Test
    public void testSelfMapping() {
        SimpleAccount simpleAccount = newInstance(SimpleAccount.class);
        simpleAccount.setName("name");
        simpleAccount.setPostcode(1234);
        simpleAccount.setStreetName("streetName");
        simpleAccount.setSuburb("suburb");
        Account account = mapper.map(simpleAccount, Account.class);
        Assert.assertEquals(account.getAddress().getStreet(), simpleAccount.getStreetName());
        Assert.assertEquals(account.getAddress().getSuburb(), simpleAccount.getSuburb());
        Assert.assertEquals(account.getAddress().getPostcode(), simpleAccount.getPostcode());
        // try mapping back
        SimpleAccount dest = mapper.map(account, SimpleAccount.class);
        Assert.assertEquals(account.getAddress().getStreet(), dest.getStreetName());
        Assert.assertEquals(account.getAddress().getSuburb(), dest.getSuburb());
        Assert.assertEquals(account.getAddress().getPostcode(), dest.getPostcode());
    }

    @Test
    public void testSetToArray() {
        Orange orange1 = newInstance(Orange.class);
        orange1.setName("orange1");
        Orange orange2 = newInstance(Orange.class);
        orange2.setName("orange2");
        Orange orange3 = newInstance(Orange.class);
        orange3.setName("orange3");
        Orange orange4 = newInstance(Orange.class);
        orange4.setName("orange4");
        Set<Orange> set = newInstance(HashSet.class);
        set.add(orange1);
        set.add(orange2);
        Set<Orange> set2 = newInstance(HashSet.class);
        set2.add(orange3);
        set2.add(orange4);
        TestObject to = newInstance(TestObject.class);
        to.setSetToArray(set);
        to.setSetToObjectArray(set2);
        TestObjectPrime top = mapper.map(to, TestObjectPrime.class);
        Set<String> fruitNames = new HashSet<>();
        fruitNames.add(top.getArrayToSet()[0].getName());
        fruitNames.add(top.getArrayToSet()[1].getName());
        Assert.assertTrue(fruitNames.remove("orange1"));
        Assert.assertTrue(fruitNames.remove("orange2"));
        fruitNames.add(((Apple) (top.getObjectArrayToSet()[0])).getName());
        fruitNames.add(((Apple) (top.getObjectArrayToSet()[1])).getName());
        Assert.assertTrue(fruitNames.remove("orange3"));
        Assert.assertTrue(fruitNames.remove("orange4"));
        Apple apple = newInstance(Apple.class);
        apple.setName("apple1");
        Apple[] appleArray = new Apple[]{ apple };
        top.setSetToArrayWithValues(appleArray);
        // now map back
        Apple apple2 = newInstance(Apple.class);
        apple2.setName("apple2");
        TestObject toDest = newInstance(TestObject.class);
        Set<Apple> hashSet = newInstance(HashSet.class);
        hashSet.add(apple2);
        toDest.setSetToArrayWithValues(hashSet);
        mapper.map(top, toDest);
        Assert.assertTrue(toDest.getSetToArray().contains(top.getArrayToSet()[0]));
        Assert.assertTrue(toDest.getSetToArray().contains(top.getArrayToSet()[1]));
        Assert.assertTrue(toDest.getSetToObjectArray().contains(top.getObjectArrayToSet()[0]));
        Assert.assertTrue(toDest.getSetToObjectArray().contains(top.getObjectArrayToSet()[1]));
        Assert.assertTrue(toDest.getSetToArrayWithValues().contains(apple));
        Assert.assertTrue(toDest.getSetToArrayWithValues().contains(apple2));
        Assert.assertTrue(((toDest.getSetToArrayWithValues()) instanceof HashSet));
    }

    @Test
    public void testSetToList() {
        Orange orange1 = newInstance(Orange.class);
        orange1.setName("orange1");
        Orange orange2 = newInstance(Orange.class);
        orange2.setName("orange2");
        Set<Orange> set = newInstance(HashSet.class);
        set.add(orange1);
        set.add(orange2);
        TestObject to = newInstance(TestObject.class);
        to.setSetToList(set);
        TestObjectPrime top = mapper.map(to, TestObjectPrime.class);
        Assert.assertEquals("orange1", ((Orange) (top.getListToSet().get(0))).getName());
        Assert.assertEquals("orange2", ((Orange) (top.getListToSet().get(1))).getName());
        List<Orange> list = newInstance(ArrayList.class);
        Orange orange4 = newInstance(Orange.class);
        orange4.setName("orange4");
        list.add(orange4);
        top.setSetToListWithValues(list);
        // Map back
        Orange orange3 = newInstance(Orange.class);
        orange3.setName("orange3");
        Set<Orange> set2 = newInstance(HashSet.class);
        set2.add(orange3);
        set2.add(orange4);
        TestObject toDest = newInstance(TestObject.class);
        toDest.setSetToListWithValues(set2);
        mapper.map(top, toDest);
        Assert.assertTrue(toDest.getSetToList().contains(top.getListToSet().get(0)));
        Assert.assertTrue(toDest.getSetToList().contains(top.getListToSet().get(1)));
        Assert.assertTrue(toDest.getSetToListWithValues().contains(orange3));
        Assert.assertTrue(toDest.getSetToListWithValues().contains(orange4));
    }

    // one way
    @Test
    public void testMapValuesToList() {
        Orange orange1 = newInstance(Orange.class);
        orange1.setName("orange1");
        Orange orange2 = newInstance(Orange.class);
        orange2.setName("orange2");
        Map<String, Orange> map = newInstance(HashMap.class);
        map.put(orange1.getName(), orange1);
        map.put(orange2.getName(), orange2);
        TestObject to = newInstance(TestObject.class);
        to.setCollectionToList(map.values());
        TestObjectPrime top = mapper.map(to, TestObjectPrime.class);
        Assert.assertTrue(top.getListToCollection().contains(orange1));
        Assert.assertTrue(top.getListToCollection().contains(orange2));
    }
}

