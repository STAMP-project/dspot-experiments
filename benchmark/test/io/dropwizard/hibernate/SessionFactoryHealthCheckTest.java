package io.dropwizard.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


@SuppressWarnings("HibernateResourceOpenedButNotSafelyClosed")
public class SessionFactoryHealthCheckTest {
    private final SessionFactory factory = Mockito.mock(SessionFactory.class);

    private final SessionFactoryHealthCheck healthCheck = new SessionFactoryHealthCheck(factory, "SELECT 1");

    @Test
    public void hasASessionFactory() throws Exception {
        assertThat(healthCheck.getSessionFactory()).isEqualTo(factory);
    }

    @Test
    public void hasAValidationQuery() throws Exception {
        assertThat(healthCheck.getValidationQuery()).isEqualTo("SELECT 1");
    }

    @Test
    public void isHealthyIfNoExceptionIsThrown() throws Exception {
        final Session session = Mockito.mock(Session.class);
        Mockito.when(factory.openSession()).thenReturn(session);
        final Transaction transaction = Mockito.mock(Transaction.class);
        Mockito.when(session.beginTransaction()).thenReturn(transaction);
        final NativeQuery<?> query = Mockito.mock(NativeQuery.class);
        Mockito.when(session.createNativeQuery(ArgumentMatchers.anyString())).thenReturn(query);
        assertThat(healthCheck.execute().isHealthy()).isTrue();
        final InOrder inOrder = Mockito.inOrder(factory, session, transaction, query);
        inOrder.verify(factory).openSession();
        inOrder.verify(session).beginTransaction();
        inOrder.verify(session).createNativeQuery("SELECT 1");
        inOrder.verify(query).list();
        inOrder.verify(transaction).commit();
        inOrder.verify(session).close();
    }

    @Test
    public void isUnhealthyIfAnExceptionIsThrown() throws Exception {
        final Session session = Mockito.mock(Session.class);
        Mockito.when(factory.openSession()).thenReturn(session);
        final Transaction transaction = Mockito.mock(Transaction.class);
        Mockito.when(session.beginTransaction()).thenReturn(transaction);
        Mockito.when(transaction.getStatus()).thenReturn(ACTIVE);
        final NativeQuery<?> query = Mockito.mock(NativeQuery.class);
        Mockito.when(session.createNativeQuery(ArgumentMatchers.anyString())).thenReturn(query);
        Mockito.when(query.list()).thenThrow(new HibernateException("OH NOE"));
        assertThat(healthCheck.execute().isHealthy()).isFalse();
        final InOrder inOrder = Mockito.inOrder(factory, session, transaction, query);
        inOrder.verify(factory).openSession();
        inOrder.verify(session).beginTransaction();
        inOrder.verify(session).createNativeQuery("SELECT 1");
        inOrder.verify(query).list();
        inOrder.verify(transaction).rollback();
        inOrder.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
    }
}

