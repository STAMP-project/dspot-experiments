package com.github.jknack.handlebars.springmvc;


import com.github.jknack.handlebars.Handlebars;
import java.io.IOException;
import java.util.Locale;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.View;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HandlebarsApp.class })
public class HandlebarsViewResolverIntegrationTest {
    @Autowired
    @Qualifier("viewResolver")
    HandlebarsViewResolver viewResolver;

    @Autowired
    @Qualifier("viewResolverWithoutMessageHelper")
    HandlebarsViewResolver viewResolverWithoutMessageHelper;

    @Autowired
    @Qualifier("parameterizedHandlebarsViewResolver")
    HandlebarsViewResolver parameterizedHandlebarsViewResolver;

    @Test
    public void getHandlebars() throws Exception {
        Assert.assertNotNull(viewResolver);
        Assert.assertNotNull(viewResolver.getHandlebars());
    }

    @Test
    public void resolveView() throws Exception {
        Assert.assertNotNull(viewResolver);
        View view = viewResolver.resolveViewName("template", Locale.getDefault());
        Assert.assertNotNull(view);
        Assert.assertEquals(HandlebarsView.class, view.getClass());
    }

    @Test
    public void resolveViewWithParameterized() throws Exception {
        Assert.assertNotNull(parameterizedHandlebarsViewResolver);
        View view = parameterizedHandlebarsViewResolver.resolveViewName("template", Locale.getDefault());
        Assert.assertNotNull(view);
        Assert.assertEquals(HandlebarsView.class, view.getClass());
    }

    @Test
    public void resolveViewWithFallback() throws Exception {
        try {
            Assert.assertNotNull(viewResolver);
            viewResolver.setFailOnMissingFile(false);
            View view = viewResolver.resolveViewName("invalidView", Locale.getDefault());
            Assert.assertNull(view);
        } finally {
            viewResolver.setFailOnMissingFile(true);
        }
    }

    @Test
    public void resolveViewWithFallbackParameterized() throws Exception {
        try {
            Assert.assertNotNull(parameterizedHandlebarsViewResolver);
            parameterizedHandlebarsViewResolver.setFailOnMissingFile(false);
            View view = parameterizedHandlebarsViewResolver.resolveViewName("invalidView", Locale.getDefault());
            Assert.assertNull(view);
        } finally {
            parameterizedHandlebarsViewResolver.setFailOnMissingFile(true);
        }
    }

    @Test(expected = IOException.class)
    public void failToResolve() throws Exception {
        try {
            Assert.assertNotNull(viewResolver);
            viewResolver.setFailOnMissingFile(true);
            viewResolver.resolveViewName("invalidView", Locale.getDefault());
        } finally {
            viewResolver.setFailOnMissingFile(true);
        }
    }

