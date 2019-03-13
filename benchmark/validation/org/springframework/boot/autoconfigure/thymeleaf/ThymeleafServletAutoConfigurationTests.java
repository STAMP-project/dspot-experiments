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
package org.springframework.boot.autoconfigure.thymeleaf;


import DispatcherType.ERROR;
import DispatcherType.INCLUDE;
import DispatcherType.REQUEST;
import RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import java.io.File;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.boot.testsupport.BuildOutput;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafView;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;


/**
 * Tests for {@link ThymeleafAutoConfiguration} in Servlet-based applications.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @author Edd? Mel?ndez
 * @author Brian Clozel
 * @author Kazuki Shimizu
 * @author Artsiom Yudovin
 */
public class ThymeleafServletAutoConfigurationTests {
    private final BuildOutput buildOutput = new BuildOutput(getClass());

    @Rule
    public OutputCapture output = new OutputCapture();

    private AnnotationConfigWebApplicationContext context;

    @Test
    public void createFromConfigClass() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class, "spring.thymeleaf.mode:HTML", "spring.thymeleaf.suffix:");
        TemplateEngine engine = this.context.getBean(TemplateEngine.class);
        Context attrs = new Context(Locale.UK, Collections.singletonMap("foo", "bar"));
        String result = engine.process("template.html", attrs);
        assertThat(result).isEqualTo("<html>bar</html>");
    }

    @Test
    public void overrideCharacterEncoding() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class, "spring.thymeleaf.encoding:UTF-16");
        ITemplateResolver resolver = this.context.getBean(ITemplateResolver.class);
        assertThat((resolver instanceof SpringResourceTemplateResolver)).isTrue();
        assertThat(getCharacterEncoding()).isEqualTo("UTF-16");
        ThymeleafViewResolver views = this.context.getBean(ThymeleafViewResolver.class);
        assertThat(views.getCharacterEncoding()).isEqualTo("UTF-16");
        assertThat(views.getContentType()).isEqualTo("text/html;charset=UTF-16");
    }

    @Test
    public void overrideDisableProducePartialOutputWhileProcessing() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class, "spring.thymeleaf.servlet.produce-partial-output-while-processing:false");
        assertThat(this.context.getBean(ThymeleafViewResolver.class).getProducePartialOutputWhileProcessing()).isFalse();
    }

    @Test
    public void disableProducePartialOutputWhileProcessingIsEnabledByDefault() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class);
        assertThat(this.context.getBean(ThymeleafViewResolver.class).getProducePartialOutputWhileProcessing()).isTrue();
    }

    @Test
    public void overrideTemplateResolverOrder() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class, "spring.thymeleaf.templateResolverOrder:25");
        ITemplateResolver resolver = this.context.getBean(ITemplateResolver.class);
        assertThat(resolver.getOrder()).isEqualTo(Integer.valueOf(25));
    }

    @Test
    public void overrideViewNames() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class, "spring.thymeleaf.viewNames:foo,bar");
        ThymeleafViewResolver views = this.context.getBean(ThymeleafViewResolver.class);
        assertThat(views.getViewNames()).isEqualTo(new String[]{ "foo", "bar" });
    }

    @Test
    public void overrideEnableSpringElCompiler() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class, "spring.thymeleaf.enable-spring-el-compiler:true");
        assertThat(this.context.getBean(SpringTemplateEngine.class).getEnableSpringELCompiler()).isTrue();
    }

    @Test
    public void enableSpringElCompilerIsDisabledByDefault() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class);
        assertThat(this.context.getBean(SpringTemplateEngine.class).getEnableSpringELCompiler()).isFalse();
    }

    @Test
    public void overrideRenderHiddenMarkersBeforeCheckboxes() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class, "spring.thymeleaf.render-hidden-markers-before-checkboxes:true");
        assertThat(this.context.getBean(SpringTemplateEngine.class).getRenderHiddenMarkersBeforeCheckboxes()).isTrue();
    }

    @Test
    public void enableRenderHiddenMarkersBeforeCheckboxesIsDisabledByDefault() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class);
        assertThat(this.context.getBean(SpringTemplateEngine.class).getRenderHiddenMarkersBeforeCheckboxes()).isFalse();
    }

    @Test
    public void templateLocationDoesNotExist() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class, "spring.thymeleaf.prefix:classpath:/no-such-directory/");
        this.output.expect(Matchers.containsString("Cannot find template location"));
    }

    @Test
    public void templateLocationEmpty() {
        new File(this.buildOutput.getTestResourcesLocation(), "empty-templates/empty-directory").mkdirs();
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class, "spring.thymeleaf.prefix:classpath:/empty-templates/empty-directory/");
    }

    @Test
    public void createLayoutFromConfigClass() throws Exception {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(ThymeleafAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class);
        MockServletContext servletContext = new MockServletContext();
        context.setServletContext(servletContext);
        context.refresh();
        ThymeleafView view = ((ThymeleafView) (context.getBean(ThymeleafViewResolver.class).resolveViewName("view", Locale.UK)));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
        view.render(Collections.singletonMap("foo", "bar"), request, response);
        String result = response.getContentAsString();
        assertThat(result).contains("<title>Content</title>");
        assertThat(result).contains("<span>bar</span>");
        context.close();
    }

    @Test
    public void useDataDialect() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class);
        TemplateEngine engine = this.context.getBean(TemplateEngine.class);
        Context attrs = new Context(Locale.UK, Collections.singletonMap("foo", "bar"));
        String result = engine.process("data-dialect", attrs);
        assertThat(result).isEqualTo("<html><body data-foo=\"bar\"></body></html>");
    }

    @Test
    public void useJava8TimeDialect() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class);
        TemplateEngine engine = this.context.getBean(TemplateEngine.class);
        Context attrs = new Context(Locale.UK);
        String result = engine.process("java8time-dialect", attrs);
        assertThat(result).isEqualTo("<html><body>2015-11-24</body></html>");
    }

    @Test
    public void useSecurityDialect() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class);
        TemplateEngine engine = this.context.getBean(TemplateEngine.class);
        WebContext attrs = new WebContext(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockServletContext());
        try {
            SecurityContextHolder.setContext(new org.springframework.security.core.context.SecurityContextImpl(new TestingAuthenticationToken("alice", "admin")));
            String result = engine.process("security-dialect", attrs);
            assertThat(result).isEqualTo(("<html><body><div>alice</div></body></html>" + (System.lineSeparator())));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    public void renderTemplate() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class);
        TemplateEngine engine = this.context.getBean(TemplateEngine.class);
        Context attrs = new Context(Locale.UK, Collections.singletonMap("foo", "bar"));
        String result = engine.process("home", attrs);
        assertThat(result).isEqualTo("<html><body>bar</body></html>");
    }

    @Test
    public void renderNonWebAppTemplate() {
        try (AnnotationConfigApplicationContext customContext = new AnnotationConfigApplicationContext(ThymeleafAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class)) {
            assertThat(customContext.getBeanNamesForType(ViewResolver.class).length).isEqualTo(0);
            TemplateEngine engine = customContext.getBean(TemplateEngine.class);
            Context attrs = new Context(Locale.UK, Collections.singletonMap("greeting", "Hello World"));
            String result = engine.process("message", attrs);
            assertThat(result).contains("Hello World");
        }
    }

    @Test
    public void registerResourceHandlingFilterDisabledByDefault() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class);
        assertThat(this.context.getBeansOfType(FilterRegistrationBean.class)).isEmpty();
    }

    @Test
    public void registerResourceHandlingFilterOnlyIfResourceChainIsEnabled() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class, "spring.resources.chain.enabled:true");
        FilterRegistrationBean<?> registration = this.context.getBean(FilterRegistrationBean.class);
        assertThat(registration.getFilter()).isInstanceOf(ResourceUrlEncodingFilter.class);
        assertThat(registration).hasFieldOrPropertyWithValue("dispatcherTypes", EnumSet.of(REQUEST, ERROR));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void registerResourceHandlingFilterWithOtherRegistrationBean() {
        // gh-14897
        load(ThymeleafServletAutoConfigurationTests.FilterRegistrationOtherConfiguration.class, "spring.resources.chain.enabled:true");
        Map<String, FilterRegistrationBean> beans = this.context.getBeansOfType(FilterRegistrationBean.class);
        assertThat(beans).hasSize(2);
        FilterRegistrationBean registration = beans.values().stream().filter(( r) -> (r.getFilter()) instanceof ResourceUrlEncodingFilter).findFirst().get();
        assertThat(registration).hasFieldOrPropertyWithValue("dispatcherTypes", EnumSet.of(REQUEST, ERROR));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void registerResourceHandlingFilterWithResourceRegistrationBean() {
        // gh-14926
        load(ThymeleafServletAutoConfigurationTests.FilterRegistrationResourceConfiguration.class, "spring.resources.chain.enabled:true");
        Map<String, FilterRegistrationBean> beans = this.context.getBeansOfType(FilterRegistrationBean.class);
        assertThat(beans).hasSize(1);
        FilterRegistrationBean registration = beans.values().stream().filter(( r) -> (r.getFilter()) instanceof ResourceUrlEncodingFilter).findFirst().get();
        assertThat(registration).hasFieldOrPropertyWithValue("dispatcherTypes", EnumSet.of(INCLUDE));
    }

    @Test
    public void layoutDialectCanBeCustomized() {
        load(ThymeleafServletAutoConfigurationTests.LayoutDialectConfiguration.class);
        LayoutDialect layoutDialect = this.context.getBean(LayoutDialect.class);
        assertThat(ReflectionTestUtils.getField(layoutDialect, "sortingStrategy")).isInstanceOf(GroupingStrategy.class);
    }

    @Test
    public void cachingCanBeDisabled() {
        load(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class, "spring.thymeleaf.cache:false");
        assertThat(this.context.getBean(ThymeleafViewResolver.class).isCache()).isFalse();
        SpringResourceTemplateResolver templateResolver = this.context.getBean(SpringResourceTemplateResolver.class);
        assertThat(templateResolver.isCacheable()).isFalse();
    }

    @Configuration
    @ImportAutoConfiguration({ ThymeleafAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class })
    static class BaseConfiguration {}

    @Configuration
    @Import(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class)
    static class LayoutDialectConfiguration {
        @Bean
        public LayoutDialect layoutDialect() {
            return new LayoutDialect(new GroupingStrategy());
        }
    }

    @Configuration
    @Import(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class)
    static class FilterRegistrationResourceConfiguration {
        @Bean
        public FilterRegistrationBean<ResourceUrlEncodingFilter> filterRegistration() {
            FilterRegistrationBean<ResourceUrlEncodingFilter> bean = new FilterRegistrationBean(new ResourceUrlEncodingFilter());
            bean.setDispatcherTypes(EnumSet.of(INCLUDE));
            return bean;
        }
    }

    @Configuration
    @Import(ThymeleafServletAutoConfigurationTests.BaseConfiguration.class)
    static class FilterRegistrationOtherConfiguration {
        @Bean
        public FilterRegistrationBean<OrderedCharacterEncodingFilter> filterRegistration() {
            return new FilterRegistrationBean(new OrderedCharacterEncodingFilter());
        }
    }
}

