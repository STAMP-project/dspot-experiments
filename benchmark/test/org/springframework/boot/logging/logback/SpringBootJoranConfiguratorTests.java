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
package org.springframework.boot.logging.logback;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.support.TestPropertySourceUtils;


/**
 * Tests for {@link SpringBootJoranConfigurator}.
 *
 * @author Phillip Webb
 * @author Edd? Mel?ndez
 * @author Stephane Nicoll
 */
public class SpringBootJoranConfiguratorTests {
    @Rule
    public OutputCapture out = new OutputCapture();

    private MockEnvironment environment;

    private LoggingInitializationContext initializationContext;

    private JoranConfigurator configurator;

    private LoggerContext context;

    private Logger logger;

    @Test
    public void profileActive() throws Exception {
        this.environment.setActiveProfiles("production");
        initialize("production-profile.xml");
        this.logger.trace("Hello");
        this.out.expect(Matchers.containsString("Hello"));
    }

    @Test
    public void multipleNamesFirstProfileActive() throws Exception {
        this.environment.setActiveProfiles("production");
        initialize("multi-profile-names.xml");
        this.logger.trace("Hello");
        this.out.expect(Matchers.containsString("Hello"));
    }

    @Test
    public void multipleNamesSecondProfileActive() throws Exception {
        this.environment.setActiveProfiles("test");
        initialize("multi-profile-names.xml");
        this.logger.trace("Hello");
        this.out.expect(Matchers.containsString("Hello"));
    }

    @Test
    public void profileNotActive() throws Exception {
        initialize("production-profile.xml");
        this.logger.trace("Hello");
        this.out.expect(Matchers.not(Matchers.containsString("Hello")));
    }

    @Test
    public void profileExpressionMatchFirst() throws Exception {
        this.environment.setActiveProfiles("production");
        initialize("profile-expression.xml");
        this.logger.trace("Hello");
        this.out.expect(Matchers.containsString("Hello"));
    }

    @Test
    public void profileExpressionMatchSecond() throws Exception {
        this.environment.setActiveProfiles("test");
        initialize("profile-expression.xml");
        this.logger.trace("Hello");
        this.out.expect(Matchers.containsString("Hello"));
    }

    @Test
    public void profileExpressionNoMatch() throws Exception {
        this.environment.setActiveProfiles("development");
        initialize("profile-expression.xml");
        this.logger.trace("Hello");
        this.out.expect(Matchers.not(Matchers.containsString("Hello")));
    }

    @Test
    public void profileNestedActiveActive() throws Exception {
        doTestNestedProfile(true, "outer", "inner");
    }

    @Test
    public void profileNestedActiveNotActive() throws Exception {
        doTestNestedProfile(false, "outer");
    }

    @Test
    public void profileNestedNotActiveActive() throws Exception {
        doTestNestedProfile(false, "inner");
    }

    @Test
    public void profileNestedNotActiveNotActive() throws Exception {
        doTestNestedProfile(false);
    }

    @Test
    public void springProperty() throws Exception {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.environment, "my.example-property=test");
        initialize("property.xml");
        assertThat(this.context.getProperty("MINE")).isEqualTo("test");
    }

    @Test
    public void relaxedSpringProperty() throws Exception {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.environment, "my.EXAMPLE_PROPERTY=test");
        ConfigurationPropertySources.attach(this.environment);
        initialize("property.xml");
        assertThat(this.context.getProperty("MINE")).isEqualTo("test");
    }

    @Test
    public void springPropertyNoValue() throws Exception {
        initialize("property.xml");
        assertThat(this.context.getProperty("SIMPLE")).isNull();
    }

    @Test
    public void relaxedSpringPropertyNoValue() throws Exception {
        initialize("property.xml");
        assertThat(this.context.getProperty("MINE")).isNull();
    }

    @Test
    public void springPropertyWithDefaultValue() throws Exception {
        initialize("property-default-value.xml");
        assertThat(this.context.getProperty("SIMPLE")).isEqualTo("foo");
    }

    @Test
    public void relaxedSpringPropertyWithDefaultValue() throws Exception {
        initialize("property-default-value.xml");
        assertThat(this.context.getProperty("MINE")).isEqualTo("bar");
    }
}

