/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy;


import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-11117")
@RunWith(BytecodeEnhancerRunner.class)
public class LazyBasicFieldMergeTest extends BaseCoreFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.bytecode.enhancement.lazy.Manager manager = new org.hibernate.test.bytecode.enhancement.lazy.Manager();
            manager.setName("John Doe");
            manager.setResume(new byte[]{ 1, 2, 3 });
            org.hibernate.test.bytecode.enhancement.lazy.Company company = new org.hibernate.test.bytecode.enhancement.lazy.Company();
            company.setName("Company");
            company.setManager(manager);
            org.hibernate.test.bytecode.enhancement.lazy.Company _company = ((org.hibernate.test.bytecode.enhancement.lazy.Company) (session.merge(company)));
            assertEquals(company.getName(), _company.getName());
            assertArrayEquals(company.getManager().getResume(), _company.getManager().getResume());
        });
    }

    @Entity(name = "Company")
    @Table(name = "COMPANY")
    public static class Company {
        @Id
        @GeneratedValue
        @Column(name = "COMP_ID")
        private Long id;

        @Column(name = "NAME")
        private String name;

        @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
        private LazyBasicFieldMergeTest.Manager manager;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public LazyBasicFieldMergeTest.Manager getManager() {
            return manager;
        }

        public void setManager(LazyBasicFieldMergeTest.Manager manager) {
            this.manager = manager;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity(name = "Manager")
    @Table(name = "MANAGER")
    public static class Manager {
        @Id
        @GeneratedValue
        @Column(name = "MAN_ID")
        private Long id;

        @Column(name = "NAME")
        private String name;

        @Lob
        @Column(name = "RESUME")
        @Basic(fetch = FetchType.LAZY)
        private byte[] resume;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "COMP_ID")
        private LazyBasicFieldMergeTest.Company company;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public byte[] getResume() {
            return resume;
        }

        public void setResume(byte[] resume) {
            this.resume = resume;
        }

        public LazyBasicFieldMergeTest.Company getCompany() {
            return company;
        }

        public void setCompany(LazyBasicFieldMergeTest.Company company) {
            this.company = company;
        }
    }
}

