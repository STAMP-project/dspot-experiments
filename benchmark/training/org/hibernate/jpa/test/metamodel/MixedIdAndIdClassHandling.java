/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metamodel;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Ugh
 *
 * @author Steve Ebersole
 */
public class MixedIdAndIdClassHandling extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-8533")
    public void testAccess() {
        EntityType<MixedIdAndIdClassHandling.FullTimeEmployee> entityType = entityManagerFactory().getMetamodel().entity(MixedIdAndIdClassHandling.FullTimeEmployee.class);
        try {
            entityType.getId(String.class);
            Assert.fail("getId on entity defining @IdClass should cause IAE");
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertNotNull(entityType.getSupertype().getIdClassAttributes());
        Assert.assertEquals(1, entityType.getSupertype().getIdClassAttributes().size());
        Assert.assertFalse(entityType.hasSingleIdAttribute());
        Assert.assertEquals(String.class, entityType.getIdType().getJavaType());
    }

    @MappedSuperclass
    @IdClass(MixedIdAndIdClassHandling.EmployeeId.class)
    public abstract static class Employee {
        @Id
        private String id;

        private String name;
    }

    @Entity(name = "FullTimeEmployee")
    @Table(name = "EMPLOYEE")
    public static class FullTimeEmployee extends MixedIdAndIdClassHandling.Employee {
        @Column(name = "SALARY")
        private float salary;

        public FullTimeEmployee() {
        }
    }

    public static class EmployeeId implements Serializable {
        String id;

        public EmployeeId() {
        }

        public EmployeeId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            final MixedIdAndIdClassHandling.EmployeeId other = ((MixedIdAndIdClassHandling.EmployeeId) (obj));
            if ((this.id) == null ? (other.id) != null : !(this.id.equals(other.id))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = (29 * hash) + ((this.id) != null ? this.id.hashCode() : 0);
            return hash;
        }
    }
}

