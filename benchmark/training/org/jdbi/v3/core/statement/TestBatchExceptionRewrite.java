/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.statement;


import org.jdbi.v3.core.rule.PgDatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestBatchExceptionRewrite {
    @Rule
    public PgDatabaseRule dbRule = new PgDatabaseRule();

    @Test
    public void testSimpleBatch() {
        Batch b = dbRule.openHandle().createBatch();
        b.add("insert into something (id, name) values (0, 'Keith')");
        b.add("insert into something (id, name) values (0, 'Keith')");
        assertThatExceptionOfType(UnableToExecuteStatementException.class).isThrownBy(b::execute).satisfies(( e) -> assertSuppressions(e.getCause()));
    }

    @Test
    public void testPreparedBatch() {
        PreparedBatch b = dbRule.openHandle().prepareBatch("insert into something (id, name) values (?,?)");
        b.add(0, "a");
        b.add(0, "a");
        assertThatExceptionOfType(UnableToExecuteStatementException.class).isThrownBy(b::execute).satisfies(( e) -> assertSuppressions(e.getCause()));
    }
}

