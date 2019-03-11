/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.state.kerberos;


import KerberosServiceDescriptor.KEY_AUTH_TO_LOCAL_PROPERTIES;
import KerberosServiceDescriptor.KEY_COMPONENTS;
import KerberosServiceDescriptor.KEY_CONFIGURATIONS;
import KerberosServiceDescriptor.KEY_IDENTITIES;
import category.KerberosTest;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ KerberosTest.class })
public class KerberosServiceDescriptorTest {
    static final String JSON_VALUE = ((((((((((((((((("{" + (("  \"name\": \"SERVICE_NAME\"," + "  \"preconfigure\": \"true\",") + "  \"identities\": [")) + (KerberosIdentityDescriptorTest.JSON_VALUE)) + "],") + "  \"components\": [") + (KerberosComponentDescriptorTest.JSON_VALUE)) + "],") + "  \"auth_to_local_properties\": [") + "      service.name.rules1") + "    ],") + "  \"configurations\": [") + "    {") + "      \"service-site\": {") + "        \"service.property1\": \"value1\",") + "        \"service.property2\": \"value2\"") + "      }") + "    }") + "  ]") + "}";

    private static final String JSON_VALUE_SERVICES = (((((((((((((((((((((((((((((((((((("{ " + (((("\"services\" : [" + "{") + "  \"name\": \"SERVICE_NAME\",") + "  \"preconfigure\": \"true\",") + "  \"identities\": [")) + (KerberosIdentityDescriptorTest.JSON_VALUE)) + "],") + "  \"components\": [") + (KerberosComponentDescriptorTest.JSON_VALUE)) + "],") + "  \"auth_to_local_properties\": [") + "      service.name.rules1") + "    ],") + "  \"configurations\": [") + "    {") + "      \"service-site\": {") + "        \"service.property1\": \"value1\",") + "        \"service.property2\": \"value2\"") + "      }") + "    }") + "  ]") + "},") + "{") + "  \"name\": \"A_DIFFERENT_SERVICE_NAME\",") + "  \"identities\": [") + (KerberosIdentityDescriptorTest.JSON_VALUE)) + "],") + "  \"components\": [") + (KerberosComponentDescriptorTest.JSON_VALUE)) + "],") + "  \"configurations\": [") + "    {") + "      \"service-site\": {") + "        \"service.property1\": \"value1\",") + "        \"service.property2\": \"value2\"") + "      }") + "    }") + "  ]") + "}") + "]") + "}";

    public static final Map<String, Object> MAP_VALUE;

    static {
        Map<String, Object> identitiesMap = new TreeMap<>();
        identitiesMap.put(((String) (KerberosIdentityDescriptorTest.MAP_VALUE.get("name"))), KerberosIdentityDescriptorTest.MAP_VALUE);
        Map<String, Object> componentsMap = new TreeMap<>();
        componentsMap.put(((String) (KerberosComponentDescriptorTest.MAP_VALUE.get("name"))), KerberosComponentDescriptorTest.MAP_VALUE);
        Map<String, Object> serviceSiteProperties = new TreeMap<>();
        serviceSiteProperties.put("service.property1", "red");
        serviceSiteProperties.put("service.property", "green");
        Map<String, Map<String, Object>> serviceSiteMap = new TreeMap<>();
        serviceSiteMap.put("service-site", serviceSiteProperties);
        TreeMap<String, Map<String, Map<String, Object>>> configurationsMap = new TreeMap<>();
        configurationsMap.put("service-site", serviceSiteMap);
        Collection<String> authToLocalRules = new ArrayList<>();
        authToLocalRules.add("service.name.rules2");
        MAP_VALUE = new TreeMap<>();
        KerberosServiceDescriptorTest.MAP_VALUE.put("name", "A_DIFFERENT_SERVICE_NAME");
        KerberosServiceDescriptorTest.MAP_VALUE.put(KEY_IDENTITIES, identitiesMap.values());
        KerberosServiceDescriptorTest.MAP_VALUE.put(KEY_COMPONENTS, componentsMap.values());
        KerberosServiceDescriptorTest.MAP_VALUE.put(KEY_CONFIGURATIONS, configurationsMap.values());
        KerberosServiceDescriptorTest.MAP_VALUE.put(KEY_AUTH_TO_LOCAL_PROPERTIES, authToLocalRules);
    }

    private static final KerberosServiceDescriptorFactory KERBEROS_SERVICE_DESCRIPTOR_FACTORY = new KerberosServiceDescriptorFactory();

    @Test
    public void testJSONDeserialize() throws AmbariException {
        KerberosServiceDescriptorTest.validateFromJSON(createFromJSON());
    }

