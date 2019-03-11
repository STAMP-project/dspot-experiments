package com.alibaba.json.bvt.parser.deser.asm;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.json.test.benchmark.encode.EishayEncode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class TestASM extends TestCase {
    public void test_asm() throws Exception {
        String text = JSON.toJSONString(EishayEncode.mediaContent);
        System.out.println(text);
    }

    public void test_0() throws Exception {
        TestASM.Department department = new TestASM.Department();
        TestASM.Person person = new TestASM.Person();
        person.setId(123);
        person.setName("???");
        person.setAge(40);
        person.setSalary(new BigDecimal("123456"));
        person.getValues().add("A");
        person.getValues().add("B");
        person.getValues().add("C");
        department.getPersons().add(person);
        department.getPersons().add(new TestASM.Person());
        department.getPersons().add(new TestASM.Person());
        {
            String text = JSON.toJSONString(department);
            System.out.println(text);
        }
        {
            String text = JSON.toJSONString(department, WriteMapNullValue);
            System.out.println(text);
        }
    }

    public static class Person {
        private int id;

        private String name;

        private int age;

        private BigDecimal salary;

        private List<TestASM.Person> childrens = new ArrayList<TestASM.Person>();

        private List<String> values = new ArrayList<String>();

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public BigDecimal getSalary() {
            return salary;
        }

        public void setSalary(BigDecimal salary) {
            this.salary = salary;
        }

        public List<TestASM.Person> getChildrens() {
            return childrens;
        }

        public void setChildrens(List<TestASM.Person> childrens) {
            this.childrens = childrens;
        }
    }

    public static class Department {
        private int id;

        private String name;

        private List<TestASM.Person> persons = new ArrayList<TestASM.Person>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<TestASM.Person> getPersons() {
            return persons;
        }

        public void setPersons(List<TestASM.Person> persons) {
            this.persons = persons;
        }
    }
}

