package com.github.jknack.handlebars.springmvc;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HandlebarsApp.class })
public class Issue257 {
    @Autowired
    @Qualifier("viewResolver")
    HandlebarsViewResolver viewResolver;

    @Test
    public void viewResolverShouldHaveBuiltInHelpers() {
        Assert.assertNotNull(viewResolver);
        Assert.assertNotNull(viewResolver.helper("with"));
        Assert.assertNotNull(viewResolver.helper("if"));
        Assert.assertNotNull(viewResolver.helper("unless"));
        Assert.assertNotNull(viewResolver.helper("each"));
        Assert.assertNotNull(viewResolver.helper("embedded"));
        Assert.assertNotNull(viewResolver.helper("block"));
        Assert.assertNotNull(viewResolver.helper("partial"));
        Assert.assertNotNull(viewResolver.helper("precompile"));
        Assert.assertNotNull(viewResolver.helper("i18n"));
        Assert.assertNotNull(viewResolver.helper("i18nJs"));
    }
}

