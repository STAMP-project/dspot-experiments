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
package com.thoughtworks.go.util.command;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PasswordArgumentTest {
    private CommandArgument argument = new PasswordArgument("secret");

    @Test
    public void shouldReturnStringValueForCommandLine() throws Exception {
        Assert.assertThat(argument.forCommandline(), Matchers.is("secret"));
    }

    @Test
    public void shouldReturnStringValueForReporting() throws Exception {
        Assert.assertThat(argument.forDisplay(), Matchers.is("******"));
    }

    @Test
    public void shouldReturnValueForToString() throws Exception {
        Assert.assertThat(argument.toString(), Matchers.is("******"));
    }

    @Test
    public void shouldReturnSampNumberOfStarsForAnyPassword() throws Exception {
        Assert.assertThat(new PasswordArgument("foo").toString(), Matchers.is("******"));
        Assert.assertThat(new PasswordArgument("very very long password").toString(), Matchers.is("******"));
    }
}

