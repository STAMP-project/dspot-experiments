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
package org.jdbi.v3.sqlobject;


import java.sql.Types;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.rule.PgDatabaseRule;
import org.jdbi.v3.core.statement.OutParameters;
import org.jdbi.v3.sqlobject.customizer.OutParameter;
import org.jdbi.v3.sqlobject.statement.SqlCall;
import org.junit.Rule;
import org.junit.Test;


public class TestOutParameterAnnotation {
    @Rule
    public PgDatabaseRule dbRule = new PgDatabaseRule().withPlugin(new SqlObjectPlugin());

    private Jdbi db;

    @Test
    public void testOutParameter() {
        TestOutParameterAnnotation.MyDao myDao = db.onDemand(TestOutParameterAnnotation.MyDao.class);
        OutParameters outParameters = myDao.callStoredProc();
        assertThat(outParameters.getInt("outparam")).isEqualTo(100);
    }

    @Test
    public void testMultipleOutParameters() {
        TestOutParameterAnnotation.MyDao myDao = db.onDemand(TestOutParameterAnnotation.MyDao.class);
        OutParameters outParameters = myDao.callMultipleOutParameters(1, 9);
        assertThat(outParameters.getInt("c")).isEqualTo(9);
        assertThat(outParameters.getInt("d")).isEqualTo(1);
    }

    public interface MyDao {
        @SqlCall("{call set100(:outparam)}")
        @OutParameter(name = "outparam", sqlType = Types.INTEGER)
        OutParameters callStoredProc();

        @SqlCall("{call swap(:a, :b, :c, :d)}")
        @OutParameter(name = "c", sqlType = Types.INTEGER)
        @OutParameter(name = "d", sqlType = Types.INTEGER)
        OutParameters callMultipleOutParameters(int a, int b);
    }
}

