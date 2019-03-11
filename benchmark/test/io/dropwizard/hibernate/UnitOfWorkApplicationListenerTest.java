package io.dropwizard.hibernate;


import CacheMode.IGNORE;
import FlushMode.ALWAYS;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@SuppressWarnings("HibernateResourceOpenedButNotSafelyClosed")
public class UnitOfWorkApplicationListenerTest {
    private final SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);

    private final SessionFactory analyticsSessionFactory = Mockito.mock(SessionFactory.class);

    private final UnitOfWorkApplicationListener listener = new UnitOfWorkApplicationListener();

    private final ExtendedUriInfo uriInfo = Mockito.mock(ExtendedUriInfo.class);

    private final RequestEvent requestStartEvent = Mockito.mock(RequestEvent.class);

    private final RequestEvent requestMethodStartEvent = Mockito.mock(RequestEvent.class);

    private final RequestEvent responseFiltersStartEvent = Mockito.mock(RequestEvent.class);

    private final RequestEvent responseFinishedEvent = Mockito.mock(RequestEvent.class);

    private final RequestEvent requestMethodExceptionEvent = Mockito.mock(RequestEvent.class);

    private final Session session = Mockito.mock(Session.class);

    private final Session analyticsSession = Mockito.mock(Session.class);

    private final Transaction transaction = Mockito.mock(Transaction.class);

    private final Transaction analyticsTransaction = Mockito.mock(Transaction.class);

    @Test
    public void opensAndClosesASession() throws Exception {
        execute();
        final InOrder inOrder = Mockito.inOrder(sessionFactory, session);
        inOrder.verify(sessionFactory).openSession();
        inOrder.verify(session).close();
    }

    @Test
    public void bindsAndUnbindsTheSessionToTheManagedContext() throws Exception {
        Mockito.doAnswer(( invocation) -> {
            assertThat(ManagedSessionContext.hasBind(sessionFactory)).isTrue();
            return null;
        }).when(session).beginTransaction();
        execute();
        assertThat(ManagedSessionContext.hasBind(sessionFactory)).isFalse();
    }

    @Test
    public void configuresTheSessionsReadOnlyDefault() throws Exception {
        prepareResourceMethod("methodWithReadOnlyAnnotation");
        execute();
        Mockito.verify(session).setDefaultReadOnly(true);
    }

    @Test
    public void configuresTheSessionsCacheMode() throws Exception {
        prepareResourceMethod("methodWithCacheModeIgnoreAnnotation");
        execute();
        Mockito.verify(session).setCacheMode(IGNORE);
    }

    @Test
    public void configuresTheSessionsFlushMode() throws Exception {
        prepareResourceMethod("methodWithFlushModeAlwaysAnnotation");
        execute();
        Mockito.verify(session).setHibernateFlushMode(ALWAYS);
    }

    @Test
    public void doesNotBeginATransactionIfNotTransactional() throws Exception {
        final String resourceMethodName = "methodWithTransactionalFalseAnnotation";
        prepareResourceMethod(resourceMethodName);
        Mockito.when(session.getTransaction()).thenReturn(null);
        execute();
        Mockito.verify(session, Mockito.never()).beginTransaction();
        Mockito.verifyZeroInteractions(transaction);
    }

    @Test
    public void detectsAnnotationOnHandlingMethod() throws NoSuchMethodException {
        final String resourceMethodName = "handlingMethodAnnotated";
        prepareResourceMethod(resourceMethodName);
        execute();
        Mockito.verify(session).setDefaultReadOnly(true);
    }

    @Test
    public void detectsAnnotationOnDefinitionMethod() throws NoSuchMethodException {
        final String resourceMethodName = "definitionMethodAnnotated";
        prepareResourceMethod(resourceMethodName);
        execute();
        Mockito.verify(session).setDefaultReadOnly(true);
    }

    @Test
    public void annotationOnDefinitionMethodOverridesHandlingMethod() throws NoSuchMethodException {
        final String resourceMethodName = "bothMethodsAnnotated";
        prepareResourceMethod(resourceMethodName);
        execute();
        Mockito.verify(session).setDefaultReadOnly(true);
    }

    @Test
    public void beginsAndCommitsATransactionIfTransactional() throws Exception {
        execute();
        final InOrder inOrder = Mockito.inOrder(session, transaction);
        inOrder.verify(session).beginTransaction();
        inOrder.verify(transaction).commit();
        inOrder.verify(session).close();
    }

    @Test
    public void rollsBackTheTransactionOnException() throws Exception {
        executeWithException();
        final InOrder inOrder = Mockito.inOrder(session, transaction);
        inOrder.verify(session).beginTransaction();
        inOrder.verify(transaction).rollback();
        inOrder.verify(session).close();
    }

    @Test
    public void doesNotCommitAnInactiveTransaction() throws Exception {
        Mockito.when(transaction.getStatus()).thenReturn(NOT_ACTIVE);
        execute();
        Mockito.verify(transaction, Mockito.never()).commit();
    }

    @Test
    public void doesNotCommitANullTransaction() throws Exception {
        Mockito.when(session.getTransaction()).thenReturn(null);
        execute();
        Mockito.verify(transaction, Mockito.never()).commit();
    }

    @Test
    public void doesNotRollbackAnInactiveTransaction() throws Exception {
        Mockito.when(transaction.getStatus()).thenReturn(NOT_ACTIVE);
        executeWithException();
        Mockito.verify(transaction, Mockito.never()).rollback();
    }

    @Test
    public void doesNotRollbackANullTransaction() throws Exception {
        Mockito.when(session.getTransaction()).thenReturn(null);
        executeWithException();
        Mockito.verify(transaction, Mockito.never()).rollback();
    }

    @Test
    public void beginsAndCommitsATransactionForAnalytics() throws Exception {
        prepareResourceMethod("methodWithUnitOfWorkOnAnalyticsDatabase");
        execute();
        final InOrder inOrder = Mockito.inOrder(analyticsSession, analyticsTransaction);
        inOrder.verify(analyticsSession).beginTransaction();
        inOrder.verify(analyticsTransaction).commit();
        inOrder.verify(analyticsSession).close();
    }

    @Test
    public void throwsExceptionOnNotRegisteredDatabase() throws Exception {
        prepareResourceMethod("methodWithUnitOfWorkOnNotRegisteredDatabase");
        assertThatThrownBy(this::execute).isInstanceOf(IllegalArgumentException.class).hasMessage("Unregistered Hibernate bundle: 'warehouse'");
    }

    public static class MockResource implements UnitOfWorkApplicationListenerTest.MockResourceInterface {
        @UnitOfWork(readOnly = false, cacheMode = CacheMode.NORMAL, transactional = true, flushMode = FlushMode.AUTO)
        public void methodWithDefaultAnnotation() {
        }

        @UnitOfWork(readOnly = true, cacheMode = CacheMode.NORMAL, transactional = true, flushMode = FlushMode.AUTO)
        public void methodWithReadOnlyAnnotation() {
        }

        @UnitOfWork(readOnly = false, cacheMode = CacheMode.IGNORE, transactional = true, flushMode = FlushMode.AUTO)
        public void methodWithCacheModeIgnoreAnnotation() {
        }

        @UnitOfWork(readOnly = false, cacheMode = CacheMode.NORMAL, transactional = true, flushMode = FlushMode.ALWAYS)
        public void methodWithFlushModeAlwaysAnnotation() {
        }

        @UnitOfWork(readOnly = false, cacheMode = CacheMode.NORMAL, transactional = false, flushMode = FlushMode.AUTO)
        public void methodWithTransactionalFalseAnnotation() {
        }

        @UnitOfWork(readOnly = true)
        @Override
        public void handlingMethodAnnotated() {
        }

        @Override
        public void definitionMethodAnnotated() {
        }

        @UnitOfWork(readOnly = false)
        @Override
        public void bothMethodsAnnotated() {
        }

        @UnitOfWork("analytics")
        public void methodWithUnitOfWorkOnAnalyticsDatabase() {
        }

        @UnitOfWork("warehouse")
        public void methodWithUnitOfWorkOnNotRegisteredDatabase() {
        }
    }

    public static interface MockResourceInterface {
        void handlingMethodAnnotated();

        @UnitOfWork(readOnly = true)
        void definitionMethodAnnotated();

        @UnitOfWork(readOnly = true)
        void bothMethodsAnnotated();
    }
}

