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
import java.util.Collections;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests for {@link AliasedConfigurationPropertySource}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class AliasedConfigurationPropertySourceTests {
    @Test
    public void getConfigurationPropertyShouldConsiderAliases() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.bar", "bing");
        source.put("foo.baz", "biff");
        ConfigurationPropertySource aliased = source.nonIterable().withAliases(new ConfigurationPropertyNameAliases("foo.bar", "foo.bar1"));
        assertThat(getValue(aliased, "foo.bar")).isEqualTo("bing");
        assertThat(getValue(aliased, "foo.bar1")).isEqualTo("bing");
    }

    @Test
    public void getConfigurationPropertyWhenNotAliasesShouldReturnValue() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.bar", "bing");
        source.put("foo.baz", "biff");
        ConfigurationPropertySource aliased = source.nonIterable().withAliases(new ConfigurationPropertyNameAliases("foo.bar", "foo.bar1"));
        assertThat(getValue(aliased, "foo.baz")).isEqualTo("biff");
    }

    @Test
    public void containsDescendantOfWhenSourceReturnsUnknownShouldReturnUnknown() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        ConfigurationPropertySource source = Mockito.mock(ConfigurationPropertySource.class, Answers.CALLS_REAL_METHODS);
        BDDMockito.given(source.containsDescendantOf(name)).willReturn(UNKNOWN);
        ConfigurationPropertySource aliased = source.withAliases(new ConfigurationPropertyNameAliases("foo.bar", "foo.bar1"));
        assertThat(aliased.containsDescendantOf(name)).isEqualTo(UNKNOWN);
    }

    @Test
    public void containsDescendantOfWhenSourceReturnsPresentShouldReturnPresent() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        ConfigurationPropertySource source = Mockito.mock(ConfigurationPropertySource.class, Answers.CALLS_REAL_METHODS);
        BDDMockito.given(source.containsDescendantOf(name)).willReturn(PRESENT);
        BDDMockito.given(source.containsDescendantOf(ConfigurationPropertyName.of("bar"))).willReturn(UNKNOWN);
        ConfigurationPropertySource aliased = source.withAliases(new ConfigurationPropertyNameAliases("foo.bar", "foo.bar1"));
        assertThat(aliased.containsDescendantOf(name)).isEqualTo(PRESENT);
    }

    @Test
    public void containsDescendantOfWhenAllAreAbsentShouldReturnAbsent() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        ConfigurationPropertySource source = Mockito.mock(ConfigurationPropertySource.class, Answers.CALLS_REAL_METHODS);
        BDDMockito.given(source.containsDescendantOf(name)).willReturn(ABSENT);
        BDDMockito.given(source.containsDescendantOf(ConfigurationPropertyName.of("bar"))).willReturn(ABSENT);
        ConfigurationPropertySource aliased = source.withAliases(new ConfigurationPropertyNameAliases("foo", "bar"));
        assertThat(aliased.containsDescendantOf(name)).isEqualTo(ABSENT);
    }

    @Test
    public void containsDescendantOfWhenAnyIsPresentShouldReturnPresent() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        ConfigurationPropertySource source = Mockito.mock(ConfigurationPropertySource.class, Answers.CALLS_REAL_METHODS);
        BDDMockito.given(source.containsDescendantOf(name)).willReturn(ABSENT);
        BDDMockito.given(source.containsDescendantOf(ConfigurationPropertyName.of("bar"))).willReturn(PRESENT);
        ConfigurationPropertySource aliased = source.withAliases(new ConfigurationPropertyNameAliases("foo", "bar"));
        assertThat(aliased.containsDescendantOf(name)).isEqualTo(PRESENT);
    }

    @Test
    public void containsDescendantOfWhenPresentInAliasShouldReturnPresent() {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(Collections.singletonMap("foo.bar", "foobar"));
        ConfigurationPropertySource aliased = source.withAliases(new ConfigurationPropertyNameAliases("foo.bar", "baz.foo"));
        assertThat(aliased.containsDescendantOf(ConfigurationPropertyName.of("baz"))).isEqualTo(PRESENT);
    }
}

