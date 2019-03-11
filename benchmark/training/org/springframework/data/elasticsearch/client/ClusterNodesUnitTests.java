/**
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.elasticsearch.client;


import org.junit.Test;

import static ClusterNodes.DEFAULT;


/**
 * Unit tests for {@link ClusterNodes}.
 *
 * @author Oliver Gierke
 */
public class ClusterNodesUnitTests {
    // DATAES-470
    @Test
    public void parsesSingleClusterNode() {
        ClusterNodes nodes = DEFAULT;
        // 
        assertThat(nodes).hasSize(1).first().satisfies(( it) -> {
            assertThat(it.getAddress()).isEqualTo("127.0.0.1");
            assertThat(it.getPort()).isEqualTo(9300);
        });
    }

    // DATAES-470
    @Test
    public void parsesMultiClusterNode() {
        ClusterNodes nodes = ClusterNodes.of("127.0.0.1:1234,10.1.0.1:5678");
        assertThat(nodes.stream()).hasSize(2);// 

        assertThat(nodes.stream()).element(0).satisfies(( it) -> {
            assertThat(it.getAddress()).isEqualTo("127.0.0.1");
            assertThat(it.getPort()).isEqualTo(1234);
        });
        assertThat(nodes.stream()).element(1).satisfies(( it) -> {
            assertThat(it.getAddress()).isEqualTo("10.1.0.1");
            assertThat(it.getPort()).isEqualTo(5678);
        });
    }

    // DATAES-470
    @Test
    public void rejectsEmptyHostName() {
        // 
        // 
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> ClusterNodes.of(":8080")).withMessageContaining("host");
    }

    // DATAES-470
    @Test
    public void rejectsEmptyPort() {
        // 
        // 
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> ClusterNodes.of("localhost:")).withMessageContaining("port");
    }

    // DATAES-470
    @Test
    public void rejectsMissingPort() {
        // 
        // 
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> ClusterNodes.of("localhost")).withMessageContaining("host:port");
    }

    // DATAES-470
    @Test
    public void rejectsUnresolvableHost() {
        // 
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> ClusterNodes.of("mylocalhost:80"));
    }
}

