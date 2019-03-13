/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.groovy.template;


import groovy.text.markup.MarkupTemplateEngine;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import org.junit.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.testsupport.BuildOutput;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfig;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer;
import org.springframework.web.servlet.view.groovy.GroovyMarkupViewResolver;


/**
 * Tests for {@link GroovyTemplateAutoConfiguration}.
 *
 * @author Dave Syer
 */
public class GroovyTemplateAutoConfigurationTests {
    private final BuildOutput buildOutput = new BuildOutput(getClass());

    private AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

    @Test
    public void defaultConfiguration() {
        registerAndRefreshContext();
        assertThat(this.context.getBean(GroovyMarkupViewResolver.class)).isNotNull();
    }

    @Test
    public void emptyTemplateLocation() {
        new File(this.buildOutput.getTestResourcesLocation(), "empty-templates/empty-directory").mkdirs();
        registerAndRefreshContext(("spring.groovy.template.resource-loader-path:" + "classpath:/templates/empty-directory/"));
    }

    @Test
    public void defaultViewResolution() throws Exception {
        registerAndRefreshContext();
        MockHttpServletResponse response = render("home");
        String result = response.getContentAsString();
        assertThat(result).contains("home");
        assertThat(response.getContentType()).isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    public void includesViewResolution() throws Exception {
        registerAndRefreshContext();
        MockHttpServletResponse response = render("includes");
        String result = response.getContentAsString();
        assertThat(result).contains("here");
        assertThat(response.getContentType()).isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    public void disableViewResolution() {
        TestPropertyValues.of("spring.groovy.template.enabled:false").applyTo(this.context);
        registerAndRefreshContext();
        assertThat(this.context.getBeanNamesForType(ViewResolver.class)).isEmpty();
    }

    @Test
    public void localeViewResolution() throws Exception {
        registerAndRefreshContext();
        MockHttpServletResponse response = render("includes", Locale.FRENCH);
        String result = response.getContentAsString();
        assertThat(result).contains("voila");
        assertThat(response.getContentType()).isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    public void customContentType() throws Exception {
        registerAndRefreshContext("spring.groovy.template.contentType:application/json");
        MockHttpServletResponse response = render("home");
        String result = response.getContentAsString();
        assertThat(result).contains("home");
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
    }

    @Test
    public void customPrefix() throws Exception {
        registerAndRefreshContext("spring.groovy.template.prefix:prefix/");
        MockHttpServletResponse response = render("prefixed");
        String result = response.getContentAsString();
        assertThat(result).contains("prefixed");
    }

    @Test
    public void customSuffix() throws Exception {
        registerAndRefreshContext("spring.groovy.template.suffix:.groovytemplate");
        MockHttpServletResponse response = render("suffixed");
        String result = response.getContentAsString();
        assertThat(result).contains("suffixed");
    }

    @Test
    public void customTemplateLoaderPath() throws Exception {
        registerAndRefreshContext("spring.groovy.template.resource-loader-path:classpath:/custom-templates/");
        MockHttpServletResponse response = render("custom");
        String result = response.getContentAsString();
        assertThat(result).contains("custom");
    }

    @Test
    public void disableCache() {
        registerAndRefreshContext("spring.groovy.template.cache:false");
        assertThat(this.context.getBean(GroovyMarkupViewResolver.class).getCacheLimit()).isEqualTo(0);
    }

    @Test
    public void renderTemplate() throws Exception {
        registerAndRefreshContext();
        GroovyMarkupConfig config = this.context.getBean(GroovyMarkupConfig.class);
        MarkupTemplateEngine engine = config.getTemplateEngine();
        Writer writer = new StringWriter();
        engine.createTemplate(new ClassPathResource("templates/message.tpl").getFile()).make(new HashMap<String, Object>(Collections.singletonMap("greeting", "Hello World"))).writeTo(writer);
        assertThat(writer.toString()).contains("Hello World");
    }

    @Test
    public void customConfiguration() {
        registerAndRefreshContext("spring.groovy.template.configuration.auto-indent:true");
        assertThat(this.context.getBean(GroovyMarkupConfigurer.class).isAutoIndent()).isTrue();
    }
}

