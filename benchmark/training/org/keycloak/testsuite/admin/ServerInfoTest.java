/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.admin;


import Version.VERSION;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.info.ServerInfoRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class ServerInfoTest extends AbstractKeycloakTest {
    @Test
    public void testServerInfo() {
        ServerInfoRepresentation info = adminClient.serverInfo().getInfo();
        Assert.assertNotNull(info);
        Assert.assertNotNull(info.getProviders());
        Assert.assertNotNull(info.getProviders().get("realm"));
        Assert.assertNotNull(info.getProviders().get("user"));
        Assert.assertNotNull(info.getProviders().get("authenticator"));
        Assert.assertNotNull(info.getThemes());
        Assert.assertNotNull(info.getThemes().get("account"));
        Assert.assertNotNull(info.getThemes().get("admin"));
        Assert.assertNotNull(info.getThemes().get("email"));
        Assert.assertNotNull(info.getThemes().get("login"));
        Assert.assertNotNull(info.getThemes().get("welcome"));
        Assert.assertNotNull(info.getEnums());
        Assert.assertNotNull(info.getMemoryInfo());
        Assert.assertNotNull(info.getSystemInfo());
        Assert.assertEquals(VERSION, info.getSystemInfo().getVersion());
        Assert.assertNotNull(info.getSystemInfo().getServerTime());
        Assert.assertNotNull(info.getSystemInfo().getUptime());
        log.infof("JPA Connections provider info: %s", info.getProviders().get("connectionsJpa").getProviders().get("default").getOperationalInfo().toString());
    }
}

