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
package com.thoughtworks.go.config.upgrades;


import GoConstants.CONFIG_SCHEMA_VERSION;
import com.thoughtworks.go.config.ConfigMigrator;
import com.thoughtworks.go.util.ConfigElementImplementationRegistryMother;
import java.io.ByteArrayOutputStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


// test for 14.xsl
public class AuthorizationMigrationTest {
    private static final String OLD_AUTH = "          <auth>\n" + ((("            <role>admin</role>\n" + "            <role>qa_lead</role>\n") + "            <user>jez</user>\n") + "          </auth>\n");

    private static final String NEW_AUTHORIZATION = "          <authorization>\n" + ((("            <role>admin</role>\n" + "            <role>qa_lead</role>\n") + "            <user>jez</user>\n") + "          </authorization>\n");

    private static final String CONFIG_WITH_AUTH = ((((((((((((((("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + (((((((((((((((("<cruise xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + "xsi:noNamespaceSchemaLocation=\"cruise-config.xsd\" schemaVersion=\"13\">\n") + "  <server artifactsdir=\"other-artifacts\">\n") + "    <security>\n") + "      <roles>\n") + "        <role name=\"admin\" />\n") + "        <role name=\"qa_lead\" />\n") + "      </roles>\n") + "    </security>\n") + "  </server>\n") + "  <pipelines group=\"defaultGroup\">\n") + "    <pipeline name=\"pipeline1\" labeltemplate=\"alpha.${COUNT}\">\n") + "       <materials>\n") + "         <svn url=\"foobar\" checkexternals=\"true\" />\n") + "       </materials>\n") + "      <stage name=\"stage1\">\n") + "       <approval type=\"manual\">\n")) + (AuthorizationMigrationTest.OLD_AUTH)) + "       </approval>\n") + "       <jobs>\n") + "         <job name=\"functional\">\n") + "           <artifacts>\n") + "             <log src=\"artifact1.xml\" dest=\"cruise-output\" />\n") + "           </artifacts>\n") + "         </job>\n") + "         <job name=\"unit\">\n") + "         </job>\n") + "        </jobs>\n") + "      </stage>\n") + "    </pipeline>\n") + "  </pipelines>\n") + "</cruise>";

    @Test
    public void shouldMigrateAuthToAuthorization() throws Exception {
        String newConfig = ConfigMigrator.migrate(AuthorizationMigrationTest.CONFIG_WITH_AUTH).replace("\r\n", "\n");
        Assert.assertThat(newConfig, Matchers.containsString(String.valueOf(CONFIG_SCHEMA_VERSION)));
        Assert.assertThat(newConfig, Matchers.containsString(AuthorizationMigrationTest.NEW_AUTHORIZATION));
    }

    @Test
    public void shouldBeAbleToParseNewAuthorization() throws Exception {
        AuthConfig config = fromXmlPartial(AuthorizationMigrationTest.NEW_AUTHORIZATION, AuthConfig.class);
        Assert.assertThat(config.size(), Matchers.is(3));
    }

    @Test
    public void shouldBeAbleToParseNewConfig() throws Exception {
        CruiseConfig newConfig = ConfigMigrator.loadWithMigration(AuthorizationMigrationTest.CONFIG_WITH_AUTH).config;
        Assert.assertThat(newConfig.stageConfigByName(new CaseInsensitiveString("pipeline1"), new CaseInsensitiveString("stage1")).getApproval().getAuthConfig().size(), Matchers.is(3));
    }

    @Test
    public void shouldBeAbleToWriteNewConfig() throws Exception {
        CruiseConfig newConfig = ConfigMigrator.loadWithMigration(AuthorizationMigrationTest.CONFIG_WITH_AUTH).config;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        new MagicalGoConfigXmlWriter(new ConfigCache(), ConfigElementImplementationRegistryMother.withNoPlugins()).write(newConfig, buffer, false);
        Assert.assertThat(new String(buffer.toByteArray()).replace("\r\n", "\n"), Matchers.containsString(AuthorizationMigrationTest.NEW_AUTHORIZATION));
    }
}

