package com.baeldug.groovyconfig;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.GenericGroovyApplicationContext;


public class GroovyConfigurationUnitTest {
    private static final String FILE_NAME = "GroovyBeanConfig.groovy";

    private static final String FILE_PATH = "src/main/java/com/baeldug/groovyconfig/";

    @Test
    public void whenGroovyConfig_thenCorrectPerson() throws Exception {
        GenericGroovyApplicationContext ctx = new GenericGroovyApplicationContext();
        ctx.load((("file:" + (getPathPart())) + (GroovyConfigurationUnitTest.FILE_NAME)));
        ctx.refresh();
        JavaPersonBean j = ctx.getBean(JavaPersonBean.class);
        Assert.assertEquals("32", j.getAge());
        Assert.assertEquals("blue", j.getEyesColor());
        Assert.assertEquals("black", j.getHairColor());
    }

    @Test
    public void whenGroovyConfig_thenCorrectListLength() throws Exception {
        GenericGroovyApplicationContext ctx = new GenericGroovyApplicationContext();
        ctx.load((("file:" + (getPathPart())) + (GroovyConfigurationUnitTest.FILE_NAME)));
        ctx.refresh();
        BandsBean bb = ctx.getBean(BandsBean.class);
        Assert.assertEquals(3, bb.getBandsList().size());
    }
}

