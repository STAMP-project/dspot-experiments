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


import KerberosPrincipalDescriptor.KEY_CONFIGURATION;
import KerberosPrincipalDescriptor.KEY_LOCAL_USERNAME;
import KerberosPrincipalDescriptor.KEY_TYPE;
import KerberosPrincipalDescriptor.KEY_VALUE;
import KerberosPrincipalType.SERVICE;
import KerberosPrincipalType.USER;
import category.KerberosTest;
import java.util.Map;
import java.util.TreeMap;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ KerberosTest.class })
public class KerberosPrincipalDescriptorTest {
    static final String JSON_VALUE = "{" + (((("\"value\": \"service/_HOST@_REALM\"," + "\"configuration\": \"service-site/service.component.kerberos.principal\",") + "\"type\": \"service\",") + "\"local_username\": \"localUser\"") + "}");

    private static final String JSON_VALUE_SPARSE = "{" + ("\"value\": \"serviceOther/_HOST@_REALM\"" + "}");

    public static final Map<String, Object> MAP_VALUE;

    private static final Map<String, Object> MAP_VALUE_SPARSE;

    static {
        MAP_VALUE = new TreeMap<>();
        KerberosPrincipalDescriptorTest.MAP_VALUE.put(KEY_VALUE, "user@_REALM");
        KerberosPrincipalDescriptorTest.MAP_VALUE.put(KEY_CONFIGURATION, "service-site/service.component.kerberos.https.principal");
        KerberosPrincipalDescriptorTest.MAP_VALUE.put(KEY_TYPE, "user");
        KerberosPrincipalDescriptorTest.MAP_VALUE.put(KEY_LOCAL_USERNAME, null);
        MAP_VALUE_SPARSE = new TreeMap<>();
        KerberosPrincipalDescriptorTest.MAP_VALUE_SPARSE.put(KEY_VALUE, "userOther@_REALM");
    }

    @Test
    public void testJSONDeserialize() {
        KerberosPrincipalDescriptorTest.validateFromJSON(KerberosPrincipalDescriptorTest.createFromJSON());
    }

    @Test
    public void testMapDeserialize() {
        KerberosPrincipalDescriptorTest.validateFromMap(KerberosPrincipalDescriptorTest.createFromMap());
    }

    @Test
    public void testEquals() throws AmbariException {
        Assert.assertTrue(KerberosPrincipalDescriptorTest.createFromJSON().equals(KerberosPrincipalDescriptorTest.createFromJSON()));
        Assert.assertFalse(KerberosPrincipalDescriptorTest.createFromJSON().equals(KerberosPrincipalDescriptorTest.createFromMap()));
    }

    @Test
    public void testToMap() throws AmbariException {
        KerberosPrincipalDescriptor descriptor = KerberosPrincipalDescriptorTest.createFromMap();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(KerberosPrincipalDescriptorTest.MAP_VALUE, descriptor.toMap());
    }

    @Test
    public void testUpdate() {
        KerberosPrincipalDescriptor principalDescriptor = KerberosPrincipalDescriptorTest.createFromJSON();
        KerberosPrincipalDescriptor updatedPrincipalDescriptor = KerberosPrincipalDescriptorTest.createFromMap();
        Assert.assertNotNull(principalDescriptor);
        Assert.assertNotNull(updatedPrincipalDescriptor);
        principalDescriptor.update(updatedPrincipalDescriptor);
        KerberosPrincipalDescriptorTest.validateUpdatedData(principalDescriptor);
    }

    @Test
    public void testUpdateSparse() {
        KerberosPrincipalDescriptor principalDescriptor;
        KerberosPrincipalDescriptor updatedPrincipalDescriptor;
        /* ****************************************
        Test updating a service principal
        ****************************************
         */
        principalDescriptor = KerberosPrincipalDescriptorTest.createFromJSON();
        updatedPrincipalDescriptor = KerberosPrincipalDescriptorTest.createFromJSONSparse();
        Assert.assertNotNull(principalDescriptor);
        Assert.assertNotNull(updatedPrincipalDescriptor);
        // The original value
        Assert.assertEquals("service/_HOST@_REALM", principalDescriptor.getValue());
        Assert.assertEquals("service-site/service.component.kerberos.principal", principalDescriptor.getConfiguration());
        Assert.assertEquals(SERVICE, principalDescriptor.getType());
        Assert.assertEquals("localUser", principalDescriptor.getLocalUsername());
        principalDescriptor.update(updatedPrincipalDescriptor);
        // The updated value
        Assert.assertEquals("serviceOther/_HOST@_REALM", principalDescriptor.getValue());
        Assert.assertEquals("service-site/service.component.kerberos.principal", principalDescriptor.getConfiguration());
        Assert.assertEquals(SERVICE, principalDescriptor.getType());
        Assert.assertEquals("localUser", principalDescriptor.getLocalUsername());
        /* ****************************************
        Test updating a user principal
        ****************************************
         */
        principalDescriptor = KerberosPrincipalDescriptorTest.createFromMap();
        updatedPrincipalDescriptor = KerberosPrincipalDescriptorTest.createFromMapSparse();
        Assert.assertNotNull(principalDescriptor);
        Assert.assertNotNull(updatedPrincipalDescriptor);
        Assert.assertEquals("user@_REALM", principalDescriptor.getValue());
        Assert.assertEquals("service-site/service.component.kerberos.https.principal", principalDescriptor.getConfiguration());
        Assert.assertEquals(USER, principalDescriptor.getType());
        Assert.assertNull(principalDescriptor.getLocalUsername());
        principalDescriptor.update(updatedPrincipalDescriptor);
        Assert.assertEquals("userOther@_REALM", principalDescriptor.getValue());
        Assert.assertEquals("service-site/service.component.kerberos.https.principal", principalDescriptor.getConfiguration());
        Assert.assertEquals(USER, principalDescriptor.getType());
        Assert.assertNull(principalDescriptor.getLocalUsername());
    }
}

