/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.procedure;


import java.sql.CallableStatement;
import javax.persistence.StoredProcedureQuery;
import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(SQLServer2012Dialect.class)
@TestForIssue(jiraKey = "HHH-12704")
public class SQLServerStoredProcedureCrossDatabaseTest extends BaseEntityManagerFunctionalTestCase {
    private final String DATABASE_NAME_TOKEN = "databaseName=";

    private final String DATABASE_NAME = "hibernate_orm_test_sp";

    @Test
    @FailureExpected(jiraKey = "HHH-12704", message = "SQL Server JDBC Driver does not support registering name parameters properly")
    public void testStoredProcedureViaJPANamedParameters() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery(((DATABASE_NAME) + ".dbo.sp_square_number"));
            query.registerStoredProcedureParameter("inputNumber", .class, ParameterMode.IN);
            query.registerStoredProcedureParameter("outputNumber", .class, ParameterMode.OUT);
            query.setParameter("inputNumber", 7);
            query.execute();
            int result = ((int) (query.getOutputParameterValue("outputNumber")));
            assertEquals(49, result);
        });
    }

    @Test
    public void testStoredProcedureViaJPA() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery(((DATABASE_NAME) + ".dbo.sp_square_number"));
            query.registerStoredProcedureParameter(1, .class, ParameterMode.IN);
            query.registerStoredProcedureParameter(2, .class, ParameterMode.OUT);
            query.setParameter(1, 7);
            query.execute();
            int result = ((int) (query.getOutputParameterValue(2)));
            assertEquals(49, result);
        });
    }

    @Test
    public void testStoredProcedureViaJDBC() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.unwrap(.class).doWork(( connection) -> {
                try (CallableStatement storedProcedure = connection.prepareCall((("{ call " + (DATABASE_NAME)) + ".dbo.sp_square_number(?, ?) }"))) {
                    try {
                        storedProcedure.registerOutParameter(2, Types.INTEGER);
                        storedProcedure.setInt(1, 7);
                        storedProcedure.execute();
                        int result = storedProcedure.getInt(2);
                        assertEquals(49, result);
                    } finally {
                        if (storedProcedure != null) {
                            storedProcedure.close();
                        }
                    }
                }
            });
        });
    }
}

