package com.vip.vjtools.vjkit.mapper;


import JsonMapper.INSTANCE;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;


/**
 * ??Jackson?Object,Map,List,??,??,????????. ?????showcase??JsonDemo.
 */
public class JsonMapperTest {
    /**
     * ?????/???Json???.
     */
    @Test
    public void toJson() throws Exception {
        // Bean
        JsonMapperTest.TestBean bean = new JsonMapperTest.TestBean("A");
        String beanString = INSTANCE.toJson(bean);
        System.out.println(("Bean:" + beanString));
        assertThat(beanString).isEqualTo("{\"name\":\"A\",\"defaultValue\":\"hello\",\"nullValue\":null,\"emptyValue\":[]}");
        // Map
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("name", "A");
        map.put("age", 2);
        String mapString = INSTANCE.toJson(map);
        System.out.println(("Map:" + mapString));
        assertThat(mapString).isEqualTo("{\"name\":\"A\",\"age\":2}");
        // List<String>
        List<String> stringList = Lists.newArrayList("A", "B", "C");
        String listString = INSTANCE.toJson(stringList);
        System.out.println(("String List:" + listString));
        assertThat(listString).isEqualTo("[\"A\",\"B\",\"C\"]");
        // List<Bean>
        List<JsonMapperTest.TestBean> beanList = Lists.newArrayList(new JsonMapperTest.TestBean("A"), new JsonMapperTest.TestBean("B"));
        String beanListString = INSTANCE.toJson(beanList);
        System.out.println(("Bean List:" + beanListString));
        assertThat(beanListString).isEqualTo("[{\"name\":\"A\",\"defaultValue\":\"hello\",\"nullValue\":null,\"emptyValue\":[]},{\"name\":\"B\",\"defaultValue\":\"hello\",\"nullValue\":null,\"emptyValue\":[]}]");
        // Bean[]
        JsonMapperTest.TestBean[] beanArray = new JsonMapperTest.TestBean[]{ new JsonMapperTest.TestBean("A"), new JsonMapperTest.TestBean("B") };
        String beanArrayString = INSTANCE.toJson(beanArray);
        System.out.println(("Array List:" + beanArrayString));
        assertThat(beanArrayString).isEqualTo("[{\"name\":\"A\",\"defaultValue\":\"hello\",\"nullValue\":null,\"emptyValue\":[]},{\"name\":\"B\",\"defaultValue\":\"hello\",\"nullValue\":null,\"emptyValue\":[]}]");
    }

