package org.hibernate.test.c3p0;


import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12749")
@RequiresDialect(H2Dialect.class)
public class C3P0DefaultIsolationLevelTest extends BaseNonConfigCoreFunctionalTestCase {
    private C3P0ProxyConnectionProvider connectionProvider;

    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    public void testStoredProcedureOutParameter() throws SQLException {
        clearSpies();
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.c3p0.Person person = new org.hibernate.test.c3p0.Person();
            person.id = 1L;
            person.name = "Vlad Mihalcea";
            session.persist(person);
        });
        Assert.assertEquals(1, sqlStatementInterceptor.getSqlQueries().size());
        Assert.assertTrue(sqlStatementInterceptor.getSqlQueries().get(0).toLowerCase().startsWith("insert into"));
        Connection connectionSpy = connectionProvider.getConnectionSpyMap().keySet().iterator().next();
        Mockito.verify(connectionSpy, Mockito.never()).setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        clearSpies();
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.c3p0.Person person = session.find(.class, 1L);
            assertEquals("Vlad Mihalcea", person.name);
        });
        Assert.assertEquals(1, sqlStatementInterceptor.getSqlQueries().size());
        Assert.assertTrue(sqlStatementInterceptor.getSqlQueries().get(0).toLowerCase().startsWith("select"));
        connectionSpy = connectionProvider.getConnectionSpyMap().keySet().iterator().next();
        Mockito.verify(connectionSpy, Mockito.never()).setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String name;
    }
}

