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


import DockerConstants.DOCKER_BEFORE;
import DockerConstants.DOCKER_LIMIT;
import DockerConstants.DOCKER_SHOW_ALL;
import DockerConstants.DOCKER_SHOW_SIZE;
import DockerConstants.DOCKER_SINCE;
import com.github.dockerjava.api.command.ListContainersCmd;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Validates List Containers Request headers are applied properly
 */
public class ListContainersCmdHeaderTest extends BaseDockerHeaderTest<ListContainersCmd> {
    @Mock
    private ListContainersCmd mockObject;

    @Test
    public void listContainerHeaderTest() {
        boolean showSize = true;
        boolean showAll = false;
        int limit = 2;
        String since = "id1";
        String before = "id2";
        Map<String, Object> headers = getDefaultParameters();
        headers.put(DOCKER_LIMIT, limit);
        headers.put(DOCKER_SHOW_ALL, showAll);
        headers.put(DOCKER_SHOW_SIZE, showSize);
        headers.put(DOCKER_SINCE, since);
        headers.put(DOCKER_BEFORE, before);
        template.sendBodyAndHeaders("direct:in", "", headers);
        Mockito.verify(dockerClient, Mockito.times(1)).listContainersCmd();
        Mockito.verify(mockObject, Mockito.times(1)).withShowAll(ArgumentMatchers.eq(showAll));
        Mockito.verify(mockObject, Mockito.times(1)).withShowSize(ArgumentMatchers.eq(showSize));
        Mockito.verify(mockObject, Mockito.times(1)).withLimit(ArgumentMatchers.eq(limit));
        Mockito.verify(mockObject, Mockito.times(1)).withSince(ArgumentMatchers.eq(since));
        Mockito.verify(mockObject, Mockito.times(1)).withBefore(ArgumentMatchers.eq(before));
    }
}

