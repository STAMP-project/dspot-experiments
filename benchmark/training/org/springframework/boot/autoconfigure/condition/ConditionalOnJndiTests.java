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
package org.springframework.boot.autoconfigure.condition;


import org.junit.Test;
import org.springframework.boot.autoconfigure.jndi.TestableInitialContextFactory;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link ConditionalOnJndi}
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class ConditionalOnJndiTests {
    private ClassLoader threadContextClassLoader;

    private String initialContextFactory;

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    private ConditionalOnJndiTests.MockableOnJndi condition = new ConditionalOnJndiTests.MockableOnJndi();

    @Test
    public void jndiNotAvailable() {
        this.contextRunner.withUserConfiguration(ConditionalOnJndiTests.JndiAvailableConfiguration.class, ConditionalOnJndiTests.JndiConditionConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void jndiAvailable() {
        setupJndi();
        this.contextRunner.withUserConfiguration(ConditionalOnJndiTests.JndiAvailableConfiguration.class, ConditionalOnJndiTests.JndiConditionConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void jndiLocationNotBound() {
        setupJndi();
        this.contextRunner.withUserConfiguration(ConditionalOnJndiTests.JndiConditionConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void jndiLocationBound() {
        setupJndi();
        TestableInitialContextFactory.bind("java:/FooManager", new Object());
        this.contextRunner.withUserConfiguration(ConditionalOnJndiTests.JndiConditionConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void jndiLocationNotFound() {
        ConditionOutcome outcome = this.condition.getMatchOutcome(null, mockMetaData("java:/a"));
        assertThat(outcome.isMatch()).isFalse();
    }

    @Test
    public void jndiLocationFound() {
        this.condition.setFoundLocation("java:/b");
        ConditionOutcome outcome = this.condition.getMatchOutcome(null, mockMetaData("java:/a", "java:/b"));
        assertThat(outcome.isMatch()).isTrue();
    }

    @Configuration
    @ConditionalOnJndi
    static class JndiAvailableConfiguration {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnJndi("java:/FooManager")
    static class JndiConditionConfiguration {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    private static class MockableOnJndi extends OnJndiCondition {
        private boolean jndiAvailable = true;

        private String foundLocation;

        @Override
        protected boolean isJndiAvailable() {
            return this.jndiAvailable;
        }

        @Override
        protected JndiLocator getJndiLocator(String[] locations) {
            return new JndiLocator(locations) {
                @Override
                public String lookupFirstLocation() {
                    return ConditionalOnJndiTests.MockableOnJndi.this.foundLocation;
                }
            };
        }

        public void setFoundLocation(String foundLocation) {
            this.foundLocation = foundLocation;
        }
    }
}

