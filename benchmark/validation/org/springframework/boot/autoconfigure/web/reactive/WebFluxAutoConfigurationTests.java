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
package org.springframework.boot.autoconfigure.web.reactive;


import java.util.Date;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidatorAdapter;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.CacheControl;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.resource.ResourceWebHandler;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.reactive.result.view.ViewResolutionResultHandler;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.util.pattern.PathPattern;


/**
 * Tests for {@link WebFluxAutoConfiguration}.
 *
 * @author Brian Clozel
 * @author Andy Wilkinson
 * @author Artsiom Yudovin
 */
public class WebFluxAutoConfigurationTests {
    private static final MockReactiveWebServerFactory mockReactiveWebServerFactory = new MockReactiveWebServerFactory();

    private ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner().withConfiguration(AutoConfigurations.of(WebFluxAutoConfiguration.class)).withUserConfiguration(WebFluxAutoConfigurationTests.Config.class);

    @Test
    public void shouldNotProcessIfExistingWebReactiveConfiguration() {
        this.contextRunner.withUserConfiguration(WebFluxConfigurationSupport.class).run(( context) -> {
            assertThat(context).getBeans(.class).hasSize(1);
            assertThat(context).getBeans(.class).hasSize(1);
        });
    }

    @Test
    public void shouldCreateDefaultBeans() {
        this.contextRunner.run(( context) -> {
            assertThat(context).getBeans(.class).hasSize(1);
            assertThat(context).getBeans(.class).hasSize(1);
            assertThat(context).getBeans(.class).hasSize(1);
            assertThat(context.getBean("resourceHandlerMapping", .class)).isNotNull();
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRegisterCustomHandlerMethodArgumentResolver() {
        this.contextRunner.withUserConfiguration(WebFluxAutoConfigurationTests.CustomArgumentResolvers.class).run(( context) -> {
            RequestMappingHandlerAdapter adapter = context.getBean(.class);
            List<HandlerMethodArgumentResolver> customResolvers = ((List<HandlerMethodArgumentResolver>) (ReflectionTestUtils.getField(adapter.getArgumentResolverConfigurer(), "customResolvers")));
            assertThat(customResolvers).contains(context.getBean("firstResolver", .class), context.getBean("secondResolver", .class));
        });
    }

    @Test
    public void shouldCustomizeCodecs() {
        this.contextRunner.withUserConfiguration(WebFluxAutoConfigurationTests.CustomCodecCustomizers.class).run(( context) -> {
            CodecCustomizer codecCustomizer = context.getBean("firstCodecCustomizer", .class);
            assertThat(codecCustomizer).isNotNull();
            verify(codecCustomizer).customize(any(.class));
        });
    }

    @Test
    public void shouldRegisterResourceHandlerMapping() {
        this.contextRunner.run(( context) -> {
            SimpleUrlHandlerMapping hm = context.getBean("resourceHandlerMapping", .class);
            assertThat(hm.getUrlMap().get("/**")).isInstanceOf(.class);
            ResourceWebHandler staticHandler = ((ResourceWebHandler) (hm.getUrlMap().get("/**")));
            assertThat(staticHandler.getLocations()).hasSize(4);
            assertThat(hm.getUrlMap().get("/webjars/**")).isInstanceOf(.class);
            ResourceWebHandler webjarsHandler = ((ResourceWebHandler) (hm.getUrlMap().get("/webjars/**")));
            assertThat(webjarsHandler.getLocations()).hasSize(1);
            assertThat(webjarsHandler.getLocations().get(0)).isEqualTo(new ClassPathResource("/META-INF/resources/webjars/"));
        });
    }

    @Test
    public void shouldMapResourcesToCustomPath() {
        this.contextRunner.withPropertyValues("spring.webflux.static-path-pattern:/static/**").run(( context) -> {
            SimpleUrlHandlerMapping hm = context.getBean("resourceHandlerMapping", .class);
            assertThat(hm.getUrlMap().get("/static/**")).isInstanceOf(.class);
            ResourceWebHandler staticHandler = ((ResourceWebHandler) (hm.getUrlMap().get("/static/**")));
            assertThat(staticHandler.getLocations()).hasSize(4);
        });
    }

    @Test
    public void shouldNotMapResourcesWhenDisabled() {
        this.contextRunner.withPropertyValues("spring.resources.add-mappings:false").run(( context) -> assertThat(context.getBean("resourceHandlerMapping")).isNotInstanceOf(.class));
    }

    @Test
    public void resourceHandlerChainEnabled() {
        this.contextRunner.withPropertyValues("spring.resources.chain.enabled:true").run(( context) -> {
            SimpleUrlHandlerMapping hm = context.getBean("resourceHandlerMapping", .class);
            assertThat(hm.getUrlMap().get("/**")).isInstanceOf(.class);
            ResourceWebHandler staticHandler = ((ResourceWebHandler) (hm.getUrlMap().get("/**")));
            assertThat(staticHandler.getResourceResolvers()).extractingResultOf("getClass").containsOnly(.class, .class);
            assertThat(staticHandler.getResourceTransformers()).extractingResultOf("getClass").containsOnly(.class);
        });
    }

    @Test
    public void shouldRegisterViewResolvers() {
        this.contextRunner.withUserConfiguration(WebFluxAutoConfigurationTests.ViewResolvers.class).run(( context) -> {
            ViewResolutionResultHandler resultHandler = context.getBean(.class);
            assertThat(resultHandler.getViewResolvers()).containsExactly(context.getBean("aViewResolver", .class), context.getBean("anotherViewResolver", .class));
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
        this.contextRunner.withPropertyValues("spring.webflux.date-format:dd*MM*yyyy").run(( context) -> {
            FormattingConversionService conversionService = context.getBean(.class);
            Date date = new DateTime(1988, 6, 25, 20, 30).toDate();
            assertThat(conversionService.convert(date, .class)).isEqualTo("25*06*1988");
        });
    }

    @Test
    public void validatorWhenNoValidatorShouldUseDefault() {
        this.contextRunner.run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).getBeanNames(.class).containsExactly("webFluxValidator");
        });
    }

    @Test
    public void validatorWhenNoCustomizationShouldUseAutoConfigured() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class)).run(( context) -> {
            assertThat(context).getBeanNames(.class).containsExactly("defaultValidator");
            assertThat(context).getBeanNames(.class).containsExactlyInAnyOrder("defaultValidator", "webFluxValidator");
            Validator validator = context.getBean("webFluxValidator", .class);
            assertThat(validator).isInstanceOf(.class);
            Object defaultValidator = context.getBean("defaultValidator");
            assertThat(((ValidatorAdapter) (validator)).getTarget()).isSameAs(defaultValidator);
            // Primary Spring validator is the one used by WebFlux behind the
            // scenes
            assertThat(context.getBean(.class)).isEqualTo(defaultValidator);
        });
    }

    @Test
    public void validatorWithConfigurerShouldUseSpringValidator() {
        this.contextRunner.withUserConfiguration(WebFluxAutoConfigurationTests.ValidatorWebFluxConfigurer.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).getBeanNames(.class).containsOnly("webFluxValidator");
            assertThat(context.getBean("webFluxValidator")).isSameAs(context.getBean(.class).validator);
        });
    }

    @Test
    public void validatorWithConfigurerDoesNotExposeJsr303() {
        this.contextRunner.withUserConfiguration(WebFluxAutoConfigurationTests.ValidatorJsr303WebFluxConfigurer.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).getBeanNames(.class).containsOnly("webFluxValidator");
            Validator validator = context.getBean("webFluxValidator", .class);
            assertThat(validator).isInstanceOf(.class);
            assertThat(((ValidatorAdapter) (validator)).getTarget()).isSameAs(context.getBean(.class).validator);
        });
    }

    @Test
    public void validationCustomConfigurerTakesPrecedence() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class)).withUserConfiguration(WebFluxAutoConfigurationTests.ValidatorWebFluxConfigurer.class).run(( context) -> {
            assertThat(context).getBeans(.class).hasSize(1);
            assertThat(context).getBeans(.class).hasSize(1);
            assertThat(context).getBeanNames(.class).containsExactlyInAnyOrder("defaultValidator", "webFluxValidator");
            assertThat(context.getBean("webFluxValidator")).isSameAs(context.getBean(.class).validator);
            // Primary Spring validator is the auto-configured one as the WebFlux
            // one has been
            // customized via a WebFluxConfigurer
            assertThat(context.getBean(.class)).isEqualTo(context.getBean("defaultValidator"));
        });
    }

    @Test
    public void validatorWithCustomSpringValidatorIgnored() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class)).withUserConfiguration(WebFluxAutoConfigurationTests.CustomSpringValidator.class).run(( context) -> {
            assertThat(context).getBeanNames(.class).containsExactly("defaultValidator");
            assertThat(context).getBeanNames(.class).containsExactlyInAnyOrder("customValidator", "defaultValidator", "webFluxValidator");
            Validator validator = context.getBean("webFluxValidator", .class);
            assertThat(validator).isInstanceOf(.class);
            Object defaultValidator = context.getBean("defaultValidator");
            assertThat(((ValidatorAdapter) (validator)).getTarget()).isSameAs(defaultValidator);
            // Primary Spring validator is the one used by WebFlux behind the
            // scenes
            assertThat(context.getBean(.class)).isEqualTo(defaultValidator);
        });
    }

    @Test
    public void validatorWithCustomJsr303ValidatorExposedAsSpringValidator() {
        this.contextRunner.withUserConfiguration(WebFluxAutoConfigurationTests.CustomJsr303Validator.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).getBeanNames(.class).containsExactly("customValidator");
            assertThat(context).getBeanNames(.class).containsExactly("webFluxValidator");
            Validator validator = context.getBean(.class);
            assertThat(validator).isInstanceOf(.class);
            Validator target = ((ValidatorAdapter) (validator)).getTarget();
            assertThat(target).hasFieldOrPropertyWithValue("targetValidator", context.getBean("customValidator"));
        });
    }

    @Test
    public void hiddenHttpMethodFilterIsAutoConfigured() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void hiddenHttpMethodFilterCanBeOverridden() {
        this.contextRunner.withUserConfiguration(WebFluxAutoConfigurationTests.CustomHiddenHttpMethodFilter.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void hiddenHttpMethodFilterCanBeDisabled() {
        this.contextRunner.withPropertyValues("spring.webflux.hiddenmethod.filter.enabled=false").run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void customRequestMappingHandlerMapping() {
        this.contextRunner.withUserConfiguration(WebFluxAutoConfigurationTests.CustomRequestMappingHandlerMapping.class).run(( context) -> assertThat(context).getBean(.class).isInstanceOf(.class));
    }

    @Test
    public void customRequestMappingHandlerAdapter() {
        this.contextRunner.withUserConfiguration(WebFluxAutoConfigurationTests.CustomRequestMappingHandlerAdapter.class).run(( context) -> assertThat(context).getBean(.class).isInstanceOf(.class));
    }

    @Test
    public void multipleWebFluxRegistrations() {
        this.contextRunner.withUserConfiguration(WebFluxAutoConfigurationTests.MultipleWebFluxRegistrations.class).run(( context) -> {
            assertThat(context.getBean(.class)).isNotInstanceOf(.class);
            assertThat(context.getBean(.class)).isNotInstanceOf(.class);
        });
    }

    @Test
    public void cachePeriod() {
        this.contextRunner.withPropertyValues("spring.resources.cache.period:5").run(( context) -> {
            Map<PathPattern, Object> handlerMap = getHandlerMap(context);
            assertThat(handlerMap).hasSize(2);
            for (Object handler : handlerMap.values()) {
                if (handler instanceof ResourceWebHandler) {
                    Assertions.setExtractBareNamePropertyMethods(false);
                    assertThat(((ResourceWebHandler) (handler)).getCacheControl()).isEqualToComparingFieldByField(CacheControl.maxAge(5, TimeUnit.SECONDS));
                }
            }
        });
    }

    @Test
    public void cacheControl() {
        this.contextRunner.withPropertyValues("spring.resources.cache.cachecontrol.max-age:5", "spring.resources.cache.cachecontrol.proxy-revalidate:true").run(( context) -> {
            Map<PathPattern, Object> handlerMap = getHandlerMap(context);
            assertThat(handlerMap).hasSize(2);
            for (Object handler : handlerMap.values()) {
                if (handler instanceof ResourceWebHandler) {
                    Assertions.setExtractBareNamePropertyMethods(false);
                    assertThat(((ResourceWebHandler) (handler)).getCacheControl()).isEqualToComparingFieldByField(CacheControl.maxAge(5, TimeUnit.SECONDS).proxyRevalidate());
                }
            }
        });
    }

    @Configuration
    protected static class CustomArgumentResolvers {
        @Bean
        public HandlerMethodArgumentResolver firstResolver() {
            return Mockito.mock(HandlerMethodArgumentResolver.class);
        }

        @Bean
        public HandlerMethodArgumentResolver secondResolver() {
            return Mockito.mock(HandlerMethodArgumentResolver.class);
        }
    }

    @Configuration
    protected static class CustomCodecCustomizers {
        @Bean
        public CodecCustomizer firstCodecCustomizer() {
            return Mockito.mock(CodecCustomizer.class);
        }
    }

    @Configuration
    protected static class ViewResolvers {
        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public ViewResolver aViewResolver() {
            return Mockito.mock(ViewResolver.class);
        }

        @Bean
        public ViewResolver anotherViewResolver() {
            return Mockito.mock(ViewResolver.class);
        }
    }

    @Configuration
    protected static class Config {
        @Bean
        public MockReactiveWebServerFactory mockReactiveWebServerFactory() {
            return WebFluxAutoConfigurationTests.mockReactiveWebServerFactory;
        }
    }

    @Configuration
    protected static class CustomHttpHandler {
        @Bean
        public HttpHandler httpHandler() {
            return ( serverHttpRequest, serverHttpResponse) -> null;
        }
    }

    @Configuration
    protected static class ValidatorWebFluxConfigurer implements WebFluxConfigurer {
        private final Validator validator = Mockito.mock(Validator.class);

        @Override
        public Validator getValidator() {
            return this.validator;
        }
    }

    @Configuration
    protected static class ValidatorJsr303WebFluxConfigurer implements WebFluxConfigurer {
        private final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        @Override
        public Validator getValidator() {
            return this.validator;
        }
    }

    @Configuration
    static class CustomJsr303Validator {
        @Bean
        public Validator customValidator() {
            return Mockito.mock(Validator.class);
        }
    }

    @Configuration
    static class CustomSpringValidator {
        @Bean
        public Validator customValidator() {
            return Mockito.mock(Validator.class);
        }
    }

    @Configuration
    static class CustomHiddenHttpMethodFilter {
        @Bean
        public HiddenHttpMethodFilter customHiddenHttpMethodFilter() {
            return Mockito.mock(HiddenHttpMethodFilter.class);
        }
    }

    @Configuration
    static class CustomRequestMappingHandlerAdapter {
        @Bean
        public WebFluxRegistrations webFluxRegistrationsHandlerAdapter() {
            return new WebFluxRegistrations() {
                @Override
                public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
                    return new WebFluxAutoConfigurationTests.MyRequestMappingHandlerAdapter();
                }
            };
        }
    }

    private static class MyRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {}

    @Configuration
    @Import({ WebFluxAutoConfigurationTests.CustomRequestMappingHandlerMapping.class, WebFluxAutoConfigurationTests.CustomRequestMappingHandlerAdapter.class })
    static class MultipleWebFluxRegistrations {}

    @Configuration
    static class CustomRequestMappingHandlerMapping {
        @Bean
        public WebFluxRegistrations webFluxRegistrationsHandlerMapping() {
            return new WebFluxRegistrations() {
                @Override
                public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                    return new WebFluxAutoConfigurationTests.MyRequestMappingHandlerMapping();
                }
            };
        }
    }

    private static class MyRequestMappingHandlerMapping extends RequestMappingHandlerMapping {}
}

