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


import DockerConstants.DOCKER_TERM;
import com.github.dockerjava.api.command.SearchImagesCmd;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Validates Search Image Request headers are applied properly
 */
public class SearchImagesCmdHeaderTest extends BaseDockerHeaderTest<SearchImagesCmd> {
    @Mock
    private SearchImagesCmd mockObject;

    @Test
    public void searchImagesHeaderTest() {
        String term = "dockerTerm";
        Map<String, Object> headers = getDefaultParameters();
        headers.put(DOCKER_TERM, term);
        template.sendBodyAndHeaders("direct:in", "", headers);
        Mockito.verify(dockerClient, Mockito.times(1)).searchImagesCmd(ArgumentMatchers.eq(term));
    }
}

