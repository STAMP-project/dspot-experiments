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
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class StartServerCommandWorkingDirectoryTest {
    private String memberName;

    private String workingDirectory;

    private StartServerCommand startServerCommand;

    private ArgumentCaptor<String> workingDirectoryCaptor;

    @Test
    public void startLocatorWithRelativeWorkingDirectory() throws Exception {
        workingDirectory = "server1Directory";
        startServerCommand.startServer(memberName, false, null, null, null, 0.0F, 0.0F, workingDirectory, false, false, false, 0.0F, 0.0F, false, null, null, null, false, null, null, null, 0, false, null, 0, null, 0, 0, null, 0, 0, null, null, 0, null, null, 0, null, null, false, null, null, 0, 0, null, null, false, false, null, null, null, null, false);
        verifyDoStartServerInvoked();
        assertThat(workingDirectoryCaptor.getValue()).isEqualTo(new File(workingDirectory).getAbsolutePath());
    }

    @Test
    public void startServerWithNullWorkingDirectory() throws Exception {
        workingDirectory = null;
        startServerCommand.startServer(memberName, false, null, null, null, 0.0F, 0.0F, workingDirectory, false, false, false, 0.0F, 0.0F, false, null, null, null, false, null, null, null, 0, false, null, 0, null, 0, 0, null, 0, 0, null, null, 0, null, null, 0, null, null, false, null, null, 0, 0, null, null, false, false, null, null, null, null, false);
        verifyDoStartServerInvoked();
        assertThat(workingDirectoryCaptor.getValue()).isEqualTo(new File(memberName).getAbsolutePath());
    }

    @Test
    public void startServerWithEmptyWorkingDirectory() throws Exception {
        workingDirectory = "";
        startServerCommand.startServer(memberName, false, null, null, null, 0.0F, 0.0F, workingDirectory, false, false, false, 0.0F, 0.0F, false, null, null, null, false, null, null, null, 0, false, null, 0, null, 0, 0, null, 0, 0, null, null, 0, null, null, 0, null, null, false, null, null, 0, 0, null, null, false, false, null, null, null, null, false);
        verifyDoStartServerInvoked();
        assertThat(workingDirectoryCaptor.getValue()).isEqualTo(new File(memberName).getAbsolutePath());
    }

    @Test
    public void startServerWithDotWorkingDirectory() throws Exception {
        workingDirectory = ".";
        startServerCommand.startServer(memberName, false, null, null, null, 0.0F, 0.0F, workingDirectory, false, false, false, 0.0F, 0.0F, false, null, null, null, false, null, null, null, 0, false, null, 0, null, 0, 0, null, 0, 0, null, null, 0, null, null, 0, null, null, false, null, null, 0, 0, null, null, false, false, null, null, null, null, false);
        verifyDoStartServerInvoked();
        assertThat(workingDirectoryCaptor.getValue()).isEqualTo(StartMemberUtils.resolveWorkingDir(new File("."), new File(memberName)));
    }

    @Test
    public void startServerWithAbsoluteWorkingDirectory() throws Exception {
        workingDirectory = new File(System.getProperty("user.dir")).getAbsolutePath();
        startServerCommand.startServer(memberName, false, null, null, null, 0.0F, 0.0F, workingDirectory, false, false, false, 0.0F, 0.0F, false, null, null, null, false, null, null, null, 0, false, null, 0, null, 0, 0, null, 0, 0, null, null, 0, null, null, 0, null, null, false, null, null, 0, 0, null, null, false, false, null, null, null, null, false);
        verifyDoStartServerInvoked();
        assertThat(workingDirectoryCaptor.getValue()).isEqualTo(workingDirectory);
    }
}

