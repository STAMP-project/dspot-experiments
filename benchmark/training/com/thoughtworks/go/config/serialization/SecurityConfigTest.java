/**
 * Copyright 2016 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config.serialization;


import ConfigFileFixture.DEFAULT_XML_WITH_2_AGENTS;
import com.thoughtworks.go.config.ConfigMigrator;
import com.thoughtworks.go.util.ConfigElementImplementationRegistryMother;
import java.io.ByteArrayOutputStream;
import org.junit.Test;


public class SecurityConfigTest {
    @Test
    public void shouldAllowUnorderedRoleAndUserInXsd() throws Exception {
        CruiseConfig config = new MagicalGoConfigXmlLoader(new ConfigCache(), ConfigElementImplementationRegistryMother.withNoPlugins()).loadConfigHolder(ConfigMigrator.migrate(DEFAULT_XML_WITH_2_AGENTS)).configForEdit;
        config.setServerConfig(new ServerConfig("dir", security(null, admins(role("role2"), user("jez"), role("role1")))));
        new MagicalGoConfigXmlWriter(new ConfigCache(), ConfigElementImplementationRegistryMother.withNoPlugins()).write(config, new ByteArrayOutputStream(), false);
    }
}

