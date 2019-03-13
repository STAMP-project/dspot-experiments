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
import com.github.dozermapper.core.vo.NoSuperClass;
import com.github.dozermapper.core.vo.SubClassPrime;
import com.github.dozermapper.core.vo.TheFirstSubClass;
import com.github.dozermapper.core.vo.inheritance.A;
import com.github.dozermapper.core.vo.inheritance.AnotherSubClass;
import com.github.dozermapper.core.vo.inheritance.AnotherSubClassPrime;
import com.github.dozermapper.core.vo.inheritance.B;
import com.github.dozermapper.core.vo.inheritance.BaseSubClass;
import com.github.dozermapper.core.vo.inheritance.BaseSubClassCombined;
import com.github.dozermapper.core.vo.inheritance.Main;
import com.github.dozermapper.core.vo.inheritance.MainDto;
import com.github.dozermapper.core.vo.inheritance.S2Class;
import com.github.dozermapper.core.vo.inheritance.S2ClassPrime;
import com.github.dozermapper.core.vo.inheritance.SClass;
import com.github.dozermapper.core.vo.inheritance.SClassPrime;
import com.github.dozermapper.core.vo.inheritance.Specific3;
import com.github.dozermapper.core.vo.inheritance.SpecificObject;
import com.github.dozermapper.core.vo.inheritance.SubClass;
import com.github.dozermapper.core.vo.inheritance.SubDto;
import com.github.dozermapper.core.vo.inheritance.SubMarker;
import com.github.dozermapper.core.vo.inheritance.SubMarkerDto;
import com.github.dozermapper.core.vo.inheritance.WrapperSpecific;
import com.github.dozermapper.core.vo.inheritance.WrapperSpecificPrime;
import com.github.dozermapper.core.vo.inheritance.iface.Person;
import com.github.dozermapper.core.vo.inheritance.iface.PersonDTO;
import com.github.dozermapper.core.vo.inheritance.iface.PersonImpl;
import com.github.dozermapper.core.vo.inheritance.iface.PersonWithAddressDTO;
import com.github.dozermapper.core.vo.km.Property;
import com.github.dozermapper.core.vo.km.PropertyB;
import com.github.dozermapper.core.vo.km.SomeVo;
import com.github.dozermapper.core.vo.km.Sub;
import com.github.dozermapper.core.vo.km.Super;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;


