/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.loadplans.process;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.Session;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class EncapsulatedCompositeAttributeResultSetProcessorTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSimpleNestedCompositeAttributeProcessing() throws Exception {
        // create some test data
        Session session = openSession();
        session.beginTransaction();
        EncapsulatedCompositeAttributeResultSetProcessorTest.Person person = new EncapsulatedCompositeAttributeResultSetProcessorTest.Person();
        person.id = 1;
        person.name = "Joe Blow";
        person.address = new EncapsulatedCompositeAttributeResultSetProcessorTest.Address();
        person.address.address1 = "1313 Mockingbird Lane";
        person.address.city = "Pleasantville";
        person.address.country = "USA";
        EncapsulatedCompositeAttributeResultSetProcessorTest.AddressType addressType = new EncapsulatedCompositeAttributeResultSetProcessorTest.AddressType();
        addressType.typeName = "snail mail";
        person.address.type = addressType;
        session.save(person);
        session.getTransaction().commit();
        session.close();
        // session = openSession();
        // session.beginTransaction();
        // Person personGotten = (Person) session.get( Person.class, person.id );
        // assertEquals( person.id, personGotten.id );
        // assertEquals( person.address.address1, personGotten.address.address1 );
        // assertEquals( person.address.city, personGotten.address.city );
        // assertEquals( person.address.country, personGotten.address.country );
        // assertEquals( person.address.type.typeName, personGotten.address.type.typeName );
        // session.getTransaction().commit();
        // session.close();
        List results = getResults(sessionFactory().getEntityPersister(EncapsulatedCompositeAttributeResultSetProcessorTest.Person.class.getName()));
        Assert.assertEquals(1, results.size());
        Object result = results.get(0);
        Assert.assertNotNull(result);
        EncapsulatedCompositeAttributeResultSetProcessorTest.Person personWork = ExtraAssertions.assertTyping(EncapsulatedCompositeAttributeResultSetProcessorTest.Person.class, result);
        Assert.assertEquals(person.id, personWork.id);
        Assert.assertEquals(person.address.address1, personWork.address.address1);
        Assert.assertEquals(person.address.city, personWork.address.city);
        Assert.assertEquals(person.address.country, personWork.address.country);
        Assert.assertEquals(person.address.type.typeName, person.address.type.typeName);
        // clean up test data
        session = openSession();
        session.beginTransaction();
        session.createQuery("delete Person").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testNestedCompositeElementCollectionQueryBuilding() {
        doCompare(sessionFactory(), ((OuterJoinLoadable) (sessionFactory().getClassMetadata(EncapsulatedCompositeAttributeResultSetProcessorTest.Customer.class))));
    }

    @Test
    public void testNestedCompositeElementCollectionProcessing() throws Exception {
        // create some test data
        Session session = openSession();
        session.beginTransaction();
        EncapsulatedCompositeAttributeResultSetProcessorTest.Person person = new EncapsulatedCompositeAttributeResultSetProcessorTest.Person();
        person.id = 1;
        person.name = "Joe Blow";
        session.save(person);
        EncapsulatedCompositeAttributeResultSetProcessorTest.Customer customer = new EncapsulatedCompositeAttributeResultSetProcessorTest.Customer();
        customer.id = 1L;
        EncapsulatedCompositeAttributeResultSetProcessorTest.Investment investment1 = new EncapsulatedCompositeAttributeResultSetProcessorTest.Investment();
        investment1.description = "stock";
        investment1.date = new Date();
        investment1.monetaryAmount = new EncapsulatedCompositeAttributeResultSetProcessorTest.MonetaryAmount();
        investment1.monetaryAmount.currency = EncapsulatedCompositeAttributeResultSetProcessorTest.MonetaryAmount.CurrencyCode.USD;
        investment1.monetaryAmount.amount = BigDecimal.valueOf(1234, 2);
        investment1.performedBy = person;
        EncapsulatedCompositeAttributeResultSetProcessorTest.Investment investment2 = new EncapsulatedCompositeAttributeResultSetProcessorTest.Investment();
        investment2.description = "bond";
        investment2.date = new Date();
        investment2.monetaryAmount = new EncapsulatedCompositeAttributeResultSetProcessorTest.MonetaryAmount();
        investment2.monetaryAmount.currency = EncapsulatedCompositeAttributeResultSetProcessorTest.MonetaryAmount.CurrencyCode.EUR;
        investment2.monetaryAmount.amount = BigDecimal.valueOf(98176, 1);
        customer.investments.add(investment1);
        customer.investments.add(investment2);
        session.save(customer);
        session.getTransaction().commit();
        session.close();
        // session = openSession();
        // session.beginTransaction();
        // Customer customerGotten = (Customer) session.get( Customer.class, customer.id );
        // assertEquals( customer.id, customerGotten.id );
        // session.getTransaction().commit();
        // session.close();
        List results = getResults(sessionFactory().getEntityPersister(EncapsulatedCompositeAttributeResultSetProcessorTest.Customer.class.getName()));
        Assert.assertEquals(2, results.size());
        Assert.assertSame(results.get(0), results.get(1));
        Object result = results.get(0);
        Assert.assertNotNull(result);
        EncapsulatedCompositeAttributeResultSetProcessorTest.Customer customerWork = ExtraAssertions.assertTyping(EncapsulatedCompositeAttributeResultSetProcessorTest.Customer.class, result);
        // clean up test data
        session = openSession();
        session.beginTransaction();
        session.delete(customerWork.investments.get(0).performedBy);
        session.delete(customerWork);
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "Person")
    public static class Person implements Serializable {
        @Id
        Integer id;

        String name;

        @Embedded
        EncapsulatedCompositeAttributeResultSetProcessorTest.Address address;
    }

    @Embeddable
    public static class Address implements Serializable {
        String address1;

        String city;

        String country;

        EncapsulatedCompositeAttributeResultSetProcessorTest.AddressType type;
    }

    @Embeddable
    public static class AddressType {
        String typeName;
    }

    @Entity(name = "Customer")
    public static class Customer {
        private Long id;

        private List<EncapsulatedCompositeAttributeResultSetProcessorTest.Investment> investments = new ArrayList<EncapsulatedCompositeAttributeResultSetProcessorTest.Investment>();

        @Id
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "investments", joinColumns = @JoinColumn(name = "customer_id"))
        public List<EncapsulatedCompositeAttributeResultSetProcessorTest.Investment> getInvestments() {
            return investments;
        }

        public void setInvestments(List<EncapsulatedCompositeAttributeResultSetProcessorTest.Investment> investments) {
            this.investments = investments;
        }
    }

    @Embeddable
    public static class Investment {
        private EncapsulatedCompositeAttributeResultSetProcessorTest.MonetaryAmount monetaryAmount;

        private String description;

        private Date date;

        private EncapsulatedCompositeAttributeResultSetProcessorTest.Person performedBy;

        @Embedded
        public EncapsulatedCompositeAttributeResultSetProcessorTest.MonetaryAmount getMonetaryAmount() {
            return monetaryAmount;
        }

        public void setMonetaryAmount(EncapsulatedCompositeAttributeResultSetProcessorTest.MonetaryAmount monetaryAmount) {
            this.monetaryAmount = monetaryAmount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Column(name = "`date`")
        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        @ManyToOne
        public EncapsulatedCompositeAttributeResultSetProcessorTest.Person getPerformedBy() {
            return performedBy;
        }

        public void setPerformedBy(EncapsulatedCompositeAttributeResultSetProcessorTest.Person performedBy) {
            this.performedBy = performedBy;
        }
    }

    @Embeddable
    public static class MonetaryAmount {
        public static enum CurrencyCode {

            USD,
            EUR;}

        private BigDecimal amount;

        @Column(length = 3)
        @Enumerated(EnumType.STRING)
        private EncapsulatedCompositeAttributeResultSetProcessorTest.MonetaryAmount.CurrencyCode currency;

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public EncapsulatedCompositeAttributeResultSetProcessorTest.MonetaryAmount.CurrencyCode getCurrency() {
            return currency;
        }

        public void setCurrency(EncapsulatedCompositeAttributeResultSetProcessorTest.MonetaryAmount.CurrencyCode currency) {
            this.currency = currency;
        }
    }
}

