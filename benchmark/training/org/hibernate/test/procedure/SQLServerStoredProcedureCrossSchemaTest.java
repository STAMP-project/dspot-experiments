/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.procedure;


import javax.persistence.StoredProcedureQuery;
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
public class SQLServerStoredProcedureCrossSchemaTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testStoredProcedureViaJPA() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_test.sp_square_number");
            query.registerStoredProcedureParameter("inputNumber", .class, ParameterMode.IN);
            query.registerStoredProcedureParameter("outputNumber", .class, ParameterMode.OUT);
            query.setParameter("inputNumber", 7);
            query.execute();
            int result = ((int) (query.getOutputParameterValue("outputNumber")));
            assertEquals(49, result);
        });
    }
}

