/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config;


import GoConfigMigration.UPGRADE;
import com.thoughtworks.go.domain.GoConfigRevision;
import com.thoughtworks.go.service.ConfigRepository;
import com.thoughtworks.go.util.GoConstants;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class GoConfigMigrationTest {
    private GoConfigMigration goConfigMigration;

    private ConfigRepository configRepo;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    public static final String OLDER_VERSION_XML = (((((((((((((((((((("<cruise schemaVersion='" + ((GoConstants.CONFIG_SCHEMA_VERSION) - 1)) + "\' >\n") + "<server artifactsdir='artifactsDir' >") + "</server>") + "<pipelines group='foo'>") + "  <pipeline name='pipeline1'>") + "    <materials>") + "      <svn url='svnurl'/>") + "    </materials>") + "    <stage name='mingle'>") + "      <jobs>") + "        <job name='do-something'>") + "         <tasks>") + "              <exec command=\"tools/jruby/bin/jruby\" args=\"-S buildr kill_server\" />") + "         </tasks>") + "        </job>") + "      </jobs>") + "    </stage>") + "  </pipeline>") + "</pipelines>") + "</cruise>";

    @Test
    public void shouldCommitConfig_WithUsername_Upgrade() throws Exception {
        File file = temporaryFolder.newFile("my-config.xml");
        FileUtils.writeStringToFile(file, GoConfigMigrationTest.OLDER_VERSION_XML, StandardCharsets.UTF_8);
        final GoConfigRevision[] commitMade = new GoConfigRevision[1];
        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws Throwable {
                commitMade[0] = ((GoConfigRevision) (invocation.getArguments()[0]));
                return null;
            }
        }).when(configRepo).checkin(ArgumentMatchers.any(GoConfigRevision.class));
        goConfigMigration.upgradeIfNecessary(file, null);
        Assert.assertThat(commitMade[0].getUsername(), Matchers.is(UPGRADE));
    }
}

