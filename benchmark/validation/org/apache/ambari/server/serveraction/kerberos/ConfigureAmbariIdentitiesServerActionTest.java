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
package org.apache.ambari.server.serveraction.kerberos;


import org.apache.ambari.server.serveraction.ActionLog;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ConfigureAmbariIdentitiesServerActionTest extends EasyMockSupport {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void installAmbariServerIdentity() throws Exception {
        installAmbariServerIdentity(createNiceMock(ActionLog.class), true);
    }

    @Test
    public void installAmbariServerIdentityWithNoAgentOnAmbariServer() throws Exception {
        installAmbariServerIdentity(createNiceMock(ActionLog.class), false);
    }

    @Test
    public void installAmbariServerIdentityWithNullActionLog() throws Exception {
        installAmbariServerIdentity(null, true);
    }

    @Test
    public void configureJAAS() throws Exception {
        configureJAAS(createNiceMock(ActionLog.class));
    }

    @Test
    public void configureJAASWithNullActionLog() throws Exception {
        configureJAAS(null);
    }
}

