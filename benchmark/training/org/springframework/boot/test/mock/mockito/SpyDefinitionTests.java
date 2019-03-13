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


import MockReset.AFTER;
import MockReset.BEFORE;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.mock.MockCreationSettings;
import org.springframework.boot.test.mock.mockito.example.ExampleService;
import org.springframework.boot.test.mock.mockito.example.ExampleServiceCaller;
import org.springframework.boot.test.mock.mockito.example.RealExampleService;
import org.springframework.core.ResolvableType;

import static MockReset.BEFORE;


/**
 * Tests for {@link SpyDefinition}.
 *
 * @author Phillip Webb
 */
public class SpyDefinitionTests {
    private static final ResolvableType REAL_SERVICE_TYPE = ResolvableType.forClass(RealExampleService.class);

    @Test
    public void classToSpyMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new SpyDefinition(null, null, null, true, null)).withMessageContaining("TypeToSpy must not be null");
    }

    @Test
    public void createWithDefaults() {
        SpyDefinition definition = new SpyDefinition(null, SpyDefinitionTests.REAL_SERVICE_TYPE, null, true, null);
        assertThat(definition.getName()).isNull();
        assertThat(definition.getTypeToSpy()).isEqualTo(SpyDefinitionTests.REAL_SERVICE_TYPE);
        assertThat(definition.getReset()).isEqualTo(AFTER);
        assertThat(definition.isProxyTargetAware()).isTrue();
        assertThat(definition.getQualifier()).isNull();
    }

    @Test
    public void createExplicit() {
        QualifierDefinition qualifier = Mockito.mock(QualifierDefinition.class);
        SpyDefinition definition = new SpyDefinition("name", SpyDefinitionTests.REAL_SERVICE_TYPE, BEFORE, false, qualifier);
        assertThat(definition.getName()).isEqualTo("name");
        assertThat(definition.getTypeToSpy()).isEqualTo(SpyDefinitionTests.REAL_SERVICE_TYPE);
        assertThat(definition.getReset()).isEqualTo(BEFORE);
        assertThat(definition.isProxyTargetAware()).isFalse();
        assertThat(definition.getQualifier()).isEqualTo(qualifier);
    }

    @Test
    public void createSpy() {
        SpyDefinition definition = new SpyDefinition("name", SpyDefinitionTests.REAL_SERVICE_TYPE, BEFORE, true, null);
        RealExampleService spy = definition.createSpy(new RealExampleService("hello"));
        MockCreationSettings<?> settings = Mockito.mockingDetails(spy).getMockCreationSettings();
        assertThat(spy).isInstanceOf(ExampleService.class);
        assertThat(settings.getMockName().toString()).isEqualTo("name");
        assertThat(settings.getDefaultAnswer()).isEqualTo(Answers.CALLS_REAL_METHODS);
        assertThat(MockReset.get(spy)).isEqualTo(BEFORE);
    }

    @Test
    public void createSpyWhenNullInstanceShouldThrowException() {
        SpyDefinition definition = new SpyDefinition("name", SpyDefinitionTests.REAL_SERVICE_TYPE, BEFORE, true, null);
        assertThatIllegalArgumentException().isThrownBy(() -> definition.createSpy(null)).withMessageContaining("Instance must not be null");
    }

    @Test
    public void createSpyWhenWrongInstanceShouldThrowException() {
        SpyDefinition definition = new SpyDefinition("name", SpyDefinitionTests.REAL_SERVICE_TYPE, BEFORE, true, null);
        assertThatIllegalArgumentException().isThrownBy(() -> definition.createSpy(new ExampleServiceCaller(null))).withMessageContaining("must be an instance of");
    }

    @Test
    public void createSpyTwice() {
        SpyDefinition definition = new SpyDefinition("name", SpyDefinitionTests.REAL_SERVICE_TYPE, BEFORE, true, null);
        Object instance = new RealExampleService("hello");
        instance = definition.createSpy(instance);
        definition.createSpy(instance);
    }
}

