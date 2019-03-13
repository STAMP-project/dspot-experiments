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
package com.thoughtworks.go.plugin.api.task;


import com.thoughtworks.go.plugin.api.config.Property;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TaskConfigTest {
    private TaskConfig taskConfig;

    @Test
    public void shouldAddPropertyWithGiveName() throws Exception {
        String abcd = "Abcd";
        String abcdDefault = "first of alphabets";
        String wxyz = "wxyz";
        String wxyzDefault = "last of alphabets";
        taskConfig.addProperty(wxyz).withDefault(wxyzDefault);
        taskConfig.addProperty(abcd).withDefault(abcdDefault);
        List<? extends Property> properties = taskConfig.list();
        Assert.assertThat(properties.size(), Matchers.is(2));
        for (Property property : properties) {
            Assert.assertThat((property != null), Matchers.is(true));
            Assert.assertThat((property instanceof TaskConfigProperty), Matchers.is(true));
        }
        Assert.assertThat(((taskConfig.get(abcd)) != null), Matchers.is(true));
        Assert.assertThat(taskConfig.get(abcd).getValue(), Matchers.is(abcdDefault));
        Assert.assertThat(((taskConfig.get(wxyz)) != null), Matchers.is(true));
        Assert.assertThat(taskConfig.get(wxyz).getValue(), Matchers.is(wxyzDefault));
    }

    @Test
    public void shouldSortTheProperties() {
        TaskConfigProperty k1 = getTaskConfigProperty(3);
        TaskConfigProperty k2 = getTaskConfigProperty(0);
        TaskConfigProperty k3 = getTaskConfigProperty(2);
        TaskConfigProperty k4 = getTaskConfigProperty(1);
        taskConfig.add(k1);
        taskConfig.add(k2);
        taskConfig.add(k3);
        taskConfig.add(k4);
        Assert.assertThat(taskConfig.list().get(0), Matchers.is(k2));
        Assert.assertThat(taskConfig.list().get(1), Matchers.is(k4));
        Assert.assertThat(taskConfig.list().get(2), Matchers.is(k3));
        Assert.assertThat(taskConfig.list().get(3), Matchers.is(k1));
    }
}

