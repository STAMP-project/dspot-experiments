package com.alibaba.json.bvt.bug;


import JSON.defaultTimeZone;
import SerializerFeature.WriteClassName;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Bug_for_lenolix_8 extends TestCase {
    public void test_for_objectKey() throws Exception {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", JSON.defaultLocale);
        simpleDateFormat.setTimeZone(defaultTimeZone);
        Map<Integer, Bug_for_lenolix_8.User> map = new HashMap<Integer, Bug_for_lenolix_8.User>();
        Bug_for_lenolix_8.User user = new Bug_for_lenolix_8.User();
        user.setId(1);
        user.setIsBoy(true);
        user.setName("leno.lix");
        user.setBirthDay(simpleDateFormat.parse("2012-03-07 22:38:21"));
        user.setGmtCreate(new Date(simpleDateFormat.parse("2012-02-03 22:38:21").getTime()));
        map.put(1, user);
        String mapJson = JSON.toJSONString(map, WriteClassName, WriteMapNullValue);
        System.out.println(mapJson);
        Object object = JSON.parse(mapJson);
    }

    public static class User implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 6192533820796587011L;

        private Integer id;

        private String name;

        private Boolean isBoy;

        private java.util.Date birthDay;

        private Date gmtCreate;

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

        public java.util.Date getBirthDay() {
            return birthDay;
        }

        public void setBirthDay(java.util.Date birthDay) {
            this.birthDay = birthDay;
        }

        public Date getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(Date gmtCreate) {
            this.gmtCreate = gmtCreate;
        }

        public Timestamp getGmtModified() {
            return gmtModified;
        }

        public void setGmtModified(Timestamp gmtModified) {
            this.gmtModified = gmtModified;
        }
    }
}

