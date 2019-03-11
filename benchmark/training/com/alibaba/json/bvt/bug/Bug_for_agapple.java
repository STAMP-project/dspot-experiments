package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.Properties;
import junit.framework.TestCase;


public class Bug_for_agapple extends TestCase {
    public void test_for_agapple() throws Exception {
        Bug_for_agapple.Entity entity = new Bug_for_agapple.Entity();
        entity.setProperties(new Properties());
        String text = JSON.toJSONString(entity);
        JSON.parseObject(text, Bug_for_agapple.Entity.class);
    }

    private static class Entity {
        private Properties properties;

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }
    }
}

