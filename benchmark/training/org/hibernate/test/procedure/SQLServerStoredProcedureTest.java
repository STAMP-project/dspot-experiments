/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.procedure;


import java.sql.CallableStatement;
import java.util.regex.Pattern;
import javax.persistence.StoredProcedureQuery;
import org.hibernate.Session;
import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(SQLServer2012Dialect.class)
public class SQLServerStoredProcedureTest extends BaseEntityManagerFunctionalTestCase {
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
            try {
                StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_phones");
                query.registerStoredProcedureParameter(1, .class, ParameterMode.IN);
                query.registerStoredProcedureParameter(2, .class, ParameterMode.REF_CURSOR);
                query.setParameter(1, 1L);
                query.execute();
                List<Object[]> postComments = query.getResultList();
                assertNotNull(postComments);
            } catch ( e) {
                assertTrue(Pattern.compile("Dialect .*? not known to support REF_CURSOR parameters").matcher(e.getCause().getMessage()).matches());
            }
        });
    }

    @Test
    public void testStoredProcedureReturnValue() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            session.doWork(( connection) -> {
                CallableStatement function = null;
                try {
                    function = connection.prepareCall("{ ? = call fn_count_phones(?) }");
                    function.registerOutParameter(1, Types.INTEGER);
                    function.setInt(2, 1);
                    function.execute();
                    int phoneCount = function.getInt(1);
                    assertEquals(2, phoneCount);
                } finally {
                    if (function != null) {
                        function.close();
                    }
                }
            });
        });
    }
}

