/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
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
package org.apache.maven.cli.event;


import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class ExecutionEventLoggerTest {
    private ExecutionEventLogger executionEventLogger;

    @Test
    public void testProjectStarted() {
        // prepare
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(logger.isInfoEnabled()).thenReturn(true);
        executionEventLogger = new ExecutionEventLogger(logger);
        ExecutionEvent event = Mockito.mock(ExecutionEvent.class);
        MavenProject project = Mockito.mock(MavenProject.class);
        Mockito.when(project.getGroupId()).thenReturn("org.apache.maven");
        Mockito.when(project.getArtifactId()).thenReturn("maven-embedder");
        Mockito.when(project.getPackaging()).thenReturn("jar");
        Mockito.when(project.getName()).thenReturn("Apache Maven Embedder");
        Mockito.when(project.getVersion()).thenReturn("3.5.4-SNAPSHOT");
        Mockito.when(event.getProject()).thenReturn(project);
        // execute
        executionEventLogger.projectStarted(event);
        // verify
        InOrder inOrder = Mockito.inOrder(logger);
        inOrder.verify(logger).info("");
        inOrder.verify(logger).info("------------------< org.apache.maven:maven-embedder >-------------------");
        inOrder.verify(logger).info("Building Apache Maven Embedder 3.5.4-SNAPSHOT");
        inOrder.verify(logger).info("--------------------------------[ jar ]---------------------------------");
    }

    @Test
    public void testProjectStartedOverflow() {
        // prepare
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(logger.isInfoEnabled()).thenReturn(true);
        executionEventLogger = new ExecutionEventLogger(logger);
        ExecutionEvent event = Mockito.mock(ExecutionEvent.class);
        MavenProject project = Mockito.mock(MavenProject.class);
        Mockito.when(project.getGroupId()).thenReturn("org.apache.maven.plugins.overflow");
        Mockito.when(project.getArtifactId()).thenReturn("maven-project-info-reports-plugin");
        Mockito.when(project.getPackaging()).thenReturn("maven-plugin");
        Mockito.when(project.getName()).thenReturn("Apache Maven Project Info Reports Plugin");
        Mockito.when(project.getVersion()).thenReturn("3.0.0-SNAPSHOT");
        Mockito.when(event.getProject()).thenReturn(project);
        // execute
        executionEventLogger.projectStarted(event);
        // verify
        InOrder inOrder = Mockito.inOrder(logger);
        inOrder.verify(logger).info("");
        inOrder.verify(logger).info("--< org.apache.maven.plugins.overflow:maven-project-info-reports-plugin >--");
        inOrder.verify(logger).info("Building Apache Maven Project Info Reports Plugin 3.0.0-SNAPSHOT");
        inOrder.verify(logger).info("----------------------------[ maven-plugin ]----------------------------");
    }
}

