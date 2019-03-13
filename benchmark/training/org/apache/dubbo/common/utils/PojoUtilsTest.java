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
package org.apache.dubbo.common.utils;


import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.dubbo.common.model.Person;
import org.apache.dubbo.common.model.SerializablePerson;
import org.apache.dubbo.common.model.person.BigPerson;
import org.apache.dubbo.common.model.person.FullAddress;
import org.apache.dubbo.common.model.person.PersonInfo;
import org.apache.dubbo.common.model.person.PersonStatus;
import org.apache.dubbo.common.model.person.Phone;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class PojoUtilsTest {
    BigPerson bigPerson;

    {
        bigPerson = new BigPerson();
        bigPerson.setPersonId("id1");
        bigPerson.setLoginName("name1");
        bigPerson.setStatus(PersonStatus.ENABLED);
        bigPerson.setEmail("abc@123.com");
        bigPerson.setPenName("pname");
        ArrayList<Phone> phones = new ArrayList<Phone>();
        Phone phone1 = new Phone("86", "0571", "11223344", "001");
        Phone phone2 = new Phone("86", "0571", "11223344", "002");
        phones.add(phone1);
        phones.add(phone2);
        PersonInfo pi = new PersonInfo();
        pi.setPhones(phones);
        Phone fax = new Phone("86", "0571", "11223344", null);
        pi.setFax(fax);
        FullAddress addr = new FullAddress("CN", "zj", "1234", "Road1", "333444");
        pi.setFullAddress(addr);
        pi.setMobileNo("1122334455");
        pi.setMale(true);
        pi.setDepartment("b2b");
        pi.setHomepageUrl("www.abc.com");
        pi.setJobTitle("dev");
        pi.setName("name2");
        bigPerson.setInfoProfile(pi);
    }

    @Test
    public void test_primitive() throws Exception {
        assertObject(Boolean.TRUE);
        assertObject(Boolean.FALSE);
        assertObject(Byte.valueOf(((byte) (78))));
        assertObject('a');
        assertObject('?');
        assertObject(Short.valueOf(((short) (37))));
        assertObject(78);
        assertObject(123456789L);
        assertObject(3.14F);
        assertObject(3.14);
    }

    @Test
    public void test_pojo() throws Exception {
        assertObject(new Person());
        assertObject(new SerializablePerson());
    }

    @Test
    public void test_Map_List_pojo() throws Exception {
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        List<Object> list = new ArrayList<Object>();
        list.add(new Person());
        list.add(new SerializablePerson());
        map.put("k", list);
        Object generalize = PojoUtils.generalize(map);
        Object realize = PojoUtils.realize(generalize, Map.class);
        Assertions.assertEquals(map, realize);
    }

    @Test
    public void test_PrimitiveArray() throws Exception {
        assertObject(new boolean[]{ true, false });
        assertObject(new Boolean[]{ true, false, true });
        assertObject(new byte[]{ 1, 12, 28, 78 });
        assertObject(new Byte[]{ 1, 12, 28, 78 });
        assertObject(new char[]{ 'a', '?', '?' });
        assertObject(new Character[]{ 'a', '?', '?' });
        assertObject(new short[]{ 37, 39, 12 });
        assertObject(new Short[]{ 37, 39, 12 });
        assertObject(new int[]{ 37, -39, 12456 });
        assertObject(new Integer[]{ 37, -39, 12456 });
        assertObject(new long[]{ 37L, -39L, 123456789L });
        assertObject(new Long[]{ 37L, -39L, 123456789L });
        assertObject(new float[]{ 37.0F, -3.14F, 123456.7F });
        assertObject(new Float[]{ 37.0F, -39.0F, 123456.7F });
        assertObject(new double[]{ 37.0, -3.14, 123456.7 });
        assertObject(new Double[]{ 37.0, -39.0, 123456.7 });
        assertArrayObject(new Boolean[]{ true, false, true });
        assertArrayObject(new Byte[]{ 1, 12, 28, 78 });
        assertArrayObject(new Character[]{ 'a', '?', '?' });
        assertArrayObject(new Short[]{ 37, 39, 12 });
        assertArrayObject(new Integer[]{ 37, -39, 12456 });
        assertArrayObject(new Long[]{ 37L, -39L, 123456789L });
        assertArrayObject(new Float[]{ 37.0F, -39.0F, 123456.7F });
        assertArrayObject(new Double[]{ 37.0, -39.0, 123456.7 });
    }

    @Test
    public void test_PojoArray() throws Exception {
        Person[] array = new Person[2];
        array[0] = new Person();
        {
            Person person = new Person();
            person.setName("xxxx");
            array[1] = person;
        }
        assertArrayObject(array);
    }

    @Test
    public void testArrayToCollection() throws Exception {
        Person[] array = new Person[2];
        Person person1 = new Person();
        person1.setName("person1");
        Person person2 = new Person();
        person2.setName("person2");
        array[0] = person1;
        array[1] = person2;
        Object o = PojoUtils.realize(PojoUtils.generalize(array), LinkedList.class);
        Assertions.assertTrue((o instanceof LinkedList));
        Assertions.assertEquals(((List) (o)).get(0), person1);
        Assertions.assertEquals(((List) (o)).get(1), person2);
    }

    @Test
    public void testCollectionToArray() throws Exception {
        Person person1 = new Person();
        person1.setName("person1");
        Person person2 = new Person();
        person2.setName("person2");
        List<Person> list = new LinkedList<Person>();
        list.add(person1);
        list.add(person2);
        Object o = PojoUtils.realize(PojoUtils.generalize(list), Person[].class);
        Assertions.assertTrue((o instanceof Person[]));
        Assertions.assertEquals(((Person[]) (o))[0], person1);
        Assertions.assertEquals(((Person[]) (o))[1], person2);
    }

    @Test
    public void testMapToEnum() throws Exception {
        Map map = new HashMap();
        map.put("name", "MONDAY");
        Object o = PojoUtils.realize(map, PojoUtilsTest.Day.class);
        Assertions.assertEquals(o, PojoUtilsTest.Day.MONDAY);
    }

    @Test
    public void testGeneralizeEnumArray() throws Exception {
        Object days = new Enum[]{ PojoUtilsTest.Day.FRIDAY, PojoUtilsTest.Day.SATURDAY };
        Object o = PojoUtils.generalize(days);
        Assertions.assertTrue((o instanceof String[]));
        Assertions.assertEquals(((String[]) (o))[0], "FRIDAY");
        Assertions.assertEquals(((String[]) (o))[1], "SATURDAY");
    }

    @Test
    public void testGeneralizePersons() throws Exception {
        Object persons = new Person[]{ new Person(), new Person() };
        Object o = PojoUtils.generalize(persons);
        Assertions.assertTrue((o instanceof Object[]));
        Assertions.assertEquals(((Object[]) (o)).length, 2);
    }

    @Test
    public void testMapToInterface() throws Exception {
        Map map = new HashMap();
        map.put("content", "greeting");
        map.put("from", "dubbo");
        map.put("urgent", true);
        Object o = PojoUtils.realize(map, PojoUtilsTest.Message.class);
        PojoUtilsTest.Message message = ((PojoUtilsTest.Message) (o));
        MatcherAssert.assertThat(message.getContent(), Matchers.equalTo("greeting"));
        MatcherAssert.assertThat(message.getFrom(), Matchers.equalTo("dubbo"));
        Assertions.assertTrue(message.isUrgent());
    }

    @Test
    public void testException() throws Exception {
        Map map = new HashMap();
        map.put("message", "dubbo exception");
        Object o = PojoUtils.realize(map, RuntimeException.class);
        Assertions.assertEquals(((Throwable) (o)).getMessage(), "dubbo exception");
    }

    @Test
    public void testIsPojo() throws Exception {
        Assertions.assertFalse(PojoUtils.isPojo(boolean.class));
        Assertions.assertFalse(PojoUtils.isPojo(Map.class));
        Assertions.assertFalse(PojoUtils.isPojo(List.class));
        Assertions.assertTrue(PojoUtils.isPojo(Person.class));
    }

    @Test
    public void test_simpleCollection() throws Exception {
        Type gtype = getType("returnListPersonMethod");
        List<Person> list = new ArrayList<Person>();
        list.add(new Person());
        {
            Person person = new Person();
            person.setName("xxxx");
            list.add(person);
        }
        assertObject(list, gtype);
    }

    @Test
    public void test_total() throws Exception {
        Object generalize = PojoUtils.generalize(bigPerson);
        Type gtype = getType("returnBigPersonMethod");
        Object realize = PojoUtils.realize(generalize, BigPerson.class, gtype);
        Assertions.assertEquals(bigPerson, realize);
    }

    @Test
    public void test_total_Array() throws Exception {
        Object[] persons = new Object[]{ bigPerson, bigPerson, bigPerson };
        Object generalize = PojoUtils.generalize(persons);
        Object[] realize = ((Object[]) (PojoUtils.realize(generalize, Object[].class)));
        Assertions.assertArrayEquals(persons, realize);
    }

    @Test
    public void test_Loop_pojo() throws Exception {
        PojoUtilsTest.Parent p = new PojoUtilsTest.Parent();
        p.setAge(10);
        p.setName("jerry");
        PojoUtilsTest.Child c = new PojoUtilsTest.Child();
        c.setToy("haha");
        p.setChild(c);
        c.setParent(p);
        Object generalize = PojoUtils.generalize(p);
        PojoUtilsTest.Parent parent = ((PojoUtilsTest.Parent) (PojoUtils.realize(generalize, PojoUtilsTest.Parent.class)));
        Assertions.assertEquals(10, parent.getAge());
        Assertions.assertEquals("jerry", parent.getName());
        Assertions.assertEquals("haha", parent.getChild().getToy());
        Assertions.assertSame(parent, parent.getChild().getParent());
    }

    @Test
    public void test_Loop_Map() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("k", "v");
        map.put("m", map);
        Assertions.assertSame(map, map.get("m"));
        System.out.println(map);
        Object generalize = PojoUtils.generalize(map);
        System.out.println(generalize);
        @SuppressWarnings("unchecked")
        Map<String, Object> ret = ((Map<String, Object>) (PojoUtils.realize(generalize, Map.class)));
        System.out.println(ret);
        Assertions.assertEquals("v", ret.get("k"));
        Assertions.assertSame(ret, ret.get("m"));
    }

    @Test
    public void test_LoopPojoInMap() throws Exception {
        PojoUtilsTest.Parent p = new PojoUtilsTest.Parent();
        p.setAge(10);
        p.setName("jerry");
        PojoUtilsTest.Child c = new PojoUtilsTest.Child();
        c.setToy("haha");
        p.setChild(c);
        c.setParent(p);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("k", p);
        Object generalize = PojoUtils.generalize(map);
        @SuppressWarnings("unchecked")
        Map<String, Object> realize = ((Map<String, Object>) (PojoUtils.realize(generalize, Map.class, getType("getMapGenericType"))));
        PojoUtilsTest.Parent parent = ((PojoUtilsTest.Parent) (realize.get("k")));
        Assertions.assertEquals(10, parent.getAge());
        Assertions.assertEquals("jerry", parent.getName());
        Assertions.assertEquals("haha", parent.getChild().getToy());
        Assertions.assertSame(parent, parent.getChild().getParent());
    }

    @Test
    public void test_LoopPojoInList() throws Exception {
        PojoUtilsTest.Parent p = new PojoUtilsTest.Parent();
        p.setAge(10);
        p.setName("jerry");
        PojoUtilsTest.Child c = new PojoUtilsTest.Child();
        c.setToy("haha");
        p.setChild(c);
        c.setParent(p);
        List<Object> list = new ArrayList<Object>();
        list.add(p);
        Object generalize = PojoUtils.generalize(list);
        @SuppressWarnings("unchecked")
        List<Object> realize = ((List<Object>) (PojoUtils.realize(generalize, List.class, getType("getListGenericType"))));
        PojoUtilsTest.Parent parent = ((PojoUtilsTest.Parent) (realize.get(0)));
        Assertions.assertEquals(10, parent.getAge());
        Assertions.assertEquals("jerry", parent.getName());
        Assertions.assertEquals("haha", parent.getChild().getToy());
        Assertions.assertSame(parent, parent.getChild().getParent());
        Object[] objects = PojoUtils.realize(new Object[]{ generalize }, new Class[]{ List.class }, new Type[]{ getType("getListGenericType") });
        Assertions.assertTrue(((((List) (objects[0])).get(0)) instanceof PojoUtilsTest.Parent));
    }

    @Test
    public void test_PojoInList() throws Exception {
        PojoUtilsTest.Parent p = new PojoUtilsTest.Parent();
        p.setAge(10);
        p.setName("jerry");
        List<Object> list = new ArrayList<Object>();
        list.add(p);
        Object generalize = PojoUtils.generalize(list);
        @SuppressWarnings("unchecked")
        List<Object> realize = ((List<Object>) (PojoUtils.realize(generalize, List.class, getType("getListGenericType"))));
        PojoUtilsTest.Parent parent = ((PojoUtilsTest.Parent) (realize.get(0)));
        Assertions.assertEquals(10, parent.getAge());
        Assertions.assertEquals("jerry", parent.getName());
    }

    // java.lang.IllegalArgumentException: argument type mismatch
    @Test
    public void test_realize_LongPararmter_IllegalArgumentException() throws Exception {
        Method method = PojoUtilsTest.class.getMethod("setLong", long.class);
        Assertions.assertNotNull(method);
        Object value = PojoUtils.realize("563439743927993", method.getParameterTypes()[0], method.getGenericParameterTypes()[0]);
        method.invoke(new PojoUtilsTest(), value);
    }

    // java.lang.IllegalArgumentException: argument type mismatch
    @Test
    public void test_realize_IntPararmter_IllegalArgumentException() throws Exception {
        Method method = PojoUtilsTest.class.getMethod("setInt", int.class);
        Assertions.assertNotNull(method);
        Object value = PojoUtils.realize("123", method.getParameterTypes()[0], method.getGenericParameterTypes()[0]);
        method.invoke(new PojoUtilsTest(), value);
    }

    @Test
    public void testStackOverflow() throws Exception {
        PojoUtilsTest.Parent parent = PojoUtilsTest.Parent.getNewParent();
        parent.setAge(Integer.MAX_VALUE);
        String name = UUID.randomUUID().toString();
        parent.setName(name);
        Object generalize = PojoUtils.generalize(parent);
        Assertions.assertTrue((generalize instanceof Map));
        Map map = ((Map) (generalize));
        Assertions.assertEquals(Integer.MAX_VALUE, map.get("age"));
        Assertions.assertEquals(name, map.get("name"));
        PojoUtilsTest.Parent realize = ((PojoUtilsTest.Parent) (PojoUtils.realize(generalize, PojoUtilsTest.Parent.class)));
        Assertions.assertEquals(Integer.MAX_VALUE, realize.getAge());
        Assertions.assertEquals(name, realize.getName());
    }

    @Test
    public void testGenerializeAndRealizeClass() throws Exception {
        Object generalize = PojoUtils.generalize(Integer.class);
        Assertions.assertEquals(Integer.class.getName(), generalize);
        Object real = PojoUtils.realize(generalize, Integer.class.getClass());
        Assertions.assertEquals(Integer.class, real);
        generalize = PojoUtils.generalize(int[].class);
        Assertions.assertEquals(int[].class.getName(), generalize);
        real = PojoUtils.realize(generalize, int[].class.getClass());
        Assertions.assertEquals(int[].class, real);
    }

    @Test
    public void testPublicField() throws Exception {
        PojoUtilsTest.Parent parent = new PojoUtilsTest.Parent();
        parent.gender = "female";
        parent.email = "email@host.com";
        parent.setEmail("securityemail@host.com");
        PojoUtilsTest.Child child = new PojoUtilsTest.Child();
        parent.setChild(child);
        child.gender = "male";
        child.setAge(20);
        child.setParent(parent);
        Object obj = PojoUtils.generalize(parent);
        PojoUtilsTest.Parent realizedParent = ((PojoUtilsTest.Parent) (PojoUtils.realize(obj, PojoUtilsTest.Parent.class)));
        Assertions.assertEquals(parent.gender, realizedParent.gender);
        Assertions.assertEquals(child.gender, parent.getChild().gender);
        Assertions.assertEquals(child.age, realizedParent.getChild().getAge());
        Assertions.assertEquals(parent.getEmail(), realizedParent.getEmail());
        Assertions.assertNull(realizedParent.email);
    }

    @Test
    public void testMapField() throws Exception {
        PojoUtilsTest.TestData data = new PojoUtilsTest.TestData();
        PojoUtilsTest.Child child = PojoUtilsTest.newChild("first", 1);
        data.addChild(child);
        child = PojoUtilsTest.newChild("second", 2);
        data.addChild(child);
        child = PojoUtilsTest.newChild("third", 3);
        data.addChild(child);
        data.setList(Arrays.asList(PojoUtilsTest.newChild("forth", 4)));
        Object obj = PojoUtils.generalize(data);
        Assertions.assertEquals(3, data.getChildren().size());
        Assertions.assertTrue(((data.getChildren().get("first").getClass()) == (PojoUtilsTest.Child.class)));
        Assertions.assertEquals(1, data.getList().size());
        Assertions.assertTrue(((data.getList().get(0).getClass()) == (PojoUtilsTest.Child.class)));
        PojoUtilsTest.TestData realizadData = ((PojoUtilsTest.TestData) (PojoUtils.realize(obj, PojoUtilsTest.TestData.class)));
        Assertions.assertEquals(data.getChildren().size(), realizadData.getChildren().size());
        Assertions.assertEquals(data.getChildren().keySet(), realizadData.getChildren().keySet());
        for (Map.Entry<String, PojoUtilsTest.Child> entry : data.getChildren().entrySet()) {
            PojoUtilsTest.Child c = realizadData.getChildren().get(entry.getKey());
            Assertions.assertNotNull(c);
            Assertions.assertEquals(entry.getValue().getName(), c.getName());
            Assertions.assertEquals(entry.getValue().getAge(), c.getAge());
        }
        Assertions.assertEquals(1, realizadData.getList().size());
        Assertions.assertEquals(data.getList().get(0).getName(), realizadData.getList().get(0).getName());
        Assertions.assertEquals(data.getList().get(0).getAge(), realizadData.getList().get(0).getAge());
    }

    @Test
    public void testRealize() throws Exception {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("key", "value");
        Object obj = PojoUtils.generalize(map);
        Assertions.assertTrue((obj instanceof LinkedHashMap));
        Object outputObject = PojoUtils.realize(map, LinkedHashMap.class);
        Assertions.assertTrue((outputObject instanceof LinkedHashMap));
        Object[] objects = PojoUtils.realize(new Object[]{ map }, new Class[]{ LinkedHashMap.class });
        Assertions.assertTrue(((objects[0]) instanceof LinkedHashMap));
        Assertions.assertEquals(objects[0], outputObject);
    }

    @Test
    public void testRealizeLinkedList() throws Exception {
        LinkedList<Person> input = new LinkedList<Person>();
        Person person = new Person();
        person.setAge(37);
        input.add(person);
        Object obj = PojoUtils.generalize(input);
        Assertions.assertTrue((obj instanceof List));
        Assertions.assertTrue(((input.get(0)) instanceof Person));
        Object output = PojoUtils.realize(obj, LinkedList.class);
        Assertions.assertTrue((output instanceof LinkedList));
    }

    @Test
    public void testPojoList() throws Exception {
        PojoUtilsTest.ListResult<PojoUtilsTest.Parent> result = new PojoUtilsTest.ListResult<PojoUtilsTest.Parent>();
        List<PojoUtilsTest.Parent> list = new ArrayList<PojoUtilsTest.Parent>();
        PojoUtilsTest.Parent parent = new PojoUtilsTest.Parent();
        parent.setAge(Integer.MAX_VALUE);
        parent.setName("zhangsan");
        list.add(parent);
        result.setResult(list);
        Object generializeObject = PojoUtils.generalize(result);
        Object realizeObject = PojoUtils.realize(generializeObject, PojoUtilsTest.ListResult.class);
        Assertions.assertTrue((realizeObject instanceof PojoUtilsTest.ListResult));
        PojoUtilsTest.ListResult listResult = ((PojoUtilsTest.ListResult) (realizeObject));
        List l = listResult.getResult();
        Assertions.assertTrue(((l.size()) == 1));
        Assertions.assertTrue(((l.get(0)) instanceof PojoUtilsTest.Parent));
        PojoUtilsTest.Parent realizeParent = ((PojoUtilsTest.Parent) (l.get(0)));
        Assertions.assertEquals(parent.getName(), realizeParent.getName());
        Assertions.assertEquals(parent.getAge(), realizeParent.getAge());
    }

    @Test
    public void testListPojoListPojo() throws Exception {
        PojoUtilsTest.InnerPojo<PojoUtilsTest.Parent> parentList = new PojoUtilsTest.InnerPojo<PojoUtilsTest.Parent>();
        PojoUtilsTest.Parent parent = new PojoUtilsTest.Parent();
        parent.setName("zhangsan");
        parent.setAge(Integer.MAX_VALUE);
        parentList.setList(Arrays.asList(parent));
        PojoUtilsTest.ListResult<PojoUtilsTest.InnerPojo<PojoUtilsTest.Parent>> list = new PojoUtilsTest.ListResult<PojoUtilsTest.InnerPojo<PojoUtilsTest.Parent>>();
        list.setResult(Arrays.asList(parentList));
        Object generializeObject = PojoUtils.generalize(list);
        Object realizeObject = PojoUtils.realize(generializeObject, PojoUtilsTest.ListResult.class);
        Assertions.assertTrue((realizeObject instanceof PojoUtilsTest.ListResult));
        PojoUtilsTest.ListResult realizeList = ((PojoUtilsTest.ListResult) (realizeObject));
        List realizeInnerList = realizeList.getResult();
        Assertions.assertEquals(1, realizeInnerList.size());
        Assertions.assertTrue(((realizeInnerList.get(0)) instanceof PojoUtilsTest.InnerPojo));
        PojoUtilsTest.InnerPojo realizeParentList = ((PojoUtilsTest.InnerPojo) (realizeInnerList.get(0)));
        Assertions.assertEquals(1, realizeParentList.getList().size());
        Assertions.assertTrue(((realizeParentList.getList().get(0)) instanceof PojoUtilsTest.Parent));
        PojoUtilsTest.Parent realizeParent = ((PojoUtilsTest.Parent) (realizeParentList.getList().get(0)));
        Assertions.assertEquals(parent.getName(), realizeParent.getName());
        Assertions.assertEquals(parent.getAge(), realizeParent.getAge());
    }

    @Test
    public void testDateTimeTimestamp() throws Exception {
        String dateStr = "2018-09-12";
        String timeStr = "10:12:33";
        String dateTimeStr = "2018-09-12 10:12:33";
        String[] dateFormat = new String[]{ "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss" };
        // java.util.Date
        Object date = PojoUtils.realize(dateTimeStr, Date.class, ((Type) (Date.class)));
        Assertions.assertEquals(Date.class, date.getClass());
        Assertions.assertEquals(dateTimeStr, new SimpleDateFormat(dateFormat[0]).format(date));
        // java.sql.Time
        Object time = PojoUtils.realize(dateTimeStr, Time.class, ((Type) (Time.class)));
        Assertions.assertEquals(Time.class, time.getClass());
        Assertions.assertEquals(timeStr, new SimpleDateFormat(dateFormat[2]).format(time));
        // java.sql.Date
        Object sqlDate = PojoUtils.realize(dateTimeStr, java.sql.Date.class, ((Type) (java.sql.Date.class)));
        Assertions.assertEquals(java.sql.Date.class, sqlDate.getClass());
        Assertions.assertEquals(dateStr, new SimpleDateFormat(dateFormat[1]).format(sqlDate));
        // java.sql.Timestamp
        Object timestamp = PojoUtils.realize(dateTimeStr, Timestamp.class, ((Type) (Timestamp.class)));
        Assertions.assertEquals(Timestamp.class, timestamp.getClass());
        Assertions.assertEquals(dateTimeStr, new SimpleDateFormat(dateFormat[0]).format(timestamp));
    }

    public enum Day {

        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY;}

    public static class Parent {
        public String gender;

        public String email;

        String name;

        int age;

        PojoUtilsTest.Child child;

        private String securityEmail;

        public static PojoUtilsTest.Parent getNewParent() {
            return new PojoUtilsTest.Parent();
        }

        public String getEmail() {
            return this.securityEmail;
        }

        public void setEmail(String email) {
            this.securityEmail = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public PojoUtilsTest.Child getChild() {
            return child;
        }

        public void setChild(PojoUtilsTest.Child child) {
            this.child = child;
        }
    }

    public static class Child {
        public String gender;

        public int age;

        String toy;

        PojoUtilsTest.Parent parent;

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getToy() {
            return toy;
        }

        public void setToy(String toy) {
            this.toy = toy;
        }

        public PojoUtilsTest.Parent getParent() {
            return parent;
        }

        public void setParent(PojoUtilsTest.Parent parent) {
            this.parent = parent;
        }
    }

    public static class TestData {
        private Map<String, PojoUtilsTest.Child> children = new HashMap<String, PojoUtilsTest.Child>();

        private List<PojoUtilsTest.Child> list = new ArrayList<PojoUtilsTest.Child>();

        public List<PojoUtilsTest.Child> getList() {
            return list;
        }

        public void setList(List<PojoUtilsTest.Child> list) {
            if (CollectionUtils.isNotEmpty(list)) {
                this.list.addAll(list);
            }
        }

        public Map<String, PojoUtilsTest.Child> getChildren() {
            return children;
        }

        public void setChildren(Map<String, PojoUtilsTest.Child> children) {
            if (CollectionUtils.isNotEmptyMap(children)) {
                this.children.putAll(children);
            }
        }

        public void addChild(PojoUtilsTest.Child child) {
            this.children.put(child.getName(), child);
        }
    }

    public static class InnerPojo<T> {
        private List<T> list;

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }
    }

    public static class ListResult<T> {
        List<T> result;

        public List<T> getResult() {
            return result;
        }

        public void setResult(List<T> result) {
            this.result = result;
        }
    }

    interface Message {
        String getContent();

        String getFrom();

        boolean isUrgent();
    }
}

