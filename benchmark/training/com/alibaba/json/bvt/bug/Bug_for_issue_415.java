package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_415 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_415.Teacher t = new Bug_for_issue_415.Teacher();
        Bug_for_issue_415.Address addr = new Bug_for_issue_415.Address();
        addr.setAddrDetail("???????");
        Bug_for_issue_415.Student s1 = new Bug_for_issue_415.Student();
        s1.setName("??");
        s1.setAddr(addr);
        Bug_for_issue_415.Student s2 = new Bug_for_issue_415.Student();
        s2.setName("??");
        s2.setAddr(addr);
        t.setStudentList(Arrays.asList(s1, s2));
        String json = JSON.toJSONString(t, WriteClassName);
        // @1 ????????json?
        Bug_for_issue_415.Teacher t2 = ((Bug_for_issue_415.Teacher) (JSON.parse(json)));
        for (Bug_for_issue_415.Student s : t2.getStudentList()) {
            Assert.assertNotNull(s);
            Assert.assertNotNull(s.getAddr());
        }
    }

    public static class Teacher {
        private List<Bug_for_issue_415.Student> studentList;

        public List<Bug_for_issue_415.Student> getStudentList() {
            return studentList;
        }

        public void setStudentList(List<Bug_for_issue_415.Student> studentList) {
            this.studentList = studentList;
        }
    }

    public static class Address {
        private String addrDetail;

        public String getAddrDetail() {
            return addrDetail;
        }

        public void setAddrDetail(String addressDetail) {
            this.addrDetail = addressDetail;
        }
    }

    public static class Student {
        private String name;

        private Bug_for_issue_415.Address address;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Bug_for_issue_415.Address getAddr() {
            return address;
        }

        public void setAddr(Bug_for_issue_415.Address address) {
            this.address = address;
        }
    }
}

