/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.graphs;


import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Subgraph;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.Attribute;
import org.hibernate.Hibernate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.metamodel.model.domain.spi.EntityTypeDescriptor;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Baris Cubukcuoglu
 */
public class EntityGraphUsingFetchGraphTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9392")
    public void fetchSubGraphFromSubgraph() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        EntityGraphUsingFetchGraphTest.Address address = new EntityGraphUsingFetchGraphTest.Address();
        address.city = "TestCity";
        EntityGraphUsingFetchGraphTest.CustomerOrder customerOrder = new EntityGraphUsingFetchGraphTest.CustomerOrder();
        customerOrder.shippingAddress = address;
        EntityGraphUsingFetchGraphTest.Product product = new EntityGraphUsingFetchGraphTest.Product();
        EntityGraphUsingFetchGraphTest.OrderPosition orderPosition = new EntityGraphUsingFetchGraphTest.OrderPosition();
        orderPosition.product = product;
        customerOrder.orderPosition = orderPosition;
        em.persist(address);
        em.persist(orderPosition);
        em.persist(product);
        em.persist(customerOrder);
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        final EntityGraph<EntityGraphUsingFetchGraphTest.CustomerOrder> entityGraph = em.createEntityGraph(EntityGraphUsingFetchGraphTest.CustomerOrder.class);
        // entityGraph.addAttributeNodes( "shippingAddress", "orderDate" );
        entityGraph.addAttributeNodes("shippingAddress");
        final Subgraph<EntityGraphUsingFetchGraphTest.OrderPosition> orderProductsSubgraph = entityGraph.addSubgraph("orderPosition");
        // orderProductsSubgraph.addAttributeNodes( "amount" );
        final Subgraph<EntityGraphUsingFetchGraphTest.Product> productSubgraph = orderProductsSubgraph.addSubgraph("product");
        // productSubgraph.addAttributeNodes( "productName" );
        TypedQuery<EntityGraphUsingFetchGraphTest.CustomerOrder> query = em.createQuery("SELECT o FROM EntityGraphUsingFetchGraphTest$CustomerOrder o", EntityGraphUsingFetchGraphTest.CustomerOrder.class);
        query.setHint("javax.persistence.loadgraph", entityGraph);
        final List<EntityGraphUsingFetchGraphTest.CustomerOrder> results = query.getResultList();
        Assert.assertTrue(Hibernate.isInitialized(results));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9392")
    public void fetchAttributeNodeByStringFromSubgraph() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        EntityGraphUsingFetchGraphTest.Address address = new EntityGraphUsingFetchGraphTest.Address();
        address.city = "TestCity";
        EntityGraphUsingFetchGraphTest.CustomerOrder customerOrder = new EntityGraphUsingFetchGraphTest.CustomerOrder();
        customerOrder.shippingAddress = address;
        EntityGraphUsingFetchGraphTest.Product product = new EntityGraphUsingFetchGraphTest.Product();
        EntityGraphUsingFetchGraphTest.OrderPosition orderPosition = new EntityGraphUsingFetchGraphTest.OrderPosition();
        orderPosition.product = product;
        customerOrder.orderPosition = orderPosition;
        em.persist(address);
        em.persist(orderPosition);
        em.persist(product);
        em.persist(customerOrder);
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        final EntityGraph<EntityGraphUsingFetchGraphTest.CustomerOrder> entityGraph = em.createEntityGraph(EntityGraphUsingFetchGraphTest.CustomerOrder.class);
        entityGraph.addAttributeNodes("shippingAddress", "orderDate");
        entityGraph.addAttributeNodes("shippingAddress");
        final Subgraph<EntityGraphUsingFetchGraphTest.OrderPosition> orderProductsSubgraph = entityGraph.addSubgraph("orderPosition");
        orderProductsSubgraph.addAttributeNodes("amount");
        orderProductsSubgraph.addAttributeNodes("product");
        final Subgraph<EntityGraphUsingFetchGraphTest.Product> productSubgraph = orderProductsSubgraph.addSubgraph("product");
        productSubgraph.addAttributeNodes("productName");
        TypedQuery<EntityGraphUsingFetchGraphTest.CustomerOrder> query = em.createQuery("SELECT o FROM EntityGraphUsingFetchGraphTest$CustomerOrder o", EntityGraphUsingFetchGraphTest.CustomerOrder.class);
        query.setHint("javax.persistence.loadgraph", entityGraph);
        final List<EntityGraphUsingFetchGraphTest.CustomerOrder> results = query.getResultList();
        assertEntityGraph(entityGraph);
        Assert.assertTrue(Hibernate.isInitialized(results));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13233")
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void fetchAttributeNodeByAttributeFromSubgraph() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        EntityGraphUsingFetchGraphTest.Address address = new EntityGraphUsingFetchGraphTest.Address();
        address.city = "TestCity";
        EntityGraphUsingFetchGraphTest.CustomerOrder customerOrder = new EntityGraphUsingFetchGraphTest.CustomerOrder();
        customerOrder.shippingAddress = address;
        EntityGraphUsingFetchGraphTest.Product product = new EntityGraphUsingFetchGraphTest.Product();
        EntityGraphUsingFetchGraphTest.OrderPosition orderPosition = new EntityGraphUsingFetchGraphTest.OrderPosition();
        orderPosition.product = product;
        customerOrder.orderPosition = orderPosition;
        em.persist(address);
        em.persist(orderPosition);
        em.persist(product);
        em.persist(customerOrder);
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        final EntityGraph<EntityGraphUsingFetchGraphTest.CustomerOrder> entityGraph = em.createEntityGraph(EntityGraphUsingFetchGraphTest.CustomerOrder.class);
        EntityTypeDescriptor<EntityGraphUsingFetchGraphTest.CustomerOrder> customerOrderEntityType = entityManagerFactory().getMetamodel().entity(EntityGraphUsingFetchGraphTest.CustomerOrder.class);
        entityGraph.addAttributeNodes(((Attribute) (customerOrderEntityType.getAttribute("shippingAddress"))), ((Attribute) (customerOrderEntityType.getAttribute("orderDate"))));
        entityGraph.addAttributeNodes(((Attribute) (customerOrderEntityType.getAttribute("shippingAddress"))));
        final Subgraph<EntityGraphUsingFetchGraphTest.OrderPosition> orderProductsSubgraph = entityGraph.addSubgraph(((Attribute) (customerOrderEntityType.getAttribute("orderPosition"))));
        EntityTypeDescriptor<EntityGraphUsingFetchGraphTest.OrderPosition> positionEntityType = entityManagerFactory().getMetamodel().entity(EntityGraphUsingFetchGraphTest.OrderPosition.class);
        orderProductsSubgraph.addAttributeNodes(((Attribute) (positionEntityType.getAttribute("amount"))));
        orderProductsSubgraph.addAttributeNodes(((Attribute) (positionEntityType.getAttribute("product"))));
        final Subgraph<EntityGraphUsingFetchGraphTest.Product> productSubgraph = orderProductsSubgraph.addSubgraph(((Attribute) (positionEntityType.getAttribute("product"))));
        EntityTypeDescriptor<EntityGraphUsingFetchGraphTest.Product> productEntityType = entityManagerFactory().getMetamodel().entity(EntityGraphUsingFetchGraphTest.Product.class);
        productSubgraph.addAttributeNodes(((Attribute) (productEntityType.getAttribute("productName"))));
        TypedQuery<EntityGraphUsingFetchGraphTest.CustomerOrder> query = em.createQuery("SELECT o FROM EntityGraphUsingFetchGraphTest$CustomerOrder o", EntityGraphUsingFetchGraphTest.CustomerOrder.class);
        query.setHint("javax.persistence.loadgraph", entityGraph);
        final List<EntityGraphUsingFetchGraphTest.CustomerOrder> results = query.getResultList();
        assertEntityGraph(entityGraph);
        Assert.assertTrue(Hibernate.isInitialized(results));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9392")
    public void fetchUsingHql() {
        // This test is here only for comparison with results from fetchAttributeNodeFromSubgraph.
        // At the time this was written, the generated SQL from the HQL is the same as that generated with the
        // query hint in fetchAttributeNodeFromSubgraph. I am leaving this here for future debugging purposes.
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        EntityGraphUsingFetchGraphTest.Address address = new EntityGraphUsingFetchGraphTest.Address();
        address.city = "TestCity";
        EntityGraphUsingFetchGraphTest.CustomerOrder customerOrder = new EntityGraphUsingFetchGraphTest.CustomerOrder();
        customerOrder.shippingAddress = address;
        EntityGraphUsingFetchGraphTest.Product product = new EntityGraphUsingFetchGraphTest.Product();
        EntityGraphUsingFetchGraphTest.OrderPosition orderPosition = new EntityGraphUsingFetchGraphTest.OrderPosition();
        orderPosition.product = product;
        customerOrder.orderPosition = orderPosition;
        em.persist(address);
        em.persist(orderPosition);
        em.persist(product);
        em.persist(customerOrder);
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        TypedQuery<EntityGraphUsingFetchGraphTest.CustomerOrder> query = em.createQuery("SELECT o FROM EntityGraphUsingFetchGraphTest$CustomerOrder o left join fetch o.orderPosition pos left join fetch pos.product left join fetch o.shippingAddress", EntityGraphUsingFetchGraphTest.CustomerOrder.class);
        final List<EntityGraphUsingFetchGraphTest.CustomerOrder> results = query.getResultList();
        Assert.assertTrue(Hibernate.isInitialized(results));
        em.getTransaction().commit();
        em.close();
    }

    @Entity
    @Table(name = "customerOrder")
    public static class CustomerOrder {
        @Id
        @GeneratedValue
        public Long id;

        @OneToOne
        public EntityGraphUsingFetchGraphTest.OrderPosition orderPosition;

        @Temporal(TemporalType.TIMESTAMP)
        public Date orderDate;

        @OneToOne
        public EntityGraphUsingFetchGraphTest.Address shippingAddress;
    }

    @Entity
    @Table(name = "address")
    public static class Address {
        @Id
        @GeneratedValue
        public Long id;

        public String city;
    }

    @Entity
    @Table(name = "orderPosition")
    public static class OrderPosition {
        @Id
        @GeneratedValue
        public Long id;

        public Integer amount;

        @ManyToOne
        @JoinColumn(name = "product")
        public EntityGraphUsingFetchGraphTest.Product product;
    }

    @Entity
    @Table(name = "product")
    public static class Product {
        @Id
        @GeneratedValue
        public Long id;

        public String productName;
    }
}