    @Test
    public void testJSONDeserializeMultiple() throws AmbariException {
        KerberosServiceDescriptorTest.validateFromJSON(createMultipleFromJSON());
    }

    @Test
    public void testInvalid() {
        // Invalid JSON syntax
        try {
            KerberosServiceDescriptorTest.KERBEROS_SERVICE_DESCRIPTOR_FACTORY.createInstances(((KerberosServiceDescriptorTest.JSON_VALUE_SERVICES) + "erroneous text"));
            Assert.fail("Should have thrown AmbariException.");
        } catch (AmbariException e) {
            // This is expected
        } catch (Throwable t) {
            Assert.fail("Should have thrown AmbariException.");
        }
        // Test missing top-level "services"
        try {
            KerberosServiceDescriptorTest.KERBEROS_SERVICE_DESCRIPTOR_FACTORY.createInstances(KerberosServiceDescriptorTest.JSON_VALUE);
            Assert.fail("Should have thrown AmbariException.");
        } catch (AmbariException e) {
            // This is expected
        } catch (Throwable t) {
            Assert.fail("Should have thrown AmbariException.");
        }
        // Test missing top-level "services" in file
        URL url = getClass().getClassLoader().getResource("service_level_kerberos_invalid.json");
        File file = (url == null) ? null : new File(url.getFile());
        try {
            KerberosServiceDescriptorTest.KERBEROS_SERVICE_DESCRIPTOR_FACTORY.createInstances(file);
            Assert.fail("Should have thrown AmbariException.");
        } catch (AmbariException e) {
            // This is expected
        } catch (Throwable t) {
            Assert.fail("Should have thrown AmbariException.");
        }
    }

    @Test
    public void testFileDeserialize() throws IOException {
        KerberosServiceDescriptor[] descriptors = createFromFile();
        Assert.assertNotNull(descriptors);
        Assert.assertEquals(2, descriptors.length);
    }

    @Test
    public void testMapDeserialize() throws AmbariException {
        KerberosServiceDescriptorTest.validateFromMap(createFromMap());
    }

    @Test
    public void testEquals() throws AmbariException {
        Assert.assertTrue(createFromJSON().equals(createFromJSON()));
        Assert.assertFalse(createFromJSON().equals(createFromMap()));
    }

    @Test
    public void testToMap() throws AmbariException {
        Gson gson = new Gson();
        KerberosServiceDescriptor descriptor = createFromMap();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(gson.toJson(KerberosServiceDescriptorTest.MAP_VALUE), gson.toJson(descriptor.toMap()));
    }

    @Test
    public void testUpdate() throws AmbariException {
        KerberosServiceDescriptor serviceDescriptor = createFromJSON();
        KerberosServiceDescriptor updatedServiceDescriptor = createFromMap();
        Assert.assertNotNull(serviceDescriptor);
        Assert.assertNotNull(updatedServiceDescriptor);
        serviceDescriptor.update(updatedServiceDescriptor);
        validateUpdatedData(serviceDescriptor);
    }

    /**
     * Test a JSON object in which only only a Service and configs are defined, but no Components.
     */
    @Test
    public void testJSONWithOnlyServiceNameAndConfigurations() throws AmbariException {
        String JSON_VALUE_ONLY_NAME_AND_CONFIGS = "{" + ((((((((("  \"name\": \"SERVICE_NAME\"," + "  \"configurations\": [") + "    {") + "      \"service-site\": {") + "        \"service.property1\": \"value1\",") + "        \"service.property2\": \"value2\"") + "      }") + "    }") + "  ]") + "}");
        TreeMap<String, Object> CHANGE_NAME = new TreeMap<String, Object>() {
            {
                put("name", "A_DIFFERENT_SERVICE_NAME");
            }
        };
        KerberosServiceDescriptor serviceDescriptor = KerberosServiceDescriptorTest.KERBEROS_SERVICE_DESCRIPTOR_FACTORY.createInstance("SERVICE_NAME", JSON_VALUE_ONLY_NAME_AND_CONFIGS);
        KerberosServiceDescriptor updatedServiceDescriptor = new KerberosServiceDescriptor(CHANGE_NAME);
        Assert.assertNotNull(serviceDescriptor);
        Assert.assertNotNull(updatedServiceDescriptor);
        // Update
        serviceDescriptor.update(updatedServiceDescriptor);
        // Validate
        Assert.assertNotNull(serviceDescriptor);
        Assert.assertEquals("A_DIFFERENT_SERVICE_NAME", serviceDescriptor.getName());
    }
}

