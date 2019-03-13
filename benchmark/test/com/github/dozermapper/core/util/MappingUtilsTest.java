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
package com.github.dozermapper.core.util;


import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.functional_tests.runner.ProxyDataObjectInstantiator;
import com.github.dozermapper.core.loader.MappingsParser;
import com.github.dozermapper.core.loader.xml.MappingFileReader;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.vo.enumtest.DestType;
import com.github.dozermapper.core.vo.enumtest.DestTypeWithOverride;
import com.github.dozermapper.core.vo.enumtest.SrcType;
import com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride;
import com.github.dozermapper.core.vo.interfacerecursion.AnotherLevelTwo;
import com.github.dozermapper.core.vo.interfacerecursion.AnotherLevelTwoImpl;
import com.github.dozermapper.core.vo.interfacerecursion.Base;
import com.github.dozermapper.core.vo.interfacerecursion.BaseImpl;
import com.github.dozermapper.core.vo.interfacerecursion.LevelOne;
import com.github.dozermapper.core.vo.interfacerecursion.LevelOneImpl;
import com.github.dozermapper.core.vo.interfacerecursion.LevelTwo;
import com.github.dozermapper.core.vo.interfacerecursion.LevelTwoImpl;
import com.github.dozermapper.core.vo.interfacerecursion.User;
import com.github.dozermapper.core.vo.interfacerecursion.UserSub;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.junit.Assert;
import org.junit.Test;


public class MappingUtilsTest extends AbstractDozerTest {
    private BeanContainer beanContainer = new BeanContainer();

