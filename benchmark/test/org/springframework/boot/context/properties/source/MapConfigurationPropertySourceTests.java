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


import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;


/**
 * Tests for {@link MapConfigurationPropertySource}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class MapConfigurationPropertySourceTests {
    @Test
    public void createWhenMapIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new MapConfigurationPropertySource(null)).withMessageContaining("Map must not be null");
    }

    @Test
    public void createWhenMapHasEntriesShouldAdaptMap() {
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("foo.BAR", "spring");
        map.put(ConfigurationPropertyName.of("foo.baz"), "boot");
        MapConfigurationPropertySource source = new MapConfigurationPropertySource(map);
        assertThat(getValue(source, "foo.bar")).isEqualTo("spring");
        assertThat(getValue(source, "foo.baz")).isEqualTo("boot");
    }

    @Test
    public void putAllWhenMapIsNullShouldThrowException() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        assertThatIllegalArgumentException().isThrownBy(() -> source.putAll(null)).withMessageContaining("Map must not be null");
    }

    @Test
    public void putAllShouldPutEntries() {
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("foo.BAR", "spring");
        map.put("foo.baz", "boot");
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        source.putAll(map);
        assertThat(getValue(source, "foo.bar")).isEqualTo("spring");
        assertThat(getValue(source, "foo.baz")).isEqualTo("boot");
    }

    @Test
    public void putShouldPutEntry() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        source.put("foo.bar", "baz");
        assertThat(getValue(source, "foo.bar")).isEqualTo("baz");
    }

    @Test
    public void getConfigurationPropertyShouldGetFromMemory() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        source.put("foo.bar", "baz");
        assertThat(getValue(source, "foo.bar")).isEqualTo("baz");
        source.put("foo.bar", "big");
        assertThat(getValue(source, "foo.bar")).isEqualTo("big");
    }

    @Test
    public void iteratorShouldGetFromMemory() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        source.put("foo.BAR", "spring");
        source.put("foo.baz", "boot");
        assertThat(source.iterator()).toIterable().containsExactly(ConfigurationPropertyName.of("foo.bar"), ConfigurationPropertyName.of("foo.baz"));
    }

    @Test
    public void streamShouldGetFromMemory() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource();
        source.put("foo.BAR", "spring");
        source.put("foo.baz", "boot");
        assertThat(source.stream()).containsExactly(ConfigurationPropertyName.of("foo.bar"), ConfigurationPropertyName.of("foo.baz"));
    }
}

