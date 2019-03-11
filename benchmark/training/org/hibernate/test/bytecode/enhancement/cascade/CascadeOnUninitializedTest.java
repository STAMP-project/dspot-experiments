package org.hibernate.test.bytecode.enhancement.cascade;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import junit.framework.TestCase;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Bolek Ziobrowski
 * @author Gail Badner
 */
@RunWith(BytecodeEnhancerRunner.class)
@TestForIssue(jiraKey = "HHH-13129")
public class CascadeOnUninitializedTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testMergeDetachedEnhancedEntityWithUninitializedManyToOne() {
        CascadeOnUninitializedTest.Person person = persistPersonWithManyToOne();
        // get a detached Person
        CascadeOnUninitializedTest.Person detachedPerson = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            return session.get(.class, person.getId());
        });
        // address should not be initialized
        Assert.assertFalse(Hibernate.isPropertyInitialized(detachedPerson, "primaryAddress"));
        detachedPerson.setName("newName");
        CascadeOnUninitializedTest.Person mergedPerson = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            return ((org.hibernate.test.bytecode.enhancement.cascade.Person) (session.merge(detachedPerson)));
        });
        // address should not be initialized
        Assert.assertFalse(Hibernate.isPropertyInitialized(mergedPerson, "primaryAddress"));
        TestCase.assertEquals("newName", mergedPerson.getName());
    }

    @Test
    public void testDeleteEnhancedEntityWithUninitializedManyToOne() {
        CascadeOnUninitializedTest.Person person = persistPersonWithManyToOne();
        // get a detached Person
        CascadeOnUninitializedTest.Person detachedPerson = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            return session.get(.class, person.getId());
        });
        // address should not be initialized
        Assert.assertFalse(Hibernate.isPropertyInitialized(detachedPerson, "primaryAddress"));
        // deleting detachedPerson should result in detachedPerson.address being initialized,
        // so that the DELETE operation can be cascaded to it.
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.delete(detachedPerson);
        });
        // both the Person and its Address should be deleted
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            assertNull(session.get(.class, person.getId()));
            assertNull(session.get(.class, person.getPrimaryAddress().getId()));
        });
    }

    @Test
    public void testMergeDetachedEnhancedEntityWithUninitializedOneToMany() {
        CascadeOnUninitializedTest.Person person = persistPersonWithOneToMany();
        // get a detached Person
        CascadeOnUninitializedTest.Person detachedPerson = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            return session.get(.class, person.getId());
        });
        // address should not be initialized
        Assert.assertFalse(Hibernate.isPropertyInitialized(detachedPerson, "addresses"));
        detachedPerson.setName("newName");
        CascadeOnUninitializedTest.Person mergedPerson = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            return ((org.hibernate.test.bytecode.enhancement.cascade.Person) (session.merge(detachedPerson)));
        });
        // address should be initialized
        Assert.assertTrue(Hibernate.isPropertyInitialized(mergedPerson, "addresses"));
        TestCase.assertEquals("newName", mergedPerson.getName());
    }

    @Test
    public void testDeleteEnhancedEntityWithUninitializedOneToMany() {
        CascadeOnUninitializedTest.Person person = persistPersonWithOneToMany();
        // get a detached Person
        CascadeOnUninitializedTest.Person detachedPerson = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            return session.get(.class, person.getId());
        });
        // address should not be initialized
        Assert.assertFalse(Hibernate.isPropertyInitialized(detachedPerson, "addresses"));
        // deleting detachedPerson should result in detachedPerson.address being initialized,
        // so that the DELETE operation can be cascaded to it.
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.delete(detachedPerson);
        });
        // both the Person and its Address should be deleted
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            assertNull(session.get(.class, person.getId()));
            assertNull(session.get(.class, person.getAddresses().iterator().next().getId()));
        });
    }

    @Entity
    @Table(name = "TEST_PERSON")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "NAME", length = 300, nullable = true)
        private String name;

        @ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
        @JoinColumn(name = "ADDRESS_ID")
        @LazyToOne(LazyToOneOption.NO_PROXY)
        private CascadeOnUninitializedTest.Address primaryAddress;

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @JoinColumn
        private Set<CascadeOnUninitializedTest.Address> addresses = new HashSet<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public CascadeOnUninitializedTest.Address getPrimaryAddress() {
            return primaryAddress;
        }

        public void setPrimaryAddress(CascadeOnUninitializedTest.Address primaryAddress) {
            this.primaryAddress = primaryAddress;
        }

        public Set<CascadeOnUninitializedTest.Address> getAddresses() {
            return addresses;
        }

        public void setAddresses(Set<CascadeOnUninitializedTest.Address> addresses) {
            this.addresses = addresses;
        }
    }

    @Entity
    @Table(name = "TEST_ADDRESS")
    public static class Address {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "DESCRIPTION", length = 300, nullable = true)
        private String description;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}

