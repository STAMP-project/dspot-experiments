package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import junit.framework.TestCase;


public class Bug_for_lenolix_7 extends TestCase {
    public void test_for_objectKey() throws Exception {
        Bug_for_lenolix_7.User user = new Bug_for_lenolix_7.User();
        user.setId(1);
        user.setName("leno.lix");
        user.setIsBoy(true);
        user.setBirthDay(new Date());
        user.setGmtCreate(new java.sql.Date(new Date().getTime()));
        user.setGmtModified(new Timestamp(new Date().getTime()));
        String userJSON = JSON.toJSONString(user, WriteClassName, WriteMapNullValue);
        System.out.println(userJSON);
        Bug_for_lenolix_7.User returnUser = ((Bug_for_lenolix_7.User) (JSON.parse(userJSON)));
    }

    private static class User implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 6192533820796587011L;

        private Integer id;

        private String name;

        private Boolean isBoy;

        private Bug_for_lenolix_7.Address address;

        private Date birthDay;

        private java.sql.Date gmtCreate;

        private Timestamp gmtModified;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getIsBoy() {
            return isBoy;
        }

        public void setIsBoy(Boolean isBoy) {
            this.isBoy = isBoy;
        }

        public Bug_for_lenolix_7.Address getAddress() {
            return address;
        }

        public void setAddress(Bug_for_lenolix_7.Address address) {
            this.address = address;
        }

        public Date getBirthDay() {
            return birthDay;
        }

        public void setBirthDay(Date birthDay) {
            this.birthDay = birthDay;
        }

        public java.sql.Date getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(java.sql.Date gmtCreate) {
            this.gmtCreate = gmtCreate;
        }

        public Timestamp getGmtModified() {
            return gmtModified;
        }

        public void setGmtModified(Timestamp gmtModified) {
            this.gmtModified = gmtModified;
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

