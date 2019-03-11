package com.alibaba.json.bvt.annotation;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;
import org.junit.Assert;


public class JsonSeeAlsoTest extends TestCase {
    public void test_seeAlso_dog() throws Exception {
        JsonSeeAlsoTest.Dog dog = new JsonSeeAlsoTest.Dog();
        dog.dogName = "dog1001";
        String text = JSON.toJSONString(dog, WriteClassName);
        Assert.assertEquals("{\"@type\":\"dog\",\"dogName\":\"dog1001\"}", text);
        JsonSeeAlsoTest.Dog dog2 = ((JsonSeeAlsoTest.Dog) (JSON.parseObject(text, JsonSeeAlsoTest.Animal.class)));
        Assert.assertEquals(dog.dogName, dog2.dogName);
    }

    public void test_seeAlso_cat() throws Exception {
        JsonSeeAlsoTest.Cat cat = new JsonSeeAlsoTest.Cat();
        cat.catName = "cat2001";
        String text = JSON.toJSONString(cat, WriteClassName);
        Assert.assertEquals("{\"@type\":\"cat\",\"catName\":\"cat2001\"}", text);
        JsonSeeAlsoTest.Cat cat2 = ((JsonSeeAlsoTest.Cat) (JSON.parseObject(text, JsonSeeAlsoTest.Animal.class)));
        Assert.assertEquals(cat.catName, cat2.catName);
    }

    public void test_seeAlso_tidy() throws Exception {
        JsonSeeAlsoTest.Tidy tidy = new JsonSeeAlsoTest.Tidy();
        tidy.dogName = "dog2001";
        tidy.tidySpecific = "tidy1001";
        String text = JSON.toJSONString(tidy, WriteClassName);
        Assert.assertEquals("{\"@type\":\"tidy\",\"dogName\":\"dog2001\",\"tidySpecific\":\"tidy1001\"}", text);
        JsonSeeAlsoTest.Tidy tidy2 = ((JsonSeeAlsoTest.Tidy) (JSON.parseObject(text, JsonSeeAlsoTest.Animal.class)));
        Assert.assertEquals(tidy.dogName, tidy2.dogName);
    }

    @JSONType(seeAlso = { JsonSeeAlsoTest.Dog.class, JsonSeeAlsoTest.Cat.class })
    public static class Animal {}

    @JSONType(typeName = "dog", seeAlso = { JsonSeeAlsoTest.Tidy.class, JsonSeeAlsoTest.Labrador.class })
    public static class Dog extends JsonSeeAlsoTest.Animal {
        public String dogName;
    }

    @JSONType(typeName = "cat")
    public static class Cat extends JsonSeeAlsoTest.Animal {
        public String catName;
    }

    @JSONType(typeName = "tidy")
    public static class Tidy extends JsonSeeAlsoTest.Dog {
        public String tidySpecific;
    }

    @JSONType(typeName = "labrador")
    public static class Labrador extends JsonSeeAlsoTest.Dog {
        public String tidySpecific;
    }
}

