package io.dropwizard.hibernate;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


@SuppressWarnings("deprecation")
public class AbstractDAOTest {
    private static class MockDAO extends AbstractDAO<String> {
        MockDAO(SessionFactory factory) {
            super(factory);
        }

        @Override
        public Session currentSession() {
            return super.currentSession();
        }

        @Override
        public Criteria criteria() {
            return super.criteria();
        }

        @Override
        public Query<?> namedQuery(String queryName) throws HibernateException {
            return super.namedQuery(queryName);
        }

        @Override
        public Class<String> getEntityClass() {
            return super.getEntityClass();
        }

        @Override
        public String uniqueResult(Criteria criteria) throws HibernateException {
            return super.uniqueResult(criteria);
        }

        @Override
        public String uniqueResult(Query<String> query) throws HibernateException {
            return super.uniqueResult(query);
        }

        @Override
        public List<String> list(Criteria criteria) throws HibernateException {
            return super.list(criteria);
        }

        @Override
        public List<String> list(Query<String> query) throws HibernateException {
            return super.list(query);
        }

        @Override
        public String get(Serializable id) {
            return super.get(id);
        }

        @Override
        public String persist(String entity) throws HibernateException {
            return super.persist(entity);
        }

        @Override
        public <T> T initialize(T proxy) {
            return super.initialize(proxy);
        }
    }

    private final SessionFactory factory = Mockito.mock(SessionFactory.class);

    private final CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);

    private final Criteria criteria = Mockito.mock(Criteria.class);

    @SuppressWarnings("unchecked")
    private final CriteriaQuery<String> criteriaQuery = Mockito.mock(CriteriaQuery.class);

    @SuppressWarnings("unchecked")
    private final Query<String> query = Mockito.mock(Query.class);

    private final Session session = Mockito.mock(Session.class);

    private final AbstractDAOTest.MockDAO dao = new AbstractDAOTest.MockDAO(factory);

    @Test
    public void getsASessionFromTheSessionFactory() throws Exception {
        assertThat(dao.currentSession()).isSameAs(session);
    }

    @Test
    public void hasAnEntityClass() throws Exception {
        assertThat(dao.getEntityClass()).isEqualTo(String.class);
    }

    @Test
    public void getsNamedQueries() throws Exception {
        assertThat(dao.namedQuery("query-name")).isEqualTo(query);
        Mockito.verify(session).getNamedQuery("query-name");
    }

    @Test
    public void getsTypedQueries() throws Exception {
        assertThat(query("HQL")).isEqualTo(query);
        Mockito.verify(session).createQuery("HQL", String.class);
    }

    @Test
    public void createsNewCriteria() throws Exception {
        assertThat(dao.criteria()).isEqualTo(criteria);
        Mockito.verify(session).createCriteria(String.class);
    }

    @Test
    public void createsNewCriteriaQueries() throws Exception {
        assertThat(criteriaQuery()).isEqualTo(criteriaQuery);
        Mockito.verify(session).getCriteriaBuilder();
        Mockito.verify(criteriaBuilder).createQuery(String.class);
    }

    @Test
    public void returnsUniqueResultsFromCriteriaQueries() throws Exception {
        Mockito.when(criteria.uniqueResult()).thenReturn("woo");
        assertThat(dao.uniqueResult(criteria)).isEqualTo("woo");
    }

    @Test
    public void returnsUniqueResultsFromJpaCriteriaQueries() throws Exception {
        Mockito.when(session.createQuery(criteriaQuery)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(Collections.singletonList("woo"));
        assertThat(dao.uniqueResult(criteriaQuery)).isEqualTo("woo");
    }

    @Test
    public void throwsOnNonUniqueResultsFromJpaCriteriaQueries() throws Exception {
        Mockito.when(session.createQuery(criteriaQuery)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(Arrays.asList("woo", "boo"));
        assertThatExceptionOfType(NonUniqueResultException.class).isThrownBy(() -> dao.uniqueResult(criteriaQuery));
    }

    @Test
    public void returnsUniqueResultsFromQueries() throws Exception {
        Mockito.when(query.uniqueResult()).thenReturn("woo");
        assertThat(dao.uniqueResult(query)).isEqualTo("woo");
    }

    @Test
    public void returnsUniqueListsFromCriteriaQueries() throws Exception {
        Mockito.when(criteria.list()).thenReturn(Collections.singletonList("woo"));
        assertThat(dao.list(criteria)).containsOnly("woo");
    }

    @Test
    public void returnsUniqueListsFromJpaCriteriaQueries() throws Exception {
        Mockito.when(session.createQuery(criteriaQuery)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(Collections.singletonList("woo"));
        assertThat(dao.list(criteriaQuery)).containsOnly("woo");
    }

    @Test
    public void returnsUniqueListsFromQueries() throws Exception {
        Mockito.when(query.list()).thenReturn(Collections.singletonList("woo"));
        assertThat(dao.list(query)).containsOnly("woo");
    }

    @Test
    public void getsEntitiesById() throws Exception {
        Mockito.when(session.get(String.class, 200)).thenReturn("woo!");
        assertThat(dao.get(200)).isEqualTo("woo!");
        Mockito.verify(session).get(String.class, 200);
    }

    @Test
    public void persistsEntities() throws Exception {
        assertThat(dao.persist("woo")).isEqualTo("woo");
        Mockito.verify(session).saveOrUpdate("woo");
    }

    @Test
    public void initializesProxies() throws Exception {
        final LazyInitializer initializer = Mockito.mock(LazyInitializer.class);
        Mockito.when(initializer.isUninitialized()).thenReturn(true);
        final HibernateProxy proxy = Mockito.mock(HibernateProxy.class);
        Mockito.when(proxy.getHibernateLazyInitializer()).thenReturn(initializer);
        dao.initialize(proxy);
        Mockito.verify(initializer).initialize();
    }
}

