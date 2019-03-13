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
package org.springframework.boot.env;


import org.junit.Test;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.origin.PropertySourceOrigin;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.support.TestPropertySourceUtils;


/**
 * Tests for {@link SpringApplicationJsonEnvironmentPostProcessor}.
 *
 * @author Dave Syer
 * @author Madhura Bhave
 * @author Phillip Webb
 * @author Artsiom Yudovin
 */
public class SpringApplicationJsonEnvironmentPostProcessorTests {
    private SpringApplicationJsonEnvironmentPostProcessor processor = new SpringApplicationJsonEnvironmentPostProcessor();

    private ConfigurableEnvironment environment = new StandardEnvironment();

    @Test
    public void error() {
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEmpty();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.environment, "spring.application.json=foo:bar");
        assertThatExceptionOfType(JsonParseException.class).isThrownBy(() -> this.processor.postProcessEnvironment(this.environment, null)).withMessageContaining("Cannot parse JSON");
    }

    @Test
    public void missing() {
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEmpty();
        this.processor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEmpty();
    }

    @Test
    public void empty() {
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEmpty();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.environment, "spring.application.json={}");
        this.processor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEmpty();
    }

    @Test
    public void periodSeparated() {
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEmpty();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.environment, "spring.application.json={\"foo\":\"bar\"}");
        this.processor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEqualTo("bar");
    }

    @Test
    public void envVar() {
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEmpty();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.environment, "SPRING_APPLICATION_JSON={\"foo\":\"bar\"}");
        this.processor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEqualTo("bar");
    }

    @Test
    public void nested() {
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEmpty();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.environment, "SPRING_APPLICATION_JSON={\"foo\":{\"bar\":\"spam\",\"rab\":\"maps\"}}");
        this.processor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.resolvePlaceholders("${foo.bar:}")).isEqualTo("spam");
        assertThat(this.environment.resolvePlaceholders("${foo.rab:}")).isEqualTo("maps");
    }

    @Test
    public void prefixed() {
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEmpty();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.environment, "SPRING_APPLICATION_JSON={\"foo.bar\":\"spam\"}");
        this.processor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.resolvePlaceholders("${foo.bar:}")).isEqualTo("spam");
    }

    @Test
    public void list() {
        assertThat(this.environment.resolvePlaceholders("${foo[1]:}")).isEmpty();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.environment, "SPRING_APPLICATION_JSON={\"foo\":[\"bar\",\"spam\"]}");
        this.processor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.resolvePlaceholders("${foo[1]:}")).isEqualTo("spam");
    }

    @Test
    public void listOfObject() {
        assertThat(this.environment.resolvePlaceholders("${foo[0].bar:}")).isEmpty();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.environment, "SPRING_APPLICATION_JSON={\"foo\":[{\"bar\":\"spam\"}]}");
        this.processor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.resolvePlaceholders("${foo[0].bar:}")).isEqualTo("spam");
    }

    @Test
    public void propertySourceShouldTrackOrigin() {
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEmpty();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.environment, "spring.application.json={\"foo\":\"bar\"}");
        this.processor.postProcessEnvironment(this.environment, null);
        PropertySource<?> propertySource = this.environment.getPropertySources().get("spring.application.json");
        PropertySourceOrigin origin = ((PropertySourceOrigin) (PropertySourceOrigin.get(propertySource, "foo")));
        assertThat(origin.getPropertySource().getName()).isEqualTo("Inlined Test Properties");
        assertThat(origin.getPropertyName()).isEqualTo("spring.application.json");
        assertThat(this.environment.resolvePlaceholders("${foo:}")).isEqualTo("bar");
    }
}

