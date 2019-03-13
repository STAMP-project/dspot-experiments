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
package org.springframework.boot.autoconfigure.sendgrid;


import com.sendgrid.SendGrid;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link SendGridAutoConfiguration}.
 *
 * @author Maciej Walkowiak
 * @author Patrick Bray
 */
public class SendGridAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void expectedSendGridBeanCreatedApiKey() {
        loadContext("spring.sendgrid.api-key:SG.SECRET-API-KEY");
        SendGrid sendGrid = this.context.getBean(SendGrid.class);
        assertThat(sendGrid).extracting("apiKey").containsExactly("SG.SECRET-API-KEY");
    }

    @Test
    public void autoConfigurationNotFiredWhenPropertiesNotSet() {
        loadContext();
        assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(() -> this.context.getBean(.class));
    }

    @Test
    public void autoConfigurationNotFiredWhenBeanAlreadyCreated() {
        loadContext(SendGridAutoConfigurationTests.ManualSendGridConfiguration.class, "spring.sendgrid.api-key:SG.SECRET-API-KEY");
        SendGrid sendGrid = this.context.getBean(SendGrid.class);
        assertThat(sendGrid).extracting("apiKey").containsExactly("SG.CUSTOM_API_KEY");
    }

    @Test
    public void expectedSendGridBeanWithProxyCreated() {
        loadContext("spring.sendgrid.api-key:SG.SECRET-API-KEY", "spring.sendgrid.proxy.host:localhost", "spring.sendgrid.proxy.port:5678");
        SendGrid sendGrid = this.context.getBean(SendGrid.class);
        assertThat(sendGrid).extracting("client").extracting("httpClient").extracting("routePlanner").hasOnlyElementsOfType(DefaultProxyRoutePlanner.class);
    }

    @Configuration
    static class ManualSendGridConfiguration {
        @Bean
        SendGrid sendGrid() {
            return new SendGrid("SG.CUSTOM_API_KEY", true);
        }
    }
}

