/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.web.servlet.support;


import HttpStatus.CREATED;
import HttpStatus.OK;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.xnio.channels.UnsupportedOptionException;


/**
 * Integration tests for {@link ErrorPageFilter}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@DirtiesContext
@ContextConfiguration(classes = ErrorPageFilterIntegrationTests.TomcatConfig.class, loader = ErrorPageFilterIntegrationTests.EmbeddedWebContextLoader.class)
public class ErrorPageFilterIntegrationTests {
    @Autowired
    private ErrorPageFilterIntegrationTests.HelloWorldController controller;

    @Autowired
    private AnnotationConfigServletWebServerApplicationContext context;

    @Test
    public void created() throws Exception {
        doTest(this.context, "/create", CREATED);
        assertThat(this.controller.getStatus()).isEqualTo(201);
    }

    @Test
    public void ok() throws Exception {
        doTest(this.context, "/hello", OK);
        assertThat(this.controller.getStatus()).isEqualTo(200);
    }

    @Configuration
    @EnableWebMvc
    public static class TomcatConfig {
        @Bean
        public ServletWebServerFactory webServerFactory() {
            return new TomcatServletWebServerFactory(0);
        }

        @Bean
        public ErrorPageFilter errorPageFilter() {
            return new ErrorPageFilter();
        }

        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        @Bean
        public ErrorPageFilterIntegrationTests.HelloWorldController helloWorldController() {
            return new ErrorPageFilterIntegrationTests.HelloWorldController();
        }
    }

    @Controller
    public static class HelloWorldController implements WebMvcConfigurer {
        private int status;

        private CountDownLatch latch = new CountDownLatch(1);

        public int getStatus() throws InterruptedException {
            assertThat(this.latch.await(1, TimeUnit.SECONDS)).as("Timed out waiting for latch").isTrue();
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void reset() {
            this.status = 0;
            this.latch = new CountDownLatch(1);
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new HandlerInterceptorAdapter() {
                @Override
                public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
                    ErrorPageFilterIntegrationTests.HelloWorldController.this.setStatus(response.getStatus());
                    ErrorPageFilterIntegrationTests.HelloWorldController.this.latch.countDown();
                }
            });
        }

        @RequestMapping("/hello")
        @ResponseBody
        public String sayHello() {
            return "Hello World";
        }

        @RequestMapping("/create")
        @ResponseBody
        @ResponseStatus(HttpStatus.CREATED)
        public String created() {
            return "Hello World";
        }
    }

    static class EmbeddedWebContextLoader extends AbstractContextLoader {
        private static final String[] EMPTY_RESOURCE_SUFFIXES = new String[]{  };

        @Override
        public ApplicationContext loadContext(MergedContextConfiguration config) {
            AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(config.getClasses());
            context.registerShutdownHook();
            return context;
        }

        @Override
        public ApplicationContext loadContext(String... locations) {
            throw new UnsupportedOptionException();
        }

        @Override
        protected String[] getResourceSuffixes() {
            return ErrorPageFilterIntegrationTests.EmbeddedWebContextLoader.EMPTY_RESOURCE_SUFFIXES;
        }

        @Override
        protected String getResourceSuffix() {
            throw new UnsupportedOptionException();
        }
    }
}

