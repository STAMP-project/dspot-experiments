package com.alibaba.json.bvt.naming;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class NamingSerTest extends TestCase {
    public void test_snake() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        NamingSerTest.Model model = new NamingSerTest.Model();
        model.personId = 1001;
        String text = JSON.toJSONString(model, config);
        Assert.assertEquals("{\"person_id\":1001}", text);
        ParserConfig parserConfig = new ParserConfig();
        parserConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        NamingSerTest.Model model2 = JSON.parseObject(text, NamingSerTest.Model.class, parserConfig);
        Assert.assertEquals(model.personId, model2.personId);
        NamingSerTest.Model model3 = JSON.parseObject(text, NamingSerTest.Model.class);
        Assert.assertEquals(model.personId, model3.personId);
    }

    public void test_kebab() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.KebabCase;
        NamingSerTest.Model model = new NamingSerTest.Model();
        model.personId = 1001;
        String text = JSON.toJSONString(model, config);
        Assert.assertEquals("{\"person-id\":1001}", text);
        ParserConfig parserConfig = new ParserConfig();
        parserConfig.propertyNamingStrategy = PropertyNamingStrategy.KebabCase;
        NamingSerTest.Model model2 = JSON.parseObject(text, NamingSerTest.Model.class, parserConfig);
        Assert.assertEquals(model.personId, model2.personId);
        NamingSerTest.Model model3 = JSON.parseObject(text, NamingSerTest.Model.class);
        Assert.assertEquals(model.personId, model3.personId);
    }

    public void test_pascal() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.PascalCase;
        NamingSerTest.Model model = new NamingSerTest.Model();
        model.personId = 1001;
        String text = JSON.toJSONString(model, config);
        Assert.assertEquals("{\"PersonId\":1001}", text);
        ParserConfig parserConfig = new ParserConfig();
        parserConfig.propertyNamingStrategy = PropertyNamingStrategy.PascalCase;
        NamingSerTest.Model model2 = JSON.parseObject(text, NamingSerTest.Model.class, parserConfig);
        Assert.assertEquals(model.personId, model2.personId);
        NamingSerTest.Model model3 = JSON.parseObject(text, NamingSerTest.Model.class);
        Assert.assertEquals(model.personId, model3.personId);
    }

    public void test_camel() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.CamelCase;
        NamingSerTest.Model model = new NamingSerTest.Model();
        model.personId = 1001;
        String text = JSON.toJSONString(model, config);
        Assert.assertEquals("{\"personId\":1001}", text);
        ParserConfig parserConfig = new ParserConfig();
        parserConfig.propertyNamingStrategy = PropertyNamingStrategy.CamelCase;
        NamingSerTest.Model model2 = JSON.parseObject(text, NamingSerTest.Model.class, parserConfig);
        Assert.assertEquals(model.personId, model2.personId);
        NamingSerTest.Model model3 = JSON.parseObject(text, NamingSerTest.Model.class);
        Assert.assertEquals(model.personId, model3.personId);
    }

    public static class Model {
        public int personId;
    }
}

