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
package org.springframework.boot.test.autoconfigure.web.reactive.webclient;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.AutoConfigurationImportedCondition;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * Tests for the auto-configuration imported by {@link WebFluxTest}.
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringRunner.class)
@WebFluxTest
public class WebFluxTestAutoConfigurationIntegrationTests {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void messageSourceAutoConfigurationIsImported() {
        assertThat(this.applicationContext).has(AutoConfigurationImportedCondition.importedAutoConfiguration(MessageSourceAutoConfiguration.class));
    }

    @Test
    public void validationAutoConfigurationIsImported() {
        assertThat(this.applicationContext).has(AutoConfigurationImportedCondition.importedAutoConfiguration(ValidationAutoConfiguration.class));
    }

    @Test
    public void whatever() {
        WebTestClient client = this.applicationContext.getBean(WebTestClient.class);
        System.out.println(client);
    }
}

