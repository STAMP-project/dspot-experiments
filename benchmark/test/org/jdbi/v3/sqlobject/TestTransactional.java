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


import TransactionIsolationLevel.SERIALIZABLE;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.transaction.TransactionException;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.jdbi.v3.sqlobject.transaction.Transactional;
import org.junit.Test;


public class TestTransactional {
    private Jdbi db;

    private Handle handle;

    private final AtomicBoolean inTransaction = new AtomicBoolean();

    public interface TheBasics extends Transactional<TestTransactional.TheBasics> {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        @Transaction(TransactionIsolationLevel.SERIALIZABLE)
        int insert(@BindBean
        Something something);
    }

    @Test
    public void testDoublyTransactional() {
        final TestTransactional.TheBasics dao = db.onDemand(TestTransactional.TheBasics.class);
        dao.inTransaction(SERIALIZABLE, ( transactional) -> {
            transactional.insert(new Something(1, "2"));
            inTransaction.set(true);
            transactional.insert(new Something(2, "3"));
            inTransaction.set(false);
            return null;
        });
    }

    @Test
    public void testOnDemandBeginTransaction() {
        // Calling methods like begin() on an on-demand Transactional SQL object makes no sense--the transaction would
        // begin and the connection would just close.
        // Jdbi should identify this scenario and throw an exception informing the user that they're not managing their
        // transactions correctly.
        assertThatThrownBy(db.onDemand(Transactional.class)::begin).isInstanceOf(TransactionException.class);
    }

    private static final Set<Method> CHECKED_METHODS;

    static {
        try {
            CHECKED_METHODS = ImmutableSet.of(Connection.class.getMethod("setTransactionIsolation", int.class));
        } catch (final Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private class TxnIsolationCheckingInvocationHandler implements InvocationHandler {
        private final Connection real;

        TxnIsolationCheckingInvocationHandler(Connection real) {
            this.real = real;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ((TestTransactional.CHECKED_METHODS.contains(method)) && (inTransaction.get())) {
                throw new SQLException("PostgreSQL would not let you set the transaction isolation here");
            }
            return method.invoke(real, args);
        }
    }
}

