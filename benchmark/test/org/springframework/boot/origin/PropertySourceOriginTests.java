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
package org.springframework.boot.origin;


import java.util.HashMap;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;


/**
 * Tests for {@link PropertySourceOrigin}.
 *
 * @author Phillip Webb
 */
public class PropertySourceOriginTests {
    @Test
    public void createWhenPropertySourceIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PropertySourceOrigin(null, "name")).withMessageContaining("PropertySource must not be null");
    }

    @Test
    public void createWhenPropertyNameIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PropertySourceOrigin(mock(.class), null)).withMessageContaining("PropertyName must not be empty");
    }

    @Test
    public void createWhenPropertyNameIsEmptyShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PropertySourceOrigin(mock(.class), "")).withMessageContaining("PropertyName must not be empty");
    }

    @Test
    public void getPropertySourceShouldReturnPropertySource() {
        MapPropertySource propertySource = new MapPropertySource("test", new HashMap());
        PropertySourceOrigin origin = new PropertySourceOrigin(propertySource, "foo");
        assertThat(origin.getPropertySource()).isEqualTo(propertySource);
    }

    @Test
    public void getPropertyNameShouldReturnPropertyName() {
        MapPropertySource propertySource = new MapPropertySource("test", new HashMap());
        PropertySourceOrigin origin = new PropertySourceOrigin(propertySource, "foo");
        assertThat(origin.getPropertyName()).isEqualTo("foo");
    }

    @Test
    public void toStringShouldShowDetails() {
        MapPropertySource propertySource = new MapPropertySource("test", new HashMap());
        PropertySourceOrigin origin = new PropertySourceOrigin(propertySource, "foo");
        assertThat(origin.toString()).isEqualTo("\"foo\" from property source \"test\"");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getWhenPropertySourceSupportsOriginLookupShouldReturnOrigin() {
        Origin origin = Mockito.mock(Origin.class);
        PropertySource<?> propertySource = Mockito.mock(PropertySource.class, Mockito.withSettings().extraInterfaces(OriginLookup.class));
        OriginLookup<String> originCapablePropertySource = ((OriginLookup<String>) (propertySource));
        BDDMockito.given(originCapablePropertySource.getOrigin("foo")).willReturn(origin);
        assertThat(PropertySourceOrigin.get(propertySource, "foo")).isSameAs(origin);
    }

    @Test
    public void getWhenPropertySourceSupportsOriginLookupButNoOriginShouldWrap() {
        PropertySource<?> propertySource = Mockito.mock(PropertySource.class, Mockito.withSettings().extraInterfaces(OriginLookup.class));
        assertThat(PropertySourceOrigin.get(propertySource, "foo")).isInstanceOf(PropertySourceOrigin.class);
    }

    @Test
    public void getWhenPropertySourceIsNotOriginAwareShouldWrap() {
        MapPropertySource propertySource = new MapPropertySource("test", new HashMap());
        PropertySourceOrigin origin = new PropertySourceOrigin(propertySource, "foo");
        assertThat(origin.getPropertySource()).isEqualTo(propertySource);
        assertThat(origin.getPropertyName()).isEqualTo("foo");
    }
}

