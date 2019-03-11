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
package com.thoughtworks.go.plugin.infra;


import com.thoughtworks.go.plugin.infra.commons.GoFileSystem;
import com.thoughtworks.go.plugin.infra.commons.PluginUploadResponse;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.File;
import java.io.IOException;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PluginWriterTest {
    @Mock
    private SystemEnvironment systemEnvironment;

    @Mock
    private GoFileSystem goFileSystem;

    @InjectMocks
    private PluginWriter pluginWriter = new PluginWriter(systemEnvironment, goFileSystem);

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private String EXTERNAL_DIRECTORY_PATH = "external_path";

    private File srcFile;

    @Test
    public void shouldConstructCorrectDestinationFilePath() throws Exception {
        Mockito.when(systemEnvironment.get(PLUGIN_EXTERNAL_PROVIDED_PATH)).thenReturn(EXTERNAL_DIRECTORY_PATH);
        pluginWriter.addPlugin(srcFile, srcFile.getName());
        ArgumentCaptor<File> srcfileArgumentCaptor = ArgumentCaptor.forClass(File.class);
        ArgumentCaptor<File> destfileArgumentCaptor = ArgumentCaptor.forClass(File.class);
        Mockito.verify(goFileSystem).copyFile(srcfileArgumentCaptor.capture(), destfileArgumentCaptor.capture());
        Assert.assertThat(srcfileArgumentCaptor.getValue(), Matchers.is(srcFile));
        Assert.assertThat(destfileArgumentCaptor.getValue().getName(), Matchers.is(new File((((EXTERNAL_DIRECTORY_PATH) + "/") + (srcFile.getName()))).getName()));
    }

    @Test
    public void shouldReturnSuccessResponseWhenSuccessfullyUploadedFile() throws Exception {
        Mockito.when(systemEnvironment.get(PLUGIN_EXTERNAL_PROVIDED_PATH)).thenReturn(EXTERNAL_DIRECTORY_PATH);
        PluginUploadResponse response = pluginWriter.addPlugin(srcFile, srcFile.getName());
        Assert.assertTrue(response.isSuccess());
        Assert.assertThat(response.success(), Matchers.is("Your file is saved!"));
    }

    @Test
    public void shouldReturnErrorResponseWhenFailedToUploadFile() throws Exception {
        Mockito.when(systemEnvironment.get(PLUGIN_EXTERNAL_PROVIDED_PATH)).thenReturn(EXTERNAL_DIRECTORY_PATH);
        Mockito.doThrow(new IOException()).when(goFileSystem).copyFile(ArgumentMatchers.any(File.class), ArgumentMatchers.any(File.class));
        PluginUploadResponse response = pluginWriter.addPlugin(srcFile, srcFile.getName());
        Assert.assertFalse(response.isSuccess());
        Assert.assertTrue(response.errors().containsKey(HttpStatus.SC_INTERNAL_SERVER_ERROR));
    }
}

