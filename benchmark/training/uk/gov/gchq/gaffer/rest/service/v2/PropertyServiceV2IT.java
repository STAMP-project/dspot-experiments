/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.rest.service.v2;


import SystemProperty.APP_TITLE;
import SystemProperty.APP_TITLE_DEFAULT;
import SystemProperty.GAFFER_VERSION;
import SystemProperty.KORYPHE_VERSION;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.rest.AbstractRestApiIT;

import static PropertiesServiceV2.CORE_EXPOSED_PROPERTIES;


public class PropertyServiceV2IT extends AbstractRestApiV2IT {
    @Test
    public void shouldThrowErrorOnUnknownPropertyWhenNoneSet() throws IOException {
        // Given
        // When
        final Response response = client.getProperty("UNKNOWN");
        // Then
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void shouldThrowErrorOnUnknownProperty() throws IOException {
        // Given
        System.setProperty("gaffer.properties", "gaffer.test1,gaffer.test2");
        System.setProperty("gaffer.test1", "1");
        System.setProperty("gaffer.test2", "2");
        // When
        final Response response = client.getProperty("UNKNOWN");
        // Then
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void shouldThrowErrorOnPropertyThatIsNotExposed() throws IOException {
        // Given
        System.setProperty("gaffer.properties", "gaffer.test1,gaffer.test2");
        System.setProperty("gaffer.test1", "1");
        System.setProperty("gaffer.test2", "2");
        System.setProperty("gaffer.test3", "3");
        // When
        final Response response = client.getProperty("gaffer.test3");
        // Then
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void shouldGetAllProperties() throws IOException {
        // Given
        System.setProperty("gaffer.properties", "gaffer.test1,gaffer.test2");
        System.setProperty("gaffer.test1", "1");
        System.setProperty("gaffer.test2", "2");
        System.setProperty("gaffer.test3", "3");
        System.setProperty(APP_TITLE, "newTitle");
        // When
        final Response response = client.getProperties();
        // Then
        Assert.assertEquals(200, response.getStatus());
        Map<String, Object> properties = response.readEntity(Map.class);
        final LinkedHashMap<String, String> expectedProperties = new LinkedHashMap(CORE_EXPOSED_PROPERTIES);
        expectedProperties.put("gaffer.test1", "1");
        expectedProperties.put("gaffer.test2", "2");
        expectedProperties.put(APP_TITLE, "newTitle");
        Assert.assertEquals(expectedProperties, properties);
    }

    @Test
    public void shouldGetAllPropertiesWhenNoCustomPropertiesCsvDefined() throws IOException {
        // Given
        System.setProperty("gaffer.test1", "1");
        System.setProperty("gaffer.test2", "2");
        System.setProperty("gaffer.test3", "3");
        System.setProperty(APP_TITLE, "newTitle");
        // When
        final Response response = client.getProperties();
        // Then
        Assert.assertEquals(200, response.getStatus());
        Map<String, Object> properties = response.readEntity(Map.class);
        final LinkedHashMap<String, String> expectedProperties = new LinkedHashMap(CORE_EXPOSED_PROPERTIES);
        expectedProperties.put(APP_TITLE, "newTitle");
        Assert.assertEquals(expectedProperties, properties);
    }

    @Test
    public void shouldGetKnownProperty() throws IOException {
        // Given
        System.setProperty("gaffer.properties", "gaffer.test1,gaffer.test2");
        System.setProperty("gaffer.test1", "1");
        System.setProperty("gaffer.test2", "2");
        // When
        final Response response = client.getProperty("gaffer.test1");
        // Then
        Assert.assertEquals(200, response.getStatus());
        String property = response.readEntity(String.class);
        Assert.assertEquals("1", property);
    }

    @Test
    public void shouldGetKnownCoreProperty() throws IOException {
        // Given
        System.setProperty("gaffer.properties", "gaffer.test1,gaffer.test2");
        System.setProperty("gaffer.test1", "1");
        System.setProperty("gaffer.test2", "2");
        // When
        final Response response = client.getProperty(APP_TITLE);
        // Then
        Assert.assertEquals(200, response.getStatus());
        String property = response.readEntity(String.class);
        Assert.assertEquals(APP_TITLE_DEFAULT, property);
    }

    @Test
    public void shouldGetOverriddenKnownCoreProperty() throws IOException {
        // Given
        System.setProperty("gaffer.properties", "gaffer.test1,gaffer.test2");
        System.setProperty("gaffer.test1", "1");
        System.setProperty("gaffer.test2", "2");
        System.setProperty(APP_TITLE, "newTitle");
        // When
        final Response response = client.getProperty(APP_TITLE);
        // Then
        Assert.assertEquals(200, response.getStatus());
        String property = response.readEntity(String.class);
        Assert.assertEquals("newTitle", property);
    }

    @Test
    public void shouldGetKorypheVersion() throws IOException {
        // When
        final Response response = client.getProperty(KORYPHE_VERSION);
        final String propertyValue = response.readEntity(String.class);
        Assert.assertNotNull(propertyValue);
    }

    @Test
    public void shouldGetGafferVersion() throws IOException {
        // When
        final Response response = client.getProperty(GAFFER_VERSION);
        final String propertyValue = response.readEntity(String.class);
        Assert.assertNotNull(propertyValue);
    }
}

