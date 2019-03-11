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
import org.hibernate.testing.util.ExceptionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(H2Dialect.class)
@TestForIssue(jiraKey = "HHH-12589")
public class SqlFunctionMissingTest extends BaseEntityManagerFunctionalTestCase {
    final SqlFunctionMissingTest.Employee employee = new SqlFunctionMissingTest.Employee();

    @Test
    public void test() {
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                Number result = ((Number) (entityManager.createQuery(("select INSTR(e.username,'@') " + (("from Employee e " + "where ") + "	e.id = :employeeId"))).setParameter("employeeId", employee.id).getSingleResult()));
                fail("Should throw exception!");
            });
        } catch (Exception expected) {
            Assert.assertTrue(ExceptionUtil.rootCause(expected).getMessage().contains("No data type for node: org.hibernate.hql.internal.ast.tree.MethodNod"));
        }
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

