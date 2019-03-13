package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;


public class Bug_for_jared1 extends TestCase {
    public void test_for_jared1() throws Exception {
        Bug_for_jared1.User user = new Bug_for_jared1.User();
        String text = JSON.toJSONString(user);
        System.out.println(text);
        JSON.parseObject(text, Bug_for_jared1.User.class);
    }

    // ????
    public static class User implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private Integer id;

        private String acount;

        private String password;

        private Set<Bug_for_jared1.Crowd> crowds = new HashSet<Bug_for_jared1.Crowd>();

        private Set<Bug_for_jared1.User> friends = new HashSet<Bug_for_jared1.User>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getAcount() {
            return acount;
        }

        public void setAcount(String acount) {
            this.acount = acount;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Set<Bug_for_jared1.Crowd> getCrowds() {
            return crowds;
        }

        public void setCrowds(Set<Bug_for_jared1.Crowd> crowds) {
            this.crowds = crowds;
        }

        public Set<Bug_for_jared1.User> getFriends() {
            return friends;
        }

        public void setFriends(Set<Bug_for_jared1.User> friends) {
            this.friends = friends;
        }
    }

    public static class Crowd {}
}

