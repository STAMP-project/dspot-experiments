package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Bug_for_lenolix_10 extends TestCase {
    public void test_for_objectKey() throws Exception {
        Map<Integer, Bug_for_lenolix_10.User> map2 = new HashMap<Integer, Bug_for_lenolix_10.User>();
        Bug_for_lenolix_10.User user = new Bug_for_lenolix_10.User();
        user.setId(1);
        user.setIsBoy(true);
        user.setName("leno.lix");
        // user.setBirthDay(simpleDateFormat.parse("2012-03-07 22:38:21 CST"));
        // user.setGmtCreate(new java.sql.Date(simpleDateFormat.parse("2012-02-03 22:38:21 CST")
        // .getTime()));
        map2.put(1, user);
        String mapJson2 = JSON.toJSONString(map2, WriteClassName, WriteMapNullValue);
        System.out.println(mapJson2);
        Object object2 = JSON.parse(mapJson2);
    }

    public static class User {
        private int id;

        private Boolean isBoy;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Boolean getIsBoy() {
            return isBoy;
        }

        public void setIsBoy(Boolean isBoy) {
            this.isBoy = isBoy;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

