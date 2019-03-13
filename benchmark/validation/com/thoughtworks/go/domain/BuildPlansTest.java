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


import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.JobConfigs;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class BuildPlansTest {
    @Test
    public void shouldFindBuildPlanByName() {
        JobConfigs jobConfigs = new JobConfigs();
        jobConfigs.add(jobConfig("Test"));
        Assert.assertThat(jobConfigs.containsName(new CaseInsensitiveString("Poo")), Matchers.is(false));
    }

    @Test
    public void shouldNotBombIfABuildPlanWithSameNameIsSetAgain() throws Exception {
        JobConfigs jobConfigs = new JobConfigs();
        jobConfigs.add(jobConfig("Test"));
        jobConfigs.set(0, jobConfig("Test"));
    }

    @Test
    public void shouldBombIfABuildPlanWithSameNameIsAdded() throws Exception {
        JobConfigs jobConfigs = new JobConfigs();
        jobConfigs.add(jobConfig("Test"));
        try {
            jobConfigs.add(jobConfig("Test"));
            Assert.fail("Should not be able to add build plan with the same name again");
        } catch (RuntimeException ignored) {
        }
    }

    @Test
    public void shouldBombIfABuildPlanWithSameNameWithDifferentCaseIsAdded() throws Exception {
        JobConfigs jobConfigs = new JobConfigs();
        jobConfigs.add(jobConfig("Test"));
        try {
            jobConfigs.add(jobConfig("test"));
            Assert.fail("Should not be able to add build plan with the same name again");
        } catch (RuntimeException ignored) {
        }
    }
}

