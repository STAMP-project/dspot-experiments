/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.domain;


import com.thoughtworks.go.config.ValidationContext;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class KillAllChildProcessTaskTest {
    private ValidationContext validationContext;

    @Test
    public void shouldReturnDefaultsForCancelTaskAndGetConditions() throws Exception {
        KillAllChildProcessTask processTask = new KillAllChildProcessTask();
        Task actual = processTask.cancelTask();
        Assert.assertThat(actual, Matchers.is(Matchers.instanceOf(NullTask.class)));
        Assert.assertThat(processTask.getConditions().size(), Matchers.is(0));
    }

    @Test
    public void shouldNotAllowSettingOfConfigAttributes() throws Exception {
        KillAllChildProcessTask processTask = new KillAllChildProcessTask();
        try {
            processTask.setConfigAttributes(new HashMap());
            Assert.fail("should have failed, as configuration of kill-all task is not allowed");
        } catch (UnsupportedOperationException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Not a configurable task"));
        }
    }

    @Test
    public void validateShouldReturnNoErrors() throws Exception {
        KillAllChildProcessTask processTask = new KillAllChildProcessTask();
        processTask.validate(validationContext);
        Assert.assertThat(processTask.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldKnowItsType() {
        Assert.assertThat(new KillAllChildProcessTask().getTaskType(), Matchers.is("killallchildprocess"));
    }

    @Test
    public void shouldReturnEmptyPropertiesForDisplay() {
        Assert.assertThat(new KillAllChildProcessTask().getPropertiesForDisplay().isEmpty(), Matchers.is(true));
    }
}

