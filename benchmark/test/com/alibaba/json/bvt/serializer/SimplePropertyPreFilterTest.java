package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class SimplePropertyPreFilterTest extends TestCase {
    private SimplePropertyPreFilterTest.VO vo;

    private SimplePropertyPreFilterTest.A a;

    private Map<String, Object> map;

    public void test_name() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(SimplePropertyPreFilterTest.VO.class, "name");
        Assert.assertEquals("{\"name\":\"sandzhangtoo\"}", JSON.toJSONString(vo, filter));
    }

    public void test_name_0() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("name");
        Assert.assertEquals("{\"name\":\"sandzhangtoo\"}", JSON.toJSONString(vo, filter));
    }

    public void test_name_a() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(SimplePropertyPreFilterTest.VO.class, "name");
        Assert.assertEquals(SimplePropertyPreFilterTest.VO.class, filter.getClazz());
        Assert.assertEquals(1, filter.getIncludes().size());
        Assert.assertTrue(filter.apply(null, null, null));
        String text = JSON.toJSONString(a, filter);
        Assert.assertTrue((("{\"id\":123,\"name\":\"sandzhangtoo\"}".equals(text)) || ("{\"name\":\"sandzhangtoo\",\"id\":123}".equals(text))));
    }

    public void test_name_a1() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("name");
        Assert.assertEquals("{\"name\":\"sandzhangtoo\"}", JSON.toJSONString(a, filter));
    }

    public void test_id() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(SimplePropertyPreFilterTest.VO.class, "id");
        Assert.assertEquals("{\"id\":123}", JSON.toJSONString(vo, filter));
    }

    public void test_id_0() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("id");
        Assert.assertEquals("{\"id\":123}", JSON.toJSONString(vo, filter));
    }

    public void test_map() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(SimplePropertyPreFilterTest.VO.class, "name");
        String text = JSON.toJSONString(map, filter);
        Assert.assertTrue((("{\"id\":123,\"name\":\"sandzhangtoo\"}".equals(text)) || ("{\"name\":\"sandzhangtoo\",\"id\":123}".equals(text))));
    }

    public void test_map_id() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("id");
        Assert.assertEquals("{\"id\":123}", JSON.toJSONString(map, filter));
    }

    public void test_map_name() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("name");
        Assert.assertEquals("{\"name\":\"sandzhangtoo\"}", JSON.toJSONString(map, filter));
    }

    public void test_all() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(SimplePropertyPreFilterTest.VO.class);
        String text = JSON.toJSONString(vo, filter);
        Assert.assertTrue((("{\"id\":123,\"name\":\"sandzhangtoo\"}".equals(text)) || ("{\"name\":\"sandzhangtoo\",\"id\":123}".equals(text))));
    }

    public void test_all_map() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(SimplePropertyPreFilterTest.VO.class);
        String text = JSON.toJSONString(map, filter);
        Assert.assertTrue((("{\"id\":123,\"name\":\"sandzhangtoo\"}".equals(text)) || ("{\"name\":\"sandzhangtoo\",\"id\":123}".equals(text))));
    }

    public void test_exclude_id() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(SimplePropertyPreFilterTest.VO.class);
        filter.getExcludes().add("id");
        Assert.assertEquals("{\"name\":\"sandzhangtoo\"}", JSON.toJSONString(vo, filter));
    }

    public void test_exclude_id_map() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(SimplePropertyPreFilterTest.VO.class);
        filter.getExcludes().add("id");
        Assert.assertEquals("{\"name\":\"sandzhangtoo\"}", JSON.toJSONString(vo, filter));
    }

    public void test_exclude_name() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(SimplePropertyPreFilterTest.VO.class);
        filter.getExcludes().add("name");
        Assert.assertEquals("{\"id\":123}", JSON.toJSONString(vo, filter));
    }

    public static class VO {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class A {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

