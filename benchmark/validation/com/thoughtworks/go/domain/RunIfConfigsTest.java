/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.domain;


import JobResult.Cancelled;
import JobResult.Failed;
import JobResult.Passed;
import com.thoughtworks.go.config.RunIfConfig;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class RunIfConfigsTest {
    @Test
    public void shouldMatchWhenContainsCondition() {
        RunIfConfigs configs = new RunIfConfigs(RunIfConfig.PASSED);
        Assert.assertThat(configs.match(RunIfConfig.fromJobResult(Passed.toLowerCase())), Matchers.is(true));
        Assert.assertThat(configs.match(RunIfConfig.fromJobResult(Failed.toLowerCase())), Matchers.is(false));
        Assert.assertThat(configs.match(RunIfConfig.fromJobResult(Cancelled.toLowerCase())), Matchers.is(false));
    }

    @Test
    public void shouldMatchAnyWhenAnyIsDefined() {
        RunIfConfigs configs = new RunIfConfigs(RunIfConfig.ANY);
        Assert.assertThat(configs.match(RunIfConfig.fromJobResult(Passed.toLowerCase())), Matchers.is(true));
        Assert.assertThat(configs.match(RunIfConfig.fromJobResult(Failed.toLowerCase())), Matchers.is(true));
        Assert.assertThat(configs.match(RunIfConfig.fromJobResult(Cancelled.toLowerCase())), Matchers.is(true));
    }

    @Test
    public void testOnlyMatchPassedWhenNoneIsDefined() {
        RunIfConfigs configs = new RunIfConfigs();
        Assert.assertThat(configs.match(RunIfConfig.fromJobResult(Passed.toLowerCase())), Matchers.is(true));
        Assert.assertThat(configs.match(RunIfConfig.fromJobResult(Failed.toLowerCase())), Matchers.is(false));
        Assert.assertThat(configs.match(RunIfConfig.fromJobResult(Cancelled.toLowerCase())), Matchers.is(false));
    }

    @Test
    public void shouldAddErrorsToErrorCollectionOfTheCollectionAsWellAsEachRunIfConfig() {
        RunIfConfigs configs = new RunIfConfigs();
        RunIfConfig config = new RunIfConfig("passed");
        config.addError("status", "some error");
        configs.add(config);
        configs.addError("key", "some error");
        Assert.assertThat(configs.errors().on("key"), Matchers.is("some error"));
        Assert.assertThat(configs.get(0).errors().on("status"), Matchers.is("some error"));
    }
}

