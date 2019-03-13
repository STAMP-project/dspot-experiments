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
package org.jdbi.v3.core.mapper;


import CaseStrategy.LOCALE_LOWER;
import CaseStrategy.LOCALE_UPPER;
import CaseStrategy.NOP;
import java.util.Map;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.SqliteDatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestMapMapper {
    // h2 makes column names uppercase
    @Rule
    public SqliteDatabaseRule db = new SqliteDatabaseRule();

    private Handle h;

    @Test
    public void testCaseDefaultNop() {
        h.getConfig(MapMappers.class).setCaseChange(NOP);
        Map<String, Object> noOne = h.createQuery("select * from Foo").mapToMap().findOnly();
        assertThat(noOne).containsOnlyKeys("Id", "FirstName");
    }

    @Test
    public void testCaseLower() {
        h.getConfig(MapMappers.class).setCaseChange(LOCALE_LOWER);
        Map<String, Object> noOne = h.createQuery("select * from Foo").mapToMap().findOnly();
        assertThat(noOne).containsOnlyKeys("id", "firstname");
    }

    @Test
    public void testCaseUpper() {
        h.getConfig(MapMappers.class).setCaseChange(LOCALE_UPPER);
        Map<String, Object> noOne = h.createQuery("select * from Foo").mapToMap().findOnly();
        assertThat(noOne).containsOnlyKeys("ID", "FIRSTNAME");
    }
}

