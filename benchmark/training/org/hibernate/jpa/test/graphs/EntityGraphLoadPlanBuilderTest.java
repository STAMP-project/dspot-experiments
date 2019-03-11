/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.graphs;


import GraphSemantic.FETCH;
import GraphSemantic.LOAD;
import LoadPlanTreePrinter.INSTANCE;
import QuerySpace.Disposition;
import QuerySpace.Disposition.COLLECTION;
import QuerySpace.Disposition.COMPOSITE;
import QuerySpace.Disposition.ENTITY;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.loader.plan.spi.Join;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Strong Liu <stliu@hibernate.org>
 */
public class EntityGraphLoadPlanBuilderTest extends BaseEntityManagerFunctionalTestCase {
    @Entity
    public static class Dog {
        @Id
        String name;

        @ElementCollection
        Set<String> favorites;
    }

    @Entity
    public static class Cat {
        @Id
        String name;

        @ManyToOne(fetch = FetchType.LAZY)
        EntityGraphLoadPlanBuilderTest.Person owner;
    }

    @Entity
    public static class Person {
        @Id
        String name;

        @OneToMany(mappedBy = "owner")
        Set<EntityGraphLoadPlanBuilderTest.Cat> pets;

        @Embedded
        EntityGraphLoadPlanBuilderTest.Address homeAddress;
    }

    @Embeddable
    public static class Address {
        @ManyToOne
        EntityGraphLoadPlanBuilderTest.Country country;
    }

    @Entity
    public static class ExpressCompany {
        @Id
        String name;

        @ElementCollection
        Set<EntityGraphLoadPlanBuilderTest.Address> shipAddresses;
    }

    @Entity
    public static class Country {
        @Id
        String name;
    }

    /**
     * EntityGraph(1):
     *
     * Cat
     *
     * LoadPlan:
     *
     * Cat
     *
     * ---------------------
     *
     * EntityGraph(2):
     *
     * Cat
     * owner -- Person
     *
     * LoadPlan:
     *
     * Cat
     * owner -- Person
     * address --- Address
     */
    @Test
    public void testBasicFetchLoadPlanBuilding() {
        EntityManager em = getOrCreateEntityManager();
        EntityGraph eg = em.createEntityGraph(EntityGraphLoadPlanBuilderTest.Cat.class);
        LoadPlan plan = buildLoadPlan(eg, FETCH, EntityGraphLoadPlanBuilderTest.Cat.class);
        INSTANCE.logTree(plan, new org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl(sfi()));
        QuerySpace rootQuerySpace = plan.getQuerySpaces().getRootQuerySpaces().get(0);
        Assert.assertFalse("With fetchgraph property and an empty EntityGraph, there should be no join at all", rootQuerySpace.getJoins().iterator().hasNext());
        // -------------------------------------------------- another a little more complicated case
        eg = em.createEntityGraph(EntityGraphLoadPlanBuilderTest.Cat.class);
        eg.addSubgraph("owner", EntityGraphLoadPlanBuilderTest.Person.class);
        plan = buildLoadPlan(eg, FETCH, EntityGraphLoadPlanBuilderTest.Cat.class);
        INSTANCE.logTree(plan, new org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl(sfi()));
        rootQuerySpace = plan.getQuerySpaces().getRootQuerySpaces().get(0);
        Iterator<Join> iterator = rootQuerySpace.getJoins().iterator();
        Assert.assertTrue("With fetchgraph property and an empty EntityGraph, there should be no join at all", iterator.hasNext());
        Join personJoin = iterator.next();
        Assert.assertNotNull(personJoin);
        QuerySpace.Disposition disposition = personJoin.getRightHandSide().getDisposition();
        Assert.assertEquals("This should be an entity join which fetches Person", ENTITY, disposition);
        iterator = personJoin.getRightHandSide().getJoins().iterator();
        Assert.assertTrue("The composite address should be fetched", iterator.hasNext());
        Join addressJoin = iterator.next();
        Assert.assertNotNull(addressJoin);
        disposition = addressJoin.getRightHandSide().getDisposition();
        Assert.assertEquals(COMPOSITE, disposition);
        Assert.assertFalse(iterator.hasNext());
        Assert.assertFalse("The ManyToOne attribute in composite should not be fetched", addressJoin.getRightHandSide().getJoins().iterator().hasNext());
        em.close();
    }

