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


import KerberosIdentityDescriptor.KEY_KEYTAB;
import KerberosIdentityDescriptor.KEY_NAME;
import KerberosIdentityDescriptor.KEY_PRINCIPAL;
import KerberosIdentityDescriptor.KEY_REFERENCE;
import KerberosKeytabDescriptor.KEY_ACL_ACCESS;
import KerberosKeytabDescriptor.KEY_ACL_NAME;
import KerberosKeytabDescriptor.KEY_CONFIGURATION;
import KerberosKeytabDescriptor.KEY_FILE;
import KerberosKeytabDescriptor.KEY_GROUP;
import KerberosKeytabDescriptor.KEY_OWNER;
import category.KerberosTest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ KerberosTest.class })
public class KerberosIdentityDescriptorTest {
    static final String JSON_VALUE = ((((((("{" + (("  \"name\": \"identity_1\"" + ",") + "  \"principal\":")) + (KerberosPrincipalDescriptorTest.JSON_VALUE)) + ",") + "  \"keytab\":") + (KerberosKeytabDescriptorTest.JSON_VALUE)) + ",") + "  \"when\": {\"contains\" : [\"services\", \"HIVE\"]}") + "}";

    static final Map<String, Object> MAP_VALUE;

    static final Map<String, Object> MAP_VALUE_ALT;

    static final Map<String, Object> MAP_VALUE_REFERENCE;

    static {
        MAP_VALUE = new TreeMap<>();
        KerberosIdentityDescriptorTest.MAP_VALUE.put(KEY_NAME, "identity_1");
        KerberosIdentityDescriptorTest.MAP_VALUE.put(KEY_PRINCIPAL, KerberosPrincipalDescriptorTest.MAP_VALUE);
        KerberosIdentityDescriptorTest.MAP_VALUE.put(KEY_KEYTAB, KerberosKeytabDescriptorTest.MAP_VALUE);
        MAP_VALUE_ALT = new TreeMap<>();
        KerberosIdentityDescriptorTest.MAP_VALUE_ALT.put(KEY_NAME, "identity_2");
        KerberosIdentityDescriptorTest.MAP_VALUE_ALT.put(KEY_PRINCIPAL, KerberosPrincipalDescriptorTest.MAP_VALUE);
        KerberosIdentityDescriptorTest.MAP_VALUE_ALT.put(KEY_KEYTAB, KerberosKeytabDescriptorTest.MAP_VALUE);
        TreeMap<String, Object> ownerMap = new TreeMap<>();
        ownerMap.put(KEY_ACL_NAME, "me");
        ownerMap.put(KEY_ACL_ACCESS, "rw");
        TreeMap<String, Object> groupMap = new TreeMap<>();
        groupMap.put(KEY_ACL_NAME, "nobody");
        groupMap.put(KEY_ACL_ACCESS, "");
        TreeMap<String, Object> keytabMap = new TreeMap<>();
        keytabMap.put(KEY_FILE, "/home/user/me/subject.service.keytab");
        keytabMap.put(KEY_OWNER, ownerMap);
        keytabMap.put(KEY_GROUP, groupMap);
        keytabMap.put(KEY_CONFIGURATION, "service-site/me.component.keytab.file");
        MAP_VALUE_REFERENCE = new TreeMap<>();
        KerberosIdentityDescriptorTest.MAP_VALUE_REFERENCE.put(KEY_NAME, "shared_identity");
        KerberosIdentityDescriptorTest.MAP_VALUE_REFERENCE.put(KEY_REFERENCE, "/shared");
        KerberosIdentityDescriptorTest.MAP_VALUE_REFERENCE.put(KEY_KEYTAB, keytabMap);
    }

    @Test
    public void testJSONDeserialize() {
        KerberosIdentityDescriptorTest.validateFromJSON(KerberosIdentityDescriptorTest.createFromJSON());
    }

    @Test
    public void testMapDeserialize() {
        KerberosIdentityDescriptorTest.validateFromMap(KerberosIdentityDescriptorTest.createFromMap());
    }

    @Test
    public void testEquals() throws AmbariException {
        Assert.assertTrue(KerberosIdentityDescriptorTest.createFromJSON().equals(KerberosIdentityDescriptorTest.createFromJSON()));
        Assert.assertFalse(KerberosIdentityDescriptorTest.createFromJSON().equals(KerberosIdentityDescriptorTest.createFromMap()));
    }

    @Test
    public void testToMap() throws AmbariException {
        KerberosIdentityDescriptor descriptor = KerberosIdentityDescriptorTest.createFromMap();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(KerberosIdentityDescriptorTest.MAP_VALUE, descriptor.toMap());
    }

    @Test
    public void testUpdate() {
        KerberosIdentityDescriptor identityDescriptor = KerberosIdentityDescriptorTest.createFromJSON();
        KerberosIdentityDescriptor updatedIdentityDescriptor = KerberosIdentityDescriptorTest.createFromMap();
        Assert.assertNotNull(identityDescriptor);
        Assert.assertNotNull(updatedIdentityDescriptor);
        identityDescriptor.update(updatedIdentityDescriptor);
        KerberosIdentityDescriptorTest.validateUpdatedData(identityDescriptor);
    }

    @Test
    public void testShouldInclude() {
        KerberosIdentityDescriptor identityDescriptor = KerberosIdentityDescriptorTest.createFromJSON();
        Map<String, Object> context = new TreeMap<>();
        context.put("services", new HashSet<>(Arrays.asList("HIVE", "HDFS", "ZOOKEEPER")));
        Assert.assertTrue(identityDescriptor.shouldInclude(context));
        context.put("services", new HashSet<>(Arrays.asList("NOT_HIVE", "HDFS", "ZOOKEEPER")));
        Assert.assertFalse(identityDescriptor.shouldInclude(context));
    }
}

