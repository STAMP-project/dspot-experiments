package com.github.jknack.handlebars.springmvc;


import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.View;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HandlebarsApp.class })
public class Issue292 {
    @Autowired
    @Qualifier("viewResolver")
    HandlebarsViewResolver viewResolver;

    @Autowired
    @Qualifier("viewResolverWithoutMessageHelper")
    HandlebarsViewResolver viewResolverWithoutMessageHelper;

    @Test
    public void getTemplate() throws Exception {
        Assert.assertNotNull(viewResolver);
        View view = viewResolver.resolveViewName("template", Locale.getDefault());
        Assert.assertTrue((view instanceof HandlebarsView));
        Assert.assertNotNull(((HandlebarsView) (view)).getTemplate());
    }
}

