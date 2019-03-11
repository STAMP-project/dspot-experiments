package com.alibaba.json.bvt.support.spring;


import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.google.common.base.Objects;
import java.util.Arrays;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


public class FastJsonRedisSerializerTest {
    private FastJsonRedisSerializer<FastJsonRedisSerializerTest.User> serializer;

    @Test
    public void test_1() {
        FastJsonRedisSerializerTest.User user = serializer.deserialize(serializer.serialize(new FastJsonRedisSerializerTest.User(1, "??", 25)));
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

    @Test
    public void test_5() {
        FastJsonRedisSerializerTest.User user = new FastJsonRedisSerializerTest.User(1, "??", 25);
        byte[] serializedValue = serializer.serialize(user);
        Arrays.sort(serializedValue);// corrupt serialization result

        Assert.assertNull(serializer.deserialize(serializedValue));
    }

    /**
     * for issue #2147
     */
    @Test
    public void test_6() {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        SerializerFeature[] serializerFeatures = new SerializerFeature[]{ SerializerFeature.WriteClassName };
        fastJsonConfig.setSerializerFeatures(serializerFeatures);
        ParserConfig parserConfig = ParserConfig.getGlobalInstance();
        parserConfig.setAutoTypeSupport(true);
        fastJsonConfig.setParserConfig(parserConfig);
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        Assert.assertNotNull(fastJsonRedisSerializer.getFastJsonConfig());
        fastJsonRedisSerializer.setFastJsonConfig(fastJsonConfig);
        FastJsonRedisSerializerTest.User userSer = new FastJsonRedisSerializerTest.User(1, "??", 25);
        byte[] serializedValue = fastJsonRedisSerializer.serialize(userSer);
        FastJsonRedisSerializerTest.User userDes = ((FastJsonRedisSerializerTest.User) (fastJsonRedisSerializer.deserialize(serializedValue)));
        Assert.assertEquals(userDes.getName(), "??");
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
}

