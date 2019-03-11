/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.procedure;


import ParameterMode.IN;
import ParameterMode.OUT;
import ParameterMode.REF_CURSOR;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.StoredProcedureQuery;
import org.hibernate.Session;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.result.Output;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea, Jonathan Bregler
 */
@RequiresDialect(AbstractHANADialect.class)
public class HANAStoredProcedureTest extends BaseEntityManagerFunctionalTestCase {
    @NamedStoredProcedureQueries({ @NamedStoredProcedureQuery(name = "singleRefCursor", procedureName = "singleRefCursor", parameters = { @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, type = void.class) }), @NamedStoredProcedureQuery(name = "outAndRefCursor", procedureName = "outAndRefCursor", parameters = { @StoredProcedureParameter(mode = ParameterMode.OUT, type = Integer.class), @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, type = void.class) }) })
    @Entity(name = "IdHolder")
    public static class IdHolder {
        @Id
        Long id;
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12138")
    public void testStoredProcedureOutParameter() {
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_count_phones");
            query.registerStoredProcedureParameter(1, Long.class, IN);
            query.registerStoredProcedureParameter(2, Long.class, OUT);
            query.setParameter(1, 1L);
            query.execute();
            Long phoneCount = ((Long) (query.getOutputParameterValue(2)));
            Assert.assertEquals(Long.valueOf(2), phoneCount);
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12138")
    public void testStoredProcedureRefCursor() {
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_person_phones");
            query.registerStoredProcedureParameter(1, Long.class, IN);
            query.registerStoredProcedureParameter(2, Class.class, REF_CURSOR);
            query.setParameter(1, 1L);
            query.execute();
            List<Object[]> postComments = query.getResultList();
            Assert.assertNotNull(postComments);
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12138")
    public void testHibernateProcedureCallRefCursor() {
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Session session = entityManager.unwrap(Session.class);
            ProcedureCall call = session.createStoredProcedureCall("sp_person_phones");
            call.registerParameter(1, Long.class, IN).bindValue(1L);
            call.registerParameter(2, Class.class, REF_CURSOR);
            Output output = call.getOutputs().getCurrent();
            List<Object[]> postComments = getResultList();
            Assert.assertEquals(2, postComments.size());
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12138")
    public void testStoredProcedureReturnValue() {
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Integer phoneCount = ((Integer) (entityManager.createNativeQuery("SELECT fn_count_phones(:personId) FROM SYS.DUMMY").setParameter("personId", 1).getSingleResult()));
            Assert.assertEquals(Integer.valueOf(2), phoneCount);
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12138")
    public void testNamedNativeQueryStoredProcedureRefCursor() {
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        try {
            List<Object[]> postAndComments = getResultList();
            Object[] postAndComment = postAndComments.get(0);
            Person person = ((Person) (postAndComment[0]));
            Phone phone = ((Phone) (postAndComment[1]));
            Assert.assertEquals(2, postAndComments.size());
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12138")
    public void testNamedNativeQueryStoredProcedureRefCursorWithJDBC() {
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Session session = entityManager.unwrap(Session.class);
            session.doWork(( connection) -> {
                try (PreparedStatement function = connection.prepareStatement("select * from fn_person_and_phones( ? )")) {
                    function.setInt(1, 1);
                    function.execute();
                    try (ResultSet resultSet = function.getResultSet()) {
                        while (resultSet.next()) {
                            Long postCommentId = resultSet.getLong(1);
                            String review = resultSet.getString(2);
                        } 
                    }
                }
            });
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12138")
    public void testSysRefCursorAsOutParameter() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            StoredProcedureQuery function = entityManager.createNamedStoredProcedureQuery("singleRefCursor");
            function.execute();
            Integer value = ((Integer) (function.getSingleResult()));
            assertFalse(function.hasMoreResults());
            assertEquals(Integer.valueOf(1), value);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12138")
    public void testOutAndSysRefCursorAsOutParameter() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            StoredProcedureQuery function = entityManager.createNamedStoredProcedureQuery("outAndRefCursor");
            function.execute();
            Integer value = ((Integer) (function.getSingleResult()));
            assertEquals(Integer.valueOf(1), value);
            assertEquals(Integer.valueOf(1), function.getOutputParameterValue(1));
            assertFalse(function.hasMoreResults());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12661")
    public void testBindParameterAsHibernateType() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_phone_validity").registerStoredProcedureParameter(1, .class, ParameterMode.IN).registerStoredProcedureParameter(2, .class, ParameterMode.REF_CURSOR).setParameter(1, true);
            query.execute();
            List phones = query.getResultList();
            assertEquals(1, phones.size());
            assertEquals("123-456-7890", phones.get(0));
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Vote vote1 = new Vote();
            vote1.setId(1L);
            vote1.setVoteChoice(true);
            entityManager.persist(vote1);
            Vote vote2 = new Vote();
            vote2.setId(2L);
            vote2.setVoteChoice(false);
            entityManager.persist(vote2);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_votes").registerStoredProcedureParameter(1, .class, ParameterMode.IN).registerStoredProcedureParameter(2, .class, ParameterMode.REF_CURSOR).setParameter(1, true);
            query.execute();
            List votes = query.getResultList();
            assertEquals(1, votes.size());
            assertEquals(1, ((Number) (votes.get(0))).intValue());
        });
    }
}