public class InheritanceMappingTest extends AbstractFunctionalTest {
    @Test
    public void testCustomMappingForSuperClasses() {
        // Test that the explicit super custom mapping definition is used when mapping sub classes
        mapper = getMapper("mappings/inheritanceMapping.xml");
        A src = getA();
        B dest = mapper.map(src, B.class);
        Assert.assertNull("superField1 should have been excluded", dest.getSuperField1());
        Assert.assertEquals("superBField not mapped correctly", src.getSuperAField(), dest.getSuperBField());
        Assert.assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
        Assert.assertEquals("fieldB not mapped correctly", src.getFieldA(), dest.getFieldB());
        // Remap to each other to test bi-directional mapping
        A mappedSrc = mapper.map(dest, A.class);
        B mappedDest = mapper.map(mappedSrc, B.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }

    @Test
    public void testNoCustomMappingForSuperClasses() {
        // Test that wildcard fields in super classes are mapped when there is no explicit super custom mapping definition
        mapper = DozerBeanMapperBuilder.buildDefault();
        A src = getA();
        B dest = mapper.map(src, B.class);
        Assert.assertEquals("superField1 not mapped correctly", src.getSuperField1(), dest.getSuperField1());
        Assert.assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
        Assert.assertNull("superBField should not have been mapped", dest.getSuperBField());
        Assert.assertNull("fieldB should not have been mapped", dest.getFieldB());
        // Remap to each other to test bi-directional mapping
        A mappedSrc = mapper.map(dest, A.class);
        B mappedDest = mapper.map(mappedSrc, B.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }

    @Test
    public void testNoCustomMappingForSuperClasses_SubclassAttrsAppliedToSuperClasses() {
        // Test that when there isnt an explicit super custom mapping definition the subclass mapping def attrs are
        // applied to the super class mapping. In this use case, wildcard="false" for the A --> B mapping definition
        mapper = getMapper("mappings/inheritanceMapping2.xml");
        A src = getA();
        B dest = mapper.map(src, B.class);
        Assert.assertNull("fieldB should not have been mapped", dest.getSuperField1());
        Assert.assertNull("superBField should have not been mapped", dest.getSuperBField());
        // Remap to each other to test bi-directional mapping
        A mappedSrc = mapper.map(dest, A.class);
        B mappedDest = mapper.map(mappedSrc, B.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }

    @Test
    public void testNoCustomMappingForSubclasses_CustomMappingForSuperClasses() {
        // Tests that custom mappings for super classes are used when there are no custom mappings
        // for subclasses. Also tests that a default class map is properly created and used for the subclass
        // field mappings
        mapper = getMapper("mappings/inheritanceMapping3.xml");
        A src = getA();
        B dest = mapper.map(src, B.class);
        Assert.assertNull("superField1 should have been excluded", dest.getSuperField1());
        Assert.assertEquals("superBField not mapped correctly", src.getSuperAField(), dest.getSuperBField());
        Assert.assertEquals("field1 not mapped correctly", src.getField1(), dest.getField1());
        // Remap to each other to test bi-directional mapping
        A mappedSrc = mapper.map(dest, A.class);
        B mappedDest = mapper.map(mappedSrc, B.class);
        Assert.assertEquals("objects not mapped correctly bi-directional", dest, mappedDest);
    }

    @Test
    public void testGeneralInheritance() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        // first test mapping of sub and base class to a single class
        SubClass sub = newInstance(SubClass.class);
        sub.setBaseAttribute("base");
        sub.setSubAttribute("sub");
        BaseSubClassCombined combined = mapper.map(sub, BaseSubClassCombined.class);
        Assert.assertEquals(sub.getSubAttribute(), combined.getSubAttribute2());
        Assert.assertEquals(sub.getBaseAttribute(), combined.getBaseAttribute2());
    }

    @Test
    public void testGeneralInheritance2() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        // test base to base and sub to sub mapping with an intermediate on the destination
        AnotherSubClass asub = newInstance(AnotherSubClass.class);
        asub.setBaseAttribute("base");
        asub.setSubAttribute("sub");
        List<BaseSubClass> list = newInstance(ArrayList.class);
        SClass s = newInstance(SClass.class);
        s.setBaseSubAttribute("sBase");
        s.setSubAttribute("s");
        S2Class s2 = newInstance(S2Class.class);
        s2.setBaseSubAttribute("s2Base");
        s2.setSub2Attribute("s2");
        list.add(s2);
        list.add(s);
        asub.setSubList(list);
        List<BaseSubClass> list2 = newInstance(ArrayList.class);
        SClass sclass = newInstance(SClass.class);
        sclass.setBaseSubAttribute("sBase");
        sclass.setSubAttribute("s");
        S2Class s2class = newInstance(S2Class.class);
        s2class.setBaseSubAttribute("s2Base");
        s2class.setSub2Attribute("s2");
        SClass sclass2 = newInstance(SClass.class);
        sclass2.setBaseSubAttribute("sclass2");
        sclass2.setSubAttribute("sclass2");
        list2.add(s2class);
        list2.add(sclass);
        list2.add(sclass2);
        asub.setListToArray(list2);
        SClass sclassA = newInstance(SClass.class);
        SClass sclassB = newInstance(SClass.class);
        sclassA.setBaseSubAttribute("sBase");
        sclassA.setSubAttribute("s");
        sclassB.setBaseSubAttribute("sBase");
        sclassB.setSubAttribute("s");
        asub.setSClass(sclassA);
        asub.setSClass2(sclassB);
        AnotherSubClassPrime subPrime = mapper.map(asub, AnotherSubClassPrime.class);
        Assert.assertEquals(asub.getSubAttribute(), subPrime.getSubAttribute2());
        Assert.assertEquals(asub.getBaseAttribute(), subPrime.getBaseAttribute2());
        Assert.assertTrue(((subPrime.getTheListOfSubClassPrime().get(0)) instanceof S2ClassPrime));
        Assert.assertTrue(((subPrime.getTheListOfSubClassPrime().get(1)) instanceof SClassPrime));
        Assert.assertEquals(s2.getSub2Attribute(), ((S2ClassPrime) (subPrime.getTheListOfSubClassPrime().get(0))).getSub2Attribute2());
        Assert.assertEquals(s2.getBaseSubAttribute(), ((S2ClassPrime) (subPrime.getTheListOfSubClassPrime().get(0))).getBaseSubAttribute2());
        Assert.assertEquals(s.getSubAttribute(), ((SClassPrime) (subPrime.getTheListOfSubClassPrime().get(1))).getSubAttribute2());
        Assert.assertEquals(s.getBaseSubAttribute(), ((SClassPrime) (subPrime.getTheListOfSubClassPrime().get(1))).getBaseSubAttribute2());
        Assert.assertTrue(((subPrime.getArrayToList()[0]) instanceof S2ClassPrime));
        Assert.assertTrue(((subPrime.getArrayToList()[1]) instanceof SClassPrime));
        Assert.assertTrue(((subPrime.getArrayToList()[2]) instanceof SClassPrime));
        Assert.assertEquals(s2class.getSub2Attribute(), ((S2ClassPrime) (subPrime.getArrayToList()[0])).getSub2Attribute2());
        Assert.assertEquals(s2class.getBaseSubAttribute(), ((S2ClassPrime) (subPrime.getArrayToList()[0])).getBaseSubAttribute2());
        Assert.assertEquals(sclass.getSubAttribute(), ((SClassPrime) (subPrime.getArrayToList()[1])).getSubAttribute2());
        Assert.assertEquals(sclass.getBaseSubAttribute(), ((SClassPrime) (subPrime.getArrayToList()[1])).getBaseSubAttribute2());
        Assert.assertEquals(sclass2.getSubAttribute(), ((SClassPrime) (subPrime.getArrayToList()[2])).getSubAttribute2());
        Assert.assertEquals(sclass2.getBaseSubAttribute(), ((SClassPrime) (subPrime.getArrayToList()[2])).getBaseSubAttribute2());
        Assert.assertEquals(asub.getSClass().getSubAttribute(), subPrime.getSClass().getSubAttribute2());
        Assert.assertEquals(asub.getSClass().getBaseSubAttribute(), subPrime.getSClass().getBaseSubAttribute2());
        Assert.assertEquals(asub.getSClass2().getSubAttribute(), subPrime.getSClass2().getSubAttribute2());
        Assert.assertEquals(asub.getSClass2().getBaseSubAttribute(), subPrime.getSClass2().getBaseSubAttribute2());
        // map it back
        AnotherSubClass sub = mapper.map(subPrime, AnotherSubClass.class);
        Assert.assertTrue(((sub.getSubList().get(0)) instanceof S2Class));
        Assert.assertTrue(((sub.getSubList().get(1)) instanceof SClass));
        Assert.assertEquals(s2.getSub2Attribute(), ((S2Class) (sub.getSubList().get(0))).getSub2Attribute());
        Assert.assertEquals(s2.getBaseSubAttribute(), ((S2Class) (sub.getSubList().get(0))).getBaseSubAttribute());
        Assert.assertEquals(s.getSubAttribute(), ((SClass) (sub.getSubList().get(1))).getSubAttribute());
        Assert.assertEquals(s.getBaseSubAttribute(), ((SClass) (sub.getSubList().get(1))).getBaseSubAttribute());
    }

    @Test
    public void testInheritanceWithAbstractClassOrInterfaceAsDestination() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        SpecificObject so = newInstance(SpecificObject.class);
        so.setSuperAttr1("superAttr1");
        // validate abstract class
        Object obj = mapper.map(so, Specific3.class);
        Assert.assertTrue((obj instanceof Specific3));
        Specific3 spec3 = ((Specific3) (obj));
        Assert.assertEquals("superAttr1", spec3.getSuperAttr3());
        Assert.assertEquals("superAttr1", spec3.getSuperAttr2());
        // validate interface
        obj = mapper.map(so, Specific3.class);
        Assert.assertTrue((obj instanceof Specific3));
        spec3 = ((Specific3) (obj));
        Assert.assertEquals("superAttr1", spec3.getSuperAttr3());
        Assert.assertEquals("superAttr1", spec3.getSuperAttr2());
        WrapperSpecific ws = newInstance(WrapperSpecific.class);
        SpecificObject so2 = newInstance(SpecificObject.class);
        so2.setSuperAttr1("superAttr1");
        ws.setSpecificObject(so2);
        WrapperSpecificPrime wsp = mapper.map(ws, WrapperSpecificPrime.class);
        Assert.assertTrue(((wsp.getSpecificObjectPrime()) instanceof Specific3));
        Assert.assertEquals("superAttr1", ((Specific3) (wsp.getSpecificObjectPrime())).getSuperAttr3());
        Assert.assertEquals("superAttr1", ((Specific3) (wsp.getSpecificObjectPrime())).getSuperAttr2());
    }

