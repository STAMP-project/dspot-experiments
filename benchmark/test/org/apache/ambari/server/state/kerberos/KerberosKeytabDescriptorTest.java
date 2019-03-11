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


import KerberosKeytabDescriptor.KEY_ACL_ACCESS;
import KerberosKeytabDescriptor.KEY_ACL_NAME;
import KerberosKeytabDescriptor.KEY_CONFIGURATION;
import KerberosKeytabDescriptor.KEY_FILE;
import KerberosKeytabDescriptor.KEY_GROUP;
import KerberosKeytabDescriptor.KEY_OWNER;
import category.KerberosTest;
import java.util.Map;
import java.util.TreeMap;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ KerberosTest.class })
public class KerberosKeytabDescriptorTest {
    static final String JSON_VALUE = "{" + (((((((((("  \"file\": \"/etc/security/keytabs/${host}/subject.service.keytab\"," + "  \"owner\": {") + "      \"name\": \"subject\",") + "      \"access\": \"rw\"") + "  },") + "  \"group\": {") + "      \"name\": \"hadoop\",") + "      \"access\": \"r\"") + "  },") + "  \"configuration\": \"service-site/service.component.keytab.file\"") + "}");

    static final Map<String, Object> MAP_VALUE;

    static {
        TreeMap<String, Object> ownerMap = new TreeMap<>();
        ownerMap.put(KEY_ACL_NAME, "root");
        ownerMap.put(KEY_ACL_ACCESS, "rw");
        TreeMap<String, Object> groupMap = new TreeMap<>();
        groupMap.put(KEY_ACL_NAME, "hadoop");
        groupMap.put(KEY_ACL_ACCESS, "r");
        MAP_VALUE = new TreeMap<>();
        KerberosKeytabDescriptorTest.MAP_VALUE.put(KEY_FILE, "/etc/security/keytabs/subject.service.keytab");
        KerberosKeytabDescriptorTest.MAP_VALUE.put(KEY_OWNER, ownerMap);
        KerberosKeytabDescriptorTest.MAP_VALUE.put(KEY_GROUP, groupMap);
        KerberosKeytabDescriptorTest.MAP_VALUE.put(KEY_CONFIGURATION, "service-site/service2.component.keytab.file");
    }

    @Test
    public void testJSONDeserialize() {
        KerberosKeytabDescriptorTest.validateFromJSON(KerberosKeytabDescriptorTest.createFromJSON());
    }

    @Test
    public void testMapDeserialize() {
        KerberosKeytabDescriptorTest.validateFromMap(KerberosKeytabDescriptorTest.createFromMap());
    }

    @Test
    public void testEquals() throws AmbariException {
        Assert.assertTrue(KerberosKeytabDescriptorTest.createFromJSON().equals(KerberosKeytabDescriptorTest.createFromJSON()));
        Assert.assertFalse(KerberosKeytabDescriptorTest.createFromJSON().equals(KerberosKeytabDescriptorTest.createFromMap()));
    }

    @Test
    public void testToMap() throws AmbariException {
        KerberosKeytabDescriptor descriptor = KerberosKeytabDescriptorTest.createFromMap();
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(KerberosKeytabDescriptorTest.MAP_VALUE, descriptor.toMap());
    }

    @Test
    public void testUpdate() {
        KerberosKeytabDescriptor keytabDescriptor = KerberosKeytabDescriptorTest.createFromJSON();
        KerberosKeytabDescriptor updatedKeytabDescriptor = KerberosKeytabDescriptorTest.createFromMap();
        Assert.assertNotNull(keytabDescriptor);
        Assert.assertNotNull(updatedKeytabDescriptor);
        keytabDescriptor.update(updatedKeytabDescriptor);
        KerberosKeytabDescriptorTest.validateUpdatedData(keytabDescriptor);
    }
}

