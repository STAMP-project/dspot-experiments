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
package org.springframework.boot.test.mock.mockito;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.example.ExampleService;
import org.springframework.boot.test.mock.mockito.example.ExampleServiceCaller;
import org.springframework.boot.test.mock.mockito.example.SimpleExampleService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Test {@link SpyBean} on a field on a {@code @Configuration} class can be used to
 * replace existing beans.
 *
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
public class SpyBeanOnConfigurationFieldForExistingBeanIntegrationTests {
    @Autowired
    private SpyBeanOnConfigurationFieldForExistingBeanIntegrationTests.Config config;

    @Autowired
    private ExampleServiceCaller caller;

    @Test
    public void testSpying() {
        assertThat(this.caller.sayGreeting()).isEqualTo("I say simple");
        Mockito.verify(this.config.exampleService).greeting();
    }

    @Configuration
    @Import({ ExampleServiceCaller.class, SimpleExampleService.class })
    static class Config {
        @SpyBean
        private ExampleService exampleService;
    }
}

