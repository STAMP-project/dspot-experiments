package com.alibaba.json.bvt.asm;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class LoopTest extends TestCase {
    public void test_loop() throws Exception {
        LoopTest.Department department = JSON.parseObject("{id:12,name:'R & D', members:[{id:2001, name:'jobs'}]}", LoopTest.Department.class);
        Assert.assertNotNull(department);
        Assert.assertEquals(12, department.getId());
    }

    public static class Department {
        private int id;

        private String name;

        private List<LoopTest.Employee> members = new ArrayList<LoopTest.Employee>();

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

        public List<LoopTest.Employee> getMembers() {
            return members;
        }

        public void setMembers(List<LoopTest.Employee> members) {
            this.members = members;
        }
    }

    public static class Employee {
        private int id;

        private String name;

        private LoopTest.Department department;

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

        public LoopTest.Department getDepartment() {
            return department;
        }

        public void setDepartment(LoopTest.Department department) {
            this.department = department;
        }
    }
}

