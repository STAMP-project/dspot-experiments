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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class SchemaTranslationTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Path SCHEMA_VALIDATION_TEST_DIR = Paths.get("schema-validation-tests");

    private static final String TOPIC_NAME = "TEST_INPUT";

    private static final String OUTPUT_TOPIC_NAME = "TEST_OUTPUT";

    private final EndToEndEngineTestUtil.TestCase testCase;

    @SuppressWarnings("unused")
    public SchemaTranslationTest(final String name, final EndToEndEngineTestUtil.TestCase testCase) {
        this.testCase = testCase;
    }

    @Test
    public void shouldBuildAndExecuteQueries() {
        EndToEndEngineTestUtil.shouldBuildAndExecuteQuery(testCase);
    }
}

