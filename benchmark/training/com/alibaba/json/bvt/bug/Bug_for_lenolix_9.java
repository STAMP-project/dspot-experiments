package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_lenolix_9 extends TestCase {
    public void test_for_objectKey() throws Exception {
        Map<String, Object> submap4 = new HashMap<String, Object>();
        Bug_for_lenolix_9.Address address = new Bug_for_lenolix_9.Address();
        address.setCity("hangzhou");
        address.setStreet("wangshang.RD");
        address.setPostCode(310002);
        submap4.put("address1", address);
        submap4.put("address2", address);
        Bug_for_lenolix_9.Address.Country country = address.new Country();
        country.setProvince("ZheJiang");
        address.setCountry(country);
        String mapString4 = JSON.toJSONString(submap4, WriteClassName, WriteMapNullValue);
        System.out.println(mapString4);
        Object object4 = JSON.parse(mapString4);
        Assert.assertNotNull(object4);
        Map<String, Object> map = ((Map<String, Object>) (object4));
        Assert.assertNotNull(map.get("address1"));
        Assert.assertNotNull(map.get("address2"));
        Assert.assertTrue(((map.get("address1")) == (map.get("address2"))));
    }

    public static class Address {
        private String city;

        private String street;

        private int postCode;

        private Bug_for_lenolix_9.Address.Country country;

        public Bug_for_lenolix_9.Address.Country getCountry() {
            return country;
        }

        public void setCountry(Bug_for_lenolix_9.Address.Country country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public int getPostCode() {
            return postCode;
        }

        public void setPostCode(int postCode) {
            this.postCode = postCode;
        }

        public class Country {
            private String province;

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }
        }
    }
}

