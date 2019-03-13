/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.docker.headers;


import DockerConstants.DOCKER_EMAIL;
import DockerConstants.DOCKER_NAME;
import DockerConstants.DOCKER_PASSWORD;
import DockerConstants.DOCKER_SERVER_ADDRESS;
import DockerConstants.DOCKER_TAG;
import DockerConstants.DOCKER_USERNAME;
import com.github.dockerjava.api.command.PushImageCmd;
import com.github.dockerjava.core.command.PushImageResultCallback;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Validates Push Image Request headers are applied properly
 */
public class PushImageCmdHeaderTest extends BaseDockerHeaderTest<PushImageCmd> {
    @Mock
    private PushImageCmd mockObject;

    @Mock
    private PushImageResultCallback callback;

    private String userName = "jdoe";

    private String password = "password";

    private String email = "jdoe@example.com";

    private String serverAddress = "http://docker.io/v1";

    private String name = "imagename";

    private String tag = "1.0";

    @Test
    public void pushImageHeaderTest() {
        Map<String, Object> headers = getDefaultParameters();
        headers.put(DOCKER_USERNAME, userName);
        headers.put(DOCKER_PASSWORD, password);
        headers.put(DOCKER_EMAIL, email);
        headers.put(DOCKER_SERVER_ADDRESS, serverAddress);
        headers.put(DOCKER_NAME, name);
        headers.put(DOCKER_TAG, tag);
        template.sendBodyAndHeaders("direct:in", "", headers);
        Mockito.verify(dockerClient, Mockito.times(1)).pushImageCmd(name);
        Mockito.verify(mockObject, Mockito.times(1)).withTag(tag);
    }
}

