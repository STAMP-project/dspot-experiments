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
package org.apache.geode.connectors.jdbc.internal.cli;


import CreateMappingCommand.Interceptor;
import MappingConstants.PDX_CLASS_FILE;
import Result.Status.ERROR;
import Result.Status.OK;
import java.io.File;
import java.io.IOException;
import org.apache.geode.management.cli.Result;
import org.apache.geode.management.internal.cli.GfshParseResult;
import org.apache.geode.management.internal.cli.result.FileResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class CreateMappingCommandInterceptorTest {
    private final Interceptor interceptor = new CreateMappingCommand.Interceptor();

    private GfshParseResult gfshParseResult = Mockito.mock(GfshParseResult.class);

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void preExecutionGivenNullPdxClassFileReturnsOK() {
        Mockito.when(gfshParseResult.getParamValue(PDX_CLASS_FILE)).thenReturn(null);
        Result result = interceptor.preExecution(gfshParseResult);
        assertThat(result.getStatus()).isEqualTo(OK);
    }

    @Test
    public void preExecutionGivenNonExistingPdxClassFileReturnsError() {
        Mockito.when(gfshParseResult.getParamValue(PDX_CLASS_FILE)).thenReturn("NonExistingFile");
        Result result = interceptor.preExecution(gfshParseResult);
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.nextLine()).contains("NonExistingFile not found.");
    }

    @Test
    public void preExecutionGivenDirectoryAsPdxClassFileReturnsError() throws IOException {
        File tempFolder = testFolder.newFolder("tempFolder");
        Mockito.when(gfshParseResult.getParamValue(PDX_CLASS_FILE)).thenReturn(tempFolder.getAbsolutePath());
        Result result = interceptor.preExecution(gfshParseResult);
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.nextLine()).contains(((tempFolder.getAbsolutePath()) + " is not a file."));
    }

    @Test
    public void preExecutionGivenFileWithoutExtensionAsPdxClassFileReturnsError() throws IOException {
        File tempFile = testFolder.newFile("tempFile");
        Mockito.when(gfshParseResult.getParamValue(PDX_CLASS_FILE)).thenReturn(tempFile.getAbsolutePath());
        Result result = interceptor.preExecution(gfshParseResult);
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.nextLine()).contains(((tempFile.getAbsolutePath()) + " must end with \".jar\" or \".class\"."));
    }

    @Test
    public void preExecutionGivenClassFileAsPdxClassFileReturnsOK() throws IOException {
        File tempFile = testFolder.newFile("tempFile.class");
        Mockito.when(gfshParseResult.getParamValue(PDX_CLASS_FILE)).thenReturn(tempFile.getAbsolutePath());
        Result result = interceptor.preExecution(gfshParseResult);
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result).isInstanceOf(FileResult.class);
        FileResult fileResult = ((FileResult) (result));
        assertThat(fileResult.getFiles()).containsExactly(tempFile);
    }

    @Test
    public void preExecutionGivenJarFileAsPdxClassFileReturnsOK() throws IOException {
        File tempFile = testFolder.newFile("tempFile.jar");
        Mockito.when(gfshParseResult.getParamValue(PDX_CLASS_FILE)).thenReturn(tempFile.getAbsolutePath());
        Result result = interceptor.preExecution(gfshParseResult);
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result).isInstanceOf(FileResult.class);
        FileResult fileResult = ((FileResult) (result));
        assertThat(fileResult.getFiles()).containsExactly(tempFile);
    }
}

