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
package com.thoughtworks.go.remote.work.artifact;


import ConsoleLogMessage.LogLevel.ERROR;
import ConsoleLogMessage.LogLevel.INFO;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ConsoleLogMessageTest {
    @Test
    public void shouldDeserializeJsonWithInfoLogLevel() {
        final ConsoleLogMessage consoleLogMessage = ConsoleLogMessage.fromJSON("{\"logLevel\":\"INFO\",\"message\":\"This is info message.\"}");
        Assert.assertNotNull(consoleLogMessage);
        Assert.assertThat(consoleLogMessage.getLogLevel(), Matchers.is(INFO));
        Assert.assertThat(consoleLogMessage.getMessage(), Matchers.is("This is info message."));
    }

    @Test
    public void shouldDeserializeJsonWithErrorLogLevel() {
        final ConsoleLogMessage consoleLogMessage = ConsoleLogMessage.fromJSON("{\"logLevel\":\"ERROR\",\"message\":\"This is error.\"}");
        Assert.assertNotNull(consoleLogMessage);
        Assert.assertThat(consoleLogMessage.getLogLevel(), Matchers.is(ERROR));
        Assert.assertThat(consoleLogMessage.getMessage(), Matchers.is("This is error."));
    }
}

