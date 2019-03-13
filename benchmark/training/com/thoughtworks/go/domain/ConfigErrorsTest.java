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


import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;


public class ConfigErrorsTest {
    @Test
    public void shouldGetAll() {
        ConfigErrors configErrors = new ConfigErrors();
        configErrors.add("err1", "foo");
        configErrors.add("err1", "baz");
        configErrors.add("err2", "bar");
        Assert.assertThat(configErrors.getAll(), JUnitMatchers.hasItems("foo", "baz", "bar"));
    }

    @Test
    public void shouldAddAll() {
        ConfigErrors configErrors = new ConfigErrors();
        configErrors.add("err1", "foo");
        configErrors.add("err1", "baz");
        configErrors.add("err2", "bar");
        ConfigErrors other = new ConfigErrors();
        other.add("baz", "one");
        other.addAll(configErrors);
        Assert.assertThat(other.getAll(), JUnitMatchers.hasItems("baz", "foo", "baz", "bar"));
    }

    @Test
    public void shouldNotAddSameErrorAgain() {
        ConfigErrors configErrors = new ConfigErrors();
        configErrors.add("field", "error");
        configErrors.add("field", "error");
        Assert.assertThat(configErrors.getAllOn("field"), Matchers.is(Arrays.asList("error")));
    }
}

