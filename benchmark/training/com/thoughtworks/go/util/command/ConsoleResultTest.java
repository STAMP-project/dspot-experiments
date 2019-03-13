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
package com.thoughtworks.go.util.command;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ConsoleResultTest {
    @Test
    @SuppressWarnings({ "ThrowableInstanceNeverThrown" })
    public void shouldSmudgeExceptionMessagesForNestedExceptions() {
        List<CommandArgument> args = Arrays.asList(new StringArgument("foo"), new PasswordArgument("bar"));
        List<SecretString> secrets = Arrays.asList(((SecretString) (new PasswordArgument("quux"))));
        ConsoleResult result = new ConsoleResult(0, Arrays.asList(" foo ", " bar ", " baz ", " abc "), Arrays.asList(" quux ", " bang "), args, secrets);
        Exception innerException = new Exception("baz quux baz");
        Exception topException = new RuntimeException("foo bar abc", innerException);
        Exception exception = result.smudgedException(topException);
        Assert.assertThat(exception.getMessage(), is("foo ****** abc"));
        Assert.assertThat(exception, sameInstance(topException));
        Assert.assertThat(exception.getCause().getMessage(), is("baz ****** baz"));
        Assert.assertThat(exception.getCause(), sameInstance(innerException));
    }

    @Test
    public void shouldReplaceSecretInfoShouldNotFailForNull() {
        ArrayList<CommandArgument> commands = new ArrayList<>();
        commands.add(new PasswordArgument("foo"));
        ArrayList<SecretString> secretStrings = new ArrayList<>();
        secretStrings.add(new PasswordArgument("foo"));
        ConsoleResult result = new ConsoleResult(10, new ArrayList(), new ArrayList(), commands, secretStrings);
        Assert.assertThat(result.replaceSecretInfo(null), is(nullValue()));
    }

    @Test
    public void shouldDescribeResult() {
        List<CommandArgument> args = Arrays.asList(new StringArgument("foo"), new PasswordArgument("bar"));
        List<SecretString> secrets = Arrays.asList(((SecretString) (new PasswordArgument("quux"))));
        ConsoleResult result = new ConsoleResult(42, Arrays.asList(" foo ", " bar ", " baz ", " abc "), Arrays.asList(" quux ", " bang "), args, secrets);
        Assert.assertThat(result.describe(), containsString("--- EXIT CODE (42) ---"));
        Assert.assertThat(result.describe(), containsString("--- STANDARD OUT ---"));
        Assert.assertThat(result.describe(), containsString("--- STANDARD ERR ---"));
    }
}

