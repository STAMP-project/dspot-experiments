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
package org.apache.flink.container.entrypoint;


import java.util.Optional;
import java.util.Properties;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.entrypoint.FlinkParseException;
import org.apache.flink.runtime.entrypoint.parser.CommandLineParser;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link StandaloneJobClusterConfigurationParserFactory}.
 */
public class StandaloneJobClusterConfigurationParserFactoryTest extends TestLogger {
    private static final CommandLineParser<StandaloneJobClusterConfiguration> commandLineParser = new CommandLineParser(new StandaloneJobClusterConfigurationParserFactory());

    private static final String JOB_CLASS_NAME = "foobar";

    private static final String CONFIG_DIR = "/foo/bar";

    @Test
    public void testEntrypointClusterConfigurationParsing() throws FlinkParseException {
        final String key = "key";
        final String value = "value";
        final int restPort = 1234;
        final String arg1 = "arg1";
        final String arg2 = "arg2";
        final String[] args = new String[]{ "--configDir", StandaloneJobClusterConfigurationParserFactoryTest.CONFIG_DIR, "--webui-port", String.valueOf(restPort), "--job-classname", StandaloneJobClusterConfigurationParserFactoryTest.JOB_CLASS_NAME, String.format("-D%s=%s", key, value), arg1, arg2 };
        final StandaloneJobClusterConfiguration clusterConfiguration = StandaloneJobClusterConfigurationParserFactoryTest.commandLineParser.parse(args);
        Assert.assertThat(clusterConfiguration.getConfigDir(), Matchers.is(Matchers.equalTo(StandaloneJobClusterConfigurationParserFactoryTest.CONFIG_DIR)));
        Assert.assertThat(clusterConfiguration.getJobClassName(), Matchers.is(Matchers.equalTo(StandaloneJobClusterConfigurationParserFactoryTest.JOB_CLASS_NAME)));
        Assert.assertThat(clusterConfiguration.getRestPort(), Matchers.is(Matchers.equalTo(restPort)));
        final Properties dynamicProperties = clusterConfiguration.getDynamicProperties();
        Assert.assertThat(dynamicProperties, Matchers.hasEntry(key, value));
        Assert.assertThat(clusterConfiguration.getArgs(), Matchers.arrayContaining(arg1, arg2));
        Assert.assertThat(clusterConfiguration.getSavepointRestoreSettings(), Matchers.is(Matchers.equalTo(SavepointRestoreSettings.none())));
        Assert.assertThat(clusterConfiguration.getJobId(), Matchers.is(Matchers.equalTo(StandaloneJobClusterConfigurationParserFactory.DEFAULT_JOB_ID)));
    }

    @Test
    public void testOnlyRequiredArguments() throws FlinkParseException {
        final String configDir = "/foo/bar";
        final String[] args = new String[]{ "--configDir", configDir };
        final StandaloneJobClusterConfiguration clusterConfiguration = StandaloneJobClusterConfigurationParserFactoryTest.commandLineParser.parse(args);
        Assert.assertThat(clusterConfiguration.getConfigDir(), Matchers.is(Matchers.equalTo(configDir)));
        Assert.assertThat(clusterConfiguration.getDynamicProperties(), Matchers.is(Matchers.equalTo(new Properties())));
        Assert.assertThat(clusterConfiguration.getArgs(), Matchers.is(new String[0]));
        Assert.assertThat(clusterConfiguration.getRestPort(), Matchers.is(Matchers.equalTo((-1))));
        Assert.assertThat(clusterConfiguration.getHostname(), Matchers.is(IsNull.nullValue()));
        Assert.assertThat(clusterConfiguration.getSavepointRestoreSettings(), Matchers.is(Matchers.equalTo(SavepointRestoreSettings.none())));
        Assert.assertThat(clusterConfiguration.getJobId(), Matchers.is(Matchers.not(IsNull.nullValue())));
        Assert.assertThat(clusterConfiguration.getJobClassName(), Matchers.is(IsNull.nullValue()));
    }

