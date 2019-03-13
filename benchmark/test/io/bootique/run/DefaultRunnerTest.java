/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.run;


import io.bootique.cli.Cli;
import io.bootique.command.Command;
import io.bootique.command.CommandOutcome;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultRunnerTest {
    private Cli mockCli;

    @Test
    public void testRun() {
        Mockito.when(mockCli.commandName()).thenReturn("c1");
        Command mockDefault = DefaultRunnerTest.mockCommand("d1", CommandOutcome.succeeded());
        Command mockHelp = DefaultRunnerTest.mockCommand("h1", CommandOutcome.succeeded());
        Command mockC1 = DefaultRunnerTest.mockCommand("c1", CommandOutcome.succeeded(), "c1o1", "c1o2");
        Command mockC2 = DefaultRunnerTest.mockCommand("c2", CommandOutcome.succeeded(), "c2o1");
        CommandOutcome result = run(Optional.of(mockDefault), Optional.of(mockHelp), mockC1, mockC2);
        Assert.assertTrue(result.isSuccess());
        Mockito.verify(mockC1).run(mockCli);
        Mockito.verify(mockC2, Mockito.times(0)).run(mockCli);
        Mockito.verify(mockDefault, Mockito.times(0)).run(mockCli);
        Mockito.verify(mockHelp, Mockito.times(0)).run(mockCli);
    }

    @Test
    public void testRun_ReverseOrder() {
        Mockito.when(mockCli.commandName()).thenReturn("c2");
        Command mockDefault = DefaultRunnerTest.mockCommand("d1", CommandOutcome.succeeded());
        Command mockHelp = DefaultRunnerTest.mockCommand("h1", CommandOutcome.succeeded());
        Command mockC1 = DefaultRunnerTest.mockCommand("c1", CommandOutcome.succeeded(), "c1o1", "c1o2");
        Command mockC2 = DefaultRunnerTest.mockCommand("c2", CommandOutcome.succeeded(), "c2o1");
        CommandOutcome result = run(Optional.of(mockDefault), Optional.of(mockHelp), mockC1, mockC2);
        Assert.assertTrue(result.isSuccess());
        Mockito.verify(mockC2).run(mockCli);
        Mockito.verify(mockC1, Mockito.times(0)).run(mockCli);
        Mockito.verify(mockDefault, Mockito.times(0)).run(mockCli);
        Mockito.verify(mockHelp, Mockito.times(0)).run(mockCli);
    }

    @Test(expected = IllegalStateException.class)
    public void testRun_NoMatch() {
        Mockito.when(mockCli.commandName()).thenReturn("c3");
        Command mockDefault = DefaultRunnerTest.mockCommand("d1", CommandOutcome.succeeded());
        Command mockHelp = DefaultRunnerTest.mockCommand("h1", CommandOutcome.succeeded());
        Command mockC1 = DefaultRunnerTest.mockCommand("c1", CommandOutcome.succeeded(), "c1o1", "c1o2");
        Command mockC2 = DefaultRunnerTest.mockCommand("c2", CommandOutcome.succeeded(), "c2o1");
        run(Optional.of(mockDefault), Optional.of(mockHelp), mockC1, mockC2);
    }

    @Test
    public void testRun_NullName_Default() {
        Mockito.when(mockCli.commandName()).thenReturn(null);
        Command mockDefault = DefaultRunnerTest.mockCommand("d1", CommandOutcome.succeeded());
        Command mockHelp = DefaultRunnerTest.mockCommand("h1", CommandOutcome.succeeded());
        Command mockC1 = DefaultRunnerTest.mockCommand("c1", CommandOutcome.succeeded(), "c1o1", "c1o2");
        Command mockC2 = DefaultRunnerTest.mockCommand("c2", CommandOutcome.succeeded(), "c2o1");
        CommandOutcome result = run(Optional.of(mockDefault), Optional.of(mockHelp), mockC1, mockC2);
        Assert.assertTrue(result.isSuccess());
        Mockito.verify(mockC1, Mockito.times(0)).run(mockCli);
        Mockito.verify(mockC2, Mockito.times(0)).run(mockCli);
        Mockito.verify(mockDefault).run(mockCli);
        Mockito.verify(mockHelp, Mockito.times(0)).run(mockCli);
    }

    @Test
    public void testRun_NullName_Help() {
        Mockito.when(mockCli.commandName()).thenReturn(null);
        Command mockHelp = DefaultRunnerTest.mockCommand("h1", CommandOutcome.succeeded());
        Command mockC1 = DefaultRunnerTest.mockCommand("c1", CommandOutcome.succeeded(), "c1o1", "c1o2");
        Command mockC2 = DefaultRunnerTest.mockCommand("c2", CommandOutcome.succeeded(), "c2o1");
        CommandOutcome result = run(Optional.empty(), Optional.of(mockHelp), mockC1, mockC2);
        Assert.assertTrue(result.isSuccess());
        Mockito.verify(mockC1, Mockito.times(0)).run(mockCli);
        Mockito.verify(mockC2, Mockito.times(0)).run(mockCli);
        Mockito.verify(mockHelp).run(mockCli);
    }

    @Test
    public void testRun_NullName_NoFallback() {
        Mockito.when(mockCli.commandName()).thenReturn(null);
        Command mockC1 = DefaultRunnerTest.mockCommand("c1", CommandOutcome.succeeded(), "c1o1", "c1o2");
        Command mockC2 = DefaultRunnerTest.mockCommand("c2", CommandOutcome.succeeded(), "c2o1");
        CommandOutcome result = run(Optional.empty(), Optional.empty(), mockC1, mockC2);
        Assert.assertTrue(result.isSuccess());
        Mockito.verify(mockC1, Mockito.times(0)).run(mockCli);
        Mockito.verify(mockC2, Mockito.times(0)).run(mockCli);
    }

    @Test
    public void testRun_Failure() {
        Mockito.when(mockCli.commandName()).thenReturn("c1");
        Command mockC1 = DefaultRunnerTest.mockCommand("c1", CommandOutcome.failed((-1), "fff"), "c1o1", "c1o2");
        Command mockC2 = DefaultRunnerTest.mockCommand("c2", CommandOutcome.succeeded(), "c2o1");
        CommandOutcome result = run(Optional.empty(), Optional.empty(), mockC1, mockC2);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals((-1), result.getExitCode());
        Assert.assertEquals("fff", result.getMessage());
    }
}

