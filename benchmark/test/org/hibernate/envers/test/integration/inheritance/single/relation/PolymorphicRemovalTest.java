/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.inheritance.single.relation;


import java.util.Arrays;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that after the removal of an entity that maintains a polymorphic relation that
 * the {@code AuditReader} queries return the correct polymorphic type for revisions.
 * <p/>
 * Previously, this test would have returned {@link EmployeeType} when looking up the
 * entity associated to revision 3 of typeId; however after the fix it properly will
 * return {@link SalaryEmployeeType} instances instead.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-7249")
public class PolymorphicRemovalTest extends BaseEnversJPAFunctionalTestCase {
    private Integer typeId;

    private Integer employeeId;

    @Test
    @Priority(10)
    public void initData() {
        // revision 1
        this.typeId = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.envers.test.integration.inheritance.single.relation.SalaryEmployeeType type = new org.hibernate.envers.test.integration.inheritance.single.relation.SalaryEmployeeType();
            type.setData("salaried");
            entityManager.persist(type);
            return type.getId();
        });
        // revision 2
        this.employeeId = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.envers.test.integration.inheritance.single.relation.EmployeeType type = entityManager.find(.class, typeId);
            org.hibernate.envers.test.integration.inheritance.single.relation.Employee employee = new org.hibernate.envers.test.integration.inheritance.single.relation.Employee();
            employee.setType(type);
            entityManager.persist(employee);
            return employee.getId();
        });
        // revision 3
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.envers.test.integration.inheritance.single.relation.Employee employee = entityManager.find(.class, employeeId);
            entityManager.remove(employee);
        });
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3), getAuditReader().getRevisions(PolymorphicRemovalTest.EmployeeType.class, typeId));
        Assert.assertEquals(Arrays.asList(2, 3), getAuditReader().getRevisions(PolymorphicRemovalTest.Employee.class, employeeId));
    }

    @Test
    public void testRevisionHistoryPayment() {
        final PolymorphicRemovalTest.EmployeeType rev1 = getAuditReader().find(PolymorphicRemovalTest.EmployeeType.class, typeId, 1);
        assertTyping(PolymorphicRemovalTest.SalaryEmployeeType.class, rev1);
        Assert.assertEquals("SALARY", rev1.getType());
        final PolymorphicRemovalTest.EmployeeType rev2 = getAuditReader().find(PolymorphicRemovalTest.EmployeeType.class, typeId, 2);
        assertTyping(PolymorphicRemovalTest.SalaryEmployeeType.class, rev2);
        Assert.assertEquals("SALARY", rev2.getType());
        final PolymorphicRemovalTest.EmployeeType rev3 = getAuditReader().find(PolymorphicRemovalTest.EmployeeType.class, typeId, 3);
        assertTyping(PolymorphicRemovalTest.SalaryEmployeeType.class, rev3);
        Assert.assertEquals("SALARY", rev3.getType());
    }

    @Entity(name = "EmployeeType")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "TYPE")
    @DiscriminatorValue("UNKNOWN")
    @Audited
    public static class EmployeeType {
        @Id
        @GeneratedValue
        private Integer id;

        @OneToMany(mappedBy = "type")
        private Set<PolymorphicRemovalTest.Employee> employees;

        // used to expose the discriminator value for assertion checking
        @Column(name = "TYPE", insertable = false, updatable = false, nullable = false, length = 31)
        private String type;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Set<PolymorphicRemovalTest.Employee> getEmployees() {
            return employees;
        }

        public void setEmployees(Set<PolymorphicRemovalTest.Employee> employees) {
            this.employees = employees;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @Entity(name = "SalaryEmployee")
    @DiscriminatorValue("SALARY")
    @Audited
    public static class SalaryEmployeeType extends PolymorphicRemovalTest.EmployeeType {
        private String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    @Entity(name = "Employee")
    @Audited
    public static class Employee {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

        @ManyToOne(fetch = FetchType.LAZY)
        private PolymorphicRemovalTest.EmployeeType type;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public PolymorphicRemovalTest.EmployeeType getType() {
            return type;
        }

        public void setType(PolymorphicRemovalTest.EmployeeType type) {
            this.type = type;
        }
    }
}

