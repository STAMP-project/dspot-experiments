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
package org.jdbi.v3.commonstext;


import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.rule.DatabaseRule;
import org.jdbi.v3.core.rule.SqliteDatabaseRule;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.config.UseTemplateEngine;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class TestUseStringSubstitutorTemplateEngine {
    @Rule
    public DatabaseRule db = new SqliteDatabaseRule().withPlugin(new SqlObjectPlugin());

    private Jdbi jdbi;

    @Test
    public void testUseTemplateEngine() {
        String selected = jdbi.withExtension(TestUseStringSubstitutorTemplateEngine.Queries1.class, ( q) -> q.select("foo"));
        assertThat(selected).isEqualTo("foo");
    }

    @Test
    public void testCustomAnnotation() {
        String selected = jdbi.withExtension(TestUseStringSubstitutorTemplateEngine.Queries2.class, ( q) -> q.select("foo"));
        assertThat(selected).isEqualTo("foo");
    }

    public interface Queries1 {
        @UseTemplateEngine(StringSubstitutorTemplateEngine.class)
        @SqlQuery("select * from (values('${v}'))")
        String select(@Define("v")
        String value);
    }

    public interface Queries2 {
        @UseStringSubstitutorTemplateEngine(prefix = "_", suffix = "_")
        @SqlQuery("select * from (values('_v_'))")
        String select(@Define("v")
        String value);
    }
}

