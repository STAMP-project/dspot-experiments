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
package com.thoughtworks.go.presentation.environment;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class EnvironmentPipelineModelTest {
    @Test
    public void shouldUnderstandWhenAssociatedWithGivenEnvironment() {
        EnvironmentPipelineModel foo = new EnvironmentPipelineModel("foo", "env");
        Assert.assertThat(foo.isAssociatedWithEnvironment("env"), Matchers.is(true));
        Assert.assertThat(foo.isAssociatedWithEnvironment("env2"), Matchers.is(false));
        Assert.assertThat(foo.isAssociatedWithEnvironment(null), Matchers.is(false));
        foo = new EnvironmentPipelineModel("foo");
        Assert.assertThat(foo.isAssociatedWithEnvironment("env"), Matchers.is(false));
        Assert.assertThat(foo.isAssociatedWithEnvironment("env2"), Matchers.is(false));
        Assert.assertThat(foo.isAssociatedWithEnvironment(null), Matchers.is(false));
    }

    @Test
    public void shouldUnderstandWhenAssiciatedWithADifferentEnvironment() {
        EnvironmentPipelineModel foo = new EnvironmentPipelineModel("foo", "env");
        Assert.assertThat(foo.isAssociatedWithEnvironmentOtherThan("env"), Matchers.is(false));
        Assert.assertThat(foo.isAssociatedWithEnvironmentOtherThan("env2"), Matchers.is(true));
        Assert.assertThat(foo.isAssociatedWithEnvironmentOtherThan(null), Matchers.is(true));
        foo = new EnvironmentPipelineModel("foo");
        Assert.assertThat(foo.isAssociatedWithEnvironmentOtherThan("env"), Matchers.is(false));
        Assert.assertThat(foo.isAssociatedWithEnvironmentOtherThan("env2"), Matchers.is(false));
        Assert.assertThat(foo.isAssociatedWithEnvironmentOtherThan(null), Matchers.is(false));
    }

    @Test
    public void hasEnvironmentAssociated_shouldReturnTrueWhenAPipelineIsAsscoiatedWithAnEnvironment() {
        EnvironmentPipelineModel foo = new EnvironmentPipelineModel("foo");
        Assert.assertThat(foo.hasEnvironmentAssociated(), Matchers.is(false));
    }

    @Test
    public void hasEnvironmentAssociated_shouldReturnFalseWhenAPipelineIsNotAsscoiatedWithAnEnvironment() {
        EnvironmentPipelineModel foo = new EnvironmentPipelineModel("foo", "env-name");
        Assert.assertThat(foo.hasEnvironmentAssociated(), Matchers.is(true));
    }

    @Test
    public void shouldSortOnPipelineName() {
        EnvironmentPipelineModel foo = new EnvironmentPipelineModel("foo", "env-name");
        EnvironmentPipelineModel bar = new EnvironmentPipelineModel("bar");
        EnvironmentPipelineModel baz = new EnvironmentPipelineModel("baz");
        List<EnvironmentPipelineModel> models = Arrays.asList(foo, bar, baz);
        Collections.sort(models);
        Assert.assertThat(models.get(0), Matchers.is(bar));
        Assert.assertThat(models.get(1), Matchers.is(baz));
        Assert.assertThat(models.get(2), Matchers.is(foo));
    }

    @Test
    public void shouldIgnorecaseWhileSortingOnPipelineName() {
        EnvironmentPipelineModel first = new EnvironmentPipelineModel("first");
        EnvironmentPipelineModel First = new EnvironmentPipelineModel("First", "env-name");
        EnvironmentPipelineModel Third = new EnvironmentPipelineModel("Third");
        EnvironmentPipelineModel second = new EnvironmentPipelineModel("second");
        List<EnvironmentPipelineModel> models = Arrays.asList(first, First, Third, second);
        Collections.sort(models);
        Assert.assertThat(models.get(0), Matchers.is(first));
        Assert.assertThat(models.get(1), Matchers.is(First));
        Assert.assertThat(models.get(2), Matchers.is(second));
        Assert.assertThat(models.get(3), Matchers.is(Third));
    }
}

