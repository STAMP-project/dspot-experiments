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
package com.thoughtworks.go.config.materials;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FilterTest {
    @Test
    public void shouldReturnEmptyTextToDisplayWhenFilterIsEmpty() {
        Assert.assertThat(new Filter().getStringForDisplay(), Matchers.is(""));
    }

    @Test
    public void shouldConcatenateIgnoredFilesWithCommaWhenDisplaying() {
        Filter filter = new Filter(new IgnoredFiles("/foo/**.*"), new IgnoredFiles("/another/**.*"), new IgnoredFiles("bar"));
        Assert.assertThat(filter.getStringForDisplay(), Matchers.is("/foo/**.*,/another/**.*,bar"));
    }

    @Test
    public void shouldInitializeFromDisplayString() {
        Assert.assertThat(Filter.fromDisplayString("/foo/**.*,/another/**.*,bar"), Matchers.is(new Filter(new IgnoredFiles("/foo/**.*"), new IgnoredFiles("/another/**.*"), new IgnoredFiles("bar"))));
        Assert.assertThat(Filter.fromDisplayString("/foo/**.* , /another/**.*,     bar     "), Matchers.is(new Filter(new IgnoredFiles("/foo/**.*"), new IgnoredFiles("/another/**.*"), new IgnoredFiles("bar"))));
    }

    @Test
    public void shouldAddErrorToItsErrorCollection() {
        IgnoredFiles ignore = new IgnoredFiles("helper/*.*");
        Filter filter = new Filter(ignore);
        filter.addError("key", "some error");
        Assert.assertThat(filter.errors().on("key"), Matchers.is("some error"));
    }
}

