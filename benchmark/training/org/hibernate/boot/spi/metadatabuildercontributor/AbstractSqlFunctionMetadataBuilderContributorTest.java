/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.boot.spi.metadatabuildercontributor;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.NaturalId;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(H2Dialect.class)
@TestForIssue(jiraKey = "HHH-12589")
public abstract class AbstractSqlFunctionMetadataBuilderContributorTest extends BaseEntityManagerFunctionalTestCase {
    final AbstractSqlFunctionMetadataBuilderContributorTest.Employee employee = new AbstractSqlFunctionMetadataBuilderContributorTest.Employee();

    @Test
    public void test() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            String result = ((String) (entityManager.createQuery(("select INSTR(e.username,'@acme.com') " + (("from Employee e " + "where ") + "	e.id = :employeeId"))).setParameter("employeeId", employee.id).getSingleResult()));
            assertEquals("5", result);
        });
    }

    @Entity(name = "Employee")
    public static class Employee {
        @Id
        private Long id;

        @NaturalId
        private String username;

        private String password;
    }
}

