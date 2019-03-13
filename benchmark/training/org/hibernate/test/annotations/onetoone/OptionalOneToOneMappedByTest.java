/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.onetoone;


import java.util.concurrent.atomic.AtomicReference;
import javax.persistence.PersistenceException;
import org.hibernate.criterion.Restrictions;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 * @author Gail Badner
 */
public class OptionalOneToOneMappedByTest extends BaseCoreFunctionalTestCase {
    // @OneToOne(mappedBy="address") with foreign generator
    @Test
    public void testBidirForeignIdGenerator() {
        try {
            TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
                OwnerAddress address = new OwnerAddress();
                address.setOwner(null);
                session.persist(address);
                session.flush();
                fail("should have failed with IdentifierGenerationException");
            });
        } catch (PersistenceException ex) {
            ExtraAssertions.assertTyping(IdentifierGenerationException.class, ex.getCause());
            // expected
        }
    }

    @Test
    public void testBidirAssignedId() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            PartyAffiliate affiliate = new PartyAffiliate();
            affiliate.partyId = "id";
            session.persist(affiliate);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            PartyAffiliate affiliate = ((PartyAffiliate) (session.createCriteria(.class).add(Restrictions.idEq("id")).uniqueResult()));
            assertNotNull(affiliate);
            assertEquals("id", affiliate.partyId);
            assertNull(affiliate.party);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            PartyAffiliate affiliate = session.get(.class, "id");
            assertNull(affiliate.party);
            session.delete(affiliate);
        });
    }

    @Test
    public void testBidirDefaultIdGenerator() throws Exception {
        PersonAddress _personAddress = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            PersonAddress personAddress = new PersonAddress();
            personAddress.setPerson(null);
            session.persist(personAddress);
            return personAddress;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            PersonAddress personAddress = ((PersonAddress) (session.createCriteria(.class).add(Restrictions.idEq(_personAddress.getId())).uniqueResult()));
            assertNotNull(personAddress);
            assertNull(personAddress.getPerson());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            PersonAddress personAddress = session.get(.class, _personAddress.getId());
            assertNull(personAddress.getPerson());
            session.delete(personAddress);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-5757")
    public void testBidirQueryEntityProperty() throws Exception {
        AtomicReference<Person> personHolder = new AtomicReference<>();
        PersonAddress _personAddress = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            PersonAddress personAddress = new PersonAddress();
            Person person = new Person();
            personAddress.setPerson(person);
            person.setPersonAddress(personAddress);
            session.persist(person);
            session.persist(personAddress);
            personHolder.set(person);
            return personAddress;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            PersonAddress personAddress = ((PersonAddress) (session.createCriteria(.class).add(Restrictions.idEq(_personAddress.getId())).uniqueResult()));
            assertNotNull(personAddress);
            assertNotNull(personAddress.getPerson());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Person person = personHolder.get();
            // this call throws GenericJDBCException
            PersonAddress personAddress = ((PersonAddress) (session.createQuery("select pa from PersonAddress pa where pa.person = :person", .class).setParameter("person", person).getSingleResult()));
            // the other way should also work
            person = ((Person) (session.createCriteria(.class).add(Restrictions.eq("personAddress", personAddress)).uniqueResult()));
            session.delete(personAddress);
        });
    }
}

