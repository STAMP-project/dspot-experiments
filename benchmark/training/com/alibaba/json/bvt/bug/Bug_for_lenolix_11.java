package com.alibaba.json.bvt.bug;


import JSON.defaultTimeZone;
import SerializerFeature.WriteClassName;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.text.SimpleDateFormat;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_lenolix_11 extends TestCase {
    public void test_for_objectKey() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy", JSON.defaultLocale);
        simpleDateFormat.setTimeZone(defaultTimeZone);
        String simpleDateFormatJson = JSON.toJSONString(simpleDateFormat, WriteClassName, WriteMapNullValue);
        System.out.println(simpleDateFormatJson);
        SimpleDateFormat format = ((SimpleDateFormat) (JSON.parse(simpleDateFormatJson)));
        Assert.assertEquals("MM-dd-yyyy", format.toPattern());
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

