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
package org.springframework.boot.autoconfigure.web.servlet;


import MediaType.ALL;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidatorAdapter;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.FormContentFilter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.resource.VersionStrategy;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;


/**
 * Tests for {@link WebMvcAutoConfiguration}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Brian Clozel
 * @author Edd? Mel?ndez
 * @author Kristine Jetzke
 * @author Artsiom Yudovin
 */
public class WebMvcAutoConfigurationTests {
    private static final MockServletWebServerFactory webServerFactory = new MockServletWebServerFactory();

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(WebMvcAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class)).withUserConfiguration(WebMvcAutoConfigurationTests.Config.class);

    @Test
    public void handlerAdaptersCreated() {
        this.contextRunner.run(( context) -> {
            assertThat(context).getBeans(.class).hasSize(3);
            assertThat(context.getBean(.class).getMessageConverters()).isNotEmpty().isEqualTo(context.getBean(.class).getConverters());
        });
    }

    @Test
    public void handlerMappingsCreated() {
        this.contextRunner.run(( context) -> assertThat(context).getBeans(.class).hasSize(5));
    }

    @Test
    public void resourceHandlerMapping() {
        this.contextRunner.run(( context) -> {
            Map<String, List<Resource>> locations = getResourceMappingLocations(context);
            assertThat(locations.get("/**")).hasSize(5);
            assertThat(locations.get("/webjars/**")).hasSize(1);
            assertThat(locations.get("/webjars/**").get(0)).isEqualTo(new ClassPathResource("/META-INF/resources/webjars/"));
            assertThat(getResourceResolvers(context, "/webjars/**")).hasSize(1);
            assertThat(getResourceTransformers(context, "/webjars/**")).hasSize(0);
            assertThat(getResourceResolvers(context, "/**")).hasSize(1);
            assertThat(getResourceTransformers(context, "/**")).hasSize(0);
        });
    }

    @Test
    public void customResourceHandlerMapping() {
        this.contextRunner.withPropertyValues("spring.mvc.static-path-pattern:/static/**").run(( context) -> {
            Map<String, List<Resource>> locations = getResourceMappingLocations(context);
            assertThat(locations.get("/static/**")).hasSize(5);
            assertThat(getResourceResolvers(context, "/static/**")).hasSize(1);
        });
    }

    @Test
    public void resourceHandlerMappingOverrideWebjars() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.WebJars.class).run(( context) -> {
            Map<String, List<Resource>> locations = getResourceMappingLocations(context);
            assertThat(locations.get("/webjars/**")).hasSize(1);
            assertThat(locations.get("/webjars/**").get(0)).isEqualTo(new ClassPathResource("/foo/"));
        });
    }

    @Test
    public void resourceHandlerMappingOverrideAll() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.AllResources.class).run(( context) -> {
            Map<String, List<Resource>> locations = getResourceMappingLocations(context);
            assertThat(locations.get("/**")).hasSize(1);
            assertThat(locations.get("/**").get(0)).isEqualTo(new ClassPathResource("/foo/"));
        });
    }

    @Test
    public void resourceHandlerMappingDisabled() {
        this.contextRunner.withPropertyValues("spring.resources.add-mappings:false").run(( context) -> assertThat(context.getBean("resourceHandlerMapping")).isEqualTo(null));
    }

    @Test
    public void resourceHandlerChainEnabled() {
        this.contextRunner.withPropertyValues("spring.resources.chain.enabled:true").run(( context) -> {
            assertThat(getResourceResolvers(context, "/webjars/**")).hasSize(2);
            assertThat(getResourceTransformers(context, "/webjars/**")).hasSize(1);
            assertThat(getResourceResolvers(context, "/**")).extractingResultOf("getClass").containsOnly(.class, .class);
            assertThat(getResourceTransformers(context, "/**")).extractingResultOf("getClass").containsOnly(.class);
        });
    }

    @Test
    public void resourceHandlerFixedStrategyEnabled() {
        this.contextRunner.withPropertyValues("spring.resources.chain.strategy.fixed.enabled:true", "spring.resources.chain.strategy.fixed.version:test", "spring.resources.chain.strategy.fixed.paths:/**/*.js").run(( context) -> {
            assertThat(getResourceResolvers(context, "/webjars/**")).hasSize(3);
            assertThat(getResourceTransformers(context, "/webjars/**")).hasSize(2);
            assertThat(getResourceResolvers(context, "/**")).extractingResultOf("getClass").containsOnly(.class, .class, .class);
            assertThat(getResourceTransformers(context, "/**")).extractingResultOf("getClass").containsOnly(.class, .class);
            VersionResourceResolver resolver = ((VersionResourceResolver) (getResourceResolvers(context, "/**").get(1)));
            assertThat(resolver.getStrategyMap().get("/**/*.js")).isInstanceOf(.class);
        });
    }

    @Test
    public void resourceHandlerContentStrategyEnabled() {
        this.contextRunner.withPropertyValues("spring.resources.chain.strategy.content.enabled:true", "spring.resources.chain.strategy.content.paths:/**,/*.png").run(( context) -> {
            assertThat(getResourceResolvers(context, "/webjars/**")).hasSize(3);
            assertThat(getResourceTransformers(context, "/webjars/**")).hasSize(2);
            assertThat(getResourceResolvers(context, "/**")).extractingResultOf("getClass").containsOnly(.class, .class, .class);
            assertThat(getResourceTransformers(context, "/**")).extractingResultOf("getClass").containsOnly(.class, .class);
            VersionResourceResolver resolver = ((VersionResourceResolver) (getResourceResolvers(context, "/**").get(1)));
            assertThat(resolver.getStrategyMap().get("/*.png")).isInstanceOf(.class);
        });
    }

    @Test
    public void resourceHandlerChainCustomized() {
        this.contextRunner.withPropertyValues("spring.resources.chain.enabled:true", "spring.resources.chain.cache:false", "spring.resources.chain.strategy.content.enabled:true", "spring.resources.chain.strategy.content.paths:/**,/*.png", "spring.resources.chain.strategy.fixed.enabled:true", "spring.resources.chain.strategy.fixed.version:test", "spring.resources.chain.strategy.fixed.paths:/**/*.js", "spring.resources.chain.html-application-cache:true", "spring.resources.chain.compressed:true").run(( context) -> {
            assertThat(getResourceResolvers(context, "/webjars/**")).hasSize(3);
            assertThat(getResourceTransformers(context, "/webjars/**")).hasSize(2);
            assertThat(getResourceResolvers(context, "/**")).extractingResultOf("getClass").containsOnly(.class, .class, .class);
            assertThat(getResourceTransformers(context, "/**")).extractingResultOf("getClass").containsOnly(.class, .class);
            VersionResourceResolver resolver = ((VersionResourceResolver) (getResourceResolvers(context, "/**").get(1)));
            Map<String, VersionStrategy> strategyMap = resolver.getStrategyMap();
            assertThat(strategyMap.get("/*.png")).isInstanceOf(.class);
            assertThat(strategyMap.get("/**/*.js")).isInstanceOf(.class);
        });
    }

    @Test
    public void noLocaleResolver() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void overrideLocale() {
        this.contextRunner.withPropertyValues("spring.mvc.locale:en_UK", "spring.mvc.locale-resolver=fixed").run(( loader) -> {
            // mock request and set user preferred locale
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addPreferredLocale(StringUtils.parseLocaleString("nl_NL"));
            request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "nl_NL");
            LocaleResolver localeResolver = loader.getBean(.class);
            assertThat(localeResolver).isInstanceOf(.class);
            Locale locale = localeResolver.resolveLocale(request);
            // test locale resolver uses fixed locale and not user preferred
            // locale
            assertThat(locale.toString()).isEqualTo("en_UK");
        });
    }

    @Test
    public void useAcceptHeaderLocale() {
        this.contextRunner.withPropertyValues("spring.mvc.locale:en_UK").run(( loader) -> {
            // mock request and set user preferred locale
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addPreferredLocale(StringUtils.parseLocaleString("nl_NL"));
            request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "nl_NL");
            LocaleResolver localeResolver = loader.getBean(.class);
            assertThat(localeResolver).isInstanceOf(.class);
            Locale locale = localeResolver.resolveLocale(request);
            // test locale resolver uses user preferred locale
            assertThat(locale.toString()).isEqualTo("nl_NL");
        });
    }

    @Test
    public void useDefaultLocaleIfAcceptHeaderNoSet() {
        this.contextRunner.withPropertyValues("spring.mvc.locale:en_UK").run(( context) -> {
            // mock request and set user preferred locale
            MockHttpServletRequest request = new MockHttpServletRequest();
            LocaleResolver localeResolver = context.getBean(.class);
            assertThat(localeResolver).isInstanceOf(.class);
            Locale locale = localeResolver.resolveLocale(request);
            // test locale resolver uses default locale if no header is set
            assertThat(locale.toString()).isEqualTo("en_UK");
        });
    }

    @Test
    public void noDateFormat() {
        this.contextRunner.run(( context) -> {
            FormattingConversionService conversionService = context.getBean(.class);
            Date date = new DateTime(1988, 6, 25, 20, 30).toDate();
            // formatting conversion service should use simple toString()
            assertThat(conversionService.convert(date, .class)).isEqualTo(date.toString());
        });
    }

    @Test
    public void overrideDateFormat() {
        this.contextRunner.withPropertyValues("spring.mvc.date-format:dd*MM*yyyy").run(( context) -> {
            FormattingConversionService conversionService = context.getBean(.class);
            Date date = new DateTime(1988, 6, 25, 20, 30).toDate();
            assertThat(conversionService.convert(date, .class)).isEqualTo("25*06*1988");
        });
    }

    @Test
    public void noMessageCodesResolver() {
        this.contextRunner.run(( context) -> assertThat(context.getBean(.class).getMessageCodesResolver()).isNull());
    }

    @Test
    public void overrideMessageCodesFormat() {
        this.contextRunner.withPropertyValues("spring.mvc.messageCodesResolverFormat:POSTFIX_ERROR_CODE").run(( context) -> assertThat(context.getBean(.class).getMessageCodesResolver()).isNotNull());
    }

    @Test
    public void ignoreDefaultModelOnRedirectIsTrue() {
        this.contextRunner.run(( context) -> assertThat(context.getBean(.class)).extracting("ignoreDefaultModelOnRedirect").containsExactly(true));
    }

    @Test
    public void overrideIgnoreDefaultModelOnRedirect() {
        this.contextRunner.withPropertyValues("spring.mvc.ignore-default-model-on-redirect:false").run(( context) -> assertThat(context.getBean(.class)).extracting("ignoreDefaultModelOnRedirect").containsExactly(false));
    }

    @Test
    public void customViewResolver() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.CustomViewResolver.class).run(( context) -> assertThat(context.getBean("viewResolver")).isInstanceOf(.class));
    }

    @Test
    public void customContentNegotiatingViewResolver() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.CustomContentNegotiatingViewResolver.class).run(( context) -> assertThat(context).getBeanNames(.class).containsOnly("myViewResolver"));
    }

    @Test
    public void faviconMapping() {
        this.contextRunner.run(( context) -> {
            assertThat(context).getBeanNames(.class).contains("faviconRequestHandler");
            assertThat(context).getBeans(.class).containsKey("faviconHandlerMapping");
            assertThat(getFaviconMappingLocations(context).get("/**/favicon.ico")).hasSize(6);
        });
    }

    @Test
    public void faviconMappingUsesStaticLocations() {
        this.contextRunner.withPropertyValues("spring.resources.static-locations=classpath:/static").run(( context) -> assertThat(getFaviconMappingLocations(context).get("/**/favicon.ico")).hasSize(3));
    }

    @Test
    public void faviconMappingDisabled() {
        this.contextRunner.withPropertyValues("spring.mvc.favicon.enabled:false").run(( context) -> {
            assertThat(context).getBeans(.class).doesNotContainKey("faviconRequestHandler");
            assertThat(context).getBeans(.class).doesNotContainKey("faviconHandlerMapping");
        });
    }

    @Test
    public void defaultAsyncRequestTimeout() {
        this.contextRunner.run(( context) -> assertThat(ReflectionTestUtils.getField(context.getBean(.class), "asyncRequestTimeout")).isNull());
    }

    @Test
    public void customAsyncRequestTimeout() {
        this.contextRunner.withPropertyValues("spring.mvc.async.request-timeout:12345").run(( context) -> assertThat(ReflectionTestUtils.getField(context.getBean(.class), "asyncRequestTimeout")).isEqualTo(12345L));
    }

    @Test
    public void asyncTaskExecutorWithApplicationTaskExecutor() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(TaskExecutionAutoConfiguration.class)).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(ReflectionTestUtils.getField(context.getBean(.class), "taskExecutor")).isSameAs(context.getBean("applicationTaskExecutor"));
        });
    }

    @Test
    public void asyncTaskExecutorWithNonMatchApplicationTaskExecutorBean() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.CustomApplicationTaskExecutorConfig.class).withConfiguration(AutoConfigurations.of(TaskExecutionAutoConfiguration.class)).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(ReflectionTestUtils.getField(context.getBean(.class), "taskExecutor")).isNotSameAs(context.getBean("applicationTaskExecutor"));
        });
    }

    @Test
    public void asyncTaskExecutorWithMvcConfigurerCanOverrideExecutor() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.CustomAsyncTaskExecutorConfigurer.class).withConfiguration(AutoConfigurations.of(TaskExecutionAutoConfiguration.class)).run(( context) -> assertThat(ReflectionTestUtils.getField(context.getBean(.class), "taskExecutor")).isSameAs(context.getBean(.class).taskExecutor));
    }

    @Test
    public void asyncTaskExecutorWithCustomNonApplicationTaskExecutor() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.CustomAsyncTaskExecutorConfig.class).withConfiguration(AutoConfigurations.of(TaskExecutionAutoConfiguration.class)).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(ReflectionTestUtils.getField(context.getBean(.class), "taskExecutor")).isNotSameAs(context.getBean("customTaskExecutor"));
        });
    }

    @Test
    public void customMediaTypes() {
        this.contextRunner.withPropertyValues("spring.mvc.contentnegotiation.media-types.yaml:text/yaml", "spring.mvc.contentnegotiation.favor-path-extension:true").run(( context) -> {
            RequestMappingHandlerAdapter adapter = context.getBean(.class);
            ContentNegotiationManager contentNegotiationManager = ((ContentNegotiationManager) (ReflectionTestUtils.getField(adapter, "contentNegotiationManager")));
            assertThat(contentNegotiationManager.getAllFileExtensions()).contains("yaml");
        });
    }

    @Test
    public void formContentFilterIsAutoConfigured() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void formContentFilterCanBeOverridden() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.CustomFormContentFilter.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void formContentFilterCanBeDisabled() {
        this.contextRunner.withPropertyValues("spring.mvc.formcontent.filter.enabled=false").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void hiddenHttpMethodFilterCanBeDisabled() {
        this.contextRunner.withPropertyValues("spring.mvc.hiddenmethod.filter.enabled=false").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void hiddenHttpMethodFilterEnabledByDefault() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void customConfigurableWebBindingInitializer() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.CustomConfigurableWebBindingInitializer.class).run(( context) -> assertThat(context.getBean(.class).getWebBindingInitializer()).isInstanceOf(.class));
    }

    @Test
    public void customRequestMappingHandlerMapping() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.CustomRequestMappingHandlerMapping.class).run(( context) -> assertThat(context).getBean(.class).isInstanceOf(.class));
    }

    @Test
    public void customRequestMappingHandlerAdapter() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.CustomRequestMappingHandlerAdapter.class).run(( context) -> assertThat(context).getBean(.class).isInstanceOf(.class));
    }

    @Test
    public void multipleWebMvcRegistrations() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.MultipleWebMvcRegistrations.class).run(( context) -> {
            assertThat(context.getBean(.class)).isNotInstanceOf(.class);
            assertThat(context.getBean(.class)).isNotInstanceOf(.class);
        });
    }

    @Test
    public void defaultLogResolvedException() {
        this.contextRunner.run(assertExceptionResolverWarnLoggers(( logger) -> assertThat(logger).isNull()));
    }

    @Test
    public void customLogResolvedException() {
        this.contextRunner.withPropertyValues("spring.mvc.log-resolved-exception:true").run(assertExceptionResolverWarnLoggers(( logger) -> assertThat(logger).isNotNull()));
    }

    @Test
    public void welcomePageHandlerMappingIsAutoConfigured() {
        this.contextRunner.withPropertyValues("spring.resources.static-locations:classpath:/welcome-page/").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).getRootHandler()).isNotNull();
        });
    }

    @Test
    public void validatorWhenNoValidatorShouldUseDefault() {
        this.contextRunner.run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).getBeanNames(.class).containsOnly("mvcValidator");
        });
    }

    @Test
    public void validatorWhenNoCustomizationShouldUseAutoConfigured() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class)).run(( context) -> {
            assertThat(context).getBeanNames(.class).containsOnly("defaultValidator");
            assertThat(context).getBeanNames(.class).containsOnly("defaultValidator", "mvcValidator");
            Validator validator = context.getBean("mvcValidator", .class);
            assertThat(validator).isInstanceOf(.class);
            Object defaultValidator = context.getBean("defaultValidator");
            assertThat(((ValidatorAdapter) (validator)).getTarget()).isSameAs(defaultValidator);
            // Primary Spring validator is the one used by MVC behind the scenes
            assertThat(context.getBean(.class)).isEqualTo(defaultValidator);
        });
    }

    @Test
    public void validatorWithConfigurerShouldUseSpringValidator() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.MvcValidator.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).getBeanNames(.class).containsOnly("mvcValidator");
            assertThat(context.getBean("mvcValidator")).isSameAs(context.getBean(.class).validator);
        });
    }

    @Test
    public void validatorWithConfigurerDoesNotExposeJsr303() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.MvcJsr303Validator.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).getBeanNames(.class).containsOnly("mvcValidator");
            Validator validator = context.getBean("mvcValidator", .class);
            assertThat(validator).isInstanceOf(.class);
            assertThat(((ValidatorAdapter) (validator)).getTarget()).isSameAs(context.getBean(.class).validator);
        });
    }

    @Test
    public void validatorWithConfigurerTakesPrecedence() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class)).withUserConfiguration(WebMvcAutoConfigurationTests.MvcValidator.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            assertThat(context).getBeanNames(.class).containsOnly("defaultValidator", "mvcValidator");
            assertThat(context.getBean("mvcValidator")).isSameAs(context.getBean(.class).validator);
            // Primary Spring validator is the auto-configured one as the MVC one
            // has been customized via a WebMvcConfigurer
            assertThat(context.getBean(.class)).isEqualTo(context.getBean("defaultValidator"));
        });
    }

    @Test
    public void validatorWithCustomSpringValidatorIgnored() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class)).withUserConfiguration(WebMvcAutoConfigurationTests.CustomSpringValidator.class).run(( context) -> {
            assertThat(context).getBeanNames(.class).containsOnly("defaultValidator");
            assertThat(context).getBeanNames(.class).containsOnly("customSpringValidator", "defaultValidator", "mvcValidator");
            Validator validator = context.getBean("mvcValidator", .class);
            assertThat(validator).isInstanceOf(.class);
            Object defaultValidator = context.getBean("defaultValidator");
            assertThat(((ValidatorAdapter) (validator)).getTarget()).isSameAs(defaultValidator);
            // Primary Spring validator is the one used by MVC behind the scenes
            assertThat(context.getBean(.class)).isEqualTo(defaultValidator);
        });
    }

    @Test
    public void validatorWithCustomJsr303ValidatorExposedAsSpringValidator() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class)).withUserConfiguration(WebMvcAutoConfigurationTests.CustomJsr303Validator.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).getBeanNames(.class).containsOnly("customJsr303Validator");
            assertThat(context).getBeanNames(.class).containsOnly("mvcValidator");
            Validator validator = context.getBean(.class);
            assertThat(validator).isInstanceOf(.class);
            Validator target = ((ValidatorAdapter) (validator)).getTarget();
            assertThat(ReflectionTestUtils.getField(target, "targetValidator")).isSameAs(context.getBean("customJsr303Validator"));
        });
    }

    @Test
    public void httpMessageConverterThatUsesConversionServiceDoesNotCreateACycle() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.CustomHttpMessageConverter.class).run(( context) -> assertThat(context).hasNotFailed());
    }

    @Test
    public void cachePeriod() {
        this.contextRunner.withPropertyValues("spring.resources.cache.period:5").run(this::assertCachePeriod);
    }

    @Test
    public void cacheControl() {
        this.contextRunner.withPropertyValues("spring.resources.cache.cachecontrol.max-age:5", "spring.resources.cache.cachecontrol.proxy-revalidate:true").run(this::assertCacheControl);
    }

    @Test
    public void defaultPathMatching() {
        this.contextRunner.run(( context) -> {
            RequestMappingHandlerMapping handlerMapping = context.getBean(.class);
            assertThat(handlerMapping.useSuffixPatternMatch()).isFalse();
            assertThat(handlerMapping.useRegisteredSuffixPatternMatch()).isFalse();
        });
    }

    @Test
    public void useSuffixPatternMatch() {
        this.contextRunner.withPropertyValues("spring.mvc.pathmatch.use-suffix-pattern:true", "spring.mvc.pathmatch.use-registered-suffix-pattern:true").run(( context) -> {
            RequestMappingHandlerMapping handlerMapping = context.getBean(.class);
            assertThat(handlerMapping.useSuffixPatternMatch()).isTrue();
            assertThat(handlerMapping.useRegisteredSuffixPatternMatch()).isTrue();
        });
    }

    @Test
    public void defaultContentNegotiation() {
        this.contextRunner.run(( context) -> {
            RequestMappingHandlerMapping handlerMapping = context.getBean(.class);
            ContentNegotiationManager contentNegotiationManager = handlerMapping.getContentNegotiationManager();
            assertThat(contentNegotiationManager.getStrategies()).doesNotHaveAnyElementsOfTypes(.class);
        });
    }

    @Test
    public void pathExtensionContentNegotiation() {
        this.contextRunner.withPropertyValues("spring.mvc.contentnegotiation.favor-path-extension:true").run(( context) -> {
            RequestMappingHandlerMapping handlerMapping = context.getBean(.class);
            ContentNegotiationManager contentNegotiationManager = handlerMapping.getContentNegotiationManager();
            assertThat(contentNegotiationManager.getStrategies()).hasAtLeastOneElementOfType(.class);
        });
    }

    @Test
    public void queryParameterContentNegotiation() {
        this.contextRunner.withPropertyValues("spring.mvc.contentnegotiation.favor-parameter:true").run(( context) -> {
            RequestMappingHandlerMapping handlerMapping = context.getBean(.class);
            ContentNegotiationManager contentNegotiationManager = handlerMapping.getContentNegotiationManager();
            assertThat(contentNegotiationManager.getStrategies()).hasAtLeastOneElementOfType(.class);
        });
    }

    @Test
    public void customConfigurerAppliedAfterAutoConfig() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.CustomConfigurer.class).run(( context) -> {
            ContentNegotiationManager manager = context.getBean(.class);
            assertThat(manager.getStrategies()).anyMatch(( strategy) -> .class.isAssignableFrom(strategy.getClass()));
        });
    }

    @Test
    public void contentNegotiationStrategySkipsPathExtension() throws Exception {
        ContentNegotiationStrategy delegate = Mockito.mock(ContentNegotiationStrategy.class);
        ContentNegotiationStrategy strategy = new WebMvcAutoConfiguration.OptionalPathExtensionContentNegotiationStrategy(delegate);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(((PathExtensionContentNegotiationStrategy.class.getName()) + ".SKIP"), Boolean.TRUE);
        ServletWebRequest webRequest = new ServletWebRequest(request);
        java.util.List<MediaType> mediaTypes = strategy.resolveMediaTypes(webRequest);
        assertThat(mediaTypes).containsOnly(ALL);
    }

    @Test
    public void requestContextFilterIsAutoConfigured() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void whenUserDefinesARequestContextFilterTheAutoConfiguredRegistrationBacksOff() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.RequestContextFilterConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasBean("customRequestContextFilter");
        });
    }

    @Test
    public void whenUserDefinesARequestContextFilterRegistrationTheAutoConfiguredFilterBacksOff() {
        this.contextRunner.withUserConfiguration(WebMvcAutoConfigurationTests.RequestContextFilterRegistrationConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasBean("customRequestContextFilterRegistration");
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Configuration
    protected static class ViewConfig {
        @Bean
        public View jsonView() {
            return new AbstractView() {
                @Override
                protected void renderMergedOutputModel(java.util.Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                    response.getOutputStream().write("Hello World".getBytes());
                }
            };
        }
    }

    @Configuration
    protected static class WebJars implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/foo/");
        }
    }

    @Configuration
    protected static class AllResources implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/**").addResourceLocations("classpath:/foo/");
        }
    }

    @Configuration
    public static class Config {
        @Bean
        public ServletWebServerFactory webServerFactory() {
            return WebMvcAutoConfigurationTests.webServerFactory;
        }

        @Bean
        public WebServerFactoryCustomizerBeanPostProcessor ServletWebServerCustomizerBeanPostProcessor() {
            return new WebServerFactoryCustomizerBeanPostProcessor();
        }
    }

    @Configuration
    public static class CustomViewResolver {
        @Bean
        public ViewResolver viewResolver() {
            return new WebMvcAutoConfigurationTests.MyViewResolver();
        }
    }

    @Configuration
    public static class CustomContentNegotiatingViewResolver {
        @Bean
        public ContentNegotiatingViewResolver myViewResolver() {
            return new ContentNegotiatingViewResolver();
        }
    }

    private static class MyViewResolver implements ViewResolver {
        @Override
        public View resolveViewName(String viewName, Locale locale) {
            return null;
        }
    }

    @Configuration
    static class CustomConfigurableWebBindingInitializer {
        @Bean
        public ConfigurableWebBindingInitializer customConfigurableWebBindingInitializer() {
            return new WebMvcAutoConfigurationTests.CustomWebBindingInitializer();
        }
    }

    private static class CustomWebBindingInitializer extends ConfigurableWebBindingInitializer {}

    @Configuration
    static class CustomFormContentFilter {
        @Bean
        public FormContentFilter customFormContentFilter() {
            return new FormContentFilter();
        }
    }

    @Configuration
    static class CustomRequestMappingHandlerMapping {
        @Bean
        public WebMvcRegistrations webMvcRegistrationsHandlerMapping() {
            return new WebMvcRegistrations() {
                @Override
                public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                    return new WebMvcAutoConfigurationTests.MyRequestMappingHandlerMapping();
                }
            };
        }
    }

    private static class MyRequestMappingHandlerMapping extends RequestMappingHandlerMapping {}

    @Configuration
    static class CustomRequestMappingHandlerAdapter {
        @Bean
        public WebMvcRegistrations webMvcRegistrationsHandlerAdapter() {
            return new WebMvcRegistrations() {
                @Override
                public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
                    return new WebMvcAutoConfigurationTests.MyRequestMappingHandlerAdapter();
                }
            };
        }
    }

    private static class MyRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {}

    @Configuration
    @Import({ WebMvcAutoConfigurationTests.CustomRequestMappingHandlerMapping.class, WebMvcAutoConfigurationTests.CustomRequestMappingHandlerAdapter.class })
    static class MultipleWebMvcRegistrations {}

    @Configuration
    protected static class MvcValidator implements WebMvcConfigurer {
        private final Validator validator = Mockito.mock(Validator.class);

        @Override
        public Validator getValidator() {
            return this.validator;
        }
    }

    @Configuration
    protected static class MvcJsr303Validator implements WebMvcConfigurer {
        private final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        @Override
        public Validator getValidator() {
            return this.validator;
        }
    }

    @Configuration
    static class CustomJsr303Validator {
        @Bean
        public Validator customJsr303Validator() {
            return Mockito.mock(Validator.class);
        }
    }

    @Configuration
    static class CustomSpringValidator {
        @Bean
        public Validator customSpringValidator() {
            return Mockito.mock(Validator.class);
        }
    }

    @Configuration
    static class CustomHttpMessageConverter {
        @Bean
        public HttpMessageConverter<?> customHttpMessageConverter(ConversionService conversionService) {
            return Mockito.mock(HttpMessageConverter.class);
        }
    }

    @Configuration
    static class CustomConfigurer implements WebMvcConfigurer {
        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            configurer.favorPathExtension(true);
        }
    }

    @Configuration
    static class CustomApplicationTaskExecutorConfig {
        @Bean
        public Executor applicationTaskExecutor() {
            return Mockito.mock(Executor.class);
        }
    }

    @Configuration
    static class CustomAsyncTaskExecutorConfig {
        @Bean
        public AsyncTaskExecutor customTaskExecutor() {
            return Mockito.mock(AsyncTaskExecutor.class);
        }
    }

    @Configuration
    static class CustomAsyncTaskExecutorConfigurer implements WebMvcConfigurer {
        private final AsyncTaskExecutor taskExecutor = Mockito.mock(AsyncTaskExecutor.class);

        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            configurer.setTaskExecutor(this.taskExecutor);
        }
    }

    @Configuration
    static class RequestContextFilterConfiguration {
        @Bean
        public RequestContextFilter customRequestContextFilter() {
            return new RequestContextFilter();
        }
    }

    @Configuration
    static class RequestContextFilterRegistrationConfiguration {
        @Bean
        public FilterRegistrationBean<RequestContextFilter> customRequestContextFilterRegistration() {
            return new FilterRegistrationBean<RequestContextFilter>(new RequestContextFilter());
        }
    }
}

