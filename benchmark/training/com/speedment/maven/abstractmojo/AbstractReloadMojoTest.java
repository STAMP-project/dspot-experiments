/**
 * Copyright (c) 2006-2019, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.maven.abstractmojo;


import com.speedment.runtime.core.Speedment;
import com.speedment.tool.core.internal.util.ConfigFileHelper;
import java.io.File;
import java.nio.file.Paths;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class AbstractReloadMojoTest {
    private AbstractReloadMojoTestImpl mojo;

    private String mockedConfigLocation = "testFile.txt";

    @Mock
    private Speedment mockedSpeedment;

    @Mock
    private ConfigFileHelper mockedConfigFileHelper;

    @Mock
    private MavenProject mockedMavenProject;

    @Test
    public void execute() throws Exception {
        // Given
        Mockito.when(mockedMavenProject.getBasedir()).thenReturn(new File("baseDir"));
        Mockito.when(mockedSpeedment.getOrThrow(ConfigFileHelper.class)).thenReturn(mockedConfigFileHelper);
        mojo.setConfigFile(mockedConfigLocation);
        // When
        Assertions.assertDoesNotThrow(() -> mojo.execute(mockedSpeedment));
        // Then
        Mockito.verify(mockedConfigFileHelper).setCurrentlyOpenFile(Paths.get("baseDir", mockedConfigLocation).toFile());
        Mockito.verify(mockedConfigFileHelper).loadFromDatabaseAndSaveToFile();
    }

    @Test
    public void executeException() throws Exception {
        // Given
        Mockito.when(mockedMavenProject.getBasedir()).thenReturn(new File("baseDir"));
        Mockito.when(mockedSpeedment.getOrThrow(ConfigFileHelper.class)).thenReturn(mockedConfigFileHelper);
        Mockito.doThrow(new RuntimeException("test Exception")).when(mockedConfigFileHelper).loadFromDatabaseAndSaveToFile();
        mojo.setConfigFile(mockedConfigLocation);
        // When
        Assertions.assertThrows(MojoExecutionException.class, () -> mojo.execute(mockedSpeedment));
        // Then
    }
}

