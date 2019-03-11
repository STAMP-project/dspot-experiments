/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.commands;


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ExportStackTraceCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ExportStackTraceCommand command;

    @Test
    public void noMemberFound() {
        Mockito.doReturn(Collections.emptySet()).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        ExportStackTraceCommandTest.gfsh.executeAndAssertThat(command, "export stack-traces").statusIsError().containsOutput("No Members Found");
    }

    @Test
    public void abortIfFileExists() throws IOException {
        File file = temporaryFolder.newFile("stackTrace.txt");
        ExportStackTraceCommandTest.gfsh.executeAndAssertThat(command, ("export stack-traces --abort-if-file-exists --file=" + (file.getAbsolutePath()))).statusIsError().containsOutput("already present");
        // try again without the flag, the command should continue after the check
        Mockito.doReturn(Collections.emptySet()).when(command).findMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
        ExportStackTraceCommandTest.gfsh.executeAndAssertThat(command, ("export stack-traces --file=" + (file.getAbsolutePath()))).statusIsError().containsOutput("No Members Found");
    }
}

