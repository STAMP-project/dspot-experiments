package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_318 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_318.Person o1 = new Bug_for_issue_318.Person("zhangsan", 20);
        Bug_for_issue_318.Person o2 = new Bug_for_issue_318.Person("liuXX", 30);
        Bug_for_issue_318.Person o3 = new Bug_for_issue_318.Person("Test", 10);
        List<Bug_for_issue_318.Person> users = new ArrayList<Bug_for_issue_318.Person>();
        users.add(o1);
        users.add(o2);
        users.add(o3);
        List<Bug_for_issue_318.Person> managers = new ArrayList<Bug_for_issue_318.Person>();
        managers.add(o2);
        managers.add(o3);
        Bug_for_issue_318.PersonAll pa = new Bug_for_issue_318.PersonAll();
        pa.setCount(30);
        // map
        Map<String, List<Bug_for_issue_318.Person>> userMap = new LinkedHashMap<String, List<Bug_for_issue_318.Person>>();
        userMap.put("managers", managers);
        userMap.put("users", users);
        pa.setUserMap(userMap);
        // bean???
        pa.setUsers(users);
        pa.setManagers(managers);
        // String json = JSON.toJSONString(pa, SerializerFeature.DisableCircularReferenceDetect);
        String json = JSON.toJSONString(pa);
        // System.out.println("???: ");
        // System.out.println(json);
        Bug_for_issue_318.PersonAll target = JSON.parseObject(json, Bug_for_issue_318.PersonAll.class);
        // System.out.println("??????: ");
        // System.out.println("map users: " + target.getUserMap().get("users"));
        // System.out.println("map managers: " + target.getUserMap().get("managers"));
        // 
        // // ???? "BUG" ????????null
        // System.out.println("bean users: " + target.getUsers());
        // System.out.println("bean managers: " + target.getManagers());
        // 
        // System.out.println(JSON.toJSONString(target));
        Assert.assertNotNull(target.getUsers().get(0));
        Assert.assertNotNull(target.getManagers().get(0));
    }

    private static class Person {
        private String name;

        private Integer age;

        public Person() {
        }

        public Person(String name, Integer age) {
            super();
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    private static class PersonAll {
        private Map<String, List<Bug_for_issue_318.Person>> userMap = new HashMap<String, List<Bug_for_issue_318.Person>>();

        private Integer count;

        private List<Bug_for_issue_318.Person> users;

        private List<Bug_for_issue_318.Person> managers;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Map<String, List<Bug_for_issue_318.Person>> getUserMap() {
            return userMap;
        }

        public void setUserMap(Map<String, List<Bug_for_issue_318.Person>> userMap) {
            this.userMap = userMap;
        }

        public List<Bug_for_issue_318.Person> getUsers() {
            return users;
        }

        public void setUsers(List<Bug_for_issue_318.Person> users) {
            this.users = users;
        }

        public List<Bug_for_issue_318.Person> getManagers() {
            return managers;
        }

        public void setManagers(List<Bug_for_issue_318.Person> managers) {
            this.managers = managers;
        }
    }
}