    /**
     * EntityGraph(1):
     *
     * Cat
     *
     * LoadPlan:
     *
     * Cat
     *
     * ---------------------
     *
     * EntityGraph(2):
     *
     * Cat
     * owner -- Person
     *
     * LoadPlan:
     *
     * Cat
     * owner -- Person
     * address --- Address
     * country -- Country
     */
    @Test
    public void testBasicLoadLoadPlanBuilding() {
        EntityManager em = getOrCreateEntityManager();
        EntityGraph eg = em.createEntityGraph(EntityGraphLoadPlanBuilderTest.Cat.class);
        LoadPlan plan = buildLoadPlan(eg, LOAD, EntityGraphLoadPlanBuilderTest.Cat.class);
        INSTANCE.logTree(plan, new org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl(sfi()));
        QuerySpace rootQuerySpace = plan.getQuerySpaces().getRootQuerySpaces().get(0);
        Assert.assertFalse("With fetchgraph property and an empty EntityGraph, there should be no join at all", rootQuerySpace.getJoins().iterator().hasNext());
        // -------------------------------------------------- another a little more complicated case
        eg = em.createEntityGraph(EntityGraphLoadPlanBuilderTest.Cat.class);
        eg.addSubgraph("owner", EntityGraphLoadPlanBuilderTest.Person.class);
        plan = buildLoadPlan(eg, LOAD, EntityGraphLoadPlanBuilderTest.Cat.class);
        INSTANCE.logTree(plan, new org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl(sfi()));
        rootQuerySpace = plan.getQuerySpaces().getRootQuerySpaces().get(0);
        Iterator<Join> iterator = rootQuerySpace.getJoins().iterator();
        Assert.assertTrue("With fetchgraph property and an empty EntityGraph, there should be no join at all", iterator.hasNext());
        Join personJoin = iterator.next();
        Assert.assertNotNull(personJoin);
        QuerySpace.Disposition disposition = personJoin.getRightHandSide().getDisposition();
        Assert.assertEquals("This should be an entity join which fetches Person", ENTITY, disposition);
        iterator = personJoin.getRightHandSide().getJoins().iterator();
        Assert.assertTrue("The composite address should be fetched", iterator.hasNext());
        Join addressJoin = iterator.next();
        Assert.assertNotNull(addressJoin);
        disposition = addressJoin.getRightHandSide().getDisposition();
        Assert.assertEquals(COMPOSITE, disposition);
        iterator = addressJoin.getRightHandSide().getJoins().iterator();
        Assert.assertTrue(iterator.hasNext());
        Join countryJoin = iterator.next();
        Assert.assertNotNull(countryJoin);
        disposition = countryJoin.getRightHandSide().getDisposition();
        Assert.assertEquals(ENTITY, disposition);
        Assert.assertFalse("The ManyToOne attribute in composite should not be fetched", countryJoin.getRightHandSide().getJoins().iterator().hasNext());
        em.close();
    }

    @Test
    public void testBasicElementCollections() {
        EntityManager em = getOrCreateEntityManager();
        EntityGraph eg = em.createEntityGraph(EntityGraphLoadPlanBuilderTest.Dog.class);
        eg.addAttributeNodes("favorites");
        LoadPlan loadLoadPlan = buildLoadPlan(eg, LOAD, EntityGraphLoadPlanBuilderTest.Dog.class);// WTF name!!!

        INSTANCE.logTree(loadLoadPlan, new org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl(sfi()));
        QuerySpace querySpace = loadLoadPlan.getQuerySpaces().getRootQuerySpaces().iterator().next();
        Iterator<Join> iterator = querySpace.getJoins().iterator();
        Assert.assertTrue(iterator.hasNext());
        Join collectionJoin = iterator.next();
        Assert.assertEquals(COLLECTION, collectionJoin.getRightHandSide().getDisposition());
        Assert.assertFalse(iterator.hasNext());
        // ----------------------------------------------------------------
        LoadPlan fetchLoadPlan = buildLoadPlan(eg, FETCH, EntityGraphLoadPlanBuilderTest.Dog.class);
        INSTANCE.logTree(fetchLoadPlan, new org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl(sfi()));
        querySpace = fetchLoadPlan.getQuerySpaces().getRootQuerySpaces().iterator().next();
        iterator = querySpace.getJoins().iterator();
        Assert.assertTrue(iterator.hasNext());
        collectionJoin = iterator.next();
        Assert.assertEquals(COLLECTION, collectionJoin.getRightHandSide().getDisposition());
        Assert.assertFalse(iterator.hasNext());
        em.close();
    }

    @Test
    public void testEmbeddedCollection() {
        EntityManager em = getOrCreateEntityManager();
        EntityGraph eg = em.createEntityGraph(EntityGraphLoadPlanBuilderTest.ExpressCompany.class);
        eg.addAttributeNodes("shipAddresses");
        LoadPlan loadLoadPlan = buildLoadPlan(eg, LOAD, EntityGraphLoadPlanBuilderTest.ExpressCompany.class);// WTF name!!!

        INSTANCE.logTree(loadLoadPlan, new org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl(sfi()));
        QuerySpace querySpace = loadLoadPlan.getQuerySpaces().getRootQuerySpaces().iterator().next();
        Iterator<Join> iterator = querySpace.getJoins().iterator();
        Assert.assertTrue(iterator.hasNext());
        Join collectionJoin = iterator.next();
        Assert.assertEquals(COLLECTION, collectionJoin.getRightHandSide().getDisposition());
        Assert.assertFalse(iterator.hasNext());
        iterator = collectionJoin.getRightHandSide().getJoins().iterator();
        Assert.assertTrue(iterator.hasNext());
        Join collectionElementJoin = iterator.next();
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals(COMPOSITE, collectionElementJoin.getRightHandSide().getDisposition());
        iterator = collectionElementJoin.getRightHandSide().getJoins().iterator();
        Assert.assertTrue(iterator.hasNext());
        Join countryJoin = iterator.next();
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals(ENTITY, countryJoin.getRightHandSide().getDisposition());
        // ----------------------------------------------------------------
        LoadPlan fetchLoadPlan = buildLoadPlan(eg, FETCH, EntityGraphLoadPlanBuilderTest.ExpressCompany.class);
        INSTANCE.logTree(fetchLoadPlan, new org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl(sfi()));
        querySpace = fetchLoadPlan.getQuerySpaces().getRootQuerySpaces().iterator().next();
        iterator = querySpace.getJoins().iterator();
        Assert.assertTrue(iterator.hasNext());
        collectionJoin = iterator.next();
        Assert.assertEquals(COLLECTION, collectionJoin.getRightHandSide().getDisposition());
        Assert.assertFalse(iterator.hasNext());
        iterator = collectionJoin.getRightHandSide().getJoins().iterator();
        Assert.assertTrue(iterator.hasNext());
        collectionElementJoin = iterator.next();
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals(COMPOSITE, collectionElementJoin.getRightHandSide().getDisposition());
        iterator = collectionElementJoin.getRightHandSide().getJoins().iterator();
        Assert.assertFalse(iterator.hasNext());
        // ----------------------------------------------------------------
        em.close();
    }
}

