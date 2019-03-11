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
package com.thoughtworks.go.domain.materials.tfs;


import com.thoughtworks.go.config.materials.SubprocessExecutionContext;
import com.thoughtworks.go.util.command.UrlArgument;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TfsCommandFactoryTest {
    private SubprocessExecutionContext executionContext;

    private TfsCommandFactory tfsCommandFactory;

    private String materialFingerprint = "fingerprint";

    private String computedWorkspaceName = "boo-yaa-goo-moo-foo";

    private String DOMAIN = "domain";

    private String USERNAME = "userName";

    private String PROJECT_PATH = "$/project";

    private final String PASSWORD = "password";

    private final UrlArgument URL = new UrlArgument("url");

    @Test
    public void shouldReturnSdkCommand() throws Exception {
        TfsCommand expectedTfsCommand = Mockito.mock(TfsCommand.class);
        TfsCommandFactory spyCommandFactory = Mockito.spy(tfsCommandFactory);
        TfsSDKCommandBuilder commandBuilder = Mockito.mock(TfsSDKCommandBuilder.class);
        Mockito.doReturn(commandBuilder).when(spyCommandFactory).getSDKBuilder();
        Mockito.when(commandBuilder.buildTFSSDKCommand("fingerprint", URL, DOMAIN, USERNAME, PASSWORD, computedWorkspaceName, PROJECT_PATH)).thenReturn(expectedTfsCommand);
        TfsCommand actualTfsCommand = spyCommandFactory.create(executionContext, URL, DOMAIN, USERNAME, PASSWORD, "fingerprint", PROJECT_PATH);
        Assert.assertThat(actualTfsCommand, Matchers.is(expectedTfsCommand));
        Mockito.verify(commandBuilder).buildTFSSDKCommand("fingerprint", URL, DOMAIN, USERNAME, PASSWORD, computedWorkspaceName, PROJECT_PATH);
    }

    @Test
    public void shouldReturnExecutionContextsProcessNamespace() {
        String fingerprint = "material-fingerprint";
        Mockito.when(executionContext.getProcessNamespace(fingerprint)).thenReturn("workspace-name");
        Assert.assertThat(executionContext.getProcessNamespace(fingerprint), Matchers.is("workspace-name"));
        Mockito.verify(executionContext, Mockito.times(1)).getProcessNamespace(fingerprint);
    }

    @Test
    public void shouldPassFingerPrintAlongWithExecutionContextWhenCreatingTfsCommand() {
        String fingerprint = "fingerprint";
        Mockito.when(executionContext.getProcessNamespace(materialFingerprint)).thenReturn(fingerprint);
        TfsCommandFactory spy = Mockito.spy(tfsCommandFactory);
        TfsSDKCommandBuilder commandBuilder = Mockito.mock(TfsSDKCommandBuilder.class);
        Mockito.doReturn(commandBuilder).when(spy).getSDKBuilder();
        TfsCommand tfsCommand = Mockito.mock(TfsCommand.class);
        Mockito.when(commandBuilder.buildTFSSDKCommand(materialFingerprint, URL, DOMAIN, USERNAME, PASSWORD, fingerprint, PROJECT_PATH)).thenReturn(tfsCommand);
        spy.create(executionContext, URL, DOMAIN, USERNAME, PASSWORD, materialFingerprint, PROJECT_PATH);
        Mockito.verify(executionContext).getProcessNamespace(materialFingerprint);
        Mockito.verify(commandBuilder).buildTFSSDKCommand(materialFingerprint, URL, DOMAIN, USERNAME, PASSWORD, fingerprint, PROJECT_PATH);
    }
}

