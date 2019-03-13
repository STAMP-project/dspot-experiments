package cn.hutool.extra.template;


import cn.hutool.core.lang.Dict;
import cn.hutool.extra.template.TemplateConfig.ResourceMode;
import cn.hutool.extra.template.engine.beetl.BeetlEngine;
import cn.hutool.extra.template.engine.enjoy.EnjoyEngine;
import cn.hutool.extra.template.engine.freemarker.FreemarkerEngine;
import cn.hutool.extra.template.engine.thymeleaf.ThymeleafEngine;
import cn.hutool.extra.template.engine.velocity.VelocityEngine;
import org.junit.Assert;
import org.junit.Test;


/**
 * ????????
 *
 * @author looly
 */
public class TemplateUtilTest {
    @Test
    public void createEngineTest() {
        // ??????????Beetl
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig());
        Template template = engine.getTemplate("hello,${name}");
        String result = template.render(Dict.create().set("name", "hutool"));
        Assert.assertEquals("hello,hutool", result);
    }

    @Test
    public void beetlEngineTest() {
        // ?????
        TemplateEngine engine = new BeetlEngine(new TemplateConfig("templates"));
        Template template = engine.getTemplate("hello,${name}");
        String result = template.render(Dict.create().set("name", "hutool"));
        Assert.assertEquals("hello,hutool", result);
        // classpath?????
        engine = new BeetlEngine(new TemplateConfig("templates", ResourceMode.CLASSPATH));
        Template template2 = engine.getTemplate("beetl_test.btl");
        String result2 = template2.render(Dict.create().set("name", "hutool"));
        Assert.assertEquals("hello,hutool", result2);
    }

    @Test
    public void rythmEngineTest() {
        // ?????
        TemplateEngine engine = new cn.hutool.extra.template.engine.rythm.RythmEngine(new TemplateConfig("templates"));
        Template template = engine.getTemplate("hello,@name");
        String result = template.render(Dict.create().set("name", "hutool"));
        Assert.assertEquals("hello,hutool", result);
        // classpath?????
        Template template2 = engine.getTemplate("rythm_test.tmpl");
        String result2 = template2.render(Dict.create().set("name", "hutool"));
        Assert.assertEquals("hello,hutool", result2);
    }

    @Test
    public void freemarkerEngineTest() {
        // ?????
        TemplateEngine engine = new FreemarkerEngine(new TemplateConfig("templates", ResourceMode.STRING));
        Template template = engine.getTemplate("hello,${name}");
        String result = template.render(Dict.create().set("name", "hutool"));
        Assert.assertEquals("hello,hutool", result);
        // ClassPath??
        engine = new FreemarkerEngine(new TemplateConfig("templates", ResourceMode.CLASSPATH));
        template = engine.getTemplate("freemarker_test.ftl");
        result = template.render(Dict.create().set("name", "hutool"));
        Assert.assertEquals("hello,hutool", result);
    }

    @Test
    public void velocityEngineTest() {
        // ?????
        TemplateEngine engine = new VelocityEngine(new TemplateConfig("templates", ResourceMode.STRING));
        Template template = engine.getTemplate("??,$name");
        String result = template.render(Dict.create().set("name", "hutool"));
        Assert.assertEquals("??,hutool", result);
        // ClassPath??
        engine = new VelocityEngine(new TemplateConfig("templates", ResourceMode.CLASSPATH));
        template = engine.getTemplate("templates/velocity_test.vtl");
        result = template.render(Dict.create().set("name", "hutool"));
        Assert.assertEquals("??,hutool", result);
    }

    @Test
    public void enjoyEngineTest() {
        // ?????
        TemplateEngine engine = new EnjoyEngine(new TemplateConfig("templates"));
        Template template = engine.getTemplate("#(x + 123)");
        String result = template.render(Dict.create().set("x", 1));
        Assert.assertEquals("124", result);
        // ClassPath??
        engine = new EnjoyEngine(new TemplateConfig("templates", ResourceMode.CLASSPATH));
        template = engine.getTemplate("enjoy_test.etl");
        result = template.render(Dict.create().set("x", 1));
        Assert.assertEquals("124", result);
    }

    @Test
    public void thymeleafEngineTest() {
        // ?????
        TemplateEngine engine = new ThymeleafEngine(new TemplateConfig("templates"));
        Template template = engine.getTemplate("<h3 th:text=\"${message}\"></h3>");
        String result = template.render(Dict.create().set("message", "Hutool"));
        Assert.assertEquals("<h3>Hutool</h3>", result);
        // ClassPath??
        engine = new ThymeleafEngine(new TemplateConfig("templates", ResourceMode.CLASSPATH));
        template = engine.getTemplate("thymeleaf_test.ttl");
        result = template.render(Dict.create().set("message", "Hutool"));
        Assert.assertEquals("<h3>Hutool</h3>", result);
    }
}

