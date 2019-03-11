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


import com.thoughtworks.go.config.ConfigCache;
import com.thoughtworks.go.config.MagicalGoConfigXmlLoader;
import com.thoughtworks.go.config.MagicalGoConfigXmlWriter;
import com.thoughtworks.go.helper.ConfigFileFixture;
import java.io.ByteArrayOutputStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JobPropertiesTest {
    private MagicalGoConfigXmlLoader loader;

    private MagicalGoConfigXmlWriter writer;

    private ConfigCache configCache = new ConfigCache();

    @Test
    public void shouldLoadJobProperties() throws Exception {
        String jobXml = "<job name=\"dev\">\n" + (((("  <properties>\n" + "    <property name=\"coverage\" src=\"reports/emma.html\" xpath=\"//coverage/class\" />\n") + "    <property name=\"prop2\" src=\"test.xml\" xpath=\"//value\" />\n") + "  </properties>\n") + "</job>");
        CruiseConfig config = loader.loadConfigHolder(ConfigFileFixture.withJob(jobXml)).configForEdit;
        JobConfig jobConfig = config.pipelineConfigByName(new CaseInsensitiveString("pipeline1")).get(1).allBuildPlans().first();
        Assert.assertThat(jobConfig.getProperties().first(), Matchers.is(new ArtifactPropertyConfig("coverage", "reports/emma.html", "//coverage/class")));
        Assert.assertThat(jobConfig.getProperties().get(1), Matchers.is(new ArtifactPropertyConfig("prop2", "test.xml", "//value")));
    }

    @Test
    public void shouldWriteJobProperties() throws Exception {
        String jobXml = "<job name=\"dev\">\n" + (((("  <properties>\n" + "    <property name=\"coverage\" src=\"reports/emma.html\" xpath=\"//coverage/class\" />\n") + "    <property name=\"prop2\" src=\"test.xml\" xpath=\"//value\" />\n") + "  </properties>\n") + "</job>");
        CruiseConfig config = loader.loadConfigHolder(ConfigFileFixture.withJob(jobXml)).configForEdit;
        JobConfig jobConfig = config.pipelineConfigByName(new CaseInsensitiveString("pipeline1")).get(1).allBuildPlans().first();
        Assert.assertThat(writer.toXmlPartial(jobConfig), Matchers.is(jobXml));
    }

    @Test
    public void shouldNotLoadDuplicateJobProperties() throws Exception {
        String jobXml = "<job name=\"dev\">\n" + (((("<properties>\n" + "<property name=\"coverage\" src=\"reports/emma.html\" xpath=\"//coverage/class\" />\n") + "<property name=\"coverage\" src=\"test.xml\" xpath=\"//value\" />\n") + "</properties>\n") + "</job>");
        try {
            loadJobConfig(jobXml);
            Assert.fail("should not define two job properties with same name");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Duplicate unique value"));
        }
    }

    @Test
    public void shouldNotWriteDuplicateJobProperties() throws Exception {
        ArtifactPropertyConfig artifactPropertyConfig = new ArtifactPropertyConfig("coverage", "reports/emma.html", "//coverage/class");
        CruiseConfig cruiseConfig = cruiseConfigWithProperties(artifactPropertyConfig, artifactPropertyConfig);
        try {
            writer.write(cruiseConfig, new ByteArrayOutputStream(), false);
            Assert.fail("should not write two job properties with same name");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Duplicate unique value"));
        }
    }

    @Test
    public void shouldNotAllowEmptyJobProperties() throws Exception {
        String jobXml = "<job name=\"dev\">\n" + (("<properties>\n" + "</properties>\n") + "</job>");
        try {
            loadJobConfig(jobXml);
            Assert.fail("should not allow empty job properties");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("One of '{property}' is expected"));
        }
    }

    @Test
    public void shouldNotWriteEmptyJobProperties() throws Exception {
        CruiseConfig cruiseConfig = cruiseConfigWithProperties();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        writer.write(cruiseConfig, buffer, false);
        Assert.assertThat(new String(buffer.toByteArray()), Matchers.not(Matchers.containsString("properties")));
    }
}

