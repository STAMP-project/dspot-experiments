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
package org.springframework.boot.docs.context.embedded;


import org.apache.catalina.Context;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.docs.context.embedded.TomcatLegacyCookieProcessorExample.LegacyCookieProcessorConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link TomcatLegacyCookieProcessorExample}.
 *
 * @author Andy Wilkinson
 */
public class TomcatLegacyCookieProcessorExampleTests {
    @Test
    public void cookieProcessorIsCustomized() {
        ServletWebServerApplicationContext applicationContext = ((ServletWebServerApplicationContext) (new SpringApplication(TomcatLegacyCookieProcessorExampleTests.TestConfiguration.class, LegacyCookieProcessorConfiguration.class).run()));
        Context context = ((Context) (getTomcat().getHost().findChildren()[0]));
        assertThat(context.getCookieProcessor()).isInstanceOf(LegacyCookieProcessor.class);
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public TomcatServletWebServerFactory tomcatFactory() {
            return new TomcatServletWebServerFactory(0);
        }

        @Bean
        public WebServerFactoryCustomizerBeanPostProcessor postProcessor() {
            return new WebServerFactoryCustomizerBeanPostProcessor();
        }
    }
}

