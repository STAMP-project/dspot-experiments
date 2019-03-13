/**
 * Copyright 2016 Red Hat, Inc.
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
package org.jboss.as.test.integration.domain.mixed;


import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.junit.Test;


/**
 * Tests slave behavior in a mixed domain when the master has booted with a legacy domain.xml.
 *
 * @author Brian Stansberry
 */
public abstract class LegacyConfigTest {
    private static final PathElement SLAVE = PathElement.pathElement("host", "slave");

    private static final PathAddress TEST_SERVER_CONFIG = PathAddress.pathAddress(LegacyConfigTest.SLAVE, PathElement.pathElement("server-config", "legacy-server"));

    private static final PathAddress TEST_SERVER = PathAddress.pathAddress(LegacyConfigTest.SLAVE, PathElement.pathElement("server", "legacy-server"));

    private static final PathAddress TEST_SERVER_GROUP = PathAddress.pathAddress("server-group", "legacy-group");

    private static final Map<String, String> STD_PROFILES;

    static {
        final Map<String, String> stdProfiles = new HashMap<>();
        stdProfiles.put("default", "standard-sockets");
        stdProfiles.put("ha", "ha-sockets");
        stdProfiles.put("full", "full-sockets");
        stdProfiles.put("full-ha", "full-ha-sockets");
        STD_PROFILES = Collections.unmodifiableMap(stdProfiles);
    }

    private static DomainTestSupport support;

    @Test
    public void testServerLaunching() throws IOException, InterruptedException, MgmtOperationException {
        DomainClient client = LegacyConfigTest.support.getDomainMasterLifecycleUtil().getDomainClient();
        for (Map.Entry<String, String> entry : getProfilesToTest().entrySet()) {
            String profile = entry.getKey();
            String sbg = entry.getValue();
            try {
                installTestServer(client, profile, sbg);
                awaitServerLaunch(client, profile);
                validateServerProfile(client, profile);
                verifyHttp(profile);
            } finally {
                cleanTestServer(client);
            }
        }
    }
}

