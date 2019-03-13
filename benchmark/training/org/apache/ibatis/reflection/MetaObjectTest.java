/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.reflection;


import SystemMetaObject.DEFAULT_OBJECT_FACTORY;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.domain.blog.Section;
import org.apache.ibatis.domain.misc.CustomBeanWrapper;
import org.apache.ibatis.domain.misc.CustomBeanWrapperFactory;
import org.apache.ibatis.domain.misc.RichType;
import org.junit.Assert;
import org.junit.Test;


public class MetaObjectTest {
    @Test
    public void shouldGetAndSetField() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richField", "foo");
        Assert.assertEquals("foo", meta.getValue("richField"));
    }

    @Test
    public void shouldGetAndSetNestedField() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richType.richField", "foo");
        Assert.assertEquals("foo", meta.getValue("richType.richField"));
    }

    @Test
    public void shouldGetAndSetProperty() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richProperty", "foo");
        Assert.assertEquals("foo", meta.getValue("richProperty"));
    }

    @Test
    public void shouldGetAndSetNestedProperty() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richType.richProperty", "foo");
        Assert.assertEquals("foo", meta.getValue("richType.richProperty"));
    }

    @Test
    public void shouldGetAndSetMapPair() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richMap.key", "foo");
        Assert.assertEquals("foo", meta.getValue("richMap.key"));
    }

    @Test
    public void shouldGetAndSetMapPairUsingArraySyntax() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richMap[key]", "foo");
        Assert.assertEquals("foo", meta.getValue("richMap[key]"));
    }

    @Test
    public void shouldGetAndSetNestedMapPair() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richType.richMap.key", "foo");
        Assert.assertEquals("foo", meta.getValue("richType.richMap.key"));
    }

    @Test
    public void shouldGetAndSetNestedMapPairUsingArraySyntax() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richType.richMap[key]", "foo");
        Assert.assertEquals("foo", meta.getValue("richType.richMap[key]"));
    }

    @Test
    public void shouldGetAndSetListItem() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richList[0]", "foo");
        Assert.assertEquals("foo", meta.getValue("richList[0]"));
    }

    @Test
    public void shouldSetAndGetSelfListItem() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richList[0]", "foo");
        Assert.assertEquals("foo", meta.getValue("richList[0]"));
    }

    @Test
    public void shouldGetAndSetNestedListItem() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        meta.setValue("richType.richList[0]", "foo");
        Assert.assertEquals("foo", meta.getValue("richType.richList[0]"));
    }

    @Test
    public void shouldGetReadablePropertyNames() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        String[] readables = meta.getGetterNames();
        Assert.assertEquals(5, readables.length);
        for (String readable : readables) {
            Assert.assertTrue(meta.hasGetter(readable));
            Assert.assertTrue(meta.hasGetter(("richType." + readable)));
        }
        Assert.assertTrue(meta.hasGetter("richType"));
    }

    @Test
    public void shouldGetWriteablePropertyNames() {
        RichType rich = new RichType();
        MetaObject meta = SystemMetaObject.forObject(rich);
        String[] writeables = meta.getSetterNames();
        Assert.assertEquals(5, writeables.length);
        for (String writeable : writeables) {
            Assert.assertTrue(meta.hasSetter(writeable));
            Assert.assertTrue(meta.hasSetter(("richType." + writeable)));
        }
        Assert.assertTrue(meta.hasSetter("richType"));
    }

    @Test
    public void shouldSetPropertyOfNullNestedProperty() {
        MetaObject richWithNull = SystemMetaObject.forObject(new RichType());
        richWithNull.setValue("richType.richProperty", "foo");
        Assert.assertEquals("foo", richWithNull.getValue("richType.richProperty"));
    }

    @Test
    public void shouldSetPropertyOfNullNestedPropertyWithNull() {
        MetaObject richWithNull = SystemMetaObject.forObject(new RichType());
        richWithNull.setValue("richType.richProperty", null);
        Assert.assertEquals(null, richWithNull.getValue("richType.richProperty"));
    }

    @Test
    public void shouldGetPropertyOfNullNestedProperty() {
        MetaObject richWithNull = SystemMetaObject.forObject(new RichType());
        Assert.assertNull(richWithNull.getValue("richType.richProperty"));
    }

    @Test
    public void shouldVerifyHasReadablePropertiesReturnedByGetReadablePropertyNames() {
        MetaObject object = SystemMetaObject.forObject(new Author());
        for (String readable : object.getGetterNames()) {
            Assert.assertTrue(object.hasGetter(readable));
        }
    }

    @Test
    public void shouldVerifyHasWriteablePropertiesReturnedByGetWriteablePropertyNames() {
        MetaObject object = SystemMetaObject.forObject(new Author());
        for (String writeable : object.getSetterNames()) {
            Assert.assertTrue(object.hasSetter(writeable));
        }
    }

    @Test
    public void shouldSetAndGetProperties() {
        MetaObject object = SystemMetaObject.forObject(new Author());
        object.setValue("email", "test");
        Assert.assertEquals("test", object.getValue("email"));
    }

    @Test
    public void shouldVerifyPropertyTypes() {
        MetaObject object = SystemMetaObject.forObject(new Author());
        Assert.assertEquals(6, object.getSetterNames().length);
        Assert.assertEquals(int.class, object.getGetterType("id"));
        Assert.assertEquals(String.class, object.getGetterType("username"));
        Assert.assertEquals(String.class, object.getGetterType("password"));
        Assert.assertEquals(String.class, object.getGetterType("email"));
        Assert.assertEquals(String.class, object.getGetterType("bio"));
        Assert.assertEquals(Section.class, object.getGetterType("favouriteSection"));
    }

    @Test
    public void shouldDemonstrateDeeplyNestedMapProperties() {
        HashMap<String, String> map = new HashMap<String, String>();
        MetaObject metaMap = SystemMetaObject.forObject(map);
        Assert.assertTrue(metaMap.hasSetter("id"));
        Assert.assertTrue(metaMap.hasSetter("name.first"));
        Assert.assertTrue(metaMap.hasSetter("address.street"));
        Assert.assertFalse(metaMap.hasGetter("id"));
        Assert.assertFalse(metaMap.hasGetter("name.first"));
        Assert.assertFalse(metaMap.hasGetter("address.street"));
        metaMap.setValue("id", "100");
        metaMap.setValue("name.first", "Clinton");
        metaMap.setValue("name.last", "Begin");
        metaMap.setValue("address.street", "1 Some Street");
        metaMap.setValue("address.city", "This City");
        metaMap.setValue("address.province", "A Province");
        metaMap.setValue("address.postal_code", "1A3 4B6");
        Assert.assertTrue(metaMap.hasGetter("id"));
        Assert.assertTrue(metaMap.hasGetter("name.first"));
        Assert.assertTrue(metaMap.hasGetter("address.street"));
        Assert.assertEquals(3, metaMap.getGetterNames().length);
        Assert.assertEquals(3, metaMap.getSetterNames().length);
        Map<String, String> name = ((Map<String, String>) (metaMap.getValue("name")));
        Map<String, String> address = ((Map<String, String>) (metaMap.getValue("address")));
        Assert.assertEquals("Clinton", name.get("first"));
        Assert.assertEquals("1 Some Street", address.get("street"));
    }

    @Test
    public void shouldDemonstrateNullValueInMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        MetaObject metaMap = SystemMetaObject.forObject(map);
        Assert.assertFalse(metaMap.hasGetter("phone.home"));
        metaMap.setValue("phone", null);
        Assert.assertTrue(metaMap.hasGetter("phone"));
        // hasGetter returns true if the parent exists and is null.
        Assert.assertTrue(metaMap.hasGetter("phone.home"));
        Assert.assertTrue(metaMap.hasGetter("phone.home.ext"));
        Assert.assertNull(metaMap.getValue("phone"));
        Assert.assertNull(metaMap.getValue("phone.home"));
        Assert.assertNull(metaMap.getValue("phone.home.ext"));
        metaMap.setValue("phone.office", "789");
        Assert.assertFalse(metaMap.hasGetter("phone.home"));
        Assert.assertFalse(metaMap.hasGetter("phone.home.ext"));
        Assert.assertEquals("789", metaMap.getValue("phone.office"));
        Assert.assertNotNull(metaMap.getValue("phone"));
        Assert.assertNull(metaMap.getValue("phone.home"));
    }

    @Test
    public void shouldNotUseObjectWrapperFactoryByDefault() {
        MetaObject meta = SystemMetaObject.forObject(new Author());
        Assert.assertTrue((!(meta.getObjectWrapper().getClass().equals(CustomBeanWrapper.class))));
    }

    @Test
    public void shouldUseObjectWrapperFactoryWhenSet() {
        MetaObject meta = MetaObject.forObject(new Author(), DEFAULT_OBJECT_FACTORY, new CustomBeanWrapperFactory());
        Assert.assertTrue(meta.getObjectWrapper().getClass().equals(CustomBeanWrapper.class));
        // Make sure the old default factory is in place and still works
        meta = SystemMetaObject.forObject(new Author());
        Assert.assertFalse(meta.getObjectWrapper().getClass().equals(CustomBeanWrapper.class));
    }

    @Test
    public void shouldMethodHasGetterReturnTrueWhenListElementSet() {
        List<Object> param1 = new ArrayList<Object>();
        param1.add("firstParam");
        param1.add(222);
        param1.add(new Date());
        Map<String, Object> parametersEmulation = new HashMap<String, Object>();
        parametersEmulation.put("param1", param1);
        parametersEmulation.put("filterParams", param1);
        MetaObject meta = SystemMetaObject.forObject(parametersEmulation);
        Assert.assertEquals(param1.get(0), meta.getValue("filterParams[0]"));
        Assert.assertEquals(param1.get(1), meta.getValue("filterParams[1]"));
        Assert.assertEquals(param1.get(2), meta.getValue("filterParams[2]"));
        Assert.assertTrue(meta.hasGetter("filterParams[0]"));
        Assert.assertTrue(meta.hasGetter("filterParams[1]"));
        Assert.assertTrue(meta.hasGetter("filterParams[2]"));
    }
}

