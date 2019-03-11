/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.procedure;


import java.util.Date;
import java.util.List;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@RequiresDialect(H2Dialect.class)
public class StoredProcedureResultSetMappingTest extends BaseEntityManagerFunctionalTestCase {
    // ignore the questionable-ness of constructing a partial entity
    @Entity(name = "Employee")
    @Table(name = "EMP")
    @SqlResultSetMapping(name = "id-fname-lname", classes = { @ConstructorResult(targetClass = StoredProcedureResultSetMappingTest.Employee.class, columns = { @ColumnResult(name = "ID"), @ColumnResult(name = "FIRSTNAME"), @ColumnResult(name = "LASTNAME") }) })
    public static class Employee {
        @Id
        private int id;

        private String userName;

        private String firstName;

        private String lastName;

        @Temporal(TemporalType.DATE)
        private Date hireDate;

        public Employee() {
        }

        public Employee(Integer id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    @Test
    public void testPartialResults() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        StoredProcedureQuery query = em.createStoredProcedureQuery("allEmployeeNames", "id-fname-lname");
        List results = query.getResultList();
        Assert.assertEquals(3, results.size());
        em.getTransaction().commit();
        em.close();
    }
}

