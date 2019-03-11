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


import java.sql.Connection;
import java.util.concurrent.atomic.AtomicReference;
import org.jdbi.v3.core.ConnectionException;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Test;


public class TestNewApiOnDbiAndHandle {
    private Jdbi db;

    private Handle handle;

    @Test
    public void testOpenNewSpiffy() throws Exception {
        final AtomicReference<Connection> c = new AtomicReference<>();
        db.useExtension(TestNewApiOnDbiAndHandle.Spiffy.class, ( spiffy) -> {
            spiffy.insert(new Something(1, "Tim"));
            spiffy.insert(new Something(2, "Diego"));
            assertThat(spiffy.findNameById(2)).isEqualTo("Diego");
            c.set(spiffy.getHandle().getConnection());
        });
        assertThat(c.get().isClosed()).isTrue();
    }

    @Test
    public void testOnDemandSpiffy() {
        TestNewApiOnDbiAndHandle.Spiffy spiffy = db.onDemand(TestNewApiOnDbiAndHandle.Spiffy.class);
        spiffy.insert(new Something(1, "Tim"));
        spiffy.insert(new Something(2, "Diego"));
        assertThat(spiffy.findNameById(2)).isEqualTo("Diego");
    }

    @Test
    public void testAttach() {
        TestNewApiOnDbiAndHandle.Spiffy spiffy = handle.attach(TestNewApiOnDbiAndHandle.Spiffy.class);
        spiffy.insert(new Something(1, "Tim"));
        spiffy.insert(new Something(2, "Diego"));
        assertThat(spiffy.findNameById(2)).isEqualTo("Diego");
    }

    @Test
    public void testCorrectExceptionIfUnableToConnectOnDemand() {
        assertThatThrownBy(() -> Jdbi.create("jdbc:mysql://invalid.invalid/test", "john", "scott").installPlugin(new SqlObjectPlugin()).onDemand(.class).findNameById(1)).isInstanceOf(ConnectionException.class);
    }

    @Test
    public void testCorrectExceptionIfUnableToConnectOnOpen() {
        assertThatThrownBy(() -> Jdbi.create("jdbc:mysql://invalid.invalid/test", "john", "scott").installPlugin(new SqlObjectPlugin()).open().attach(.class)).isInstanceOf(ConnectionException.class);
    }

    @Test
    public void testCorrectExceptionIfUnableToConnectOnAttach() {
        assertThatThrownBy(() -> Jdbi.create("jdbc:mysql://invalid.invalid/test", "john", "scott").installPlugin(new SqlObjectPlugin()).open().attach(.class)).isInstanceOf(ConnectionException.class);
    }

    public interface Spiffy extends SqlObject {
        @SqlUpdate("insert into something (id, name) values (:it.id, :it.name)")
        void insert(@BindSomething("it")
        Something s);

        @SqlQuery("select name from something where id = :id")
        String findNameById(@Bind("id")
        int id);
    }
}

