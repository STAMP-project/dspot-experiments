/**
 * Copyright 2012-2018 the original author or authors.
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
package sample.data.cassandra;


import java.io.File;
import org.cassandraunit.spring.CassandraDataSet;
import org.cassandraunit.spring.EmbeddedCassandra;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Tests for {@link SampleCassandraApplication}.
 */
@TestExecutionListeners(mergeMode = MergeMode.MERGE_WITH_DEFAULTS, listeners = { OrderedCassandraTestExecutionListener.class })
@RunWith(SpringRunner.class)
@SpringBootTest
@CassandraDataSet(keyspace = "mykeyspace", value = "setup.cql")
@EmbeddedCassandra(timeout = 60000)
public class SampleCassandraApplicationTests {
    @ClassRule
    public static final SampleCassandraApplicationTests.SkipOnWindows skipOnWindows = new SampleCassandraApplicationTests.SkipOnWindows();

    @ClassRule
    public static final OutputCapture output = new OutputCapture();

    @Test
    public void testDefaultSettings() {
        assertThat(SampleCassandraApplicationTests.output.toString()).contains("firstName='Alice', lastName='Smith'");
    }

    static class SkipOnWindows implements TestRule {
        @Override
        public Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    if (!(runningOnWindows())) {
                        base.evaluate();
                    }
                }

                private boolean runningOnWindows() {
                    return (File.separatorChar) == '\\';
                }
            };
        }
    }
}

