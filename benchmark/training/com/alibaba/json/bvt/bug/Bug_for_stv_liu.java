package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_stv_liu extends TestCase {
    public void test() {
        Bug_for_stv_liu.User user = new Bug_for_stv_liu.User();
        user.setId("1");
        user.setUsername("test");
        String json = JSON.toJSONString(user, WriteClassName);
        user = ((Bug_for_stv_liu.User) (JSON.parse(json)));// ?????

        Assert.assertNotNull(user);
    }

    public static interface IdEntity<T extends Serializable> extends Serializable {
        T getId();

        void setId(T id);
    }

    public static class BaseEntity implements Bug_for_stv_liu.IdEntity<String> {
        private static final long serialVersionUID = 1L;

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class User extends Bug_for_stv_liu.BaseEntity {
        private String username;

        /**
         *
         *
         * @return the username
         */
        public String getUsername() {
            return username;
        }

        /**
         *
         *
         * @param username
         * 		the username to set
         */
        public void setUsername(String username) {
            this.username = username;
        }
    }
}

