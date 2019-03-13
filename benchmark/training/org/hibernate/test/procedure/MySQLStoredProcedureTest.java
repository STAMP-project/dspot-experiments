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
import java.sql.CallableStatement;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import org.hibernate.Session;
import org.hibernate.dialect.MySQL5Dialect;
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
 * @author Vlad Mihalcea
 */
@RequiresDialect(MySQL5Dialect.class)
public class MySQLStoredProcedureTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testStoredProcedureOutParameter() {
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_count_phones");
            query.registerStoredProcedureParameter("personId", Long.class, IN);
            query.registerStoredProcedureParameter("phoneCount", Long.class, OUT);
            query.setParameter("personId", 1L);
            query.execute();
            Long phoneCount = ((Long) (query.getOutputParameterValue("phoneCount")));
            Assert.assertEquals(Long.valueOf(2), phoneCount);
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    @Test
    public void testHibernateProcedureCallOutParameter() {
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Session session = entityManager.unwrap(Session.class);
            ProcedureCall call = session.createStoredProcedureCall("sp_count_phones");
            call.registerParameter("personId", Long.class, IN).bindValue(1L);
            call.registerParameter("phoneCount", Long.class, OUT);
            Long phoneCount = ((Long) (call.getOutputs().getOutputParameterValue("phoneCount")));
            Assert.assertEquals(Long.valueOf(2), phoneCount);
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    @Test
    public void testStoredProcedureRefCursor() {
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_phones");
            query.registerStoredProcedureParameter(1, void.class, REF_CURSOR);
            query.registerStoredProcedureParameter(2, Long.class, IN);
            query.setParameter(2, 1L);
            List<Object[]> personComments = query.getResultList();
            Assert.assertEquals(2, personComments.size());
        } catch (Exception e) {
            Assert.assertTrue(Pattern.compile("Dialect .*? not known to support REF_CURSOR parameters").matcher(e.getCause().getMessage()).matches());
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    @Test
    public void testStoredProcedureReturnValue() {
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_phones");
            query.registerStoredProcedureParameter(1, Long.class, IN);
            query.setParameter(1, 1L);
            List<Object[]> personComments = query.getResultList();
            Assert.assertEquals(2, personComments.size());
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    @Test
    public void testHibernateProcedureCallReturnValueParameter() {
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Session session = entityManager.unwrap(Session.class);
            ProcedureCall call = session.createStoredProcedureCall("sp_phones");
            call.registerParameter(1, Long.class, IN).bindValue(1L);
            Output output = call.getOutputs().getCurrent();
            List<Object[]> personComments = getResultList();
            Assert.assertEquals(2, personComments.size());
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    @Test
    public void testFunctionWithJDBC() {
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        try {
            final AtomicReference<Integer> phoneCount = new AtomicReference<>();
            Session session = entityManager.unwrap(Session.class);
            session.doWork(( connection) -> {
                try (CallableStatement function = connection.prepareCall("{ ? = call fn_count_phones(?) }")) {
                    function.registerOutParameter(1, Types.INTEGER);
                    function.setInt(2, 1);
                    function.execute();
                    phoneCount.set(function.getInt(1));
                }
            });
            Assert.assertEquals(Integer.valueOf(2), phoneCount.get());
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12905")
    public void testStoredProcedureNullParameter() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            ProcedureCall procedureCall = entityManager.unwrap(.class).createStoredProcedureCall("sp_is_null");
            procedureCall.registerParameter(1, .class, ParameterMode.IN).enablePassingNulls(true);
            procedureCall.registerParameter(2, .class, ParameterMode.OUT);
            procedureCall.setParameter(1, null);
            Boolean result = ((Boolean) (procedureCall.getOutputParameterValue(2)));
            assertTrue(result);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            ProcedureCall procedureCall = entityManager.unwrap(.class).createStoredProcedureCall("sp_is_null");
            procedureCall.registerParameter(1, .class, ParameterMode.IN).enablePassingNulls(true);
            procedureCall.registerParameter(2, .class, ParameterMode.OUT);
            procedureCall.setParameter(1, "test");
            Boolean result = ((Boolean) (procedureCall.getOutputParameterValue(2)));
            assertFalse(result);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12905")
    public void testStoredProcedureNullParameterHibernateWithoutEnablePassingNulls() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            try {
                ProcedureCall procedureCall = entityManager.unwrap(.class).createStoredProcedureCall("sp_is_null");
                procedureCall.registerParameter(1, .class, ParameterMode.IN);
                procedureCall.registerParameter(2, .class, ParameterMode.OUT);
                procedureCall.setParameter(1, null);
                procedureCall.getOutputParameterValue(2);
                fail("Should have thrown exception");
            } catch ( e) {
                assertEquals("The parameter at position [1] was null. You need to call ParameterRegistration#enablePassingNulls(true) in order to pass null parameters.", e.getMessage());
            }
        });
    }
}

