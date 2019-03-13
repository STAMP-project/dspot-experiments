/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.context.properties.source;


import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.env.MockPropertySource;


/**
 * Tests for {@link UnboundElementsSourceFilter}.
 *
 * @author Madhura Bhave
 */
public class UnboundElementsSourceFilterTests {
    private UnboundElementsSourceFilter filter;

    private ConfigurationPropertySource source;

    @Test
    public void filterWhenSourceIsSystemPropertiesPropertySourceShouldReturnFalse() {
        MockPropertySource propertySource = new MockPropertySource(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
        BDDMockito.given(this.source.getUnderlyingSource()).willReturn(propertySource);
        assertThat(this.filter.apply(this.source)).isFalse();
    }

    @Test
    public void filterWhenSourceIsSystemEnvironmentPropertySourceShouldReturnFalse() {
        MockPropertySource propertySource = new MockPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
        BDDMockito.given(this.source.getUnderlyingSource()).willReturn(propertySource);
        assertThat(this.filter.apply(this.source)).isFalse();
    }

    @Test
    public void filterWhenSourceIsNotSystemShouldReturnTrue() {
        MockPropertySource propertySource = new MockPropertySource("test");
        BDDMockito.given(this.source.getUnderlyingSource()).willReturn(propertySource);
        assertThat(this.filter.apply(this.source)).isTrue();
    }
}

