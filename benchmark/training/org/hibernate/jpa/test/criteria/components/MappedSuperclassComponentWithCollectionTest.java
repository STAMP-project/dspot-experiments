/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.components;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-6562")
public class MappedSuperclassComponentWithCollectionTest extends BaseEntityManagerFunctionalTestCase {
    private MappedSuperclassComponentWithCollectionTest.Projects projects;

    @Test
    public void testSizeExpressionForTheOneToManyPropertyOfAComponent() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.components.Manager> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.components.Manager> root = query.from(.class);
            query.where(builder.equal(builder.size(root.get("projects").get("previousProjects")), 2));
            final List<org.hibernate.jpa.test.criteria.components.Manager> results = entityManager.createQuery(query).getResultList();
            assertThat(results.size(), is(1));
        });
    }

    @Test
    public void testSizeExpressionForTheElementCollectionPropertyOfAComponent() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.components.Manager> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.components.Manager> root = query.from(.class);
            query.where(builder.equal(builder.size(root.get("contactDetail").get("phones")), 1));
            final List<org.hibernate.jpa.test.criteria.components.Manager> results = entityManager.createQuery(query).getResultList();
            assertThat(results.size(), is(1));
        });
    }

    @Test
    public void testSizeExpressionForTheElementCollectionPropertyOfASubComponent() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.components.Leader> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.components.Leader> root = query.from(.class);
            query.where(builder.equal(builder.size(root.get("information").get("infoContactDetail").get("phones")), 1));
            final List<org.hibernate.jpa.test.criteria.components.Leader> results = entityManager.createQuery(query).getResultList();
            assertThat(results.size(), is(1));
        });
    }

    @MappedSuperclass
    public abstract static class AbstractEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        protected Long id;

        public Long getId() {
            return id;
        }
    }

    @MappedSuperclass
    public static class Employee extends MappedSuperclassComponentWithCollectionTest.AbstractEntity {
        @Embedded
        private MappedSuperclassComponentWithCollectionTest.Projects projects;

        @Embedded
        private MappedSuperclassComponentWithCollectionTest.ContactDetail contactDetail;

        public void setProjects(MappedSuperclassComponentWithCollectionTest.Projects projects) {
            this.projects = projects;
        }

        public void setContactDetail(MappedSuperclassComponentWithCollectionTest.ContactDetail contactDetail) {
            this.contactDetail = contactDetail;
        }
    }

    @Entity(name = "Manager")
    @Table(name = "MANAGER")
    public static class Manager extends MappedSuperclassComponentWithCollectionTest.Employee {}

    @Embeddable
    public static class ContactDetail {
        private String email;

        @ElementCollection
        private java.util.List<MappedSuperclassComponentWithCollectionTest.Phone> phones = new ArrayList<>();

        public void addPhone(MappedSuperclassComponentWithCollectionTest.Phone phone) {
            this.phones.add(phone);
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    @Embeddable
    public static class Projects {
        @OneToMany(cascade = CascadeType.PERSIST)
        private Set<MappedSuperclassComponentWithCollectionTest.Project> previousProjects = new HashSet<>();

        @ManyToOne(cascade = CascadeType.PERSIST)
        private MappedSuperclassComponentWithCollectionTest.Project currentProject;

        public void setCurrentProject(MappedSuperclassComponentWithCollectionTest.Project project) {
            this.currentProject = project;
        }

        public void addPreviousProject(MappedSuperclassComponentWithCollectionTest.Project project) {
            this.previousProjects.add(project);
        }

        public Set<MappedSuperclassComponentWithCollectionTest.Project> getPreviousProjects() {
            return previousProjects;
        }

        public MappedSuperclassComponentWithCollectionTest.Project getCurrentProject() {
            return currentProject;
        }
    }

    @Entity(name = "Project")
    @Table(name = "PROJECT")
    public static class Project extends MappedSuperclassComponentWithCollectionTest.AbstractEntity {
        public Project() {
        }

        public Project(String name) {
            this.name = name;
        }

        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    @Embeddable
    public static class Phone {
        @Column(name = "phone_number")
        private String number;

        public Phone() {
        }

        public Phone(String number) {
            this.number = number;
        }

        public String getNumber() {
            return this.number;
        }
    }

    @MappedSuperclass
    public static class Person extends MappedSuperclassComponentWithCollectionTest.AbstractEntity {
        @Embedded
        private MappedSuperclassComponentWithCollectionTest.Information information;

        public MappedSuperclassComponentWithCollectionTest.Information getInformation() {
            return information;
        }

        public void setInformation(MappedSuperclassComponentWithCollectionTest.Information information) {
            this.information = information;
        }
    }

    @Entity
    public static class Dummy1 extends MappedSuperclassComponentWithCollectionTest.Person {}

    @MappedSuperclass
    public static class Dummy2 extends MappedSuperclassComponentWithCollectionTest.Dummy1 {}

    @Entity(name = "Leader")
    @Table(name = "LEADER")
    public static class Leader extends MappedSuperclassComponentWithCollectionTest.Person {}

    @Embeddable
    public static class Information {
        @Embedded
        private MappedSuperclassComponentWithCollectionTest.ContactDetail infoContactDetail;

        public MappedSuperclassComponentWithCollectionTest.ContactDetail getInfoContactDetail() {
            return infoContactDetail;
        }

        public void setInfoContactDetail(MappedSuperclassComponentWithCollectionTest.ContactDetail infoContactDetail) {
            this.infoContactDetail = infoContactDetail;
        }
    }
}

