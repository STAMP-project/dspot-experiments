/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.beanutil;


import JavaBeanAccessor.METHOD;
import JavaBeanDescriptor.TYPE_PRIMITIVE;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.dubbo.common.model.person.BigPerson;
import org.apache.dubbo.common.model.person.FullAddress;
import org.apache.dubbo.common.model.person.PersonStatus;
import org.apache.dubbo.common.model.person.Phone;
import org.apache.dubbo.common.utils.PojoUtilsTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static JavaBeanDescriptor.TYPE_ARRAY;
import static JavaBeanDescriptor.TYPE_BEAN;
import static JavaBeanDescriptor.TYPE_PRIMITIVE;


public class JavaBeanSerializeUtilTest {
    @Test
    public void testSerialize_Primitive() {
        JavaBeanDescriptor descriptor;
        descriptor = JavaBeanSerializeUtil.serialize(Integer.MAX_VALUE);
        Assertions.assertTrue(descriptor.isPrimitiveType());
        Assertions.assertEquals(Integer.MAX_VALUE, descriptor.getPrimitiveProperty());
        Date now = new Date();
        descriptor = JavaBeanSerializeUtil.serialize(now);
        Assertions.assertTrue(descriptor.isPrimitiveType());
        Assertions.assertEquals(now, descriptor.getPrimitiveProperty());
    }

    @Test
    public void testSerialize_Primitive_NUll() {
        JavaBeanDescriptor descriptor;
        descriptor = JavaBeanSerializeUtil.serialize(null);
        Assertions.assertTrue((descriptor == null));
    }

    @Test
    public void testDeserialize_Primitive() {
        JavaBeanDescriptor descriptor = new JavaBeanDescriptor(long.class.getName(), TYPE_PRIMITIVE);
        descriptor.setPrimitiveProperty(Long.MAX_VALUE);
        Assertions.assertEquals(Long.MAX_VALUE, JavaBeanSerializeUtil.deserialize(descriptor));
        BigDecimal decimal = BigDecimal.TEN;
        Assertions.assertEquals(Long.MAX_VALUE, descriptor.setPrimitiveProperty(decimal));
        Assertions.assertEquals(decimal, JavaBeanSerializeUtil.deserialize(descriptor));
        String string = UUID.randomUUID().toString();
        Assertions.assertEquals(decimal, descriptor.setPrimitiveProperty(string));
        Assertions.assertEquals(string, JavaBeanSerializeUtil.deserialize(descriptor));
    }

