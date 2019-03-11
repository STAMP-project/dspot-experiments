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
package com.thoughtworks.go.buildsession;


import com.thoughtworks.go.domain.BuildCommand;
import com.thoughtworks.go.domain.JobResult;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ExportCommandExecutorTest extends BuildSessionBasedTestCase {
    @Test
    public void exportEnvironmentVariableHasMeaningfulOutput() throws Exception {
        runBuild(BuildCommand.compose(BuildCommand.export("answer", "2", false), BuildCommand.export("answer", "42", false)), JobResult.Passed);
        Assert.assertThat(console.asList().get(0), Matchers.is("[go] setting environment variable 'answer' to value '2'"));
        Assert.assertThat(console.asList().get(1), Matchers.is("[go] overriding environment variable 'answer' with value '42'"));
    }

    @Test
    public void exportOutputWhenOverridingSystemEnv() throws Exception {
        String envName = pathSystemEnvName();
        runBuild(BuildCommand.export(envName, "/foo/bar", false), JobResult.Passed);
        Assert.assertThat(console.output(), Matchers.is(String.format("[go] overriding environment variable '%s' with value '/foo/bar'", envName)));
    }

    @Test
    public void exportSecretEnvShouldMaskValue() throws Exception {
        runBuild(BuildCommand.export("answer", "42", true), JobResult.Passed);
        Assert.assertThat(console.output(), Matchers.is("[go] setting environment variable 'answer' to value '********'"));
    }

    @Test
    public void exportWithoutValueDisplayCurrentValue() throws Exception {
        runBuild(BuildCommand.export("foo"), JobResult.Passed);
        Assert.assertThat(console.lastLine(), Matchers.is("[go] setting environment variable 'foo' to value 'null'"));
        runBuild(BuildCommand.compose(BuildCommand.export("foo", "bar", false), BuildCommand.export("foo")), JobResult.Passed);
        Assert.assertThat(console.lastLine(), Matchers.is("[go] setting environment variable 'foo' to value 'bar'"));
    }
}

