package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.util.Optional;
import junit.framework.TestCase;
import org.junit.Assert;


public class OptionalTest3 extends TestCase {
    public void test_optional() throws Exception {
        OptionalTest3.UserExt ext = new OptionalTest3.UserExt();
        ext.setValue(Optional.of(123));
        OptionalTest3.User user = new OptionalTest3.User();
        user.setExt(Optional.of(ext));
        String text = JSON.toJSONString(user);
        Assert.assertEquals("{\"ext\":{\"value\":123}}", text);
        OptionalTest3.User user2 = JSON.parseObject(text, OptionalTest3.User.class);
        Assert.assertEquals(user.getExt().get().getValue().get(), user2.getExt().get().getValue().get());
    }

    public static class User {
        private Optional<OptionalTest3.UserExt> ext;

        public Optional<OptionalTest3.UserExt> getExt() {
            return ext;
        }

        public void setExt(Optional<OptionalTest3.UserExt> ext) {
            this.ext = ext;
        }
    }

    public static class UserExt {
        private Optional<Integer> value;

        public Optional<Integer> getValue() {
            return value;
        }

        public void setValue(Optional<Integer> value) {
            this.value = value;
        }
    }
}