    @Test
    public void testDeserialize_Primitive0() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            JavaBeanDescriptor descriptor = new JavaBeanDescriptor(long.class.getName(), ((TYPE_BEAN) + 1));
        });
    }

    @Test
    public void testDeserialize_Null() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            JavaBeanDescriptor descriptor = new JavaBeanDescriptor(null, TYPE_BEAN);
        });
    }

    @Test
    public void testDeserialize_containsProperty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            JavaBeanDescriptor descriptor = new JavaBeanDescriptor(long.class.getName(), TYPE_PRIMITIVE);
            descriptor.containsProperty(null);
        });
    }

    @Test
    public void testSetEnumNameProperty() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            JavaBeanDescriptor descriptor = new JavaBeanDescriptor(long.class.getName(), TYPE_PRIMITIVE);
            descriptor.setEnumNameProperty(JavaBeanDescriptor.class.getName());
        });
    }

    @Test
    public void testGetEnumNameProperty() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            JavaBeanDescriptor descriptor = new JavaBeanDescriptor(long.class.getName(), TYPE_PRIMITIVE);
            descriptor.getEnumPropertyName();
        });
    }

    @Test
    public void testSetClassNameProperty() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            JavaBeanDescriptor descriptor = new JavaBeanDescriptor(long.class.getName(), TYPE_PRIMITIVE);
            descriptor.setClassNameProperty(JavaBeanDescriptor.class.getName());
        });
    }

    @Test
    public void testGetClassNameProperty() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            JavaBeanDescriptor descriptor = new JavaBeanDescriptor(long.class.getName(), TYPE_PRIMITIVE);
            descriptor.getClassNameProperty();
        });
    }

    @Test
    public void testSetPrimitiveProperty() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            JavaBeanDescriptor descriptor = new JavaBeanDescriptor(JavaBeanDescriptor.class.getName(), TYPE_BEAN);
            descriptor.setPrimitiveProperty(JavaBeanDescriptor.class.getName());
        });
    }

    @Test
    public void testGetPrimitiveProperty() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            JavaBeanDescriptor descriptor = new JavaBeanDescriptor(JavaBeanDescriptor.class.getName(), TYPE_BEAN);
            descriptor.getPrimitiveProperty();
        });
    }

    @Test
    public void testDeserialize_get_and_set() {
        JavaBeanDescriptor descriptor = new JavaBeanDescriptor(long.class.getName(), TYPE_BEAN);
        descriptor.setType(TYPE_PRIMITIVE);
        Assertions.assertTrue(((descriptor.getType()) == (TYPE_PRIMITIVE)));
        descriptor.setClassName(JavaBeanDescriptor.class.getName());
        Assertions.assertEquals(JavaBeanDescriptor.class.getName(), descriptor.getClassName());
    }

    @Test
    public void testSerialize_Array() {
        int[] array = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        JavaBeanDescriptor descriptor = JavaBeanSerializeUtil.serialize(array, METHOD);
        Assertions.assertTrue(descriptor.isArrayType());
        Assertions.assertEquals(int.class.getName(), descriptor.getClassName());
        for (int i = 0; i < (array.length); i++) {
            Assertions.assertEquals(array[i], getPrimitiveProperty());
        }
        Integer[] integers = new Integer[]{ 1, 2, 3, 4, null, null, null };
        descriptor = JavaBeanSerializeUtil.serialize(integers, METHOD);
        Assertions.assertTrue(descriptor.isArrayType());
        Assertions.assertEquals(Integer.class.getName(), descriptor.getClassName());
        Assertions.assertEquals(integers.length, descriptor.propertySize());
        for (int i = 0; i < (integers.length); i++) {
            if ((integers[i]) == null) {
                Assertions.assertTrue(((integers[i]) == (descriptor.getProperty(i))));
            } else {
                Assertions.assertEquals(integers[i], getPrimitiveProperty());
            }
        }
        int[][] second = new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4 } };
        descriptor = JavaBeanSerializeUtil.serialize(second, METHOD);
        Assertions.assertTrue(descriptor.isArrayType());
        Assertions.assertEquals(int[].class.getName(), descriptor.getClassName());
        for (int i = 0; i < (second.length); i++) {
            for (int j = 0; j < (second[i].length); j++) {
                JavaBeanDescriptor item = ((JavaBeanDescriptor) (descriptor.getProperty(i)));
                Assertions.assertTrue(item.isArrayType());
                Assertions.assertEquals(int.class.getName(), item.getClassName());
                Assertions.assertEquals(second[i][j], getPrimitiveProperty());
            }
        }
        BigPerson[] persons = new BigPerson[]{ JavaBeanSerializeUtilTest.createBigPerson(), JavaBeanSerializeUtilTest.createBigPerson() };
        descriptor = JavaBeanSerializeUtil.serialize(persons);
        Assertions.assertTrue(descriptor.isArrayType());
        Assertions.assertEquals(BigPerson.class.getName(), descriptor.getClassName());
        for (int i = 0; i < (persons.length); i++) {
            JavaBeanSerializeUtilTest.assertEqualsBigPerson(persons[i], descriptor.getProperty(i));
        }
    }

    @Test
    public void testConstructorArg() {
        Assertions.assertFalse(((boolean) (JavaBeanSerializeUtil.getConstructorArg(boolean.class))));
        Assertions.assertFalse(((boolean) (JavaBeanSerializeUtil.getConstructorArg(Boolean.class))));
        Assertions.assertEquals(((byte) (0)), JavaBeanSerializeUtil.getConstructorArg(byte.class));
        Assertions.assertEquals(((byte) (0)), JavaBeanSerializeUtil.getConstructorArg(Byte.class));
        Assertions.assertEquals(((short) (0)), JavaBeanSerializeUtil.getConstructorArg(short.class));
        Assertions.assertEquals(((short) (0)), JavaBeanSerializeUtil.getConstructorArg(Short.class));
        Assertions.assertEquals(0, JavaBeanSerializeUtil.getConstructorArg(int.class));
        Assertions.assertEquals(0, JavaBeanSerializeUtil.getConstructorArg(Integer.class));
        Assertions.assertEquals(((long) (0)), JavaBeanSerializeUtil.getConstructorArg(long.class));
        Assertions.assertEquals(((long) (0)), JavaBeanSerializeUtil.getConstructorArg(Long.class));
        Assertions.assertEquals(((float) (0)), JavaBeanSerializeUtil.getConstructorArg(float.class));
        Assertions.assertEquals(((float) (0)), JavaBeanSerializeUtil.getConstructorArg(Float.class));
        Assertions.assertEquals(((double) (0)), JavaBeanSerializeUtil.getConstructorArg(double.class));
        Assertions.assertEquals(((double) (0)), JavaBeanSerializeUtil.getConstructorArg(Double.class));
        Assertions.assertEquals(((char) (0)), JavaBeanSerializeUtil.getConstructorArg(char.class));
        Assertions.assertEquals(new Character(((char) (0))), JavaBeanSerializeUtil.getConstructorArg(Character.class));
        Assertions.assertEquals(null, JavaBeanSerializeUtil.getConstructorArg(JavaBeanSerializeUtil.class));
    }

    @Test
    public void testDeserialize_Array() {
        final int len = 10;
        JavaBeanDescriptor descriptor = new JavaBeanDescriptor(int.class.getName(), TYPE_ARRAY);
        for (int i = 0; i < len; i++) {
            descriptor.setProperty(i, i);
        }
        Object obj = JavaBeanSerializeUtil.deserialize(descriptor);
        Assertions.assertTrue(obj.getClass().isArray());
        Assertions.assertTrue(((int.class) == (obj.getClass().getComponentType())));
        for (int i = 0; i < len; i++) {
            Assertions.assertEquals(i, Array.get(obj, i));
        }
        descriptor = new JavaBeanDescriptor(int[].class.getName(), TYPE_ARRAY);
        for (int i = 0; i < len; i++) {
            JavaBeanDescriptor innerItem = new JavaBeanDescriptor(int.class.getName(), TYPE_ARRAY);
            for (int j = 0; j < len; j++) {
                innerItem.setProperty(j, j);
            }
            descriptor.setProperty(i, innerItem);
        }
        obj = JavaBeanSerializeUtil.deserialize(descriptor);
        Assertions.assertTrue(obj.getClass().isArray());
        Assertions.assertEquals(int[].class, obj.getClass().getComponentType());
        for (int i = 0; i < len; i++) {
            Object innerItem = Array.get(obj, i);
            Assertions.assertTrue(innerItem.getClass().isArray());
            Assertions.assertEquals(int.class, innerItem.getClass().getComponentType());
            for (int j = 0; j < len; j++) {
                Assertions.assertEquals(j, Array.get(innerItem, j));
            }
        }
        descriptor = new JavaBeanDescriptor(BigPerson[].class.getName(), TYPE_ARRAY);
        JavaBeanDescriptor innerDescriptor = new JavaBeanDescriptor(BigPerson.class.getName(), TYPE_ARRAY);
        innerDescriptor.setProperty(0, JavaBeanSerializeUtil.serialize(JavaBeanSerializeUtilTest.createBigPerson(), METHOD));
        descriptor.setProperty(0, innerDescriptor);
        obj = JavaBeanSerializeUtil.deserialize(descriptor);
        Assertions.assertTrue(obj.getClass().isArray());
        Assertions.assertEquals(BigPerson[].class, obj.getClass().getComponentType());
        Assertions.assertEquals(1, Array.getLength(obj));
        obj = Array.get(obj, 0);
        Assertions.assertTrue(obj.getClass().isArray());
        Assertions.assertEquals(BigPerson.class, obj.getClass().getComponentType());
        Assertions.assertEquals(1, Array.getLength(obj));
        Assertions.assertEquals(JavaBeanSerializeUtilTest.createBigPerson(), Array.get(obj, 0));
    }

    @Test
    public void test_Circular_Reference() {
        PojoUtilsTest.Parent parent = new PojoUtilsTest.Parent();
        parent.setAge(Integer.MAX_VALUE);
        parent.setEmail("a@b");
        parent.setName("zhangsan");
        PojoUtilsTest.Child child = new PojoUtilsTest.Child();
        child.setAge(100);
        child.setName("lisi");
        child.setParent(parent);
        parent.setChild(child);
        JavaBeanDescriptor descriptor = JavaBeanSerializeUtil.serialize(parent, METHOD);
        Assertions.assertTrue(descriptor.isBeanType());
        JavaBeanSerializeUtilTest.assertEqualsPrimitive(parent.getAge(), descriptor.getProperty("age"));
        JavaBeanSerializeUtilTest.assertEqualsPrimitive(parent.getName(), descriptor.getProperty("name"));
        JavaBeanSerializeUtilTest.assertEqualsPrimitive(parent.getEmail(), descriptor.getProperty("email"));
        JavaBeanDescriptor childDescriptor = ((JavaBeanDescriptor) (descriptor.getProperty("child")));
        Assertions.assertTrue((descriptor == (childDescriptor.getProperty("parent"))));
        JavaBeanSerializeUtilTest.assertEqualsPrimitive(child.getName(), childDescriptor.getProperty("name"));
        JavaBeanSerializeUtilTest.assertEqualsPrimitive(child.getAge(), childDescriptor.getProperty("age"));
    }

    @Test
    public void testBeanSerialize() {
        Bean bean = new Bean();
        bean.setDate(new Date());
        bean.setStatus(PersonStatus.ENABLED);
        bean.setType(Bean.class);
        bean.setArray(new Phone[]{  });
        Collection<Phone> collection = new ArrayList<Phone>();
        bean.setCollection(collection);
        Phone phone = new Phone();
        collection.add(phone);
        Map<String, FullAddress> map = new HashMap<String, FullAddress>();
        FullAddress address = new FullAddress();
        map.put("first", address);
        bean.setAddresses(map);
        JavaBeanDescriptor descriptor = JavaBeanSerializeUtil.serialize(bean, METHOD);
        Assertions.assertTrue(descriptor.isBeanType());
        JavaBeanSerializeUtilTest.assertEqualsPrimitive(bean.getDate(), descriptor.getProperty("date"));
        JavaBeanSerializeUtilTest.assertEqualsEnum(bean.getStatus(), descriptor.getProperty("status"));
        Assertions.assertTrue(isClassType());
        Assertions.assertEquals(Bean.class.getName(), getClassNameProperty());
        Assertions.assertTrue(isArrayType());
        Assertions.assertEquals(0, propertySize());
        JavaBeanDescriptor property = ((JavaBeanDescriptor) (descriptor.getProperty("collection")));
        Assertions.assertTrue(property.isCollectionType());
        Assertions.assertEquals(1, property.propertySize());
        property = ((JavaBeanDescriptor) (property.getProperty(0)));
        Assertions.assertTrue(property.isBeanType());
        Assertions.assertEquals(Phone.class.getName(), property.getClassName());
        Assertions.assertEquals(0, property.propertySize());
        property = ((JavaBeanDescriptor) (descriptor.getProperty("addresses")));
        Assertions.assertTrue(property.isMapType());
        Assertions.assertEquals(bean.getAddresses().getClass().getName(), property.getClassName());
        Assertions.assertEquals(1, property.propertySize());
        Map.Entry<Object, Object> entry = property.iterator().next();
        Assertions.assertTrue(isPrimitiveType());
        Assertions.assertEquals("first", getPrimitiveProperty());
        Assertions.assertTrue(isBeanType());
        Assertions.assertEquals(FullAddress.class.getName(), getClassName());
        Assertions.assertEquals(0, propertySize());
    }

    @Test
    public void testDeserializeBean() {
        Bean bean = new Bean();
        bean.setDate(new Date());
        bean.setStatus(PersonStatus.ENABLED);
        bean.setType(Bean.class);
        bean.setArray(new Phone[]{  });
        Collection<Phone> collection = new ArrayList<Phone>();
        bean.setCollection(collection);
        Phone phone = new Phone();
        collection.add(phone);
        Map<String, FullAddress> map = new HashMap<String, FullAddress>();
        FullAddress address = new FullAddress();
        map.put("first", address);
        bean.setAddresses(map);
        JavaBeanDescriptor beanDescriptor = JavaBeanSerializeUtil.serialize(bean, METHOD);
        Object deser = JavaBeanSerializeUtil.deserialize(beanDescriptor);
        Assertions.assertTrue((deser instanceof Bean));
        Bean deserBean = ((Bean) (deser));
        Assertions.assertEquals(bean.getDate(), deserBean.getDate());
        Assertions.assertEquals(bean.getStatus(), deserBean.getStatus());
        Assertions.assertEquals(bean.getType(), deserBean.getType());
        Assertions.assertEquals(bean.getCollection().size(), deserBean.getCollection().size());
        Assertions.assertEquals(bean.getCollection().iterator().next().getClass(), deserBean.getCollection().iterator().next().getClass());
        Assertions.assertEquals(bean.getAddresses().size(), deserBean.getAddresses().size());
        Assertions.assertEquals(bean.getAddresses().entrySet().iterator().next().getKey(), deserBean.getAddresses().entrySet().iterator().next().getKey());
        Assertions.assertEquals(bean.getAddresses().entrySet().iterator().next().getValue().getClass(), deserBean.getAddresses().entrySet().iterator().next().getValue().getClass());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSerializeJavaBeanDescriptor() {
        JavaBeanDescriptor descriptor = new JavaBeanDescriptor();
        JavaBeanDescriptor result = JavaBeanSerializeUtil.serialize(descriptor);
        Assertions.assertTrue((descriptor == result));
        Map map = new HashMap();
        map.put("first", descriptor);
        result = JavaBeanSerializeUtil.serialize(map);
        Assertions.assertTrue(result.isMapType());
        Assertions.assertEquals(HashMap.class.getName(), result.getClassName());
        Assertions.assertEquals(map.size(), result.propertySize());
        Object object = result.iterator().next().getValue();
        Assertions.assertTrue((object instanceof JavaBeanDescriptor));
        JavaBeanDescriptor actual = ((JavaBeanDescriptor) (object));
        Assertions.assertEquals(map.get("first"), actual);
    }
}

