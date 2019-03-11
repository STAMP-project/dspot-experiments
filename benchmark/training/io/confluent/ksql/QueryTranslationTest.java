/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Runs the json functional tests defined under
 *  `ksql-engine/src/test/resources/query-validation-tests`.
 *
 *  See `ksql-engine/src/test/resources/query-validation-tests/README.md` for more info.
 */
@RunWith(Parameterized.class)
public class QueryTranslationTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Path QUERY_VALIDATION_TEST_DIR = Paths.get("query-validation-tests");

    private static final String TOPOLOGY_CHECKS_DIR = "expected_topology/";

    private static final String TOPOLOGY_VERSIONS_DELIMITER = ",";

    private static final String TOPOLOGY_VERSIONS_PROP = "topology.versions";

    private final EndToEndEngineTestUtil.TestCase testCase;

    /**
     *
     *
     * @param name
     * 		- unused. Is just so the tests get named.
     * @param testCase
     * 		- testCase to run.
     */
    @SuppressWarnings("unused")
    public QueryTranslationTest(final String name, final EndToEndEngineTestUtil.TestCase testCase) {
        this.testCase = Objects.requireNonNull(testCase, "testCase");
    }

    @Test
    public void shouldBuildAndExecuteQueries() {
        EndToEndEngineTestUtil.shouldBuildAndExecuteQuery(testCase);
    }

    private static final class MissingFieldException extends RuntimeException {
        private MissingFieldException(final String fieldName) {
            super((("test it must define '" + fieldName) + "' field"));
        }
    }

    private static final class InvalidFieldException extends RuntimeException {
        private InvalidFieldException(final String fieldName, final String reason) {
            super(((("'" + fieldName) + "': ") + reason));
        }

        private InvalidFieldException(final String fieldName, final String reason, final Throwable cause) {
            super(((fieldName + ": ") + reason), cause);
        }
    }

    private static class TopologiesAndVersion {
        private final String version;

        private final Map<String, EndToEndEngineTestUtil.TopologyAndConfigs> topologies;

        TopologiesAndVersion(final String version, final Map<String, EndToEndEngineTestUtil.TopologyAndConfigs> topologies) {
            this.version = version;
            this.topologies = topologies;
        }

        String getVersion() {
            return version;
        }

        EndToEndEngineTestUtil.TopologyAndConfigs getTopology(final String name) {
            return topologies.get(name);
        }
    }
}

