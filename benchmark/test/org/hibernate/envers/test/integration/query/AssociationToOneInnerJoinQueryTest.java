/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.query;


import JoinType.INNER;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.integration.query.entities.Address;
import org.hibernate.envers.test.integration.query.entities.Car;
import org.hibernate.envers.test.integration.query.entities.Person;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Felix Feisst (feisst dot felix at gmail dot com)
 */
@SuppressWarnings("unchecked")
public class AssociationToOneInnerJoinQueryTest extends BaseEnversJPAFunctionalTestCase {
    private Car vw;

    private Car ford;

    private Car toyota;

    private Address address1;

    private Address address2;

    private Person vwOwner;

    private Person fordOwner;

    private Person toyotaOwner;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        // revision 1
        em.getTransaction().begin();
        address1 = new Address("Freiburgerstrasse", 5);
        em.persist(address1);
        address2 = new Address("Hindenburgstrasse", 30);
        em.persist(address2);
        vwOwner = new Person("VW owner", 20, address1);
        em.persist(vwOwner);
        fordOwner = new Person("Ford owner", 30, address1);
        em.persist(fordOwner);
        toyotaOwner = new Person("Toyota owner", 30, address2);
        em.persist(toyotaOwner);
        final Person nonOwner = new Person("NonOwner", 30, address1);
        em.persist(nonOwner);
        vw = new Car("VW");
        vw.setOwner(vwOwner);
        em.persist(vw);
        ford = new Car("Ford");
        ford.setOwner(fordOwner);
        em.persist(ford);
        toyota = new Car("Toyota");
        toyota.setOwner(toyotaOwner);
        em.persist(toyota);
        em.getTransaction().commit();
        // revision 2
        em.getTransaction().begin();
        toyotaOwner.setAge(40);
        em.getTransaction().commit();
    }

    @Test
    public void testAssociationQuery() {
        final AuditReader auditReader = getAuditReader();
        final Car result1 = ((Car) (auditReader.createQuery().forEntitiesAtRevision(Car.class, 1).traverseRelation("owner", INNER).add(AuditEntity.property("name").like("Ford%")).getSingleResult()));
        Assert.assertEquals("Unexpected single car at revision 1", ford.getId(), result1.getId());
        Car result2 = ((Car) (auditReader.createQuery().forEntitiesAtRevision(Car.class, 1).traverseRelation("owner", INNER).traverseRelation("address", INNER).add(AuditEntity.property("number").eq(30)).getSingleResult()));
        Assert.assertEquals("Unexpected single car at revision 1", toyota.getId(), result2.getId());
        List<Car> resultList1 = auditReader.createQuery().forEntitiesAtRevision(Car.class, 1).traverseRelation("owner", INNER).add(AuditEntity.property("age").ge(30)).add(AuditEntity.property("age").lt(40)).up().addOrder(AuditEntity.property("make").asc()).getResultList();
        Assert.assertEquals("Unexpected number of cars for query in revision 1", 2, resultList1.size());
        Assert.assertEquals("Unexpected car at index 0 in revision 1", ford.getId(), resultList1.get(0).getId());
        Assert.assertEquals("Unexpected car at index 1 in revision 2", toyota.getId(), resultList1.get(1).getId());
        Car result3 = ((Car) (auditReader.createQuery().forEntitiesAtRevision(Car.class, 2).traverseRelation("owner", INNER).add(AuditEntity.property("age").ge(30)).add(AuditEntity.property("age").lt(40)).up().addOrder(AuditEntity.property("make").asc()).getSingleResult()));
        Assert.assertEquals("Unexpected car at revision 2", ford.getId(), result3.getId());
    }

    @Test
    public void testAssociationQueryWithOrdering() {
        AuditReader auditReader = getAuditReader();
        List<Car> cars1 = auditReader.createQuery().forEntitiesAtRevision(Car.class, 1).traverseRelation("owner", INNER).traverseRelation("address", INNER).addOrder(AuditEntity.property("number").asc()).up().addOrder(AuditEntity.property("age").desc()).getResultList();
        Assert.assertEquals("Unexpected number of results", 3, cars1.size());
        Assert.assertEquals("Unexpected car at index 0", ford.getId(), cars1.get(0).getId());
        Assert.assertEquals("Unexpected car at index 1", vw.getId(), cars1.get(1).getId());
        Assert.assertEquals("Unexpected car at index 2", toyota.getId(), cars1.get(2).getId());
        List<Car> cars2 = auditReader.createQuery().forEntitiesAtRevision(Car.class, 1).traverseRelation("owner", INNER).traverseRelation("address", INNER).addOrder(AuditEntity.property("number").asc()).up().addOrder(AuditEntity.property("age").asc()).getResultList();
        Assert.assertEquals("Unexpected number of results", 3, cars2.size());
        Assert.assertEquals("Unexpected car at index 0", vw.getId(), cars2.get(0).getId());
        Assert.assertEquals("Unexpected car at index 1", ford.getId(), cars2.get(1).getId());
        Assert.assertEquals("Unexpected car at index 2", toyota.getId(), cars2.get(2).getId());
    }

    @Test
    public void testAssociationQueryWithProjection() {
        AuditReader auditReader = getAuditReader();
        List<Integer> list1 = auditReader.createQuery().forEntitiesAtRevision(Car.class, 2).traverseRelation("owner", INNER).addProjection(AuditEntity.property("age")).addOrder(AuditEntity.property("age").asc()).getResultList();
        Assert.assertEquals("Unexpected number of results", 3, list1.size());
        Assert.assertEquals("Unexpected age at index 0", Integer.valueOf(20), list1.get(0));
        Assert.assertEquals("Unexpected age at index 0", Integer.valueOf(30), list1.get(1));
        Assert.assertEquals("Unexpected age at index 0", Integer.valueOf(40), list1.get(2));
        List<Address> list2 = auditReader.createQuery().forEntitiesAtRevision(Car.class, 2).traverseRelation("owner", INNER).addOrder(AuditEntity.property("age").asc()).traverseRelation("address", INNER).addProjection(AuditEntity.selectEntity(false)).getResultList();
        Assert.assertEquals("Unexpected number of results", 3, list2.size());
        Assert.assertEquals("Unexpected address at index 0", address1.getId(), list2.get(0).getId());
        Assert.assertEquals("Unexpected address at index 1", address1.getId(), list2.get(1).getId());
        Assert.assertEquals("Unexpected address at index 2", address2.getId(), list2.get(2).getId());
        List<Address> list3 = auditReader.createQuery().forEntitiesAtRevision(Car.class, 2).traverseRelation("owner", INNER).traverseRelation("address", INNER).addProjection(AuditEntity.selectEntity(true)).addOrder(AuditEntity.property("number").asc()).getResultList();
        Assert.assertEquals("Unexpected number of results", 2, list3.size());
        Assert.assertEquals("Unexpected address at index 0", address1.getId(), list3.get(0).getId());
        Assert.assertEquals("Unexpected address at index 1", address2.getId(), list3.get(1).getId());
        List<Object[]> list4 = auditReader.createQuery().forEntitiesAtRevision(Car.class, 2).traverseRelation("owner", INNER).addOrder(AuditEntity.property("age").asc()).addProjection(AuditEntity.selectEntity(false)).traverseRelation("address", INNER).addProjection(AuditEntity.property("number")).getResultList();
        Assert.assertEquals("Unexpected number of results", 3, list4.size());
        final Object[] index0 = list4.get(0);
        Assert.assertEquals("Unexpected owner at index 0", vwOwner.getId(), ((Person) (index0[0])).getId());
        Assert.assertEquals("Unexpected number at index 0", Integer.valueOf(5), index0[1]);
        final Object[] index1 = list4.get(1);
        Assert.assertEquals("Unexpected owner at index 1", fordOwner.getId(), ((Person) (index1[0])).getId());
        Assert.assertEquals("Unexpected number at index 1", Integer.valueOf(5), index1[1]);
        final Object[] index2 = list4.get(2);
        Assert.assertEquals("Unexpected owner at index 2", toyotaOwner.getId(), ((Person) (index2[0])).getId());
        Assert.assertEquals("Unexpected number at index 2", Integer.valueOf(30), index2[1]);
    }

    @Test
    public void testDisjunctionOfPropertiesFromDifferentEntities() {
        AuditReader auditReader = getAuditReader();
        // all cars where the owner has an age of 20 or lives in an address with number 30.
        List<Car> resultList = auditReader.createQuery().forEntitiesAtRevision(Car.class, 1).traverseRelation("owner", INNER, "p").traverseRelation("address", INNER, "a").up().up().add(AuditEntity.disjunction().add(AuditEntity.property("p", "age").eq(20)).add(AuditEntity.property("a", "number").eq(30))).addOrder(AuditEntity.property("make").asc()).getResultList();
        Assert.assertEquals("Expected two cars to be returned, Toyota and VW", 2, resultList.size());
        Assert.assertEquals("Unexpected car at index 0", toyota.getId(), resultList.get(0).getId());
        Assert.assertEquals("Unexpected car at index 1", vw.getId(), resultList.get(1).getId());
    }

    @Test
    public void testComparisonOfTwoPropertiesFromDifferentEntities() {
        AuditReader auditReader = getAuditReader();
        // the car where the owner age is equal to the owner address number.
        Car result = ((Car) (auditReader.createQuery().forEntitiesAtRevision(Car.class, 1).traverseRelation("owner", INNER, "p").traverseRelation("address", INNER, "a").up().up().add(AuditEntity.property("p", "age").eqProperty("a", "number")).getSingleResult()));
        Assert.assertEquals("Unexpected car returned", toyota.getId(), result.getId());
    }
}

