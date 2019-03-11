/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.vibur;


import java.util.concurrent.ConcurrentMap;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.vibur.dbcp.ViburDBCPDataSource;
import org.vibur.dbcp.stcache.StatementHolder;
import org.vibur.dbcp.stcache.StatementMethod;


/**
 * Hibernate unit/integration test for {@link ViburDBCPConnectionProvider}.
 *
 * @author Simeon Malchev
 */
@RunWith(MockitoJUnitRunner.class)
public class ViburDBCPConnectionProviderTest extends BaseCoreFunctionalTestCase {
    private int poolMaxSize;

    private int statementCacheMaxSize;

    @Captor
    private ArgumentCaptor<StatementMethod> key1;

    @Captor
    private ArgumentCaptor<StatementMethod> key2;

    @Captor
    private ArgumentCaptor<StatementHolder> val1;

    @Test
    public void testSelectStatementNoStatementsCache() {
        /* disables the statements cache */
        setUpPoolAndDatabase(2, 0);
        doInHibernate(this::sessionFactory, ViburDBCPConnectionProviderTest::executeAndVerifySelect);
    }

    @Test
    public void testSelectStatementWithStatementsCache() {
        /* statement cache is enabled */
        setUpPoolAndDatabase(1, 10);
        ConnectionProvider cp = sessionFactory().getServiceRegistry().getService(ConnectionProvider.class);
        ViburDBCPDataSource ds = getDataSource();
        ConcurrentMap<StatementMethod, StatementHolder> mockedStatementCache = mockStatementCache(ds);
        doInHibernate(this::sessionFactory, ViburDBCPConnectionProviderTest::executeAndVerifySelect);
        // We set above the poolMaxSize = 1, that's why the second session will get and use the same underlying connection.
        doInHibernate(this::sessionFactory, ViburDBCPConnectionProviderTest::executeAndVerifySelect);
        InOrder inOrder = Mockito.inOrder(mockedStatementCache);
        inOrder.verify(mockedStatementCache).get(key1.capture());
        inOrder.verify(mockedStatementCache).putIfAbsent(ArgumentMatchers.same(key1.getValue()), val1.capture());
        inOrder.verify(mockedStatementCache).get(key2.capture());
        Assert.assertEquals(1, mockedStatementCache.size());
        Assert.assertTrue(mockedStatementCache.containsKey(key1.getValue()));
        Assert.assertEquals(key1.getValue(), key2.getValue());
        Assert.assertEquals(AVAILABLE, val1.getValue().state().get());
    }

    @Entity(name = "Actor")
    public static class Actor {
        @Id
        @GeneratedValue
        private Long id;

        private String firstName;

        private String lastName;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}

