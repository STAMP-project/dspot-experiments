/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot;


import AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;
import Banner.Mode;
import Banner.Mode.LOG;
import Banner.Mode.OFF;
import CachedIntrospectionResults.IGNORE_BEANINFO_PROPERTY_NAME;
import TestPropertySourceUtils.INLINED_PROPERTIES_PROPERTY_SOURCE_NAME;
import WebApplicationType.NONE;
import WebApplicationType.REACTIVE;
import WebApplicationType.SERVLET;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.boot.web.reactive.context.StandardReactiveWebEnvironment;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.Profiles;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.StandardServletEnvironment;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.isA;


/**
 * Tests for {@link SpringApplication}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Christian Dupuis
 * @author Stephane Nicoll
 * @author Jeremy Rickard
 * @author Craig Burke
 * @author Madhura Bhave
 * @author Brian Clozel
 * @author Artsiom Yudovin
 */
public class SpringApplicationTests {
    private String headlessProperty;

    @Rule
    public OutputCapture output = new OutputCapture();

    private ConfigurableApplicationContext context;

    @Test
    public void sourcesMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new SpringApplication(((Class<?>[]) (null))).run()).withMessageContaining("PrimarySources must not be null");
    }

    @Test
    public void sourcesMustNotBeEmpty() {
        assertThatIllegalArgumentException().isThrownBy(() -> new SpringApplication().run()).withMessageContaining("Sources must not be empty");
    }

    @Test
    public void sourcesMustBeAccessible() {
        assertThatIllegalStateException().isThrownBy(() -> new SpringApplication(.class).run()).withMessageContaining("Cannot load configuration");
    }

    @Test
    public void customBanner() {
        SpringApplication application = Mockito.spy(new SpringApplication(SpringApplicationTests.ExampleConfig.class));
        application.setWebApplicationType(NONE);
        this.context = application.run("--spring.banner.location=classpath:test-banner.txt");
        assertThat(this.output.toString()).startsWith("Running a Test!");
    }

    @Test
    public void customBannerWithProperties() {
        SpringApplication application = Mockito.spy(new SpringApplication(SpringApplicationTests.ExampleConfig.class));
        application.setWebApplicationType(NONE);
        this.context = application.run("--spring.banner.location=classpath:test-banner-with-placeholder.txt", "--test.property=123456");
        assertThat(this.output.toString()).containsPattern("Running a Test!\\s+123456");
    }

    @Test
    public void imageBannerAndTextBanner() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        SpringApplicationTests.MockResourceLoader resourceLoader = new SpringApplicationTests.MockResourceLoader();
        resourceLoader.addResource("banner.gif", "black-and-white.gif");
        resourceLoader.addResource("banner.txt", "foobar.txt");
        application.setWebApplicationType(NONE);
        application.setResourceLoader(resourceLoader);
        application.run();
        assertThat(this.output.toString()).contains("@@@@").contains("Foo Bar");
    }

    @Test
    public void imageBannerLoads() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        SpringApplicationTests.MockResourceLoader resourceLoader = new SpringApplicationTests.MockResourceLoader();
        resourceLoader.addResource("banner.gif", "black-and-white.gif");
        application.setWebApplicationType(NONE);
        application.setResourceLoader(resourceLoader);
        application.run();
        assertThat(this.output.toString()).contains("@@@@@@");
    }

    @Test
    public void logsNoActiveProfiles() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        assertThat(this.output.toString()).contains("No active profile set, falling back to default profiles: default");
    }

    @Test
    public void logsActiveProfiles() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run("--spring.profiles.active=myprofiles");
        assertThat(this.output.toString()).contains("The following profiles are active: myprofile");
    }

    @Test
    public void enableBannerInLogViaProperty() {
        SpringApplication application = Mockito.spy(new SpringApplication(SpringApplicationTests.ExampleConfig.class));
        application.setWebApplicationType(NONE);
        this.context = application.run("--spring.main.banner-mode=log");
        Mockito.verify(application, Mockito.atLeastOnce()).setBannerMode(LOG);
        assertThat(this.output.toString()).contains("o.s.b.SpringApplication");
    }

    @Test
    public void setIgnoreBeanInfoPropertyByDefault() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        String property = System.getProperty(IGNORE_BEANINFO_PROPERTY_NAME);
        assertThat(property).isEqualTo("true");
    }

    @Test
    public void disableIgnoreBeanInfoProperty() {
        System.setProperty(IGNORE_BEANINFO_PROPERTY_NAME, "false");
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        String property = System.getProperty(IGNORE_BEANINFO_PROPERTY_NAME);
        assertThat(property).isEqualTo("false");
    }

    @Test
    public void triggersConfigFileApplicationListenerBeforeBinding() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run("--spring.config.name=bindtoapplication");
        assertThat(application).hasFieldOrPropertyWithValue("bannerMode", OFF);
    }

    @Test
    public void bindsSystemPropertyToSpringApplication() {
        System.setProperty("spring.main.banner-mode", "off");
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        assertThat(application).hasFieldOrPropertyWithValue("bannerMode", OFF);
    }

    @Test
    public void customId() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run("--spring.application.name=foo");
        assertThat(this.context.getId()).startsWith("foo");
    }

    @Test
    public void specificApplicationContextClass() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setApplicationContextClass(StaticApplicationContext.class);
        this.context = application.run();
        assertThat(this.context).isInstanceOf(StaticApplicationContext.class);
    }

    @Test
    public void specificWebApplicationContextClassDetectWebApplicationType() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setApplicationContextClass(AnnotationConfigWebApplicationContext.class);
        assertThat(application.getWebApplicationType()).isEqualTo(SERVLET);
    }

    @Test
    public void specificReactiveApplicationContextClassDetectReactiveApplicationType() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setApplicationContextClass(AnnotationConfigReactiveWebApplicationContext.class);
        assertThat(application.getWebApplicationType()).isEqualTo(REACTIVE);
    }

    @Test
    public void nonWebNorReactiveApplicationContextClassDetectNoneApplicationType() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setApplicationContextClass(StaticApplicationContext.class);
        assertThat(application.getWebApplicationType()).isEqualTo(NONE);
    }

    @Test
    public void specificApplicationContextInitializer() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        final AtomicReference<ApplicationContext> reference = new AtomicReference<>();
        application.setInitializers(Collections.singletonList(((ApplicationContextInitializer<ConfigurableApplicationContext>) (reference::set))));
        this.context = application.run("--foo=bar");
        assertThat(this.context).isSameAs(reference.get());
        // Custom initializers do not switch off the defaults
        assertThat(getEnvironment().getProperty("foo")).isEqualTo("bar");
    }

    @Test
    public void applicationRunningEventListener() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        final AtomicReference<SpringApplication> reference = new AtomicReference<>();
        class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {
            @Override
            public void onApplicationEvent(ApplicationReadyEvent event) {
                reference.set(event.getSpringApplication());
            }
        }
        application.addListeners(new ApplicationReadyEventListener());
        this.context = application.run("--foo=bar");
        assertThat(application).isSameAs(reference.get());
    }

    @Test
    public void contextRefreshedEventListener() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        final AtomicReference<ApplicationContext> reference = new AtomicReference<>();
        class InitializerListener implements ApplicationListener<ContextRefreshedEvent> {
            @Override
            public void onApplicationEvent(ContextRefreshedEvent event) {
                reference.set(event.getApplicationContext());
            }
        }
        application.setListeners(Collections.singletonList(new InitializerListener()));
        this.context = application.run("--foo=bar");
        assertThat(this.context).isSameAs(reference.get());
        // Custom initializers do not switch off the defaults
        assertThat(getEnvironment().getProperty("foo")).isEqualTo("bar");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void eventsArePublishedInExpectedOrder() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        ApplicationListener<ApplicationEvent> listener = Mockito.mock(ApplicationListener.class);
        application.addListeners(listener);
        this.context = application.run();
        InOrder inOrder = Mockito.inOrder(listener);
        inOrder.verify(listener).onApplicationEvent(isA(ApplicationStartingEvent.class));
        inOrder.verify(listener).onApplicationEvent(ArgumentMatchers.isA(ApplicationEnvironmentPreparedEvent.class));
        inOrder.verify(listener).onApplicationEvent(ArgumentMatchers.isA(ApplicationContextInitializedEvent.class));
        inOrder.verify(listener).onApplicationEvent(ArgumentMatchers.isA(ApplicationPreparedEvent.class));
        inOrder.verify(listener).onApplicationEvent(ArgumentMatchers.isA(ContextRefreshedEvent.class));
        inOrder.verify(listener).onApplicationEvent(ArgumentMatchers.isA(org.springframework.boot.context.event.ApplicationStartedEvent.class));
        inOrder.verify(listener).onApplicationEvent(ArgumentMatchers.isA(ApplicationReadyEvent.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void defaultApplicationContext() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        assertThat(this.context).isInstanceOf(AnnotationConfigApplicationContext.class);
    }

    @Test
    public void defaultApplicationContextForWeb() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleWebConfig.class);
        application.setWebApplicationType(SERVLET);
        this.context = application.run();
        assertThat(this.context).isInstanceOf(AnnotationConfigServletWebServerApplicationContext.class);
    }

    @Test
    public void defaultApplicationContextForReactiveWeb() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleReactiveWebConfig.class);
        application.setWebApplicationType(REACTIVE);
        this.context = application.run();
        assertThat(this.context).isInstanceOf(AnnotationConfigReactiveWebServerApplicationContext.class);
    }

    @Test
    public void environmentForWeb() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleWebConfig.class);
        application.setWebApplicationType(SERVLET);
        this.context = application.run();
        assertThat(this.context.getEnvironment()).isInstanceOf(StandardServletEnvironment.class);
    }

    @Test
    public void environmentForReactiveWeb() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleReactiveWebConfig.class);
        application.setWebApplicationType(REACTIVE);
        this.context = application.run();
        assertThat(this.context.getEnvironment()).isInstanceOf(StandardReactiveWebEnvironment.class);
    }

    @Test
    public void customEnvironment() {
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        ConfigurableEnvironment environment = new StandardEnvironment();
        application.setEnvironment(environment);
        this.context = application.run();
        Mockito.verify(application.getLoader()).setEnvironment(environment);
    }

    @Test
    public void customResourceLoader() {
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        application.setResourceLoader(resourceLoader);
        this.context = application.run();
        Mockito.verify(application.getLoader()).setResourceLoader(resourceLoader);
    }

    @Test
    public void customResourceLoaderFromConstructor() {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication(resourceLoader, SpringApplicationTests.ExampleWebConfig.class);
        this.context = application.run();
        Mockito.verify(application.getLoader()).setResourceLoader(resourceLoader);
    }

    @Test
    public void customBeanNameGenerator() {
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication(SpringApplicationTests.ExampleWebConfig.class);
        BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();
        application.setBeanNameGenerator(beanNameGenerator);
        this.context = application.run();
        Mockito.verify(application.getLoader()).setBeanNameGenerator(beanNameGenerator);
        Object actualGenerator = this.context.getBean(CONFIGURATION_BEAN_NAME_GENERATOR);
        assertThat(actualGenerator).isSameAs(beanNameGenerator);
    }

    @Test
    public void customBeanNameGeneratorWithNonWebApplication() {
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication(SpringApplicationTests.ExampleWebConfig.class);
        application.setWebApplicationType(NONE);
        BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();
        application.setBeanNameGenerator(beanNameGenerator);
        this.context = application.run();
        Mockito.verify(application.getLoader()).setBeanNameGenerator(beanNameGenerator);
        Object actualGenerator = this.context.getBean(CONFIGURATION_BEAN_NAME_GENERATOR);
        assertThat(actualGenerator).isSameAs(beanNameGenerator);
    }

    @Test
    public void commandLinePropertySource() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        ConfigurableEnvironment environment = new StandardEnvironment();
        application.setEnvironment(environment);
        this.context = application.run("--foo=bar");
        assertThat(environment).has(matchingPropertySource(CommandLinePropertySource.class, "commandLineArgs"));
    }

    @Test
    public void commandLinePropertySourceEnhancesEnvironment() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        ConfigurableEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("commandLineArgs", Collections.singletonMap("foo", "original")));
        application.setEnvironment(environment);
        this.context = application.run("--foo=bar", "--bar=foo");
        assertThat(environment).has(matchingPropertySource(CompositePropertySource.class, "commandLineArgs"));
        assertThat(environment.getProperty("bar")).isEqualTo("foo");
        // New command line properties take precedence
        assertThat(environment.getProperty("foo")).isEqualTo("bar");
        CompositePropertySource composite = ((CompositePropertySource) (environment.getPropertySources().get("commandLineArgs")));
        assertThat(composite.getPropertySources()).hasSize(2);
        assertThat(composite.getPropertySources()).first().matches(( source) -> source.getName().equals("springApplicationCommandLineArgs"), "is named springApplicationCommandLineArgs");
        assertThat(composite.getPropertySources()).element(1).matches(( source) -> source.getName().equals("commandLineArgs"), "is named commandLineArgs");
    }

    @Test
    public void propertiesFileEnhancesEnvironment() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        ConfigurableEnvironment environment = new StandardEnvironment();
        application.setEnvironment(environment);
        this.context = application.run();
        assertThat(environment.getProperty("foo")).isEqualTo("bucket");
    }

    @Test
    public void addProfiles() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        application.setAdditionalProfiles("foo");
        ConfigurableEnvironment environment = new StandardEnvironment();
        application.setEnvironment(environment);
        this.context = application.run();
        assertThat(environment.acceptsProfiles(Profiles.of("foo"))).isTrue();
    }

    @Test
    public void addProfilesOrder() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        application.setAdditionalProfiles("foo");
        ConfigurableEnvironment environment = new StandardEnvironment();
        application.setEnvironment(environment);
        this.context = application.run("--spring.profiles.active=bar,spam");
        // Command line should always come last
        assertThat(environment.getActiveProfiles()).containsExactly("foo", "bar", "spam");
    }

    @Test
    public void addProfilesOrderWithProperties() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        application.setAdditionalProfiles("other");
        ConfigurableEnvironment environment = new StandardEnvironment();
        application.setEnvironment(environment);
        this.context = application.run();
        // Active profile should win over default
        assertThat(environment.getProperty("my.property")).isEqualTo("fromotherpropertiesfile");
    }

    @Test
    public void emptyCommandLinePropertySourceNotAdded() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        ConfigurableEnvironment environment = new StandardEnvironment();
        application.setEnvironment(environment);
        this.context = application.run();
        assertThat(environment.getProperty("foo")).isEqualTo("bucket");
    }

    @Test
    public void disableCommandLinePropertySource() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        application.setAddCommandLineProperties(false);
        ConfigurableEnvironment environment = new StandardEnvironment();
        application.setEnvironment(environment);
        this.context = application.run("--foo=bar");
        assertThat(environment).doesNotHave(matchingPropertySource(PropertySource.class, "commandLineArgs"));
    }

    @Test
    public void contextUsesApplicationConversionService() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        assertThat(this.context.getBeanFactory().getConversionService()).isInstanceOf(ApplicationConversionService.class);
        assertThat(this.context.getEnvironment().getConversionService()).isInstanceOf(ApplicationConversionService.class);
    }

    @Test
    public void contextWhenHasAddConversionServiceFalseUsesRegularConversionService() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        application.setAddConversionService(false);
        this.context = application.run();
        assertThat(this.context.getBeanFactory().getConversionService()).isNull();
        assertThat(this.context.getEnvironment().getConversionService()).isNotInstanceOf(ApplicationConversionService.class);
    }

    @Test
    public void runCommandLineRunnersAndApplicationRunners() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.CommandLineRunConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run("arg");
        assertThat(this.context).has(runTestRunnerBean("runnerA"));
        assertThat(this.context).has(runTestRunnerBean("runnerB"));
        assertThat(this.context).has(runTestRunnerBean("runnerC"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void runnersAreCalledAfterStartedIsLoggedAndBeforeApplicationReadyEventIsPublished() throws Exception {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        ApplicationRunner applicationRunner = Mockito.mock(ApplicationRunner.class);
        CommandLineRunner commandLineRunner = Mockito.mock(CommandLineRunner.class);
        application.addInitializers(( context) -> {
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            beanFactory.registerSingleton("commandLineRunner", ((CommandLineRunner) (( args) -> {
                assertThat(this.output.toString()).contains("Started");
                commandLineRunner.run(args);
            })));
            beanFactory.registerSingleton("applicationRunner", ((ApplicationRunner) (( args) -> {
                assertThat(this.output.toString()).contains("Started");
                applicationRunner.run(args);
            })));
        });
        application.setWebApplicationType(NONE);
        ApplicationListener<ApplicationReadyEvent> eventListener = Mockito.mock(ApplicationListener.class);
        application.addListeners(eventListener);
        this.context = application.run();
        InOrder applicationRunnerOrder = Mockito.inOrder(eventListener, applicationRunner);
        applicationRunnerOrder.verify(applicationRunner).run(ArgumentMatchers.any(ApplicationArguments.class));
        applicationRunnerOrder.verify(eventListener).onApplicationEvent(ArgumentMatchers.any(ApplicationReadyEvent.class));
        InOrder commandLineRunnerOrder = Mockito.inOrder(eventListener, commandLineRunner);
        commandLineRunnerOrder.verify(commandLineRunner).run();
        commandLineRunnerOrder.verify(eventListener).onApplicationEvent(ArgumentMatchers.any(ApplicationReadyEvent.class));
    }

    @Test
    public void applicationRunnerFailureCausesApplicationFailedEventToBePublished() throws Exception {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        @SuppressWarnings("unchecked")
        ApplicationListener<SpringApplicationEvent> listener = Mockito.mock(ApplicationListener.class);
        application.addListeners(listener);
        ApplicationRunner runner = Mockito.mock(ApplicationRunner.class);
        Exception failure = new Exception();
        BDDMockito.willThrow(failure).given(runner).run(ArgumentMatchers.isA(ApplicationArguments.class));
        application.addInitializers(( context) -> context.getBeanFactory().registerSingleton("runner", runner));
        assertThatIllegalStateException().isThrownBy(application::run).withCause(failure);
        Mockito.verify(listener).onApplicationEvent(ArgumentMatchers.isA(org.springframework.boot.context.event.ApplicationStartedEvent.class));
        Mockito.verify(listener).onApplicationEvent(ArgumentMatchers.isA(ApplicationFailedEvent.class));
        Mockito.verify(listener, Mockito.never()).onApplicationEvent(ArgumentMatchers.isA(ApplicationReadyEvent.class));
    }

    @Test
    public void commandLineRunnerFailureCausesApplicationFailedEventToBePublished() throws Exception {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        @SuppressWarnings("unchecked")
        ApplicationListener<SpringApplicationEvent> listener = Mockito.mock(ApplicationListener.class);
        application.addListeners(listener);
        CommandLineRunner runner = Mockito.mock(CommandLineRunner.class);
        Exception failure = new Exception();
        BDDMockito.willThrow(failure).given(runner).run();
        application.addInitializers(( context) -> context.getBeanFactory().registerSingleton("runner", runner));
        assertThatIllegalStateException().isThrownBy(application::run).withCause(failure);
        Mockito.verify(listener).onApplicationEvent(ArgumentMatchers.isA(org.springframework.boot.context.event.ApplicationStartedEvent.class));
        Mockito.verify(listener).onApplicationEvent(ArgumentMatchers.isA(ApplicationFailedEvent.class));
        Mockito.verify(listener, Mockito.never()).onApplicationEvent(ArgumentMatchers.isA(ApplicationReadyEvent.class));
    }

    @Test
    public void failureInReadyEventListenerDoesNotCausePublicationOfFailedEvent() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        @SuppressWarnings("unchecked")
        ApplicationListener<SpringApplicationEvent> listener = Mockito.mock(ApplicationListener.class);
        application.addListeners(listener);
        RuntimeException failure = new RuntimeException();
        BDDMockito.willThrow(failure).given(listener).onApplicationEvent(ArgumentMatchers.isA(ApplicationReadyEvent.class));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(application::run).isEqualTo(failure);
        Mockito.verify(listener).onApplicationEvent(ArgumentMatchers.isA(ApplicationReadyEvent.class));
        Mockito.verify(listener, Mockito.never()).onApplicationEvent(ArgumentMatchers.isA(ApplicationFailedEvent.class));
    }

    @Test
    public void failureInReadyEventListenerCloseApplicationContext() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        SpringApplicationTests.ExitCodeListener exitCodeListener = new SpringApplicationTests.ExitCodeListener();
        application.addListeners(exitCodeListener);
        @SuppressWarnings("unchecked")
        ApplicationListener<SpringApplicationEvent> listener = Mockito.mock(ApplicationListener.class);
        application.addListeners(listener);
        SpringApplicationTests.ExitStatusException failure = new SpringApplicationTests.ExitStatusException();
        BDDMockito.willThrow(failure).given(listener).onApplicationEvent(ArgumentMatchers.isA(ApplicationReadyEvent.class));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(application::run);
        Mockito.verify(listener).onApplicationEvent(ArgumentMatchers.isA(ApplicationReadyEvent.class));
        Mockito.verify(listener, Mockito.never()).onApplicationEvent(ArgumentMatchers.isA(ApplicationFailedEvent.class));
        assertThat(exitCodeListener.getExitCode()).isEqualTo(11);
        assertThat(this.output.toString()).contains("Application run failed");
    }

    @Test
    public void loadSources() {
        Class<?>[] sources = new Class<?>[]{ SpringApplicationTests.ExampleConfig.class, SpringApplicationTests.TestCommandLineRunner.class };
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication(sources);
        getSources().add("a");
        application.setWebApplicationType(NONE);
        application.setUseMockLoader(true);
        this.context = application.run();
        Set<Object> allSources = getAllSources();
        assertThat(allSources).contains(SpringApplicationTests.ExampleConfig.class, SpringApplicationTests.TestCommandLineRunner.class, "a");
    }

    @Test
    public void wildcardSources() {
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication();
        getSources().add("classpath:org/springframework/boot/sample-${sample.app.test.prop}.xml");
        application.setWebApplicationType(NONE);
        this.context = application.run();
    }

    @Test
    public void run() {
        this.context = SpringApplication.run(SpringApplicationTests.ExampleWebConfig.class);
        assertThat(this.context).isNotNull();
    }

    @Test
    public void runComponents() {
        this.context = SpringApplication.run(new Class<?>[]{ SpringApplicationTests.ExampleWebConfig.class, Object.class }, new String[0]);
        assertThat(this.context).isNotNull();
    }

    @Test
    public void exit() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        assertThat(this.context).isNotNull();
        assertThat(SpringApplication.exit(this.context)).isEqualTo(0);
    }

    @Test
    public void exitWithExplicitCode() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        SpringApplicationTests.ExitCodeListener listener = new SpringApplicationTests.ExitCodeListener();
        application.addListeners(listener);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        assertThat(this.context).isNotNull();
        assertThat(SpringApplication.exit(this.context, ((ExitCodeGenerator) (() -> 2)))).isEqualTo(2);
        assertThat(listener.getExitCode()).isEqualTo(2);
    }

    @Test
    public void exitWithExplicitCodeFromException() {
        final SpringBootExceptionHandler handler = Mockito.mock(SpringBootExceptionHandler.class);
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExitCodeCommandLineRunConfig.class) {
            @Override
            SpringBootExceptionHandler getSpringBootExceptionHandler() {
                return handler;
            }
        };
        SpringApplicationTests.ExitCodeListener listener = new SpringApplicationTests.ExitCodeListener();
        application.addListeners(listener);
        application.setWebApplicationType(NONE);
        assertThatIllegalStateException().isThrownBy(application::run);
        Mockito.verify(handler).registerExitCode(11);
        assertThat(listener.getExitCode()).isEqualTo(11);
    }

    @Test
    public void exitWithExplicitCodeFromMappedException() {
        final SpringBootExceptionHandler handler = Mockito.mock(SpringBootExceptionHandler.class);
        SpringApplication application = new SpringApplication(SpringApplicationTests.MappedExitCodeCommandLineRunConfig.class) {
            @Override
            SpringBootExceptionHandler getSpringBootExceptionHandler() {
                return handler;
            }
        };
        SpringApplicationTests.ExitCodeListener listener = new SpringApplicationTests.ExitCodeListener();
        application.addListeners(listener);
        application.setWebApplicationType(NONE);
        assertThatIllegalStateException().isThrownBy(application::run);
        Mockito.verify(handler).registerExitCode(11);
        assertThat(listener.getExitCode()).isEqualTo(11);
    }

    @Test
    public void exceptionFromRefreshIsHandledGracefully() {
        final SpringBootExceptionHandler handler = Mockito.mock(SpringBootExceptionHandler.class);
        SpringApplication application = new SpringApplication(SpringApplicationTests.RefreshFailureConfig.class) {
            @Override
            SpringBootExceptionHandler getSpringBootExceptionHandler() {
                return handler;
            }
        };
        SpringApplicationTests.ExitCodeListener listener = new SpringApplicationTests.ExitCodeListener();
        application.addListeners(listener);
        application.setWebApplicationType(NONE);
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(application::run);
        ArgumentCaptor<RuntimeException> exceptionCaptor = ArgumentCaptor.forClass(RuntimeException.class);
        Mockito.verify(handler).registerLoggedException(exceptionCaptor.capture());
        assertThat(exceptionCaptor.getValue()).hasCauseInstanceOf(SpringApplicationTests.RefreshFailureException.class);
        assertThat(this.output.toString()).doesNotContain("NullPointerException");
    }

    @Test
    public void defaultCommandLineArgs() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setDefaultProperties(StringUtils.splitArrayElementsIntoProperties(new String[]{ "baz=", "bar=spam" }, "="));
        application.setWebApplicationType(NONE);
        this.context = application.run("--bar=foo", "bucket", "crap");
        assertThat(this.context).isInstanceOf(AnnotationConfigApplicationContext.class);
        assertThat(getEnvironment().getProperty("bar")).isEqualTo("foo");
        assertThat(getEnvironment().getProperty("baz")).isEqualTo("");
    }

    @Test
    public void commandLineArgsApplyToSpringApplication() {
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run("--spring.main.banner-mode=OFF");
        assertThat(application.getBannerMode()).isEqualTo(OFF);
    }

    @Test
    public void registerShutdownHook() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setApplicationContextClass(SpringApplicationTests.SpyApplicationContext.class);
        this.context = application.run();
        SpringApplicationTests.SpyApplicationContext applicationContext = ((SpringApplicationTests.SpyApplicationContext) (this.context));
        Mockito.verify(applicationContext.getApplicationContext()).registerShutdownHook();
    }

    @Test
    public void registerListener() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class, SpringApplicationTests.ListenerConfig.class);
        application.setApplicationContextClass(SpringApplicationTests.SpyApplicationContext.class);
        Set<ApplicationEvent> events = new LinkedHashSet<>();
        application.addListeners(((ApplicationListener<ApplicationEvent>) (events::add)));
        this.context = application.run();
        assertThat(events).hasAtLeastOneElementOfType(ApplicationPreparedEvent.class);
        assertThat(events).hasAtLeastOneElementOfType(ContextRefreshedEvent.class);
        verifyTestListenerEvents();
    }

    @Test
    public void registerListenerWithCustomMulticaster() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class, SpringApplicationTests.ListenerConfig.class, SpringApplicationTests.Multicaster.class);
        application.setApplicationContextClass(SpringApplicationTests.SpyApplicationContext.class);
        Set<ApplicationEvent> events = new LinkedHashSet<>();
        application.addListeners(((ApplicationListener<ApplicationEvent>) (events::add)));
        this.context = application.run();
        assertThat(events).hasAtLeastOneElementOfType(ApplicationPreparedEvent.class);
        assertThat(events).hasAtLeastOneElementOfType(ContextRefreshedEvent.class);
        verifyTestListenerEvents();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void applicationListenerFromApplicationIsCalledWhenContextFailsRefreshBeforeListenerRegistration() {
        ApplicationListener<ApplicationEvent> listener = Mockito.mock(ApplicationListener.class);
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.addListeners(listener);
        assertThatExceptionOfType(ApplicationContextException.class).isThrownBy(application::run);
        verifyListenerEvents(listener, ApplicationStartingEvent.class, ApplicationEnvironmentPreparedEvent.class, ApplicationContextInitializedEvent.class, ApplicationPreparedEvent.class, ApplicationFailedEvent.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void applicationListenerFromApplicationIsCalledWhenContextFailsRefreshAfterListenerRegistration() {
        ApplicationListener<ApplicationEvent> listener = Mockito.mock(ApplicationListener.class);
        SpringApplication application = new SpringApplication(SpringApplicationTests.BrokenPostConstructConfig.class);
        application.setWebApplicationType(NONE);
        application.addListeners(listener);
        assertThatExceptionOfType(BeanCreationException.class).isThrownBy(application::run);
        verifyListenerEvents(listener, ApplicationStartingEvent.class, ApplicationEnvironmentPreparedEvent.class, ApplicationContextInitializedEvent.class, ApplicationPreparedEvent.class, ApplicationFailedEvent.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void applicationListenerFromContextIsCalledWhenContextFailsRefreshBeforeListenerRegistration() {
        final ApplicationListener<ApplicationEvent> listener = Mockito.mock(ApplicationListener.class);
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.addInitializers(( applicationContext) -> applicationContext.addApplicationListener(listener));
        assertThatExceptionOfType(ApplicationContextException.class).isThrownBy(application::run);
        verifyListenerEvents(listener, ApplicationFailedEvent.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void applicationListenerFromContextIsCalledWhenContextFailsRefreshAfterListenerRegistration() {
        ApplicationListener<ApplicationEvent> listener = Mockito.mock(ApplicationListener.class);
        SpringApplication application = new SpringApplication(SpringApplicationTests.BrokenPostConstructConfig.class);
        application.setWebApplicationType(NONE);
        application.addInitializers(( applicationContext) -> applicationContext.addApplicationListener(listener));
        assertThatExceptionOfType(BeanCreationException.class).isThrownBy(application::run);
        verifyListenerEvents(listener, ApplicationFailedEvent.class);
    }

    @Test
    public void registerShutdownHookOff() {
        SpringApplication application = new SpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setApplicationContextClass(SpringApplicationTests.SpyApplicationContext.class);
        application.setRegisterShutdownHook(false);
        this.context = application.run();
        SpringApplicationTests.SpyApplicationContext applicationContext = ((SpringApplicationTests.SpyApplicationContext) (this.context));
        Mockito.verify(applicationContext.getApplicationContext(), Mockito.never()).registerShutdownHook();
    }

    @Test
    public void headless() {
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        assertThat(System.getProperty("java.awt.headless")).isEqualTo("true");
    }

    @Test
    public void headlessFalse() {
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        setHeadless(false);
        this.context = application.run();
        assertThat(System.getProperty("java.awt.headless")).isEqualTo("false");
    }

    @Test
    public void headlessSystemPropertyTakesPrecedence() {
        System.setProperty("java.awt.headless", "false");
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run();
        assertThat(System.getProperty("java.awt.headless")).isEqualTo("false");
    }

    @Test
    public void getApplicationArgumentsBean() {
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication(SpringApplicationTests.ExampleConfig.class);
        application.setWebApplicationType(NONE);
        this.context = application.run("--debug", "spring", "boot");
        ApplicationArguments args = this.context.getBean(ApplicationArguments.class);
        assertThat(args.getNonOptionArgs()).containsExactly("spring", "boot");
        assertThat(args.containsOption("debug")).isTrue();
    }

    @Test
    public void webApplicationSwitchedOffInListener() {
        SpringApplicationTests.TestSpringApplication application = new SpringApplicationTests.TestSpringApplication(SpringApplicationTests.ExampleConfig.class);
        addListeners(((ApplicationListener<ApplicationEnvironmentPreparedEvent>) (( event) -> {
            Assertions.assertThat(event.getEnvironment()).isInstanceOf(.class);
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(event.getEnvironment(), "foo=bar");
            event.getSpringApplication().setWebApplicationType(WebApplicationType.NONE);
        })));
        this.context = application.run();
        assertThat(this.context.getEnvironment()).isNotInstanceOf(StandardServletEnvironment.class);
        assertThat(this.context.getEnvironment().getProperty("foo")).isEqualTo("bar");
        Iterator<PropertySource<?>> iterator = this.context.getEnvironment().getPropertySources().iterator();
        assertThat(iterator.next().getName()).isEqualTo("configurationProperties");
        assertThat(iterator.next().getName()).isEqualTo(INLINED_PROPERTIES_PROPERTY_SOURCE_NAME);
    }

    @Test
    public void nonWebApplicationConfiguredViaAPropertyHasTheCorrectTypeOfContextAndEnvironment() {
        ConfigurableApplicationContext context = new SpringApplication(SpringApplicationTests.ExampleConfig.class).run("--spring.main.web-application-type=none");
        assertThat(context).isNotInstanceOfAny(WebApplicationContext.class, ReactiveWebApplicationContext.class);
        assertThat(context.getEnvironment()).isNotInstanceOfAny(ConfigurableWebEnvironment.class);
    }

    @Test
    public void webApplicationConfiguredViaAPropertyHasTheCorrectTypeOfContextAndEnvironment() {
        ConfigurableApplicationContext context = new SpringApplication(SpringApplicationTests.ExampleWebConfig.class).run("--spring.main.web-application-type=servlet");
        assertThat(context).isInstanceOf(WebApplicationContext.class);
        assertThat(context.getEnvironment()).isInstanceOf(StandardServletEnvironment.class);
    }

    @Test
    public void reactiveApplicationConfiguredViaAPropertyHasTheCorrectTypeOfContextAndEnvironment() {
        ConfigurableApplicationContext context = new SpringApplication(SpringApplicationTests.ExampleReactiveWebConfig.class).run("--spring.main.web-application-type=reactive");
        assertThat(context).isInstanceOf(ReactiveWebApplicationContext.class);
        assertThat(context.getEnvironment()).isInstanceOf(StandardReactiveWebEnvironment.class);
    }

    @Test
    public void environmentIsConvertedIfTypeDoesNotMatch() {
        ConfigurableApplicationContext context = new SpringApplication(SpringApplicationTests.ExampleReactiveWebConfig.class).run("--spring.profiles.active=withwebapplicationtype");
        assertThat(context).isInstanceOf(ReactiveWebApplicationContext.class);
        assertThat(context.getEnvironment()).isInstanceOf(StandardReactiveWebEnvironment.class);
    }

    @Test
    public void failureResultsInSingleStackTrace() throws Exception {
        ThreadGroup group = new ThreadGroup("main");
        Thread thread = new Thread(group, "main") {
            @Override
            public void run() {
                SpringApplication application = new SpringApplication(SpringApplicationTests.FailingConfig.class);
                application.setWebApplicationType(NONE);
                application.run();
            }
        };
        thread.start();
        thread.join(6000);
        int occurrences = StringUtils.countOccurrencesOf(this.output.toString(), "Caused by: java.lang.RuntimeException: ExpectedError");
        assertThat(occurrences).as("Expected single stacktrace").isEqualTo(1);
    }

    @Test
    public void beanDefinitionOverridingIsDisabledByDefault() {
        assertThatExceptionOfType(BeanDefinitionOverrideException.class).isThrownBy(() -> new SpringApplication(.class, .class).run());
    }

    @Test
    public void beanDefinitionOverridingCanBeEnabled() {
        assertThat(new SpringApplication(SpringApplicationTests.ExampleConfig.class, SpringApplicationTests.OverrideConfig.class).run("--spring.main.allow-bean-definition-overriding=true", "--spring.main.web-application-type=none").getBean("someBean")).isEqualTo("override");
    }

    @Test
    public void lazyInitializationIsDisabledByDefault() {
        assertThat(new SpringApplication(SpringApplicationTests.LazyInitializationConfig.class).run("--spring.main.web-application-type=none").getBean(AtomicInteger.class)).hasValue(1);
    }

    @Test
    public void lazyInitializationCanBeEnabled() {
        assertThat(new SpringApplication(SpringApplicationTests.LazyInitializationConfig.class).run("--spring.main.web-application-type=none", "--spring.main.lazy-initialization=true").getBean(AtomicInteger.class)).hasValue(0);
    }

    @Configuration
    protected static class InaccessibleConfiguration {
        private InaccessibleConfiguration() {
        }
    }

    public static class SpyApplicationContext extends AnnotationConfigApplicationContext {
        ConfigurableApplicationContext applicationContext = Mockito.spy(new AnnotationConfigApplicationContext());

        @Override
        public void registerShutdownHook() {
            this.applicationContext.registerShutdownHook();
        }

        public ConfigurableApplicationContext getApplicationContext() {
            return this.applicationContext;
        }

        @Override
        public void close() {
            this.applicationContext.close();
        }
    }

    private static class TestSpringApplication extends SpringApplication {
        private BeanDefinitionLoader loader;

        private boolean useMockLoader;

        private Mode bannerMode;

        TestSpringApplication(Class<?>... primarySources) {
            super(primarySources);
        }

        TestSpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
            super(resourceLoader, primarySources);
        }

        public void setUseMockLoader(boolean useMockLoader) {
            this.useMockLoader = useMockLoader;
        }

        @Override
        protected BeanDefinitionLoader createBeanDefinitionLoader(BeanDefinitionRegistry registry, Object[] sources) {
            if (this.useMockLoader) {
                this.loader = Mockito.mock(BeanDefinitionLoader.class);
            } else {
                this.loader = Mockito.spy(super.createBeanDefinitionLoader(registry, sources));
            }
            return this.loader;
        }

        public BeanDefinitionLoader getLoader() {
            return this.loader;
        }

        @Override
        public void setBannerMode(Banner.Mode bannerMode) {
            super.setBannerMode(bannerMode);
            this.bannerMode = bannerMode;
        }

        public Mode getBannerMode() {
            return this.bannerMode;
        }
    }

    @Configuration
    static class ExampleConfig {
        @Bean
        public String someBean() {
            return "test";
        }
    }

    @Configuration
    static class OverrideConfig {
        @Bean
        public String someBean() {
            return "override";
        }
    }

    @Configuration
    static class BrokenPostConstructConfig {
        @Bean
        public SpringApplicationTests.BrokenPostConstructConfig.Thing thing() {
            return new SpringApplicationTests.BrokenPostConstructConfig.Thing();
        }

        static class Thing {
            @PostConstruct
            public void boom() {
                throw new IllegalStateException();
            }
        }
    }

    @Configuration
    static class ListenerConfig {
        @Bean
        public ApplicationListener<?> testApplicationListener() {
            return Mockito.mock(ApplicationListener.class);
        }
    }

    @Configuration
    static class Multicaster {
        @Bean(name = AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME)
        public ApplicationEventMulticaster applicationEventMulticaster() {
            return Mockito.spy(new SimpleApplicationEventMulticaster());
        }
    }

    @Configuration
    static class ExampleWebConfig {
        @Bean
        public TomcatServletWebServerFactory webServer() {
            return new TomcatServletWebServerFactory(0);
        }
    }

    @Configuration
    static class ExampleReactiveWebConfig {
        @Bean
        public NettyReactiveWebServerFactory webServerFactory() {
            return new NettyReactiveWebServerFactory(0);
        }

        @Bean
        public HttpHandler httpHandler() {
            return ( serverHttpRequest, serverHttpResponse) -> Mono.empty();
        }
    }

    @Configuration
    static class FailingConfig {
        @Bean
        public Object fail() {
            throw new RuntimeException("ExpectedError");
        }
    }

    @Configuration
    static class CommandLineRunConfig {
        @Bean
        public SpringApplicationTests.TestCommandLineRunner runnerC() {
            return new SpringApplicationTests.TestCommandLineRunner(Ordered.LOWEST_PRECEDENCE, "runnerB", "runnerA");
        }

        @Bean
        public SpringApplicationTests.TestApplicationRunner runnerB() {
            return new SpringApplicationTests.TestApplicationRunner(((Ordered.LOWEST_PRECEDENCE) - 1), "runnerA");
        }

        @Bean
        public SpringApplicationTests.TestCommandLineRunner runnerA() {
            return new SpringApplicationTests.TestCommandLineRunner(Ordered.HIGHEST_PRECEDENCE);
        }
    }

    @Configuration
    static class ExitCodeCommandLineRunConfig {
        @Bean
        public CommandLineRunner runner() {
            return ( args) -> {
                throw new IllegalStateException(new org.springframework.boot.ExitStatusException());
            };
        }
    }

    @Configuration
    static class MappedExitCodeCommandLineRunConfig {
        @Bean
        public CommandLineRunner runner() {
            return ( args) -> {
                throw new IllegalStateException();
            };
        }

        @Bean
        public ExitCodeExceptionMapper exceptionMapper() {
            return ( exception) -> {
                if (exception instanceof IllegalStateException) {
                    return 11;
                }
                return 0;
            };
        }
    }

    @Configuration
    static class RefreshFailureConfig {
        @PostConstruct
        public void fail() {
            throw new SpringApplicationTests.RefreshFailureException();
        }
    }

    @Configuration
    static class LazyInitializationConfig {
        @Bean
        public AtomicInteger counter() {
            return new AtomicInteger(0);
        }

        @Bean
        public SpringApplicationTests.LazyInitializationConfig.LazyBean lazyBean(AtomicInteger counter) {
            return new SpringApplicationTests.LazyInitializationConfig.LazyBean(counter);
        }

        static class LazyBean {
            LazyBean(AtomicInteger counter) {
                counter.incrementAndGet();
            }
        }
    }

    static class ExitStatusException extends RuntimeException implements ExitCodeGenerator {
        @Override
        public int getExitCode() {
            return 11;
        }
    }

    static class RefreshFailureException extends RuntimeException {}

    abstract static class AbstractTestRunner implements ApplicationContextAware , Ordered {
        private final String[] expectedBefore;

        private ApplicationContext applicationContext;

        private final int order;

        private boolean run;

        AbstractTestRunner(int order, String... expectedBefore) {
            this.expectedBefore = expectedBefore;
            this.order = order;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        @Override
        public int getOrder() {
            return this.order;
        }

        public void markAsRan() {
            this.run = true;
            for (String name : this.expectedBefore) {
                SpringApplicationTests.AbstractTestRunner bean = this.applicationContext.getBean(name, SpringApplicationTests.AbstractTestRunner.class);
                assertThat(bean.hasRun()).isTrue();
            }
        }

        public boolean hasRun() {
            return this.run;
        }
    }

    private static class TestCommandLineRunner extends SpringApplicationTests.AbstractTestRunner implements CommandLineRunner {
        TestCommandLineRunner(int order, String... expectedBefore) {
            super(order, expectedBefore);
        }

        @Override
        public void run(String... args) {
            markAsRan();
        }
    }

    private static class TestApplicationRunner extends SpringApplicationTests.AbstractTestRunner implements ApplicationRunner {
        TestApplicationRunner(int order, String... expectedBefore) {
            super(order, expectedBefore);
        }

        @Override
        public void run(ApplicationArguments args) {
            markAsRan();
        }
    }

    private static class ExitCodeListener implements ApplicationListener<ExitCodeEvent> {
        private Integer exitCode;

        @Override
        public void onApplicationEvent(ExitCodeEvent event) {
            this.exitCode = event.getExitCode();
        }

        public Integer getExitCode() {
            return this.exitCode;
        }
    }

    private static class MockResourceLoader implements ResourceLoader {
        private final Map<String, Resource> resources = new HashMap<>();

        public void addResource(String source, String path) {
            this.resources.put(source, new ClassPathResource(path, getClass()));
        }

        @Override
        public Resource getResource(String path) {
            Resource resource = this.resources.get(path);
            return resource != null ? resource : new ClassPathResource("doesnotexist");
        }

        @Override
        public ClassLoader getClassLoader() {
            return getClass().getClassLoader();
        }
    }
}