    /**
     * ?Json?????????/??.
     */
    @Test
    public void fromJson() throws Exception {
        // Bean
        String beanString = "{\"name\":\"A\"}";
        JsonMapperTest.TestBean bean = INSTANCE.fromJson(beanString, JsonMapperTest.TestBean.class);
        System.out.println(("Bean:" + bean));
        // Map
        String mapString = "{\"name\":\"A\",\"age\":2}";
        Map<String, Object> map = INSTANCE.fromJson(mapString, HashMap.class);
        System.out.println("Map:");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println((((entry.getKey()) + " ") + (entry.getValue())));
        }
        // List<String>
        String listString = "[\"A\",\"B\",\"C\"]";
        List<String> stringList = INSTANCE.fromJson(listString, List.class);
        System.out.println("String List:");
        for (String element : stringList) {
            System.out.println(element);
        }
        // List<Bean>
        String beanListString = "[{\"name\":\"A\"},{\"name\":\"B\"}]";
        List<JsonMapperTest.TestBean> beanList = INSTANCE.fromJson(beanListString, INSTANCE.buildCollectionType(List.class, JsonMapperTest.TestBean.class));
        System.out.println("Bean List:");
        for (JsonMapperTest.TestBean element : beanList) {
            System.out.println(element);
        }
    }

    /**
     * ???????,????,Empty???,"null"??????.
     */
    @Test
    public void nullAndEmpty() {
        // toJson?? //
        // Null Bean
        JsonMapperTest.TestBean nullBean = null;
        String nullBeanString = INSTANCE.toJson(nullBean);
        assertThat(nullBeanString).isEqualTo("null");
        // Empty List
        List<String> emptyList = Lists.newArrayList();
        String emptyListString = INSTANCE.toJson(emptyList);
        assertThat(emptyListString).isEqualTo("[]");
        // fromJson?? //
        // Null String for Bean
        JsonMapperTest.TestBean nullBeanResult = INSTANCE.fromJson(null, JsonMapperTest.TestBean.class);
        assertThat(nullBeanResult).isNull();
        nullBeanResult = INSTANCE.fromJson("null", JsonMapperTest.TestBean.class);
        assertThat(nullBeanResult).isNull();
        nullBeanResult = INSTANCE.fromJson("", JsonMapperTest.TestBean.class);
        assertThat(nullBeanResult).isNull();
        nullBeanResult = INSTANCE.fromJson("{}", JsonMapperTest.TestBean.class);
        assertThat(nullBeanResult).isNotNull();
        assertThat(nullBeanResult.getDefaultValue()).isEqualTo("hello");
        // Null/Empty String for List
        List nullListResult = INSTANCE.fromJson(null, List.class);
        assertThat(nullListResult).isNull();
        nullListResult = INSTANCE.fromJson("null", List.class);
        assertThat(nullListResult).isNull();
        nullListResult = INSTANCE.fromJson("[]", List.class);
        assertThat(nullListResult).isEmpty();
    }

    /**
     * ???????Mapper.
     */
    @Test
    public void threeTypeMappers() {
        // ??????
        JsonMapper normalBinder = JsonMapper.defaultMapper();
        JsonMapperTest.TestBean bean = new JsonMapperTest.TestBean("A");
        assertThat(normalBinder.toJson(bean)).isEqualTo("{\"name\":\"A\",\"defaultValue\":\"hello\",\"nullValue\":null,\"emptyValue\":[]}");
        // ???nullValue??
        JsonMapper nonNullMapper = JsonMapper.nonNullMapper();
        assertThat(nonNullMapper.toJson(bean)).isEqualTo("{\"name\":\"A\",\"defaultValue\":\"hello\",\"emptyValue\":[]}");
        // ???nullValue?empty???
        JsonMapper nonEmptyMapper = JsonMapper.nonEmptyMapper();
        assertThat(nonEmptyMapper.toJson(bean)).isEqualTo("{\"name\":\"A\",\"defaultValue\":\"hello\"}");
        JsonMapperTest.TestBean nonEmptyBean = nonEmptyMapper.fromJson("{\"name\":\"A\",\"defaultValue\":\"hello\"}", JsonMapperTest.TestBean.class);
        assertThat(nonEmptyBean.getEmptyValue()).isEmpty();
    }

    @Test
    public void jsonp() {
        JsonMapperTest.TestBean bean = new JsonMapperTest.TestBean("A");
        String jsonp = JsonMapper.nonEmptyMapper().toJsonP("haha", bean);
        assertThat(jsonp).isEqualTo("haha({\"name\":\"A\",\"defaultValue\":\"hello\"})");
    }

    @Test
    public void update() {
        JsonMapperTest.TestBean bean = new JsonMapperTest.TestBean("A");
        bean.setDefaultValue("lalala");
        INSTANCE.update("{\"name\":\"B\"}", bean);
        assertThat(bean.getName()).isEqualTo("B");
        assertThat(bean.getDefaultValue()).isEqualTo("lalala");
    }

    public static class TestBean {
        private String name;

        private String defaultValue = "hello";

        private String nullValue = null;

        private List<String> emptyValue = new ArrayList();

        public TestBean() {
        }

        public TestBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getNullValue() {
            return nullValue;
        }

        public void setNullValue(String nullValue) {
            this.nullValue = nullValue;
        }

        public List<String> getEmptyValue() {
            return emptyValue;
        }

        public void setEmptyValue(List<String> emptyValue) {
            this.emptyValue = emptyValue;
        }

        @Override
        public String toString() {
            return ((((((("TestBean [name=" + (name)) + ", defaultValue=") + (defaultValue)) + ", nullValue=") + (nullValue)) + ", emptyValue=") + (emptyValue)) + "]";
        }
    }
}

