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
package org.springframework.boot.test.autoconfigure.override;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.ExampleTestConfig;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Integration tests for {@link OverrideAutoConfiguration} when {@code enabled} is
 * {@code false}.
 *
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@OverrideAutoConfiguration(enabled = false)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@ImportAutoConfiguration(ExampleTestConfig.class)
public class OverrideAutoConfigurationEnabledFalseIntegrationTests {
    @Autowired
    private ApplicationContext context;

    @Test
    public void disabledAutoConfiguration() {
        ApplicationContext context = this.context;
        assertThat(context.getBean(ExampleTestConfig.class)).isNotNull();
        assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(() -> context.getBean(.class));
    }
}

