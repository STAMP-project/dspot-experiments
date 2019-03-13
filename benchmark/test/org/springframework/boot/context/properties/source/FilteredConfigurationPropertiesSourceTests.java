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
package org.springframework.boot.context.properties.source;


import ConfigurationPropertyState.ABSENT;
import ConfigurationPropertyState.PRESENT;
import ConfigurationPropertyState.UNKNOWN;
import java.util.Objects;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Test for {@link FilteredIterableConfigurationPropertiesSource}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class FilteredConfigurationPropertiesSourceTests {
    @Test
    public void createWhenSourceIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new FilteredConfigurationPropertiesSource(null, Objects::nonNull)).withMessageContaining("Source must not be null");
    }

    @Test
    public void createWhenFilterIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new FilteredConfigurationPropertiesSource(new MockConfigurationPropertySource(), null)).withMessageContaining("Filter must not be null");
    }

    @Test
    public void getValueShouldFilterNames() {
        ConfigurationPropertySource source = createTestSource();
        ConfigurationPropertySource filtered = source.filter(this::noBrackets);
        ConfigurationPropertyName name = ConfigurationPropertyName.of("a");
        assertThat(source.getConfigurationProperty(name).getValue()).isEqualTo("1");
        assertThat(filtered.getConfigurationProperty(name).getValue()).isEqualTo("1");
        ConfigurationPropertyName bracketName = ConfigurationPropertyName.of("a[1]");
        assertThat(source.getConfigurationProperty(bracketName).getValue()).isEqualTo("2");
        assertThat(filtered.getConfigurationProperty(bracketName)).isNull();
    }

    @Test
    public void containsDescendantOfWhenSourceReturnsEmptyShouldReturnEmpty() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        ConfigurationPropertySource source = Mockito.mock(ConfigurationPropertySource.class, Answers.CALLS_REAL_METHODS);
        BDDMockito.given(source.containsDescendantOf(name)).willReturn(UNKNOWN);
        ConfigurationPropertySource filtered = source.filter(( n) -> true);
        assertThat(filtered.containsDescendantOf(name)).isEqualTo(UNKNOWN);
    }

    @Test
    public void containsDescendantOfWhenSourceReturnsFalseShouldReturnFalse() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        ConfigurationPropertySource source = Mockito.mock(ConfigurationPropertySource.class, Answers.CALLS_REAL_METHODS);
        BDDMockito.given(source.containsDescendantOf(name)).willReturn(ABSENT);
        ConfigurationPropertySource filtered = source.filter(( n) -> true);
        assertThat(filtered.containsDescendantOf(name)).isEqualTo(ABSENT);
    }

    @Test
    public void containsDescendantOfWhenSourceReturnsTrueShouldReturnEmpty() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        ConfigurationPropertySource source = Mockito.mock(ConfigurationPropertySource.class, Answers.CALLS_REAL_METHODS);
        BDDMockito.given(source.containsDescendantOf(name)).willReturn(PRESENT);
        ConfigurationPropertySource filtered = source.filter(( n) -> true);
        assertThat(filtered.containsDescendantOf(name)).isEqualTo(UNKNOWN);
    }
}

