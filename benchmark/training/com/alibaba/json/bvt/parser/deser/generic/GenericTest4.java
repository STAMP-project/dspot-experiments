package com.alibaba.json.bvt.parser.deser.generic;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class GenericTest4 extends TestCase {
    public void test_0() throws Exception {
        String text;
        {
            GenericTest4.User user = new GenericTest4.User("Z??");
            user.getAddresses().add(new GenericTest4.Address("??"));
            text = JSON.toJSONString(user);
        }
        System.out.println(text);
        GenericTest4.User user = JSON.parseObject(text, GenericTest4.User.class);
        Assert.assertEquals("Z??", user.getName());
        Assert.assertEquals(1, user.getAddresses().size());
        Assert.assertEquals(GenericTest4.Address.class, user.getAddresses().get(0).getClass());
        Assert.assertEquals("??", user.getAddresses().get(0).getValue());
    }

    public static class User {
        private String name;

        public User() {
        }

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private List<GenericTest4.Address> addresses = new ArrayList<GenericTest4.Address>();

        public List<GenericTest4.Address> getAddresses() {
            return addresses;
        }

        public void setAddresses(List<GenericTest4.Address> addresses) {
            this.addresses = addresses;
        }
    }

    public static class Address {
        private String value;

        public Address() {
        }

        public Address(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

