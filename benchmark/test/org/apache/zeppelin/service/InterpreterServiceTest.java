/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.dep.DependencyResolver;
import org.apache.zeppelin.interpreter.InterpreterSettingManager;
import org.apache.zeppelin.rest.message.InterpreterInstallationRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class InterpreterServiceTest {
    @Mock
    private ZeppelinConfiguration mockZeppelinConfiguration;

    @Mock
    private InterpreterSettingManager mockInterpreterSettingManager;

    private Path temporaryDir;

    private Path interpreterDir;

    private Path localRepoDir;

    InterpreterService interpreterService;

    @Test(expected = Exception.class)
    public void invalidProxyUrl() throws Exception {
        Mockito.when(mockZeppelinConfiguration.getZeppelinProxyUrl()).thenReturn("invalidProxyPath");
        interpreterService.installInterpreter(new InterpreterInstallationRequest("name", "artifact"), null);
    }

    @Test(expected = Exception.class)
    public void interpreterAlreadyExist() throws Exception {
        String alreadyExistName = "aen";
        Path specificInterpreterDir = Files.createDirectory(Paths.get(interpreterDir.toString(), alreadyExistName));
        interpreterService.installInterpreter(new InterpreterInstallationRequest(alreadyExistName, "artifact"), null);
    }

    @Test(expected = Exception.class)
    public void interpreterAlreadyExistWithDifferentName() throws Exception {
        String interpreterName = "in";
        Files.createDirectory(Paths.get(interpreterDir.toString(), interpreterName));
        String anotherButSameInterpreterName = "zeppelin-" + interpreterName;
        interpreterService.installInterpreter(new InterpreterInstallationRequest(anotherButSameInterpreterName, "artifact"), null);
    }

    @Test
    public void downloadInterpreter() throws IOException {
        final String interpreterName = "test-interpreter";
        String artifactName = "junit:junit:4.11";
        Path specificInterpreterPath = Files.createDirectory(Paths.get(interpreterDir.toString(), interpreterName));
        DependencyResolver dependencyResolver = new DependencyResolver(localRepoDir.toString());
        Mockito.doNothing().when(mockInterpreterSettingManager).refreshInterpreterTemplates();
        interpreterService.downloadInterpreter(new InterpreterInstallationRequest(interpreterName, artifactName), dependencyResolver, specificInterpreterPath, new SimpleServiceCallback<String>() {
            @Override
            public void onStart(String message, ServiceContext context) {
                Assert.assertEquals((("Starting to download " + interpreterName) + " interpreter"), message);
            }

            @Override
            public void onSuccess(String message, ServiceContext context) {
                Assert.assertEquals((interpreterName + " downloaded"), message);
            }

            @Override
            public void onFailure(Exception ex, ServiceContext context) {
                Assert.fail();
            }
        });
        Mockito.verify(mockInterpreterSettingManager, Mockito.times(1)).refreshInterpreterTemplates();
    }
}

