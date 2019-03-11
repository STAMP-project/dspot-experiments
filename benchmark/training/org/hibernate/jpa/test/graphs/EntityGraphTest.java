/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.graphs;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Subgraph;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Christian Bauer
 * @author Brett Meyer
 */
public class EntityGraphTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-8857")
    public void loadMultipleAssociations() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        EntityGraphTest.Bar bar = new EntityGraphTest.Bar();
        em.persist(bar);
        EntityGraphTest.Baz baz = new EntityGraphTest.Baz();
        em.persist(baz);
        EntityGraphTest.Foo foo = new EntityGraphTest.Foo();
        foo.bar = bar;
        foo.baz = baz;
        em.persist(foo);
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        EntityGraph<EntityGraphTest.Foo> fooGraph = em.createEntityGraph(EntityGraphTest.Foo.class);
        fooGraph.addAttributeNodes("bar", "baz");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("javax.persistence.loadgraph", fooGraph);
        EntityGraphTest.Foo result = em.find(EntityGraphTest.Foo.class, foo.id, properties);
        Assert.assertTrue(Hibernate.isInitialized(result));
        Assert.assertTrue(Hibernate.isInitialized(result.bar));
        Assert.assertTrue(Hibernate.isInitialized(result.baz));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void loadCollection() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        EntityGraphTest.Bar bar = new EntityGraphTest.Bar();
        em.persist(bar);
        EntityGraphTest.Foo foo = new EntityGraphTest.Foo();
        foo.bar = bar;
        bar.foos.add(foo);
        em.persist(foo);
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        EntityGraph<EntityGraphTest.Bar> barGraph = em.createEntityGraph(EntityGraphTest.Bar.class);
        barGraph.addAttributeNodes("foos");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("javax.persistence.loadgraph", barGraph);
        EntityGraphTest.Bar result = em.find(EntityGraphTest.Bar.class, bar.id, properties);
        Assert.assertTrue(Hibernate.isInitialized(result));
        Assert.assertTrue(Hibernate.isInitialized(result.foos));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void loadInverseCollection() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        EntityGraphTest.Bar bar = new EntityGraphTest.Bar();
        em.persist(bar);
        EntityGraphTest.Baz baz = new EntityGraphTest.Baz();
        em.persist(baz);
        EntityGraphTest.Foo foo = new EntityGraphTest.Foo();
        foo.bar = bar;
        foo.baz = baz;
        bar.foos.add(foo);
        baz.foos.add(foo);
        em.persist(foo);
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        EntityGraph<EntityGraphTest.Foo> fooGraph = em.createEntityGraph(EntityGraphTest.Foo.class);
        fooGraph.addAttributeNodes("bar");
        fooGraph.addAttributeNodes("baz");
        Subgraph<EntityGraphTest.Bar> barGraph = fooGraph.addSubgraph("bar", EntityGraphTest.Bar.class);
        barGraph.addAttributeNodes("foos");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("javax.persistence.loadgraph", fooGraph);
        EntityGraphTest.Foo result = em.find(EntityGraphTest.Foo.class, foo.id, properties);
        Assert.assertTrue(Hibernate.isInitialized(result));
        Assert.assertTrue(Hibernate.isInitialized(result.bar));
        Assert.assertTrue(Hibernate.isInitialized(result.bar.foos));
        Assert.assertTrue(Hibernate.isInitialized(result.baz));
        // sanity check -- ensure the only bi-directional fetch was the one identified by the graph
        Assert.assertFalse(Hibernate.isInitialized(result.baz.foos));
        em.getTransaction().commit();
        em.close();
    }

    /**
     * JPA 2.1 spec: "Add a node to the graph that corresponds to a managed type with inheritance. This allows for
     * multiple subclass subgraphs to be defined for this node of the entity graph. Subclass subgraphs will
     * automatically include the specified attributes of superclass subgraphs."
     */
    @Test
    @TestForIssue(jiraKey = "HHH-8640")
    public void inheritanceTest() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Manager manager = new Manager();
        em.persist(manager);
        Employee employee = new Employee();
        employee.friends.add(manager);
        employee.managers.add(manager);
        em.persist(employee);
        Company company = new Company();
        company.employees.add(employee);
        company.employees.add(manager);
        em.persist(company);
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        EntityGraph<Company> entityGraph = em.createEntityGraph(Company.class);
        Subgraph<Employee> subgraph = entityGraph.addSubgraph("employees");
        subgraph.addAttributeNodes("managers");
        subgraph.addAttributeNodes("friends");
        Subgraph<Manager> subSubgraph = subgraph.addSubgraph("managers", Manager.class);
        subSubgraph.addAttributeNodes("managers");
        subSubgraph.addAttributeNodes("friends");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("javax.persistence.loadgraph", entityGraph);
        Company result = em.find(Company.class, company.id, properties);
        Assert.assertTrue(Hibernate.isInitialized(result));
        Assert.assertTrue(Hibernate.isInitialized(result.employees));
        Assert.assertEquals(result.employees.size(), 2);
        for (Employee resultEmployee : result.employees) {
            Assert.assertTrue(Hibernate.isInitialized(resultEmployee.managers));
            Assert.assertTrue(Hibernate.isInitialized(resultEmployee.friends));
        }
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9080")
    public void attributeNodeInheritanceTest() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Manager manager = new Manager();
        em.persist(manager);
        Employee employee = new Employee();
        manager.friends.add(employee);
        em.persist(employee);
        Manager anotherManager = new Manager();
        manager.managers.add(anotherManager);
        em.persist(anotherManager);
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        EntityGraph<Manager> entityGraph = em.createEntityGraph(Manager.class);
        entityGraph.addAttributeNodes("friends");
        entityGraph.addAttributeNodes("managers");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("javax.persistence.loadgraph", entityGraph);
        Manager result = em.find(Manager.class, manager.id, properties);
        Assert.assertTrue(Hibernate.isInitialized(result));
        Assert.assertTrue(Hibernate.isInitialized(result.friends));
        Assert.assertEquals(result.friends.size(), 1);
        Assert.assertTrue(Hibernate.isInitialized(result.managers));
        Assert.assertEquals(result.managers.size(), 1);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9735")
    public void loadIsMemeberQueriedCollection() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        EntityGraphTest.Bar bar = new EntityGraphTest.Bar();
        em.persist(bar);
        EntityGraphTest.Foo foo = new EntityGraphTest.Foo();
        foo.bar = bar;
        bar.foos.add(foo);
        em.persist(foo);
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        foo = em.find(EntityGraphTest.Foo.class, foo.id);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EntityGraphTest.Bar> cq = cb.createQuery(EntityGraphTest.Bar.class);
        Root<EntityGraphTest.Bar> from = cq.from(EntityGraphTest.Bar.class);
        Expression<Set<EntityGraphTest.Foo>> foos = from.get("foos");
        cq.where(cb.isMember(foo, foos));
        TypedQuery<EntityGraphTest.Bar> query = em.createQuery(cq);
        EntityGraph<EntityGraphTest.Bar> barGraph = em.createEntityGraph(EntityGraphTest.Bar.class);
        barGraph.addAttributeNodes("foos");
        query.setHint("javax.persistence.loadgraph", barGraph);
        EntityGraphTest.Bar result = query.getSingleResult();
        Assert.assertTrue(Hibernate.isInitialized(result));
        Assert.assertTrue(Hibernate.isInitialized(result.foos));
        em.getTransaction().commit();
        em.close();
    }

    @Entity
    @Table(name = "foo")
    public static class Foo {
        @Id
        @GeneratedValue
        public Integer id;

        @ManyToOne(fetch = FetchType.LAZY)
        public EntityGraphTest.Bar bar;

        @ManyToOne(fetch = FetchType.LAZY)
        public EntityGraphTest.Baz baz;
    }

    @Entity
    @Table(name = "bar")
    public static class Bar {
        @Id
        @GeneratedValue
        public Integer id;

        @OneToMany(mappedBy = "bar")
        public Set<EntityGraphTest.Foo> foos = new HashSet<EntityGraphTest.Foo>();
    }

    @Entity
    @Table(name = "baz")
    public static class Baz {
        @Id
        @GeneratedValue
        public Integer id;

        @OneToMany(mappedBy = "baz")
        public Set<EntityGraphTest.Foo> foos = new HashSet<EntityGraphTest.Foo>();
    }
}