    @Test(expected = IOException.class)
    public void failToResolveParameterized() throws Exception {
        try {
            Assert.assertNotNull(parameterizedHandlebarsViewResolver);
            parameterizedHandlebarsViewResolver.setFailOnMissingFile(true);
            parameterizedHandlebarsViewResolver.resolveViewName("invalidView", Locale.getDefault());
        } finally {
            parameterizedHandlebarsViewResolver.setFailOnMissingFile(true);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void getHandlebarsFail() throws Exception {
        Assert.assertNotNull(new HandlebarsViewResolver().getHandlebars());
    }

    @Test
    public void messageHelper() throws Exception {
        Assert.assertNotNull(viewResolver);
        Handlebars handlebars = viewResolver.getHandlebars();
        Assert.assertEquals("Handlebars Spring MVC!", handlebars.compileInline("{{message \"hello\"}}").apply(new Object()));
        Assert.assertEquals("Handlebars Spring MVC!", handlebars.compileInline("{{i18n \"hello\"}}").apply(new Object()));
    }

    @Test
    public void messageHelperWithParams() throws Exception {
        Assert.assertNotNull(viewResolver);
        Handlebars handlebars = viewResolver.getHandlebars();
        Assert.assertEquals("Hello Handlebars!", handlebars.compileInline("{{message \"hello.0\" \"Handlebars\"}}").apply(new Object()));
        Assert.assertEquals("Hello Handlebars!", handlebars.compileInline("{{i18n \"hello.0\" \"Handlebars\"}}").apply(new Object()));
        Assert.assertEquals("Hello Spring MVC!", handlebars.compileInline("{{message \"hello.0\" \"Spring MVC\"}}").apply(new Object()));
        Assert.assertEquals("Hello Spring MVC!", handlebars.compileInline("{{i18n \"hello.0\" \"Spring MVC\"}}").apply(new Object()));
    }

    @Test
    public void i18nJs() throws Exception {
        // maven classpath
        String expected = "<script type=\'text/javascript\'>\n" + (((((("  /* Spanish (Argentina) */\n" + "  I18n.translations = I18n.translations || {};\n") + "  I18n.translations[\'es_AR\'] = {\n") + "    \"hello\": \"Handlebars Spring MVC!\",\n") + "    \"hello.0\": \"Hello {{arg0}}!\"\n") + "  };\n") + "</script>\n");
        Assert.assertNotNull(viewResolver);
        Handlebars handlebars = viewResolver.getHandlebars();
        String output = handlebars.compileInline("{{i18nJs \"es_AR\"}}").apply(new Object());
        try {
            // maven classpath
            Assert.assertEquals(expected, output);
        } catch (ComparisonFailure ex) {
            try {
                // eclipse classpath
                Assert.assertEquals(("<script type=\'text/javascript\'>\n" + (((((("  /* Spanish (Argentina) */\n" + "  I18n.translations = I18n.translations || {};\n") + "  I18n.translations[\'es_AR\'] = {\n") + "    \"hello\": \"Hola\",\n") + "    \"hello.0\": \"Hello {{arg0}}!\"\n") + "  };\n") + "</script>\n")), output);
            } catch (ComparisonFailure java18) {
                // java 1.8
                Assert.assertEquals(("<script type=\'text/javascript\'>\n" + (((((("  /* Spanish (Argentina) */\n" + "  I18n.translations = I18n.translations || {};\n") + "  I18n.translations[\'es_AR\'] = {\n") + "    \"hello.0\": \"Hello {{arg0}}!\",\n") + "    \"hello\": \"Handlebars Spring MVC!\"\n") + "  };\n") + "</script>\n")), output);
            }
        }
    }

    @Test
    public void messageHelperWithDefaultValue() throws Exception {
        Assert.assertNotNull(viewResolver);
        Handlebars handlebars = viewResolver.getHandlebars();
        Assert.assertEquals("hey", handlebars.compileInline("{{message \"hi\" default=\'hey\'}}").apply(new Object()));
    }

    @Test
    public void customHelper() throws Exception {
        Assert.assertNotNull(viewResolver);
        Handlebars handlebars = viewResolver.getHandlebars();
        Assert.assertEquals("Spring Helper", handlebars.compileInline("{{spring}}").apply(new Object()));
    }

    @Test
    public void setCustomHelper() throws Exception {
        Assert.assertNotNull(viewResolver);
        Handlebars handlebars = viewResolver.getHandlebars();
        Assert.assertEquals("Spring Helper", handlebars.compileInline("{{setHelper}}").apply(new Object()));
    }

    @Test
    public void helperSource() throws Exception {
        Assert.assertNotNull(viewResolver);
        Handlebars handlebars = viewResolver.getHandlebars();
        Assert.assertEquals("helper source!", handlebars.compileInline("{{helperSource}}").apply(new Object()));
    }

    @Test
    public void viewResolverWithMessageHelper() throws Exception {
        Assert.assertNotNull(viewResolver);
        Assert.assertNotNull(viewResolver.helper("message"));
    }

    @Test
    public void viewResolverWithoutMessageHelper() throws Exception {
        Assert.assertNotNull(viewResolverWithoutMessageHelper);
        Assert.assertNull(viewResolverWithoutMessageHelper.helper("message"));
    }
}

