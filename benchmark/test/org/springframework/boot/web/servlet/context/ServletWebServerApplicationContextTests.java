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
package org.springframework.boot.web.servlet.context;


import AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR;
import DispatcherType.REQUEST;
import WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import WebApplicationContext.SCOPE_APPLICATION;
import WebApplicationContext.SCOPE_REQUEST;
import WebApplicationContext.SCOPE_SESSION;
import WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME;
import java.util.EnumSet;
import java.util.Properties;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.MockServletWebServerFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.request.SessionScope;
import org.springframework.web.filter.GenericFilterBean;


/**
 * Tests for {@link ServletWebServerApplicationContext}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class ServletWebServerApplicationContextTests {
    private ServletWebServerApplicationContext context;

    @Rule
    public OutputCapture output = new OutputCapture();

    @Captor
    private ArgumentCaptor<Filter> filterCaptor;

    @Test
    public void startRegistrations() {
        addWebServerFactoryBean();
        this.context.refresh();
        MockServletWebServerFactory factory = getWebServerFactory();
        // Ensure that the context has been setup
        assertThat(this.context.getServletContext()).isEqualTo(factory.getServletContext());
        Mockito.verify(factory.getServletContext()).setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
        // Ensure WebApplicationContextUtils.registerWebApplicationScopes was called
        assertThat(this.context.getBeanFactory().getRegisteredScope(SCOPE_SESSION)).isInstanceOf(SessionScope.class);
        // Ensure WebApplicationContextUtils.registerEnvironmentBeans was called
        assertThat(this.context.containsBean(SERVLET_CONTEXT_BEAN_NAME)).isTrue();
    }

    @Test
    public void doesNotRegistersShutdownHook() {
        // See gh-314 for background. We no longer register the shutdown hook
        // since it is really the callers responsibility. The shutdown hook could
        // also be problematic in a classic WAR deployment.
        addWebServerFactoryBean();
        this.context.refresh();
        assertThat(this.context).hasFieldOrPropertyWithValue("shutdownHook", null);
    }

    @Test
    public void ServletWebServerInitializedEventPublished() {
        addWebServerFactoryBean();
        this.context.registerBeanDefinition("listener", new RootBeanDefinition(ServletWebServerApplicationContextTests.MockListener.class));
        this.context.refresh();
        ServletWebServerInitializedEvent event = this.context.getBean(ServletWebServerApplicationContextTests.MockListener.class).getEvent();
        assertThat(event).isNotNull();
        assertThat(((getPort()) >= 0)).isTrue();
        assertThat(event.getApplicationContext()).isEqualTo(this.context);
    }

    @Test
    public void localPortIsAvailable() {
        addWebServerFactoryBean();
        new ServerPortInfoApplicationContextInitializer().initialize(this.context);
        this.context.refresh();
        ConfigurableEnvironment environment = this.context.getEnvironment();
        assertThat(environment.containsProperty("local.server.port")).isTrue();
        assertThat(environment.getProperty("local.server.port")).isEqualTo("8080");
    }

    @Test
    public void stopOnClose() {
        addWebServerFactoryBean();
        this.context.refresh();
        MockServletWebServerFactory factory = getWebServerFactory();
        this.context.close();
        stop();
    }

    @Test
    public void cannotSecondRefresh() {
        addWebServerFactoryBean();
        this.context.refresh();
        assertThatIllegalStateException().isThrownBy(() -> this.context.refresh());
    }

    @Test
    public void servletContextAwareBeansAreInjected() {
        addWebServerFactoryBean();
        ServletContextAware bean = Mockito.mock(ServletContextAware.class);
        this.context.registerBeanDefinition("bean", beanDefinition(bean));
        this.context.refresh();
        Mockito.verify(bean).setServletContext(getWebServerFactory().getServletContext());
    }

    @Test
    public void missingServletWebServerFactory() {
        assertThatExceptionOfType(ApplicationContextException.class).isThrownBy(() -> this.context.refresh()).withMessageContaining(("Unable to start ServletWebServerApplicationContext due to missing " + "ServletWebServerFactory bean"));
    }

    @Test
    public void tooManyWebServerFactories() {
        addWebServerFactoryBean();
        this.context.registerBeanDefinition("webServerFactory2", new RootBeanDefinition(MockServletWebServerFactory.class));
        assertThatExceptionOfType(ApplicationContextException.class).isThrownBy(() -> this.context.refresh()).withMessageContaining(("Unable to start ServletWebServerApplicationContext due to " + "multiple ServletWebServerFactory beans"));
    }

    @Test
    public void singleServletBean() {
        addWebServerFactoryBean();
        Servlet servlet = Mockito.mock(Servlet.class);
        this.context.registerBeanDefinition("servletBean", beanDefinition(servlet));
        this.context.refresh();
        MockServletWebServerFactory factory = getWebServerFactory();
        Mockito.verify(factory.getServletContext()).addServlet("servletBean", servlet);
        Mockito.verify(factory.getRegisteredServlet(0).getRegistration()).addMapping("/");
    }

    @Test
    public void orderedBeanInsertedCorrectly() {
        addWebServerFactoryBean();
        ServletWebServerApplicationContextTests.OrderedFilter filter = new ServletWebServerApplicationContextTests.OrderedFilter();
        this.context.registerBeanDefinition("filterBean", beanDefinition(filter));
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean();
        registration.setFilter(Mockito.mock(Filter.class));
        registration.setOrder(100);
        this.context.registerBeanDefinition("filterRegistrationBean", beanDefinition(registration));
        this.context.refresh();
        MockServletWebServerFactory factory = getWebServerFactory();
        Mockito.verify(factory.getServletContext()).addFilter("filterBean", filter);
        Mockito.verify(factory.getServletContext()).addFilter("object", registration.getFilter());
        assertThat(factory.getRegisteredFilter(0).getFilter()).isEqualTo(filter);
    }

    @Test
    public void multipleServletBeans() {
        addWebServerFactoryBean();
        Servlet servlet1 = Mockito.mock(Servlet.class, Mockito.withSettings().extraInterfaces(Ordered.class));
        BDDMockito.given(getOrder()).willReturn(1);
        Servlet servlet2 = Mockito.mock(Servlet.class, Mockito.withSettings().extraInterfaces(Ordered.class));
        BDDMockito.given(getOrder()).willReturn(2);
        this.context.registerBeanDefinition("servletBean2", beanDefinition(servlet2));
        this.context.registerBeanDefinition("servletBean1", beanDefinition(servlet1));
        this.context.refresh();
        MockServletWebServerFactory factory = getWebServerFactory();
        ServletContext servletContext = factory.getServletContext();
        InOrder ordered = Mockito.inOrder(servletContext);
        ordered.verify(servletContext).addServlet("servletBean1", servlet1);
        ordered.verify(servletContext).addServlet("servletBean2", servlet2);
        Mockito.verify(factory.getRegisteredServlet(0).getRegistration()).addMapping("/servletBean1/");
        Mockito.verify(factory.getRegisteredServlet(1).getRegistration()).addMapping("/servletBean2/");
    }

    @Test
    public void multipleServletBeansWithMainDispatcher() {
        addWebServerFactoryBean();
        Servlet servlet1 = Mockito.mock(Servlet.class, Mockito.withSettings().extraInterfaces(Ordered.class));
        BDDMockito.given(getOrder()).willReturn(1);
        Servlet servlet2 = Mockito.mock(Servlet.class, Mockito.withSettings().extraInterfaces(Ordered.class));
        BDDMockito.given(getOrder()).willReturn(2);
        this.context.registerBeanDefinition("servletBean2", beanDefinition(servlet2));
        this.context.registerBeanDefinition("dispatcherServlet", beanDefinition(servlet1));
        this.context.refresh();
        MockServletWebServerFactory factory = getWebServerFactory();
        ServletContext servletContext = factory.getServletContext();
        InOrder ordered = Mockito.inOrder(servletContext);
        ordered.verify(servletContext).addServlet("dispatcherServlet", servlet1);
        ordered.verify(servletContext).addServlet("servletBean2", servlet2);
        Mockito.verify(factory.getRegisteredServlet(0).getRegistration()).addMapping("/");
        Mockito.verify(factory.getRegisteredServlet(1).getRegistration()).addMapping("/servletBean2/");
    }

    @Test
    public void servletAndFilterBeans() {
        addWebServerFactoryBean();
        Servlet servlet = Mockito.mock(Servlet.class);
        Filter filter1 = Mockito.mock(Filter.class, Mockito.withSettings().extraInterfaces(Ordered.class));
        BDDMockito.given(getOrder()).willReturn(1);
        Filter filter2 = Mockito.mock(Filter.class, Mockito.withSettings().extraInterfaces(Ordered.class));
        BDDMockito.given(getOrder()).willReturn(2);
        this.context.registerBeanDefinition("servletBean", beanDefinition(servlet));
        this.context.registerBeanDefinition("filterBean2", beanDefinition(filter2));
        this.context.registerBeanDefinition("filterBean1", beanDefinition(filter1));
        this.context.refresh();
        MockServletWebServerFactory factory = getWebServerFactory();
        ServletContext servletContext = factory.getServletContext();
        InOrder ordered = Mockito.inOrder(servletContext);
        Mockito.verify(factory.getServletContext()).addServlet("servletBean", servlet);
        Mockito.verify(factory.getRegisteredServlet(0).getRegistration()).addMapping("/");
        ordered.verify(factory.getServletContext()).addFilter("filterBean1", filter1);
        ordered.verify(factory.getServletContext()).addFilter("filterBean2", filter2);
        Mockito.verify(factory.getRegisteredFilter(0).getRegistration()).addMappingForUrlPatterns(EnumSet.of(REQUEST), false, "/*");
        Mockito.verify(factory.getRegisteredFilter(1).getRegistration()).addMappingForUrlPatterns(EnumSet.of(REQUEST), false, "/*");
    }

    @Test
    public void servletContextInitializerBeans() throws Exception {
        addWebServerFactoryBean();
        ServletContextInitializer initializer1 = Mockito.mock(ServletContextInitializer.class, Mockito.withSettings().extraInterfaces(Ordered.class));
        BDDMockito.given(getOrder()).willReturn(1);
        ServletContextInitializer initializer2 = Mockito.mock(ServletContextInitializer.class, Mockito.withSettings().extraInterfaces(Ordered.class));
        BDDMockito.given(getOrder()).willReturn(2);
        this.context.registerBeanDefinition("initializerBean2", beanDefinition(initializer2));
        this.context.registerBeanDefinition("initializerBean1", beanDefinition(initializer1));
        this.context.refresh();
        ServletContext servletContext = getWebServerFactory().getServletContext();
        InOrder ordered = Mockito.inOrder(initializer1, initializer2);
        ordered.verify(initializer1).onStartup(servletContext);
        ordered.verify(initializer2).onStartup(servletContext);
    }

    @Test
    public void servletContextListenerBeans() {
        addWebServerFactoryBean();
        ServletContextListener initializer = Mockito.mock(ServletContextListener.class);
        this.context.registerBeanDefinition("initializerBean", beanDefinition(initializer));
        this.context.refresh();
        ServletContext servletContext = getWebServerFactory().getServletContext();
        Mockito.verify(servletContext).addListener(initializer);
    }

    @Test
    public void unorderedServletContextInitializerBeans() throws Exception {
        addWebServerFactoryBean();
        ServletContextInitializer initializer1 = Mockito.mock(ServletContextInitializer.class);
        ServletContextInitializer initializer2 = Mockito.mock(ServletContextInitializer.class);
        this.context.registerBeanDefinition("initializerBean2", beanDefinition(initializer2));
        this.context.registerBeanDefinition("initializerBean1", beanDefinition(initializer1));
        this.context.refresh();
        ServletContext servletContext = getWebServerFactory().getServletContext();
        Mockito.verify(initializer1).onStartup(servletContext);
        Mockito.verify(initializer2).onStartup(servletContext);
    }

    @Test
    public void servletContextInitializerBeansDoesNotSkipServletsAndFilters() throws Exception {
        addWebServerFactoryBean();
        ServletContextInitializer initializer = Mockito.mock(ServletContextInitializer.class);
        Servlet servlet = Mockito.mock(Servlet.class);
        Filter filter = Mockito.mock(Filter.class);
        this.context.registerBeanDefinition("initializerBean", beanDefinition(initializer));
        this.context.registerBeanDefinition("servletBean", beanDefinition(servlet));
        this.context.registerBeanDefinition("filterBean", beanDefinition(filter));
        this.context.refresh();
        ServletContext servletContext = getWebServerFactory().getServletContext();
        Mockito.verify(initializer).onStartup(servletContext);
        Mockito.verify(servletContext).addServlet(ArgumentMatchers.anyString(), ArgumentMatchers.any(Servlet.class));
        Mockito.verify(servletContext).addFilter(ArgumentMatchers.anyString(), ArgumentMatchers.any(Filter.class));
    }

    @Test
    public void servletContextInitializerBeansSkipsRegisteredServletsAndFilters() {
        addWebServerFactoryBean();
        Servlet servlet = Mockito.mock(Servlet.class);
        Filter filter = Mockito.mock(Filter.class);
        ServletRegistrationBean<Servlet> initializer = new ServletRegistrationBean(servlet, "/foo");
        this.context.registerBeanDefinition("initializerBean", beanDefinition(initializer));
        this.context.registerBeanDefinition("servletBean", beanDefinition(servlet));
        this.context.registerBeanDefinition("filterBean", beanDefinition(filter));
        this.context.refresh();
        ServletContext servletContext = getWebServerFactory().getServletContext();
        Mockito.verify(servletContext, Mockito.atMost(1)).addServlet(ArgumentMatchers.anyString(), ArgumentMatchers.any(Servlet.class));
        Mockito.verify(servletContext, Mockito.atMost(1)).addFilter(ArgumentMatchers.anyString(), ArgumentMatchers.any(Filter.class));
    }

    @Test
    public void filterRegistrationBeansSkipsRegisteredFilters() {
        addWebServerFactoryBean();
        Filter filter = Mockito.mock(Filter.class);
        FilterRegistrationBean<Filter> initializer = new FilterRegistrationBean(filter);
        this.context.registerBeanDefinition("initializerBean", beanDefinition(initializer));
        this.context.registerBeanDefinition("filterBean", beanDefinition(filter));
        this.context.refresh();
        ServletContext servletContext = getWebServerFactory().getServletContext();
        Mockito.verify(servletContext, Mockito.atMost(1)).addFilter(ArgumentMatchers.anyString(), ArgumentMatchers.any(Filter.class));
    }

    @Test
    public void delegatingFilterProxyRegistrationBeansSkipsTargetBeanNames() throws Exception {
        addWebServerFactoryBean();
        DelegatingFilterProxyRegistrationBean initializer = new DelegatingFilterProxyRegistrationBean("filterBean");
        this.context.registerBeanDefinition("initializerBean", beanDefinition(initializer));
        BeanDefinition filterBeanDefinition = beanDefinition(new IllegalStateException("Create FilterBean Failure"));
        filterBeanDefinition.setLazyInit(true);
        this.context.registerBeanDefinition("filterBean", filterBeanDefinition);
        this.context.refresh();
        ServletContext servletContext = getWebServerFactory().getServletContext();
        Mockito.verify(servletContext, Mockito.atMost(1)).addFilter(ArgumentMatchers.anyString(), this.filterCaptor.capture());
        // Up to this point the filterBean should not have been created, calling
        // the delegate proxy will trigger creation and an exception
        assertThatExceptionOfType(BeanCreationException.class).isThrownBy(() -> {
            this.filterCaptor.getValue().init(new MockFilterConfig());
            this.filterCaptor.getValue().doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());
        }).withMessageContaining("Create FilterBean Failure");
    }

    @Test
    public void postProcessWebServerFactory() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(MockServletWebServerFactory.class);
        MutablePropertyValues pv = new MutablePropertyValues();
        pv.add("port", "${port}");
        beanDefinition.setPropertyValues(pv);
        this.context.registerBeanDefinition("webServerFactory", beanDefinition);
        PropertySourcesPlaceholderConfigurer propertySupport = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();
        properties.put("port", 8080);
        propertySupport.setProperties(properties);
        this.context.registerBeanDefinition("propertySupport", beanDefinition(propertySupport));
        this.context.refresh();
        assertThat(getPort()).isEqualTo(8080);
    }

    @Test
    public void doesNotReplaceExistingScopes() {
        // gh-2082
        Scope scope = Mockito.mock(Scope.class);
        ConfigurableListableBeanFactory factory = this.context.getBeanFactory();
        factory.registerScope(SCOPE_REQUEST, scope);
        factory.registerScope(SCOPE_SESSION, scope);
        addWebServerFactoryBean();
        this.context.refresh();
        assertThat(factory.getRegisteredScope(SCOPE_REQUEST)).isSameAs(scope);
        assertThat(factory.getRegisteredScope(SCOPE_SESSION)).isSameAs(scope);
    }

    @Test
    public void servletRequestCanBeInjectedEarly() throws Exception {
        // gh-14990
        int initialOutputLength = this.output.toString().length();
        addWebServerFactoryBean();
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ServletWebServerApplicationContextTests.WithAutowiredServletRequest.class);
        beanDefinition.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        this.context.registerBeanDefinition("withAutowiredServletRequest", beanDefinition);
        this.context.addBeanFactoryPostProcessor(( beanFactory) -> {
            org.springframework.boot.web.servlet.context.WithAutowiredServletRequest bean = beanFactory.getBean(.class);
            assertThat(bean.getRequest()).isNotNull();
        });
        this.context.refresh();
        String output = this.output.toString().substring(initialOutputLength);
        assertThat(output).doesNotContain("Replacing scope");
    }

    @Test
    public void webApplicationScopeIsRegistered() throws Exception {
        addWebServerFactoryBean();
        this.context.refresh();
        assertThat(this.context.getBeanFactory().getRegisteredScope(SCOPE_APPLICATION)).isNotNull();
    }

    public static class MockListener implements ApplicationListener<ServletWebServerInitializedEvent> {
        private ServletWebServerInitializedEvent event;

        @Override
        public void onApplicationEvent(ServletWebServerInitializedEvent event) {
            this.event = event;
        }

        public ServletWebServerInitializedEvent getEvent() {
            return this.event;
        }
    }

    @Order(10)
    protected static class OrderedFilter extends GenericFilterBean {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        }
    }

    protected static class WithAutowiredServletRequest {
        private final ServletRequest request;

        public WithAutowiredServletRequest(ServletRequest request) {
            this.request = request;
        }

        public ServletRequest getRequest() {
            return this.request;
        }
    }
}

