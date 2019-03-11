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
public class ComponentInWhereClauseTest extends BaseEntityManagerFunctionalTestCase {
    private ComponentInWhereClauseTest.Projects projects;

    @Test
    public void testSizeExpressionForTheOneToManyPropertyOfAComponent() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.components.Employee> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.components.Employee> root = query.from(.class);
            query.where(builder.equal(builder.size(root.get("projects").get("previousProjects")), 2));
            final List<org.hibernate.jpa.test.criteria.components.Employee> results = entityManager.createQuery(query).getResultList();
            assertThat(results.size(), is(1));
        });
    }

    @Test
    public void testSizeExpressionForTheElementCollectionPropertyOfAComponent() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.components.Employee> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.components.Employee> root = query.from(.class);
            query.where(builder.equal(builder.size(root.get("contactDetail").get("phones")), 1));
            final List<org.hibernate.jpa.test.criteria.components.Employee> results = entityManager.createQuery(query).getResultList();
            assertThat(results.size(), is(1));
        });
    }

    @Test
    public void testSizeExpressionForTheElementCollectionPropertyOfASubComponent() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.components.Person> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.components.Person> root = query.from(.class);
            query.where(builder.equal(builder.size(root.get("information").get("infoContactDetail").get("phones")), 1));
            final List<org.hibernate.jpa.test.criteria.components.Person> results = entityManager.createQuery(query).getResultList();
            assertThat(results.size(), is(1));
        });
    }

    @Test
    public void testEqualExpressionForThePropertyOfTheElementCollectionPropertyOfAComponent() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.components.Employee> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.components.Employee> root = query.from(.class);
            query.where(builder.equal(root.join("contactDetail").join("phones").get("number"), "+4411111111"));
            final List<org.hibernate.jpa.test.criteria.components.Employee> results = entityManager.createQuery(query).getResultList();
            assertThat(results.size(), is(1));
        });
    }

    @Test
    public void testEqualityForThePropertyOfAComponent() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.components.Employee> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.components.Employee> root = query.from(.class);
            query.where(builder.equal(root.join("contactDetail").get("email"), "abc@mail.org"));
            final List<org.hibernate.jpa.test.criteria.components.Employee> results = entityManager.createQuery(query).getResultList();
            assertThat(results.size(), is(1));
        });
    }

    @Test
    public void testInExpressionForAComponent() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.components.Employee> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.components.Employee> root = query.from(.class);
            query.where(root.get("projects").in(projects, new org.hibernate.jpa.test.criteria.components.Projects()));
            final List<org.hibernate.jpa.test.criteria.components.Employee> results = entityManager.createQuery(query).getResultList();
            assertThat(results.size(), is(1));
        });
    }

    @Test
    public void testInExpressionForTheManyToOnePropertyOfAComponent() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.components.Employee> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.components.Employee> root = query.from(.class);
            query.where(root.get("projects").get("currentProject").in(projects.getCurrentProject()));
            final List<org.hibernate.jpa.test.criteria.components.Employee> results = entityManager.createQuery(query).getResultList();
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

    @Entity(name = "Employee")
    @Table(name = "EMPLOYEE")
    public static class Employee extends ComponentInWhereClauseTest.AbstractEntity {
        @Embedded
        private ComponentInWhereClauseTest.Projects projects;

        @Embedded
        private ComponentInWhereClauseTest.ContactDetail contactDetail;

        public void setProjects(ComponentInWhereClauseTest.Projects projects) {
            this.projects = projects;
        }

        public void setContactDetail(ComponentInWhereClauseTest.ContactDetail contactDetail) {
            this.contactDetail = contactDetail;
        }
    }

    @Embeddable
    public static class ContactDetail {
        private String email;

        @ElementCollection
        private java.util.List<ComponentInWhereClauseTest.Phone> phones = new ArrayList<>();

        public void addPhone(ComponentInWhereClauseTest.Phone phone) {
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
        private Set<ComponentInWhereClauseTest.Project> previousProjects = new HashSet<>();

        @ManyToOne(cascade = CascadeType.PERSIST)
        private ComponentInWhereClauseTest.Project currentProject;

        public void setCurrentProject(ComponentInWhereClauseTest.Project project) {
            this.currentProject = project;
        }

        public void addPreviousProject(ComponentInWhereClauseTest.Project project) {
            this.previousProjects.add(project);
        }

        public Set<ComponentInWhereClauseTest.Project> getPreviousProjects() {
            return previousProjects;
        }

        public ComponentInWhereClauseTest.Project getCurrentProject() {
            return currentProject;
        }
    }

    @Entity(name = "Project")
    @Table(name = "PROJECT")
    public static class Project extends ComponentInWhereClauseTest.AbstractEntity {
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

    @Entity(name = "Person")
    @Table(name = "PERSON")
    public static class Person extends ComponentInWhereClauseTest.AbstractEntity {
        @Embedded
        private ComponentInWhereClauseTest.Information information;

        public ComponentInWhereClauseTest.Information getInformation() {
            return information;
        }

        public void setInformation(ComponentInWhereClauseTest.Information information) {
            this.information = information;
        }
    }

    @Embeddable
    public static class Information {
        @Embedded
        private ComponentInWhereClauseTest.ContactDetail infoContactDetail;

        public ComponentInWhereClauseTest.ContactDetail getInfoContactDetail() {
            return infoContactDetail;
        }

        public void setInfoContactDetail(ComponentInWhereClauseTest.ContactDetail infoContactDetail) {
            this.infoContactDetail = infoContactDetail;
        }
    }
}

