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
package org.springframework.boot.docs.autoconfigure;


import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link UserServiceAutoConfiguration}.
 *
 * @author Stephane Nicoll
 */
// end::test-user-config[]
public class UserServiceAutoConfigurationTests {
    // tag::runner[]
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(UserServiceAutoConfiguration.class));

    // end::runner[]
    // tag::test-env[]
    @Test
    public void serviceNameCanBeConfigured() {
        this.contextRunner.withPropertyValues("user.name=test123").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class).getName()).isEqualTo("test123");
        });
    }

    // end::test-env[]
    // tag::test-classloader[]
    @Test
    public void serviceIsIgnoredIfLibraryIsNotPresent() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(UserService.class)).run(( context) -> assertThat(context).doesNotHaveBean("userService"));
    }

    // end::test-classloader[]
    // tag::test-user-config[]
    @Test
    public void defaultServiceBacksOff() {
        this.contextRunner.withUserConfiguration(UserServiceAutoConfigurationTests.UserConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class)).isSameAs(context.getBean(.class).myUserService());
        });
    }

    @Configuration
    static class UserConfiguration {
        @Bean
        public UserService myUserService() {
            return new UserService("mine");
        }
    }
}

