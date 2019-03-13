package com.alibaba.json.bvt.taobao;


import SerializerFeature.WriteMapNullValue.mask;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.BeanContext;
import com.alibaba.fastjson.serializer.ContextValueFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import junit.framework.TestCase;
import org.junit.Assert;


public class MTopTest extends TestCase {
    public void test_for_mtop() throws Exception {
        MTopTest.P0 p = new MTopTest.P0();
        p.model = new MTopTest.Model();
        ContextValueFilter valueFilter = new ContextValueFilter() {
            @Override
            public Object process(BeanContext context, Object object, String name, Object value) {
                if (value instanceof MTopTest.Model) {
                    Assert.assertEquals(MTopTest.P0.class, context.getBeanClass());
                    Assert.assertNotNull(context.getField());
                    Assert.assertNotNull(context.getMethod());
                    Assert.assertEquals("model", context.getName());
                    Assert.assertEquals(MTopTest.Model.class, context.getFieldClass());
                    Assert.assertEquals(MTopTest.Model.class, context.getFieldType());
                    Assert.assertEquals(mask, context.getFeatures());
                    Field field = context.getField();
                    Assert.assertNotNull(field.getAnnotation(MTopTest.UrlIdentify.class));
                    Assert.assertNotNull(context.getAnnation(MTopTest.UrlIdentify.class));
                    return value;
                }
                return value;
            }
        };
        JSON.toJSONString(p, valueFilter);
    }

    private static class P0 {
        @JSONField(serialzeFeatures = SerializerFeature.WriteMapNullValue)
        @MTopTest.UrlIdentify(schema = "xxxx")
        private MTopTest.Model model;

        public MTopTest.Model getModel() {
            return model;
        }

        public void setModel(MTopTest.Model model) {
            this.model = model;
        }
    }

    public static class Model {
        private int id;

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface UrlIdentify {
        String schema();
    }
}