    private DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);

    private PropertyDescriptorFactory propertyDescriptorFactory = new PropertyDescriptorFactory();

    @Test
    public void testIsBlankOrNull() {
        Assert.assertTrue(MappingUtils.isBlankOrNull(null));
        Assert.assertTrue(MappingUtils.isBlankOrNull(""));
        Assert.assertTrue(MappingUtils.isBlankOrNull(" "));
    }

    @Test
    public void testOverridenFields() {
        MappingFileReader fileReader = new MappingFileReader(new com.github.dozermapper.core.loader.xml.XMLParserFactory(beanContainer), new com.github.dozermapper.core.loader.xml.XMLParser(beanContainer, destBeanCreator, propertyDescriptorFactory), beanContainer);
        MappingFileData mappingFileData = fileReader.read("mappings/overridemapping.xml");
        MappingsParser mappingsParser = new MappingsParser(beanContainer, destBeanCreator, propertyDescriptorFactory);
        mappingsParser.processMappings(mappingFileData.getClassMaps(), mappingFileData.getConfiguration());
        // validate class mappings
        for (ClassMap classMap : mappingFileData.getClassMaps()) {
            if (classMap.getSrcClassToMap().getName().equals("com.github.dozermapper.core.util.mapping.vo.FurtherTestObject")) {
                Assert.assertTrue(classMap.isStopOnErrors());
            }
            if (classMap.getSrcClassToMap().getName().equals("com.github.dozermapper.core.util.mapping.vo.SuperSuperSuperClass")) {
                Assert.assertTrue(classMap.isWildcard());
            }
            if (classMap.getSrcClassToMap().getName().equals("com.github.dozermapper.core.util.mapping.vo.TestObject")) {
                Assert.assertTrue((!(classMap.getFieldMaps().get(0).isCopyByReference())));
            }
        }
    }

    @Test
    public void testGetClassWithoutPackage() {
        String result = MappingUtils.getClassNameWithoutPackage(String.class);
        Assert.assertNotNull("result should not be null", result);
        Assert.assertEquals("invalid result value", "String", result);
    }

    @Test(expected = MappingException.class)
    public void testThrowMappingException_MappingException() {
        MappingException ex = new MappingException(String.valueOf(System.currentTimeMillis()));
        MappingUtils.throwMappingException(ex);
        Assert.fail("should have thrown exception");
    }

    @Test(expected = NullPointerException.class)
    public void testThrowMappingException_RuntimeException() {
        // Runtime ex should not get wrapped in MappingException
        NullPointerException ex = new NullPointerException(String.valueOf(System.currentTimeMillis()));
        MappingUtils.throwMappingException(ex);
        Assert.fail("should have thrown exception");
    }

    @Test(expected = MappingException.class)
    public void testThrowMappingException_CheckedException() {
        // Checked exception should get wrapped in MappingException
        NoSuchFieldException ex = new NoSuchFieldException(String.valueOf(System.currentTimeMillis()));
        MappingUtils.throwMappingException(ex);
        Assert.fail("should have thrown exception");
    }

    @Test
    public void testGetRealClass() {
        Object proxyObj = ProxyDataObjectInstantiator.INSTANCE.newInstance(ArrayList.class);
        Assert.assertEquals(ArrayList.class, MappingUtils.getRealClass(proxyObj.getClass(), beanContainer));
        Assert.assertEquals(ArrayList.class, MappingUtils.getRealClass(ArrayList.class, beanContainer));
    }

    @Test
    public void testGetRealClass_CGLIBTarget() {
        Object proxyObj = ProxyDataObjectInstantiator.INSTANCE.newInstance(new Class[]{ List.class }, new ArrayList());
        Assert.assertSame(proxyObj.getClass(), MappingUtils.getRealClass(proxyObj.getClass(), beanContainer));
    }

    @Test
    public void testIsSupportedMap() {
        Assert.assertTrue(MappingUtils.isSupportedMap(Map.class));
        Assert.assertTrue(MappingUtils.isSupportedMap(HashMap.class));
        Assert.assertFalse(MappingUtils.isSupportedMap(String.class));
    }

    @Test
    public void testIsDeepMapping() {
        Assert.assertTrue(MappingUtils.isDeepMapping("a.b"));
        Assert.assertTrue(MappingUtils.isDeepMapping("."));
        Assert.assertTrue(MappingUtils.isDeepMapping("aa.bb.cc"));
        Assert.assertFalse(MappingUtils.isDeepMapping(null));
        Assert.assertFalse(MappingUtils.isDeepMapping(""));
        Assert.assertFalse(MappingUtils.isDeepMapping("aaa"));
    }

    @Test
    public void testPrepareIndexedCollection_Array() {
        String[] result = ((String[]) (MappingUtils.prepareIndexedCollection(String[].class, null, "some entry", 0)));
        Assert.assertTrue(Arrays.equals(new String[]{ "some entry" }, result));
        result = ((String[]) (MappingUtils.prepareIndexedCollection(String[].class, null, "some entry", 3)));
        Assert.assertTrue(Arrays.equals(new String[]{ null, null, null, "some entry" }, result));
        result = ((String[]) (MappingUtils.prepareIndexedCollection(String[].class, new String[]{ "a", "b", "c" }, "some entry", 5)));
        Assert.assertTrue(Arrays.equals(new String[]{ "a", "b", "c", null, null, "some entry" }, result));
    }

    @Test
    public void testPrepareIndexedCollection_List() {
        List<?> result = ((List<?>) (MappingUtils.prepareIndexedCollection(List.class, null, "some entry", 0)));
        Assert.assertEquals(Arrays.asList("some entry"), result);
        result = ((List<?>) (MappingUtils.prepareIndexedCollection(List.class, null, "some entry", 3)));
        Assert.assertEquals(Arrays.asList(null, null, null, "some entry"), result);
        result = ((List<?>) (MappingUtils.prepareIndexedCollection(List.class, Arrays.asList("a", "b", "c"), "some entry", 5)));
        Assert.assertEquals(Arrays.asList("a", "b", "c", null, null, "some entry"), result);
    }

    @Test
    public void testPrepareIndexedCollection_Vector() {
        Vector<?> result = ((Vector<?>) (MappingUtils.prepareIndexedCollection(Vector.class, null, "some entry", 0)));
        Assert.assertEquals(new Vector<>(Arrays.asList("some entry")), result);
        result = ((Vector<?>) (MappingUtils.prepareIndexedCollection(Vector.class, null, "some entry", 3)));
        Assert.assertEquals(new Vector<>(Arrays.asList(null, null, null, "some entry")), result);
        result = ((Vector<?>) (MappingUtils.prepareIndexedCollection(Vector.class, new Vector(Arrays.asList("a", "b", "c")), "some entry", 5)));
        Assert.assertEquals(new Vector<>(Arrays.asList("a", "b", "c", null, null, "some entry")), result);
    }

    @Test
    public void testPrepareIndexedCollection_ArrayResize() {
        String[] result = ((String[]) (MappingUtils.prepareIndexedCollection(String[].class, new String[]{ "a", "b" }, "some entry", 3)));
        Assert.assertTrue(Arrays.equals(new String[]{ "a", "b", null, "some entry" }, result));
    }

    @Test(expected = MappingException.class)
    public void testPrepareIndexedCollection_UnsupportedType() {
        MappingUtils.prepareIndexedCollection(String.class, null, "some entry", 0);
    }

    /**
     * Test for isEnumType(Class srcFieldClass, Class destFieldType) defined in MappingUtils
     */
    @Test
    public void testIsEnum() {
        Assert.assertTrue(MappingUtils.isEnumType(SrcType.class, DestType.class));
        Assert.assertTrue(MappingUtils.isEnumType(SrcType.FOO.getClass(), DestType.FOO.getClass()));
        Assert.assertTrue(MappingUtils.isEnumType(SrcTypeWithOverride.FOO.getClass(), DestType.FOO.getClass()));
        Assert.assertTrue(MappingUtils.isEnumType(SrcTypeWithOverride.FOO.getClass(), DestTypeWithOverride.FOO.getClass()));
        Assert.assertFalse(MappingUtils.isEnumType(SrcType.class, String.class));
        Assert.assertFalse(MappingUtils.isEnumType(String.class, SrcType.class));
    }

    @Test
    public void testLoadClass() {
        Assert.assertNotNull(MappingUtils.loadClass("java.lang.String", beanContainer));
        Assert.assertNotNull(MappingUtils.loadClass("java.lang.String[]", beanContainer));
        Assert.assertNotNull(MappingUtils.loadClass("[Ljava.lang.String;", beanContainer));
    }

    @Test
    public void testGetDeepInterfaces() {
        testGetDeepInterfaces(Base.class);
        testGetDeepInterfaces(LevelOne.class, Base.class, User.class);
        testGetDeepInterfaces(LevelTwo.class, LevelOne.class, User.class, Base.class);
        testGetDeepInterfaces(AnotherLevelTwo.class, LevelOne.class, UserSub.class, Base.class, User.class);
        testGetDeepInterfaces(BaseImpl.class, Base.class);
        testGetDeepInterfaces(LevelOneImpl.class, LevelOne.class, Serializable.class, Base.class, User.class);
        testGetDeepInterfaces(LevelTwoImpl.class, LevelTwo.class, LevelOne.class, User.class, Base.class);
        testGetDeepInterfaces(AnotherLevelTwoImpl.class, AnotherLevelTwo.class, LevelOne.class, UserSub.class, Base.class, User.class);
    }

    @Test
    public void testGetSuperClasses() {
        testGetSuperClasses(BaseImpl.class, Base.class);
        testGetSuperClasses(LevelOneImpl.class, BaseImpl.class, LevelOne.class, Serializable.class, Base.class, User.class);
        testGetSuperClasses(LevelTwoImpl.class, LevelTwo.class, LevelOne.class, User.class, Base.class);
        testGetSuperClasses(AnotherLevelTwoImpl.class, LevelOneImpl.class, BaseImpl.class, AnotherLevelTwo.class, LevelOne.class, UserSub.class, Base.class, User.class, Serializable.class);
    }
}

