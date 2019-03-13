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


import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.statement.UnableToCreateStatementException;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Test;


public class TestReentrancy {
    private Jdbi db;

    private Handle handle;

    private interface TheBasics extends SqlObject {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        int insert(@BindBean
        Something something);
    }

    @Test
    public void testGetHandleProvidesSeperateHandle() {
        final TestReentrancy.TheBasics dao = db.onDemand(TestReentrancy.TheBasics.class);
        Handle h = getHandle();
        assertThatThrownBy(() -> h.execute("insert into something (id, name) values (1, 'Stephen')")).isInstanceOf(UnableToCreateStatementException.class);
    }

    @Test
    public void testHandleReentrant() {
        final TestReentrancy.TheBasics dao = db.onDemand(TestReentrancy.TheBasics.class);
        withHandle(( handle1) -> {
            dao.insert(new Something(7, "Martin"));
            handle1.createQuery("SELECT 1").mapToMap().list();
            return null;
        });
    }

    @Test
    public void testTxnReentrant() {
        final TestReentrancy.TheBasics dao = db.onDemand(TestReentrancy.TheBasics.class);
        withHandle(( handle1) -> {
            handle1.useTransaction(( h) -> {
                dao.insert(new Something(1, "x"));
                List<String> rs = h.createQuery("select name from something where id = 1").mapTo(.class).list();
                assertThat(rs).hasSize(1);
                h.createQuery("SELECT 1").mapTo(.class).list();
            });
            return null;
        });
    }
}

