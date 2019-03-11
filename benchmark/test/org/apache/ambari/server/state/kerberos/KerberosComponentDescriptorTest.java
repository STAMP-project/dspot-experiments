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


import KerberosComponentDescriptor.KEY_AUTH_TO_LOCAL_PROPERTIES;
import KerberosComponentDescriptor.KEY_CONFIGURATIONS;
import KerberosComponentDescriptor.KEY_IDENTITIES;
import KerberosIdentityDescriptor.KEY_NAME;
import category.KerberosTest;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ KerberosTest.class })
public class KerberosComponentDescriptorTest {
    static final String JSON_VALUE = ((((((((((((((" {" + ("  \"name\": \"COMPONENT_NAME\"," + "  \"identities\": [")) + (KerberosIdentityDescriptorTest.JSON_VALUE)) + "],") + "  \"configurations\": [") + "    {") + "      \"service-site\": {") + "        \"service.component.property1\": \"value1\",") + "        \"service.component.property2\": \"value2\"") + "      }") + "    }") + "  ],") + "  \"auth_to_local_properties\": [") + "      component.name.rules1") + "    ]") + "}";

    static final Map<String, Object> MAP_VALUE;

    static {
        Map<String, Object> identitiesMap = new TreeMap<>();
        identitiesMap.put(((String) (KerberosIdentityDescriptorTest.MAP_VALUE.get(KEY_NAME))), KerberosIdentityDescriptorTest.MAP_VALUE);
        identitiesMap.put(((String) (KerberosIdentityDescriptorTest.MAP_VALUE_ALT.get(KEY_NAME))), KerberosIdentityDescriptorTest.MAP_VALUE_ALT);
        identitiesMap.put(((String) (KerberosIdentityDescriptorTest.MAP_VALUE_REFERENCE.get(KEY_NAME))), KerberosIdentityDescriptorTest.MAP_VALUE_REFERENCE);
        Map<String, Object> serviceSiteProperties = new TreeMap<>();
        serviceSiteProperties.put("service.component.property1", "red");
        serviceSiteProperties.put("service.component.property", "green");
        Map<String, Map<String, Object>> serviceSiteMap = new TreeMap<>();
        serviceSiteMap.put("service-site", serviceSiteProperties);
        TreeMap<String, Map<String, Map<String, Object>>> configurationsMap = new TreeMap<>();
        configurationsMap.put("service-site", serviceSiteMap);
        Collection<String> authToLocalRules = new ArrayList<>();
        authToLocalRules.add("component.name.rules2");
        MAP_VALUE = new TreeMap<>();
        KerberosComponentDescriptorTest.MAP_VALUE.put(KEY_NAME, "A_DIFFERENT_COMPONENT_NAME");
        KerberosComponentDescriptorTest.MAP_VALUE.put(KEY_IDENTITIES, new ArrayList(identitiesMap.values()));
        KerberosComponentDescriptorTest.MAP_VALUE.put(KEY_CONFIGURATIONS, configurationsMap.values());
        KerberosComponentDescriptorTest.MAP_VALUE.put(KEY_AUTH_TO_LOCAL_PROPERTIES, authToLocalRules);
    }

    @Test
    public void testJSONDeserialize() {
        KerberosComponentDescriptorTest.validateFromJSON(KerberosComponentDescriptorTest.createFromJSON());
    }

    @Test
    public void testMapDeserialize() throws AmbariException {
        KerberosComponentDescriptorTest.validateFromMap(KerberosComponentDescriptorTest.createFromMap());
    }

    @Test
    public void testEquals() throws AmbariException {
        Assert.assertTrue(KerberosComponentDescriptorTest.createFromJSON().equals(KerberosComponentDescriptorTest.createFromJSON()));
        Assert.assertFalse(KerberosComponentDescriptorTest.createFromJSON().equals(KerberosComponentDescriptorTest.createFromMap()));
    }

    @Test
    public void testToMap() throws AmbariException {
        Gson gson = new Gson();
        KerberosComponentDescriptor descriptor = KerberosComponentDescriptorTest.createFromMap();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(gson.toJson(KerberosComponentDescriptorTest.MAP_VALUE), gson.toJson(descriptor.toMap()));
    }

    @Test
    public void testUpdate() throws AmbariException {
        KerberosComponentDescriptor componentDescriptor = KerberosComponentDescriptorTest.createFromJSON();
        KerberosComponentDescriptor updatedComponentDescriptor = KerberosComponentDescriptorTest.createFromMap();
        Assert.assertNotNull(componentDescriptor);
        Assert.assertNotNull(updatedComponentDescriptor);
        componentDescriptor.update(updatedComponentDescriptor);
        KerberosComponentDescriptorTest.validateUpdatedData(componentDescriptor);
    }
}

