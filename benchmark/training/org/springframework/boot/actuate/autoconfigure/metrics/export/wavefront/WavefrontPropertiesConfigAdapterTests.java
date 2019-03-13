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
package org.springframework.boot.actuate.autoconfigure.metrics.export.wavefront;


import java.net.URI;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryPropertiesConfigAdapterTests;


/**
 * Tests for {@link WavefrontPropertiesConfigAdapter}.
 *
 * @author Stephane Nicoll
 */
public class WavefrontPropertiesConfigAdapterTests extends StepRegistryPropertiesConfigAdapterTests<WavefrontProperties, WavefrontPropertiesConfigAdapter> {
    @Test
    public void whenPropertiesUriIsSetAdapterUriReturnsIt() {
        WavefrontProperties properties = createProperties();
        properties.setUri(URI.create("https://wavefront.example.com"));
        assertThat(createConfigAdapter(properties).uri()).isEqualTo("https://wavefront.example.com");
    }

    @Test
    public void whenPropertiesSourceIsSetAdapterSourceReturnsIt() {
        WavefrontProperties properties = createProperties();
        properties.setSource("test");
        assertThat(createConfigAdapter(properties).source()).isEqualTo("test");
    }

    @Test
    public void whenPropertiesApiTokenIsSetAdapterApiTokenReturnsIt() {
        WavefrontProperties properties = createProperties();
        properties.setApiToken("ABC123");
        assertThat(createConfigAdapter(properties).apiToken()).isEqualTo("ABC123");
    }

    @Test
    public void whenPropertiesGlobalPrefixIsSetAdapterGlobalPrefixReturnsIt() {
        WavefrontProperties properties = createProperties();
        properties.setGlobalPrefix("test");
        assertThat(createConfigAdapter(properties).globalPrefix()).isEqualTo("test");
    }
}

