package com.alibaba.json.bvt.ref;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import junit.framework.TestCase;
import org.junit.Assert;


public class RefTest17 extends TestCase {
    public void test_0() throws Exception {
        RefTest17.Person pA = new RefTest17.Person("a");
        RefTest17.Person pB = new RefTest17.Person("b");
        RefTest17.Family fA = new RefTest17.Family();
        fA.setMembers(new RefTest17.Person[]{ pA, pB });
        fA.setMaster(pA);
        RefTest17.Person pC = new RefTest17.Person("c");
        RefTest17.Person pD = new RefTest17.Person("d");
        RefTest17.Family fB = new RefTest17.Family();
        fB.setMembers(new RefTest17.Person[]{ pC, pD });
        fB.setMaster(pC);
        RefTest17.Family[] familyArray = new RefTest17.Family[]{ fA, fB };
        String text = JSON.toJSONString(familyArray, true);
        System.out.println(text);
        JSONArray array = JSON.parseArray(text);
        Assert.assertSame(array.getJSONObject(0).get("master"), array.getJSONObject(0).getJSONArray("members").get(0));
        RefTest17.Family family = array.getObject(0, RefTest17.Family.class);
        Assert.assertNotNull(family.getMembers()[0]);
        Assert.assertNotNull(family.getMembers()[1]);
    }

    public static class Family {
        private RefTest17.Person master;

        private RefTest17.Person[] members;

        public RefTest17.Person getMaster() {
            return master;
        }

        public void setMaster(RefTest17.Person master) {
            this.master = master;
        }

        public RefTest17.Person[] getMembers() {
            return members;
        }

        public void setMembers(RefTest17.Person[] members) {
            this.members = members;
        }
    }

    public static class Person {
        private String name;

        public Person() {
        }

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

