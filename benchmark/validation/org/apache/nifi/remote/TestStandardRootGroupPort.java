/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.remote;


import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestStandardRootGroupPort {
    @Test
    public void testCheckUserAuthorizationByDn() {
        final NiFiProperties nifiProperties = Mockito.mock(NiFiProperties.class);
        Mockito.doReturn("1 millis").when(nifiProperties).getBoredYieldDuration();
        final RootGroupPort port = createRootGroupPort(nifiProperties);
        PortAuthorizationResult authResult = port.checkUserAuthorization("CN=node1, OU=nifi.test");
        Assert.assertFalse(authResult.isAuthorized());
        authResult = port.checkUserAuthorization("node1@nifi.test");
        Assert.assertTrue(authResult.isAuthorized());
    }

    @Test
    public void testCheckUserAuthorizationByMappedDn() {
        final NiFiProperties nifiProperties = Mockito.mock(NiFiProperties.class);
        final String mapKey = ".dn";
        Set<String> propertyKeys = new LinkedHashSet<>();
        propertyKeys.add(((NiFiProperties.SECURITY_IDENTITY_MAPPING_PATTERN_PREFIX) + mapKey));
        propertyKeys.add(((NiFiProperties.SECURITY_IDENTITY_MAPPING_VALUE_PREFIX) + mapKey));
        Mockito.doReturn(propertyKeys).when(nifiProperties).getPropertyKeys();
        final String mapPattern = "^CN=(.*?), OU=(.*?)$";
        final String mapValue = "$1@$2";
        Mockito.doReturn(mapPattern).when(nifiProperties).getProperty(ArgumentMatchers.eq(((NiFiProperties.SECURITY_IDENTITY_MAPPING_PATTERN_PREFIX) + mapKey)));
        Mockito.doReturn(mapValue).when(nifiProperties).getProperty(ArgumentMatchers.eq(((NiFiProperties.SECURITY_IDENTITY_MAPPING_VALUE_PREFIX) + mapKey)));
        Mockito.doReturn("1 millis").when(nifiProperties).getBoredYieldDuration();
        final RootGroupPort port = createRootGroupPort(nifiProperties);
        PortAuthorizationResult authResult = port.checkUserAuthorization("CN=node2, OU=nifi.test");
        Assert.assertFalse(authResult.isAuthorized());
        authResult = port.checkUserAuthorization("CN=node1, OU=nifi.test");
        Assert.assertTrue(authResult.isAuthorized());
    }

    @Test
    public void testCheckUserAuthorizationByMappedDnWithTransformation() {
        final NiFiProperties nifiProperties = Mockito.mock(NiFiProperties.class);
        final String mapKey = ".dn";
        Set<String> propertyKeys = new LinkedHashSet<>();
        propertyKeys.add(((NiFiProperties.SECURITY_IDENTITY_MAPPING_PATTERN_PREFIX) + mapKey));
        propertyKeys.add(((NiFiProperties.SECURITY_IDENTITY_MAPPING_VALUE_PREFIX) + mapKey));
        propertyKeys.add(((NiFiProperties.SECURITY_IDENTITY_MAPPING_TRANSFORM_PREFIX) + mapKey));
        Mockito.doReturn(propertyKeys).when(nifiProperties).getPropertyKeys();
        final String mapPattern = "^CN=(.*?), OU=(.*?)$";
        final String mapValue = "$1@$2";
        final String mapTransform = "UPPER";
        Mockito.doReturn(mapPattern).when(nifiProperties).getProperty(ArgumentMatchers.eq(((NiFiProperties.SECURITY_IDENTITY_MAPPING_PATTERN_PREFIX) + mapKey)));
        Mockito.doReturn(mapValue).when(nifiProperties).getProperty(ArgumentMatchers.eq(((NiFiProperties.SECURITY_IDENTITY_MAPPING_VALUE_PREFIX) + mapKey)));
        Mockito.doReturn(mapTransform).when(nifiProperties).getProperty(ArgumentMatchers.eq(((NiFiProperties.SECURITY_IDENTITY_MAPPING_TRANSFORM_PREFIX) + mapKey)));
        Mockito.doReturn("1 millis").when(nifiProperties).getBoredYieldDuration();
        final RootGroupPort port = createRootGroupPort(nifiProperties);
        PortAuthorizationResult authResult = port.checkUserAuthorization("CN=node2, OU=nifi.test");
        Assert.assertFalse(authResult.isAuthorized());
        authResult = port.checkUserAuthorization("CN=node1, OU=nifi.test");
        Assert.assertTrue(authResult.isAuthorized());
    }
}

