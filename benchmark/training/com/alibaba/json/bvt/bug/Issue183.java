package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue183 extends TestCase {
    public void test_issue_183() throws Exception {
        Issue183.A a = new Issue183.A();
        a.setName("xiao").setAge(21);
        String result = JSON.toJSONString(a);
        Issue183.A newA = JSON.parseObject(result, Issue183.A.class);
        Assert.assertTrue(a.equals(newA));
    }

    static interface IA {
        @JSONField(name = "wener")
        String getName();

        @JSONField(name = "wener")
        Issue183.IA setName(String name);
    }

    static class A implements Issue183.IA {
        String name;

        int age;

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public Issue183.A setAge(int age) {
            this.age = age;
            return this;
        }

        public Issue183.A setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            Issue183.A other = ((Issue183.A) (obj));
            if ((age) != (other.age))
                return false;

            if ((name) == null) {
                if ((other.name) != null)
                    return false;

            } else
                if (!(name.equals(other.name)))
                    return false;


            return true;
        }
    }
}

