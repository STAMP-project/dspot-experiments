/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.insertordering;


import DialectChecks.SupportsJdbcDriverProxying;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.BatchSize;
import org.hibernate.test.util.jdbc.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-9864")
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class InsertOrderingWithJoinedTableInheritance extends BaseNonConfigCoreFunctionalTestCase {
    private PreparedStatementSpyConnectionProvider connectionProvider = new PreparedStatementSpyConnectionProvider(false, false);

    @Test
    public void testBatchOrdering() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.insertordering.Person person = new org.hibernate.test.insertordering.Person();
            person.addAddress(new org.hibernate.test.insertordering.Address());
            session.persist(person);
            // Derived Object with dependent object (address)
            final org.hibernate.test.insertordering.SpecialPerson specialPerson = new org.hibernate.test.insertordering.SpecialPerson();
            specialPerson.addAddress(new org.hibernate.test.insertordering.Address());
            session.persist(specialPerson);
        });
    }

    @Test
    public void testBatchingAmongstSubClasses() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            int iterations = 12;
            for (int i = 0; i < iterations; i++) {
                final org.hibernate.test.insertordering.Person person = new org.hibernate.test.insertordering.Person();
                person.addAddress(new org.hibernate.test.insertordering.Address());
                session.persist(person);
                final org.hibernate.test.insertordering.SpecialPerson specialPerson = new org.hibernate.test.insertordering.SpecialPerson();
                specialPerson.addAddress(new org.hibernate.test.insertordering.Address());
                session.persist(specialPerson);
            }
            connectionProvider.clear();
        });
        Assert.assertEquals(26, connectionProvider.getPreparedStatements().size());
    }

    @Entity(name = "Address")
    @Table(name = "ADDRESS")
    @Access(AccessType.FIELD)
    public static class Address {
        @Id
        @Column(name = "ID", nullable = false)
        @SequenceGenerator(name = "ID", sequenceName = "ADDRESS_SEQ")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID")
        private Long id;
    }

    @Entity(name = "Person")
    @Access(AccessType.FIELD)
    @Table(name = "PERSON")
    @Inheritance(strategy = InheritanceType.JOINED)
    @DiscriminatorColumn(name = "CLASSINDICATOR", discriminatorType = DiscriminatorType.INTEGER)
    @DiscriminatorValue("1")
    public static class Person {
        @Id
        @Column(name = "ID", nullable = false)
        @SequenceGenerator(name = "ID_2", sequenceName = "PERSON_SEQ")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_2")
        private Long id;

        @OneToMany(orphanRemoval = true, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
        @JoinColumn(name = "PERSONID", referencedColumnName = "ID", nullable = false, updatable = false)
        @BatchSize(size = 100)
        private Set<InsertOrderingWithJoinedTableInheritance.Address> addresses = new HashSet<InsertOrderingWithJoinedTableInheritance.Address>();

        public void addAddress(InsertOrderingWithJoinedTableInheritance.Address address) {
            this.addresses.add(address);
        }
    }

    @Entity(name = "SpecialPerson")
    @Access(AccessType.FIELD)
    @DiscriminatorValue("2")
    public static class SpecialPerson extends InsertOrderingWithJoinedTableInheritance.Person {
        @Column(name = "special")
        private String special;
    }
}

