/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server.quorum;


import ServerStats.Provider;
import java.io.StringWriter;
import org.apache.zookeeper.server.command.StatCommand;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class StatCommandTest {
    private StringWriter outputWriter;

    private StatCommand statCommand;

    private Provider providerMock;

    @Test
    public void testLeaderStatCommand() {
        // Arrange
        Mockito.when(providerMock.getState()).thenReturn("leader");
        // Act
        statCommand.commandRun();
        // Assert
        String output = outputWriter.toString();
        assertCommonStrings(output);
        Assert.assertThat(output, CoreMatchers.containsString("Mode: leader"));
        Assert.assertThat(output, CoreMatchers.containsString("Proposal sizes last/min/max:"));
    }

    @Test
    public void testFollowerStatCommand() {
        // Arrange
        Mockito.when(providerMock.getState()).thenReturn("follower");
        // Act
        statCommand.commandRun();
        // Assert
        String output = outputWriter.toString();
        assertCommonStrings(output);
        Assert.assertThat(output, CoreMatchers.containsString("Mode: follower"));
    }
}