    @Test
    public void testComplexSuperClassMapping() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        com.github.dozermapper.core.vo.SubClass obj = testDataFactory.getSubClass();
        SubClassPrime objPrime = mapper.map(obj, SubClassPrime.class);
        com.github.dozermapper.core.vo.SubClass obj2 = mapper.map(objPrime, com.github.dozermapper.core.vo.SubClass.class);
        SubClassPrime objPrime2 = mapper.map(obj2, SubClassPrime.class);
        Assert.assertEquals(("" + (obj.getCustomConvert().getAttribute().getTheDouble())), ((obj2.getCustomConvert().getAttribute().getTheDouble()) + ""));
        // one-way mapping
        objPrime.setSuperFieldToExcludePrime(null);
        Assert.assertEquals(objPrime, objPrime2);
        // Pass by reference
        obj = testDataFactory.getSubClass();
        com.github.dozermapper.core.vo.SubClass subClassClone = SerializationUtils.clone(obj);
        objPrime = mapper.map(obj, SubClassPrime.class);
        mapper.map(objPrime, obj);
        obj.setCustomConvert(null);
        subClassClone.setCustomConvert(null);
        // more objects should be added to the clone from the ArrayList
        TheFirstSubClass fsc = newInstance(TheFirstSubClass.class);
        fsc.setS("s");
        subClassClone.getTestObject().getHintList().add(fsc);
        subClassClone.getTestObject().getHintList().add(fsc);
        subClassClone.getTestObject().getEqualNamedList().add("1value");
        subClassClone.getTestObject().getEqualNamedList().add("2value");
        int[] pa = new int[]{ 0, 1, 2, 3, 4, 0, 1, 2, 3, 4 };
        int[] intArray = new int[]{ 1, 1, 1, 1 };
        Integer[] integerArray = new Integer[]{ new Integer(1), new Integer(1), new Integer(1), new Integer(1) };
        subClassClone.getTestObject().setAnArray(intArray);
        subClassClone.getTestObject().setArrayForLists(integerArray);
        subClassClone.getTestObject().setPrimArray(pa);
        subClassClone.getTestObject().setBlankDate(null);
        subClassClone.getTestObject().setBlankStringToLong(null);
        subClassClone.getSuperList().add("one");
        subClassClone.getSuperList().add("two");
        subClassClone.getSuperList().add("three");
        // since we copy by reference the attribute copyByReference we need to null it out. The clone method above creates
        // two versions of it...
        // which is incorrect
        obj.getTestObject().setCopyByReference(null);
        subClassClone.getTestObject().setCopyByReference(null);
        obj.getTestObject().setCopyByReferenceDeep(null);
        subClassClone.getTestObject().setCopyByReferenceDeep(null);
        obj.getTestObject().setGlobalCopyByReference(null);
        subClassClone.getTestObject().setGlobalCopyByReference(null);
        // null out string array because we get NPE since a NULL value in the String []
        obj.getTestObject().setStringArrayWithNullValue(null);
        subClassClone.getTestObject().setStringArrayWithNullValue(null);
        subClassClone.getTestObject().setExcludeMeOneWay("excludeMeOneWay");
        Assert.assertEquals(subClassClone, obj);
    }

    @Test
    public void testSuperClassMapping() {
        // source object does not extend a base custom data object, but destination object extends a custom data object.
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        NoSuperClass src = newInstance(NoSuperClass.class);
        src.setAttribute("somefieldvalue");
        src.setSuperAttribute("someotherfieldvalue");
        com.github.dozermapper.core.vo.SubClass dest = mapper.map(src, com.github.dozermapper.core.vo.SubClass.class);
        NoSuperClass src1 = mapper.map(dest, NoSuperClass.class);
        com.github.dozermapper.core.vo.SubClass dest2 = mapper.map(src1, com.github.dozermapper.core.vo.SubClass.class);
        Assert.assertEquals(src, src1);
        Assert.assertEquals(dest, dest2);
    }

    /* Related to bug #1486105 */
    @Test
    public void testKM1() {
        SomeVo request = newInstance(SomeVo.class);
        request.setUserName("yo");
        request.setAge("2");
        request.setColor("blue");
        Mapper mapper = getMapper("mappings/kmmapping.xml");
        Super afterMapping = mapper.map(request, Super.class);
        Assert.assertNotNull("login name should not be null", afterMapping.getLoginName());
        Assert.assertNotNull("age should not be null", afterMapping.getAge());
        Assert.assertEquals("should map SuperClass.name to SubClassPrime userName.", request.getUserName(), afterMapping.getLoginName());
        Assert.assertEquals(request.getAge(), afterMapping.getAge());
    }

    /* Bug #1486105 and #1757573 */
    @Test
    public void testKM2() {
        Sub request = newInstance(Sub.class);
        request.setAge("2");
        request.setColor("blue");
        request.setLoginName("fred");
        Property property = newInstance(Property.class);
        PropertyB nestedProperty = newInstance(PropertyB.class);
        nestedProperty.setTestProperty("boo");
        property.setTestProperty("testProperty");
        property.setMapMe(nestedProperty);
        request.setProperty(property);
        Mapper mapper = getMapper("mappings/kmmapping.xml");
        SomeVo afterMapping = mapper.map(request, SomeVo.class);
        Assert.assertNotNull("un should not be null", afterMapping.getUserName());
        Assert.assertNotNull("color should not be null", afterMapping.getColor());
        Assert.assertNotNull("age should not be null", afterMapping.getAge());
        Assert.assertEquals("should map SuperClass.name to SubClassPrime userName.", request.getLoginName(), afterMapping.getUserName());
        Assert.assertEquals(request.getColor(), afterMapping.getColor());
        Assert.assertEquals(request.getAge(), afterMapping.getAge());
        Assert.assertEquals(request.getProperty().getMapMe().getTestProperty(), afterMapping.getProperty().getTestProperty());
        Assert.assertNull(afterMapping.getProperty().getMapMe());
    }

    @Test
    public void testInterfaceInheritance_GetterSetterAtDifferentLevels() {
        mapper = getMapper("mappings/inheritanceMapping.xml");
        Long id = new Long(100L);
        String name = "John";
        Person person = new PersonImpl(id, name);
        PersonDTO personDTO = mapper.map(person, PersonDTO.class);
        Assert.assertEquals("Person DTO has incorrect personId value", id, personDTO.getPersonId());
        Assert.assertNotNull("name should not be null", personDTO.getName());
        Assert.assertEquals("Person DTO has incorrect name value", name, personDTO.getName());
    }

    /**
     * Bug #1828693 -- Problem when using with proxies based on interfaces.
     */
    @Test
    public void testInheritance_UnevenHierarchies() {
        mapper = getMapper("mappings/inheritanceMapping.xml");
        Long id = new Long(100L);
        String name = "John";
        String address = "123 Main Street";
        final Person person = new PersonImpl(id, name);
        person.setAddress(address);
        PersonWithAddressDTO personWithAddressDTO = mapper.map(newInstance(new Class[]{ Person.class }, person), PersonWithAddressDTO.class);
        Assert.assertEquals("Person DTO has incorrect personId value", id, personWithAddressDTO.getPersonId());
        Assert.assertNotNull("name should not be null", personWithAddressDTO.getName());
        Assert.assertEquals("Person DTO has incorrect name value", name, personWithAddressDTO.getName());
        Assert.assertNotNull("addresss should not be null", personWithAddressDTO.getAddress());
        Assert.assertEquals("Person DTO has incorrect address value", address, personWithAddressDTO.getAddress());
    }

    @Test
    public void testSubMarker() {
        mapper = super.getMapper("mappings/inheritanceMapping.xml");
        Main src = testDataFactory.getMain();
        // Map to Dto
        MainDto dest = mapper.map(src, MainDto.class);
        Assert.assertNotNull(dest);
        Assert.assertEquals(src.getName(), dest.getName());
        SubMarker subMarker = src.getSub();
        SubMarkerDto subMarkerDto = dest.getSub();
        Assert.assertNotNull(subMarkerDto);
        Assert.assertNull(subMarkerDto.getSub());
        Assert.assertEquals(subMarker.getMarker(), subMarkerDto.getMarker());
        Assert.assertEquals(subMarker.getName(), subMarkerDto.getName());
        com.github.dozermapper.core.vo.inheritance.Sub sub = ((com.github.dozermapper.core.vo.inheritance.Sub) (subMarker));
        SubDto subDto = ((SubDto) (subMarkerDto));
        Assert.assertEquals(sub.getDetail(), subDto.getDetail());
        // Map back
        src = mapper.map(dest, Main.class);
        Assert.assertNotNull(dest);
        Assert.assertEquals(dest.getName(), src.getName());
        subMarker = src.getSub();
        Assert.assertNotNull(subMarker);
        Assert.assertNull(subMarker.getSub());
        Assert.assertNull(subMarker.getMarker());// One way mapping

        Assert.assertEquals(subMarkerDto.getName(), subMarker.getName());
        sub = ((com.github.dozermapper.core.vo.inheritance.Sub) (subMarker));
        Assert.assertEquals(subDto.getDetail(), sub.getDetail());
    }
}

