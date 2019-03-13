/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2006-2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.subselectfetch;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Stephen Fikes
 * @author Gail Badner
 */
public class SubselectFetchCollectionFromBatchTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10679")
    public void testSubselectFetchFromEntityBatch() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        SubselectFetchCollectionFromBatchTest.EmployeeGroup group1 = new SubselectFetchCollectionFromBatchTest.EmployeeGroup();
        SubselectFetchCollectionFromBatchTest.Employee employee1 = new SubselectFetchCollectionFromBatchTest.Employee("Jane");
        SubselectFetchCollectionFromBatchTest.Employee employee2 = new SubselectFetchCollectionFromBatchTest.Employee("Jeff");
        group1.addEmployee(employee1);
        group1.addEmployee(employee2);
        SubselectFetchCollectionFromBatchTest.EmployeeGroup group2 = new SubselectFetchCollectionFromBatchTest.EmployeeGroup();
        SubselectFetchCollectionFromBatchTest.Employee employee3 = new SubselectFetchCollectionFromBatchTest.Employee("Joan");
        SubselectFetchCollectionFromBatchTest.Employee employee4 = new SubselectFetchCollectionFromBatchTest.Employee("John");
        group2.addEmployee(employee3);
        group2.addEmployee(employee4);
        s.save(group1);
        s.save(group2);
        s.flush();
        s.clear();
        sessionFactory().getStatistics().clear();
        SubselectFetchCollectionFromBatchTest.EmployeeGroup[] groups = new SubselectFetchCollectionFromBatchTest.EmployeeGroup[]{ ((SubselectFetchCollectionFromBatchTest.EmployeeGroup) (s.load(SubselectFetchCollectionFromBatchTest.EmployeeGroup.class, group1.getId()))), ((SubselectFetchCollectionFromBatchTest.EmployeeGroup) (s.load(SubselectFetchCollectionFromBatchTest.EmployeeGroup.class, group2.getId()))) };
        // groups should only contain proxies
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        for (SubselectFetchCollectionFromBatchTest.EmployeeGroup group : groups) {
            Assert.assertFalse(Hibernate.isInitialized(group));
        }
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        for (int i = 0; i < (groups.length); i++) {
            // Both groups get initialized  and are added to the PersistenceContext when i == 0;
            // Still need to call Hibernate.initialize( groups[i] ) for i > 0 so that the entity
            // in the PersistenceContext gets assigned to its respective proxy target (is this a
            // bug???)
            Hibernate.initialize(groups[i]);
            Assert.assertTrue(Hibernate.isInitialized(groups[i]));
            // the collections should be uninitialized
            Assert.assertFalse(Hibernate.isInitialized(groups[i].getEmployees()));
        }
        // both Group proxies should have been loaded in the same batch;
        Assert.assertEquals(1, sessionFactory().getStatistics().getPrepareStatementCount());
        sessionFactory().getStatistics().clear();
        for (SubselectFetchCollectionFromBatchTest.EmployeeGroup group : groups) {
            Assert.assertTrue(Hibernate.isInitialized(group));
            Assert.assertFalse(Hibernate.isInitialized(group.getEmployees()));
        }
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        // now initialize the collection in the first; collections in both groups
        // should get initialized
        Hibernate.initialize(groups[0].getEmployees());
        Assert.assertEquals(1, sessionFactory().getStatistics().getPrepareStatementCount());
        sessionFactory().getStatistics().clear();
        // all collections should be initialized now
        for (SubselectFetchCollectionFromBatchTest.EmployeeGroup group : groups) {
            Assert.assertTrue(Hibernate.isInitialized(group.getEmployees()));
        }
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        t.rollback();
        s.close();
    }

    @Test
    public void testSubselectFetchFromQueryList() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        SubselectFetchCollectionFromBatchTest.EmployeeGroup group1 = new SubselectFetchCollectionFromBatchTest.EmployeeGroup();
        SubselectFetchCollectionFromBatchTest.Employee employee1 = new SubselectFetchCollectionFromBatchTest.Employee("Jane");
        SubselectFetchCollectionFromBatchTest.Employee employee2 = new SubselectFetchCollectionFromBatchTest.Employee("Jeff");
        group1.addEmployee(employee1);
        group1.addEmployee(employee2);
        SubselectFetchCollectionFromBatchTest.EmployeeGroup group2 = new SubselectFetchCollectionFromBatchTest.EmployeeGroup();
        SubselectFetchCollectionFromBatchTest.Employee employee3 = new SubselectFetchCollectionFromBatchTest.Employee("Joan");
        SubselectFetchCollectionFromBatchTest.Employee employee4 = new SubselectFetchCollectionFromBatchTest.Employee("John");
        group2.addEmployee(employee3);
        group2.addEmployee(employee4);
        s.save(group1);
        s.save(group2);
        s.flush();
        s.clear();
        sessionFactory().getStatistics().clear();
        List<SubselectFetchCollectionFromBatchTest.EmployeeGroup> results = s.createQuery("from SubselectFetchCollectionFromBatchTest$EmployeeGroup where id in :groups").setParameterList("groups", new Long[]{ group1.getId(), group2.getId() }).list();
        Assert.assertEquals(1, sessionFactory().getStatistics().getPrepareStatementCount());
        sessionFactory().getStatistics().clear();
        for (SubselectFetchCollectionFromBatchTest.EmployeeGroup group : results) {
            Assert.assertTrue(Hibernate.isInitialized(group));
            Assert.assertFalse(Hibernate.isInitialized(group.getEmployees()));
        }
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        // now initialize the collection in the first; collections in both groups
        // should get initialized
        Hibernate.initialize(results.get(0).getEmployees());
        Assert.assertEquals(1, sessionFactory().getStatistics().getPrepareStatementCount());
        sessionFactory().getStatistics().clear();
        // all collections should be initialized now
        for (SubselectFetchCollectionFromBatchTest.EmployeeGroup group : results) {
            Assert.assertTrue(Hibernate.isInitialized(group.getEmployees()));
        }
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        t.rollback();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10679")
    public void testMultiSubselectFetchSamePersisterQueryList() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        SubselectFetchCollectionFromBatchTest.EmployeeGroup group1 = new SubselectFetchCollectionFromBatchTest.EmployeeGroup();
        SubselectFetchCollectionFromBatchTest.Employee employee1 = new SubselectFetchCollectionFromBatchTest.Employee("Jane");
        SubselectFetchCollectionFromBatchTest.Employee employee2 = new SubselectFetchCollectionFromBatchTest.Employee("Jeff");
        group1.addEmployee(employee1);
        group1.addEmployee(employee2);
        group1.setManager(new SubselectFetchCollectionFromBatchTest.Employee("group1 manager"));
        group1.getManager().addCollaborator(new SubselectFetchCollectionFromBatchTest.Employee("group1 manager's collaborator#1"));
        group1.getManager().addCollaborator(new SubselectFetchCollectionFromBatchTest.Employee("group1 manager's collaborator#2"));
        group1.setLead(new SubselectFetchCollectionFromBatchTest.Employee("group1 lead"));
        group1.getLead().addCollaborator(new SubselectFetchCollectionFromBatchTest.Employee("group1 lead's collaborator#1"));
        SubselectFetchCollectionFromBatchTest.EmployeeGroup group2 = new SubselectFetchCollectionFromBatchTest.EmployeeGroup();
        SubselectFetchCollectionFromBatchTest.Employee employee3 = new SubselectFetchCollectionFromBatchTest.Employee("Joan");
        SubselectFetchCollectionFromBatchTest.Employee employee4 = new SubselectFetchCollectionFromBatchTest.Employee("John");
        group2.addEmployee(employee3);
        group2.addEmployee(employee4);
        group2.setManager(new SubselectFetchCollectionFromBatchTest.Employee("group2 manager"));
        group2.getManager().addCollaborator(new SubselectFetchCollectionFromBatchTest.Employee("group2 manager's collaborator#1"));
        group2.getManager().addCollaborator(new SubselectFetchCollectionFromBatchTest.Employee("group2 manager's collaborator#2"));
        group2.getManager().addCollaborator(new SubselectFetchCollectionFromBatchTest.Employee("group2 manager's collaborator#3"));
        group2.setLead(new SubselectFetchCollectionFromBatchTest.Employee("group2 lead"));
        group2.getLead().addCollaborator(new SubselectFetchCollectionFromBatchTest.Employee("group2 lead's collaborator#1"));
        group2.getLead().addCollaborator(new SubselectFetchCollectionFromBatchTest.Employee("group2 lead's collaborator#2"));
        s.save(group1);
        s.save(group2);
        s.flush();
        s.clear();
        sessionFactory().getStatistics().clear();
        SubselectFetchCollectionFromBatchTest.EmployeeGroup[] groups = new SubselectFetchCollectionFromBatchTest.EmployeeGroup[]{ ((SubselectFetchCollectionFromBatchTest.EmployeeGroup) (s.load(SubselectFetchCollectionFromBatchTest.EmployeeGroup.class, group1.getId()))), ((SubselectFetchCollectionFromBatchTest.EmployeeGroup) (s.load(SubselectFetchCollectionFromBatchTest.EmployeeGroup.class, group2.getId()))) };
        // groups should only contain proxies
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        for (SubselectFetchCollectionFromBatchTest.EmployeeGroup group : groups) {
            Assert.assertFalse(Hibernate.isInitialized(group));
        }
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        for (int i = 0; i < (groups.length); i++) {
            // Both groups get initialized  and are added to the PersistenceContext when i == 0;
            // Still need to call Hibernate.initialize( groups[i] ) for i > 0 so that the entity
            // in the PersistenceContext gets assigned to its respective proxy target (is this a
            // bug???)
            Hibernate.initialize(groups[i]);
            Assert.assertTrue(Hibernate.isInitialized(groups[i]));
            Assert.assertTrue(Hibernate.isInitialized(groups[i].getLead()));
            Assert.assertFalse(Hibernate.isInitialized(groups[i].getLead().getCollaborators()));
            Assert.assertTrue(Hibernate.isInitialized(groups[i].getManager()));
            Assert.assertFalse(Hibernate.isInitialized(groups[i].getManager().getCollaborators()));
            // the collections should be uninitialized
            Assert.assertFalse(Hibernate.isInitialized(groups[i].getEmployees()));
        }
        // both Group proxies should have been loaded in the same batch;
        Assert.assertEquals(1, sessionFactory().getStatistics().getPrepareStatementCount());
        sessionFactory().getStatistics().clear();
        for (SubselectFetchCollectionFromBatchTest.EmployeeGroup group : groups) {
            Assert.assertTrue(Hibernate.isInitialized(group));
            Assert.assertFalse(Hibernate.isInitialized(group.getEmployees()));
        }
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        // now initialize the collection in the first; collections in both groups
        // should get initialized
        Hibernate.initialize(groups[0].getEmployees());
        Assert.assertEquals(1, sessionFactory().getStatistics().getPrepareStatementCount());
        sessionFactory().getStatistics().clear();
        // all EmployeeGroup#employees should be initialized now
        for (SubselectFetchCollectionFromBatchTest.EmployeeGroup group : groups) {
            Assert.assertTrue(Hibernate.isInitialized(group.getEmployees()));
            Assert.assertFalse(Hibernate.isInitialized(group.getLead().getCollaborators()));
            Assert.assertFalse(Hibernate.isInitialized(group.getManager().getCollaborators()));
        }
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        // now initialize groups[0].getLead().getCollaborators();
        // groups[1].getLead().getCollaborators() should also be initialized
        Hibernate.initialize(groups[0].getLead().getCollaborators());
        Assert.assertEquals(1, sessionFactory().getStatistics().getPrepareStatementCount());
        sessionFactory().getStatistics().clear();
        for (SubselectFetchCollectionFromBatchTest.EmployeeGroup group : groups) {
            Assert.assertTrue(Hibernate.isInitialized(group.getLead().getCollaborators()));
            Assert.assertFalse(Hibernate.isInitialized(group.getManager().getCollaborators()));
        }
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        // now initialize groups[0].getManager().getCollaborators();
        // groups[1].getManager().getCollaborators() should also be initialized
        Hibernate.initialize(groups[0].getManager().getCollaborators());
        Assert.assertEquals(1, sessionFactory().getStatistics().getPrepareStatementCount());
        sessionFactory().getStatistics().clear();
        for (SubselectFetchCollectionFromBatchTest.EmployeeGroup group : groups) {
            Assert.assertTrue(Hibernate.isInitialized(group.getManager().getCollaborators()));
        }
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        Assert.assertEquals(group1.getLead().getCollaborators().size(), groups[0].getLead().getCollaborators().size());
        Assert.assertEquals(group2.getLead().getCollaborators().size(), groups[1].getLead().getCollaborators().size());
        Assert.assertEquals(group1.getManager().getCollaborators().size(), groups[0].getManager().getCollaborators().size());
        Assert.assertEquals(group2.getManager().getCollaborators().size(), groups[1].getManager().getCollaborators().size());
        Assert.assertEquals(0, sessionFactory().getStatistics().getPrepareStatementCount());
        t.rollback();
        s.close();
    }

    @Entity
    @Table(name = "EmployeeGroup")
    @BatchSize(size = 1000)
    private static class EmployeeGroup {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        private SubselectFetchCollectionFromBatchTest.Employee manager;

        @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        private SubselectFetchCollectionFromBatchTest.Employee lead;

        @OneToMany(cascade = CascadeType.ALL)
        @Fetch(FetchMode.SUBSELECT)
        @JoinTable(name = "EmployeeGroup_employees")
        private List<SubselectFetchCollectionFromBatchTest.Employee> employees = new ArrayList<SubselectFetchCollectionFromBatchTest.Employee>();

        public EmployeeGroup(long id) {
            this.id = id;
        }

        @SuppressWarnings("unused")
        protected EmployeeGroup() {
        }

        public SubselectFetchCollectionFromBatchTest.Employee getManager() {
            return manager;
        }

        public void setManager(SubselectFetchCollectionFromBatchTest.Employee manager) {
            this.manager = manager;
        }

        public SubselectFetchCollectionFromBatchTest.Employee getLead() {
            return lead;
        }

        public void setLead(SubselectFetchCollectionFromBatchTest.Employee lead) {
            this.lead = lead;
        }

        public boolean addEmployee(SubselectFetchCollectionFromBatchTest.Employee employee) {
            return employees.add(employee);
        }

        public List<SubselectFetchCollectionFromBatchTest.Employee> getEmployees() {
            return employees;
        }

        public long getId() {
            return id;
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }

    @Entity
    @Table(name = "Employee")
    private static class Employee {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        @OneToMany(cascade = CascadeType.ALL)
        @Fetch(FetchMode.SUBSELECT)
        private List<SubselectFetchCollectionFromBatchTest.Employee> collaborators = new ArrayList<SubselectFetchCollectionFromBatchTest.Employee>();

        public String getName() {
            return name;
        }

        @SuppressWarnings("unused")
        private Employee() {
        }

        public Employee(String name) {
            this.name = name;
        }

        public boolean addCollaborator(SubselectFetchCollectionFromBatchTest.Employee employee) {
            return collaborators.add(employee);
        }

        public List<SubselectFetchCollectionFromBatchTest.Employee> getCollaborators() {
            return collaborators;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

