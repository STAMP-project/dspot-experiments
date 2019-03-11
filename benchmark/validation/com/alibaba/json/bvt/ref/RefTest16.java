package com.alibaba.json.bvt.ref;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class RefTest16 extends TestCase {
    public void test_0() throws Exception {
        RefTest16.Person pA = new RefTest16.Person("a");
        RefTest16.Person pB = new RefTest16.Person("b");
        RefTest16.Family fA = new RefTest16.Family();
        fA.setMembers(new RefTest16.Person[]{ pA, pB });
        fA.setMaster(pA);
        RefTest16.Person pC = new RefTest16.Person("c");
        RefTest16.Person pD = new RefTest16.Person("d");
        RefTest16.Family fB = new RefTest16.Family();
        fB.setMembers(new RefTest16.Person[]{ pC, pD });
        fB.setMaster(pC);
        RefTest16.Family[] familyArray = new RefTest16.Family[]{ fA, fB };
        String text = JSON.toJSONString(familyArray);
        System.out.println(text);
        RefTest16.Family[] result = JSON.parseObject(text, RefTest16.Family[].class);
        Assert.assertSame(result[0].getMaster(), result[0].getMembers()[0]);
        Assert.assertSame(result[1].getMaster(), result[1].getMembers()[0]);
    }

    public static class Family {
        private RefTest16.Person master;

        private RefTest16.Person[] members;

        public RefTest16.Person getMaster() {
            return master;
        }

        public void setMaster(RefTest16.Person master) {
            this.master = master;
        }

        public RefTest16.Person[] getMembers() {
            return members;
        }

        public void setMembers(RefTest16.Person[] members) {
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

