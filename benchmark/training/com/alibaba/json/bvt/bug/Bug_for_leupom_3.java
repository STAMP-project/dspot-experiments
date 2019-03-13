package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_leupom_3 extends TestCase {
    public void test_bug() throws Exception {
        Bug_for_leupom_3.Person person = new Bug_for_leupom_3.Person();
        person.setId(12345);
        String text = JSON.toJSONString(person);
        System.out.println(text);
        Bug_for_leupom_3.Person person2 = JSON.parseObject(text, Bug_for_leupom_3.Person.class);
        Assert.assertEquals(person.getId(), person2.getId());
    }

    public abstract static interface Model {
        Serializable getId();

        void setId(Integer value);
    }

    public static class Person implements Bug_for_leupom_3.Model {
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}