    @Test(expected = FlinkParseException.class)
    public void testMissingRequiredArgument() throws FlinkParseException {
        final String[] args = new String[]{  };
        StandaloneJobClusterConfigurationParserFactoryTest.commandLineParser.parse(args);
    }

    @Test
    public void testSavepointRestoreSettingsParsing() throws FlinkParseException {
        final String restorePath = "foobar";
        final String[] args = new String[]{ "-c", StandaloneJobClusterConfigurationParserFactoryTest.CONFIG_DIR, "-j", StandaloneJobClusterConfigurationParserFactoryTest.JOB_CLASS_NAME, "-s", restorePath, "-n" };
        final StandaloneJobClusterConfiguration standaloneJobClusterConfiguration = StandaloneJobClusterConfigurationParserFactoryTest.commandLineParser.parse(args);
        final SavepointRestoreSettings savepointRestoreSettings = standaloneJobClusterConfiguration.getSavepointRestoreSettings();
        Assert.assertThat(savepointRestoreSettings.restoreSavepoint(), Matchers.is(true));
        Assert.assertThat(savepointRestoreSettings.getRestorePath(), Matchers.is(Matchers.equalTo(restorePath)));
        Assert.assertThat(savepointRestoreSettings.allowNonRestoredState(), Matchers.is(true));
    }

    @Test
    public void testSetJobIdManually() throws FlinkParseException {
        final JobID jobId = new JobID();
        final String[] args = new String[]{ "--configDir", "/foo/bar", "--job-classname", "foobar", "--job-id", jobId.toString() };
        final StandaloneJobClusterConfiguration standaloneJobClusterConfiguration = StandaloneJobClusterConfigurationParserFactoryTest.commandLineParser.parse(args);
        Assert.assertThat(standaloneJobClusterConfiguration.getJobId(), Matchers.is(Matchers.equalTo(jobId)));
    }

    @Test
    public void testInvalidJobIdThrows() {
        final String invalidJobId = "0xINVALID";
        final String[] args = new String[]{ "--configDir", "/foo/bar", "--job-classname", "foobar", "--job-id", invalidJobId };
        try {
            StandaloneJobClusterConfigurationParserFactoryTest.commandLineParser.parse(args);
            Assert.fail("Did not throw expected FlinkParseException");
        } catch (FlinkParseException e) {
            Optional<IllegalArgumentException> cause = ExceptionUtils.findThrowable(e, IllegalArgumentException.class);
            Assert.assertTrue(cause.isPresent());
            Assert.assertThat(cause.get().getMessage(), Matchers.containsString(invalidJobId));
        }
    }

    @Test
    public void testShortOptions() throws FlinkParseException {
        final String configDir = "/foo/bar";
        final String jobClassName = "foobar";
        final JobID jobId = new JobID();
        final String savepointRestorePath = "s3://foo/bar";
        final String[] args = new String[]{ "-c", configDir, "-j", jobClassName, "-jid", jobId.toString(), "-s", savepointRestorePath, "-n" };
        final StandaloneJobClusterConfiguration clusterConfiguration = StandaloneJobClusterConfigurationParserFactoryTest.commandLineParser.parse(args);
        Assert.assertThat(clusterConfiguration.getConfigDir(), Matchers.is(Matchers.equalTo(configDir)));
        Assert.assertThat(clusterConfiguration.getJobClassName(), Matchers.is(Matchers.equalTo(jobClassName)));
        Assert.assertThat(clusterConfiguration.getJobId(), Matchers.is(Matchers.equalTo(jobId)));
        final SavepointRestoreSettings savepointRestoreSettings = clusterConfiguration.getSavepointRestoreSettings();
        Assert.assertThat(savepointRestoreSettings.restoreSavepoint(), Matchers.is(true));
        Assert.assertThat(savepointRestoreSettings.getRestorePath(), Matchers.is(Matchers.equalTo(savepointRestorePath)));
        Assert.assertThat(savepointRestoreSettings.allowNonRestoredState(), Matchers.is(true));
    }
}

