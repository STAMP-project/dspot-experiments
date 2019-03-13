/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.graphs.queryhint;


import QueryHints.HINT_LOADGRAPH;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Subgraph;
import org.hibernate.Hibernate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.jpa.test.graphs.Company;
import org.hibernate.jpa.test.graphs.Course;
import org.hibernate.jpa.test.graphs.Employee;
import org.hibernate.jpa.test.graphs.Location;
import org.hibernate.jpa.test.graphs.Manager;
import org.hibernate.jpa.test.graphs.Student;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Brett Meyer
 */
public class QueryHintEntityGraphTest extends BaseEntityManagerFunctionalTestCase {
    // TODO: Currently, "loadgraph" and "fetchgraph" operate identically in JPQL.  The spec states that "fetchgraph"
    // shall use LAZY for non-specified attributes, ignoring their metadata.  Changes to ToOne select vs. join,
    // allowing queries to force laziness, etc. will require changes here and impl logic.
    @Test
    public void testLoadGraph() {
        EntityManager entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        EntityGraph<Company> entityGraph = entityManager.createEntityGraph(Company.class);
        entityGraph.addAttributeNodes("location");
        entityGraph.addAttributeNodes("markets");
        Query query = entityManager.createQuery(("from " + (Company.class.getName())));
        query.setHint(HINT_LOADGRAPH, entityGraph);
        Company company = ((Company) (query.getSingleResult()));
        entityManager.getTransaction().commit();
        entityManager.close();
        Assert.assertFalse(Hibernate.isInitialized(company.employees));
        Assert.assertTrue(Hibernate.isInitialized(company.location));
        Assert.assertTrue(Hibernate.isInitialized(company.markets));
        // With "loadgraph", non-specified attributes use the fetch modes defined in the mappings.  So, here,
        // @ElementCollection(fetch = FetchType.EAGER) should cause the follow-on selects to happen.
        Assert.assertTrue(Hibernate.isInitialized(company.phoneNumbers));
        entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        Subgraph<Employee> subgraph = entityGraph.addSubgraph("employees");
        subgraph.addAttributeNodes("managers");
        subgraph.addAttributeNodes("friends");
        Subgraph<Manager> subSubgraph = subgraph.addSubgraph("managers", Manager.class);
        subSubgraph.addAttributeNodes("managers");
        subSubgraph.addAttributeNodes("friends");
        query = entityManager.createQuery(("from " + (Company.class.getName())));
        query.setHint(HINT_LOADGRAPH, entityGraph);
        company = ((Company) (query.getSingleResult()));
        entityManager.getTransaction().commit();
        entityManager.close();
        Assert.assertTrue(Hibernate.isInitialized(company.employees));
        Assert.assertTrue(Hibernate.isInitialized(company.location));
        Assert.assertEquals(12345, company.location.zip);
        Assert.assertTrue(Hibernate.isInitialized(company.markets));
        // With "loadgraph", non-specified attributes use the fetch modes defined in the mappings.  So, here,
        // @ElementCollection(fetch = FetchType.EAGER) should cause the follow-on selects to happen.
        Assert.assertTrue(Hibernate.isInitialized(company.phoneNumbers));
        boolean foundManager = false;
        Iterator<Employee> employeeItr = company.employees.iterator();
        while (employeeItr.hasNext()) {
            Employee employee = employeeItr.next();
            Assert.assertTrue(Hibernate.isInitialized(employee.managers));
            Assert.assertTrue(Hibernate.isInitialized(employee.friends));
            // test 1 more level
            Iterator<Manager> managerItr = employee.managers.iterator();
            while (managerItr.hasNext()) {
                foundManager = true;
                Manager manager = managerItr.next();
                Assert.assertTrue(Hibernate.isInitialized(manager.managers));
                Assert.assertTrue(Hibernate.isInitialized(manager.friends));
            } 
        } 
        Assert.assertTrue(foundManager);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9457")
    public void testLoadGraphOrderByWithImplicitJoin() {
        EntityManager entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        // create a new Company at a different location in a different zip code
        Location location = new Location();
        location.address = "123 somewhere";
        location.zip = 11234;
        entityManager.persist(location);
        Company companyNew = new Company();
        companyNew.location = location;
        entityManager.persist(companyNew);
        entityManager.getTransaction().commit();
        entityManager.close();
        entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        EntityGraph<Company> entityGraph = entityManager.createEntityGraph(Company.class);
        // entityGraph.addAttributeNodes( "location" );
        entityGraph.addAttributeNodes("markets");
        Query query = entityManager.createQuery((("from " + (Company.class.getName())) + " c order by c.location.zip, c.id"));
        query.setHint(HINT_LOADGRAPH, entityGraph);
        List results = query.getResultList();
        // - 1st will be the Company with location.zip == 11234 with an empty markets collection
        // - 2nd should be the Company with location.zip == 12345
        Assert.assertEquals(2, results.size());
        Company companyResult = ((Company) (results.get(0)));
        Assert.assertFalse(Hibernate.isInitialized(companyResult.employees));
        Assert.assertFalse(Hibernate.isInitialized(companyResult.location));
        // initialize and check zip
        // TODO: must have getters to access lazy entity after being initialized (why?)
        // assertEquals( 11234, companyResult.location.zip );
        Assert.assertEquals(11234, companyResult.getLocation().getZip());
        Assert.assertTrue(Hibernate.isInitialized(companyResult.markets));
        Assert.assertEquals(0, companyResult.markets.size());
        // With "loadgraph", non-specified attributes use the fetch modes defined in the mappings.  So, here,
        // @ElementCollection(fetch = FetchType.EAGER) should cause the follow-on selects to happen.
        Assert.assertTrue(Hibernate.isInitialized(companyResult.phoneNumbers));
        Assert.assertEquals(0, companyResult.phoneNumbers.size());
        companyResult = ((Company) (results.get(1)));
        Assert.assertFalse(Hibernate.isInitialized(companyResult.employees));
        Assert.assertFalse(Hibernate.isInitialized(companyResult.location));
        // initialize and check zip
        // TODO: must have getters to access lazy entity after being initialized (why?)
        // assertEquals( 12345, companyResult.location.zip );
        Assert.assertEquals(12345, companyResult.getLocation().getZip());
        Assert.assertTrue(Hibernate.isInitialized(companyResult.markets));
        Assert.assertEquals(2, companyResult.markets.size());
        // With "loadgraph", non-specified attributes use the fetch modes defined in the mappings.  So, here,
        // @ElementCollection(fetch = FetchType.EAGER) should cause the follow-on selects to happen.
        Assert.assertTrue(Hibernate.isInitialized(companyResult.phoneNumbers));
        Assert.assertEquals(2, companyResult.phoneNumbers.size());
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9448")
    public void testLoadGraphWithRestriction() {
        EntityManager entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        EntityGraph<Company> entityGraph = entityManager.createEntityGraph(Company.class);
        entityGraph.addAttributeNodes("location");
        entityGraph.addAttributeNodes("markets");
        Query query = entityManager.createQuery((("from " + (Company.class.getName())) + " where location.zip = :zip")).setParameter("zip", 12345);
        query.setHint(HINT_LOADGRAPH, entityGraph);
        Company company = ((Company) (query.getSingleResult()));
        entityManager.getTransaction().commit();
        entityManager.close();
        Assert.assertFalse(Hibernate.isInitialized(company.employees));
        Assert.assertTrue(Hibernate.isInitialized(company.location));
        Assert.assertTrue(Hibernate.isInitialized(company.markets));
        // With "loadgraph", non-specified attributes use the fetch modes defined in the mappings.  So, here,
        // @ElementCollection(fetch = FetchType.EAGER) should cause the follow-on selects to happen.
        Assert.assertTrue(Hibernate.isInitialized(company.phoneNumbers));
        entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        Subgraph<Employee> subgraph = entityGraph.addSubgraph("employees");
        subgraph.addAttributeNodes("managers");
        subgraph.addAttributeNodes("friends");
        Subgraph<Manager> subSubgraph = subgraph.addSubgraph("managers", Manager.class);
        subSubgraph.addAttributeNodes("managers");
        subSubgraph.addAttributeNodes("friends");
        query = entityManager.createQuery((("from " + (Company.class.getName())) + " where location.zip = :zip")).setParameter("zip", 12345);
        query.setHint(HINT_LOADGRAPH, entityGraph);
        company = ((Company) (query.getSingleResult()));
        entityManager.getTransaction().commit();
        entityManager.close();
        Assert.assertTrue(Hibernate.isInitialized(company.employees));
        Assert.assertTrue(Hibernate.isInitialized(company.location));
        Assert.assertTrue(Hibernate.isInitialized(company.markets));
        // With "loadgraph", non-specified attributes use the fetch modes defined in the mappings.  So, here,
        // @ElementCollection(fetch = FetchType.EAGER) should cause the follow-on selects to happen.
        Assert.assertTrue(Hibernate.isInitialized(company.phoneNumbers));
        boolean foundManager = false;
        Iterator<Employee> employeeItr = company.employees.iterator();
        while (employeeItr.hasNext()) {
            Employee employee = employeeItr.next();
            Assert.assertTrue(Hibernate.isInitialized(employee.managers));
            Assert.assertTrue(Hibernate.isInitialized(employee.friends));
            // test 1 more level
            Iterator<Manager> managerItr = employee.managers.iterator();
            while (managerItr.hasNext()) {
                foundManager = true;
                Manager manager = managerItr.next();
                Assert.assertTrue(Hibernate.isInitialized(manager.managers));
                Assert.assertTrue(Hibernate.isInitialized(manager.friends));
            } 
        } 
        Assert.assertTrue(foundManager);
    }

    @Test
    public void testEntityGraphWithExplicitFetch() {
        EntityManager entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        EntityGraph<Company> entityGraph = entityManager.createEntityGraph(Company.class);
        entityGraph.addAttributeNodes("location");
        entityGraph.addAttributeNodes("markets");
        entityGraph.addAttributeNodes("employees");
        // Ensure the EntityGraph and explicit fetches do not conflict.
        Query query = entityManager.createQuery((("from " + (Company.class.getName())) + " as c left join fetch c.location left join fetch c.employees"));
        query.setHint(HINT_LOADGRAPH, entityGraph);
        Company company = ((Company) (query.getSingleResult()));
        entityManager.getTransaction().commit();
        entityManager.close();
        Assert.assertTrue(Hibernate.isInitialized(company.employees));
        Assert.assertTrue(Hibernate.isInitialized(company.location));
        Assert.assertTrue(Hibernate.isInitialized(company.markets));
        // With "loadgraph", non-specified attributes use the fetch modes defined in the mappings.  So, here,
        // @ElementCollection(fetch = FetchType.EAGER) should cause the follow-on selects to happen.
        Assert.assertTrue(Hibernate.isInitialized(company.phoneNumbers));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9448")
    public void testEntityGraphWithExplicitFetchAndRestriction() {
        EntityManager entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        EntityGraph<Company> entityGraph = entityManager.createEntityGraph(Company.class);
        entityGraph.addAttributeNodes("location");
        entityGraph.addAttributeNodes("markets");
        entityGraph.addAttributeNodes("employees");
        // Ensure the EntityGraph and explicit fetches do not conflict.
        Query query = entityManager.createQuery((("from " + (Company.class.getName())) + " as c left join fetch c.location left join fetch c.employees where c.location.zip = :zip")).setParameter("zip", 12345);
        query.setHint(HINT_LOADGRAPH, entityGraph);
        Company company = ((Company) (query.getSingleResult()));
        entityManager.getTransaction().commit();
        entityManager.close();
        Assert.assertTrue(Hibernate.isInitialized(company.employees));
        Assert.assertTrue(Hibernate.isInitialized(company.location));
        Assert.assertTrue(Hibernate.isInitialized(company.markets));
        // With "loadgraph", non-specified attributes use the fetch modes defined in the mappings.  So, here,
        // @ElementCollection(fetch = FetchType.EAGER) should cause the follow-on selects to happen.
        Assert.assertTrue(Hibernate.isInitialized(company.phoneNumbers));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9374")
    public void testEntityGraphWithCollectionSubquery() {
        EntityManager entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        EntityGraph<Company> entityGraph = entityManager.createEntityGraph(Company.class);
        entityGraph.addAttributeNodes("location");
        Query query = entityManager.createQuery((("select c from " + (Company.class.getName())) + " c where c.employees IS EMPTY"));
        query.setHint(HINT_LOADGRAPH, entityGraph);
        query.getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11569")
    public void testCollectionSizeLoadedWithGraph() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Student student1 = new Student();
            student1.setId(1);
            student1.setName("Student 1");
            Student student2 = new Student();
            student2.setId(2);
            student2.setName("Student 2");
            Course course1 = new Course();
            course1.setName("Full Time");
            Course course2 = new Course();
            course2.setName("Part Time");
            Set<Course> std1Courses = new HashSet<Course>();
            std1Courses.add(course1);
            std1Courses.add(course2);
            student1.setCourses(std1Courses);
            Set<Course> std2Courses = new HashSet<Course>();
            std2Courses.add(course2);
            student2.setCourses(std2Courses);
            entityManager.persist(student1);
            entityManager.persist(student2);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            EntityGraph<?> graph = entityManager.getEntityGraph("Student.Full");
            List<Student> students = entityManager.createNamedQuery("LIST_OF_STD", .class).setHint(QueryHints.HINT_FETCHGRAPH, graph).getResultList();
            assertEquals(2, students.size());
        });
    }
}

