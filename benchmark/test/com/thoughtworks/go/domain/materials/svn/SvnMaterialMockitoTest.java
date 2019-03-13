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
package com.thoughtworks.go.domain.materials.svn;


import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.domain.materials.TestSubprocessExecutionContext;
import com.thoughtworks.go.util.command.InMemoryStreamConsumer;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import com.thoughtworks.go.util.command.UrlArgument;
import java.io.File;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class SvnMaterialMockitoTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    SubversionRevision revision = new SubversionRevision("1");

    private InMemoryStreamConsumer outputStreamConsumer = ProcessOutputStreamConsumer.inMemoryConsumer();

    @Test
    public void shouldNotDeleteWorkingDirIfSvnRepositoryUsesFileProtocol() throws IOException {
        Subversion subversion = Mockito.mock(Subversion.class);
        Mockito.when(subversion.getUserName()).thenReturn("");
        Mockito.when(subversion.getPassword()).thenReturn("");
        Mockito.when(subversion.isCheckExternals()).thenReturn(false);
        File workingCopy = createSvnWorkingCopy(true);
        Mockito.when(subversion.workingRepositoryUrl(workingCopy)).thenReturn(workingCopy.getPath());
        String url = "file://" + (workingCopy.getPath());
        Mockito.when(subversion.getUrl()).thenReturn(new UrlArgument(url));
        SvnMaterial svnMaterial = SvnMaterial.createSvnMaterialWithMock(subversion);
        svnMaterial.setUrl(url);
        svnMaterial.updateTo(outputStreamConsumer, workingCopy, new com.thoughtworks.go.domain.materials.RevisionContext(revision), new TestSubprocessExecutionContext());
        Assert.assertThat(workingCopy.exists(), Matchers.is(true));
        Mockito.verify(subversion).updateTo(outputStreamConsumer, workingCopy, revision);
    }
}

