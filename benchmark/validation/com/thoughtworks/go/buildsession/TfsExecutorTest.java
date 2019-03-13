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
package com.thoughtworks.go.buildsession;


import com.thoughtworks.go.config.materials.SubprocessExecutionContext;
import com.thoughtworks.go.config.materials.tfs.TfsMaterial;
import com.thoughtworks.go.domain.BuildCommand;
import com.thoughtworks.go.domain.materials.RevisionContext;
import com.thoughtworks.go.util.command.ConsoleOutputStreamConsumer;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TfsExecutorTest {
    private TfsMaterial tfsMaterial;

    private BuildCommand buildCommand;

    private BuildSession buildSession;

    private File workingDir;

    @Test
    public void shouldUpdateTheTfsMaterial() throws Exception {
        TfsExecutor tfsExecutor = new TfsExecutor() {
            @Override
            protected TfsMaterial createMaterial(String url, String username, String password, String domain, String projectPath) {
                Assert.assertThat(url, Matchers.is("some url"));
                Assert.assertThat(username, Matchers.is("username"));
                Assert.assertThat(password, Matchers.is("password"));
                Assert.assertThat(domain, Matchers.is("domain"));
                Assert.assertThat(projectPath, Matchers.is("project path"));
                return tfsMaterial;
            }
        };
        boolean result = tfsExecutor.execute(buildCommand, buildSession);
        ArgumentCaptor<RevisionContext> revisionCaptor = ArgumentCaptor.forClass(RevisionContext.class);
        ArgumentCaptor<File> workingDirCaptor = ArgumentCaptor.forClass(File.class);
        Mockito.verify(tfsMaterial).updateTo(ArgumentMatchers.any(ConsoleOutputStreamConsumer.class), workingDirCaptor.capture(), revisionCaptor.capture(), ArgumentMatchers.any(SubprocessExecutionContext.class));
        Assert.assertThat(revisionCaptor.getValue().getLatestRevision().getRevision(), Matchers.is("revision1"));
        Assert.assertThat(workingDirCaptor.getValue(), Matchers.is(workingDir));
        Assert.assertThat(result, Matchers.is(true));
    }
}

