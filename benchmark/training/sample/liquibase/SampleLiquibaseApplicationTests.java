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
package sample.liquibase;


import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;


public class SampleLiquibaseApplicationTests {
    @Rule
    public final OutputCapture output = new OutputCapture();

    @Test
    public void testDefaultSettings() throws Exception {
        try {
            SampleLiquibaseApplication.main(new String[]{ "--server.port=0" });
        } catch (IllegalStateException ex) {
            if (serverNotRunning(ex)) {
                return;
            }
        }
        assertThat(this.output.toString()).contains("Successfully acquired change log lock").contains(("Creating database history " + "table with name: PUBLIC.DATABASECHANGELOG")).contains("Table person created").contains(("ChangeSet classpath:/db/" + ("changelog/db.changelog-master.yaml::1::" + "marceloverdijk ran successfully"))).contains("New row inserted into person").contains(("ChangeSet classpath:/db/changelog/" + ("db.changelog-master.yaml::2::" + "marceloverdijk ran successfully"))).contains("Successfully released change log lock");
    }
}

