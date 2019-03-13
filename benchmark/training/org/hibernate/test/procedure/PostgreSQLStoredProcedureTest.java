/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.procedure;


import java.sql.CallableStatement;
import java.sql.ResultSet;
import javax.persistence.StoredProcedureQuery;
import org.hibernate.Session;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(PostgreSQL81Dialect.class)
public class PostgreSQLStoredProcedureTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testStoredProcedureOutParameter() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_count_phones");
            query.registerStoredProcedureParameter("personId", .class, ParameterMode.IN);
            query.registerStoredProcedureParameter("phoneCount", .class, ParameterMode.OUT);
            query.setParameter("personId", 1L);
            query.execute();
            Long phoneCount = ((Long) (query.getOutputParameterValue("phoneCount")));
            assertEquals(Long.valueOf(2), phoneCount);
        });
    }

    @Test
    public void testStoredProcedureRefCursor() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("fn_phones");
            query.registerStoredProcedureParameter(1, .class, ParameterMode.REF_CURSOR);
            query.registerStoredProcedureParameter(2, .class, ParameterMode.IN);
            query.setParameter(2, 1L);
            List<Object[]> phones = query.getResultList();
            assertEquals(2, phones.size());
        });
    }

    @Test
    public void testFunctionWithJDBC() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            Long phoneCount = session.doReturningWork(( connection) -> {
                CallableStatement function = null;
                try {
                    function = connection.prepareCall("{ ? = call sp_count_phones(?) }");
                    function.registerOutParameter(1, Types.BIGINT);
                    function.setLong(2, 1L);
                    function.execute();
                    return function.getLong(1);
                } finally {
                    if (function != null) {
                        function.close();
                    }
                }
            });
            assertEquals(Long.valueOf(2), phoneCount);
        });
    }

    @Test
    public void testFunctionWithJDBCByName() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            try {
                Session session = entityManager.unwrap(.class);
                Long phoneCount = session.doReturningWork(( connection) -> {
                    CallableStatement function = null;
                    try {
                        function = connection.prepareCall("{ ? = call sp_count_phones(?) }");
                        function.registerOutParameter("phoneCount", Types.BIGINT);
                        function.setLong("personId", 1L);
                        function.execute();
                        return function.getLong(1);
                    } finally {
                        if (function != null) {
                            function.close();
                        }
                    }
                });
                assertEquals(Long.valueOf(2), phoneCount);
            } catch ( e) {
                assertEquals(.class, e.getCause().getClass());
            }
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11863")
    public void testSysRefCursorAsOutParameter() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long value = null;
            Session session = entityManager.unwrap(.class);
            try (ResultSet resultSet = session.doReturningWork(( connection) -> {
                CallableStatement function = null;
                try {
                    function = connection.prepareCall("{ ? = call singleRefCursor() }");
                    function.registerOutParameter(1, Types.REF_CURSOR);
                    function.execute();
                    return ((ResultSet) (function.getObject(1)));
                } finally {
                    if (function != null) {
                        function.close();
                    }
                }
            })) {
                while (resultSet.next()) {
                    value = resultSet.getLong(1);
                } 
            } catch ( e) {
                fail(e.getMessage());
            }
            assertEquals(Long.valueOf(1), value);
            StoredProcedureQuery function = entityManager.createStoredProcedureQuery("singleRefCursor");
            function.registerStoredProcedureParameter(1, .class, ParameterMode.REF_CURSOR);
            function.execute();
            assertFalse(function.hasMoreResults());
            value = null;
            try (ResultSet resultSet = ((ResultSet) (function.getOutputParameterValue(1)))) {
                while (resultSet.next()) {
                    value = resultSet.getLong(1);
                } 
            } catch ( e) {
                fail(e.getMessage());
            }
            assertEquals(Long.valueOf(1), value);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12905")
    public void testStoredProcedureNullParameterHibernate() {
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
                procedureCall.registerParameter("param", .class, ParameterMode.IN);
                procedureCall.registerParameter("result", .class, ParameterMode.OUT);
                procedureCall.setParameter("param", null);
                procedureCall.getOutputParameterValue("result");
                fail("Should have thrown exception");
            } catch ( e) {
                assertEquals("The parameter named [param] was null. You need to call ParameterRegistration#enablePassingNulls(true) in order to pass null parameters.", e.getMessage());
            }
        });
    }
}

