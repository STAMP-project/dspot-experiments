/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.servlet.view.groovy;


import groovy.text.Template;
import groovy.text.TemplateEngine;
import groovy.text.markup.MarkupTemplateEngine;
import groovy.text.markup.TemplateConfiguration;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.context.WebApplicationContext;


/**
 *
 *
 * @author Brian Clozel
 */
public class GroovyMarkupViewTests {
    private static final String RESOURCE_LOADER_PATH = "classpath*:org/springframework/web/servlet/view/groovy/";

    private WebApplicationContext webAppContext;

    private ServletContext servletContext;

    @Test
    public void missingGroovyMarkupConfig() throws Exception {
        GroovyMarkupView view = new GroovyMarkupView();
        BDDMockito.given(this.webAppContext.getBeansOfType(GroovyMarkupConfig.class, true, false)).willReturn(new HashMap());
        view.setUrl("sampleView");
        try {
            view.setApplicationContext(this.webAppContext);
            Assert.fail();
        } catch (ApplicationContextException ex) {
            Assert.assertTrue(ex.getMessage().contains("GroovyMarkupConfig"));
        }
    }

    @Test
    public void customTemplateEngine() throws Exception {
        GroovyMarkupView view = new GroovyMarkupView();
        view.setTemplateEngine(new GroovyMarkupViewTests.TestTemplateEngine());
        view.setApplicationContext(this.webAppContext);
        DirectFieldAccessor accessor = new DirectFieldAccessor(view);
        TemplateEngine engine = ((TemplateEngine) (accessor.getPropertyValue("engine")));
        Assert.assertNotNull(engine);
        Assert.assertEquals(GroovyMarkupViewTests.TestTemplateEngine.class, engine.getClass());
    }

    @Test
    public void detectTemplateEngine() throws Exception {
        GroovyMarkupView view = new GroovyMarkupView();
        view.setTemplateEngine(new GroovyMarkupViewTests.TestTemplateEngine());
        view.setApplicationContext(this.webAppContext);
        DirectFieldAccessor accessor = new DirectFieldAccessor(view);
        TemplateEngine engine = ((TemplateEngine) (accessor.getPropertyValue("engine")));
        Assert.assertNotNull(engine);
        Assert.assertEquals(GroovyMarkupViewTests.TestTemplateEngine.class, engine.getClass());
    }

    @Test
    public void checkResource() throws Exception {
        GroovyMarkupView view = createViewWithUrl("test.tpl");
        Assert.assertTrue(view.checkResource(Locale.US));
    }

    @Test
    public void checkMissingResource() throws Exception {
        GroovyMarkupView view = createViewWithUrl("missing.tpl");
        Assert.assertFalse(view.checkResource(Locale.US));
    }

    @Test
    public void checkI18nResource() throws Exception {
        GroovyMarkupView view = createViewWithUrl("i18n.tpl");
        Assert.assertTrue(view.checkResource(Locale.FRENCH));
    }

    @Test
    public void checkI18nResourceMissingLocale() throws Exception {
        GroovyMarkupView view = createViewWithUrl("i18n.tpl");
        Assert.assertTrue(view.checkResource(Locale.CHINESE));
    }

    @Test
    public void renderMarkupTemplate() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("name", "Spring");
        MockHttpServletResponse response = renderViewWithModel("test.tpl", model, Locale.US);
        Assert.assertThat(response.getContentAsString(), Matchers.containsString("<h1>Hello Spring</h1>"));
    }

    @Test
    public void renderI18nTemplate() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("name", "Spring");
        MockHttpServletResponse response = renderViewWithModel("i18n.tpl", model, Locale.FRANCE);
        Assert.assertEquals("<p>Bonjour Spring</p>", response.getContentAsString());
        response = renderViewWithModel("i18n.tpl", model, Locale.GERMANY);
        Assert.assertEquals("<p>Include German</p><p>Hallo Spring</p>", response.getContentAsString());
        response = renderViewWithModel("i18n.tpl", model, new Locale("es"));
        Assert.assertEquals("<p>Include Default</p><p>Hola Spring</p>", response.getContentAsString());
    }

    @Test
    public void renderLayoutTemplate() throws Exception {
        Map<String, Object> model = new HashMap<>();
        MockHttpServletResponse response = renderViewWithModel("content.tpl", model, Locale.US);
        Assert.assertEquals("<html><head><title>Layout example</title></head><body><p>This is the body</p></body></html>", response.getContentAsString());
    }

    public class TestTemplateEngine extends MarkupTemplateEngine {
        public TestTemplateEngine() {
            super(new TemplateConfiguration());
        }

        @Override
        public Template createTemplate(Reader reader) {
            return null;
        }
    }

    @Configuration
    static class GroovyMarkupConfiguration {
        @Bean
        public GroovyMarkupConfig groovyMarkupConfigurer() {
            GroovyMarkupConfigurer configurer = new GroovyMarkupConfigurer();
            configurer.setResourceLoaderPath(GroovyMarkupViewTests.RESOURCE_LOADER_PATH);
            return configurer;
        }
    }
}

