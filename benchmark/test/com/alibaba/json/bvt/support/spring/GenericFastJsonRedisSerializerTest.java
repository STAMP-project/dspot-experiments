package com.alibaba.json.bvt.support.spring;


import IOUtils.UTF8;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.redis.serializer.SerializationException;


public class GenericFastJsonRedisSerializerTest {
    private GenericFastJsonRedisSerializer serializer;

    @Test
    public void test_1() {
        GenericFastJsonRedisSerializerTest.User user = ((GenericFastJsonRedisSerializerTest.User) (serializer.deserialize(serializer.serialize(new GenericFastJsonRedisSerializerTest.User(1, "??", 25)))));
        Assert.assertTrue(Objects.equal(user.getId(), 1));
        Assert.assertTrue(Objects.equal(user.getName(), "??"));
        Assert.assertTrue(Objects.equal(user.getAge(), 25));
    }

    @Test
    public void test_2() {
        Assert.assertThat(serializer.serialize(null), Is.is(new byte[0]));
    }

    @Test
    public void test_3() {
        Assert.assertThat(serializer.deserialize(new byte[0]), IsNull.nullValue());
    }

    @Test
    public void test_4() {
        Assert.assertThat(serializer.deserialize(null), IsNull.nullValue());
    }

    @Test(expected = SerializationException.class)
    public void test_5() {
        GenericFastJsonRedisSerializerTest.User user = new GenericFastJsonRedisSerializerTest.User(1, "??", 25);
        byte[] serializedValue = serializer.serialize(user);
        Arrays.sort(serializedValue);// corrupt serialization result

        serializer.deserialize(serializedValue);
    }

    /**
     * for issue #2155
     */
    @Test
    public void test_6() {
        GenericFastJsonRedisSerializerTest.BaseResult<List<String>> baseResult = new GenericFastJsonRedisSerializerTest.BaseResult<List<String>>();
        baseResult.setCode("1000");
        baseResult.setMsg("success");
        baseResult.setData(Lists.newArrayList("??1", "??2", "??3"));
        GenericFastJsonRedisSerializer genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
        byte[] bytes = genericFastJsonRedisSerializer.serialize(baseResult);
        GenericFastJsonRedisSerializerTest.BaseResult<List<String>> baseResult2 = ((GenericFastJsonRedisSerializerTest.BaseResult<List<String>>) (genericFastJsonRedisSerializer.deserialize(bytes)));
        Assert.assertEquals(baseResult2.getCode(), "1000");
        Assert.assertEquals(baseResult2.getData().size(), 3);
        String json = "{\n" + ((((((((((("\"@type\": \"com.alibaba.json.bvt.support.spring.GenericFastJsonRedisSerializerTest$BaseResult\",\n" + "\"code\": \"1000\",\n") + "\"data\": [\n") + "\"\u6309\u624b\u52a8\u63a7\u5236\u6309\u94ae\",\n") + "\"\u4e0d\u505c\u673a\",\n") + "\"\u4e0d\u8f6c\u52a8\",\n") + "\"\u4f20\u52a8\u8f74\u632f\u52a8\u5927\",\n") + "\"\u7b2c\u4e00\u63a8\u8fdb\u5668\",\n") + "\"\u7535\u673a\u4e0d\u8fd0\u884c\",\n") + "],\n") + "\"msg\": \"success\"\n") + "}");
        GenericFastJsonRedisSerializerTest.BaseResult<List<String>> baseResult3 = ((GenericFastJsonRedisSerializerTest.BaseResult<List<String>>) (genericFastJsonRedisSerializer.deserialize(json.getBytes(UTF8))));
        Assert.assertEquals(baseResult3.getCode(), "1000");
        Assert.assertEquals(baseResult3.getData().size(), 6);
    }

    static class User {
        private Integer id;

        private String name;

        private Integer age;

        public User() {
        }

        public User(Integer id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

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

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    static class BaseResult<T> {
        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        private String msg;

        private String code;

        private T data;
    }
}

